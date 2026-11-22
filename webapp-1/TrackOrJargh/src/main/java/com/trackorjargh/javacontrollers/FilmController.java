package com.trackorjargh.javacontrollers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.trackorjargh.commoncode.CommonCodeFilm;
import com.trackorjargh.commoncode.CommonCodeImages;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.CommentFilm;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.PointFilm;
import com.trackorjargh.javaclass.PreparateMessageShow;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.CommentFilmRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.PointFilmRepository;
import com.trackorjargh.javarepository.UserRepository;

@Controller
public class FilmController {
	private final FilmRepository filmRepository;
	private final UserRepository userRepository;
	private final UserComponent userComponent;
	private final PointFilmRepository pointFilmRepository;
	private final CommentFilmRepository commentFilmRepository;
	private final GenderRepository genderRepository;
	private final CommonCodeFilm commonCodeFilm;
	private final CommonCodeImages commonCodeImages;
	
	@Autowired
	public FilmController(FilmRepository filmRepository, UserRepository userRepository, UserComponent userComponent,
			PointFilmRepository pointFilmRepository, CommentFilmRepository commentFilmRepository,
			GenderRepository genderRepository, CommonCodeFilm commonCodeFilm, CommonCodeImages commonCodeImages) {
		this.filmRepository = filmRepository;
		this.userRepository = userRepository;
		this.userComponent = userComponent;
		this.pointFilmRepository = pointFilmRepository;
		this.commentFilmRepository = commentFilmRepository;
		this.genderRepository = genderRepository;
		this.commonCodeFilm = commonCodeFilm;
		this.commonCodeImages = commonCodeImages;
	}

	@RequestMapping(value = { "/peliculas", "/peliculas/mejorvaloradas" })
	public String serveFilmList(Model model, HttpServletRequest request) {
		List<Film> films = filmRepository.findByLastAdded(5);
		films.get(0).setFirstInList(true);

		if (userComponent.isLoggedUser()) {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());

			model.addAttribute("userList", user.getLists());
		}

		Page<Film> filmsPage;
		String typePage;
		if (request.getServletPath().equalsIgnoreCase("/peliculas")) {
			filmsPage = filmRepository.findAll(new PageRequest(0, 10));
			model.addAttribute("contentShowButton", true);
			typePage = "/api/peliculas";
		} else {
			filmsPage = filmRepository.findBestPointFilm(new PageRequest(0, 10));
			model.addAttribute("bestPointContentShowButton", true);
			typePage = "/api/peliculas/mejorvaloradas";
		}

		if (filmsPage.getNumberOfElements() > 0 && filmsPage.getNumberOfElements() < 10) {
			model.addAttribute("noElementsSearch", true);
		}

		model.addAttribute("linkContent", "/peliculas");
		model.addAttribute("linkBestPointContent", "/peliculas/mejorvaloradas");
		model.addAttribute("content", filmsPage);
		model.addAttribute("typePage", typePage);
		model.addAttribute("filmsActive", true);
		model.addAttribute("contentCarousel", films);
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "pelicula");

		return "contentList";
	}
	
	@RequestMapping("/pelicula/{name}")
	public String serveFilmProfile(Model model, @PathVariable String name, @RequestParam Optional<String> messageSent,
			@RequestParam Optional<String> pointsSent) {
		Film film = filmRepository.findByNameIgnoreCase(name);

		if (messageSent.isPresent()) {
			commonCodeFilm.addCommentFilm(film, messageSent.get());
		}

		if (pointsSent.isPresent()) {
			double points = Double.parseDouble(pointsSent.get());
			commonCodeFilm.updatePointsFilm(film, points);
		}

		List<PreparateMessageShow> listMessages = new LinkedList<>();
		for (CommentFilm cf : commentFilmRepository.findByFilm(film))
			listMessages.add(cf.preparateShowMessage());

		model.addAttribute("content", film);
		model.addAttribute("comments", listMessages);
		model.addAttribute("typeContent", "la película");
		model.addAttribute("actionMessage", "/pelicula/" + name);

		double points = 0;
		double userPoints = 0;

		List<PointFilm> listPoints = pointFilmRepository.findByFilm(film);

		if (listPoints.size() > 0) {
			for (PointFilm pf : listPoints)
				points += pf.getPoints();
			points /= listPoints.size();
		}

		PointFilm userPointFilm = pointFilmRepository.findByUserAndFilm(userComponent.getLoggedUser(), film);
		if (userPointFilm != null)
			if (userPointFilm != null)
				userPoints = userPointFilm.getPoints();

		model.addAttribute("totalPoints", points);
		model.addAttribute("userPoints", userPoints);

		model.addAttribute("contentRelation",
				filmRepository.findFilmsRelationsById(film.getId(), new PageRequest(0, 8)));
		model.addAttribute("iconFilmShow", true);
		model.addAttribute("deleteComment", "/pelicula/borrarcomentario/");

		return "contentProfile";
	}
	
	@RequestMapping("/pelicula/borrarcomentario/{id}/{name}")
	public String deleteCommentFilm(Model model, @PathVariable int id, @PathVariable String name) {
		commonCodeFilm.deleteCommentFilm(new Long(id));

		return "redirect:/pelicula/" + name;
	}
	
	//Use of search
	@RequestMapping("/busqueda/{optionSearch}/pelicula/{name}")
	public String searchFilms(Model model, @PathVariable String optionSearch, @PathVariable String name) {
		Page<Film> films;

		if(optionSearch.equalsIgnoreCase("titulo")) {
			films = filmRepository.findByNameContainingIgnoreCase(name, new PageRequest(0, 10));
			model.addAttribute("searchTitle", true);
		} else {
			films = filmRepository.findFilmsByGender("%" + name + "%", new PageRequest(0, 10));
			model.addAttribute("searchGende", true);
		}
			
		model.addAttribute("search", name);
		model.addAttribute("searchActive", true);
		model.addAttribute("content", films);
		model.addAttribute("typeFilm", true);
		model.addAttribute("inputSearch", name);
		model.addAttribute("typeSearch", "/api/busqueda/" + optionSearch +"/peliculas/" + name + "/page");
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "pelicula");
		
		if(films.getNumberOfElements() == 0) {
			model.addAttribute("noElementsSearch", true);
			model.addAttribute("noResult", true);
		}
		
		if(films.getNumberOfElements() > 0 && films.getNumberOfElements() < 10) {
			model.addAttribute("noElementsSearch", true);
		}
		
		if (userComponent.isLoggedUser()) {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());

			model.addAttribute("userList", user.getLists());
		}
		
		return "search";
	}
	
	//Use of administration page
	@RequestMapping("/nuevaPelicula")
	public String newFilm(@RequestParam Optional<MultipartFile> imageFilm, @RequestParam String newName,
			@RequestParam String actors, @RequestParam String directors, @RequestParam Optional<String[]> newGenres,
			@RequestParam String synopsis, @RequestParam String trailer, @RequestParam String year) {
		String image;
		if (imageFilm.isPresent()) {
			image = commonCodeImages.uploadImage(newName, imageFilm.get());
		} else {
			image = "";
		}
		int yearInt = Integer.parseInt(year);
		Film film = new Film(newName, actors, directors, synopsis, image, trailer, yearInt);
		if (newGenres.isPresent()) {
			for (String genre : newGenres.get()) {
				film.getGenders().add(genderRepository.findByName(genre));
			}
		}
		filmRepository.save(film);
		String name = film.getName();
		return "redirect:/pelicula/" + name;
	}
}

package com.trackorjargh.apirestcontrolers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.trackorjargh.commoncode.CommonCodeFilm;
import com.trackorjargh.grafics.Grafics;
import com.trackorjargh.javaclass.CommentFilm;
import com.trackorjargh.javaclass.DeleteElementsOfBBDD;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointFilm;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.CommentFilmRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.PointFilmRepository;

@RestController
@RequestMapping("/api")
public class ApiFilmController {
	private final FilmRepository filmRepository;
	private final GenderRepository genderRepository;
	private final PointFilmRepository pointFilmRepository;
	private final CommonCodeFilm commonCodeFilm;
	private final DeleteElementsOfBBDD deleteElementofBBDD;
	private final CommentFilmRepository commentFilmRepository;

	@Autowired
	public ApiFilmController(FilmRepository filmRepository, PointFilmRepository pointFilmRepository,
			CommonCodeFilm commonCodeFilm, DeleteElementsOfBBDD deleteElementofBBDD,
			CommentFilmRepository commentFilmRepository, GenderRepository genderRepository) {
		this.filmRepository = filmRepository;
		this.pointFilmRepository = pointFilmRepository;
		this.commonCodeFilm = commonCodeFilm;
		this.deleteElementofBBDD = deleteElementofBBDD;
		this.commentFilmRepository = commentFilmRepository;
		this.genderRepository = genderRepository;
	}
	
	@RequestMapping(value = "/peliculas/administracion", method = RequestMethod.GET)
	@JsonView(Film.NameFilmAndType.class)
	public ResponseEntity<List<Film>> getAllFilms() {
		return new ResponseEntity<>(filmRepository.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/peliculas", method = RequestMethod.GET)
	@JsonView(basicInfoFilm.class)
	public Page<Film> getFilms(Pageable page) {
		return filmRepository.findAll(page);
	}

	public interface basicInfoFilm extends Film.BasicInformation, Gender.BasicInformation {
	}
	
	@RequestMapping(value = "/peliculas/{name}", method = RequestMethod.GET)
	@JsonView(basicInfoFilm.class)
	public ResponseEntity<Film> getFilm(@PathVariable String name) {
		Film film = filmRepository.findByNameIgnoreCase(name);

		if (film != null) {
			return new ResponseEntity<>(film, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/peliculas/ultimas/{num}", method = RequestMethod.GET)
	@JsonView(basicInfoFilm.class)
	public List<Film> getLastFilm(@PathVariable int num) {
		return filmRepository.findByLastAdded(num);
	}

	@RequestMapping(value = "/peliculas/mejorvaloradas", method = RequestMethod.GET)
	@JsonView(basicInfoFilm.class)
	public Page<Film> getBestPointPeliculas(Pageable page) {
		return filmRepository.findBestPointFilm(page);
	}
	
	@RequestMapping(value = "/peliculas/relacionadas/{id}", method = RequestMethod.GET)
	@JsonView(basicInfoFilm.class)
	public Page<Film> getFilmRelated(@PathVariable int id, Pageable page) {
		return filmRepository.findFilmsRelationsById(id, page);
	}

	@RequestMapping(value = "/peliculas/grafico", method = RequestMethod.GET)
	public List<Grafics> getBestPointFilms() {
		List<Film> films = filmRepository.findBestPointFilm(new PageRequest(0, 10)).getContent();
		List<PointFilm> listPoints;
		List<Grafics> graficFilms = new ArrayList<>();
		double points;

		for (Film film : films) {
			points = 0;

			listPoints = pointFilmRepository.findByFilm(film);

			if (listPoints.size() > 0) {
				for (PointFilm pf : listPoints)
					points += pf.getPoints();
				points /= listPoints.size();
			}

			graficFilms.add(new Grafics(film.getName(), points));
		}

		return graficFilms;
	}

	@RequestMapping(value = "/peliculas", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(basicInfoFilm.class)
	public ResponseEntity<Film> addFilm(@RequestBody Film film) {
		if (filmRepository.findByNameIgnoreCase(film.getName()) == null) {
			List<Gender> genders = new LinkedList<>();
			
			for (Gender genre : film.getGenders()) {
				genders.add(genderRepository.findByName(genre.getName()));
			}
			
			film.setGenders(genders);
			filmRepository.save(film);
			return new ResponseEntity<>(film, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.IM_USED);
		}
	}

	@RequestMapping(value = "/peliculas/{name}", method = RequestMethod.DELETE)
	@JsonView(Film.BasicInformation.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Film> deleteFilm(@PathVariable String name) {
		if (filmRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			Film deletedFilm = filmRepository.findByNameIgnoreCase(name);
			deleteElementofBBDD.deleteFilm(filmRepository.findByNameIgnoreCase(name));
			return new ResponseEntity<>(deletedFilm, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/peliculas/{name}", method = RequestMethod.PUT)
	@JsonView(basicInfoFilm.class)
	public ResponseEntity<Film> editFilm(@PathVariable String name, @RequestBody Film film) {
		if (filmRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			Film editedFilm = filmRepository.findByNameIgnoreCase(name);
			List<Gender> genders = new LinkedList<>();
			
			for (Gender genre : film.getGenders()) {
				genders.add(genderRepository.findByName(genre.getName()));
			}
			
			return new ResponseEntity<>(
					commonCodeFilm.editFilm(editedFilm, film.getName(), film.getActors(), film.getDirectors(),
							film.getImage(), genders, film.getSynopsis(), film.getTrailer(), film.getYear()),
					HttpStatus.OK);
		}
	}

	public interface basicInfoCommentFilm extends CommentFilm.BasicInformation, User.BasicInformation {
	}

	@RequestMapping(value = "/peliculas/comentarios/{name}", method = RequestMethod.GET)
	@JsonView(basicInfoCommentFilm.class)
	public ResponseEntity<List<CommentFilm>> getCommentsFilm(@PathVariable String name) {
		Film film = filmRepository.findByNameIgnoreCase(name);

		if (film != null) {
			return new ResponseEntity<>(commentFilmRepository.findByFilmOrderByIdAsc(film), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/peliculas/comentarios/{name}", method = RequestMethod.POST)
	@JsonView(basicInfoCommentFilm.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentFilm> addComentFilm(@PathVariable String name, @RequestBody CommentFilm comment) {
		if (filmRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					commonCodeFilm.addCommentFilm(filmRepository.findByNameIgnoreCase(name), comment.getComment()),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/peliculas/comentarios/{id}", method = RequestMethod.DELETE)
	@JsonView(basicInfoCommentFilm.class)
	public ResponseEntity<CommentFilm> deleteFilmComent(@PathVariable Long id) {
		if (commentFilmRepository.findById(id) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(commonCodeFilm.deleteCommentFilm(id), HttpStatus.OK);
		}
	}

	public interface joinedPointFilmUserInfo extends PointFilm.BasicInformation, Film.NameFilmInfo, User.NameUserInfo {
	}

	@RequestMapping(value = "/peliculas/puntos/{name}", method = RequestMethod.GET)
	@JsonView(joinedPointFilmUserInfo.class)
	public ResponseEntity<List<PointFilm>> getFilmPoint(@PathVariable String name) {
		Film film = filmRepository.findByNameIgnoreCase(name);

		if (film != null) {
			return new ResponseEntity<>(film.getPointsFilm(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/peliculas/puntos/{name}", method = RequestMethod.POST)
	@JsonView(joinedPointFilmUserInfo.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PointFilm> addfilmPoint(@PathVariable String name, @RequestBody PointFilm filmPoint) {
		if (filmRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					commonCodeFilm.updatePointsFilm(filmRepository.findByNameIgnoreCase(name), filmPoint.getPoints()),
					HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/busqueda/{optionSearch}/peliculas/{name}/page", method = RequestMethod.GET)
	@JsonView(Film.BasicInformation.class)
	public Page<Film> getSearchPeliculas(Pageable page, @PathVariable String optionSearch, @PathVariable String name) {
		if (optionSearch.equalsIgnoreCase("titulo")) {
			return filmRepository.findByNameContainingIgnoreCase(name, page);
		} else {
			return filmRepository.findFilmsByGender("%" + name + "%", page);
		}
	}
}

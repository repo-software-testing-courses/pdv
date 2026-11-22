package com.trackorjargh.javacontrollers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trackorjargh.commoncode.CommonCodeBook;
import com.trackorjargh.commoncode.CommonCodeFilm;
import com.trackorjargh.commoncode.CommonCodeImages;
import com.trackorjargh.commoncode.CommonCodeShow;
import com.trackorjargh.commoncode.CommonCodeUser;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.DeleteElementsOfBBDD;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.ShowRepository;
import com.trackorjargh.javarepository.UserRepository;

@Controller
public class AdminContoller {
	private final FilmRepository filmRepository;
	private final ShowRepository showRepository;
	private final BookRepository bookRepository;
	private final GenderRepository genderRepository;
	private final UserRepository userRepository;
	private final UserComponent userComponent;
	private final DeleteElementsOfBBDD deleteElementsOfBBDD;
	private final CommonCodeFilm commonCodeFilm;
	private final CommonCodeShow commonCodeShow;
	private final CommonCodeBook commonCodeBook;
	private final CommonCodeUser commonCodeUser;
	private final CommonCodeImages commonCodeImages;

	@Autowired
	public AdminContoller(FilmRepository filmRepository, ShowRepository showRepository, BookRepository bookRepository,
			GenderRepository genderRepository, UserRepository userRepository, UserComponent userComponent,
			DeleteElementsOfBBDD deleteElementsOfBBDD, CommonCodeFilm commonCodeFilm, CommonCodeShow commonCodeShow,
			CommonCodeBook commonCodeBook, CommonCodeUser commonCodeUser, CommonCodeImages commonCodeImages) {
		this.filmRepository = filmRepository;
		this.showRepository = showRepository;
		this.bookRepository = bookRepository;
		this.genderRepository = genderRepository;
		this.userRepository = userRepository;
		this.userComponent = userComponent;
		this.deleteElementsOfBBDD = deleteElementsOfBBDD;
		this.commonCodeFilm = commonCodeFilm;
		this.commonCodeShow = commonCodeShow;
		this.commonCodeBook = commonCodeBook;
		this.commonCodeUser = commonCodeUser;
		this.commonCodeImages = commonCodeImages;
	}

	@RequestMapping("/administracion")
	public String serveAdmin(Model model) {
		model.addAttribute("adminActive", true);

		List<User> users = userRepository.findAll();
		User userRemove = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());
		users.remove(userRemove);

		model.addAttribute("users", users);
		model.addAttribute("films", filmRepository.findAll());
		model.addAttribute("shows", showRepository.findAll());
		model.addAttribute("books", bookRepository.findAll());

		return "administration";
	}

	@RequestMapping("/subirContenido")
	public String newContent(Model model) {
		model.addAttribute("genres", genderRepository.findAll());
		return "newContent";
	}

	@RequestMapping("/seleccionarPelicula")
	public ModelAndView filmSelection(RedirectAttributes redir, @RequestParam String name) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/administracion");
		Film film = filmRepository.findByNameIgnoreCase(name);
		redir.addFlashAttribute("adminFilm", true);
		redir.addFlashAttribute("film", film);
		redir.addFlashAttribute("genres", genderRepository.findByFilms(film));
		redir.addFlashAttribute("genresNotInFilm", genderRepository.findByNotInFilm(film.getId()));

		return modelAndView;
	}

	@RequestMapping("/seleccionarSerie")
	public ModelAndView showSelection(RedirectAttributes redir, @RequestParam String name) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/administracion");
		Shows show = showRepository.findByNameIgnoreCase(name);
		redir.addFlashAttribute("adminShow", true);
		redir.addFlashAttribute("show", show);
		redir.addFlashAttribute("genres", genderRepository.findByShows(show));
		redir.addFlashAttribute("genresNotInShow", genderRepository.findByNotInShow(show.getId()));

		return modelAndView;
	}

	@RequestMapping("/seleccionarLibro")
	public ModelAndView bookSelection(RedirectAttributes redir, @RequestParam String name) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/administracion");
		Book book = bookRepository.findByNameIgnoreCase(name);
		redir.addFlashAttribute("adminBook", true);
		redir.addFlashAttribute("book", book);
		redir.addFlashAttribute("genres", genderRepository.findByBooks(book));
		redir.addFlashAttribute("genresNotInBook", genderRepository.findByNotInBook(book.getId()));

		return modelAndView;
	}

	@RequestMapping("/seleccionarUsuario")
	public ModelAndView userSelection(RedirectAttributes redir, @RequestParam String name) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/administracion");
		User user = userRepository.findByNameIgnoreCase(name);
		if (user.getRoles().size() == 3) {
			redir.addFlashAttribute("isAdmin", true);
		} else {
			if (user.getRoles().size() == 2) {
				redir.addFlashAttribute("isModerator", true);
			}
		}
		redir.addFlashAttribute("adminUser", true);
		redir.addFlashAttribute("user", user);
		return modelAndView;
	}

	@RequestMapping("editarContenido/{type}/{name}")
	public ModelAndView editContent(RedirectAttributes redir, @PathVariable String type, @PathVariable String name) {
		switch (type) {
		case "pelicula":
			return filmSelection(redir, name);
		case "serie":
			return showSelection(redir, name);
		case "libro":
			return bookSelection(redir, name);
		default:
			return filmSelection(redir, name);
		}
	}

	@RequestMapping("/adminUsuario")
	public String adminUser(Model model, @RequestParam String name, @RequestParam String email,
			@RequestParam Optional<Boolean> confirmDelete, @RequestParam String userType) {
		User user = userRepository.findByNameIgnoreCase(name);
		if (confirmDelete.isPresent()) {
			deleteElementsOfBBDD.deleteUser(user);
		} else {
			user.setRoles(new LinkedList<String>());
			if (userType.equals("Usuario")) {
				user.setRoles(new LinkedList<String>());
				user.getRoles().add("ROLE_USER");
			} else {
				if (userType.equals("Moderador")) {
					user.getRoles().add("ROLE_USER");
					user.getRoles().add("ROLE_MODERATOR");
				} else {
					user.getRoles().add("ROLE_USER");
					user.getRoles().add("ROLE_MODERATOR");
					user.getRoles().add("ROLE_ADMIN");
				}
			}
			user = commonCodeUser.editUser(user, email, "", user.getRoles(), user.getImage(), false);
			if (user.getName().equals(userComponent.getLoggedUser().getName())) {
				userComponent.setLoggedUser(user);
			}
		}

		return "redirect:/administracion";
	}

	@RequestMapping("/adminPelicula")
	public String adminFilm(Model model, @RequestParam String name, @RequestParam String newName,
			@RequestParam Optional<Boolean> confirmDelete, @RequestParam String actors, @RequestParam String directors,
			@RequestParam MultipartFile imageFilm, @RequestParam Optional<String[]> genreContent,
			@RequestParam Optional<String[]> newGenres, @RequestParam String synopsis, @RequestParam String trailer,
			@RequestParam String year) {
		Film film = filmRepository.findByNameIgnoreCase(name);
		if (confirmDelete.isPresent()) {
			deleteElementsOfBBDD.deleteFilm(film);
		} else {
			int yearInt = Integer.parseInt(year);
			List<Gender> genders = new LinkedList<>();
			if (genreContent.isPresent()) {
				for (String genre : genreContent.get()) {
					genders.add(genderRepository.findByName(genre));
				}
			}
			if (newGenres.isPresent()) {
				for (String genre : newGenres.get()) {
					genders.add(genderRepository.findByName(genre));
				}
			}
			if (!imageFilm.isEmpty()) {
				film.setImage(commonCodeImages.uploadImage(film.getName(), imageFilm));
			}

			commonCodeFilm.editFilm(film, newName, actors, directors, film.getImage(), genders, synopsis, trailer,
					yearInt);
		}

		return "redirect:/administracion";
	}

	@RequestMapping("/adminSerie")
	public String adminShow(Model model, @RequestParam String name, @RequestParam String newName,
			@RequestParam Optional<Boolean> confirmDelete, @RequestParam String actors, @RequestParam String directors,
			@RequestParam MultipartFile imageShow, @RequestParam Optional<String[]> genreContent,
			@RequestParam Optional<String[]> newGenres, @RequestParam String synopsis, @RequestParam String trailer,
			@RequestParam String year) {
		Shows show = showRepository.findByNameIgnoreCase(name);
		if (confirmDelete.isPresent()) {
			deleteElementsOfBBDD.deleteShow(show);
		} else {
			int yearInt = Integer.parseInt(year);
			List<Gender> genders = new LinkedList<>();
			if (genreContent.isPresent()) {
				for (String genre : genreContent.get()) {
					genders.add(genderRepository.findByName(genre));
				}
			}
			if (newGenres.isPresent()) {
				for (String genre : newGenres.get()) {
					genders.add(genderRepository.findByName(genre));
				}
			}
			if (!imageShow.isEmpty()) {
				show.setImage(commonCodeImages.uploadImage(show.getName(), imageShow));
			}

			commonCodeShow.editShow(show, newName, actors, directors, show.getImage(), genders, synopsis, trailer,
					yearInt);
		}

		return "redirect:/administracion";
	}

	@RequestMapping("/adminLibro")
	public String adminBook(Model model, @RequestParam String name, @RequestParam String newName,
			@RequestParam Optional<Boolean> confirmDelete, @RequestParam String authors,
			@RequestParam MultipartFile imageBook, @RequestParam Optional<String[]> genreContent,
			@RequestParam Optional<String[]> newGenres, @RequestParam String synopsis, @RequestParam String year) {
		Book book = bookRepository.findByNameIgnoreCase(name);
		if (confirmDelete.isPresent()) {
			deleteElementsOfBBDD.deleteBook(book);
		} else {
			int yearInt = Integer.parseInt(year);
			List<Gender> genders = new LinkedList<>();
			if (genreContent.isPresent()) {
				for (String genre : genreContent.get()) {
					genders.add(genderRepository.findByName(genre));
				}
			}
			if (newGenres.isPresent()) {
				for (String genre : newGenres.get()) {
					genders.add(genderRepository.findByName(genre));
				}
			}
			if (!imageBook.isEmpty()) {
				book.setImage(commonCodeImages.uploadImage(book.getName(), imageBook));
			}

			commonCodeBook.editBook(book, newName, authors, book.getImage(), genders, synopsis, yearInt);
		}

		return "redirect:/administracion";
	}

	@RequestMapping("/nuevaSerie")
	public String newShow(@RequestParam Optional<MultipartFile> imageShow, @RequestParam String newName,
			@RequestParam String actors, @RequestParam String directors, @RequestParam Optional<String[]> newGenres,
			@RequestParam String synopsis, @RequestParam String trailer, @RequestParam String year) {
		String image;
		if (imageShow.isPresent()) {
			image = commonCodeImages.uploadImage(newName, imageShow.get());
		} else {
			image = "";
		}
		int yearInt = Integer.parseInt(year);
		Shows show = new Shows(newName, actors, directors, synopsis, image, trailer, yearInt);
		if (newGenres.isPresent()) {
			for (String genre : newGenres.get()) {
				show.getGenders().add(genderRepository.findByName(genre));
			}
		}
		showRepository.save(show);
		String name = show.getName();
		return "redirect:/serie/" + name;
	}

	@RequestMapping("/nuevoLibro")
	public String newBook(@RequestParam Optional<MultipartFile> imageBook, @RequestParam String newName,
			@RequestParam String authors, @RequestParam Optional<String[]> newGenres, @RequestParam String synopsis,
			@RequestParam String year) {
		String image;
		if (imageBook.isPresent()) {
			image = commonCodeImages.uploadImage(newName, imageBook.get());
		} else {
			image = "";
		}
		int yearInt = Integer.parseInt(year);
		Book book = new Book(newName, authors, synopsis, image, yearInt);
		if (newGenres.isPresent()) {
			for (String genre : newGenres.get()) {
				book.getGenders().add(genderRepository.findByName(genre));
			}
		}
		bookRepository.save(book);
		String name = book.getName();
		return "redirect:/libro/" + name;
	}
}

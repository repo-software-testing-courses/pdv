package com.trackorjargh.apirestcontrolers;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackorjargh.commoncode.CommonCodeUser;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Lists;
import com.trackorjargh.javaclass.PreparateListsShow;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.ListsRepository;
import com.trackorjargh.javarepository.ShowRepository;
import com.trackorjargh.javarepository.UserRepository;

@RestController
@RequestMapping("/api")
public class ApiListsController {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ListsRepository listsRepository;
	private final UserRepository userRepository;
	private final UserComponent userComponent;
	private final CommonCodeUser commonCodeUser;
	private final FilmRepository filmRepository;
	private final ShowRepository showRepository;
	private final BookRepository bookRepository;

	@Autowired
	public ApiListsController(ListsRepository listsRepository, UserRepository userRepository,
			UserComponent userComponent, CommonCodeUser commonCodeUser, FilmRepository filmRepository,
			ShowRepository showRepository, BookRepository bookRepository) {
		this.listsRepository = listsRepository;
		this.userRepository = userRepository;
		this.userComponent = userComponent;
		this.commonCodeUser = commonCodeUser;
		this.filmRepository = filmRepository;
		this.showRepository = showRepository;
		this.bookRepository = bookRepository;
	}

	@RequestMapping(value = "/listas", method = RequestMethod.GET)
	@JsonView(Lists.BasicInformation.class)
	public ResponseEntity<List<Lists>> getListsUser() {
		if (!userComponent.isLoggedUser()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());
			return new ResponseEntity<>(user.getLists(), HttpStatus.OK);
		}
	}

	public interface basicInfoLists extends PreparateListsShow.BasicInformation, Film.NameFilmAndType, Shows.NameFilmAndType, Book.NameBookAndType {
	}
	
	@RequestMapping(value = "/listas/contenido", method = RequestMethod.GET)
	@JsonView(basicInfoLists.class)
	public ResponseEntity<List<PreparateListsShow>> getContentListsUser() {		
		List<PreparateListsShow> listsUser = new LinkedList<>();
		for (Lists list : listsRepository.findByUser(userComponent.getLoggedUser()))
			listsUser.add(new PreparateListsShow(list.getName(), list.getFilms(), list.getBooks(), list.getShows()));
		
		return new ResponseEntity<>(listsUser, HttpStatus.OK);
	}

	public interface basicInfoNewLists extends Lists.BasicInformation, User.NameUserInfo {
	}
	
	@RequestMapping(value = "/listas", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(basicInfoNewLists.class)
	@Transactional
	public ResponseEntity<Lists> addEmptyListInUser(@RequestBody Lists newList) {
		Lists listUser = listsRepository.findByUserAndName(userComponent.getLoggedUser(), newList.getName());

		if (listUser == null) {
			Lists createList = commonCodeUser.addEmptyListInUser(newList.getName());
			logger.info("Created list: {}", createList.getName());
			return new ResponseEntity<>(createList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.IM_USED);
		}
	}
	
	@RequestMapping(value = "/listas", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> addedListInUser(@RequestBody Lists list) {
		Lists listUser = listsRepository.findByUserAndName(userComponent.getLoggedUser(), list.getName());

		if (!listUser.getUser().getName().equals(userComponent.getLoggedUser().getName())) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
			
		if(!list.getFilms().isEmpty()) {
			Film film = filmRepository.findByNameIgnoreCase(list.getFilms().get(0).getName());
			
			if (listUser.getFilms().contains(film)) {
				return new ResponseEntity<>(false, HttpStatus.OK);
			}

			listUser.getFilms().add(film);
		} else if(!list.getShows().isEmpty()) {
			Shows show = showRepository.findByNameIgnoreCase(list.getShows().get(0).getName());
			
			if (listUser.getShows().contains(show)) {
				return new ResponseEntity<>(false, HttpStatus.OK);
			}

			listUser.getShows().add(show);
		} else if(!list.getBooks().isEmpty()) {
			Book book = bookRepository.findByNameIgnoreCase(list.getBooks().get(0).getName());
			
			if (listUser.getBooks().contains(book)) {
				return new ResponseEntity<>(false, HttpStatus.OK);
			}

			listUser.getBooks().add(book);
		}
		
		listsRepository.save(listUser);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	@RequestMapping(value = "/listas/{nameList}", method = RequestMethod.DELETE)
	@JsonView(Lists.BasicInformation.class)
	public ResponseEntity<Boolean> deletedListInUser(@PathVariable String nameList) {
		Lists listUser = listsRepository.findByUserAndName(userComponent.getLoggedUser(), nameList);

		if (!listUser.getUser().getName().equals(userComponent.getLoggedUser().getName())) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			logger.info("Deleted list: {}", listUser.getName());
			listsRepository.delete(listUser);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/listas/{nameList}/{typeContent}/{nameContent}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deletedContentInList(@PathVariable String nameList, @PathVariable String typeContent,
			@PathVariable String nameContent) {
		Lists listUser = listsRepository.findByUserAndName(userComponent.getLoggedUser(), nameList);

		if (!listUser.getUser().getName().equals(userComponent.getLoggedUser().getName())) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		if (typeContent.equalsIgnoreCase("pelicula")) {
			Film film = filmRepository.findByNameIgnoreCase(nameContent);
			listUser.getFilms().remove(film);

		} else if (typeContent.equalsIgnoreCase("serie")) {
			Shows show = showRepository.findByNameIgnoreCase(nameContent);
			listUser.getShows().remove(show);

		} else if (typeContent.equalsIgnoreCase("libro")) {
			Book book = bookRepository.findByNameIgnoreCase(nameContent);
			listUser.getBooks().remove(book);
		}

		listsRepository.save(listUser);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
}

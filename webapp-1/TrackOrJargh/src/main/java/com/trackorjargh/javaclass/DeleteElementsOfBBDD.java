package com.trackorjargh.javaclass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.CommentBookRepository;
import com.trackorjargh.javarepository.CommentFilmRepository;
import com.trackorjargh.javarepository.CommentShowRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.ListsRepository;
import com.trackorjargh.javarepository.PointBookRepository;
import com.trackorjargh.javarepository.PointFilmRepository;
import com.trackorjargh.javarepository.PointShowRepository;
import com.trackorjargh.javarepository.ShowRepository;
import com.trackorjargh.javarepository.UserRepository;

@Component
public class DeleteElementsOfBBDD {
	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ShowRepository showRepository;
	@Autowired
	private GenderRepository genderRepository;
	@Autowired
	private CommentFilmRepository commentFilmRepository;
	@Autowired
	private CommentShowRepository commentShowRepository;
	@Autowired
	private CommentBookRepository commentBookRepository;
	@Autowired
	private PointFilmRepository pointFilmRepository;
	@Autowired
	private PointShowRepository pointShowRepository;
	@Autowired
	private PointBookRepository pointBookRepository;
	@Autowired
	private ListsRepository listsRepository;
	
	public void deleteUser(User user) {
		Long id = user.getId();
		
		commentBookRepository.removeCommentsBooksByUserId(id);
		commentFilmRepository.removeCommentsFilmsByUserId(id);
		commentShowRepository.removeCommentsShowsByUserId(id);
		
		pointBookRepository.removePointsBooksByUserId(id);
		pointFilmRepository.removePointsFilmsByUserId(id);
		pointShowRepository.removePointsShowsByUserId(id);
		
		listsRepository.removeListsByUserId(id);
		
		userRepository.removeRoles(id);
		
		userRepository.delete(user);
	}
	
	public void deleteBook(Book book) {
		Long id = book.getId();
		
		genderRepository.removePointsBooksByBookId(id);
		listsRepository.removeBooksListById(id);
		commentBookRepository.removeCommentsBooksByBookId(id);
		pointBookRepository.removePointsBooksByBookId(id);	
		
		bookRepository.delete(book);
	}
	
	public void deleteFilm(Film film) {		
		Long id = film.getId();
		
		genderRepository.removePointsFilmsByFilmId(id);
		listsRepository.removeFilmsListById(id);
		commentFilmRepository.removeCommentsFilmssByFilmId(id);
		pointFilmRepository.removePointsFilmsByFilmId(id);
		
		filmRepository.delete(film);
	}
	
	public void deleteShow(Shows show) {
		Long id = show.getId();
		
		genderRepository.removePointShowsByShowId(id);
		listsRepository.removeShowsListById(id);
		commentShowRepository.removeCommentsShowsByShowId(id);
		pointShowRepository.removePointsShowsByShowId(id);
		
		showRepository.delete(show);
	}
}

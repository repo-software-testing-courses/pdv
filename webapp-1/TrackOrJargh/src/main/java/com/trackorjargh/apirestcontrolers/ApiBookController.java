package com.trackorjargh.apirestcontrolers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import com.trackorjargh.commoncode.CommonCodeBook;
import com.trackorjargh.grafics.Grafics;
import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.CommentBook;
import com.trackorjargh.javaclass.DeleteElementsOfBBDD;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointBook;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.CommentBookRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.PointBookRepository;

@RestController
@RequestMapping("/api")
public class ApiBookController {
	
	private final BookRepository bookRepository;
	private final GenderRepository genderRepository;
	private final PointBookRepository pointBookRepository;
	private final DeleteElementsOfBBDD deleteElementofBBDD;
	private final CommonCodeBook commonCode;
	private final CommentBookRepository commentBookRepository;
	
	public ApiBookController(BookRepository bookRepository, PointBookRepository pointBookRepository,
			DeleteElementsOfBBDD deleteElementofBBDD, CommonCodeBook commonCode,
			CommentBookRepository commentBookRepository, GenderRepository genderRepository) {
		super();
		this.bookRepository = bookRepository;
		this.pointBookRepository = pointBookRepository;
		this.deleteElementofBBDD = deleteElementofBBDD;
		this.commonCode = commonCode;
		this.commentBookRepository = commentBookRepository;
		this.genderRepository = genderRepository;
	}
	
	@RequestMapping(value = "/libros/administracion", method = RequestMethod.GET)
	@JsonView(Book.NameBookAndType.class)
	public ResponseEntity<List<Book>> getAllBooks() {
		return new ResponseEntity<>(bookRepository.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/libros", method = RequestMethod.GET)
	@JsonView(basicInfoBooks.class)
	public Page<Book> getBooks(Pageable page) {
		return bookRepository.findAll(page);
	}
	
	public interface basicInfoBooks extends Book.BasicInformation, Gender.BasicInformation {
	}
	
	@RequestMapping(value = "/libros/{name}", method = RequestMethod.GET)
	@JsonView(basicInfoBooks.class)
	public ResponseEntity<Book> getBook(@PathVariable String name) {
		Book book = bookRepository.findByNameIgnoreCase(name);
		
		if (book == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(book, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/libros/relacionadas/{id}", method = RequestMethod.GET)
	@JsonView(basicInfoBooks.class)
	public Page<Book> getBookRelated(@PathVariable int id, Pageable page) {
		return bookRepository.findBooksRelationsById(id, page);
	}
	
	@RequestMapping(value = "/libros/ultimas/{num}", method = RequestMethod.GET)
	@JsonView(basicInfoBooks.class)
	public List<Book> getLastBook(@PathVariable int num) {
		return bookRepository.findByLastAdded(num);
	}
	
	@RequestMapping(value = "/libros/mejorvalorados", method = RequestMethod.GET)
	@JsonView(basicInfoBooks.class)
	public Page<Book> getBestPointLibros(Pageable page) {
		return bookRepository.findBestPointBook(page);
	}
	
	@RequestMapping(value = "/libros/grafico", method = RequestMethod.GET)
	public List<Grafics> getBestPointBooks() {
		List<Book> books = bookRepository.findBestPointBook(new PageRequest(0, 10)).getContent();
		List<PointBook> listPoints;
		List<Grafics> graficShows = new ArrayList<>();
		double points;

		for (Book book : books) {
			points = 0;

			listPoints = pointBookRepository.findByBook(book);

			if (listPoints.size() > 0) {
				for (PointBook sb : listPoints)
					points += sb.getPoints();
				points /= listPoints.size();
			}

			graficShows.add(new Grafics(book.getName(), points));
		}

		return graficShows;
	}
	
	@RequestMapping(value = "/libros", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(basicInfoBooks.class)
	public ResponseEntity<Book> addBook(@RequestBody Book book) {
		if (bookRepository.findByNameIgnoreCase(book.getName()) == null) {
			List<Gender> genders = new LinkedList<>();
			
			for (Gender genre : book.getGenders()) {
				genders.add(genderRepository.findByName(genre.getName()));
			}
			
			book.setGenders(genders);
			bookRepository.save(book);
			return new ResponseEntity<>(book, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.IM_USED);
		}
	}
	
	@RequestMapping(value = "/libros/{name}", method = RequestMethod.DELETE)
	@JsonView(Book.BasicInformation.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Book> deleteBook(@PathVariable String name) {
		if (bookRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			Book deletedBook = bookRepository.findByNameIgnoreCase(name);
			deleteElementofBBDD.deleteBook(bookRepository.findByNameIgnoreCase(name));
			return new ResponseEntity<>(deletedBook, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/libros/{name}", method = RequestMethod.PUT)
	@JsonView(basicInfoBooks.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Book> editBook(@PathVariable String name, @RequestBody Book book) {
		if (bookRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			Book editedBook = bookRepository.findByNameIgnoreCase(name);
			List<Gender> genders = new LinkedList<>();
			
			for (Gender genre : book.getGenders()) {
				genders.add(genderRepository.findByName(genre.getName()));
			}
			
			return new ResponseEntity<>(commonCode.editBook(editedBook, book.getName(), book.getAuthors(),
					book.getImage(), genders, book.getSynopsis(), book.getYear()), HttpStatus.OK);
		}
	}
	
	public interface basicInfoCommentBook extends CommentBook.BasicInformation, User.BasicInformation {
	}

	@RequestMapping(value = "/libros/comentarios/{name}", method = RequestMethod.GET)
	@JsonView(basicInfoCommentBook.class)
	public ResponseEntity<List<CommentBook>> getCommentsBook(@PathVariable String name) {
		Book book = bookRepository.findByNameIgnoreCase(name);
		
		if (book == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(commentBookRepository.findByBookOrderByIdAsc(book), HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/libros/comentarios/{name}", method = RequestMethod.POST)
	@JsonView(basicInfoCommentBook.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentBook> addComentBook(@PathVariable String name, @RequestBody CommentBook comment) {
		if (bookRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					commonCode.addCommentBook(bookRepository.findByNameIgnoreCase(name), comment.getComment()),
					HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/libros/comentarios/{id}", method = RequestMethod.DELETE)
	@JsonView(basicInfoCommentBook.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentBook> deleteBookComent(@PathVariable Long id) {
		if (commentBookRepository.findById(id) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(commonCode.deleteCommentBook(id), HttpStatus.OK);
		}
	}
	
	public interface joinedPointBookUserInfo extends PointBook.BasicInformation, Book.NameBookInfo, User.NameUserInfo {
	}

	@RequestMapping(value = "/libros/puntos/{name}", method = RequestMethod.POST)
	@JsonView(joinedPointBookUserInfo.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PointBook> addBookPoint(@PathVariable String name, @RequestBody PointBook bookPoint) {
		if (bookRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					commonCode.updatePointsBook(bookRepository.findByNameIgnoreCase(name), bookPoint.getPoints()),
					HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/libros/puntos/{name}", method = RequestMethod.GET)
	@JsonView(joinedPointBookUserInfo.class)
	public ResponseEntity<List<PointBook>> getBookPoint(@PathVariable String name) {
		Book book = bookRepository.findByNameIgnoreCase(name);
		
		if(book == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(book.getPointsBook(),HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/busqueda/{optionSearch}/libros/{name}/page", method = RequestMethod.GET)
	@JsonView(Book.BasicInformation.class)
	public Page<Book> getSearchLibros(Pageable page, @PathVariable String optionSearch, @PathVariable String name) {
		if (optionSearch.equalsIgnoreCase("titulo")) {
			return bookRepository.findByNameContainingIgnoreCase(name, page);
		} else {
			return bookRepository.findBooksByGender("%" + name + "%", page);
		}
	}

}

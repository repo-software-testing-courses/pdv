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

import com.trackorjargh.commoncode.CommonCodeBook;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.CommentBook;
import com.trackorjargh.javaclass.PointBook;
import com.trackorjargh.javaclass.PreparateMessageShow;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.CommentBookRepository;
import com.trackorjargh.javarepository.PointBookRepository;
import com.trackorjargh.javarepository.UserRepository;

@Controller
public class BookController {
	final BookRepository bookRepository;
	final CommentBookRepository commentBookRepository;
	final PointBookRepository pointBookRepository;
	final UserRepository userRepository;
	final UserComponent userComponent;
	final CommonCodeBook commonCodeBook;
	
	@Autowired
	public BookController(BookRepository bookRepository, CommentBookRepository commentBookRepository,
			PointBookRepository pointBookRepository, UserRepository userRepository, UserComponent userComponent,
			CommonCodeBook commonCodeBook) {
		this.bookRepository = bookRepository;
		this.commentBookRepository = commentBookRepository;
		this.pointBookRepository = pointBookRepository;
		this.userRepository = userRepository;
		this.userComponent = userComponent;
		this.commonCodeBook = commonCodeBook;
	}

	@RequestMapping({ "/libros", "/libros/mejorvalorados" })
	public String serveBookList(Model model, HttpServletRequest request) {
		List<Book> books = bookRepository.findByLastAdded(5);
		books.get(0).setFirstInList(true);

		if (userComponent.isLoggedUser()) {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());

			model.addAttribute("userList", user.getLists());
		}

		Page<Book> booksPage;
		String typePage;
		if (request.getServletPath().equalsIgnoreCase("/libros")) {
			booksPage = bookRepository.findAll(new PageRequest(0, 10));
			model.addAttribute("contentShowButton", true);
			typePage = "/api/libros";
		} else {
			booksPage = bookRepository.findBestPointBook(new PageRequest(0, 10));
			model.addAttribute("bestPointContentShowButton", true);
			typePage = "/api/libros/mejorvalorados";
		}

		if (booksPage.getNumberOfElements() > 0 && booksPage.getNumberOfElements() < 10) {
			model.addAttribute("noElementsSearch", true);
		}

		model.addAttribute("linkContent", "/libros");
		model.addAttribute("linkBestPointContent", "/libros/mejorvalorados");
		model.addAttribute("content", booksPage);
		model.addAttribute("typePage", typePage);
		model.addAttribute("booksActive", true);
		model.addAttribute("contentCarousel", books);
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "libro");

		return "contentList";
	}
	
	@RequestMapping("/libro/{name}")
	public String serveProfile(Model model, @PathVariable String name, @RequestParam Optional<String> messageSent,
			@RequestParam Optional<String> pointsSent) {
		Book book = bookRepository.findByNameIgnoreCase(name);

		if (messageSent.isPresent()) {
			commonCodeBook.addCommentBook(book, messageSent.get());
		}

		if (pointsSent.isPresent()) {
			double points = Double.parseDouble(pointsSent.get());
			commonCodeBook.updatePointsBook(book, points);
		}

		List<PreparateMessageShow> listMessages = new LinkedList<>();
		for (CommentBook cb : commentBookRepository.findByBook(book))
			listMessages.add(cb.preparateShowMessage());

		model.addAttribute("content", bookRepository.findByNameIgnoreCase(name));
		model.addAttribute("comments", listMessages);
		model.addAttribute("typeContent", "el libro");
		model.addAttribute("isBook", true);
		model.addAttribute("actionMessage", "/libro/" + name);

		double points = 0;
		double userPoints = 0;

		List<PointBook> listPoints = pointBookRepository.findByBook(book);

		if (listPoints.size() > 0) {
			for (PointBook pb : listPoints)
				points += pb.getPoints();
			points /= listPoints.size();
		}

		PointBook userPointBook = pointBookRepository.findByUserAndBook(userComponent.getLoggedUser(), book);
		if (userPointBook != null)
			if (userPointBook != null)
				userPoints = userPointBook.getPoints();

		model.addAttribute("totalPoints", points);
		model.addAttribute("userPoints", userPoints);

		model.addAttribute("contentRelation",
				bookRepository.findBooksRelationsById(book.getId(), new PageRequest(0, 8)));
		model.addAttribute("iconBook", true);
		model.addAttribute("deleteComment", "/libro/borrarcomentario/");

		return "contentProfile";
	}
	
	@RequestMapping("/libro/borrarcomentario/{id}/{name}")
	public String deleteCommentBook(Model model, @PathVariable int id, @PathVariable String name) {
		commonCodeBook.deleteCommentBook(new Long(id));

		return "redirect:/libro/" + name;
	}	
	
	//Use to Search
	@RequestMapping("/busqueda/{optionSearch}/libro/{name}")
	public String searchBooks(Model model, @PathVariable String optionSearch, @PathVariable String name) {		
		Page<Book> books;
		
		if(optionSearch.equalsIgnoreCase("titulo")) {
			books = bookRepository.findByNameContainingIgnoreCase(name, new PageRequest(0, 10));
			model.addAttribute("searchTitle", true);
		} else {
			books = bookRepository.findBooksByGender("%" + name + "%", new PageRequest(0, 10));
			model.addAttribute("searchGende", true);
		}
		
		model.addAttribute("search", name);
		model.addAttribute("searchActive", true);
		model.addAttribute("content", books);		
		model.addAttribute("typeBook", true);
		model.addAttribute("inputSearch", name);
		model.addAttribute("typeSearch", "/api/busqueda/" + optionSearch +"/libros/" + name + "/page");
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "libro");
		
		if(books.getNumberOfElements() == 0) {
			model.addAttribute("noElementsSearch", true);
			model.addAttribute("noResult", true);
		}
		
		if(books.getNumberOfElements() > 0 && books.getNumberOfElements() < 10) {
			model.addAttribute("noElementsSearch", true);
		}
		
		if (userComponent.isLoggedUser()) {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());

			model.addAttribute("userList", user.getLists());
		}
		
		return "search";
	}
}

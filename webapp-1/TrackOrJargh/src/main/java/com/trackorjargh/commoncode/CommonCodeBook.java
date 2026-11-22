package com.trackorjargh.commoncode;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.CommentBook;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointBook;
import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.CommentBookRepository;
import com.trackorjargh.javarepository.PointBookRepository;

@Service
public class CommonCodeBook {
	final BookRepository bookRepository;
	final PointBookRepository pointBookRepository;
	final CommentBookRepository commentBookRepository;
	final UserComponent userComponent;
	
	@Autowired
	public CommonCodeBook(BookRepository bookRepository, PointBookRepository pointBookRepository,
			CommentBookRepository commentBookRepository, UserComponent userComponent) {
		this.bookRepository = bookRepository;
		this.pointBookRepository = pointBookRepository;
		this.commentBookRepository = commentBookRepository;
		this.userComponent = userComponent;
	}

	public PointBook updatePointsBook(Book book, double points) {
		PointBook pointBook = pointBookRepository.findByUserAndBook(userComponent.getLoggedUser(), book);

		if (pointBook == null) {
			pointBook = new PointBook(points);
			pointBook.setBook(book);
			pointBook.setUser(userComponent.getLoggedUser());
		} else {
			pointBook.setPoints(points);
		}

		pointBookRepository.save(pointBook);
		return pointBook;
	}
	
	public CommentBook addCommentBook(Book book, String messageUser) {
		CommentBook message = new CommentBook(messageUser);
		message.setBook(book);
		message.setUser(userComponent.getLoggedUser());

		commentBookRepository.save(message);
		return message;
	}
	
	public CommentBook deleteCommentBook(Long id) {
		CommentBook comment = commentBookRepository.findById(new Long(id));
		commentBookRepository.delete(comment);

		return comment;
	}
	
	public Book editBook(Book book, String newName, String authors, String imageBook, List<Gender> genders,
			String synopsis, int year) {
		if (newName != null) {
			book.setName(newName);
		}
		if (authors != null) {
			book.setAuthors(authors);
		}
		if (synopsis != null) {
			book.setSynopsis(synopsis);
		}
		if (year >= 0) {
			book.setYear(year);
		}
		if (!genders.isEmpty()) {
			book.setGenders(genders);
		}
		if (imageBook != null) {
			book.setImage(imageBook);
		}
		bookRepository.save(book);
		return book;
	}
}

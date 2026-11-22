package com.trackorjargh.javaclass;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class CommentBook {
	
	public interface BasicInformation {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicInformation.class)
	private Long id;
	
	@ManyToOne
	private Book book;
	
	@OneToOne
	@JsonView(BasicInformation.class)
	private User user;
	
	@JsonView(BasicInformation.class)
	private String comment;

	public CommentBook() {
	}

	public CommentBook(String comment) {
		this.comment = comment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public PreparateMessageShow preparateShowMessage() {
		return new PreparateMessageShow(this.id, this.user.getName(), this.comment);
	}
}

package com.trackorjargh.javaclass;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class CommentFilm {
	
	public interface BasicInformation {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicInformation.class)
	private Long id;
	
	@ManyToOne
	private Film film;

	@OneToOne
	@JsonView(BasicInformation.class)
	private User user;
	
	@JsonView(BasicInformation.class)
	private String comment;

	public CommentFilm() {
	}

	public CommentFilm(String comment) {
		this.comment = comment;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
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

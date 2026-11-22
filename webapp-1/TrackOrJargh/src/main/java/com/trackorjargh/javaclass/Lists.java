package com.trackorjargh.javaclass;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Lists {
	public interface BasicInformation {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicInformation.class)
	private Long id;
	
	@JsonView(BasicInformation.class)
	private String name;
	
	@ManyToOne
	@JsonIgnore
	@JsonView(BasicInformation.class)
	private User user;
	
	@ManyToMany
	private List<Film> films = new LinkedList<>();
	
	@ManyToMany
	private List<Shows> shows = new LinkedList<>();
	
	@ManyToMany
	private List<Book> books = new LinkedList<>();

	public Lists() {
	}

	public Lists(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Film> getFimls() {
		return films;
	}

	public void setFimls(List<Film> films) {
		this.films = films;
	}

	public List<Shows> getShows() {
		return shows;
	}

	public void setShows(List<Shows> shows) {
		this.shows = shows;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(List<Film> films) {
		this.films = films;
	}
}

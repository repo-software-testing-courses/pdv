package com.trackorjargh.javaclass;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Gender {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@JsonView({BasicInformation.class, ListInformation.class})
	private String name;
	
	public interface BasicInformation {}
	public interface ListInformation{}
	
	@ManyToMany(mappedBy="genders")
	@JsonView(ListInformation.class)
	private List<Film> films = new LinkedList<>();
	
	@ManyToMany(mappedBy="genders")
	@JsonView(ListInformation.class)
	private List<Shows> shows = new LinkedList<>();
	
	@ManyToMany(mappedBy="genders")
	@JsonView(ListInformation.class)
	private List<Book> books = new LinkedList<>();
	
	public Gender() {
	}

	public Gender(String name) {
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

	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(List<Film> films) {
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
}

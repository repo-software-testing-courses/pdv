package com.trackorjargh.javaclass;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class Film implements InterfaceMainItem{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(BasicInformation.class)
	private Long id;
	
	@JsonView(BasicInformation.class)
	private String directors;
	@JsonView(BasicInformation.class)
	private String actors;
	
	@JsonView({BasicInformation.class, NameFilmAndType.class})
	private String url = "/pelicula/";
	
	public interface BasicInformation {}
	public interface NameFilmInfo {}
	public interface NameFilmAndType {}
	
	@JsonView({BasicInformation.class, NameFilmInfo.class, NameFilmAndType.class})
	private String name;
	@JsonView(BasicInformation.class)
	@Column(columnDefinition = "TEXT")
	private String synopsis;
	@JsonView(BasicInformation.class)
	private String image;
	@JsonView(BasicInformation.class)
	private String trailer;
	@JsonView(BasicInformation.class)
	private int year;
	private boolean firstInList;
	
	@ManyToMany
	@JsonView(BasicInformation.class)
	private List<Gender> genders = new LinkedList<>();
	
	@ManyToMany(mappedBy="films")
	private List<Lists> lists = new LinkedList<>();
	
	@OneToMany(mappedBy="film")
	private List<CommentFilm> commentsFilm = new LinkedList<>();
	
	@OneToMany(mappedBy="film")
	private List<PointFilm> pointsFilm = new LinkedList<>();

	public Film() {	
	}
	
	public Film(String name) {
		this.name = name;
	}

	public Film(String name, String actors, String directors,String synopsis, String image, String trailer, int year) {
		this.name = name;
		this.actors = actors;
		this.directors = directors;
		this.synopsis = synopsis;
		this.image = image;
		this.trailer = trailer;
		this.year = year;
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

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isFirstInList() {
		return firstInList;
	}

	public void setFirstInList(boolean firstInList) {
		this.firstInList = firstInList;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

	public List<Lists> getLists() {
		return lists;
	}

	public void setLists(List<Lists> lists) {
		this.lists = lists;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<CommentFilm> getCommentsFilm() {
		return commentsFilm;
	}

	public void setCommentsFilm(List<CommentFilm> commentsFilm) {
		this.commentsFilm = commentsFilm;
	}

	public List<PointFilm> getPointsFilm() {
		return pointsFilm;
	}

	public void setPointsFilm(List<PointFilm> pointsFilm) {
		this.pointsFilm = pointsFilm;
	}

	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directores) {
		this.directors = directores;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actores) {
		this.actors = actores;
	}
}

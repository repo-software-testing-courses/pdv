package com.trackorjargh.commoncode;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.CommentFilm;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointFilm;
import com.trackorjargh.javarepository.CommentFilmRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.PointFilmRepository;

@Service
public class CommonCodeFilm {
	final FilmRepository filmRepository;
	final CommentFilmRepository commentFilmRepository;
	final UserComponent userComponent;
	final PointFilmRepository pointFilmRepository;
	
	@Autowired
	public CommonCodeFilm(FilmRepository filmRepository, CommentFilmRepository commentFilmRepository,
			UserComponent userComponent, PointFilmRepository pointFilmRepository) {
		this.filmRepository = filmRepository;
		this.commentFilmRepository = commentFilmRepository;
		this.userComponent = userComponent;
		this.pointFilmRepository = pointFilmRepository;
	}

	public Film editFilm(Film film, String newName, String actors, String directors, String imageFilm,
			List<Gender> genders, String synopsis, String trailer, int year) {
		if (newName != null) {
			film.setName(newName);
		}
		if (actors != null) {
			film.setActors(actors);
		}
		if (directors != null) {
			film.setDirectors(directors);
		}
		if (synopsis != null) {
			film.setSynopsis(synopsis);
		}
		if (trailer != null) {
			film.setTrailer(trailer);
		}
		if ((year >= 0)) {
			film.setYear(year);
		}
		if (!genders.isEmpty()) {
			film.setGenders(genders);
		}
		if (imageFilm != null) {
			film.setImage(imageFilm);
		}

		filmRepository.save(film);
		return film;
	}
	
	public CommentFilm deleteCommentFilm(Long id) {
		CommentFilm comment = commentFilmRepository.findById(new Long(id));
		commentFilmRepository.delete(comment);

		return comment;
	}
	
	public CommentFilm addCommentFilm(Film film, String messageUser) {
		CommentFilm message = new CommentFilm(messageUser);
		message.setFilm(film);
		message.setUser(userComponent.getLoggedUser());

		commentFilmRepository.save(message);
		return message;
	}
	
	public PointFilm updatePointsFilm(Film film, double points) {
		PointFilm pointFilm = pointFilmRepository.findByUserAndFilm(userComponent.getLoggedUser(), film);

		if (pointFilm == null) {
			pointFilm = new PointFilm(points);
			pointFilm.setFilm(film);
			pointFilm.setUser(userComponent.getLoggedUser());
		} else {
			pointFilm.setPoints(points);
		}

		pointFilmRepository.save(pointFilm);
		return pointFilm;
	}
}

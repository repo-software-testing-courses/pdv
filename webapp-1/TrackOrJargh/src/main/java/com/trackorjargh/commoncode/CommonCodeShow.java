package com.trackorjargh.commoncode;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.CommentShow;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointShow;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javarepository.CommentShowRepository;
import com.trackorjargh.javarepository.PointShowRepository;
import com.trackorjargh.javarepository.ShowRepository;

@Service
public class CommonCodeShow {
	final ShowRepository showRepository;
	final PointShowRepository pointShowRepository;
	final CommentShowRepository commentShowRepository;
	final UserComponent userComponent;
	
	@Autowired
	public CommonCodeShow(ShowRepository showRepository, PointShowRepository pointShowRepository,
			CommentShowRepository commentShowRepository, UserComponent userComponent) {
		this.showRepository = showRepository;
		this.pointShowRepository = pointShowRepository;
		this.commentShowRepository = commentShowRepository;
		this.userComponent = userComponent;
	}

	public PointShow updatePointsShow(Shows show, double points) {
		PointShow pointShow = pointShowRepository.findByUserAndShow(userComponent.getLoggedUser(), show);

		if (pointShow == null) {
			pointShow = new PointShow(points);
			pointShow.setShow(show);
			pointShow.setUser(userComponent.getLoggedUser());
		} else {
			pointShow.setPoints(points);
		}

		pointShowRepository.save(pointShow);
		return pointShow;
	}
	
	public CommentShow addCommentShow(Shows show, String messageUser) {
		CommentShow message = new CommentShow(messageUser);
		message.setShow(show);
		message.setUser(userComponent.getLoggedUser());

		commentShowRepository.save(message);
		return message;
	}

	public CommentShow deleteCommentShow(Long id) {
		CommentShow comment = commentShowRepository.findById(new Long(id));
		commentShowRepository.delete(comment);

		return comment;
	}
	
	public Shows editShow(Shows show, String newName, String actors, String directors, String imageShow,
			List<Gender> genders, String synopsis, String trailer, int year) {
		if (newName != null) {
			show.setName(newName);
		}
		if (actors != null) {
			show.setActors(actors);
		}
		if (directors != null) {
			show.setDirectors(directors);
		}
		if (synopsis != null) {
			show.setSynopsis(synopsis);
		}
		if (trailer != null) {
			show.setTrailer(trailer);
		}
		if ((year >= 0)) {
			show.setYear(year);
		}
		if (!genders.isEmpty()) {
			show.setGenders(genders);
		}
		if (imageShow != null) {
			show.setImage(imageShow);
		}
		showRepository.save(show);
		return show;
	}
}

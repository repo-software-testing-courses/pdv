package com.trackorjargh.apirestcontrolers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.trackorjargh.commoncode.CommonCodeShow;
import com.trackorjargh.grafics.Grafics;
import com.trackorjargh.javaclass.CommentShow;
import com.trackorjargh.javaclass.DeleteElementsOfBBDD;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointShow;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.CommentShowRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.PointShowRepository;
import com.trackorjargh.javarepository.ShowRepository;

@RestController
@RequestMapping("/api")
public class ApiShowController {
	
	private final ShowRepository showRepository;
	private final GenderRepository genderRepository;
	private final PointShowRepository pointShowRepository;
	private final DeleteElementsOfBBDD deleteElementofBBDD;
	private final CommonCodeShow commonCode;
	private final CommentShowRepository commentShowRepository;
	
	@Autowired
	public ApiShowController (ShowRepository showRepository, PointShowRepository pointShowRepository, DeleteElementsOfBBDD deleteElementofBBDD, CommonCodeShow commonCode, CommentShowRepository commentShowRepository, GenderRepository genderRepository) {
		this.showRepository = showRepository;
		this.pointShowRepository = pointShowRepository;
		this.deleteElementofBBDD = deleteElementofBBDD;
		this.commonCode = commonCode;
		this.commentShowRepository = commentShowRepository;
		this.genderRepository = genderRepository;
	}
	
	@RequestMapping(value = "/series/administracion", method = RequestMethod.GET)
	@JsonView(Shows.NameFilmAndType.class)
	public ResponseEntity<List<Shows>> getAllShows(Pageable page) {
		return new ResponseEntity<>(showRepository.findAll(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/series", method = RequestMethod.GET)
	@JsonView(basicInfoShows.class)
	public Page<Shows> getShows(Pageable page) {
		return showRepository.findAll(page);
	}
	
	public interface basicInfoShows extends Shows.BasicInformation, Gender.BasicInformation {
	}
	
	@RequestMapping(value = "/series/{name}")
	@JsonView(basicInfoShows.class)
	public ResponseEntity<Shows> getShow(@PathVariable("name") String name) {
		Shows show = showRepository.findByNameIgnoreCase(name);
		
		if (show == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(show, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/series/relacionadas/{id}", method = RequestMethod.GET)
	@JsonView(basicInfoShows.class)
	public Page<Shows> getShowRelated(@PathVariable int id, Pageable page) {
		return showRepository.findShowsRelationsById(id, page);
	}
	
	@RequestMapping(value = "/series/ultimas/{num}", method = RequestMethod.GET)
	@JsonView(basicInfoShows.class)
	public List<Shows> getLastShow(@PathVariable int num) {
		return showRepository.findByLastAdded(num);
	}
	
	@RequestMapping(value = "/series/mejorvaloradas", method = RequestMethod.GET)
	@JsonView(basicInfoShows.class)
	public Page<Shows> getBestPointSeries(Pageable page) {
		return showRepository.findBestPointShow(page);
	}
	
	@RequestMapping(value = "/series/grafico", method = RequestMethod.GET)
	public List<Grafics> getBestPointShows() {
		List<Shows> shows = showRepository.findBestPointShow(new PageRequest(0, 10)).getContent();
		List<PointShow> listPoints;
		List<Grafics> graficShows = new ArrayList<>();
		double points;

		for (Shows show : shows) {
			points = 0;

			listPoints = pointShowRepository.findByShow(show);

			if (listPoints.size() > 0) {
				for (PointShow sf : listPoints)
					points += sf.getPoints();
				points /= listPoints.size();
			}

			graficShows.add(new Grafics(show.getName(), points));
		}

		return graficShows;
	}
	
	@RequestMapping(value = "/series", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(basicInfoShows.class)
	public ResponseEntity<Shows> addShow(@RequestBody Shows show) {
		if (showRepository.findByNameIgnoreCase(show.getName()) == null) {
			List<Gender> genders = new LinkedList<>();
			
			for (Gender genre : show.getGenders()) {
				genders.add(genderRepository.findByName(genre.getName()));
			}
			
			show.setGenders(genders);
			showRepository.save(show);
			return new ResponseEntity<>(show, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.IM_USED);
		}

	}
	
	@RequestMapping(value = "/series/{name}", method = RequestMethod.DELETE)
	@JsonView(Shows.BasicInformation.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Shows> deleteShow(@PathVariable("name") String name) {
		Shows show = showRepository.findByNameIgnoreCase(name);
		if (show == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			deleteElementofBBDD.deleteShow(show);
			return new ResponseEntity<>(show, HttpStatus.OK);
		}

	}
	
	@RequestMapping(value = "/series/{name}", method = RequestMethod.PUT)
	@JsonView(basicInfoShows.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Shows> editShow(@PathVariable String name, @RequestBody Shows show) {
		if (showRepository.findByNameIgnoreCase(name) == null) { // if the film does not exists, then i return
																	// a not found statement
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else { // if it exists, then i modify the item
			Shows editedShow = showRepository.findByNameIgnoreCase(name);
			List<Gender> genders = new LinkedList<>();
			
			for (Gender genre : show.getGenders()) {
				genders.add(genderRepository.findByName(genre.getName()));
			}

			return new ResponseEntity<>(
					commonCode.editShow(editedShow, show.getName(), show.getActors(), show.getDirectors(),
							show.getImage(), genders, show.getSynopsis(), show.getTrailer(), show.getYear()),
					HttpStatus.OK);
		}
	}
	
	public interface basicInfoCommentShow extends CommentShow.BasicInformation, User.BasicInformation {
	}
	
	@RequestMapping(value = "/series/comentarios/{name}", method = RequestMethod.GET)
	@JsonView(basicInfoCommentShow.class)
	public ResponseEntity<List<CommentShow>> getCommentsShow(@PathVariable String name) {
		Shows show = showRepository.findByNameIgnoreCase(name);
		
		if (show == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(commentShowRepository.findByShowOrderByIdAsc(show), HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/series/comentarios/{name}", method = RequestMethod.POST)
	@JsonView(basicInfoCommentShow.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentShow> addComentShow(@PathVariable String name, @RequestBody CommentShow comment) {
		if (showRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					commonCode.addCommentShow(showRepository.findByNameIgnoreCase(name), comment.getComment()),
					HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/series/comentarios/{id}", method = RequestMethod.DELETE)
	@JsonView(basicInfoCommentShow.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentShow> deleteShowComent(@PathVariable Long id) {
		if (commentShowRepository.findById(id) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(commonCode.deleteCommentShow(id), HttpStatus.OK);
		}
	}
	
	public interface joinedPointShowUserInfo extends PointShow.BasicInformation, Shows.NameShowInfo, User.NameUserInfo {
	}
	
	@RequestMapping(value = "/series/puntos/{name}", method = RequestMethod.POST)
	@JsonView(joinedPointShowUserInfo.class)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PointShow> addShowPoint(@PathVariable String name, @RequestBody PointShow showPoint) {
		if (showRepository.findByNameIgnoreCase(name) == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(
					commonCode.updatePointsShow(showRepository.findByNameIgnoreCase(name), showPoint.getPoints()),
					HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/series/puntos/{name}", method = RequestMethod.GET)
	@JsonView(joinedPointShowUserInfo.class)
	public ResponseEntity<List<PointShow>> getShowPoint(@PathVariable String name) {
		Shows show = showRepository.findByNameIgnoreCase(name);
		
		if (show == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<>(show.getPointsShow(), HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/busqueda/{optionSearch}/series/{name}/page", method = RequestMethod.GET)
	@JsonView(Shows.BasicInformation.class)
	public Page<Shows> getSearchSeries(Pageable page, @PathVariable String optionSearch, @PathVariable String name) {
		if (optionSearch.equalsIgnoreCase("titulo")) {
			return showRepository.findByNameContainingIgnoreCase(name, page);
		} else {
			return showRepository.findShowsByGender("%" + name + "%", page);
		}
	}

}

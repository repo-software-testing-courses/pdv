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

import com.trackorjargh.commoncode.CommonCodeShow;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.CommentShow;
import com.trackorjargh.javaclass.PointShow;
import com.trackorjargh.javaclass.PreparateMessageShow;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.CommentShowRepository;
import com.trackorjargh.javarepository.PointShowRepository;
import com.trackorjargh.javarepository.ShowRepository;
import com.trackorjargh.javarepository.UserRepository;

@Controller
public class ShowController {
	final ShowRepository showRepository;
	final CommentShowRepository commentShowRepository;
	final PointShowRepository pointShowRepository;
	final UserComponent userComponent;
	final UserRepository userRepository;
	final CommonCodeShow commonCodeShow;
	
	@Autowired
	public ShowController(ShowRepository showRepository, CommentShowRepository commentShowRepository,
			PointShowRepository pointShowRepository, UserComponent userComponent, UserRepository userRepository,
			CommonCodeShow commonCodeShow) {
		this.showRepository = showRepository;
		this.commentShowRepository = commentShowRepository;
		this.pointShowRepository = pointShowRepository;
		this.userComponent = userComponent;
		this.userRepository = userRepository;
		this.commonCodeShow = commonCodeShow;
	}

	@RequestMapping({ "/series", "/series/mejorvaloradas" })
	public String serveShowList(Model model, HttpServletRequest request) {
		List<Shows> shows = showRepository.findByLastAdded(5);
		shows.get(0).setFirstInList(true);

		if (userComponent.isLoggedUser()) {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());

			model.addAttribute("userList", user.getLists());
		}

		Page<Shows> showsPage;
		String typePage;
		if (request.getServletPath().equalsIgnoreCase("/series")) {
			showsPage = showRepository.findAll(new PageRequest(0, 10));
			model.addAttribute("contentShowButton", true);
			typePage = "/api/series";
		} else {
			showsPage = showRepository.findBestPointShow(new PageRequest(0, 10));
			model.addAttribute("bestPointContentShowButton", true);
			typePage = "/api/series/mejorvaloradas";
		}

		if (showsPage.getNumberOfElements() > 0 && showsPage.getNumberOfElements() < 10) {
			model.addAttribute("noElementsSearch", true);
		}

		model.addAttribute("linkContent", "/series");
		model.addAttribute("linkBestPointContent", "/series/mejorvaloradas");
		model.addAttribute("content", showsPage);
		model.addAttribute("typePage", typePage);
		model.addAttribute("showsActive", true);
		model.addAttribute("contentCarousel", shows);
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "serie");

		return "contentList";
	}	
	
	@RequestMapping("/serie/{name}")
	public String serveShowProfile(Model model, @PathVariable String name, @RequestParam Optional<String> messageSent,
			@RequestParam Optional<String> pointsSent) {
		Shows show = showRepository.findByNameIgnoreCase(name);

		if (messageSent.isPresent()) {
			commonCodeShow.addCommentShow(show, messageSent.get());
		}

		if (pointsSent.isPresent()) {
			double points = Double.parseDouble(pointsSent.get());
			commonCodeShow.updatePointsShow(show, points);
		}

		List<PreparateMessageShow> listMessages = new LinkedList<>();
		for (CommentShow ch : commentShowRepository.findByShow(show))
			listMessages.add(ch.preparateShowMessage());

		model.addAttribute("content", show);
		model.addAttribute("comments", listMessages);
		model.addAttribute("typeContent", "la serie");
		model.addAttribute("episodeSection", true);
		model.addAttribute("actionMessage", "/serie/" + name);

		double points = 0;
		double userPoints = 0;

		List<PointShow> listPoints = pointShowRepository.findByShow(show);

		if (listPoints.size() > 0) {
			for (PointShow ph : listPoints)
				points += ph.getPoints();
			points /= listPoints.size();
		}

		PointShow userPointShow = pointShowRepository.findByUserAndShow(userComponent.getLoggedUser(), show);
		if (userPointShow != null)
			if (userPointShow != null)
				userPoints = userPointShow.getPoints();

		model.addAttribute("totalPoints", points);
		model.addAttribute("userPoints", userPoints);

		model.addAttribute("contentRelation",
				showRepository.findShowsRelationsById(show.getId(), new PageRequest(0, 8)));
		model.addAttribute("iconFilmShow", true);
		model.addAttribute("deleteComment", "/serie/borrarcomentario/");

		return "contentProfile";
	}
	
	@RequestMapping("/serie/borrarcomentario/{id}/{name}")
	public String deleteCommentShow(Model model, @PathVariable int id, @PathVariable String name) {
		commonCodeShow.deleteCommentShow(new Long(id));

		return "redirect:/serie/" + name;
	}
	
	@RequestMapping("/busqueda/{optionSearch}/serie/{name}")
	public String searchShows(Model model, @PathVariable String optionSearch, @PathVariable String name) {		
		Page<Shows> shows;
		
		if(optionSearch.equalsIgnoreCase("titulo")) {
			shows = showRepository.findByNameContainingIgnoreCase(name, new PageRequest(0, 10));
			model.addAttribute("searchTitle", true);
		} else {
			shows = showRepository.findShowsByGender("%" + name + "%", new PageRequest(0, 10));
			model.addAttribute("searchGende", true);
		}
		
		model.addAttribute("search", name);
		model.addAttribute("searchActive", true);
		model.addAttribute("content", shows);
		model.addAttribute("typeShow", true);
		model.addAttribute("inputSearch", name);
		model.addAttribute("typeSearch", "/api/busqueda/" + optionSearch +"/series/" + name + "/page");
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "serie");
		
		if(shows.getNumberOfElements() == 0) {
			model.addAttribute("noElementsSearch", true);
			model.addAttribute("noResult", true);
		}
		
		if(shows.getNumberOfElements() > 0 && shows.getNumberOfElements() < 10) {
			model.addAttribute("noElementsSearch", true);
		}
		
		if (userComponent.isLoggedUser()) {
			User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());

			model.addAttribute("userList", user.getLists());
		}
		
		return "search";
	}
}

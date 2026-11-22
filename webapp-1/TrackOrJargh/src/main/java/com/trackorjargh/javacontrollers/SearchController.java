package com.trackorjargh.javacontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.trackorjargh.component.UserComponent;

@Controller
public class SearchController {
	
	@Autowired
	private UserComponent userComponent;

	@RequestMapping("/busqueda")
	public String search(Model model) {	
		model.addAttribute("searchActive", true);
		model.addAttribute("noElementsSearch", true);
		model.addAttribute("index", true);
		model.addAttribute("loggedUserJS", userComponent.isLoggedUser());
		model.addAttribute("typePageAddList", "null");
		
		return "search";
	}
}

package com.trackorjargh;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageAngular {

	@RequestMapping(value = { "/new", "/new/**/{[path:[^\\\\.]*}" })
	public String getFortyPersona() {
		return "forward:/new/index.html";
	}

}

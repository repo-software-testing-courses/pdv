package com.trackorjargh.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.UserRepository;

@Configuration
public class UserHandlerConfigurer extends WebMvcConfigurerAdapter {
	@Bean
	public UserHandlerInterceptor userHandlerInterceptor() {
		return new UserHandlerInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userHandlerInterceptor());
	}
}

class UserHandlerInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private UserComponent userComponent;
	@Autowired
	private UserRepository userRepository;

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			if (userComponent.isLoggedUser()) {
				User user = userRepository.findByNameIgnoreCase(userComponent.getLoggedUser().getName());
				
				modelAndView.addObject("userLogged", user);
				modelAndView.addObject("isUserAdmin", request.isUserInRole("ADMIN"));
				modelAndView.addObject("isUserModerator", request.isUserInRole("MODERATOR"));
			}
		}
	}
}

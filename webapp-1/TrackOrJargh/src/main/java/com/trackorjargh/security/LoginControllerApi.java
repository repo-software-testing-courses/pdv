package com.trackorjargh.security;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.trackorjargh.component.UserComponent;
import com.trackorjargh.javaclass.User;

@RestController
public class LoginControllerApi {
	@Autowired
	private UserComponent userComponent;

	@RequestMapping("/api/login")
	@JsonView(User.BasicInformation.class)
	public ResponseEntity<User> logIn() {

		if (!userComponent.isLoggedUser()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			User loggedUser = userComponent.getLoggedUser();
			return new ResponseEntity<>(loggedUser, HttpStatus.OK);
		}
	}

	@RequestMapping("/api/logout")
	public ResponseEntity<Boolean> logOut(HttpSession session) {

		if (!userComponent.isLoggedUser()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			session.invalidate();
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
	}

}
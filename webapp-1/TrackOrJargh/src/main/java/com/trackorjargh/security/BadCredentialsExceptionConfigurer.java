package com.trackorjargh.security;

import org.springframework.security.authentication.BadCredentialsException;

public class BadCredentialsExceptionConfigurer extends BadCredentialsException {
	private static final long serialVersionUID = 1L;
	private String user;

	public BadCredentialsExceptionConfigurer(String message, String user) {
		super(message);
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}

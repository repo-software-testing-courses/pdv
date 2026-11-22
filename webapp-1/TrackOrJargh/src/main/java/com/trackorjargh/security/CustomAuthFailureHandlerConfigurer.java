package com.trackorjargh.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthFailureHandlerConfigurer extends SimpleUrlAuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		BadCredentialsExceptionConfigurer exceptionMod = (BadCredentialsExceptionConfigurer)exception;

		switch (exceptionMod.getMessage()) {
		case "User not found":
			getRedirectStrategy().sendRedirect(request, response, "/error/noexiste/" + exceptionMod.getUser());
			break;
		case "User wrong password":
			getRedirectStrategy().sendRedirect(request, response, "/error/errorcontra/" + exceptionMod.getUser());
			break;
		case "User not activated":
			getRedirectStrategy().sendRedirect(request, response, "/error/noactivado/" + exceptionMod.getUser());
			break;
		}
	}
}

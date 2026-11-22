package com.trackorjargh.javaclass;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class GenerateURLPage {
	private HttpServletRequest request;
	
	public GenerateURLPage() {
	}
	
	public GenerateURLPage(HttpServletRequest request) {
		this.request = request;
	}
	
	public String generateURLActivateAccount(User user) {
		String url = "";
		
		try {
			URL urlPage = new URL(this.request.getRequestURL().toString());
			url = urlPage.getProtocol() + "://" +  urlPage.getHost() + ":" + urlPage.getPort() + "/activarusuario/" + user.getName();

		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		
		return url;
	}
	
	public String generateURLChangePass(ForgotPassword forgotPass) {
		String url = "";
		
		try {
			URL urlPage = new URL(this.request.getRequestURL().toString());
			url = urlPage.getProtocol() + "://" +  urlPage.getHost() + ":" + urlPage.getPort() + "/cambiarcontra/" + forgotPass.getSecretAlphanumeric();

		} catch (MalformedURLException exception) {
			exception.printStackTrace();
		}
		
		return url;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}

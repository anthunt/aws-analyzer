package com.anthunt.aws.network.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Error {

	private String message;
	private String redirect;
	
	protected Error(String message, String redirect) {
		this.setMessage(message);
		this.setRedirect(redirect);
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public String getRedirect() {
		return redirect;
	}

	private void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	
}

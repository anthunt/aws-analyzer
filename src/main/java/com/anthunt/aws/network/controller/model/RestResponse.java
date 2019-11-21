package com.anthunt.aws.network.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestResponse extends Response {

	private boolean success;
	private Error error;
	
	public RestResponse() {
		this.setSuccess(true);
	}

	public boolean isSuccess() {
		return success;
	}

	private void setSuccess(boolean success) {
		this.success = success;
	}

	public Error getError() {
		return error;
	}

	public RestResponse setError(String message) {
		return this.setError(message, null);
	}
	
	public RestResponse setError(String message, String redirectURL) {
		this.setError(new Error(message, redirectURL));
		return this;
	}
	
	public RestResponse setError(Error error) {
		this.error = error;
		this.setSuccess(false);
		return this;
	}
	
}
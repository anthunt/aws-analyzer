package com.anthunt.aws.network.repository.model;

public class ServiceResult {
	private int percent;
	private String message;
	
	public ServiceResult(int percent, String message) {
		this.setPercent(percent);
		this.setMessage(message);
	}

	public int getPercent() {
		return percent;
	}

	private void setPercent(int percent) {
		this.percent = percent;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}
}

package com.anthunt.aws.network.controller.model;

public class ProfileContents {

	private String credentials;
	private String config;
	
	public ProfileContents() {		
	}
	
	public String getCredentials() {
		return credentials;
	}
	
	public byte[] getCredentialsBytes() {
		return this.credentials.getBytes();
	}
	
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	
	public String getConfig() {
		return config;
	}
	
	public byte[] getConfigBytes() {
		return this.config.getBytes();
	}
	
	public void setConfig(String config) {
		this.config = config;
	}
	
}

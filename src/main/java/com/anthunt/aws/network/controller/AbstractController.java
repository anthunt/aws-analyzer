package com.anthunt.aws.network.controller;

import javax.servlet.http.HttpSession;

import com.anthunt.aws.network.service.checker.ServiceRepository;
import com.anthunt.aws.network.session.SessionProfile;

public abstract class AbstractController {

	public static final String SESSION_PROFILE_KEY = "profile";
	public static final String SESSION_SERVICE_REPOSITORY = "serviceRepository";
	
	protected SessionProfile getSessionProfile(HttpSession session) {
		SessionProfile sessionProfile = (SessionProfile) session.getAttribute(SESSION_PROFILE_KEY);
		if(sessionProfile == null) {
			sessionProfile = new SessionProfile();
		}
		return sessionProfile;
	}
	
	public static void setSessionProfile(HttpSession session, SessionProfile sessionProfile) {
		session.setAttribute(SESSION_PROFILE_KEY, sessionProfile);
	}
	
	protected ServiceRepository getSessionServiceRepository(HttpSession session) {
		return (ServiceRepository) session.getAttribute(SESSION_SERVICE_REPOSITORY);
	}
	
	public static void setServiceRepository(HttpSession session, ServiceRepository serviceRepository) {
		session.setAttribute(SESSION_SERVICE_REPOSITORY, serviceRepository);
	}
	
}

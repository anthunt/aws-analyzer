package com.anthunt.aws.network.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.anthunt.aws.network.repository.ServiceRepository;

import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;

public class SessionProvider {

	public static final String SESSION_PROFILE_KEY = "profile";
	public static final String SESSION_SERVICE_REPOSITORY = "serviceRepository";
	public static List<String> PROFILES;
	public static List<Region> REGIONS;
	
	static {
		loadProfiles();
	    REGIONS = Region.regions();
	}
	
	public static void loadProfiles() {
		if(PROFILES == null) {
			PROFILES = new ArrayList<String>();
		} else {
			PROFILES.clear();
		}
		ProfileFile profileFile = ProfileFile.defaultProfileFile();
	    Map<String, Profile> profileMap = profileFile.profiles();
	    Set<String> keys = profileMap.keySet();
	    Iterator<String> iKey = keys.iterator();
	    while(iKey.hasNext()) {
	    	PROFILES.add(iKey.next());
	    }
	}
	
	public static SessionProfile getSessionProfile(HttpSession session) {
		SessionProfile sessionProfile = (SessionProfile) session.getAttribute(SESSION_PROFILE_KEY);
		if(sessionProfile == null) {
			sessionProfile = new SessionProfile();
		}
		return sessionProfile;
	}
	
	public static boolean hasSessionProfile(HttpSession session) {
		return session.getAttribute(SESSION_PROFILE_KEY) != null;
	}
	
	public static boolean hasSessionServiceRepository(HttpSession session) {
		return session.getAttribute(SESSION_SERVICE_REPOSITORY) != null;
	}
	
	public static void setSessionProfile(HttpSession session, SessionProfile sessionProfile) {
		session.removeAttribute(SESSION_PROFILE_KEY);
		session.removeAttribute(SESSION_SERVICE_REPOSITORY);
		session.setAttribute(SESSION_PROFILE_KEY, sessionProfile);
	}
	
	public static ServiceRepository getSessionServiceRepository(HttpSession session) {
		return (ServiceRepository) session.getAttribute(SESSION_SERVICE_REPOSITORY);
	}
	
	public static void setServiceRepository(HttpSession session, ServiceRepository serviceRepository) {
		session.setAttribute(SESSION_SERVICE_REPOSITORY, serviceRepository);
	}
	
}

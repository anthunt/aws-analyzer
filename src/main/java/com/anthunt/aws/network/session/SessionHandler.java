package com.anthunt.aws.network.session;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.model.ServiceMapType;
import com.anthunt.aws.network.repository.profile.model.ProfileContents;
import com.anthunt.aws.network.repository.user.model.UserDetails;

import software.amazon.awssdk.regions.Region;

public class SessionHandler {

	public static final String SESSION_PROFILE_KEY = "profile";
	public static final String SESSION_SERVICE_REPOSITORY = "serviceRepository";
	public static List<String> PROFILES;
	public static List<Region> REGIONS;
	
	static {
	    REGIONS = Region.regions();
	}
	
	public static SessionProfile createSessionProfile(HttpSession session, Optional<ProfileContents> profileContents) {
		SessionProfile sessionProfile = (SessionProfile) session.getAttribute(SESSION_PROFILE_KEY);
		if(sessionProfile == null) {
			sessionProfile = new SessionProfile(SessionHandler.getUserDetails(), ServiceMapType.MONGODB);
			if(profileContents.isPresent()) {
				sessionProfile.setProfileFile(profileContents.get());
			}
		}
		session.setAttribute(SESSION_PROFILE_KEY, sessionProfile);
		return sessionProfile;
	}
	
	public static SessionProfile getSessionProfile(HttpSession session) throws IOException {
		return (SessionProfile) session.getAttribute(SESSION_PROFILE_KEY);
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
	
	public static UserDetails getUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
        	if(authentication.getPrincipal() instanceof UserDetails) {
                return (UserDetails) authentication.getPrincipal();
        	}
        }
        return null;
    }
	
	public static String getUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;
        if (authentication != null) {

        	if(authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                userName = userDetails.getUsername();
        	}

        }
        return userName;
    }
}

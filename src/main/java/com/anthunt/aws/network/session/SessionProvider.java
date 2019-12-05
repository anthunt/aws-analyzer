package com.anthunt.aws.network.session;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.anthunt.aws.network.controller.model.ProfileContents;
import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.profiles.ProfileFileLocation;
import software.amazon.awssdk.regions.Region;

public class SessionProvider {

	public static final String SESSION_PROFILE_KEY = "profile";
	public static final String SESSION_SERVICE_REPOSITORY = "serviceRepository";
	public static List<String> PROFILES;
	public static List<Region> REGIONS;
	
	static {
	    REGIONS = Region.regions();
	}
	
	public static SessionProfile getSessionProfile(HttpSession session) throws IOException {
		SessionProfile sessionProfile = (SessionProfile) session.getAttribute(SESSION_PROFILE_KEY);
		if(sessionProfile == null) {
			sessionProfile = new SessionProfile();
			
			ProfileContents profileContents = new ProfileContents();
			profileContents.setConfig(Utils.readFile(ProfileFileLocation.configurationFilePath(), Charset.forName("utf-8")));
			profileContents.setCredentials(Utils.readFile(ProfileFileLocation.credentialsFilePath(), Charset.forName("utf-8")));
			
			sessionProfile.setProfileFile(profileContents);
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
	
	public static String getUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;
        if (authentication != null) {

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                userName = userDetails.getUsername();

        }
        return userName;
    }
}

package com.anthunt.aws.network.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.anthunt.aws.network.controller.model.RestResponse;
import com.anthunt.aws.network.repository.user.model.UserDetails;
import com.anthunt.aws.network.service.ProfileService;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.session.SessionHandler;
import com.anthunt.aws.network.utils.Logging;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfig  implements WebMvcConfigurer {

	private static final Logger log = Logging.getLogger(WebConfig.class);

	private static void sendResponseBody(HttpServletResponse response, Object object) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String objJson = mapper.writeValueAsString(object);
		log.trace("generated response body : {}", objJson);
		
		response.setContentType("application/json");
		log.trace("set response status HttpServletResponse.SC_OK");
		response.setStatus(HttpServletResponse.SC_OK);
		log.trace("set response status HttpServletResponse.SC_OK");
		
		response.getWriter().write(objJson);
	}
	
	@Autowired
	private ProfileService profileService;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {

			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
				SecurityContext securityContext = SecurityContextHolder.getContext();
		        Authentication authentication = securityContext.getAuthentication();

		        if(authentication != null) {
		        	if(authentication.isAuthenticated()) {
		        		
		        		log.trace("Principal : {} : {}", authentication.getPrincipal(), request);
		        		
		        		if(authentication.getPrincipal() instanceof UserDetails) {
				        	
				        	HttpSession session = request.getSession();
				        	UserDetails userDetails = (UserDetails) authentication.getPrincipal();
				        	
				        	SessionProfile sessionProfile = SessionHandler.getSessionProfile(session);
				        	if(sessionProfile == null) {
				        		sessionProfile = SessionHandler.createSessionProfile(session, profileService.getProfileContents(userDetails.getUser().getUserid().toString()));
				        	}
				    		
				        	if((request.getRequestURI().startsWith("/profiles") || request.getRequestURI().startsWith("/api/profiles/edit"))) {
				        		log.trace("go to profiles something : {}", request);
				        		return true;
				        	} else if(!sessionProfile.isSelected()) {
				        		if(request.getRequestURI().startsWith("/api")) {
				        			log.trace("goto have no profile from api : {}", request);
				        			sendResponseBody(response, new RestResponse().setError("have no session profile", "/profiles"));
				        		} else {
				        			log.trace("go to profiles from api : {}", request);
				        			response.sendRedirect("/profiles");
				        		}
				        		return false;
				        	} else if("/dashboard".equals(request.getRequestURI()) || request.getRequestURI().startsWith("/api/collect")) {
				        		
				        		return true;
				        	} else if(!sessionProfile.isSelected()) {
				        		if(request.getRequestURI().startsWith("/api")) {
				        			sendResponseBody(response, new RestResponse().setError("have no sync data", "/dashboard"));
				        		} else {
				        			response.sendRedirect("/dashboard");
				        		}
						        return false;
				        	}
		        		} 
		        		
			        }
		        }
				return true;
			}
			
		});
	}

}

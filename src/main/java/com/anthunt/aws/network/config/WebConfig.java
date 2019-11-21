package com.anthunt.aws.network.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.anthunt.aws.network.controller.model.RestResponse;
import com.anthunt.aws.network.session.SessionProvider;
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
		        		
		        		if(authentication.getPrincipal() instanceof UserDetails) {
				        	
				        	HttpSession session = request.getSession();
				        	if(("/profiles".equals(request.getRequestURI()) || request.getRequestURI().startsWith("/setProfile"))) {
				        		return true;
				        	} else if(!SessionProvider.hasSessionProfile(session)) {
				        		if(request.getRequestURI().startsWith("/api")) {
				        			sendResponseBody(response, new RestResponse().setError("have no session profile", "/profiles"));
				        		} else {
				        			response.sendRedirect("/profiles");
				        		}
				        		return false;
				        	} else if("/dashboard".equals(request.getRequestURI()) || request.getRequestURI().startsWith("/api/collect")) {
				        		return true;
				        	} else if(!SessionProvider.hasSessionServiceRepository(session)) {
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

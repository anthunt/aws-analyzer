package com.anthunt.aws.network.controller.api;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.anthunt.aws.network.service.aws.ServiceCollectorService;
import com.anthunt.aws.network.session.SessionProvider;

@RestController
@RequestMapping("/api/collect")
public class CollectController extends AbstractAPIController {
	
	@Autowired
	private ServiceCollectorService serviceCollectorService;
	
	@RequestMapping(value= {"", "{serviceName}"})
	public SseEmitter collectServices( HttpSession session
			                         , @PathVariable("serviceName") Optional<String> serviceName) throws IOException {
		
		SseEmitter sseEmitter = new SseEmitter(180000L);
		serviceCollectorService.collectServices(session, sseEmitter, SessionProvider.getSessionProfile(session), serviceName);
		return sseEmitter;
	}
	
}

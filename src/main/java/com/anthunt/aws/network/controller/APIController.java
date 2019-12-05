package com.anthunt.aws.network.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.anthunt.aws.network.controller.model.ProfileContents;
import com.anthunt.aws.network.controller.model.Response;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.service.ProfileService;
import com.anthunt.aws.network.service.ServiceCollectorService;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.session.SessionProvider;
import com.anthunt.aws.network.utils.Logging;
import com.anthunt.aws.network.utils.Utils;

@RestController
@RequestMapping("api")
public class APIController extends AbstractController {

	private static final Logger log = Logging.getLogger(APIController.class);

	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private ServiceCollectorService serviceCollectorService;
	
	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	@RequestMapping(value= {"/network/ec2/{instanceId}", "/network/ec2/{instanceId}/{targetIp}"})
	public DiagramResult ec2Network( HttpSession session
			                       , @PathVariable("instanceId") String instanceId
			                       , @PathVariable("targetIp") Optional<String> targetIp
			                       ) throws UnsupportedEncodingException {
		String target = null;
		if(targetIp.isPresent()) {
			target = Utils.decodeB64URL(targetIp.get());
		}
		log.trace("called /network/ec2/{}/{}", instanceId, target);
		
		return this.ec2Service.getNetwork(SessionProvider.getSessionServiceRepository(session), Utils.decodeB64URL(instanceId), target);
	}

	@RequestMapping(value= {"/network/loadBalancer/{loadBalancerArn}", "/network/loadBalancer/{loadBalancerArn}/{targetIp}"})
	public DiagramResult loadBalancerNetwork( HttpSession session
								            , @PathVariable("loadBalancerArn") String loadBalancerArn
								            , @PathVariable("targetIp") Optional<String> targetIp
								            ) throws UnsupportedEncodingException {
		String target = null;
		if(targetIp.isPresent()) {
			target = Utils.decodeB64URL(targetIp.get());
		}
		log.trace("called /network/loadBalancer/{}/{}", loadBalancerArn, target);
		
		return this.loadBalancerService.getNetwork(SessionProvider.getSessionServiceRepository(session), Utils.decodeB64URL(loadBalancerArn), target);
	}
	
	@RequestMapping("/network/detail/{className}/{resourceId}")
	public DiagramResult loadResourceDetail(HttpSession session
			                               , @PathVariable("className") String className
			                               , @PathVariable("resourceId") String resourceId) {
		
		DiagramResult diagramResult = new DiagramResult();
		
		switch(className) {
		case "ec2Instance" :
			diagramResult = ec2Service.getInstanceNetwork(SessionProvider.getSessionServiceRepository(session), diagramResult, resourceId);
		}
		
		return diagramResult;
	}
	
	@RequestMapping(value= {"/collect", "/collect/{serviceName}"})
	public SseEmitter collectServices( HttpSession session
			                         , @PathVariable("serviceName") Optional<String> serviceName) throws IOException {
		
		SseEmitter sseEmitter = new SseEmitter(180000L);
		serviceCollectorService.collectServices(session, sseEmitter, SessionProvider.getSessionProfile(session), serviceName);
		return sseEmitter;
	}
	
	@RequestMapping("/statistics")
	public List<ServiceStatistic> getServiceStatistics(HttpSession session) {
		return SessionProvider.getSessionServiceRepository(session).getServiceStatistic();		
	}
	
	@PostMapping("/profiles/edit")
	public Response editCredentials(HttpSession session
												, ProfileContents profileContents) throws IOException {
		
		SessionProvider.setSessionProfile(
				session
				, profileService.updateProfile(SessionProvider.getSessionProfile(session), profileContents)
		);
		
		return new Response();
	}
	
}

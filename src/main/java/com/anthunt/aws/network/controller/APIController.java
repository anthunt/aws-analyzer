package com.anthunt.aws.network.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.service.ServiceCollectorService;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.utils.Utils;

@RestController
@RequestMapping("api")
public class APIController extends AbstractController {

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
		return this.ec2Service.getNetwork(getSessionServiceRepository(session), Utils.decodeB64URL(instanceId), target);
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
		return this.loadBalancerService.getNetwork(getSessionServiceRepository(session), Utils.decodeB64URL(loadBalancerArn), target);
	}
	
	@RequestMapping(value= {"/collect", "/collect/{serviceName}"})
	public SseEmitter collectServices( HttpSession session
			                         , @PathVariable("serviceName") Optional<String> serviceName) throws IOException {
		
		SseEmitter sseEmitter = new SseEmitter(180000L);
		serviceCollectorService.collectServices(session, sseEmitter, this.getSessionProfile(session), serviceName);
		return sseEmitter;
	}
	
}

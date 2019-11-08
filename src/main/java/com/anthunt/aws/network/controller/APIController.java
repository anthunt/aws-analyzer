package com.anthunt.aws.network.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
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

@RestController
@RequestMapping("api")
public class APIController extends AbstractController {

	@Autowired
	private ServiceCollectorService serviceCollectorService;
	
	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	@RequestMapping(value= {"/network/ec2/{instanceId}/{targetIp}", "/network/ec2/{instanceId}/{targetIp}/{bits}"})
	public DiagramResult ec2Network( HttpSession session
			                       , @PathVariable("instanceId") String instanceId
			                       , @PathVariable("targetIp") String targetIp
			                       , @PathVariable("bits") Optional<String>  bits
			                       ) throws UnsupportedEncodingException {
		if(bits.isPresent()) {
			targetIp += "/" + bits.get();
		}
		return this.ec2Service.getEc2Network(this.getSessionServiceRepository(session), URLDecoder.decode(new String(Base64.getDecoder().decode(instanceId), "utf8"), "utf8"), targetIp);
	}

	@RequestMapping(value= {"/network/loadBalancer/{loadBalancerArn}/{targetIp}", "/network/loadBalancer/{loadBalancerArn}/{targetIp}/{bits}"})
	public DiagramResult loadBalancerNetwork( HttpSession session
								            , @PathVariable("loadBalancerArn") String loadBalancerArn
								            , @PathVariable("targetIp") String targetIp
								            , @PathVariable("bits") Optional<String>  bits
								            ) throws UnsupportedEncodingException {
		if(bits.isPresent()) {
			targetIp += "/" + bits.get();
		}
		return this.loadBalancerService.getLoadBalancerNetwork(this.getSessionServiceRepository(session), URLDecoder.decode(new String(Base64.getDecoder().decode(loadBalancerArn), "utf8"), "utf8"), targetIp);
	}
	
	@RequestMapping(value= {"/collect", "/collect/{serviceName}"})
	public SseEmitter collectServices( HttpSession session
			                         , @PathVariable("serviceName") Optional<String> serviceName) throws IOException {
		
		SseEmitter sseEmitter = new SseEmitter(18000L);
		serviceCollectorService.collectServices(session, sseEmitter, this.getSessionProfile(session), serviceName);
		return sseEmitter;
	}
	
}

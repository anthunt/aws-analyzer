package com.anthunt.aws.network.controller.api;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthunt.aws.network.service.aws.Ec2Service;
import com.anthunt.aws.network.service.aws.LoadBalancerService;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.session.SessionHandler;
import com.anthunt.aws.network.utils.Logging;
import com.anthunt.aws.network.utils.Utils;

@RestController
@RequestMapping("/api/network")
public class NetworkController extends AbstractAPIController {

	private static final Logger log = Logging.getLogger(NetworkController.class);
	
	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	@RequestMapping(value= {"ec2/{instanceId}", "ec2/{instanceId}/{targetIp}"})
	public DiagramResult ec2Network( HttpSession session
			                       , @PathVariable("instanceId") String instanceId
			                       , @PathVariable("targetIp") Optional<String> targetIp
			                       ) throws UnsupportedEncodingException {
		String target = null;
		if(targetIp.isPresent()) {
			target = Utils.decodeB64URL(targetIp.get());
		}
		log.trace("called /network/ec2/{}/{}", instanceId, target);
		
		return this.ec2Service.getNetwork(SessionHandler.getSessionServiceRepository(session), Utils.decodeB64URL(instanceId), target);
	}

	@RequestMapping(value= {"loadBalancer/{loadBalancerArn}", "loadBalancer/{loadBalancerArn}/{targetIp}"})
	public DiagramResult loadBalancerNetwork( HttpSession session
								            , @PathVariable("loadBalancerArn") String loadBalancerArn
								            , @PathVariable("targetIp") Optional<String> targetIp
								            ) throws UnsupportedEncodingException {
		String target = null;
		if(targetIp.isPresent()) {
			target = Utils.decodeB64URL(targetIp.get());
		}
		log.trace("called /network/loadBalancer/{}/{}", loadBalancerArn, target);
		
		return this.loadBalancerService.getNetwork(SessionHandler.getSessionServiceRepository(session), Utils.decodeB64URL(loadBalancerArn), target);
	}
	
	@RequestMapping("detail/{className}/{resourceId}")
	public DiagramResult loadResourceDetail(HttpSession session
			                               , @PathVariable("className") String className
			                               , @PathVariable("resourceId") String resourceId) {
		
		DiagramResult diagramResult = new DiagramResult();
		
		switch(className) {
		case "ec2Instance" :
			diagramResult = ec2Service.getInstanceNetwork(SessionHandler.getSessionServiceRepository(session), diagramResult, resourceId);
		}
		
		return diagramResult;
	}
	
}

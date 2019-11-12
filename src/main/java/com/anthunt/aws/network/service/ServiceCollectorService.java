package com.anthunt.aws.network.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.anthunt.aws.network.controller.AbstractController;
import com.anthunt.aws.network.service.checker.ServiceRepository;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Listener;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;

@Service
public class ServiceCollectorService {

	private static final Logger log = LoggerFactory.getLogger(ServiceCollectorService.class);

	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	@Autowired
	private DirectConnectService directConnectService;
	
	public class Result {
		private int percent;
		private String message;
		
		public Result(int percent, String message) {
			this.setPercent(percent);
			this.setMessage(message);
		}

		public int getPercent() {
			return percent;
		}

		private void setPercent(int percent) {
			this.percent = percent;
		}

		public String getMessage() {
			return message;
		}

		private void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	@Async
	public void collectServices(HttpSession session, SseEmitter sseEmitter, SessionProfile sessionProfile, Optional<String> serviceName) throws IOException {

		ServiceRepository serviceRepository = AbstractController.getSessionServiceRepository(session);
		
		int num = 0;
		int total = !serviceName.isPresent() ? 14 
					: ServiceType.EC2.getName().equals(serviceName.get()) ? 7 
					: ServiceType.ELB.getName().equals(serviceName.get()) ? 6 : 1;
		
		if(!serviceName.isPresent() || ServiceType.EC2.getName().equals(serviceName.get())) {
			serviceRepository.setVpcMap(ec2Service.getVpcs(sessionProfile));
			this.send(sseEmitter, num, total, "Vpc data is loaded");
			num++;
			serviceRepository.setEc2InstanceMap(ec2Service.getInstances(sessionProfile));
			this.send(sseEmitter, num, total, "Ec2 instances data is loaded");
			num++;
			serviceRepository.setSecurityGroupMap(ec2Service.getSecurityGroups(sessionProfile));
			this.send(sseEmitter, num, total, "Securitygroups data is loaded");
			num++;
			Map<String, Subnet> subnetMap = ec2Service.getSubnets(sessionProfile);
			serviceRepository.setSubnetMap(subnetMap);
			this.send(sseEmitter, num, total, "Subnets data is loaded");
			num++;
			serviceRepository.setRouteTablesMap(ec2Service.getRouteTables(sessionProfile, subnetMap.values()));
			this.send(sseEmitter, num, total, "Routes data is loaded");
			num++;
			serviceRepository.setPrefixListMap(ec2Service.getPrefixLists(sessionProfile));
			this.send(sseEmitter, num, total, "PrefixLists data is loaded");
			num++;
			serviceRepository.setNetworkAclsMap(ec2Service.getNetworkAcls(sessionProfile, subnetMap.values()));
			this.send(sseEmitter, num, total, "PrefixLists data is loaded");
			num++;
			serviceRepository.setVpnGatewayMap(ec2Service.getVpnGateways(sessionProfile));
			this.send(sseEmitter, num, total, "VpnGateways data is loaded");
			num++;			
			serviceRepository.setVpnConnectionsMap(ec2Service.getVpnConnections(sessionProfile));
			this.send(sseEmitter, num, total, "VpnConnections data is loaded");
			num++;			
		}
		
		if(!serviceName.isPresent() || ServiceType.ELB.getName().equals(serviceName.get())) {
			serviceRepository.setClassicLoadBalancerMap(loadBalancerService.getClassicLoadBalancers(sessionProfile));
			this.send(sseEmitter, num, total, "Classic loadbalancers data is loaded");
			num++;
			Map<String, LoadBalancer> loadBalancerMap = loadBalancerService.getLoadBalancers(sessionProfile);
			serviceRepository.setLoadBalancerMap(loadBalancerMap);
			this.send(sseEmitter, num, total, "LoadBalancers data is loaded");
			num++;
			Map<String, List<Listener>> listenersMap = loadBalancerService.getLoadBalancerListeners(sessionProfile, loadBalancerMap.values());
			serviceRepository.setLoadBalancerListenersMap(listenersMap);
			this.send(sseEmitter, num, total, "LoadBalancer listeners data is loaded");
			num++;
			serviceRepository.setLoadBalancerRulesMap(loadBalancerService.getLoadBalancerRules(sessionProfile, listenersMap.values()));
			this.send(sseEmitter, num, total, "LoadBalancer rules data is loaded");
			num++;
			Map<String, TargetGroup> targetGroupMap = loadBalancerService.getTargetGroups(sessionProfile);
			serviceRepository.setTargetGroupMap(targetGroupMap);
			this.send(sseEmitter, num, total, "LoadBalancer targetgroups data is loaded");
			num++;
			serviceRepository.setTargetHealthDescriptionsMap(loadBalancerService.getTargetHealthDescriptions(sessionProfile, targetGroupMap.values()));
			this.send(sseEmitter, num, total, "LoadBalancer target healths data is loaded");
			num++;
		}
		
		if(!serviceName.isPresent() || ServiceType.DX.getName().equals(serviceName.get())) {
			serviceRepository.setVirtualInterfacesMap(directConnectService.getVirtualInterfaces(sessionProfile));
			this.send(sseEmitter, num, total, "Virtual Interfaces healths data is loaded");
			num++;
		}

		AbstractController.setServiceRepository(session, serviceRepository);
		
		this.send(sseEmitter, num, total, "All data is loaded");
		log.debug("All data collected - {}", sessionProfile.toString());
		
		sseEmitter.complete();
	}

	private void send(SseEmitter sseEmitter, int num, int total, String message) throws IOException {
		sseEmitter.send(SseEmitter.event()
	      .id(UUID.randomUUID().toString())
	      .data(new Result((int) Math.floor(((float) num/total)*100), message))
	    );
	}
	
}

package com.anthunt.aws.network.service.aws;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.anthunt.aws.network.repository.MemoryServiceRepositoryProvider;
import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.ServiceRepositoryCollectListener;
import com.anthunt.aws.network.repository.model.ServiceResult;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.session.SessionHandler;

@Service
public class ServiceCollectorService {

	private static final Logger log = LoggerFactory.getLogger(ServiceCollectorService.class);

	@Autowired
	private Ec2Service ec2Service;
	
	@Autowired
	private LoadBalancerService loadBalancerService;

	@Autowired
	private RdsService rdsService;
	
	@Autowired
	private DirectConnectService directConnectService;
		
	@Async
	public void collectServices(HttpSession session, SseEmitter sseEmitter, SessionProfile sessionProfile, Optional<String> serviceName) throws IOException {

		ServiceRepository serviceRepository = SessionHandler.getSessionServiceRepository(session);
		if(serviceRepository == null) {
			serviceRepository = ServiceRepository.build(new MemoryServiceRepositoryProvider());
		}
		
		serviceRepository.setServiceRepositoryCollectListener(new ServiceRepositoryCollectListener(sseEmitter));
		
		int num = 0;
		int total = !serviceName.isPresent() ? 25
					: ServiceType.EC2.getName().equals(serviceName.get()) ? 16 
					: ServiceType.ELB.getName().equals(serviceName.get()) ? 6 
					: ServiceType.RDS.getName().equals(serviceName.get()) ? 2 : 1;
		
		if(!serviceName.isPresent() || ServiceType.EC2.getName().equals(serviceName.get())) {
			num = serviceRepository.ec2Sync(num, total, sessionProfile, ec2Service);		
		}
		
		if(!serviceName.isPresent() || ServiceType.ELB.getName().equals(serviceName.get())) {
			num = serviceRepository.elbSync(num, total, sessionProfile, loadBalancerService);
		}
		
		if(!serviceName.isPresent() || ServiceType.RDS.getName().equals(serviceName.get())) {
			num = serviceRepository.rdsSync(num, total, sessionProfile, rdsService);
		}
		
		if(!serviceName.isPresent() || ServiceType.DX.getName().equals(serviceName.get())) {
			num = serviceRepository.dxSync(num, total, sessionProfile, directConnectService);
		}
		
		serviceRepository.collect(sessionProfile);
		
		SessionHandler.setServiceRepository(session, serviceRepository);
		
		sseEmitter.send(SseEmitter.event()
			      .id(UUID.randomUUID().toString())
			      .data(new ServiceResult(100, "All data is loaded"))
		);		
		log.debug("All data collected - {}", sessionProfile.toString());
		
		sseEmitter.complete();
	}
	
}

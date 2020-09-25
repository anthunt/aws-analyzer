package com.anthunt.aws.network.repository;

import java.util.List;

import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.session.SessionProfile;

public class ServiceCollector {

	private ServiceRepositoryProvider serviceRepository;
	private List<ServiceStatistic> serviceStatistics;
	
	public ServiceCollector(ServiceRepositoryProvider serviceRepository) {
		this.serviceRepository = serviceRepository;
	}
	
	public List<ServiceStatistic> collect(SessionProfile sessionProfile) {
		this.serviceStatistics.add(this.serviceRepository.getVpcMap().getServiceStatistic(sessionProfile, ServiceType.VPC));
		this.serviceStatistics.add(this.serviceRepository.getSubnetMap().getServiceStatistic(sessionProfile, ServiceType.PRSB));
		this.serviceStatistics.add(this.serviceRepository.getNetworkAclsMap().getServiceStatistic(sessionProfile, ServiceType.NACL));
		this.serviceStatistics.add(this.serviceRepository.getRouteTablesMap().getServiceStatistic(sessionProfile, ServiceType.RT));
		this.serviceStatistics.add(this.serviceRepository.getSecurityGroupMap().getServiceStatistic(sessionProfile, ServiceType.SG));
		this.serviceStatistics.add(this.serviceRepository.getVpnGatewayMap().getServiceStatistic(sessionProfile, ServiceType.VGW));
		this.serviceStatistics.add(this.serviceRepository.getVpnConnectionsMap().getServiceStatistic(sessionProfile, ServiceType.VPNC));
		this.serviceStatistics.add(this.serviceRepository.getVirtualInterfacesMap().getServiceStatistic(sessionProfile, ServiceType.DX));
		this.serviceStatistics.add(this.serviceRepository.getEc2InstanceMap().getServiceStatistic(sessionProfile, ServiceType.EC2));
		this.serviceStatistics.add(this.serviceRepository.getLoadBalancerMap().getServiceStatistic(sessionProfile, ServiceType.ELB));
		return this.serviceStatistics;
	}
	
}

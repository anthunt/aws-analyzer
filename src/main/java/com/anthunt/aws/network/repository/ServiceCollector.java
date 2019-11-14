package com.anthunt.aws.network.repository;

import java.util.List;

import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.model.ServiceType;

public class ServiceCollector {

	private ServiceRepositoryProvider serviceRepository;
	private List<ServiceStatistic> serviceStatistics;
	
	public ServiceCollector(ServiceRepositoryProvider serviceRepository) {
		this.serviceRepository = serviceRepository;
	}
	
	public List<ServiceStatistic> collect() {
		this.serviceStatistics.add(this.serviceRepository.getVpcMap().getServiceStatistic(ServiceType.VPC));
		this.serviceStatistics.add(this.serviceRepository.getSubnetMap().getServiceStatistic(ServiceType.PRSB));
		this.serviceStatistics.add(this.serviceRepository.getNetworkAclsMap().getServiceStatistic(ServiceType.NACL));
		this.serviceStatistics.add(this.serviceRepository.getRouteTablesMap().getServiceStatistic(ServiceType.RT));
		this.serviceStatistics.add(this.serviceRepository.getSecurityGroupMap().getServiceStatistic(ServiceType.SG));
		this.serviceStatistics.add(this.serviceRepository.getVpnGatewayMap().getServiceStatistic(ServiceType.VGW));
		this.serviceStatistics.add(this.serviceRepository.getVpnConnectionsMap().getServiceStatistic(ServiceType.VPNC));
		this.serviceStatistics.add(this.serviceRepository.getVirtualInterfacesMap().getServiceStatistic(ServiceType.DX));
		this.serviceStatistics.add(this.serviceRepository.getEc2InstanceMap().getServiceStatistic(ServiceType.EC2));
		this.serviceStatistics.add(this.serviceRepository.getLoadBalancerMap().getServiceStatistic(ServiceType.ELB));
		return this.serviceStatistics;
	}
	
}

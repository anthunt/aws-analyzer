package com.anthunt.aws.network.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.aws.DirectConnectService;
import com.anthunt.aws.network.service.aws.Ec2Service;
import com.anthunt.aws.network.service.aws.LoadBalancerService;
import com.anthunt.aws.network.service.aws.RdsService;
import com.anthunt.aws.network.session.SessionProfile;

public class ServiceRepository {
	
	private List<ServiceStatistic> serviceStatistics;
	
	private ServiceRepositoryProvider serviceRepositoryProvider;
	
	private ServiceRepository(ServiceRepositoryProvider serviceRepositoryProvider) {
		this.serviceStatistics = new ArrayList<>();
		this.serviceRepositoryProvider = serviceRepositoryProvider;
	}
	
	public static ServiceRepository build(ServiceRepositoryProvider serviceRepositoryProvider) {		
		return new ServiceRepository(serviceRepositoryProvider);
	}
	
	public ServiceMap getVpcMap() {
		return this.serviceRepositoryProvider.getVpcMap();
	}

	public ServiceMap getEc2InstanceMap() {
		return this.serviceRepositoryProvider.getEc2InstanceMap();
	}
	
	public ServiceMap getVolumeMap() {
		return this.serviceRepositoryProvider.getVolumeMap();
	}
	
	public ServiceMap getNetworkInterfaceMap() {
		return this.serviceRepositoryProvider.getNetworkInterfaceMap();
	}

	public ServiceMap getSubnetMap() {
		return this.serviceRepositoryProvider.getSubnetMap();
	}

	public ServiceMap getVpcPeeringMap() {
		return this.serviceRepositoryProvider.getVpcPeeringMap();
	}
	
	public ServiceMap getLoadBalancerMap() {
		return this.serviceRepositoryProvider.getLoadBalancerMap();
	}

	public ServiceMap getLoadBalancerListenersMap() {
		return this.serviceRepositoryProvider.getLoadBalancerListenersMap();
	}

	public ServiceMap getLoadBalancerRulesMap() {
		return this.serviceRepositoryProvider.getLoadBalancerRulesMap();
	}

	public ServiceMap getTargetGroupMap() {
		return this.serviceRepositoryProvider.getTargetGroupMap();
	}

	public ServiceMap getTargetHealthDescriptionsMap() {
		return this.serviceRepositoryProvider.getTargetHealthDescriptionsMap();
	}

	public ServiceMap getClassicLoadBalancerMap() {
		return this.serviceRepositoryProvider.getClassicLoadBalancerMap();
	}

	public ServiceMap getSecurityGroupMap() {
		return this.serviceRepositoryProvider.getSecurityGroupMap();
	}

	public ServiceMap getRouteTablesMap() {
		return this.serviceRepositoryProvider.getRouteTablesMap();
	}

	public ServiceMap getPrefixListMap() {
		return this.serviceRepositoryProvider.getPrefixListMap();
	}

	public ServiceMap getVpnGatewayMap() {
		return this.serviceRepositoryProvider.getVpnGatewayMap();
	}

	public ServiceMap getVpnConnectionsMap() {
		return this.serviceRepositoryProvider.getVpnConnectionsMap();
	}

	public ServiceMap getVirtualInterfacesMap() {
		return this.serviceRepositoryProvider.getVirtualInterfacesMap();
	}
	
	public ServiceMap getCustomerGatewayMap() {
		return this.serviceRepositoryProvider.getCustomerGatewayMap();
	}

	public ServiceMap getNetworkAclsMap() {
		return this.serviceRepositoryProvider.getNetworkAclsMap();
	}

	public ServiceMap getVpcEndpointMap() {
		return this.serviceRepositoryProvider.getVpcEndpointMap();
	}

	public ServiceMap getEgressInternetGatewayMap() {
		return this.serviceRepositoryProvider.getEgressInternetGatewayMap();
	}

	public ServiceMap getInternetGatewayMap() {
		return this.serviceRepositoryProvider.getInternetGatewayMap();
	}

	public ServiceMap getTransitGatewayMap() {
		return this.serviceRepositoryProvider.getTransitGatewayMap();
	}
	
	public ServiceMap getRdsClusterMap() {
		return this.serviceRepositoryProvider.getRdsClusterMap();
	}
	
	public ServiceMap getRdsInstanceMap() {
		return this.serviceRepositoryProvider.getRdsInstanceMap();
	}
	
	public List<ServiceStatistic> getServiceStatistic() {
		return this.serviceStatistics;
	}
	
	public int ec2Sync(int num, int total, SessionProfile sessionProfile, Ec2Service ec2Service) throws IOException {
		return this.serviceRepositoryProvider.ec2Sync(num, total, sessionProfile, ec2Service);
	}
	
	public int elbSync(int num, int total, SessionProfile sessionProfile, LoadBalancerService loadBalancerService) throws IOException {
		return this.serviceRepositoryProvider.elbSync(num, total, sessionProfile, loadBalancerService);
	}
	
	public int rdsSync(int num, int total, SessionProfile sessionProfile, RdsService rdsService) throws IOException {
		return this.serviceRepositoryProvider.rdsSync(num, total, sessionProfile, rdsService);
	}
	
	public int dxSync(int num, int total, SessionProfile sessionProfile, DirectConnectService directConnectService) throws IOException {
		return this.serviceRepositoryProvider.dxSync(num, total, sessionProfile, directConnectService);
	}
	
	public void collect(SessionProfile sessionProfile) {
		if(this.serviceStatistics.size() > 0) this.serviceStatistics.clear();
		this.serviceStatistics.addAll(this.serviceRepositoryProvider.collect(sessionProfile));
	}

	public void setServiceRepositoryCollectListener(ServiceRepositoryCollectListener serviceRepositoryCollectListener) {
		this.serviceRepositoryProvider.setServiceRepositoryCollectListener(serviceRepositoryCollectListener);
	}

}


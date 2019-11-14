package com.anthunt.aws.network.service.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.service.DirectConnectService;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.PrefixList;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Listener;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Rule;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;

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
	
	public ServiceMap<Vpc> getVpcMap() {
		return this.serviceRepositoryProvider.getVpcMap();
	}

	public ServiceMap<Instance> getEc2InstanceMap() {
		return this.serviceRepositoryProvider.getEc2InstanceMap();
	}

	public ServiceMap<Subnet> getSubnetMap() {
		return this.serviceRepositoryProvider.getSubnetMap();
	}

	public ServiceMap<LoadBalancer> getLoadBalancerMap() {
		return this.serviceRepositoryProvider.getLoadBalancerMap();
	}

	public ServiceMap<List<Listener>> getLoadBalancerListenersMap() {
		return this.serviceRepositoryProvider.getLoadBalancerListenersMap();
	}

	public ServiceMap<List<Rule>> getLoadBalancerRulesMap() {
		return this.serviceRepositoryProvider.getLoadBalancerRulesMap();
	}

	public ServiceMap<TargetGroup> getTargetGroupMap() {
		return this.serviceRepositoryProvider.getTargetGroupMap();
	}

	public ServiceMap<List<TargetHealthDescription>> getTargetHealthDescriptionsMap() {
		return this.serviceRepositoryProvider.getTargetHealthDescriptionsMap();
	}

	public ServiceMap<LoadBalancerDescription> getClassicLoadBalancerMap() {
		return this.serviceRepositoryProvider.getClassicLoadBalancerMap();
	}

	public ServiceMap<SecurityGroup> getSecurityGroupMap() {
		return this.serviceRepositoryProvider.getSecurityGroupMap();
	}

	public ServiceMap<List<RouteTable>> getRouteTablesMap() {
		return this.serviceRepositoryProvider.getRouteTablesMap();
	}

	public ServiceMap<PrefixList> getPrefixListMap() {
		return this.serviceRepositoryProvider.getPrefixListMap();
	}

	public ServiceMap<VpnGateway> getVpnGatewayMap() {
		return this.serviceRepositoryProvider.getVpnGatewayMap();
	}

	public ServiceMap<List<VpnConnection>> getVpnConnectionsMap() {
		return this.serviceRepositoryProvider.getVpnConnectionsMap();
	}

	public ServiceMap<List<VirtualInterface>> getVirtualInterfacesMap() {
		return this.serviceRepositoryProvider.getVirtualInterfacesMap();
	}

	public ServiceMap<List<NetworkAcl>> getNetworkAclsMap() {
		return this.serviceRepositoryProvider.getNetworkAclsMap();
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
	
	public int dxSync(int num, int total, SessionProfile sessionProfile, DirectConnectService directConnectService) throws IOException {
		return this.serviceRepositoryProvider.dxSync(num, total, sessionProfile, directConnectService);
	}
	
	public void collect() {
		if(this.serviceStatistics.size() > 0) this.serviceStatistics.clear();
		this.serviceStatistics.addAll(this.serviceRepositoryProvider.collect());
	}

	public void setServiceRepositoryCollectListener(ServiceRepositoryCollectListener serviceRepositoryCollectListener) {
		this.serviceRepositoryProvider.setServiceRepositoryCollectListener(serviceRepositoryCollectListener);
	}

}


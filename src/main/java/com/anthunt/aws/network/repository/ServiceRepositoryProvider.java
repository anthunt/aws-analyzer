package com.anthunt.aws.network.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.DirectConnectService;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.service.model.ServiceType;
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

public abstract class ServiceRepositoryProvider {

	protected ServiceRepositoryCollectListener serviceRepositoryCollectListener;
	
	public ServiceRepositoryProvider() {
		
	}
	
	protected abstract ServiceMap<Vpc> getVpcMap();

	protected abstract ServiceMap<Instance> getEc2InstanceMap();

	protected abstract ServiceMap<Subnet> getSubnetMap();

	protected abstract ServiceMap<LoadBalancer> getLoadBalancerMap();

	protected abstract ServiceMap<List<Listener>> getLoadBalancerListenersMap();

	protected abstract ServiceMap<List<Rule>> getLoadBalancerRulesMap();

	protected abstract ServiceMap<TargetGroup> getTargetGroupMap();

	protected abstract ServiceMap<List<TargetHealthDescription>> getTargetHealthDescriptionsMap();

	protected abstract ServiceMap<LoadBalancerDescription> getClassicLoadBalancerMap();

	protected abstract ServiceMap<SecurityGroup> getSecurityGroupMap();

	protected abstract ServiceMap<List<RouteTable>> getRouteTablesMap();

	protected abstract ServiceMap<PrefixList> getPrefixListMap();

	protected abstract ServiceMap<VpnGateway> getVpnGatewayMap();

	protected abstract ServiceMap<List<VpnConnection>> getVpnConnectionsMap();

	protected abstract ServiceMap<List<VirtualInterface>> getVirtualInterfacesMap();

	protected abstract ServiceMap<List<NetworkAcl>> getNetworkAclsMap();

	protected abstract void setVirtualInterfacesMap(ServiceMap<List<VirtualInterface>> serviceMap);

	protected abstract void setTargetHealthDescriptionsMap(ServiceMap<List<TargetHealthDescription>> serviceMap);

	protected abstract ServiceMap<TargetGroup> setTargetGroupMap(ServiceMap<TargetGroup> serviceMap);

	protected abstract void setLoadBalancerRulesMap(ServiceMap<List<Rule>> serviceMap);

	protected abstract ServiceMap<List<Listener>> setLoadBalancerListenersMap(ServiceMap<List<Listener>> serviceMap);

	protected abstract ServiceMap<LoadBalancer> setLoadBalancerMap(ServiceMap<LoadBalancer> serviceMap);

	protected abstract void setClassicLoadBalancerMap(ServiceMap<LoadBalancerDescription> serviceMap);

	protected abstract void setVpnConnectionsMap(ServiceMap<List<VpnConnection>> serviceMap);

	protected abstract void setVpnGatewayMap(ServiceMap<VpnGateway> serviceMap);

	protected abstract void setNetworkAclsMap(ServiceMap<List<NetworkAcl>> serviceMap);

	protected abstract void setPrefixListMap(ServiceMap<PrefixList> serviceMap);

	protected abstract void setRouteTablesMap(ServiceMap<List<RouteTable>> serviceMap);

	protected abstract ServiceMap<Subnet> setSubnetMap(ServiceMap<Subnet> serviceMap);

	protected abstract void setSecurityGroupMap(ServiceMap<SecurityGroup> serviceMap);

	protected abstract void setEc2InstanceMap(ServiceMap<Instance> serviceMap);

	protected abstract void setVpcMap(ServiceMap<Vpc> serviceMap);
	
	public void setServiceRepositoryCollectListener(ServiceRepositoryCollectListener serviceRepositoryCollectListener) {
		this.serviceRepositoryCollectListener = serviceRepositoryCollectListener;
	}
	
	public int ec2Sync(int num, int total, SessionProfile sessionProfile, Ec2Service ec2Service) throws IOException {
		this.setVpcMap(ec2Service.getVpcs(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Vpc data is loaded");
		num++;
		this.setEc2InstanceMap(ec2Service.getInstances(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Ec2 instances data is loaded");
		num++;
		this.setSecurityGroupMap(ec2Service.getSecurityGroups(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Securitygroups data is loaded");
		num++;
		ServiceMap<Subnet> subnetMap = this.setSubnetMap(ec2Service.getSubnets(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Subnets data is loaded");
		num++;
		this.setRouteTablesMap(ec2Service.getRouteTables(sessionProfile, subnetMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Routes data is loaded");
		num++;
		this.setPrefixListMap(ec2Service.getPrefixLists(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "PrefixLists data is loaded");
		num++;
		this.setNetworkAclsMap(ec2Service.getNetworkAcls(sessionProfile, subnetMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "PrefixLists data is loaded");
		num++;
		this.setVpnGatewayMap(ec2Service.getVpnGateways(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "VpnGateways data is loaded");
		num++;			
		this.setVpnConnectionsMap(ec2Service.getVpnConnections(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "VpnConnections data is loaded");
		num++;
		return num;		
	}
	
	public int elbSync(int num, int total, SessionProfile sessionProfile, LoadBalancerService loadBalancerService) throws IOException {
		this.setClassicLoadBalancerMap(loadBalancerService.getClassicLoadBalancers(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Classic loadbalancers data is loaded");
		num++;
		ServiceMap<LoadBalancer> loadBalancerMap = this.setLoadBalancerMap(loadBalancerService.getLoadBalancers(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancers data is loaded");
		num++;
		ServiceMap<List<Listener>> listenersMap = this.setLoadBalancerListenersMap(loadBalancerService.getLoadBalancerListeners(sessionProfile, loadBalancerMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer listeners data is loaded");
		num++;
		this.setLoadBalancerRulesMap(loadBalancerService.getLoadBalancerRules(sessionProfile, listenersMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer rules data is loaded");
		num++;
		ServiceMap<TargetGroup> targetGroupMap = this.setTargetGroupMap(loadBalancerService.getTargetGroups(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer targetgroups data is loaded");
		num++;
		this.setTargetHealthDescriptionsMap(loadBalancerService.getTargetHealthDescriptions(sessionProfile, targetGroupMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer target healths data is loaded");
		num++;
		return num;
	}
	
	public int dxSync(int num, int total, SessionProfile sessionProfile, DirectConnectService directConnectService) throws IOException {
		this.setVirtualInterfacesMap(directConnectService.getVirtualInterfaces(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Virtual Interfaces healths data is loaded");
		num++;
		return num;
	}

	public List<ServiceStatistic> collect() {
		List<ServiceStatistic> serviceStatistics = new ArrayList<>();
		serviceStatistics.add(this.getVpcMap().getServiceStatistic(ServiceType.VPC));
		serviceStatistics.add(this.getSubnetMap().getServiceStatistic(ServiceType.PRSB));
		serviceStatistics.add(this.getNetworkAclsMap().getServiceStatistic(ServiceType.NACL));
		serviceStatistics.add(this.getRouteTablesMap().getServiceStatistic(ServiceType.RT));
		serviceStatistics.add(this.getSecurityGroupMap().getServiceStatistic(ServiceType.SG));
		serviceStatistics.add(this.getVpnGatewayMap().getServiceStatistic(ServiceType.VGW));
		serviceStatistics.add(this.getVpnConnectionsMap().getServiceStatistic(ServiceType.VPNC));
		serviceStatistics.add(this.getVirtualInterfacesMap().getServiceStatistic(ServiceType.VIF));
		serviceStatistics.add(this.getEc2InstanceMap().getServiceStatistic(ServiceType.EC2));
		serviceStatistics.add(this.getLoadBalancerMap().getServiceStatistic(ServiceType.ELB));
		return serviceStatistics;
	}
	
}

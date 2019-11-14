package com.anthunt.aws.network.service;

import java.util.List;

import com.anthunt.aws.network.service.checker.ServiceMap;
import com.anthunt.aws.network.service.checker.ServiceRepositoryProvider;

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

public class MemoryServiceRepositoryProvider extends ServiceRepositoryProvider {

	/**
	 * String vpcId
	 */
	private ServiceMap<Vpc> vpcMap;
	
	/**
	 * String instanceId
	 */
	private ServiceMap<Instance> ec2InstanceMap;
	
	/**
	 * String subnetId
	 */
	private ServiceMap<Subnet> subnetMap;
	
	/**
	 * String loadBalancerArn
	 */
	private ServiceMap<LoadBalancer> loadBalancerMap;
	
	/**
	 * String loadBalancerArn
	 */
	private ServiceMap<List<Listener>> loadBalancerListenersMap;
	
	/**
	 * String listenerArn
	 */
	private ServiceMap<List<Rule>> loadBalancerRulesMap;
	
	/**
	 * String targetGroupArn
	 */
	private ServiceMap<TargetGroup> targetGroupMap;

	/**
	 * String targetGroupArn
	 */
	private ServiceMap<List<TargetHealthDescription>> targetHealthDescriptionsMap;
	
	/**
	 * String loadBalancerName
	 */
	private ServiceMap<LoadBalancerDescription> classicLoadBalancerMap;
	
	/**
	 * String securityGroupId
	 */
	private ServiceMap<SecurityGroup> securityGroupMap;
	
	/**
	 * String subnetId
	 */
	private ServiceMap<List<RouteTable>> routeTablesMap;
	
	/**
	 * String prefixListId
	 */
	private ServiceMap<PrefixList> prefixListMap;
	
	/**
	 * String virtualGatewayId
	 */
	private ServiceMap<VpnGateway> vpnGatewayMap;
	
	/**
	 * String virtualGatewayId
	 */
	private ServiceMap<List<VpnConnection>> vpnConnectionsMap;
	
	/**
	 * String virtualGatewayId
	 */
	private ServiceMap<List<VirtualInterface>> virtualInterfacesMap;
	
	/**
	 * String subnetId
	 */
	private ServiceMap<List<NetworkAcl>> networkAclsMap;
	
	public MemoryServiceRepositoryProvider() {
		super();
	}

	@Override
	protected ServiceMap<Vpc> getVpcMap() {
		return this.vpcMap;
	}

	@Override
	protected ServiceMap<Instance> getEc2InstanceMap() {
		return this.ec2InstanceMap;
	}

	@Override
	protected ServiceMap<Subnet> getSubnetMap() {
		return this.subnetMap;
	}

	@Override
	protected ServiceMap<LoadBalancer> getLoadBalancerMap() {
		return this.loadBalancerMap;
	}

	@Override
	protected ServiceMap<List<Listener>> getLoadBalancerListenersMap() {
		return this.loadBalancerListenersMap;
	}

	@Override
	protected ServiceMap<List<Rule>> getLoadBalancerRulesMap() {
		return this.loadBalancerRulesMap;
	}

	@Override
	protected ServiceMap<TargetGroup> getTargetGroupMap() {
		return this.targetGroupMap;
	}

	@Override
	protected ServiceMap<List<TargetHealthDescription>> getTargetHealthDescriptionsMap() {
		return this.targetHealthDescriptionsMap;
	}

	@Override
	protected ServiceMap<LoadBalancerDescription> getClassicLoadBalancerMap() {
		return this.classicLoadBalancerMap;
	}

	@Override
	protected ServiceMap<SecurityGroup> getSecurityGroupMap() {
		return this.securityGroupMap;
	}

	@Override
	protected ServiceMap<List<RouteTable>> getRouteTablesMap() {
		return this.routeTablesMap;
	}

	@Override
	protected ServiceMap<PrefixList> getPrefixListMap() {
		return this.prefixListMap;
	}

	@Override
	protected ServiceMap<VpnGateway> getVpnGatewayMap() {
		return this.vpnGatewayMap;
	}

	@Override
	protected ServiceMap<List<VpnConnection>> getVpnConnectionsMap() {
		return this.vpnConnectionsMap;
	}

	@Override
	protected ServiceMap<List<VirtualInterface>> getVirtualInterfacesMap() {
		return this.virtualInterfacesMap;
	}

	@Override
	protected ServiceMap<List<NetworkAcl>> getNetworkAclsMap() {
		return this.networkAclsMap;
	}

	@Override
	protected void setVirtualInterfacesMap(ServiceMap<List<VirtualInterface>> serviceMap) {
		this.virtualInterfacesMap = serviceMap;
	}

	@Override
	protected void setTargetHealthDescriptionsMap(ServiceMap<List<TargetHealthDescription>> serviceMap) {
		this.targetHealthDescriptionsMap = serviceMap;
	}

	@Override
	protected ServiceMap<TargetGroup> setTargetGroupMap(ServiceMap<TargetGroup> serviceMap) {
		this.targetGroupMap = serviceMap;
		return this.getTargetGroupMap();
	}

	@Override
	protected void setLoadBalancerRulesMap(ServiceMap<List<Rule>> serviceMap) {
		this.loadBalancerRulesMap = serviceMap;
	}

	@Override
	protected ServiceMap<List<Listener>> setLoadBalancerListenersMap(ServiceMap<List<Listener>> serviceMap) {
		this.loadBalancerListenersMap = serviceMap;
		return this.getLoadBalancerListenersMap();
	}

	@Override
	protected ServiceMap<LoadBalancer> setLoadBalancerMap(ServiceMap<LoadBalancer> serviceMap) {
		this.loadBalancerMap = serviceMap;
		return this.getLoadBalancerMap();
	}

	@Override
	protected void setClassicLoadBalancerMap(ServiceMap<LoadBalancerDescription> serviceMap) {
		this.classicLoadBalancerMap = serviceMap;
	}

	@Override
	protected void setVpnConnectionsMap(ServiceMap<List<VpnConnection>> serviceMap) {
		this.vpnConnectionsMap = serviceMap;
	}

	@Override
	protected void setVpnGatewayMap(ServiceMap<VpnGateway> serviceMap) {
		this.vpnGatewayMap = serviceMap;
	}

	@Override
	protected void setNetworkAclsMap(ServiceMap<List<NetworkAcl>> serviceMap) {
		this.networkAclsMap = serviceMap;
	}

	@Override
	protected void setPrefixListMap(ServiceMap<PrefixList> serviceMap) {
		this.prefixListMap = serviceMap;
	}

	@Override
	protected void setRouteTablesMap(ServiceMap<List<RouteTable>> serviceMap) {
		this.routeTablesMap = serviceMap;
	}

	@Override
	protected ServiceMap<Subnet> setSubnetMap(ServiceMap<Subnet> serviceMap) {
		this.subnetMap = serviceMap;
		return this.getSubnetMap();
	}

	@Override
	protected void setSecurityGroupMap(ServiceMap<SecurityGroup> serviceMap) {
		this.securityGroupMap = serviceMap;
	}

	@Override
	protected void setEc2InstanceMap(ServiceMap<Instance> serviceMap) {
		this.ec2InstanceMap = serviceMap;
	}

	@Override
	protected void setVpcMap(ServiceMap<Vpc> serviceMap) {
		this.vpcMap = serviceMap;
	}
	
}


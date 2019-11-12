package com.anthunt.aws.network.repository;

import java.util.List;
import java.util.Map;

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

public class DefaultServiceRepository implements com.anthunt.aws.network.service.checker.ServiceRepository {

	/**
	 * String vpcId
	 */
	private Map<String, Vpc> vpcMap;
	
	/**
	 * String instanceId
	 */
	private Map<String, Instance> ec2InstanceMap;
	
	/**
	 * String subnetId
	 */
	private Map<String, Subnet> subnetMap;
	
	/**
	 * String loadBalancerArn
	 */
	private Map<String, LoadBalancer> loadBalancerMap;
	
	/**
	 * String loadBalancerArn
	 */
	private Map<String, List<Listener>> loadBalancerListenersMap;
	
	/**
	 * String listenerArn
	 */
	private Map<String, List<Rule>> loadBalancerRulesMap;
	
	/**
	 * String targetGroupArn
	 */
	private Map<String, TargetGroup> targetGroupMap;

	/**
	 * String targetGroupArn
	 */
	private Map<String, List<TargetHealthDescription>> targetHealthDescriptionsMap;
	
	/**
	 * String loadBalancerName
	 */
	private Map<String, LoadBalancerDescription> classicLoadBalancerMap;
	
	/**
	 * String securityGroupId
	 */
	private Map<String, SecurityGroup> securityGroupMap;
	
	/**
	 * String subnetId
	 */
	private Map<String, List<RouteTable>> routeTablesMap;
	
	/**
	 * String prefixListId
	 */
	private Map<String, PrefixList> prefixListMap;
	
	/**
	 * String virtualGatewayId
	 */
	private Map<String, VpnGateway> vpnGatewayMap;
	
	/**
	 * String virtualGatewayId
	 */
	private Map<String, List<VpnConnection>> vpnConnectionsMap;
	
	/**
	 * String virtualGatewayId
	 */
	private Map<String, List<VirtualInterface>> virtualInterfacesMap;
	
	/**
	 * String subnetId
	 */
	private Map<String, List<NetworkAcl>> networkAclsMap;
	
	public DefaultServiceRepository() {
	
	}

	public Map<String, Vpc> getVpcMap() {
		return vpcMap;
	}

	public void setVpcMap(Map<String, Vpc> vpcMap) {
		this.vpcMap = vpcMap;
	}

	public Map<String, Instance> getEc2InstanceMap() {
		return ec2InstanceMap;
	}

	public void setEc2InstanceMap(Map<String, Instance> ec2InstanceMap) {
		this.ec2InstanceMap = ec2InstanceMap;
	}

	public Map<String, Subnet> getSubnetMap() {
		return subnetMap;
	}

	public void setSubnetMap(Map<String, Subnet> subnetMap) {
		this.subnetMap = subnetMap;
	}

	public Map<String, LoadBalancer> getLoadBalancerMap() {
		return loadBalancerMap;
	}

	public void setLoadBalancerMap(Map<String, LoadBalancer> loadBalancerMap) {
		this.loadBalancerMap = loadBalancerMap;
	}

	public Map<String, List<Listener>> getLoadBalancerListenersMap() {
		return loadBalancerListenersMap;
	}

	public void setLoadBalancerListenersMap(Map<String, List<Listener>> loadBalancerListenersMap) {
		this.loadBalancerListenersMap = loadBalancerListenersMap;
	}

	public Map<String, List<Rule>> getLoadBalancerRulesMap() {
		return loadBalancerRulesMap;
	}

	public void setLoadBalancerRulesMap(Map<String, List<Rule>> loadBalancerRulesMap) {
		this.loadBalancerRulesMap = loadBalancerRulesMap;
	}

	public Map<String, TargetGroup> getTargetGroupMap() {
		return targetGroupMap;
	}

	public void setTargetGroupMap(Map<String, TargetGroup> targetGroupMap) {
		this.targetGroupMap = targetGroupMap;
	}

	public Map<String, List<TargetHealthDescription>> getTargetHealthDescriptionsMap() {
		return targetHealthDescriptionsMap;
	}

	public void setTargetHealthDescriptionsMap(Map<String, List<TargetHealthDescription>> targetHealthDescriptionsMap) {
		this.targetHealthDescriptionsMap = targetHealthDescriptionsMap;
	}

	public Map<String, LoadBalancerDescription> getClassicLoadBalancerMap() {
		return classicLoadBalancerMap;
	}

	public void setClassicLoadBalancerMap(Map<String, LoadBalancerDescription> classicLoadBalancerMap) {
		this.classicLoadBalancerMap = classicLoadBalancerMap;
	}

	public Map<String, SecurityGroup> getSecurityGroupMap() {
		return securityGroupMap;
	}

	public void setSecurityGroupMap(Map<String, SecurityGroup> securityGroupMap) {
		this.securityGroupMap = securityGroupMap;
	}

	public Map<String, List<RouteTable>> getRouteTablesMap() {
		return routeTablesMap;
	}

	public void setRouteTablesMap(Map<String, List<RouteTable>> routeTablesMap) {
		this.routeTablesMap = routeTablesMap;
	}

	public Map<String, PrefixList> getPrefixListMap() {
		return prefixListMap;
	}

	public void setPrefixListMap(Map<String, PrefixList> prefixListMap) {
		this.prefixListMap = prefixListMap;
	}

	public Map<String, VpnGateway> getVpnGatewayMap() {
		return vpnGatewayMap;
	}

	public void setVpnGatewayMap(Map<String, VpnGateway> vpnGatewayMap) {
		this.vpnGatewayMap = vpnGatewayMap;
	}

	public Map<String, List<VpnConnection>> getVpnConnectionsMap() {
		return vpnConnectionsMap;
	}

	public void setVpnConnectionsMap(Map<String, List<VpnConnection>> vpnConnectionsMap) {
		this.vpnConnectionsMap = vpnConnectionsMap;
	}

	public Map<String, List<VirtualInterface>> getVirtualInterfacesMap() {
		return virtualInterfacesMap;
	}

	public void setVirtualInterfacesMap(Map<String, List<VirtualInterface>> virtualInterfacesMap) {
		this.virtualInterfacesMap = virtualInterfacesMap;
	}

	public Map<String, List<NetworkAcl>> getNetworkAclsMap() {
		return networkAclsMap;
	}

	public void setNetworkAclsMap(Map<String, List<NetworkAcl>> networkAclsMap) {
		this.networkAclsMap = networkAclsMap;
	}
}


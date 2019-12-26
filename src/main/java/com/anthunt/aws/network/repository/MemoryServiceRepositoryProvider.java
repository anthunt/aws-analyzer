package com.anthunt.aws.network.repository;

import com.anthunt.aws.network.repository.model.ServiceMap;

public class MemoryServiceRepositoryProvider extends ServiceRepositoryProvider {

	/**
	 * String vpcId
	 */
	private ServiceMap vpcMap;
	
	/**
	 * String instanceId
	 */
	private ServiceMap ec2InstanceMap;
	
	/**
	 * String volumeId
	 */
	private ServiceMap volumeMap;
	
	/**
	 * String networkInterfaceId
	 */
	private ServiceMap networkInterfaceMap;
	
	/**
	 * String subnetId
	 */
	private ServiceMap subnetMap;
	
	/**
	 * String vpcPeeringConnectionId
	 */
	private ServiceMap vpcPeeringConnectionMap;
	
	/**
	 * String customerGatewayId
	 */
	private ServiceMap customerGatewayMap;
	
	/**
	 * String loadBalancerArn
	 */
	private ServiceMap loadBalancerMap;
	
	/**
	 * String loadBalancerArn
	 */
	private ServiceMap loadBalancerListenersMap;
	
	/**
	 * String listenerArn
	 */
	private ServiceMap loadBalancerRulesMap;
	
	/**
	 * String targetGroupArn
	 */
	private ServiceMap targetGroupMap;

	/**
	 * String targetGroupArn
	 */
	private ServiceMap targetHealthDescriptionsMap;
	
	/**
	 * String loadBalancerName
	 */
	private ServiceMap classicLoadBalancerMap;
	
	/**
	 * String securityGroupId
	 */
	private ServiceMap securityGroupMap;
	
	/**
	 * String subnetId
	 */
	private ServiceMap routeTablesMap;
	
	/**
	 * String prefixListId
	 */
	private ServiceMap prefixListMap;
	
	/**
	 * String virtualGatewayId
	 */
	private ServiceMap vpnGatewayMap;
	
	/**
	 * String virtualGatewayId
	 */
	private ServiceMap vpnConnectionsMap;
	
	/**
	 * String virtualGatewayId
	 */
	private ServiceMap virtualInterfacesMap;
	
	/**
	 * String subnetId
	 */
	private ServiceMap networkAclsMap;

	/**
	 * String vpcEndpointId
	 */
	private ServiceMap vpcEndpointMap;

	/**
	 * String EgressOnlyInternetGatewayId
	 */
	private ServiceMap egressInternetGatewayMap;

	/**
	 * String InternetGatewayId
	 */
	private ServiceMap internetGatewayMap;

	/**
	 * String transitGatewayId
	 */
	private ServiceMap transitGatewayMap;

	/**
	 * String dbClusterIdentifier
	 */
	private ServiceMap rdsClusterMap;
	
	/**
	 * String dbInstanceIdentifier
	 */
	private ServiceMap rdsInstanceMap;
	
	public MemoryServiceRepositoryProvider() {
		super();
	}

	@Override
	protected ServiceMap getVpcMap() {
		return this.vpcMap;
	}

	@Override
	protected ServiceMap getEc2InstanceMap() {
		return this.ec2InstanceMap;
	}

	@Override
	protected ServiceMap getVolumeMap() {
		return this.volumeMap;
	}
	
	@Override
	protected ServiceMap getNetworkInterfaceMap() {
		return this.networkInterfaceMap;
	}
	
	@Override
	protected ServiceMap getSubnetMap() {
		return this.subnetMap;
	}
	
	@Override
	protected ServiceMap getVpcPeeringMap() {
		return this.vpcPeeringConnectionMap;
	}

	@Override
	protected ServiceMap getLoadBalancerMap() {
		return this.loadBalancerMap;
	}

	@Override
	protected ServiceMap getLoadBalancerListenersMap() {
		return this.loadBalancerListenersMap;
	}

	@Override
	protected ServiceMap getLoadBalancerRulesMap() {
		return this.loadBalancerRulesMap;
	}

	@Override
	protected ServiceMap getTargetGroupMap() {
		return this.targetGroupMap;
	}

	@Override
	protected ServiceMap getTargetHealthDescriptionsMap() {
		return this.targetHealthDescriptionsMap;
	}

	@Override
	protected ServiceMap getClassicLoadBalancerMap() {
		return this.classicLoadBalancerMap;
	}

	@Override
	protected ServiceMap getSecurityGroupMap() {
		return this.securityGroupMap;
	}

	@Override
	protected ServiceMap getRouteTablesMap() {
		return this.routeTablesMap;
	}

	@Override
	protected ServiceMap getPrefixListMap() {
		return this.prefixListMap;
	}
	
	@Override
	protected ServiceMap getCustomerGatewayMap() {
		return this.customerGatewayMap;
	}

	@Override
	protected ServiceMap getVpnGatewayMap() {
		return this.vpnGatewayMap;
	}

	@Override
	protected ServiceMap getVpnConnectionsMap() {
		return this.vpnConnectionsMap;
	}

	@Override
	protected ServiceMap getVirtualInterfacesMap() {
		return this.virtualInterfacesMap;
	}

	@Override
	protected ServiceMap getNetworkAclsMap() {
		return this.networkAclsMap;
	}

	@Override
	protected void setVpcPeeringMap(ServiceMap serviceMap) {
		this.vpcPeeringConnectionMap = serviceMap;
	}
	
	@Override
	protected void setVirtualInterfacesMap(ServiceMap serviceMap) {
		this.virtualInterfacesMap = serviceMap;
	}

	@Override
	protected void setTargetHealthDescriptionsMap(ServiceMap serviceMap) {
		this.targetHealthDescriptionsMap = serviceMap;
	}

	@Override
	protected ServiceMap setTargetGroupMap(ServiceMap serviceMap) {
		this.targetGroupMap = serviceMap;
		return this.getTargetGroupMap();
	}

	@Override
	protected void setLoadBalancerRulesMap(ServiceMap serviceMap) {
		this.loadBalancerRulesMap = serviceMap;
	}

	@Override
	protected ServiceMap setLoadBalancerListenersMap(ServiceMap serviceMap) {
		this.loadBalancerListenersMap = serviceMap;
		return this.getLoadBalancerListenersMap();
	}

	@Override
	protected ServiceMap setLoadBalancerMap(ServiceMap serviceMap) {
		this.loadBalancerMap = serviceMap;
		return this.getLoadBalancerMap();
	}

	@Override
	protected void setClassicLoadBalancerMap(ServiceMap serviceMap) {
		this.classicLoadBalancerMap = serviceMap;
	}

	@Override
	protected void setCustomerGatewayMap(ServiceMap serviceMap) {
		this.customerGatewayMap = serviceMap;
	}
	
	@Override
	protected void setVpnConnectionsMap(ServiceMap serviceMap) {
		this.vpnConnectionsMap = serviceMap;
	}

	@Override
	protected void setVpnGatewayMap(ServiceMap serviceMap) {
		this.vpnGatewayMap = serviceMap;
	}

	@Override
	protected void setNetworkAclsMap(ServiceMap serviceMap) {
		this.networkAclsMap = serviceMap;
	}

	@Override
	protected void setPrefixListMap(ServiceMap serviceMap) {
		this.prefixListMap = serviceMap;
	}

	@Override
	protected void setRouteTablesMap(ServiceMap serviceMap) {
		this.routeTablesMap = serviceMap;
	}

	@Override
	protected ServiceMap setSubnetMap(ServiceMap serviceMap) {
		this.subnetMap = serviceMap;
		return this.getSubnetMap();
	}

	@Override
	protected void setSecurityGroupMap(ServiceMap serviceMap) {
		this.securityGroupMap = serviceMap;
	}

	@Override
	protected void setEc2InstanceMap(ServiceMap serviceMap) {
		this.ec2InstanceMap = serviceMap;
	}
	
	@Override
	protected void setVolumeMap(ServiceMap serviceMap) {
		this.volumeMap = serviceMap;
	}
	
	@Override
	protected void setNetworkInterfaceMap(ServiceMap serviceMap) {
		this.networkInterfaceMap = serviceMap;
	}

	@Override
	protected void setVpcMap(ServiceMap serviceMap) {
		this.vpcMap = serviceMap;
	}

	@Override
	protected ServiceMap getVpcEndpointMap() {
		return this.vpcEndpointMap;
	}

	@Override
	protected ServiceMap getEgressInternetGatewayMap() {
		return this.egressInternetGatewayMap;
	}

	@Override
	protected ServiceMap getInternetGatewayMap() {
		return this.internetGatewayMap;
	}

	@Override
	protected ServiceMap getTransitGatewayMap() {
		return this.transitGatewayMap;
	}

	@Override
	protected void setVpcEndpointMap(ServiceMap serviceMap) {
		this.vpcEndpointMap = serviceMap;
	}

	@Override
	protected void setEgressInternetGatewayMap(ServiceMap serviceMap) {
		this.egressInternetGatewayMap = serviceMap;
	}

	@Override
	protected void setInternetGatewayMap(ServiceMap serviceMap) {
		this.internetGatewayMap = serviceMap;
	}

	@Override
	protected void setTransitGatewayMap(ServiceMap serviceMap) {
		this.transitGatewayMap = serviceMap;
	}

	@Override
	protected ServiceMap getRdsInstanceMap() {
		return this.rdsInstanceMap;
	}

	@Override
	protected ServiceMap getRdsClusterMap() {
		return this.rdsClusterMap;
	}

	@Override
	protected void setRdsInstanceMap(ServiceMap serviceMap) {
		this.rdsInstanceMap = serviceMap;
	}

	@Override
	protected void setRdsClusterMap(ServiceMap serviceMap) {
		this.rdsClusterMap = serviceMap;
	}
	
}


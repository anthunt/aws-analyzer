package com.anthunt.aws.network.service.checker;

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

public interface ServiceRepository {

	/**
	 * Get Vpc Map
	 * @return Map<vpcId, Vpc>
	 */
	public Map<String, Vpc> getVpcMap();
	
	/**
	 * Set Vpc Map
	 * 
	 * @param vpcMap - Map<vpcId, Vpc>
	 */
	public void setVpcMap(Map<String, Vpc> vpcMap);
	
	/**
	 * Get EC2 Instance Map
	 * 
	 * @return Map<instanceId, Instance>
	 */
	public Map<String, Instance> getEc2InstanceMap();

	/**
	 * Set EC2 Instance Map
	 * 
	 * @param ec2InstanceMap - Map<instanceId, Instance>
	 */
	public void setEc2InstanceMap(Map<String, Instance> ec2InstanceMap);

	public Map<String, Subnet> getSubnetMap();

	public void setSubnetMap(Map<String, Subnet> subnetMap);

	public Map<String, LoadBalancer> getLoadBalancerMap();

	public void setLoadBalancerMap(Map<String, LoadBalancer> loadBalancerMap);

	public Map<String, List<Listener>> getLoadBalancerListenersMap();

	public void setLoadBalancerListenersMap(Map<String, List<Listener>> loadBalancerListenersMap);

	public Map<String, List<Rule>> getLoadBalancerRulesMap();

	public void setLoadBalancerRulesMap(Map<String, List<Rule>> loadBalancerRulesMap);

	public Map<String, TargetGroup> getTargetGroupMap();

	public void setTargetGroupMap(Map<String, TargetGroup> targetGroupMap);

	public Map<String, List<TargetHealthDescription>> getTargetHealthDescriptionsMap();

	public void setTargetHealthDescriptionsMap(Map<String, List<TargetHealthDescription>> targetHealthDescriptionsMap);

	public Map<String, LoadBalancerDescription> getClassicLoadBalancerMap();

	public void setClassicLoadBalancerMap(Map<String, LoadBalancerDescription> classicLoadBalancerMap);

	public Map<String, SecurityGroup> getSecurityGroupMap();

	public void setSecurityGroupMap(Map<String, SecurityGroup> securityGroupMap);

	public Map<String, List<RouteTable>> getRouteTablesMap();

	public void setRouteTablesMap(Map<String, List<RouteTable>> routeTablesMap);

	public Map<String, PrefixList> getPrefixListMap();

	public void setPrefixListMap(Map<String, PrefixList> prefixListMap);

	public Map<String, VpnGateway> getVpnGatewayMap();

	public void setVpnGatewayMap(Map<String, VpnGateway> vpnGatewayMap);

	public Map<String, List<VpnConnection>> getVpnConnectionsMap();

	public void setVpnConnectionsMap(Map<String, List<VpnConnection>> vpnConnectionsMap);

	public Map<String, List<VirtualInterface>> getVirtualInterfacesMap();

	public void setVirtualInterfacesMap(Map<String, List<VirtualInterface>> virtualInterfacesMap);

	public Map<String, List<NetworkAcl>> getNetworkAclsMap();

	public void setNetworkAclsMap(Map<String, List<NetworkAcl>> networkAclsMap);
	
}

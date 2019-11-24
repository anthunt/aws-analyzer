package com.anthunt.aws.network.service.model.diagram;

import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancerTypeEnum;

public enum NodeType {
	
	EC2_INSTANCE("ec2Instance")
	, EBS("ebs")
	, IAM_ROLE("iamRole")
	, INTERNET("internet")
	, LOCAL("local")
	, LAMBDA("lambda")
	, CLASSIC_LOADBALANCER("classicLoadBalancer")
	, APPLICATION_LOADBALANCER("applicationLoadBalancer")
	, NETWORK_LOADBALANCER("networkLoadBalancer")
	, TARGET_GROUP("targetGroup")
	, ROUTE_TABLE("routeTable")
    , TRANSIT_GATEWAY("transitGateway")
    , EGRESS_INTERNET_GATEWAY("internetGateway")
    , INTERNET_GATEWAY("internetGateway")
    , VIRTUAL_GATEWAY("virtualGateway")
    , NAT_GATEWAY("natGateway")
    , VPN_GATEWAY("vpnGateway")
    , VPN_CONNECTION("vpnConnection")
    , CUSTOMER_GATEWAY("customerGateway")
    , CORPORATE_DATA_CENTER("corporateDataCenter")
    , PEERING("peering")
    , VPC_ENDPOINT("vpcEndpoint")
    , DIRECT_CONNECT("directConnect")
    , NETWORK_INTERFACE("networkInterface")
    , NETWORK_ACL("networkAcl")
    , SECURITY_GROUP("securityGroup")
    , SERVER("server")
    , VPC("vpc")
    , AWS("aws")
    , REGION("region");
	
	private String name;
	
	private NodeType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static NodeType getGatewayType(String gatewayId) {
		if(gatewayId.startsWith("vgw")) {
			return NodeType.VIRTUAL_GATEWAY;
		} else if(gatewayId.startsWith("igw")){
			return NodeType.INTERNET_GATEWAY;
		} else if(gatewayId.startsWith("vpce")){
			return NodeType.VPC_ENDPOINT;
		} else {
			return NodeType.LOCAL;
		}
	}
	
	public static NodeType getLoadBalancerType(LoadBalancerTypeEnum loadBalancerTypeEnum) {
		if(loadBalancerTypeEnum == LoadBalancerTypeEnum.APPLICATION) {
			return NodeType.APPLICATION_LOADBALANCER;
		} else {
			return NodeType.NETWORK_LOADBALANCER;
		}
	}
	
}

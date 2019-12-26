package com.anthunt.aws.network.service.model;

import software.amazon.awssdk.services.directconnect.model.VirtualGateway;
import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBInstance;

public enum ServiceType {

	VPC(ServiceCategory.NETWORK, Vpc.class, "VPC", "Virtual Private Cloud", "/img/Virtual-private-cloud-VPC_dark-bg@4x.png"),
	PUSB(ServiceCategory.NETWORK, Subnet.class, "PUSB", "Public Subnet", "/img/VPC-subnet-public_dark-bg@4x.png"),
	PRSB(ServiceCategory.NETWORK, Subnet.class, "PRSB", "Private Subnet", "/img/VPC-subnet-private_dark-bg@4x.png"),
	EC2(ServiceCategory.COMPUTE, Instance.class, "EC2", "EC2 Instance", "/img/Amazon-EC2_Instance_dark-bg@4x.png"),
	ELB(ServiceCategory.NETWORK, LoadBalancer.class, "ELB", "Elastic Load Balancer", "/img/Elastic-Load-Balancing@4x.png"),
	AURORA(ServiceCategory.DATABASE, DBCluster.class, "AURORA", "Aurora DB Cluster", "/img/Amazon-Aurora@4x.png"),
	RDS(ServiceCategory.DATABASE, DBInstance.class, "RDS", "RDS DB Instance", "/img/Amazon-RDS@4x.png"),
	CLB(ServiceCategory.NETWORK, LoadBalancerDescription.class, "CLB", "Classic Load Balancer", "/img/Elastic-Load-Balancing_Classic-load-balancer_dark-bg@4x.png"),
	ALB(ServiceCategory.NETWORK, LoadBalancer.class, "ALB", "Application Load Balancer", "/img/Elastic-Load-Balancing-ELB_Application-load-balancer_dark-bg@4x.png"),
	NLB(ServiceCategory.NETWORK, LoadBalancer.class, "NLB", "Network Load Balancer", "/img/Elastic-Load-Balancing-ELB_Network-load-balancer_dark-bg@4x.png"),
	SG(ServiceCategory.SECURITY, SecurityGroup.class, "SG", "Security Group", "/img/SecurityGroup_dark-bg@4x.png"),
	EBS(ServiceCategory.STORAGE, Volume.class, "EBS", "EBS Volume", "/img/Amazon-Elastic-Block-Store-EBS@4x.png"),
	RT(ServiceCategory.NETWORK, RouteTable.class, "RT", "Route Table", "/img/Amazon-Route-53_Route-Table_dark-bg@4x.png"),
	PEERING(ServiceCategory.NETWORK, VpcPeeringConnection.class, "PEERING", "Vpc Peering", "/img/Amazon-VPC_Peering_dark-bg@4x.png"),
	VGW(ServiceCategory.NETWORK, VirtualGateway.class, "VGW", "Virtual Gateway", "/img/Amazon-VPC_Virtual-Gateway_dark-bg@4x.png"),
	CGW(ServiceCategory.NETWORK, CustomerGateway.class, "CGW", "Customer Gateway", "/img/Amazon-VPC_Customer-Gateway_dark-bg@4x.png"),
	VPNC(ServiceCategory.NETWORK, VpnConnection.class, "VPNC", "Vpn Connection", "/img/Amazon-VPC_VPN-Connection_dark-bg@4x.png"),
	DX(ServiceCategory.NETWORK, VirtualInterface.class, "DX", "Direct Connect", "/img/AWS-Direct-Connect@4x.png"),
	VIF(ServiceCategory.NETWORK, VirtualInterface.class, "VIF", "Virtual Interface", "/img/AWS-Direct-Connect@4x.png"),
	VND(ServiceCategory.NETWORK, VpcEndpoint.class, "VND", "VPC Endpoint", "/img/Amazon-VPC_Endpoints_dark-bg@4x.png"),
	IGW(ServiceCategory.NETWORK, InternetGateway.class, "IGW", "Internet Gateway", "/img/Amazon-VPC_Internet-Gateway_dark-bg@4x.png"),
	EGW(ServiceCategory.NETWORK, EgressOnlyInternetGateway.class, "EGW", "Egress Only Internet Gateway", "/img/Amazon-VPC_Internet-Gateway_dark-bg@4x.png"),
	TGW(ServiceCategory.NETWORK, TransitGateway.class, "TGW", "Transit Gateway", "/img/AWS-Transit-Gateway@4x.png"),
	NACL(ServiceCategory.SECURITY, NetworkAcl.class, "NACL", "Network ACL", "/img/Amazon-VPC_Network-Access-Control-List_dark-bg@4x.png"),
	ENI(ServiceCategory.NETWORK, NetworkInterface.class, "ENI", "Elastic Network Interface", "/img/Amazon-VPC_Elastic-Network-Interface_dark-bg@4x.png")
	;
	
	private ServiceCategory serviceCategory;
	private String name;
	private String alias;
	private String icon;
	private Class<?> clazz;
	
	private ServiceType(ServiceCategory serviceCategory, String name, String alias, String icon) {
		this(serviceCategory, null, name, alias, icon);
	}
	
	private ServiceType(ServiceCategory serviceCategory, Class<?> clazz, String name, String alias, String icon) {
		this.serviceCategory = serviceCategory;
		this.clazz = clazz;
		this.name = name;
		this.alias = alias; 
		this.icon = icon;
	}
	
	public ServiceCategory getServiceCategory() {
		return this.serviceCategory;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	public String getIcon() {
		return this.icon;
	}
	
	public Class<?> getClazz() {
		return this.clazz;
	}
	
}

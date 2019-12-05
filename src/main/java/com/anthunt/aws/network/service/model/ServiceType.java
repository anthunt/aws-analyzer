package com.anthunt.aws.network.service.model;

public enum ServiceType {

	VPC(ServiceCategory.NETWORK, "VPC", "Virtual Private Cloud", "/img/Virtual-private-cloud-VPC_dark-bg@4x.png"),
	PUSB(ServiceCategory.NETWORK, "PUSB", "Public Subnet", "/img/VPC-subnet-public_dark-bg@4x.png"),
	PRSB(ServiceCategory.NETWORK, "PRSB", "Private Subnet", "/img/VPC-subnet-private_dark-bg@4x.png"),
	EC2(ServiceCategory.COMPUTE, "EC2", "EC2 Instance", "/img/Amazon-EC2_Instance_dark-bg@4x.png"),
	ELB(ServiceCategory.NETWORK, "ELB", "Elastic Load Balancer", "/img/Elastic-Load-Balancing@4x.png"),
	AURORA(ServiceCategory.DATABASE, "AURORA", "Aurora DB Cluster", "/img/Amazon-Aurora@4x.png"),
	RDS(ServiceCategory.DATABASE, "RDS", "RDS DB Instance", "/img/Amazon-RDS@4x.png"),
	CLB(ServiceCategory.NETWORK, "CLB", "Classic Load Balancer", "/img/Elastic-Load-Balancing_Classic-load-balancer_dark-bg@4x.png"),
	ALB(ServiceCategory.NETWORK, "ALB", "Application Load Balancer", "/img/Elastic-Load-Balancing-ELB_Application-load-balancer_dark-bg@4x.png"),
	NLB(ServiceCategory.NETWORK, "NLB", "Network Load Balancer", "/img/Elastic-Load-Balancing-ELB_Network-load-balancer_dark-bg@4x.png"),
	SG(ServiceCategory.SECURITY, "SG", "Security Group", "/img/SecurityGroup_dark-bg@4x.png"),
	EBS(ServiceCategory.STORAGE, "EBS", "EBS Volume", "/img/Amazon-Elastic-Block-Store-EBS@4x.png"),
	RT(ServiceCategory.NETWORK, "RT", "Route Table", "/img/Amazon-Route-53_Route-Table_dark-bg@4x.png"),
	PEERING(ServiceCategory.NETWORK, "PEERING", "Vpc Peering", "/img/Amazon-VPC_Peering_dark-bg@4x.png"),
	VGW(ServiceCategory.NETWORK, "VGW", "Virtual Gateway", "/img/Amazon-VPC_Virtual-Gateway_dark-bg@4x.png"),
	CGW(ServiceCategory.NETWORK, "CGW", "Customer Gateway", "/img/Amazon-VPC_Customer-Gateway_dark-bg@4x.png"),
	VPNC(ServiceCategory.NETWORK, "VPNC", "Vpn Connection", "/img/Amazon-VPC_VPN-Connection_dark-bg@4x.png"),
	DX(ServiceCategory.NETWORK, "DX", "Direct Connect", "/img/AWS-Direct-Connect@4x.png"),
	VIF(ServiceCategory.NETWORK, "VIF", "Virtual Interface", "/img/AWS-Direct-Connect@4x.png"),
	VND(ServiceCategory.NETWORK, "VND", "VPC Endpoint", "/img/Amazon-VPC_Endpoints_dark-bg@4x.png"),
	IGW(ServiceCategory.NETWORK, "IGW", "Internet Gateway", "/img/Amazon-VPC_Internet-Gateway_dark-bg@4x.png"),
	EGW(ServiceCategory.NETWORK, "EGW", "Egress Only Internet Gateway", "/img/Amazon-VPC_Internet-Gateway_dark-bg@4x.png"),
	TGW(ServiceCategory.NETWORK, "TGW", "Transit Gateway", "/img/AWS-Transit-Gateway@4x.png"),
	NACL(ServiceCategory.SECURITY, "NACL", "Network ACL", "/img/Amazon-VPC_Network-Access-Control-List_dark-bg@4x.png"),
	ENI(ServiceCategory.NETWORK, "ENI", "Elastic Network Interface", "/img/Amazon-VPC_Elastic-Network-Interface_dark-bg@4x.png")
	;
	
	private ServiceCategory serviceCategory;
	private String name;
	private String alias;
	private String icon;
	
	private ServiceType(ServiceCategory serviceCategory, String name, String alias, String icon) {
		this.serviceCategory = serviceCategory;
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
	
}

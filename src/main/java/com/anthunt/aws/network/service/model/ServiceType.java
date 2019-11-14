package com.anthunt.aws.network.service.model;

public enum ServiceType {

	VPC("VPC", "Virtual Private Cloud", "/img/Virtual-private-cloud-VPC_dark-bg@4x.png"),
	PUSB("PUSB", "Public Subnet", "/img/VPC-subnet-public_dark-bg@4x.png"),
	PRSB("PRSB", "Private Subnet", "/img/VPC-subnet-private_dark-bg@4x.png"),
	EC2("EC2", "EC2 Instance", "/img/Amazon-EC2_Instance_dark-bg@4x.png"),
	ELB("ELB", "Elastic Load Balancer", "/img/Elastic-Load-Balancing@4x.png"),
	CLB("CLB", "Classic Load Balancer", "/img/Elastic-Load-Balancing_Classic-load-balancer_dark-bg@4x.png"),
	ALB("ALB", "Application Load Balancer", "/img/Elastic-Load-Balancing-ELB_Application-load-balancer_dark-bg@4x.png"),
	NLB("NLB", "Network Load Balancer", "/img/Elastic-Load-Balancing-ELB_Network-load-balancer_dark-bg@4x.png"),
	SG("SG", "Security Group", "/img/SecurityGroup_dark-bg@4x.png"),
	RT("RT", "Route Table", "/img/Amazon-Route-53_Route-Table_dark-bg@4x.png"),
	VGW("VGW", "Virtual Gateway", "/img/Amazon-VPC_Virtual-Gateway_dark-bg@4x.png"),
	VPNC("VPNC", "Vpn Connection", "/img/Amazon-VPC_VPN-Connection_dark-bg@4x.png"),
	DX("DX", "Direct Connect", "/img/AWS-Direct-Connect@4x.png"),
	NACL("NACL", "Network ACL", "/img/Amazon-VPC_Network-Access-Control-List_dark-bg@4x.png")
	;
	
	private String name;
	private String alias;
	private String icon;
	
	private ServiceType(String name, String alias, String icon) {
		this.name = name;
		this.alias = alias;
		this.icon = icon;
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

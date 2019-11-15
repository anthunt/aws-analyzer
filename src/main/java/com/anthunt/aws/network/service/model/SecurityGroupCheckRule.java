package com.anthunt.aws.network.service.model;

import software.amazon.awssdk.services.ec2.model.SecurityGroup;

public class SecurityGroupCheckRule implements CheckRule {

	private boolean isCidr;
	private String id;
	private String name;
	private String prototol;
	private String cidr;
	private Integer fromPort;
	private Integer toPort;
	private DirectionType directionType;
	private SecurityGroup securityGroup;

	public SecurityGroupCheckRule(String id, String name, SecurityGroup securityGroup) {
		this.setName(name);
		this.setId(id);
		this.setSecurityGroup(securityGroup);
	}
	
	public boolean isCidr() {
		return isCidr;
	}

	public void setCidr(boolean isCidr) {
		this.isCidr = isCidr;
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getPrototol() {
		return prototol;
	}
	
	public void setPrototol(String prototol) {
		this.prototol = prototol;
	}
	
	public String getCidr() {
		return cidr;
	}
	
	public void setCidr(String cidr) {
		this.cidr = cidr;
	}
	
	public Integer getFromPort() {
		return fromPort;
	}
	
	public void setFromPort(Integer fromPort) {
		this.fromPort = fromPort;
	}
	
	public Integer getToPort() {
		return toPort;
	}
	
	public void setToPort(Integer toPort) {
		this.toPort = toPort;
	}
	
	public DirectionType getDirectionType() {
		return directionType;
	}
	
	public void setDirectionType(DirectionType directionType) {
		this.directionType = directionType;
	}

	public SecurityGroup getSecurityGroup() {
		return securityGroup;
	}

	public void setSecurityGroup(SecurityGroup securityGroup) {
		this.securityGroup = securityGroup;
	}
	
}

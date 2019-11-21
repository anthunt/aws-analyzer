package com.anthunt.aws.network.service.model.checker;

import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.PortRange;
import software.amazon.awssdk.services.ec2.model.RuleAction;

public class NetworkAclCheckRule implements CheckRule {

	private String id;
	private String name;
	private DirectionType directionType;
	private String ruleNumber;
	private String cidr;
	private PortRange portRange;
	private String protocol;
	private RuleAction ruleAction;
	private String routeTableId;
	private NetworkAcl networkAcl;
	
	public NetworkAclCheckRule(String id, String name, NetworkAcl networkAcl) {
		this.setId(id);
		this.setName(name);
		this.setNetworkAcl(networkAcl);
	}
	
	public String getId() {
		return this.id;
	}
	
	private void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	public DirectionType getDirectionType() {
		return this.directionType;
	}
	
	public void setDirectionType(DirectionType directionType) {
		this.directionType = directionType;
	}

	public String getRuleNumber() {
		return this.ruleNumber;
	}
	
	public void setRuleNumber(String ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	public String getCidr() {
		return this.cidr;
	}
	
	public void setCidr(String cidr) {
		this.cidr = cidr;
	}

	public PortRange getPortRange() {
		return this.portRange;
	}
	
	public void setPortRange(PortRange portRange) {
		this.portRange = portRange;
	}

	public String getPrototol() {
		return this.protocol;
	}
	
	public void setProtocol(String prototol) {
		this.protocol = prototol;
	}

	public RuleAction getRuleAction() {
		return this.ruleAction;
	}
	
	public void setRuleAction(RuleAction ruleAction) {
		this.ruleAction = ruleAction;
	}

	public String getRouteTableId() {
		return routeTableId;
	}

	public void setRouteTableId(String routeTableId) {
		this.routeTableId = routeTableId;
	}

	public NetworkAcl getNetworkAcl() {
		return networkAcl;
	}

	public void setNetworkAcl(NetworkAcl networkAcl) {
		this.networkAcl = networkAcl;
	}

	@Override
	public String toString() {
		return new StringBuffer()
				.append("{")
				.append("id:").append(this.getId())
				.append(", name:").append(this.getName())
				.append(", rule number:").append(this.getRuleNumber())
				.append(", protocol:").append(this.getPrototol())
				.append(", cidr:").append(this.getCidr())
				.append(", direction:").append(this.getDirectionType())
				.append(", port range:").append(this.getPortRange())
				.append(", rule action:").append(this.getRuleAction())
				.append("}")
				.toString()
				;
	}
}

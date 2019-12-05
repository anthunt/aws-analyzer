package com.anthunt.aws.network.service.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.service.model.checker.CheckResult;
import com.anthunt.aws.network.service.model.checker.CheckResults;
import com.anthunt.aws.network.service.model.checker.CheckRule;
import com.anthunt.aws.network.service.model.checker.CheckType;
import com.anthunt.aws.network.service.model.checker.DirectionType;
import com.anthunt.aws.network.service.model.checker.NetworkAclCheckRule;
import com.anthunt.aws.network.service.model.checker.RouteCheckRule;
import com.anthunt.aws.network.service.model.checker.SecurityGroupCheckRule;
import com.anthunt.aws.network.service.model.diagram.NodeType;
import com.anthunt.aws.network.utils.Utils;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;
import software.amazon.awssdk.services.ec2.model.Ipv6Range;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkAclEntry;
import software.amazon.awssdk.services.ec2.model.PrefixList;
import software.amazon.awssdk.services.ec2.model.PrefixListId;
import software.amazon.awssdk.services.ec2.model.Route;
import software.amazon.awssdk.services.ec2.model.RouteState;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.RuleAction;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.UserIdGroupPair;
import software.amazon.awssdk.services.ec2.model.VpnGateway;

public abstract class AbstractNetwork<T> {
	
	private ServiceRepository serviceRepository;
	private T resource;
	private Map<String, List<SecurityGroupCheckRule>> sgRulesMap = new HashMap<String, List<SecurityGroupCheckRule>>();
	private List<RouteCheckRule> routeCheckRules = new ArrayList<RouteCheckRule>();
	private List<NetworkAclCheckRule> networkAclCheckRules = new ArrayList<>();
	
	protected AbstractNetwork(String resourceId, ServiceRepository serviceRepository) {
		
		this.serviceRepository = serviceRepository;
		this.resource = this.getResource(resourceId, serviceRepository);
				                     
		this.setSecurityGroupRules(this.getSecurityGroupIds());
		List<String> subnetIds = this.getSubnetIds();
		for(String subnetId : subnetIds) {
			String routeTableId = this.setRoutes(subnetId);
			this.setNetworkAcl(subnetId, routeTableId);
		}
	}
	
	protected abstract T getResource(String resourceId, ServiceRepository serviceRepository);
	protected abstract String getVpcId();
	protected abstract List<String> getSubnetIds();
	protected abstract List<String> getSecurityGroupIds();
	
	public ServiceRepository getServiceRepository() {
		return this.serviceRepository;
	}
	
	public List<SecurityGroupCheckRule> getSecurityGroupRules(SecurityGroup securityGroup) {
		
		String securityGroupName = securityGroup.groupName();
		
		List<SecurityGroupCheckRule> checkRules = new ArrayList<>();
		
		List<IpPermission> inBoundIpPermissions = securityGroup.ipPermissions();
		for (IpPermission inBoundIpPermission : inBoundIpPermissions) {				
			
			List<IpRange> ipRanges = inBoundIpPermission.ipRanges();
			for(IpRange ipRange : ipRanges) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.INGRESS);
				checkRule.setPrototol(inBoundIpPermission.ipProtocol());
				checkRule.setFromPort(inBoundIpPermission.fromPort());
				checkRule.setToPort(inBoundIpPermission.toPort());
				checkRule.setCidr(ipRange.cidrIp());
				checkRule.setCidr(true);
				checkRules.add(checkRule);
			}
			List<Ipv6Range> ipv6Ranges = inBoundIpPermission.ipv6Ranges();
			for(Ipv6Range ipv6Range : ipv6Ranges) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.INGRESS);
				checkRule.setPrototol(inBoundIpPermission.ipProtocol());
				checkRule.setFromPort(inBoundIpPermission.fromPort());
				checkRule.setToPort(inBoundIpPermission.toPort());
				checkRule.setCidr(ipv6Range.cidrIpv6());
				checkRule.setCidr(true);
				checkRules.add(checkRule);
			}
			List<PrefixListId> prefixListIds = inBoundIpPermission.prefixListIds();
			for(PrefixListId prefixListId : prefixListIds) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.INGRESS);
				checkRule.setPrototol(inBoundIpPermission.ipProtocol());
				checkRule.setFromPort(inBoundIpPermission.fromPort());
				checkRule.setToPort(inBoundIpPermission.toPort());
				checkRule.setCidr(prefixListId.prefixListId());
				checkRule.setCidr(false);
				checkRules.add(checkRule);
			}
			List<UserIdGroupPair> userIdGroupPairs = inBoundIpPermission.userIdGroupPairs();
			for(UserIdGroupPair userIdGroupPair : userIdGroupPairs) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.INGRESS);
				checkRule.setPrototol(inBoundIpPermission.ipProtocol());
				checkRule.setFromPort(inBoundIpPermission.fromPort());
				checkRule.setToPort(inBoundIpPermission.toPort());
				checkRule.setCidr(userIdGroupPair.groupId());
				checkRule.setCidr(false);
				checkRules.add(checkRule);
			}
			
		}

		List<IpPermission> outBoundIpPermissions = securityGroup.ipPermissionsEgress();
		for (IpPermission outBoundIpPermission : outBoundIpPermissions) {
			List<IpRange> ipRanges = outBoundIpPermission.ipRanges();
			for(IpRange ipRange : ipRanges) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.EGRESS);
				checkRule.setPrototol(outBoundIpPermission.ipProtocol());
				checkRule.setFromPort(outBoundIpPermission.fromPort());
				checkRule.setToPort(outBoundIpPermission.toPort());
				checkRule.setCidr(ipRange.cidrIp());
				checkRule.setCidr(true);
				checkRules.add(checkRule);
			}
			List<Ipv6Range> ipv6Ranges = outBoundIpPermission.ipv6Ranges();
			for(Ipv6Range ipv6Range : ipv6Ranges) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.EGRESS);
				checkRule.setPrototol(outBoundIpPermission.ipProtocol());
				checkRule.setFromPort(outBoundIpPermission.fromPort());
				checkRule.setToPort(outBoundIpPermission.toPort());
				checkRule.setCidr(ipv6Range.cidrIpv6());
				checkRule.setCidr(true);
				checkRules.add(checkRule);
			}
			List<PrefixListId> prefixListIds = outBoundIpPermission.prefixListIds();
			for(PrefixListId prefixListId : prefixListIds) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.EGRESS);
				checkRule.setPrototol(outBoundIpPermission.ipProtocol());
				checkRule.setFromPort(outBoundIpPermission.fromPort());
				checkRule.setToPort(outBoundIpPermission.toPort());
				checkRule.setCidr(prefixListId.prefixListId());
				checkRule.setCidr(false);
				checkRules.add(checkRule);
			}
			List<UserIdGroupPair> userIdGroupPairs = outBoundIpPermission.userIdGroupPairs();
			for(UserIdGroupPair userIdGroupPair : userIdGroupPairs) {
				SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName, securityGroup);
				checkRule.setDirectionType(DirectionType.EGRESS);
				checkRule.setPrototol(outBoundIpPermission.ipProtocol());
				checkRule.setFromPort(outBoundIpPermission.fromPort());
				checkRule.setToPort(outBoundIpPermission.toPort());
				checkRule.setCidr(userIdGroupPair.groupId());
				checkRule.setCidr(false);
				checkRules.add(checkRule);
			}
		}
		
		return checkRules;
	}
	
	private void setSecurityGroupRules(List<String> securityGroupIds) {		
		for(String securityGroupId : securityGroupIds) {
			SecurityGroup securityGroup = serviceRepository.getSecurityGroupMap().get(securityGroupId);
			String securityGroupName = securityGroup.groupName();
			this.sgRulesMap.put(securityGroupName, this.getSecurityGroupRules(securityGroup));
		}
	}
	
	private String setRoutes(String subnetId) {
		String routeTableId = "";
		List<RouteTable> routeTables = this.serviceRepository.getRouteTablesMap().get(subnetId);
		for(RouteTable routeTable : routeTables) {
			routeTableId = routeTable.routeTableId();
			String routeTableName = "Unknown Route Table";
			List<Tag> tags = routeTable.tags();
			for(Tag tag : tags) {
				if("Name".equals(tag.key())) {
					routeTableName = tag.value();
				}
			}
			
			List<Route> routes = routeTable.routes();
			for (Route route : routes) {
				
				RouteCheckRule routeCheckRule = new RouteCheckRule(routeTable.routeTableId(), routeTableName, routeTable);
				
				if(route.destinationCidrBlock() != null) {
					routeCheckRule.setCidr(true);
					routeCheckRule.addCidr(route.destinationCidrBlock());
				}
				
				if(route.destinationIpv6CidrBlock() != null) {
					routeCheckRule.setCidr(true);
					routeCheckRule.addCidr(route.destinationIpv6CidrBlock());
				}
				
				if(route.destinationPrefixListId() != null) {
					PrefixList prefixList = this.serviceRepository.getPrefixListMap().get(route.destinationPrefixListId());
					routeCheckRule.setPrefixListId(prefixList.prefixListId());
					routeCheckRule.setPrefixListName(prefixList.prefixListName());

					List<String> cidrs = prefixList.cidrs();
					for (String cidr : cidrs) {
						routeCheckRule.addCidr(cidr);
					}
				}
				
				if(route.egressOnlyInternetGatewayId() != null) routeCheckRule.setGateway(NodeType.EGRESS_INTERNET_GATEWAY, route.egressOnlyInternetGatewayId());
				if(route.gatewayId() != null) {
					NodeType gatewayType = NodeType.getGatewayType(route.gatewayId());
					routeCheckRule.setGateway(gatewayType, route.gatewayId());
					if(routeCheckRule.getGatewayType() == NodeType.VIRTUAL_GATEWAY) {
						this.setVirtualGateway(routeCheckRule);
					}
					
				}
				if(route.instanceId() != null) routeCheckRule.setGateway(NodeType.EC2_INSTANCE, route.instanceId());
				if(route.natGatewayId() != null) routeCheckRule.setGateway(NodeType.NAT_GATEWAY, route.natGatewayId());
				if(route.networkInterfaceId() != null) routeCheckRule.setGateway(NodeType.NETWORK_INTERFACE, route.networkInterfaceId());
				if(route.transitGatewayId() != null) routeCheckRule.setGateway(NodeType.TRANSIT_GATEWAY, route.transitGatewayId());
				if(route.vpcPeeringConnectionId() != null) routeCheckRule.setGateway(NodeType.PEERING, route.vpcPeeringConnectionId());
				
				routeCheckRule.setRouteState(route.state());
				
				routeCheckRules.add(routeCheckRule);
			}
		}		
		return routeTableId;
	}
	
	private void setVirtualGateway(RouteCheckRule routeCheckRule) {
		
		VpnGateway vpnGateway = this.serviceRepository.getVpnGatewayMap().get(routeCheckRule.getGatewayId());
		routeCheckRule.setGatewayName(Utils.getNameFromTags(vpnGateway.tags()));		
		this.setVpn(routeCheckRule);
		this.setDirectConnect(routeCheckRule);
	}
	
	private void setVpn(RouteCheckRule routeCheckRule) {
		routeCheckRule.setVpnConnections(this.serviceRepository.getVpnConnectionsMap().get(routeCheckRule.getGatewayId()));
	}
	
	private void setDirectConnect(RouteCheckRule routeCheckRule) {		
		routeCheckRule.setVirtualInterfaces(this.serviceRepository.getVirtualInterfacesMap().get(routeCheckRule.getGatewayId()));
	}
	
	private void setNetworkAcl(String subnetId, String routeTableId) {
		List<NetworkAcl> networkAcls = this.serviceRepository.getNetworkAclsMap().get(subnetId);
		for (NetworkAcl networkAcl : networkAcls) {
			String networkAclName = "Unknown NetworkAcl";
			List<Tag> tags = networkAcl.tags();
			for(Tag tag : tags) {
				if("Name".equals(tag.key())) {
					networkAclName = tag.value();
				}
			}
			List<NetworkAclEntry> networkAclEntries = networkAcl.entries();
			for (NetworkAclEntry networkAclEntry : networkAclEntries) {	
				NetworkAclCheckRule networkAclCheckRule = new NetworkAclCheckRule(networkAcl.networkAclId(), networkAclName, networkAcl);
				networkAclCheckRule.setRouteTableId(routeTableId);
				networkAclCheckRule.setDirectionType(networkAclEntry.egress() ? DirectionType.EGRESS : DirectionType.INGRESS);
				networkAclCheckRule.setRuleNumber(networkAclEntry.ruleNumber() == 32767 ? "*" : networkAclEntry.ruleNumber().toString());
				networkAclCheckRule.setCidr(networkAclEntry.cidrBlock());
				networkAclCheckRule.setPortRange(networkAclEntry.portRange());
				networkAclCheckRule.setProtocol("-1".equals(networkAclEntry.protocol()) ? "All" : networkAclEntry.protocol());
				networkAclCheckRule.setRuleAction(networkAclEntry.ruleAction());
				networkAclCheckRules.add(networkAclCheckRule);
			}
		}
	}
	
	public CheckResults<T> checkCommunication(String cidr) {
		
		CheckResults<T> checkResults = new CheckResults<T>(this.resource);
		if(cidr != null) {
			checkResults.setCidr(cidr);
			checkResults.put(CheckType.ROUTE_TABLE, this.checkRoute(cidr));
			checkResults.put(CheckType.SECURITY_GROUP, this.checkSecurityGroup(cidr));
			checkResults.put(CheckType.NETWORK_ACL, this.checkNetworkAcl(cidr));
		} else {
			checkResults.put(CheckType.ROUTE_TABLE, this.getAllRoute());
			checkResults.put(CheckType.SECURITY_GROUP, this.getAllSecurityGroup());
			checkResults.put(CheckType.NETWORK_ACL, this.getAllNetworkAcl());
		}
		
		return checkResults;		
	}
	
	private CheckResult getAllRoute() {
		CheckResult checkResult = new CheckResult();
		
		List<CheckRule> allowRules = new ArrayList<>();
		List<CheckRule> denyRules = new ArrayList<>();
		
		for(RouteCheckRule checkRule : this.routeCheckRules) {
				
			if(checkRule.getRouteState() == RouteState.ACTIVE) {
				allowRules.add(checkRule);
			} else {
				denyRules.add(checkRule);
			}
		}
		
		boolean isSuccess = allowRules.size() > 0;
		checkResult.setInSuccess(isSuccess);
		checkResult.setOutSuccess(isSuccess);
		checkResult.setAllowRules(allowRules);
		checkResult.setDenyRules(denyRules);
		return checkResult;
	}
	
	private CheckResult checkRoute(String cidr) {
		CheckResult checkResult = new CheckResult();
		
		List<CheckRule> allowRules = new ArrayList<>();
		List<CheckRule> denyRules = new ArrayList<>();
		
		IPAddress targetIp = new IPAddressString(cidr).getAddress();
		
		for(RouteCheckRule checkRule : this.routeCheckRules) {
				
			for(String destination : checkRule.getCidrs()) {
				IPAddress allowIp = new IPAddressString(destination).getAddress();
				if(allowIp.contains(targetIp)) {
					if(checkRule.getRouteState() == RouteState.ACTIVE) {
						allowRules.add(checkRule);
					} else {
						denyRules.add(checkRule);
					}
					break;
				}
			}
			
			if(allowRules.size() > 0) {
				break;
			}
		}
		
		boolean isSuccess = allowRules.size() > 0;
		checkResult.setInSuccess(isSuccess);
		checkResult.setOutSuccess(isSuccess);
		checkResult.setAllowRules(allowRules);
		checkResult.setDenyRules(denyRules);
		return checkResult;
	}
	
	public CheckResult getAllSecurityGroup(Map<String, List<SecurityGroupCheckRule>> sgRulesMap) {
		
		CheckResult checkResult = new CheckResult();		
		List<CheckRule> allowRules = new ArrayList<>();		
		
		Set<String> keys = sgRulesMap.keySet();
		Iterator<String> iKeys = keys.iterator();
		while(iKeys.hasNext()) {
			String securityGroupName = iKeys.next();
			List<SecurityGroupCheckRule> checkRules = sgRulesMap.get(securityGroupName);
			for (SecurityGroupCheckRule checkRule : checkRules) {
				if(checkRule.getDirectionType() == DirectionType.INGRESS) {
					checkResult.setInSuccess(true);
				} else {
					checkResult.setOutSuccess(true);
				}
				allowRules.add(checkRule);
			}
		}

		checkResult.setAllowRules(allowRules);
		return checkResult;
	}
	
	private CheckResult getAllSecurityGroup() {
		return this.getAllSecurityGroup(this.sgRulesMap);
	}

	public CheckResult checkSecurityGroup(Map<String, List<SecurityGroupCheckRule>> sgRulesMap, String cidr) {
		List<String> cidrs = new ArrayList<>();
		cidrs.add(cidr);
		return this.checkSecurityGroup(sgRulesMap, cidrs);
	}
	
	public CheckResult checkSecurityGroup(Map<String, List<SecurityGroupCheckRule>> sgRulesMap, List<String> cidrs) {
		
		CheckResult checkResult = new CheckResult();
		
		List<CheckRule> allowRules = new ArrayList<>();
		
		List<IPAddress> targetIps = new ArrayList<>();
		
		for(String cidr : cidrs) {
			targetIps.add(new IPAddressString(cidr).getAddress());
		}
		
		Set<String> keys = sgRulesMap.keySet();
		Iterator<String> iKeys = keys.iterator();
		while(iKeys.hasNext()) {
			String securityGroupName = iKeys.next();
			List<SecurityGroupCheckRule> checkRules = sgRulesMap.get(securityGroupName);
			for (SecurityGroupCheckRule checkRule : checkRules) {
				if(checkRule.isCidr()) {
					IPAddress allowIp = new IPAddressString(checkRule.getCidr()).getAddress();
					for(IPAddress targetIp : targetIps) {
						if(allowIp.contains(targetIp)) {
							if(checkRule.getDirectionType() == DirectionType.INGRESS) {
								checkResult.setInSuccess(true);
							} else {
								checkResult.setOutSuccess(true);
							}
							allowRules.add(checkRule);
							break;
						}
					}
				}
			}
		}

		checkResult.setAllowRules(allowRules);
		return checkResult;
	}

	private CheckResult checkSecurityGroup(String cidr) {
		return this.checkSecurityGroup(this.sgRulesMap, cidr);
	}
	
	private CheckResult getAllNetworkAcl() {
		
		CheckResult checkResult = new CheckResult();
				
		List<CheckRule> allowRules = new ArrayList<>();
		List<CheckRule> denyRules = new ArrayList<>();
		
		for(NetworkAclCheckRule checkRule : this.networkAclCheckRules) {	
			if(checkRule.getDirectionType() == DirectionType.INGRESS) {
				if(checkRule.getRuleAction() == RuleAction.ALLOW) {
					checkResult.setInSuccess(true);
					allowRules.add(checkRule);
				} else {
					denyRules.add(checkRule);
				}
			} else if(checkRule.getDirectionType() == DirectionType.EGRESS) {
				if(checkRule.getRuleAction() == RuleAction.ALLOW) {
					checkResult.setOutSuccess(true);
					allowRules.add(checkRule);
				} else {
					denyRules.add(checkRule);
				}
			}
		}
		
		checkResult.setAllowRules(allowRules);
		checkResult.setDenyRules(denyRules);
		
		return checkResult;
	}
	
	private CheckResult checkNetworkAcl(String cidr) {
	
		CheckResult checkResult = new CheckResult();
				
		List<CheckRule> allowRules = new ArrayList<>();
		List<CheckRule> denyRules = new ArrayList<>();
		
		IPAddress targetIp = new IPAddressString(cidr).getAddress();
		
		boolean in = false;
		boolean out = false;
		for(NetworkAclCheckRule checkRule : this.networkAclCheckRules) {
			IPAddress allowIp = new IPAddressString(checkRule.getCidr()).getAddress();
			if(allowIp.contains(targetIp)) {
				
				if(!in && checkRule.getDirectionType() == DirectionType.INGRESS) {
					in = true;
					if(checkRule.getRuleAction() == RuleAction.ALLOW) {
						checkResult.setInSuccess(true);
						allowRules.add(checkRule);
					} else {
						denyRules.add(checkRule);
					}
				} else if(!out && checkRule.getDirectionType() == DirectionType.EGRESS) {
					out = true;
					if(checkRule.getRuleAction() == RuleAction.ALLOW) {
						checkResult.setOutSuccess(true);
						allowRules.add(checkRule);
					} else {
						denyRules.add(checkRule);
					}
				}
				
			}
		}
		
		checkResult.setAllowRules(allowRules);
		checkResult.setDenyRules(denyRules);
		
		return checkResult;
	}

}

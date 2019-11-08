package com.anthunt.aws.network.service.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anthunt.aws.network.service.model.CheckResult;
import com.anthunt.aws.network.service.model.CheckResults;
import com.anthunt.aws.network.service.model.CheckRule;
import com.anthunt.aws.network.service.model.CheckType;
import com.anthunt.aws.network.service.model.DirectionType;
import com.anthunt.aws.network.service.model.NetworkAclCheckRule;
import com.anthunt.aws.network.service.model.RouteCheckRule;
import com.anthunt.aws.network.service.model.SecurityGroupCheckRule;
import com.anthunt.aws.network.service.model.diagram.NodeType;
import com.anthunt.aws.network.utils.Utils;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directconnect.model.DescribeVirtualInterfacesResponse;
import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkAclsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkAclsResponse;
import software.amazon.awssdk.services.ec2.model.DescribePrefixListsRequest;
import software.amazon.awssdk.services.ec2.model.DescribePrefixListsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpnConnectionsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpnConnectionsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpnGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpnGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
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

public abstract class AbstractNetworkBak<T> {
	
	private String profileName;
	private Region region;
	private Ec2Client ec2Client;
	private T resource;
	private Filter subnetFilter;
	private List<Filter> mainRouteFilters = new ArrayList<>();
	private List<String> securityGroupIds;
	private Map<String, List<SecurityGroupCheckRule>> sgRulesMap = new HashMap<String, List<SecurityGroupCheckRule>>();
	private List<RouteCheckRule> routeCheckRules = new ArrayList<RouteCheckRule>();
	private List<NetworkAclCheckRule> networkAclCheckRules = new ArrayList<>();
	
	protected AbstractNetworkBak(String resourceId, String profileName, String regionId) {
		
		this.profileName = profileName;
		this.region = Region.of(regionId);
		
		this.ec2Client = Ec2Client.builder()
								  .credentialsProvider(
										  ProfileCredentialsProvider.builder()
										  							.profileName(this.profileName)
										  							.build()
								   )
								  .region(this.region)
								  .build();
		
		this.resource = this.getResource(resourceId, profileName, regionId);
		this.securityGroupIds = this.getSecurityGroupIds();
		
		this.subnetFilter = Filter.builder()
								  .name("association.subnet-id")
								  .values(this.getSubnetIds())
								  .build();
		
		this.mainRouteFilters.add(Filter.builder()
				                     .name("association.main")
				                     .values("true")
				                     .build());
		this.mainRouteFilters.add(Filter.builder()
										.name("vpc-id")
										.values(this.getVpcId())
										.build());
				                     
		this.setSecurityGroupRules();
		this.setRoutes();
		this.setNetworkAcl();
	}
	
	protected abstract T getResource(String resourceId, String profileName, String regionId);
	protected abstract String getVpcId();
	protected abstract List<String> getSubnetIds();
	protected abstract List<String> getSecurityGroupIds();
	
	private void setSecurityGroupRules() {
		
		DescribeSecurityGroupsResponse describeSecurityGroupsResponse = this.ec2Client.describeSecurityGroups(
																			DescribeSecurityGroupsRequest.builder()
																										 .groupIds(this.securityGroupIds)
																										 .build()
																		);
		List<SecurityGroup> securityGroups = describeSecurityGroupsResponse.securityGroups();
		for (SecurityGroup securityGroup : securityGroups) {
			
			String securityGroupName = securityGroup.groupName();
			
			List<SecurityGroupCheckRule> checkRules = new ArrayList<>();
			
			List<IpPermission> inBoundIpPermissions = securityGroup.ipPermissions();
			for (IpPermission inBoundIpPermission : inBoundIpPermissions) {				
				
				List<IpRange> ipRanges = inBoundIpPermission.ipRanges();
				for(IpRange ipRange : ipRanges) {
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
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
					SecurityGroupCheckRule checkRule = new SecurityGroupCheckRule(securityGroup.groupId(), securityGroupName);
					checkRule.setDirectionType(DirectionType.EGRESS);
					checkRule.setPrototol(outBoundIpPermission.ipProtocol());
					checkRule.setFromPort(outBoundIpPermission.fromPort());
					checkRule.setToPort(outBoundIpPermission.toPort());
					checkRule.setCidr(userIdGroupPair.groupId());
					checkRule.setCidr(false);
					checkRules.add(checkRule);
				}
			}
			this.sgRulesMap.put(securityGroupName, checkRules);
		}
	}
	
	private void setRoutes() {
		
		DescribeRouteTablesResponse describeRouteTablesResponse = this.ec2Client.describeRouteTables(
			DescribeRouteTablesRequest.builder()
									  .filters(subnetFilter)
									  .build()
		);
		List<RouteTable> routeTables = describeRouteTablesResponse.routeTables();
		
		if(routeTables.size() < 1) {
			describeRouteTablesResponse = this.ec2Client.describeRouteTables(
					DescribeRouteTablesRequest.builder()
											  .filters(this.mainRouteFilters)
											  .build()
			);
			routeTables = describeRouteTablesResponse.routeTables();
		}
		
		RouteTable routeTable = routeTables.get(0);
		
		String routeTableName = "Unknown Route Table";
		List<Tag> tags = routeTable.tags();
		for(Tag tag : tags) {
			if("Name".equals(tag.key())) {
				routeTableName = tag.value();
			}
		}
		
		List<Route> routes = routeTable.routes();
		for (Route route : routes) {
			
			RouteCheckRule routeCheckRule = new RouteCheckRule(routeTable.routeTableId(), routeTableName);
			
			if(route.destinationCidrBlock() != null) {
				routeCheckRule.setCidr(true);
				routeCheckRule.addCidr(route.destinationCidrBlock());
			}
			
			if(route.destinationIpv6CidrBlock() != null) {
				routeCheckRule.setCidr(true);
				routeCheckRule.addCidr(route.destinationIpv6CidrBlock());
			}
			
			if(route.destinationPrefixListId() != null) {
				DescribePrefixListsResponse describePrefixListsResponse = ec2Client.describePrefixLists(
																						DescribePrefixListsRequest.builder()
																												  .prefixListIds(route.destinationPrefixListId())
																												  .build()
																		  );
				List<PrefixList> prefixLists = describePrefixListsResponse.prefixLists();
				if(prefixLists.size() > 0) {
					PrefixList prefixList = prefixLists.get(0);
					routeCheckRule.setPrefixListId(prefixList.prefixListId());
					routeCheckRule.setPrefixListName(prefixList.prefixListName());

					List<String> cidrs = prefixList.cidrs();
					for (String cidr : cidrs) {
						routeCheckRule.addCidr(cidr);
					}
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
	
	private void setVirtualGateway(RouteCheckRule routeCheckRule) {
		
		DescribeVpnGatewaysResponse describeVpnGatewaysResponse = this.ec2Client.describeVpnGateways(DescribeVpnGatewaysRequest.builder().vpnGatewayIds(routeCheckRule.getGatewayId()).build());
		for(VpnGateway vpnGateway : describeVpnGatewaysResponse.vpnGateways()) {
			routeCheckRule.setGatewayName(Utils.getNameFromTags(vpnGateway.tags()));
		}
		
		this.setVpn(routeCheckRule);
		this.setDirectConnect(routeCheckRule);
	}
	
	private void setVpn(RouteCheckRule routeCheckRule) {
		
		DescribeVpnConnectionsResponse describeVpnConnectionsResponse = this.ec2Client.describeVpnConnections(
				DescribeVpnConnectionsRequest.builder()
											 .filters(Filter.builder().name("vpn-gateway-id").values(routeCheckRule.getGatewayId()).build())
											 .build()
		);
		routeCheckRule.setVpnConnections(describeVpnConnectionsResponse.vpnConnections());
	}
	
	private void setDirectConnect(RouteCheckRule routeCheckRule) {
		DirectConnectClient directConnectClient = DirectConnectClient.builder()
																	 .credentialsProvider(ProfileCredentialsProvider.builder().profileName(this.profileName).build())
																	 .region(this.region)
																	 .build();
		
		List<VirtualInterface> virtualInterfaces = new ArrayList<>();
		DescribeVirtualInterfacesResponse describeVirtualInterfacesResponse = directConnectClient.describeVirtualInterfaces();
		for(VirtualInterface virtualInterface : describeVirtualInterfacesResponse.virtualInterfaces()) {	
			if(routeCheckRule.getGatewayId().equals(virtualInterface.virtualGatewayId())) {
				virtualInterfaces.add(virtualInterface);
			}
		}
		
		routeCheckRule.setVirtualInterfaces(virtualInterfaces);
	}
	
	private void setNetworkAcl() {
		DescribeNetworkAclsResponse describeNetworkAclsResponse = this.ec2Client.describeNetworkAcls(
																		DescribeNetworkAclsRequest.builder()
																								  .filters(subnetFilter)
																								  .build()
																  );
		
		List<NetworkAcl> networkAcls = describeNetworkAclsResponse.networkAcls();
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
				NetworkAclCheckRule networkAclCheckRule = new NetworkAclCheckRule(networkAcl.networkAclId(), networkAclName);
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
		checkResults.setCidr(cidr);
		checkResults.put(CheckType.ROUTE_TABLE, this.checkRoute(cidr));
		checkResults.put(CheckType.SECURITY_GROUP, this.checkSecurityGroup(cidr));
		checkResults.put(CheckType.NETWORK_ACL, this.checkNetworkAcl(cidr));
		
		return checkResults;		
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
	
	private CheckResult checkSecurityGroup(String cidr) {
		
		CheckResult checkResult = new CheckResult();
		
		List<CheckRule> allowRules = new ArrayList<>();
		
		IPAddress targetIp = new IPAddressString(cidr).getAddress();
		
		Set<String> keys = this.sgRulesMap.keySet();
		Iterator<String> iKeys = keys.iterator();
		while(iKeys.hasNext()) {
			String securityGroupName = iKeys.next();
			List<SecurityGroupCheckRule> checkRules = this.sgRulesMap.get(securityGroupName);
			for (SecurityGroupCheckRule checkRule : checkRules) {
				if(checkRule.isCidr()) {
					IPAddress allowIp = new IPAddressString(checkRule.getCidr()).getAddress();
					if(allowIp.contains(targetIp)) {
						if(checkRule.getDirectionType() == DirectionType.INGRESS) {
							checkResult.setInSuccess(true);
						} else {
							checkResult.setOutSuccess(true);
						}
						allowRules.add(checkRule);
					}
				}
			}
		}

		checkResult.setAllowRules(allowRules);
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
						checkResult.setInSuccess(true);
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

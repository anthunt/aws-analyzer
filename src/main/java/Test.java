import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkAclsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkAclsResponse;
import software.amazon.awssdk.services.ec2.model.DescribePrefixListsRequest;
import software.amazon.awssdk.services.ec2.model.DescribePrefixListsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.GroupIdentifier;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;
import software.amazon.awssdk.services.ec2.model.Ipv6Range;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkAclEntry;
import software.amazon.awssdk.services.ec2.model.PrefixList;
import software.amazon.awssdk.services.ec2.model.PrefixListId;
import software.amazon.awssdk.services.ec2.model.Route;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.UserIdGroupPair;

public class Test {
	
	public static void main(String[] args) {
		
		Ec2Client ec2Client = Ec2Client.builder().credentialsProvider(ProfileCredentialsProvider.builder().profileName("KEAPPMIG").build()).build();
		
		DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances(DescribeInstancesRequest.builder().instanceIds("i-086c17d67bf0dfea1").build());
		
		Instance instance = describeInstancesResponse.reservations().get(0).instances().get(0);
		
		Filter subnetFilter = Filter.builder().name("association.subnet-id").values(instance.subnetId()).build();
		DescribeRouteTablesResponse describeRouteTablesResponse = ec2Client.describeRouteTables(DescribeRouteTablesRequest.builder().filters(subnetFilter).build());
		RouteTable routeTable = describeRouteTablesResponse.routeTables().get(0);
		List<Route> routes = routeTable.routes();
		for (Route route : routes) {
			if(route.destinationCidrBlock() != null) System.out.print("Route : " + route.destinationCidrBlock());
			if(route.destinationIpv6CidrBlock() != null) System.out.print(route.destinationIpv6CidrBlock());
			
			if(route.destinationPrefixListId() != null) {
				DescribePrefixListsResponse describePrefixListsResponse = ec2Client.describePrefixLists(DescribePrefixListsRequest.builder().prefixListIds(route.destinationPrefixListId()).build());
				PrefixList prefixList = describePrefixListsResponse.prefixLists().get(0);
				System.out.print(prefixList.prefixListId());
				System.out.print("|" + prefixList.prefixListName() + "(");
				List<String> cidrs = prefixList.cidrs();
				for (String cidr : cidrs) {
					System.out.print(cidr + ",");
				}
				System.out.print(")");
			}
			System.out.print(" -> ");
			
			if(route.egressOnlyInternetGatewayId() != null) System.out.print(route.egressOnlyInternetGatewayId());
			if(route.gatewayId() != null) System.out.print(route.gatewayId());
			if(route.instanceId() != null) System.out.print(route.instanceId());
			if(route.natGatewayId() != null) System.out.print(route.natGatewayId());
			if(route.networkInterfaceId() != null) System.out.print(route.networkInterfaceId());
			if(route.transitGatewayId() != null) System.out.print(route.transitGatewayId());
			if(route.vpcPeeringConnectionId() != null) System.out.print(route.vpcPeeringConnectionId());
			
			System.out.println(" " + route.state());
			
		}
		
		System.out.println("-------------------------------------------------------------------------------------");
		DescribeNetworkAclsResponse describeNetworkAclsResponse = ec2Client.describeNetworkAcls(DescribeNetworkAclsRequest.builder().filters(subnetFilter).build());
		List<NetworkAcl> networkAcls = describeNetworkAclsResponse.networkAcls();
		for (NetworkAcl networkAcl : networkAcls) {
			List<Tag> tags = networkAcl.tags();
			for(Tag tag : tags) {
				if("Name".equals(tag.key())) {
					System.out.println(tag.value());
				}
			}
			List<NetworkAclEntry> networkAclEntries = networkAcl.entries();
			for (NetworkAclEntry networkAclEntry : networkAclEntries) {
				System.out.print(networkAclEntry.egress() ? "Outbound" : "InBound");
				System.out.print(" " + (networkAclEntry.ruleNumber() == 32767 ? "*" : networkAclEntry.ruleNumber()));
				System.out.print(" " + networkAclEntry.cidrBlock());
				System.out.print(":" + (networkAclEntry.portRange() == null ? "All" : networkAclEntry.portRange()));
				System.out.print(":" + ("-1".equals(networkAclEntry.protocol()) ? "All" : networkAclEntry.protocol()));
				System.out.println(" " + networkAclEntry.ruleAction());
			}
		}
		
		List<String> groupIds = new ArrayList<>();
		List<GroupIdentifier> groupIdentifiers = instance.securityGroups();
		for (GroupIdentifier groupIdentifier : groupIdentifiers) {
			groupIds.add(groupIdentifier.groupId());
		}
		
		DescribeSecurityGroupsResponse describeSecurityGroupsResponse = ec2Client.describeSecurityGroups(DescribeSecurityGroupsRequest.builder().groupIds(groupIds).build());
		List<SecurityGroup> securityGroups = describeSecurityGroupsResponse.securityGroups();
		for (SecurityGroup securityGroup : securityGroups) {
			System.out.println("InBound SG Rules");
			List<IpPermission> inBoundIpPermissions = securityGroup.ipPermissions();
			for (IpPermission inBoundIpPermission : inBoundIpPermissions) {
				System.out.print(("-1".equals(inBoundIpPermission.ipProtocol()) ? "All Traffic" : inBoundIpPermission.ipProtocol()));
				
				if(!"-1".equals(inBoundIpPermission.ipProtocol())) {
					if((inBoundIpPermission.fromPort() == null && inBoundIpPermission.toPort() == null)) {
						System.out.print(" All");
					} else if(inBoundIpPermission.fromPort() == -1 && inBoundIpPermission.toPort() == -1) {
						System.out.print(" ");
					} else {
						System.out.print(" " + inBoundIpPermission.fromPort());
						System.out.print("-" + inBoundIpPermission.toPort());
					}
				}
				
				List<IpRange> ipRanges = inBoundIpPermission.ipRanges();
				for(IpRange ipRange : ipRanges) {
					System.out.print(" " + ipRange.cidrIp());
				}
				List<Ipv6Range> ipv6Ranges = inBoundIpPermission.ipv6Ranges();
				for(Ipv6Range ipv6Range : ipv6Ranges) {
					System.out.print(" " + ipv6Range.cidrIpv6());
				}
				List<PrefixListId> prefixListIds = inBoundIpPermission.prefixListIds();
				for(PrefixListId prefixListId : prefixListIds) {
					System.out.print(" " + prefixListId.prefixListId());
				}
				List<UserIdGroupPair> userIdGroupPairs = inBoundIpPermission.userIdGroupPairs();
				for(UserIdGroupPair userIdGroupPair : userIdGroupPairs) {
					System.out.print(" " + userIdGroupPair.groupId());
					System.out.print("|" + userIdGroupPair.groupName());
					System.out.print(" " + userIdGroupPair.userId());
					System.out.print(" " + userIdGroupPair.vpcId());
					System.out.print(" " + userIdGroupPair.vpcPeeringConnectionId());
				}
				System.out.println("");
				
			}
			System.out.println("OutBound SG Rules");
			List<IpPermission> outBoundIpPermissions = securityGroup.ipPermissionsEgress();
			for (IpPermission outBoundIpPermission : outBoundIpPermissions) {
				System.out.print(("-1".equals(outBoundIpPermission.ipProtocol()) ? "All Traffic" : outBoundIpPermission.ipProtocol()));
				
				if(!"-1".equals(outBoundIpPermission.ipProtocol())) {
					if((outBoundIpPermission.fromPort() == null && outBoundIpPermission.toPort() == null)) {
						System.out.print(" All");
					} else if(outBoundIpPermission.fromPort() == -1 && outBoundIpPermission.toPort() == -1) {
						System.out.print(" ");
					} else {
						System.out.print(" " + outBoundIpPermission.fromPort());
						System.out.print("-" + outBoundIpPermission.toPort());
					}
				}
				
				List<IpRange> ipRanges = outBoundIpPermission.ipRanges();
				for(IpRange ipRange : ipRanges) {
					System.out.print(" " + ipRange.cidrIp());
				}
				List<Ipv6Range> ipv6Ranges = outBoundIpPermission.ipv6Ranges();
				for(Ipv6Range ipv6Range : ipv6Ranges) {
					System.out.print(" " + ipv6Range.cidrIpv6());
				}
				List<PrefixListId> prefixListIds = outBoundIpPermission.prefixListIds();
				for(PrefixListId prefixListId : prefixListIds) {
					System.out.print(" " + prefixListId.prefixListId());
				}
				List<UserIdGroupPair> userIdGroupPairs = outBoundIpPermission.userIdGroupPairs();
				for(UserIdGroupPair userIdGroupPair : userIdGroupPairs) {
					System.out.print(" " + userIdGroupPair.groupId());
					System.out.print("|" + userIdGroupPair.groupName());
					System.out.print(" " + userIdGroupPair.userId());
					System.out.print(" " + userIdGroupPair.vpcId());
					System.out.print(" " + userIdGroupPair.vpcPeeringConnectionId());
				}
				System.out.println("");
			}
		}
		
		
		
	}

}

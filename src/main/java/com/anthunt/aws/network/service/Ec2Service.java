package com.anthunt.aws.network.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.checker.Ec2InstanceNetwork;
import com.anthunt.aws.network.service.model.CheckResults;
import com.anthunt.aws.network.service.model.CheckType;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.service.model.diagram.DiagramData;
import com.anthunt.aws.network.service.model.diagram.DiagramNode;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.service.model.diagram.NodeType;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkAclsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.PrefixList;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;
import software.amazon.awssdk.services.ec2.model.VpnState;

@Service
public class Ec2Service extends AbstractNetworkService {

	public Ec2Client getEc2Client(SessionProfile sessionProfile) {
		return this.getEc2Client(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	public Ec2Client getEc2Client(String profileName, String regionId) {
		return Ec2Client.builder()
				   .credentialsProvider(ProfileCredentialsProvider.create(profileName))
				   .region(Region.of(regionId))
				   .build();
	}
	
	public ServiceMap<Vpc> getVpcs(SessionProfile sessionProfile) {
		ServiceMap<Vpc> vpcMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		DescribeVpcsResponse describeVpcsResponse = ec2Client.describeVpcs();
		for(Vpc vpc : describeVpcsResponse.vpcs()) {
			vpcMap.put(vpc.vpcId(), vpc);
		}
		return vpcMap;
	}
	
	public ServiceMap<Instance> getInstances(SessionProfile sessionProfile) {
		return this.getInstanceMap(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	public ServiceMap<Instance> getInstanceMap(String profileName, String regionId) {
		
		ServiceMap<Instance> instanceMap = new ServiceMap<Instance>();
		
		Ec2Client ec2Client = this.getEc2Client(profileName, regionId);
		
		DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances();
		List<Reservation> reservations = describeInstancesResponse.reservations();
		int active = 0;
		for (Reservation reservation : reservations) {
			for(Instance instance : reservation.instances()) {
				if(instance.state().code() == 16) active++;
				instanceMap.put(instance.instanceId(), instance);
			}
		}
		instanceMap.setActive(active);
		return instanceMap;
	}

	public ServiceMap<SecurityGroup> getSecurityGroups(SessionProfile sessionProfile) {
		ServiceMap<SecurityGroup> securityGroupMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		DescribeSecurityGroupsResponse describeSecurityGroupsResponse = ec2Client.describeSecurityGroups();
		for (SecurityGroup securityGroup : describeSecurityGroupsResponse.securityGroups()) {
			securityGroupMap.put(securityGroup.groupId(), securityGroup);
		}
		return securityGroupMap;
	}
	
	public ServiceMap<Subnet> getSubnets(SessionProfile sessionProfile) {
		ServiceMap<Subnet> subnetMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		DescribeSubnetsResponse describeSubnetsResponse = ec2Client.describeSubnets();
		for(Subnet subnet : describeSubnetsResponse.subnets()) {
			subnetMap.put(subnet.subnetId(), subnet);
		}
		return subnetMap;
	}
	
	public ServiceMap<List<RouteTable>> getRouteTables(SessionProfile sessionProfile, Collection<Subnet> subnets) {
		ServiceMap<List<RouteTable>> routeTablesMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		for(Subnet subnet : subnets) {
			DescribeRouteTablesResponse describeRouteTablesResponse = ec2Client.describeRouteTables(
					DescribeRouteTablesRequest.builder()
						.filters(
								Filter.builder()
								  .name("association.subnet-id")
								  .values(subnet.subnetId())
								  .build()
						)
						.build()
			);
			
			List<RouteTable> routeTables = describeRouteTablesResponse.routeTables();
			if(routeTables.size() < 1) {
				describeRouteTablesResponse = ec2Client.describeRouteTables(
						DescribeRouteTablesRequest.builder()
							.filters(
									  Filter.builder()
						                    .name("association.main")
						                    .values("true")
						                    .build()
									, Filter.builder()
											.name("vpc-id")
											.values(subnet.vpcId())
											.build()
							)
							.build()
				);
				routeTables = describeRouteTablesResponse.routeTables();
			}
			
			routeTablesMap.put(subnet.subnetId(), routeTables);
		}
		return routeTablesMap;
	}
	
	public ServiceMap<PrefixList> getPrefixLists(SessionProfile sessionProfile) {
		ServiceMap<PrefixList> prefixListMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		for(PrefixList prefixList : ec2Client.describePrefixLists().prefixLists()) {
			prefixListMap.put(prefixList.prefixListId(), prefixList);
		}
		return prefixListMap;
	}
	
	public ServiceMap<List<NetworkAcl>> getNetworkAcls(SessionProfile sessionProfile, Collection<Subnet> subnets) {
		ServiceMap<List<NetworkAcl>> networkAclMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		for(Subnet subnet : subnets) {
			networkAclMap.put(subnet.subnetId(), ec2Client.describeNetworkAcls(
					DescribeNetworkAclsRequest.builder()
						.filters(
								Filter.builder()
								  .name("association.subnet-id")
								  .values(subnet.subnetId())
								  .build()
						)
						.build()
			).networkAcls()); 
		}
		return networkAclMap;
	}
	
	public ServiceMap<VpnGateway> getVpnGateways(SessionProfile sessionProfile) {
		ServiceMap<VpnGateway> vpnGatewayMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		for(VpnGateway vpnGateway : ec2Client.describeVpnGateways().vpnGateways()) {
			vpnGatewayMap.put(vpnGateway.vpnGatewayId(), vpnGateway);
		}
		return vpnGatewayMap;
	}
	
	public ServiceMap<List<VpnConnection>> getVpnConnections(SessionProfile sessionProfile) {
		ServiceMap<List<VpnConnection>> vpnConnectionMap = new ServiceMap<>();
		Ec2Client ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		for(VpnConnection vpnConnection : ec2Client.describeVpnConnections().vpnConnections()) {
			if(vpnConnectionMap.containsKey(vpnConnection.vpnGatewayId())) {
				vpnConnectionMap.get(vpnConnection.vpnGatewayId()).add(vpnConnection);
			} else {
				if(vpnConnection.state() == VpnState.AVAILABLE) active++; 
				List<VpnConnection> vpnConnections = new ArrayList<>();
				vpnConnections.add(vpnConnection);
				vpnConnectionMap.put(vpnConnection.vpnGatewayId(), vpnConnections);
			}
		}
		vpnConnectionMap.setActive(active);
		return vpnConnectionMap;
	}
	
	public Collection<Instance> getInstances(ServiceRepository serviceRepository) {
		return serviceRepository.getEc2InstanceMap().values();
	}
	
	public ServiceStatistic getInstanceStatistic(ServiceRepository serviceRepository) {
		
		int active = 0;
		int total = 0;
		Collection<Instance> instances = this.getInstances(serviceRepository);
		for (Instance instance : instances) {
			if(instance.state().code() == 16) {
				active++;
			}
			total++;
		}

		ServiceStatistic serviceStatistic = new ServiceStatistic(ServiceType.EC2);
		serviceStatistic.setServiceActive(active);
		serviceStatistic.setServiceTotal(total);
		
		return serviceStatistic;
	}
	
	public DiagramResult getNetwork(ServiceRepository serviceRepository, String instanceId, String targetIp) {
		Ec2InstanceNetwork ec2InstanceNetwork = new Ec2InstanceNetwork(instanceId, serviceRepository);
		
		CheckResults<Instance> checkResults = ec2InstanceNetwork.checkCommunication(targetIp);
		
		DiagramResult diagramResult = new DiagramResult(targetIp == null);
		
		String targetId = checkResults.getCidr();
		
		if(targetId != null) {
			diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(targetId, targetId)).addClass(NodeType.SERVER));
		}
		
		String routeTableId = this.setRouteTable(targetId, checkResults.get(CheckType.ROUTE_TABLE), diagramResult);
		
		String networkAclId = this.setNetworkAcl(routeTableId, checkResults.get(CheckType.NETWORK_ACL), diagramResult);
		
		this.setSecurityGroup(networkAclId, checkResults.getResource().instanceId(), checkResults.get(CheckType.SECURITY_GROUP), diagramResult);
		
		diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(checkResults.getResource().instanceId(), checkResults.getResource().privateIpAddress(), checkResults.getResource())).addClass(NodeType.EC2_INSTANCE));
		
		return diagramResult;
	}
	
}

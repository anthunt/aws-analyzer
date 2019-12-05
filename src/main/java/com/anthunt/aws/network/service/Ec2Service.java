package com.anthunt.aws.network.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.checker.Ec2InstanceNetwork;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.service.model.checker.CheckResults;
import com.anthunt.aws.network.service.model.checker.CheckType;
import com.anthunt.aws.network.service.model.diagram.DiagramData;
import com.anthunt.aws.network.service.model.diagram.DiagramEdge;
import com.anthunt.aws.network.service.model.diagram.DiagramNode;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.service.model.diagram.NodeType;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.DescribeCustomerGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.DescribeEgressOnlyInternetGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInternetGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkAclsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeNetworkInterfacesResponse;
import software.amazon.awssdk.services.ec2.model.DescribePrefixListsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcPeeringConnectionsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpnConnectionsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpnGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.GroupIdentifier;
import software.amazon.awssdk.services.ec2.model.IamInstanceProfile;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceBlockDeviceMapping;
import software.amazon.awssdk.services.ec2.model.InstanceNetworkInterface;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ec2.model.NetworkInterfaceStatus;
import software.amazon.awssdk.services.ec2.model.PrefixList;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.TransitGatewayState;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.VolumeState;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnectionStateReasonCode;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;

@Service
public class Ec2Service extends AbstractNetworkService {

	public Ec2AsyncClient getEc2Client(SessionProfile sessionProfile) {
		return Ec2AsyncClient.builder()
				   .credentialsProvider(sessionProfile.getProfileCredentialsProvider())
				   .region(sessionProfile.getRegion())
				   .build();
	}
	
	public ServiceMap<Vpc> getVpcs(SessionProfile sessionProfile) {
		ServiceMap<Vpc> vpcMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		DescribeVpcsResponse describeVpcsResponse = ec2Client.describeVpcs().join();		
		for(Vpc vpc : describeVpcsResponse.vpcs()) {
			vpcMap.put(vpc.vpcId(), vpc);
		}
		return vpcMap;
	}
	
	public ServiceMap<Instance> getInstances(SessionProfile sessionProfile) {
		return this.getInstanceMap(sessionProfile);
	}
	
	public ServiceMap<Instance> getInstanceMap(SessionProfile sessionProfile) {
		
		ServiceMap<Instance> instanceMap = new ServiceMap<Instance>();
		
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		
		DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances().join();
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

	public ServiceMap<Volume> getVolumes(SessionProfile sessionProfile) {
		ServiceMap<Volume> volumeMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		for(Volume volume : ec2Client.describeVolumes().join().volumes()) {
			if(volume.state() == VolumeState.IN_USE) {
				active++;
			}
			volumeMap.put(volume.volumeId(), volume);
		}
		volumeMap.setActive(active);
		return volumeMap;
	}
	
	public ServiceMap<NetworkInterface> getNetworkInterfaces(SessionProfile sessionProfile) {
		ServiceMap<NetworkInterface> networkInterfaceMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeNetworkInterfacesResponse describeNetworkInterfacesResponse = ec2Client.describeNetworkInterfaces().join();
		for(NetworkInterface networkInterface : describeNetworkInterfacesResponse.networkInterfaces()) {
			if(networkInterface.status() == NetworkInterfaceStatus.IN_USE) {
				active++;
			}
			networkInterfaceMap.put(networkInterface.networkInterfaceId(), networkInterface);
		}
		networkInterfaceMap.setActive(active);
		return networkInterfaceMap;
	}
	
	public ServiceMap<SecurityGroup> getSecurityGroups(SessionProfile sessionProfile) {
		ServiceMap<SecurityGroup> securityGroupMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		DescribeSecurityGroupsResponse describeSecurityGroupsResponse = ec2Client.describeSecurityGroups().join();
		for (SecurityGroup securityGroup : describeSecurityGroupsResponse.securityGroups()) {
			securityGroupMap.put(securityGroup.groupId(), securityGroup);
		}
		return securityGroupMap;
	}
	
	public ServiceMap<Subnet> getSubnets(SessionProfile sessionProfile) {
		ServiceMap<Subnet> subnetMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		DescribeSubnetsResponse describeSubnetsResponse = ec2Client.describeSubnets().join();
		for(Subnet subnet : describeSubnetsResponse.subnets()) {
			subnetMap.put(subnet.subnetId(), subnet);
		}
		return subnetMap;
	}
	
	public ServiceMap<List<RouteTable>> getRouteTables(SessionProfile sessionProfile, Collection<Subnet> subnets) {
		ServiceMap<List<RouteTable>> routeTablesMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
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
			).join();
			
			Utils.sleep(100);
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
				).join();
				routeTables = describeRouteTablesResponse.routeTables();
			}
			
			routeTablesMap.put(subnet.subnetId(), routeTables);
			Utils.sleep(100);
		}
		return routeTablesMap;
	}
	
	public ServiceMap<VpcPeeringConnection> getVpcPeerings(SessionProfile sessionProfile) {
		ServiceMap<VpcPeeringConnection> vpcPeeringConnectionsMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeVpcPeeringConnectionsResponse describeVpcPeeringConnectionsResponse = ec2Client.describeVpcPeeringConnections().join();
		for(VpcPeeringConnection vpcPeeringConnection : describeVpcPeeringConnectionsResponse.vpcPeeringConnections()) {
			if(vpcPeeringConnection.status().code() == VpcPeeringConnectionStateReasonCode.ACTIVE) {
				active++;
			}
			vpcPeeringConnectionsMap.put(vpcPeeringConnection.vpcPeeringConnectionId(), vpcPeeringConnection);
		}
		vpcPeeringConnectionsMap.setActive(active);
		return vpcPeeringConnectionsMap;
	}
	
	public ServiceMap<PrefixList> getPrefixLists(SessionProfile sessionProfile) {
		ServiceMap<PrefixList> prefixListMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		DescribePrefixListsResponse describePrefixListsResponse = ec2Client.describePrefixLists().join();
		for(PrefixList prefixList : describePrefixListsResponse.prefixLists()) {
			prefixListMap.put(prefixList.prefixListId(), prefixList);
		}
		return prefixListMap;
	}
	
	public ServiceMap<List<NetworkAcl>> getNetworkAcls(SessionProfile sessionProfile, Collection<Subnet> subnets) {
		ServiceMap<List<NetworkAcl>> networkAclMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
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
			).join().networkAcls()); 
			Utils.sleep(100);
		}
		return networkAclMap;
	}
	
	public ServiceMap<CustomerGateway> getCustomerGateways(SessionProfile sessionProfile) {
		ServiceMap<CustomerGateway> customerGatewayMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeCustomerGatewaysResponse describeCustomerGatewaysResponse = ec2Client.describeCustomerGateways().join();
		for(CustomerGateway customerGateway : describeCustomerGatewaysResponse.customerGateways()) {
			if("available".equals(customerGateway.state())) {
				active++;
			}
			customerGatewayMap.put(customerGateway.customerGatewayId(), customerGateway);
		}
		customerGatewayMap.setActive(active);
		return customerGatewayMap;
	}
	
	public ServiceMap<VpnGateway> getVpnGateways(SessionProfile sessionProfile) {
		ServiceMap<VpnGateway> vpnGatewayMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		DescribeVpnGatewaysResponse describeVpnGatewaysResponse = ec2Client.describeVpnGateways().join();
		for(VpnGateway vpnGateway : describeVpnGatewaysResponse.vpnGateways()) {
			vpnGatewayMap.put(vpnGateway.vpnGatewayId(), vpnGateway);
		}
		return vpnGatewayMap;
	}
	
	public ServiceMap<List<VpnConnection>> getVpnConnections(SessionProfile sessionProfile) {
		ServiceMap<List<VpnConnection>> vpnConnectionMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeVpnConnectionsResponse describeVpnConnectionsResponse = ec2Client.describeVpnConnections().join();
		for(VpnConnection vpnConnection : describeVpnConnectionsResponse.vpnConnections()) {
			if("available".equals(vpnConnection.stateAsString())) active++;
			if(vpnConnectionMap.containsKey(vpnConnection.vpnGatewayId())) {
				vpnConnectionMap.get(vpnConnection.vpnGatewayId()).add(vpnConnection);
			} else { 
				List<VpnConnection> vpnConnections = new ArrayList<>();
				vpnConnections.add(vpnConnection);
				vpnConnectionMap.put(vpnConnection.vpnGatewayId(), vpnConnections);
			}
		}
		vpnConnectionMap.setActive(active);
		return vpnConnectionMap;
	}

	public ServiceMap<InternetGateway> getInternetGateways(SessionProfile sessionProfile) {
		ServiceMap<InternetGateway> internetGatewayMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeInternetGatewaysResponse describeInternetGatewaysResponse = ec2Client.describeInternetGateways().join();
		for(InternetGateway internetGateway : describeInternetGatewaysResponse.internetGateways()) {
			if(internetGateway.attachments().size() > 0) {
				active++;
			}
			internetGatewayMap.put(internetGateway.internetGatewayId(), internetGateway);
		}
		internetGatewayMap.setActive(active);
		return internetGatewayMap;
	}

	public ServiceMap<EgressOnlyInternetGateway> getEgressInternetGateways(SessionProfile sessionProfile) {
		ServiceMap<EgressOnlyInternetGateway> egressInternetGatewayMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeEgressOnlyInternetGatewaysResponse describeEgressOnlyInternetGatewaysResponse = ec2Client.describeEgressOnlyInternetGateways().join();
		for(EgressOnlyInternetGateway egressOnlyInternetGateway : describeEgressOnlyInternetGatewaysResponse.egressOnlyInternetGateways()) {
			if(egressOnlyInternetGateway.attachments().size() > 0) {
				active++;
			}
			egressInternetGatewayMap.put(egressOnlyInternetGateway.egressOnlyInternetGatewayId(), egressOnlyInternetGateway);
		}
		egressInternetGatewayMap.setActive(active);
		return egressInternetGatewayMap;
	}

	public ServiceMap<VpcEndpoint> getVpcEndpoints(SessionProfile sessionProfile) {
		ServiceMap<VpcEndpoint> vpcEndpointMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeVpcEndpointsResponse describeVpcEndpointsResponse = ec2Client.describeVpcEndpoints().join();
		for(VpcEndpoint vpcEndpoint : describeVpcEndpointsResponse.vpcEndpoints()) {
			if("available".equals(vpcEndpoint.stateAsString())) {
				active++;
			}
			vpcEndpointMap.put(vpcEndpoint.vpcEndpointId(), vpcEndpoint);
		}
		vpcEndpointMap.setActive(active);
		return vpcEndpointMap;
	}

	public ServiceMap<TransitGateway> getTransitGateways(SessionProfile sessionProfile) {
		ServiceMap<TransitGateway> transitGatewayMap = new ServiceMap<>();
		Ec2AsyncClient ec2Client = this.getEc2Client(sessionProfile);
		int active = 0;
		DescribeTransitGatewaysResponse describeTransitGatewaysResponse = ec2Client.describeTransitGateways().join();
		for(TransitGateway transitGateway : describeTransitGatewaysResponse.transitGateways()) {
			if(transitGateway.state() == TransitGatewayState.AVAILABLE) {
				active++;
			}
			transitGatewayMap.put(transitGateway.transitGatewayId(), transitGateway);
		}
		transitGatewayMap.setActive(active);
		return transitGatewayMap;
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
		Vpc vpc = serviceRepository.getVpcMap().get(checkResults.getResource().vpcId());
		
		DiagramResult diagramResult = new DiagramResult(vpc.vpcId(), targetIp == null);
		
//		diagramResult.addNode(
//				new DiagramData<DiagramNode>(
//						new DiagramNode(AWS, "")
//				).addClass(NodeType.AWS)
//		);
//		
//		diagramResult.addNode(
//				new DiagramData<DiagramNode>(
//						new DiagramNode(vpc.vpcId(), vpc.vpcId(), AWS)
//				).addClass(NodeType.VPC)
//		);
		
		String targetId = checkResults.getCidr();
		
		if(targetId != null) {
			diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(targetId, targetId)).addClass(NodeType.SERVER));
		}
		
		List<String> routeTableIds = this.setRouteTable(serviceRepository, targetId, checkResults.get(CheckType.ROUTE_TABLE), diagramResult);
		
		for(String routeTableId : routeTableIds) {
			String networkAclId = this.setNetworkAcl(routeTableId, checkResults.get(CheckType.NETWORK_ACL), diagramResult);
			this.setSecurityGroup(networkAclId, checkResults.getResource().instanceId(), checkResults.get(CheckType.SECURITY_GROUP), diagramResult);
		}
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(checkResults.getResource().instanceId(), checkResults.getResource())
				).addClass(NodeType.EC2_INSTANCE)
		);
		
		return diagramResult;
	}

	public DiagramResult getInstanceNetwork(ServiceRepository serviceRepository, DiagramResult diagramResult, String instanceId) {
		
		Instance instance = serviceRepository.getEc2InstanceMap().get(instanceId);
		Vpc vpc = serviceRepository.getVpcMap().get(instance.vpcId());
		Subnet subnet = serviceRepository.getSubnetMap().get(instance.subnetId());
		
		diagramResult.addNode(
			new DiagramData<DiagramNode>(
					new DiagramNode(AWS, "Amazon Web Service")
			).addClass(NodeType.AWS)
		);

		diagramResult.addNode(
			new DiagramData<DiagramNode>(
					new DiagramNode(vpc.vpcId(), vpc, AWS)
			).addClass(NodeType.VPC)
		);
		
		diagramResult.addNode(
			new DiagramData<DiagramNode>(
					new DiagramNode(subnet.availabilityZone(), subnet.availabilityZone(), vpc.vpcId())
			).addClass(NodeType.AZ)
		);
		
		diagramResult.addNode(
			new DiagramData<DiagramNode>(
					new DiagramNode(subnet.subnetId(), subnet, subnet.availabilityZone())
			).addClass(NodeType.PRIVATE_SUBNET)
		);
		
		diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(instance.instanceId(), instance, subnet.subnetId())
				).addClass(NodeType.EC2_INSTANCE)
		);
		
		if(instance.iamInstanceProfile() != null) {
			IamInstanceProfile iamInstanceProfile = instance.iamInstanceProfile();
			diagramResult.addNode(
					new DiagramData<DiagramNode>(
							new DiagramNode(iamInstanceProfile.id(), iamInstanceProfile, AWS)
					).addClass(NodeType.IAM_ROLE)
			);
			
			diagramResult.addEdge(new DiagramData<DiagramEdge>(
					DiagramEdge.make(iamInstanceProfile.id(), instance.instanceId())
							   .setLabel("instance profile")
							   .setBoth(true)
			));
		}
				
		for(InstanceBlockDeviceMapping instanceBlockDeviceMapping : instance.blockDeviceMappings()) {
			Volume volume = serviceRepository.getVolumeMap().get(instanceBlockDeviceMapping.ebs().volumeId());
			
			diagramResult.addNode(
					new DiagramData<DiagramNode>(
							new DiagramNode(volume.volumeId(), volume, volume.availabilityZone())
					).addClass(NodeType.EBS)
			);

			diagramResult.addEdge(new DiagramData<DiagramEdge>(
					DiagramEdge.make(instance.instanceId(), volume.volumeId())
							   .setLabel(instanceBlockDeviceMapping.deviceName())
							   .setBoth(true)
			));
		}
		
		for(InstanceNetworkInterface instanceNetworkInterface : instance.networkInterfaces()) {
			NetworkInterface networkInterface = serviceRepository.getNetworkInterfaceMap().get(instanceNetworkInterface.networkInterfaceId());
			
			subnet = serviceRepository.getSubnetMap().get(networkInterface.subnetId());
			
			diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(subnet.availabilityZone(), subnet.availabilityZone(), subnet.vpcId())
				).addClass(NodeType.AZ)
			);
			
			diagramResult.addNode(
				new DiagramData<DiagramNode>(
						new DiagramNode(subnet.subnetId(), subnet, subnet.availabilityZone())
				).addClass(NodeType.PRIVATE_SUBNET)
			);
				
			diagramResult.addNode(
					new DiagramData<DiagramNode>(
							new DiagramNode(instanceNetworkInterface.networkInterfaceId(), networkInterface, subnet.subnetId())
					).addClass(NodeType.NETWORK_INTERFACE)
			);
			
			diagramResult.addEdge(new DiagramData<DiagramEdge>(
					DiagramEdge.make(instanceNetworkInterface.networkInterfaceId(), instance.instanceId())
							   .setLabel(instanceNetworkInterface.description())
							   .setBoth(true)
			));
			
			for(GroupIdentifier groupIdentifier : instanceNetworkInterface.groups()) {
				SecurityGroup securityGroup  = serviceRepository.getSecurityGroupMap().get(groupIdentifier.groupId());
				
				diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(securityGroup.groupId(), securityGroup, securityGroup.vpcId())
						).addClass(NodeType.SECURITY_GROUP)
				);
				
				diagramResult.addEdge(new DiagramData<DiagramEdge>(
						DiagramEdge.make(securityGroup.groupId(), instanceNetworkInterface.networkInterfaceId())
							       .setLabel(securityGroup.description())
							       .setBoth(true)
				));				
			}
		}
		
		return diagramResult;
	}
	
}

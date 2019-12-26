package com.anthunt.aws.network.service.aws;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.service.model.checker.CheckResult;
import com.anthunt.aws.network.service.model.checker.CheckRule;
import com.anthunt.aws.network.service.model.checker.DirectionType;
import com.anthunt.aws.network.service.model.checker.NetworkAclCheckRule;
import com.anthunt.aws.network.service.model.checker.RouteCheckRule;
import com.anthunt.aws.network.service.model.checker.SecurityGroupCheckRule;
import com.anthunt.aws.network.service.model.diagram.DiagramData;
import com.anthunt.aws.network.service.model.diagram.DiagramEdge;
import com.anthunt.aws.network.service.model.diagram.DiagramNode;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.service.model.diagram.NodeType;
import com.anthunt.aws.network.utils.Logging;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.directconnect.model.VirtualInterfaceState;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ec2.model.RouteState;
import software.amazon.awssdk.services.ec2.model.RuleAction;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnectionVpcInfo;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;

public abstract class AbstractNetworkService {

	private static final Logger log = Logging.getLogger(AbstractNetworkService.class);
	
	public static final String AWS = "aws";
	
	public DiagramResult getNetworkDiagram(ServiceRepository serviceRepository, String instanceId, String targetIp) {
		return this.getNetwork(serviceRepository, instanceId, targetIp);
	}
	
	protected abstract DiagramResult getNetwork(ServiceRepository serviceRepository, String instanceId, String targetIp);
	
	protected List<String> setRouteTable(ServiceRepository serviceRepository, String serverId, CheckResult routeCheckResult, DiagramResult diagramResult) {

		log.trace("set route table - {serverId: {}}", serverId);
		
		List<String> routeTableIds = new ArrayList<>();
		for(CheckRule checkRule : routeCheckResult.getAllRules()) {
			if(checkRule instanceof RouteCheckRule) {
				RouteCheckRule routeCheckRule = (RouteCheckRule) checkRule;
				String routeTableId = routeCheckRule.getId(); 
				routeTableIds.add(routeTableId);
				log.trace("route table id - {}", routeTableId);
				
				List<String> gatewayIds = new ArrayList<>();
				List<VpnConnection> vpnConnections = routeCheckRule.getVpnConnections();
				
				for(VpnConnection vpnConnection : vpnConnections) {
					CustomerGateway customerGateway = serviceRepository.getCustomerGatewayMap().get(vpnConnection.customerGatewayId(), CustomerGateway.class).get().getData();
					DiagramNode customerGatewayNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(vpnConnection.customerGatewayId(), customerGateway)
							).addClass(NodeType.CUSTOMER_GATEWAY)
					);
					log.trace("added node - {routeTableId: {}, {}}", routeTableId, customerGatewayNode.toString());
					
					DiagramNode vpnConnectionNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(vpnConnection.vpnConnectionId(), vpnConnection)
							).addClass(NodeType.VPN_CONNECTION)
					);
					log.trace("added node - {routeTableId: {}, {}}", routeTableId, vpnConnectionNode.toString());
					
					gatewayIds.add(vpnConnection.vpnConnectionId());
					
					if(serverId != null) {
//						diagramResult.addEdge(new DiagramData<DiagramEdge>(
//								DiagramEdge.make(vpnConnection.customerGatewayId(), AWS)
//								.setAllMode(true)
//						));
						DiagramEdge diagramCustomerGatewayEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(serverId, vpnConnection.customerGatewayId())
										   .setLabel(vpnConnection.category())
										   .setBoth(true)
						));
						log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramCustomerGatewayEdge.toString());
					}
					
					DiagramEdge diagramVpnConnectionEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
							DiagramEdge.make(vpnConnection.customerGatewayId(), vpnConnection.vpnConnectionId())
									   .setLabel(vpnConnection.typeAsString())
									   .setBoth(true)
					));
					log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramVpnConnectionEdge.toString());
					
					DiagramEdge diagramVpnGatewayEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
							DiagramEdge.make(vpnConnection.vpnConnectionId(), vpnConnection.vpnGatewayId())
									   .setLabel(vpnConnection.options().staticRoutesOnly() ? "static" : "dynamic")
									   .setBoth(true)
					));
					log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramVpnGatewayEdge.toString());
				}
				
				List<VirtualInterface> virtualInterfaces = routeCheckRule.getVirtualInterfaces();
				for(VirtualInterface virtualInterface : virtualInterfaces) {
					DiagramNode dxLoacationNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(virtualInterface.location(), virtualInterface.location())
							).addClass(NodeType.CORPORATE_DATA_CENTER)
					);
					log.trace("added node - {routeTableId: {}, {}}", routeTableId, dxLoacationNode.toString());
					DiagramNode vifConnectionNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(virtualInterface.connectionId(), virtualInterface)
							).addClass(NodeType.DIRECT_CONNECT)
					);
					log.trace("added node - {routeTableId: {}, {}}", routeTableId, vifConnectionNode.toString());
					
					gatewayIds.add(virtualInterface.connectionId());
					
					if(serverId != null) {
//						diagramResult.addEdge(new DiagramData<DiagramEdge>(
//								DiagramEdge.make(virtualInterface.location(), AWS)
//										   .setAllMode(true)
//						));
						DiagramEdge diagramLocationEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(serverId, virtualInterface.location())
										   .setLabel(virtualInterface.customerAddress() + " (Vlan : " + virtualInterface.vlan() + ", Asn :" + virtualInterface.asn() + ")")
										   .setBoth(true)
						));
						log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramLocationEdge.toString());
					}
					
					DiagramEdge diagramDirectConnectEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
							DiagramEdge.make(virtualInterface.location(), virtualInterface.connectionId())
									   .setLabel(virtualInterface.amazonAddress() + " (" + virtualInterface.awsDeviceV2() + ")")
									   .setBoth(true)
					));
					log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramDirectConnectEdge.toString());
					
					if(virtualInterface.directConnectGatewayId() != null && !"".equals(virtualInterface.directConnectGatewayId())) {
						DiagramEdge diagramEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(virtualInterface.connectionId(), virtualInterface.directConnectGatewayId())
										   .setLabel(virtualInterface.virtualInterfaceType() + ":" + virtualInterface.virtualInterfaceName() + " (Asn :" + virtualInterface.amazonSideAsn() + ")")
										   .setBoth(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE)
						));
						log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
					}
					
					if(virtualInterface.virtualGatewayId() != null && !"".equals(virtualInterface.virtualGatewayId())) {
						DiagramEdge diagramEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
								DiagramEdge.make(virtualInterface.connectionId(), virtualInterface.virtualGatewayId())
										   .setLabel(virtualInterface.virtualInterfaceType() + ":" + virtualInterface.virtualInterfaceName() + " (Asn :" + virtualInterface.amazonSideAsn() + ")")
										   .setBoth(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE)
						));
						log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
					}
					
				}
				
				Object gateway = null;
				String delegateGatewayId = routeCheckRule.getGatewayId();
				
				switch(routeCheckRule.getGatewayType()) {
				case VIRTUAL_GATEWAY:
					gateway = serviceRepository.getVpnGatewayMap().get(routeCheckRule.getGatewayId(), VpnGateway.class).get().getData();
					break;
				case LOCAL:
					gateway = routeCheckRule.getGatewayId();
					break;
				case VPC_ENDPOINT:
					gateway = serviceRepository.getVpcEndpointMap().get(routeCheckRule.getGatewayId(), VpcEndpoint.class).get().getData();
					break;
				case TRANSIT_GATEWAY:
					gateway = serviceRepository.getTransitGatewayMap().get(routeCheckRule.getGatewayId(), TransitGateway.class).get().getData();
					break;
				case PEERING:
					VpcPeeringConnection vpcPeeringConnection = serviceRepository.getVpcPeeringMap().get(routeCheckRule.getGatewayId(), VpcPeeringConnection.class).get().getData();
					VpcPeeringConnectionVpcInfo accepterVpcInfo = vpcPeeringConnection.accepterVpcInfo();
					VpcPeeringConnectionVpcInfo requesterVpcInfo = vpcPeeringConnection.requesterVpcInfo();
					
					if(routeCheckRule.getRouteTable().vpcId().equals(accepterVpcInfo.vpcId())) {
						delegateGatewayId = requesterVpcInfo.vpcId();
						diagramResult.addNode(
								new DiagramData<DiagramNode>(
										new DiagramNode(requesterVpcInfo.vpcId(), requesterVpcInfo.vpcId())
								).addClass(NodeType.VPC)
						);
						diagramResult.addEdge(
								new DiagramData<DiagramEdge>(
									DiagramEdge.make(requesterVpcInfo.vpcId(), vpcPeeringConnection.vpcPeeringConnectionId())
										.setLabel(requesterVpcInfo.cidrBlock())
										.setBoth(true)
								)
									
						);
					} else {
						delegateGatewayId = accepterVpcInfo.vpcId();
						diagramResult.addNode(
								new DiagramData<DiagramNode>(
										new DiagramNode(accepterVpcInfo.vpcId(), accepterVpcInfo.vpcId())
								).addClass(NodeType.VPC)
						);
						diagramResult.addEdge(
								new DiagramData<DiagramEdge>(
									DiagramEdge.make(accepterVpcInfo.vpcId(), vpcPeeringConnection.vpcPeeringConnectionId())
										.setLabel(accepterVpcInfo.cidrBlock())
										.setBoth(true)
								)
									
						);
					}
					
					
					gateway = vpcPeeringConnection;
					
					break;
				case INTERNET_GATEWAY:
					gateway = serviceRepository.getInternetGatewayMap().get(routeCheckRule.getGatewayId(), InternetGateway.class).get().getData();
					break;
				case EGRESS_INTERNET_GATEWAY:
					gateway = serviceRepository.getEgressInternetGatewayMap().get(routeCheckRule.getGatewayId(), EgressOnlyInternetGateway.class).get().getData();
					break;
				case NETWORK_INTERFACE:
					gateway = serviceRepository.getNetworkInterfaceMap().get(routeCheckRule.getGatewayId(), NetworkInterface.class).get().getData();
					break;
				default:
					break;
				}
				
				DiagramNode gatewayNode = diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(routeCheckRule.getGatewayId(), gateway)
						).addClass(routeCheckRule.getGatewayType())
				);
				log.trace("added node - {routeTableId: {}, {}}", routeTableId, gatewayNode.toString());
				DiagramNode routeNode = diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(routeCheckRule.getId(), routeCheckRule.getRouteTable())
						).addClass(NodeType.ROUTE_TABLE)
				);
				log.trace("added node - {routeTableId: {}, {}}", routeTableId, routeNode.toString());

				for(String cidr : routeCheckRule.getCidrs()) {
					
					if(serverId != null) {
						if(gatewayIds.size() < 1) {
							DiagramEdge diagramGatewayEdge = DiagramEdge.make(serverId, delegateGatewayId).setLabel(cidr);
							if(routeCheckRule.getGatewayType() == NodeType.EGRESS_INTERNET_GATEWAY || routeCheckRule.getGatewayType() == NodeType.NAT_GATEWAY) {
								diagramGatewayEdge.setOut(routeCheckRule.getRouteState() == RouteState.ACTIVE);
							} else {
								diagramGatewayEdge.setBoth(routeCheckRule.getRouteState() == RouteState.ACTIVE);
							}
							diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramGatewayEdge));
							log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramGatewayEdge.toString());
						}
					}
					
					DiagramEdge diagramEdge = DiagramEdge.make(routeCheckRule.getGatewayId(), routeCheckRule.getId()).setLabel(cidr);
					if(routeCheckRule.getGatewayType() == NodeType.EGRESS_INTERNET_GATEWAY || routeCheckRule.getGatewayType() == NodeType.NAT_GATEWAY) {
						diagramEdge.setOut(routeCheckRule.getRouteState() == RouteState.ACTIVE);
					} else {
						diagramEdge.setBoth(routeCheckRule.getRouteState() == RouteState.ACTIVE);
					}
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
					log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
					
				}
				
			}
		}
		
		if(routeCheckResult.getAllRules().size() < 1) {
			String noRouteId = "noRoute";
			String noRouteName = "Unknown Route";
			String routeTableId = noRouteId;
			routeTableIds.add(routeTableId);
			DiagramNode noRouteNode = diagramResult.addNode(
					new DiagramData<DiagramNode>(
							new DiagramNode(noRouteId, noRouteName)
					).addClass(NodeType.ROUTE_TABLE)
			);
			log.trace("added node - {routeTableId: {}, {}}", routeTableId, noRouteNode.toString());
			if(serverId != null) {
				DiagramEdge diagramEdge = diagramResult.addEdge(new DiagramData<DiagramEdge>(
						DiagramEdge.make(serverId, noRouteId)
								   .setLabel("Have no route")
								   .setBoth(false)
				));
				log.trace("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
			}
		}

		return routeTableIds;
	}
	
	protected String setNetworkAcl(String routeTableId, CheckResult networkAclCheckResult, DiagramResult diagramResult) {
	
		log.trace("set network acl - {}", routeTableId);
		
		String networkAclId = "";
		for(CheckRule checkRule : networkAclCheckResult.getAllRules()) {
			if(checkRule instanceof NetworkAclCheckRule) {
				NetworkAclCheckRule networkAclCheckRule = (NetworkAclCheckRule) checkRule;
				
				if(routeTableId.equals(networkAclCheckRule.getRouteTableId())) {
					networkAclId = networkAclCheckRule.getId();
					
					DiagramNode diagramNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(networkAclCheckRule.getId(), networkAclCheckRule.getNetworkAcl())
							).addClass(NodeType.NETWORK_ACL)
					);
					log.trace("add node - {networkAclId:{}, {}}", networkAclId, diagramNode.toString());
					
					DiagramEdge diagramEdge = DiagramEdge.make(routeTableId, networkAclCheckRule.getId());
					
					String port = "";
					if("All".equals(networkAclCheckRule.getPrototol()) && networkAclCheckRule.getPortRange() == null) {
						port = "All Traffic";
					} else if(networkAclCheckRule.getPortRange() == null) {
						port = "All " + networkAclCheckRule.getPrototol();
					} else {
						port = networkAclCheckRule.getPrototol() + ":" + networkAclCheckRule.getPortRange().toString();
					}
					
					diagramEdge.setLabel(networkAclCheckRule.getCidr() + " " + port);
					if(networkAclCheckRule.getDirectionType() == DirectionType.INGRESS) {
						diagramEdge.setIn(networkAclCheckRule.getRuleAction() == RuleAction.ALLOW);
					} else {
						diagramEdge.setOut(networkAclCheckRule.getRuleAction() == RuleAction.ALLOW);
					}
					
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
					log.trace("add edge - {networkAclId:{}, {}}", networkAclId, diagramEdge.toString());
				}
				
			}
		}
		return "".equals(networkAclId) ? routeTableId : networkAclId;
	}

	protected void setSecurityGroup(String networkAclId, String instanceId, CheckResult securityGroupCheckResult, DiagramResult diagramResult) {
		
		for(CheckRule checkRule : securityGroupCheckResult.getAllRules()) {
			if(checkRule instanceof SecurityGroupCheckRule) {
				SecurityGroupCheckRule securityGroupCheckRule = (SecurityGroupCheckRule) checkRule;
				
				diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(securityGroupCheckRule.getId(), securityGroupCheckRule.getSecurityGroup())
						).addClass(NodeType.SECURITY_GROUP)
				);
				
				String port = "";
				if("-1".equals(securityGroupCheckRule.getPrototol())) {
					port = "All Traffic";
				} else if(securityGroupCheckRule.getFromPort() == -1 && securityGroupCheckRule.getToPort() == -1){
					port = securityGroupCheckRule.getPrototol();
				} else if(securityGroupCheckRule.getFromPort() == securityGroupCheckRule.getToPort()) {
					port = securityGroupCheckRule.getPrototol() + ":" + securityGroupCheckRule.getToPort();
				} else {
					port = securityGroupCheckRule.getPrototol() + ":" + securityGroupCheckRule.getFromPort() + "-" + securityGroupCheckRule.getToPort();
				}
				
				DiagramEdge diagramEdge = DiagramEdge.make(networkAclId, securityGroupCheckRule.getId());
				DiagramEdge serverEdge = DiagramEdge.make(securityGroupCheckRule.getId(), instanceId);
				serverEdge.setLabel(port);
				diagramEdge.setLabel(securityGroupCheckRule.getCidr() + "\n" + port);
				if(securityGroupCheckRule.getDirectionType() == DirectionType.INGRESS) {
					diagramEdge.setIn(true);
					serverEdge.setIn(true);
				} else {
					diagramEdge.setOut(true);
					serverEdge.setOut(true);
				}
				diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
				diagramResult.addEdge(new DiagramData<DiagramEdge>(serverEdge));
				
			}
		}
		
		if(securityGroupCheckResult.getAllRules().size() < 1) {
			diagramResult.addEdge(new DiagramData<DiagramEdge>(
					DiagramEdge.make(networkAclId, instanceId)
							   .setLabel("Not allow in SecurityGroup")
							   .setBoth(false)
			));
		}
	}

}

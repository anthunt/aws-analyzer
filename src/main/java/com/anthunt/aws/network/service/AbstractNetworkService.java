package com.anthunt.aws.network.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.directconnect.model.VirtualInterfaceState;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.RouteState;
import software.amazon.awssdk.services.ec2.model.RuleAction;
import software.amazon.awssdk.services.ec2.model.VpnConnection;

public abstract class AbstractNetworkService {

	private static final Logger log = LoggerFactory.getLogger(AbstractNetworkService.class);
	
	public static final String AWS = "aws";
	
	public DiagramResult getNetworkDiagram(ServiceRepository serviceRepository, String instanceId, String targetIp) {
		return this.getNetwork(serviceRepository, instanceId, targetIp);
	}
	
	protected abstract DiagramResult getNetwork(ServiceRepository serviceRepository, String instanceId, String targetIp);
	
	protected List<String> setRouteTable(ServiceRepository serviceRepository, String serverId, CheckResult routeCheckResult, DiagramResult diagramResult) {

		log.debug("set route table - {serverId: {}}", serverId);
		
		List<String> routeTableIds = new ArrayList<>();
		for(CheckRule checkRule : routeCheckResult.getAllRules()) {
			if(checkRule instanceof RouteCheckRule) {
				RouteCheckRule routeCheckRule = (RouteCheckRule) checkRule;
				String routeTableId = routeCheckRule.getId(); 
				routeTableIds.add(routeTableId);
				log.debug("route table id - {}", routeTableId);
				
				List<String> gatewayIds = new ArrayList<>();
				List<VpnConnection> vpnConnections = routeCheckRule.getVpnConnections();
				
				for(VpnConnection vpnConnection : vpnConnections) {
					CustomerGateway customerGateway = serviceRepository.getCustomerGatewayMap().get(vpnConnection.customerGatewayId());
					DiagramNode customerGatewayNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(vpnConnection.customerGatewayId(), customerGateway)
							).addClass(NodeType.CUSTOMER_GATEWAY)
					);
					log.debug("added node - {routeTableId: {}, {}}", routeTableId, customerGatewayNode.toString());
					
					DiagramNode vpnConnectionNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(vpnConnection.vpnConnectionId(), vpnConnection, AWS)
							).addClass(NodeType.VPN_CONNECTION)
					);
					log.debug("added node - {routeTableId: {}, {}}", routeTableId, vpnConnectionNode.toString());
					
					gatewayIds.add(vpnConnection.vpnConnectionId());
					
					if(serverId != null) {
						DiagramEdge diagramCustomerGatewayEdge = new DiagramEdge(serverId, vpnConnection.customerGatewayId());
						diagramCustomerGatewayEdge.setLabel(vpnConnection.category());
						diagramCustomerGatewayEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramCustomerGatewayEdge));
						log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramCustomerGatewayEdge.toString());
					}
					
					DiagramEdge diagramVpnConnectionEdge = new DiagramEdge(vpnConnection.customerGatewayId(), vpnConnection.vpnConnectionId());
					diagramVpnConnectionEdge.setLabel(vpnConnection.typeAsString());
					diagramVpnConnectionEdge.setBoth(true);
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramVpnConnectionEdge));
					log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramVpnConnectionEdge.toString());
					
					DiagramEdge diagramVpnGatewayEdge = new DiagramEdge(vpnConnection.vpnConnectionId(), vpnConnection.vpnGatewayId());
					diagramVpnGatewayEdge.setLabel(vpnConnection.options().staticRoutesOnly() ? "static" : "dynamic");
					diagramVpnGatewayEdge.setBoth(true);
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramVpnGatewayEdge));
					log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramVpnGatewayEdge.toString());
				}
				
				List<VirtualInterface> virtualInterfaces = routeCheckRule.getVirtualInterfaces();
				for(VirtualInterface virtualInterface : virtualInterfaces) {
					DiagramNode dxLoacationNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(virtualInterface.location(), virtualInterface.location())
							).addClass(NodeType.CORPORATE_DATA_CENTER)
					);
					log.debug("added node - {routeTableId: {}, {}}", routeTableId, dxLoacationNode.toString());
					DiagramNode vifConnectionNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(virtualInterface.connectionId(), virtualInterface, AWS)
							).addClass(NodeType.DIRECT_CONNECT)
					);
					log.debug("added node - {routeTableId: {}, {}}", routeTableId, vifConnectionNode.toString());
					
					gatewayIds.add(virtualInterface.connectionId());
					
					if(serverId != null) {
						DiagramEdge diagramLocationEdge = new DiagramEdge(serverId, virtualInterface.location());
						diagramLocationEdge.setLabel(virtualInterface.customerAddress() + " (Vlan : " + virtualInterface.vlan() + ", Asn :" + virtualInterface.asn() + ")");
						diagramLocationEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramLocationEdge));
						log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramLocationEdge.toString());
					}
					
					DiagramEdge diagramDirectConnectEdge = new DiagramEdge(virtualInterface.location(), virtualInterface.connectionId());
					diagramDirectConnectEdge.setLabel(virtualInterface.amazonAddress() + " (" + virtualInterface.awsDeviceV2() + ")");
					diagramDirectConnectEdge.setBoth(true);
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramDirectConnectEdge));
					log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramDirectConnectEdge.toString());
					
					if(virtualInterface.directConnectGatewayId() != null && !"".equals(virtualInterface.directConnectGatewayId())) {
						DiagramEdge diagramEdge = new DiagramEdge(virtualInterface.connectionId(), virtualInterface.directConnectGatewayId());
						diagramEdge.setLabel(virtualInterface.virtualInterfaceType() + ":" + virtualInterface.virtualInterfaceName() + " (Asn :" + virtualInterface.amazonSideAsn() + ")");
						diagramEdge.setBoth(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
						log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
					}
					
					if(virtualInterface.virtualGatewayId() != null && !"".equals(virtualInterface.virtualGatewayId())) {
						DiagramEdge diagramEdge = new DiagramEdge(virtualInterface.connectionId(), virtualInterface.virtualGatewayId());
						diagramEdge.setLabel(virtualInterface.virtualInterfaceType() + ":" + virtualInterface.virtualInterfaceName() + " (Asn :" + virtualInterface.amazonSideAsn() + ")");
						diagramEdge.setBoth(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
						log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
					}
					
				}
				
				Object gateway = null;
				switch(routeCheckRule.getGatewayType()) {
				case VIRTUAL_GATEWAY:
					gateway = serviceRepository.getVpnGatewayMap().get(routeCheckRule.getGatewayId());
					break;
				case LOCAL:
					gateway = routeCheckRule.getGatewayId();
					break;
				case VPC_ENDPOINT:
					gateway = serviceRepository.getVpcEndpointMap().get(routeCheckRule.getGatewayId());
					break;
				case TRANSIT_GATEWAY:
					gateway = serviceRepository.getTransitGatewayMap().get(routeCheckRule.getGatewayId());
					break;
				case PEERING:
					gateway = serviceRepository.getVpcPeeringMap().get(routeCheckRule.getGatewayId());
					break;
				case INTERNET_GATEWAY:
					gateway = serviceRepository.getInternetGatewayMap().get(routeCheckRule.getGatewayId());
					break;
				case EGRESS_INTERNET_GATEWAY:
					gateway = serviceRepository.getEgressInternetGatewayMap().get(routeCheckRule.getGatewayId());
					break;
				case NETWORK_INTERFACE:
					gateway = serviceRepository.getNetworkInterfaceMap().get(routeCheckRule.getGatewayId());
					break;
				default:
					break;
				}
				
				DiagramNode gatewayNode = diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(routeCheckRule.getGatewayId(), gateway, AWS)
						).addClass(routeCheckRule.getGatewayType())
				);
				log.debug("added node - {routeTableId: {}, {}}", routeTableId, gatewayNode.toString());
				DiagramNode routeNode = diagramResult.addNode(
						new DiagramData<DiagramNode>(
								new DiagramNode(routeCheckRule.getId(), routeCheckRule.getRouteTable(), diagramResult.getVpcId())
						).addClass(NodeType.ROUTE_TABLE)
				);
				log.debug("added node - {routeTableId: {}, {}}", routeTableId, routeNode.toString());

				for(String cidr : routeCheckRule.getCidrs()) {
					
					if(serverId != null) {
						if(gatewayIds.size() < 1) {
							DiagramEdge diagramGatewayEdge = new DiagramEdge(serverId, routeCheckRule.getGatewayId());
							diagramGatewayEdge.setLabel(cidr);
							if(routeCheckRule.getGatewayType() == NodeType.EGRESS_INTERNET_GATEWAY || routeCheckRule.getGatewayType() == NodeType.NAT_GATEWAY) {
								diagramGatewayEdge.setOut(routeCheckRule.getRouteState() == RouteState.ACTIVE);
							} else {
								diagramGatewayEdge.setBoth(routeCheckRule.getRouteState() == RouteState.ACTIVE);
							}
							diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramGatewayEdge));
							log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramGatewayEdge.toString());
						}
					}
					
					DiagramEdge diagramEdge = new DiagramEdge(routeCheckRule.getGatewayId(), routeCheckRule.getId());
					diagramEdge.setLabel(cidr);
					if(routeCheckRule.getGatewayType() == NodeType.EGRESS_INTERNET_GATEWAY || routeCheckRule.getGatewayType() == NodeType.NAT_GATEWAY) {
						diagramEdge.setOut(routeCheckRule.getRouteState() == RouteState.ACTIVE);
					} else {
						diagramEdge.setBoth(routeCheckRule.getRouteState() == RouteState.ACTIVE);
					}
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
					log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
					
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
							new DiagramNode(noRouteId, noRouteName, diagramResult.getVpcId())
					).addClass(NodeType.ROUTE_TABLE)
			);
			log.debug("added node - {routeTableId: {}, {}}", routeTableId, noRouteNode.toString());
			if(serverId != null) {
				DiagramEdge diagramEdge = new DiagramEdge(serverId, noRouteId);
				diagramEdge.setLabel("Have no route");
				diagramEdge.setBoth(false);
				diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
				log.debug("added edge - {routeTableId: {}, {}}", routeTableId, diagramEdge.toString());
			}
		}

		return routeTableIds;
	}
	
	protected String setNetworkAcl(String routeTableId, CheckResult networkAclCheckResult, DiagramResult diagramResult) {
	
		log.debug("set network acl - {}", routeTableId);
		
		String networkAclId = "";
		for(CheckRule checkRule : networkAclCheckResult.getAllRules()) {
			if(checkRule instanceof NetworkAclCheckRule) {
				NetworkAclCheckRule networkAclCheckRule = (NetworkAclCheckRule) checkRule;
				
				if(routeTableId.equals(networkAclCheckRule.getRouteTableId())) {
					networkAclId = networkAclCheckRule.getId();
					
					DiagramNode diagramNode = diagramResult.addNode(
							new DiagramData<DiagramNode>(
									new DiagramNode(networkAclCheckRule.getId(), networkAclCheckRule.getNetworkAcl(), diagramResult.getVpcId())
							).addClass(NodeType.NETWORK_ACL)
					);
					log.debug("add node - {networkAclId:{}, {}}", networkAclId, diagramNode.toString());
					
					DiagramEdge diagramEdge = new DiagramEdge(routeTableId, networkAclCheckRule.getId());
					
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
					log.debug("add edge - {networkAclId:{}, {}}", networkAclId, diagramEdge.toString());
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
								new DiagramNode(securityGroupCheckRule.getId(), securityGroupCheckRule.getSecurityGroup(), diagramResult.getVpcId())
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
				
				DiagramEdge diagramEdge = new DiagramEdge(networkAclId, securityGroupCheckRule.getId());
				DiagramEdge serverEdge = new DiagramEdge(securityGroupCheckRule.getId(), instanceId);
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
			DiagramEdge serverEdge = new DiagramEdge(networkAclId, instanceId);
			serverEdge.setLabel("Not allow in SecurityGroup");
			serverEdge.setBoth(false);
			diagramResult.addEdge(new DiagramData<DiagramEdge>(serverEdge));
		}
	}

}

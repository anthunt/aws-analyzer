package com.anthunt.aws.network.service;

import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.service.checker.ServiceRepository;
import com.anthunt.aws.network.service.model.CheckResult;
import com.anthunt.aws.network.service.model.CheckRule;
import com.anthunt.aws.network.service.model.DirectionType;
import com.anthunt.aws.network.service.model.NetworkAclCheckRule;
import com.anthunt.aws.network.service.model.RouteCheckRule;
import com.anthunt.aws.network.service.model.SecurityGroupCheckRule;
import com.anthunt.aws.network.service.model.diagram.DiagramData;
import com.anthunt.aws.network.service.model.diagram.DiagramEdge;
import com.anthunt.aws.network.service.model.diagram.DiagramNode;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.service.model.diagram.NodeType;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.directconnect.model.VirtualInterfaceState;
import software.amazon.awssdk.services.ec2.model.RouteState;
import software.amazon.awssdk.services.ec2.model.RuleAction;
import software.amazon.awssdk.services.ec2.model.VpnConnection;

public abstract class AbstractNetworkService {

	public DiagramResult getNetworkDiagram(ServiceRepository serviceRepository, String instanceId, String targetIp) {
		return this.getNetwork(serviceRepository, instanceId, targetIp);
	}
	
	protected abstract DiagramResult getNetwork(ServiceRepository serviceRepository, String instanceId, String targetIp);
	
	protected String setRouteTable(String serverId, CheckResult routeCheckResult, DiagramResult diagramResult) {

		String routeTableId = "";
		for(CheckRule checkRule : routeCheckResult.getAllRules()) {
			if(checkRule instanceof RouteCheckRule) {
				RouteCheckRule routeCheckRule = (RouteCheckRule) checkRule;
				
				routeTableId = routeCheckRule.getId();
				
				List<String> gatewayIds = new ArrayList<>();
				
				List<VpnConnection> vpnConnections = routeCheckRule.getVpnConnections();
				
				for(VpnConnection vpnConnection : vpnConnections) {
					diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(vpnConnection.customerGatewayId(), vpnConnection.customerGatewayId())).addClass(NodeType.CUSTOMER_GATEWAY));
					diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(vpnConnection.vpnConnectionId(), vpnConnection.vpnConnectionId())).addClass(NodeType.VPN_CONNECTION));
					
					gatewayIds.add(vpnConnection.vpnConnectionId());
					
					if(serverId != null) {
						DiagramEdge diagramCustomerGatewayEdge = new DiagramEdge(serverId, vpnConnection.customerGatewayId());
						diagramCustomerGatewayEdge.setLabel(vpnConnection.category());
						diagramCustomerGatewayEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramCustomerGatewayEdge));
					}
					
					DiagramEdge diagramVpnConnectionEdge = new DiagramEdge(vpnConnection.customerGatewayId(), vpnConnection.vpnConnectionId());
					diagramVpnConnectionEdge.setLabel(vpnConnection.typeAsString());
					diagramVpnConnectionEdge.setBoth(true);
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramVpnConnectionEdge));
					
					DiagramEdge diagramVpnGatewayEdge = new DiagramEdge(vpnConnection.vpnConnectionId(), vpnConnection.vpnGatewayId());
					diagramVpnGatewayEdge.setLabel(vpnConnection.options().staticRoutesOnly() ? "static" : "dynamic");
					diagramVpnGatewayEdge.setBoth(true);
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramVpnGatewayEdge));
				}
				
				List<VirtualInterface> virtualInterfaces = routeCheckRule.getVirtualInterfaces();
				for(VirtualInterface virtualInterface : virtualInterfaces) {
					diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(virtualInterface.location(), virtualInterface.location())).addClass(NodeType.CORPORATE_DATA_CENTER));
					diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(virtualInterface.connectionId(), virtualInterface.connectionId())).addClass(NodeType.DIRECT_CONNECT));
					
					gatewayIds.add(virtualInterface.connectionId());
					
					if(serverId != null) {
						DiagramEdge diagramLocationEdge = new DiagramEdge(serverId, virtualInterface.location());
						diagramLocationEdge.setLabel(virtualInterface.customerAddress() + " (Vlan : " + virtualInterface.vlan() + ", Asn :" + virtualInterface.asn() + ")");
						diagramLocationEdge.setBoth(true);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramLocationEdge));
					}
					
					DiagramEdge diagramDirectConnectEdge = new DiagramEdge(virtualInterface.location(), virtualInterface.connectionId());
					diagramDirectConnectEdge.setLabel(virtualInterface.amazonAddress() + " (" + virtualInterface.awsDeviceV2() + ")");
					diagramDirectConnectEdge.setBoth(true);
					diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramDirectConnectEdge));
					
					if(virtualInterface.directConnectGatewayId() != null && !"".equals(virtualInterface.directConnectGatewayId())) {
						DiagramEdge diagramEdge = new DiagramEdge(virtualInterface.connectionId(), virtualInterface.directConnectGatewayId());
						diagramEdge.setLabel(virtualInterface.virtualInterfaceType() + ":" + virtualInterface.virtualInterfaceName() + " (Asn :" + virtualInterface.amazonSideAsn() + ")");
						diagramEdge.setBoth(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
					}
					
					if(virtualInterface.virtualGatewayId() != null && !"".equals(virtualInterface.virtualGatewayId())) {
						DiagramEdge diagramEdge = new DiagramEdge(virtualInterface.connectionId(), virtualInterface.virtualGatewayId());
						diagramEdge.setLabel(virtualInterface.virtualInterfaceType() + ":" + virtualInterface.virtualInterfaceName() + " (Asn :" + virtualInterface.amazonSideAsn() + ")");
						diagramEdge.setBoth(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE);
						diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
					}
					
				}
				
				diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(routeCheckRule.getGatewayId(), routeCheckRule.getGatewayId())).addClass(routeCheckRule.getGatewayType()));
				diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(routeCheckRule.getId(), routeCheckRule.getName())).addClass(NodeType.ROUTE_TABLE));

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
					
				}
				
			}
		}
		
		if(routeCheckResult.getAllRules().size() < 1) {
			String noRouteId = "noRoute";
			String noRouteName = "Unknown Route";
			routeTableId = noRouteId;
			diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(noRouteId, noRouteName)).addClass(NodeType.ROUTE_TABLE));
			if(serverId != null) {
				DiagramEdge diagramEdge = new DiagramEdge(serverId, noRouteId);
				diagramEdge.setLabel("Have no route");
				diagramEdge.setBoth(false);
				diagramResult.addEdge(new DiagramData<DiagramEdge>(diagramEdge));
			}
		}

		return routeTableId;
	}
	
	protected String setNetworkAcl(String routeTableId, CheckResult networkAclCheckResult, DiagramResult diagramResult) {
	
		String networkAclId = "";
		for(CheckRule checkRule : networkAclCheckResult.getAllRules()) {
			if(checkRule instanceof NetworkAclCheckRule) {
				NetworkAclCheckRule networkAclCheckRule = (NetworkAclCheckRule) checkRule;
				
				networkAclId = networkAclCheckRule.getId();
				diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(networkAclCheckRule.getId(), networkAclCheckRule.getName())).addClass(NodeType.NETWORK_ACL));
				
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
			}
		}
		return networkAclId;
	}

	protected void setSecurityGroup(String networkAclId, String instanceId, CheckResult securityGroupCheckResult, DiagramResult diagramResult) {
		
		for(CheckRule checkRule : securityGroupCheckResult.getAllRules()) {
			if(checkRule instanceof SecurityGroupCheckRule) {
				SecurityGroupCheckRule securityGroupCheckRule = (SecurityGroupCheckRule) checkRule;
				
				diagramResult.addNode(new DiagramData<DiagramNode>(new DiagramNode(securityGroupCheckRule.getId(), securityGroupCheckRule.getName())).addClass(NodeType.SECURITY_GROUP));
				
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

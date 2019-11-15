package com.anthunt.aws.network.service.model;

import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.service.model.diagram.NodeType;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.RouteState;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.VpnConnection;

public class RouteCheckRule implements CheckRule {

	private String id;
	private String name;
	private boolean isCidr = false;
	private String prefixListId;
	private String prefixListName;
	private List<String> cidrs;
	private NodeType gatewayType;
	private String gatewayId;
	private String gatewayName;
	private List<VpnConnection> vpnConnections;
	private List<VirtualInterface> virtualInterfaces;
	private RouteState routeState;
	private RouteTable routeTable;
	
	public RouteCheckRule(String id, String name, RouteTable routeTable) {
		this.setId(id);
		this.setName(name);
		this.cidrs = new ArrayList<String>();
		this.vpnConnections = new ArrayList<VpnConnection>();
		this.virtualInterfaces = new ArrayList<VirtualInterface>();
		this.setRouteTable(routeTable);
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

	public boolean isCidr() {
		return isCidr;
	}

	public void setCidr(boolean isCidr) {
		this.isCidr = isCidr;
	}

	public String getPrefixListId() {
		return prefixListId;
	}

	public void setPrefixListId(String prefixListId) {
		this.prefixListId = prefixListId;
	}

	public String getPrefixListName() {
		return prefixListName;
	}

	public void setPrefixListName(String prefixListName) {
		this.prefixListName = prefixListName;
	}

	public List<String> getCidrs() {
		return cidrs;
	}
	
	public void addCidr(String cidr) {
		this.cidrs.add(cidr);
	}

	public String getGatewayId() {
		return gatewayId;
	}
	
	public String getGatewayName() {
		return gatewayName;
	}
	
	public NodeType getGatewayType() {
		return gatewayType;
	}

	public void setGateway(NodeType gatewayType, String gatewayId) {
		this.gatewayType = gatewayType;
		this.gatewayId = gatewayId;
	}
	
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public List<VpnConnection> getVpnConnections() {
		return this.vpnConnections;
	}
	
	public void setVpnConnections(List<VpnConnection> vpnConnections) {
		this.vpnConnections = vpnConnections;
	}
	
	public List<VirtualInterface> getVirtualInterfaces() {
		return this.virtualInterfaces;
	}
	
	public void setVirtualInterfaces(List<VirtualInterface> virtualInterfaces) {
		this.virtualInterfaces = virtualInterfaces;
	}
	
	public RouteState getRouteState() {
		return routeState;
	}

	public void setRouteState(RouteState routeState) {
		this.routeState = routeState;
	}

	public RouteTable getRouteTable() {
		return routeTable;
	}

	private void setRouteTable(RouteTable routeTable) {
		this.routeTable = routeTable;
	}

}

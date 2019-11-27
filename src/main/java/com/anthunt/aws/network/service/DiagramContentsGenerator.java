package com.anthunt.aws.network.service;

import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.IamInstanceProfile;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnectionVpcInfo;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetTypeEnum;

public class DiagramContentsGenerator {

	private enum Type {
		TITLE,
		LABEL;
	}
	
	public static DiagramContentsGenerator getInstance(Object resource) {
		return new DiagramContentsGenerator(resource);
	}
	
	private Object resource;
	
	public DiagramContentsGenerator(Object resource) {
		this.resource = resource;
	}
	
	public String title() {
		return this.generate(Type.TITLE);
	}
	
	public String label() {
		return this.generate(Type.LABEL);
	}
	
	private String generate(Type type) {
		
		String label = new String();
		
		if(resource instanceof Instance) {
			label = type == Type.TITLE ? getEc2InstanceTitle() : getEc2InstanceLabel();
		} else if(resource instanceof Volume) {
			label = type == Type.TITLE ? getVolumeTitle() : getVolumeLabel();
		} else if(resource instanceof NetworkInterface) {
			label = type == Type.TITLE ? getNetworkInterfaceTitle() : getNetworkInterfaceLabel();
		} else if(resource instanceof SecurityGroup) {
			label = type == Type.TITLE ? getSecurityGroupTitle() : getSecurityGroupLabel();
		} else if(resource instanceof RouteTable) {
			label = type == Type.TITLE ? getRouteTableTitle() : getRouteTableLabel();
		} else if(resource instanceof VirtualInterface) {
			label = type == Type.TITLE ? getVirtualInterfaceTitle() : getVirtualInterfaceLabel();
		} else if(resource instanceof VpnConnection) {
			label = type == Type.TITLE ? getVpnConnectionTitle() : getVpnConnectionLabel();
		} else if(resource instanceof CustomerGateway) {
			label = type == Type.TITLE ? getCustomerGatewayTitle() : getCustomerGatewayLabel();
		} else if(resource instanceof NetworkAcl) {
			label = type == Type.TITLE ? getNetworkAclTitle() : getNetworkAclLabel();
		} else if(resource instanceof LoadBalancer) {
			label = type == Type.TITLE ? getLoadBalancerTitle() : getLoadBalnacerLabel();
		} else if(resource instanceof TargetGroup) {
			label = type == Type.TITLE ? getTargetGroupTitle() : getTargetGroupLabel();
		} else if(resource instanceof VpnGateway) {
			label = type == Type.TITLE ? getVpnGatewayTitle() : getVpnGatewayLabel();
		} else if(resource instanceof VpcEndpoint) {
			label = type == Type.TITLE ? getVpcEndpointTitle() : getVpcEndpointLabel();
		} else if(resource instanceof TransitGateway) {
			label = type == Type.TITLE ? getTransitGatewayTitle() : getTransitGatewayLabel();
		} else if(resource instanceof VpcPeeringConnection) {
			label = type == Type.TITLE ? getVpcPeeringTitle() : getVpcPeeringLabel();
		} else if(resource instanceof InternetGateway) {
			label = type == Type.TITLE ? getInternetGatewayTitle() : getInternetGatewayLabel();
		} else if(resource instanceof EgressOnlyInternetGateway) {
			label = type == Type.TITLE ? getEgressOnlyInternetGatewayTitle() : getEgressOnlyInternetGatewayLabel();
		} else if(resource instanceof IamInstanceProfile) {
			//String roleName = iamInstanceProfile.arn().substring(iamInstanceProfile.arn().lastIndexOf("/"), iamInstanceProfile.arn().length());
		} else if(resource instanceof Vpc) {
			
		} else if(resource instanceof Subnet) {
			
		} else if(resource instanceof String) {
			label = (String) resource;
		}
		
		return label;
	}
	
	private static String getNameTag(String name, String resourceId) {
		if(Utils.DEFAULT_NAME.equals(name)) {
			return resourceId;
		}
		return name;
	}
	
	private String getEgressOnlyInternetGatewayTitle() {
		return ((EgressOnlyInternetGateway) this.resource).egressOnlyInternetGatewayId();
	}
	
	private String getEgressOnlyInternetGatewayLabel() {
		return new StringBuffer()
				.append("<div>")
				.append(this.getEgressOnlyInternetGatewayTitle())
				.append("</div>").toString();
	}

	private String getInternetGatewayTitle() {
		InternetGateway internetGateway = (InternetGateway) this.resource;
		return getNameTag(Utils.getNameFromTags(internetGateway.tags()), internetGateway.internetGatewayId());
	}
	
	private String getInternetGatewayLabel() {
		InternetGateway internetGateway = (InternetGateway) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getInternetGatewayTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Vpcs:</strong> ").append(internetGateway.attachments().size()).append("ea<br>")
				.append("</p>")
				.append("</div>").toString();
	}

	private String getVpcPeeringTitle() {
		VpcPeeringConnection vpcPeeringConnection = (VpcPeeringConnection) this.resource;
		return getNameTag(Utils.getNameFromTags(vpcPeeringConnection.tags()), vpcPeeringConnection.vpcPeeringConnectionId());
	}
	
	private String getVpcPeeringLabel() {
		
		VpcPeeringConnection vpcPeeringConnection = (VpcPeeringConnection) this.resource;
		
		VpcPeeringConnectionVpcInfo requesterVpcInfo = vpcPeeringConnection.requesterVpcInfo();
		VpcPeeringConnectionVpcInfo accepterVpcInfo = vpcPeeringConnection.accepterVpcInfo();
		
		return new StringBuffer()
				.append("<div>")
				.append(this.getVpcPeeringTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Requester</strong> ").append("<br>")
				.append("<strong>Region:</strong> ").append(requesterVpcInfo.region()).append("<br>")
				.append("<strong>CIDR:</strong> ").append(requesterVpcInfo.cidrBlock()).append("<br>")
				.append("<strong>Vpc Id:</strong> ").append(requesterVpcInfo.vpcId()).append("<br>")
				.append("<strong>Owner Id:</strong> ").append(requesterVpcInfo.ownerId()).append("<br>")
				.append("<strong>Accepter</strong> ").append("<br>")
				.append("<strong>Region:</strong> ").append(accepterVpcInfo.region()).append("<br>")
				.append("<strong>CIDR:</strong> ").append(accepterVpcInfo.cidrBlock()).append("<br>")
				.append("<strong>Vpc Id:</strong> ").append(accepterVpcInfo.vpcId()).append("<br>")
				.append("<strong>Owner Id:</strong> ").append(accepterVpcInfo.ownerId()).append("<br>")
				.append("<strong>State:</strong> ").append(vpcPeeringConnection.status().codeAsString())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getTransitGatewayTitle() {
		TransitGateway transitGateway = (TransitGateway) this.resource;
		return getNameTag(Utils.getNameFromTags(transitGateway.tags()), transitGateway.transitGatewayId());
	}
	
	private String getTransitGatewayLabel() {
		TransitGateway transitGateway = (TransitGateway) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getTransitGatewayTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>State:</strong> ").append(transitGateway.state().name())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getVpcEndpointTitle() {
		VpcEndpoint vpcEndpoint = (VpcEndpoint) this.resource;
		return getNameTag(Utils.getNameFromTags(vpcEndpoint.tags()), vpcEndpoint.vpcEndpointId());
	}
	
	private String getVpcEndpointLabel() {
		VpcEndpoint vpcEndpoint = (VpcEndpoint) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getVpcEndpointTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Service Name:</strong> ").append(vpcEndpoint.serviceName()).append("<br>")
				.append("<strong>State:</strong> ").append(vpcEndpoint.stateAsString())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getVpnGatewayTitle() {
		VpnGateway vpnGateway = (VpnGateway) this.resource;
		return getNameTag(Utils.getNameFromTags(vpnGateway.tags()), vpnGateway.vpnGatewayId());
	}
	
	private String getVpnGatewayLabel() {
		VpnGateway vpnGateway = (VpnGateway) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getVpnGatewayTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(vpnGateway.typeAsString()).append("<br>")
				.append("<strong>Amazon Side ASN:</strong> ").append(vpnGateway.amazonSideAsn()).append("<br>")
				.append("<strong>State:</strong> ").append(vpnGateway.stateAsString()).append("<br>")
				.append("<strong>Vpcs:</strong> ").append(vpnGateway.vpcAttachments().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}

	private String getTargetGroupTitle() {
		return ((TargetGroup) this.resource).targetGroupName();
	}
	
	private String getTargetGroupLabel() {
		TargetGroup targetGroup = (TargetGroup) this.resource;
		StringBuffer targetGroupLabel = new StringBuffer()
				.append("<div>")
				.append(this.getTargetGroupTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(targetGroup.targetTypeAsString()).append("<br>")
				.append("<strong>Target Type:</strong> ").append(targetGroup.targetType()).append("<br>");
		
		if(targetGroup.targetType() != TargetTypeEnum.LAMBDA) {
			targetGroupLabel
				.append("<strong>Protocol:</strong> ").append(targetGroup.protocol()).append("<br>")
				.append("<strong>Port:</strong> ").append(targetGroup.port());
		}
		
		targetGroupLabel
			.append("</p>")
			.append("</div>");
		
		return targetGroupLabel.toString();
	}

	private String getLoadBalancerTitle() {
		return ((LoadBalancer) this.resource).loadBalancerName();
	}
	
	private String getLoadBalnacerLabel() {
		LoadBalancer loadBalancer = (LoadBalancer) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getLoadBalancerTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(loadBalancer.typeAsString()).append("<br>")
				.append("<strong>Ip Address Type:</strong> ").append(loadBalancer.ipAddressTypeAsString()).append("<br>")
				.append("<strong>State:</strong> ").append(loadBalancer.state().codeAsString()).append("<br>")
				.append("<strong>Dns:</strong> ").append(loadBalancer.dnsName())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getNetworkAclTitle() {
		NetworkAcl networkAcl = (NetworkAcl) this.resource;
		return getNameTag(Utils.getNameFromTags(networkAcl.tags()), networkAcl.networkAclId());
	}
	
	private String getNetworkAclLabel() {
		NetworkAcl networkAcl = (NetworkAcl) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getNetworkAclTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Associated Subnets:</strong> ").append(networkAcl.associations().size()).append("ea<br>")
				.append("<strong>Rules:</strong> ").append(networkAcl.entries().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}

	private String getCustomerGatewayTitle() {
		CustomerGateway customerGateway = (CustomerGateway) this.resource;
		return getNameTag(Utils.getNameFromTags(customerGateway.tags()), customerGateway.customerGatewayId());
	}
	
	private String getCustomerGatewayLabel() {
		CustomerGateway customerGateway = (CustomerGateway) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getCustomerGatewayTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(customerGateway.type()).append("<br>")
				.append("<strong>State:</strong> ").append(customerGateway.state()).append("<br>")
				.append("<strong>Ip:</strong> " ).append(customerGateway.ipAddress()).append("<br>")
				.append("<strong>Bgp ASN:</strong> ").append(customerGateway.bgpAsn())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getVpnConnectionTitle() {
		VpnConnection vpnConnection = (VpnConnection) this.resource;
		return getNameTag(Utils.getNameFromTags(vpnConnection.tags()), vpnConnection.vpnConnectionId());
	}
	
	private String getVpnConnectionLabel() {
		VpnConnection vpnConnection = (VpnConnection) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getVpnConnectionTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Category:</strong> ").append(vpnConnection.category()).append("<br>")
				.append("<strong>Type:</strong> ").append(vpnConnection.typeAsString()).append("<br>")
				.append("<strong>State:</strong> ").append(vpnConnection.stateAsString())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getVirtualInterfaceTitle() {
		VirtualInterface virtualInterface = (VirtualInterface) this.resource;
		return getNameTag(Utils.getNameFromDXTags(virtualInterface.tags()), virtualInterface.virtualInterfaceId());
	}
	
	private String getVirtualInterfaceLabel() {
		VirtualInterface virtualInterface = (VirtualInterface) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getVirtualInterfaceTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(virtualInterface.virtualInterfaceType()).append("<br>")
				.append("<strong>State:</strong> ").append(virtualInterface.virtualInterfaceStateAsString()).append("<br>")
				.append("<strong>AWS Device:</strong> " ).append(virtualInterface.awsDeviceV2()).append("<br>")
				.append("<strong>Customer Address:</strong> " ).append(virtualInterface.customerAddress()).append("<br>")
				.append("<strong>ASN:</strong> " ).append(virtualInterface.asn()).append("<br>")
				.append("<strong>Amazon Address:</strong> " ).append(virtualInterface.amazonAddress()).append("<br>")
				.append("<strong>Amazon Side ASN:</strong> ").append(virtualInterface.amazonSideAsn())
				.append("</p>")
				.append("</div>").toString();
	}

	private String getRouteTableTitle() {
		RouteTable routeTable = (RouteTable) this.resource;
		return getNameTag(Utils.getNameFromTags(routeTable.tags()), routeTable.routeTableId());
	}
	
	private String getRouteTableLabel() {
		RouteTable routeTable = (RouteTable) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getRouteTableTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Associated Subnets:</strong> ").append(routeTable.associations().size()).append("ea<br>")
				.append("<strong>Propagating Vgws:</strong> ").append(routeTable.propagatingVgws().size()).append("ea<br>")
				.append("<strong>Routes:</strong> " ).append(routeTable.routes().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}

	private String getEc2InstanceTitle() {
		Instance instance = (Instance) this.resource;
		return getNameTag(Utils.getNameFromTags(instance.tags()), instance.instanceId());
	}
	
	private String getEc2InstanceLabel() {
		Instance instance = (Instance) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getEc2InstanceTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(instance.instanceTypeAsString()).append("<br>")
				.append("<strong>State:</strong> ").append(instance.state().nameAsString()).append("<br>")
				.append("<strong>Private Ip:</strong> " ).append(instance.privateIpAddress()).append("<br>")
				.append("<strong>Source Dest Check:</strong> ").append(instance.sourceDestCheck())
				.append("</p>")
				.append("</div>").toString();
	}
	
	private String getVolumeTitle() {
		Volume volume = (Volume) this.resource;
		return getNameTag(Utils.getNameFromTags(volume.tags()), volume.volumeId());
	}
	
	private String getVolumeLabel() {
		Volume volume = (Volume) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getVolumeTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Az:</strong> ").append(volume.availabilityZone()).append("<br>")
				.append("<strong>Type:</strong> ").append(volume.volumeTypeAsString()).append("<br>")
				.append(volume.iops() == null ? "" : "<strong>Iops:</strong> " + volume.iops() + "iops<br>")
				.append("<strong>Size:</strong> ").append(volume.size()).append("GB<br>")
				.append(volume.encrypted() ? "Encrypted" : "")
				.append("</p>")
				.append("</div>").toString();
	}
	
	private String getNetworkInterfaceTitle() {
		NetworkInterface networkInterface = (NetworkInterface) this.resource;
		return getNameTag(Utils.getNameFromTags(networkInterface.tagSet()), networkInterface.networkInterfaceId());
	}
	
	private String getNetworkInterfaceLabel() {
		NetworkInterface networkInterface = (NetworkInterface) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getNetworkInterfaceTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Az:</strong> ").append(networkInterface.availabilityZone()).append("<br>")
				.append("<strong>Subnet:</strong> ").append(networkInterface.subnetId()).append("<br>")
				.append("<strong>Status:</strong> ").append(networkInterface.statusAsString()).append("<br>")
				.append("<strong>Type:</strong> ").append(networkInterface.interfaceTypeAsString()).append("<br>")
				.append("<strong>Private Ip:</strong> ").append(networkInterface.privateIpAddress()).append("<br>")
				.append("<strong>Private Dns:</strong> ").append(networkInterface.privateDnsName()).append("<br>")
				.append("<strong>Mac:</strong> ").append(networkInterface.macAddress()).append("<br>")
				.append("<strong>Source Dest Check:</strong> ").append(networkInterface.sourceDestCheck())
				.append("</p>")
				.append("</div>").toString();
	}
	
	private String getSecurityGroupTitle() {
		SecurityGroup securityGroup = (SecurityGroup) this.resource;
		return getNameTag(Utils.getNameFromTags(securityGroup.tags()), securityGroup.groupName());
	}
	
	private String getSecurityGroupLabel() {
		SecurityGroup securityGroup = (SecurityGroup) this.resource;
		return new StringBuffer()
				.append("<div>")
				.append(this.getSecurityGroupTitle())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>InBound Rules:</strong> ").append(securityGroup.ipPermissions().size()).append("ea<br>")
				.append("<strong>OutBound Rules:</strong> ").append(securityGroup.ipPermissionsEgress().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}
	
}

package com.anthunt.aws.network.service;

import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnectionVpcInfo;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetTypeEnum;

public class DiagramLabelGenerator {

	public static String generate(Object resource) {
		
		String label = new String();
		
		if(resource instanceof Instance) {
			label = getEc2InstanceLabel((Instance) resource);
		} else if(resource instanceof Volume) {
			label = getVolumeLabel((Volume) resource);
		} else if(resource instanceof NetworkInterface) {
			label = getNetworkInterfaceLabel((NetworkInterface) resource);
		} else if(resource instanceof SecurityGroup) {
			label = getSecurityGroupLabel((SecurityGroup) resource);
		} else if(resource instanceof RouteTable) {
			label = getRouteTableLabel((RouteTable) resource);
		} else if(resource instanceof VirtualInterface) {
			label = getVirtualInterfaceLabel((VirtualInterface) resource);
		} else if(resource instanceof VpnConnection) {
			label = getVpnConnectionLabel((VpnConnection) resource);
		} else if(resource instanceof CustomerGateway) {
			label = getCustomerGatewayLabel((CustomerGateway) resource);
		} else if(resource instanceof NetworkAcl) {
			label = getNetworkAclLabel((NetworkAcl) resource);
		} else if(resource instanceof LoadBalancer) {
			label = getLoadBalnacerLabel((LoadBalancer) resource);
		} else if(resource instanceof TargetGroup) {
			label = getTargetGroupLabel((TargetGroup) resource);
		} else if(resource instanceof VpnGateway) {
			label = getVpnGatewayLabel((VpnGateway) resource);
		} else if(resource instanceof VpcEndpoint) {
			label = getVpcEndpointLabel((VpcEndpoint) resource);
		} else if(resource instanceof TransitGateway) {
			label = getTransitGatewayLabel((TransitGateway) resource);
		} else if(resource instanceof VpcPeeringConnection) {
			label = getVpcPeeringLabel((VpcPeeringConnection) resource);
		} else if(resource instanceof InternetGateway) {
			label = getInternetGatewayLabel((InternetGateway) resource);
		} else if(resource instanceof EgressOnlyInternetGateway) {
			label = getEgressOnlyInternetGatewayLabel((EgressOnlyInternetGateway) resource);
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
	
	private static String getEgressOnlyInternetGatewayLabel(EgressOnlyInternetGateway egressOnlyInternetGateway) {
		return new StringBuffer()
				.append("<div>")
				.append(egressOnlyInternetGateway.egressOnlyInternetGatewayId())
				.append("</div>").toString();
	}

	private static String getInternetGatewayLabel(InternetGateway internetGateway) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(internetGateway.tags()), internetGateway.internetGatewayId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Vpcs:</strong> ").append(internetGateway.attachments().size()).append("ea<br>")
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getVpcPeeringLabel(VpcPeeringConnection vpcPeeringConnection) {
		
		VpcPeeringConnectionVpcInfo requesterVpcInfo = vpcPeeringConnection.requesterVpcInfo();
		VpcPeeringConnectionVpcInfo accepterVpcInfo = vpcPeeringConnection.accepterVpcInfo();
		
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(vpcPeeringConnection.tags()), vpcPeeringConnection.vpcPeeringConnectionId()))
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

	private static String getTransitGatewayLabel(TransitGateway transitGateway) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(transitGateway.tags()), transitGateway.transitGatewayId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>State:</strong> ").append(transitGateway.state().name())
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getVpcEndpointLabel(VpcEndpoint vpcEndpoint) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(vpcEndpoint.tags()), vpcEndpoint.vpcEndpointId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Service Name:</strong> ").append(vpcEndpoint.serviceName()).append("<br>")
				.append("<strong>State:</strong> ").append(vpcEndpoint.stateAsString())
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getVpnGatewayLabel(VpnGateway vpnGateway) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(vpnGateway.tags()), vpnGateway.vpnGatewayId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(vpnGateway.typeAsString()).append("<br>")
				.append("<strong>Amazon Side ASN:</strong> ").append(vpnGateway.amazonSideAsn()).append("<br>")
				.append("<strong>State:</strong> ").append(vpnGateway.stateAsString()).append("<br>")
				.append("<strong>Vpcs:</strong> ").append(vpnGateway.vpcAttachments().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getTargetGroupLabel(TargetGroup targetGroup) {
		StringBuffer targetGroupLabel = new StringBuffer()
				.append("<div>")
				.append(targetGroup.targetGroupName())
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

	private static String getLoadBalnacerLabel(LoadBalancer loadBalancer) {
		return new StringBuffer()
				.append("<div>")
				.append(loadBalancer.loadBalancerName())
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(loadBalancer.typeAsString()).append("<br>")
				.append("<strong>Ip Address Type:</strong> ").append(loadBalancer.ipAddressTypeAsString()).append("<br>")
				.append("<strong>State:</strong> ").append(loadBalancer.state().codeAsString()).append("<br>")
				.append("<strong>Dns:</strong> ").append(loadBalancer.dnsName())
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getNetworkAclLabel(NetworkAcl networkAcl) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(networkAcl.tags()), networkAcl.networkAclId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Associated Subnets:</strong> ").append(networkAcl.associations().size()).append("ea<br>")
				.append("<strong>Rules:</strong> ").append(networkAcl.entries().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getCustomerGatewayLabel(CustomerGateway customerGateway) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(customerGateway.tags()), customerGateway.customerGatewayId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(customerGateway.type()).append("<br>")
				.append("<strong>State:</strong> ").append(customerGateway.state()).append("<br>")
				.append("<strong>Ip:</strong> " ).append(customerGateway.ipAddress()).append("<br>")
				.append("<strong>Bgp ASN:</strong> ").append(customerGateway.bgpAsn())
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getVpnConnectionLabel(VpnConnection vpnConnection) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(vpnConnection.tags()), vpnConnection.vpnConnectionId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Category:</strong> ").append(vpnConnection.category()).append("<br>")
				.append("<strong>Type:</strong> ").append(vpnConnection.typeAsString()).append("<br>")
				.append("<strong>State:</strong> ").append(vpnConnection.stateAsString())
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getVirtualInterfaceLabel(VirtualInterface virtualInterface) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromDXTags(virtualInterface.tags()), virtualInterface.virtualInterfaceId()))
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

	private static String getRouteTableLabel(RouteTable routeTable) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(routeTable.tags()), routeTable.routeTableId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Associated Subnets:</strong> ").append(routeTable.associations().size()).append("ea<br>")
				.append("<strong>Propagating Vgws:</strong> ").append(routeTable.propagatingVgws().size()).append("ea<br>")
				.append("<strong>Routes:</strong> " ).append(routeTable.routes().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}

	private static String getEc2InstanceLabel(Instance instance) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(instance.tags()), instance.instanceId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Type:</strong> ").append(instance.instanceTypeAsString()).append("<br>")
				.append("<strong>State:</strong> ").append(instance.state().nameAsString()).append("<br>")
				.append("<strong>Private Ip:</strong> " ).append(instance.privateIpAddress()).append("<br>")
				.append("<strong>Source Dest Check:</strong> ").append(instance.sourceDestCheck())
				.append("</p>")
				.append("</div>").toString();
	}
	
	private static String getVolumeLabel(Volume volume) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(volume.tags()), volume.volumeId()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>Az:</strong> ").append(volume.availabilityZone()).append("<br>")
				.append("<strong>Type:</strong> ").append(volume.volumeTypeAsString()).append("<br>")
				.append(volume.iops() == null ? "" : "<strong>Iops:</strong> " + volume.iops() + "iops<br>")
				.append("<strong>Size:</strong> ").append(volume.size()).append("GB<br>")
				.append(volume.encrypted() ? "Encrypted" : "")
				.append("</p>")
				.append("</div>").toString();
	}
	
	private static String getNetworkInterfaceLabel(NetworkInterface networkInterface) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(networkInterface.tagSet()), networkInterface.networkInterfaceId()))
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
	
	private static String getSecurityGroupLabel(SecurityGroup securityGroup) {
		return new StringBuffer()
				.append("<div>")
				.append(getNameTag(Utils.getNameFromTags(securityGroup.tags()), securityGroup.groupName()))
				.append("<p style='text-align: left; background-color: #ffffff7a; padding: 5px;'>")
				.append("<strong>InBound Rules:</strong> ").append(securityGroup.ipPermissions().size()).append("ea<br>")
				.append("<strong>OutBound Rules:</strong> ").append(securityGroup.ipPermissionsEgress().size()).append("ea")
				.append("</p>")
				.append("</div>").toString();
	}
	
}

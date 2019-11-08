import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directconnect.model.BGPPeer;
import software.amazon.awssdk.services.directconnect.model.DescribeDirectConnectGatewayAssociationsRequest;
import software.amazon.awssdk.services.directconnect.model.DescribeDirectConnectGatewayAssociationsResponse;
import software.amazon.awssdk.services.directconnect.model.DescribeDirectConnectGatewayAttachmentsRequest;
import software.amazon.awssdk.services.directconnect.model.DescribeVirtualGatewaysRequest;
import software.amazon.awssdk.services.directconnect.model.DescribeVirtualInterfacesRequest;
import software.amazon.awssdk.services.directconnect.model.DescribeVirtualInterfacesResponse;
import software.amazon.awssdk.services.directconnect.model.DirectConnectGatewayAssociation;
import software.amazon.awssdk.services.directconnect.model.RouteFilterPrefix;
import software.amazon.awssdk.services.directconnect.model.VirtualGateway;
import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpnConnectionsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpnConnectionsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpnGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpnGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Route;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.VgwTelemetry;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;

public class VGWTest {

	public static void main(String[] args) {
		Ec2Client ec2Client = Ec2Client.builder().credentialsProvider(ProfileCredentialsProvider.builder().profileName("KEAPPMIG").build()).build();
		
		/*
		DescribeInstancesResponse describeInstancesResponse = ec2Client.describeInstances(DescribeInstancesRequest.builder().instanceIds("i-086c17d67bf0dfea1").build());
		
		Instance instance = describeInstancesResponse.reservations().get(0).instances().get(0);
		
		Filter subnetFilter = Filter.builder().name("association.subnet-id").values(instance.subnetId()).build();
		DescribeRouteTablesResponse describeRouteTablesResponse = ec2Client.describeRouteTables(DescribeRouteTablesRequest.builder().filters(subnetFilter).build());
		RouteTable routeTable = describeRouteTablesResponse.routeTables().get(0);
		List<Route> routes = routeTable.routes();
		for (Route route : routes) {
			System.out.println(route.gatewayId());			
		}
		*/
		/*
		
		*/
		
		/*
		DescribeDirectConnectGatewayAssociationsResponse describeDirectConnectGatewayAssociationsResponse = directConnectClient.describeDirectConnectGatewayAssociations(DescribeDirectConnectGatewayAssociationsRequest.builder().virtualGatewayId("vgw-0270f982a0cdf5eaf").build());
		for(DirectConnectGatewayAssociation directConnectGatewayAssociation : describeDirectConnectGatewayAssociationsResponse.directConnectGatewayAssociations()) {
			System.out.println(directConnectGatewayAssociation.associatedGateway().id());
			System.out.println(directConnectGatewayAssociation.directConnectGatewayId());
			System.out.println(directConnectGatewayAssociation.virtualGatewayId());
		}
		*/
		
		/*
		for(VirtualGateway virtualGateway : directConnectClient.describeVirtualGateways().virtualGateways()) {
			virtualGateway.
		}
		*/
		
		//directConnectClient.describeDirectConnectGatewayAttachments(DescribeDirectConnectGatewayAttachmentsRequest.builder().)
		
		DescribeVpnGatewaysResponse describeVpnGatewaysResponse = ec2Client.describeVpnGateways(DescribeVpnGatewaysRequest.builder().vpnGatewayIds("vgw-0270f982a0cdf5eaf").build());
		for(VpnGateway vpnGateway : describeVpnGatewaysResponse.vpnGateways()) {
			System.out.println(vpnGateway.type().name());
		}
		
		DescribeVpnConnectionsResponse describeVpnConnectionsResponse = ec2Client.describeVpnConnections(DescribeVpnConnectionsRequest.builder().filters(Filter.builder().name("vpn-gateway-id").values("vgw-0270f982a0cdf5eaf").build()).build());
		for(VpnConnection vpnConnection : describeVpnConnectionsResponse.vpnConnections()) {
			vpnConnection.customerGatewayId();
			for(VgwTelemetry vgwTelemetry : vpnConnection.vgwTelemetry()) {
				vgwTelemetry.outsideIpAddress();
			}
		}
		
		DirectConnectClient directConnectClient = DirectConnectClient.builder().credentialsProvider(ProfileCredentialsProvider.builder().profileName("KEAPPMIG").build()).build();
		
		List<VirtualInterface> virtualInterfaces = new ArrayList<>();
		DescribeVirtualInterfacesResponse describeVirtualInterfacesResponse = directConnectClient.describeVirtualInterfaces();
		for(VirtualInterface virtualInterface : describeVirtualInterfacesResponse.virtualInterfaces()) {	
			if("vgw-0270f982a0cdf5eaf".equals(virtualInterface.virtualGatewayId())) {
				virtualInterfaces.add(virtualInterface);
			}
		}
		
		for(VirtualInterface virtualInterface : virtualInterfaces) {
			virtualInterface.connectionId();
			virtualInterface.location();
			virtualInterface.virtualInterfaceId();
			virtualInterface.virtualInterfaceName();
			virtualInterface.virtualInterfaceType();
			virtualInterface.virtualInterfaceStateAsString();
		}
		
	}
}

package com.anthunt.aws.network.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.DirectConnectService;
import com.anthunt.aws.network.service.Ec2Service;
import com.anthunt.aws.network.service.LoadBalancerService;
import com.anthunt.aws.network.service.RdsService;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;
import software.amazon.awssdk.services.ec2.model.EgressOnlyInternetGateway;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InternetGateway;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ec2.model.PrefixList;
import software.amazon.awssdk.services.ec2.model.RouteTable;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;
import software.amazon.awssdk.services.ec2.model.VpnConnection;
import software.amazon.awssdk.services.ec2.model.VpnGateway;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Listener;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Rule;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBInstance;

public abstract class ServiceRepositoryProvider {

	protected ServiceRepositoryCollectListener serviceRepositoryCollectListener;
	
	public ServiceRepositoryProvider() {
		
	}
	
	protected abstract ServiceMap<Vpc> getVpcMap();

	protected abstract ServiceMap<Instance> getEc2InstanceMap();

	protected abstract ServiceMap<Volume> getVolumeMap();
	
	protected abstract ServiceMap<NetworkInterface> getNetworkInterfaceMap();
	
	protected abstract ServiceMap<Subnet> getSubnetMap();

	protected abstract ServiceMap<VpcPeeringConnection> getVpcPeeringMap();
	
	protected abstract ServiceMap<LoadBalancer> getLoadBalancerMap();

	protected abstract ServiceMap<List<Listener>> getLoadBalancerListenersMap();

	protected abstract ServiceMap<List<Rule>> getLoadBalancerRulesMap();

	protected abstract ServiceMap<TargetGroup> getTargetGroupMap();

	protected abstract ServiceMap<List<TargetHealthDescription>> getTargetHealthDescriptionsMap();

	protected abstract ServiceMap<LoadBalancerDescription> getClassicLoadBalancerMap();

	protected abstract ServiceMap<SecurityGroup> getSecurityGroupMap();

	protected abstract ServiceMap<List<RouteTable>> getRouteTablesMap();

	protected abstract ServiceMap<PrefixList> getPrefixListMap();

	protected abstract ServiceMap<CustomerGateway> getCustomerGatewayMap();
	
	protected abstract ServiceMap<VpnGateway> getVpnGatewayMap();

	protected abstract ServiceMap<List<VpnConnection>> getVpnConnectionsMap();

	protected abstract ServiceMap<List<VirtualInterface>> getVirtualInterfacesMap();

	protected abstract ServiceMap<List<NetworkAcl>> getNetworkAclsMap();
	
	protected abstract ServiceMap<VpcEndpoint> getVpcEndpointMap();
	
	protected abstract ServiceMap<EgressOnlyInternetGateway> getEgressInternetGatewayMap();
	
	protected abstract ServiceMap<InternetGateway> getInternetGatewayMap();
	
	protected abstract ServiceMap<TransitGateway> getTransitGatewayMap();
	
	protected abstract ServiceMap<DBInstance> getRdsInstanceMap();

	protected abstract ServiceMap<DBCluster> getRdsClusterMap();
	
	protected abstract void setVirtualInterfacesMap(ServiceMap<List<VirtualInterface>> serviceMap);

	protected abstract void setTargetHealthDescriptionsMap(ServiceMap<List<TargetHealthDescription>> serviceMap);

	protected abstract ServiceMap<TargetGroup> setTargetGroupMap(ServiceMap<TargetGroup> serviceMap);

	protected abstract void setLoadBalancerRulesMap(ServiceMap<List<Rule>> serviceMap);

	protected abstract ServiceMap<List<Listener>> setLoadBalancerListenersMap(ServiceMap<List<Listener>> serviceMap);

	protected abstract ServiceMap<LoadBalancer> setLoadBalancerMap(ServiceMap<LoadBalancer> serviceMap);

	protected abstract void setClassicLoadBalancerMap(ServiceMap<LoadBalancerDescription> serviceMap);

	protected abstract void setVpnConnectionsMap(ServiceMap<List<VpnConnection>> serviceMap);

	protected abstract void setVpnGatewayMap(ServiceMap<VpnGateway> serviceMap);

	protected abstract void setNetworkAclsMap(ServiceMap<List<NetworkAcl>> serviceMap);

	protected abstract void setPrefixListMap(ServiceMap<PrefixList> serviceMap);

	protected abstract void setRouteTablesMap(ServiceMap<List<RouteTable>> serviceMap);

	protected abstract void setVpcPeeringMap(ServiceMap<VpcPeeringConnection> serviceMap);
	
	protected abstract void setCustomerGatewayMap(ServiceMap<CustomerGateway> serviceMap);
	
	protected abstract ServiceMap<Subnet> setSubnetMap(ServiceMap<Subnet> serviceMap);

	protected abstract void setSecurityGroupMap(ServiceMap<SecurityGroup> serviceMap);

	protected abstract void setEc2InstanceMap(ServiceMap<Instance> serviceMap);
	
	protected abstract void setVolumeMap(ServiceMap<Volume> serviceMap);
	
	protected abstract void setNetworkInterfaceMap(ServiceMap<NetworkInterface> serviceMap);
	
	protected abstract void setVpcMap(ServiceMap<Vpc> serviceMap);
	
	protected abstract void setVpcEndpointMap(ServiceMap<VpcEndpoint> serviceMap);

	protected abstract void setEgressInternetGatewayMap(ServiceMap<EgressOnlyInternetGateway> serviceMap);

	protected abstract void setInternetGatewayMap(ServiceMap<InternetGateway> serviceMap);
	
	protected abstract void setTransitGatewayMap(ServiceMap<TransitGateway> serviceMap);
	
	protected abstract void setRdsInstanceMap(ServiceMap<DBInstance> rdsInstances);

	protected abstract void setRdsClusterMap(ServiceMap<DBCluster> rdsClusters);
	
	public void setServiceRepositoryCollectListener(ServiceRepositoryCollectListener serviceRepositoryCollectListener) {
		this.serviceRepositoryCollectListener = serviceRepositoryCollectListener;
	}
	
	public int ec2Sync(int num, int total, SessionProfile sessionProfile, Ec2Service ec2Service) throws IOException {
		this.setVpcMap(ec2Service.getVpcs(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Vpc data is loaded");
		num++;
		Utils.sleep(100);
		this.setEc2InstanceMap(ec2Service.getInstances(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Ec2 instances data is loaded");
		num++;
		Utils.sleep(100);
		this.setVolumeMap(ec2Service.getVolumes(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "EBS volumes data is loaded");
		num++;
		Utils.sleep(100);
		this.setNetworkInterfaceMap(ec2Service.getNetworkInterfaces(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Network Interfaces data is loaded");
		num++;
		Utils.sleep(100);
		this.setSecurityGroupMap(ec2Service.getSecurityGroups(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Securitygroups data is loaded");
		num++;
		Utils.sleep(100);
		ServiceMap<Subnet> subnetMap = this.setSubnetMap(ec2Service.getSubnets(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Subnets data is loaded");
		num++;
		Utils.sleep(100);
		this.setRouteTablesMap(ec2Service.getRouteTables(sessionProfile, subnetMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Routes data is loaded");
		num++;
		Utils.sleep(100);
		this.setInternetGatewayMap(ec2Service.getInternetGateways(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Internet Gateways data is loaded");
		num++;
		Utils.sleep(100);
		this.setEgressInternetGatewayMap(ec2Service.getEgressInternetGateways(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Egress Internet Gateways data is loaded");
		num++;
		Utils.sleep(100);
		this.setVpcEndpointMap(ec2Service.getVpcEndpoints(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Vpc Endpoints data is loaded");
		num++;
		Utils.sleep(100);
		this.setVpcPeeringMap(ec2Service.getVpcPeerings(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Vpc Peering data is loaded");
		num++;
		Utils.sleep(100);
		this.setTransitGatewayMap(ec2Service.getTransitGateways(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Trangit Gateways data is loaded");
		num++;
		Utils.sleep(100);
		this.setPrefixListMap(ec2Service.getPrefixLists(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "PrefixLists data is loaded");
		num++;
		Utils.sleep(100);
		this.setNetworkAclsMap(ec2Service.getNetworkAcls(sessionProfile, subnetMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "PrefixLists data is loaded");
		num++;
		Utils.sleep(100);
		this.setCustomerGatewayMap(ec2Service.getCustomerGateways(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "CustomerGateways data is loaded");
		num++;
		Utils.sleep(100);
		this.setVpnGatewayMap(ec2Service.getVpnGateways(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "VpnGateways data is loaded");
		num++;		
		Utils.sleep(100);	
		this.setVpnConnectionsMap(ec2Service.getVpnConnections(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "VpnConnections data is loaded");
		num++;
		Utils.sleep(100);
		return num;		
	}

	public int elbSync(int num, int total, SessionProfile sessionProfile, LoadBalancerService loadBalancerService) throws IOException {
		this.setClassicLoadBalancerMap(loadBalancerService.getClassicLoadBalancers(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Classic loadbalancers data is loaded");
		num++;
		Utils.sleep(100);
		ServiceMap<LoadBalancer> loadBalancerMap = this.setLoadBalancerMap(loadBalancerService.getLoadBalancers(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancers data is loaded");
		num++;
		Utils.sleep(100);
		ServiceMap<List<Listener>> listenersMap = this.setLoadBalancerListenersMap(loadBalancerService.getLoadBalancerListeners(sessionProfile, loadBalancerMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer listeners data is loaded");
		num++;
		Utils.sleep(100);
		this.setLoadBalancerRulesMap(loadBalancerService.getLoadBalancerRules(sessionProfile, listenersMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer rules data is loaded");
		num++;
		Utils.sleep(100);
		ServiceMap<TargetGroup> targetGroupMap = this.setTargetGroupMap(loadBalancerService.getTargetGroups(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer targetgroups data is loaded");
		num++;
		Utils.sleep(100);
		this.setTargetHealthDescriptionsMap(loadBalancerService.getTargetHealthDescriptions(sessionProfile, targetGroupMap.values()));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer target healths data is loaded");
		num++;
		Utils.sleep(100);
		return num;
	}
	
	public int rdsSync(int num, int total, SessionProfile sessionProfile, RdsService rdsService) throws IOException {
		this.setRdsClusterMap(rdsService.getRdsClusters(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Classic loadbalancers data is loaded");
		num++;
		Utils.sleep(100);
		
		this.setRdsInstanceMap(rdsService.getRdsInstances(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Classic loadbalancers data is loaded");
		num++;
		Utils.sleep(100);
		return num;
	}

	public int dxSync(int num, int total, SessionProfile sessionProfile, DirectConnectService directConnectService) throws IOException {
		this.setVirtualInterfacesMap(directConnectService.getVirtualInterfaces(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Virtual Interfaces healths data is loaded");
		num++;
		Utils.sleep(100);
		return num;
	}

	public List<ServiceStatistic> collect() {
		List<ServiceStatistic> serviceStatistics = new ArrayList<>();
		serviceStatistics.add(this.getVpcMap().getServiceStatistic(ServiceType.VPC));
		serviceStatistics.add(this.getSubnetMap().getServiceStatistic(ServiceType.PRSB));
		serviceStatistics.add(this.getNetworkAclsMap().getServiceStatistic(ServiceType.NACL));
		serviceStatistics.add(this.getRouteTablesMap().getServiceStatistic(ServiceType.RT));
		serviceStatistics.add(this.getSecurityGroupMap().getServiceStatistic(ServiceType.SG));
		serviceStatistics.add(this.getVpcEndpointMap().getServiceStatistic(ServiceType.VND));
		serviceStatistics.add(this.getInternetGatewayMap().getServiceStatistic(ServiceType.IGW));
		serviceStatistics.add(this.getEgressInternetGatewayMap().getServiceStatistic(ServiceType.EGW));
		serviceStatistics.add(this.getTransitGatewayMap().getServiceStatistic(ServiceType.TGW));
		serviceStatistics.add(this.getVpcPeeringMap().getServiceStatistic(ServiceType.PEERING));
		serviceStatistics.add(this.getVpnGatewayMap().getServiceStatistic(ServiceType.VGW));
		serviceStatistics.add(this.getCustomerGatewayMap().getServiceStatistic(ServiceType.CGW));
		serviceStatistics.add(this.getVpnConnectionsMap().getServiceStatistic(ServiceType.VPNC));
		serviceStatistics.add(this.getVirtualInterfacesMap().getServiceStatistic(ServiceType.VIF));
		serviceStatistics.add(this.getEc2InstanceMap().getServiceStatistic(ServiceType.EC2));
		serviceStatistics.add(this.getVolumeMap().getServiceStatistic(ServiceType.EBS));
		serviceStatistics.add(this.getNetworkInterfaceMap().getServiceStatistic(ServiceType.ENI));
		serviceStatistics.add(this.getLoadBalancerMap().getServiceStatistic(ServiceType.ELB));
		serviceStatistics.add(this.getRdsClusterMap().getServiceStatistic(ServiceType.AURORA));
		serviceStatistics.add(this.getRdsInstanceMap().getServiceStatistic(ServiceType.RDS));
		return serviceStatistics;
	}
	
}

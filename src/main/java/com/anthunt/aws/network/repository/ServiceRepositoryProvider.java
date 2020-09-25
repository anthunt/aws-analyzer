package com.anthunt.aws.network.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceStatistic;
import com.anthunt.aws.network.service.aws.DirectConnectService;
import com.anthunt.aws.network.service.aws.Ec2Service;
import com.anthunt.aws.network.service.aws.LoadBalancerService;
import com.anthunt.aws.network.service.aws.RdsService;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.elasticloadbalancing.model.LoadBalancerDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.opsworks.model.RdsDbInstance;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBInstance;

import javax.websocket.Session;

public abstract class ServiceRepositoryProvider {

	protected ServiceRepositoryCollectListener serviceRepositoryCollectListener;
	
	public ServiceRepositoryProvider() {
		
	}
	
	protected abstract ServiceMap getVpcMap();

	protected abstract ServiceMap getEc2InstanceMap();

	protected abstract ServiceMap getVolumeMap();
	
	protected abstract ServiceMap getNetworkInterfaceMap();
	
	protected abstract ServiceMap getSubnetMap();

	protected abstract ServiceMap getVpcPeeringMap();
	
	protected abstract ServiceMap getLoadBalancerMap();

	protected abstract ServiceMap getLoadBalancerListenersMap();

	protected abstract ServiceMap getLoadBalancerRulesMap();

	protected abstract ServiceMap getTargetGroupMap();

	protected abstract ServiceMap getTargetHealthDescriptionsMap();

	protected abstract ServiceMap getClassicLoadBalancerMap();

	protected abstract ServiceMap getSecurityGroupMap();

	protected abstract ServiceMap getRouteTablesMap();

	protected abstract ServiceMap getPrefixListMap();

	protected abstract ServiceMap getCustomerGatewayMap();
	
	protected abstract ServiceMap getVpnGatewayMap();

	protected abstract ServiceMap getVpnConnectionsMap();

	protected abstract ServiceMap getVirtualInterfacesMap();

	protected abstract ServiceMap getNetworkAclsMap();
	
	protected abstract ServiceMap getVpcEndpointMap();
	
	protected abstract ServiceMap getEgressInternetGatewayMap();
	
	protected abstract ServiceMap getInternetGatewayMap();
	
	protected abstract ServiceMap getTransitGatewayMap();
	
	protected abstract ServiceMap getRdsInstanceMap();

	protected abstract ServiceMap getRdsClusterMap();
	
	protected abstract void setVirtualInterfacesMap(ServiceMap serviceMap);

	protected abstract void setTargetHealthDescriptionsMap(ServiceMap serviceMap);

	protected abstract ServiceMap setTargetGroupMap(ServiceMap serviceMap);

	protected abstract void setLoadBalancerRulesMap(ServiceMap serviceMap);

	protected abstract ServiceMap setLoadBalancerListenersMap(ServiceMap serviceMap);

	protected abstract ServiceMap setLoadBalancerMap(ServiceMap serviceMap);

	protected abstract void setClassicLoadBalancerMap(ServiceMap serviceMap);

	protected abstract void setVpnConnectionsMap(ServiceMap serviceMap);

	protected abstract void setVpnGatewayMap(ServiceMap serviceMap);

	protected abstract void setNetworkAclsMap(ServiceMap serviceMap);

	protected abstract void setPrefixListMap(ServiceMap serviceMap);

	protected abstract void setRouteTablesMap(ServiceMap serviceMap);

	protected abstract void setVpcPeeringMap(ServiceMap serviceMap);
	
	protected abstract void setCustomerGatewayMap(ServiceMap serviceMap);
	
	protected abstract ServiceMap setSubnetMap(ServiceMap serviceMap);

	protected abstract void setSecurityGroupMap(ServiceMap serviceMap);

	protected abstract void setEc2InstanceMap(ServiceMap serviceMap);
	
	protected abstract void setVolumeMap(ServiceMap serviceMap);
	
	protected abstract void setNetworkInterfaceMap(ServiceMap serviceMap);
	
	protected abstract void setVpcMap(ServiceMap serviceMap);
	
	protected abstract void setVpcEndpointMap(ServiceMap serviceMap);

	protected abstract void setEgressInternetGatewayMap(ServiceMap serviceMap);

	protected abstract void setInternetGatewayMap(ServiceMap serviceMap);
	
	protected abstract void setTransitGatewayMap(ServiceMap serviceMap);
	
	protected abstract void setRdsInstanceMap(ServiceMap rdsInstances);

	protected abstract void setRdsClusterMap(ServiceMap rdsClusters);
	
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
		ServiceMap subnetMap = this.setSubnetMap(ec2Service.getSubnets(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "Subnets data is loaded");
		num++;
		Utils.sleep(100);
		this.setRouteTablesMap(ec2Service.getRouteTables(sessionProfile, subnetMap.values(sessionProfile, Subnet.class)));
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
		this.setNetworkAclsMap(ec2Service.getNetworkAcls(sessionProfile, subnetMap.values(sessionProfile, Subnet.class)));
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
		ServiceMap loadBalancerMap = this.setLoadBalancerMap(loadBalancerService.getLoadBalancers(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancers data is loaded");
		num++;
		Utils.sleep(100);
		ServiceMap listenersMap = this.setLoadBalancerListenersMap(loadBalancerService.getLoadBalancerListeners(sessionProfile, loadBalancerMap.values(sessionProfile, LoadBalancer.class)));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer listeners data is loaded");
		num++;
		Utils.sleep(100);
		this.setLoadBalancerRulesMap(loadBalancerService.getLoadBalancerRules(sessionProfile, listenersMap.values(sessionProfile, Listener.class)));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer rules data is loaded");
		num++;
		Utils.sleep(100);
		ServiceMap targetGroupMap = this.setTargetGroupMap(loadBalancerService.getTargetGroups(sessionProfile));
		this.serviceRepositoryCollectListener.serviceLoaded(num, total, "LoadBalancer targetgroups data is loaded");
		num++;
		Utils.sleep(100);
		this.setTargetHealthDescriptionsMap(loadBalancerService.getTargetHealthDescriptions(sessionProfile, targetGroupMap.values(sessionProfile, TargetGroup.class)));
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

	public List<ServiceStatistic> collect(SessionProfile sessionProfile) {
		List<ServiceStatistic> serviceStatistics = new ArrayList<>();
		serviceStatistics.add(this.getVpcMap().getServiceStatistic(sessionProfile, ServiceType.VPC));
		serviceStatistics.add(this.getSubnetMap().getServiceStatistic(sessionProfile, ServiceType.PRSB));
		serviceStatistics.add(this.getNetworkAclsMap().getServiceStatistic(sessionProfile, ServiceType.NACL));
		serviceStatistics.add(this.getRouteTablesMap().getServiceStatistic(sessionProfile, ServiceType.RT));
		serviceStatistics.add(this.getSecurityGroupMap().getServiceStatistic(sessionProfile, ServiceType.SG));
		serviceStatistics.add(this.getVpcEndpointMap().getServiceStatistic(sessionProfile, ServiceType.VND));
		serviceStatistics.add(this.getInternetGatewayMap().getServiceStatistic(sessionProfile, ServiceType.IGW));
		serviceStatistics.add(this.getEgressInternetGatewayMap().getServiceStatistic(sessionProfile, ServiceType.EGW));
		serviceStatistics.add(this.getTransitGatewayMap().getServiceStatistic(sessionProfile, ServiceType.TGW));
		serviceStatistics.add(this.getVpcPeeringMap().getServiceStatistic(sessionProfile, ServiceType.PEERING));
		serviceStatistics.add(this.getVpnGatewayMap().getServiceStatistic(sessionProfile, ServiceType.VGW));
		serviceStatistics.add(this.getCustomerGatewayMap().getServiceStatistic(sessionProfile, ServiceType.CGW));
		serviceStatistics.add(this.getVpnConnectionsMap().getServiceStatistic(sessionProfile, ServiceType.VPNC));
		serviceStatistics.add(this.getVirtualInterfacesMap().getServiceStatistic(sessionProfile, ServiceType.VIF));
		serviceStatistics.add(this.getEc2InstanceMap().getServiceStatistic(sessionProfile, ServiceType.EC2));
		serviceStatistics.add(this.getVolumeMap().getServiceStatistic(sessionProfile, ServiceType.EBS));
		serviceStatistics.add(this.getNetworkInterfaceMap().getServiceStatistic(sessionProfile, ServiceType.ENI));
		serviceStatistics.add(this.getLoadBalancerMap().getServiceStatistic(sessionProfile, ServiceType.ELB));
		serviceStatistics.add(this.getRdsClusterMap().getServiceStatistic(sessionProfile, ServiceType.AURORA));
		serviceStatistics.add(this.getRdsInstanceMap().getServiceStatistic(sessionProfile, ServiceType.RDS));
		return serviceStatistics;
	}
	
}

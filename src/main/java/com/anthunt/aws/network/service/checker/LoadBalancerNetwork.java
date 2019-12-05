package com.anthunt.aws.network.service.checker;

import java.util.ArrayList;
import java.util.List;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.service.model.checker.CheckResults;

import software.amazon.awssdk.services.elasticloadbalancingv2.model.AvailabilityZone;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancerTypeEnum;

public class LoadBalancerNetwork extends AbstractNetwork<LoadBalancer> {

	private LoadBalancer loadBalancer; 
	
	public LoadBalancerNetwork(String loadBalncerArn, ServiceRepository serviceRepository) {
		super(loadBalncerArn, serviceRepository);
	}

	@Override
	public CheckResults<LoadBalancer> checkCommunication(String cidr) {
		return super.checkCommunication(cidr);
	}

	@Override
	protected LoadBalancer getResource(String loadBalancerArn, ServiceRepository serviceRepository) {		
		this.loadBalancer = serviceRepository.getLoadBalancerMap().get(loadBalancerArn);
		return this.loadBalancer;
	}

	@Override
	protected String getVpcId() {
		return this.loadBalancer.vpcId();
	}

	@Override
	protected List<String> getSubnetIds() {
		
		List<String> subnetIds = new ArrayList<>();
		List<AvailabilityZone> availabilityZones = this.loadBalancer.availabilityZones();
		for (AvailabilityZone availabilityZone : availabilityZones) {
			subnetIds.add(availabilityZone.subnetId());
		}
		return subnetIds;
	}

	@Override
	protected List<String> getSecurityGroupIds() {
		return this.loadBalancer.type() == LoadBalancerTypeEnum.APPLICATION ? this.loadBalancer.securityGroups() : new ArrayList<>();
	}
	
}

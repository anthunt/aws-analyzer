package com.anthunt.aws.network.service.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.aws.AwsData;
import com.anthunt.aws.network.service.model.checker.CheckResults;

import com.anthunt.aws.network.session.SessionProfile;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.AvailabilityZone;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancerTypeEnum;

public class LoadBalancerNetwork extends AbstractNetwork<LoadBalancer> {

	private LoadBalancer loadBalancer; 
	
	public LoadBalancerNetwork(SessionProfile sessionProfile, String loadBalncerArn, ServiceRepository serviceRepository) {
		super(sessionProfile, loadBalncerArn, serviceRepository);
	}

	@Override
	public CheckResults<LoadBalancer> checkCommunication(String cidr) {
		return super.checkCommunication(cidr);
	}

	@Override
	protected LoadBalancer getResource(SessionProfile sessionProfile, String loadBalancerArn, ServiceRepository serviceRepository) {
		serviceRepository.getLoadBalancerMap()
			.get(sessionProfile, loadBalancerArn, LoadBalancer.class)
			.ifPresent(new Consumer<AwsData>() {
				@Override
				public void accept(AwsData awsData) {
					loadBalancer = awsData.getData();	
				}
			});
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

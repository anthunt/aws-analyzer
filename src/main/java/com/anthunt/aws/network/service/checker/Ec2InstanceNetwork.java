package com.anthunt.aws.network.service.checker;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.ec2.model.GroupIdentifier;
import software.amazon.awssdk.services.ec2.model.Instance;

public class Ec2InstanceNetwork extends AbstractNetwork<Instance> {

	private Instance instance;
	
	public Ec2InstanceNetwork(String instanceId, ServiceRepository serviceRepository) {
		super(instanceId, serviceRepository);
	}

	@Override
	protected Instance getResource(String instanceId, ServiceRepository serviceRepository) {		
		this.instance = serviceRepository.getEc2InstanceMap().get(instanceId);
		return this.instance;
	}

	@Override
	protected String getVpcId() {
		return this.instance.vpcId();
	}

	@Override
	protected List<String> getSubnetIds() {
		List<String> subnetIds = new ArrayList<>();
		subnetIds.add(this.instance.subnetId());
		return subnetIds;
	}

	@Override
	protected List<String> getSecurityGroupIds() {
		List<String> securityGroupIds = new ArrayList<>();
		List<GroupIdentifier> groupIdentifiers = this.instance.securityGroups();
		for (GroupIdentifier groupIdentifier : groupIdentifiers) {
			securityGroupIds.add(groupIdentifier.groupId());
		}
		return securityGroupIds;
	}

}

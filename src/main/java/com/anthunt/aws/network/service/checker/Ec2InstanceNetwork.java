package com.anthunt.aws.network.service.checker;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.aws.AwsData;

import com.anthunt.aws.network.session.SessionProfile;
import software.amazon.awssdk.services.ec2.model.GroupIdentifier;
import software.amazon.awssdk.services.ec2.model.Instance;

public class Ec2InstanceNetwork extends AbstractNetwork<Instance> {

	private Instance instance;
	
	public Ec2InstanceNetwork(SessionProfile sessionProfile, String instanceId, ServiceRepository serviceRepository) {
		super(sessionProfile, instanceId, serviceRepository);
	}

	@Override
	protected Instance getResource(SessionProfile sessionProfile, String instanceId, ServiceRepository serviceRepository) {
		serviceRepository.getEc2InstanceMap()
			.get(sessionProfile, instanceId, Instance.class)
			.ifPresent(new Consumer<AwsData>() {
				@Override
				public void accept(AwsData awsData) {
					instance = awsData.getData();	
				}
			});
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

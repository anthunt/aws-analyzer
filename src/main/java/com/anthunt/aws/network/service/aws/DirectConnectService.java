package com.anthunt.aws.network.service.aws;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.directconnect.DirectConnectAsyncClient;
import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.directconnect.model.VirtualInterfaceState;

@Service
public class DirectConnectService {
	
	public DirectConnectAsyncClient getDirectConnectClient(SessionProfile sessionProfile) {
		return this.getDirectConnectClient(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	public DirectConnectAsyncClient getDirectConnectClient(String profileName, String regionId) {
		return DirectConnectAsyncClient.builder()
				   .credentialsProvider(ProfileCredentialsProvider.create(profileName))
				   .region(Region.of(regionId))
				   .build();
	}

	public ServiceMap getVirtualInterfaces(SessionProfile sessionProfile) {
		ServiceMap virtualInterfaceMap = sessionProfile.serviceMap(true);
		DirectConnectAsyncClient directConnectClient = this.getDirectConnectClient(sessionProfile);
		int active = 0;
		for(VirtualInterface virtualInterface : directConnectClient.describeVirtualInterfaces().join().virtualInterfaces()) {
			if(virtualInterfaceMap.containsKey(sessionProfile, virtualInterface.virtualGatewayId(), VirtualInterface.class)) {
				virtualInterfaceMap.get(sessionProfile, virtualInterface.virtualGatewayId(), VirtualInterface.class).get().getDataList().add(virtualInterface);
			} else {
				List<VirtualInterface> virtualInterfaces = new ArrayList<>();
				virtualInterfaces.add(virtualInterface);
				virtualInterfaceMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), virtualInterface.virtualGatewayId(), virtualInterfaces, VirtualInterface.class);
			}
			if(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE) active++;
		}
		virtualInterfaceMap.setActive(active);
		return virtualInterfaceMap;
	}
	
}

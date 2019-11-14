package com.anthunt.aws.network.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directconnect.model.VirtualInterface;
import software.amazon.awssdk.services.directconnect.model.VirtualInterfaceState;

@Service
public class DirectConnectService {
	
	public DirectConnectClient getDirectConnectClient(SessionProfile sessionProfile) {
		return this.getDirectConnectClient(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	public DirectConnectClient getDirectConnectClient(String profileName, String regionId) {
		return DirectConnectClient.builder()
				   .credentialsProvider(ProfileCredentialsProvider.create(profileName))
				   .region(Region.of(regionId))
				   .build();
	}

	public ServiceMap<List<VirtualInterface>> getVirtualInterfaces(SessionProfile sessionProfile) {
		ServiceMap<List<VirtualInterface>> virtualInterfaceMap = new ServiceMap<>();
		DirectConnectClient directConnectClient = this.getDirectConnectClient(sessionProfile);
		int active = 0;
		for(VirtualInterface virtualInterface : directConnectClient.describeVirtualInterfaces().virtualInterfaces()) {
			if(virtualInterfaceMap.containsKey(virtualInterface.virtualGatewayId())) {
				virtualInterfaceMap.get(virtualInterface.virtualGatewayId()).add(virtualInterface);
			} else {
				if(virtualInterface.virtualInterfaceState() == VirtualInterfaceState.AVAILABLE) active++;
				List<VirtualInterface> virtualInterfaces = new ArrayList<>();
				virtualInterfaces.add(virtualInterface);
				virtualInterfaceMap.put(virtualInterface.virtualGatewayId(), virtualInterfaces);
			}
		}
		virtualInterfaceMap.setActive(active);
		return virtualInterfaceMap;
	}
	
}

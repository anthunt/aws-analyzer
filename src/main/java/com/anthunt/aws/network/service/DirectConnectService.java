package com.anthunt.aws.network.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.directconnect.DirectConnectClient;
import software.amazon.awssdk.services.directconnect.model.VirtualInterface;

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

	public Map<String, List<VirtualInterface>> getVirtualInterfaces(SessionProfile sessionProfile) {
		Map<String, List<VirtualInterface>> virtualInterfaceMap = new HashMap<>();
		DirectConnectClient directConnectClient = this.getDirectConnectClient(sessionProfile);
		for(VirtualInterface virtualInterface : directConnectClient.describeVirtualInterfaces().virtualInterfaces()) {
			if(virtualInterfaceMap.containsKey(virtualInterface.virtualGatewayId())) {
				virtualInterfaceMap.get(virtualInterface.virtualGatewayId()).add(virtualInterface);
			} else {
				List<VirtualInterface> virtualInterfaces = new ArrayList<>();
				virtualInterfaces.add(virtualInterface);
				virtualInterfaceMap.put(virtualInterface.virtualGatewayId(), virtualInterfaces);
			}
		}
		return virtualInterfaceMap;
	}
	
}

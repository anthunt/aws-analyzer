package com.anthunt.aws.network.service.aws;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.ServiceRepository;
import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.service.model.diagram.DiagramResult;
import com.anthunt.aws.network.session.SessionProfile;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsAsyncClient;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rds.model.DBInstance;

@Service
public class RdsService extends AbstractNetworkService {

	public RdsAsyncClient getRdsClient(SessionProfile sessionProfile) {
		return this.getRdsClient(sessionProfile.getProfileName(), sessionProfile.getRegionId());
	}
	
	public RdsAsyncClient getRdsClient(String profileName, String regionId) {
		return RdsAsyncClient.builder()
					.credentialsProvider(ProfileCredentialsProvider.create(profileName))
					.region(Region.of(regionId))
					.build();
	}
	
	public ServiceMap getRdsClusters(SessionProfile sessionProfile) {
		ServiceMap dbClusterMap = sessionProfile.serviceMap();
		RdsAsyncClient rdsClient = this.getRdsClient(sessionProfile);
		int active = 0;
		for(DBCluster dbCluster : rdsClient.describeDBClusters().join().dbClusters()) {
			dbClusterMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), dbCluster.dbClusterIdentifier(), dbCluster, DBCluster.class);
			if("available".equals(dbCluster.status())) {
				active++;
			}
		}
		dbClusterMap.setActive(active);
		return dbClusterMap;
	}
	
	public ServiceMap getRdsInstances(SessionProfile sessionProfile) {
		ServiceMap dbInstanceMap = sessionProfile.serviceMap();
		RdsAsyncClient rdsClient = this.getRdsClient(sessionProfile);
		int active = 0;
		for(DBInstance dbInstance : rdsClient.describeDBInstances().join().dbInstances()) {
			dbInstanceMap.put(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegion(), dbInstance.dbInstanceIdentifier(), dbInstance, DBInstance.class);
			if("available".equals(dbInstance.dbInstanceStatus())) {
				active++;
			}
		}
		dbInstanceMap.setActive(active);
		return dbInstanceMap;
	}
	
	@Override
	protected DiagramResult getNetwork(SessionProfile sessionProfile, ServiceRepository serviceRepository, String instanceId, String targetIp) {
		// TODO Auto-generated method stub
		return null;
	}

}

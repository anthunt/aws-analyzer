package com.anthunt.aws.network.session;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.anthunt.aws.network.repository.model.ServiceMap;
import com.anthunt.aws.network.repository.model.ServiceMapType;
import com.anthunt.aws.network.repository.profile.model.ProfileContents;
import com.anthunt.aws.network.repository.user.model.UserDetails;

import lombok.Data;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFile.Aggregator;
import software.amazon.awssdk.regions.Region;

@Data
public class SessionProfile {

	private ProfileFile profileFile;
	private ProfileContents profileContents;
	private String profileName;
	private String regionId;
	private UserDetails userDetails;
	private ServiceMapType serviceMapType;
	
	public SessionProfile(UserDetails userDetails, ServiceMapType serviceMapType) {
		this.setUserDetails(userDetails);
		this.setServiceMapType(serviceMapType);
	}
	
	public ServiceMap serviceMap(boolean hasList) {
		return this.getServiceMapType().serviceMap(hasList);
	}
	
	public ServiceMap serviceMap() {
		return this.getServiceMapType().serviceMap();
	}
	
	public boolean isSelected() {
		return this.getProfileName() != null;
	}
	
	public ObjectId getUserid() {
		return this.getUserDetails().getUser().getUserid();
	}
	
	private ProfileFile getProfileFile() {
		return this.profileFile;
	}
	
	public Map<String, Profile> getProfiles() {
	    return this.profileFile == null ? new HashMap<>() : this.profileFile.profiles();
	}
	
	public void setProfileFile(ProfileContents profileContents) {
		this.profileContents = profileContents;
		
		Aggregator aggregator = ProfileFile.aggregator();
		
		if(this.profileContents.hasCredentials()) {
			aggregator.addFile(this.profileContents.getCredentialsProfileFile());
		}
		
		if(this.profileContents.hasConfig()) {
			aggregator.addFile(this.profileContents.getConfigProfileFile());
		}
		
		this.profileFile = aggregator.build();
	}
	
	public ProfileCredentialsProvider getProfileCredentialsProvider() {
		return ProfileCredentialsProvider.builder()
				.profileFile(this.getProfileFile())
				.profileName(this.getProfileName())
				.build();
	}
	
	public ProfileContents getProfileContents() {
		return this.profileContents == null ? new ProfileContents(this.getUserid()) : this.profileContents;
	}
	
	public String getProfileName() {
		return profileName;
	}
	
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public String getRegionId() {
		return regionId;
	}

	public Region getRegion() {
		return Region.of(this.getRegionId());
	}
	
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	
	public String toString() {
		return new StringBuilder()
				.append("SessionProfile : {")
				.append("profileName :").append(this.getProfileName()).append(", ")
				.append("RegionId : ").append(this.getRegionId())
				.append("}").toString();
	}
	
}

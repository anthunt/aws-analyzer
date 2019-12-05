package com.anthunt.aws.network.session;

import java.io.ByteArrayInputStream;
import java.util.Map;

import com.anthunt.aws.network.controller.model.ProfileContents;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;

public class SessionProfile {

	private ProfileFile profileFile;
	private ProfileContents profileContents;
	private String profileName;
	private String regionId;
	
	public SessionProfile() {
	}
	
	private ProfileFile getProfileFile() {
		return this.profileFile;
	}
	
	public Map<String, Profile> getProfiles() {
	    return this.profileFile.profiles();
	}
	
	public void setProfileFile(ProfileContents profileContents) {
		this.profileContents = profileContents;
		this.profileFile = ProfileFile.aggregator()
				.addFile(
						ProfileFile.builder()
							.content(new ByteArrayInputStream(this.profileContents.getCredentialsBytes()))
							.type(ProfileFile.Type.CREDENTIALS)
							.build()
				)
				.addFile(
						ProfileFile.builder()
							.content(new ByteArrayInputStream(this.profileContents.getConfigBytes()))
							.type(ProfileFile.Type.CONFIGURATION)
							.build()
				)
				.build();
	}
	
	public ProfileCredentialsProvider getProfileCredentialsProvider() {
		return ProfileCredentialsProvider.builder()
				.profileFile(this.getProfileFile())
				.profileName(this.getProfileName())
				.build();
	}
	
	public ProfileContents getProfileContents() {
		return this.profileContents;
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

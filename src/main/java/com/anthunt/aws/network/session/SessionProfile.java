package com.anthunt.aws.network.session;

public class SessionProfile {

	private String profileName;
	private String regionId;
	
	public String getProfileName() {
		return profileName;
	}
	
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public String getRegionId() {
		return regionId;
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

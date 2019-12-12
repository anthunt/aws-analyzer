package com.anthunt.aws.network.repository.profile.model;

import java.io.ByteArrayInputStream;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.anthunt.aws.network.utils.Utils;

import lombok.Data;
import software.amazon.awssdk.profiles.ProfileFile;

@Data
@Document
public class ProfileContents {

	@Id
	private ObjectId userid;
	private String credentials;
	private String config;
		
	public ProfileContents(ObjectId userid) {
		this.setUserid(userid);
	}
	
	public boolean hasCredentials() {
		return this.getCredentials() == null ? false : true;
	}
	
	public boolean hasConfig() {
		return this.getConfig() == null ? false : true;
	}
	
	public String getCredentialsDecrypt() {
		return Utils.decrypt(this.getUserid().toString(), this.credentials);
	}
	
	public void setCredentials(String credentials) {
		this.credentials = Utils.encrypt(this.getUserid().toString(), credentials);
	}
	
	public String getConfigDecrypt() {
		return Utils.decrypt(this.getUserid().toString(), this.config);
	}
	
	public void setConfig(String config) {
		this.config = Utils.encrypt(this.getUserid().toString(), config);
	}
	
	public byte[] getCredentialsBytes() {
		return this.getCredentialsDecrypt().getBytes();
	}
	
	public ProfileFile getCredentialsProfileFile() {
		return ProfileFile.builder()
			.content(new ByteArrayInputStream(this.getCredentialsBytes()))
			.type(ProfileFile.Type.CREDENTIALS)
			.build();
	}
	
	public byte[] getConfigBytes() {
		return this.getConfigDecrypt().getBytes();
	}
	
	public ProfileFile getConfigProfileFile() {
		return ProfileFile.builder()
				.content(new ByteArrayInputStream(this.getConfigBytes()))
				.type(ProfileFile.Type.CONFIGURATION)
				.build();
	}
	
}

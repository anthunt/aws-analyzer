package com.anthunt.aws.network.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.anthunt.aws.network.controller.model.ProfileContents;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.utils.Utils;

import software.amazon.awssdk.profiles.ProfileFileLocation;

@Service
public class ProfileService {
	
	public Map<String, String> getCredentialFileContent() throws IOException {
		
		Map<String, String> configFileMap = new HashMap<>();
		
		configFileMap.put("config",  Utils.readFile(ProfileFileLocation.configurationFilePath(), Charset.forName("utf-8")));
		configFileMap.put("credentials", Utils.readFile(ProfileFileLocation.credentialsFilePath(), Charset.forName("utf-8")));
		
		return configFileMap;
	}

	public SessionProfile updateProfile(SessionProfile sessionProfile, ProfileContents profileContents) throws IOException {
		
			Utils.writeFile(
					ProfileFileLocation.configurationFilePath()
					, profileContents.getConfig()
					, Charset.forName("utf-8")
			);
			Utils.writeFile(
					ProfileFileLocation.credentialsFilePath()
					, profileContents.getCredentials()
					, Charset.forName("utf-8")
			);	
			
			sessionProfile.setProfileFile(profileContents);
			
			return sessionProfile;			
	}
}

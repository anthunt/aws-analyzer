package com.anthunt.aws.network.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthunt.aws.network.repository.profile.ProfileRepository;
import com.anthunt.aws.network.repository.profile.model.ProfileContents;

@Service
public class ProfileService {
	
	@Autowired
	private ProfileRepository profileRepository;
	
	public Optional<ProfileContents> getProfileContents(String userid) {
		return this.profileRepository.findById(userid);
	}

	public ProfileContents updateProfile(ProfileContents profileContents) throws IOException {
		return this.profileRepository.save(profileContents);
	}
}

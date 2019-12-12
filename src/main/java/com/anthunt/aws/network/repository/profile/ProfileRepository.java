package com.anthunt.aws.network.repository.profile;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.anthunt.aws.network.repository.profile.model.ProfileContents;

public interface ProfileRepository extends MongoRepository<ProfileContents, String>{

}

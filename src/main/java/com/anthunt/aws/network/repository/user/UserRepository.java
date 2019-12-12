package com.anthunt.aws.network.repository.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.anthunt.aws.network.repository.user.model.User;

public interface UserRepository extends MongoRepository<User, String>{

	public User findByUseremail(String useremail);
	
}

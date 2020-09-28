package com.anthunt.aws.network.service.user;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.anthunt.aws.network.controller.ui.model.JoinUser;
import com.anthunt.aws.network.repository.user.UserRepository;
import com.anthunt.aws.network.repository.user.model.User;
import com.anthunt.aws.network.repository.user.model.UserRoleType;
import com.anthunt.aws.network.utils.Logging;

@Service
public class UserService implements UserDetailsService {

	
	private static final Logger log = Logging.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;
	
	public void joinUser(JoinUser joinUser) {
		User user = this.getUserByUseremail(joinUser.getUseremail());
		if( user == null) {
			user = new User();
		}
		user.setUseremail(joinUser.getUseremail());
		user.setUsername(joinUser.getUsername());
		user.setPassword(new BCryptPasswordEncoder().encode(joinUser.getPassword()));
		user.addUserRole(UserRoleType.USER.userRole());
		userRepository.save(user);
		log.info("Saved user : {}", user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.getUserByUseremail(username);
		log.info("Get user : {}", user);
		return user.userDetails();
	}
	
	public User getUserByUseremail(String useremail) {
		return userRepository.findByUseremail(useremail);
	}

}

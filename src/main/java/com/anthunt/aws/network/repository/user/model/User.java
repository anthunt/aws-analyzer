package com.anthunt.aws.network.repository.user.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class User implements Serializable {

	private static final long serialVersionUID = -3607921291629700561L;

	@Id
	private ObjectId userid;
	private String useremail;
	private String username;
	private String password;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;
	private boolean enabled = true;
	
	private List<UserRole> userRoles;
	
	public User() {
		this.setUserid(ObjectId.get());
		this.setUserRoles(new ArrayList<>());
	}
	
	public UserDetails userDetails() {
		return new UserDetails(this);
	}
	
	public void addUserRole(UserRole userRole) {
		this.userRoles.add(userRole);
	}
	
}

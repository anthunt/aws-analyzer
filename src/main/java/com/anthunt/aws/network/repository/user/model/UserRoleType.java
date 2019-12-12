package com.anthunt.aws.network.repository.user.model;

public enum UserRoleType {

	USER
	, ADMIN
	;
	
	public UserRole userRole() {
		return UserRole.getUserRole(this);
	}
	
}


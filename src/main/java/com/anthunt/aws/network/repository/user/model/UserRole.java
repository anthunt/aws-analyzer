package com.anthunt.aws.network.repository.user.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements GrantedAuthority, Serializable {

	private static final long serialVersionUID = 7800004785542155781L;
	
	private static final String ROLE_PREFIX = "ROLE_";
		
	static UserRole getUserRole(UserRoleType userRoleType) {
		return new UserRole(userRoleType);
	}
	
	private UserRole(UserRoleType userRoleType) {
		this.setRoleName(userRoleType.name());
	}
	
	private String roleName;

	@Override
	public String getAuthority() {
		return ROLE_PREFIX + this.roleName;
	}
	
}

package com.anthunt.aws.network.repository.user.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {

	private static final long serialVersionUID = 2237419746663612807L;
	
	private User user;

	public UserDetails(User user) {
		this.setUser(user);
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getUser().getUserRoles();
	}

	public String getPassword() {
		return this.getUser().getPassword();
	}

	public String getUsername() {
		return this.getUser().getUseremail();
	}

	public boolean isAccountNonExpired() {
		return this.getUser().isAccountNonExpired();
	}

	public boolean isAccountNonLocked() {
		return this.getUser().isAccountNonLocked();
	}

	public boolean isCredentialsNonExpired() {
		return this.getUser().isCredentialsNonExpired();
	}

	public boolean isEnabled() {
		return this.getUser().isEnabled();
	}
	
}

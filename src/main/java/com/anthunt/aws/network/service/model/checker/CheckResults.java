package com.anthunt.aws.network.service.model.checker;

import java.util.HashMap;

public class CheckResults<T> extends HashMap<CheckType, CheckResult> {

	private static final long serialVersionUID = 8507845472362212874L;

	private T resource;
	private String cidr;
	
	public CheckResults(T resource) {
		this.resource = resource;
	}
	
	public T getResource() {
		return this.resource;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}
	
}

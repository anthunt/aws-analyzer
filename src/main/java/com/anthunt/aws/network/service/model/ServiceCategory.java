package com.anthunt.aws.network.service.model;

public enum ServiceCategory {

	COMPUTE("Compute", "Compute")
	, NETWORK("Network", "Networking & Content Delivery")
	, SECURITY("Security", "Security,Identity & Compliance")
	, DATABASE("Database", "Database")
	, STORAGE("Storage", "Storage")
	, GENERAL("General", "General")
	, GROUP("Group", "Group")
	;
	
	private String name;
	private String alias;
	
	private ServiceCategory(String name, String alias) {
		this.setName(name);
		this.setAlias(alias);
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	private void setAlias(String alias) {
		this.alias = alias;
	}
	
}

package com.anthunt.aws.network.repository.model;

public enum ServiceMapType {

	MEMORY
	, MONGODB
	;
	
	public ServiceMap serviceMap() {
		return this.serviceMap(false);
	}
	
	public ServiceMap serviceMap(boolean hasList) {
		ServiceMap serviceMap = null;
		switch (this) {
		case MONGODB:
			serviceMap = new MongoDBServiceMap(hasList);
			break;		
		default:
			serviceMap = new DefaultServiceMap(hasList);
			break;
		}
		return serviceMap;
	}
	
}

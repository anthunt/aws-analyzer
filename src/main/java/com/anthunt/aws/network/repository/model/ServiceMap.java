package com.anthunt.aws.network.repository.model;

import java.util.HashMap;

import com.anthunt.aws.network.service.model.ServiceType;

public class ServiceMap<T> extends HashMap<String, T> {

	private static final long serialVersionUID = -1376430339217822089L;
	
	private Integer active;
	
	public ServiceMap() {
		this.active = null;
	}

	public int getActive() {
		return active == null ? this.size() : active;
	}

	public void setActive(int active) {
		this.active = active;
	}
	
	public ServiceStatistic getServiceStatistic(ServiceType serviceType) {
		ServiceStatistic serviceStatistic = new ServiceStatistic(serviceType);
		serviceStatistic.setServiceTotal(this.size());
		serviceStatistic.setServiceActive(this.getActive());
		return serviceStatistic;
	}
	
}

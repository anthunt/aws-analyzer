package com.anthunt.aws.network.repository.model;

import com.anthunt.aws.network.service.model.ServiceType;

public class ServiceStatistic {

	private String serviceName;
	private int serviceActive;
	private int serviceTotal;
	private String icon;

	public ServiceStatistic(ServiceType serviceType) {
		this.setServiceName(serviceType.getName());
		this.setIcon(serviceType.getIcon());
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	private void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public int getServiceActive() {
		return serviceActive;
	}
	
	public void setServiceActive(int serviceActive) {
		this.serviceActive = serviceActive;
	}
	
	public int getServiceTotal() {
		return serviceTotal;
	}
	
	public void setServiceTotal(int serviceTotal) {
		this.serviceTotal = serviceTotal;
	}	
	
	public String getIcon() {
		return icon;
	}

	private void setIcon(String icon) {
		this.icon = icon;
	}
	
}

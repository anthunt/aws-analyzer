package com.anthunt.aws.network.repository.model;

import com.anthunt.aws.network.service.model.ServiceCategory;
import com.anthunt.aws.network.service.model.ServiceType;

public class ServiceStatistic {

	private ServiceCategory serviceCategory;
	private String serviceName;
	private String serviceDisplayName;
	private int serviceActive;
	private int serviceTotal;
	private String icon;

	public ServiceStatistic(ServiceType serviceType) {
		this.setServiceCategory(serviceType.getServiceCategory());
		this.setServiceName(serviceType.getName());
		this.setServiceDisplayName(serviceType.getAlias());
		this.setIcon(serviceType.getIcon());
	}
	
	public ServiceCategory getServiceCategory() {
		return serviceCategory;
	}

	private void setServiceCategory(ServiceCategory serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public String getServiceName() {
		return serviceName;
	}
	
	private void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceDisplayName() {
		return serviceDisplayName;
	}

	private void setServiceDisplayName(String serviceDisplayName) {
		this.serviceDisplayName = serviceDisplayName;
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

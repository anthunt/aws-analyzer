package com.anthunt.aws.network.repository.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		
		int total = 0;
		
		if(this.size() > 0) {
			if(this.values().iterator().next() instanceof List) {
				Set<String> keys = this.keySet();
				Iterator<String> iKeys = keys.iterator();
				while(iKeys.hasNext()) {
					total += ((List) this.get(iKeys.next())).size();
				}
			} else {
				total = this.size();
			}
		}
		
		serviceStatistic.setServiceTotal(total);
		serviceStatistic.setServiceActive(this.getActive());
		return serviceStatistic;
	}
	
}

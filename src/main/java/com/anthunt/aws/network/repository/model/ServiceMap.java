package com.anthunt.aws.network.repository.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.anthunt.aws.network.repository.aws.AwsData;
import com.anthunt.aws.network.service.model.ServiceType;
import com.anthunt.aws.network.session.SessionProfile;
import org.bson.types.ObjectId;
import software.amazon.awssdk.regions.Region;

public abstract class ServiceMap {

	private boolean hasList;
	private Integer active;
	
	public ServiceMap() {
		this(false);
	}
	
	public ServiceMap(boolean hasList) {
		this.hasList = hasList;
		this.active = null;
	}
	
	public boolean hasList() {
		return this.hasList;
	}
	
	public <T> AwsData put(ObjectId userId, String profileName, Region region, String id, Object object, Class<T> clazz) {
		return this.put(new AwsData(userId, profileName, region, id, object), clazz);
	}
	
	public <T> List<T> allValues(SessionProfile sessionProfile, Class<T> clazz) {
		List<T> list = new ArrayList<>();
		List<AwsData> awsDatas = this.values(sessionProfile, clazz);
		awsDatas.forEach(new Consumer<AwsData>() {

			@Override
			public void accept(AwsData awsData) {
				if(awsData.isList()) {
					list.addAll(awsData.getDataList());
				} else {
					list.add(awsData.getData());
				}
			}
			
		});
		return list;
	}
	
	public abstract <T> List<AwsData> values(SessionProfile sessionProfile, Class<T> clazz);
	public abstract <T> Optional<AwsData> get(SessionProfile sessionProfile, String id, Class<T> clazz);
	public abstract <T> boolean containsKey(SessionProfile sessionProfile, String id, Class<T> clazz);
	public abstract <T> void clear(SessionProfile sessionProfile, Class<T> clazz);
	protected abstract <T> AwsData put(AwsData awsData, Class<T> clazz);

	public Integer getActive() {
		return this.active;
	}
	public void setActive(int active) {
		this.active = active;
	}

	public ServiceStatistic getServiceStatistic(SessionProfile sessionProfile, ServiceType serviceType) {
		ServiceStatistic serviceStatistic = new ServiceStatistic(serviceType);
		
		int total = 0;
		
		List<AwsData> lists = this.values(sessionProfile, serviceType.getClazz());
		
		if(lists.size() > 0) {
			if(this.hasList) {
				for(AwsData awsData : lists) {
					total += awsData.getDataList().size();
				}
			} else {
				total = lists.size();
			}
		}
		
		serviceStatistic.setServiceTotal(total);
		serviceStatistic.setServiceActive(this.getActive());
		return serviceStatistic;
	}
	
}

package com.anthunt.aws.network.repository.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.anthunt.aws.network.repository.aws.AwsData;

public class DefaultServiceMap extends ServiceMap {

	private HashMap<String, AwsData> serviceMap;
	
	public DefaultServiceMap() {
		this(false);
	}
	
	public DefaultServiceMap(boolean hasList) {
		super(hasList);
		this.serviceMap = new HashMap<>();
	}

	@Override
	protected <T> AwsData put(AwsData awsData, Class<T> clazz) {
		return this.serviceMap.put(awsData.getDataId(), awsData);
	}

	@Override
	public <T> Optional<AwsData> get(String id, Class<T> clazz) {
		return Optional.ofNullable(this.serviceMap.get(id));
	}

	@Override
	public <T> boolean containsKey(String id, Class<T> clazz) {
		return this.serviceMap.containsKey(id);
	}

	@Override
	public <T> List<AwsData> values(Class<T> clazz) {
		return new ArrayList<>(this.serviceMap.values());
	}
	
}

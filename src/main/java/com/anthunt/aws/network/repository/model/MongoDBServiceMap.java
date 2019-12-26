package com.anthunt.aws.network.repository.model;

import java.util.List;
import java.util.Optional;

import com.anthunt.aws.network.repository.aws.AwsData;
import com.anthunt.aws.network.repository.aws.AwsRepository;
import com.anthunt.aws.network.utils.Utils;

public class MongoDBServiceMap extends ServiceMap {

	private AwsRepository awsRepository;
	
	public MongoDBServiceMap() {
		this(false);
	}
	
	public MongoDBServiceMap(boolean hasList) {
		super(hasList);
		this.awsRepository = (AwsRepository) Utils.getBean(AwsRepository.class);
	}
	
	@Override
	public <T> List<AwsData> values(Class<T> clazz) {
		return this.awsRepository.findAll(clazz);
	}
	
	@Override
	public <T> Optional<AwsData> get(String id, Class<T> clazz) {
		return awsRepository.findByDataId(id, clazz);
	}

	@Override
	public <T> boolean containsKey(String id, Class<T> clazz) {
		return awsRepository.findByDataId(id, clazz).isPresent();
	}

	@Override
	protected <T> AwsData put(AwsData awsData, Class<T> clazz) {
		return this.awsRepository.save(awsData, clazz);
	}

}

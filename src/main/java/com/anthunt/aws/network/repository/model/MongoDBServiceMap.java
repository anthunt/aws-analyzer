package com.anthunt.aws.network.repository.model;

import java.util.List;
import java.util.Optional;

import com.anthunt.aws.network.repository.aws.AwsData;
import com.anthunt.aws.network.repository.aws.AwsRepository;
import com.anthunt.aws.network.session.SessionProfile;
import com.anthunt.aws.network.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public <T> List<AwsData> values(SessionProfile sessionProfile, Class<T> clazz) {
		return this.awsRepository.findAll(sessionProfile, clazz);
	}
	
	@Override
	public <T> Optional<AwsData> get(SessionProfile sessionProfile, String id, Class<T> clazz) {
		return awsRepository.findByDataId(sessionProfile, id, clazz);
	}

	@Override
	public <T> boolean containsKey(SessionProfile sessionProfile, String id, Class<T> clazz) {
		return awsRepository.findByDataId(sessionProfile, id, clazz).isPresent();
	}

	@Override
	public <T> void clear(SessionProfile sessionProfile, Class<T> clazz) {
		this.awsRepository.removeAll(sessionProfile, clazz);
		log.debug("called remove - {}", clazz.getSimpleName());
	}

	@Override
	protected <T> AwsData put(AwsData awsData, Class<T> clazz) {
		return this.awsRepository.save(awsData, clazz);
	}

}

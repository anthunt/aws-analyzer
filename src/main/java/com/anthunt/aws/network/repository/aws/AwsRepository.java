package com.anthunt.aws.network.repository.aws;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class AwsRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
		
	public <V> AwsData save(AwsData awsData, Class<V> clazz) {
		return this.mongoTemplate.save(awsData, clazz.getSimpleName());
	}
	
	public <V> List<AwsData> findAll(Class<V> clazz) {
		return this.mongoTemplate.findAll(AwsData.class, clazz.getSimpleName());
	}

	public <V> Optional<AwsData> findByDataId(String id, Class<V> clazz) {
		CriteriaDefinition criteriaDefinition = Criteria.where("dataId").is(id);
		Query query = new Query(criteriaDefinition);
		return Optional.ofNullable(this.mongoTemplate.findOne(query, AwsData.class, clazz.getSimpleName()));
	}
	
}

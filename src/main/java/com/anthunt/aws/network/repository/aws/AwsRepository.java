package com.anthunt.aws.network.repository.aws;

import java.util.List;
import java.util.Optional;

import com.anthunt.aws.network.session.SessionProfile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.regions.Region;

@Slf4j
@Repository
public class AwsRepository {

	@Autowired
	private MongoTemplate mongoTemplate;
		
	public <V> AwsData save(AwsData awsData, Class<V> clazz) {

		Optional<AwsData> savedAwsData = this.findByDataId(awsData.getUserId(), awsData.getProfileName(), awsData.getRegionId(), awsData.getDataId(), clazz);
		if(savedAwsData.isPresent()) {
			awsData.setId(savedAwsData.get().getId());
		}

		return this.mongoTemplate.save(awsData, clazz.getSimpleName());
	}
	
	public <V> List<AwsData> findAll(SessionProfile sessionProfile, Class<V> clazz) {
		Query query = new Query(
				new Criteria()
						.andOperator(
								Criteria.where("userId").is(sessionProfile.getUserid())
								, Criteria.where("profileName").is(sessionProfile.getProfileName())
								, Criteria.where("regionId").is(sessionProfile.getRegion().id())
						)
		);
		return this.mongoTemplate.find(query, AwsData.class, clazz.getSimpleName());
	}

	private <V> Optional<AwsData> findByDataId(ObjectId userId, String profileName, String regionId, String id, Class<V> clazz) {
		Query query = new Query(
				new Criteria()
						.andOperator(
								Criteria.where("dataId").is(id)
								, Criteria.where("userId").is(userId)
								, Criteria.where("profileName").is(profileName)
								, Criteria.where("regionId").is(regionId)
						)
		);
		return Optional.ofNullable(this.mongoTemplate.findOne(query, AwsData.class, clazz.getSimpleName()));
	}

	public <V> Optional<AwsData> findByDataId(SessionProfile sessionProfile, String id, Class<V> clazz) {
		return this.findByDataId(sessionProfile.getUserid(), sessionProfile.getProfileName(), sessionProfile.getRegionId(), id, clazz);
	}

	public <V> void removeAll(SessionProfile sessionProfile, Class<V> clazz) {
		Query query = new Query(
				new Criteria()
						.andOperator(
								Criteria.where("userId").is(sessionProfile.getUserid())
								, Criteria.where("profileName").is(sessionProfile.getProfileName())
								, Criteria.where("regionId").is(sessionProfile.getRegion().id())
						)
		);
		this.mongoTemplate.remove(query, clazz.getSimpleName());
		log.debug("Deleted collection data - {}, {}", sessionProfile, clazz.getSimpleName());
	}
	
}

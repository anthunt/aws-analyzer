package com.anthunt.aws.network.repository.aws;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import software.amazon.awssdk.regions.Region;

@Document
@Data
public class AwsData {

	@Id
	private ObjectId id;
	private ObjectId userId;
	private String profileName;
	private String regionId;
	private String dataId;
	private Object data;
	
	public AwsData(ObjectId userId, String profileName, Region region, String dataId, Object object) {
		this(userId, profileName, region, null, dataId, object);
	}
	
	public AwsData(ObjectId userId, String profileName, Region region, ObjectId id, String dataId, Object object) {
		this.setId(id);
		this.setUserId(userId);
		this.setProfileName(profileName);
		this.setRegionId(region.id());
		this.setDataId(dataId);
		this.setData(object);
	}
	
	public boolean isList() {
		return this.data instanceof List;
	}
	
	@SuppressWarnings("unchecked")
	public <V> V getData() {
		return (V) this.data;
	}
	
	@SuppressWarnings("unchecked")
	public <V> List<V> getDataList() {
		return (List<V>) this.data;
	}
	
}

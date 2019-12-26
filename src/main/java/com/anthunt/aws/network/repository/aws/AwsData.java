package com.anthunt.aws.network.repository.aws;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class AwsData {

	@Id
	private ObjectId id;
	private String dataId;
	private Object data;
	
	public AwsData(String dataId, Object object) {	
		this(null, dataId, object);
	}
	
	public AwsData(ObjectId id, String dataId, Object object) {
		this.setId(id);
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

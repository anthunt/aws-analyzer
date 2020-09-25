package com.anthunt.aws.network.repository.aws;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.builder.Buildable;

@Slf4j
public class AwsConverter implements Converter<Document, AwsData> {

	@Override
	public AwsData convert(Document source) {
		
		AwsData awsData = null;
		
		log.trace("Document : {}", source);
		
		Object dataObject = source.get("data");
				
		Object unmarshalled;
		
		if(dataObject instanceof List) {
			List<Object> unmarshalledList = new ArrayList<>();
			List<?> datas = (List<?>) dataObject;
			for(Object data : datas) {
				unmarshalledList.add(unmarshall((Document) data));
			}
			unmarshalled = unmarshalledList;
		} else {
			unmarshalled = unmarshall((Document) dataObject);	
		}
		
		awsData = new AwsData(
				source.getObjectId("userId")
				, source.getString("profileName")
				, Region.of(source.getString("regionId"))
				, source.getObjectId("_id")
				, source.getString("dataId")
				, unmarshalled
		);
		log.debug("AwsData : {}", awsData);
		
		return awsData;
	}
	
	public static Object unmarshall(Document data) {
		Object unmarshared = null;
		
		try {
			SdkPojo sdkPojo = getSdkPojo(data.getString("_class"));
			unmarshared = unmarshall(sdkPojo, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return unmarshared;
	}
	
	public static String unmarshallLocationName(String unmarshallLocationName) {
		
		if("TagSet".equalsIgnoreCase(unmarshallLocationName)) {
			unmarshallLocationName = "tags";
		} else if("RouteSet".equalsIgnoreCase(unmarshallLocationName)) {
			unmarshallLocationName = "routes";
		} else if("GroupSet".equalsIgnoreCase(unmarshallLocationName)) {
			unmarshallLocationName = "securityGroups";
		} else {
			StringBuilder location = new StringBuilder()
					.append(unmarshallLocationName.substring(0,1).toLowerCase())
					.append(unmarshallLocationName.substring(1, unmarshallLocationName.length()));
			unmarshallLocationName = location.toString();
		}
		
		return unmarshallLocationName;		
	}
	
	public static SdkPojo unmarshall(SdkPojo sdkPojo, Object data) {
		
		for (SdkField<?> field : sdkPojo.sdkFields()) {
            field.set(sdkPojo, unmarshall(field, data));
        }
		
		return (SdkPojo) ((Buildable) sdkPojo).build();
	}
	
	public static Object unmarshall(SdkField<?> field, Object data) {
		String unmarshallLocationName = unmarshallLocationName(field.unmarshallLocationName());
		Object fieldObj;
		
		if(data instanceof Document) {
			fieldObj = ((Document) data).get(unmarshallLocationName);
		} else {
			fieldObj = data;
		}
		
		if(field.marshallingType() == MarshallingType.SDK_POJO) {
			fieldObj = unmarshall(
					field.constructor().get()
					, "item".equalsIgnoreCase(unmarshallLocationName) 
						|| "member".equalsIgnoreCase(unmarshallLocationName)
						|| unmarshallLocationName == null 
						? data : Document.class.cast(fieldObj));
		} else if(field.marshallingType() == MarshallingType.LIST) {
			fieldObj = listUnmarshaller(field, fieldObj);
		} else {
			if(fieldObj instanceof Date) {
				fieldObj = Date.class.cast(fieldObj).toInstant();
			}
		}
		return fieldObj;
	}
	
	public static List<?> listUnmarshaller(SdkField<?> field, Object fieldObj) {		
		List<Object> list = new ArrayList<>();
		if(fieldObj != null) {
			ListTrait listTrait = field.getTrait(ListTrait.class);
			SdkField<?> listField = listTrait.memberFieldInfo();
			
			List<?> listObj = List.class.cast(fieldObj);
			listObj.forEach(member -> {			
				list.add(unmarshall(listField, member));
			});
		}
		return list;
	}
	
	public static SdkPojo getSdkPojo(String _class) throws Exception {
		Class<?> clazz = Class.forName(_class);
		Method method = clazz.getMethod("builder", new Class<?>[0]);
		Object obj = method.invoke(null, new Object[0]);
		return SdkPojo.class.cast(obj);
	}
	
}

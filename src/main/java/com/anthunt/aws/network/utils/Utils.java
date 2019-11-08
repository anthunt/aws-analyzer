package com.anthunt.aws.network.utils;

import java.util.List;

import software.amazon.awssdk.services.ec2.model.Tag;

public class Utils {

	public static String getNameFromTags(List<Tag> tags) {
		String name = "Unknown";
		for (Tag tag : tags) {
			if("Name".equals(tag.key())) {
				name = tag.value();
			}
		}
		return name;
	}
	
}

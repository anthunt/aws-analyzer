package com.anthunt.aws.network.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
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
	
	public static String decodeB64URL(String encodedURL) throws UnsupportedEncodingException {
		return URLDecoder.decode(new String(Base64.getDecoder().decode(encodedURL), "utf8"), "utf8");
	}
	
	public static String encodeB64URL(String plainURL) throws UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(URLEncoder.encode(plainURL, "utf8").getBytes());
	}
	
}

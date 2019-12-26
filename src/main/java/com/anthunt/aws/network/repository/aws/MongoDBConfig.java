package com.anthunt.aws.network.repository.aws;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoDBConfig {

	@Bean
    public MongoCustomConversions customConversions()
    {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new AwsConverter());
        return new MongoCustomConversions(converterList);
    }
	
}

package com.anthunt.aws.network;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.anthunt.aws.network.utils.Logging;

@EnableAsync
@SpringBootApplication
public class NetworkApplication {
	
	private static final Logger log = Logging.getLogger(NetworkApplication.class);

	public static void main(String[] args) throws Exception {
//		MongoDBHandler mongoDBHandler = new MongoDBHandler();
//    	mongoDBHandler.runMongoDB();
//    	log.info("MongoDB Started");
        SpringApplication.run(NetworkApplication.class, args);
    }
	
}

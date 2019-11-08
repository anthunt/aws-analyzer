package com.anthunt.aws.network;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class NetworkApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(NetworkApplication.class, args);
    }
	
}

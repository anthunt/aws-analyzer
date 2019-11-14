package com.anthunt.aws.network.service.checker;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.anthunt.aws.network.service.model.ServiceResult;

public class ServiceRepositoryCollectListener {

	private SseEmitter sseEmitter;
	
	public ServiceRepositoryCollectListener(SseEmitter sseEmitter) {
		this.sseEmitter = sseEmitter;
	}
	
	public void serviceLoaded(int num, int total, String message) throws IOException {
		this.sseEmitter.send(SseEmitter.event()
			      .id(UUID.randomUUID().toString())
			      .data(new ServiceResult((int) Math.floor(((float) num/total)*100), message))
		);
	}

}

package com.anthunt.aws.network.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.anthunt.aws.network.controller.model.RestResponse;

@RestControllerAdvice
public class RestResponseAdvice implements ResponseBodyAdvice<Object> {
	
    @Override
    public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType,
			MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request,
			ServerHttpResponse response) {
		
		if(!(body instanceof RestResponse)) {
			body = new RestResponse().setData(body);
		}
		response.setStatusCode(HttpStatus.OK);
		
		return body;
	}
	
	@ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public RestResponse handle(Exception e) {
        return new RestResponse().setError(e.getMessage());
    }
    
}
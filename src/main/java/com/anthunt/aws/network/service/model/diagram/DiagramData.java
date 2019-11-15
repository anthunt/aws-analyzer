package com.anthunt.aws.network.service.model.diagram;

import java.util.ArrayList;
import java.util.List;

public class DiagramData<T> {

	private T data;
	private List<String> classes;
	
	public DiagramData(T data) {
		this.data = data;
		this.classes = new ArrayList<>();
	}
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public List<String> getClasses() {
		return classes;
	}
	
	public DiagramData<T> addClass(NodeType nodeType) {
		this.classes.add(nodeType.getName());
		return this;
	}
	
}

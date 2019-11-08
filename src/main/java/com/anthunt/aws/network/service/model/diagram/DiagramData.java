package com.anthunt.aws.network.service.model.diagram;

import java.util.ArrayList;
import java.util.List;

public class DiagramData<E> {

	private E data;
	private List<String> classes;
	
	public DiagramData(E data) {
		this.data = data;
		this.classes = new ArrayList<>();
	}
	
	public E getData() {
		return data;
	}

	public void setData(E data) {
		this.data = data;
	}
	
	public List<String> getClasses() {
		return classes;
	}
	
	public DiagramData<E> addClass(NodeType nodeType) {
		this.classes.add(nodeType.getName());
		return this;
	}
	
}

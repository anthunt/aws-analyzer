package com.anthunt.aws.network.service.model.diagram;

import com.anthunt.aws.network.service.DiagramContentsGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagramNode {

	private String id;
	private String parent;
	private String title;
	private String label;
	private Object resource;
	
	public DiagramNode(String id, Object resource) {
		this(id, resource, null);
	}

	public DiagramNode(String id, Object resource, String parentId) {
		
		DiagramContentsGenerator diagramContentsGenerator = DiagramContentsGenerator.getInstance(resource);
		
		this.setId(id);
		this.setParent(parentId);
		this.setTitle(diagramContentsGenerator.title());
		this.setLabel(diagramContentsGenerator.label());
		this.setResource(resource);
	}
	
	public String getId() {
		return id;
	}
	
	private void setId(String id) {
		this.id = id;
	}
	
	public String getParent() {
		return parent;
	}

	private void setParent(String parent) {
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	public String getLabel() {
		return this.label;
	}
	
	private void setLabel(String label) {
		this.label = label;
	}
	
	public Object getResource() {
		return resource;
	}
	
	private void setResource(Object resource) {
		this.resource = resource;
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
				.append("{")
				.append("id: ").append(this.getId())
				.append(", label: ").append(this.getLabel())
				.append("}")
				.toString();
	}
}

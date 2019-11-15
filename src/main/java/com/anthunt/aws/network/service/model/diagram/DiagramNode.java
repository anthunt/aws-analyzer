package com.anthunt.aws.network.service.model.diagram;

public class DiagramNode {

	private String id;
	private String label;
	private Object resource;
	
	public DiagramNode(String id, String label) {
		this(id, label, null);
	}
	
	public DiagramNode(String id, String label, Object resource ) {
		this.setId(id);
		this.setLabel(label);
		this.setResource(resource);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Object getResource() {
		return resource;
	}
	
	public void setResource(Object resource) {
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

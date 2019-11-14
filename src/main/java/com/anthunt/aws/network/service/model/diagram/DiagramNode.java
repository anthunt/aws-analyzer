package com.anthunt.aws.network.service.model.diagram;

public class DiagramNode {

	private String id;
	private String label;
	
	public DiagramNode(String id, String label) {
		this.setId(id);
		this.setLabel(label);
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

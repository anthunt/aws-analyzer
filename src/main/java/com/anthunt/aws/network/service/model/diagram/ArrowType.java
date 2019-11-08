package com.anthunt.aws.network.service.model.diagram;

public enum ArrowType {

	NONE("none"),
	TEE("tee"),
	TRIANGLE_BACKCURVE("triangle-backcurve")
	;
	
	private String name;
	
	private ArrowType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}

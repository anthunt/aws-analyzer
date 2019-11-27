package com.anthunt.aws.network.service.model.diagram;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagramEdge {

	private boolean isAllMode;
	private String source;
	private String target;
	private String label;
	private String sourceArrowShape;
	private String targetArrowShape;
	private String lineColor;
	private boolean allow;
	private boolean in;
	private boolean out;

	public static DiagramEdge make(String source, String target) {
		return new DiagramEdge(source, target);
	}
	
	private DiagramEdge(String source, String target) {
		this.setSource(source)
		    .setTarget(target);
	}
	
	public boolean isAllMode() {
		return isAllMode;
	}

	public DiagramEdge setAllMode(boolean isAllMode) {
		this.isAllMode = isAllMode;
		return this;
	}

	public String getSource() {
		return source;
	}
	
	public DiagramEdge setSource(String source) {
		this.source = source;
		return this;
	}
	
	public String getTarget() {
		return target;
	}
	
	public DiagramEdge setTarget(String target) {
		this.target = target;
		return this;
	}
	
	public String getLabel() {
		return label;
	}
	
	public DiagramEdge setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public DiagramEdge setBoth(boolean allow) {
		this.setAllow(allow)
		    .setInBound(true)
		    .setOutBound(true);
		if(allow) {
			this.setSourceArrowShape(ArrowType.TRIANGLE_BACKCURVE)
			    .setTargetArrowShape(ArrowType.TRIANGLE_BACKCURVE)
			    .setLineColor("green");
		} else {
			this.setSourceArrowShape(ArrowType.TEE)
			    .setTargetArrowShape(ArrowType.TEE)
			    .setLineColor("#999");
		}
		return this;
	}

	public DiagramEdge setIn(boolean allow) {
		this.setAllow(allow)
		    .setInBound(true)
		    .setOutBound(false);
		if(allow) {
			this.setSourceArrowShape(ArrowType.NONE)
			    .setTargetArrowShape(ArrowType.TRIANGLE_BACKCURVE)
			    .setLineColor("green");
		} else {
			this.setSourceArrowShape(ArrowType.NONE)
			    .setTargetArrowShape(ArrowType.TEE)
			    .setLineColor("#999");
		}
		return this;
	}

	public DiagramEdge setOut(boolean allow) {
		this.setAllow(allow)
			.setInBound(false)
			.setOutBound(true);
		if(allow) {
			this.setSourceArrowShape(ArrowType.TRIANGLE_BACKCURVE)
			    .setTargetArrowShape(ArrowType.NONE)
			    .setLineColor("green");
		} else {
			this.setSourceArrowShape(ArrowType.TEE)
			    .setTargetArrowShape(ArrowType.NONE)
			    .setLineColor("#999");
		}
		return this;
	}
	
	public String getSourceArrowShape() {
		return sourceArrowShape;
	}
	
	private DiagramEdge setSourceArrowShape(ArrowType sourceArrowShape) {
		this.sourceArrowShape = sourceArrowShape.getName();
		return this;
	}
	
	public String getTargetArrowShape() {
		return targetArrowShape;
	}
	
	private DiagramEdge setTargetArrowShape(ArrowType targetArrowShape) {
		this.targetArrowShape = targetArrowShape.getName();
		return this;
	}
	
	public String getLineColor() {
		return lineColor;
	}
	
	private DiagramEdge setLineColor(String lineColor) {
		this.lineColor = lineColor;
		return this;
	}
	
	public boolean isAllow() {
		return this.allow;
	}
	
	private DiagramEdge setAllow(boolean allow) {
		this.allow = allow;
		return this;
	}
	
	public boolean isIn() {
		return this.in;
	}
	
	private DiagramEdge setInBound(boolean in) {
		this.in = in;
		return this;
	}
	
	public boolean isOut() {
		return this.out;
	}
	
	private DiagramEdge setOutBound(boolean out) {
		this.out = out;
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
            return true;
        if (!(o instanceof DiagramEdge))
            return false;
        DiagramEdge de = (DiagramEdge)o;
        return de.target.equals(target)
            && de.source.equals(source)
            && (isAllMode || de.label.equals(label));
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
				.append("{")
				.append("source: ").append(this.getSource())
				.append(", target: ").append(this.getTarget())
				.append(", label: ").append(this.getLabel())
				.append(", lineColor: ").append(this.getLineColor())
				.append(", sourceArrowShape: ").append(this.getSourceArrowShape())
				.append(", targetArrowShape: ").append(this.getTargetArrowShape())
				.append("}")
				.toString();
	}
	
}

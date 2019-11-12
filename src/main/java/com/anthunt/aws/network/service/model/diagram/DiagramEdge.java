package com.anthunt.aws.network.service.model.diagram;

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
	
	public DiagramEdge(String source, String target) {
		this.setSource(source);
		this.setTarget(target);
	}
	
	public boolean isAllMode() {
		return isAllMode;
	}

	public void setAllMode(boolean isAllMode) {
		this.isAllMode = isAllMode;
	}

	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setBoth(boolean allow) {
		this.setAllow(allow);
		this.setInBound(true);
		this.setOutBound(true);
		if(allow) {
			this.setSourceArrowShape(ArrowType.TRIANGLE_BACKCURVE);
			this.setTargetArrowShape(ArrowType.TRIANGLE_BACKCURVE);
			this.setLineColor("green");
		} else {
			this.setSourceArrowShape(ArrowType.TEE);
			this.setTargetArrowShape(ArrowType.TEE);
			this.setLineColor("#999");
		}
	}

	public void setIn(boolean allow) {
		this.setAllow(allow);
		this.setInBound(true);
		this.setOutBound(false);
		if(allow) {
			this.setSourceArrowShape(ArrowType.NONE);
			this.setTargetArrowShape(ArrowType.TRIANGLE_BACKCURVE);
			this.setLineColor("green");
		} else {
			this.setSourceArrowShape(ArrowType.NONE);
			this.setTargetArrowShape(ArrowType.TEE);
			this.setLineColor("#999");
		}
	}

	public void setOut(boolean allow) {
		this.setAllow(allow);
		this.setInBound(false);
		this.setOutBound(true);
		if(allow) {
			this.setSourceArrowShape(ArrowType.TRIANGLE_BACKCURVE);
			this.setTargetArrowShape(ArrowType.NONE);
			this.setLineColor("green");
		} else {
			this.setSourceArrowShape(ArrowType.TEE);
			this.setTargetArrowShape(ArrowType.NONE);
			this.setLineColor("#999");
		}
	}
	
	public String getSourceArrowShape() {
		return sourceArrowShape;
	}
	
	private void setSourceArrowShape(ArrowType sourceArrowShape) {
		this.sourceArrowShape = sourceArrowShape.getName();
	}
	
	public String getTargetArrowShape() {
		return targetArrowShape;
	}
	
	private void setTargetArrowShape(ArrowType targetArrowShape) {
		this.targetArrowShape = targetArrowShape.getName();
	}
	
	public String getLineColor() {
		return lineColor;
	}
	
	private void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}
	
	public boolean isAllow() {
		return this.allow;
	}
	
	private void setAllow(boolean allow) {
		this.allow = allow;
	}
	
	public boolean isIn() {
		return this.in;
	}
	
	private void setInBound(boolean in) {
		this.in = in;
	}
	
	public boolean isOut() {
		return this.out;
	}
	
	private void setOutBound(boolean out) {
		this.out = out;
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
	
}

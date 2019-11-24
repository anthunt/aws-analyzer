package com.anthunt.aws.network.service.model.diagram;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagramResult {

	@JsonIgnore
	private boolean isAllMode;
	
	@JsonIgnore
	private List<String> nodeIds;
	
	@JsonIgnore
	private List<DiagramEdge> diagramEdges;
	
	@JsonIgnore
	private String vpcId;
	
	private List<DiagramData<DiagramNode>> nodes;
	private List<DiagramData<DiagramEdge>> edges;
	
	public DiagramResult() {
		this(null);
	}
	
	public DiagramResult(String vpcId) {
		this(vpcId, false);
	}

	public DiagramResult(String vpcId, boolean isAllMode) {
		this.setVpcId(vpcId);
		this.isAllMode = isAllMode;
		this.nodeIds = new ArrayList<>();
		this.diagramEdges = new ArrayList<>();
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
	}
	
	public String getVpcId() {
		return vpcId;
	}

	private void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	
	public List<DiagramData<DiagramNode>> getNodes() {
		return nodes;
	}
	
	public DiagramNode addNode(DiagramData<DiagramNode> node) {
		String nodeId = node.getData().getId();
		if(!this.nodeIds.contains(nodeId)) {
			this.nodeIds.add(nodeId);
			this.nodes.add(node);
		}
		return node.getData();
	}
	
	public List<DiagramData<DiagramEdge>> getEdges() {
		return edges;
	}
	
	public DiagramEdge addEdge(DiagramData<DiagramEdge> edge) {
		edge.getData().setAllMode(this.isAllMode);
		if(this.isAllMode) {
			edge.getData().setLabel("");
			if(!this.diagramEdges.contains(edge.getData())) {
				this.diagramEdges.add(edge.getData());
				this.edges.add(edge);
			}
		} else {
			this.diagramEdges.add(edge.getData());
			this.edges.add(edge);
		}
		return edge.getData();
	}
	
}

package com.anthunt.aws.network.service.model.diagram;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DiagramResult {

	@JsonIgnore
	private List<String> nodeIds;
	private List<DiagramData<DiagramNode>> nodes;
	private List<DiagramData<DiagramEdge>> edges;
	
	public DiagramResult() {
		this.nodeIds = new ArrayList<>();
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
	}
	
	public List<DiagramData<DiagramNode>> getNodes() {
		return nodes;
	}
	
	public void addNode(DiagramData<DiagramNode> node) {
		String nodeId = node.getData().getId();
		if(!this.nodeIds.contains(nodeId)) {
			this.nodeIds.add(nodeId);
			this.nodes.add(node);
		}
	}
	
	public List<DiagramData<DiagramEdge>> getEdges() {
		return edges;
	}
	
	public void addEdge(DiagramData<DiagramEdge> edge) {
		this.edges.add(edge);
	}
	
}

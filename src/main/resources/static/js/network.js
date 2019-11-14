var NetworkLoad = function() {
	
	var diagram;
	var navigator;
	
	var layoutOptions = {
		name: 'klay',
		nodeDimensionsIncludeLabels: false, // Boolean which changes whether label dimensions are included when calculating node dimensions
		fit: true, // Whether to fit
		padding: 20, // Padding on fit
		animate: false, // Whether to transition the node positions
		animateFilter: function( node, i ){ return true; }, // Whether to animate specific nodes when animation is on; non-animated nodes immediately go to their final positions
		animationDuration: 500, // Duration of animation in ms if enabled
		animationEasing: undefined, // Easing of animation if enabled
		transform: function( node, pos ){ return pos; }, // A function that applies a transform to the final node position
		ready: undefined, // Callback on layoutready
		stop: undefined, // Callback on layoutstop
		klay: {
			// Following descriptions taken from http://layout.rtsys.informatik.uni-kiel.de:9444/Providedlayout.html?algorithm=de.cau.cs.kieler.klay.layered
			addUnnecessaryBendpoints: false, // Adds bend points even if an edge does not change direction.
			aspectRatio: 1.6, // The aimed aspect ratio of the drawing, that is the quotient of width by height
			borderSpacing: 20, // Minimal amount of space to be left to the border
			compactComponents: false, // Tries to further compact components (disconnected sub-graphs).
			crossingMinimization: 'LAYER_SWEEP', // Strategy for crossing minimization.
			/* LAYER_SWEEP The layer sweep algorithm iterates multiple times over the layers, trying to find node orderings that minimize the number of crossings. The algorithm uses randomization to increase the odds of finding a good result. To improve its results, consider increasing the Thoroughness option, which influences the number of iterations done. The Randomization seed also influences results.
			INTERACTIVE Orders the nodes of each layer by comparing their positions before the layout algorithm was started. The idea is that the relative order of nodes as it was before layout was applied is not changed. This of course requires valid positions for all nodes to have been set on the input graph before calling the layout algorithm. The interactive layer sweep algorithm uses the Interactive Reference Point option to determine which reference point of nodes are used to compare positions. */
			cycleBreaking: 'GREEDY', // Strategy for cycle breaking. Cycle breaking looks for cycles in the graph and determines which edges to reverse to break the cycles. Reversed edges will end up pointing to the opposite direction of regular edges (that is, reversed edges will point left if edges usually point right).
			/* GREEDY This algorithm reverses edges greedily. The algorithm tries to avoid edges that have the Priority property set.
			INTERACTIVE The interactive algorithm tries to reverse edges that already pointed leftwards in the input graph. This requires node and port coordinates to have been set to sensible values.*/
			direction: 'UNDEFINED', // Overall direction of edges: horizontal (right / left) or vertical (down / up)
			/* UNDEFINED, RIGHT, LEFT, DOWN, UP */
			edgeRouting: 'ORTHOGONAL', // Defines how edges are routed (POLYLINE, ORTHOGONAL, SPLINES)
			edgeSpacingFactor: 0.5, // Factor by which the object spacing is multiplied to arrive at the minimal spacing between edges.
			feedbackEdges: false, // Whether feedback edges should be highlighted by routing around the nodes.
			fixedAlignment: 'NONE', // Tells the BK node placer to use a certain alignment instead of taking the optimal result.  This option should usually be left alone.
			/* NONE Chooses the smallest layout from the four possible candidates.
			LEFTUP Chooses the left-up candidate from the four possible candidates.
			RIGHTUP Chooses the right-up candidate from the four possible candidates.
			LEFTDOWN Chooses the left-down candidate from the four possible candidates.
			RIGHTDOWN Chooses the right-down candidate from the four possible candidates.
			BALANCED Creates a balanced layout from the four possible candidates. */
			inLayerSpacingFactor: 1.0, // Factor by which the usual spacing is multiplied to determine the in-layer spacing between objects.
			layoutHierarchy: false, // Whether the selected layouter should consider the full hierarchy
			linearSegmentsDeflectionDampening: 0.3, // Dampens the movement of nodes to keep the diagram from getting too large.
			mergeEdges: false, // Edges that have no ports are merged so they touch the connected nodes at the same points.
			mergeHierarchyCrossingEdges: true, // If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports as possible.
			nodeLayering:'NETWORK_SIMPLEX', // Strategy for node layering.
			/* NETWORK_SIMPLEX This algorithm tries to minimize the length of edges. This is the most computationally intensive algorithm. The number of iterations after which it aborts if it hasn't found a result yet can be set with the Maximal Iterations option.
			LONGEST_PATH A very simple algorithm that distributes nodes along their longest path to a sink node.
			INTERACTIVE Distributes the nodes into layers by comparing their positions before the layout algorithm was started. The idea is that the relative horizontal order of nodes as it was before layout was applied is not changed. This of course requires valid positions for all nodes to have been set on the input graph before calling the layout algorithm. The interactive node layering algorithm uses the Interactive Reference Point option to determine which reference point of nodes are used to compare positions. */
			nodePlacement:'BRANDES_KOEPF', // Strategy for Node Placement
			/* BRANDES_KOEPF Minimizes the number of edge bends at the expense of diagram size: diagrams drawn with this algorithm are usually higher than diagrams drawn with other algorithms.
			LINEAR_SEGMENTS Computes a balanced placement.
			INTERACTIVE Tries to keep the preset y coordinates of nodes from the original layout. For dummy nodes, a guess is made to infer their coordinates. Requires the other interactive phase implementations to have run as well.
			SIMPLE Minimizes the area at the expense of... well, pretty much everything else. */
			randomizationSeed: 1, // Seed used for pseudo-random number generators to control the layout algorithm; 0 means a new seed is generated
			routeSelfLoopInside: false, // Whether a self-loop is routed around or inside its node.
			separateConnectedComponents: true, // Whether each connected component should be processed separately
			spacing: 200, // Overall setting for the minimal amount of space to be left between objects
			thoroughness: 7 // How much effort should be spent to produce a nice layout..
    	},
    	priority: function( edge ){ return null; }
	};
	
	this.diagram = function() {
		return diagram;
	};
	
	function on() {
		  document.getElementById("overlayLoading").style.display = "flex";
	}

	function off() {
	  document.getElementById("overlayLoading").style.display = "none";
	}
	
	function destroy() {
		if(diagram != null) {
			diagram.destroy();
		}
	}
	
	this.load = function(jsonURL) {
		on();
		diagram.elements().forEach(function(ele) { NetworkLoad.diagram().remove(ele); });
		fetch(jsonURL, {mode: 'no-cors'})
		.then(function(res) {
			if(!res.ok) {
				alert(res.error);
			}
			return res.json(); 
		})
		.then(function(data) {
			diagram.add(data);
			diagram.layout(layoutOptions).run();
			diagram.elements().forEach(function(element, index) {
			    var popup = tippy(element.popperRef(), {
			        content: function(){
			            var div = document.createElement('div');

			            div.innerHTML = element.data().label;

			            return div;
			        },
			        animation: 'perspective-extreme',
			        trigger: 'manual',
			        arrow: true,
			        placement: 'bottom',
			        hideOnClick: true,
			        multiple: false,
			        sticky: true
			    });
			    element.on('mouseover', () => popup.show());
			    element.on('mouseout', () => popup.hide());
			});
			off();
		});
	};
	
	this.initialize = function() {
		
		diagram = cytoscape({
		  container: document.getElementById('cy'),
		
		  boxSelectionEnabled: false,
		  autounselectify: true,
		
		  style: cytoscape.stylesheet()
		    .selector('node')
		      .css({
		        'shape': 'rectangle',
		        'height': 80,
		        'width': 80,
		        'background-fit': 'cover',
		        'background-opacity': 0,
		        "color": "#fff",
		        "label": "data(label)"
		      })
		    .selector('edge')
		      .css({
		        'curve-style': 'bezier',
		        "control-point-step-size": 60,
		        'width': 3,
		        'source-arrow-shape': "data(sourceArrowShape)",
		        'target-arrow-shape': "data(targetArrowShape)",
		        'line-color': "data(lineColor)",
		        'source-arrow-color': '#ffaaaa',
		        'target-arrow-color': '#ffaaaa',
		        "color": "#fff",
		        "label": "data(label)"
		      })
		    .selector('.trafficAllow')
		      .css({
		    	'shape': 'rectangle',
		        'height': 10,
		        'width': 10,
		        'background-opacity': 1,
		        "background-color": "#28a745",
		        "label": ""
		      })
		    .selector('.trafficDeny')
		      .css({
		    	'shape': 'rectangle',
		        'height': 10,
		        'width': 10,
		        'background-opacity': 1,
		        "background-color": "#dc3545",
		        "label": ""
		      })
		    .selector('.ec2Instance')
		      .css({
		        'background-image': '/img/Amazon-EC2_Instance_dark-bg@4x.png'
		      })
		    .selector('.internet')
		      .css({
		    	  'background-image': '/img/Internet-alt1_dark-bg@4x.png'
		      })
		    .selector('.lambda')
		      .css({
		        'background-image': '/img/AWS-Lambda_Lambda-Function_dark-bg@4x.png'
		      })
		    .selector('.classicLoadBalancer')
		      .css({
		        'background-image': '/img/Elastic-Load-Balancing_Classic-load-balancer_dark-bg@4x.png'
		      })
		    .selector('.applicationLoadBalancer')
		      .css({
		        'background-image': '/img/Elastic-Load-Balancing-ELB_Application-load-balancer_dark-bg@4x.png'
		      })
		    .selector('.networkLoadBalancer')
		      .css({
		        'background-image': '/img/Elastic-Load-Balancing-ELB_Network-load-balancer_dark-bg@4x.png'
		      })
		    .selector(".targetGroup")
		      .css({
		    	  "background-image": "/img/Elastic-Load-Balancing-TargetGroup_dark-bg@4x.png"
		      })
		    .selector(".routeTable")
		      .css({
		    	  "background-image": "/img/Amazon-Route-53_Route-Table_dark-bg@4x.png"
		      })
		    .selector(".transitGateway")
		      .css({
		    	  "background-image": "/img/AWS-Transit-Gateway@4x.png"
		      })
		    .selector(".internetGateway")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Internet-Gateway_dark-bg@4x.png"
		      })
		    .selector(".virtualGateway")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Virtual-Gateway_dark-bg@4x.png"
		      })
		    .selector(".natGateway")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_NAT-Gateway_dark-bg@4x.png"
		      })
		    .selector(".vpnGateway")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_VPN-Gateway_dark-bg@4x.png"
		      })
		    .selector(".vpnConnection")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_VPN-Connection_dark-bg@4x.png"
		      })
		    .selector(".customerGateway")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Customer-Gateway_dark-bg@4x.png"
		      })
		    .selector(".corporateDataCenter")
		      .css({
		    	  "background-image": "/img/Corporate-data-center_dark-bg@4x.png"
		      })
		    .selector(".peering")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Peering_dark-bg@4x.png"
		      })
		    .selector(".vpcEndpoint")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Endpoints_dark-bg@4x.png"
		      })
		    .selector(".networkInterface")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Elastic-Network-Interface_dark-bg@4x.png"
		      })
		    .selector(".directConnect")
		      .css({
		    	  "background-image": "/img/AWS-Direct-Connect@4x.png"
		      })
		    .selector(".networkAcl")
		      .css({
		    	  "background-image": "/img/Amazon-VPC_Network-Access-Control-List_dark-bg@4x.png"
		      })
		    .selector(".securityGroup")
		      .css({
		    	  "background-image": "/img/SecurityGroup_dark-bg@4x.png"
		      })
		    .selector(".server")
		      .css({
		    	  "background-image": "/img/Traditional-server_dark-bg@4x.png"
		      }),
		  layout: {
			  name: 'preset'
		  }
		}); // cy init
		
		// add the panzoom control
		diagram.panzoom({
			  zoomFactor: 0.05, // zoom factor per zoom tick
			  zoomDelay: 45, // how many ms between zoom ticks
			  minZoom: 0.1, // min zoom level
			  maxZoom: 10, // max zoom level
			  fitPadding: 50, // padding when fitting
			  panSpeed: 10, // how many ms in between pan ticks
			  panDistance: 10, // max pan distance per tick
			  panDragAreaSize: 75, // the length of the pan drag box in which the vector for panning is calculated (bigger = finer control of pan speed and direction)
			  panMinPercentSpeed: 0.25, // the slowest speed we can pan by (as a percent of panSpeed)
			  panInactiveArea: 8, // radius of inactive area in pan drag box
			  panIndicatorMinOpacity: 0.5, // min opacity of pan indicator (the draggable nib); scales from this to 1.0
			  zoomOnly: false, // a minimal version of the ui only with zooming (useful on systems with bad mousewheel resolution)
			  fitSelector: undefined, // selector of elements to fit
			  animateOnFit: function(){ // whether to animate on fit
			    return false;
			  },
			  fitAnimationDuration: 1000, // duration of animation on fit

			  // icon class names
			  sliderHandleIcon: 'fa fa-minus',
			  zoomInIcon: 'fa fa-plus',
			  zoomOutIcon: 'fa fa-minus',
			  resetIcon: 'fa fa-expand'
		});

		navigator = diagram.navigator({
		    container: "#cytoscape-navigator" // can be a HTML or jQuery element or jQuery selector
			  , viewLiveFramerate: 0 // set false to update graph pan only on drag end; set 0 to do it instantly; set a number (frames per second) to update not more than N times per second
			  , thumbnailEventFramerate: 30 // max thumbnail's updates per second triggered by graph updates
			  , thumbnailLiveFramerate: false // max thumbnail's updates per second. Set false to disable
			  , dblClickDelay: 200 // milliseconds
			  , removeCustomContainer: true // destroy the container specified by user on plugin destroy
			  , rerenderDelay: 100 // ms to throttle rerender updates to the panzoom for performance
		}); // get navigator instance, nav
		
	};
	
	return this;
}();

function playAnimation(cy, node, target, speed, animations, animation, index) {
	animation.promise().then(function() {
		if(animations.length - 1 == index) {
			traffic(cy, node, target, speed);
			removeTraffic(animations[index].obj);
		} else {
			playAnimation(animations[index + 1].animation.play(), index + 1);
		}
	});
}

function traffic(cy, parent, node, speed) {
	
	if(node.connectedEdges()) {
		
			node.connectedEdges().forEach(function(edge) {
				if(node.id() == edge.source().id()) {
					if(edge.data().in) {
						var trafficObj = cy.add({
							group:'nodes'
							, data:{id:('trafficObj' + Math.round(+new Date()/1000) + Math.random())}
						    , position: {x:edge.sourceEndpoint().x, y: edge.sourceEndpoint().y}
						    , classes: (edge.data().allow ? 'trafficAllow' : 'trafficDeny')
						})[0]; 
						/*
						var positions = [];
						if(edge.controlPoints()) {
							edge.controlPoints().forEach(function(pos) {
								positions.push({position:{x: pos.x, y: pos.y}});
							});
						}
						positions.push({position:{x: edge.targetEndpoint().x, y: edge.targetEndpoint().y}});
						
						var animations = [];
						positions.forEach(function(position) {
							animations.push({obj: trafficObj, animation: trafficObj.animation(position, {duration:speed})});
						});
						
						playAnimation(cy, node, edge.target(), speed, animations, animations[0].animation.play(), 0);
						*/
						var animation = trafficObj.animation({position:{x:edge.targetEndpoint().x, y:edge.targetEndpoint().y}}, {duration:speed});
						animation.play().promise().then(function() {
							traffic(cy, node, edge.target(), speed);
							removeTraffic(trafficObj);
						});
					}
				}
			});
	}
  
}

function removeTraffic(trafficObj) {
	trafficObj.remove();
}

var isTrafficEnable = false;

function startTraffic(speed) {
	
	if(isTrafficEnable) {
		var cy = NetworkLoad.diagram();
		var rootNode = cy.nodes()[0];
		
		traffic(cy, null, rootNode, speed);
		setTimeout(startTraffic, speed);
	}
	
}
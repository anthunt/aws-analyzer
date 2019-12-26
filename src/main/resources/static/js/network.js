var NetworkLoad = function() {
	
	var _this = this;
	var diagram;
	var navigator;
	
	var layoutOption = {
		name: 'elk',
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
		elk: {
			zoomToFit: true,
			algorithm: 'layered',
			// Following descriptions taken from http://layout.rtsys.informatik.uni-kiel.de:9444/Providedlayout.html?algorithm=de.cau.cs.kieler.klay.layered
			addUnnecessaryBendpoints: true, // Adds bend points even if an edge does not change direction.
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
			edgeRouting: 'POLYLINE', // Defines how edges are routed (POLYLINE, ORTHOGONAL, SPLINES)
			edgeSpacingFactor: 0.5, // Factor by which the object spacing is multiplied to arrive at the minimal spacing between edges.
			feedbackEdges: true, // Whether feedback edges should be highlighted by routing around the nodes.
			fixedAlignment: 'LEFTUP', // Tells the BK node placer to use a certain alignment instead of taking the optimal result.  This option should usually be left alone.
			/* NONE Chooses the smallest layout from the four possible candidates.
			LEFTUP Chooses the left-up candidate from the four possible candidates.
			RIGHTUP Chooses the right-up candidate from the four possible candidates.
			LEFTDOWN Chooses the left-down candidate from the four possible candidates.
			RIGHTDOWN Chooses the right-down candidate from the four possible candidates.
			BALANCED Creates a balanced layout from the four possible candidates. */
			inLayerSpacingFactor: 0.3, // Factor by which the usual spacing is multiplied to determine the in-layer spacing between objects.
			layoutHierarchy: false, // Whether the selected layouter should consider the full hierarchy
			linearSegmentsDeflectionDampening: 0.03, // Dampens the movement of nodes to keep the diagram from getting too large.
			mergeEdges: false, // Edges that have no ports are merged so they touch the connected nodes at the same points.
			mergeHierarchyCrossingEdges: true, // If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports as possible.
			nodeLayering:'NETWORK_SIMPLEX', // Strategy for node layering.
			/* NETWORK_SIMPLEX This algorithm tries to minimize the length of edges. This is the most computationally intensive algorithm. The number of iterations after which it aborts if it hasn't found a result yet can be set with the Maximal Iterations option.
			LONGEST_PATH A very simple algorithm that distributes nodes along their longest path to a sink node.
			INTERACTIVE Distributes the nodes into layers by comparing their positions before the layout algorithm was started. The idea is that the relative horizontal order of nodes as it was before layout was applied is not changed. This of course requires valid positions for all nodes to have been set on the input graph before calling the layout algorithm. The interactive node layering algorithm uses the Interactive Reference Point option to determine which reference point of nodes are used to compare positions. */
			nodePlacement:'LINEAR_SEGMENTS', // Strategy for Node Placement
			/* BRANDES_KOEPF Minimizes the number of edge bends at the expense of diagram size: diagrams drawn with this algorithm are usually higher than diagrams drawn with other algorithms.
			LINEAR_SEGMENTS Computes a balanced placement.
			INTERACTIVE Tries to keep the preset y coordinates of nodes from the original layout. For dummy nodes, a guess is made to infer their coordinates. Requires the other interactive phase implementations to have run as well.
			SIMPLE Minimizes the area at the expense of... well, pretty much everything else. */
			randomizationSeed: 1, // Seed used for pseudo-random number generators to control the layout algorithm; 0 means a new seed is generated
			routeSelfLoopInside: false, // Whether a self-loop is routed around or inside its node.
			separateConnectedComponents: false, // Whether each connected component should be processed separately
			spacing: 80, // Overall setting for the minimal amount of space to be left between objects
			thoroughness: 7 // How much effort should be spent to produce a nice layout..
		}
	};
	
	var layoutOptions = {
		name: 'klay',
		nodeDimensionsIncludeLabels: true, // Boolean which changes whether label dimensions are included when calculating node dimensions
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
			addUnnecessaryBendpoints: true, // Adds bend points even if an edge does not change direction.
			aspectRatio: 1, // The aimed aspect ratio of the drawing, that is the quotient of width by height
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
			feedbackEdges: true, // Whether feedback edges should be highlighted by routing around the nodes.
			fixedAlignment: 'BALANCED', // Tells the BK node placer to use a certain alignment instead of taking the optimal result.  This option should usually be left alone.
			/* NONE Chooses the smallest layout from the four possible candidates.
			LEFTUP Chooses the left-up candidate from the four possible candidates.
			RIGHTUP Chooses the right-up candidate from the four possible candidates.
			LEFTDOWN Chooses the left-down candidate from the four possible candidates.
			RIGHTDOWN Chooses the right-down candidate from the four possible candidates.
			BALANCED Creates a balanced layout from the four possible candidates. */
			inLayerSpacingFactor: 0.1, // Factor by which the usual spacing is multiplied to determine the in-layer spacing between objects.
			layoutHierarchy: true, // Whether the selected layouter should consider the full hierarchy
			linearSegmentsDeflectionDampening: 0.01, // Dampens the movement of nodes to keep the diagram from getting too large.
			mergeEdges: false, // Edges that have no ports are merged so they touch the connected nodes at the same points.
			mergeHierarchyCrossingEdges: false, // If hierarchical layout is active, hierarchy-crossing edges use as few hierarchical ports as possible.
			nodeLayering:'NETWORK_SIMPLEX', // Strategy for node layering.
			/* NETWORK_SIMPLEX This algorithm tries to minimize the length of edges. This is the most computationally intensive algorithm. The number of iterations after which it aborts if it hasn't found a result yet can be set with the Maximal Iterations option.
			LONGEST_PATH A very simple algorithm that distributes nodes along their longest path to a sink node.
			INTERACTIVE Distributes the nodes into layers by comparing their positions before the layout algorithm was started. The idea is that the relative horizontal order of nodes as it was before layout was applied is not changed. This of course requires valid positions for all nodes to have been set on the input graph before calling the layout algorithm. The interactive node layering algorithm uses the Interactive Reference Point option to determine which reference point of nodes are used to compare positions. */
			nodePlacement:'LINEAR_SEGMENTS', // Strategy for Node Placement
			/* BRANDES_KOEPF Minimizes the number of edge bends at the expense of diagram size: diagrams drawn with this algorithm are usually higher than diagrams drawn with other algorithms.
			LINEAR_SEGMENTS Computes a balanced placement.
			INTERACTIVE Tries to keep the preset y coordinates of nodes from the original layout. For dummy nodes, a guess is made to infer their coordinates. Requires the other interactive phase implementations to have run as well.
			SIMPLE Minimizes the area at the expense of... well, pretty much everything else. */
			randomizationSeed: 1, // Seed used for pseudo-random number generators to control the layout algorithm; 0 means a new seed is generated
			routeSelfLoopInside: false, // Whether a self-loop is routed around or inside its node.
			separateConnectedComponents: false, // Whether each connected component should be processed separately
			spacing: 80, // Overall setting for the minimal amount of space to be left between objects
			thoroughness: 7 // How much effort should be spent to produce a nice layout..
    	},
    	priority: function( edge ){ return null; }
	};
	
	_this.diagram = () => { return diagram; };
	
	var on = () => { document.getElementById("overlayLoading").style.display = "flex"; };
	var off = () => { document.getElementById("overlayLoading").style.display = "none"; };
	
	var destroy = () => {
		if(diagram != null) {
			diagram.destroy();
		}
	};
	
	_this.load = (jsonURL)=>{
		on();
		diagram.elements().forEach((ele) => { NetworkLoad.diagram().remove(ele); });
		
		Utils.async(jsonURL, (data) => {
			diagram.add(data);
			diagram.layout(layoutOptions).run();
			diagram.nodes().noOverlap({
                padding: 30
            });
			diagram.elements().forEach(function(element, index) {
				
				if(element.data().label != null && element.data().label != "") {
				    var popup = tippy(element.popperRef(), {
				        content: function(){
				            var div = document.createElement('div');
	
				            div.innerHTML = element.data().label;
	
				            return div;
				        },
				        animation: 'shift-away',
				        trigger: 'manual',
				        arrow: true,
				        placement: 'bottom',
				        hideOnClick: false,
				        multiple: true,
				        sticky: true,
				        delay: [10, 10]
				    });
				    element.popup = popup;
				    element.on('mouseover', () => popup.show());
				    element.on('mouseout', () => popup.hide());
				}
				
			});
		}, () => off());
		
	};
	
	_this.initialize = ()=>{
		
		diagram = cytoscape({
		  container: document.getElementById('cy'),
				  
		  zoom: 1,
		  pan: { x: 0, y: 0 },

		  // interaction options:
		  minZoom: 1e-50,
		  maxZoom: 1e50,
		  zoomingEnabled: true,
		  userZoomingEnabled: true,
		  panningEnabled: true,
		  userPanningEnabled: true,
		  boxSelectionEnabled: true,
		  selectionType: 'single',
		  touchTapThreshold: 8,
		  desktopTapThreshold: 4,
		  autolock: false,
		  autoungrabify: false,
		  autounselectify: false,

		  // rendering options:
		  headless: false,
		  styleEnabled: true,
		  hideEdgesOnViewport: false,
		  textureOnViewport: false,
		  motionBlur: false,
		  motionBlurOpacity: 0.2,
		  wheelSensitivity: 0.05,
		  pixelRatio: 'auto',
		  
		  style: fetch('/css/cy-style.json').then(function(res){
		      return res.json();
		  }),
		  layout: {
			  name: 'preset'
		  }
		}); // cy init
		
//		diagram.toolbar({
//            toolbarClass: "cy-position-toolbar",
//            position: 'top'
//        });
		
		var api = diagram.viewUtilities({
            node: {
              selected: {
                'border-color': 'white',
                'border-width': 3,
                'background-color': 'lightgrey'
              }

            },
            edge: {
              selected: {
                'line-color': 'white',
                'width' : 3
              }
            },
            setVisibilityOnHide: false, // whether to set visibility on hide/show
            setDisplayOnHide: true, // whether to set display on hide/show
            zoomAnimationDuration: 1500, //default duration for zoom animation speed                  
            neighbor: function(node){
                return node.closedNeighborhood();
            },
            neighborSelectTime: 1000
        });
		/*
		diagram.nodeHtmlLabel([
	        {
	            query: 'node',
	            cssClass: 'cy-title',
	            valign: "bottom",
	            valignBox: "bottom",
	            tpl: function (data) {
	                return data.title;
	            }
	        }
	    ]);
		*/
		diagram.dblclick();
		diagram.on("dblclick", (evt)=>{
			var element = evt.target;
			try {
				if(element.isNode()) {
					if(element.classes()[0] == "ec2Instance") {
						var jsonURL = "/api/network/detail/";
						jsonURL += element.classes()[0];
						jsonURL += "/";
						jsonURL += element.id();
						_this.load(jsonURL);
					}
				}
			}catch(e) {}
		});
		
		diagram.on("mouseover", (evt)=>{
			var element = evt.target;
			try {
				if(element.isNode()) {					
					if(element.classes()[0] == "ec2Instance") {
						$('html,body').css('cursor', 'pointer');
					}
				}
			}catch(e) {}
		});
		
		diagram.on("mouseout", (evt)=>{
			$('html,body').css('cursor', 'default');
		});
		
		var contextMenu = diagram.contextMenus({
			menuItems: [
                {
                    id: 'startInstance',
                    content: '<div class="stars"><i class="material-icons text-success">play_arrow</i> Start Instance</div>',
                    selector: '.ec2Instance',
                    onClickFunction: function (event) {
                      var target = event.target || event.cyTarget;
                      console.log(target.data().resource.instanceId);
                    },
                    show: true,
                    coreAsWell: true,
                    hasTrailingDivider: true,
                    disabled: false
               },
               {
                   id: 'stopInstance',
                   content: '<div class="stars"><i class="material-icons text-danger">stop</i> Stop Instance</div>',
                   selector: '.ec2Instance',
                   onClickFunction: function (event) {
                     var target = event.target || event.cyTarget;
                     console.log(target.data().resource.instanceId);
                   },
                   show: true,
                   coreAsWell: true,
                   hasTrailingDivider: true,
                   disabled: false
              }
           ]
        });
		
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
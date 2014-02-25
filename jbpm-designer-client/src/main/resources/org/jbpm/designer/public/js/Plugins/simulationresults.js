if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.SimulationResults = Clazz.extend({
	construct: function(facade){
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_SHOW_RESULTS, this.showSimulationResults.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_DISPLAY_GRAPH, this.showGraph.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_PATH_SVG_GENERATED, this.pathSvgGenerated.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_ANNOTATE_PROCESS, this.annotateProcess.bind(this));
		
		this.resultsjson = "";
	},
	showSimulationResults: function(options) {
		Ext.getCmp('maintabs').setActiveTab(1);
		this.updateSimView(options);
	},
	showGraph: function(options) {
		if(options && options.value) {
			var selectedNode = options.value;
            if(selectedNode.id.startsWith("pgraph:")) {
				var valueParts = selectedNode.id.split(":");
        		var nodeid = valueParts[1];
        		if(nodeid == "processaverages") {
        			this.showProcessAveragesGraph(nodeid, this.resultsjson);
        		}
			} else if(selectedNode.id.startsWith("htgraph:")) {
				var valueParts = selectedNode.id.split(":");
        		var nodeid = valueParts[1];
        		this.showHumanTaskAveragesGraph(nodeid, this.resultsjson);
			} else if(selectedNode.id.startsWith("tgraph:")) {
				var valueParts = selectedNode.id.split(":");
        		var nodeid = valueParts[1];
        		this.showTaskAveragesGraph(nodeid, this.resultsjson);
			} else if(selectedNode.id.startsWith("pathgraph:")) {
				var valueParts = selectedNode.id.split(":");
        		var pathid = valueParts[1];
        		this.showPathGraph(pathid, this.resultsjson);
			}
		}
	},
	_showProcessGraphs: function(nodeid) {
		this.showProcessAveragesGraph(nodeid, this.resultsjson);
	},
	updateSimView: function(options) {
        this.createProcessInfo(options);
		this.createGraphsTree(options);
	},
    createProcessInfo: function(options) {
        var simInfo = jsonPath(options.results.evalJSON(), "$.siminfo.*");
        var infomarkup = '<table border="0" width="100%"> \
                          <tr>\
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsProcessId + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].id  + '</span></td> \
                          </tr> \
                          <tr> \
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsProcessName + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].name  + '</span></td> \
                          </tr> \
                          <tr> \
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsProcessVersion + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].version  + '</span></td> \
                          </tr> \
                          <tr> \
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsSimStartTime + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].starttime  + '</span></td> \
                          </tr> \
                          <tr> \
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsSimEndTime + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].endtime  + '</span></td> \
                          </tr> \
                          <tr> \
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsNumOfExecutions + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].executions  + '</span></td> \
                          </tr> \
                          <tr> \
                          <td><span style="font-size: 10px"><b>' + ORYX.I18N.View.sim.resultsInterval + '</b></span></td> \
                          <td><span style="font-size: 10px">' + simInfo[0].interval  + '</span></td> \
                          </tr> \
                          </table>';
        if(simInfo) {
            Ext.getCmp('siminfopanel').body.update(infomarkup);
        }
    },
	createGraphsTree: function(options) {
		var graphList = new Ext.tree.TreeNode({
            listeners: {
                beforecollapse: function(node, deep, anim){
                    return false;
                }
            }
        });
		var graphType;
		var graphTypeChild;
		this.resultsjson = options.results;
		var processSimInfo = jsonPath(options.results.evalJSON(), "$.processsim.*");
		if(processSimInfo) {
			graphType = new Ext.tree.TreeNode({
				text: ORYX.I18N.View.sim.resultsGroupProcess,
				allowDrag:false,
	    		allowDrop:false,           
	            expanded: true,
	            isLeaf: false,
				singleClickExpand:false,
                listeners: {
                    beforecollapse: function(node, deep, anim){
                        return false;
                    }
                }});
			graphTypeChild = new Ext.tree.TreeNode({
				id:"pgraph:processaverages",
				text:processSimInfo[0].name + " (" + processSimInfo[0].id + ")",
				allowDrag:false,
	    		allowDrop:false,           
	            expanded: true,
	            isLeaf: true,
	            iconCls: 'xnd-icon',
	            icon: ORYX.BASE_FILE_PATH + 'images/simulation/diagram.png',
				singleClickExpand:false,
                listeners: {
                    beforecollapse: function(node, deep, anim){
                        return false;
                    }
                }});
			graphType.appendChild(graphTypeChild);
			graphList.appendChild(graphType);

		}
		var htSimInfo = jsonPath(options.results.evalJSON(), "$.htsim.*");
		var taskSimInfo = jsonPath(options.results.evalJSON(), "$.tasksim.*");
		if(htSimInfo || taskSimInfo) {
			graphType = new Ext.tree.TreeNode({
				text: ORYX.I18N.View.sim.resultsGroupProcessElements,
				allowDrag:false,
	    		allowDrop:false,           
	            expanded: true,
	            isLeaf: false,
				singleClickExpand:true,
                listeners: {
                    beforecollapse: function(node, deep, anim){
                        return false;
                    }
                }});
			for (var i = 0; i < htSimInfo.length; i++) {
				var nextHt = htSimInfo[i];
					graphTypeChild = new Ext.tree.TreeNode({
						id:"htgraph:" + nextHt.id,
						text:nextHt.name + " (" + nextHt.id + ")", 			
						allowDrag:false,
			    		allowDrop:false,           
			            expanded: true,
			            isLeaf: true,
			            iconCls: 'xnd-icon',
			            icon: ORYX.BASE_FILE_PATH + 'images/simulation/activities/User.png',
						singleClickExpand:true});
				    graphType.appendChild(graphTypeChild);
			}
			for (var j = 0; j < taskSimInfo.length; j++) {
				var nextTask = taskSimInfo[j];
				// find the task type
				this.taskType = "None";
				this.findTaskType(nextTask.id);
				this.taskType = this.taskType.replace(/\s/g, "");
			    graphTypeChild = new Ext.tree.TreeNode({
					id:"tgraph:" + nextTask.id,
					text:nextTask.name + " (" + nextTask.id + ")", 				
					allowDrag:false,
		    		allowDrop:false,           
		            expanded: true,
		            isLeaf: true,
		            iconCls: 'xnd-icon',
		            icon: ORYX.BASE_FILE_PATH + 'images/simulation/activities/' + this.taskType + '.png',
					singleClickExpand:true});
			    
			    graphType.appendChild(graphTypeChild);
			}
			graphList.appendChild(graphType);
		}
		var pathSimInfo = jsonPath(options.results.evalJSON(), "$.pathsim.*");
		if(pathSimInfo) {
			graphType = new Ext.tree.TreeNode({
				text: ORYX.I18N.View.sim.resultsGroupProcessPaths,
				allowDrag:false,
	    		allowDrop:false,           
	            expanded: true,
	            isLeaf: false,
				singleClickExpand:true,
                listeners: {
                    beforecollapse: function(node, deep, anim){
                        return false;
                    }
                }});
			for (var i = 0; i < pathSimInfo.length; i++) {
				var nextPath = pathSimInfo[i];
					graphTypeChild = new Ext.tree.TreeNode({
						id:"pathgraph:" + nextPath.id,
						text:"Path " + (i+1) + " (" + nextPath.id + ")", 			
						allowDrag:false,
			    		allowDrop:false,           
			            expanded: true,
			            isLeaf: true,
			            iconCls: 'xnd-icon',
			            icon: ORYX.BASE_FILE_PATH + 'images/simulation/pathicon.png',
						singleClickExpand:true});
				    graphType.appendChild(graphTypeChild);
			}
			graphList.appendChild(graphType);
		}
		
		Ext.getCmp('simresultscharts').setRootNode(graphList);
		Ext.getCmp('simresultscharts').getRootNode().render();
        Ext.getCmp('simresultscharts').el.dom.style.height = '100%';
        Ext.getCmp('simresultscharts').el.dom.style.overflow = 'scroll';
		Ext.getCmp('simresultscharts').render();
		
		// select process graph and show its chart
		var tp = Ext.getCmp('simresultscharts');
		var node = tp.getNodeById("pgraph:processaverages");
	    node.select();
	    this._showProcessGraphs("processaverages");
	},
	findTaskType: function(taskid) {
		ORYX.EDITOR._canvas.getChildren().each((function(child) {
			this.isTaskType(child, taskid);
		}).bind(this));
	},
	isTaskType: function(shape, taskid) {
		if(shape instanceof ORYX.Core.Node) {
			if(shape.resourceId == taskid && shape.properties["oryx-tasktype"]) {
				this.taskType = shape.properties["oryx-tasktype"];
			}
			if(shape.getChildren().size() > 0) {
				for (var i = 0; i < shape.getChildren().size(); i++) {
					if(shape.getChildren()[i] instanceof ORYX.Core.Node) {
						this.isTaskType(shape.getChildren()[i], taskid);
					}
				}
			}
		}
	},
	showProcessAveragesGraph : function(nodeid, jsonstr) {
		var jsonObj = jsonPath(jsonstr.evalJSON(), "$.processsim.*");
		var jsonSimObj = jsonPath(jsonstr.evalJSON(), "$.timeline");
		var jsonInstancesObj = jsonPath(jsonstr.evalJSON(), "$.activityinstances.*");
		var jsonEventAggregationsObj = jsonPath(jsonstr.evalJSON(), "$.eventaggregations.*");

        var htobjarray = [];
        var htobj = jsonPath(jsonstr.evalJSON(), "$.htsim.*");
        for(var i=0; i < htobj.length; i++ ) {
            var inner = htobj[i];
            htobjarray.push(inner.costvalues);
        }

		var jsonSimObjWrapper = {
			"timeline": jsonSimObj[0]
		};

        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var simTimeUnit = jsonPath(processJSON.evalJSON(), "$.properties.timeunit");
        ORYX.EDITOR.simulationChartTimeUnit = simTimeUnit;
		ORYX.EDITOR.simulationChartData = jsonObj;
		ORYX.EDITOR.simulationEventData = jsonSimObjWrapper;
		ORYX.EDITOR.simulationEventAggregationData = jsonEventAggregationsObj;
		ORYX.EDITOR.simulationInstancesData = jsonInstancesObj;
        ORYX.EDITOR.simulationHTCostData = htobjarray;
		ORYX.EDITOR.simulationChartTitle = ORYX.I18N.View.sim.resultsTitlesProcessSimResults;
		ORYX.EDITOR.simulationChartId = jsonObj[0].id;
		ORYX.EDITOR.simulationChartNodeName = jsonObj[0].name;
		Ext.getDom('simchartframe').src = ORYX.BASE_FILE_PATH + "simulation/processchart.jsp";

	},
	showTaskAveragesGraph : function(nodeid, jsonstr) {
		var taskobj = jsonPath(jsonstr.evalJSON(), "$.tasksim.*");
		for(var j=0; j < taskobj.length; j++) {
			var inner = taskobj[j];
			if(inner.id == nodeid) {
				var innerWrapper = [];
				innerWrapper[0] = inner;
                var processJSON = ORYX.EDITOR.getSerializedJSON();
                var simTimeUnit = jsonPath(processJSON.evalJSON(), "$.properties.timeunit");
                ORYX.EDITOR.simulationChartTimeUnit = simTimeUnit;
				ORYX.EDITOR.simulationChartData = innerWrapper;
				ORYX.EDITOR.simulationEventData = innerWrapper[0].timeline;
				ORYX.EDITOR.simulationChartTitle = ORYX.I18N.View.sim.resultsTitlesTaskSimResults;
				ORYX.EDITOR.simulationChartId = innerWrapper[0].id;
				ORYX.EDITOR.simulationChartNodeName = innerWrapper[0].name;
				Ext.getDom('simchartframe').src = ORYX.BASE_FILE_PATH + "simulation/taskchart.jsp";
			}
		}
	},
	showHumanTaskAveragesGraph : function(nodeid, jsonstr) {
		var htobj = jsonPath(jsonstr.evalJSON(), "$.htsim.*");
		for(var i=0; i < htobj.length; i++ ) {
			var inner = htobj[i];
			if(inner.id == nodeid) {
                var processJSON = ORYX.EDITOR.getSerializedJSON();
                var simTimeUnit = jsonPath(processJSON.evalJSON(), "$.properties.timeunit");
                ORYX.EDITOR.simulationChartTimeUnit = simTimeUnit;
				ORYX.EDITOR.simulationChartData = inner;
				ORYX.EDITOR.simulationEventData = inner.timeline;
				ORYX.EDITOR.simulationChartTitle = ORYX.I18N.View.sim.resultsTitlesHumanTaskSimResults;
				ORYX.EDITOR.simulationChartId = inner.id;
				ORYX.EDITOR.simulationChartNodeName = inner.name;
				Ext.getDom('simchartframe').src = ORYX.BASE_FILE_PATH + "simulation/humantaskchart.jsp";
			}
		}
	},
	showPathGraph : function(pathid, jsonstr) {
		var pathobj = jsonPath(jsonstr.evalJSON(), "$.pathsim.*");
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var simTimeUnit = jsonPath(processJSON.evalJSON(), "$.properties.timeunit");
        ORYX.EDITOR.simulationChartTimeUnit = simTimeUnit;
		ORYX.EDITOR.simulationChartTitle = ORYX.I18N.View.sim.resultsTitlesPathExecutionInfo + " (" + pathid + ")";
		ORYX.EDITOR.simulationPathData = pathobj;
		ORYX.EDITOR.simulationPathId = pathid;
		
		this.facade.raiseEvent({
	            type: ORYX.CONFIG.EVENT_SIMULATION_BUILD_PATH_SVG,
	            pid: pathid
	    });
	},
	pathSvgGenerated : function() {
		ORYX.EDITOR.simulationPathSVG = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
		Ext.getDom('simchartframe').src = ORYX.BASE_FILE_PATH+ "simulation/pathschart.jsp";
		this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_SIMULATION_CLEAR_PATH_SVG
		});
	},
	annotateProcess : function(options) {
        this.resetNodeColors();
        this.resetNodeOverlays();
        this.annotateNode(options.nodeid, options.eventnum, options.data);
        setTimeout(function() {
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_SIMULATION_SHOW_ANNOTATED_PROCESS,
                data: DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false)),
                wind : window,
                docu : document
            });
            this.resetNodeColors();
            this.resetNodeOverlays();
        }.bind(this), 500);
    },
    resetNodeOverlays : function() {
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
            id: "simmodelmax"
        });
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
            id: "simmodelmin"
        });
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
            id: "simmodelavg"
        });
    },
    resetNodeColors : function() {
        ORYX.EDITOR._canvas.getChildren().each((function(child) {
            this.setOriginalValues(child);
        }).bind(this));
    },
    setOriginalValues : function(shape) {
        if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
            shape.setProperty("oryx-bordercolor", shape.properties["oryx-origbordercolor"]);
            shape.setProperty("oryx-bgcolor", shape.properties["oryx-origbgcolor"]);
        }
        shape.refresh();
        if(shape.getChildren().size() > 0) {
            for (var i = 0; i < shape.getChildren().size(); i++) {
                if(shape.getChildren()[i] instanceof ORYX.Core.Node || shape.getChildren()[i] instanceof ORYX.Core.Edge) {
                    this.setOriginalValues(shape.getChildren()[i]);
                }
            }
        }
    },
    annotateNode : function(nodeid, eventnum, data) {
        ORYX.EDITOR._canvas.getChildren().each((function(child) {
            this.setNodeAnnotation(child, nodeid, eventnum, data);
        }).bind(this));
    },
    setNodeAnnotation : function(shape, nodeid, eventnum, data) {
        if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
            if(shape.resourceId == nodeid) {
                var color = this.getDisplayColor(1);
                shape.setProperty("oryx-bordercolor", color);
                shape.setProperty("oryx-bgcolor", color);
                shape.refresh();

                var dataMax = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
                    ['text', {"id" : "modelmax",
                        "style": "stroke-width:1;fill:red;font-family:arial;font-weight:bold",
                        "font-size": 10}]
                );
                dataMax.textContent = "Max: " + data.values[0].value;

                var dataMin = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
                    ['text', {"id" : "modelmin",
                        "style": "stroke-width:1;fill:blue;font-family:arial;font-weight:bold",
                        "font-size": 10}]
                );
                dataMin.textContent = "Min: " + data.values[1].value;

                var dataAvg = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
                    ['text', {"id" : "modelavg",
                        "style": "stroke-width:1;fill:green;font-family:arial;font-weight:bold",
                        "font-size": 10}]
                );
                dataAvg.textContent = "Avg: " + data.values[2].value;

                // overlays
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
                    id: "simmodelmax",
                    shapes: [shape],
                    node: dataMax,
                    nodePosition: "SIMMODELMAX"
                });

                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
                    id: "simmodelmin",
                    shapes: [shape],
                    node: dataMin,
                    nodePosition: "SIMMODELMIN"
                });

                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
                    id: "simmodelavg",
                    shapes: [shape],
                    node: dataAvg,
                    nodePosition: "SIMMODELAVG"
                });

            }
        }
        if(shape.getChildren().size() > 0) {
            for (var i = 0; i < shape.getChildren().size(); i++) {
                if(shape.getChildren()[i] instanceof ORYX.Core.Node || shape.getChildren()[i] instanceof ORYX.Core.Edge) {
                    this.setNodeAnnotation(shape.getChildren()[i], nodeid, eventnum, data);
                }
            }
        }
    },
    getDisplayColor : function(cindex) {
        var colors = ["#3399FF", "#FFCC33", "#FF99FF", "#6666CC", "#CCCCCC", "#66FF00", "#FFCCFF", "#0099CC", "#CC66FF", "#FFFF00", "#993300", "#0000CC", "#3300FF","#990000","#33CC00"];
        return colors[cindex];
    }
	
});

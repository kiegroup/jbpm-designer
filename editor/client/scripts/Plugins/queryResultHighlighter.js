/**
 * Copyright (c) 2008-2009, Steffen Ryll
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.QueryResultHighlighter = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,
	
	isHighlighted: false,
	
	construct: function(facade){
	
		this.facade = facade;
		
		this.raisedEventIds = [];
		this.raisedHighlightEventIds = [];
		
		this.facade.offer({
			'name': "Query result highlighter",
			'functionality': this.buttonClick.bind(this),
			'group': ORYX.I18N.QueryEvaluator.group,
			'icon': ORYX.PATH + "images/xforms_export.png",
			'description': "This plugin highlights model parts which were matched by a query.",
			'index': 1,
			'toggle': true,
			'minShape': 0,
			'maxShape': 0
		});
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.highlightMatches.bind(this));
		
	},
	
	highlightMatches : function() {
		var elements = this.deserializeMatches();
		if (!elements) {
			return;
		}
		
		try {
			elements.each(function (item) {
				if (item.nodeType != null && item.nodeId != null) {
					var shape = this.getShapeById(item.nodeId);
				} else if (item.edgeType != null) {
					var shape = this.getEdgeByFromAndTo(item.from, item.to)
				} else return; // unknown element type
				
				if (!shape) return;
				
				//this.highlightSelectedTask(shape);
				this.raiseOverlay(shape, "Moeep! Error when raising an overlay.");
			}.bind(this));
		} catch (e) {
			Ext.MessageBox.alert(ORYX.I18N.Oryx.title, "Something went wrong while applying highlighting to shapes: " + e);
		}
		
		this.isHighlighted = true;
		Ext.MessageBox.alert(ORYX.I18N.Oryx.title, "Finished highlighting!");
	},

	raiseOverlay: function(shape, errorMsg){
        var id = "queryhighlighter." + this.raisedEventIds.length;
        
        var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['path', {
            "title": errorMsg,
            "stroke-width": 5.0,
            "stroke": "red",
            "d": "M20,-5 L5,-20 M5,-5 L20,-20",
            "line-captions": "round"
        }]);
        
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id: id,
            shapes: [shape],
            node: cross,
            nodePosition: shape instanceof ORYX.Core.Edge ? "START" : "NW"
        });
        
        this.raisedEventIds.push(id);
        
        return cross;
    },
	
	removeHighlighting: function(shape, errorMsg){
        this.raisedEventIds.each(function(id) {
	        this.facade.raiseEvent({
	            type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
	            id: id
	        });
		}.bind(this));
        
		this.raisedEventIds = [];
		
		this.raisedHighlightEventIds.each(function(id) {
	        this.facade.raiseEvent({
	            type: ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
	            id: id
	        });
		}.bind(this));
        
		this.raisedHighlightEventIds = [];
    },
	
	highlightSelectedTask: function(shape){ //edge marking of selected/ given task
		if(!(shape instanceof ORYX.Core.Shape)) return;
		this.facade.raiseEvent({
			type:			ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW, 
			highlightId:	shape.id,
			elements:		[shape],
			color:			'#FF0000'
		});
		this.raisedHighlightEventIds.push(task.id);
	},

	buttonClick: function(button, pressed) {
		if (this.isHighlighted) {
			this.removeHighlighting();
			this.isHighlighted = false;
		} else {
			this.highlightMatches();
			this.isHighlighted = true;
		}

		// skip buttons toggle if both aren't in sync. This is because we can't set the buttons to pressed initially 
		if (this.isHighlighted && !pressed) {		
			button.toggle();
		}
/*		if ((this.isHighlighted && !pressed)|| (!this.isHighlighted && pressed)) {		
			button.toggle();
		}
*/
	},
	
	/**
	 * returns an object (or array), which was passed in the URL query part 
	 * in JSON+URL-encoded form
	 */
	deserializeMatches : function() {
		var parameters = window.location.search;
		var PARAMKEY = "matches=";
		
		var paramStart = parameters.indexOf(PARAMKEY) + PARAMKEY.length;
		if (paramStart < PARAMKEY.length) {
			return null; // no highlighting info found
		}
		var paramEnd = parameters.indexOf("&", paramStart); // delimiter from potential other parameters
		
		var paramComponents = parameters.substring(paramStart, 
			(paramEnd > paramStart ? paramEnd : parameters.length ));
		
		try {
			var matchedElementsJson = decodeURIComponent(paramComponents);
			var matchedElements = Ext.decode(matchedElementsJson);
		} catch (e) {
			Ext.MessageBox.alert(ORYX.I18N.Oryx.title, "I found highlighting information from BPMN-Q, but they could not be understood: " + e);
			return null;
		}
		
		return matchedElements;
	},

	getShapeById: function(resourceId) {
		var shapes = this.facade.getCanvas().getChildShapeByResourceId(resourceId);
/*		var task;
		for (var index = 0; index < shapes.length; index++) {
    		if (shapes[index].properties["oryx-activitytype"] == "Task") {
				if (shapes[index].properties["oryx-id"] == taskId) {
					task = shapes[index];
					break;
				}
			}
    	} */
		return shapes;
	},
	
	getEdgeByFromAndTo: function(fromId, toId) {
		fromId = fromId.replace(/^.*#/, '');
		toId = toId.replace(/^.*#/, '');
		
		var edges = this.facade.getCanvas().getChildEdges(true);
		
		var suspectEdge = edges.find(function(edge) {
			return edge.incoming != null
			  && edge.incoming[0] != null
			  && edge.incoming[0].resourceId == fromId
			  && edge.outgoing != null
			  && edge.outgoing[0] != null
			  && edge.outgoing[0].resourceId == toId;
		}.bind(this));
		
		return suspectEdge;
	}

});

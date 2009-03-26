/**
 * Copyright (c) 2009, Andreas Meyer
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

ORYX.Plugins.ResourcesSoDShow = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedOverlayEventIds = [];
		this.raisedHighlightEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.ResourcesSoDShow.name,
            'functionality': this.showSoD.bind(this),
            'group': ORYX.I18N.ResourcesSoDShow.group,
			'dropDownGroupIcon': ORYX.PATH + "images/sod.png",
            'icon': ORYX.PATH + "images/sod_view.png",
            'description': ORYX.I18N.ResourcesSoDShow.desc,
            'index': 2,
            'toggle': true,
            'minShape': 1,
            'maxShape': 1
        });
		
    },
    
	showSoD: function(){
		this.removeHighlightsAndOverlays();
		var selectedElements = this.facade.getSelection();
 		if(selectedElements[0].properties["oryx-activitytype"] == "Task") {
			this.highlightSelectedTask(selectedElements[0]);
			this.prepareOverlays(selectedElements[0]);
		} else {
			alert("Please select a task to show the related Separation of Duties constraints.");
		}
	},
	
	highlightSelectedTask: function(task){ //edge marking of selected/ given task
		if(!(task instanceof ORYX.Core.Shape)) return;
		this.facade.raiseEvent({
			type:			ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW, 
			highlightId:	task.properties["oryx-id"],
			elements:		[task],
			color:			'#FF0000'
		});
		this.raisedHighlightEventIds.push(task.properties["oryx-id"]);
	},
	
	showOverlaysForSeparations: function(task, elementId) { //colour given task red
		if(!(task instanceof ORYX.Core.Shape)) return;
		//colour element
		var attr = {fill: "#FF0000", stroke:"white", "stroke-width": 1};
		this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			task.properties["oryx-id"],
			shapes: 		[task],
			attributes: 	attr
		});
		//calculate node position
		var appearanceCounter = 0;
		var nodePosition;
		var x; //x coordinate for text
		var y; //y coordinate for text
		var cx; //x coordinate for circle center
		var cy; //y coordinate for circle center
		for(index = 0; index < this.raisedOverlayEventIds.length; index++) {
			if (task.properties["oryx-id"] == this.raisedOverlayEventIds[index]) {
				appearanceCounter++;
			} 
		}
		switch(appearanceCounter) { //avoid overlappings of dependency markers as long as not more than 8 exist
			case 0:		nodePosition = 'NW';
						x = 9;
						y = 17;
						cx = 13;
						cy = 13;
						break;
			case 1:		nodePosition = 'NE';
						x = -17;
						y = 17;
						cx = -13;
						cy = 13;
						break;
			case 2:		nodePosition = 'SW';
						x = 9;
						y = -9;
						cx = 13;
						cy = -13;
						break;
			case 3:		nodePosition = 'SE';
						x = -17;
						y = -9;
						cx = -13;
						cy = -13;
						break;
			case 4:		nodePosition = 'N';
						x = -4;
						y = 17;
						cx = 0;
						cy = 13;
						break;
			case 5:		nodePosition = 'S';
						x = -4;
						y = -9;
						cx = 0;
						cy = -13;
						break;
			case 6:		nodePosition = 'W';
						x = 9;
						y = 4;
						cx = 13;
						cy = 0;
						break;
			case 7:		nodePosition = 'E';
						x = -17;
						y = 4;
						cx = -13;
						cy = 0;
						break;
			case 8: 	alert("There exist more Separation of Duties constraints for task " + task.properties["oryx-id"] + ", but they will not be illustrated by a number in that task's rectangle.");
						break;
			default:	break;
		}
		if(elementId >= 10) { //keep marker centered based on single-digit or double-digit entry
			x = x - 4;
		}
		if(appearanceCounter < 8) { //skip number representation, if task occurs too often
			//create circle of marker
			var circle = ORYX.Editor.graft("http://www.w3.org/2000/svg", null, 
				['circle', {"cx":cx, "cy":cy, "r":"10", "stroke":"white", "fill":"white", "stroke-width":"2"}]
			);
			this.facade.raiseEvent({
				type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
				id: 			task.properties["oryx-id"],
				shapes: 		[task],
				node:			circle,
				nodePosition:	nodePosition
			});
			//create text/ number of marker
			var text = ORYX.Editor.graft("http://www.w3.org/2000/svg", null, 
				['text', {"x":x, "y":y, "style": "font-size: 12px;"}, elementId]
			);
			this.facade.raiseEvent({
				type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
				id: 			task.properties["oryx-id"],
				shapes: 		[task],
				node:			text,
				nodePosition:	nodePosition
			});
		}
		this.raisedOverlayEventIds.push(task.properties["oryx-id"]);
	},
	
	prepareOverlays: function(task) { //identify tasks to be coloured as a constraint exist with given and initiate colouring
		if(task.properties["oryx-separationofduties"] != "") {
			var jsonObject = task.properties["oryx-separationofduties"].evalJSON();
			var items = jsonObject.items.toArray();
			var overlayTask;
			for(var index = 0; index < items.length; index++) {
				var taskIds = items[index].SeparatedTasks;
				while(taskIds.indexOf(';') > -1) {
					//overlayTask = this.facade.getCanvas().getChildById(taskIds.substring(0,taskIds.indexOf(';')));
					overlayTask = this.getTaskById(taskIds.substring(0,taskIds.indexOf(';')));
					taskIds = taskIds.substring((taskIds.indexOf(';')) + 2);
					this.showOverlaysForSeparations(overlayTask, index+1);
				}
				overlayTask = this.getTaskById(taskIds);
				//initiate colouring
				this.showOverlaysForSeparations(overlayTask, index+1);
			}
		} else {
			alert("No Separation of Duties Constraints are defined for this task");
		}
	},
	
	removeHighlightsAndOverlays: function(){
		var allShapes = this.facade.getCanvas().getChildShapes(true);
		var allShapeIds = [];
		var i = 0;
		//get all tasks of canvas
		for (var index = 0; index < allShapes.length; index++) {
    		if (allShapes[index].properties["oryx-activitytype"] == "Task") {
				allShapeIds[i] = allShapes[index].properties["oryx-id"];
				i++;
			}
    	}
		
		//remove highlights
		allShapeIds.each(function(id){
			this.facade.raiseEvent({
					type: 	ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
					highlightId: 	id
				});
		}.bind(this))
		this.raisedHighlightEventIds = [];
		
		//remove overlays 
		allShapeIds.each(function(id){
			this.facade.raiseEvent({
					type: 	ORYX.CONFIG.EVENT_OVERLAY_HIDE,
					id: 	id
				});
		}.bind(this))
		this.raisedOverlayEventIds = [];
	},
	
	getTaskById: function(taskId){
		var shapes = this.facade.getCanvas().getChildShapes(true);
		var task;
		for (var index = 0; index < shapes.length; index++) {
    		if (shapes[index].properties["oryx-activitytype"] == "Task") {
				if (shapes[index].properties["oryx-id"] == taskId) {
					task = shapes[index];
					break;
				}
			}
    	}
		return task;
	}
});
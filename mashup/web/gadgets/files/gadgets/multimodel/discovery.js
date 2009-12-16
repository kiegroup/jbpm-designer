/**
 * Copyright (c) 2009
 * Helen Kaltegaertner
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

Discovery = function(gadget){
	this.gadget 	= gadget;
	this.currentViewer = null;
	this.currentShape = null;
	this.init();
}

Discovery.prototype = {
		
	init : function(){
	
		this.gadget.registerSelectionChanged("all");
		this.gadget.registerRPC("handleSelection", "", "", this.showConnections, this);
		
		// switch single-select mode in all viewers
		var setSingleSelect = function(viewers) {
			for (var i = 0; i < viewers.length; i++){
				var index = viewers[i];
				this.gadget.setSelectionMode(index, "single");
			}
		};
		
		this.gadget.sendViewers( setSingleSelect, this);
		
	},

	/*
	 * deselct previously selected shape and remove all markers
	 * and show connections associated with the recently chosen one
	 */
	showConnections: function(reply){
		
		var oldViewer = this.currentViewer;
		var oldShape = this.currentShape;
		
		var selectedShapes = reply.split(";");
		this.currentViewer = selectedShapes.shift();	
		
		if (selectedShapes[0].evalJSON(true).length > 0){
			this.currentShape = selectedShapes[0].evalJSON(true)[0];
			
			// deselct previously selected shape
			if (oldViewer && oldViewer != this.currentViewer){
				this.gadget.resetSelection(oldViewer);
			}
			
			// remove all markers (associations belonging to previously selected shape)
			// mark all shapes associated to the recently selected
			var showAssociations = function(viewers) {
				
				for (var i = 0; i < viewers.length; i++){
					this.gadget.removeMarker(viewers[i], "all");
				}
				
				var connections = this.gadget.connections;
				for ( var i = 0; i < connections.length; i++){
					if (connections[i] && connections[i].includesShape(this.currentViewer, this.currentShape.resourceId))
						connections[i].markShapes(false);
				}
			};
			this.gadget.sendViewers( showAssociations, this);
		}
		
	},
	
	
	/*
	 * removeselection and markers associated shapes
	 */
	stopDiscoveryMode : function(){
		
		this.gadget.resetModels();
		
	}
		
}


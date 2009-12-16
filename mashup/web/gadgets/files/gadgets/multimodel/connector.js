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

Connector = function(gadget){
	this.gadget 	= gadget;
	//STYLE 			= {"background-color" : "blue", "opacity" : "0.1"};
	//ICON 			= this.gadget.GADGET_BASE + 'this.gadget/icons/connect.png';
	this.selections = [];
	this.init();
}

Connector.prototype = {
		
	init : function(){
	
		var greyModels = function(viewers){
			
			for (var i = 0; i < viewers.length; i++){
				this.gadget.resetSelection(viewers[i]);
				this.gadget.removeMarker(viewers[i], "all");
				this.gadget.greyModel(viewers[i]);
			}
		};
	
		this.gadget.sendViewers(greyModels, this);
		this.gadget.registerSelectionChanged("all");
		this.gadget.registerRPC("handleSelection", "", "", this.updateSelection, this);
	},

	/*
	 * save currently selected shapes of the viewer that caused the event
	 * 
	 */
	updateSelection: function(reply){
		
		var selectedShapes = reply.split(";");
		var index = selectedShapes.shift();
		
		var nodes = selectedShapes[0].evalJSON(true);
		
		var resourceIds = [];
		for (var i = 0; i < nodes.length; i++)
			resourceIds.push(nodes[i].resourceId);
		
		// remove shadow
		this.gadget.undoGrey(index, resourceIds);
		
		if (!this.selections[index]){
			this.gadget.sendInfo(index, this.addSelection, this, {index: index, nodes: nodes} )	
		} else {
			this.selections[index].shapes = nodes;	
		}	
	},
	
	/*
	 * add a new viewer to the collection of viewers with selected shapes
	 * save the selected shapes and title and url of the model
	 * 
	 */
	addSelection : function(info, args){
		
		this.selections[args.index] = {
				shapes : 	args.nodes,
				model : 	info.model,
				url	: 		info.url
				//description: info.description
		};
	},
	
	/*
	 * create a new connection, mark all selected shapes and remove shadows 
	 */
	stopSelectionMode : function(){
		
		this.gadget.unregisterSelectionChanged();
		
		// remove shadows
		var undoGreyModels = function(viewers){
			for (var i = 0; i < viewers.length; i++){
				this.gadget.undoGrey(viewers[i], "all");
			}
		};
		
		this.gadget.sendViewers(undoGreyModels, this);
		
		var exitSelectionMode = function(viewers){
			
			// no shapes selected
			if (viewers.length == 0) return;
			
			var connection = new Connection( this.gadget,  prompt("comment: ") );
			
			// mark selected shapes and reset selection
			// create connection object with information about selected models and shapes
			for (var i = 0; i < viewers.length; i++){

				var resourceIds = [];
				for (var j = 0; j < this.selections[ viewers[i] ].shapes.length; j++)
					resourceIds.push(this.selections[ viewers[i] ].shapes[j].resourceId);
				this.gadget.markShapes( viewers[i] , resourceIds );
				this.gadget.resetSelection( viewers[i] );
				
				connection.addModel(viewers[i], this.selections[ viewers[i] ].model, this.selections[ viewers[i] ].url, this.selections[ viewers[i] ].shapes);
			}
			this.gadget.addConnection(connection);
		};
		
		// collection of indices of all models that were selected during selection mode
		var viewers = [];
		for (var i = 0; i < this.selections.length; i++){
			if (this.selections[i])
				viewers.push(i);
		};
		
		this.gadget.sendAvailableGadgets(viewers, exitSelectionMode, this, {});
		
	}
		
}


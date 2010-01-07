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

Connection = function(gadget, comment){
	this.comment = comment;
	this.gadget = gadget;
	this.models = [];
};

Connection.prototype = {
	
	addModel : function(index, model, url, nodes){		
		this.models[index] = {
				title : 	model,
				url : 		url,
				nodes : 	nodes
			}
		
	},
	
	/*
	 * add connection button in multimodel gadget
	 */
	display : function(){
		
		var el = document.createElement("li");
		el.className = "connection";
		
		var elContainer = document.createElement("div");
		elContainer.className = "connection-item";
		elContainer.onclick = this.highlightInViewer.bind(this);
		el.appendChild(elContainer);
		
		var elComment = document.createElement("p");
		elComment.innerHTML = this.comment;
		elContainer.appendChild(elComment);
		
		var elDelete = document.createElement("img");
		elDelete.src = this.gadget.GADGET_BASE + "multimodel/icons/delete.png";
		elDelete.onclick = this.remove.bind(this);
		el.appendChild(elDelete);
	
		var elClear = document.createElement("div");
		elClear.className = "clear";
		el.appendChild(elClear);

		$("connections").appendChild(el);
		
		this.connectionEl = elContainer;
		
		// deselct currently active connection and select the new one
		var connections = this.gadget.connections;
		
		for (var i = 0; i < connections.length; i++){
			if (connections[i] && connections[i].isActive ){
				connections[i].deselect();
			}	
		}
		this.select();

	},
	
	remove: function(){
		
		if (this.isActive){
			for (var i = 0; i < this.models.length; i++){
				if (this.models[i]){
					this.gadget.undoGrey(i, "all");
					
					//mark shapes of the current model that belong to the connection
					var resourceIds = [];
					for (var j = 0; j < this.models[i].nodes.length; j++){
						resourceIds.push(this.models[i].nodes[j].resourceId)
					}
					this.gadget.removeMarker( i, resourceIds );
				}
			}
		}
		
		this.connectionEl.ancestors()[0].remove();
		
		var connections = this.gadget.connections;
		
		for (var i = 0; i < connections.length; i++){
			if (connections[i] && connections[i] == this)
				connections[i] = null;
		}
		
	},
	
	/*
	 * mark all shapes in viewers that belong to the connection
	 */
	highlightInViewer : function(){
		
		this.isActive = true;
		
		// remove all markers and shadows in all models covered by the connection 
		// and mark shapes belonging to the connection
		this.markShapes(true);
		
		// remove all shadows, selections and markers in all other viewers
		var resetModels = function(viewers){
			
			for (var i = 0; i < viewers.length; i++){
				if ( ! this.models[ viewers[i] ] ){
					this.gadget.removeMarker(viewers[i], "all");
					this.gadget.undoGrey(viewers[i], "all");
					this.gadget.resetSelection( viewers[i] );
				}
			}
		};
	
		this.gadget.sendViewers(resetModels, this);
		
		var connections = this.gadget.connections;
		
		for (var i = 0; i < connections.length; i++){
			if (connections[i] && connections[i].isActive ){
				connections[i].deselect();
			}	
		}
		this.select();
	},
	
	deselect : function(){
		this.isActive = false;
		this.connectionEl.removeClassName("connection-item-active");
	},
	
	select : function(){
		this.isActive = true;
		this.connectionEl.addClassName("connection-item-active");
	},
	
	/*
	 * in all models that belong to the connection mark the included shapes
	 * if reset is treu remove all other markers and shadows
	 */
	markShapes : function(reset){
		
		for (var i = 0; i < this.models.length; i++){
			if (this.models[i]){
				
				if (reset){
					this.gadget.removeMarker(i, "all");
					this.gadget.undoGrey(i, "all");
				}
				
				//mark shapes of the current model that belong to the connection
				var resourceIds = [];
				for (var j = 0; j < this.models[i].nodes.length; j++){
					resourceIds.push(this.models[i].nodes[j].resourceId)
				}
				this.gadget.markShapes( i, resourceIds );
			}
		}
		
	},
	
	/*
	 * checks whether a shape specified by its resourceId and the viewer belongs to the connection
	 */
	includesShape: function(viewer, resourceId){
		if (this.models[viewer]){
			for (var i = 0; i < this.models[viewer].nodes.length; i++){
				if (this.models[viewer].nodes[i].resourceId == resourceId)
					return true;
			}
		}
		return false;
		
	}
		
};

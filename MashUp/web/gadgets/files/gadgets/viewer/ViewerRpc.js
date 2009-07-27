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



(function() {

	rpcHandler = function(){
		
		this.viewer = null;
		this.selection = null;
		this.marker = null;
		this.index = null;
		this.url = null;
		this.title = '';
		this.description = '';
		
	};
	
	rpcHandler.prototype = {
			
		setIndex: function(index){
			this.index = index;
		},
		
		setTitle: function(title){
			this.title = title;
		},
		
		setDescription: function(description){
			this.description = description;
		},
		
		setViewer: function(modelViewer){
		
			if(! modelViewer instanceof MOVI.widget.ModelViewer) {
				throw new Error("No viewer specified.", "error", "viewerRpc.js");
				return false;
			}
			this.viewer = modelViewer;
		},
		
		setSelection: function(selection){
			
			if(! selection instanceof MOVI.util.ShapeSelect) {
				throw new Error("No selection specified.", "error", "viewerRpc.js");
				return false;
			}
			this.selection = selection;
		},
		
		setUrl: function(url){
			this.url = url;
		},
		
		// loads a model into the viewer
		loadModel: function(url){
			
			gadgets.rpc.call("..", "dispatcher.setTitle", function(reply){return;}, url);
			this.url = url;
			viewer.addViewer(url);
			return false;
			
		},

		// expects a keymap of nodes and returns a string with some properties of all shapes
		// properties: ressourceId, name/label (others properties can be added)
		_stringify: function(nodes){
		
			var data = new Array() ;
			
			for (var key in nodes){
				var name = "";
				if (nodes[key].properties.name)
					name = nodes[key].properties.name;
				data.push({
					ressourceId: key,
					name: name
				});
			}
			
			return data.toJSON();
		},
		
		// sends some metadata of the model
		sendInfo: function(msg){
			var info = {
				title: this.title,
				description: this.description,
				url: this.url
			};
			return Object.toJSON(info);
		},
	
		// sends all nodes with attributes ressouceId and label (name) 
		sendShapes : function(msg){
	
			var nodeInfo;
			document.getElementById('rpc').innerHTML = "received " + msg;
			//gadgets.window.adjustHeight();
			
			if (this.viewer){
				nodeInfo = this._stringify( this.viewer.canvas.getNodes() )
			}
			return this.index + ';' + nodeInfo;
			
		},
		
		// send all selected nodes, same attributes as sendShapes delivers 
		sendSelection: function(msg){
			var nodeInfo
			if (this.selection)
				nodeInfo = this.index + ';' + this._stringify( this.selection._selectedShapes );
			return nodeInfo;
			
		},
	
		// marks the specified shapes
		// all nodes can be marked by sending the message "all" (probably never needed)
		markShapes : function(msg){
	
			if (this.viewer){
				if (!this.marker){
					this.marker = new MOVI.util.Marker(
						null,
						{"border": "2px solid blue"}
					);
				}	
				if (msg == "all"){
					for (key in this.viewer.canvas.getShapes())
						this.marker.addShape(this.viewer.canvas.getShape(key));	
				} else {
					var shapes = msg.split(".");
					for (var i = 0; i < shapes.length; i++){
						if (! (shapes[i] == ""))
							this.marker.addShape(this.viewer.canvas.getShape(shapes[i]));
					}
				}
			}	
			return "";
		},
	
		// removes markers
		undoMarking : function(msg){
			
			if (this.marker){
				if (msg == "all"){
					this.marker.removeAllShapes();
				}else{
					var shapes = msg.split(".");
					for (var i = 0; i < shapes.length; i++){
						if (! (shapes[i] == ""))
							this.marker.removeShape(this.viewer.canvas.getShape(shapes[i]));
					}
				}
			}
		},
		
		// scrolls the specified shape to the center of the viewer
		centerShapes: function(ressourceId){
			this.viewer.scrollToShape(ressourceId);
		},
	
		//  throws event "selectionChanged" by calling the dedicated RPC
		throwSelectionChanged : function(){

			var selectedShapes = this.index + ';' + this._stringify( this.selection._selectedShapes );
			gadgets.rpc.call(null, "dispatcher.selectionChanged", function(reply){return;}, selectedShapes);
		}
	};
})();


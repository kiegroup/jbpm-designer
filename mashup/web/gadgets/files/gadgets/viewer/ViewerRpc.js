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

	rpcHandler = function(viewer){
		
		this.GREY_CLASS = "grey-node";
		this.viewer = viewer;
		this.selection = null;
		this.marker = null;
		this.url = null;
		this.title = '';
		this.description = '';
		
	};
	
	rpcHandler.prototype = {
			
		setDescription: function(description){
			this.description = description;
		},
		
		setViewer: function(modelViewer){
		
			if(! modelViewer instanceof MOVI.widget.ModelViewer) {
				throw new Error("No viewer specified.", "error", "viewerRpc.js");
				return false;
			}
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
		
		setTitle: function(title){
			this.title = title;
		},
		
		// loads a model into the viewer, used from repository
		loadModel: function(url){
			
			// args[0]: url
			// args[1]: title
			var args = url.split(".");
			this.title = args[1].replace(/_/g, " ");
			this.url = args[0];
			
			this.viewer.setTitle(this.title);
			
			this.viewer.addViewer(args[0]);
			return false;
			
		},
		/*
		 * grey overlay to visualize selection mode for multimodel gadget
		 * 
		 */
		greyModel : function() {
			var shapes = $$(".movi-node");
			for (var i = 0; i < shapes.length; i++){
				shapes[i].addClassName(this.GREY_CLASS);
			}
		},
		
		/*
		 * remove shadow from shapes
		 * args can be "all" to remove all shadows
		 * or a collection of resourceIds
		 */
		undoGrey : function( args ){
			if (args == "all"){
				var shapes = $$(".movi-node");
				for (var i = 0; i < shapes.length; i++){
					shapes[i].removeClassName(this.GREY_CLASS);
				} 
			}
			
			else {
				var prefix = "movi_0-"
				var shapes = args.evalJSON();
				for (var i = 0; i < shapes.length; i++){
					var element = $( prefix + shapes[i])
					element.removeClassName(this.GREY_CLASS);
				}
				
			}
			
		},
		
		//TODO
		// sends some metadata of the model
		sendInfo: function(msg){
			var info = {
				model : 		this.title,
				description : 	this.description,
				url : 			this.url
			};
			return Object.toJSON(info);
		},
	
		// sends all nodes with attributes ressouceId and label (name) 
		// returns index;shapes (shapes in JSON)
		sendShapes : function(msg){
	
			var nodeInfo;
			//gadgets.window.adjustHeight();
			
			if (this.viewer){
				nodeInfo = this._stringify( this.viewer.modelViewer.canvas.getNodes() )
			}			
			return this.viewer.index + ';' + nodeInfo;
			
		},
		
		// send all selected nodes, same attributes as sendShapes delivers 
		sendSelection: function(msg){
			var nodeInfo
			if (this.selection)
				nodeInfo = this.viewer.index + ';' + this._stringify( this.selection._selectedShapes );
			return nodeInfo;
			
		},
		
		resetSelection: function(){
			this.selection.reset();
		},
		
		/*
		 * change to the specified selection mode
		 */
		setSelectionMode: function(args){
			if (args == "single"){
				this.selection._allowMultiselect = false;
			}else if (args == "multi"){
				this.selection._allowMultiselect = true;
			}
			return "";
				
		},
	
		// marks the specified shapes
		// all nodes can be marked by sending the message "all" (probably never needed)
		markShapes : function(args){
	
			args = args.evalJSON();
			
			style = args.style || {"border": "2px solid blue"};
			
			if (this.viewer){
				if (!this.marker){
					this.marker = new MOVI.util.Marker(
						null,
						style
					);
					if (args.icon) this.marker.addIcon("northwest", args.icon);
					
				}	
				if (args.toMark == "all"){
					for (key in this.viewer.canvas.getShapes())
						this.marker.addShape(this.viewer.modelViewer.canvas.getShape(key));	
				} else {
					//var shapes = msg.evalJSON();
					for (var i = 0; i < args.toMark.length; i++){
						if (! (args.toMark[i] == ""))
							this.marker.addShape(this.viewer.modelViewer.canvas.getShape(args.toMark[i]));

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
					var shapes = msg.evalJSON();
					for (var i = 0; i < shapes.length; i++){
						if (! (shapes[i] == ""))
							this.marker.removeShape(this.viewer.modelViewer.canvas.getShape(shapes[i]));
					}
				}
			}
		},
		
		// scrolls the specified shape to the center of the viewer
		centerShapes: function(resourceId){
			this.viewer.modelViewer.scrollToShape(resourceId);
		},
	
		//  throws event "selectionChanged" by calling the dedicated RPC
		throwSelectionChanged : function(){

			var selectedShapes = this.viewer.index + ';' + this._stringify( this.selection._selectedShapes );
			gadgets.rpc.call(null, "dispatcher.selectionChanged", function(reply){return;}, selectedShapes);
		},
		

		// expects a keymap of nodes and returns a string with some properties of all shapes
		// properties: resourceId, name/label (others properties can be added)
		_stringify: function(nodes){
		
			var data = new Array() ;
			
			for (var key in nodes){
				var name = "";
				if (nodes[key].properties.name)
					name = nodes[key].properties.name;
				data.push({
					resourceId: key,
					name: name
				});
			}
			
			return data.toJSON();
		},
	};
})();


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

/*
 * serves as superclass for gadgets
 * provides standard functionality to register a gadget and its RPCs in container catalog 
 * abstracts from communication with viewer gadgets
 * 
 */


(function (){

	AbstractGadget = function(type){
		
		this.GADGET_BASE = "/gadgets/files/gadgets/";
		this.SERVER_BASE = "http://localhost:8080";
		this.REPOSITORY_BASE =	"/backend/poem" ;
		this.index = null;
		
		if (type)
			{this.registerGadget(type);}
		else
			{this.registerGadget("none");}

		
	};
	
	AbstractGadget.prototype = {
			
		/*
		 * register gadget in container catalog
		 */
		registerGadget : function(type) {
			gadgets.rpc.call(
					"..", 
					"dispatcher.register", 
					function(reply){ 
						this.index = reply;
						return; 
					}.bind(this), 
					type);
		},
		
		/*
		 * register an RPCs in conatiner calatog
		 * parameters specify name, input parameters and return value 
		 */
		registerRPC : function(name, params, returnValue, method, scope){
			
			rpc = {
				name : 			name,
				params :		params,
				returnValue : 	returnValue
			};
			
			gadgets.rpc.call(
				"..",
				"dispatcher.registerRPC",
				function (reply){return;},
				Object.toJSON(rpc)
			);
			
			gadgets.rpc.register(
				name, 
				function(args) { 
					return method.call(scope? scope: this, args);
				}.bind(this)
			);
		},	
		
		/*
		 * unregister an RPC
		 */
		unregisterRPC : function(name){
			
			// unregister service
			gadgets.rpc.unregister(
					name, 
					function(args) { 
						return method.call(scope? scope: this, args);
					}.bind(this)
			);
			
			// remove from rpc catalogue 
			gadgets.rpc.call(
				null,
				"dispatcher.unregisterRPC",
				function(reply){return;},
				name
			);
			
		},
		
		
		/*
		 * register for selection changed event of one or multiple viewers
		 * 
		 */
		registerSelectionChanged: function(option){
			
			gadgets.rpc.call(
					null, 
					"dispatcher.registerSelection", 
					function(reply){return;}, 
					option
			);
		},
		
		/*
		 * unregister for selection changed event of one or multiple viewers
		 * 
		 */
		unregisterSelectionChanged: function(){
			
			gadgets.rpc.call(
					null, 
					"dispatcher.unregisterSelection", 
					function(reply){return;}, 
					""
			);
			this.unregisterRPC("handleSelection");
		},
		
		/*
		 * set title of the panel the gadget is rendered in
		 * 
		 */
		setTitle : function(title){
			
			gadgets.rpc.call("..", "dispatcher.setTitle", function(reply){return;}, title);
		},
		
		/*
		 * invoke callback if a gadget specified via index exists on the dashboard
		 */
		 sendAvailableGadgets : function(indices, callback, scope, args){
			gadgets.rpc.call(
				null, 
				"dispatcher.gadgetsAvailable", 
				function(reply){
					var availableGadgets = reply.evalJSON();
					callback.call(scope? scope: this, availableGadgets, args); 
				},
				Object.toJSON(indices)
			);
			
		},
		
		/*
		 * add a new viewer gadget to the dashboard 
		 * model specified by its url
		 * 
		 */
		loadViewer : function(url){
			
			gadgets.rpc.call(
				"..", 
				'dispatcher.displayModel', 
				function(reply){return}, 
				url);
		},
		
		/*
		 * center a shape in a given viewer
		 * 
		 */
		centerShape : function(viewer, resourceId){
			
			var channel = "dispatcher." + viewer + ".centerShape";
			
			gadgets.rpc.call(
					"..", 
					channel, 
					function(){return;}, 
					resourceId);
		},
		
		/*
		 * mark shapes in the specified viewer
		 * shapes are specified by their resourceId
		 * 
		 */
		markShapes : function(viewer, toMark, style, icon){
			
			var args = {
				toMark: 	toMark,
				style : 	style,
				icon : 		icon
			};
			
			var channel = "dispatcher." + viewer + ".mark";
			gadgets.rpc.call(null, channel, function(){return;}, Object.toJSON(args));
		},
		
		/*
		 * remove markers from shapes
		 * either "all" or a list of resourceIds
		 * 
		 */
		removeMarker : function(viewer, option){
			
			option = option || "all"
			
			if (option != "all")
				option = Object.toJSON(option);
			
			var channel = "dispatcher." + viewer + ".undoMark";
			gadgets.rpc.call(null, channel, function(){return;}, option);
		},
		
		/*
		 * cover all shapes of specified viewer with a shadow
		 * 
		 */
		greyModel : function(viewer){
			
			var channel = "dispatcher." + viewer + ".greyModel";
			gadgets.rpc.call(null, channel, function(){return;}, "");
			
		},
		
		/*
		 * remove shadowy from shapes
		 * either all shapes are specified ("all") or just a collection of resourceIds
		 * 
		 */
		undoGrey : function(viewer, option){
			 if (option != "all")
					option = Object.toJSON(option);
				
			var channel = "dispatcher." + viewer + ".undoGrey";
			gadgets.rpc.call(null, channel, function(){return;}, option);
		},
		
		
		/*
		 * request currently available viewers
		 * callback will be executed when the rpc returns
		 * 
		 */
		sendViewers : function(callback, scope, args){
			
			gadgets.rpc.call(
				null, 
				"dispatcher.countViewer" , 
				function(reply){
					
					var viewers = reply.evalJSON();
					callback.call(scope? scope: this, viewers, args);
				}, 
				""
			);
		},
		
		/*
		 * request resourceIds and labels of all nodes in a specific viewer
		 * 
		 */
		sendShapes : function(viewer, callback, scope, args){
			
			var channel = "dispatcher." + viewer + ".sendShapes";
			
			gadgets.rpc.call(
				null,
				channel,
				function(reply){
					var shapes = reply.split(";")[1].evalJSON();
					callback.call(scope? scope : this, shapes, args);
				},
				""
			);
			
		},
		
		/*
		 * request resourceIds and labels of all selected nodes in a specific viewer
		 */
		sendSelection : function(viewer, callback, scope, args){
			
			var channel = "dispatcher." + viewer + ".sendSelection";
			
			gadgets.rpc.call(
				null,
				channel,
				function(reply){
					var shapes = reply.split(";")[1].evalJSON();
					
					callback.call(scope? scope: this, shapes, args);
				},
				""
			);	
		},
		
		/*
		 * remove selection
		 */
		resetSelection : function(viewer, callback, scope, args){
			
			var channel = "dispatcher." + viewer + ".resetSelection";
			gadgets.rpc.call( 
				null, 
				channel, 
				function(){
					if (callback)
						return callback.call(scope? scope: this, args);
					return;
				}, 
				""
			);	
			
		},
		
		/*
		 * changes the viewer to the specified selection mode
		 * option must be "multi or "single"
		 */
		setSelectionMode: function(viewer, option){
			var channel = "dispatcher." + viewer + ".setSelectionMode";
			gadgets.rpc.call( null, channel, function(){return;}, option);	
		},
		
		/*
		 * request meta information about a model
		 * 
		 */
		sendInfo : function(viewer, callback, scope, args){
			
			var channel = "dispatcher." + viewer + ".sendInfo";
			
			gadgets.rpc.call(
				null,
				channel,
				function(reply){
					
					var info = reply.evalJSON();
					callback.call(scope? scope: this, info, args);
				},
				""
			);	
		}
	}
})();
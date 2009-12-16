
if (! tool)
	var tool = {};

tool.rpcHandler = (function(){

	var _selectionHandler = null;
	
	return {
	
		init: function(registerSelection){
		
		//register on a selected set of viewers not yet implemented
			if (registerSelection){
				gadgets.rpc.call(
					null, 
					"dispatcher.registerSelection", 
					function(reply){return;}, 
					"all"
				);
			}
			_selectionHandler = null;
			
		},
			
		setSelectionHandler: function(callback, scope, args){
		
			if (! typeof callback  == "function" ) {
				throw new TypeError("Specified callback is not a function.", "toolRpc.js");
				return;
			}

			if (!scope) 
				scope = this;
			
			_selectionHandler = {
				callback: callback,
				scope: scope,
				args: args
			};
			
			//eventHandler for selectionChanged
			gadgets.rpc.register(
				"handleSelection", 
				this._handleSelection
			);
			
			var rpc = {
				name : 			"handleSelection",
				params :		"",
				returnValue : 	""
			};
			
			gadgets.rpc.call(
				"..",
				"dispatcher.registerRPC",
				function (reply){return;},
				Object.toJSON(rpc)
			);
			
		},
		
		_handleSelection: function(args) {
			
			if(! _selectionHandler) return;
			
			_selectionHandler.callback.call(
				_selectionHandler.scope,
				args);
		},
		
		unregisterSelection: function (callback){
			
			gadgets.rpc.call(
				null, 
				"dispatcher.unregisterSelection", 
				callback, 
				""
			);
		},
		
		parseToNodes: function(nodes){
	
			var _nodes = new Array(); 
			
			for (var i = 0; i < nodes.length; i++){
				
				if (nodes[i]!= ''){
					var n = nodes[i].split('.');
					_nodes.push({
						ressourceId: n[0],
						name: n[1]});
				}
			}
			return _nodes;
		},
		
		// returns an array with indizes of valid viewers
		getViewers : function() {	

			var validViewers;
			
			gadgets.rpc.call(
				null, 
				"dispatcher.countViewer", 
				function(reply){
					reply = reply.evalJSON();
					validViewers = reply;
				}, 
				""
			);
			
			return validViewers;
		},
		
		sendShapes: function(viewerIndex, msg){
		
			var channel = "dispatcher." + viewerIndex + ".sendShapes";
			var nodes;
			
			gadgets.rpc.call(null, channel, function(reply){
			
				var shapes = reply.split(",");
				shapes.shift(); 	//might be redundant
				nodes = tool.rpcHandler.parseToNodes(shapes);			
			
			}, msg);
			
			while (! nodes);
			
			return nodes;

		},
		
		// expects array of ressourceIds		
		mark: function(viewerIndex, shapes){
			
			var toMark = "";
			
			for (var i = 0; i < shapes.length; i++)
				toMark += nodes[j] + '.';

			var channel = "dispatcher." + viewerIndex + ".sendShapes";
			gadgets.rpc.call(null, channel, function(){return;}, toMark);
		}
		
	};
		
})();
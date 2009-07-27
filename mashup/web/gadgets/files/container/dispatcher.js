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


var dispatcher = (function(){

	var _viewers = [null];
	var waiting = 0;
	var modelsToLoad = [null];
	
	// eventlisteners (selectionChanged) for all current and future viewers
	var _general_registered = [null];

	// register a service for gadgets to register for receiving
	gadgets.rpc.register(
		"dispatcher.register", 
		function(args){	
			// scope: this = rpc
			var index = _viewers.length;
			_viewers.push({
				id: index,
				gadget: this.f,
				info: args,
				listeners: [null]
			});
			for (var i = 1; i < _general_registered.length; i++){
				if (_general_registered[i])
					_viewers[index].listeners.push(_general_registered[i]);
			}
			return index;
		}
	);
		
	// register a service for gadgets to unregister from receiving
	gadgets.rpc.register(
		"dispatcher.unregisterViewer", 
		function(args) {
			var gadget = this.f;
			for (var i = 0; i < _viewers.length; i++) {
				if (_viewers[i] && _viewers[i].gadget == gadget) {
					_viewers[i] = null;
				}
			}
			return "";
		}
	);
	
	gadgets.rpc.register(
		"dispatcher.setTitle", 
		function(args){
			var gadget = this.f;
			var widgets = dashboard.getGadgets();
			
			for (var i = 0; i < widgets.length; i++) {
				if (widgets[i].__gadget && ("remote_iframe_" + widgets[i].__gadget.id) == gadget)
					widgets[i].setTitle(args);
			}
		}		
	);
	
	
	// registers a tool for "selectionChanged"
	gadgets.rpc.register(
		"dispatcher.registerSelection", 
		function(args) {
			var gadget = this.f;
			if (args == "all"){
				for (var i = 0; i < _viewers.length; i++) {
					// adds tool to list of gadgets that will be eventlisteners of all new viewers
					_general_registered.push(gadget);
					
					// adds the tool to all already existing viewers
					if (_viewers[i] && _viewers[i].gadget) 
						_viewers[i].listeners.push(gadget);
				}
			} else {
				//certain viewers are chosen
				var viewers = args.split(".");
				for (var i = 0; i < viewers.length; i++){
					if (! (viewers[i] == ""))
						_viewers[parseInt(viewers[i])].listeners.push(gadget);
				}
			}
			return "";
	});
	
	
	// unregisters a tool for "selectionChanged"
	gadgets.rpc.register(
		"dispatcher.unregisterSelection", 
		function(args) {
			var gadget = this.f;
			
			// remove gadget from list of eventlisteners for all viewers
			for (var i = 0; i < _general_registered.length; i++) {
				if (_general_registered[i] == gadget) {
					_general_registered[i] = null;
				}
			}
			
			// remove gadget from the list of specific eventlisteners of each viewer
			for (var i = 1; i < _viewers.length; i++){
				for (var j = 1; j < _viewers[i].listeners.length; j++){
					if (_viewers[i].listeners[j] == gadget)
						_viewers[i].listeners[j] = null;
				}
			}
			return "";
	});
	
	//forward selectionChanged
	gadgets.rpc.register(
		"dispatcher.selectionChanged", 
		function(args) {
			var gadget = this.f;
			var currentViewer; //element from _viewers
			for (var i = 0; i < _viewers.length; i++) {
				if (_viewers[i] && _viewers[i].gadget == gadget) {
					currentViewer = _viewers[i];
					break;
				}
			}
			for (var i = 1; i < currentViewer.listeners.length; i++){
				if (currentViewer.listeners[i]){
					var listener = currentViewer.listeners[i];
					gadgets.rpc.call(listener, "handleSelection", function(reply){return;}, args);
				}
			}
	});
	
	// TODO
	gadgets.rpc.register(
		"dispatcher.resize", 
		function(args) {
			var gadget = this.f;
			var widgets = dashboard.getGadgets();
			var dimensions = args.split('.');
			
			for (var i in widgets) {
				if (widgets[i].__gadget && widgets[i].__gadget == gadget) {
					alert(widgets[i].panel.height);
					//widgets[i].panel.height = parseInt(dimension[0] + 10) + 'px';
					//widgets[i].panel.width = parseInt(dimensions[1] + 10) + 'px';
				}
			}
			return "";
	});
	
	// returns indices of valid viewer gadgets
	gadgets.rpc.register(
		"dispatcher.countViewer", 
		function(args) {
			var validViewers = "";
			for (var i = 0; i < _viewers.length; i++) {
				if (_viewers[i] && _viewers[i].gadget) {
					validViewers += (i.toString() + ".");
				}
			}
			return validViewers;
	});
	
	gadgets.rpc.register(
	
		"dispatcher.displayModel", 
		function(args){
			
			var gadget = Container.addViewer();
			
			modelsToLoad.push(args);
			waiting += 1;

		}		
	);
	
	gadgets.rpc.register(
		
		"dispatcher.loadWaiting",
		function(args){
			
			var gadget = this.f;
			
			if (waiting > 0){
				for (var i = 1; i < modelsToLoad.length; i++){
					if (modelsToLoad[i]){
						gadgets.rpc.call(
							gadget, 
							"loadModel",   
							function (reply) { return;},
							modelsToLoad[i]);
						
						modelsToLoad[i] = null;
						waiting -= 1;
						
						return "";
					}
				}
			}
			return "noneWaiting";
		}
	);
	
	// register default service for dispatching
	gadgets.rpc.registerDefault(function(args){

		var channel = this.s;
		var data = args;
		var m = channel.match(/^dispatcher\.(\d+)\.(.+)$/);
		var from = this.f;
		var callId = this.c;
		var response = "response";
		
		
		for (var i = 1; i < _viewers.length; i++) {
		
			// 0 indicates a broadcast to all available viewers
			// otherwise just the selected viewer will be called
			if (_viewers[i].gadget && (parseInt(m[1]) == 0 || parseInt(m[1]) == i) ){
			
				var gadget = _viewers[i].gadget;
				
				console.info("incoming rpc", {
					"target": gadget,
					"channel": channel,
					"data": data,
					"rpc": this
				});
				
				if (gadget) {
					// forward the original request
					gadgets.rpc.call(
						gadget, 
						m[2],   
						function (response) { 
						
							console.info("rpc response", {
								"target": from,
								"data": response
							});
							
							gadgets.rpc.call(from, '__cb', 	null, callId, response);
						},
						data
					);
				} else {
					console.warn("incoming rpc not dispatched: " + channel, this, data);
				}
			}
		}
	});
	
	return {
	
		getViewers: function() {
			var copy = [];
			for (var i = 0; i < _viewers.length; i++) {
				if (_viewers[i] && _viewers[i].gadget) {
					copy.push(_viewers[i]);
				}
			}
			return copy;
		},
		
		deleteViewer: function(gadget) {
			for (var i = 1; i < _viewers.length; i++){
				if (_viewers[i] && (_viewers[i].gadget == ("remote_iframe_" + gadget.id)) )
					_viewers[i].gadget = null;

			}
		}
	}
})();
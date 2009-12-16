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

	var waiting = 0;
	var modelsToLoad = [null];
	var _gadgets = [null];
	
	// eventlisteners (selectionChanged) for all current and future viewers
	var _general_registered = [null];

	// register a service for gadgets to register for receiving
	gadgets.rpc.register(
		"dispatcher.register", 
		function(args){	
			//scope: this = rpc
			
			var index = _gadgets.length;
			var gadget = {
					id : 		index,
					gadget : 	this.f,
					type : 		args,
					listeners : [null],
					rpcs : 		[]
			};
			
			if (args == "viewer"){
				for (var i = 1; i < _general_registered.length; i++){
					if (_general_registered[i])
						gadget.listeners.push(_general_registered[i]);
				}	
			}
			
			_gadgets.push(gadget);
			
			return index;
		}
	);
		
	// register a service for gadgets to unregister from receiving
	gadgets.rpc.register(
		"dispatcher.unregister", 
		function(args) {
			var gadget = this.f;
			for (var i = 0; i < _gadgets.length; i++) {
				if (_gadgets[i] && _gadgets[i].gadget == gadget) {
					_gadgets[i] = null;
				}
			}
			return "";
		}
	);
	
	// gadgets can enter their RPC in the RPC catalog belonging to the gadget
	gadgets.rpc.register(
		"dispatcher.registerRPC",
		function(args) {
			
			var gadget = this.f;
			var rpc = args.evalJSON(true);
			
			// find gadget
			for (var i = 0; i < _gadgets.length; i++) {
				
				// add rpc to catalogue of registered rpcs of this gadget
				if ( _gadgets[i] && _gadgets[i].gadget == gadget ){
					_gadgets[i].rpcs.push( rpc );
				}
			}	
		}
	);
	
	// remove rpc from catalog belonging to the gadget
	gadgets.rpc.register(
		"dispatcher.unregisterRPC",
		function(args) {
			
			var gadget = this.f;			
			// find gadget
			for (var i = 0; i < _gadgets.length; i++) {
				
				// remove rpc from catalogue of registered rpcs of this gadget
				if ( _gadgets[i] && _gadgets[i].gadget == gadget ){
					for ( var j = 0; j < _gadgets[i].rpcs.length; j++){
						if ( _gadgets[i].rpcs[j] && _gadgets[i].rpcs[j].name == args )
							 _gadgets[i].rpcs[j] = null;
					}
				}
			}	
		}
	);
	
	// returns a collection of objects containing information about 
	// each gadget currently on the dashboard and its RPCs
	gadgets.rpc.register(
		"dispatcher.getRPCs",
		function(args) {
			
			// collection of Objects containing the gadget and its RPCs
			var rpcs = [];
			
			// all gadgets on the dashboard
			if (args == "all"){
				
				for (var i = 0; i < _gadgets.length; i++){
					if( _gadgets[i] && _gadgets[i].gadget )
						rpcs.push({
							type : 		_gadgets[i].type,
							id : 		_gadgets[i].id,
							gadget : 	_gadgets[i].gadget,
							rpcs : 		_gadgets[i].rpcs,
						});
				}
			}
			
			// one specific gadget, specified by its id
			else if ( args.match(/\d+/) ){
				if (_gadgets[i] && _gadgets[i].gadget) 
					rpcs.push({
						type : 		_gadgets[i].type,
						id : 		_gadgets[i].id,
						gadget : 	_gadgets[i].gadget,
						rpcs : 		_gadgets[i].rpcs,
					});
			}
			
			// gadgets of a specific type
			else {
				for (var i = 0; i < _gadgets.length; i++){
					if( _gadgets[i] && _gadgets[i].gadget && _gadgets[i].type == args )
						rpcs.push({
							type : 		_gadgets[i].type,
							id : 		_gadgets[i].id,
							gadget : 	_gadgets[i].gadget,
							rpcs : 		_gadgets[i].rpcs,
						});
				}	
			}
				
			return Object.toJSON(rpcs);	
			
		}
	
	);
	
	// returns a list of pairs of url and icon of all gadgets that can be added to the dashboard
	gadgets.rpc.register(
		"dispatcher.getAvailableGadgets",
		function(args){
			var gadgetUrls = [];
			for (var i = 0; i < Container.gadgetData.length; i++){
				gadgetUrls.push({ 
					url : 		Container.gadgetData[i].url, 
					icon : 		Container.gadgetData[i].icon, 
					title : 	Container.gadgetData[i].title });
			}
			return Object.toJSON(gadgetUrls);

		}	
	);
	
	// adds an gadget specified by its url to the dashboard
	gadgets.rpc.register(
		"dispatcher.loadGadget",
		function(args){
			for (var i in Container.gadgetData){
				if (Container.gadgetData[i].url == args)
					Container.addGadget( null, null, 
							{ url : args, options : Container.gadgetData[i].options });
			}
		}
	);
	
	//expects a collection of gadget indices 
	// returns all gadgets that are still on the dashboard
	gadgets.rpc.register(
		"dispatcher.gadgetsAvailable",
		function(args){	
			var requestedGadgets = args.evalJSON();
			var availableGadgets = [];
			for (var i = 0; i < requestedGadgets.length; i++){
				if (_gadgets[ requestedGadgets[i] ].gadget )
					availableGadgets.push( requestedGadgets[i] );
			}
			return Object.toJSON(availableGadgets);
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
				
				// adds tool to list of gadgets that will be eventlisteners of all new viewers
				_general_registered.push(gadget);
				
				for (var i = 0; i < _gadgets.length; i++) {
					
					// adds the tool to all already existing viewers
					if (_gadgets[i] && _gadgets[i].gadget && _gadgets[i].type == "viewer") 
						_gadgets[i].listeners.push(gadget);
				}
			} else {
				// certain viewers are chosen
				var viewers = args.split(".");
				for (var i = 0; i < viewers.length; i++){
					if (! (viewers[i] == ""))
						_gadgets[ parseInt(viewers[i]) ].listeners.push(gadget);
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
			for (var i = 1; i < _gadgets.length; i++){
				
				// if gadget is viewer remove registered gadget
				if (_gadgets[i] && _gadgets[i].type == "viewer"){
				
					for (var j = 1; j < _gadgets[i].listeners.length; j++){
						if (_gadgets[i].listeners[j] == gadget)
							_gadgets[i].listeners[j] = null;
					}
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
			
			// find out viewer that has thrown the event
			for (var i = 0; i < _gadgets.length; i++) {
				if (_gadgets[i] && _gadgets[i].gadget == gadget) {
					currentViewer = _gadgets[i];
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
			var validViewers = [];
			for (var i = 0; i < _gadgets.length; i++) {
				if (_gadgets[i] && _gadgets[i].gadget && _gadgets[i].type == "viewer") {
					validViewers.push(i);
				}
			}
			return Object.toJSON(validViewers);
	});
	
	gadgets.rpc.register(
	
		"dispatcher.displayModel", 
		function(args){
			
			// args: url.title
			for (var i = 0; i < Container.gadgetData.length; i++){
			
				if ( Container.gadgetData[i].url.match(/viewer/) ){
					Container.addGadget(null, null, Container.gadgetData[i] );
					break;
				}		
			}
			modelsToLoad.push(args);
			waiting += 1;

		}		
	);
	
	//called by an viewer when it has rendered so far that it can display a model
	//asks for one of the queued models
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
							modelsToLoad[i]);	//modelsToLoad[i]: url.title
						
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
		
		
		for (var i = 1; i < _gadgets.length; i++) {
		
			// 0 indicates a broadcast to all available viewers
			// otherwise just the selected viewer will be called
			if (_gadgets[i].gadget && (parseInt(m[1]) == 0 || parseInt(m[1]) == i) ){
			
				var gadget = _gadgets[i].gadget;
				
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
	
		getGadgets: function() {
			var copy = [];
			for (var i = 0; i < _gadgets.length; i++) {
				if (_gadgets[i] && _gadgets[i].gadget) {
					copy.push(_gadgets[i]);
				}
			}
			return copy;
		},
		
		deleteGadget: function(gadget) {
			for (var i = 1; i < _gadgets.length; i++){
				if (_gadgets[i] && (_gadgets[i].gadget == ("remote_iframe_" + gadget.id)) )
					_gadgets[i].gadget = null;

			}
		}
	}
})();
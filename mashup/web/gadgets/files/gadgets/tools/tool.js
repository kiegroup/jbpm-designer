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


if (! tool)
	var tool = {};

gadgets.util.registerOnLoadHandler(init);

var rpcHandler;

function init(){
	
	gadgets.rpc.call("..", "dispatcher.register", function(reply){return;}, "tool");
	
	gadgets.window.setTitle('Test Tool');
	
	tool.rpcHandler.init(true);
	
	tool.rpcHandler.setSelectionHandler(displaySelected, this);
	/*
	//registers for the event selectionChanged
	gadgets.rpc.call(
		null, 
		"dispatcher.registerSelection", 
		function(reply){return;}, 
		"all"
	);

	//eventHandler for selectionChanged
	gadgets.rpc.register(
		"handleSelection", 
		displaySelected
	);
	
	*/
	
};

function getRPCs(){
	
	gadgets.rpc.call(
		null,
		'dispatcher.getRPCs',
		function (reply){alert(reply);},
		"all"
	);
	
};

function getGadgets(){
	gadgets.rpc.call(
			null,
			'dispatcher.getAvailableGadgets',
			function (reply){alert(reply);},
			""
		);
}

// adds a new viewer with the specified url
function loadViewer(url){
	
	gadgets.rpc.call(
			null, 
			'dispatcher.displayModel', 
			function(reply){return}, 
			url);
	
};

// centers an abitrary shape in all modelviewers
function centerShape(){
	
	gadgets.rpc.call(
		null, 
		"dispatcher.countViewer", 
		function(reply){
			var validViewers = reply.evalJSON();
			
			//request for shapes from all viewers
			for (var i = 0; i < validViewers.length; i++){
				if (validViewers[i] != ""){
					var channel = "dispatcher." + parseInt(validViewers[i]) + ".sendShapes";
					gadgets.rpc.call(
						null, 
						channel, 
						function(reply2){
							var shapes = reply2.split(";");
							var channel = "dispatcher." + shapes.shift().toString() + ".centerShape";
							var _nodes = shapes[0].evalJSON();
							var toCenter = _nodes[Math.floor(Math.random() * _nodes.length)].ressourceId;
							gadgets.rpc.call(null, channel, function(){return;}, toCenter);
					}, 
					"");
						
				}
			}
		},
		"");
};

// displays the labels of all selected shapes
function displaySelected(reply){ 
	
	var selectedShapes = reply.split(";");
	var output = 'viewer ' + selectedShapes.shift().toString() + ' has selected:' + '<br>';
	
	var nodes = selectedShapes[0].evalJSON(true);
	
	for (var i = 0; i < nodes.length; i++)
		output += nodes[i].name + '<br>';
	
	document.getElementById("console").innerHTML = output;
	gadgets.window.adjustHeight();
};

// translates the rpc-message into a collection of nodes with attributes name and ressurceId
function parseToNodes(nodes){
	
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
};

// requests a list nodes from all viewers and marks some of them
function mark(msg) {
	
	gadgets.rpc.call(
		null, 
		"dispatcher.countViewer", 
		function(reply){
			var validViewers = reply.evalJSON();
			
			//request for shapes from all viewers
			for (var i = 0; i < validViewers.length; i++){
				if (validViewers[i] != ""){
					var channel = "dispatcher." + parseInt(validViewers[i]) + ".sendShapes";
					gadgets.rpc.call(null, channel, selectShapes, msg);
		}}}, 
		""
	);
};

// selects every fifth shape to highlight
function selectShapes(reply){

	var shapes = reply.split(";");
	var channel = "dispatcher." + shapes.shift().toString() + ".mark";
	var _nodes = shapes[0].evalJSON();
	
	var output = 'mark nodes: <br>';	
	var toMark = [];
	
	for (var i = 0; i < _nodes.length; i++){
		if ( i % 5 == 0){
			output += _nodes[i].name + '<br>';
			toMark.push(_nodes[i].resourceId);
		}
	}
	
	gadgets.rpc.call(null, channel, function(){return;}, Object.toJSON({toMark : toMark}));
	$("console").innerHTML = output;
	gadgets.window.adjustHeight();
	
};

// restes markers in all viewers
function undoMark(){

	gadgets.rpc.call(
		null, 
		"dispatcher.countViewer", 
		function(viewers){
			var validViewers = viewers.evalJSON();
			
			//remove markers from all viewers
			for (var i = 0; i < validViewers.length; i++){
				if (validViewers[i] != ""){
					var channel = "dispatcher." + parseInt(validViewers[i]) + ".undoMark";
					gadgets.rpc.call(null, channel, function(reply){return;}, "all");
				}	
			}
		}, 
		""
	);	
};

function greyModel(viewerIndex){
	
	var channel = "dispatcher." + viewerIndex + ".greyModel";
	
	gadgets.rpc.call(
		null,
		channel,
		function(){return;},
		""
	);
};

function undoGrey(viewerIndex){
	var channel = "dispatcher." + viewerIndex + ".sendShapes";
	gadgets.rpc.call(null, channel, removeShadows, "");
};

function removeShadows(reply){
	var shapes = reply.split(";");
	var channel = "dispatcher." + shapes.shift().toString() + ".undoGrey";
	var _nodes = shapes[0].evalJSON();
	
	var output = 'show nodes: <br>';	
	var toShow = [];
	
	for (var i = 0; i < _nodes.length; i++){
		if ( i % 5 == 0){
			output += _nodes[i].name + '<br>';
			toShow.push(_nodes[i].resourceId);
		}
	}
	
	gadgets.rpc.call(null, channel, function(){return;}, Object.toJSON(toShow));
	$("console").innerHTML = output;
	gadgets.window.adjustHeight();
}

// nothing useful
function test(){
	$("console").innerHTML = "unregistered eventListener for selectionChanged";
};

// unregister eventlistener for selectionChanged
function unregisterSelection(){
	
	tool.rpcHandler.unregisterSelection(test);
	
	/* 
	gadgets.rpc.call(
		null, 
		"dispatcher.unregisterSelection", 
		function(reply){
			document.getElementById("console").innerHTML = "unregistered eventListener for selectionChanged";
		}, 
		""
	);
	*/
};


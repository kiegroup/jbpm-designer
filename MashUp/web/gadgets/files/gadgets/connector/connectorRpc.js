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


if (!connector)
	var connector = {};

connector.rpc = (function(){
	
	var connections = new Array();
	var selections = {};
	
	return {
	
		init: function(){
			gadgets.rpc.call(
				null, 
				"dispatcher.registerSelection", 
				function(reply){return;}, 
				"all"
			);
	
			gadgets.rpc.register(
				"handleSelection", 
				connector.rpc.updateSelection
			);
		},
		
		updateSelection: function(reply){
			
			var selectedShapes = reply.split(";");
			var index = selectedShapes.shift();
			
			var nodes = selectedShapes[0].evalJSON(true);
			
			if (!selections[index]){
				var channel = "dispatcher." + index + ".sendInfo";
				gadgets.rpc.call(
					null,
					channel,
					function(reply){
						var info = reply.evalJSON();
						selections[index] = {
								shapes: nodes,
								title: info.title,
								url: info.url
								//description: info.description
						};
					},
					""
				);	
			} else{
				selections[index].shapes = nodes;	
			}		
		},
		
		createConnection: function(){
			
			var handleCancel = function(){
				panel.cancel();
				return true;
				
			};
		
			var handleSubmit = function(){
				alert(getData().model[0]);
				panel.cancel();
				return true;
			};
		
			
			var confirmDialog = '<form method="post"><br>you selected:';
			
			for (s in selection){
				confirmDialog += '<input type = "checkbox" name = "model" value = "'+ s.url + '">' 
							+ connector.rpc.modelCell(s) + '</input>'; 				
			}
			
			confirmDialog +=  '</form>';
	
			// dialog to confirm selection
			var panel = new YAHOO.widget.Dialog("panel", 
					{ width:"400px", visible : false, constraintoviewport : true, x: 30, y: 40, 
				buttons : [ { text:"Submit", handler: connector.rpc.handleSubmit, isDefault:true }, 
				            { text:"Cancel", handler: connector.rpc.handleCancel } ] 
			} ); 	
			
	
			panel.setHeader("Confirm Selection!");
			panel.setBody(confirmDialog);
			panel.render("connector");	

		},
		
		
		modelCell: function(selection){
			
			var model = selection.title + ': <br>';	
			var shapes = "";
			
			for (var i = 0; i < selection.shapes.length; i++)
				shapes += selection.shapes.name + ' ,';
			
			return model + shapes;
		},

		// expects a keymap of nodes and returns a string with some properties of all shapes
		// properties: ressourceId, name/label (others properties can be added)
		// ressourceIdA.labelA,ressourceIdB.labelB,...
		_stringify: function(nodes){
			
			var infoString = '';
			
			for (var key in nodes){
				infoString += key + ".";
					if (nodes[key].properties.name)
						infoString += nodes[key].properties.name + ',';
					else 
						infoString += ','
			}
			return infoString;
		}
	}
})();

connector.rpc.init();



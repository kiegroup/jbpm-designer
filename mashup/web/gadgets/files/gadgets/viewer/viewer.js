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

if (! viewer)
	var viewer ={};

viewer = (function(){

	return {
		
		rpcHandler:  null,
		
		/*
		 * initializes movi-api
		 * loads needed yui files
		 * 
		 */
		init: function(){
			
			viewer.rpcHandler = new rpcHandler();
			
			// gadgets.window.setTitle('Viewer');
			MOVI.init(
					
				function(){
					
				    new YAHOO.util.YUILoader({ 
						base: "http://yui.yahooapis.com/2.7.0/build/", 
				        require: ["fonts","grids","layout","reset","resize"],
				        loadOptional: false, 
				        combine: true, 
				        filter: "MIN", 
				        allowRollup: true, 
				        onSuccess: function(){ viewer._init();}
				    }).insert(); 
		
				},
				"http://oryx-editor.googlecode.com/svn/trunk/poem-jvm/src/javascript/movi/src",
				undefined,
				["resize"]
			);
			
		},
		
		/*
		 * mainly registers RPCs
		 * displays model upload dialog in the toolbar (footer) 
		 */
		_init: function(){
		
			// register on public dispatcher (container)
			gadgets.rpc.call("..", "dispatcher.register", function(reply){
				
				viewer.rpcHandler.setIndex(reply);
				document.getElementById('rpc').innerHTML = "registered as " + reply;
				
			}, "info");
			
			gadgets.rpc.register(
				"loadModel", 
				function(args){ return viewer.rpcHandler.loadModel(args); }
			);
			
			gadgets.rpc.register(
				"sendInfo", 
				function(args) { return viewer.rpcHandler.sendInfo(args); }
			);
			
			//send ressourceIDs and labels of all nodes
			gadgets.rpc.register(
				"sendShapes", 
				function(args) { return viewer.rpcHandler.sendShapes(args); }
			);
			
			//send ressourceIDs and labels of all selected nodes
			gadgets.rpc.register(
				"sendSelection", 
				function(args) { return viewer.rpcHandler.sendSelection(args); }
			);
			
			//highlight specified shapes (ressourceID)
			gadgets.rpc.register(
				"mark", 
				function(args){ return viewer.rpcHandler.markShapes(args); }
			);
			
			// remove markers from one or more shapes
			gadgets.rpc.register(
				"undoMark", 
				function(args){ return viewer.rpcHandler.undoMarking(args); }
			);	
			
			// centers a shape specified by ressourceId
			gadgets.rpc.register(
				"centerShape", 
				function(args){ return viewer.rpcHandler.centerShapes(args); }
			);	
		
			// inner layout
			viewer.layout = new YAHOO.widget.Layout({ 
				units: [ 
		            { position: 'bottom', header: 'Viewer - Menu', height: 120, resize: false, body: 'bottom', gutter: '5px', collapse: true }, 
		            { position: 'center', body: 'center'} 
		            ] 
		     }); 
			
			
			/*
			 * informs the container that a new viewer-widget was added
			 * if the conatiner has some models left to display 
			 * one can be loaded into this viewer
			 *
			 */
			gadgets.rpc.call(null, "dispatcher.loadWaiting", function(reply){
				
				// first dialog to enter a url
				if (reply == "noneWaiting"){
					html = new Array();
			
					html.push('<p>Select a Model to be displayed!<br>');
					html.push('<input id="model" type="text" size="45" value="http://oryx-editor.org/backend/poem/model/"></input>');
					html.push('<a href="#" onclick= "viewer.load(); return false;">browse</a> </p>');
		
					document.getElementById('start').innerHTML =html.join('');
				}
		
			}, "");
		
			viewer.layout.render(); 
			//gadgets.window.adjustHeight(); 		
			
		},
		
		// loads the model the user has defined via url into the viewer
		load: function() {
			
			// load the model from the specified url
			var url	= document.getElementById('model').value;
			
			// panel title changes to model url
			gadgets.rpc.call("..", "dispatcher.setTitle", function(reply){return;}, url);
			
			viewer.rpcHandler.setUrl(url);
			viewer.addViewer(url);
			return false;
		
		},
		
		// initializes modelViewer (movi-api)
		addViewer: function(url) {
			
			// removes start dialog
			document.getElementById('start').removeChild(document.getElementById('start').firstChild);
		
			var modelViewer = new MOVI.widget.ModelViewer("viewer");
			document.getElementById('viewer').className = "viewer";
			
			modelViewer.loadModel(url ,  { 	
			
				onSuccess: function(){ 
					viewer.rpcHandler.setViewer(modelViewer);
					viewer.addNavigator(modelViewer); 
				},	
				
				onFailure: function(){
					viewer.url = 
						prompt("The specified direction does not lead to a valid model!");
					viewer.addViewer(modelViewer);
				}
			} );
		},
		
		// navigator for modelviewer in toolbar
		addNavigator: function(modelViewer){
			
			var	navigator = new MOVI.widget.ModelNavigator("navigator", modelViewer);
			document.getElementById('navigator').className = "navigator";
			
			viewer.addToolbar(navigator, modelViewer);
			viewer.enableMultiselect(modelViewer);
			
		},
		
		// toolbar below modelviewer (footer)
		addToolbar: function(navigator, modelViewer){
			
			// toolbar below navigator
			var toolbar = new MOVI.widget.Toolbar("toolbar", modelViewer);
		
			// button for fullscreen
			var fullscreenViewer = new MOVI.widget.FullscreenViewer(modelViewer);
			
			// TODO
			toolbar.addButton({
			    icon: "http://localhost:8080/gadgets/files/gadgets/viewer/icons/arrow_switch.png",
			    tooltip: "change between multi und single select",
			    group: "Selection Modus",
			    callback: viewer.changeSelectModus
			});
			
			toolbar.addButton({
			    icon: "http://localhost:8080/gadgets/files/gadgets/viewer/icons/delete.png",
			    tooltip: "reset selection",
			    group: "Selection Modus",
			    callback: function(){ viewer.rpcHandler.selection.reset(); }
			});
			
			//toolbar.showGroupCaptions();
			
			toolbar.addButton({
				icon: "http://localhost:8080/gadgets/files/gadgets/viewer//icons/arrow_out.png",
				caption: 'fullscreen',
				tooltip: 'View the model in fullscreen mode',
				group: 'View options',
				callback: fullscreenViewer.open,
				scope: fullscreenViewer
			});
			
			//zoom slider for viewer
			zoomslider = new MOVI.widget.ZoomSlider("zoomslider", modelViewer)
			
			var resize = new YAHOO.util.Resize(modelViewer, {
		        handles		: ['b', 'r'],
		        minHeight	: 200,
		        minWidth	: 200,
		    });
			
			// viewer is fitted to the bounds of the outer panel
			resize.on("startResize", function() 
					{ modelViewer.onZoomLevelChangeStart.fire(modelViewer.getZoomLevel()); });
			
			resize.on("resize", function() {
					navigator.update();
					zoomslider.onChange();
					
			}, this, true);
					
			resize.on("endResize", function(){
					modelViewer.onZoomLevelChangeEnd.fire(modelViewer.getZoomLevel());
					
					console.info("end resize", {"unit": viewer.layout.getUnitByPosition('bottom')});
		
					viewer.layout.getUnitByPosition('bottom').body.offsetHeight = document.getElementById('viewer').offsetHeight + 20 ;
					viewer.layout.getUnitByPosition('bottom').body.offsetWidth = document.getElementById('viewer').offsetWidth + 20 ;
					viewer.layout.getUnitByPosition('bottom').resize();
					//gadgets.window.adjustHeight();
			});
		
			
			resize.reset();
			//gadgets.window.adjustHeight();
			
			
			// not yet implemented, possibly redundant
			var	viewport = gadgets.window.getViewportDimensions();
			var dimensions = viewport.height.toString() + '.' + viewport.width.toString();
			gadgets.rpc.call("..", "dispatcher.resize", function(reply){return;}, dimensions);
			
		},
		
		// initially multiselect-mode in activated
		enableMultiselect: function(modelViewer){
			
			var selection = new MOVI.util.ShapeSelect(modelViewer, true);
			selection.onSelectionChanged( function(){ viewer.rpcHandler.throwSelectionChanged() } );
			
			viewer.rpcHandler.setSelection(selection);
			
			return selection;
			
		},
		
		// selection mode changes between single- and multi-select
		changeSelectModus: function(){
			viewer.rpcHandler.selection.reset();
			if (viewer.rpcHandler.selection._allowMultiselect)
				viewer.rpcHandler.selection._allowMultiselect = false;
			else 
				viewer.rpcHandler.selection._allowMultiselect = true;
			
		}
	}
})();

gadgets.util.registerOnLoadHandler(viewer.init);

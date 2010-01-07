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

Viewer = function(){
	
	Viewer.superclass.constructor.call(this, "viewer");
	this.init();
	// TODO call rpc container serves icon url from gadgets.xml
};


YAHOO.extend( Viewer, AbstractGadget, {
		
	modelViewer: null,
	
	/*
	 * initializes movi-api
	 * loads needed yui files
	 * 
	 */
	init: function(){
		
		this.rpcHandler = new rpcHandler(this);
	
		// register on public dispatcher (container)
		this.registerRPCs();
				
		// inner layout
		this.layout = new YAHOO.widget.Layout({ 
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
				html.push('<input id="model" type="text" size="45" value="/backend/poem/model/"></input>');
				html.push('<a href="#" onclick= "viewer.load(); return false;">browse</a> </p>');
	
				$('start').innerHTML =html.join('');
			}
	
		}, "");
	
		this.layout.render(); 
		//gadgets.window.adjustHeight(); 		
		
	},
	
	registerRPCs: function(){
		
		// fills viewer with model from specified url
		this.registerRPC("loadModel", "modelUrl", "", this.rpcHandler.loadModel, this.rpcHandler );
		
		// sends meta data
		//TODO
		this.registerRPC("sendInfo", "", "metaDataModel", this.rpcHandler.sendInfo, this.rpcHandler);
		
		//send ressourceIDs and labels of all nodes
		// TODO nodes rename (attributes)
		this.registerRPC("sendShapes", "", "allNodes", this.rpcHandler.sendShapes, this.rpcHandler);
		
		//send ressourceIDs and labels of all selected nodes		
		this.registerRPC("sendSelection", "", "shapeResourceIds", this.rpcHandler.sendSelection, this.rpcHandler);
		
		// remove selection
		this.registerRPC("resetSelection", "", "", this.rpcHandler.resetSelection, this.rpcHandler);
		
		// set selection mode (multi or single)
		this.registerRPC("setSelectionMode", "mode", "", this.rpcHandler.setSelectionMode, this.rpcHandler);
		
		// TODO (all)highlight specified shapes (ressourceID) or all
		this.registerRPC("mark", "shapeResourceIds", "", this.rpcHandler.markShapes, this.rpcHandler);
		
		// TODO (all) remove markers from one or more shapes
		this.registerRPC("undoMark", "","", this.rpcHandler.undoMarking, this.rpcHandler);
		
		// center a shape specified by ressourceId
		this.registerRPC("centerShape", "shapeResourceId", "", this.rpcHandler.centerShapes, this.rpcHandler);
		
		// cover all shapes with a grey shadow
		this.registerRPC("greyModel", "", "", this.rpcHandler.greyModel, this.rpcHandler);
		
		// remove shadow from shapes (from all or just a subset)
		this.registerRPC("undoGrey", "", "", this.rpcHandler.undoGrey, this.rpcHandler);
	
	},
	
	// loads the model the user has defined via url into the viewer
	load: function() {
		
		// load metaData
		var requestUrl = $('model').value + "/meta";
		new Ajax.Request(requestUrl, {
			method			: "get",
			asynchronous 	: false,
			onSuccess		: function(response){
				var metaData = response.responseText.evalJSON();
				this.setTitle(metaData.title);
				this.rpcHandler.setUrl(this.SERVER_BASE + $('model').value);
				this.rpcHandler.setTitle(metaData.title);
				this.addViewer(this.SERVER_BASE + $('model').value);
				return false;
			}.bind(this),

			onFailure		: function(){
				alert('Oryx: Server communication failed!');
			}
		});
	
	},
	
	// initializes modelViewer (movi-api)
	addViewer: function(url) {
		
		// removes start dialog
		$('start').removeChild($('start').firstChild);
	
		this.modelViewer = new MOVI.widget.ModelViewer("viewer");
		$('viewer').className = "viewer";
		
		this.modelViewer.loadModel(url ,  { 	
		
			onSuccess: function(){ 
				this.rpcHandler.setViewer();
				this.addNavigator(); 
			}.bind(this),	
			
			onFailure: function(){				
				var url = 
					prompt("The specified direction does not lead to a valid model!");
				this.addViewer(url);
			}.bind(this)
		} );
	},
	
	// navigator for modelviewer in toolbar
	addNavigator: function(){
		
		var	navigator = new MOVI.widget.ModelNavigator("navigator", this.modelViewer);
		$('navigator').className = "navigator";
		
		this.addToolbar(navigator);
		this.enableMultiselect();
		
	},
	
	// toolbar below modelviewer (footer)
	addToolbar: function(navigator){
		
		// toolbar below navigator
		var toolbar = new MOVI.widget.Toolbar("toolbar", this.modelViewer);
	
		// button for fullscreen
		var fullscreenViewer = new MOVI.widget.FullscreenViewer(this.modelViewer);
		
		var viewerHome = this.GADGET_BASE + "viewer";
		
		// TODO
		toolbar.addButton({
		    icon: viewerHome + "/icons/arrow_switch.png",
		    tooltip: "change between multi und single select",
		    //group: "Selection Modus",
		    callback: this.changeSelectModus.bind(this)
		});
		
		var clearModel = function(){
			this.rpcHandler.selection.reset();
			this.rpcHandler.undoGrey("all");
			this.rpcHandler.undoMarking("all");
		};
		
		toolbar.addButton({
		    icon: viewerHome + "/icons/delete.png",
		    tooltip: "reset selection, shadows and markers",
		    //group: "Selection Modus",
		    callback: clearModel.bind(this)
		});
		
		//toolbar.showGroupCaptions();
		
		toolbar.addButton({
			icon: viewerHome + "/icons/arrow_out.png",
			caption: 'fullscreen',
			tooltip: 'View the model in fullscreen mode',
			//group: 'View options',
			callback: fullscreenViewer.open,
			scope: fullscreenViewer
		});
		
		//zoom slider for viewer
		zoomslider = new MOVI.widget.ZoomSlider("zoomslider", this.modelViewer)
		
		var resize = new YAHOO.util.Resize(this.modelViewer, {
	        handles		: ['b', 'r'],
	        minHeight	: 200,
	        minWidth	: 200,
	    });
		
		// viewer is fitted to the bounds of the outer panel
		resize.on("startResize", function() 
				{ this.modelViewer.onZoomLevelChangeStart.fire(this.modelViewer.getZoomLevel()); }.bind(this));
		
		resize.on("resize", function() {
				navigator.update();
				zoomslider.onChange();
				
		}, this, true);
				
		resize.on("endResize", function(){
				this.modelViewer.onZoomLevelChangeEnd.fire(this.modelViewer.getZoomLevel());
				
				console.info("end resize", {"unit": viewer.layout.getUnitByPosition('bottom')});
	
				this.layout.getUnitByPosition('bottom').body.offsetHeight = $('viewer').offsetHeight + 20 ;
				this.layout.getUnitByPosition('bottom').body.offsetWidth = $('viewer').offsetWidth + 20 ;
				this.layout.getUnitByPosition('bottom').resize();
		}.bind(this));
	
		
		resize.reset();
		//gadgets.window.adjustHeight();
		
		
		// TODO not yet implemented, possibly redundant
		var	viewport = gadgets.window.getViewportDimensions();
		var dimensions = viewport.height.toString() + '.' + viewport.width.toString();
		gadgets.rpc.call("..", "dispatcher.resize", function(reply){return;}, dimensions);
		
	},
	
	// initially multiselect-mode in activated
	enableMultiselect: function(){
		
		var selection = new MOVI.util.ShapeSelect(this.modelViewer, true);
		selection.onSelectionChanged( function(){ this.rpcHandler.throwSelectionChanged() }.bind(this) );
		
		this.rpcHandler.setSelection(selection);
		
		return selection;
		
	},
	
	// selection mode changes between single- and multi-select
	changeSelectModus: function(){
		viewer.rpcHandler.selection.reset();
		if (this.rpcHandler.selection._allowMultiselect)
			this.rpcHandler.selection._allowMultiselect = false;
		else 
			this.rpcHandler.selection._allowMultiselect = true;
		
	}

});


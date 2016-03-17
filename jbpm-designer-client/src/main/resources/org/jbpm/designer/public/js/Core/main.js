/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
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

var idCounter = 0;
var ID_PREFIX = "resource";

/**
 * Main initialization method. To be called when loading
 * of the document, including all scripts, is completed.
 */
function init() {
	/* When the blank image url is not set programatically to a local
	 * representation, a spacer gif on the site of ext is loaded from the
	 * internet. This causes problems when internet or the ext site are not
	 * available. */
	Ext.BLANK_IMAGE_URL = ORYX.BASE_FILE_PATH + 'lib/ext-2.0.2/resources/images/default/s.gif';

	ORYX.Log.debug("Querying editor instances");

	// Hack for WebKit to set the SVGElement-Classes
	ORYX.Editor.setMissingClasses();
    // use this hook to get initialized through the plugin in charge of loading the model
    //window.onOryxResourcesLoaded();

}

/**
   @namespace Global Oryx name space
   @name ORYX
*/
if(!ORYX) {var ORYX = {};}

/**
 * The Editor class.
 * @class ORYX.Editor
 * @extends Clazz
 * @param {Object} config An editor object, passed to {@link ORYX.Editor#loadSerialized}
 * @param {String} config.id Any ID that can be used inside the editor. If fullscreen=false, any HTML node with this id must be present to render the editor to this node.
 * @param {boolean} [config.fullscreen=true] Render editor in fullscreen mode or not.
 * @param {String} config.stencilset.url Stencil set URL.
 * @param {String} [config.stencil.id] Stencil type used for creating the canvas.  
 * @param {Object} config.properties Any properties applied to the canvas.
*/
ORYX.Editor = {
    /** @lends ORYX.Editor.prototype */
	// Defines the global dom event listener 
	DOMEventListeners: new Hash(),

	// Defines the selection
	selection: [],
	
	// Defines the current zoom level
	zoomLevel:1.0,

	construct: function(config) {
		// initialization.
		this._eventsQueue 	= [];
		this.loadedPlugins 	= [];
		this.pluginsData 	= [];

        this.simulationChartTimeUnit = "";
		this.simulationChartData = "";
		this.simulationEventData = "";
		this.simulationEventAggregationData = "";
		this.simulationInstancesData = "";
        this.simulationHTCostData = "";
        this.simulationHTResourceData = "";
		this.simulationChartTitle = "";
		this.simulationChartId = "";
		this.simulationChartNodeName = "";
		this.simulationPathData = "";
		this.simulationPathId = "";
		this.simulationPathSVG = "";
        this.localStorageSVG = "";
        this.imagePreviewSVG = "";

		//meta data about the model for the signavio warehouse
		//directory, new, name, description, revision, model (the model data)
		
		this.modelMetaData = config;
		
		var model = config;
		if(config.model) {
			model = config.model;
		}

        this.updateViewLockState();

        if(config.error) {
            Ext.Msg.show({
                title   : 'Unable to open Process',
                msg     : "Process will be opened with XML Editor",
                buttons: Ext.MessageBox.OK,
                fn      : function(text,btn) { parent.designeropeninxmleditortab(ORYX.UUID); }
                });
        }

		this.id = model.resourceId;
        if(!this.id) {
        	this.id = model.id;
        	if(!this.id) {
        		this.id = ORYX.Editor.provideId();
        	}
        }

        // Defines if the editor should be fullscreen or not
		this.fullscreen = model.fullscreen || true;

		// Initialize the eventlistener
		this._initEventListener();

		// Load particular stencilset
		if(ORYX.CONFIG.BACKEND_SWITCH) {
			var ssUrl = (model.stencilset.namespace||model.stencilset.url).replace("#", "%23");
        	ORYX.Core.StencilSet.loadStencilSet(ORYX.CONFIG.STENCILSET_HANDLER + ssUrl, this.id);
		} else {
			var ssUrl = model.stencilset.url;
        	ORYX.Core.StencilSet.loadStencilSet(ssUrl, this.id);
		}
        
        //load the extensions
        if(!!ORYX.CONFIG.SSEXTS){
        	ORYX.CONFIG.SSEXTS.each(function(ssext){
                this.loadSSExtension(ssext);
            }.bind(this));
        }

		// CREATES the canvas
		this._createCanvas(model.stencil ? model.stencil.id : null, model.properties);

		// GENERATES the whole EXT.VIEWPORT
		this._generateGUI(config);

		// Initializing of a callback to check loading ends
		var loadPluginFinished 	= false;
		var loadContentFinished = false;
		var initFinished = function(){	
			if( !loadPluginFinished || !loadContentFinished ){ return }
			this._finishedLoading();
		}.bind(this)
		
		// disable key events when Ext modal window is active
		ORYX.Editor.makeExtModalWindowKeysave(this._getPluginFacade());
		
		// LOAD the plugins
		window.setTimeout(function(){
			this.loadPlugins();
			loadPluginFinished = true;
			initFinished();
		}.bind(this), 100);

		// LOAD the content of the current editor instance
		window.setTimeout(function(){
            this.loadSerialized(model);
            this.getCanvas().update();
			loadContentFinished = true;
			initFinished();
		}.bind(this), 200);
	},

	updateViewLockState: function() {
		if(ORYX.INSTANCE_VIEW_MODE != true) {
			if ( (typeof parent.isLocked === "function") && (typeof parent.isLockedByCurrentUser === "function") ) {
				var isEditorLocked = parent.isLocked();
				var isEditorLockedByCurrentUser = parent.isLockedByCurrentUser();
				var isReadOnly = ( ORYX.READONLY == true ) || ( ORYX.VIEWLOCKED == true );

				if(!isEditorLocked) {
					ORYX.VIEWLOCKED = false;
				} else {
					if(isEditorLocked && !isEditorLockedByCurrentUser) {
						ORYX.VIEWLOCKED = true;
					} else if(isEditorLocked && isEditorLockedByCurrentUser) {
						ORYX.VIEWLOCKED = false;
					}
				}

				// We're in read only mode, but got the lock, so let's reload to enter edit mode.
				if (isReadOnly && !ORYX.VIEWLOCKED) {
					if (typeof parent.reload === "function") {
						ORYX.PROCESS_SAVED = true;
						parent.reload();
					}
				}
			}
		}
	},	

	_finishedLoading: function() {
		if(Ext.getCmp('oryx-loading-panel')){
			Ext.getCmp('oryx-loading-panel').hide()
		}
		
		// Do Layout for viewport
		this.layout.doLayout();
		// Generate a drop target
		new Ext.dd.DropTarget(this.getCanvas().rootNode.parentNode);
		
		// Fixed the problem that the viewport can not 
		// start with collapsed panels correctly
		if (ORYX.CONFIG.PANEL_RIGHT_COLLAPSED === true){
			this.layout_regions.east.collapse();
		}
		if (ORYX.CONFIG.PANEL_LEFT_COLLAPSED === true){
			this.layout_regions.west.collapse();
		}
		
		// Raise Loaded Event
		this.handleEvents( {type:ORYX.CONFIG.EVENT_LOADED} );        
	},
	
	_initEventListener: function(){

		// Register on Events
		
		document.documentElement.addEventListener(ORYX.CONFIG.EVENT_KEYDOWN, this.catchKeyDownEvents.bind(this), true);
		document.documentElement.addEventListener(ORYX.CONFIG.EVENT_KEYUP, this.catchKeyUpEvents.bind(this), true);

		// Enable Key up and down Event
		this._keydownEnabled = 	true;
		this._keyupEnabled =  	true;

		this.DOMEventListeners[ORYX.CONFIG.EVENT_MOUSEDOWN] = [];
		this.DOMEventListeners[ORYX.CONFIG.EVENT_MOUSEUP] 	= [];
		this.DOMEventListeners[ORYX.CONFIG.EVENT_MOUSEOVER] = [];
		this.DOMEventListeners[ORYX.CONFIG.EVENT_MOUSEOUT] 	= [];
		this.DOMEventListeners[ORYX.CONFIG.EVENT_SELECTION_CHANGED] = [];
		this.DOMEventListeners[ORYX.CONFIG.EVENT_MOUSEMOVE] = [];
				
	},
	
	_chartSelected : function(node,event) {
		this._getPluginFacade().raiseEvent({
			type 		: ORYX.CONFIG.EVENT_SIMULATION_DISPLAY_GRAPH, 
	        value		: node
	    });
	},
	
	/**
	 * Generate the whole viewport of the
	 * Editor and initialized the Ext-Framework
	 * 
	 */
	_generateGUI: function(config) {

		//TODO make the height be read from eRDF data from the canvas.
		// default, a non-fullscreen editor shall define its height by layout.setHeight(int) 
		
		// Defines the layout hight if it's NOT fullscreen
		var layoutHeight 	= 660;
	
		var canvasParent	= this.getCanvas().rootNode.parentNode;
		
		
		this.centerContentPanel = new Ext.Panel({
			autoScroll: true,
			cmargins: {left:0, right:0},
			border: false,
			items	: {
				layout	: "fit",
				autoHeight: true,
				el		: canvasParent
			}
		});

		this.resultsChartPanel = new Ext.Panel({
			border: false,
			id: 'simchart',
		    html: "<svg></svg>"
		});
		
		this.simResultsContentPanel = new Ext.Panel({
			id: "simresultscontent",
			autoScroll: true,
			autoheight: true, 
			border: false,
			items	: [{
			    xtype : "component",
			    id    : 'simchartframe',
			    anchor: '100%',
			    autoScroll: true,
			    autoEl : {
			        tag : "iframe",
			        src : ORYX.BASE_FILE_PATH + 'simulation/default.jsp',
			        width: "100%",
			            height: "500",
			            frameborder: "0",
			            scrolling: "auto"
			    }
			}]
		});

        this.simInfoPanel = new Ext.Panel({
            bodyStyle:'background:#ffff;font-size:9px;font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;padding-left:5px;',
            id: "siminfopanel",
            title: ORYX.I18N.View.sim.resultsInfo,
            autoScroll: true,
            autoheight: false,
            height: 300,
            border: false,
            html: ""
        });

		this.simResultsTree = new Ext.tree.TreePanel({
			id: "simresultscharts",
			title: ORYX.I18N.View.sim.resultsGraphs,
            //layout: 'fit',
			animate:true,
			loader: new Ext.tree.TreeLoader(),
			rootVisible: false,
            scroll: true,
            autoScroll: true,
            autoheight: true,
            viewConfig : {
                style : { overflow: 'scroll', overflowY: 'scroll', overflowX: 'scroll' }
            },
			lines: true,
			listeners: {
		        click: {
		            fn:this._chartSelected.bind(this)
		        }
		    }
		});
		var simTreeRoot = new Ext.tree.TreeNode({
		    draggable: false,
		    id: 'simcharts'
		});
		this.simResultsTree.setRootNode(simTreeRoot);

		this.simResultsContentPanelLayout = new Ext.Panel({
		    width: "100%",
            autoscroll: true,
            //sheight: 1000,
            //autoheight: true,
		    layout: 'border',
		    items: [{
		    	xtype:'panel',
		        region:'east',
		        margins: '5 0 0 5',
		        layout	: 'fit',
		        anchor:'100%',
		        width: 300,
		        border: false,
		        collapsible: true,  
		        autoscroll: true,
		        split: false,
		        //minSize: 100,
		        //maxSize: 500,
		        //autoheight: true,
		        cmargins: '5 5 0 5',
                bodyCfg : { style: {'overflow':'auto'} },
                autoScroll : true,
		        items: [this.simResultsTree, this.simInfoPanel]
		    },{
		    	xtype:'panel',
		        region: 'center',  
		        layout	: 'fit',
		        anchor:'100%',
		        border: false,
		        autoscroll: true,
		        autoheight: true,
		        margins: '5 5 0 0',
		        items: [this.simResultsContentPanel]
		    }]
		});

		this.processDocContentPanel = new Ext.Panel({
			id: "processdoccontent",
			autoScroll: true,
			autoheight: true,
			border: false,
			items	: [{
				xtype : "component",
				id    : 'processdocframe',
				anchor: '100%',
				autoScroll: true,
				autoEl : {
					tag : "iframe",
					src : ORYX.BASE_FILE_PATH + 'processdoc/default.jsp',
					width: "100%",
					height: "500",
					frameborder: "0",
					scrolling: "auto"
				}
			}]
		});

		this.processDocPanelLayout = new Ext.Panel({
			width: "100%",
			autoscroll: true,
			//sheight: 1000,
			//autoheight: true,
			layout: 'border',
			items: [{
				xtype:'panel',
				region: 'center',
				layout	: 'fit',
				anchor:'100%',
				border: false,
				autoscroll: true,
				autoheight: true,
				margins: '5 5 0 0',
				items: [this.processDocContentPanel]
			}]
		});
		
		var tabs_config = {
				id : 'maintabs',
				region	: 'center',
				cls		: 'x-panel-editor-center',
				autoScroll: false,
				cmargins: {left:0, right:0},
				activeTab: 0,
				border: false,
		        tabPosition: 'top',
		        anchor: "100%",
		        deferredRender : false,
		        listeners: {
	             	tabchange: function(tabpanel, tab) {
						if (tab.id == 'processdoctab') {
							document.getElementById('processdocframe').contentWindow.showProcessDocs();
						}
	            		this.centerContentTabPannel.doLayout();
	            		this.simResultsContentPanelLayout.doLayout();
						this.processDocPanelLayout.doLayout();
	            		tabpanel.doLayout();
	             	}.bind(this)
	            },
		        items: [{
		        	layout: "fit",
		            title: ORYX.I18N.View.tabs.modelling,
                    id: 'processmodellingtab',
	                items	: [this.centerContentPanel]
		        },
		        {
		        	layout: "fit",
		        	title: ORYX.I18N.View.tabs.simResults,
                    id: 'simulationtab',
	                autoScroll   : false,
	                items	: [this.simResultsContentPanelLayout]
		        },
				{
					layout: "fit",
					title: ORYX.I18N.View.tabs.processDoc,
					id: 'processdoctab',
					autoScroll   : false,
					items	: [this.processDocPanelLayout]
				}
		        ]
			};
		this.centerContentTabPannel = new Ext.TabPanel(tabs_config);

        if(ORYX.READONLY == true) {
            Ext.getCmp('maintabs').remove("simulationtab");
			Ext.getCmp('maintabs').remove("processdoctab");
        }

        if(ORYX.VIEWLOCKED == true) {
            Ext.getCmp('maintabs').remove("simulationtab");
			Ext.getCmp('maintabs').remove("processdoctab");
        }

		// DEFINITION OF THE VIEWPORT AREAS
        var eastWidth = ORYX.CONFIG.PANEL_LEFT_WIDTH || 400;
        if(ORYX.READONLY == true) {
            eastWidth = 10;
        }

        if(ORYX.VIEWLOCKED == true) {
            eastWidth = 10;
        }

		this.layout_regions = {
				
				// DEFINES TOP-AREA
				north	: new Ext.Panel({ //TOOO make a composite of the oryx header and addable elements (for toolbar), second one should contain margins
					region	: 'north',
					cls		: 'x-panel-editor-north',
					autoEl	: 'div',
					border	: false
				}),	
				
				// DEFINES RIGHT-AREA
				east	: new Ext.Panel({
                    region	: 'east',
                    layout	: 'anchor',
                    autoEl	: 'div',
                    border: false,
                    cls		: 'x-panel-editor-east',
                    width	: eastWidth,
                    autoScroll:true,
                    split	: false,
                    animate: true,
                    collapsible : true,
                    titleCollapse: true,
                    title: "Properties",
                    plugins: new Ext.ux.PanelCollapsedTitlePlugin()
				}),

				// DEFINES BOTTOM-AREA
				south	: new Ext.Panel({
					region	: 'south',
					cls		: 'x-panel-editor-south',
					autoEl	: 'div',
					border	: false
				}),
				
				
				// DEFINES LEFT-AREA
				west	: new Ext.Panel({
					region	: 'west',
					layout	: 'anchor',
					autoEl	: 'div',
                    border: false,
					cls		: 'x-panel-editor-west',
					width	: ORYX.CONFIG.PANEL_LEFT_WIDTH || 200,
					autoScroll:true,
					split	: false,
					animate: true,
					collapsible : true,
					titleCollapse: true,
					title: ORYX.I18N.main.shapeRepo,
					plugins: new Ext.ux.PanelCollapsedTitlePlugin()
				}),
				
				
				// DEFINES CENTER-AREA (FOR THE EDITOR)
				center	: this.centerContentTabPannel
		};
		
		// Hide every region except the center
		for (region in this.layout_regions) {
			if ( (region != "center" && region != "north") && (ORYX.READONLY == true || ORYX.VIEWLOCKED == true)) {
                this.layout_regions[ region ].setVisible(false);
			}
		}
		
		// Config for the Ext.Viewport 
		var layout_config = {
			layout: 'border',
			items: [
				this.layout_regions.north,
				this.layout_regions.east,
				this.layout_regions.south,
				this.layout_regions.west,
				this.layout_regions.center
			]
		};
		this.contentviewport = new Ext.Viewport( layout_config  );

        //this.fullscreen = config.fullscreen;

		if (this.fullscreen) {
			this.layout = new Ext.Viewport( layout_config );
		} else {
            layout_config.renderTo 	= this.id;
			layout_config.height 	= layoutHeight;
			this.layout = new Ext.Panel( layout_config );
		}
		
		// Set the editor to the center, and refresh the size
	 	canvasParent.parentNode.setAttributeNS(null, 'align', 'center');
	 	canvasParent.setAttributeNS(null, 'align', 'left');
	 	this.getCanvas().setSize({
			width	: ORYX.CONFIG.CANVAS_WIDTH,
			height	: ORYX.CONFIG.CANVAS_HEIGHT
		});
	},

	/**
	 * adds a component to the specified region
	 * 
	 * @param {String} region
	 * @param {Ext.Component} component
	 * @param {String} title, optional
	 * @return {Ext.Component} dom reference to the current region or null if specified region is unknown
	 */
	addToRegion: function(region, component, title) {
		
		if (region.toLowerCase && this.layout_regions[region.toLowerCase()]) {
			var current_region = this.layout_regions[region.toLowerCase()];

			current_region.add(component);

			/*if( (region.toLowerCase() == 'east' || region.toLowerCase() == 'west') && current_region.items.length == 2){ //!current_region.getLayout() instanceof Ext.layout.Accordion ){
				var layout = new Ext.layout.Accordion( current_region.layoutConfig );
            	current_region.setLayout( layout );
				
				var items = current_region.items.clone();
				
				current_region.items.each(function(item){ current_region.remove( item )})
				items.each(function(item){ current_region.add( item )})
				
			}	*/		

			ORYX.Log.debug("original dimensions of region %0: %1 x %2", current_region.region, current_region.width, current_region.height)

			// update dimensions of region if required.
			if  (!current_region.width && component.initialConfig && component.initialConfig.width) {
				ORYX.Log.debug("resizing width of region %0: %1", current_region.region, component.initialConfig.width)	
				current_region.setWidth(component.initialConfig.width)
			}
			if  (component.initialConfig && component.initialConfig.height) {
				ORYX.Log.debug("resizing height of region %0: %1", current_region.region, component.initialConfig.height)
				var current_height = current_region.height || 0;
				current_region.height = component.initialConfig.height + current_height;
				current_region.setHeight(component.initialConfig.height + current_height)
			}
			
			// set title if provided as parameter.
			if (typeof title == "string") {
				current_region.setTitle(title);	
			}
						
			// trigger doLayout() and show the pane
			current_region.ownerCt.doLayout();

            if((ORYX.VIEWLOCKED == true || ORYX.READONLY == true) && current_region.region != "center" ) {
            } else {
                current_region.show();
            }

			if(Ext.isMac)
				ORYX.Editor.resizeFix();
			
			return current_region;
		}
		
		return null;
	},
	getAvailablePlugins: function(){
		var curAvailablePlugins=ORYX.availablePlugins.clone();
		curAvailablePlugins.each(function(plugin){
			if(this.loadedPlugins.find(function(loadedPlugin){
				return loadedPlugin.type==this.name;
			}.bind(plugin))){
				plugin.engaged=true;
			}else{
				plugin.engaged=false;
			}
			}.bind(this));
		return curAvailablePlugins;
	},

	loadScript: function (url, callback){
	    var script = document.createElement("script")
	    script.type = "text/javascript";
	    if (script.readyState){  //IE
	        script.onreadystatechange = function(){
	            if (script.readyState == "loaded" || script.readyState == "complete"){
	                script.onreadystatechange = null;
	                callback();
	            }
        	};
    	} else {  //Others
	        script.onload = function(){
	            callback();
	        };
		}
	    script.src = url;
		document.getElementsByTagName("head")[0].appendChild(script);
	},
	/**
	 * activate Plugin
	 * 
	 * @param {String} name
	 * @param {Function} callback
	 * 		callback(sucess, [errorCode])
	 * 			errorCodes: NOTUSEINSTENCILSET, REQUIRESTENCILSET, NOTFOUND, YETACTIVATED
	 */
	activatePluginByName: function(name, callback, loadTry){

		var match=this.getAvailablePlugins().find(function(value){return value.name==name});
		if(match && (!match.engaged || (match.engaged==='false'))){		
				var loadedStencilSetsNamespaces = this.getStencilSets().keys();
				var facade = this._getPluginFacade();
				var newPlugin;
				var me=this;
				ORYX.Log.debug("Initializing plugin '%0'", match.name);
				
					if (!match.requires 	|| !match.requires.namespaces 	|| match.requires.namespaces.any(function(req){ return loadedStencilSetsNamespaces.indexOf(req) >= 0 }) ){
						if(!match.notUsesIn 	|| !match.notUsesIn.namespaces 	|| !match.notUsesIn.namespaces.any(function(req){ return loadedStencilSetsNamespaces.indexOf(req) >= 0 })){
	
					try {
						
						var className 	= eval(match.name);
							var newPlugin = new className(facade, match);
							newPlugin.type = match.name;
							
							// If there is an GUI-Plugin, they get all Plugins-Offer-Meta-Data
							if (newPlugin.registryChanged) 
								newPlugin.registryChanged(me.pluginsData);
							
							// If there have an onSelection-Method it will pushed to the Editor Event-Handler
							if (newPlugin.onSelectionChanged) 
								me.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, newPlugin.onSelectionChanged.bind(newPlugin));
							this.loadedPlugins.push(newPlugin);
							this.loadedPlugins.each(function(loaded){
								if(loaded.registryChanged)
									loaded.registryChanged(this.pluginsData);
							}.bind(me));
							callback(true);
						
					} catch(e) {
						ORYX.Log.warn("Plugin %0 is not available", match.name);
						if(!!loadTry){
							callback(false,"INITFAILED");
							return;
						}
						this.loadScript("plugins/scripts/"+match.source, this.activatePluginByName.bind(this,match.name,callback,true));
					}
					}else{
						callback(false,"NOTUSEINSTENCILSET");
						ORYX.Log.info("Plugin need a stencilset which is not loaded'", match.name);
					}
								
				} else {
					callback(false,"REQUIRESTENCILSET");
					ORYX.Log.info("Plugin need a stencilset which is not loaded'", match.name);
				}

			
			}else{
				callback(false, match?"NOTFOUND":"YETACTIVATED");
				//TODO error handling
			}
	},

	/**
	 *  Laden der Plugins
	 */
	loadPlugins: function() {
		
		// if there should be plugins but still are none, try again.
		// TODO this should wait for every plugin respectively.
		/*if (!ORYX.Plugins && ORYX.availablePlugins.length > 0) {
			window.setTimeout(this.loadPlugins.bind(this), 100);
			return;
		}*/
		
		var me = this;
		var newPlugins = [];


		var loadedStencilSetsNamespaces = this.getStencilSets().keys();

		// Available Plugins will be initalize
		var facade = this._getPluginFacade();
		
		// If there is an Array where all plugins are described, than only take those
		// (that comes from the usage of oryx with a mashup api)
		if( ORYX.MashupAPI && ORYX.MashupAPI.loadablePlugins && ORYX.MashupAPI.loadablePlugins instanceof Array ){
			// Get the plugins from the available plugins (those who are in the plugins.xml)
			ORYX.availablePlugins = $A(ORYX.availablePlugins).findAll(function(value){
										return ORYX.MashupAPI.loadablePlugins.include( value.name )
									})
			
			// Add those plugins to the list, which are only in the loadablePlugins list
			ORYX.MashupAPI.loadablePlugins.each(function( className ){
				if( !(ORYX.availablePlugins.find(function(val){ return val.name == className }))){
					ORYX.availablePlugins.push( {name: className } );
				}
			})
		}
		
		ORYX.availablePlugins.each(function(value) {
			ORYX.Log.debug("Initializing plugin '%0'", value.name);
				if( (!value.requires 	|| !value.requires.namespaces 	|| value.requires.namespaces.any(function(req){ return loadedStencilSetsNamespaces.indexOf(req) >= 0 }) ) &&
					(!value.notUsesIn 	|| !value.notUsesIn.namespaces 	|| !value.notUsesIn.namespaces.any(function(req){ return loadedStencilSetsNamespaces.indexOf(req) >= 0 }) )&&
					/*only load activated plugins or undefined */
					(value.engaged || (value.engaged===undefined)) ){

				try {
					var className 	= eval(value.name);
					if( className ){
						var plugin		= new className(facade, value);
						plugin.type		= value.name;
						newPlugins.push( plugin );
						plugin.engaged=true;
					}
				} catch(e) {
					ORYX.Log.warn("Plugin %0 is not available", value.name);
				}
							
			} else {
				ORYX.Log.info("Plugin need a stencilset which is not loaded'", value.name);
			}
			
		});

		newPlugins.each(function(value) {
			// If there is an GUI-Plugin, they get all Plugins-Offer-Meta-Data
			if(value.registryChanged)
				value.registryChanged(me.pluginsData);

			// If there have an onSelection-Method it will pushed to the Editor Event-Handler
			if(value.onSelectionChanged)
				me.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, value.onSelectionChanged.bind(value));
		});

		this.loadedPlugins = newPlugins;
		
		// Hack for the Scrollbars
		if(Ext.isMac) {
			ORYX.Editor.resizeFix();
		}
		
		this.registerPluginsOnKeyEvents();
		
		this.setSelection();
		
	},

	/**
	 * Creates the Canvas
	 * @param {String} [stencilType] The stencil type used for creating the canvas. If not given, a stencil with myBeRoot = true from current stencil set is taken.
	 * @param {Object} [canvasConfig] Any canvas properties (like language).
	 */
	_createCanvas: function(stencilType, canvasConfig) {
        if (stencilType) {
            // Add namespace to stencilType
            if (stencilType.search(/^http/) === -1) {
                stencilType = this.getStencilSets().values()[0].namespace() + stencilType;
            }
        }
        else {
            // Get any root stencil type
            stencilType = this.getStencilSets().values()[0].findRootStencilName();
        }
        
		// get the stencil associated with the type
		var canvasStencil = ORYX.Core.StencilSet.stencil(stencilType);
			
		if (!canvasStencil) 
			ORYX.Log.fatal("Initialisation failed, because the stencil with the type %0 is not part of one of the loaded stencil sets.", stencilType);
		
		// create all dom
		// TODO fix border, so the visible canvas has a double border and some spacing to the scrollbars
		var div = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", null, ['div']);
		// set class for custom styling
		div.addClassName("ORYX_Editor");
						
		// create the canvas
		this._canvas = new ORYX.Core.Canvas({
			width					: ORYX.CONFIG.CANVAS_WIDTH,
			height					: ORYX.CONFIG.CANVAS_HEIGHT,
			'eventHandlerCallback'	: this.handleEvents.bind(this),
			id						: this.id,
			parentNode				: div
		}, canvasStencil);
        
        if (canvasConfig) {
          // Migrate canvasConfig to an RDF-like structure
          //FIXME this isn't nice at all because we don't want rdf any longer
          var properties = [];
          for(field in canvasConfig){
            properties.push({
              prefix: 'oryx',
              name: field,
              value: canvasConfig[field]
            });
          }
            
          this._canvas.deserialize(properties);
        }
				
	},

	/**
	 * Returns a per-editor singleton plugin facade.
	 * To be used in plugin initialization.
	 */
	_getPluginFacade: function() {

		// if there is no pluginfacade already created:
		if(!(this._pluginFacade))

			// create it.
			this._pluginFacade = {

				activatePluginByName:		this.activatePluginByName.bind(this),
				//deactivatePluginByName:		this.deactivatePluginByName.bind(this),
				getAvailablePlugins:	this.getAvailablePlugins.bind(this),
				offer:					this.offer.bind(this),
				getStencilSets:			this.getStencilSets.bind(this),
				getRules:				this.getRules.bind(this),
				loadStencilSet:			this.loadStencilSet.bind(this),
				createShape:			this.createShape.bind(this),
				deleteShape:			this.deleteShape.bind(this),
				getSelection:			this.getSelection.bind(this),
				setSelection:			this.setSelection.bind(this),
				updateSelection:		this.updateSelection.bind(this),
				getCanvas:				this.getCanvas.bind(this),
				
				importJSON:				this.importJSON.bind(this),
				importERDF:				this.importERDF.bind(this),
				getERDF:				this.getERDF.bind(this),
                getJSON:                this.getJSON.bind(this),
                getSerializedJSON:      this.getSerializedJSON.bind(this),
                
                checkParsingErrors:     this.checkParsingErrors.bind(this),
                showParsingErrors:      this.showParsingErrors.bind(this),
				
				executeCommands:		this.executeCommands.bind(this),
				
				registerOnEvent:		this.registerOnEvent.bind(this),
				unregisterOnEvent:		this.unregisterOnEvent.bind(this),
				raiseEvent:				this.handleEvents.bind(this),
				enableEvent:			this.enableEvent.bind(this),
				disableEvent:			this.disableEvent.bind(this),
				
				eventCoordinates:		this.eventCoordinates.bind(this),
				addToRegion:			this.addToRegion.bind(this),
				
				getModelMetaData:		this.getModelMetaData.bind(this)
			};

		// return it.
		return this._pluginFacade;
	},

	/**
	 * Implementes the command pattern
	 * (The real usage of the command pattern
	 * is implemented and shown in the Plugins/undo.js)
	 *
	 * @param <Oryx.Core.Command>[] Array of commands
	 */
	executeCommands: function(commands){
		
		// Check if the argument is an array and the elements are from command-class
		if ( 	commands instanceof Array 	&& 
				commands.length > 0 		&& 
				commands.all(function(command){ return command instanceof ORYX.Core.Command }) ) {
		
			// Raise event for executing commands
			this.handleEvents({
				type		: ORYX.CONFIG.EVENT_EXECUTE_COMMANDS,
				commands	: commands
			});

            var ret;
			// Execute every command
			commands.each(function(command){
				ret = command.execute();
			})
		    return ret;
		}
	},
	
    /**
     * Returns JSON of underlying canvas (calls ORYX.Canvas#toJSON()).
     * @return {Object} Returns JSON representation as JSON object.
     */
    getJSON: function(){
        var canvas = this.getCanvas().toJSON();
        canvas.ssextensions = this.getStencilSets().values()[0].extensions().keys();
        return canvas;
    },
    
    /**
     * Serializes a call to toJSON().
     * @return {String} Returns JSON representation as string.
     */
    getSerializedJSON: function() {
        return Ext.encode(this.getJSON());
    },
    
    checkParsingErrors : function() {
    	var processJSON = ORYX.EDITOR.getSerializedJSON();
		var ajaxObj = new XMLHttpRequest;
		var url = ORYX.PATH + "uuidRepository";
	    var params  = "action=checkErrors&pp=" + ORYX.PREPROCESSING + "&profile=" + ORYX.PROFILE + "&data=" + encodeURIComponent(processJSON);
	    ajaxObj.open("POST",url,false);
	    ajaxObj.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	    ajaxObj.send(params);
	    if (ajaxObj.status == 200) {
            if(ajaxObj.responseText == "true") {
                return "true";
            } else {
                var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
                var rawSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getRootNode().cloneNode(true));
                var processJSON = ORYX.EDITOR.getSerializedJSON();
                var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
                Ext.Ajax.request({
                    url: ORYX.PATH + "transformer",
                    method: 'POST',
                    success: function(request) {
                        // be quiet
                    },
                    failure: function(){
                        Ext.Msg.minWidth = 400;
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.main.failSave,
                            title       : ''

                        });
                    },
                    params: {
                        fsvg: formattedSvgDOM,
                        rsvg: rawSvgDOM,
                        uuid: ORYX.UUID,
                        profile: ORYX.PROFILE,
                        transformto: 'svg',
                        processid: processId
                    }
                });
                return "false";
            }
		} else {
			return "true";
		}
    },
    
    showParsingErrors : function() {
    	Ext.Msg.minWidth = 360;
    	Ext.MessageBox.alert( ORYX.I18N.main.unableUserAction );
    },
	
    /**
	 * @return {String} Returns eRDF representation.
	 * @deprecated Use ORYX.Editor#getJSON instead, if possible.
	 */
	getERDF:function(){

		// Get the serialized dom
        var serializedDOM = DataManager.serializeDOM( this._getPluginFacade() );
		
		// Add xml definition if there is no
		serializedDOM = '<?xml version="1.0" encoding="utf-8"?>' +
						'<html xmlns="http://www.w3.org/1999/xhtml" ' +
						'xmlns:b3mn="http://b3mn.org/2007/b3mn" ' +
						'xmlns:ext="http://b3mn.org/2007/ext" ' +
						'xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" ' +
						'xmlns:atom="http://b3mn.org/2007/atom+xhtml">' +
						'<head profile="http://purl.org/NET/erdf/profile">' +
						'<link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />' +
						'<link rel="schema.dcTerms" href="http://purl.org/dc/terms/ " />' +
						'<link rel="schema.b3mn" href="http://b3mn.org" />' +
						'<link rel="schema.oryx" href="http://oryx-editor.org/" />' +
						'<link rel="schema.raziel" href="http://raziel.org/" />' +
						'<base href="' +
						location.href.split("?")[0] +
						'" />' +
						'</head><body>' +
						serializedDOM +
						'</body></html>';
		
		return serializedDOM;				
	},
    
	/**
	* Imports shapes in JSON as expected by {@link ORYX.Editor#loadSerialized}
	* @param {Object|String} jsonObject The (serialized) json object to be imported
	* @param {boolean } [noSelectionAfterImport=false] Set to true if no shapes should be selected after import
	* @throws {SyntaxError} If the serialized json object contains syntax errors
	*/
	importJSON: function(jsonObject, noSelectionAfterImport) {
		try {
			jsonObject = this.renewResourceIds(jsonObject);
		} catch(error){
			throw error;
		}     
		//check, if the imported json model can be loaded in this editor
		// (stencil set has to fit)
		if (!jsonObject.stencilset) {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.JSONImport.invalidJSON,
                title       : ORYX.I18N.JSONImport.title

            });
			return null;
		}
		if(jsonObject.stencilset.namespace && jsonObject.stencilset.namespace !== this.getCanvas().getStencil().stencilSet().namespace()) {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : String.format(ORYX.I18N.JSONImport.wrongSS, jsonObject.stencilset.namespace, this.getCanvas().getStencil().stencilSet().namespace()),
                title       : ORYX.I18N.JSONImport.title

            });
			return null;
		} else {
			var commandClass = ORYX.Core.Command.extend({
			construct: function(jsonObject, loadSerializedCB, noSelectionAfterImport, facade){
				this.jsonObject = jsonObject;
				this.noSelection = noSelectionAfterImport;
				this.facade = facade;
				this.shapes;
				this.connections = [];
				this.parents = new Hash();
				this.selection = this.facade.getSelection();
				this.loadSerialized = loadSerializedCB;
			},			
			execute: function(){
				
				if (!this.shapes) {
					// Import the shapes out of the serialization		
					this.shapes	= this.loadSerialized( this.jsonObject );		
					
					//store all connections
					this.shapes.each(function(shape) {
						
						if (shape.getDockers) {
							var dockers = shape.getDockers();
							if (dockers) {
								if (dockers.length > 0) {
									this.connections.push([dockers.first(), dockers.first().getDockedShape(), dockers.first().referencePoint]);
								}
								if (dockers.length > 1) {
									this.connections.push([dockers.last(), dockers.last().getDockedShape(), dockers.last().referencePoint]);
								}
							}
						}
						
						//store parents
						this.parents[shape.id] = shape.parent;
					}.bind(this));
				} else {
					this.shapes.each(function(shape) {
						this.parents[shape.id].add(shape);
					}.bind(this));
					
					this.connections.each(function(con) {
						con[0].setDockedShape(con[1]);
						con[0].setReferencePoint(con[2]);
						//con[0].update();
					});
				}
				
				//this.parents.values().uniq().invoke("update");
				this.facade.getCanvas().update();
					
				if(!this.noSelection)
					this.facade.setSelection(this.shapes);
				else
					this.facade.updateSelection();
				},
				rollback: function(){
					var selection = this.facade.getSelection();
					
					this.shapes.each(function(shape) {
						selection = selection.without(shape);
						this.facade.deleteShape(shape);
					}.bind(this));
					
					/*this.parents.values().uniq().each(function(parent) {
						if(!this.shapes.member(parent))
							parent.update();
					}.bind(this));*/
					
					this.facade.getCanvas().update();
					
					this.facade.setSelection(selection);
				}
			})
			
			var command = new commandClass(jsonObject, 
											this.loadSerialized.bind(this),
											noSelectionAfterImport,
											this._getPluginFacade());
			
			this.executeCommands([command]);	
			
			return command.shapes.clone();
		}
	},
    
    /**
     * This method renew all resource Ids and according references.
     * Warning: The implementation performs a substitution on the serialized object for
     * easier implementation. This results in a low performance which is acceptable if this
     * is only used when importing models.
     * @param {Object|String} jsonObject
     * @throws {SyntaxError} If the serialized json object contains syntax errors.
     * @return {Object} The jsonObject with renewed ids.
     * @private
     */
    renewResourceIds: function(jsonObject){
        // For renewing resource ids, a serialized and object version is needed
        if(Ext.type(jsonObject) === "string"){
            try {
                var serJsonObject = jsonObject;
                jsonObject = Ext.decode(jsonObject);
            } catch(error){
                throw new SyntaxError(error.message);
            }
        } else {
            var serJsonObject = Ext.encode(jsonObject);
        }        
        
        // collect all resourceIds recursively
        var collectResourceIds = function(shapes){
            if(!shapes) return [];
            return shapes.map(function(shape){
                return collectResourceIds(shape.childShapes).concat(shape.resourceId);
            }).flatten();
        }
        var resourceIds = collectResourceIds(jsonObject.childShapes);
        
        // Replace each resource id by a new one
        resourceIds.each(function(oldResourceId) {
            var newResourceId = ORYX.Editor.provideId();
            serJsonObject = serJsonObject.gsub('"'+oldResourceId+'"', '"'+newResourceId+'"')
        });
        
        return Ext.decode(serJsonObject);
    },
	
	/**
	 * Import erdf structure to the editor
	 *
	 */
	importERDF: function( erdfDOM ){

		var serialized = this.parseToSerializeObjects( erdfDOM );	
		
		if(serialized)
			return this.importJSON(serialized, true);
	},

	/**
	 * Parses one model (eRDF) to the serialized form (JSON)
	 * 
	 * @param {Object} oneProcessData
	 * @return {Object} The JSON form of given eRDF model, or null if it couldn't be extracted 
	 */
	parseToSerializeObjects: function( oneProcessData ){
		
		// Firefox splits a long text node into chunks of 4096 characters.
		// To prevent truncation of long property values the normalize method must be called
		if(oneProcessData.normalize) oneProcessData.normalize();
		try {
			var xsl = "";
			var source=ORYX.PATH + "lib/extract-rdf.xsl";
			new Ajax.Request(source, {
				asynchronous: false,
				method: 'get',
				onSuccess: function(transport){
					xsl = transport.responseText
				}.bind(this),
				onFailure: (function(transport){
					ORYX.Log.error("XSL load failed" + transport);
				}).bind(this)
			});
			var domParser = new DOMParser();
			var xmlObject = oneProcessData;
			var xslObject = domParser.parseFromString(xsl, "text/xml");
        	var xsltProcessor = new XSLTProcessor();
        	var xslRef = document.implementation.createDocument("", "", null);
        	xsltProcessor.importStylesheet(xslObject);
        
            var new_rdf = xsltProcessor.transformToFragment(xmlObject, document);
            var serialized_rdf = (new XMLSerializer()).serializeToString(new_rdf);
			} catch(e){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.BPELSupport.error+': ' + e,
                    title       : ''

                });
			    var serialized_rdf = "";
		    }
            
            // Firefox 2 to 3 problem?!
            serialized_rdf = !serialized_rdf.startsWith("<?xml") ? "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf : serialized_rdf;

        var req = new Ajax.Request(ORYX.CONFIG.ROOT_PATH+"rdf2json", {
          method: 'POST',
          asynchronous: false,
          onSuccess: function(transport) {
              Ext.decode(transport.responseText);
          },
          parameters: {
              rdf: serialized_rdf
          }
        });
        
        return Ext.decode(req.transport.responseText);
	},

    /**
     * Loads serialized model to the oryx.
     * @example
     * editor.loadSerialized({
     *    resourceId: "mymodel1",
     *    childShapes: [
     *       {
     *          stencil:{ id:"Subprocess" },
     *          outgoing:[{resourceId: 'aShape'}],
     *          target: {resourceId: 'aShape'},
     *          bounds:{ lowerRight:{ y:510, x:633 }, upperLeft:{ y:146, x:210 } },
     *          resourceId: "myshape1",
     *          childShapes:[],
     *          properties:{},
     *       }
     *    ],
     *    properties:{
     *       language: "English"
     *    },
     *    stencilset:{
     *       url:"http://localhost:8080/oryx/stencilsets/bpmn1.1/bpmn1.1.json"
     *    },
     *    stencil:{
     *       id:"BPMNDiagram"
     *    }
     * });
     * @param {Object} model Description of the model to load.
     * @param {Array} [model.ssextensions] List of stenctil set extensions.
     * @param {String} model.stencilset.url
     * @param {String} model.stencil.id 
     * @param {Array} model.childShapes
     * @param {Array} [model.properties]
     * @param {String} model.resourceId
     * @return {ORYX.Core.Shape[]} List of created shapes
     * @methodOf ORYX.Editor.prototype
     */
    loadSerialized: function( model ){
        var canvas  = this.getCanvas();
      
        
        // Bugfix (cf. http://code.google.com/p/oryx-editor/issues/detail?id=240)
        // Deserialize the canvas' stencil set extensions properties first!
        this.loadSSExtensions(model.ssextensions);
        var shapes = this.getCanvas().addShapeObjects(model.childShapes, this.handleEvents.bind(this));
        
        if(model.properties) {
        	for(key in model.properties) {
        		var prop = model.properties[key];
        		if (!(typeof prop === "string")) {
        			prop = Ext.encode(prop);
        		}
            	this.getCanvas().setProperty("oryx-" + key, prop);
            }
        }
        
        
        this.getCanvas().updateSize();
        return shapes;
    },
    
    /**
     * Calls ORYX.Editor.prototype.ss_extension_namespace for each element
     * @param {Array} ss_extension_namespaces An array of stencil set extension namespaces.
     */
    loadSSExtensions: function(ss_extension_namespaces){
        if(!ss_extension_namespaces) return;

        ss_extension_namespaces.each(function(ss_extension_namespace){
            this.loadSSExtension(ss_extension_namespace);
        }.bind(this));
    },
	
	/**
	* Loads a stencil set extension.
	* The stencil set extensions definiton file must already
	* be loaded when the editor is initialized.
	*/
	loadSSExtension: function(extension) {				
    	if (!extension) {
    		return;
    	}

    	var stencilset = this.getStencilSets()[extension["extends"]];

    	if (!stencilset) {
    		return;
    	}
    	stencilset.addExtension(extension);
    	//stencilset.addExtension("/oryx/build/stencilsets/extensions/" + extension["definition"])
    	this.getRules().initializeRules(stencilset);

    	this._getPluginFacade().raiseEvent({
    		type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED
    	});
		
	},

	disableEvent: function(eventType){
		if(eventType == ORYX.CONFIG.EVENT_KEYDOWN) {
			this._keydownEnabled = false;
		}
		if(eventType == ORYX.CONFIG.EVENT_KEYUP) {
			this._keyupEnabled = false;
		}
		if(this.DOMEventListeners.keys().member(eventType)) {
			var value = this.DOMEventListeners.remove(eventType);
			this.DOMEventListeners['disable_' + eventType] = value;
		}
	},

	enableEvent: function(eventType){
		if(eventType == ORYX.CONFIG.EVENT_KEYDOWN) {
			this._keydownEnabled = true;
		}
		
		if(eventType == ORYX.CONFIG.EVENT_KEYUP) {
			this._keyupEnabled = true;
		}
		
		if(this.DOMEventListeners.keys().member("disable_" + eventType)) {
			var value = this.DOMEventListeners.remove("disable_" + eventType);
			this.DOMEventListeners[eventType] = value;
		}
	},

	/**
	 *  Methods for the PluginFacade
	 */
	registerOnEvent: function(eventType, callback) {
		if(!(this.DOMEventListeners.keys().member(eventType))) {
			this.DOMEventListeners[eventType] = [];
		}

		this.DOMEventListeners[eventType].push(callback);
	},

	unregisterOnEvent: function(eventType, callback) {
		if(this.DOMEventListeners.keys().member(eventType)) {
			this.DOMEventListeners[eventType] = this.DOMEventListeners[eventType].without(callback);
		} else {
			// Event is not supported
			// TODO: Error Handling
		}
	},

	getSelection: function() {
		return this.selection;
	},

	getStencilSets: function() { 
		return ORYX.Core.StencilSet.stencilSets(this.id); 
	},
	
	getRules: function() {
		return ORYX.Core.StencilSet.rules(this.id);
	},
	
	loadStencilSet: function(source) {
		try {
			ORYX.Core.StencilSet.loadStencilSet(source, this.id);
			this.handleEvents({type:ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
		} catch (e) {
			ORYX.Log.warn("Requesting stencil set file failed. (" + e + ")");
		}
	},

	offer: function(pluginData) {
		if(!this.pluginsData.member(pluginData)){
			this.pluginsData.push(pluginData);
		}
	},
	
	/**
	 * It creates an new event or adds the callback, if already existing,
	 * for the key combination that the plugin passes in keyCodes attribute
	 * of the offer method.
	 * 
	 * The new key down event fits the schema:
	 * 		key.event[.metactrl][.alt][.shift].'thekeyCode'
	 */
	registerPluginsOnKeyEvents: function() {
		this.pluginsData.each(function(pluginData) {
			
			if(pluginData.keyCodes) {
				
				pluginData.keyCodes.each(function(keyComb) {
					var eventName = "key.event";
					
					/* Include key action */
					eventName += '.' + keyComb.keyAction;
					
					if(keyComb.metaKeys) {
						/* Register on ctrl or apple meta key as meta key */
						if(keyComb.metaKeys.
							indexOf(ORYX.CONFIG.META_KEY_META_CTRL) > -1) {
								eventName += "." + ORYX.CONFIG.META_KEY_META_CTRL;
						}
							
						/* Register on alt key as meta key */
						if(keyComb.metaKeys.
							indexOf(ORYX.CONFIG.META_KEY_ALT) > -1) {
								eventName += '.' + ORYX.CONFIG.META_KEY_ALT;
						}
						
						/* Register on shift key as meta key */
						if(keyComb.metaKeys.
							indexOf(ORYX.CONFIG.META_KEY_SHIFT) > -1) {
								eventName += '.' + ORYX.CONFIG.META_KEY_SHIFT;
						}		
					}
					
					/* Register on the actual key */
					if(keyComb.keyCode)	{
						eventName += '.' + keyComb.keyCode;
					}
					
					/* Register the event */
					ORYX.Log.debug("Register Plugin on Key Event: %0", eventName);
					this.registerOnEvent(eventName,pluginData.functionality);
				
				}.bind(this));
			}
		}.bind(this));
	},

	setSelection: function(elements, subSelectionElement, force) {
		
		if (!elements) { elements = [] }
		
		elements = elements.compact().findAll(function(n){ return n instanceof ORYX.Core.Shape });
		
		if (elements.first() instanceof ORYX.Core.Canvas) {
			elements = [];
		}
		
		if (!force && elements.length === this.selection.length && this.selection.all(function(r){ return elements.include(r) })){
			return;
		}
		
		this.selection = elements;
		this._subSelection = subSelectionElement;
		
		this.handleEvents({type:ORYX.CONFIG.EVENT_SELECTION_CHANGED, elements:elements, subSelection: subSelectionElement})
	},
	
	updateSelection: function() {
		this.setSelection(this.selection, this._subSelection, true);
		/*var s = this.selection;
		this.setSelection();
		this.setSelection(s);*/
	},

	getCanvas: function() {
		return this._canvas;
	},
	

	/**
	*	option = {
	*		type: string,
	*		position: {x:int, y:int},
	*		connectingType:	uiObj-Class
	*		connectedShape: uiObj
	*		draggin: bool
	*		namespace: url
	*       parent: ORYX.Core.AbstractShape
	*		template: a template shape that the newly created inherits properties from.
	*		}
	*/
	createShape: function(option) {

		if(option && option.serialize && option.serialize instanceof Array){
		
			var type = option.serialize.find(function(obj){return (obj.prefix+"-"+obj.name) == "oryx-type"});
			var stencil = ORYX.Core.StencilSet.stencil(type.value);
		
			if(stencil.type() == 'node'){
				var newShapeObject = new ORYX.Core.Node({'eventHandlerCallback':this.handleEvents.bind(this)}, stencil);	
			} else {
				var newShapeObject = new ORYX.Core.Edge({'eventHandlerCallback':this.handleEvents.bind(this)}, stencil);	
			}
		
			this.getCanvas().add(newShapeObject);
			newShapeObject.deserialize(option.serialize);
		
			return newShapeObject;
		}

		// If there is no argument, throw an exception
		if(!option || !option.type || !option.namespace) { throw "To create a new shape you have to give an argument with type and namespace";}
		
		var canvas = this.getCanvas();
		var newShapeObject;

		// Get the shape type
		var shapetype = option.type;

		// Get the stencil set
		var sset = ORYX.Core.StencilSet.stencilSet(option.namespace);

		// Create an New Shape, dependents on an Edge or a Node

		if(sset.stencil(shapetype).type() == "node") {
			newShapeObject = new ORYX.Core.Node({'eventHandlerCallback':this.handleEvents.bind(this)}, sset.stencil(shapetype))
		} else {
			newShapeObject = new ORYX.Core.Edge({'eventHandlerCallback':this.handleEvents.bind(this)}, sset.stencil(shapetype))
		}
		
		// when there is a template, inherit the properties.
		if(option.template) {

			newShapeObject._jsonStencil.properties = option.template._jsonStencil.properties;
			newShapeObject.postProcessProperties();
		}

		// Add to the canvas
		if(option.parent && newShapeObject instanceof ORYX.Core.Node) {
			option.parent.add(newShapeObject);
		} else {
			canvas.add(newShapeObject);
		}
		
		
		// Set the position
		var point = option.position ? option.position : {x:100, y:200};
	
		
		var con;
		// If there is create a shape and in the argument there is given an ConnectingType and is instance of an edge
		if(option.connectingType && option.connectedShape && !(newShapeObject instanceof ORYX.Core.Edge)) {
			// there will be create a new Edge
			con = new ORYX.Core.Edge({'eventHandlerCallback':this.handleEvents.bind(this)}, sset.stencil(option.connectingType));
			// And both endings dockers will be referenced to the both shapes
			con.dockers.first().setDockedShape(option.connectedShape);
			var magnet = option.connectedShape.getDefaultMagnet();
			var cPoint = magnet ? magnet.bounds.center() : option.connectedShape.bounds.midPoint();
			con.dockers.first().setReferencePoint( cPoint );
			con.dockers.last().setDockedShape(newShapeObject);
			con.dockers.last().setReferencePoint(newShapeObject.getDefaultMagnet().bounds.center());
			// The Edge will be added to the canvas and be updated
			canvas.add(con);	
			//con.update();
		}
		
		// Move the new Shape to the position
		if(newShapeObject instanceof ORYX.Core.Edge && option.connectedShape) {

			newShapeObject.dockers.first().setDockedShape(option.connectedShape);
			
			if( option.connectedShape instanceof ORYX.Core.Node ){
				newShapeObject.dockers.first().setReferencePoint(option.connectedShape.getDefaultMagnet().bounds.center());					
				newShapeObject.dockers.last().bounds.centerMoveTo(point);			
			} else {
				newShapeObject.dockers.first().setReferencePoint(option.connectedShape.bounds.midPoint());								
			}

		} else {
			
			var b = newShapeObject.bounds
			if( newShapeObject instanceof ORYX.Core.Node && newShapeObject.dockers.length == 1){
				b = newShapeObject.dockers.first().bounds
			}
			
			b.centerMoveTo(point);
			
			var upL = b.upperLeft();
			b.moveBy( -Math.min(upL.x, 0) , -Math.min(upL.y, 0) )
			
			var lwR = b.lowerRight();
			b.moveBy( -Math.max(lwR.x-canvas.bounds.width(), 0) , -Math.max(lwR.y-canvas.bounds.height(), 0) )
			
		}
		
		// Update the shape
		if (newShapeObject instanceof ORYX.Core.Edge) {
			newShapeObject._update(false);
		}
		
		// And refresh the selection
		if(!(newShapeObject instanceof ORYX.Core.Edge)) {
			this.setSelection([newShapeObject]);
		}
		
		if(con && con.alignDockers) {
			con.alignDockers();
		} 
		if(newShapeObject.alignDockers) {
			newShapeObject.alignDockers();
		}

		this._getPluginFacade().raiseEvent({
			type 		: ORYX.CONFIG.EVENT_SHAPE_CREATED,
			value		: newShapeObject
		});

		return newShapeObject;
	},
	
	deleteShape: function(shape) {
		
		if (!shape || !shape.parent){ return }
		
		//remove shape from parent
		// this also removes it from DOM
		shape.parent.remove(shape);
		
		//delete references to outgoing edges
		shape.getOutgoingShapes().each(function(os) {
			var docker = os.getDockers().first();
			if(docker && docker.getDockedShape() == shape) {
				docker.setDockedShape(undefined);
			}
		}.bind(this));
		
		//delete references to incoming edges
		shape.getIncomingShapes().each(function(is) {
			var docker = is.getDockers().last();
			if(docker && docker.getDockedShape() == shape) {
				docker.setDockedShape(undefined);
			}
		}.bind(this));
		
		//delete references of the shape's dockers
		shape.getDockers().each(function(docker) {
			docker.setDockedShape(undefined);
		});
                
		this._getPluginFacade().raiseEvent({
			type 		: ORYX.CONFIG.EVENT_SHAPE_DELETED,
			value		: shape
		});
	},
	
	/**
	 * Returns an object with meta data about the model.
	 * Like name, description, ...
	 * 
	 * Empty object with the current backend.
	 * 
	 * @return {Object} Meta data about the model
	 */
	getModelMetaData: function() {
		return this.modelMetaData;
	},

	/* Event-Handler Methods */
	
	/**
	* Helper method to execute an event immediately. The event is not
	* scheduled in the _eventsQueue. Needed to handle Layout-Callbacks.
	*/
	_executeEventImmediately: function(eventObj) {
		if(this.DOMEventListeners.keys().member(eventObj.event.type)) {
			this.DOMEventListeners[eventObj.event.type].each((function(value) {
				value(eventObj.event, eventObj.arg);		
			}).bind(this));
		}
	},

	_executeEvents: function() {
		this._queueRunning = true;
		while(this._eventsQueue.length > 0) {
			var val = this._eventsQueue.shift();
			this._executeEventImmediately(val);
		}
		this._queueRunning = false;
	},
	
	/**
	 * Leitet die Events an die Editor-Spezifischen Event-Methoden weiter
	 * @param {Object} event Event , welches gefeuert wurde
	 * @param {Object} uiObj Target-UiObj
	 */
	handleEvents: function(event, uiObj) {
		
		ORYX.Log.trace("Dispatching event type %0 on %1", event.type, uiObj);

		switch(event.type) {
			case ORYX.CONFIG.EVENT_MOUSEDOWN:
				this._handleMouseDown(event, uiObj);
				break;
			case ORYX.CONFIG.EVENT_MOUSEMOVE:
				this._handleMouseMove(event, uiObj);
				break;
			case ORYX.CONFIG.EVENT_MOUSEUP:
				this._handleMouseUp(event, uiObj);
				break;
			case ORYX.CONFIG.EVENT_MOUSEOVER:
				this._handleMouseHover(event, uiObj);
				break;
			case ORYX.CONFIG.EVENT_MOUSEOUT:
				this._handleMouseOut(event, uiObj);
				break;
		}
		/* Force execution if necessary. Used while handle Layout-Callbacks. */
		if(event.forceExecution) {
			this._executeEventImmediately({event: event, arg: uiObj});
		} else {
			this._eventsQueue.push({event: event, arg: uiObj});
		}
		
		if(!this._queueRunning) {
			this._executeEvents();
		}
		
		// TODO: Make this return whether no listener returned false.
		// So that, when one considers bubbling undesireable, it won't happen.
		return false;
	},

	catchKeyUpEvents: function(event) {
		if(!this._keyupEnabled) {
			return;
		}
		/* assure we have the current event. */
        if (!event) 
            event = window.event;
        
		// Checks if the event comes from some input field
		if ( ["INPUT", "TEXTAREA"].include(event.target.tagName.toUpperCase()) ){
			return;
		}
		
		/* Create key up event type */
		var keyUpEvent = this.createKeyCombEvent(event,	ORYX.CONFIG.KEY_ACTION_UP);
		
		ORYX.Log.debug("Key Event to handle: %0", keyUpEvent);

		/* forward to dispatching. */
		this.handleEvents({type: keyUpEvent, event:event});
	},
	
	/**
	 * Catches all key down events and forward the appropriated event to 
	 * dispatching concerning to the pressed keys.
	 * 
	 * @param {Event} 
	 * 		The key down event to handle
	 */
	catchKeyDownEvents: function(event) {
		if(!this._keydownEnabled) {
			return;
		}
		/* Assure we have the current event. */
        if (!event) 
            event = window.event;
        
		/* Fixed in FF3 */
		// This is a mac-specific fix. The mozilla event object has no knowledge
		// of meta key modifier on osx, however, it is needed for certain
		// shortcuts. This fix adds the metaKey field to the event object, so
		// that all listeners that registered per Oryx plugin facade profit from
		// this. The original bug is filed in
		// https://bugzilla.mozilla.org/show_bug.cgi?id=418334
		//if (this.__currentKey == ORYX.CONFIG.KEY_CODE_META) {
		//	event.appleMetaKey = true;
		//}
		//this.__currentKey = pressedKey;
		
		// Checks if the event comes from some input field
		if ( ["INPUT", "TEXTAREA"].include(event.target.tagName.toUpperCase()) ){
			return;
		}
		
		/* Create key up event type */
		var keyDownEvent = this.createKeyCombEvent(event, ORYX.CONFIG.KEY_ACTION_DOWN);
		
		ORYX.Log.debug("Key Event to handle: %0", keyDownEvent);
		
		/* Forward to dispatching. */
		this.handleEvents({type: keyDownEvent,event: event});
	},
	
	/**
	 * Creates the event type name concerning to the pressed keys.
	 * 
	 * @param {Event} keyDownEvent
	 * 		The source keyDownEvent to build up the event name
	 */
	createKeyCombEvent: function(keyEvent, keyAction) {

		/* Get the currently pressed key code. */
        var pressedKey = keyEvent.which || keyEvent.keyCode;
		//this.__currentKey = pressedKey;
		
		/* Event name */
		var eventName = "key.event";
		
		/* Key action */
		if(keyAction) {
			eventName += "." + keyAction;
		}
		
		/* Ctrl or apple meta key is pressed */
		if(keyEvent.ctrlKey || keyEvent.metaKey) {
			eventName += "." + ORYX.CONFIG.META_KEY_META_CTRL;
		}
		
		/* Alt key is pressed */
		if(keyEvent.altKey) {
			eventName += "." + ORYX.CONFIG.META_KEY_ALT;
		}
		
		/* Alt key is pressed */
		if(keyEvent.shiftKey) {
			eventName += "." + ORYX.CONFIG.META_KEY_SHIFT;
		}
		
		/* Return the composed event name */
		return  eventName + "." + pressedKey;
	},

	_handleMouseDown: function(event, uiObj) {
		
		// get canvas.
		var canvas = this.getCanvas();
		// Try to get the focus
		canvas.focus()
	
		// find the shape that is responsible for this element's id.
		var element = event.currentTarget;
		var elementController = uiObj;

		// gather information on selection.
		var currentIsSelectable = (elementController !== null) &&
			(elementController !== undefined) && (elementController.isSelectable);
		var currentIsMovable = (elementController !== null) &&
			(elementController !== undefined) && (elementController.isMovable);

		var modifierKeyPressed = event.shiftKey || event.ctrlKey;
		var noObjectsSelected = this.selection.length === 0;
		var currentIsSelected = this.selection.member(elementController);


		// Rule #1: When there is nothing selected, select the clicked object.
		if(currentIsSelectable && noObjectsSelected) {
			this.setSelection([elementController]);

			ORYX.Log.trace("Rule #1 applied for mouse down on %0", element.id);

		// Rule #3: When at least one element is selected, and there is no
		// control key pressed, and the clicked object is not selected, select
		// the clicked object.
		} else if(currentIsSelectable && !noObjectsSelected &&
			!modifierKeyPressed && !currentIsSelected) {
			this.setSelection([elementController]);

			//var objectType = elementController.readAttributes();
			//alert(objectType[0] + ": " + objectType[1]);

			ORYX.Log.trace("Rule #3 applied for mouse down on %0", element.id);

		// Rule #4: When the control key is pressed, and the current object is
		// not selected, add it to the selection.
		} else if(currentIsSelectable && modifierKeyPressed
			&& !currentIsSelected) {
			var newSelection = this.selection.clone();
			newSelection.push(elementController)
			this.setSelection(newSelection)

			ORYX.Log.trace("Rule #4 applied for mouse down on %0", element.id);

		// Rule #6
		} else if(currentIsSelectable && currentIsSelected &&
			modifierKeyPressed) {
			var newSelection = this.selection.clone();
			this.setSelection(newSelection.without(elementController))

			ORYX.Log.trace("Rule #6 applied for mouse down on %0", elementController.id);

		// Rule #5: When there is at least one object selected and no control
		// key pressed, we're dragging.
		/*} else if(currentIsSelectable && !noObjectsSelected
			&& !modifierKeyPressed) {

			if(this.log.isTraceEnabled())
				this.log.trace("Rule #5 applied for mouse down on "+element.id);
*/
		// Rule #2: When clicked on something that is neither
		// selectable nor movable, clear the selection, and return.
		} else if (!currentIsSelectable && !currentIsMovable) {
			this.setSelection([]);
			
			ORYX.Log.trace("Rule #2 applied for mouse down on %0", element.id);

			return;

		// Rule #7: When the current object is not selectable but movable,
		// it is probably a control. Leave the selection unchanged but set
		// the movedObject to the current one and enable Drag. Dockers will
		// be processed in the dragDocker plugin.
		} else if(!currentIsSelectable && currentIsMovable && !(elementController instanceof ORYX.Core.Controls.Docker)) {
			// TODO: If there is any moveable elements, do this in a plugin
			//ORYX.Core.UIEnableDrag(event, elementController);

			ORYX.Log.trace("Rule #7 applied for mouse down on %0", element.id);
		
		// Rule #8: When the element is selectable and is currently selected and no 
		// modifier key is pressed
		} else if(currentIsSelectable && currentIsSelected &&
			!modifierKeyPressed) {
			this._subSelection = this._subSelection != elementController ? elementController : undefined;
						
			this.setSelection(this.selection, this._subSelection);
			
			ORYX.Log.trace("Rule #8 applied for mouse down on %0", element.id);
		}
		
		
		// prevent event from bubbling, return.
		//Event.stop(event);
		return;
	},

	_handleMouseMove: function(event, uiObj) {
		return;
	},

	_handleMouseUp: function(event, uiObj) {
		// get canvas.
		var canvas = this.getCanvas();

		// find the shape that is responsible for this elemement's id.
		var elementController = uiObj;

		//get event position
		var evPos = this.eventCoordinates(event);

		//Event.stop(event);
	},

	_handleMouseHover: function(event, uiObj) {
		return;
	},

	_handleMouseOut: function(event, uiObj) {
		return;
	},

	/**
	 * Calculates the event coordinates to SVG document coordinates.
	 * @param {Event} event
	 * @return {SVGPoint} The event coordinates in the SVG document
	 */
	eventCoordinates: function(event) {

		var canvas = this.getCanvas();

		var svgPoint = canvas.node.ownerSVGElement.createSVGPoint();
		svgPoint.x = event.clientX;
		svgPoint.y = event.clientY;
		var matrix = canvas.node.getScreenCTM();
		return svgPoint.matrixTransform(matrix.inverse());
	}
};
ORYX.Editor = Clazz.extend(ORYX.Editor);

/**
 * Creates a new ORYX.Editor instance by fetching a model from given url and passing it to the constructur
 * @param {String} modelUrl The JSON URL of a model.
 * @param {Object} config Editor config passed to the constructur, merged with the response of the request to modelUrl
 */
ORYX.Editor.createByUrl = function(modelUrl, config){
    if(!config) config = {};
    
    new Ajax.Request(modelUrl, {
      method: 'GET',
      onSuccess: function(transport) {
        var editorConfig = Ext.decode(transport.responseText);
        editorConfig = Ext.applyIf(editorConfig, config);
        new ORYX.Editor(editorConfig);
        
        if ("function" == typeof(config.onSuccess)) {
		  	config.onSuccess(transport);
	    }
      }.bind(this),
      onFailure: function(transport) {
    	if ("function" == typeof(config.onFailure)) {
    	  config.onFailure(transport);
    	}
      }.bind(this)
    });
}

// TODO Implement namespace awareness on attribute level.
/**
 * graft() function
 * Originally by Sean M. Burke from interglacial.com, altered for usage with
 * SVG and namespace (xmlns) support. Be sure you understand xmlns before
 * using this funtion, as it creates all grafted elements in the xmlns
 * provided by you and all element's attribures in default xmlns. If you
 * need to graft elements in a certain xmlns and wish to assign attributes
 * in both that and another xmlns, you will need to do stepwise grafting,
 * adding non-default attributes yourself or you'll have to enhance this
 * function. Latter, I would appreciate: martin�apfelfabrik.de
 * @param {Object} namespace The namespace in which
 * 					elements should be grafted.
 * @param {Object} parent The element that should contain the grafted
 * 					structure after the function returned.
 * @param {Object} t the crafting structure.
 * @param {Object} doc the document in which grafting is performed.
 */
ORYX.Editor.graft = function(namespace, parent, t, doc) {

    doc = (doc || (parent && parent.ownerDocument) || document);
    var e;
    if(t === undefined) {
        throw "Can't graft an undefined value";
    } else if(t.constructor == String) {
        e = doc.createTextNode( t );
    } else {
        for(var i = 0; i < t.length; i++) {
            if( i === 0 && t[i].constructor == String ) {
                var snared;
                snared = t[i].match( /^([a-z][a-z0-9]*)\.([^\s\.]+)$/i );
                if( snared ) {
                    e = doc.createElementNS(namespace, snared[1] );
                    e.setAttributeNS(null, 'class', snared[2] );
                    continue;
                }
                snared = t[i].match( /^([a-z][a-z0-9]*)$/i );
                if( snared ) {
                    e = doc.createElementNS(namespace, snared[1] );  // but no class
                    continue;
                }

                // Otherwise:
                e = doc.createElementNS(namespace, "span" );
                e.setAttribute(null, "class", "namelessFromLOL" );
            }

            if( t[i] === undefined ) {
                throw "Can't graft an undefined value in a list!";
            } else if( t[i].constructor == String || t[i].constructor == Array ) {
                this.graft(namespace, e, t[i], doc );
            } else if(  t[i].constructor == Number ) {
                this.graft(namespace, e, t[i].toString(), doc );
            } else if(  t[i].constructor == Object ) {
                // hash's properties => element's attributes
                for(var k in t[i]) { e.setAttributeNS(null, k, t[i][k] ); }
            } else {

			}
        }
    }
	if(parent) {
	    parent.appendChild( e );
	} else {

	}
    return e; // return the topmost created node
};

ORYX.Editor.provideId = function() {
	var res = [], hex = '0123456789ABCDEF';

	for (var i = 0; i < 36; i++) res[i] = Math.floor(Math.random()*0x10);

	res[14] = 4;
	res[19] = (res[19] & 0x3) | 0x8;

	for (var i = 0; i < 36; i++) res[i] = hex[res[i]];

	res[8] = res[13] = res[18] = res[23] = '-';

	return "_" + res.join('');
};

/**
 * When working with Ext, conditionally the window needs to be resized. To do
 * so, use this class method. Resize is deferred until 100ms, and all subsequent
 * resizeBugFix calls are ignored until the initially requested resize is
 * performed.
 */
ORYX.Editor.resizeFix = function() {
	if (!ORYX.Editor._resizeFixTimeout) {
		ORYX.Editor._resizeFixTimeout = window.setTimeout(function() {
			window.resizeBy(1,1);
			window.resizeBy(-1,-1);
			ORYX.Editor._resizefixTimeout = null;
		}, 100); 
	}
};

ORYX.Editor.Cookie = {
	
	callbacks:[],
		
	onChange: function( callback, interval ){
	
		this.callbacks.push(callback);
		this.start( interval )
	
	},
	
	start: function( interval ){
		
		if( this.pe ){
			return;
		}
		
		var currentString = document.cookie;
		
		this.pe = new PeriodicalExecuter( function(){
			
			if( currentString != document.cookie ){
				currentString = document.cookie;
				this.callbacks.each(function(callback){ callback(this.getParams()) }.bind(this));
			}
			
		}.bind(this), ( interval || 10000 ) / 1000);	
	},
	
	stop: function(){

		if( this.pe ){
			this.pe.stop();
			this.pe = null;
		}
	},
		
	getParams: function(){
		var res = {};
		
		var p = document.cookie;
		p.split("; ").each(function(param){ res[param.split("=")[0]] = param.split("=")[1];});
		
		return res;
	},	
	
	toString: function(){
		return document.cookie;
	}
};

/**
 * Workaround for SAFARI/Webkit, because
 * when trying to check SVGSVGElement of instanceof there is 
 * raising an error
 * 
 */
ORYX.Editor.SVGClassElementsAreAvailable = true;
ORYX.Editor.setMissingClasses = function() {
	
	try {
		SVGElement;
	} catch(e) {
		ORYX.Editor.SVGClassElementsAreAvailable = false;
		SVGSVGElement 		= document.createElementNS('http://www.w3.org/2000/svg', 'svg').toString();
		SVGGElement 		= document.createElementNS('http://www.w3.org/2000/svg', 'g').toString();
		SVGPathElement 		= document.createElementNS('http://www.w3.org/2000/svg', 'path').toString();
		SVGTextElement 		= document.createElementNS('http://www.w3.org/2000/svg', 'text').toString();
		//SVGMarkerElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'marker').toString();
		SVGRectElement 		= document.createElementNS('http://www.w3.org/2000/svg', 'rect').toString();
		SVGImageElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'image').toString();
		SVGCircleElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'circle').toString();
		SVGEllipseElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'ellipse').toString();
		SVGLineElement	 	= document.createElementNS('http://www.w3.org/2000/svg', 'line').toString();
		SVGPolylineElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'polyline').toString();
		SVGPolygonElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'polygon').toString();
		
	}
	
}

ORYX.Editor.checkIfSaved = function() {
    if(ORYX.READONLY == true || ORYX.VIEWLOCKED == true) {
        return true;
    } else {
        return ORYX.PROCESS_SAVED;
    }
};

ORYX.Editor.checkClassType = function( classInst, classType ) {
	
	if( ORYX.Editor.SVGClassElementsAreAvailable ){
		return classInst instanceof classType
	} else {
		return classInst == classType
	}
};

ORYX.Editor.makeExtModalWindowKeysave = function(facade) {
	Ext.override(Ext.Window,{
		beforeShow : function(){
			delete this.el.lastXY;
			delete this.el.lastLT;
			if(this.x === undefined || this.y === undefined){
				var xy = this.el.getAlignToXY(this.container, 'c-c');
				var pos = this.el.translatePoints(xy[0], xy[1]);
				this.x = this.x === undefined? pos.left : this.x;
				this.y = this.y === undefined? pos.top : this.y;
			}
			this.el.setLeftTop(this.x, this.y);
	
			if(this.expandOnShow){
				this.expand(false);
			}
	
			if(this.modal){
				facade.disableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
				Ext.getBody().addClass("x-body-masked");
				this.mask.setSize(Ext.lib.Dom.getViewWidth(true), Ext.lib.Dom.getViewHeight(true));
				this.mask.show();
			}
		},
		afterHide : function(){
	        this.proxy.hide();
	        if(this.monitorResize || this.modal || this.constrain || this.constrainHeader){
	            Ext.EventManager.removeResizeListener(this.onWindowResize, this);
	        }
	        if(this.modal){
	            this.mask.hide();
	            facade.enableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
	            Ext.getBody().removeClass("x-body-masked");
	        }
	        if(this.keyMap){
	            this.keyMap.disable();
	        }
	        this.fireEvent("hide", this);
	    },
	    beforeDestroy : function(){
	    	if(this.modal)
	    		facade.enableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
	        Ext.destroy(
	            this.resizer,
	            this.dd,
	            this.proxy,
	            this.mask
	        );
	        Ext.Window.superclass.beforeDestroy.call(this);
	    }
	});
}

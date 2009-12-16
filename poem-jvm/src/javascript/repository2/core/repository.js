/**
 * Copyright (c) 2008
 * Bjoern Wagner, Sven Wagner-Boysen
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

// init repository namespace

if(!Repository) var Repository = {};
if(!Repository.Core) Repository.Core = {};




Repository.Core.Repository = {
		
		initialized: false,
	
		construct : function() {

			arguments.callee.$.construct.apply(this, arguments); // call super class constructor
			this._currentUser = decodeURI( Repository.currentUser );
			this._publicUser = 'public';
			this._modelCache = new Repository.Core.DataCache();

			this._eventListeners = new Hash();
			
			this._initializeLoadEvent();
			/*var loadMask = this.showMask();
			
			// After 300 milsec after initialized, hide loading panel
			this.on('initialized', function(){ 
								
					window.setTimeout(function(){ this.hideMask(loadMask) }.bind(this), 200) 
				
				}.bind(this));*/
			
			// Event handler
			this._viewChangedHandler = new EventHandler();
			this._selectionChangedHandler = new EventHandler();
			this._filterChangedHandler = new EventHandler();
			
			// Plugin facade
			this._facade = null;
			
			// Model arrays
			this._filteredModels = new Array();
			this._selectedModels = new Array();
			this._displayedModels = new Array();
			
			this._filters = this.isPublicUser() ? new Hash() : new Hash({access:'owner,read,write'});

			this._currentSort = this.isPublicUser() ? "rating" : "lastChange";
			this._currentSortDirection = null;
			// UI
			this._controls = new Object();
			
			this._plugins = [];
			this._views = new Hash();
			this._currentView = "";				
			
			this._bootstrapUI();
					
			// Loads all required plugins which 
			// are specified in the plugins.xml
			this._loadPlugins();
			
			// Apply Filter and Refresh
			this.setSort( this._currentSort , Repository.Config.SORT_DESC);
			
			
 		},
		
		_initializeLoadEvent: function(){
			
			var bh = this._modelCache.getBusyHandler();
			
			var startCount = 0;
			var timer;
			var nothingTimer;
			
			
			var increase 	= function(){ clearTimer();if(nothingTimer){ window.clearTimeout(nothingTimer); nothingTimer = null} startCount++; };
			var decrease 	= function(){ startCount--; finished() }.bind(this);
			var clearTimer 	= function(){ if(timer){ startCount--; window.clearTimeout(timer);timer=null;} }.bind(this);
			var finished 	= function(){ 
				if( startCount > 0 || this.initialized ){ return }
				bh.start.unregisterCallback(increase)
				bh.end.unregisterCallback(decrease)
				this.initialized = true;
				this.raise('initialized') 
			}.bind(this)
			
			// For each start while loading time, raise the count
			bh.start.registerCallback( increase )
			// For each finishing while loading time, remove a count, and if complete finished, raise event
			bh.end.registerCallback( function(){ clearTimer(); timer = window.setTimeout(decrease, 250) }.bind(this) )	


			nothingTimer = window.setTimeout( finished.bind(this), 200 );
						
		},
		
	
		/**
		 * Implementation of an event listener
		 * @param {Object} event
		 * @param {Object} fn
		 */
		on: function(event, fn){
			if( !(fn instanceof Function &&  typeof event == "string") ){ return }
			
			if( !this._eventListeners.get( event )){
				this._eventListeners.set( event, [])
			}
			
			this._eventListeners.get( event ).push( fn )			
		},
		
		/**
		 * Raises the event and call every listener
		 * @param {Object} event
		 */
		raise: function(event){
			if( !(typeof event == "string" && this._eventListeners.get( event )) ){ return }
						
			this._eventListeners.get( event ).each(function(fn){
				fn.apply(fn)
			})			
		},
		
		
		showMask: function() {
			
			var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: Repository.I18N.Repository.loadingText
                    });
           	loadMask.show();
			loadMask.el._mask.applyStyles('background-color:#FFFFFF;opacity:1.0;')
			
			return loadMask;
		},
		
		hideMask: function( mask ){
			var duration = 0.5;

			// Fade out the background
			mask.el._mask.fadeOut({
									duration	: duration, 
									useDisplay	: true, 
									easing		: 'easeOut',
									callback	: function(){ mask.hide() }
								})		
								
			
			//Fade out the message
			mask.el._maskMsg.fadeOut({
									duration	: duration, 
									useDisplay	: true
								})										
		},
		
		getFacade : function() {
			if (!this._facade) {
				this._facade = {
						
						// Event handler
						registerOnViewChanged 		: this._viewChangedHandler.registerCallback.bind(this._viewChangedHandler),
						registerOnSelectionChanged 	: this._selectionChangedHandler.registerCallback.bind(this._selectionChangedHandler),
						registerOnFilterChanged 	: this._filterChangedHandler.registerCallback.bind(this._filterChangedHandler),
						
						modelCache 			: this._modelCache,
						
						isPublicUser 		: this.isPublicUser.bind(this),
						getCurrentUser		: this.getCurrentUser.bind(this),
						setSort				: this.setSort.bind(this),
						getSort				: this.getSort.bind(this),
						
						updateView			: this.updateView.bind(this),
						 
						applyFilter 		: this.applyFilter.bind(this),
						removeFilter 		: this.removeFilter.bind(this),
						getFilter			: this.getFilter.bind(this),
						getFilteredModels 	: this.getFilteredModels.bind(this),
						
						changeSelection 	: this.changeSelection.bind(this),
						getSelectedModels 	: this.getSelectedModels.bind(this),
						
						getDisplayedModels	: this.getDisplayedModels.bind(this),						
						setDisplayedModels	: this.setDisplayedModels.bind(this),
						
						getCurrentView 		: this.getCurrentView.bind(this),
						
						createNewModel		: this.createNewModel.bind(this),
						openModelInEditor 	: this.openModelInEditor.bind(this),
						
						registerPlugin: this.registerPlugin.bind(this)
				};
			}
			return this._facade;
		},
		
		updateView: function(){
			this._viewChangedHandler.invoke( this.getDisplayedModels() )	
		},
		
		isPublicUser: function() {
			return this._currentUser == this._publicUser;
		},

		getCurrentUser: function() {
			return this._currentUser;
		},
				
		setSort : function(sort, direction) {
			this._currentSort 			= sort;
			this._currentSortDirection	= direction ? direction : Repository.Config.SORT_DESC;
			this.applyFilter();
		},
		
		getSort : function() {
			return this._currentSort;
		},
		
		applyFilter : function(name, parameters) {
			if (name) this._filters.set(name, parameters);
			var params = this._filters.clone();
			if (this._currentSort) {
				params.set('sort', this._currentSort);
			}
			
			this._modelCache.doRequest("filter", function(transport) {
														this._filteredModels = eval(transport.responseText);
														if( this._currentSortDirection == Repository.Config.SORT_ASC)
															this._filteredModels.reverse()
															
														this._filterChangedHandler.invoke(this._filteredModels);
													}.bind(this), params, 'get', false )

		},
		
		removeFilter : function(name) {
			if (this._filters.get(name) != undefined) {
				this._filters.unset(name);
				this.updateFilteredIds();
			}
		},

		getFilter : function() {
			return this._filters;
		},
				
		getFilteredModels : function(){
			return this._filteredModels;
		},		
		
		getViews : function(){
			return this._views;
		},

		getCurrentView : function(){
			return this._currentView;
		},
				
		_switchView : function(view) {
			if(this._currentView instanceof Repository.Core.ViewPlugin)
				this._currentView.disable();

			// Get the current index of displayed models
			var currentDisplayedIndex 	= this._currentView.lastStartIndexOfDisplayedModel || 0;
			// Get the current selection index within the displayed models
			var currentSelectedIndex 	= this.getSelectedModels().length > 0 ? this.getDisplayedModels().indexOf(this.getSelectedModels()[0]) : 0;
			
			this._currentView = view;			
			this._currentView.enable();
			
			var newIndex = currentDisplayedIndex + currentSelectedIndex ;
			var size	 = view.numOfDisplayedModels;			
			
			this._currentView.showDisplayedModelsStartingFrom( newIndex-(newIndex%size) );
			//view.preRender(this.getDisplayedModels());

		},
		
		changeSelection : function(selectedIds) {
			selArray =  $A(selectedIds); // Make sure that it's an array
			if (selArray.length > 1) {
				selArray = selArray.reduce(); // remove double entries
			}
			this._selectedModels = selArray;
			this._selectionChangedHandler.invoke(selArray);
		},
		
		getSelectedModels : function() {
			return this._selectedModels;
		},
		
		getDisplayedModels : function() {
			return this._displayedModels;
		},
		
		setDisplayedModels : function(modelIds) {
			displArray =  $A(modelIds); // Make sure that it's an array
			if (displArray.length > 1) {
				displArray = displArray.reduce(); // remove double entries
			}
			this._displayedModels = displArray;
			
			this._viewChangedHandler.invoke( displArray )
		},
		
		createNewModel : function(stencilsetUrl, profileName) {
			
			var callback = function() {
				
				var url = './new';
				if(stencilsetUrl && profileName)
					url+= '?stencilset=' + stencilsetUrl +"&profile="+profileName;
				if(!stencilsetUrl && profileName)
					url+= '?profile=' + profileName;
				if(stencilsetUrl && !profileName)
					url+= '?stencilset=' + stencilsetUrl;
				
				var editor = window.open(url);
				
				window.setTimeout(function(){
					if (!editor || !editor.opener || editor.closed) {
						Ext.MessageBox.alert(Repository.I18N.Repository.windowTitle, Repository.I18N.Repository.windowTimeoutMessage).setIcon(Ext.MessageBox.QUESTION)
					}
				}, 5000);
			}
		
			if(this.isPublicUser()){
				
				Ext.MessageBox.buttonText.yes 	= Repository.I18N.Repository.yes;
				Ext.MessageBox.buttonText.no 	= Repository.I18N.Repository.no;
				
				Ext.Msg.show({
				   title	: Repository.I18N.Repository.noSaveTitle,
				   msg		: Repository.I18N.Repository.noSaveMessage,
				   buttons	: Ext.Msg.YESNO,
				   fn		: function(btn, text){
							   		if(btn == "yes"){
										callback();
									}
							   }
				});
						
			} else {
				callback();
			}
		},
		
		/**
		 * opens model with "model_id" in editor
		 * @param {Object} model_id
		 */
		openModelInEditor : function (model_id) {

			var uri	= model_id.slice(1) + "/self";
			
			// Open the model in a new window
			var editor = window.open( uri );
			window.setTimeout(
                        function() {
                                if(!editor || !editor.opener || editor.closed) {
                                        Ext.MessageBox.alert(Repository.I18N.Repository.windowTitle, Repository.I18N.Repository.windowTimeoutMessage).setIcon(Ext.MessageBox.QUESTION)
                                }
                        }, 5000);			
		},
		
		/**
		 * register plugin on panel for this plugin type and returns a panel, where the plugin can render itselfs. 
		 * @param {Object} plugin
		 */
		registerPlugin: function(plugin) {
			
			var pluginPanel
			
			if( plugin.viewRegion == "view" ) { 
				pluginPanel = this._registerPluginOnView(plugin, "view");
			} else {
				pluginPanel = this._registerPluginOnPanel(plugin.name, plugin.viewRegion);
			}
			
			if( plugin.toolbarButtons )
				plugin.toolbarButtons.each(function(button) {
					this._registerButtonOnToolbar(button);
				}.bind(this));
			
			return pluginPanel;
		},
		
		_registerPluginOnPanel : function(pluginName, panelName) {
			panel = this._controls[panelName + 'Panel'];
			if (!panel) return null; // Panel doesn't exist
			var pluginPanel = new Ext.Panel({
				pluginName 	: pluginName,
                title		: pluginName,
                collapsible	: true,
                collapsed	: false,
				border		: false,
                split 		: true,
				layout		: 'anchor'
				
			});
			pluginPanel = panel.add(pluginPanel);
			panel.doLayout(); // Force rendering to display image and generate body
			return pluginPanel;
		},
		
		_registerButtonOnToolbar : function(buttonConfig) {
					
			if (buttonConfig) {
				if ( buttonConfig instanceof Ext.Toolbar.Button || (buttonConfig.text != undefined) && (typeof(buttonConfig.handler) == "function")) {
					
					var menu = null;
										
					// Depending on if there is already a spacer, add the following element befor or behind this
					var spacer 		= this._controls.toolbar.items.find(function(item){ return item instanceof Ext.Toolbar.Spacer})
					var indexSpacer	= spacer ? this._controls.toolbar.items.indexOf(spacer) : null;
					var region		= buttonConfig.region && buttonConfig.region.toLowerCase() == "right" ? "right" : "left";
					
					if( !indexSpacer && region == "right" ){
						this._controls.toolbar.addFill();
						indexSpacer =  this._controls.toolbar.items.length-1;
					}
				

					// if the button should be added to a sub menu try to find it and create it if it isn't there
					if (buttonConfig.menu != undefined) {
					

						this._controls.toolbar.items.each(function(item) {
							if ((item.text == buttonConfig.menu) && (item.menu != undefined)) {
								menu = item.menu;
							}
						});
						
						// If no menu exists
						if (menu == null) {
							
							menu = new Ext.menu.Menu({items : []});
							
							// Insert the button to the particular place
							//this._controls.toolbar.insertButton(
									//indexSpacer ? (region == "right" ? indexSpacer+1 : indexSpacer-1) : this._controls.toolbar.items.length,
							// TODO: Add at the particular place --> Like it was the IE raises Errors
							this._controls.toolbar.add(
									{
										//id : buttonConfig.menu,
										iconCls: 'some_class_that_does_not_exist_but_fixes-rendering repository_ext_btn_align_center', // do not remove!
										text : buttonConfig.menu, 
										menu : menu,
										icon : buttonConfig.menuIcon,
										tooltip: {
											text: buttonConfig.tooltipText,
	                                        autoHide: true
	                                    }
									});
							
							menu.render();
						}
						
						if( !buttonConfig.iconCls ){
							buttonConfig.iconCls = 'repository_ext_icon_align_center';
						}

						menu.addMenuItem( buttonConfig );
						
					} else if( buttonConfig instanceof Ext.Toolbar.Button){

							// Insert the button to the particular place
							this._controls.toolbar.insertButton(
									indexSpacer ? (region == "right" ? indexSpacer+1 : indexSpacer-1) : this._controls.toolbar.items.length,
									buttonConfig
							);
							
					} else {
						
							// Insert the button to the particular place
							this._controls.toolbar.insertButton(
									indexSpacer ? (region == "right" ? indexSpacer+1 : indexSpacer-1) : this._controls.toolbar.items.length,
									
									new Ext.Toolbar.Button({
										text : buttonConfig.text,
										handler : buttonConfig.handler,
										iconCls: 'some_class_that_does_not_exist_but_fixes-rendering', // do not remove!
										icon : buttonConfig.icon
									})
							);
					}
					
				}
			}
			
		},
		
		_registerPluginOnView : function(plugin) {
			// TODO: check if all values are passed and check whether the plugin already exists
			
			this._registerButtonOnToolbar({
				text : plugin.name, 
				icon : plugin.icon, 
				tooltipText : Repository.I18N.Repository.viewMenuTooltip,
				menu : Repository.I18N.Repository.viewMenu, 
				menuIcon : '/backend/images/silk/application.png',
				handler : function() {this._switchView(plugin)}.bind(this)
			});
			
			return this._registerPluginOnPanel( null, 'view' );
		},
		
		_loadPlugins : function() {
			
			var source = Repository.Config.PATH + Repository.Config.PLUGIN_PATH	+ Repository.Config.PLUGIN_CONFIG
	
			this._modelCache.doRequest(		source, 
											function(result){
												
													// get plugins.xml content
													var resultXml = result.responseXML;
													
													// Get all source names
													//var files = $A(resultXml.getElementsByTagName("plugin")).map(function(p){ return p.getAttribute('source') }).compact()
													//this._intializePluginFiles( files );
													
													// Get all plugin names
													var names = $A(resultXml.getElementsByTagName("plugin")).map(function(p){ return p.getAttribute('name') }).compact()
													window.setTimeout(this._initializePlugins.bind(this, names), 200);
													 					
												}.bind(this),
											null, 'get', false);
			
		},
		
		_initializePlugins: function( names ){
			 
			names.each(function( name ){
				
				// Try to initialize a new plugin-class
				try {
					var className 	= eval( name )
					var plugin 		= new className( this.getFacade() )
					this._plugins.push( plugin )
					
				} catch(e){
					// TODO: Error Handling
				}
			}.bind(this));
			
			// Find a view plugin and switch to the first one
			var firstView = this._plugins.find(function(plugin){ return plugin instanceof Repository.Core.ViewPlugin })
			if( firstView ){
				this._switchView( firstView );
			}

			this._viewChangedHandler.invoke( this._displayedModels );
			this._selectionChangedHandler.invoke( this._selectedModels );
			
			// HACK
			// Added a unvisible last child so that every view gets displayed
			this._controls.viewPanel.add( {xtype:'label'} )
			this._controls.viewPanel.doLayout();
			
		}, 
		
		_intializePluginFiles: function( files ){

			var prefixURL = Repository.Config.PATH + Repository.Config.PLUGIN_PATH;

			files.each(function(file){

				// prepare a script tag and place it in html head.
				/*var head = document.getElementsByTagName('head')[0];
				var s = document.createElementNS(XMLNS.XHTML, "script");
				s.setAttributeNS(XMLNS.XHTML, 'type', 'text/javascript');
			   	s.src = prefixURL + file;
		
			   	head.appendChild(s);	*/			
							
			});
			
		},
		
		/* This functions defines and initialize the basic UI components
		 * 
		 */
		_bootstrapUI : function() {
			
			Ext.QuickTips.init();
			
			test_tpl = new Ext.XTemplate('<div id="oryx_repository_header"> <tpl if="isPublicUser"> PUBLIC </tpl><tpl if="!isPublicUser">  NOT PUBLIC </tpl></div>');
			
			// View panel
			this._controls.viewPanel = new Ext.Panel({ 
                region		: 'center',
				autoScroll	: true,
				border		: false
            });
			// Left panel
			this._controls.leftPanel = new Ext.Panel({ 
                region		: 'west',
                title		: Repository.I18N.Repository.leftPanelTitle,
                collapsible	: true,
                collapsed	: false,
                split 		: true,	
				width		: 200,
				autoScroll	: true			            		
            });
			// Right panel
			this._controls.rightPanel = new Ext.Panel({ 
                region		: 'east',
                title		: Repository.I18N.Repository.rightPanelTitle,
                collapsible	: true,
                collapsed	: false,
                split 		: true,
				width		: 210,
				autoScroll	: true		            		
            });			
			// Bottom panel
			this._controls.bottomPanel = new Ext.Panel({ 
                region		: 'south',
                //title: Repository.I18N.Repository.bottomPanelTitle,
                collapsible	: true,
				height		: 40,
				border		: false
				//titleCollapse: true,
                //collapsed: true,            		
            });
			// Toolbar
			this._controls.toolbar = new Ext.Toolbar({
				region 		: "south",
				items 		: []
			});
			
			
			// center panel, contains view panel and bottom panel
			this._controls.centerPanel = new Ext.Panel({
				region 		: 'center',
				layout		: 'border',
				items 		: [this._controls.viewPanel, this._controls.bottomPanel]
			});
			
			this._viewport = new Ext.Viewport({
					layout		: 'border',
					margins 	: '0 0 0 0',
					defaults	: {}, // default config for all child widgets
					items		: [ 
							        new Ext.Panel({ // Header panel for login and toolbar
										region : 'north',
										height : 60,
										margins : '0 0 0 0',
										border : true,
										items :[{ // Header with logo and login
									                region: 'north',
									                html: Repository.Templates.login.apply({currentUser : this._currentUser, isPublicUser : this._currentUser=='public'}),
									                height: 30
									           },
									           this._controls.toolbar
									    ] // Toolbar
									}), // Panel 
									this._controls.centerPanel,
						            this._controls.leftPanel,	
								    this._controls.rightPanel
							       ]});
						
			
			this._viewport.doLayout();	
										   
				
		}
};

Repository.Core.Repository = Clazz.extend(Repository.Core.Repository);


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
Repository.Core.graft = function(namespace, parent, t, doc) {

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

Ext.onReady(function(){new Repository.Core.Repository();});
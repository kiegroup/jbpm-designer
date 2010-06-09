if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.CPNToolsSupport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,
	
	stencilSetNamespace: 'http://b3mn.org/stencilset/coloredpetrinet#',

	construct: function(facade) 
	{
		
		this.facade = facade;
			
		this.facade.offer({
			'name':				"Export to CPN Tools",
			'functionality': 	this.exportCPN.bind(this),
			'group': 			ORYX.I18N.cpntoolsSupport.group,
			'dropDownGroupIcon':ORYX.PATH + "images/export2.png",
			'icon': 			ORYX.PATH + "images/cpn/cpn_export.png",
			'description': 		ORYX.I18N.cpntoolsSupport.exportDescription,
			'index': 			0,
			'minShape': 		0,
			'maxShape': 		0,
			'maxShape': 		0
		});
					
		this.facade.offer({
			'name':				"Import from CPN Tools",
			'functionality': 	this.importCPN.bind(this),
			'group': 			ORYX.I18N.cpntoolsSupport.group,
			'dropDownGroupIcon':ORYX.PATH + "images/import.png",
			'icon': 			ORYX.PATH + "images/cpn/cpn_import.png",
			'description': 		ORYX.I18N.cpntoolsSupport.importDescription,
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});

		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, this.resetTokenPosition.bind(this));
	},

	// Imports CPN - File
	importCPN: function()
	{
		this._showImportDialog();
	},		
	
	// Exports CPN - File
	exportCPN: function()
	{
				
		// Get the JSON representation which is needed for the mapping 
		var cpnJson = this.facade.getSerializedJSON();
		
		// Do the export
		this._doExportToCPNTools( cpnJson );
	},
	
	resetTokenPosition: function()
	{
		// Get selected places		
		var allplaces = this.facade.getSelection().findAll(function(selectedItem) {
			return (selectedItem.getStencil().id() === "http://b3mn.org/stencilset/coloredpetrinet#Place");
		});
		
		if (allplaces.length > 0)
		{
			allplaces.each(function(place) {
				
				var placeBounds = place.absoluteBounds();
				var placeCenter = placeBounds.center();
				
				// Calculate radius in order to check if a token is in the place
				var radiusY = placeCenter.y - placeBounds.upperLeft().y;
				var radiusX = placeCenter.x - placeBounds.upperLeft().x;
				var radius = Math.min(radiusY,radiusX);
				var c = radius / 2;
				
				// Get all tokens inside the place 
				var alltokens = place.getChildNodes(false).findAll(function(child) {
					return (child.getStencil().id() === "http://b3mn.org/stencilset/coloredpetrinet#Token");
				});
				
				if (alltokens.length > 0)
				{
					var i = 0;
					var x = 0;
					var y = 0;
					
					alltokens.each(function(token) {
						var tokenBounds = token.absoluteBounds();
						var tokenCenter = tokenBounds.center();
						
						// Calculate the distance between token and center of the place
						var diffX = placeCenter.x - tokenCenter.x;
						var diffY = placeCenter.y - tokenCenter.y;
						var distanceToPlaceCenter= diffX*diffX + diffY*diffY; // take care it's squared
						
						// Check if the token is in the place
						if (radius*radius <= distanceToPlaceCenter)
						{	// if the token is out of the place, calculate the position for the token
							// the token are positioned in circle which is in the place
							y = Math.round(Math.sin((Math.PI / 6) * i) * c);
							x = Math.round(Math.cos((Math.PI / 6) * i) * c);
							// take care centerMoveTo is referred to the position in the selected place (not absolute) 
							token.bounds.centerMoveTo(place.bounds.width() / 2  + x, place.bounds.height() / 2 + y);
							token.update();
							i = i + 1;
						}
					});					
				}
			});

		}			
		this.facade.getCanvas().update();
	},

	// ---------------------------------------- Ajax Request --------------------------------
	
	_sendRequest: function( url, method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(
		url, 
		{
           method			: method,
           asynchronous		: false,
           parameters		: params,
		   onSuccess		: function(transport) 
		   {
				suc = true;
		
				if(successcallback)
				{
					successcallback( transport.responseText )	
				}
		
		   }.bind(this),
		   onFailure		: function(transport) 
		   {
				if(failedcallback)
				{							
					failedcallback();							
				} 
				else 
				{
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.cpntoolsSupport.serverConnectionFailed);
					ORYX.log.warn("Communication failed: " + transport.responseText);	
				}					
		   }.bind(this)		
		});
		
		return suc;		
	},	

// -------------------------------------------- Export Functions ----------------------------
	
	_doExportToCPNTools: function( cpnJSON )
	{		
		this._sendRequest
		(
			ORYX.CONFIG.CPNTOOLSEXPORTER,
			'POST',
			{ 
				data: cpnJSON
			},
			function( result )
			{ 			
				if (result.startsWith("error:"))
				{
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, result);
				}
				else
				{
					this.openDownloadWindow( window.document.title + ".cpn", result );
				}				
			}.bind(this),
			function()
			{ 
				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.cpntoolsSupport.serverConnectionFailed);
		 	}.bind(this)
		)
	}, 

// -------------------------------------------- Import Functions ------------------------
	
	 // Opens a upload dialog
	_showImportDialog: function( successCallback )
	{
		// Define the form panel
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: 
	        [
	         {
	            text : 		ORYX.I18N.cpntoolsSupport.importTask, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	         },
	         {
	            fieldLabel: ORYX.I18N.cpntoolsSupport.File,
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	         }, 
	         {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
	            anchor: '100% -63'  
	         }
	        ]
	    });

		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.cpntoolsSupport.cpn, 
			height: 	350, 
			width:		500,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[form],
			buttons:[
				{
					text: ORYX.I18N.cpntoolsSupport.importLable,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.jPDLSupport.impProgress});
						loadMask.show();
						
						window.setTimeout(function()
						{					
							// Get the text which is in the text field
							var cpnToImport =  form.items.items[2].getValue();							
							this._getAllPages(cpnToImport, loadMask);

						}.bind(this), 100);

						dialog.hide();
						
					}.bind(this)
					
				},{
					text: ORYX.I18N.cpntoolsSupport.close,
					handler:function()
					{						
						dialog.hide();					
					}.bind(this)
				}
			]
		});
		
		// Destroy the panel when hiding
		dialog.on('hide', function()
		{
			dialog.destroy(true);
			delete dialog;
		});

		// Show the panel
		dialog.show();
		
				
		// Adds the change event handler to 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt)
			{
				var text = evt.target.files[0].getAsText('UTF-8');
				form.items.items[2].setValue( text );
			}, true)

	},
	
	_getAllPages: function(cpnXML, loadMask)
	{
		var parser = new DOMParser();
		var xmlDoc = parser.parseFromString(cpnXML,"text/xml");
		var allPages = xmlDoc.getElementsByTagName("page");
		
		// If there are no pages then it is propably that cpnXML is not a cpn - File
		if (allPages.length == 0)
		{
			loadMask.hide();
			this._showErrorMessageBox(ORYX.I18N.cpntoolsSupport.title, ORYX.I18N.cpntoolsSupport.wrongCPNFile);
			
			return;
		}
		
		
		if (allPages.length == 1)
		{
			pageAttr = allPages[0].children[0];
			pageName = pageAttr.attributes[0].nodeValue;
			
			this._sendRequest(
					ORYX.CONFIG.CPNTOOLSIMPORTER,
					'POST',
					{ 
						'pagesToImport': pageName,
						'data' : cpnXML 
					},
					function( arg )
					{
						if (arg.startsWith("error:"))
						{
							this._showErrorMessageBox(ORYX.I18N.Oryx.title, arg);
							loadMask.hide();
						}
						else
						{
							this.facade.importJSON(arg); 
							loadMask.hide();							
						}
					}.bind(this),
					function()
					{
						loadMask.hide();
						this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.cpntoolsSupport.serverConnectionFailed);
					}.bind(this)
				);
			
			return;
		}
		
		var i, pageName, data = [];
		for (i = 0; i < allPages.length; i++)
		{
			pageAttr = allPages[i].children[0];
			pageName = pageAttr.attributes[0].nodeValue;
			data.push([pageName]);
		}
		
		loadMask.hide();
		this.showPageDialog(data, cpnXML);		
	},
	
	_showErrorMessageBox: function(title, msg)
	{
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	},
	
	
	showPageDialog: function(data, cpnXML)
	{
		var reader = new Ext.data.ArrayReader(
				{}, 
				[ {name: 'name'} ]);
		
		var sm = new Ext.grid.CheckboxSelectionModel(
			{
				singleSelect: true
			});
		
	    var grid2 = new Ext.grid.GridPanel({
	    		store: new Ext.data.Store({
		            reader: reader,
		            data: data
		        	}),
		        cm: new Ext.grid.ColumnModel([
		            {
		            	id:'name',
		            	width:200,
		            	sortable: true, 
		            	dataIndex: 'name'
		            },
					sm]),
			sm: sm,
	        frame:true,
			hideHeaders:true,
	        iconCls:'icon-grid',
			listeners : {
				render: function() {
					var recs=[];
					this.grid.getStore().each(function(rec)
					{
						if(rec.data.engaged){
							recs.push(rec);
						}
					}.bind(this));
					this.suspendEvents();
					this.selectRecords(recs);
					this.resumeEvents();
				}.bind(sm)
			}
	    });
	    
	 // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: 'CPNTools Page',
                style: 'margin:10px;display:block'
            }, grid2],
            frame: true
        })
        
        // Create a new Window
        var window = new Ext.Window({
            id: 'oryx_new_page_selection',
            autoWidth: true,
            title: ORYX.I18N.cpntoolsSupport.title,
            floating: true,
            shim: true,
            modal: true,
            resizable: true,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: ORYX.I18N.cpntoolsSupport.importLable,
                handler: function()
                {
            		var chosenRecs = "";

            		// Actually it doesn't matter because it's one
            		sm.getSelections().each(function(rec)
            		{
						chosenRecs = rec.data.name;						
					}.bind(this));
            		
            		var strLen = chosenRecs.length; 
            		
            		if (chosenRecs.length == 0)
            		{
            			alert(ORYX.I18N.cpntoolsSupport.noPageSelection);
            			return;
            		}
            		
            		var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.cpntoolsSupport.importProgress});
					loadMask.show();
					
            		window.hide();
            		
        			pageName = chosenRecs;
        			this._sendRequest(
        					ORYX.CONFIG.CPNTOOLSIMPORTER,
        					'POST',
        					{ 
        						'pagesToImport': pageName,
        						'data' : cpnXML 
        					},
        					function( arg )
        					{
								if (arg.startsWith("error:"))
								{
									this._showErrorMessageBox(ORYX.I18N.Oryx.title, arg);
									loadMask.hide();
								}
								else
								{
									this.facade.importJSON(arg); 
									loadMask.hide();							
								}
							}.bind(this),
        					function()
        					{
								loadMask.hide();
								this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.cpntoolsSupport.serverConnectionFailed);
							}.bind(this)
        				);
                }.bind(this)
            }, 
            {
                text: ORYX.I18N.cpntoolsSupport.close,
                handler: function(){
                    window.hide();
                }.bind(this)
            }]
        })
        
        // Show the window
        window.show();
	}		
	
});
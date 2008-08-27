/**
 * Copyright (c) 2008
 * Willi Tscheschner
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * Supports EPCs by offering a syntax check and export and import ability..
 * 
 * 
 */
ORYX.Plugins.ERDFSupport = Clazz.extend({

	facade: undefined,
	
	ERDFServletURL: '/erdfsupport',

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		
		this.facade = facade;
			
			
		this.facade.offer({
			'name':				"Export from ERDF",
			'functionality': 	this.exportERDF.bind(this),
			'group': 			"erdf",
			'icon': 			ORYX.PATH + "images/epc_export.png",
			'description': 		"Export from ERDF",
			'index': 			0,
			'minShape': 		0,
			'maxShape': 		0
		});
					
		this.facade.offer({
			'name':				"Import from ERDF",
			'functionality': 	this.importERDF.bind(this),
			'group': 			"erdf",
			'icon': 			ORYX.PATH + "images/epc_import.png",
			'description': 		"Import from ERDF",
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});

	},

	
	/**
	 * Imports an AML description
	 * 
	 */
	importERDF: function(){
		this._showImportDialog();
	},		

	
	/**
	 * Imports an AML description
	 * 
	 */
	exportERDF: function(){
		
		var s 	= DataManager.serializeDOM( this.facade );
		s		= s.gsub('><div', ">\n<div");		
		s		= '<?xml version="1.0" encoding="utf-8"?>\n<div class="processdata">\n' + s + "\n</div>"; 

		this.openXMLWindow( s );
	},	

	
	
	/**
	 * 
	 * 
	 * @param {Object} url
	 * @param {Object} params
	 * @param {Object} successcallback
	 */
	sendRequest: function( url, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(url, {
            method			: 'POST',
            asynchronous	: false,
            parameters		: params,
			onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport.result )	
				}
				
			}.bind(this),
			
			onFailure		: function(transport) {

				if(failedcallback){
					
					failedcallback();
					
				} else {
					Ext.Msg.alert("Oryx", "Request in Import of ERDF failed.");
					ORYX.log.warn("Import ERDF failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		
		return suc;
							
	},


	loadERDF: function( erdfString, success, failed ){
		
		var s 	= erdfString;
		s 		= s.startsWith('<?xml') ? s : '<?xml version="1.0" encoding="utf-8"?>'+s+'';	
						
		var parser	= new DOMParser();			
		var doc 	=  parser.parseFromString( s ,"text/xml");
							
		if( doc.firstChild.tagName == "parsererror"){
		


			Ext.MessageBox.show({
					title: 		'Error',
 					msg: 		"Error: An error while importing occurs! <br/>Please check error message: <br/><br/>" + doc.firstChild.textContent.escapeHTML(),
					buttons: 	Ext.MessageBox.OK,
					icon: 		Ext.MessageBox.ERROR
				});
																
			if(failed)
				failed()
		} else {
			
			this.facade.importERDF( doc );
			
			if(success)
				success();
		
		}
	},

	
	/**
	 * Opens a new window that shows the given XML content.
	 * 
	 * @param {Object} content The XML content to be shown.
	 */
	openXMLWindow: function(content) {
		var win = window.open(
		   'data:application/xml,' + encodeURIComponent(
		     content
		   ),
		   '_blank', "resizable=yes,width=600,height=600,toolbar=0,scrollbars=yes"
		);
	},

	/**
	 * Opens a upload dialog.
	 * 
	 */
	_showImportDialog: function( successCallback ){

		var formFile = new Ext.form.FormPanel({
			title			: 'File',	
			bodyStyle		: 'padding:5px;',
			defaultType 	: 'textfield',
			style			: 'width:100%',
		  	fileUpload 		: true,
		  	enctype 		: 'multipart/form-data',
		  	items 			: [
							  	{
							    	text : 		'Select an ERDF (.xml) file to import it!', 
									style : 	'font-size:12px;margin-bottom:10px;display:block;',
									xtype : 	'label'
							  	},{
							    	fieldLabel : 	'File',
							    	inputType : 	'file',
									labelStyle :	'width:50px;',
									itemCls :		'ext_specific_window_overflow'
							  	}]
		});

		var formInput = new Ext.form.FormPanel({
			title			: 'Input',	
			bodyStyle		: 'padding:5px;',
			defaultType 	: 'textarea',
			anchor			: '100%',
		  	items 			: [
							  	{
							    	text 	: 'Type in ERDF!', 
									style 	: 'font-size:12px;margin-bottom:10px;display:block;',
									xtype 	: 'label'
							  	},{
									hideLabel	: true,
									anchor		: '100% -25'
							  	}]
		});
	    var tabs = new Ext.TabPanel({
			anchor		: '100%',
	        activeTab	: 0,
	        frame		: true,
	        items		: [
					      	formInput, 
							formFile            
					       ]
	    });


		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			title: 		'Import ERDF', 
			height: 	350, 
			width:		500,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: [tabs],
			buttons:[
				{
					text:'Import',
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Importing..."});
						loadMask.show();
						
						window.setTimeout(function(){
							
							if(tabs.activeTab == formInput){
								
								var erdfString = formInput.form.items.items[0].getValue();
								this.loadERDF(erdfString, function(){loadMask.hide();dialog.hide()}.bind(this), function(){loadMask.hide();}.bind(this))
														
							} else if(tabs.activeTab == formFile){
								
								var erdfString = formFile.form.items.items[0].getEl().dom.files[0].getAsBinary();
								this.loadERDF(erdfString, function(){loadMask.hide();dialog.hide()}.bind(this), function(){loadMask.hide();}.bind(this))
	
							}							
							
						}.bind(this), 100);
			
					}.bind(this)
				},{
					text:'Close',
					handler:function(){
						
						dialog.hide();
					
					}.bind(this)
				}
			]
		});
		
		// Destroy the panel when hiding
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		
		// Show the panel
		dialog.show();
	},
	
    _showPanel: function(values, successCallback){
    
							
        // Extract the data
        var data = [];
        values.each(function(value){
            data.push([ value.title, value.data ])
        });
        
        // Create a new Selection Model
        var sm = new Ext.grid.CheckboxSelectionModel({ 
			header			:'',
			//singleSelect	:true
		});
        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
			//ddGroup          	: 'gridPanel',
			//enableDragDrop   	: true,
			//cls				: 'ext_specialize_gridPanel_aml',
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['title']
            }),
            cm: new Ext.grid.ColumnModel([sm, {
                header: "Title",
                width: 260,
                sortable: true,
                dataIndex: 'title'
            }, ]),
            sm: sm,
            frame: true,
            width: 300,
			height:300,
            iconCls: 'icon-grid',
			//draggable: true
        });
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype	: 'label',
                html	: 'Select the diagram(s) you want to import! <br/> If one model is selected, it will be imported in the current editor, if more than one is selected, those models will directly be stored in the repository.',
                style	: 'margin:5px;display:block'
            }, grid],
			height:'auto',
            frame: true
        })
        
        // Create a new Window
        var extWindow = new Ext.Window({
            width: 327,
			height:'auto',
            title: 'Oryx',
            floating: true,
            shim: true,
            modal: true,
            resizable: false,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: "Import",
                handler: function(){

					var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Importing..."});
					loadMask.show();
							
                    var selectionModel = grid.getSelectionModel();
                    var result = selectionModel.selections.items.collect(function(item){
                        return {name: item.json[0], data: item.json[1]};
                    })
                    extWindow.close();
                
					window.setTimeout( function(){
						
						successCallback(result);
						loadMask.hide();
						
					}.bind(this), 100);		

										
                }.bind(this)
            }, {
                text: "Cancel",
                handler: function(){
                    extWindow.close();
                }.bind(this)
            }]			
        })
        
        // Show the window
        extWindow.show();
        
    },
	
    _showResultPanel: function(values){
    
							
        // Extract the data
        var data = [];
        values.each(function(value){
            data.push([ value.name, '<a href="' + value.url + '" target="_blank">' + value.url + '</a>' ])
        });
        

        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['name', 'url' ]
            }),
            cm: new Ext.grid.ColumnModel([{
                header: "Name",
                width: 260,
                sortable: true,
                dataIndex: 'name'
            }, {
                header: "URL",
                width: 300,
                sortable: true,
                dataIndex: 'url'
            }]),
            frame: true,
            width: 500,
			height:300,
            iconCls: 'icon-grid'
        });
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: 'All imported diagrams!',
                style: 'margin:5px;display:block'
            }, grid],
			height:'auto',
            frame: true
        })
        
        // Create a new Window
        var extWindow2 = new Ext.Window({
            width		: 'auto',
            title		: 'Oryx',
            floating	: true,
            shim		: true,
            modal		: true,
            resizable	: false,
            autoHeight	: true,
            items: [panel],
            buttons: [{
                text: "Ok",
                handler: function(){

					extWindow2.close()	
									
                }.bind(this)
            }]			
        })
        
        // Show the window
        extWindow2.show();
        
    },	
	/**
	 * 
	 * @param {Object} message
	 */
	throwErrorMessage: function(message){
		Ext.Msg.alert( 'Oryx', message )
	},		
	
});
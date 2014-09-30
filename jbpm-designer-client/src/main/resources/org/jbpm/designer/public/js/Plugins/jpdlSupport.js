/**
 * Copyright (c) 2009
 * Stefan Krumnow, Ole Eckermann
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

ORYX.Plugins.JPDLSupport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,
	
	
	stencilSetExtensionNamespace: 'http://oryx-editor.org/stencilsets/extensions/jbpm#',
	stencilSetExtensionDefinition: 'jbpm/jbpm.json',
	
	stencilSetNamespace: 'http://b3mn.org/stencilset/bpmn1.1#',
	stencilSetUrlSuffix: '/bpmn1.1/bpmn1.1.json',

	/**
	 * constructor method
	 * 
	 */
	construct: function(facade) {
		
		this.facade = facade;
			
		this.facade.offer({
			'name':				ORYX.I18N.jPDLSupport.exp,
			'functionality': 	this.exportJPDL.bind(this),
			'group': 			ORYX.I18N.jPDLSupport.group,
			'icon': 			ORYX.BASE_FILE_PATH + "images/jpdl_export_icon.png",
			'description': 		ORYX.I18N.jPDLSupport.expDesc,
            dropDownGroupIcon: ORYX.BASE_FILE_PATH + "images/export2.png",
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0,
			'maxShape': 		0,
			'isEnabled': 		this._isJpdlStencilSetExtensionLoaded.bind(this)
		});
					
		this.facade.offer({
			'name':				ORYX.I18N.jPDLSupport.imp,
			'functionality': 	this.importJPDL.bind(this),
			'group': 			ORYX.I18N.jPDLSupport.group,
            dropDownGroupIcon: ORYX.BASE_FILE_PATH + "images/import.png",
			'icon': 			ORYX.BASE_FILE_PATH + "images/jpdl_import_icon.png",
			'description': 		ORYX.I18N.jPDLSupport.impDesc,
			'index': 			2,
			'minShape': 		0,
			'maxShape': 		0
		});

	},
	
	/**
	 * Checks if the jPDL stencil set is loaded right now.
	 */
	_isJpdlStencilSetExtensionLoaded: function() {
		return this.isStencilSetExtensionLoaded(this.stencilSetExtensionNamespace);
	},

	/**
	 * Imports jPDL
	 * 
	 */
	importJPDL: function(){
		this._showImportDialog();
	},		

	/**
	 * Exports jPDL
	 * 
	 */
	exportJPDL: function(){
		// raise loading enable event
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE
        });
            
		// asynchronously ...
        window.setTimeout((function(){
			
			// ... save synchronously
    		this._doExport();			
			// raise loading disable event.
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_LOADING_DISABLE
            });
			
        }).bind(this), 10);

		return true;

		
	},
	
	/**
	 * Sends request to a given URL.
	 * 
	 */
	_sendRequest: function( url, method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(url, {
           method			: method,
           asynchronous	: false,
           parameters		: params,
		   onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport.responseText )	
				}
				
			}.bind(this),
			
			onFailure		: function(transport) {

				if(failedcallback){
					
					failedcallback();
					
				} else {
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedReq);
					ORYX.log.warn("Import jPDL failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		return suc;		
	},
	
	/**
	 * Loads JSON into the editor
	 * 
	 */
	_loadJSON: function( jsonString ){
		
		if (jsonString) {
			var jsonObj = jsonString.evalJSON();
			if( jsonObj && this._hasStencilset(jsonObj) ) {
				if ( this._isJpdlStencilSetExtensionLoaded() ) {
					this.facade.importJSON(jsonString);
				} else {
					Ext.MessageBox.confirm(
						ORYX.I18N.jPDLSupport.loadSseQuestionTitle,
						ORYX.I18N.jPDLSupport.loadSseQuestionBody,
						function(btn){
							if (btn == 'yes') {
								
								if (this.loadStencilSetExtension(this.stencilSetNamespace, this.stencilSetExtensionDefinition)){
									this.facade.importJSON(jsonString);
								} else {
									this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
								}
								
							} else {
								this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJsonAbort);
							}
						},
						this
					);
				}
				
			} else {
				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
			}
		} else {
			this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
		}
	},
	
	loadStencilSetExtension: function(stencilSetNamespace, stencilSetExtensionDefinition) {
		var stencilset = this.facade.getStencilSets()[stencilSetNamespace];
		if (stencilset) {
			stencilset.addExtension(ORYX.CONFIG.SS_EXTENSIONS_FOLDER + stencilSetExtensionDefinition);
			this.facade.getRules().initializeRules(stencilset);
			this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
			return true;
		} 
		return false;
	},
	
	/**
	 * Checks if a json object references the jPDL stencil set extension.
	 * 
	 */
	_hasStencilset: function( jsonObj ){
		return jsonObj.properties.ssextension == this.stencilSetExtensionNamespace && jsonObj.stencilset.url.endsWith(this.stencilSetUrlSuffix);
	},
	

	/**
	 * Opens an export window / tab.
	 * 
	 */
	_doExport: function(){
		var serialized_json = this.facade.getSerializedJSON();

		this._sendRequest(
			ORYX.CONFIG.JPDLEXPORTURL,
			'POST',
			{ data:serialized_json },
			function( result ) { 
				var parser = new DOMParser();
				var parsedResult = parser.parseFromString(result, "text/xml");
				if (parsedResult.firstChild.localName == "error") {
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.expFailedXml + parsedResult.firstChild.firstChild.data);
				} else {
					this.openXMLWindow(result);
				}
			}.bind(this),
			function() { 
				this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.expFailedReq);
		 	}.bind(this)
		)
	}, 
	
	/**
	 * Opens a upload dialog.
	 * 
	 */
	_showImportDialog: function( successCallback ){
	
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		ORYX.I18N.jPDLSupport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: ORYX.I18N.jPDLSupport.file,
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	        }, {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
	            anchor: '100% -63'  
	        }]
	    });

		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.jPDLSupport.impJPDL, 
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
					text:ORYX.I18N.jPDLSupport.impBtn,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.jPDLSupport.impProgress});
						loadMask.show();
						
						window.setTimeout(function(){
					
							var jpdlString =  form.items.items[2].getValue();
							
							this._sendRequest(
									ORYX.CONFIG.JPDLIMPORTURL,
									'POST',
									{ 'data' : jpdlString },
									function( arg ) { this._loadJSON( arg );  loadMask.hide();  dialog.hide(); }.bind(this),
									function() { loadMask.hide();  dialog.hide(); }.bind(this)
								);

						}.bind(this), 100);
			
					}.bind(this)
				},{
					text:ORYX.I18N.jPDLSupport.close,
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
		
				
		// Adds the change event handler to 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt){
				var text = evt.target.files[0].getAsText('UTF-8');
				form.items.items[2].setValue( text );
			}, true)
			
			form.items.items[3].getEl().dom.addEventListener('change',function(evt){
				var text = evt.target.files[0].getAsText('UTF-8');
				form.items.items[4].setValue( text );
			}, true)

	},
	
	_showErrorMessageBox: function(title, msg){
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	}
	
});
/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
   @namespace Oryx name space for plugins
   @name ORYX.Plugins
*/
if(!ORYX.Plugins)
	ORYX.Plugins = new Object();
	
/**
 * This plugin provides methodes to serialize and deserialize a BPMN 2.0 diagram.
 * 
 * @class ORYX.Plugins.Bpmn2_0Serialization
 * @extends ORYX.Plugins.AbstractPlugin
 * @param {Object} facade
 * 		The facade of the Editor
 */
ORYX.Plugins.BPMN2_0Serialization = {
	bpmnSerializationHandlerUrl: ORYX.CONFIG.ROOT_PATH + "bpmn2_0serialization",
	bpmnDeserializationHandlerUrl : ORYX.CONFIG.ROOT_PATH + "bpmn2_0deserialization",
	bpmn2XpdlSerializationHandlerUrl : ORYX.CONFIG.ROOT_PATH + "bpmn2xpdlserialization",
	
	construct: function(facade) {
	
		this.facade = facade;
	
		/* BPMN 2.0 XML */
		
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization.show,
			'functionality'		: this.showBpmnXml.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/export2.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.showDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
		
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization.download,
			'functionality'		: this.downloadBpmnXml.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/export2.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.downloadDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
		
		/* XPDL 2.2 */
		
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization.xpdlShow,
			'functionality'		: this.showXpdl.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/export2.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.xpdlShowDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
		
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization.xpdlDownload,
			'functionality'		: this.downloadXpdl.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/export2.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.xpdlDownloadDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
		
		/* Import BPMN 2.0 XML */
		
		this.facade.offer({
			'name'				: ORYX.I18N.Bpmn2_0Serialization['import'],
			'functionality'		: this.showImportDialog.bind(this),
			'group'				: 'Export',
            dropDownGroupIcon : ORYX.PATH + "images/import.png",
			'icon' 				: ORYX.PATH + "images/source.png",
			'description'		: ORYX.I18N.Bpmn2_0Serialization.importDesc,
			'index'				: 0,
			'minShape'			: 0,
			'maxShape'			: 0
		});
	},
	
	showBpmnXml: function() {	
		//var options = JSON.stringify({action : 'transform'});
		
		this.generateBpmnXml( function( response ) {
			var json = response.evalJSON();
			this.showSchemaValidationEvent(json.validationEvents);
			this.openXMLWindow(json.xml);
		}.bind(this),
		this.bpmnSerializationHandlerUrl);
	},
	
	downloadBpmnXml: function() {	
		//var options = JSON.stringify({action : 'transform'});
		this.generateBpmnXml(
			function ( response ) {
				var json = response.evalJSON();
				this.showSchemaValidationEvent(json.validationEvents);
				this.openDownloadWindow("model.bpmn", json.xml);
			}.bind(this),
			this.bpmnSerializationHandlerUrl);
	},
	
	/**
	 * Show the results of the schema validation in a message box, if it is
	 * enabled in the configuration.
	 */
	showSchemaValidationEvent : function(validationEvents) {
		if(validationEvents && ORYX.CONFIG.BPMN20_SCHEMA_VALIDATION_ON) {
			this._showErrorMessageBox("Validation", validationEvents);
		}
	},
	
	/**
	 * Calls the serialization to XPDL 2.2 and shows the result in a XML-Window.
	 */
	showXpdl: function() {
		this.generateBpmnXml( function( xml ) {
			this.openXMLWindow(xml);
		}.bind(this),
		this.bpmn2XpdlSerializationHandlerUrl);
	},
	
	/**
	 * Calls the serialization to XPDL 2.2 and offers the result as a file
	 * download.
	 */
	downloadXpdl: function() {
		this.generateBpmnXml(
			function ( xml ) {
				this.openDownloadWindow("model.xpdl", xml);
			}.bind(this),
			this.bpmn2XpdlSerializationHandlerUrl);
	},
	
	generateBpmnXml: function( bpmnHandleFunction, handlerUrl ) {
		var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Serialization of BPMN 2.0 model"});
		loadMask.show();
		
		var jsonString = this.facade.getSerializedJSON();
		this._sendRequest(
				handlerUrl,
				'POST',
				{ 'data' : jsonString },
				function( response ) { 
					bpmnHandleFunction( response );  
					loadMask.hide();
				}.bind(this),
				function(transport) { 
					loadMask.hide();
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.Bpmn2_0Serialization.serialFailed);
					ORYX.log.warn("Serialization of BPMN 2.0 model failed: " + transport.responseText);
				}.bind(this)
			);
	},
	
	/**
     * Opens a upload dialog.
     *
     */
    showImportDialog: function(successCallback){
    
        var form = new Ext.form.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 50,
            defaultType: 'textfield',
            items: [{
                text: ORYX.I18N.Bpmn2_0Serialization.selectFile,
                style: 'font-size:12px;margin-bottom:10px;display:block;',
                anchor: '100%',
                xtype: 'label'
            }, {
                fieldLabel: ORYX.I18N.Bpmn2_0Serialization.file,
                name: 'subject',
                inputType: 'file',
                style: 'margin-bottom:10px;display:block;',
                itemCls: 'ext_specific_window_overflow'
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
            layout: 'fit',
            plain: true,
            bodyStyle: 'padding:5px;',
            title: ORYX.I18N.Bpmn2_0Serialization.name,
            height: 350,
            width: 500,
            modal: true,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: true,
            items: [form],
            buttons: [{
                text: ORYX.I18N.Bpmn2_0Serialization.btnImp,
                handler: function(){
                
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: ORYX.I18N.Bpmn2_0Serialization.progress
                    });
                    loadMask.show();
                    
                    window.setTimeout(function(){
                        var bpmnXml = form.items.items[2].getValue();
						try {
							this._sendRequest(
								this.bpmnDeserializationHandlerUrl,
								'POST',
								{ 'data' : bpmnXml },
								function( json ) { 
		                            this.facade.importJSON(json, true);
		                            dialog.close();
								}.bind(this),
								function(transport) { 
									loadMask.hide();
									this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.Bpmn2_0Serialization.serialFailed);
									ORYX.log.warn("Serialization of BPMN 2.0 model failed: " + transport.responseText);
								}.bind(this)
							); 
						} 
                        catch (error) {
                            Ext.Msg.alert(ORYX.I18N.Bpmn2_0Serialization.error, error.message);
                        }
                        finally {
                            loadMask.hide();
                        }
                    }.bind(this), 100);
                    
                }.bind(this)
            }, {
                text: ORYX.I18N.Bpmn2_0Serialization.btnClose,
                handler: function(){
                    dialog.close();
                }.bind(this)
            }]
        });
        
        // Show the panel
        dialog.show();
        
        // Adds the change event handler to 
        form.items.items[1].getEl().dom.addEventListener('change', function(evt){
            var text = evt.target.files[0].getAsText('UTF-8');
            form.items.items[2].setValue(text);
        }, true)
        
    },
	
	_sendRequest: function( url, method, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(url, {
           method			: method,
           asynchronous	: false,
           parameters		: params,
		   onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport.responseText );
				}
				
			}.bind(this),
			
			onFailure : function(transport) {

				if(failedcallback){
					failedcallback(transport);
					
				} else {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Bpmn2Bpel.transfFailed);
					ORYX.log.warn("Serialization of BPMN 2.0 model failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		return suc;		
	},
	
	_showErrorMessageBox: function(title, msg){
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	}
};

ORYX.Plugins.BPMN2_0Serialization = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.BPMN2_0Serialization);
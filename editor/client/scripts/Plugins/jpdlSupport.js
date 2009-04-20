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

ORYX.Plugins.JPDLSupport = Clazz.extend({

	facade: undefined,
	
	jPDLImporterUrl: '/backend/poem/new_jpdl',
	jPDLExporterUrlSuffix: '/jpdl',

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
			'icon': 			ORYX.PATH + "images/jpdl_export_icon.png",
			'description': 		ORYX.I18N.jPDLSupport.expDesc,
			'index': 			0,
			'minShape': 		0,
			'maxShape': 		0
		});
					
		this.facade.offer({
			'name':				ORYX.I18N.jPDLSupport.imp,
			'functionality': 	this.importJPDL.bind(this),
			'group': 			ORYX.I18N.jPDLSupport.group,
			'icon': 			ORYX.PATH + "images/jpdl_import_icon.png",
			'description': 		ORYX.I18N.jPDLSupport.impDesc,
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});

	},

	
	importJPDL: function(){
		this._showImportDialog();
	},		

	exportJPDL: function(){
		var loc = location.href;
		var jpdlLoc ;
		if ( loc.length > 4 && loc.substring(loc.length - 5) == "/self" ) {
			jpdlLoc = loc.substring(0, loc.length - 5) + this.jPDLExporterUrlSuffix;
		} else {
			alert("TODO: Integrate existing export with new models.. ");
			return ;
		}
		this._openExportWindow( jpdlLoc );
		
	},
	
	_sendRequest: function( url, params, successcallback, failedcallback ){

		var suc = false;

		new Ajax.Request(url, {
           method			: 'POST',
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
					Ext.Msg.alert("Oryx", ORYX.I18N.jPDLSupport.impFailed);
					ORYX.log.warn("Import jPDL failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		return suc;		
	},
	
	_loadJSON: function( jsonString ){
		
		// test data:
		// jsonString = '{"resourceId":"oryx-canvas123","childShapes":[{"bounds":{"lowerRight":{"y":290,"x":360},"upperLeft":{"y":210,"x":260}},"resourceId":"oryx_66E0FE4E-8EBE-40A2-B478-171FBE2F990D","childShapes":[],"properties":{"bgcolor":"#ffffcc"},"stencil":{"id":"java"},"outgoing":[{"resourceId":"oryx_A5230798-DAD0-4683-9410-1F3E97FDD6C7"}]},{"bounds":{"lowerRight":{"y":253,"x":452},"upperLeft":{"y":225,"x":424}},"resourceId":"oryx_3C16ACDE-E39B-4DE6-AF01-11B75BA6FB44","childShapes":[],"properties":{"ends":"processinstance","bgcolor":"#ffffff"},"stencil":{"id":"EndEvent"},"outgoing":[]},{"bounds":{"lowerRight":{"y":254.4146918778988,"x":259.23511382536867},"upperLeft":{"y":251.9524956221012,"x":195.2180111746313}},"resourceId":"oryx_D3AED622-9567-45EA-BBB6-497333DCA5B3","target":{"resourceId":"oryx_66E0FE4E-8EBE-40A2-B478-171FBE2F990D"},"childShapes":[],"properties":{"conditiontype":"None"},"stencil":{"id":"SequenceFlow"},"outgoing":[{"resourceId":"oryx_66E0FE4E-8EBE-40A2-B478-171FBE2F990D"}]},{"bounds":{"lowerRight":{"y":270,"x":195},"upperLeft":{"y":240,"x":165}},"resourceId":"oryx_1B2C84D9-4CF0-461E-AE71-2A7A452E1CED","childShapes":[],"properties":{"bgcolor":"#ffffff"},"stencil":{"id":"StartEvent"},"outgoing":[{"resourceId":"oryx_D3AED622-9567-45EA-BBB6-497333DCA5B3"}]},{"bounds":{"lowerRight":{"y":244.42252340871437,"x":424.00592830824564},"upperLeft":{"y":237.53060159128563,"x":360.99407169175436}},"resourceId":"oryx_A5230798-DAD0-4683-9410-1F3E97FDD6C7","target":{"resourceId":"oryx_3C16ACDE-E39B-4DE6-AF01-11B75BA6FB44"},"childShapes":[],"properties":{"conditiontype":"None"},"stencil":{"id":"SequenceFlow"},"outgoing":[{"resourceId":"oryx_3C16ACDE-E39B-4DE6-AF01-11B75BA6FB44"}]}],"properties":{"ssextension":"http://oryx-editor.org/stencilsets/extensions/jbpm#"},"stencilset":{"url":"http://localhost:8080/oryx/stencilsets/bpmn1.1/bpmn1.1.json"},"stencil":{"id":"BPMNDiagram"}}';
		alert(jsonString);
		
		if (jsonString) {
			var jsonObj = jsonString.evalJSON();
			if( jsonObj && this._hasStencilset(jsonObj) ) {
				this.facade.importJSON(jsonString);
			}
		}
		// TODO: Error Messages..
	},
	
	_hasStencilset: function( jsonObj ){
		// TODO: Implement real check.
		alert(jsonObj);
		return true;
	},
	

	_openExportWindow: function( url ){
		// TODO: Errors in jpdl document should be handled in Oryx UI
		window.open( url );
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
									this.jPDLImporterUrl,
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

	}
	
});
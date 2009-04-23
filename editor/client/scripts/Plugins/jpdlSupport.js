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
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});
					
		this.facade.offer({
			'name':				ORYX.I18N.jPDLSupport.imp,
			'functionality': 	this.importJPDL.bind(this),
			'group': 			ORYX.I18N.jPDLSupport.group,
			'icon': 			ORYX.PATH + "images/jpdl_import_icon.png",
			'description': 		ORYX.I18N.jPDLSupport.impDesc,
			'index': 			2,
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
		//jsonString = '{"resourceId":"oryx-canvas123","properties":{"id":"","name":"","version":"","author":"","language":"English","expressionlanguage":"","querylanguage":"","creationdate":"","modificationdate":"","pools":"","documentation":""},"stencil":{"id":"BPMNDiagram"},"childShapes":[{"resourceId":"oryx_B9194F2B-68EE-4AC1-B9A3-AB197A3E5483","properties":{"id":"","categories":"","documentation":"","name":"","assignments":"","pool":"","lanes":"","eventtype":"Start","trigger":"None","bgcolor":"#ffffff"},"stencil":{"id":"StartEvent"},"childShapes":[],"outgoing":[{"resourceId":"oryx_937581EC-B462-4A79-A112-3B5E922ABE5F"}],"bounds":{"lowerRight":{"x":197,"y":148},"upperLeft":{"x":167,"y":118}},"dockers":[]},{"resourceId":"oryx_7EF6A824-175A-49C5-8AF2-63C705C94E3C","properties":{"id":"","categories":"","documentation":"","name":"Test","assignments":"","pool":"","lanes":"","activitytype":"Task","status":"None","performers":"","properties":"","inputsets":"","inputs":"","outputsets":"","outputs":"","iorules":"","startquantity":1,"completionquantity":1,"looptype":"None","loopcondition":"","loopcounter":1,"loopmaximum":1,"testtime":"After","mi_condition":"","mi_ordering":"Sequential","mi_flowcondition":"All","complexmi_condition":"","iscompensation":"","tasktype":"None","inmessage":"","outmessage":"","implementation":"Webservice","messageref":"","instantiate":"","script":"","taskref":"","bgcolor":"#ffffcc"},"stencil":{"id":"Task"},"childShapes":[],"outgoing":[{"resourceId":"oryx_202FB34E-3C30-4BBC-B98A-4C3DDF0A595A"}],"bounds":{"lowerRight":{"x":374,"y":166},"upperLeft":{"x":274,"y":86}},"dockers":[]},{"resourceId":"oryx_937581EC-B462-4A79-A112-3B5E922ABE5F","properties":{"id":"","categories":"","documentation":"","name":"","sourceref":"","targetref":"","conditiontype":"None","conditionexpression":"","quantity":1,"showdiamondmarker":"false"},"stencil":{"id":"SequenceFlow"},"childShapes":[],"outgoing":[{"resourceId":"oryx_7EF6A824-175A-49C5-8AF2-63C705C94E3C"}],"bounds":{"lowerRight":{"x":273.0793378267027,"y":132.23982651258393},"upperLeft":{"x":197.42066217329727,"y":128.51017348741607}},"dockers":[{"x":15,"y":15},{"x":50,"y":40}]},{"resourceId":"oryx_0BBA69E4-B6AC-4CCC-9AC5-0D6583FBD7FC","properties":{"id":"","categories":"","documentation":"","name":"","assignments":"","pool":"","lanes":"","eventtype":"End","result":"None","bgcolor":"#ffffff"},"stencil":{"id":"EndEvent"},"childShapes":[],"outgoing":[],"bounds":{"lowerRight":{"x":490,"y":156},"upperLeft":{"x":462,"y":128}},"dockers":[]},{"resourceId":"oryx_202FB34E-3C30-4BBC-B98A-4C3DDF0A595A","properties":{"id":"","categories":"","documentation":"","name":"","sourceref":"","targetref":"","conditiontype":"None","conditionexpression":"","quantity":1,"showdiamondmarker":"false"},"stencil":{"id":"SequenceFlow"},"childShapes":[],"outgoing":[{"resourceId":"oryx_0BBA69E4-B6AC-4CCC-9AC5-0D6583FBD7FC"}],"bounds":{"lowerRight":{"x":466.59837667841475,"y":131.50516466427695},"upperLeft":{"x":374.3501305552209,"y":94}},"dockers":[{"x":50,"y":40},{"x":433,"y":94},{"x":14,"y":14}]}],"bounds":{"lowerRight":{"x":1485,"y":1050},"upperLeft":{"x":0,"y":0}},"stencilset":{"url":"/oryx/stencilsets/bpmn1.1/bpmn1.1.json"},"ssextensions":[]}';
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
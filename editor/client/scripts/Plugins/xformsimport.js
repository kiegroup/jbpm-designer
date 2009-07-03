/**
 * Copyright (c) 2008
 * Jan-Felix Schwarz
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

ORYX.Plugins.XFormsImport = Clazz.extend({

	facade: undefined,

	construct: function(facade) {
		this.facade = facade;

		this.facade.offer({
			'name':ORYX.I18N.XFormsSerialization.importXForms,
			'functionality': this.importXForms.bind(this),
			'group': ORYX.I18N.XFormsSerialization.group,
			'icon': ORYX.PATH + "images/xforms_import.png",
			'description': ORYX.I18N.XFormsSerialization.importXFormsDesc,
			'index': 3,
			'minShape': 0,
			'maxShape': 0});
	},

	
	/**
	 * Imports a XForms+XHTML document
	 * 
	 */
	importXForms: function(){
		this._showImportDialog();
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
			onSuccess		: function(request) {
				
				suc = true;
				
				if(successcallback){
					successcallback( request )	
				}
				
			}.bind(this),
			
			onFailure		: function(request) {

				if(failedcallback){
					
					failedcallback();
					
				} else {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.XFormsSerialization.impFailed);
					ORYX.log.warn("Import XForms failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		
		return suc;
							
	},

	/**
	 * This function loads the given ERDF adjusting the shape bounds to stencil's default width and height
	 * 
	 * @param {String} erdfString
	 * @param {function} success
	 * @param {function} failed
	 */
	loadERDF: function( erdfString, success, failed ){
		
		var s 	= erdfString;
		s 		= s.startsWith('<?xml') ? s : '<?xml version="1.0" encoding="utf-8"?>'+s+'';	
						
		var parser	= new DOMParser();			
		var doc 	=  parser.parseFromString( s ,"text/xml");
		
		doc.normalize();
							
		if( doc.firstChild.tagName == "parsererror" ){

			Ext.MessageBox.show({
					title: 		ORYX.I18N.ERDFSupport.error,
 					msg: 		ORYX.I18N.ERDFSupport.impFailed2 + doc.firstChild.textContent.escapeHTML(),
					buttons: 	Ext.MessageBox.OK,
					icon: 		Ext.MessageBox.ERROR
				});
																
			if(failed)
				failed();
				
		} else {
			
			var serializedJSON = this.parseToSerializeObjects(doc);
			
			serializedJSON.each(function(element) {
				console.log(element);
				if(element.shape && element.shape instanceof ORYX.Core.Canvas)
					return;
	
				var type = element.serialize.find(function(ser) { return ser.name=="type" });
				var bounds = element.serialize.find(function(ser) { return ser.name=="bounds" });
				
				if(bounds) {
					// create dummy shape of same stencil (has default width and height)
					var stencil = ORYX.Core.StencilSet.stencil(type.value);
					var dummyShape = new ORYX.Core.Node({}, stencil);
					
					// adjust values in JSON accordingly
					boundsArr = bounds.value.split(",");
					bounds.value = boundsArr[0] + "," + boundsArr[1] + "," + (parseInt(boundsArr[0]) + dummyShape.bounds.width()) + "," + (parseInt(boundsArr[1]) + dummyShape.bounds.height());
				}
				
			}.bind(this));
			console.log("ok1");
			console.log(serializedJSON);
			this.facade.importJSON( serializedJSON );
			console.log("ok2");
			if(success)
				success();
		
		}
	},
	
	throwWarning: function( text ){
		Ext.MessageBox.show({
					title: 		ORYX.I18N.Oryx.title,
 					msg: 		text,
					buttons: 	Ext.MessageBox.OK,
					icon: 		Ext.MessageBox.WARNING
				});
	},
	
	
	/**
	 * Opens an upload dialog.
	 * 
	 */
	_showImportDialog: function( successCallback ){
	
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		ORYX.I18N.XFormsSerialization.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: ORYX.I18N.XFormsSerialization.file,
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
			title: 		ORYX.I18N.XFormsSerialization.impTitle, 
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
					text:ORYX.I18N.XFormsSerialization.impButton,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.XFormsSerialization.impProgress});
						loadMask.show();
						
						window.setTimeout(function(){
					
							var xhtmlString =  form.items.items[2].getValue();
							
							var params = { resource: location.href, data: xhtmlString };
							this.sendRequest(
								ORYX.CONFIG.XFORMS_IMPORT_URL, 
								params, 
								function(request) {
									this.facade.importJSON(request.responseText);
									loadMask.hide();
									dialog.hide();
									//this.loadERDF(request.responseText, function(){loadMask.hide();dialog.hide()}.bind(this), function(){loadMask.hide();}.bind(this))
								}.bind(this) );
														
							
						}.bind(this), 100);
			
					}.bind(this)
				},{
					text:ORYX.I18N.XFormsSerialization.close,
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
				var text = evt.target.files[0].getAsBinary();
				form.items.items[2].setValue( text );
			}, true)

	}
	
});

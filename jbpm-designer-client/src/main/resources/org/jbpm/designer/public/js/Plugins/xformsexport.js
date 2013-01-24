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

ORYX.Plugins.XFormsExport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,

	construct: function(facade) {
		this.facade = facade;

		this.facade.offer({
			'name':ORYX.I18N.XFormsSerialization.exportXForms,
			'functionality': this.exportXForms.bind(this),
			'group': ORYX.I18N.XFormsSerialization.group,
			'icon': ORYX.PATH + "images/xforms_export.png",
			'description': ORYX.I18N.XFormsSerialization.exportXFormsDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0});
	},
	
	exportXForms: function(){
		this._showCssDialog();
	},

	exportIt: function(cssUrl){

		// raise loading enable event
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE
        });
		
		//this.checkClientXFormsSupport();
            
		// asynchronously ...
        window.setTimeout((function(){
			
			// ... save synchronously
            this.exportSynchronously(cssUrl);
			
			// raise loading disable event.
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_LOADING_DISABLE
            });
			
        }).bind(this), 10);

		return true;
    },

    exportSynchronously: function(cssUrl) {
		 var resource = location.href;
		try {
			var serialized_rdf 	= this.getRDFFromDOM();
			serialized_rdf 		= serialized_rdf.startsWith("<?xml") ? serialized_rdf : "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;

			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.XFORMS_EXPORT_URL, {
				method: 'POST',
				asynchronous: false,
				parameters: {
					resource: resource,
					data: serialized_rdf,
					css: cssUrl
				},
				onSuccess: function(request){
					
						var win = window.open("data:text/xml," +
								request.responseText, 
								"_blank", 
								"resizable=yes,width=640,height=480,toolbar=0,scrollbars=yes");
						
				}
			});
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
	 	}
	},
	
	checkClientXFormsSupport: function() {
		if(!clientSupportsXForms) {
			
			var output = ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc;

			var win = new Ext.Window({
                            width: 320,
                            height: 240,
                            resizable: false,
                            minimizable: false,
                            modal: true,
                            autoScroll: true,
                            title: ORYX.I18N.XFormsSerialization.noClientXFormsSupport,
                            html: output,
                            buttons: [{
                                text: ORYX.I18N.XFormsSerialization.ok,
                                handler: function(){
                                    win.hide();
                                }
                            }]
                        });
        	win.show();
			
		}
	},
	
	/**
	 * Opens dialog
	 * 
	 */
	_showCssDialog: function( successCallback ){
	
	    var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		ORYX.I18N.XFormsSerialization.selectCss, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
	            anchor: '100% -30' 
	        }]
	    });



		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.XFormsSerialization.expTitle, 
			height: 	150, 
			width:		500,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[form],
			buttons:[
				{
					text:ORYX.I18N.XFormsSerialization.ok,
					handler:function(){		
						this.exportIt(form.items.items[1].getValue());
						dialog.hide();
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

	}

});


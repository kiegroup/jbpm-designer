if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.JPDLMigration = Clazz.extend({
	construct: function(facade){
		this.facade = facade;

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name':ORYX.I18N.View.migratejPDL,
                'functionality': this.migrateJPDL.bind(this),
                'group': 'importgroup',
                'icon': ORYX.BASE_FILE_PATH + "images/jpdl_import_icon.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/import.png",
                'description': ORYX.I18N.View.migratejPDLDesc,
                'index': 3,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.READONLY != true;
    //				profileParamName = "profile";
    //				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //		        regexa = new RegExp( regexSa );
    //		        profileParams = regexa.exec( window.location.href );
    //		        profileParamValue = profileParams[1];
    //				return profileParamValue == "jbpm";
                }.bind(this)
            });
        }
	},
	
	migrateJPDL : function() {
		this._showImportDialog();
	},
	_showImportDialog: function( successCallback ) {
	    var form = new Ext.form.FormPanel({
		baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [
	        {
	            text : 		ORYX.I18N.jPDLSupport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            //anchor:		'100%',
				xtype : 	'label' 
	        },
	        {
	            fieldLabel: ORYX.I18N.jPDLSupport.file,
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	        }, 
	        {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
		        grow: false,
				width: 450,
				height: 200
	        }
	        ]
	    });
	    
	    var form2 = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [
	        {
	            text : 		ORYX.I18N.jPDLSupport.selectGpdFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            //anchor:		'100%',
				xtype : 	'label' 
	        },
	        {
	            fieldLabel: ORYX.I18N.jPDLSupport.gpdfile,
	            name: 		'subject',
				inputType : 'file',
				style : 	'margin-bottom:10px;display:block;',
				itemCls :	'ext_specific_window_overflow'
	        }, 
	        {
	            xtype: 'textarea',
	            hideLabel: true,
	            name: 'msg',
		        grow: false,
                width: 450,
                height: 200
	        }
	        ]
	    });

		// Create the panel
		var dialog = new Ext.Window({ 
			autoCreate: true,
			autoScroll:true, 
			//layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.jPDLSupport.impJPDL, 
			height: 	450, 
			width:		500,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[form,form2],
			buttons:[
				{
					text:ORYX.I18N.jPDLSupport.impBtn,
					handler:function(){

                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'info',
                            msg         : ORYX.I18N.jPDLSupport.impProgress,
                            title       : ''

                        });

						window.setTimeout(function(){
					
							var jpdlString =  form.items.items[2].getValue();
							var gpdString =  form2.items.items[2].getValue();
							
							this._sendRequest(
									ORYX.CONFIG.TRANSFORMER_URL(),
									'POST',
									{ 'jpdl' : jpdlString,
									  'gpd'  : gpdString,
									  'transformto' : 'jpdl2bpmn2',
									  'profile' : ORYX.PROFILE,
									  'uuid' :  window.btoa(encodeURI(ORYX.UUID))
									},
									function( arg ) { this._loadJSON( arg );  dialog.hide(); }.bind(this),
									function() { dialog.hide(); }.bind(this)
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
			var reader = new FileReader();
			reader.onload = function(e) { 
				form.items.items[2].setValue( e.target.result ); 
			} 
			reader.readAsText(evt.target.files[0], 'UTF-8');
		}, true);
		form2.items.items[1].getEl().dom.addEventListener('change',function(evt){
			var reader = new FileReader();
			reader.onload = function(e) { 
				form2.items.items[2].setValue( e.target.result ); 
			} 
			reader.readAsText(evt.target.files[0], 'UTF-8');
        }, true);
	},
	_loadJSON: function( jsonString ){
		if (jsonString) {
			var jsonObj = jsonString.evalJSON();
			this.facade.importJSON(jsonString);
		} else {
			this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
		}
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
					successcallback( transport.responseText )	
				}
				
			}.bind(this),
			
			onFailure		: function(transport) {

				if(failedcallback){
					
					failedcallback();
					
				} else {
					this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedReq);
					ORYX.log.warn("jPDL migration failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		return suc;		
	}
});
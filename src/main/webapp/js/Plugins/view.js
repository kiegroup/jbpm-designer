/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
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

/**
 * @namespace Oryx name space for plugins
 * @name ORYX.Plugins
*/
if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * The view plugin offers all of zooming functionality accessible over the 
 * tool bar. This are zoom in, zoom out, zoom to standard, zoom fit to model.
 * 
 * @class ORYX.Plugins.View
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
*/
ORYX.Plugins.View = {
	/** @lends ORYX.Plugins.View.prototype */
	facade: undefined,
	diffEditor: undefined,
	diffDialog: undefined,

	construct: function(facade, ownPluginData) {
		this.facade = facade;
		
		// registered voice commands
		this.facade.registerOnEvent(ORYX.CONFIG.VOICE_COMMAND_GENERATE_FORMS, this.generateTaskForms.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.VOICE_COMMAND_GENERATE_IMAGE, this.showAsPNG.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.VOICE_COMMAND_VIEW_SOURCE, this.showProcessBPMN.bind(this));
		
		//Standard Values
		this.zoomLevel = 1.0;
		this.maxFitToScreenLevel=1.5;
		this.minZoomLevel = 0.4;
		this.maxZoomLevel = 2.0;
		this.diff=5; //difference between canvas and view port, s.th. like toolbar??
		
		//Read properties
		if (ownPluginData.properties) {
			ownPluginData.properties.each( function(property) {			
				if (property.zoomLevel) {this.zoomLevel = Number(1.0);}		
				if (property.maxFitToScreenLevel) {this.maxFitToScreenLevel=Number(property.maxFitToScreenLevel);}
				if (property.minZoomLevel) {this.minZoomLevel = Number(property.minZoomLevel);}
				if (property.maxZoomLevel) {this.maxZoomLevel = Number(property.maxZoomLevel);}
			}.bind(this));
		}

		
		/* Register zoom in */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomIn,
			'functionality': this.zoom.bind(this, [1.0 + ORYX.CONFIG.ZOOM_OFFSET]),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/magnifier_zoom_in.png",
			'description': ORYX.I18N.View.zoomInDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){return this.zoomLevel < this.maxZoomLevel }.bind(this)});
		
		/* Register zoom out */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomOut,
			'functionality': this.zoom.bind(this, [1.0 - ORYX.CONFIG.ZOOM_OFFSET]),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/magnifier_zoom_out.png",
			'description': ORYX.I18N.View.zoomOutDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){ return this._checkSize() }.bind(this)});
		
		/* Register zoom standard */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomStandard,
			'functionality': this.setAFixZoomLevel.bind(this, 1),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/zoom_standard.png",
			'cls' : 'icon-large',
			'description': ORYX.I18N.View.zoomStandardDesc,
			'index': 3,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){return this.zoomLevel != 1}.bind(this)
		});
		
		/* Register zoom fit to model */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomFitToModel,
			'functionality': this.zoomFitToModel.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/image.png",
			'description': ORYX.I18N.View.zoomFitToModelDesc,
			'index': 4,
			'minShape': 0,
			'maxShape': 0
		});
		
		/* Register popout to model */
//		this.facade.offer({
//			'name':ORYX.I18N.View.showInPopout,
//			'functionality': this.showInPopout.bind(this),
//			'group': ORYX.I18N.View.jbpmgroup,
//			'icon': ORYX.PATH + "images/popup.gif",
//			'description': ORYX.I18N.View.showInPopoutDesc,
//			'index': 1,
//			'minShape': 0,
//			'maxShape': 0,
//			'isEnabled': function(){
//				profileParamName = "profile";
//				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//		        regexa = new RegExp( regexSa );
//		        profileParams = regexa.exec( window.location.href );
//		        profileParamValue = profileParams[1]; 
//				return profileParamValue == "jbpm";
//			}.bind(this)
//		});
		
		/* Register full screen to model */
		this.facade.offer({
			'name':'Show in full screen',
			'functionality': this.showInFullScreen.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			'icon': ORYX.PATH + "images/fullscreen.png",
			'description': 'Show in full screen mode',
			'index': 2,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		
		/* Register task form generation to model */
		this.facade.offer({
			'name':ORYX.I18N.View.generateTaskForms,
			'functionality': this.generateTaskForms.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			'icon': ORYX.PATH + "images/human_task.gif",
			'description': ORYX.I18N.View.generateTaskFormsDesc,
			'index': 3,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register sharing to model 1*/
		this.facade.offer({
			'name': "Share Process Image",
			'functionality': this.shareProcessImage.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			//'icon': ORYX.PATH + "images/share.gif",
			dropDownGroupIcon : ORYX.PATH + "images/share.gif",
			'description': "Share Process Image",
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register sharing to model 2*/
		this.facade.offer({
			'name': "Share Process PDF",
			'functionality': this.shareProcessPdf.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			//'icon': ORYX.PATH + "images/share.gif",
			dropDownGroupIcon : ORYX.PATH + "images/share.gif",
			'description': "Share Process PDF",
			'index': 2,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register import from BPMN2*/
		this.facade.offer({
			'name': "Import from BPMN2",
			'functionality': this.importFromBPMN2.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			//'icon': ORYX.PATH + "images/share.gif",
			dropDownGroupIcon : ORYX.PATH + "images/import.png",
			'description': "Import from existing BPMN2",
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register import from JSON*/
		this.facade.offer({
			'name': "Import from JSON",
			'functionality': this.importFromJSON.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			//'icon': ORYX.PATH + "images/share.gif",
			dropDownGroupIcon : ORYX.PATH + "images/import.png",
			'description': "Import from existing JSON",
			'index': 2,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register sharing to model 3*/
		this.facade.offer({
			'name': "Share Embeddable Process",
			'functionality': this.shareEmbeddableProcess.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			//'icon': ORYX.PATH + "images/share.gif",
			dropDownGroupIcon : ORYX.PATH + "images/share.gif",
			'description': "Share Embeddable Process",
			'index': 3,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register diff to model */
		this.facade.offer({
			'name':ORYX.I18N.View.viewDiff,
			'functionality': this.diffprocess.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			'icon': ORYX.PATH + "images/diff.gif",
			'description': ORYX.I18N.View.viewDiffDesc,
			'index': 5,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		// Footer items
		/* Register information view to model */
		this.facade.offer({
			'name':'ViewBPMN2Button',
			'functionality': this.showProcessBPMN.bind(this),
			'group': 'footerViewSourceButton',
			'text': 'BPMN2',
			'description': 'View BPMN2 Source',
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register information view to model */
		this.facade.offer({
			'name':'ViewJSONButton',
			'functionality': this.showProcessJSON.bind(this),
			'group': 'footerViewJSONButton',
			'text': 'JSON',
			'description': 'View JSON Source',
			'index': 2,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register information view to model */
		this.facade.offer({
			'name':'ViewSVGButton',
			'functionality': this.showProcessSVG.bind(this),
			'group': 'footerViewSVGButton',
			'text': 'SVG',
			'description': 'View SVG Source',
			'index': 3,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register information view to model */
		this.facade.offer({
			'name':'ViewERDFButton',
			'functionality': this.showProcessERDF.bind(this),
			'group': 'footerViewERDFButton',
			'text': 'ERDF',
			'description': 'View ERDF Source',
			'index': 4,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register information view to model */
		this.facade.offer({
			'name':'ViewPDFButton',
			'functionality': this.showAsPDF.bind(this),
			'group': 'footerViewPDFButton',
			'text': 'PDF',
			'description': 'View PDF',
			'index': 5,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register information view to model */
		this.facade.offer({
			'name':'ViewPNGButton',
			'functionality': this.showAsPNG.bind(this),
			'group': 'footerViewPNGButton',
			'text': 'PNG',
			'description': 'View PNG',
			'index': 6,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
	},
	
	/**
	 * It sets the zoom level to a fix value and call the zooming function.
	 * 
	 * @param {Number} zoomLevel
	 * 			the zoom level
	 */
	setAFixZoomLevel : function(zoomLevel) {
		this.zoomLevel = zoomLevel;
		this._checkZoomLevelRange();
		this.zoom(1);
	},
	
	/**
	 * Shows the canvas in bigger popout window.
	 * 
	 */
	showInPopout : function() {
		uuidParamName = "uuid";
        uuidParamName = uuidParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
        regexS = "[\\?&]"+uuidParamName+"=([^&#]*)";
        regex = new RegExp( regexS );
        uuidParams = regex.exec( window.location.href );

        uuidParamValue = uuidParams[1]; 
        window.open (ORYX.EXTERNAL_PROTOCOL + "://" + ORYX.EXTERNAL_HOST + "/" + ORYX.EXTERNAL_SUBDOMAIN  + "/org.drools.guvnor.Guvnor/standaloneEditorServlet?assetsUUIDs=" + uuidParamValue + "&client=oryx" , "Process Editor","status=0,toolbar=0,menubar=0,resizable=0,location=no,width=1400,height=1000");
	},
	
	showInFullScreen : function() {
		this.goFullscreen('jbpmdesigner');
	},
	
	/**
	 * Import from existing BPMN2
	 */
	importFromBPMN2 : function(successCallback) {
		var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		ORYX.I18N.FromBPMN2Support.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: ORYX.I18N.FromBPMN2Support.file,
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
			title: 		ORYX.I18N.FromBPMN2Support.impBPMN2, 
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
					text:ORYX.I18N.FromBPMN2Support.impBtn,
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.FromBPMN2Support.impProgress});
						loadMask.show();
						var bpmn2string =  form.items.items[2].getValue();
						Ext.Ajax.request({
				            url: ORYX.PATH + "transformer",
				            method: 'POST',
				            success: function(request) {
				            	if(request.responseText.length < 1) {
				            		loadMask.hide();
				            		dialog.hide();
				            		Ext.Msg.minWidth = 400;
				            		Ext.Msg.alert("Failed to import BPMN2. Check server logs for more details."); 
				            	} else {
					    	   		try {
					    	   			this._loadJSON( request.responseText );
					    	   		} catch(e) {
					    	   			Ext.Msg.minWidth = 400;
					    	   			Ext.Msg.alert("Failed to import BPMN2: " + e);
					    	   		}
					    	   		loadMask.hide();
					    	   		dialog.hide();
				            	}
				            }.createDelegate(this),
				            failure: function(){
				            	Ext.Msg.minWidth = 400;
				            	Ext.Msg.alert("Failed to import BPMN2.");
				            },
				            params: {
				            	profile: ORYX.PROFILE,
				            	uuid : ORYX.UUID,
				            	pp: ORYX.PREPROCESSING,
				            	bpmn2 : bpmn2string,
				            	transformto : "bpmn2json"
				            }
				        });
					}.bind(this)
				},{
					text:ORYX.I18N.FromBPMN2Support.close,
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
			}, true)
	},
	
	/**
	 * Import from existing JSON
	 */
	importFromJSON : function(successCallback) {
		var form = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	50,
	        defaultType: 	'textfield',
	        items: [{
	            text : 		ORYX.I18N.FromJSONSupport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: ORYX.I18N.FromJSONSupport.file,
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
			title: 		ORYX.I18N.FromJSONSupport.impBPMN2, 
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
					text:ORYX.I18N.FromJSONSupport.impBtn,
					handler:function(){
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.FromJSONSupport.impProgress});
						loadMask.show();
						var jsonString =  form.items.items[2].getValue();
						try {
		    	   			this._loadJSON( jsonString );
		    	   		} catch(e) {
		    	   			Ext.Msg.alert("Failed to import JSON :\n" + e);
		    	   		}
		    	   		loadMask.hide();
		    	   		dialog.hide();
					}.bind(this)
				},{
					text:ORYX.I18N.FromJSONSupport.close,
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
		}, true)
	},
	
	/**
	 * Share the embeddable process
	 */
	shareEmbeddableProcess : function() {
		Ext.Ajax.request({
            url: ORYX.PATH + "transformer",
            method: 'POST',
            success: function(request){
    	   		try {
    	   			var cf = new Ext.form.TextArea({
    	   	            id:"sharedEmbeddableArea",
    	   	            fieldLabel:"Embeddable Process",
    	   	            width:400,
    	   	            height:250,
    	   	            value:request.responseText
    	   	            });

    	   			var win = new Ext.Window({
    	   				width:400,
    	   				id:'sharedEmbeddableURL',
    	   				height:250,
    	   				autoScroll:true,
    	   				title:'Embeddable Process',
    	   				items: [cf]
    	   				});
    	   			win.show();
    	   		} catch(e) {
    	   			Ext.Msg.alert("Failed to create embeddable process code :\n" + e);
    	   		}
                Ext.Msg.hide();
            }.createDelegate(this),
            failure: function(){
            	Ext.Msg.alert("Failed to create embeddable process code.");
            },
            params: {
            	profile: ORYX.PROFILE,
            	uuid : ORYX.UUID,
            	respaction : "showembeddable"
            }
        });
	},
	
	/**
	 * Share the process PDF URL.
	 */
	shareProcessPdf : function() {
		var createStorePDFMask = new Ext.LoadMask(Ext.getBody(), {msg:"Creating the process PDF..."});
		createStorePDFMask.show();
		var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
		var rawSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getRootNode().cloneNode(true));
		
		Ext.Ajax.request({
            url: ORYX.PATH + "transformer",
            method: 'POST',
            success: function(request){
    	   		try {
    	   			createStorePDFMask.hide();
    	   			var cf = new Ext.form.TextArea({
    	   	            id:"sharedPDFArea",
    	   	            fieldLabel:"Process Image PDF",
    	   	            width:400,
    	   	            height:250,
    	   	            value:request.responseText
    	   	            });

    	   			var win = new Ext.Window({
    	   				width:400,
    	   				id:'sharedPDFURL',
    	   				height:250,
    	   				autoScroll:true,
    	   				title:'Process PDF URL',
    	   				items: [cf]
    	   				});
    	   			win.show();
    	   		} catch(e) {
    	   			createStorePDFMask.hide();
    	   			Ext.Msg.alert("Failed to create the process PDF :\n" + e);
    	   		}
                Ext.Msg.hide();
            }.createDelegate(this),
            failure: function(){
            	createStorePDFMask.hide();
            	Ext.Msg.alert("Failed to create the process PDF.");
            },
            params: {
            	profile: ORYX.PROFILE,
            	uuid : ORYX.UUID,
            	fsvg : formattedSvgDOM,
            	rsvg : rawSvgDOM,
            	transformto : "pdf",
            	respaction : "showurl"
            }
        });
	},
	
	/**
	 * Share the process image URL.
	 */
	shareProcessImage : function() {
		var createStoreImageMask = new Ext.LoadMask(Ext.getBody(), {msg:"Creating the process image..."});
		createStoreImageMask.show();
		var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
		var rawSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getRootNode().cloneNode(true));
		
		Ext.Ajax.request({
            url: ORYX.PATH + "transformer",
            method: 'POST',
            success: function(request){
    	   		try {
    	   			createStoreImageMask.hide();
    	   			var cf = new Ext.form.TextArea({
    	   	            id:"sharedImageArea",
    	   	            fieldLabel:"Process Image URL",
    	   	            width:400,
    	   	            height:250,
    	   	            value:request.responseText
    	   	            });

    	   			var win = new Ext.Window({
    	   				width:400,
    	   				id:'sharedImageURL',
    	   				height:250,
    	   				autoScroll:true,
    	   				title:'Process Image URL',
    	   				items: [cf]
    	   				});
    	   			win.show();
    	   		} catch(e) {
    	   			createStoreImageMask.hide();
    	   			Ext.Msg.alert("Failed to create the process image :\n" + e);
    	   		}
                Ext.Msg.hide();
            }.createDelegate(this),
            failure: function(){
            	createStoreImageMask.hide();
            	Ext.Msg.alert("Failed to create the process image.");
            },
            params: {
            	profile: ORYX.PROFILE,
            	uuid : ORYX.UUID,
            	fsvg : formattedSvgDOM,
            	rsvg : rawSvgDOM,
            	transformto : "png",
            	respaction : "showurl"
            }
        });
	},
	
	/**
	 * Created and displays code for sharing the process.
	 */
	shareProcess : function() {
		alert('sharing process');
	},
	
	/**
	 * Displays a diff (visual and text based) between two versions of the process.
	 */
	diffprocess : function() {
		var diffLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.View.viewDiffLoadingVersions});
		diffLoadMask.show();
		Ext.Ajax.request({
            url: ORYX.PATH + "processdiff",
            method: 'POST',
            success: function(request){
    	   		try {
    	   			diffLoadMask.hide();
    	   			this._showProcessDiffDialog(request.responseText);
    	   		} catch(e) {
    	   			diffLoadMask.hide();
    	   			Ext.Msg.alert("Failed to retrieve process version information:\n" + e);
    	   		}
            }.createDelegate(this),
            failure: function(){
            	diffLoadMask.hide();
            	Ext.Msg.alert("Failed to retrieve process version information.");
            },
            params: {
            	action: 'versions',
            	profile: ORYX.PROFILE,
            	uuid : ORYX.UUID
            }
        });
	},
	
	_showProcessDiffDialog : function( jsonString ) {
		var jsonObj = jsonString.evalJSON();
		var versionKeys = [];
		var count = 0;
	    for (var key in jsonObj) {
	      if (jsonObj.hasOwnProperty(key)) {
	    	  versionKeys.push(parseInt(key));
	    	  count++;
	      }
	    }
	    
	    if(count == 0) {
	    	Ext.Msg.minWidth=300;
	    	Ext.Msg.alert("Diff", "Unable to find proces versions.");
	    } else {
		    versionKeys.sort(function(a,b){return a - b});
		    var displayVersionKeys = [];
		    for (var i = 0; i < versionKeys.length; i++) {
		    	displayVersionKeys[i] = [versionKeys[i] + ""];
		    }
		    var versionStore = new Ext.data.SimpleStore({
				fields: ["name"],
				data : displayVersionKeys 
			});
		    
		    var versionCombo = new Ext.form.ComboBox({
		    	fieldLabel: 'Select process version',
	            labelStyle: 'width:180px',
	            hiddenName: 'version_name',
	            emptyText: 'Select process version...',
	            store: versionStore,
	            displayField: 'name',
	            valueField: 'name',
	            mode: 'local',
	            typeAhead: true,
	            //width: 168,
	            triggerAction: 'all',
	            listeners: 
	              { 
	            	select: { 
	            		fn:function(combo, value) {
	                    var processJSON = ORYX.EDITOR.getSerializedJSON();
	              		Ext.Ajax.request({
	                          url: ORYX.PATH + "uuidRepository",
	                          method: 'POST',
	                          success: function(request){
	                  	   		try {
	                  	   			var canvasBPMN2 = request.responseText;
	                  	   			Ext.Ajax.request({
	                  	              url: ORYX.PATH + "processdiff",
	                  	              method: 'POST',
	                  	              success: function(request){
	                  	      	   		try {
	                  	      	   			var diffCreateMask = new Ext.LoadMask(Ext.getBody(), {msg:'Creating diff...'});
	                  	      	   			diffCreateMask.show();
	                  	      	   			var versionBPMN2 = request.responseText;
	                  	      	   			var dmp = new diff_match_patch();
	                  	      	   			dmp.Diff_Timeout = 0;
	                  	      	   			var d = dmp.diff_main(versionBPMN2, canvasBPMN2);
	                  	      	   			dmp.diff_cleanupSemantic(d);
	                  	      	   			var ds = dmp.diff_prettyHtml(d);
	                  	      	   			diffCreateMask.hide();
	                  	      	   			this.diffDialog.remove(this.diffEditor, true);
	                  	      	   			this.diffEditor = new Ext.form.HtmlEditor({
	                  	      	   				id: 'diffeditor',
	                  	      	   				value:     ds,
	                  	      	   				enableSourceEdit: false,
	                  	      	   				enableAlignments:false,
	                  	      	   				enableColors: false,
	                  	      	   				enableFont: false,
	                  	      	   				enableFontSize:false,
	                  	      	   				enableFormat: false,
	                  	      	   				enableLinks: false,
	                  	      	   				enableLists:false,
	                  	      	   				autoScroll: true,
	                  	      	   				width:  520,
	                  	      	   				height: 310
	                  	      	   			});
	                  	      	   			
	                  	      	   			this.diffDialog.add(this.diffEditor);
	                  	      	   			this.diffDialog.doLayout();
	                  	      	   		} catch(e) {
	                  	      	   			Ext.Msg.alert("Failed to retrieve process version source:\n" + e);
	                  	      	   		}
	                  	              }.bind(this),
	                  	              failure: function(){
	                  	              	Ext.Msg.alert("Failed to retrieve process version source.");
	                  	              }.bind(this),
	                  	              params: {
	                  	              	action: 'getversion',
	                  	              	version: combo.getValue(),
	                  	              	profile: ORYX.PROFILE,
	                  	              	uuid : ORYX.UUID
	                  	              }
	                  	          });
	                  	   		} catch(e){
	                  	   			Ext.Msg.alert("Converting to BPMN2 Failed :\n"+e);
	                  	   		}
	                          }.bind(this),
	                          failure: function(){
	                          	Ext.Msg.alert("Converting to BPMN2 Failed");
	                          },
	                          params: {
	                          	action: 'toXML',
	                          	pp: ORYX.PREPROCESSING,
	                          	profile: ORYX.PROFILE,
	                          	data: processJSON
	                          }
	                      });
	                    }.bind(this)
	                }  
	             }
		    });
		    
		    this.diffEditor = new Ext.form.HtmlEditor({
				 id: 'diffeditor',
		         value:     '',
		     	 enableSourceEdit: false,
		     	 enableAlignments:false,
	   			 enableColors: false,
	   			 enableFont: false,
	   			 enableFontSize:false,
	   			 enableFormat: false,
	   			 enableLinks: false,
	   			 enableLists:false,
		         autoScroll: true,
		         width: 520,
		         height: 310
		       });
		    
		    this.diffDialog = new Ext.Window({ 
				autoCreate: true,
				autoScroll: false,
				plain:		true,
				bodyStyle: 	'padding:5px;',
				title: 		'Compare process BPMN2 with previous versions', 
				height: 	410, 
				width:		550,
				modal:		true,
				fixedcenter:true, 
				shadow:		true, 
				proxyDrag: 	true,
				resizable:	true,
				items: 		[this.diffEditor],
				tbar: [versionCombo],
				buttons:[
							{
								text : 'Close',
								handler:function(){
									this.diffDialog.hide();
								}.bind(this)
							}
						]
			});
		    this.diffDialog.show();
		    this.diffDialog.doLayout();
	    }
	},
	
	/**
	 * Loads JSON into the editor
	 * 
	 */
	_loadJSON: function( jsonString ){
		if (jsonString) {
			var jsonObj = jsonString.evalJSON();
			this.facade.importJSON(jsonString);
		} else {
			this._showErrorMessageBox(ORYX.I18N.Oryx.title, ORYX.I18N.jPDLSupport.impFailedJson);
		}
	},
	
	_showErrorMessageBox: function(title, msg){
        Ext.MessageBox.show({
           title: title,
           msg: msg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	},

	/**
	 * Converts the process to pdf format.
	 * 
	 */
	showAsPDF : function() {
		var transformval = 'pdf';
		var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
		var rawSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getRootNode().cloneNode(true));
		var method ="post";
		var form = document.createElement("form");
		form.setAttribute("name", "transformerform");
		form.setAttribute("method", method);
		form.setAttribute("action", ORYX.CONFIG.TRANSFORMER_URL());
		form.setAttribute("target", "_blank");
		
		var hfFSVG = document.createElement("input");
		hfFSVG.setAttribute("type", "hidden");
		hfFSVG.setAttribute("name", "fsvg");
		hfFSVG.setAttribute("value", formattedSvgDOM);
        form.appendChild(hfFSVG);
        
        var hfRSVG = document.createElement("input");
        hfRSVG.setAttribute("type", "hidden");
        hfRSVG.setAttribute("name", "rsvg");
        hfRSVG.setAttribute("value", rawSvgDOM);
        form.appendChild(hfRSVG);
        
        var hfUUID = document.createElement("input");
        hfUUID.setAttribute("type", "hidden");
        hfUUID.setAttribute("name", "uuid");
        hfUUID.setAttribute("value", ORYX.UUID);
        form.appendChild(hfUUID);
        
        var hfPROFILE = document.createElement("input");
        hfPROFILE.setAttribute("type", "hidden");
        hfPROFILE.setAttribute("name", "profile");
        hfPROFILE.setAttribute("value", ORYX.PROFILE);
        form.appendChild(hfPROFILE);
        
        var hfTRANSFORMTO = document.createElement("input");
        hfTRANSFORMTO.setAttribute("type", "hidden");
        hfTRANSFORMTO.setAttribute("name", "transformto");
        hfTRANSFORMTO.setAttribute("value", transformval);
        form.appendChild(hfTRANSFORMTO);
        
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var hfPROCESSID = document.createElement("input");
        hfPROCESSID.setAttribute("type", "hidden");
        hfPROCESSID.setAttribute("name", "processid");
        hfPROCESSID.setAttribute("value", processId);
        form.appendChild(hfPROCESSID);
        
        document.body.appendChild(form);
        form.submit();	 
	},
	
	/**
	 * Displays the process SVG sources (formatted)
	 */
	showProcessSVG : function() {
		var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
		var cf = new Ext.form.TextArea({
            id:"svgSourceTextArea",
            fieldLabel:"SVG Source",
            value:formattedSvgDOM,
            autoScroll:true
            });

		var win = new Ext.Window({
			width:600,
			id:'processSVGSource',
			height:550,
			layout: 'fit',
			title:'Process SVG Source',
			items: [cf],
			buttons:[{
							text : 'Close',
							handler:function(){
								win.close();
								win = null;
								cf = null;
								sourceEditor = null;
							}.bind(this)
						}]
			});
		win.show();
		this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);
		var sourceEditor = CodeMirror.fromTextArea(document.getElementById("svgSourceTextArea"), {
			  mode: "application/xml",
			  lineNumbers: true,
			  lineWrapping: true,
			  onGutterClick: this.foldFunc
			});
	},
	
	showProcessERDF : function() {
		var processERDF = ORYX.EDITOR.getERDF();
		var cf = new Ext.form.TextArea({
            id:"erdfSourceTextArea",
            fieldLabel:"ERDF Source",
            value:processERDF,
            autoScroll:true,
            height:'80%'
            });

		var win = new Ext.Window({
			width:600,
			id:'processERDFSource',
			height:550,
			layout: 'fit',
			title:'ERDF Source',
			items: [cf],
			buttons:[{
							text : 'Close',
							handler:function(){
								win.close();
								win = null;
								cf = null;
								sourceEditor = null;
							}.bind(this)
						}]
			});
		win.show();
		this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);
		var sourceEditor = CodeMirror.fromTextArea(document.getElementById("erdfSourceTextArea"), {
 			  mode: "application/xml",
 			  lineNumbers: true,
 			  lineWrapping: true,
 			  onGutterClick: this.foldFunc
 			});
	},
	
	showProcessJSON : function() {
		var processJSON = ORYX.EDITOR.getSerializedJSON();
		var cf = new Ext.form.TextArea({
            id:"jsonSourceTextArea",
            fieldLabel:"JSON Source",
            value:processJSON,
            autoScroll:true
            });

		var win = new Ext.Window({
			width:600,
			id:'processJSONSource',
			height:550,
			layout: 'fit',
			title:'JSON Source',
			items: [cf],
			buttons:[{
				text : 'Close',
				handler:function(){
					win.close();
					win = null;
					cf = null;
					sourceEditor = null;
				}.bind(this)
			}]
			});
		win.show();
		this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.braceRangeFinder);
		var sourceEditor = CodeMirror.fromTextArea(document.getElementById("jsonSourceTextArea"), {
 			  mode: "application/json",
 			  lineNumbers: true,
 			  lineWrapping: true,
 			  onGutterClick: this.foldFunc
 			});
	},
	
	showProcessBPMN : function() {
		var processJSON = ORYX.EDITOR.getSerializedJSON();
		Ext.Ajax.request({
            url: ORYX.PATH + "uuidRepository",
            method: 'POST',
            success: function(request){
    	   		try{
    	   			var cf = new Ext.form.TextArea({
    	   	            id:"bpmnSourceTextArea",
    	   	            fieldLabel:"BPMN2 Source",
    	   	            value:request.responseText,
    	   	            autoScroll:true
    	   	            });

    	   			var win = new Ext.Window({
    	   				width:600,
    	   				id:'processBPMNSource',
    	   				height:550,
    	   				layout: 'fit',
    	   				title:'BPMN2 Source',
    	   				items: [cf],
    	   				buttons		: [{
    	   	                text: 'Save to file',
    	   	                handler: function(){
    	   	                	var processJSON = ORYX.EDITOR.getSerializedJSON();
    	   	                	var processName = jsonPath(processJSON.evalJSON(), "$.properties.name");
    	   	                	var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
    	   	                	var processVersion = jsonPath(processJSON.evalJSON(), "$.properties.version");
    	   	                	var fileName = "";
    	   	                	if(processPackage && processPackage != "") {
    	   	                		fileName += processPackage;
    	   	                	}
    	   	                	if(processName && processName != "") {
    	   	                		if(fileName != "") {
    	   	                			fileName += ".";
    	   	                		}
    	   	                		fileName += processName;
    	   	                	}
    	   	                	if(processVersion && processVersion != "") {
    	   	                		if(fileName != "") {
    	   	                			fileName += ".";
    	   	                		}
    	   	                		fileName += "v" + processVersion;
    	   	                	}
    	   	                	if(fileName == "") {
    	   	                		fileName = "processbpmn2";
    	   	                	}
    	   	                	var toStoreValue = cf.getValue();
    	   	         			var method ="post";
    	   	         			var form = document.createElement("form");
    	   	         			form.setAttribute("name", "storetofileform");
    	   	         			form.setAttribute("method", method);
    	   	         			form.setAttribute("action", ORYX.PATH + "filestore");
    	   	         			form.setAttribute("target", "_blank");
    	   	         		
    	   	         			var fnameInput = document.createElement("input");
    	   	         			fnameInput.setAttribute("type", "hidden");
    	   	         			fnameInput.setAttribute("name", "fname");
    	   	         			fnameInput.setAttribute("value", fileName);
    	   	                 	form.appendChild(fnameInput);
    	   	                 
    	   	                 	var fextInput = document.createElement("input");
    	   	                 	fextInput.setAttribute("type", "hidden");
    	   	              		fextInput.setAttribute("name", "fext");
    	   	           			fextInput.setAttribute("value", "bpmn2");
    	   	                 	form.appendChild(fextInput);
    	   	                 
    	   	                 	var fdataInput = document.createElement("input");
    	   	                 	fdataInput.setAttribute("type", "hidden");
    	   	              		fdataInput.setAttribute("name", "data");
    	   	           			fdataInput.setAttribute("value", toStoreValue);
    	   	                 	form.appendChild(fdataInput);
    	   	                 	document.body.appendChild(form);
    	   	                 	form.submit();	 
    	   	                }
    	   	            },{
							text : 'Close',
							handler:function(){
								win.close();
								win = null;
								cf = null;
								sourceEditor = null;
							}.bind(this)
						}]
    	   				});
    	   			win.show();
    	   			this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);
    	   			var sourceEditor = CodeMirror.fromTextArea(document.getElementById("bpmnSourceTextArea"), {
    	   			  mode: "application/xml",
    	   			  lineNumbers: true,
    	   			  lineWrapping: true,
    	   			  onGutterClick: this.foldFunc
    	   			});
    	   		}catch(e){
    	   			Ext.Msg.alert("Converting to BPMN2 Failed :\n"+e);
    	   		}
                Ext.Msg.hide();
            }.createDelegate(this),
            failure: function(){
            	Ext.Msg.alert("Converting to BPMN2 Failed");
            },
            params: {
            	action: 'toXML',
            	pp: ORYX.PREPROCESSING,
            	profile: ORYX.PROFILE,
            	data: processJSON
            }
        });
	},
	
	/**
	 * Converts the process to png format.
	 * 
	 */
	showAsPNG : function() {
		var transformval = 'png';
		var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
		var rawSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getRootNode().cloneNode(true));
		var method ="post";
		var form = document.createElement("form");
		form.setAttribute("name", "transformerform");
		form.setAttribute("method", method);
		form.setAttribute("action", ORYX.CONFIG.TRANSFORMER_URL());
		form.setAttribute("target", "_blank");
		
		var hfFSVG = document.createElement("input");
		hfFSVG.setAttribute("type", "hidden");
		hfFSVG.setAttribute("name", "fsvg");
		hfFSVG.setAttribute("value", formattedSvgDOM);
        form.appendChild(hfFSVG);
        
        var hfRSVG = document.createElement("input");
        hfRSVG.setAttribute("type", "hidden");
        hfRSVG.setAttribute("name", "rsvg");
        hfRSVG.setAttribute("value", rawSvgDOM);
        form.appendChild(hfRSVG);
        
        var hfUUID = document.createElement("input");
        hfUUID.setAttribute("type", "hidden");
        hfUUID.setAttribute("name", "uuid");
        hfUUID.setAttribute("value", ORYX.UUID);
        form.appendChild(hfUUID);
        
        var hfPROFILE = document.createElement("input");
        hfPROFILE.setAttribute("type", "hidden");
        hfPROFILE.setAttribute("name", "profile");
        hfPROFILE.setAttribute("value", ORYX.PROFILE);
        form.appendChild(hfPROFILE);
        
        var hfTRANSFORMTO = document.createElement("input");
        hfTRANSFORMTO.setAttribute("type", "hidden");
        hfTRANSFORMTO.setAttribute("name", "transformto");
        hfTRANSFORMTO.setAttribute("value", transformval);
        form.appendChild(hfTRANSFORMTO);
        
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var hfPROCESSID = document.createElement("input");
        hfPROCESSID.setAttribute("type", "hidden");
        hfPROCESSID.setAttribute("name", "processid");
        hfPROCESSID.setAttribute("value", processId);
        form.appendChild(hfPROCESSID);
        
        document.body.appendChild(form);
        form.submit();	 
	},
	
	generateTaskForms : function() {
		
		var processJSON = ORYX.EDITOR.getSerializedJSON();
		var preprocessingData = ORYX.PREPROCESSING;
		
		var method ="post";
		var form = document.createElement("form");
		form.setAttribute("name", "taskformsform");
		form.setAttribute("method", method);
		form.setAttribute("action", ORYX.CONFIG.TASKFORMS_URL());
		form.setAttribute("target", "_blank");
		
		var hfSVG = document.createElement("input");
		hfSVG.setAttribute("type", "hidden");
		hfSVG.setAttribute("name", "json");
		hfSVG.setAttribute("value", processJSON);
        form.appendChild(hfSVG);
        
        var hfSVG = document.createElement("input");
		hfSVG.setAttribute("type", "hidden");
		hfSVG.setAttribute("name", "ppdata");
		hfSVG.setAttribute("value", preprocessingData);
        form.appendChild(hfSVG);
        
        var hfUUID = document.createElement("input");
        hfUUID.setAttribute("type", "hidden");
        hfUUID.setAttribute("name", "uuid");
        hfUUID.setAttribute("value", ORYX.UUID);
        form.appendChild(hfUUID);
        
        var hfPROFILE = document.createElement("input");
        hfPROFILE.setAttribute("type", "hidden");
        hfPROFILE.setAttribute("name", "profile");
        hfPROFILE.setAttribute("value", ORYX.PROFILE);
        form.appendChild(hfPROFILE);
        
        document.body.appendChild(form);
        form.submit();
	},
	
	/**
	 * It does the actual zooming. It changes the viewable size of the canvas 
	 * and all to its child elements.
	 * 
	 * @param {Number} factor
	 * 		the factor to adjust the zoom level
	 */
	zoom: function(factor) {
		// TODO: Zoomen auf allen Objekten im SVG-DOM
		
		this.zoomLevel *= factor;
		var scrollNode 	= this.facade.getCanvas().getHTMLContainer().parentNode.parentNode;
		var canvas 		= this.facade.getCanvas();
		var newWidth 	= canvas.bounds.width()  * this.zoomLevel;
		var newHeight 	= canvas.bounds.height() * this.zoomLevel;
		
		/* Set new top offset */
		var offsetTop = (canvas.node.parentNode.parentNode.parentNode.offsetHeight - newHeight) / 2.0;	
		offsetTop = offsetTop > 20 ? offsetTop - 20 : 0;
		canvas.node.parentNode.parentNode.style.marginTop = offsetTop + "px";
		offsetTop += 5;
		canvas.getHTMLContainer().style.top = offsetTop + "px";
		
		/*readjust scrollbar*/
		var newScrollTop=	scrollNode.scrollTop - Math.round((canvas.getHTMLContainer().parentNode.getHeight()-newHeight) / 2)+this.diff;
		var newScrollLeft=	scrollNode.scrollLeft - Math.round((canvas.getHTMLContainer().parentNode.getWidth()-newWidth) / 2)+this.diff;
		
		/* Set new Zoom-Level */
		canvas.setSize({width: newWidth, height: newHeight}, true);
		
		/* Set Scale-Factor */
		canvas.node.setAttributeNS(null, "transform", "scale(" +this.zoomLevel+ ")");	

		/* Refresh the Selection */
		this.facade.updateSelection();
		scrollNode.scrollTop=newScrollTop;
		scrollNode.scrollLeft=newScrollLeft;
		
		/* Update the zoom-level*/
		canvas.zoomLevel = this.zoomLevel;
	},
	
	
	/**
	 * It calculates the zoom level to fit whole model into the visible area
	 * of the canvas. Than the model gets zoomed and the position of the 
	 * scroll bars are adjusted.
	 * 
	 */
	zoomFitToModel: function() {
		
		/* Get the size of the visible area of the canvas */
		var scrollNode 	= this.facade.getCanvas().getHTMLContainer().parentNode.parentNode;
		var visibleHeight = scrollNode.getHeight() - 30;
		var visibleWidth = scrollNode.getWidth() - 30;
		
		var nodes = this.facade.getCanvas().getChildShapes();
		
		if(!nodes || nodes.length < 1) {
			return false;			
		}
			
		/* Calculate size of canvas to fit the model */
		var bounds = nodes[0].absoluteBounds().clone();
		nodes.each(function(node) {
			bounds.include(node.absoluteBounds().clone());
		});
		
		
		/* Set new Zoom Level */
		var scaleFactorWidth =  visibleWidth / bounds.width();
		var scaleFactorHeight = visibleHeight / bounds.height();
		
		/* Choose the smaller zoom level to fit the whole model */
		var zoomFactor = scaleFactorHeight < scaleFactorWidth ? scaleFactorHeight : scaleFactorWidth;
		
		/*Test if maximum zoom is reached*/
		if(zoomFactor>this.maxFitToScreenLevel){zoomFactor=this.maxFitToScreenLevel}
		/* Do zooming */
		this.setAFixZoomLevel(zoomFactor);
		
		/* Set scroll bar position */
		scrollNode.scrollTop = Math.round(bounds.upperLeft().y * this.zoomLevel) - 5;
		scrollNode.scrollLeft = Math.round(bounds.upperLeft().x * this.zoomLevel) - 5;
		
	},
	
	/**
	 * It checks if the zoom level is less or equal to the level, which is required
	 * to schow the whole canvas.
	 * 
	 * @private
	 */
	_checkSize:function(){
		var canvasParent=this.facade.getCanvas().getHTMLContainer().parentNode;
		var minForCanvas= Math.min((canvasParent.parentNode.getWidth()/canvasParent.getWidth()),(canvasParent.parentNode.getHeight()/canvasParent.getHeight()));
		return 0.7 > minForCanvas;
		
	},
	/**
	 * It checks if the zoom level is included in the definined zoom
	 * level range.
	 * 
	 * @private
	 */
	_checkZoomLevelRange: function() {
		/*var canvasParent=this.facade.getCanvas().getHTMLContainer().parentNode;
		var maxForCanvas= Math.max((canvasParent.parentNode.getWidth()/canvasParent.getWidth()),(canvasParent.parentNode.getHeight()/canvasParent.getHeight()));
		if(this.zoomLevel > maxForCanvas) {
			this.zoomLevel = maxForCanvas;			
		}*/
		if(this.zoomLevel < this.minZoomLevel) {
			this.zoomLevel = this.minZoomLevel;			
		}
		
		if(this.zoomLevel > this.maxZoomLevel) {
			this.zoomLevel = this.maxZoomLevel;			
		}
	},
	goFullscreen : function(id) {
		if(parent && parent.frames) {
			if(parent.frames.length < 2) {
				if(document.getElementById(id).requestFullScreen) {
					document.getElementById(id).requestFullScreen();
				} else if(document.getElementById(id).mozRequestFullScreen) {
					document.getElementById(id).mozRequestFullScreen();
				} else if(document.getElementById(id).webkitRequestFullScreen) {
					document.documentElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
				} else {
					Ext.Msg.minWidth = 400;
	        		Ext.Msg.alert("Browser does not support full screen mode.");
				}
			} else {
				for (var i = 0;i < parent.frames.length;i++) {
					if(parent.frames[i].ORYX) {
						parent.frames[i].frameElement.setAttribute('allowFullScreen', 'true');
						parent.frames[i].frameElement.setAttribute('mozallowfullscreen', 'true');
						parent.frames[i].frameElement.setAttribute('webkitallowfullscreen', 'true');
						if(parent.frames[i].frameElement.contentDocument.getElementById(id).requestFullScreen) {
							parent.frames[i].frameElement.contentDocument.getElementById(id).requestFullScreen();
						} else if(parent.frames[i].frameElement.contentDocument.getElementById(id).mozRequestFullScreen) {
							parent.frames[i].frameElement.contentDocument.getElementById(id).mozRequestFullScreen();
						} else if(parent.frames[i].frameElement.contentDocument.getElementById(id).webkitRequestFullScreen) {
							parent.frames[i].frameElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
						} else {
							Ext.Msg.minWidth = 400;
			        		Ext.Msg.alert("Browser does not support full screen mode.");
						}
					}
				}
			}
			
		}
	}
};

ORYX.Plugins.View = Clazz.extend(ORYX.Plugins.View);

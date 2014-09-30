
/**
 * Copyright (c) 2009
 * Helen Kaltegaertner
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

/**
 * Enables exporting and importing current model in JSON.
 */
ORYX.Plugins.TBPMSupport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,
	
	tbpmImportServletURL: ORYX.CONFIG.TBPMIMPORT,
	
	canvasId: "ext-gen56",
	
	TMP_FOLDER: ORYX.PATH + "/tmp/",
	
    construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        
        this.facade.offer({
            'name': ORYX.I18N.TBPMSupport.imp.name,
            'functionality': this.showImportDialog.bind(this),
            'group': ORYX.I18N.TBPMSupport.imp.group,
            'toolbarGroup': ORYX.I18N.TBPMSupport.toolbarGroup,
            'dropDownGroupIcon': ORYX.BASE_FILE_PATH + "images/import.png",
			'icon': ORYX.BASE_FILE_PATH + "images/page_white_picture.png",
            'description': ORYX.I18N.TBPMSupport.imp.desc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    /**
     * Opens a upload dialog.
     *
     */
    showImportDialog: function(successCallback){
        this.form = new Ext.form.FormPanel({
            baseCls: 'x-plain',
            labelWidth: 50,
            defaultType: 'textfield',
            fileUpload : true,
		  	enctype : 'multipart/form-data',
            items: [{
                text: ORYX.I18N.TBPMSupport.imp.selectFile,
                style: 'font-size:12px;margin-bottom:10px;display:block;',
                anchor: '100%',
                xtype: 'label'
            }, {
                fieldLabel: ORYX.I18N.TBPMSupport.imp.file,
                name: 'subject',
                inputType: 'file',
                style: 'margin-bottom:10px;display:block;width:95%',
                itemCls: 'ext_specific_window_overflow'
            }]
        });
        
        // Create the panel
        this.dialog = new Ext.Window({
            autoCreate: true,
            layout: 'fit',
            plain: true,
            bodyStyle: 'padding:5px;',
            title: ORYX.I18N.TBPMSupport.imp.name,
            height: 150,
            width: 500,
            modal: true,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: true,
            items: [this.form],
            buttons: [{
                text: ORYX.I18N.TBPMSupport.imp.btnImp,
                handler: this.uploadImage.bind(this)
            }, {
                text: ORYX.I18N.TBPMSupport.imp.btnClose,
                handler: function(){
                    this.dialog.close();
                }.bind(this)
            }]
        });
        
        this.dialog.on('hide', function(){
			this.dialog.destroy(true);
			delete this.dialog;
		}.bind(this));
        
        // Show the panel
        this.dialog.show();        
    },
    
    uploadImage: function(button, event) {    	
    	  	
    	var imageName = this.form.items.items[1].getValue().replace("png", "jpg");
    	
		var loadMask = new Ext.LoadMask(Ext.getBody(), {
		    msg: ORYX.I18N.TBPMSupport.imp.progress
		});
		loadMask.show();
    	
		this.form.form.submit({	
    				url: this.tbpmImportServletURL, 
    				clientValidation: true,
    				waitMsg:'Saving Data...',
    				method: "POST",
    				
    				success: function(form, action) {
						obj = Ext.util.JSON.decode(action.response.responseText);
						alert(object);
    			    	this.dialog.hide();
    					this.showConfirmDialog(imageName);
    					// Hide the waiting panel
    					loadMask.hide()  ;  					
    			    }.bind(this),
    			    
    			    // invokes failure handler even i case of successful response (no idea why)
    				failure: function(form, action){
    			    	this.dialog.hide();
    					this.showConfirmDialog(imageName, action.response.responseText);
    					// Hide the waiting panel
    					loadMask.hide()  ;
    			    	
    			    	//this.dialog.hide();
    			    	//loadMask.hide();
    					//Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.TBPMSupport.imp.impFailed);
    				}.bind(this)
    	});
        
    },  
    
    /*
     * show image with highlighted shapes
     * import model and image layer if image confirmed
     */
    showConfirmDialog: function(imageName, json){
    	
    	var confirmDialog = new Ext.Window({
    		autoCreate: true,
    		layout: 'fit',
    		width: 600,
    	    height: 500,
    	    bodyStyle: 'padding:5px;',
    	    autoScroll: true,
    	    modal: true,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: true,
    	    title: ORYX.I18N.TBPMSupport.imp.confirm,
    	    html: '<div style="width:100%;">' +
            			'<img src="'+ this.TMP_FOLDER + imageName + '" style="width:550px;"></img>'+
            		'</div>',
    	    
    	    buttons: [{
                text: ORYX.I18N.TBPMSupport.imp.btnImp,
                handler: function() {
    	    		confirmDialog.close();
    	    		this.processImport(imageName, json);
    	    	}.bind(this)
            }, {
                text: ORYX.I18N.TBPMSupport.imp.btnClose,
                handler: function(){
                    confirmDialog.close();
                }.bind(this)
            }]
    	});
    	
    	confirmDialog.show();
    },
    
    processImport: function(imageName, json){
    	
    	this.addImageLayer(imageName);
    	this.importShapes(json);
    	
    	// update the canvas
		this.facade.getCanvas().update();
    },
    
    /*
     * show transparent Layer with image
     * 
     */
    addImageLayer: function(imageName){  
		$(this.canvasId).style.background = "url(" + this.TMP_FOLDER + imageName + ") no-repeat scroll center center";
    },
    
    /*
     * generate detected shapes
     */
    importShapes: function(json){
    	this.facade.importJSON(json, true);
    }

   
});

/**
 * Copyright (c) 2008
 * Zhen Peng
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
	
ORYX.Plugins.BPELSupport = ORYX.Plugins.AbstractPlugin.extend({

	facade: undefined,

	dialogSupport: undefined,
	
	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		
		this.facade = facade;

		this.dialogSupport = new ORYX.Plugins.TransformationDownloadDialog();

	    this.facade.offer({
			'name':ORYX.I18N.BPELSupport.exp,
			'functionality': this.exportProcess.bind(this),
			'group': ORYX.I18N.BPELSupport.group,
			'icon': ORYX.BASE_FILE_PATH + "images/bpel_export_icon.png",
			'description': ORYX.I18N.BPELSupport.expDesc,
			'index': 0,
			'minShape': 0,
			'maxShape': 0
		});
			
        this.facade.offer({
			'name':ORYX.I18N.BPELSupport.imp,
			'functionality': this.importProcess.bind(this),
			'group': ORYX.I18N.BPELSupport.group,
			'icon': ORYX.BASE_FILE_PATH + "images/bpel_import_icon.png",
			'description': ORYX.I18N.BPELSupport.impDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0
		});
	},
	
	/***************************** export **********************************/
	
	exportProcess: function(){
	
		// raise loading enable event
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE
        });
            
		// asynchronously ...
        window.setTimeout((function(){
			
			// ... save synchronously
            this.exportSynchronously();
			
			// raise loading disable event.
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_LOADING_DISABLE
            });
			
        }).bind(this), 10);

		return true;
    },
    
    exportSynchronously: function() {

        var resource = location.href;
		
		
		try {
			
			var serialized_rdf = this.getRDFFromDOM();
			if (!serialized_rdf.startsWith("<?xml")) {
				serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;
			}
			  
			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.BPEL_EXPORT_URL, {
				method: 'POST',
				asynchronous: false,
				parameters: {
					resource: resource,
					data: serialized_rdf
				},
                onSuccess: function(response){
					this.displayResult(response.responseText);
                }.bind(this)
			});
                	
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
	 	}
    
	},
	
	
	/**
	 * Analyzes the result of the servlet call.
	 * 
	 * If an fault occured or the answer is undefined, the error is shown
	 * using a message dialog.
	 * 
	 * If the first result starts with "ParserError" the error is shown using an 
	 * error dialog. Otherwise the result is shown using the result dialog.
	 * 
	 * @param {Object} result - the result of the transformation servlet (JSON)
	 */
	displayResult: function(result) {
		this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});

		var resultString = '(' + result + ')';
		
		var resultObject;
		
		try {
			resultObject = eval(resultString);
		} catch (e1) {
			alert("Error during evaluation of result: " + e1 + "\r\n" + resultString);
		}
		
		if ((!resultObject.res) || (resultObject.res.length == 0)) {
			this.dialogSupport.openMessageDialog(ORYX.I18N.TransformationDownloadDialog.error,ORYX.I18N.TransformationDownloadDialog.noResult);
		} else if (resultObject.res[0].success == "false") {
			this.dialogSupport.openErrorDialog(resultObject.res[0].content);
		} else {
			var processes = new Array();
			for (var i = 0; i < resultObject.res.length; i++) {
				processes[i] = resultObject.res[i].content;
			}
			var data = this.buildTransData(processes);
			this.dialogSupport.openResultDialog(data);
		}
	},
	
	/**
	 * Builds up the data that will be shown in the result dialog of
	 * the BPEL transformation.
	 * For this purpose the process names are determined and
	 * it is checked if the process were generated
	 * successfully.
	 * 
	 * @param {String[]} processes The generated processes
	 */
	buildTransData: function(processes) {
		var data = [];
		
		for (var i = 0; i < processes.length; i++) {
			var name = this.dialogSupport.getProcessName(processes[i]);
			if (name == undefined) {
				name = "Process " + (i+1);
			}
			data[i] = [name, processes[i], this.dialogSupport.getResultInfo(processes[i])];
		}	
		
		return data;
	},

	/***************************** import **********************************/
	
	importProcess: function(){
		this.openUploadDialog ();
	},
	
	/**
	 * Opens a upload dialog.
	 * 
	 */
	openUploadDialog: function(){
		
		var form = new Ext.form.FormPanel({
			frame : 		true,
			bodyStyle:		'padding:5px;',
			defaultType : 	'textfield',
		  	labelAlign : 	'left',
		  	buttonAlign: 	'right',
		  	fileUpload : 	true,
		  	enctype : 		'multipart/form-data',
		  	items : [
		  	{
		    	text : 		ORYX.I18N.BPELSupport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
				xtype : 	'label'
		  	},{
		    	fieldLabel : 	ORYX.I18N.BPELSupport.file,
		    	inputType : 	'file',
				labelStyle :	'width:50px;',
				itemCls :		'ext_specific_window_overflow'
		  	}]
		});


		var displayPanel = new Ext.form.FormPanel({
			frame : 		true,
			bodyStyle:		'padding:5px;',
			defaultType : 	'textfield',
		  	labelAlign : 	'left',
		  	buttonAlign: 	'right',
		  	fileUpload : 	true,
		  	enctype : 		'multipart/form-data',
		  	items : [
		  	{
		    	text : 		ORYX.I18N.BPELSupport.content, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
				xtype : 	'label'
		  	}, {
	            xtype: 'textarea',
	            width: '160',
	            height: '350',
	            hideLabel: true,
	            anchor: '100% -63'
	        }]
		});
		
		var dialog = new Ext.Window({ 
			autoCreate:     true, 
			title: 		ORYX.I18N.BPELSupport.impPanel, 
			height: 	'auto', 
			width: 		'auto', 
			modal:		true,
			collapsible:false,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	false,
			items: [form, displayPanel],
			buttons:[
				{
					text:ORYX.I18N.BPELSupport.impBtn,
					handler: function(){
						
							
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.BPELSupport.progressImp});
						loadMask.show();
												
						form.form.submit({
							// TODO according to http://www.extjs.com/deploy/dev/docs/output/Ext.form.BasicForm.html
							//      modification of the accept header should work like that. In practice, however, it doesn't
							headers: {
								accept: "application/json, text/plain, text/html"
							},
				      		url: ORYX.PATH + '/bpelimporter',
				      		timeout: 6,
				      		success: function(f,a){
								
								dialog.hide();
								// Get the json string					
								var json = a.result;
								
								//alert(json);
								
								// Load the json to the editor
								this.facade.importJSON(json.content,true);
								
								// update the canvas
								this.facade.getCanvas().update();
								
								// Hide the waiting panel
								loadMask.hide();
								
				      		}.bind(this),
							failure: function(f,a){
								dialog.hide();
								loadMask.hide();
								Ext.MessageBox.show({
		           					title: ORYX.I18N.BPELSupport.error,
		          	 				msg: ORYX.I18N.BPELSupport.impFailed + a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
		           					buttons: Ext.MessageBox.OK,
		           					icon: Ext.MessageBox.ERROR
		       					});
				      		}.bind(this)
				  		});
					}.bind(this)
				},{
					text:ORYX.I18N.BPELSupport.close,
					handler:function(){
						dialog.hide();
					}.bind(this)
				}
			]
		});

		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		
		dialog.show();
	
		// Adds the change event handler to file upload filed 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt){
				var text = evt.target.files[0].getAsBinary();
				displayPanel.items.items[1].setValue( text );
			}, true)
	},
	
	loadERDF: function(erdfString){
								
		var parser = new DOMParser();			
		var doc    = parser.parseFromString(erdfString ,"text/xml");
		
		//alert(erdfString);
		this.facade.importERDF( doc );

	}

	
});
	
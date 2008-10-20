/**
 * Copyright (c) 2008
 * Matthias Weidlich
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

/**
 * Transforms a BPEL process into its BPMN representation.
 * 
 * 
 */
ORYX.Plugins.BPEL2BPMN = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;
		
		this.facade.offer({
			'name':"Transform BPEL into BPMN",
			'functionality': this.transform.bind(this),
			'group': "BPEL2BPMN",
			'icon': ORYX.PATH + "images/epc_export.png",
			'description': "Transform a BPEL process into its BPMN representation",
			'index': 1,
			'minShape': 0,
			'maxShape': 0});
			
	},

	
	/**
	 * Trigger the actual BPEL 2 BPMN transformation.
	 */
	transform: function(){
		this.openUploadDialog();
	},
	
	/**
	 * Opens a upload dialog.
	 * (adapted from the epcSupport plugin)
	 * 
	 */
	openUploadDialog: function(){
		
		var form = new Ext.form.FormPanel({
			frame : true,
			defaultType : 'textfield',
		 	waitMsgTarget : true,
		  	labelAlign : 'left',
		  	buttonAlign: 'right',
		  	fileUpload : true,
		  	enctype : 'multipart/form-data',
		  	items : [
		  	{
		    	fieldLabel : 'File',
		    	inputType : 'file',
				allowBlank: false
		  	}]
		});

		var submit =form.addButton({
			text:"Submit",
			handler: function()
			{
				form.form.submit({
		      		url: ORYX.PATH + '/bpel2bpmn',
		      		waitMsg: "Transforming...",
		      		success: function(f,a){
						dialog.hide();
						var resultString = '{' + a.result + '}';
						var resultObject = resultString.evalJSON();
						
						var eRDF = resultObject.content;
						var successfulValidation = resultObject.successValidation;
						var validationError = resultObject.validationError;
						
						eRDF = '<?xml version="1.0" encoding="utf-8"?><div>'+eRDF+'</div>';
						var parser	= new DOMParser();			
						
						this.facade.importERDF(parser.parseFromString(eRDF ,"text/xml"));
						
						this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_AUTOLAYOUT_LAYOUT});

						
		      		}.bind(this),
					failure: function(f,a){
						dialog.hide();
						Ext.MessageBox.show({
           					title: 'Error',
          	 				msg: a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
           					buttons: Ext.MessageBox.OK,
           					icon: Ext.MessageBox.ERROR
       					});
		      		}
		  		});
		  	}.bind(this)
		})

		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: 'Upload File', 
			height: 130, 
			width: 400, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			proxyDrag: true,
			resizable:false,
			items: [new Ext.form.Label({text: "Select a BPEL (.bpel) file and transform it to BPMN.", style: 'font-size:12px;'}),form]
		});
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.show();
	},


	

	
});
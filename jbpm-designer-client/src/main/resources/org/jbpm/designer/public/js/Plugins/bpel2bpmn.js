/**
 * Copyright (c) 2008 - 2009
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
			'name':ORYX.I18N.BPELSupport.transformBPELToBPMN,
			'functionality': this.transform.bind(this),
			'group': 			'Export',
            dropDownGroupIcon: ORYX.BASE_FILE_PATH + "images/import.png",
			'description': ORYX.I18N.BPELSupport.transformBPELToBPMN_desc,
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
			frame : false,
			defaultType : 'textfield',
		 	waitMsgTarget : true,
		  	labelAlign : 'left',
		  	buttonAlign: 'right',
		  	fileUpload : true,
		  	enctype : 'multipart/form-data',
		  	style: 'font-size:12px;',
		  	items : [
		  	{
		    	fieldLabel : ORYX.I18N.BPELSupport.file,
		    	inputType : 'file',
			  	style: 'font-size:12px;',
				allowBlank: false
		  	}]
		});
		
		var errorMsg = new Ext.Panel({style: 'font-size:12px;', autoScroll: true});
		
		var dialog;

		dialog = new Ext.Window({ 
			autoCreate: true, 
			title: ORYX.I18N.BPELSupport.uploadBPELFile,
			height: 240, 
			width: 400, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
		  	style: 'font-size:12px;',
			proxyDrag: true,
			resizable:true,
			items: [new Ext.form.Label({text: ORYX.I18N.BPELSupport.selectBPELFile, style: 'font-size:12px;'}),form, errorMsg],
			buttons:[{
				text: ORYX.I18N.BPELSupport.submit,
				handler: function()
				{
					form.form.submit({
			      		clientValidation: false,
						url: ORYX.PATH + '/bpel2bpmn',
			      		waitMsg: ORYX.I18N.BPELSupport.transforming,
			      		success: function(f,a){
							/*
							 * The XML that comes from the server is escaped. Therefore we need to replace the escape symbols once again.
							 */
							var myString = a.response.responseText.replace(/&lt;/g,'<').replace(/&gt;/g,'>');
							
							if (myString) {
							
								var resultObject = myString.evalJSON();
								
								var eRDF = resultObject.content;
								var successfulValidation = resultObject.successValidation;
								var validationError = resultObject.validationError;
								
								/*
								 * In case the BPEL file complied to the schema the dialog is hidden.
								 */
								if (successfulValidation) {
									dialog.hide();
								}
								else {
									/*
									 * In case the BPEL file did not comply to the schema the dialog remains open.
									 */
									errorMsg.body.dom.innerHTML = '<p style="background-color: transparent;">'+ ORYX.I18N.BPELSupport.noComply+' <br /> <br />'+ ORYX.I18N.BPELSupport.errorMessage+' ' + validationError + '</p>';
								}
								
								/*
								 * In all cases we try to import the resulting eRDF.
								 */
								eRDF = '<?xml version="1.0" encoding="utf-8"?><html xmlns="http://www.w3.org/1999/xhtml" xmlns:b3mn="http://b3mn.org/2007/b3mn" xmlns:ext="http://b3mn.org/2007/ext" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:atom="http://b3mn.org/2007/atom+xhtml"><head profile="http://purl.org/NET/erdf/profile"><link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" /><link rel="schema.dcTerms" href="http://purl.org/dc/terms/ " /><link rel="schema.b3mn" href="http://b3mn.org" /><link rel="schema.oryx" href="http://oryx-editor.org/" /><link rel="schema.raziel" href="http://raziel.org/" /><base href="http://localhost:8080/backend/poem/new" /></head><body>'
									+eRDF+'</body></html>';
								var parser	= new DOMParser();			
								this.facade.importERDF(parser.parseFromString(eRDF ,"text/xml"));
							}
							else {
								/*
								 * Something went totally wrong. No chance to recover.
								 */
								Ext.MessageBox.show({
		           					title: 'Error',
		          	 				msg: ORYX.I18N.BPELSupport.errorImporting,
		           					buttons: Ext.MessageBox.OK,
		           					icon: Ext.MessageBox.ERROR
		       					});
							}							
			      		}.bind(this),
						failure: function(f,a){
							dialog.hide();
							Ext.MessageBox.show({
	           					title: 'Error',
	          	 				msg: a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
	           					buttons: Ext.MessageBox.OK,
	           					icon: Ext.MessageBox.ERROR
	       					});
			      		}.bind(this)
			  		});
			  	}.bind(this)
			}]
		});
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.show();
	},
});
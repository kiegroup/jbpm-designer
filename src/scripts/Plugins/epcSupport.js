/**
 * Copyright (c) 2008
 * Stefan Krumnow
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
 * Supports EPCs by offering a syntax check and export and import ability..
 * 
 * 
 */
ORYX.Plugins.EPCSupport = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;
		
		this.facade.offer({
			'name':"Export EPC",
			'functionality': this.exportEPC.bind(this),
			'group': "epc",
			'icon': ORYX.PATH + "images/epc_export.png",
			'description': "Export diagram to EPML",
			'index': 1,
			'minShape': 0,
			'maxShape': 0});
			
//		this.facade.offer({
//			'name':"Import EPC",
//			'functionality': this.importEPC.bind(this),
//			'group': "epc",
//			'icon': ORYX.PATH + "images/epc_import.png",
//			'description': "Import an EPML file",
//			'index': 2,
//			'minShape': 0,
//			'maxShape': 0});

//		Syntax Check has been migrated to syntaxchecker.js-Framework		

//		this.facade.offer({
//			'name':"Check EPC's Syntax",
//			'functionality': this.checkEPC.bind(this),
//			'group': "epc",
//			'icon': ORYX.PATH + "images/epc_check.png",
//			'description': "Perfom an EPC syntax check",
//			'index': 1,
//			'minShape': 0,
//			'maxShape': 0});		
	},

	
	/**
	 * Imports an AML or EPML description
	 */
	importEPC: function(){
		this.openUploadDialog();
	},		

	
	/**
	 * Exports the diagram into an AML or EPML file
	 */
	exportEPC: function(){

		this.facade.raiseEvent({type:'loading.enable', text:'Exporting model'});
		var xmlSerializer = new XMLSerializer();

		
		// TODO: a Syntax Syntax-Check should be triggered, here.
		 
		// TODO: get process' name
		var resource = "Oryx-EPC";
		
		// Force to set all resource IDs
		var serializedDOM = DataManager.serializeDOM( this.facade );

		//add namespaces
		serializedDOM = '<?xml version="1.0" encoding="utf-8"?>' +
		'<html xmlns="http://www.w3.org/1999/xhtml" ' +
		'xmlns:b3mn="http://b3mn.org/2007/b3mn" ' +
		'xmlns:ext="http://b3mn.org/2007/ext" ' +
		'xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" ' +
		'xmlns:atom="http://b3mn.org/2007/atom+xhtml">' +
		'<head profile="http://purl.org/NET/erdf/profile">' +
		'<link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />' +
		'<link rel="schema.dcTerms" href="http://purl.org/dc/terms/ " />' +
		'<link rel="schema.b3mn" href="http://b3mn.org" />' +
		'<link rel="schema.oryx" href="http://oryx-editor.org/" />' +
		'<link rel="schema.raziel" href="http://raziel.org/" />' +
		'<base href="' +
		location.href.split("?")[0] +
		'" />' +
		'</head><body>' +
		serializedDOM +
		'<div id="generatedProcessInfos"><span class="oryx-id">' + resource + '</span>' + 
		'<span class="oryx-name">' + resource + '</span></div>' +
		'</body></html>';
		
		/*
		 * Transform eRDF -> RDF
		 */
		var erdf2rdfXslt = ORYX.PATH + "/lib/extract-rdf.xsl";

		var rdfResultString;
		rdfResult = this.transformString(serializedDOM, erdf2rdfXslt, true);
		if (rdfResult instanceof String) {
			rdfResultString = rdfResult;
			rdfResult = null;
		} else {
			rdfResultString = xmlSerializer.serializeToString(rdfResult);
			if (!rdfResultString.startsWith("<?xml")) {
				rdfResultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + rdfResultString;
			}
		}
		
		/*
		 * Transform RDF -> EPML
		 */
		var rdf2epmlXslt = ORYX.PATH + "/xslt/RDF2EPML.xslt";
		
		var epmlResult = this.transformDOM(rdfResult, rdf2epmlXslt, true);
		var epmlResultString;
		if (epmlResult instanceof String) {
			epmlResultString = epmlResult;
			epmlResult = null;
		} else {
			epmlResultString = xmlSerializer.serializeToString(epmlResult);
			if (!epmlResultString.startsWith("<?xml")) {
				epmlResultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + epmlResultString;
			}
		}
		
		/*
		 * Transform EPML -> AML
		 */

// 		Note: This is uncommented due to the fact, that the epml2amlXslt does not work properly
//
//		var epml2amlXslt = ORYX.PATH +  + "/xslt/EPML2AML_2.xslt";
//		
//		var amlResultString = this.transformString(epmlResultString, epml2amlXslt, false);
//		if (amlResultString.substr(0, 12) != "Parse Error:") {
//			amlResultString = '<?xml version="1.0" encoding="utf-8"?>\n' + 
//				'<!DOCTYPE AML SYSTEM "ARIS-Export.dtd" [\n	<!ENTITY LocaleId.DEde "1031">\n	<!ENTITY Codepage.DEde "1252">\n]>\n' + 
//				amlResultString;
//		}

		/*
		 * Show result
		 */
		
		this.facade.raiseEvent({type:'loading.disable'});
		
		// At the moment, only EPML is going to be returned.
		this.openDownloadWindow(resource + ".epml", epmlResultString);
    },
	
	/**
	 * Transforms the given string via xslt.
	 * 
	 * @param {String} string
	 * @param {String} xsltPath
	 * @param {Boolean} getDOM
	 */
	transformString: function(string_, xsltPath, getDOM){
		var parser = new DOMParser();
		var parsedDOM = parser.parseFromString(string_,"text/xml");	
		
		return this.transformDOM(parsedDOM, xsltPath, getDOM);
	},
	
	/**
	 * Transforms the given dom via xslt.
	 * 
	 * @param {Object} domContent
	 * @param {String} xsltPath
	 * @param {Boolean} getDOM
	 */
	transformDOM: function(domContent, xsltPath, getDOM){	
		if (domContent == null) {
			return new String("Parse Error: \nThe given dom content is null.");
		}
		var result;
		var resultString;
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		
		xsltProcessor.importStylesheet(xslRef);
		try {
			result = xsltProcessor.transformToDocument(domContent);
		} catch (error){
			return new String("Parse Error: "+error.name + "\n" + error.message);
		}
		if (getDOM){
			return result;
		}
		resultString = (new XMLSerializer()).serializeToString(result);
		return resultString;
	},

	/**
	 * Opens a upload dialog.
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
		    	inputType : 'file'
		  	}]
		});

		var submit =form.addButton({
			text:"Submit",
			handler: function()
			{
				form.form.submit({
		      		url: ORYX.PATH + '/epc-upload',
		      		waitMsg: "Importing...",
		      		success: function(f,a){
						dialog.hide();
						var erdf = a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}"));
		        		erdf = erdf.replace(/&lt;/g, "<");
						erdf = erdf.replace(/&gt;/g, ">");
						//var parser = new DOMParser();
						//var parsedErdf = parser.parseFromString('<?xml version="1.0" encoding="utf-8"?><html>'+erdf+'</html>',"text/xml");	
						alert(erdf);
						// TODO ..
		      		},
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
		  	}
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
			items: [new Ext.form.Label({text: "Select an EPML (.epml) file and import it.", style: 'font-size:12px;'}),form]
		});
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.show();
	},
	
	/**
	 * Creates a hidden form element to communicate parameter values
	 * to a php file.
	 * 
	 * @param {Object} name  The name of the hidden field
	 * @param {Object} value The value of the hidden field
	 */
	createHiddenElement: function(name, value) {
		var newElement = document.createElement("input");
		newElement.name=name;
		newElement.type="hidden";
		newElement.value = value;
		return newElement
	},
	
	/**
	 * Returns the file name to the given result-entry.
	 * 
	 * @param {String} entry.
	 */
	getFileName: function(entry) {
		var l = entry.length;
		if (l > 5){
			if (entry.substr(l-5, 5) == "(AML)"){
				return entry.substr(0, l-6);
			}
		}
		return entry
	},
	
	/**
	 * Opens a download window for downloading the given content.
	 * 
	 * Creates a submit form to communicate the contents to the 
	 * download.php file.
	 * 
	 * @param {Object} content The content to be downloaded. If it is a zip 
	 *                         file, then this should be an array of contents.
	 * @param {Object} zip     True, if it is a zip file, false otherwise
	 */
	openDownloadWindow: function(file, content) {
		var win = window.open("");
		if (win != null) {
			win.document.open();
			win.document.write("<html><body>");
			var submitForm = win.document.createElement("form");
			win.document.body.appendChild(submitForm);
			
			var file = this.getFileName(file);
			submitForm.appendChild( this.createHiddenElement("download", content));
			submitForm.appendChild( this.createHiddenElement("file", file));
			
			
			submitForm.method = "POST";
			win.document.write("</body></html>");
			win.document.close();
			submitForm.action= ORYX.PATH + "/download";
			submitForm.submit();
		}		
	},
	
	loadERDF: function(form){
		
		
		
		Ext.Ajax.request({
			method: "POST",
			url: ModelProperties.app.current_model.access.edit_uri,
			params: {
				subject: openid,
				predicate: predicate
			},
			success: function(response, options) {
				if (typeof success == typeof function(){}) {
					var new_access = Ext.util.JSON.decode(response.responseText);
					// TODO add response validation
					ModelProperties.app.current_model.access.access_rights.push(new_access);
					
					if (typeof success == typeof function(){}) {
						success(response, options)
					}
				}
			},
			
			failure: function(response, options) {
				if (typeof failure == typeof function(){}) {
					failure(response, options);
				}
			}
		})

	}
	
});

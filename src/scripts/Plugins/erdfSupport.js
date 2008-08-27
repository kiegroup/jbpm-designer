/**
 * Copyright (c) 2008
 * Willi Tscheschner
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
ORYX.Plugins.ERDFSupport = Clazz.extend({

	facade: undefined,
	
	ERDFServletURL: '/erdfsupport',

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		
		this.facade = facade;
			
			
		this.facade.offer({
			'name':				"Export from ERDF",
			'functionality': 	this.exportERDF.bind(this),
			'group': 			"erdf",
			'icon': 			ORYX.PATH + "images/epc_export.png",
			'description': 		"Export from ERDF",
			'index': 			0,
			'minShape': 		0,
			'maxShape': 		0
		});
					
		this.facade.offer({
			'name':				"Import from ERDF",
			'functionality': 	this.importERDF.bind(this),
			'group': 			"erdf",
			'icon': 			ORYX.PATH + "images/epc_import.png",
			'description': 		"Import from ERDF",
			'index': 			1,
			'minShape': 		0,
			'maxShape': 		0
		});

	},

	
	/**
	 * Imports an AML description
	 * 
	 */
	importERDF: function(){
		this._showImportDialog();
	},		

	
	/**
	 * Imports an AML description
	 * 
	 */
	exportERDF: function(){
		
		var s 	= DataManager.serializeDOM( this.facade );
		s		= s.gsub('><div', ">\n<div");		
		s		= '<?xml version="1.0" encoding="utf-8"?>\n<div class="processdata">\n' + s + "\n</div>"; 

		this.openXMLWindow( s );
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
			onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport.result )	
				}
				
			}.bind(this),
			
			onFailure		: function(transport) {

				if(failedcallback){
					
					failedcallback();
					
				} else {
					Ext.Msg.alert("Oryx", "Request in Import of ERDF failed.");
					ORYX.log.warn("Import ERDF failed: " + transport.responseText);	
				}
				
			}.bind(this)		
		});
		
		
		return suc;
							
	},


	loadERDF: function( erdfString, success, failed ){
		
		var s 	= erdfString;
		s 		= s.startsWith('<?xml') ? s : '<?xml version="1.0" encoding="utf-8"?>'+s+'';	
						
		var parser	= new DOMParser();			
		var doc 	=  parser.parseFromString( s ,"text/xml");
							
		if( doc.firstChild.tagName == "parsererror" ){

			Ext.MessageBox.show({
					title: 		'Error',
 					msg: 		"Error: An error while importing occurs! <br/>Please check error message: <br/><br/>" + doc.firstChild.textContent.escapeHTML(),
					buttons: 	Ext.MessageBox.OK,
					icon: 		Ext.MessageBox.ERROR
				});
																
			if(failed)
				failed();
				
		} else if( !this.hasStencilSet(doc) ){
			
			if(failed)
				failed();		
		
		} else {
			
			this.facade.importERDF( doc );
			
			if(success)
				success();
		
		}
	},

	hasStencilSet: function( doc ){
		
		var getElementsByClassNameFromDiv 	= function(doc, id){ return $A(doc.getElementsByTagName('div')).findAll(function(el){ return $A(el.attributes).any(function(attr){ return attr.nodeName == 'class' && attr.nodeValue == id }) })	}

		// Get Canvas Node
		var editorNode 		= getElementsByClassNameFromDiv( doc, '-oryx-canvas')[0];
		
		if( !editorNode ){
			this.throwWarning('The xml document has no Oryx canvas node included!');
			return false
		}
		
		var stencilSetNode 	= $A(editorNode.getElementsByTagName('a')).find(function(node){ return node.getAttribute('rel') == 'oryx-stencilset'});

		if( !stencilSetNode ){
			this.throwWarning('The Oryx canvas node has no stencil set definition included!');
			return false
		}
		
		var stencilSetUrl	= stencilSetNode.getAttribute('href').split("/")
		stencilSetUrl		= stencilSetUrl[stencilSetUrl.length-2] + "/" + stencilSetUrl[stencilSetUrl.length-1];
		
		var isLoaded = this.facade.getStencilSets().values().any(function(ss){ return ss.source().endsWith( stencilSetUrl ) })
		if( !isLoaded ){
			this.throwWarning('The given stencil set does not fit to the current editor!');
			return false
		}
				
		return true;
	},
	
	throwWarning: function( text ){
		Ext.MessageBox.show({
					title: 		'Oryx',
 					msg: 		text,
					buttons: 	Ext.MessageBox.OK,
					icon: 		Ext.MessageBox.WARNING
				});
	},
	
	/**
	 * Opens a new window that shows the given XML content.
	 * 
	 * @param {Object} content The XML content to be shown.
	 */
	openXMLWindow: function(content) {
		var win = window.open(
		   'data:application/xml,' + encodeURIComponent(
		     content
		   ),
		   '_blank', "resizable=yes,width=600,height=600,toolbar=0,scrollbars=yes"
		);
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
	            text : 		'Select an ERDF (.xml) file or type in the ERDF to import it!', 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
	            anchor:		'100%',
				xtype : 	'label' 
	        },{
	            fieldLabel: 'File',
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
			title: 		'Import ERDF', 
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
					text:'Import',
					handler:function(){
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Importing..."});
						loadMask.show();
						
						window.setTimeout(function(){
					
							
							var erdfString =  form.items.items[2].getValue();
							this.loadERDF(erdfString, function(){loadMask.hide();dialog.hide()}.bind(this), function(){loadMask.hide();}.bind(this))
														
														
							
						}.bind(this), 100);
			
					}.bind(this)
				},{
					text:'Close',
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
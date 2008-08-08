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
ORYX.Plugins.AMLSupport = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;
			
		this.facade.offer({
			'name':				"Import from AML",
			'functionality': 	this.importAML.bind(this),
			'group': 			"epc",
			'icon': 			ORYX.PATH + "images/epc_import.png",
			'description': 		"Import an AML file",
			'index': 			3,
			'minShape': 		0,
			'maxShape': 		0
		});

	
		this.AMLServletURL = '/amlsupport';
	},

	
	/**
	 * Imports an AML description
	 */
	importAML: function(){
		this.openUploadDialog();
	},		

	/**
	 * Opens a upload dialog.
	 * 
	 */
	openUploadDialog: function(){

		var form = new Ext.form.FormPanel({
			frame : true,
            bodyStyle: 'padding:5px',
			defaultType : 'textfield',
		 	waitMsgTarget : true,
		  	labelAlign : 'left',
		  	buttonAlign: 'right',
		  	fileUpload : true,
		  	enctype : 'multipart/form-data',
		  	items : [
		  	{
		    	text : 		'Select an AML (.aml) file to import it!', 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
				xtype : 	'label'
		  	},{
		    	fieldLabel : 	'File',
		    	inputType : 	'file',
				labelStyle :	'width:50px'
		  	}]
		});

		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: 		'Import AML-File', 
			height: 	'auto', 
			width: 		400, 
			modal:		true,
			collapsible:false,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	false,
			items: [form],
			buttons:[
				{
					text:'Import',
					handler:function(){
						
						form.form.submit({
				      						url: 		ORYX.PATH + this.AMLServletURL,
				      						waitMsg: 	"Importing...",
				      						success: 	function(f,a){
													dialog.hide();
													
													console.log(f,a)
													var erdf = a.result;
													//erdf = '<?xml version="1.0" encoding="utf-8"?><div>'+erdf+'</div>';	
													
													this.loadDiagrams( erdf );
													/*
													var erdf = a.result.content;
													erdf = erdf.replace(/&lt;/g, "<");
													erdf = erdf.replace(/&gt;/g, ">");
													erdf = '<?xml version="1.0" encoding="utf-8"?><div>'+erdf+'</div>';	
													this.loadContent(erdf);*/
				      							}.bind(this),
											failure: 	function(f,a){
													dialog.hide();
													
													console.log('Error',f,a)
													/*
													Ext.MessageBox.show({
						           						title: 		'Error',
						          	 					msg: 		'Error',//a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
						           						buttons: 	Ext.MessageBox.OK,
						           						icon: 		Ext.MessageBox.ERROR
						       						});*/
								      		}	
										});					
					}.bind(this)
				},{
					text:'Close',
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
	},
	
	loadDiagrams: function(erdf){

		var doc = this.parseToDoc( erdf );
		
		// get the serialiezed object for the first process data
		var serialized = this.parseToSerializeObjects( doc.firstChild );	
				
		this.importData( serialized );
		

	},

	/**
	 * Gives a div from xml with a given id
	 * 
	 * @param {Object} doc
	 * @param {Object} id
	 */
	getElementByIdFromDiv: function(doc, id){
		
		return $A(doc.getElementsByTagName('div')).find(function(el){return el.getAttribute("id")== id})
	
	},

	/**
	 * Give all divs with a given class name
	 * 
	 * @param {Object} doc
	 * @param {Object} id
	 */
	getElementByClassNameFromDiv: function(doc, id){

		return $A(doc.getElementsByTagName('div')).findAll(function(el){ return $A(el.attributes).any(function(attr){ return attr.nodeName == 'class' && attr.nodeValue == id }) })	

	},
	
	/**
	 * Parses the erdf string to an xml-document
	 * 
	 * @param {Object} erdfString
	 */
	parseToDoc: function( erdfString ){
		
		var parser	= new DOMParser();			
		
		return parser.parseFromString( erdfString ,"text/xml");

	},

	/**
	 * Parses one process model to the serialized form
	 * 
	 * @param {Object} oneProcessData
	 */
	parseToSerializeObjects: function( oneProcessData ){

		// Get the oryx-editor div
		var editorNode 	= this.getElementByClassNameFromDiv( oneProcessData, '-oryx-canvas')[0];

		// Get all ids from the canvas node for rendering
		var renderNodes = $A(editorNode.childNodes).collect(function(el){ return el.nodeName.toLowerCase() == "a" && el.getAttribute('rel') == 'oryx-render' ? el.getAttribute('href').slice(1) : null}).compact()
		// Collect all nodes from the ids
		renderNodes = renderNodes.collect(function(el){return this.getElementByIdFromDiv( oneProcessData, el)}.bind(this));
		
		// Function for extract all eRDF-Attributes and give them back as an Object
		var parseAttribute = function(node){
		    
			var res = {type: undefined, id: undefined ,serialize: [] }
			
			// Set the resource id
			if(node.getAttribute("id")){
				res.id = node.getAttribute("id");
			}
			
			// Set all attributes
		    $A(node.childNodes).each( function(node){ 
				if( node.nodeName.toLowerCase() == "span" && node.getAttribute('class')){
		            var name 	= node.getAttribute('class').split("-");
					var value 	= node.firstChild ? node.firstChild.nodeValue : '';
					
					res.serialize.push({name: name[1], prefix:  name[0], value: value})

					if( name[1] == "type" ){
						res.type = value;
					}

				} else if( node.nodeName.toLowerCase() == "a" && node.getAttribute('rel')){
		            var name 	= node.getAttribute('rel').split("-");
					var value 	= node.getAttribute('href');
					
					res.serialize.push({name: name[1], prefix:  name[0], value: value})
		        }
		    })
			
		    return res.type ? res : null ;
		}		
		
		// Collect all Attributes out of the Nodes
		return renderNodes.collect(function(el){return parseAttribute(el)}).compact();
		
		
	},
	
	importData: function( serialized ){
		
		var canvas  = this.facade.getCanvas();
		
		serialized.each(function(ser){

			// Try to create a new Shape
			try {
				// Create a new Stencil								
				var stencil = ORYX.Core.StencilSet.stencil( ser.type );
	
				// Create a new Shape
				var newShape = (stencil.type() == "node") ?
									new ORYX.Core.Node(
										{'eventHandlerCallback':this.facade.raiseEvent},
										stencil) :
									new ORYX.Core.Edge(
										{'eventHandlerCallback':this.facade.raiseEvent},
										stencil);
				
				// Set the resource id
				newShape.resourceId = ser.id;
				
				// Add the shape to the canvas
				canvas.add( newShape );
								
				// Add to new shapes
				ser['shape'] = newShape;				
			} catch(e) {
				ORYX.Log.warn("LoadingContent: Stencil could not create.");
				//return;
			}
					
		}.bind(this))
		
		console.log( serialized )
		
		// Deserialize the properties from the shapes
		serialized.each(
			function(pair){
				pair.shape.deserialize(pair.serialize);
			}
		);
		
		// Update the canvas
		canvas.update();
				
	},

	
	/**
	 * 
	 * THE FOLLOWING METHODS ARE A TEMPORARY SOLUTION
	 * 
	 * THEY WILL BE REMOVED WHEN A GENERAL ERDF IMPORTER IS IMPLEMENTED
	 * 
	 */
	
	/**
	 * Loads the imported string into the oryx
	 * 
	 * @param {Object} content
	 */
	loadContent: function(content){
		
		var epcs = this.parseToObject( content );
		
		epcs = epcs.collect(function(epc){ return {epcData: epc, stencil: ORYX.Core.StencilSet.stencil(epc.type)}})
		console.log(epcs)
		var nodes = epcs.findAll(function(epc){ return epc.stencil.type() == "node" });
		var edges = epcs.findAll(function(epc){ return epc.stencil.type() == "edge" });
		
		nodes = nodes.collect(function(epc){
									
			if( !epc.stencil){
				throw $break;
			}
			
			// Create a new Shape
			var newShape = 	new ORYX.Core.Node( {'eventHandlerCallback':this.facade.raiseEvent }, epc.stencil );
											
			// Add the shape to the canvas
			this.facade.getCanvas().add(newShape);
		
			if( epc.epcData.bounds ){
				// Set the bounds
				newShape.bounds.centerMoveTo( epc.epcData.bounds.center )
			}
			
			for (var key in epc.epcData){
				if (key != "bounds" && key != "id" && key != "type" && key != "outgoing" && key != "parent" && key != "dockers"){
					newShape.properties['oryx-'+key] = epc.epcData[key];
				}
			} 
			return {epcData: epc.epcData, stencil: epc.stencil, shape: newShape};
			
		}.bind(this));
		
	
	
		edges.each(function(epc){
									
			if( !epc.stencil){
				throw $break;
			}
			
			// Create a new Shape
			var newShape = new ORYX.Core.Edge({'eventHandlerCallback':this.facade.raiseEvent }, epc.stencil);

			// Add the shape to the canvas
			this.facade.getCanvas().add(newShape);
		
			if( epc.epcData.bounds ){
				// Set the bounds
				newShape.bounds.centerMoveTo( epc.epcData.bounds.center )
			}
			

			var from0 	= nodes.find(function(node){ return node.epcData.outgoing && node.epcData.outgoing.any(function(out){ return out.slice(1) == epc.epcData.id }) });
			if (from0)
				var from = from0.shape;
			var to0	= nodes.find(function(node){ return epc.epcData.outgoing && node.epcData.id == epc.epcData.outgoing[0].slice(1) });
			if (to0)
				var to = to0.shape;
			// Set the docker
			if( from ){
				newShape.dockers.first().setDockedShape( from );
				newShape.dockers.first().setReferencePoint({x: from.bounds.width() / 2.0, y: from.bounds.height() / 2.0});
				newShape.dockers.first().update();
			}
			if( to ){
				newShape.dockers.last().setDockedShape( to );
				newShape.dockers.last().setReferencePoint({x: to.bounds.width() / 2.0, y: to.bounds.height() / 2.0});
				newShape.dockers.last().update();
			}
			
			for (var key in epc.epcData){
				if (key != "bounds" && key != "id" && key != "type" && key != "outgoing" && key != "parent" && key != "dockers"){
					newShape.properties['oryx-'+key] = epc.epcData[key];
				}
			}
			
		}.bind(this));
				
		this.facade.getCanvas().update();
		
	},
	/**
	 * Parsed the given ERDF-String to a Array with the individual
	 * EPC-Objects
	 * 
	 * @param {Object} erdfString
	 */
	parseToObject: function ( erdfString ){

		var parser	= new DOMParser();			
		var doc		= parser.parseFromString( erdfString ,"text/xml");

		var getElementByIdFromDiv = function(id){ return $A(doc.getElementsByTagName('div')).find(function(el){return el.getAttribute("id")== id})}
		var getElementByClassNameFromDiv = function(id){ return $A(doc.getElementsByTagName('div')).findAll(function(el){ return $A(el.attributes).any(function(attr){ return attr.nodeName == 'class' && attr.nodeValue == id }) })}

		// Get the oryx-editor div
		var editorNode 	= getElementByClassNameFromDiv('-oryx-canvas')[0];
		editorNode 		= editorNode ? editorNode : getElementByIdFromDiv('oryxcanvas');
		editorNode 		= editorNode ? editorNode : getElementByIdFromDiv('oryx-canvas123');
		
		//console.log(doc, erdfString, editorNode)

		//editorNode = editorNode[0];

		// Get all ids from the canvas node for rendering
		var renderNodes = $A(editorNode.childNodes).collect(function(el){ return el.nodeName.toLowerCase() == "a" && el.getAttribute('rel') == 'oryx-render' ? el.getAttribute('href').slice(1) : null}).compact()
		// Collect all nodes from the ids
		renderNodes = renderNodes.collect(function(el){return getElementByIdFromDiv(el)});

		// Function for extract all eRDF-Attributes and give them back as an Object
		var parseAttribute = function(node){
		    var res = {}
			// Set the resource id
			if(node.getAttribute("id")){
				res["id"] = node.getAttribute("id");
			}
			
			// Set all attributes
		    $A(node.childNodes).each( function(node){ 
				if( node.nodeName.toLowerCase() == "span" && node.getAttribute('class')){
		            var key = node.getAttribute('class').slice(5);
					res[key] = node.firstChild ? node.firstChild.nodeValue : '';
		        	if( key == "bounds" ){
						var ba = $A(res[key].split(",")).collect(function(el){return Number(el)})
						res[key] = {a:{x:ba[0], y:ba[1]},b:{x:ba[2], y:ba[3]},center:{x:ba[0]+((ba[2]-ba[0])/2),y:ba[1]+((ba[3]-ba[1])/2)}}
					}
				} else if( node.nodeName.toLowerCase() == "a" && node.getAttribute('rel')){
		            var key = node.getAttribute('rel').split("-")[1];
					if( !res[key] ){
						res[key] = [];
					}
					
		            res[key].push( node.getAttribute('href') )
		        }
		    })
		    return res
		}

		// Collect all Attributes out of the Nodes
		return renderNodes.collect(function(el){return parseAttribute(el)});
				
	},

	
	/**
	 * 
	 * @param {Object} message
	 */
	throwErrorMessage: function(message){
		Ext.Msg.alert( 'Oryx', message )
	},		
	
});
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
	 * 
	 */
	importAML: function(){
		this._showUploadDialog( this.loadDiagrams.bind(this) );
	},		

	
	/**
	 * Shows all included diagrams and imports them
	 * 
	 */	
	loadDiagrams: function(erdf){

		// Get the dom-structure
		var doc 	= this.parseToDoc( erdf );
		
		// Get the several process diagrams
		var values 	= $A(doc.firstChild.childNodes).collect(function(node){ return {title: this.getChildNodesByClassName( node.firstChild, 'oryx-title')[0].textContent, data: node}}.bind(this))
		
		this._showPanel(values, function(result){

			if( result.length > 1 ){
	
				var requestsSuccessfull = true;
	
				var loadedDiagrams = [];
				
				// Generate for every diagram a new url
				result.each(function(item){
		
					// Set url, dummy data, and params for the request, to get a new url
					var url 		= '/backend/poem' + ORYX.CONFIG.ORYX_NEW_URL + "?stencilset=/stencilsets/epc/epc.json"; 
					var dummyData 	= '<div class="processdata"><div class="-oryx-canvas" id="oryx-canvas123" style="display: none; width:1200px; height:600px;"><a href="/stencilsets/epc/epc.json" rel="oryx-stencilset"></a><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span></div></div>';
					var dummySVG 	= '<svg/>';
					var params 		= { data: dummyData, svg: dummySVG, title: item.name, summary: "", type: "http://b3mn.org/stencilset/epc#" };
		
					// Send the request
					requestsSuccessfull = this.sendRequest( url, params, function( transport ){ 
								
							var loc = transport.getResponseHeader('location');
							var id 	= this.getNodesByClassName( item.data, "div", "-oryx-canvas" )[0].getAttribute("id");
						
							loadedDiagrams.push({name: item.name, data:item.data, url: loc, id: id});
							
						}.bind(this));				
					
					// If an error during the reqest occurs, return
					if( !requestsSuccessfull ){ throw $break }
					
				}.bind(this));

				// If an error during the reqest occurs, return
				if( !requestsSuccessfull ){ return }
					
									
				// Replace all IDs within every process diagrams with the new url
				// First, find all 'oryx-uriref' spans
				var allURIRefs = loadedDiagrams.collect(function(item){ return $A(this.getNodesByClassName( refItem.data, "span", "oryx-refuri" )) }.bind(this)).flatten()
				// Second, replace it, if there is a url for it, otherwise, delete the link
				allURIRefs.each(function(uriRef){
					
					if( uriRef.textContent.length == 0 ){ return }
					
					var findURL = loadedDiagrams.find(function(item){ return uriRef.textContent == item.id })
					
					uriRef.textContent = findURL ? findURL.url : "" ;					
					
				})

				
				
				// Send all diagrams to the server
				loadedDiagrams.each(function(item){
	
					// Get the URL
					var url 		= item.url; 
					// Define the svg
					var dummySVG 	= '<svg/>';
					// Get the data
					var data		= DataManager.serialize(item.data);
					data 			= "<div " + data.slice(data.search("class"));
					// Set the parameter for the request
					var params 		= { data: data, svg: dummySVG };
					
					// Send the request
					requestsSuccessfull = this.sendRequest( url, params );
					
					
					// If an error during the reqest occurs, return
					if( !requestsSuccessfull ){ throw $break }
									
				}.bind(this));

				// If an error during the reqest occurs, return
				if( !requestsSuccessfull ){ return }
								
				// Show the results	
				this._showResultPanel( loadedDiagrams.collect(function(item){ return {name: item.name, url: item.url } }) );	
					
			} else {

				// get the serialiezed object for the first process data
				var serialized = this.parseToSerializeObjects( result[0].data );	
		
				// Import the shapes out of the serialization		
				var shapes = this.importData( serialized );
				
				//this.facade.setSelection( shapes );
							
			}
			
			

						
		}.bind(this))

	},
	
	
	/**
	 * 
	 * 
	 * @param {Object} url
	 * @param {Object} params
	 * @param {Object} successcallback
	 */
	sendRequest: function( url, params, successcallback ){

		var suc = false;

		new Ajax.Request(url, {
            method			: 'POST',
            asynchronous	: false,
            parameters		: params,
			onSuccess		: function(transport) {
				
				suc = true;
				
				if(successcallback){
					successcallback( transport )	
				}
				
			}.bind(this),
			
			onFailure		: function(transport) {

				Ext.Msg.alert("Oryx", "Request in Import of AML failed.");
				ORYX.log.warn("Import AML failed: " + transport.responseText);
				
			}.bind(this),
			
			on403			: function(transport) {

				Ext.Msg.alert("Oryx", "You have no rights to import multiple EPC-Diagrams.");
				ORYX.log.warn("Import AML failed: " + transport.responseText);
				
			}.bind(this)		
		});
		
		
		return suc;
							
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
	getElementsByClassNameFromDiv: function(doc, id){

		return $A(doc.getElementsByTagName('div')).findAll(function(el){ return $A(el.attributes).any(function(attr){ return attr.nodeName == 'class' && attr.nodeValue == id }) })	

	},

	/**
	 * Give all child nodes with the given class name
	 * 
	 * @param {Object} doc
	 * @param {Object} id
	 */
	getChildNodesByClassName: function(doc, id){

		return $A(doc.childNodes).findAll(function(el){ return $A(el.attributes).any(function(attr){ return attr.nodeName == 'class' && attr.nodeValue == id }) })	

	},

	/**
	 * Give all child nodes with the given class name
	 * 
	 * @param {Object} doc
	 * @param {Object} id
	 */
	getNodesByClassName: function(doc, tagName, className){

		return $A(doc.getElementsByTagName( tagName )).findAll(function(el){ return $A(el.attributes).any(function(attr){ return attr.nodeName == 'class' && attr.nodeValue == className }) })	

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
		var editorNode 	= this.getElementsByClassNameFromDiv( oneProcessData, '-oryx-canvas')[0];

		// Get all ids from the canvas node for rendering
		var renderNodes = $A(editorNode.childNodes).collect(function(el){ return el.nodeName.toLowerCase() == "a" && el.getAttribute('rel') == 'oryx-render' ? el.getAttribute('href').slice(1) : null}).compact()
		
		// Collect all nodes from the ids
		renderNodes = renderNodes.collect(function(el){return this.getElementByIdFromDiv( oneProcessData, el)}.bind(this));
		renderNodes.push(editorNode);
	
		// Function for extract all eRDF-Attributes and give them back as an Object
		var parseAttribute = function(node){
		    
			var res = {type: undefined, id: undefined ,serialize: [] }
			
			// Set the resource id
			if(node.getAttribute("id")){
				res.id = node.getAttribute("id");
			}

			// If the node is the canvas simply
			// set already the canvas as shape 
			if(node.getAttribute("class") == "-oryx-canvas"){
				res['shape'] = this.facade.getCanvas();
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
		}.bind(this)		
		
		// Collect all Attributes out of the Nodes
		return renderNodes.collect(function(el){return parseAttribute(el)}.bind(this)).compact();
		
		
	},
	
	importData: function( serialized ){
		
		var canvas  = this.facade.getCanvas();
		
		serialized.each(function(ser){

			// If the shape is the canvas, continue
			if( ser.shape && ser.shape instanceof ORYX.Core.Canvas){
				return;
			}

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
		
				
		// Deserialize the properties from the shapes
		serialized.each(
			function(pair){
				pair.shape.deserialize(pair.serialize);
			}
		);
		
		// Update the canvas
		canvas.update();
		
		return serialized.collect(function(item) { return item.shape })
				
	},

	/**
	 * Opens a upload dialog.
	 * 
	 */
	_showUploadDialog: function( successCallback ){

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
		    	text : 		'Select an AML (.aml) file to import it!', 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
				xtype : 	'label'
		  	},{
		    	fieldLabel : 	'File',
		    	inputType : 	'file',
				labelStyle :	'width:50px;',
				itemCls :		'ext_specific_window_overflow'
		  	}]
		});

		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: 		'Import AML-File', 
			height: 	'auto', 
			width: 		'auto', 
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
						
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Get diagrams..."});
						loadMask.show();

						form.form.submit({
				      						url: 		ORYX.PATH + this.AMLServletURL,
				      						success: 	function(f,a){
													
													loadMask.hide();
													dialog.hide();	
													successCallback( a.result );

				      							}.bind(this),
											failure: 	function(f,a){
													
													loadMask.hide();
													dialog.hide();	
													
													Ext.MessageBox.show({
						           						title: 		'Error',
						          	 					msg: 		a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
						           						buttons: 	Ext.MessageBox.OK,
						           						icon: 		Ext.MessageBox.ERROR
						       						});
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
	
    _showPanel: function(values, successCallback){
    
							
        // Extract the data
        var data = [];
        values.each(function(value){
            data.push([ value.title, value.data ])
        });
        
        // Create a new Selection Model
        var sm = new Ext.grid.CheckboxSelectionModel({ 
			header			:'',
			//singleSelect	:true
		});
        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
			//ddGroup          	: 'gridPanel',
			//enableDragDrop   	: true,
			//cls				: 'ext_specialize_gridPanel_aml',
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['title']
            }),
            cm: new Ext.grid.ColumnModel([sm, {
                header: "Title",
                width: 260,
                sortable: true,
                dataIndex: 'title'
            }, ]),
            sm: sm,
            frame: true,
            width: 300,
			height:'auto',
            iconCls: 'icon-grid',
			//draggable: true
        });
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype	: 'label',
                text	: 'Select the diagram(s) you want to import! <br/> If one model is selected, it will be imported in the current editor, if more than one is selected, those models will directly be stored in the repository.',
                style	: 'margin:5px;display:block'
            }, grid],
			height:'auto',
            frame: true
        })
        
        // Create a new Window
        var extWindow = new Ext.Window({
            width: 327,
			height:'auto',
            title: 'Oryx',
            floating: true,
            shim: true,
            modal: true,
            resizable: false,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: "Import",
                handler: function(){

					var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Importing..."});
					loadMask.show();
							
                    var selectionModel = grid.getSelectionModel();
                    var result = selectionModel.selections.items.collect(function(item){
                        return {name: item.json[0], data: item.json[1]};
                    })
                    extWindow.close();
                
					window.setTimeout( function(){
						
						successCallback(result);
						loadMask.hide();
						
					}.bind(this), 100);		

										
                }.bind(this)
            }, {
                text: "Cancel",
                handler: function(){
                    extWindow.close();
                }.bind(this)
            }]			
        })
        
        // Show the window
        extWindow.show();
        
    },
	
    _showResultPanel: function(values){
    
							
        // Extract the data
        var data = [];
        values.each(function(value){
            data.push([ value.name, '<a href="' + value.url + '" target="_blank">' + value.url + '</a>' ])
        });
        

        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['name', 'url' ]
            }),
            cm: new Ext.grid.ColumnModel([{
                header: "Name",
                width: 260,
                sortable: true,
                dataIndex: 'name'
            }, {
                header: "URL",
                width: 300,
                sortable: true,
                dataIndex: 'url'
            }]),
            frame: true,
            width: 500,
			height:'auto',
            iconCls: 'icon-grid'
        });
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: 'All imported diagrams!',
                style: 'margin:5px;display:block'
            }, grid],
			height:'auto',
            frame: true
        })
        
        // Create a new Window
        var extWindow2 = new Ext.Window({
            width		: 'auto',
            title		: 'Oryx',
            floating	: true,
            shim		: true,
            modal		: true,
            resizable	: false,
            autoHeight	: true,
            items: [panel],
            buttons: [{
                text: "Ok",
                handler: function(){

					extWindow2.close()	
									
                }.bind(this)
            }]			
        })
        
        // Show the window
        extWindow2.show();
        
    },	
	/**
	 * 
	 * @param {Object} message
	 */
	throwErrorMessage: function(message){
		Ext.Msg.alert( 'Oryx', message )
	},		
	
});
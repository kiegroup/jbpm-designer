/**
 * Copyright (c) 2008-2009, Steffen Ryll
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

ORYX.Plugins.QueryEvaluator = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.QueryEvaluator.name,
            'functionality': this.showOverlay.bind(this),
            'group': ORYX.I18N.QueryEvaluator.group,
            'icon': ORYX.PATH + "images/xforms_export.png",
            'description': ORYX.I18N.QueryEvaluator.desc,
            'index': 0,
			'toggle': true,
            'minShape': 0,
            'maxShape': 0
        });
		
    },
    
	showOverlay: function(button, pressed){

		if (!pressed) {
			
/*			this.raisedEventIds.each(function(id){
				this.facade.raiseEvent({
						type: 	ORYX.CONFIG.EVENT_OVERLAY_HIDE,
						id: 	id
					});
			}.bind(this))
*/
			this.raisedEventIds = [];
			this.active 		= !this.active;
			
			return;
		} 
		
		var options = {
			command : 'undef'
		}
		
		var optionsPopup = new Ext.Window({
			layout      : 'fit',
            width       : 500,
            height      : 350,
            closable	: true,
            plain       : true,
			modal		: true,
			id			: 'optionsPopup',
			
			buttons: [{
				text	: 'Submit',
				handler	: function(){
					// set options
					options = formPanel.getForm().getValues(false);
					
					optionsPopup.close();
					this.issueQuery(options);
				}.bind(this)
			}, {
                text     : 'Abort',
                handler  : function(){
                    optionsPopup.close();
                }.bind(this)
            }]

		})
		
		var modelIdField = new Ext.form.TextField({
			fieldLabel	: 'Model ID',
			name		: 'modelID',
			grow		: true,
//			hideLabel	: true
		});
		modelIdField.hide();
		
		var checkListener = function(field, checked){
			// keep checked states of all relevant fields in this array 
			if (!this.fieldStates) {
				this.fieldStates = [];
			}
			var found = false;
			var mustShowField = false;
			var i, f;
			for (i = 0; i < this.fieldStates.length; i++){
				f =  this.fieldStates[i];
				// update field entry
				if (f.field === field) {
					found = true;
					f.checked = checked;
				}
				// and at the same time check whether at least one field is checked
				mustShowField = mustShowField || f.checked;
			}
			if (!found) {
				// was not used before, so add to array
				this.fieldStates.push({
					field	: field,
					checked	: checked
				});
				mustShowField = true;
			}
			// change field visibility, if necessary
			if (mustShowField){
				modelIdField.show();
			} else {
				modelIdField.hide();
			}
		}
		
		var formPanel = new Ext.form.FormPanel({
			frame		: true,
			title		: 'Query options',
			bodyStyle	: 'padding:0 10px 0;',
			items		: [{
				// create a radio button group
				xtype		: 'fieldset',
            	autoHeight	: true,
				columns		: 1,
				allowBlank	: false,
				defaultType	: 'radio',
				items		: [
                    {
						boxLabel	: 'Process query', 
						fieldLabel	: 'Query Type', 
						name		: 'command', 
						inputValue	: 'processQuery', 
						checked: true},
					{
						boxLabel	: 'Test whether query matches specific model', 
						labelSeparator: '', 
						name		: 'command', 
						inputValue	: 'testQueryAgainstModel', 
						listeners	: {
							'check': checkListener.bind(this)
						} 
					},
                    {
						boxLabel	: 'Run query against specific model', 
						labelSeparator: '',
						name		: 'command',
						inputValue	: 'runQueryAgainstModel',
						listeners	: {
							'check': checkListener.bind(this)
						}
					},
                    {
						boxLabel	: 'Process MultiQuery', 
						labelSeparator: '', 
						name		: 'command', 
						inputValue	: 'processMultiQuery'},
					{
						xtype		: 'checkbox',
						fieldLabel	: 'Stop after first match in a model was found',
						name		: 'stopAtFirstMatch',
						checked		: true,
					}
                ]
			}]
		});
		formPanel.add(modelIdField);
		
		optionsPopup.add(formPanel);
		optionsPopup.show();
		
		button.toggle();
	},
	
	issueQuery : function(options){
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
		'</body></html>';

		//convert to RDF
		var parser = new DOMParser();
		var parsedDOM = parser.parseFromString(serializedDOM, "text/xml");
		var xsltPath = ORYX.PATH + "lib/extract-rdf.xsl";
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		xsltProcessor.importStylesheet(xslRef);
		try {
			var rdf = xsltProcessor.transformToDocument(parsedDOM);
			var serialized_rdf = (new XMLSerializer()).serializeToString(rdf);
//			serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;

			this.facade.raiseEvent({
	            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
				text: "Waiting for server"  //ORYX.I18N.Save.saving
	        });
			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.QUERYEVAL_URL, {
				method: 'POST',
				asynchronous: true,
				parameters: {
					resource	: location.href,
					command		: options.command,
					modelID		: options.modelID,
					stopAtFirstMatch: options.stopAtFirstMatch,
					data		: serialized_rdf
				},
                onSuccess: function(response){
                    this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_DISABLE
					});
					
					var respXML = response.responseXML;
                    var root = respXML.firstChild;
                    var processList = new Array();
                    var nodecnt, graph;
                    var pchildren = root.getElementsByTagName("ProcessGraph");
                    
                    // puts all matching process models into array processList with model ID 
                    // and model elements
					for (nodecnt = 0; nodecnt < pchildren.length; nodecnt++) {
                        graph = pchildren.item(nodecnt);
                        var graphID = graph.getAttributeNode("modelID").nodeValue;
                        processList.push({
							id 			: graphID,
							elements 	: this.processResultGraph(graph),
							metadata	: ''
						});
                        
                    }
					this.processProcessList(processList);
                }.bind(this),
				
				onFailure: function(response){
					this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_DISABLE
					});
					Ext.Msg.alert("Oryx", "Server encountered an error (" + response.statusText + ").\n"
						+ response.responseText);
				}.bind(this)
			});
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert("Oryx", error);
	 	}

	},
	
	raiseOverlay: function(shape, errorMsg) {
		
		var id = "queryeval." + this.raisedEventIds.length;
		
		var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null ,
			['path', {
				"title":errorMsg, "stroke-width": 5.0, "stroke":"red", "d":  "M20,-5 L5,-20 M5,-5 L20,-20", "line-captions": "round"
				}]);

/*		this.facade.raiseEvent({
			type: 			ORYX.CONFIG.EVENT_OVERLAY_SHOW,
			id: 			id,
			shapes: 		[shape],
			node:			cross,
			nodePosition:	shape instanceof ORYX.Core.Edge ? "START" : "NW"
		});		
*/		
		this.raisedEventIds.push(id);
		
		return cross;		
	},
	
    processResultGraph: function(xmlNode){
        var graphElements = new Array();
		
		for (var k = 0; k < xmlNode.childNodes.length; k++) {
            var node = xmlNode.childNodes.item(k);
            if (!(node instanceof Text)) {
                if (node.hasAttribute("id")) { // it is a node
					graphElements.push({
						nodeType : node.nodeName,
						nodeId : node.getAttributeNode("id").nodeValue
					});
					
				} else if ((node.hasAttribute("from"))
						&& node.hasAttribute("to")) { // it is an edge
					graphElements.push({
						edgeType : node.nodeName,
						from : node.getAttributeNode("from").nodeValue,
						to : node.getAttributeNode("to").nodeValue
					});
				}
            }
        }
		return graphElements;
    },
	
	/**
	 * 
	 * @param {Array} processList; 
	 * 		elements' fields: id location identifier for process
	 * 						  elements array of graph nodes/edges
	 */
	processProcessList: function(processList){
		if(processList.length == 0) {
			Ext.Msg.alert("Oryx", "Found no matching processes!");
			return;
		} 
		
		// load process model meta data
		processList.each(this.getModelMetaData.bind(this));
		
		// transform array of objects into array of arrays
		var data = [];
		processList.each(function( pair ){
/*			var stencilset = pair.value.type;
			// Try to display stencilset title instead of uri
			this.facade.modelCache.getModelTypes().each(function(type){
				if (stencilset == type.namespace) {
					stencilset = type.title;
					return;
				}
			}.bind(this));
*/			
			data.push( [ pair.id, pair.metadata.thumbnailUri + "?" + Math.random(), unescape(pair.metadata.title), '' /*stencilset*/, 'Unknown' ] )
		}.bind(this));

		
		// following is mostly UI logic
		var myProcsPopup = new Ext.Window({
			layout      : 'fit',
            width       : 500,
            height      : 300,
            closable	: true,
            plain       : true,
			modal		: true,
			id			: 'procResPopup',
			
			buttons: [{
                text     : 'Close',
                handler  : function(){
                    myProcsPopup.close();
                }
            }]

		})
		
		var tableModel = new Ext.data.SimpleStore({
			fields: [
				{name: 'id'}, //, type: 'string', mapping: 'metadata.id'},
				{name: 'icon'}, //, mapping: 'metadata.icon'},
				{name: 'title'}, //, mapping: 'metadata.title'},
				{name: 'type'}, //, mapping: 'metadata.type'},
				{name: 'author'}, //, mapping: 'metadata.author'},
//				{name: 'elements', type: 'auto', mapping: 'elements'}
			],
			data : data
		});
//		tableModel.loadData(processList);
		
/*		var iconPanel = new Ext.grid.GridPanel({
			store:	tableModel,
			columns: [
				{id: 'id', header: "ID", width: 360, dataIndex: 'id'},
				{header: "Elements", width: 300, dataIndex: 'elements'}
			],
			stripeRows: true,
	        autoExpandColumn: 'id',
	        height:350,
	        width:600,
	        title:'Array Grid'
		}); */
		var iconPanel = new Ext.Panel({
			border	: false,
	        items	: new this.dataGridPanel({store: tableModel //, listeners:{click:this._onSelectionChange.bind(this), dblclick:this._onDblClick.bind(this)}
			})
	    });
		
		// grid.getSelectionModel().selectFirstRow();
		

		myProcsPopup.add(iconPanel);
		// iconPanel.show();
		
		myProcsPopup.show();
	},
	
	getModelMetaData : function(processEntry) {
		var metaUri = processEntry.id.replace(/\/rdf$/, '/meta');
		new Ajax.Request(metaUri, 
			 {
				method			: "get",
				asynchronous 	: false,
				onSuccess		: function(transport) {
					processEntry.metadata = transport.responseText.evalJSON();
				}.bind(this),
				onFailure		: function() {
					alert("Error loading model meta data.")
				}.bind(this)
			});
	},
	
	dataGridPanel : Ext.extend(Ext.DataView, {
		multiSelect		: true,
		//simpleSelect	: true, 
	    cls				: 'repository_iconview',
	    itemSelector	: 'dd',
	    overClass		: 'over',
		selectedClass	: 'selected',
	    tpl : new Ext.XTemplate(
	        '<div>',
				'<dl>',
	            '<tpl for=".">',
					'<dd>',
					'<div class="image"><img src="{icon}" title="{title}"/></div>',
		            '<div><span class="title" title="{[ values.title.length + (values.type.length*0.8) > 30 ? values.title : "" ]}">{[ values.title.truncate(30 - (values.type.length*0.8)) ]}</span><span class="author" unselectable="on">({type})</span></div>',
		            '<div><span class="type">{author}</span></div>',
					'</dd>',
	            '</tpl>',
				'</dl>',
	        '</div>'
	    )
	}), 
	

    
});

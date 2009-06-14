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

ORYX.Plugins.QueryEvaluator = ORYX.Plugins.AbstractPlugin.extend({

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

		try {
			var serialized_rdf = this.getRDFFromDOM();
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
					try {
						this.processProcessList(processList);
					} catch (error) {
						Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
					}
                }.bind(this),
				
				onFailure: function(response){
					this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_DISABLE
					});
					Ext.Msg.alert(ORYX.I18N.Oryx.title, "Server encountered an error (" + response.statusText + ").\n"
						+ response.responseText);
				}.bind(this)
			});
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
	 	}

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
			Ext.Msg.alert(ORYX.I18N.Oryx.title, "Found no matching processes!");
			return;
		}
		
		this.isRendering = true;
		
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
			data.push( [ pair.id, pair.metadata.thumbnailUri + "?" + Math.random(), unescape(pair.metadata.title), pair.metadata.type, pair.metadata.author, pair.elements ] )
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
                }.bind(this)
            }]

		});
		
		var tableModel = new Ext.data.SimpleStore({
			fields: [
				{name: 'id'}, //, type: 'string', mapping: 'metadata.id'},
				{name: 'icon'}, //, mapping: 'metadata.icon'},
				{name: 'title'}, //, mapping: 'metadata.title'},
				{name: 'type'}, //, mapping: 'metadata.type'},
				{name: 'author'}, //, mapping: 'metadata.author'},
				{name: 'elements'}, //, type: 'array', mapping: 'elements'},
			],
			data : data
		});
		
		var iconPanel = new Ext.Panel({
			border	: false,
	        items	: new this.dataGridPanel({
				store       : tableModel, 
				listeners   :{
					dblclick:this._onDblClick.bind(this)
				}
			})
	    });
		this.setPanelStyle();
		
		myProcsPopup.add(iconPanel);
		
		this.isRendering = false;
		
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
					Ext.MessageBox.alert(ORYX.I18N.Oryx.title, "Error loading model meta data.");
				}.bind(this)
			});
		
	},
	
	_onDblClick: function(dataGrid, index, node, e){
		
		// Select the new range
		dataGrid.selectRange(index, index);

		// Get uri and matched element data from the clicked model
		var modelId 	= dataGrid.getRecord( node ).data.id;
		var matchedElements = dataGrid.getRecord( node ).data.elements;
		
		// convert object to JSOn representation
		var elementsAsJson = Ext.encode(matchedElements);
		// escape JSON string to become URI-compliant
		var encodedJson = encodeURIComponent(elementsAsJson);
		
		// remove the last URI segment, append editor's 'self' and json of model elements
		var slashPos = modelId.lastIndexOf("/");
		var uri	= modelId.substr(0, slashPos) + "/self" + "?matches=" + encodedJson;

		// Open the model in Editor
		var editor = window.open( uri );
		window.setTimeout(
	        function() {
                if(!editor || !editor.opener || editor.closed) {
                        Ext.MessageBox.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Oryx.editorOpenTimeout).setIcon(Ext.MessageBox.QUESTION);
                }
	        }, 5000);			
		
	},
	
	dataGridPanel : Ext.extend(Ext.DataView, {
		multiSelect		: true,
		//simpleSelect	: true, 
	    cls				: 'iconview',
	    itemSelector	: 'dd',
	    overClass		: 'over',
		selectedClass	: 'selected',
	    tpl : new Ext.XTemplate(
        '<div>',
			'<dl class="repository_iconview">',
	            '<tpl for=".">',
					'<dd >',
					'<div class="image">',
					 '<img src="{icon}" title="{title}" /></div>',
		            '<div><span class="title" title="{[ values.title.length + (values.type.length*0.8) > 30 ? values.title : "" ]}" >{[ values.title.truncate(30 - (values.type.length*0.8)) ]}</span><span class="author" unselectable="on">({type})</span></div>',
		            '<div><span class="type">{author}</span></div>',
					'</dd>',
	            '</tpl>',
			'</dl>',
        '</div>'
	    )
	}), 
	
	setPanelStyle : function() {
		var styleRules = '\
.repository_iconview dd{\
	width		: 200px;\
	height		: 105px;\
	padding		: 10px;\
	border		: 1px solid #EEEEEE;\
	font-family	: tahoma,arial,sans-serif;\
	font-size	: 9px;\
	display		: block;\
	margin		: 5px;\
	text-align	: left;\
	float		: left;\
}\
.repository_iconview dl {\
	width		: 100%;\
	max-width	: 1000px;\
}\
.repository_iconview dd.over{\
	background-color	: #fff5e1;\
}\
.repository_iconview dd.selected{\
	border-color: #FC8B03;\
}\
.repository_iconview dd img{\
	max-width	: 190px;\
	max-height	: 70px;\
}\
.repository_iconview dd .image{\
	width	: 200px;\
	height	: 80px;\
	padding-bottom	: 10px;\
	text-align		: center;\
	vertical-align	: middle;\
	display	:table-cell;\
}\
.repository_iconview dd .title{\
	font-weight	: bold;\
	font-size	: 11px;\
	color		: #555555;\
}\
.repository_iconview dd .author{\
	margin-left	: 5px;\
}';
		Ext.util.CSS.createStyleSheet(styleRules, 'queryResultStyle');
	},
    
});

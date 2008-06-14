
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();


/**
 * Implements a mapping from EPC Models to BPMN Models
 *
 *
 */
ORYX.Plugins.EPC2BPMN = Clazz.extend({

    facade: undefined,
    
	EPC_NAMESPACE: 'http://b3mn.org/stencilset/epc#',
	BPMN_NAMESPACE: 'http://b3mn.org/stencilset/bpmn#',
    
	
    // Offers the plugin functionality
    construct: function(facade){
        this.facade = facade;
        Facade = facade;
        this.facade.offer({
            'name': "EPC to BPMN transform",
            'functionality': this.startTransform.bind(this),
            'group': "epc",
            'icon': ORYX.PATH + "images/epc_export.png",
            'description': "Tranform from EPC to BPMN",
            'index': 1,
            'minShape': 0,
            'maxShape': 0
        });
        
    },
    
    
    /**
     * Transforming from EPC to BPMN
     */
    startTransform: function(){
    
		this.showPanel( this.sendRequest.bind(this) );
	
	},
	
	/**
	 * Sends the Request out to the EPC-Model with the given url in the options
	 * 
	 * @param {Object} options
	 */
	sendRequest: function(options){

		if( !options || !options.url ){ return }


		this.facade.raiseEvent({ type: 'loading.enable',text: 'Import' });
				
				
        new Ajax.Request(options.url, {
            method: 'GET',
            onSuccess: function(request){
				
				// asynchronously ...
	            window.setTimeout((function(){
	         
					this.doTransform( request.responseText, options)
	                
					// Disable the loading panel
					this.facade.raiseEvent({ type: 'loading.disable'});					
					//show finshed status
					this.facade.raiseEvent({ type:'loading.status', text:'Import finished!' });
	
	
	            }).bind(this), 100);
		

			}.bind(this),
			onFailure: function(request){
				
				// Disable the loading panel
				this.facade.raiseEvent({ type: 'loading.disable'});	
				
				Ext.Msg.alert("Oryx", "Request to server failed!");
			
			}.bind(this)
        });
		   
    },
    
	/**
	 * 
	 * Does actually the Tranformation with an given ERDS-String and some advanced options
	 * 
	 * @param {Object} erdfString
	 * @param {Object} options
	 */
    doTransform: function( erdfString , options){

		var start = new Date()
		start = (start.getSeconds()+(start.getMinutes()*60))
		console.log("Start: " + start);

		var elements = this.parseToObject( erdfString );
		
		var shapes = [];
		
		if( !elements ){
			return 
		}
		var getEPCElementById = function(id){ return elements.find(function(el){ return el.id == id })}
		
		var eventsMappings 			= options && options.events ? options.events.split(";").compact().without("").without(" ").collect(function(s){return s.toLowerCase()}) : [];
		var isIncludedInEMapping	= function(s){ return eventsMappings.any(function(map){return s.toLowerCase().include(map)})}
		
		
		
		// ------------------------------------------
		// 1. Rule: Map - Function --> Task
		//
		
		// 
		var functions = elements.findAll(function(el){ return el.type.endsWith("Function")})
		functions.each(function(epc){
			// Create a new Task
			var shape = this.createElement("Task", epc, true);
			// Map Title -> Name
			shape.setProperty(	"oryx-name", 			epc.title);
			// Map Description -> Documentation
			shape.setProperty(	"oryx-documentation", 	epc.description);
			
			shapes.push({shape: shape, epc:epc})
		}.bind(this))
		
		
		// ------------------------------------------
		// 2. Rule: Events
		//
		
		// 			
		var events = elements.findAll(function(el){ return el.type.endsWith("Event")})
		
		// ------------------------------------------
		// 2a. Rule: Map - Events without an incoming edge --> StartEvent
		//
		
		// 
		var startevents	= events.findAll(function(ev){ return !elements.any(function(el){ return el.outgoing && el.outgoing.any(function(out){ return out.slice(1) == ev.id }) }) })
		startevents.each(function(epc){		
		
			// If its inculded in the mapping, set the type to StartMessageEvent, otherwise is it a StartEvent
			var startEventType = isIncludedInEMapping(epc.title) ? "StartMessageEvent" : "StartEvent";
			
			// Create a new Task
			var shape = this.createElement(startEventType, epc, true);
			// Map Title, Description -> Documentation
			shape.setProperty(	"oryx-documentation", epc.title + " - "+ epc.description);

			shapes.push({shape: shape, epc:epc})
		}.bind(this));		
		
		
		// ------------------------------------------
		// 2b. Rule: Map - Events without an outgoing edge --> EndEvent
		//
		
		// 		
		var endevents	= events.findAll(function(ev){ return !ev.outgoing })
		endevents.each(function(epc){
			// Create a new Task
			var shape = this.createElement("EndEvent", epc, true);
			// Map Title, Description -> Documentation
			shape.setProperty(	"oryx-documentation", epc.title + " - "+ epc.description);

			// Set the end event type of message
			if( isIncludedInEMapping(epc.title) ){
				shape.setProperty(	"oryx-result", "Message");
			}
			shapes.push({shape: shape, epc:epc})
		}.bind(this));	


		// ------------------------------------------
		// extention Rule: Map - Events to Message Events which are defined in the advance settings
		//
		
		// 	
		var intermediateEvents	= [].without.apply(events, startevents.concat(endevents))
		intermediateEvents		= intermediateEvents.findAll(function(epc){ return isIncludedInEMapping(epc.title)})
		intermediateEvents.each(function(epc){
			// Create a new Task
			var shape = this.createElement("IntermediateMessageEvent", epc, true);
			// Map Title, Description -> Documentation
			shape.setProperty(	"oryx-documentation", epc.title + " - "+ epc.description);

			shapes.push({shape: shape, epc:epc})
		}.bind(this));
			
				
		// ------------------------------------------
		// 3. Rule: Map - Connector --> Gateway
		//
		
		// 	
		var connectors	= elements.findAll(function(el){ return el.type.endsWith("Connector") })
		connectors.each(function(epc){
			
			// Set the BPMN Type
			var type = "Exclusive_Databased_Gateway"
			if(epc.type.endsWith("AndConnector")){ 		type = "AND_Gateway" } 
			else if(epc.type.endsWith("OrConnector")){ 	type = "OR_Gateway"  }
			
			// Create a new Task
			var shape = this.createElement(type, epc, true);

			shapes.push({shape: shape, epc:epc})
		}.bind(this))				


				
		// ------------------------------------------
		// 2c. Rule: Map - Events directly after an Split Connectors (except AND-Connector) --> Conditions on the Edges after the Gateway
		//
		
		// 	
		connectors.each(function(epc){
			if(epc.outgoing && epc.outgoing.length > 1 && !epc.type.endsWith("AndConnector")){

				epc.outgoing.each(function(out){
					var next = getEPCElementById(out.slice(1));
					// If its an edge
					if( next.type.endsWith("ControlFlow") ){
						
						next.outgoing.each(function(out2){
							var nextnext = getEPCElementById(out2.slice(1));
							if(nextnext.type.endsWith("Event")){
								next["expression"] = nextnext.title
							}
						})
						
					}					
				})
			}
		}.bind(this))	
		
				

		// ------------------------------------------
		// 5. Rule: Map - Data --> Data Object		
		//
		
		// 	
		var datas = elements.findAll(function(el){ return el.type.endsWith("Data")})
		datas.each(function(epc){
			// Create a new Task
			var shape = this.createElement("DataObject", epc, true);
			// Map Title -> Name
			shape.setProperty(	"oryx-name", 			epc.title);
			// Map Description -> Documentation
			shape.setProperty(	"oryx-documentation", 	epc.description);
			
			shapes.push({shape: shape, epc:epc})
		}.bind(this))


		// ------------------------------------------
		// 6. Rule: Map - System --> Annotation		
		//
		
		// 					
		var systems = elements.findAll(function(el){ return el.type.endsWith("System")})
		systems.each(function(epc){
			// Create a new Task
			var shape = this.createElement("TextAnnotation", epc, true);
			// Map Title -> Text
			shape.setProperty(	"oryx-text", "Used System: " + epc.title);
						
			shapes.push({shape: shape, epc:epc})
		}.bind(this))



		// ------------------------------------------
		// 7. Rule: Map - ProcessLink --> Sub-Process
		//
		
		// 
		var processlinks = elements.findAll(function(el){ return el.type.endsWith("ProcessInterface")})
		processlinks.each(function(epc){
			// Create a new Task
			var shape = this.createElement("Subprocess", epc, true);
			// Map Title -> Name
			shape.setProperty(	"oryx-name", 			epc.title);
			// Map Description -> Documentation
			shape.setProperty(	"oryx-documentation", 	epc.description);
			// Map URL -> SubProcessRef
			shape.setProperty(	"raziel-entry",		 	epc.refuri);
			
			shapes.push({shape: shape, epc:epc})
		}.bind(this))		

		var s1 = new Date()
		s1 = (s1.getSeconds()+(s1.getMinutes()*60))
		console.log("Before Orga: " + (s1 - start));
		
		

		// ------------------------------------------
		// 4. Rule: Map - Organization/Position --> Pool
		//
		
		// 
		var organizations = options.organization ? elements.findAll(function(el){ return el.type.endsWith("Organization") || el.type.endsWith("Position")}) : [];
		if( organizations.length > 0 ){
			
			var pool 		= this.createElement("Pool");
			
			var lanes 		= [];
			var addedShapes	= [];
			
			organizations.each(function(epc){
				// Create a new Task
				var lane = this.createElement("Lane");
				// Map Title -> Name
				lane.setProperty(	"oryx-name", epc.title);
				pool.add( lane );
				lanes.push({shape: lane, epc:epc});
				
				var prevFunctions = epc.outgoing ? epc.outgoing.collect(function(out){ return getEPCElementById(out.slice(1)).outgoing.slice(1) }) : [];
			
				var allRelatedFunctions = shapes.findAll(function(shape){ return shape.epc.type.endsWith("Function") || shape.epc.type.endsWith("ProcessInterface") })
				allRelatedFunctions = allRelatedFunctions.findAll(function(shape){ return  (prevFunctions.indexOf(shape.epc.id) >= 0) || (shape.epc.outgoing && shape.epc.outgoing.any(function(out){ return getEPCElementById(out.slice(1)).outgoing.first().slice(1) == epc.id}))})
				
				allRelatedFunctions.each(function(shape){
					lane.add(shape.shape)
					addedShapes.push(shape)
				})
						
			}.bind(this))	
			
			var notAddedShapes = [].without.apply(shapes, addedShapes);
			
			var emptyLane	= this.createElement("Lane");
			pool.add( emptyLane );
						
			var notAddedFunctions = notAddedShapes.findAll(function(shape){ return shape.epc.type.endsWith("Function")  || shape.epc.type.endsWith("ProcessInterface") });
			notAddedFunctions.each(function(shape){
				emptyLane.add( shape.shape )
				addedShapes.push(shape);				
			})
			

			var notAddedShapes = [].without.apply(shapes, addedShapes);			
			
			// Finds all shapes which are in the 'notAddedShapes'-Array 
			// but aren't in the 'addedShapes'-Array
			var findNextShapesWhichAreNotAdded = function(outgoings){
				
				if( !outgoings ){ return [] }
				
				var res = [];
				
				outgoings.each(function(out){
					// Find one following shape
					var sh = shapes.find(function(el){ return el.epc.id == out.slice(1) })
					
					if (sh) {
						// Lookup in the addedShapes array
						if (addedShapes.indexOf(sh) >= 0) {
							throw $break
						}
						
						if (notAddedShapes.indexOf(sh) >= 0) {
							res.push(sh)
						}
						
						res = res.concat( findNextShapesWhichAreNotAdded( sh.epc.outgoing ) )
					} else {
						res = res.concat( findNextShapesWhichAreNotAdded( getEPCElementById(out.slice(1)).outgoing ) );
					}
					
				});

				return res;
			}

			// Go thru all added shapes (mainly Functions) 
			// and find all following shapes which are not in the added shapes array
			// and add these to the same pool like this
			addedShapes.each(function(shape){
				var nextShapes = findNextShapesWhichAreNotAdded(shape.epc.outgoing)
				
				nextShapes.each(function(nextShape){
					shape.shape.parent.add( nextShape.shape )
					notAddedShapes = notAddedShapes.without( nextShape );	
				})
			});		


			var findNextShapeWhichIsAdded = function(outgoings){
				
				if( !outgoings ){ return [] }
				var res;
				
				outgoings.each(function(out){
					var sh = shapes.find(function(el){ return el.epc.id == out.slice(1) })
					
					if (sh) {
						// Lookup in the addedShapes array
						if (addedShapes.indexOf(sh) >= 0) {
							res = sh;
							throw $break
						}
						res = findNextShapeWhichIsAdded( sh.epc.outgoing );
					} else {
						res = findNextShapeWhichIsAdded( getEPCElementById(out.slice(1)).outgoing );
					}
					
				});

				return res;
			}			
			// For every shapes which couldn't be a following shape
			// (like a start event) add these to this following shape.
			notAddedShapes.each(function(shape){
				
				var nextShape = findNextShapeWhichIsAdded( shape.epc.outgoing );
				if( nextShape ){
					nextShape.shape.parent.add( shape.shape )
					addedShapes.push( shape );						
				}
			})
			
		
		}
		
					
		// --------------------------
		// Generate all Edges
		//
		
		// Function for finding the following shape which is already instanceiated
		var findFollowingShape = function(edge){
			if( !edge || !edge.outgoing){ return null }
			
			var nextElement = edge
			var nextShape;
			
			while(!nextShape){
				
				// Get the following shape
				nextElement = elements.find(function(el){ return nextElement.outgoing && nextElement.outgoing.any(function(out){ return out.slice(1) == el.id } )})
				// Look up if there is an instanciated shape
				nextShape 	= shapes.find(function(el){ return el.epc === nextElement })
				
				if( !nextElement || !nextElement.outgoing){
					break
				}
			}
			
			return nextShape
		}

		
		var edges = []
		// Push all edges to the array which come up in the available shapes		
		shapes.each(function(from){ 
			if(from.epc.outgoing){
				from.epc.outgoing.each(function(out){
					var edge = elements.find(function(epc){ return ( epc.type.endsWith("ControlFlow") || epc.type.endsWith("Relation") ) && epc.id == out.slice(1)}) 
					var next = findFollowingShape( edge );
					if( edge && next){				
						edges.push({
							from: 	from, 
							edge:	edge,
							to: 	next
						})
					}				
				})				
			}				
		})	
		
		// Create all the edges
		edges.each(function(edge){
			// Create a new Edge
			var shape
			if( edge.edge.type.endsWith("Relation") ) {
				if(edge.edge.informationflow.toLowerCase() == "true"){
					shape = this.createElement("Association_Unidirectional", edge.edge);				
				} else {
					shape = this.createElement("Association_Undirected", edge.edge);					
				}
			} else {
				shape = this.createElement("SequenceFlow", edge.edge);
			}
			
			var from 	= edge.from.shape;
			var to 		= edge.to.shape;
			// Set the docker
			shape.dockers.first().setDockedShape( from );
			shape.dockers.first().setReferencePoint({x: from.bounds.width() / 2.0, y: from.bounds.height() / 2.0});
			shape.dockers.first().update()

			shape.dockers.last().setDockedShape( to );
			shape.dockers.last().setReferencePoint({x: to.bounds.width() / 2.0, y: to.bounds.height() / 2.0});
			shape.dockers.last().update()
			
			// If there is an expression, it will be setted
			if( edge.edge.expression ){
				shape.setProperty("oryx-conditionexpression", edge.edge.expression)
			}
			
			shapes.push({shape: shape, epc:edge.edge})
		}.bind(this))		


		// --------------------------
		// UPDATE
		//		
		this.facade.getCanvas().update();
		
		if( options.autolayout ){
			this.facade.raiseEvent({type:'autolayout.layout'});
		}	
		
	},
	
	/**
	 * Creates a BPMN-Shape with the given type
	 * 
	 * @param {Object} bpmnType
	 * @param {Object} epcElement
	 * @param {Object} setBounds
	 */
	createElement: function(bpmnType, epcElement, setBounds){

		// Create a new Stencil								
		var stencil = ORYX.Core.StencilSet.stencil('http://b3mn.org/stencilset/bpmn#' + bpmnType);
	
		// Create a new Shape
		var newShape = (stencil.type() == "node") ?
										new ORYX.Core.Node(
											{'eventHandlerCallback':this.facade.raiseEvent },
											stencil) :
										new ORYX.Core.Edge(
											{'eventHandlerCallback':this.facade.raiseEvent },
											stencil);

		// Add the shape to the canvas
		this.facade.getCanvas().add(newShape);
		
		if( epcElement && epcElement.bounds && setBounds){
			// Set the bounds
			newShape.bounds.centerMoveTo( epcElement.bounds.center )
		}
		
		return newShape;
					
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

		// Get the oryx-editor div
		var editorNode 	= getElementByIdFromDiv('oryxcanvas')

		var hasEPC = editorNode ? $A(editorNode.childNodes).any(function(node){return node.nodeName.toLowerCase() == "a" && node.getAttribute('rel') == 'oryx-stencilset' && node.getAttribute('href').endsWith('epc/epc.json')}) : null;

		if( !hasEPC ){
			this.throwErrorMessage('Imported model is not an EPC model!');
			return null
		}


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
	
	/** ********************************************************
	 * 
	 * UI-WINDOW
	 * 
	 * ********************************************************
	 * 
	 * Shows the Popup Window where u can specify the url
	 * and some advanced parameter
	 * 
	 * @param {Object} callback
	 */
	showPanel: function( callback ){
			
		Ext.QuickTips.init();
		
		var mainForm = new Ext.form.FormPanel({
						id:				'transform-epc-bpmn-id-main',
					    labelWidth: 	40,
					    defaultType: 	'textfield',
					    bodyStyle:		'padding:5px',
					    defaults: 		{width: 300, msgTarget: 'side'},
					    items: [{
								text:'For the import and transformation from EPC to BPMN please set the URL to the EPC model.', 
								xtype:'label',
								style:'padding-bottom:10px;display:block',
								width:"100%"
							},{
								fieldLabel: 'URL',
								name: 		'last',
								//vtype: 		'url',
								allowBlank: false
							}]
					});
		
		
		var advanceForm = new Ext.form.FormPanel({
					    id:				'transform-epc-bpmn-id-advance',
					    collapsed:		true,
					    labelWidth: 	30,
					    defaultType: 	'textfield',
					    bodyStyle:		'padding:15px',
						defaults:		{width: 300,msgTarget: 'side',labelSeparator:''},
					    items: [{
								text:	'Event-Mapping',
								xtype: 	'label',
								cls:	'transform-epc-bpmn-title'
					        },{
								text:	'If u like to transform indivual event from EPC to event in BPMN, please give keyword regarding to these (sepreated with a \';\').',
								xtype: 	'label',
								width:	'100%',
								style:	'margin-bottom:10px;display:block;'
					        },{
								labelStyle: 'background:transparent url(stencilsets/bpmn/icons/intermediate-message.png) no-repeat scroll 0px -1px;width:30px;height:20px',
					            name: 	'events'
					        },{
								text:	'Organization',
								xtype: 	'label',
								style:	'margin-top:10px;display:block;',
								cls:	'transform-epc-bpmn-title'
					        },{
								text:	'Should the organizational units and roles maped to a pool/lane? (Required Auto-Layout)',
								xtype: 	'label',
								width:	'100%',
								style:	'margin-bottom:10px;display:block;'
					        },{
								boxLabel: 'Organization',
								name: 	'autolayout',
								id:		'transform-epc-bpmn-id-organization',
								xtype:	'checkbox',
								labelStyle:	'width:30px;height:20px'
					        },{
								text:	'Auto-Layout',
								xtype: 	'label',
								style:	'margin-top:10px;display:block;',
								cls:	'transform-epc-bpmn-title'
					        },{
								text:	'By enable the autolayout, the model will be auto layouted afterwards with the AutoLayout Plugin. (Needs a while)',
								xtype: 	'label',
								width:	'100%',
								style:	'margin-bottom:10px;display:block;'
					        },{
								boxLabel: 'Auto-Layout',
								name: 	'autolayout',
								id:		'transform-epc-bpmn-id-autolayout',
								xtype:	'checkbox',
								labelStyle:	'width:30px;height:20px'
					        }]
					});

		Ext.getCmp('transform-epc-bpmn-id-organization').on('check', function(obj, check){
			
			if(check){
				Ext.getCmp('transform-epc-bpmn-id-autolayout').setValue( true );
				Ext.getCmp('transform-epc-bpmn-id-autolayout').disable();
			} else {
				Ext.getCmp('transform-epc-bpmn-id-autolayout').enable();
			}
		})
		
		var groupButton = {
			            text:			'Advanced Settings',
			            xtype:			'button',
			            enableToggle:	true,
						cls:			'transform-epc-bpmn-group-button',
			            handler:function(d){
			                var d = Ext.getCmp('transform-epc-bpmn-id-advance');
			                if(d.collapsed){
			                    d.expand();
			                } else {
			                    d.collapse();
			                }
			            }
			        }
					
		
		var windowPanel = new Ext.Window({
					    title:			"Oryx - Transform EPC to BPMN",
					    width:			400,
						id:				'transform-epc-bpmn-id-panel',
						cls:			'transform-epc-bpmn-window',
					    items: 			new Ext.Panel({frame:true,autoHeight:true,items:[mainForm, groupButton , advanceForm]}),
						floating:		true,
						shim:			true,
						modal:			true,
						resizable:		false,
						autoHeight:		true,			    
						buttons:[{
								text:	'Import',
								handler: function(){
									var res = {};
									
									var urlField = Ext.getCmp('transform-epc-bpmn-id-main').findByType('textfield')[0]
									
									if( urlField.validate() ){
										res.url 			= urlField.getValue();
									}
									
									if( !Ext.getCmp('transform-epc-bpmn-id-advance').collapsed ){
										res.events 			= Ext.getCmp('transform-epc-bpmn-id-advance').findByType('textfield')[0].getValue();
										//res.other 			= Ext.getCmp('transform-epc-bpmn-id-advance').findByType('textfield')[1].getValue();
										res.organization	= Ext.getCmp('transform-epc-bpmn-id-advance').findByType('checkbox')[0].getValue();
										res.autolayout 		= Ext.getCmp('transform-epc-bpmn-id-advance').findByType('checkbox')[1].getValue();
									}
									
									Ext.getCmp('transform-epc-bpmn-id-panel').destroy();
								
									callback( res );
								}
							},{
								text:	'Cancel',
								handler: function(){
									Ext.getCmp('transform-epc-bpmn-id-panel').destroy();
								}
							}]  
					})
						
		windowPanel.show()		
	}
	
});


var Facade = undefined;

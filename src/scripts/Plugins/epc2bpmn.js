
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
    /**
     * Offers the plugin functionality:
     *
     */
    construct: function(facade){
        this.facade = facade;
        Facade = facade;
        this.facade.offer({
            'name': "EPC to BPMN transform",
            'functionality': this.transform.bind(this),
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
    transform: function(){
    
        //var checkIfEPCisLoaded = this.facade.getStencilSets().values().any(function(stencilSet){ return stencilSet.namespace() == ORYX.Plugins.EPC2BPMN.EPC_NAMESPACE })
        
		//if( !checkIfEPCisLoaded ){ return }
		
		/*
			Ext.QuickTips.init();
			
			var form1 = new Ext.form.FormPanel({
			    labelWidth: 75,
			    defaultType: 'textfield',
			    bodyStyle:'padding:15px',
			    defaults: {
			        // applied to each contained item
			        width: 230,
			        msgTarget: 'side'
			    },
			
			    items: [{
			            fieldLabel: 'URL',
			            name: 'last'
			        }
			    ]
			});
			
			
			var form2 = new Ext.form.FormPanel({
			    id:'form_advance',
			    collapsed:true,
			    labelWidth: 75,
			    defaultType: 'textfield',
			    bodyStyle:'padding:15px',
			    defaults: {
			        // applied to each contained item
			        width: 230,
			        msgTarget: 'side'
			    },
			
			    items: [{
			            fieldLabel: 'Sub',
			            name: 'last'
			        }
			    ]
			});
			
			var e = new Ext.Window({
			    title:"Oryx Import",
			    width:370,
			    items:[form1, {
			            text:'advance',
			            xtype:'button',
			            enableToggle:true,
			            handler:function(d){
			                var d = Ext.getCmp('form_advance')
			                if(d.collapsed){
			                    d.expand();
			                } else {
			                    d.collapse();
			                }
			            }
			        }, form2],
			    buttons:[{text:'Import'},{text:'Cancel'}]  
			})
			e.show()		 
		 
		 
		 */
		Ext.Msg.prompt('Transfer from EPC to BPMN', 'Please enter the URL to the EPC model:', function(btn, text){
		    if (btn == 'ok' && text != ''){
		        new Ajax.Request(text, {
		            method: 'GET',
		            onSuccess: function(request){
										
						this.facade.raiseEvent({ type: 'loading.enable',text: 'Import' });
						
						// asynchronously ...
			            window.setTimeout((function(){
			         
							this.doTransform( request.responseText)
			                
							//show finshed status
							this.facade.raiseEvent({ type:'loading.status', text:'Import finished!' });
			
			
			            }).bind(this), 100);
				
		
					}.bind(this),
					onFailure: function(request){
						
						Ext.Msg.alert("Oryx", "Request to server failed!");
					
					}.bind(this)
		        });
		    }
		}.bind(this));
    },
    
    doTransform: function( erdfString ){
	
		var elements = this.parseToObject( erdfString );
		
		var shapes = [];
		
		if( !elements ){
			return 
		}
		var getEPCElementById = function(id){ return elements.find(function(el){ return el.id == id })}
		
		
		
		// 1. Rule: Map - Function --> Task
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
		
		
		// Get all Events
		var events = elements.findAll(function(el){ return el.type.endsWith("Event")})

		
		// 2a. Rule: Map - Events without an incoming edge --> StartEvent
		var startevents	= events.findAll(function(ev){ return !elements.any(function(el){ return el.outgoing && el.outgoing.any(function(out){ return out.slice(1) == ev.id }) }) })
		startevents.each(function(epc){
			// Create a new Task
			var shape = this.createElement("StartEvent", epc, true);
			// Map Title, Description -> Documentation
			shape.setProperty(	"oryx-documentation", epc.title + " - "+ epc.description);

			shapes.push({shape: shape, epc:epc})
		}.bind(this))		
		
		
		// 2b. Rule: Map - Events without an outgoing edge --> EndEvent
		var endevents	= events.findAll(function(ev){ return !ev.outgoing })
		endevents.each(function(epc){
			// Create a new Task
			var shape = this.createElement("EndEvent", epc, true);
			// Map Title, Description -> Documentation
			shape.setProperty(	"oryx-documentation", epc.title + " - "+ epc.description);

			shapes.push({shape: shape, epc:epc})
		}.bind(this))	
		
		// 3. Rule: Map - Connector --> Gateway
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


		// 2c. Rule: Map - Events directly after an Split Connectors --> Conditions on the Edges after the Gateway
		connectors.each(function(epc){
			if(epc.outgoing && epc.outgoing.length > 1 ){

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
		
		// 4. Rule: Map - Organization --> Pool
		
		// 5. Rule: Map - Position --> Lane

		// 6. Rule: Map - Data --> Data Object
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
				
		// 7. Rule: Map - System --> ?		

		// 8. Rule: Map - ProcessLink --> Sub-Process
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
					if( edge ){				
						edges.push({
							from: 	from, 
							edge:	edge,
							to: 	findFollowingShape( edge )
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
					shape = this.createElement("Association_Bidirectional", edge.edge);					
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


		this.facade.getCanvas().update()		

	},
	
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
		
		if( epcElement.bounds && setBounds){
			// Set the bounds
			newShape.bounds.centerMoveTo( epcElement.bounds.center )
		}
		
		return newShape;
					
	},
	
	parseToObject: function ( erdfString ){

		var parser	= new DOMParser();			
		var doc		= parser.parseFromString( erdfString ,"text/xml");

		var getElementByIdFromDiv = function(id){ return $A(doc.getElementsByTagName('div')).find(function(el){return el.getAttribute("id")== id})}

		// Get the oryx-editor div
		var editorNode 	= getElementByIdFromDiv('oryxcanvas')

		var hasEPC = $A(editorNode.childNodes).any(function(node){return node.nodeName.toLowerCase() == "a" && node.getAttribute('rel') == 'oryx-stencilset' && node.getAttribute('href').endsWith('epc/epc.json')})

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
	
	throwErrorMessage: function(message){
		Ext.Msg.alert( 'Oryx', message )
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
	}
	
});


var Facade = undefined;

/**
 * Copyright (c) 2006
 * Helen Kaltegaertner
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

ORYX.Plugins.ShapeConnector = Clazz.extend({

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.active = false;
		this.sourceNode = null;

		this.facade.offer({
			name:			ORYX.I18N.ShapeConnector.add,
			functionality: 	this.enableConnector.bind(this),
			group: 			ORYX.I18N.ShapeConnector.group,
            toolbarGroup: 	ORYX.I18N.ShapeConnector.toolbarGroup,
			icon: 			ORYX.PATH + "images/pencil_go.png",
			description: 	ORYX.I18N.ShapeConnector.addDesc,
			index: 			1,
            toggle: 		true,
			minShape: 		0,
			maxShape: 		0});
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
	},
	
	enableConnector: function(button, pressed) {
		this.connectButton = button;
		if (!pressed){
        	this.active = false;
        	this.sourceNode = null;
        }
		else 
			this.active = true;
	},	
	/**
	 * MouseDown Handler
	 *
	 */	
	handleMouseDown: function(event, uiObj) {
		if (this.active && uiObj instanceof ORYX.Core.Node) {
            if (this.sourceNode){	
            	this.createEdge( this.sourceNode, uiObj)
            }
        	this.sourceNode = uiObj
		}
		else if (this.active) {
			if (this.connectButton){
				this.connectButton.toggle();
			}
		}
	},
    
	createEdge: function(source, target){

		// Create a new Stencil		
		var ssn 	= this.facade.getStencilSets().keys()[0];						
		var stencil = ORYX.Core.StencilSet.stencil(ssn + "SequenceFlow");
			
		// Create a new Shape
		var edge = new ORYX.Core.Edge({'eventHandlerCallback':this.facade.raiseEvent }, stencil);
		edge.dockers.first().setDockedShape( source );
		edge.dockers.first().setReferencePoint({x: source.bounds.width() / 2.0, y: source.bounds.height() / 2.0});
		//shape.dockers.first().update()

		edge.dockers.last().setDockedShape( target );
		edge.dockers.last().setReferencePoint({x: target.bounds.width() / 2.0, y: target.bounds.height() / 2.0});
		
		// Add the shape to the canvas
		this.facade.getCanvas().add(edge);
		this.facade.getCanvas().update();		
		
		return edge;
					
	},
});


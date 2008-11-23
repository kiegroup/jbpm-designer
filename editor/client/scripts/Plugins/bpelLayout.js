/**
 * Copyright (c) 2008
 * Zhen Peng
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

ORYX.Plugins.BPELLayouting = Clazz.extend({

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;
		
		this.toMoveShapes = [];				// Shapes that are moved
	
		this.dragBounds = undefined;
		this.offSetPosition = {x:0, y:0};

		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL, this.handleLayoutEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_VERTICAL, this.handleLayoutVerticalEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL, this.handleLayoutHorizontalEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD, this.handleSingleChildLayoutEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE, this.handleAutoResizeLayoutEvent.bind(this));
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleMouseDown.bind(this));
	},
	
	/************************ Mouse Hanlder *************************/
	
	/**
	 * On the Selection-Changed
	 */
	onSelectionChanged: function(event) {

		var elements = event.elements;

		// If there are no elements
		if(!elements || elements.length == 0) {
			// reset all variables

			this.toMoveShapes = [];
			this.dragBounds = undefined;
		} else {

			// Get all shapes with the highest parent in object hierarchy (canvas is the top most parent)
			this.toMoveShapes = this.facade.getCanvas().getShapesWithSharedParent(elements);
			
			this.toMoveShapes = this.toMoveShapes.findAll( function(shape) { return shape instanceof ORYX.Core.Node && 
																			(shape.dockers.length === 0 || !elements.member(shape.dockers.first().getDockedShape()))});		
			elements.each((function(shape){
				if(!(shape instanceof ORYX.Core.Edge)) {return}
				
				var dks = shape.getDockers() 
								
				var hasF = elements.member(dks.first().getDockedShape());
				var hasL = elements.member(dks.last().getDockedShape());	
						
				if(!hasL) {
					this.toMoveShapes.push(dks.last());
				}
				if(!hasF){
					this.toMoveShapes.push(dks.first())
				} 
				
				if( shape.dockers.length > 2){
					this.toMoveShapes = this.toMoveShapes.concat(dks.findAll(function(el,index){ return index > 0 && index < dks.length-1}))
				}
				
			}).bind(this));	
																			
			// Calculate the area-bounds of the selection
			var newBounds = undefined;
			elements.each(function(value) {
				if(!newBounds)
					newBounds = value.absoluteBounds();
				else
					newBounds.include(value.absoluteBounds());
			});

			// Set the new bounds
			this.dragBounds = newBounds;

		}
		
		return;
	},
	
	handleMouseDown: function(event, uiObj) {
		if(!this.dragBounds || !this.toMoveShapes.member(uiObj)) {return};
		
		var evCoord 	= this.facade.eventCoordinates( event );
		var ul = this.dragBounds.upperLeft();
		
		this.offSetPosition = {
			x: evCoord.x - ul.x,
			y: evCoord.y - ul.y
		}
		
		return;
	},	
	
	/**************************** Layout ****************************/
	
	
	dropShapesDown: function(event){
	
		var elements = event.shape.getChildShapes(false);
		
		var offsetPos = this.offSetPosition;
		var movedShapes = this.toMoveShapes;

		movedShapes.each(function(shape){
			if(elements.include(shape)) shape.bounds.moveBy(offsetPos);
		});
		
		return;
		
	}, 
	
	/**
	 *  realize special BPEL layouting:
	 *  main activity: placed left,
	 *  Handler: placed right.
	 */
	handleLayoutEvent: function(event) {
		
     	var elements = event.shape.getChildShapes(false);
     	
		// If there are no elements
		if(!elements || elements.length == 0) {
			return;
		};
		
		this.dropShapesDown(event);
		
		var activity = elements.find(function(node) {
				return (Array.indexOf(node.getStencil().roles(), node.getStencil().namespace() + "activity")>= 0);
		    });
		
     	var eventHandlers = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "eventHandlers");
			});
		
		var faultHandlers = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "faultHandlers");
			});
			
		var compensationHandler = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "compensationHandler");
			});	

		var terminationHandler = elements.find(function(node) {
				return (node.getStencil().id() == node.getStencil().namespace() + "terminationHandler");
			});
		
		var nextLeftBound = 30;
		var nextUpperBound = 30;
		
		// handle Activity
		if (activity){
			activity.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextLeftBound = activity.bounds.lowerRight().x + 30;
		}
		// handle EventHanlders
		if (eventHandlers){
			eventHandlers.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = eventHandlers.bounds.lowerRight().y;
		}
		// handle FaultHandlers
		if (faultHandlers){
			faultHandlers.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = faultHandlers.bounds.lowerRight().y;
		}
		// handle CompensationHandler
		if (compensationHandler){
			compensationHandler.bounds.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = compensationHandler.bounds.lowerRight().y;
		}
		// handle TerminationHandler
     	if (terminationHandler){
			terminationHandler.bounds.moveTo(nextLeftBound, nextUpperBound);
		}
		
		this.autoResizeLayout(event);
		
		return;
		
	},
	
	handleLayoutVerticalEvent: function(event) {
	
		var elements = event.shape.getChildShapes(false);
		
		// If there are no elements
		if(!elements || elements.length == 0) {
			return;
		};
		
		this.dropShapesDown(event);
		
		// remove all shapes into a column
		elements.each(function(element){
			var ul = element.bounds.upperLeft();
			element.bounds.moveTo(30, ul.y);
		});
		
		// Sort top-down
		elements = elements.sortBy(function(element){
			return element.bounds.upperLeft().y;
		});
		
		var lastUpperYPosition = 0;
		// Arrange shapes
		elements.each(function(element){
		
			var ul = element.bounds.upperLeft();
			var oldUlY = ul.y;
			
			ul.y = lastUpperYPosition + 30;
			lastUpperYPosition = ul.y + element.bounds.height();
			
			if ((ul.y != oldUlY)) {
				element.bounds.moveTo(30, ul.y);
			}
		});
		
		this.autoResizeLayout(event);
	
		return;
	},
	
	handleLayoutHorizontalEvent: function(event) {

		var elements = event.shape.getChildShapes(false);
		
		// If there are no elements
		if(!elements || elements.length == 0) {
			return;
		};

		this.dropShapesDown(event);
		
		// remove all shapes in a row
		elements.each(function(element){
			var ul = element.bounds.upperLeft();
			element.bounds.moveTo(ul.x, 30);
		});
		
		// Sort left-right
		elements = elements.sortBy(function(element){
			return element.bounds.upperLeft().x;
		});
		
		var lastLeftXPosition = 0;
		
		// Arrange shapes on rows (align left)
		elements.each(function(element){
		
			var ul = element.bounds.upperLeft();
			var oldUlX = ul.x;
			
			ul.x = lastLeftXPosition + 30;
			lastLeftXPosition = ul.x + element.bounds.width();

			if ((ul.x != oldUlX)) {
				element.bounds.moveTo(ul.x, 30);
			}
		});
		
		this.autoResizeLayout(event);
			
		return;
	},
	
	
	
	handleSingleChildLayoutEvent: function(event) {
     	
		var elements = event.shape.getChildShapes(false);
		
		// If there are no elements
		if(!elements || elements.length == 0) {
			return;
		};
		
		this.dropShapesDown(event);
		
		elements.first().bounds.moveTo(30, 30);
		
		this.autoResizeLayout(event);
		
		return;
	},
	
	handleAutoResizeLayoutEvent: function(event) {
		
		this.autoResizeLayout(event);
	},
	
	/**
	 * Resizes the shape to the bounds of the child shapes
	 */
	autoResizeLayout: function(event) {
		
		var elements = event.shape.getChildShapes(false);
		
		if (elements.length > 0) {

		    elements = elements.sortBy(function(element){
				return element.bounds.lowerRight().x;
		    });
		    
			var rightBound = elements.last().bounds.lowerRight().x;
                 
		    elements = elements.sortBy(function(element){
				return element.bounds.lowerRight().y;
		    });
		    
			var lowerBound = elements.last().bounds.lowerRight().y;
			
			var ul = event.shape.bounds.upperLeft();
			
			event.shape.bounds.set(ul.x, ul.y, ul.x + rightBound + 30, ul.y + lowerBound + 30);
		};
		
		return;
	}
	
});
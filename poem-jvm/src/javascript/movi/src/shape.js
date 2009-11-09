/**
 * Copyright (c) 2009
 * Jan-Felix Schwarz
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


MOVI.namespace("model");

(function() {
	
	/**
     * Create the host element.
     * @method _createHostElement
     * @private
     */
    var _createHostElement = function() {
        var el = document.createElement('div');
        return el;
    };

	/**
     * Returns the appropriate subclass to choose when creating the shape 
	 * object from the specified JSON object. If no subclass can be 
	 * determined the Shape base class is returned.
     * @method _getSubclassForJSONObj
 	 * @param {Object} jsonObj The JSON object to create the shape from
	 * @param {Stencilset} stencilset The stencilset object
     * @private
     */
	var _getSubclassForJSONObj = function(jsonObj, stencilset) {
		var stencil = stencilset.getStencil(jsonObj.stencil.id);
		if(stencil) {
			if(stencil.type=="node")
				return MOVI.model.Node;
			else if(stencil.type=="edge")
				return MOVI.model.Edge;
		}
		return MOVI.model.Shape;
	}
	
	/**
     * An abstract base class providing a wrapper object for all 
	 * model shapes (including the canvas, nodes, and edges).
     * @namespace MOVI.model
     * @class MOVI.model.Shape
     * @extends YAHOO.util.Element
     * @constructor
     * @param {Object} jsonObj The JSON object from which the new shape
     * is created
	 * @param {Object} stencilset The stencilset for lookup of the shape's 
	 * stencil
	 * @param {Shape} parent The shapes's parent shape
	 * @param {String} prefix The element's ID prefix (unique per modelviewer)
	 * @param {Object} attr (optional) A key map of the shape element's 
     * initial attributes
     */
    MOVI.model.Shape = function(jsonObj, stencilset, parent, prefix, attr) {
		
		YAHOO.lang.augmentObject(this, jsonObj, true);
		
		if(!stencilset) {
			throw new Error("No stencilset associated for shape with resource id" + 
					 this.resourceId + ".", "shape.js");
			return false;
		}
		
		if(!this.resourceId) {
			throw new Error("The shape has no resource id.", "shape.js");
			return false;
		}
	
		if(!this.stencil || !this.stencil.id) {
			throw new Error("No stencil definition found for shape with resource id " + 
				this.resourceId, "shape.js");
			return false;
		}
	
		// retrieve stencil
		var sId = this.stencil.id;
		this.stencil = stencilset.getStencil(sId);
		
		if(!this.stencil) {
			throw new Error("Could not find definition for stencil " + sId + 
					 " in stencilset " + stencilset.namespace + ".", "shape.js" );
			return false;
		}
		
		// register parent shape
		this.parentShape = parent;

		// create host element
		attr = attr || {};
        el = _createHostElement.call(this, attr);
		
		MOVI.model.Shape.superclass.constructor.call(this, el, attr); 		
		
		this.set("id", prefix + this.resourceId);
		
		/* create child shape objects and append them as child elements */
		
		var childSh = this.childShapes; // store children's JSON objects in temp var
		this.childShapes = {};			// reset childShapes map
	
		// create shape object for each child and append to childShapes map
		for(var i = 0; i<childSh.length; i++) { 
			var child = childSh[i];
			var subclass = _getSubclassForJSONObj(child, stencilset);
			child = new subclass(child, stencilset, this, prefix);
			this.childShapes[child.resourceId] = child;
			this.appendChild(child);
		}
		
	}
	
	MOVI.extend(MOVI.model.Shape, YAHOO.util.Element, {
		
		/**
		 * The shapes parent shape
		 * @property parentShape
		 */
		parentShape: null,
		
		/**
	     * Returns the canvas that owns this shape
	     * @method getCanvas
		 * @return {Canvas} The Canvas the shape belongs to
	     */
		getCanvas: function() {
			var e = this;
			while(e.parentShape!=null)
				e = e.parentShape;
			return e;
		},

		/**
		 * Returns a set on all incoming shapes
		 * @method getIncomingShapes
		 * @return {Array}
		 */
		getIncomingShapes: function(){
			
			if (!this._incomingShapes){
							
				var incoming = [];
				var canvas = this.getCanvas();
	
				if (canvas) {
					var nodes = canvas.shapes;
					for (var i in nodes){
						if(!YAHOO.lang.hasOwnProperty(nodes, i)) continue;
				
						if ((nodes[i].outgoing||[]).length > 0) {
							var isOutgoing = false;
								for (var j=0, oLen = nodes[i].outgoing.length; j < oLen; ++j){
									if (nodes[i].outgoing[j].resourceId == this.resourceId){
										incoming.push(nodes[i]);
										break;
									}
								}
						}
					}
				}
				this._incomingShapes = incoming;
			}

			return this._incomingShapes;				
		},
				
		/**
		 * Returns a set on all outgoing shapes
		 * @method getOutgoingShapes
		 * @return {Array}
		 */
		getOutgoingShapes: function(){
			
			if (!this._outgoingShapes){
				
				var outgoings = [];
				var canvas = this.getCanvas();
		
				if (canvas) {
					var og = this.outgoing || [];
					for (var i=0, len=og.length; i<len; ++i){
						var shape = canvas.getShape(og[i].resourceId);
						if (shape) {
							outgoings.push(shape);	
						}
					}
				}
				
				this._outgoingShapes = outgoings;
			}

			return this._outgoingShapes;				
		},
		
		/**
		 * Returns the bounds of a shape
		 * 
		 */
		getBounds: function(){
			
			if (!this.bounds) {
				
				// ERROR HANDLING if bounds are not set
				if (this.dockers) {
					var min, max;
					
					for (var i = 0, len = this.dockers.length; i < len; ++i) {
						
						var pos = {x:this.dockers[i].x, y:this.dockers[i].y}
						
						// Add the bounds of the docked incoming shape
						if (i==0){
							var nodes = this.getIncomingShapes();
							if (nodes.length > 0){
								var bounds = nodes[0].getAbsBounds();
								pos.x += bounds.upperLeft.x;
								pos.y += bounds.upperLeft.y;
							}
						}
						
						// Add the bounds of the docked outgoing shape
						if (i == len-1&&(this.outgoing||[]).length > 0) {
							var nodes = this.getOutgoingShapes();
							if (nodes.length > 0){
								var bounds = nodes[0].getAbsBounds();
								pos.x += bounds.upperLeft.x;
								pos.y += bounds.upperLeft.y;
							}
						}
						
						if (!min) min = {x: pos.x, y: pos.y}
						if (!max) max = {x: pos.x, y: pos.y }
						
						
						min.x = Math.min(min.x, pos.x);
						min.y = Math.min(min.y, pos.y);
						max.x = Math.max(max.x, pos.x);
						max.y = Math.max(max.y, pos.y);
					}
					
					if (min && max) {
						this.bounds = {
							upperLeft: min,
							lowerRight: max
						}
					}
				}
				
				if (!this.bounds){
					this.bounds =  { 
						upperLeft: { x: 0, y: 0 }, 
						lowerRight: { x: 0, y: 0 } 
					}
				}
			}
			
			return { upperLeft: { 
				x: this.bounds.upperLeft.x, 
				y: this.bounds.upperLeft.y
			  }, 
			  lowerRight: { 
				x: this.bounds.lowerRight.x,
				y: this.bounds.lowerRight.y
			  } 
			}

		},
		
		/**
	     * Returns the shape's absolute bounds coordinates. 'Absolute' means
		 * relative to the canvas element rather than relative to the document
	     * @method getAbsBounds
		 * @return {Object} The absolute shape bounds accessible as 
		 * { upperLeft: {x:Number,y:Number}, lowerRight: {x:Number,y:Number} }
	     */
		getAbsBounds: function() {
			var absBounds =	this.getBounds();	
			var e = this;
			while(e.parentShape!=null && e.parentShape.parentShape!=null) {
				e = e.parentShape;
				var b = e.getBounds();
				absBounds.upperLeft.x +=b.upperLeft.x;
				absBounds.upperLeft.y += b.upperLeft.y;
				absBounds.lowerRight.x += b.upperLeft.x;
				absBounds.lowerRight.y += b.upperLeft.y;
			}
			return absBounds;
		},
		
		/**
		 * Tests if the shape has children
		 * @method hasChildShapes
		 * @return {Boolean} true if the shape has children, false otherwise
		 */
		hasChildShapes: function() {
			for(key in this.childShapes) {
				if(!YAHOO.lang.hasOwnProperty(this.childShapes, key)) continue;
				return true;
			}
			return false;
		},
		
		/**
		 * Returns the stencil of this shape
		 * @method getStencil
		 * @return {Stencil} The stencil object
		 */
		getStencil: function() {
			return this.stencil;
		},
		
		/**
		 * Returns true if the shape is a node
		 * @method isNode
		 * @return {Boolean} true if shape is a node
		 */
		isNode: function() {
			return false;
		},
		
		/**
		 * Returns true if the shape is an edge
		 * @method isEdge
		 * @return {Boolean} true if shape is an edge
		 */
		isEdge: function() {
			return false;
		}
		
		// TODO: Doc for shape attributes from JSON
		
	});
	
})();
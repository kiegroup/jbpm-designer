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


MOVI.namespace("util");

(function() {
	
	/**
     * Attach Marker objects to the model to highlight a shape or a set of shapes
	 * by overlaying rectangles
     * @namespace MOVI.util
     * @class MOVI.util.Marker
     * @extends YAHOO.util.Element
     * @constructor
	 * @param {Shape|Shapes[]} shapes (optional) The shapes to mark
	 * @param {Object} style (optional) A key map of CSS style properties to be attached
	 * to the shape marking rectangles.
     */
    MOVI.util.Marker = function(shapes, style) {
		
		if(!shapes) shapes = [];
		if(!YAHOO.lang.isArray(shapes)) shapes = [shapes];
		
		this._style = style || {};
		this.shapeRects = {};

this._shapes = {};
		// create shape rect elements
		for(i in shapes) {
			var s = shapes[i];
			this._shapes[s.resourceId] = s;
			this.shapeRects[s.resourceId] = new YAHOO.util.Element(document.createElement('div'));
			this.shapeRects[s.resourceId].setStyle("position", "absolute");
			s.appendChild(this.shapeRects[s.resourceId]);
		}
	
		this._update();
	};
	
	/**
	 * The padding beween the marker's border and the shape's border.
	 * @property MOVI.util.Marker.PADDING
	 * @static
	 * @type integer
	 * @default 2
	 */
	MOVI.util.Marker.PADDING = 4;
	
	MOVI.util.Marker.prototype = {
		
		/**
		 * A key map containing the CSS style property definitions that are applied 
		 * to each shape marking rectangle.
		 * @property style
		 * @type Object
		 * @private
		 */
		_style: {},
		
		/**
		 * The class name that is specified for each shape marking rectangle.
		 * @property _className
		 * @type String
		 * @private
		 */
		_className: "",
		
		/**
		 * A key map containing all marked shapes with their resource IDs as keys
		 * @property shapes
		 * @type Object
		 * @private
		 */
		_shapes: {},
		
		/**
		 * A key map containing all shape rectangle elements of the marker with the
		 * associated shape resource IDs as keys
		 * @property shapeRects
		 * @type { Integer : Element }
		 */
		shapeRects: {},
		
		/**
	     * Update style properties of the element
	     * @method _update
	     * @private
	     */
		_update: function() {
			for(i in this._shapes) {
				var shape = this._shapes[i];
				var rect = this.shapeRects[i];
				
				// apply styles
				for(prop in this._style) 
					rect.setStyle(prop, this._style[prop]);
				
				/* adjust position */
				
				// get border widths
				var bTWidth = parseInt(rect.getStyle("border-top-width")),
					bRWidth = parseInt(rect.getStyle("border-right-width")),
					bBWidth = parseInt(rect.getStyle("border-bottom-width")),
					bLWidth = parseInt(rect.getStyle("border-left-width"));
							
				var left = - MOVI.util.Marker.PADDING;
				var top = - MOVI.util.Marker.PADDING;
				var width = Math.round(shape.bounds.lowerRight.x
							- shape.bounds.upperLeft.x) + 2*MOVI.util.Marker.PADDING
							- bLWidth - bRWidth;
				var height = Math.round(shape.bounds.lowerRight.y
							 - shape.bounds.upperLeft.y) + 2*MOVI.util.Marker.PADDING
						 	 - bTWidth - bBWidth;				
			
				rect.setStyle("left", left + "px");
				rect.setStyle("top", top + "px");
				rect.setStyle("width", width + "px");
				rect.setStyle("height", height + "px");
				
				rect.set("className", this._className);
			}
		},
		
		/**
	     * Wrapper for setting style properties of all marking rectangle elements
	     * @method setRectStyle
		 * @param {String} property The property
		 * @param {String} value The value
	     */
		setRectStyle: function(property, value) {
			this._style[property] = value;
			this._update();
		},
		
		/**
	     * Returns the value for the specified style property that is applied to each 
		 * marking rectangle element
	     * @method getRectStyle
		 * @param {String} property The property
		 * @returns {Object} A key map storing the style properties
	     */
		getRectStyle: function(property) {
			return this._style[property];
		},
		
		/**
	     * Wrapper for setting the class name for all marking rectangle elements
	     * @method setRectClassName
		 * @param {String} className The class name value
	     */
		setRectClassName: function(className) {
			this._className = className;
			this._update();
		},
		
		/**
	     * Return the class name applied for all marking rectangle elements
	     * @method getRectClassName
		 * @returns {String} The applied class name
	     */
		getRectClassName: function() {
			return this._className;
		},
		
		/**
	     * Add a shape to the marker
	     * @method addShape
		 * @param {Shape} shape The shape object to add
	     */
		addShape: function(shape) {
			if(this._shapes[shape.resourceId])
				return;
			
			this._shapes[shape.resourceId] = shape;
			var rect = new YAHOO.util.Element(document.createElement('div'));
			rect.set("className", this.getRectClassName());
			rect.setStyle("position", "absolute");
			shape.appendChild(rect);
			this.shapeRects[shape.resourceId] = rect;
			this._update();
		},
		
		/**
	     * Remove a shape from the marker
	     * @method removeShape
		 * @param {Shape} shape The shape object to be removed
	     */
		removeShape: function(shape) {
			shape.removeChild(this.shapeRects[shape.resourceId]);
			delete this.shapeRects[shape.resourceId];
			delete this._shapes[shape.resourceId];
			this._update();
		},
		
		/**
	     * Remove all shapes from the marker
	     * @method removeAllShapes
	     */
		removeAllShapes: function() {
			for(key in this._shapes) {
				this._shapes[key].removeChild(this.shapeRects[key]);
				delete this.shapeRects[key];
				delete this._shapes[key];
			}
			this._update();
		},
		
		/**
	     * Return the marked shapes
	     * @method getShapes
		 * @param {[Shape]} An array of the marked Shape objects
	     */
		getShapes: function() {
			var marked = new Array();
			for(key in this._shapes)
				marked.push(this._shapes[key]);
			return marked;
		},
		
		/**
	     * Convenience method to show all marking rectangles
	     * @method show
	     */
		show: function() {
			this.setRectStyle("display", "block");
		},
		
		/**
	     * Convenience method to hide all marking rectangles
	     * @method hide
	     */
		hide: function() {
			this.setRectStyle("display", "none");
		},
		
		/**
	     * Remove the marker elements from the DOM
	     * @method remove
	     */
		remove: function() {
			for(i in this._shapes) {
				var rect = this.shapeRects[i];
				rect.get("element").parentNode.removeChild(rect.get("element"));
				delete this._shapes[i];
				delete this.shapeRects[i];
			}
			
		},
		
	}
	
})();
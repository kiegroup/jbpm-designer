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
	
	var _MARKER_RECT_CLASS_NAME = "movi-marker",
		_ICON_CLASS_NAME = "movi-marker-icon",
		_ICON_MARGIN = -10; 
	
	/**
     * Attach Marker objects to the model to highlight a shape or a set of shapes
	 * by overlaying rectangles
     * @namespace MOVI.util
     * @class MOVI.util.Marker
     * @constructor
	 * @param {Shape|Shapes[]} shapes The shapes to mark
	 * @param {Object} style (optional) A key map of CSS style properties to be attached
	 * to the shape marking rectangles.
     */
    MOVI.util.Marker = function(shapes, style) {
		
		if(!shapes) shapes = [];
		if(!YAHOO.lang.isArray(shapes)) shapes = [shapes];
		
		this._icons = {
			north: null,
			west: null,
			south: null,
			east: null,
			northwest: null,
			southwest: null,
			northeast: null,
			southeast: null
		};
		
		this._style = style || {};
		this.shapeRects = {};

		this._shapes = {};
		// create shape rect elements
		for(var i = 0; i < shapes.length; i++) {
			var s = shapes[i];
			this._shapes[s.resourceId] = s;
			this.shapeRects[s.resourceId] = new YAHOO.util.Element(document.createElement('div'));
			this.shapeRects[s.resourceId].setStyle("position", "absolute");
			s.appendChild(this.shapeRects[s.resourceId]);
		}
		
		this.markerRect = new YAHOO.util.Element(document.createElement('div'));
		this.markerRect.set("className", _MARKER_RECT_CLASS_NAME);
	
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
		_style: null,
		
		/**
		 * The class name that is specified for each shape marking rectangle.
		 * @property _className
		 * @type String
		 * @private
		 */
		_className: "",
		
		/**
		 * A key map containing all marked shapes with their resource IDs as keys
		 * @property _shapes
		 * @type Object
		 * @private
		 */
		_shapes: null,
		
		/**
		 * The callback object to be executed when the marker has changed
		 * @property _changedCallback
		 * @type Object
		 * @private
		 */
		_changedCallback: null,
		
		/**
		 * The callback object to be executed when the canvas object the marker belongs to is available
		 * @property _canvasAvailableCallback
		 * @type Object
		 * @private
		 */
		_canvasAvailableCallback: null,
		
		/**
		 * A hash map containing the icon elements with their orientation as key
		 * @property _icons
		 * @type Object
		 * @private
		 */
		_icons: null,
		
		/**
		 * A key map containing all shape rectangle elements of the marker with the
		 * associated shape resource IDs as keys
		 * @property shapeRects
		 * @type { Integer : Element }
		 */
		shapeRects: null,
		
		/**
		 * The marker's outer rectangle element
		 * @property markerRect
		 * @type Element
		 */
		markerRect: null,
		
		/**
		 * The parent canvas element of this marker
		 * @property canvas
		 * @type Canvas
		 */
		canvas: null,
		
		/**
	     * Update style properties of the shape rectangle elements and update the bounds of the
		 * outer marking rectangle element
	     * @method _update
	     * @private
	     */
		_update: function() {
			
			var canvas = null;
			
			/* update shape marking rectangle elements */
			
			for(i in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, i)) continue;
				
				var shape = this._shapes[i];
				var rect = this.shapeRects[i];
				
				if(canvas==null) canvas = shape.getCanvas();
				var zoomFactor = canvas.getModelViewer().getZoomLevel() / 100;
				
				// apply styles
				for(prop in this._style) {
					if(!YAHOO.lang.hasOwnProperty(this._style, prop)) continue;
					rect.setStyle(prop, this._style[prop]);
				}
				
				/* adjust position */
				
				// get border widths
				var bTWidth = parseInt(rect.getStyle("border-top-width")),
					bRWidth = parseInt(rect.getStyle("border-right-width")),
					bBWidth = parseInt(rect.getStyle("border-bottom-width")),
					bLWidth = parseInt(rect.getStyle("border-left-width"));
							
				var left = - MOVI.util.Marker.PADDING;
				var top = - MOVI.util.Marker.PADDING;
				var width = Math.round((shape.bounds.lowerRight.x
							- shape.bounds.upperLeft.x)*zoomFactor) + 2*MOVI.util.Marker.PADDING
							- bLWidth - bRWidth;
				var height = Math.round((shape.bounds.lowerRight.y
							 - shape.bounds.upperLeft.y)*zoomFactor) + 2*MOVI.util.Marker.PADDING
						 	 - bTWidth - bBWidth;				
			
				rect.setStyle("left", left + "px");
				rect.setStyle("top", top + "px");
				rect.setStyle("width", width + "px");
				rect.setStyle("height", height + "px");
				
				rect.set("className", this._className);
			}
			
			/* update outer marking rectangle element */
			
			if(canvas!=null) {
				if(this.canvas==null) {
					this.canvas = canvas;
					
					// canvas is now set for the marker because the first shape has been added
					// now we can append the elements to the dom 
					// (all marked shapes have to belong to the same canvas)
					this.canvas.appendChild(this.markerRect); 
					for(orientation in this._icons) {
						if(!YAHOO.lang.hasOwnProperty(this._icons, orientation)) continue;
						
						if(this._icons[orientation])
							this.canvas.appendChild(this._icons[orientation]); 
					}
					
					// now that we have our canvas and therefore the model viewer we can subscribe
					// to the zoom event
					this.canvas.getModelViewer().onZoomLevelChangeStart.subscribe(function() {
						if(this.markerRect.getStyle("display")=="none") {
							this._showOnZoomEnd = false;
						} else {
							this.hide();
							this._showOnZoomEnd = true;
						}
					}, this, true);
					this.canvas.getModelViewer().onZoomLevelChangeEnd.subscribe(function() {
						this._update();
						if(this._showOnZoomEnd) this.show();
					}, this, true);
					
					this._onCanvasAvailable();
				}
				
				// get border widths
				var bTWidth = parseInt(this.markerRect.getStyle("border-top-width")),
					bRWidth = parseInt(this.markerRect.getStyle("border-right-width")),
					bBWidth = parseInt(this.markerRect.getStyle("border-bottom-width")),
					bLWidth = parseInt(this.markerRect.getStyle("border-left-width"));

				var left = Math.round(this.getAbsBounds().upperLeft.x)*zoomFactor - MOVI.util.Marker.PADDING;
				var top = Math.round(this.getAbsBounds().upperLeft.y)*zoomFactor - MOVI.util.Marker.PADDING;
				var width = Math.round((this.getAbsBounds().lowerRight.x
							- this.getAbsBounds().upperLeft.x)*zoomFactor) + 2*MOVI.util.Marker.PADDING
							- bLWidth - bRWidth;
				var height = Math.round((this.getAbsBounds().lowerRight.y
							 - this.getAbsBounds().upperLeft.y)*zoomFactor) + 2*MOVI.util.Marker.PADDING
						 	 - bTWidth - bBWidth;

				this.markerRect.setStyle("left", left + "px");
				this.markerRect.setStyle("top", top + "px");
				this.markerRect.setStyle("width", width + "px");
				this.markerRect.setStyle("height", height + "px");
			}
			
			/* update icons */
			this._updateIcons()
		},
		
		/**
	     * Update positions of the icons appended to the marker
	     * @method _updateIcons
	     * @private
	     */
		_updateIcons: function() {
			
			if(this.canvas)
				var zoomFactor = this.canvas.getModelViewer().getZoomLevel() / 100;
			else
				var zoomFactor = 1.0;
			
			var bounds = this.getAbsBounds();
			for(orientation in this._icons) {
				if(!YAHOO.lang.hasOwnProperty(this._icons, orientation)) continue;
				
				var left, top, margin;
				var icon = this._icons[orientation];
				if(icon) {
					
					var width = parseInt(icon.getStyle("width"), 10); 
					var height = parseInt(icon.getStyle("height"), 10);

					if(orientation=="north") {
						left = Math.round(bounds.upperLeft.x*zoomFactor + ((bounds.lowerRight.x-bounds.upperLeft.x)*zoomFactor - width)/2);
						top = bounds.upperLeft.y*zoomFactor - height;
						margin = -_ICON_MARGIN + "px 0 0 0";
					} else if(orientation=="west") {
						left = bounds.lowerRight.x*zoomFactor;
						top = Math.round(bounds.upperLeft.y*zoomFactor + ((bounds.lowerRight.y-bounds.upperLeft.y)*zoomFactor - height)/2);
						margin = "0 0 0 " + _ICON_MARGIN + "px";
					} else if(orientation=="south") {
						left = Math.round(bounds.upperLeft.x*zoomFactor + ((bounds.lowerRight.x-bounds.upperLeft.x)*zoomFactor - width)/2);
						top = bounds.lowerRight.y*zoomFactor;
						margin = _ICON_MARGIN + "px 0 0 0";
					} else if(orientation=="east") {
						left = bounds.upperLeft.x*zoomFactor - width;
						top = Math.round(bounds.upperLeft.y*zoomFactor + ((bounds.lowerRight.y-bounds.upperLeft.y)*zoomFactor - height)/2);
						margin = "0 0 0 " + -_ICON_MARGIN + "px";
					} else if(orientation=="northwest") {
						left = bounds.lowerRight.x*zoomFactor;
						top = bounds.upperLeft.y*zoomFactor - height;
						margin = -_ICON_MARGIN + "px 0 0 " + _ICON_MARGIN + "px";
					} else if(orientation=="southwest") {
						left = bounds.lowerRight.x*zoomFactor;
						top = bounds.lowerRight.y*zoomFactor;
						margin = _ICON_MARGIN + "px 0 0 " + _ICON_MARGIN + "px";
					} else if(orientation=="northeast") {
						left = bounds.upperLeft.x*zoomFactor - width;
						top = bounds.upperLeft.y*zoomFactor - height;
						margin = -_ICON_MARGIN + "px 0 0 " + -_ICON_MARGIN + "px";
					} else if(orientation=="southeast") {
						left = bounds.upperLeft.x*zoomFactor - width;
						top = bounds.lowerRight.y*zoomFactor;
						margin = _ICON_MARGIN + "px 0 0 " + -_ICON_MARGIN + "px";
					}
					
					icon.setStyle("left", left + "px");
					icon.setStyle("top", top + "px");
					icon.setStyle("margin", margin);
				}
			}
		},
		
		/**
	     * Executes the user-specified callback when the marker changed
	     * @method _onChanged
		 * @private
	     */
		_onChanged: function() {
			if(!this._changedCallback) return;
			this._changedCallback.callback.call(
				this._changedCallback.scope,
				this,
				this._changedCallback.data
			);
		},
		
		/**
	     * Executes the user-specified callback when the canvas object the marker belongs to is available
	     * @method _onCanvasAvailable
		 * @private
	     */
		_onCanvasAvailable: function() {
			if(!this._canvasAvailableCallback) return;
			this._canvasAvailableCallback.callback.call(
				this._canvasAvailableCallback.scope,
				this,
				this._canvasAvailableCallback.data
			);
		},
		
		/**
	     * Wrapper for setting style properties of all shape marking rectangle elements
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
		 * shape marking rectangle element
	     * @method getRectStyle
		 * @param {String} property The property
		 * @returns {Object} A key map storing the style properties
	     */
		getRectStyle: function(property) {
			return this._style[property];
		},
		
		/**
	     * Wrapper for setting the class name for all shape marking rectangle elements
	     * @method setRectClassName
		 * @param {String} className The class name value
	     */
		setRectClassName: function(className) {
			this._className = className;
			this._update();
		},
		
		/**
	     * Return the class name applied for all shape marking rectangle elements
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
			this._onChanged();
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
			this._onChanged()
		},
		
		/**
	     * Remove all shapes from the marker
	     * @method removeAllShapes
	     */
		removeAllShapes: function() {
			for(key in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, key)) continue;
				
				this._shapes[key].removeChild(this.shapeRects[key]);
				delete this.shapeRects[key];
				delete this._shapes[key];
			}
			this._update();
			this._onChanged()
		},
		
		/**
	     * Return the marked shapes
	     * @method getShapes
		 * @param {[Shape]} An array of the marked Shape objects
	     */
		getShapes: function() {
			var marked = new Array();
			for(key in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, key)) continue;
				marked.push(this._shapes[key]);
			}
				
			return marked;
		},
		
		/**
	     * Show the marker
	     * @method show
	     */
		show: function() {
			this.markerRect.setStyle("display", "block");
			this.setRectStyle("display", "block");
			for(orientation in this._icons) {
				if(!YAHOO.lang.hasOwnProperty(this._icons, orientation)) continue;
				if(this._icons[orientation]) 
					this._icons[orientation].setStyle("display", "block");
			}
		},
		
		/**
	     * Hide the marker
	     * @method hide
	     */
		hide: function() {
			this.markerRect.setStyle("display", "none");
			this.setRectStyle("display", "none");
			for(orientation in this._icons) {
				if(!YAHOO.lang.hasOwnProperty(this._icons, orientation)) continue;
				if(this._icons[orientation]) 
					this._icons[orientation].setStyle("display", "none");
			}
		},
		
		/**
	     * Toggle show/hide the marker
	     * @method toggle
	     */
		toggle: function() {
			if(this.markerRect.getStyle("display")=="none")
				this.show();
			else 
				this.hide();
		},
		
		/**
	     * Remove the marker elements from the DOM
	     * @method remove
	     */
		remove: function() {
			for(i in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, i)) continue;
				
				var rect = this.shapeRects[i];
				rect.get("element").parentNode.removeChild(rect.get("element"));
				delete this._shapes[i];
				delete this.shapeRects[i];
			}
			this.markerRect.get("element").parentNode.removeChild(this.markerRect.get("element"));
			delete this.markerRect;
		},
		
		/**
	     * Returns the marker's absolute bounds coordinates. 'Absolute' means
		 * relative to the canvas element rather than relative to the document
	     * @method getAbsBounds
		 * @return {Object} The absolute marker bounds accessible as 
		 * { upperLeft: {x:Number,y:Number}, lowerRight: {x:Number,y:Number} }
	     */
		getAbsBounds: function() {
			var upperLeft  = {x: undefined, y: undefined};
				lowerRight = {x: undefined, y: undefined};
			for(i in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, i)) continue;
				
				var bounds = this._shapes[i].getAbsBounds();
				if(upperLeft.x == undefined || bounds.upperLeft.x < upperLeft.x)
					upperLeft.x = bounds.upperLeft.x;
				if(upperLeft.y == undefined || bounds.upperLeft.y < upperLeft.y)
					upperLeft.y = bounds.upperLeft.y;
				if(lowerRight.x == undefined || bounds.lowerRight.x > lowerRight.x)
					lowerRight.x = bounds.lowerRight.x;
				if(lowerRight.y == undefined || bounds.upperLeft.y > lowerRight.y)
					lowerRight.y = bounds.lowerRight.y;
			}
			return { upperLeft: upperLeft, lowerRight: lowerRight };
		},
		
		/**
		 * Specify callback to be executed when the marker changes
		 * (shapes are added to or removed from the marker)
		 * @param {Function} callback The callback method
		 * @param {Object} scope (optional) The execution scope of the callback 
		 * (in none is specified the context of the Marker object is used)
		 * @param {Object} data (optional) An optional data object to pass to the callback method
		 * @method onChanged
		 */
		onChanged: function(callback, scope, data) {
			if(!YAHOO.lang.isFunction(callback)) {
				throw new TypeError("Specified callback is not a function.", "error", "marker.js");
				return;
			}
			if(!scope) scope = this;
			this._changedCallback = {
				callback: callback,
				scope: scope,
				data: data
			};
		},
		
		/**
		 * Specify callback to be executed when the canvas object the marker belongs to is available
		 * @param {Function} callback The callback method
		 * @param {Object} scope (optional) The execution scope of the callback 
		 * (in none is specified the context of the Marker object is used)
		 * @param {Object} data (optional) An optional data object to pass to the callback method
		 * @method onCanvasAvailable
		 */
		onCanvasAvailable: function(callback, scope, data) {
			if(!YAHOO.lang.isFunction(callback)) {
				throw new TypeError("Specified callback is not a function.", "error", "marker.js");
				return;
			}
			if(!scope) scope = this;
			this._canvasAvailableCallback = {
				callback: callback,
				scope: scope,
				data: data
			};
		},
		
		/**
		 * Append an icon element to the bounds of the marker
		 * @param {String} orientation The orientation where to position the icon.
		 * Possible values: 'north', 'west', 'south', 'east', 'northwest', 'southwest', 'northeast', 'southeast'
		 * @param {HTMLElement|String} icon An HTMLElement object representing the icon, or a String specifying
		 * the URL of an image for the icon.
		 * @return {Element} A reference to the new icon element
		 * @method addIcon
		 */
		addIcon: function(orientation, icon) {
			if(!YAHOO.lang.isString(orientation)) {
				throw new TypeError("The orientation paramter is expected to be of type String", "error", "marker.js");
				return;
			}
			
			if(YAHOO.lang.isString(icon)) {
				var img = new Image();
				var self = this;
				img.onload = function() {
					self._updateIcons.call(self);
				};
				img.src = icon;
				
				icon = new YAHOO.util.Element(img);
			} else {
				icon = new YAHOO.util.Element(icon);
			}
			
			icon.set("className", _ICON_CLASS_NAME);

			for(var key in this._icons) {
				if(!YAHOO.lang.hasOwnProperty(this._icons, key)) continue;
				
				if(key === orientation) {
					if(this._icons[orientation]) this.removeIcon(orientation);
					if(this.canvas) this.canvas.appendChild(icon);
					this._icons[key] = icon;
					this._updateIcons();
					return icon;
				}
			}
			
			throw new Error("The specified orientation is not valid.", "error", "marker.js");
		},
		
		/**
		 * Remove the icon that currently resides in the specified orientation from the marker 
		 * @param {String} orientation The orientation of the icon to remove.
		 * @method removeIcon
		 */
		removeIcon: function(orientation) {
			if(this._icons[orientation] && this.canvas) 
				this.canvas.removeChild(this._icons[orientation]);
			this._icons[orientation] = null;
		},
		
		/**
		 * Returns the icon element at the specified orientation.
		 * @param {String} orientation The orientation of the icon
		 * @return {Element} The icon at the specified orientation (null if no icon resides at that
		 * orientation)
		 * @method getIcon
		 */
		getIcon: function(orientation) {
			return this._icons[orientation];
		}
		
	}
	
})();
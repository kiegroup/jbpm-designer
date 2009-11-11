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
	 * A model overlay polygone.
	 * @namespace MOVI.util
	 * @class MOVI.util.Overlay
	 * @constructor
	 * @param {ModelViewer} modelviewer The parent model viewer
	 * @param {Object[]} coords The coordinates of the polygone
	 * @param {Object} style (optional) A key map of style options. Available keys:
	 * <dl>
     * <dt>color</dt>
     * <dd># HEX color code</dd>
     * <dt>width</dt>
     * <dd>line width unit</dd>
	 * <dt>opacity</dt>
	 * <dd>the opacity value (between 0.0 - not visible and 1.0 - completely opaque)</dd>
	 * </dl>
	 */	
	MOVI.util.Overlay = function(modelviewer, coords, style) {
	
		if(coords.length==0) throw new Error("No coordinates specified for the Overlay!");
		if(!style) style = {};
		
		this.style = {};
		this.style.color = style.color || "#FF0000";
		this.style.width = style.width || 10.0;
		this.style.opacity = style.opacity || 0.4;
		
		this.modelviewer = modelviewer;
		this.coords = coords;
				
		var canvasEl = document.createElement("canvas");
		if(canvasEl.getContext){
			// client's browser supports canvas
			
			var ctx = canvasEl.getContext("2d");
			MOVI.util.Overlay.superclass.constructor.call(this, canvasEl, {});
			this.set("width", parseInt(this.modelviewer.canvas.getStyle("width")));
			this.set("height", parseInt(this.modelviewer.canvas.getStyle("height")));
			
			ctx.beginPath();
			ctx.lineJoin = "round";
			ctx.lineCap = "round";
			ctx.moveTo(coords[0].x, coords[0].y);
			for(var i=1; i<coords.length; i++) {
				ctx.lineTo(coords[i].x, coords[i].y);
			}
			ctx.moveTo(500,800);
			ctx.lineTo(500,1000);
			ctx.strokeStyle = 
				"rgba("+
				parseInt(this.style.color.substring(1,3), 16)+ ","+
				parseInt(this.style.color.substring(3,5), 16)+ ","+
				parseInt(this.style.color.substring(5,7), 16)+ ","+
				this.style.opacity+
				")";
			ctx.lineWidth = this.style.width;
			ctx.stroke();
			
		} else {
			// IE fallback: use VML
			
			// create xmlns
			if(!document.namespaces["vml"]) { 
				var stl = document.createStyleSheet(); 
				stl.addRule("vml\\:*", "behavior: url(#default#VML);"); 
				document.namespaces.add("vml", "urn:schemas-microsoft-com:vml"); 
			 }
			
			var shapeEl = document.createElement("vml:shape");
			shapeEl.setAttribute("coordsize", parseInt(this.modelviewer.canvas.getStyle("width")) + ", " +  parseInt(this.modelviewer.canvas.getStyle("height")));
			MOVI.util.Overlay.superclass.constructor.call(this, shapeEl, {});

			var innerHTML = 
				"<vml:stroke opacity='" + this.style.opacity + "' color='" + this.style.color + "' weight='" + this.style.width +"' joinstyle='round' endcap='round'></vml:stroke>"+
				"<vml:fill opacity='0'></vml:fill>"+
				"<vml:path v='M "+ Math.round(coords[0].x)+","+Math.round(coords[0].y) +" L";
			for(var i=1; i<coords.length; i++) {
				innerHTML += " " + Math.round(coords[i].x)+","+Math.round(coords[i].y);
				if(i!=coords.length-1) innerHTML += ",";
			}
			innerHTML += 
				"'/>";
			this.set("innerHTML", innerHTML);
			
		}
		
		this.setStyle("position", "absolute");
		this.setStyle("left", "0px");
		this.setStyle("top", "0px");
		this.modelviewer.canvas.appendChild(this);
		
		this.update();
	};
	
	MOVI.extend(MOVI.util.Overlay, YAHOO.util.Element, {
		
		/**
		 * Update the overlay according to the current zoom level
		 * @method update
		 */
		update: function() {
			this.setStyle("width", Math.round(parseInt(this.modelviewer.canvas.getStyle("width"))*this.modelviewer.getZoomLevel()/100)+"px");
			this.setStyle("height", Math.round(parseInt(this.modelviewer.canvas.getStyle("height"))*this.modelviewer.getZoomLevel()/100)+"px");
		},
		
		/**
		 * Remove the overlay from the DOM
		 * @method remove
		 */
		remove: function() {
			this.modelviewer.canvas.removeChild(this);
		},
		
		/**
		 * Show the overlay
		 * @method show
		 */
		show: function() {
			this.setStyle("display", "block");
		},
		
		/**
		 * Hide the overlay
		 * @method hide
		 */
		hide: function() {
			this.setStyle("display", "none");
		}
		
	});
	
	/**
     * Attach Marker objects to the model to highlight a shape or a set of shapes
	 * by overlaying rectangles
     * @namespace MOVI.util
     * @class MOVI.util.Marker
     * @constructor
	 * @param {Shape|Shapes[]} shapes The shapes to mark
	 * @param {Object} nodeStyle (optional) A key map of CSS style properties to be attached
	 * to the node marking rectangles.
	 * @param {Object} edgeStyle (optional) A key map of style properties for all edge markings.
	 * Available properties:
	 * <dl>
     * <dt>color</dt>
     * <dd># HEX color code</dd>
     * <dt>width</dt>
     * <dd>line width unit</dd>
	 * <dt>opacity</dt>
	 * <dd>the opacity value (between 0.0 - not visible and 1.0 - completely opaque)</dd>
	 * </dl>
     */
    MOVI.util.Marker = function(shapes, nodeStyle, edgeStyle) {
		
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
		
		this._nodeStyle = nodeStyle || {};
		this._edgeStyle = edgeStyle || {};
		this.overlays = {};

		this._shapes = {};
		// create shape rect elements
		for(var i = 0; i < shapes.length; i++) {
			var s = shapes[i];
			this._shapes[s.resourceId] = s;
			if(s.isNode()) {
				this.overlays[s.resourceId] = new YAHOO.util.Element(document.createElement('div'));
				this.overlays[s.resourceId].setStyle("position", "absolute");
				s.appendChild(this.overlays[s.resourceId]);
			} else {
				this.overlays[s.resourceId] = new MOVI.util.Overlay(s.getCanvas().getModelViewer(), s.getCoordinates(), this._edgeStyle);
			}
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
		 * to each node marking rectangle.
		 * @property _nodeStyle
		 * @type Object
		 * @private
		 */
		_nodeStyle: null,
		
		/**
		 * A key map containing the style property definitions that are applied 
		 * to each edge marking.
		 * @property _edgeStyle
		 * @type Object
		 * @private
		 */
		_edgeStyle: null,
		
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
		 * A key map containing all shape overlay elements of the marker with the
		 * associated shape resource IDs as keys
		 * @property overlays
		 * @type { Integer : Element }
		 */
		overlays: null,
		
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
				
				var overlay = this.overlays[i];
				
				if(canvas==null) canvas = shape.getCanvas();
				var zoomFactor = canvas.getModelViewer().getZoomLevel() / 100;
				
				if(shape.isEdge()) {
					overlay.update();
					continue;
				}
				
				/* following code is only executed for node overlays */
				
				// apply styles
				for(prop in this._nodeStyle) {
					if(!YAHOO.lang.hasOwnProperty(this._nodeStyle, prop)) continue;
					overlay.setStyle(prop, this._nodeStyle[prop]);
				}
				
				/* adjust position */
				
				// get border widths
				var bTWidth = parseInt(overlay.getStyle("border-top-width") || 0),
					bRWidth = parseInt(overlay.getStyle("border-right-width") || 0),
					bBWidth = parseInt(overlay.getStyle("border-bottom-width") || 0),
					bLWidth = parseInt(overlay.getStyle("border-left-width") || 0);
							
				bTWidth	= isNaN(bTWidth) ? 0 : bTWidth;
				bRWidth	= isNaN(bRWidth) ? 0 : bRWidth;
				bBWidth	= isNaN(bBWidth) ? 0 : bBWidth;
				bLWidth	= isNaN(bLWidth) ? 0 : bLWidth;
				
				var left = - MOVI.util.Marker.PADDING;
				var top = - MOVI.util.Marker.PADDING;
				var width = Math.round((shape.bounds.lowerRight.x
							- shape.bounds.upperLeft.x)*zoomFactor) + 2*MOVI.util.Marker.PADDING
							- bLWidth - bRWidth;
				var height = Math.round((shape.bounds.lowerRight.y
							 - shape.bounds.upperLeft.y)*zoomFactor) + 2*MOVI.util.Marker.PADDING
						 	 - bTWidth - bBWidth;				
			
				overlay.setStyle("left", left + "px");
				overlay.setStyle("top", top + "px");
				overlay.setStyle("width", width + "px");
				overlay.setStyle("height", height + "px");
				
				overlay.set("className", this._className);
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
				var bTWidth = parseInt(this.markerRect.getStyle("border-top-width")||0),
					bRWidth = parseInt(this.markerRect.getStyle("border-right-width")||0),
					bBWidth = parseInt(this.markerRect.getStyle("border-bottom-width")||0),
					bLWidth = parseInt(this.markerRect.getStyle("border-left-width")||0);
				
				bTWidth	= isNaN(bTWidth) ? 0 : bTWidth;
				bRWidth	= isNaN(bRWidth) ? 0 : bRWidth;
				bBWidth	= isNaN(bBWidth) ? 0 : bBWidth;
				bLWidth	= isNaN(bLWidth) ? 0 : bLWidth;
				
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
						top = Math.round(bounds.upperLeft.y*zoomFactor - height);
						margin = -_ICON_MARGIN + "px 0 0 0";
					} else if(orientation=="east") {
						left = bounds.lowerRight.x*zoomFactor;
						top = Math.round(bounds.upperLeft.y*zoomFactor + ((bounds.lowerRight.y-bounds.upperLeft.y)*zoomFactor - height)/2);
						margin = "0 0 0 " + _ICON_MARGIN + "px";
					} else if(orientation=="south") {
						left = Math.round(bounds.upperLeft.x*zoomFactor + ((bounds.lowerRight.x-bounds.upperLeft.x)*zoomFactor - width)/2);
						top = bounds.lowerRight.y*zoomFactor;
						margin = _ICON_MARGIN + "px 0 0 0";
					} else if(orientation=="west") {
						left = bounds.upperLeft.x*zoomFactor - width;
						top = Math.round(bounds.upperLeft.y*zoomFactor + ((bounds.lowerRight.y-bounds.upperLeft.y)*zoomFactor - height)/2);
						margin = "0 0 0 " + -_ICON_MARGIN + "px";
					} else if(orientation=="northeast") {
						left = bounds.lowerRight.x*zoomFactor;
						top = bounds.upperLeft.y*zoomFactor - height;
						margin = -_ICON_MARGIN + "px 0 0 " + _ICON_MARGIN + "px";
					} else if(orientation=="southeast") {
						left = bounds.lowerRight.x*zoomFactor;
						top = bounds.lowerRight.y*zoomFactor;
						margin = _ICON_MARGIN + "px 0 0 " + _ICON_MARGIN + "px";
					} else if(orientation=="northwest") {
						left = bounds.upperLeft.x*zoomFactor - width;
						top = bounds.upperLeft.y*zoomFactor - height;
						margin = -_ICON_MARGIN + "px 0 0 " + -_ICON_MARGIN + "px";
					} else if(orientation=="southwest") {
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
	     * Wrapper for setting style properties of all node marking rectangle elements
	     * @method setNodeStyle
		 * @param {String} property The property
		 * @param {String} value The value
	     */
		setNodeStyle: function(property, value) {
			this._nodeStyle[property] = value;
			this._update();
		},
		
		/**
	     * Returns the value for the specified style property that is applied to each 
		 * node marking rectangle element
	     * @method getNodeStyle
		 * @param {String} property The property
		 * @returns {String} The value of the property
	     */
		getNodeStyle: function(property) {
			return this._nodeStyle[property];
		},
		
		/**
	     * Wrapper for setting the class name for all node marking rectangle elements
	     * @method setNodeClassName
		 * @param {String} className The class name value
	     */
		setNodeClassName: function(className) {
			this._className = className;
			this._update();
		},
		
		/**
	     * Return the class name applied for all node marking rectangle elements
	     * @method getNodeClassName
		 * @returns {String} The applied class name
	     */
		getNodeClassName: function() {
			return this._className;
		},
		
		/**
	     * Deprecated alias for setNodeClassName
	     * @method setRectStyle
	     */
		setRectStyle: function(property, value) {
			this.setNodeStyle(property, value);
		},
		
		/**
	     * Deprecated alias for setNodeClassName
	     * @method getRectStyle
	     */
		getRectStyle: function(property) {
			return this.getNodeStyle(property);
		},
		
		/**
	     * Deprecated alias for setNodeClassName
	     * @method setRectClassName
	     */
		setRectClassName: function(className) {
			this.setNodeClassName(className);
		},
		
		/**
	     * Deprecated alias for getNodeClassName
	     * @method getRectClassName
	     */
		getRectClassName: function() {
			return this.getNodeClassName();
		},
		
		/**
	     * Wrapper for setting style properties of all edge markings
	     * @method setEdgeStyle
		 * @param {String} property The property
		 * Available properties:
		 * <dl>
	     * <dt>color</dt>
	     * <dd># HEX color code</dd>
	     * <dt>width</dt>
	     * <dd>line width unit</dd>
		 * <dt>opacity</dt>
		 * <dd>the opacity value (between 0.0 - not visible and 1.0 - completely opaque)</dd>
		 * </dl>
		 * @param {String} value The value
	     */		
		setEdgeStyle: function(property) {
			this._edgeStyle[property] = value;
			for(key in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, key)) continue;
				
				this.overlays[key].remove();
				var coords = this.overlays[key].coords;
				var modelviewer = this.overlays[key].modelviewer;
				delete this.overlays[key];
				this.overlays[key] = new MOVI.util.Overlay(modelviewer, coords, this._edgeStyle);
			}
		},
		
		/**
	     * Returns the value for the specified style property that is applied to each 
		 * edge marking 
	     * @method getEdgeStyle
		 * @param {String} property The property
		 * Available properties:
		 * <dl>
	     * <dt>color</dt>
	     * <dd># HEX color code</dd>
	     * <dt>width</dt>
	     * <dd>line width unit</dd>
		 * <dt>opacity</dt>
		 * <dd>the opacity value (between 0.0 - not visible and 1.0 - completely opaque)</dd>
		 * </dl>
		 * @returns {String} The value of the property
	     */
		getEdgeStyle: function(property) {
			return this._edgeStyle[property];
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
			
			if(shape.isNode()) {
				var rect = new YAHOO.util.Element(document.createElement('div'));
				rect.set("className", this.getRectClassName());
				rect.setStyle("position", "absolute");
				shape.appendChild(rect);
				this.overlays[shape.resourceId] = rect;
			} else {
				this.overlays[s.resourceId] = new MOVI.util.Overlay(shape.getCanvas().getModelViewer(), shape.getCoordinates());
			}
			
			this._update();
			this._onChanged();
		},
		
		/**
	     * Remove a shape from the marker
	     * @method removeShape
		 * @param {Shape} shape The shape object to be removed
	     */
		removeShape: function(shape) {
			if(shape.isNode())
				shape.removeChild(this.overlays[shape.resourceId]);
			else
				this.overlays[shape.resourceId].remove();
			delete this.overlays[shape.resourceId];
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
				
				if(this._shapes[key].isNode())
					this._shapes[key].removeChild(this.overlays[key]);
				else
					this.overlays[key].remove();
				
				delete this.overlays[key];
				delete this._shapes[key];
			}
			this._update();
			this._onChanged()
		},
		
		/**
	     * Return all marked shapes
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
			for(key in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, key)) continue;
				if(this._shapes[key].isEdge()) this.overlays[key].show();
			}
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
			for(key in this._shapes) {
				if(!YAHOO.lang.hasOwnProperty(this._shapes, key)) continue;
				if(this._shapes[key].isEdge()) this.overlays[key].hide();
			}
			for(orientation in this._icons) {
				if(!YAHOO.lang.hasOwnProperty(this._icons, orientation)) continue;
				if(this._icons[orientation]) 
					this._icons[orientation].setStyle("display", "none");
			}
		},
		
		/**
		 * Fade in the rec
		 */		
		fadeIn: function() {
			if(YAHOO.env.ua.ie){ return }
			for( key in this.overlays ){
				if (typeof key != "string") {return}
				this.overlays[key].setStyle("opacity", 0);	
				var anim = new YAHOO.util.ColorAnim(this.overlays[key], { opacity: { to: 1 } }, 0.4, YAHOO.util.Easing.easeOut);
				anim.animate();	
			}
		},
		
		
		/**
		 * Fade out the rectangle, call fn when complete
		 * @param {Object} fn
		 */
		fadeOut: function(fn){
			if(YAHOO.env.ua.ie){ 
				if (fn instanceof Function) { fn() }
				return 
			}
			var anim;
			for( key in this.overlays ){
				if (typeof key != "string") {return}
				anim = new YAHOO.util.ColorAnim(this.overlays[key], { opacity: { to: 0 } }, 0.4, YAHOO.util.Easing.easeOut);
				anim.animate();	
			}
			if (anim && fn instanceof Function){
				anim.onComplete.subscribe(fn) 
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
	     * Remove all marker elements from the DOM
	     * @method remove
	     */
		remove: function() {
			this.removeAllShapes();
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
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
	
	var _SELECT_RECT_CLASS_NAME = "movi-select-rect",
		_HIGHLIGHT_RECT_CLASS_NAME = "movi-highlight-rect";
		
	var Marker 	= MOVI.util.Marker,
		Event 	= YAHOO.util.Event; 
	
	/**
     * Enbable shape selection for the specified model viewer
     * @namespace MOVI.util
     * @class MOVI.util.ShapeSelect
     * @constructor
	 * @param {ModelViewer} modelviewer The ModelViewer for that shape selection is enabled
	 * @param {Shape*} shapes (optional) The subset of shapes that are selectable. If not specified 
	 * all shapes are selectable.
	 * @param {Boolean} multiselect (optional) If set to true, multiple shapes can be selected (default is false).
     */
    MOVI.util.ShapeSelect = function(modelviewer, shapes, multiselect) {
	
		if(!modelviewer) {
			throw new Error("No model viewer specified for shape select.", "error", "shapeselect.js");
			return false;
		}
		
		if(!YAHOO.lang.isArray(shapes)) {
			if(shapes==true) multiselect = true;
			shapes = modelviewer.canvas.getNodes(); // only nodes supported atm
		}
		
		this._allowMultiselect = multiselect;
		
		this._selectableShapes = {},
		this._selectedShapes = {},
		this._highlightMarkers = {},
		
		
		for(key in shapes) {
			var shape = modelviewer.canvas.shapes[key];
			
			if(shape.parentShape!=null) {
				shape.addListener("mouseover", this._onMouseOver, shape, this);
				shape.addListener("mouseout", this._onMouseOut, shape, this);
				shape.addListener("click", this._onClick, shape, this);
				this._selectableShapes[shape.resourceId] = shape;
			}
			
		}
		
		modelviewer.canvas.addListener("click", this.reset, null, this)
		
		this._selectionMarker = new Marker();
		this._selectionMarker.setRectClassName(_SELECT_RECT_CLASS_NAME);
		this._selectionMarker.show();
		this._init();
	};
	
	MOVI.util.ShapeSelect.prototype = {
		
		_selectableShapes: null,
		
		_selectedShapes: null,
		
		_highlightMarkers: null,
		
		_selectionMarker: null,
		
		_selectionChangedCallback: null,
		
		_allowMultiselect: false,
		
		_init: function() {
			// create highlighting markers
			for(key in this._selectableShapes) {
				var s = this._selectableShapes[key];
				var marker = new Marker(s);
				marker.setRectClassName(_HIGHLIGHT_RECT_CLASS_NAME);
				marker.hide();
				this._highlightMarkers[s.resourceId] = marker;
			}
		},
		
		_onMouseOver: function(ev, shape) {
			Event.stopPropagation(ev);
		
			if(!this._selectedShapes[shape.resourceId])
				this.highlight(shape);
		},
		
		_onMouseOut: function(ev, shape) {
			Event.stopPropagation(ev);
		
			if(!this._selectedShapes[shape.resourceId])
				this.unhighlight(shape);
		},
		
		_onClick: function(ev, shape) {
			Event.stopPropagation(ev);
			
			if(this._selectedShapes[shape.resourceId]) {
				this.deselect(shape);
			} else {
				if(!this._allowMultiselect)
					this._reset();
				this.select(shape);	
			}
				
		},
		
		_onSelectionChanged: function() {
			if(!this._selectionChangedCallback) return;
			this._selectionChangedCallback.callback.call(
				this._selectionChangedCallback.scope,
				this,
				this._selectionChangedCallback.data
			);
		},
		
		_reset: function() {
			for(key in this._selectedShapes) {
				var s = this._selectedShapes[key];
				this._selectionMarker.removeShape(s);
				delete this._selectedShapes[key];
			}
		},
		
		/**
		 * Add the specified shapes to the current selection
		 * @method select
		 * @param {[Shape] | Shape} shapes The shapes to add to the selection
		 */
		select: function(shapes) {
			if(!YAHOO.lang.isArray(shapes)) shapes = [shapes];
			
			for(key in shapes) {
				var s = shapes[key];
				
				this.unhighlight(s);
			
				if(!this._selectableShapes[s.resourceId]) {
					MOVI.log("Specified shape with resource id " + s.resourceId + 
							 " is not selectable.", "warn", "shapeselect.js");
					continue;
				}
		
				this._selectedShapes[s.resourceId] = s;
				this._selectionMarker.addShape(s);
				
			}
			
			this._onSelectionChanged();
		}, 
		
		/**
		 * Remove the specified shapes from the current selection
		 * @method deselect
		 * @param {[Shape] | Shape} shapes The shapes to remove from the selection
		 */
		deselect: function(shapes) {
			if(!YAHOO.lang.isArray(shapes)) shapes = [shapes];
			
			for(key in shapes) {
				var s = shapes[key];
				delete this._selectedShapes[s.resourceId];
				this._selectionMarker.removeShape(s);
			}
			
			this._onSelectionChanged();
		},
		
		/**
		 * Highlight the specified shape by showing the highlighting marker
		 * @method highlight
		 * @param {Shape} shape The shape to be highlighted
		 */
		highlight: function(shape) {
			this._highlightMarkers[shape.resourceId].show();
		},
		
		/**
		 * Unhighlight the specified shape by hiding the highlighting marker
		 * @method unhighlight
		 * @param {Shape} shape The shape to be unhighlighted
		 */
		unhighlight: function(shape) {
			this._highlightMarkers[shape.resourceId].hide();
		},
		
		/**
		 * Reset the current selection
		 * @method reset
		 */
		reset: function() {
			if(this.getSelectedShapes().length==0)
				return;
			this._reset();
			this._onSelectionChanged();
		},
		
		/**
		 * Returns the currently selected shapes
		 * @method getSelectShapes
		 * @returns {[Shape]} An array of selected Shape objects
		 */
		getSelectedShapes: function() {
			var selected = new Array();
			for(key in this._selectedShapes)
				selected.push(this._selectedShapes[key]);
			return selected;
		},
		
		/**
		 * Returns the marker of the current selection
		 * @method getSelectionMarker
		 * @returns {Marker} The selection marker
		 */
		getSelectionMarker: function() {
			return this._selectionMarker;
		},
		
		/**
		 * Specify callback to be executed when the selection changes
		 * (shapes are added to or removed from the current selection)
		 * Example: 
		 * @param {Function} callback The callback method
		 * @param {Object} scope (optional) The execution scope of the callback 
		 * (in none is specified the context of the ShapeSelect object is used)
		 * @param {Any} data (optional) An optional data object to pass to the callback method
		 * @method onSelectionChanged
		 */
		onSelectionChanged: function(callback, scope, data) {
			if(!YAHOO.lang.isFunction(callback)) {
				throw new TypeError("Specified callback is not a function.", "error", "shapeselect.js");
				return;
			}
			if(!scope) scope = this;
			this._selectionChangedCallback = {
				callback: callback,
				scope: scope,
				data: data
			};
		}
		
	}
	
	
})();
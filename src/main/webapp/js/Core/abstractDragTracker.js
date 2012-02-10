/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
if(!ORYX){ var ORYX = {} }
if(!ORYX.Plugins){ ORYX.Plugins = {} }

/**
   This abstract plugin implements the core behaviour of layout
   
   @class ORYX.Plugins.AbstractLayouter
   @constructor Creates a new instance
   @author Willi Tscheschner
*/
ORYX.Plugins.AbstractDragTracker = ORYX.Plugins.AbstractPlugin.extend({
	
	/**
	 * 'shapes' defined all types of shapes which will be passed to the drag tracker. 
	 * It can be one value or an array of values. The value
	 * can be a Stencil ID (as String) or an class type of either 
	 * a ORYX.Core.Node or ORYX.Core.Edge
     * @type Array|String|Object
	 */
	shapes : [null],
	
	/**
	 * Constructor
	 * @param {Object} facade
	 */
	construct: function( facade ){
		arguments.callee.$.construct.apply(this, arguments);
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAG_TRACKER_DRAG, function(event) {
			if (this.isIncludedInShapes(event.shapes)) {
				this.drag(event.shapes, event.bounds);
			}
		}.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAG_TRACKER_RESIZE, function(event) {
			if (this.isIncludedInShapes(event.shapes)) {
				this.resize(event.shapes, event.bounds);
			}
		}.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, function(event) {
			if (this.isIncludedInShapes(event.shapes)) {
				this.resizeEnd(event.shapes);
			}
		}.bind(this));
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DROP_SHAPE, function(event) {
			if (this.isIncludedInShapes(event.shape)) {
				this.newShape(event.shape);
			}
		}.bind(this));
	},
	
	/**
	 * Implementation of dragging a set on shapes
	 * @param {Object} shapes Given shapes
     * @memberOf ORYX.Plugins.AbstractDragTracker.prototype
	 */
	drag: function(shapes, bounds){
	},
	
	
	/**
	 * Hook to intervene in the resize, during the resizing effort.
	 * @param {Object} shapes Given shapes
     * @memberOf ORYX.Plugins.AbstractDragTracker.prototype
	 */
	resize: function(shapes, bounds){
	},
	
	/**
	 * Hook to complement the resize of a shape (after the resize)
	 * @param {Object} shapes Given shapes
     * @memberOf ORYX.Plugins.AbstractDragTracker.prototype
	 */
	resizeEnd: function(shapes){
	},
	
	/**
	 * Hook to intervene after a new shape has been created.
	 * @param shape
	 * @returns
	 */
	newShape: function(shape) {
	},
	
	/**
	 * Proofs if this shape should be processed by the drag tracker or not
	 * @param {Object} shape
	 */
	isIncludedInShapes: function(shape){
		if (shape instanceof Array) {
			included = false;
			shape.each(function(s) {
				if (this.isIncludedInShapes(s)) {
					included = true;
					return;
				}
			}.bind(this));
			return included;
		}
		
		if (!(this.shapes instanceof Array)){
			this.shapes = [this.shapes].compact();
		}
		
		// If there are no elements
		if (this.shapes.length <= 0) {
			// Return TRUE
			return true;
		}
		
		// Return TRUE if there is any correlation between 
		// the 'shapes' attribute and the shape themselve.
		return this.shapes.any(function(s){
			if (typeof s == "string") {
				return shape.getStencil().id().include(s);
			} else {
				return shape instanceof s;
			}
		});
	}
});
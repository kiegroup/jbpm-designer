/**
 * Copyright (c) 2010
 * Antoine Toulme, Intalio Inc.
 **/

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
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAG_TRACKER_DRAG, this.dragEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAG_TRACKER_RESIZE, this.resizeEvent.bind(this));
	},
	
	dragEvent: function(event) {
		included = false;
		event.shapes.each(function(shape) {
			if (this.isIncludedInShapes(shape)) {
				included = true;
				return;
			}
		}.bind(this));
		if (included) {
			this.drag(event.shapes, event.offset);
		}
	},
	/**
	 * Implementation of dragging a set on shapes
	 * @param {Object} shapes Given shapes
     * @memberOf ORYX.Plugins.AbstractDragTracker.prototype
	 */
	drag: function(shapes, offset){
		throw new Error("Drag Tracker has to implement the drag function.")
	},
	
	resizeEvent: function(event) {
		included = false;
		event.shapes.each(function(shape) {
			if (this.isIncludedInShapes(shape)) {
				included = true;
				return;
			}
		}.bind(this));
		if (included) {
			this.resize(event.shapes, event.bounds);
		}
	},
	
	/**
	 * Implementation of resizing a set on shapes
	 * @param {Object} shapes Given shapes
     * @memberOf ORYX.Plugins.AbstractDragTracker.prototype
	 */
	resize: function(shapes, offset){
		throw new Error("Drag Tracker has to implement the resize function.")
	},
	
	/**
	 * Proofs if this shape should be processed by the drag tracker or not
	 * @param {Object} shape
	 */
	isIncludedInShapes: function(shape){
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
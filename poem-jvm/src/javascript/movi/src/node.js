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
	
	var _CLASS_NAME = "movi-node";
	
	/**
     * A wrapper class for model nodes.
     * @namespace MOVI.model
     * @class MOVI.model.Node
     * @extends MOVI.model.Shape
     * @constructor
     * @param {Object} jsonObj The JSON object from which the new node
     * is created.
	 * @param {Object} stencilset The stencilset for lookup of the node's 
	 * stencil.
	 * @param {Shape} parent The node's parent shape
	 * @param {String} prefix The element's ID prefix (unique per modelviewer)
     */
    MOVI.model.Node = function(jsonObj, stencilset, parent, prefix) {
	
		// element's attributes
		var attr = {};
		
		MOVI.model.Node.superclass.constructor.call(this, jsonObj, stencilset, parent, prefix, attr); 
		this.set("className", _CLASS_NAME);
		
		this.getCanvas().getModelViewer().onZoomLevelChangeEnd.subscribe(this.update, this, true);
		
		this.update();
	}
	
	MOVI.extend(MOVI.model.Node, MOVI.model.Shape, {
		
		/**
	     * Update style properties of the element
	     * @method update
	     */
		update: function() {
			var zoomFactor = this.getCanvas().getModelViewer().getZoomLevel() / 100;
			
			var left = Math.round(this.bounds.upperLeft.x * zoomFactor);
			var top = Math.round(this.bounds.upperLeft.y * zoomFactor);
			var width = Math.round((this.bounds.lowerRight.x - this.bounds.upperLeft.x) * zoomFactor);
			var height = Math.round((this.bounds.lowerRight.y - this.bounds.upperLeft.y) * zoomFactor);
			
			this.setStyle("left", left + "px");
			this.setStyle("top", top + "px");
			this.setStyle("width", width + "px");
			this.setStyle("height", height + "px");
		},
		
		/**
	     * Overrides the method of the abstract super class
	     */
		isNode: function() {
			return true;
		}
		
	});
	
})();
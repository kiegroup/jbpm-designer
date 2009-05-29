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
	
	var _CLASS_NAME = "movi-edge";
	
	/**
     * A wrapper class for model edges.
     * @namespace MOVI.model
     * @class MOVI.model.Edge
     * @extends MOVI.model.Shape
     * @constructor
     * @param {Object} jsonObj The JSON object from which the new edge
     * is created.
	 * @param {Object} stencilset The stencilset for lookup of the edge's 
	 * stencil.
	 * @param {Shape} parent The edges's parent shape
	 * @param {String} prefix The element's ID prefix (unique per modelviewer)
     */
    MOVI.model.Edge = function(jsonObj, stencilset, parent, prefix) {
	
		// element's attributes
		var attr = {};
		
		MOVI.model.Node.superclass.constructor.call(this, jsonObj, stencilset, parent, prefix, attr); 
		
		this.set("className", _CLASS_NAME);
	}
	
	MOVI.extend(MOVI.model.Edge, MOVI.model.Shape, {
		
		/**
	     * Update style properties of the element
	     * @method update
	     */
		update: function() {
			
		}
		
	});
	
})();
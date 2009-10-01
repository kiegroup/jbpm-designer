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


MOVI.namespace("stencilset");

(function() {
	
	/**
     * Stencil represents an entity of a stencilset.
     * @namespace MOVI.stencilset
     * @class MOVI.stencilset.Stencil
     * @constructor
     * @param {Object} jsonObj The JSON object from which the new stencil
     * is created.
     * @param {String} namespace The namespace of the stencil set that defines this stencil
     */
	MOVI.stencilset.Stencil = function(jsonObject, propertyPackages, namespace) {
		
		// TODO: Doc for stencil attributes from JSON
		YAHOO.lang.augmentObject(this, jsonObject, true);
		
		if(this.propertyPackages) {
			var props = {};
			for(propIndex in this.properties) {
				var prop = this.properties[propIndex];
				props[prop.id] = prop;
			}
			
			for(var i = 0; i < this.propertyPackages.length; i++) {
				var propPackName = this.propertyPackages[i];
				var propPack = propertyPackages[propPackName];
				if(propPack) {
					for(var j = 0; j < propPack.length; j++) {
						var prop = propPack[j];
						props[prop.id] = prop;
					}
				}
			}
			
			this.properties = [];
			
			for(propKey in props) {
				if(!(props[propKey] instanceof Function)) {
					this.properties.push(props[propKey]);
				}
			}
		}
		
		this.stencilSetNamespace = namespace;
		
	}
	
	// TODO: Add convenience methods
	
})();


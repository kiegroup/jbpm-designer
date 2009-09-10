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
     * Stencilset is a collection of stencils.
     * @namespace MOVI.stencilset
     * @class MOVI.stencilset.Stencilset
     * @constructor
     * @param {Object} jsonObj The JSON definition of a stencilset
     */
	MOVI.stencilset.Stencilset = function(jsonObj) {
		
		this.title = jsonObj.title;
		this.namespace = jsonObj.namespace;
		this.description = jsonObj.description;
		
		this.stencils = {};
		
		if(!jsonObj.stencils) {
			MOVI.log("Stencilset contains no stencil definitions", "warning", "stencilset.js" );
		}
		
		this.propertyPackages = {};
		
		// init property packages
		if(jsonObj.propertyPackages) {
			for(var i = 0; i < jsonObj.propertyPackages.length; i++) {
				var pp = jsonObj.propertyPackages[i];
				this.propertyPackages[pp.name] = pp.properties;
			}
		}
		
		for(key in jsonObj.stencils) {
			if(!YAHOO.lang.hasOwnProperty(jsonObj.stencils, key)) continue;
			
			var stencil = jsonObj.stencils[key];
			this.stencils[stencil.id] = new MOVI.stencilset.Stencil(stencil, this.propertyPackages);
		}

	};

	MOVI.stencilset.Stencilset.prototype = {
		
		/**
	     * A key map containing all stencils of the stencilset as values.
		 * Retrieve an entry using the stencil's id as the key.
	     * @property stencils
	     */
		stencils: null,
		
		/**
	     * Returns the stencil object with the specified id. If
		 * the stencilset does not contain a matching stencil
		 * null is returned.
	     * @method getStencil
	     * @param jsonObj The delivered JSON Object
	     */
		getStencil: function(id) {
			return this.stencils[id] || null;
		},
		
		/**
	     * Adds a stencil set extension to this stencil set.
	     * @method addExtension
	     * @param jsonObj The JSON representation of the stencil set extension
	     */
		addExtension: function(jsonObj) {
		    
		    // init additional property packages
    		if(jsonObj.propertyPackages) {
    			for(var i = 0; i < jsonObj.propertyPackages.length; i++) {
    				var pp = jsonObj.propertyPackages[i];
    				this.propertyPackages[pp.name] = pp.properties;
    			}
    		}
    		
		    // init additional stencils
		    if(jsonObj.stencils) {
		        for(var i=0; i<jsonObj.stencils.length; i++) {
		            var stencil = jsonObj.stencils[i];
		            this.stencils[stencil.id] = new MOVI.stencilset.Stencil(stencil, this.propertyPackages);
		        }
		    }
		    
		    // init additional properties
		    if(jsonObj.properties) {
		        for(var i=0; i<jsonObj.properties.length; i++) {
		            var property = jsonObj.properties[i];
		            for(key in this.stencils) {
		                if(!YAHOO.lang.hasOwnProperty(this.stencils, key)) continue;
		                var stencil = this.stencils[key];
		                for(var j=0; j<property.roles.length; j++) {
		                    for(var k=0; k<stencil.roles.length; k++) {
		                        if(property.roles[j]==stencil.roles[k]) {
		                            for(var l=0; l<property.properties.length; l++) {
		                                stencil.properties.push(property.properties[l]);
		                            }
		                        }
		                    }
		                }
		                
		            }
		        }
		    }
		    
		    // note: removing of stencils and properties is not regarded here
		}
		
	}
	
})();


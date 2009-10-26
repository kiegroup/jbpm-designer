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
			
		},
		
		/**
		 * Returns the intersection point of the bounds rectangle and the segment p1->p2 where p1
		 * is located inside the bounds rectangle.
		 * If there is no intersection, p1 is returned
		 * @method _getIntersectionPoint
		 * @private
		 */
		_getIntersectionPoint: function(bounds, p1, p2) {
			
			var linearEquation = function(x) {
				return p1.y + (p2.y-p1.y)/(p2.x-p1.x) * (x-p1.x);
			};
			
			var inverseLinearEquation = function(x) {
				return p1.x + (p2.x-p1.x)/(p2.y-p1.y) * (x-p1.y);
			};
			
			// intersection points of the straight line through p1,p2 and the bounds rectangle
			var s = []; 
				s[0] = {x: inverseLinearEquation(bounds.upperLeft.y), y: bounds.upperLeft.y},
				s[1] = {x: bounds.lowerRight.x, y: linearEquation(bounds.lowerRight.x)},
				s[2] = {x: inverseLinearEquation(bounds.lowerRight.y), y: bounds.lowerRight.y},
				s[3] = {x: bounds.upperLeft.x, y: linearEquation(bounds.upperLeft.x)};
			
			for(var i=0; i<=3; i++) {
	
				if(s[i].x==Infinity || s[i].y==Infinity) continue; // there is no intersection
				
				if(isNaN(s[i].x)) {
					// the segment p1->p2 lies over a segment of the bounding box,
					// there is no definite intersection point
					// -> use the point that is the nearest to the p2
					s[i].x = (p2.x>p1.x) ? bounds.lowerRight.x : bounds.upperLeft.x; 
				}
				if(isNaN(s[i].y)) {
					// the segment p1->p2 lies over a segment of the bounding box,
					// there is no definite intersection point
					// -> use the point that is the nearest to the p2
					s[i].y = (p2.y>p1.y) ? bounds.lowerRight.y : bounds.upperLeft.y; 
				}
				
				// test if intersection point is located on the segment p1->p2
				var tx = (s[i].x-p1.x)/(p2.x-p1.x),
					ty = (s[i].y-p1.y)/(p2.y-p1.y);
				
				if(Math.abs(tx)==Infinity||Math.abs(ty)==Infinity) continue;
				
				if(isNaN(tx) && ty>=0 && ty<=1) {
					// p1.x==p2.x==s.x
					return {x: p1.x, 
							y: p1.y + ty*(p2.y-p1.y)};
				} else if(isNaN(ty) && tx>=0 && tx<=1) {
					// p1.y==p2.y==s.y
					return {x: p1.x + tx*(p2.x-p1.x), 
							y: p1.y};
				} else if((Math.round(tx*10)/10)==(Math.round(ty*10)/10) && tx>=0 && tx<=1) {
					return {x: p1.x + tx*(p2.x-p1.x), 
							y: p1.y + tx*(p2.y-p1.y)};
				}
			}
			
			return {x:p1.x, y:p1.y};
		},
		
		/**
		 * Returns an array of the edges absolute coordinates {x: INTEGER, y: INTEGER} 
		 * @method getCoordinates
		 * @return {Object[]} An array of absolute coordinates {x: INTEGER, y: INTEGER}
		 */
		getCoordinates: function() {
			var coords = [];
			
			var incoming = this.getIncomingShapes(),
				outgoing = this.getOutgoingShapes();
				
			var len = this.dockers.length;
			for (var i = 0; i < len; ++i) {
				
				var pos = {x:this.dockers[i].x, y:this.dockers[i].y}
				
				// Add the bounds of the docked incoming shape
				if(i==0 && incoming.length>0){
					var bounds = incoming[0].getAbsBounds();
					pos.x += bounds.upperLeft.x;
					pos.y += bounds.upperLeft.y;
				}
				
				// Add the bounds of the docked outgoing shape
				if(i==(len-1) && outgoing.length>0) {
					var bounds = outgoing[0].getAbsBounds();
					pos.x += bounds.upperLeft.x;
					pos.y += bounds.upperLeft.y;
				}
				
				coords.push(pos);
			}
				
			if(incoming.length>0)
				coords[0] = this._getIntersectionPoint(incoming[0].getAbsBounds(), coords[0], coords[1]);
			if(outgoing.length>0)
				coords[len-1] = this._getIntersectionPoint(outgoing[0].getAbsBounds(), coords[len-1], coords[len-2]);
		
			return coords;
		},
		
		/**
	     * Overrides the method of the abstract super class
	     */
		isEdge: function() {
			return true;
		}
		
	});
	
})();
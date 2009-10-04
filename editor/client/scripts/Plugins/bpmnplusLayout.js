/**
 * Copyright (c) 2009
 * Kerstin Pfitzner, Oliver Kopp, Sven Wagner-Boysen
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

/**
 * @namespace Oryx name space for plugins
 * @name ORYX.Plugins
 */
if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

	
/**
 * This plugin offers the layout callbacks used in the BPMNplus stencil set.
 * 
 * @class ORYX.Plugins.BPMNPlus
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
ORYX.Plugins.BPMNPlusLayout = {
	/** @lends ORYX.Plugins.BPMNPlusLayout.prototype */
	
	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade) {
		this.facade = facade;

		this.facade.registerOnEvent('layout.bpmnplus.pool', this.handleLayoutPool.bind(this));
		this.facade.registerOnEvent('layout.bpmnplus.poolset', this.handleLayoutPoolSet.bind(this));
	},
	
	/**
	 * 'layout.bpmnplus.pool' event handler
	 * 
	 * @param {Object} event
	 * 		The layout event.
	 */
	handleLayoutPool: function(event) {
		var shape = event.shape;
		var lanes = shape.getChildNodes(false).findAll(function(node) {
			return (node.getStencil().id() === "http://b3mn.org/stencilset/bpmnplus#Lane");
		});
		
		if(lanes.length > 0) {
			lanes = lanes.sortBy(function(lane) {
				return lane.bounds.upperLeft().y;
			});
			
			var shapeWidth = shape.bounds.width();
			var shapeHeight = 0;
			lanes.each(function(lane) {
				var ul = lane.bounds.upperLeft();
				var lr = lane.bounds.lowerRight();
				ul.y = shapeHeight;
				lr.y = ul.y + lane.bounds.height();
				shapeHeight += lane.bounds.height();
				ul.x = 30;
				lr.x = shapeWidth;
				lane.bounds.set(ul, lr);
			});
			
			var upl = shape.bounds.upperLeft();
			shape.bounds.set(upl.x, upl.y, shape.bounds.lowerRight().x, upl.y + shapeHeight);
			
			// set label at the middle
			shape.getLabels().each(function(label) {
				label.y = shapeHeight / 2;
				label.x = 12;
			});
		}		
	},
	
	/**
	 * The event handler for the 'layout.bpmnplus.poolset' event
	 * 
	 * @param {Object} event
	 * 		The layout event to handle
	 */
	handleLayoutPoolSet: function(event) {
		var shape = event.shape;
		
		var lanes = shape.getChildNodes(false).findAll(function(node) {
			return (node.getStencil().id() === "http://b3mn.org/stencilset/bpmnplus#Lane");
		});
		
		if(lanes.length > 0) {
		
			lanes = lanes.sortBy(function(lane) {
				return lane.bounds.upperLeft().y;
			});
			
			var shapeWidth = shape.bounds.width();
			var laneWidth = shapeWidth * 500 / 515;
			
			// calculate height based on the contained lanes
			var oldShapeHeight = 0;
			lanes.each(function(lane) {
				oldShapeHeight += lane.bounds.height();
			});
			
			// calculate new height based on the pool set shade
			var newShapeHeight = oldShapeHeight * 315 / 300;					
			var diff = newShapeHeight - oldShapeHeight;
			
			// calculate lane bounds
			var shapeHeight = diff;
			lanes.each(function(lane) {
				var ul = lane.bounds.upperLeft();
				var lr = lane.bounds.lowerRight();
				ul.y = shapeHeight;
				lr.y = ul.y + lane.bounds.height();
				shapeHeight += lane.bounds.height();
				ul.x = 30;
				lr.x = laneWidth;
				lane.bounds.set(ul, lr);
			});
			
			// calculate shape bounds
			var upl = shape.bounds.upperLeft();
			shape.bounds.set(upl.x, upl.y, shape.bounds.lowerRight().x, upl.y + shapeHeight);
			
			// set labels at the middle
			shape.getLabels().each(function(label) {
				label.y = shapeHeight / 2;
				label.x = 12;
			});
		}
	}
	
};

ORYX.Plugins.BPMNPlusLayout = Clazz.extend(ORYX.Plugins.BPMNPlusLayout);

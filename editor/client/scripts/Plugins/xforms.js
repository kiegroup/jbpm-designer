/**
 * Copyright (c) 2008
 * Jan-Felix Schwarz
 * Sven Wagner-Boysen
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
 * The XForms plugin provides layout methods primarily referring to the XForms stencilset. 
 * 
 * @class ORYX.Plugins.XForms
 * @extends Clazz
 * @param {Object} facade The facade of the editor
 */
ORYX.Plugins.XForms = 
/** @lends ORYX.Plugins.XForms.prototype */
{
	/**
	 * Creates a new instance of the XForms plugin and registers it on the
	 * layout events listed in the XForms stencil set.
	 * 
	 * @constructor
	 * @param {Object} facade The facade of the editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.facade.registerOnEvent('layout.xforms.label', this.handleLayoutLabel.bind(this));
		this.facade.registerOnEvent('layout.xforms.label.button', this.handleLayoutLabelButton.bind(this));
		this.facade.registerOnEvent('layout.xforms.label.item', this.handleLayoutLabelItem.bind(this));
		this.facade.registerOnEvent('layout.xforms.item', this.handleLayoutItem.bind(this));
		this.facade.registerOnEvent('layout.xforms.case', this.handleLayoutCase.bind(this));
		this.facade.registerOnEvent('layout.xforms.action', this.handleLayoutAction.bind(this));
	},
	
	/**
	 * 
	 * @param {Object} event
	 */
	handleLayoutLabel : function(event) {
		var shape = event.shape;
		var moveX = event.moveX;
		var moveY = event.moveY;
		
		var labels = shape.getChildNodes(false).findAll(function(node) {
				return (node.getStencil().id() === "http://b3mn.org/stencilset/xforms#Label");
			});
			
			if(labels.length > 0) {
				labels.each(function(label) {
					var ul = label.bounds.upperLeft();
					var lr = label.bounds.lowerRight();
					ul.y = - label.bounds.height() + moveY;
					lr.y = moveY;
					ul.x = moveX;
					lr.x = label.bounds.width() + moveX;
					label.bounds.set(ul, lr);
				});
			}
	},
	
	handleLayoutLabelButton : function(event) {
		var shape = event.shape;
		
		var labels = shape.getChildNodes(false).findAll(function(node) {
			return (node.getStencil().id() === "http://b3mn.org/stencilset/xforms#Label");
		});
		
		if(labels.length > 0) {
			labels.each(function(label) {
				
				var ul = label.bounds.upperLeft();
				var lr = label.bounds.lowerRight();
				ul.y = 2;
				lr.y = 2 + label.bounds.height();
				
				if((shape.bounds.width()-4)<label.bounds.width()) {
					var sul = shape.bounds.upperLeft();
					var slr = shape.bounds.lowerRight();
					slr.x = sul.x + label.bounds.width() + 4;
					shape.bounds.set(sul, slr);
				}

				ul.x = (shape.bounds.width() - 4 - label.bounds.width()) * 0.5;
				lr.x = ul.x + label.bounds.width();
				label.bounds.set(ul, lr);
			});
		}
	},
	
	handleLayoutItem : function(event) {
		var shape = event.shape;
		
		var items = shape.getChildNodes(false).findAll(function(node) {
			return ((node.getStencil().id()==="http://b3mn.org/stencilset/xforms#Item")||(node.getStencil().id()==="http://b3mn.org/stencilset/xforms#Choices")||(node.getStencil().id()==="http://b3mn.org/stencilset/xforms#Itemset"));
		});
		
		if(items.length > 0) {
		
			items = items.sortBy(function(item) {
				return item.bounds.upperLeft().y;
			});
			
			var shapeWidth = shape.bounds.width();
			var shapeHeight = 0;
			items.each(function(item) {
				var ul = item.bounds.upperLeft();
				var lr = item.bounds.lowerRight();
				if(item.getStencil().id()==="http://b3mn.org/stencilset/xforms#Choices") {
					ul.y = shapeHeight + 25;
					shapeHeight += 25;
				} else {
					ul.y = shapeHeight + 5;
					shapeHeight += 5;
				}
				lr.y = ul.y + item.bounds.height();
				shapeHeight += item.bounds.height();
				ul.x = 30;
				lr.x = shapeWidth;
				item.bounds.set(ul, lr);
			});
			
			var upl = shape.bounds.upperLeft();
			shape.bounds.set(upl.x, upl.y, shape.bounds.lowerRight().x, upl.y + shapeHeight + 5);
		}
		
		var labels = shape.getChildNodes(false).findAll(function(node) {
			return (node.getStencil().id() === "http://b3mn.org/stencilset/xforms#Label");
		});
		
		if(labels.length > 0) {
			labels.each(function(label) {
				var ul = label.bounds.upperLeft();
				var lr = label.bounds.lowerRight();
				ul.y = - label.bounds.height() - 1;
				lr.y = - 1;
				ul.x = 0;
				lr.x = label.bounds.width();
				label.bounds.set(ul, lr);
			});
		}
	},
	
	handleLayoutCase : function(event) {
		var shape = event.shape;
		
		var cases = shape.getChildNodes(false);
				
		var maxWidth = 0;
		cases.each(function(c) {
			if(c.bounds.width()>maxWidth) maxWidth = c.bounds.width();
		});

		if(cases.length > 0) {
			
			cases = cases.sortBy(function(c) {
				return c.bounds.upperLeft().y;
			});
			
			var shapeHeight = 5;
			cases.each(function(c) {
				var ul = c.bounds.upperLeft();
				var lr = c.bounds.lowerRight();
				ul.y = shapeHeight;
				lr.y = ul.y + c.bounds.height();
				shapeHeight += c.bounds.height() + 5;
				ul.x = 0;
				lr.x = maxWidth;
				c.bounds.set(ul, lr);
			});
			
			var upl = shape.bounds.upperLeft();
			shape.bounds.set(upl.x, upl.y, upl.x + maxWidth, upl.y + shapeHeight + 20);
		}
	},
	
	handleLayoutLabelItem : function(event) {
		var shape = event.shape;
		
		var labels = shape.getChildNodes(false).findAll(function(node) {
			return (node.getStencil().id() === "http://b3mn.org/stencilset/xforms#Label");
		});
		
		if(labels.length > 0) {
			labels.each(function(label) {
				var ul = label.bounds.upperLeft();
				var lr = label.bounds.lowerRight();
				ul.y = 2;
				ul.x = 2;
				lr.y = 2 + label.bounds.height();
				lr.x = 2 + label.bounds.width();
				label.bounds.set(ul, lr);
			});
		}
	},
	
	handleLayoutAction : function(event) {
		var shape = event.shape;
		
		var actions = shape.getChildNodes(false);

		if(actions.length > 0) {
			
			actions = actions.sortBy(function(action) {
				return action.bounds.upperLeft().y;
			});
			
			var shapeHeight = 5;
			actions.each(function(action) {
				var ul = action.bounds.upperLeft();
				var lr = action.bounds.lowerRight();
				ul.y = shapeHeight;
				lr.y = ul.y + action.bounds.height();
				shapeHeight += action.bounds.height() + 5;
				ul.x = 2;
				lr.x = 2 + action.bounds.width();
				action.bounds.set(ul, lr);
			});
			
			var upl = shape.bounds.upperLeft();
			var lor = shape.bounds.lowerRight();
			shape.bounds.set(upl.x, upl.y, lor.x, upl.y + shapeHeight + 15);
		}
	}
	
};

ORYX.Plugins.XForms = Clazz.extend(ORYX.Plugins.XForms);

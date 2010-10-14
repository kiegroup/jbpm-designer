/**
 * Copyright (c) 2010
 * Antoine Toulme, Intalio Inc.
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

if(!ORYX.Plugins) { ORYX.Plugins = {} }
if(!ORYX.Plugins.DragTracker) { ORYX.Plugins.DragTracker = {} }

new function(){
	
	/**
	 * Lane drag tracker is an implementation to help drag lanes inside pools.
	 * @class ORYX.Plugins.DragTracker.LaneLayouter
	 * @author Antoine Toulme
	 */
	ORYX.Plugins.DragTracker.LaneDragTracker = ORYX.Plugins.AbstractDragTracker.extend({
		
		shapes : ["Lane"],

		/**
		 * Callback to change the offset depending on the positions.
		 * @param shape
		 * @param parent
		 * @param offset
		 */
		drag : function(shapes, bounds) {
			shape = shapes.first();
			
			var righty = bounds.lowerRight().y;
			var lefty = bounds.upperLeft().y;
			if (righty > shape.parent.absoluteBounds().lowerRight().y) {
				righty = shape.parent.absoluteBounds().lowerRight().y;
				lefty = righty - shape.bounds.height();
			} else if (lefty < shape.parent.absoluteBounds().upperLeft().y) {
				lefty = shape.parent.absoluteBounds().upperLeft().y;
				righty = lefty + shape.bounds.height();
			}
			bounds.set(shape.parent.absoluteBounds().upperLeft().x + 30, lefty, shape.parent.absoluteBounds().lowerRight().x, righty);
			
		},
	
		/**
		 * Callback to change the size delta depending on the positions.
		 * @param shapes the shapes being resized
		 * @param bounds the absolute bounds of the new position
		 */
		resize : function(shapes, bounds) {
			shape = shapes.first();
			var righty = bounds.lowerRight().y;
			if (righty > shape.parent.absoluteBounds().lowerRight().y) {
				righty = shape.parent.absoluteBounds().lowerRight().y;
			}
			var lefty = bounds.upperLeft().y;
			if (lefty < shape.parent.absoluteBounds().upperLeft().y) {
				lefty = shape.parent.absoluteBounds().upperLeft().y;
			}
			bounds.set(shape.parent.absoluteBounds().upperLeft().x + 30, lefty, shape.parent.absoluteBounds().lowerRight().x, righty);
		},
		
		newShape: function(shape) {
			//we create our own command for resizing. It's a bit awkward to recreate commands
			//for such things, and that begs for a nice framework.
			var commandClass = ORYX.Core.Command.extend({
				construct: function(shape, newBounds, plugin){
					this.shape = shape;
					this.oldBounds = shape.bounds.clone();
					this.newBounds = newBounds;
					this.plugin = plugin;
				},			
				execute: function(){
					this.shape.bounds.set(this.newBounds.a, this.newBounds.b);
					this.update(this.getOffset(this.oldBounds, this.newBounds));
					
				},
				rollback: function(){
					this.shape.bounds.set(this.oldBounds.a, this.oldBounds.b);
					this.update(this.getOffset(this.newBounds, this.oldBounds))
				},
				
				getOffset:function(b1, b2){
					return {
						x: b2.a.x - b1.a.x,
						y: b2.a.y - b1.a.y,
						xs: b2.width()/b1.width(),
						ys: b2.height()/b1.height()
					}
				},
				update:function(offset){
					this.shape.getLabels().each(function(label) {
						label.changed();
					});

					this.plugin.facade.getCanvas().update();
					this.plugin.facade.updateSelection();
				}
			});
			
			
			var righty = shape.bounds.lowerRight().y;
			if (righty > shape.parent.absoluteBounds().height()) {
				righty = shape.parent.absoluteBounds().height();
			}
			var lefty = shape.bounds.upperLeft().y;
			if (lefty < 0) {
				lefty = 0;
			}
			var newBounds = new ORYX.Core.Bounds(30, lefty, shape.parent.bounds.width(), righty);
			var command = new commandClass(shape, newBounds, this);
			this.facade.executeCommands([command]);
			this.doLayout([shape]);
		}
	});
	
	
}()

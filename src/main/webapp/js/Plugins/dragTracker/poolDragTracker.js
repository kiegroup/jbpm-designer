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
	ORYX.Plugins.DragTracker.PoolDragTracker = ORYX.Plugins.AbstractDragTracker.extend({
		
		shapes : [ "Pool"],
		
		resizeEnd : function(shapes) {
			var lanes = [];
			
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
				}
			});
			
			commands = [];
			shapes.each(function(parent) {
				var width = parent.bounds.width();
				parent.getChildShapes().each(function(child) {
					if (child.getStencil().id().include("Lane")) {
						// now resize the lanes to take all the room:
						newBounds = child.bounds.clone();
						//force lanes height to be resized if the pool cannot contain them.
						var lowerRightY = child.bounds.lowerRight().y;
						if (child.bounds.lowerRight().y > parent.bounds.height()) {
							lowerRightY = parent.bounds.height();
						}
						newBounds.set(30, child.bounds.upperLeft().y, parent.bounds.width(), lowerRightY);
						var command = new commandClass(child, newBounds, this);
						commands.push(command);
					}
				}.bind(this));
			}.bind(this));
			this.facade.executeCommands(commands);
		},
	
		/**
		 * Callback to change the size delta depending on the positions.
		 * @param shape
		 * @param parent
		 */
		resize : function(shapes, bounds) {
			//forbid resizing more than the content, without shapes.
			var findOccupiedArea = function(shape, ignoreList) {
				var childsBounds = undefined;
				shape.getChildShapes(true).findAll(function(child) {
					return !ignoreList.detect(function(ignored) { child.getStencil().id().include(ignored) }.bind(child));				
				}).each(function(childShape, i) {
					if(i == 0) {
						/* Initialize bounds that include all direct child shapes of the shape */
						childsBounds = childShape.absoluteBounds();
						return;
					}

					/* Include other child elements */
					childsBounds.include(childShape.absoluteBounds());			
				});

				return childsBounds;
			};
			
			// TODO add artifacts to the ignored shapes ?
			var ignoreList = ["Lane"]
			
			shapes.each(function(shape) {
				occupiedArea = findOccupiedArea(shape, ignoreList);
				if (occupiedArea !== undefined) {
					//make the occupiedArea absolute and add some margins.
					occupiedArea.moveBy(20, 20);
					if (bounds.lowerRight().y < occupiedArea.lowerRight().y) {
						bounds.set(bounds.upperLeft().x, bounds.upperLeft().y, bounds.lowerRight().x, occupiedArea.lowerRight().y);
					} 
					if (bounds.lowerRight().x < occupiedArea.lowerRight().x) {
						bounds.set(bounds.upperLeft().x, bounds.upperLeft().y, occupiedArea.lowerRight().x, bounds.lowerRight().y);
					}
				}
			});
		}
		
	});
	
	
}()

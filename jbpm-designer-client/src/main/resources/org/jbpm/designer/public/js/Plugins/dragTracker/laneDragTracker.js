/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
if(!ORYX.Plugins) { 
	ORYX.Plugins = {}; 
};

if(!ORYX.Plugins.DragTracker) { 
	ORYX.Plugins.DragTracker = {};
};

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
	
	
}();

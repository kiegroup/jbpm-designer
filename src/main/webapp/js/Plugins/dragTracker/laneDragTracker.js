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
		
		shapes : [	"http://b3mn.org/stencilset/bpmn2.0#Lane"],

		/**
		 * Callback to change the offset depending on the positions.
		 * @param shape
		 * @param parent
		 * @param offset
		 */
		drag : function(shapes, offset) {
			offset.x = 0;
		},
	
		/**
		 * Callback to change the size delta depending on the positions.
		 * @param shape
		 * @param parent
		 * @param offset
		 */
		resize : function(shapes, bounds) {
			shape = shapes.first(); //FIXME better do some more computation on the min to move.
			bounds.set(0, bounds.upperLeft().y, shape.getParentShape().bounds.width(), bounds.lowerRight().y);
		}
	});
	
	
}()

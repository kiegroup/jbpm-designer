/**
 * Copyright (c) 2009, Andreas Meyer
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.ClearSodBodHighlights = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active 		= false;
		this.raisedEventIds = [];
		
        this.facade.offer({
            'name': ORYX.I18N.ClearSodBodHighlights.name,
            'functionality': this.removeHighlightsAndOverlays.bind(this),
            'group': ORYX.I18N.ClearSodBodHighlights.group,
            'icon': ORYX.PATH + "images/sod_bod_view_clear.png",
            'description': ORYX.I18N.ClearSodBodHighlights.desc,
            'index': 5,
            'toggle': false,
            'minShape': 0,
            'maxShape': 0
        });
		
    },
    
	removeHighlightsAndOverlays: function(){
		var allShapes = this.facade.getCanvas().getChildShapes(true);
		var allShapeIds = [];
		var i = 0;
		//get all tasks of canvas
		for (var index = 0; index < allShapes.length; index++) {
    		if (allShapes[index].properties["oryx-activitytype"] == "Task") {
				if (allShapes[index].properties["oryx-id"] != "") {
					allShapeIds[i] = allShapes[index].properties["oryx-id"];
					i++;
				}
			}
    	}
		
		//remove highlights
		allShapeIds.each(function(id){
			this.facade.raiseEvent({
					type: 	ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE,
					highlightId: 	id
				});
		}.bind(this))
		
		//remove overlays 
		allShapeIds.each(function(id){
			this.facade.raiseEvent({
					type: 	ORYX.CONFIG.EVENT_OVERLAY_HIDE,
					id: 	id
				});
		}.bind(this))
	}
});
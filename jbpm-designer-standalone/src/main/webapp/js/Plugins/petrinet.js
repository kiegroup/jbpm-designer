/**
 * Copyright (c) 2010
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

 if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.PetriNet = {

	/**
	 *	Constructor
	 *	@param {Object} Facade: The Facade of the Editor
	 */
	construct: function(facade){
		this.facade = facade;
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
	},
	
	/**
	 * Checks if the number of tokens on a place has changed. Up to four the 
	 * tokens are visualized as drawings, otherwise a number gets displayed.
	 */
	handlePropertyChanged: function(option) {
		var shapes = option.elements;
		var propertyKey = option.key;
		var propertyValue = option.value;
		
		var changed = false;
		shapes.each(function(shape){
			if((shape.getStencil().id() === "http://b3mn.org/stencilset/petrinet#Place") &&
				(propertyKey === "oryx-numberoftokens")) {
				
				/* Visualize number of Tokens */
				if(propertyValue == 0) {
					shape.setProperty("oryx-numberoftokens_text", "");
					shape.setProperty("oryx-numberoftokens_drawing", "0");
				} else if(propertyValue == 1) {
					shape.setProperty("oryx-numberoftokens_text", "");
					shape.setProperty("oryx-numberoftokens_drawing", "1");
				} else if(propertyValue == 2) {
					shape.setProperty("oryx-numberoftokens_text", "");
					shape.setProperty("oryx-numberoftokens_drawing", "2");
				} else if(propertyValue == 3) {
					shape.setProperty("oryx-numberoftokens_text", "");
					shape.setProperty("oryx-numberoftokens_drawing", "3");
				} else if(propertyValue == 4) {
					shape.setProperty("oryx-numberoftokens_text", "");
					shape.setProperty("oryx-numberoftokens_drawing", "4");
				} else {
					var tokens = parseInt(propertyValue, 10);
					if(tokens && tokens > 0) {
						shape.setProperty("oryx-numberoftokens_text", "" + tokens);
						shape.setProperty("oryx-numberoftokens_drawing", "0");
					} else {
						shape.setProperty("oryx-numberoftokens_text", "");
						shape.setProperty("oryx-numberoftokens_drawing", "0");
					}
				}
				changed = true;
			}
		});
		
		if(changed) {this.facade.getCanvas().update();}
	}
};
	
ORYX.Plugins.PetriNet = ORYX.Plugins.AbstractPlugin.extend(ORYX.Plugins.PetriNet);
	
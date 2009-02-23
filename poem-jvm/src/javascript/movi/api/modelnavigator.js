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


// Declare the MOVI.widget namespace
MOVI.namespace("widget");

(function() {
	
	var _CLIPPING_RECT_CLASS_NAME = "movi-navigator-clippingrect",
		_NAVIGATOR_IMG_CLASS_NAME = "movi-navigator-img";
	
	/**
     * Add image to the host element and create the clipping rectangle element.
	 * Ensure that host element's position is not static to allow absolute
	 * positioning of the clipping rect relative to the host element.
     * @method _setUpHostElement
     * @private
     */
	var _setUpHostElement = function() {
		if(this.getStyle("position")=="static")
			this.setStyle("position", "relative"); 
		this.set("innerHTML", 
				"<img class=\"" + _NAVIGATOR_IMG_CLASS_NAME + "\" />" + 
				"<div class=\"" + _CLIPPING_RECT_CLASS_NAME + "\" />");
	}
	
	/**
     * The ModelNavigator widget is a UI control that enables users to navigate
	 * in a model displayed in a ModelViewer component. A rectangle indicates the
	 * model clipping.
     * @namespace MOVI.widget
     * @class MOVI.widget.ModelNavigator
     * @extends YAHOO.util.Element
     * @constructor
     * @param {HTMLElement | String } el The id of the container DIV element that will 
	 * wrap the ModelNavigator, or a reference to a DIV element. The DIV element must
	 * exist in the document.
	 * @modelviewer {ModelViewer} modelviewer The ModelViewer that is navigated 
     */
    MOVI.widget.ModelNavigator = function(el, modelviewer) {
	
		if(!modelviewer) {
			MOVI.log("No model viewer specified for model navigator", "error", "modelnavigator.js");
			return false;
		}
	
		this.modelviewer = modelviewer;
		
    	MOVI.widget.ModelNavigator.superclass.constructor.call(this, el); 

		_setUpHostElement.call(this);
		
		this._clippingRect = new YAHOO.util.Element(
			this.getElementsByClassName(_CLIPPING_RECT_CLASS_NAME)[0]);
			
		this._image = new YAHOO.util.Element(
			this.getElementsByTagName("img")[0]);
		
		// add event listeners to modelviewer's scrollbox
		var scrollboxId = this.modelviewer.getScrollboxEl().get("id");
		if(!scrollboxId || !YAHOO.util.Event.addListener(
								scrollboxId, "scroll", this.update, this, true)) {
			MOVI.log("Could not add event listener to scrollbox", "error", "modelnavigator.js");
		}
		
		this.update();
		
    };

	MOVI.extend(MOVI.widget.ModelNavigator, YAHOO.util.Element, {
		
		/**
		 * The clipping rectangle DIV element.
		 * @property _clippingRect
		 * @type Element
		 * @private
		 */
		_clippingRect: null,
		
		/**
	     * The image element
	     * @property _image
		 * @type Element
		 * @private
	     */
		_image: null,
		
		/**
		 * The ModelViewer that is navigated
		 * @property modelviewer
		 * @type ModelViewer
		 */
		modelviewer: null,
		
		/**
	     * Callback to update the clipping rectangle's position and size.
		 * Also updates the image if another model has been loaded in the 
		 * model viewer.
	     * @method update
	     */
		update: function() {
			var pngUrl = this.modelviewer.getModelUri() + "/png";
			var scrollboxEl = this.modelviewer.getScrollboxEl();
				
			// update image source if necessary
			if(this._image.get("src")!=pngUrl)
				this._image.set("src", pngUrl);
				
			var width = parseInt(this.getStyle("width"), 10);
			var scale = width / this.modelviewer.getImgWidth();
			
			// adjust navigator's height
			this.setStyle("height", Math.round(scale * this.modelviewer.getImgHeight()) + "px");
			
			// update clipping rect
			var scrollboxWidth = parseInt(scrollboxEl.getStyle("width"), 10);
			var scrollboxHeight = parseInt(scrollboxEl.getStyle("height"), 10);
			this._clippingRect.setStyle("width", Math.round(scale * scrollboxWidth) + "px");
			this._clippingRect.setStyle("height", Math.round(scale * scrollboxHeight) + "px");
			this._clippingRect.setStyle("left", Math.round(scale * scrollboxEl.get("scrollLeft")) + "px");
			this._clippingRect.setStyle("top", Math.round(scale * scrollboxEl.get("scrollTop")) + "px");
			
		}
		
	});
	
})();
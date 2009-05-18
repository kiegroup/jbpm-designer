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
	
	var _NAVIGATOR_CLASS_NAME = "movi-navigator",
		_CLIPPING_RECT_CLASS_NAME = "movi-navigator-clippingrect",
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
	};
	
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
			throw new Error("No model viewer specified for model navigator", "modelnavigator.js");
			return false;
		}
	
		this.modelviewer = modelviewer;
		
    	MOVI.widget.ModelNavigator.superclass.constructor.call(this, el); 

		_setUpHostElement.call(this);
		
		this._clippingRect = new YAHOO.util.Element(
			this.getElementsByClassName(_CLIPPING_RECT_CLASS_NAME)[0]);
			
		this._image = new YAHOO.util.Element(
			this.getElementsByTagName("img")[0]);
			
		this.addClass(_NAVIGATOR_CLASS_NAME);
		
		// add event listeners to modelviewer's scrollbox
		var scrollboxId = this.modelviewer.getScrollboxEl().get("id");
		if(!scrollboxId || !YAHOO.util.Event.addListener(
								scrollboxId, "scroll", this.update, this, true)) {
			throw new Error("Could not add event listener to scrollbox", "modelnavigator.js");
		}
		
		// add event listener for zooming
		this.modelviewer.onZoomLevelChange.subscribe(this.update, this, true);
		
		// add event listeners to modelnavigator
		this._clippingRect.addListener("mousedown", this._onClippingRectDrag, this, this, true);
		this._clippingRect.addListener("click", function(ev) { YAHOO.util.Event.stopEvent(ev); }, this, this, true);
		this.addListener("click", this._onClick, this, this, true);
		YAHOO.util.Event.addListener(document, "mouseup", this._onMouseUp, this, this, true);
		
		this._absXY = [];
		this._mouseOffset = {x: 0, y: 0};
		
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
	     * The absolute position [x, y] of the navigator element 
	     * @property _absXY
		 * @type Array
		 * @private
	     */
		_absXY: null,
		
		/**
	     * The offset of the mouse pointer when dragging the clipping rect
	     * @property _mouseOffset
		 * @type Object
		 * @private
	     */
		_mouseOffset: null,
		
		/**
		 * The ModelViewer that is navigated
		 * @property modelviewer
		 * @type ModelViewer
		 */
		modelviewer: null,
		
		/**
		 * @method _onClippingRectDrag
		 * @private
		 */
		_onClippingRectDrag: function(ev) {
			YAHOO.util.Event.preventDefault(ev);
			this._absXY = YAHOO.util.Dom.getXY(this);
			var mouseAbsXY = YAHOO.util.Event.getXY(ev);
			var clippingRectAbsXY = YAHOO.util.Dom.getXY(this._clippingRect);
			this._mouseOffset.x = mouseAbsXY[0] - clippingRectAbsXY[0];
			this._mouseOffset.y = mouseAbsXY[1] - clippingRectAbsXY[1];
			this.addListener("mousemove", this._onClippingRectMove, this, this, true);
		},
		
		/**
		 * @method _onMouseUp
		 * @private
		 */
		_onMouseUp: function(ev) {
			YAHOO.util.Event.preventDefault(ev);
			this.removeListener("mousemove", this._onClippingRectMove);
		},
		
		/**
		 * @method _onClick
		 * @private
		 */
		_onClick: function(ev) {
			YAHOO.util.Event.preventDefault(ev);
			this._absXY = YAHOO.util.Dom.getXY(this);
			var mouseAbsXY = YAHOO.util.Event.getXY(ev);
			var navigatorWidth = parseInt(this.getStyle("width"), 10);
			var scale =  this.modelviewer.getImgWidth() / navigatorWidth;
			var zoomFactor = this.modelviewer.getZoomLevel() / 100;
			var centerX = Math.round(scale * (mouseAbsXY[0] - this._absXY[0]) * zoomFactor);
			var centerY = Math.round(scale * (mouseAbsXY[1] - this._absXY[1]) * zoomFactor);
			this.modelviewer.centerScrollTo(centerX, centerY);
		},
		
		/**
		 * Callback method that is executed when the clipping rectangle is moved
		 * @method _scrollMove
		 * @private
		 */
		_onClippingRectMove: function(ev) {
			YAHOO.util.Event.preventDefault(ev);
			var mouseAbsXY = YAHOO.util.Event.getXY(ev);
			var x = mouseAbsXY[0] - this._absXY[0] - this._mouseOffset.x,
				y = mouseAbsXY[1] - this._absXY[1] - this._mouseOffset.y;
			var navigatorWidth = parseInt(this.getStyle("width"), 10);
			var scale =  this.modelviewer.getImgWidth() / navigatorWidth;
			var zoomFactor = this.modelviewer.getZoomLevel() / 100;
			var scrollboxEl = this.modelviewer.getScrollboxEl();
			scrollboxEl.set("scrollLeft", Math.round(scale * x * zoomFactor));
			scrollboxEl.set("scrollTop", Math.round(scale * y * zoomFactor));
		},
		
		/**
	     * Callback to update the clipping rectangle's position and size.
		 * Also updates the image if another model has been loaded in the 
		 * model viewer.
	     * @method update
	     */
		update: function() {
			var zoomFactor = this.modelviewer.getZoomLevel() / 100;
			
			var pngUrl = this.modelviewer.getModelUri() + "/png";
			var scrollboxEl = this.modelviewer.getScrollboxEl();
				
			// update image source if necessary
			if(this._image.get("src")!=pngUrl)
				this._image.set("src", pngUrl);
				
			var navigatorWidth = parseInt(this.getStyle("width"), 10);
			var scale = navigatorWidth / this.modelviewer.getImgWidth();
			
			if(scale>1) {
				navigatorWidth = this.modelviewer.getImgWidth();
				this.setStyle("width", navigatorWidth + "px");
				scale = 1;
			}
			
			var navigatorHeight = Math.round(scale * this.modelviewer.getImgHeight());
			
			// adjust navigator's height
			this.setStyle("height", navigatorHeight + "px");
			
			// calculate position of clipping rect
			var left = Math.round(scale * scrollboxEl.get("scrollLeft") / zoomFactor);
			var top = Math.round(scale * scrollboxEl.get("scrollTop") / zoomFactor);

			// calculate dimensions of clipping rect
			var scrollboxWidth = parseInt(scrollboxEl.getStyle("width"), 10);
			var scrollboxHeight = parseInt(scrollboxEl.getStyle("height"), 10);
			var clippingRectWidth = Math.round(scale * scrollboxWidth / zoomFactor);
			var clippingRectHeight = Math.round(scale * scrollboxHeight / zoomFactor);
			if(left+clippingRectWidth>navigatorWidth) clippingRectWidth = navigatorWidth-left;
			if(top+clippingRectHeight>navigatorHeight) clippingRectHeight = navigatorHeight-top;
			
			// update clipping rect
			this._clippingRect.setStyle("width", clippingRectWidth + "px");
			this._clippingRect.setStyle("height", clippingRectHeight + "px");
			this._clippingRect.setStyle("left", left + "px");
			this._clippingRect.setStyle("top", top + "px");
			
		}
		
	});
	
})();
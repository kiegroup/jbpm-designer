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


MOVI.namespace("widget");

(function() {
	
	var _SLIDER_CLASS_NAME			= "movi-zoomslider",
		_SLIDER_THUMB_CLASS_NAME	= "movi-zoomslider-thumb",
		_SLIDER_WIDTH				= 100,
		_SLIDER_STEP				= 1;
	
	/**
     * Add image to the host element and create the clipping rectangle element.
	 * Ensure that host element's position is not static to allow absolute
	 * positioning of the clipping rect relative to the host element
     * @method _setUpHostElement
     * @private
     */
	var _setUpHostElement = function() {
		this.set("innerHTML",
				"<div id=\"" + _SLIDER_CLASS_NAME + this.modelviewer.getIndex() + "\" class=\"" + _SLIDER_CLASS_NAME + "\" title=\"Zoom Slider\">" +
					"<div id=\"" + _SLIDER_THUMB_CLASS_NAME + this.modelviewer.getIndex() + "\" class=\"" + _SLIDER_THUMB_CLASS_NAME + "\"></div>" +
				"</div>");
	};
	
	/**
     * The ZoomSlider widget is a slider UI control that enables users to adjust the
 	 * zoom level of a model viewer.
     * @namespace MOVI.widget
     * @class MOVI.widget.ZoomSlider
     * @constructor
	 * @param {HTMLElement | String } el The id of the container DIV element that will 
	 * wrap the ZoomSlider, or a reference to a DIV element. The DIV element must
	 * exist in the document.
	 * @param {ModelViewer} modelviewer The ModelViewer for that shape selection is enabled
     */
    MOVI.widget.ZoomSlider = function(el, modelviewer) {
	
		if(!modelviewer) {
			throw new Error("No model viewer specified for zoom slider", "zoom.js");
			return false;
		}
	
		this.modelviewer = modelviewer;
		
    	MOVI.widget.ZoomSlider.superclass.constructor.call(this, el);
	
		_setUpHostElement.call(this);
		
		// instantiate the slider
		this.slider = YAHOO.widget.Slider.getHorizSlider(
			_SLIDER_CLASS_NAME + this.modelviewer.getIndex(), 
			_SLIDER_THUMB_CLASS_NAME + this.modelviewer.getIndex(), 
			0, _SLIDER_WIDTH, _SLIDER_STEP
		); 
		 
		// set the initial value of the slider instance
		this.slider.setValue(100, true);
		
		this.slider.subscribe('change', this.onChange, this, true);
	};
	
	MOVI.extend(MOVI.widget.ZoomSlider, YAHOO.util.Element, {
		
		/**
		 * The slider UI element
		 * @property slider
		 * @type YAHOO.widget.Slider
		 */
		slider: null,
		
		/**
		 * Callback method that is executed when the slider is changed. Updates the zoom level of the model viewer.
		 * This method should also be called when the model viewer is resized.
		 * @method onChange
		 */
		onChange: function() {
			// calculate minimal zoom factor (stop zooming out when model fits model viewer)
			var scaleHorizontal = (parseInt(this.modelviewer.getScrollboxEl().getStyle("width"), 10)-5) / this.modelviewer.getImgWidth();
			var scaleVertical = (parseInt(this.modelviewer.getScrollboxEl().getStyle("height"), 10)-5) / this.modelviewer.getImgHeight();
			var scale = (scaleHorizontal < scaleVertical) ? scaleHorizontal : scaleVertical;
			if(scale>1)	scale = 1;
			var minZoomFactor = scale*100;
			var maxZoomFactor = 100;
			
			var zoomStep = (maxZoomFactor-minZoomFactor) / _SLIDER_WIDTH;
			this.modelviewer.setZoomLevel(minZoomFactor + this.slider.getValue() * zoomStep);
		},
		
		/**
		 * Update the state of the slider control to current model zoom level.
		 * This method should be called when the model viewer's zoom level has been changed.
		 * @method update
		 */
		update: function() {
			// TODO: implement me
		}
		
		
	});
	
	
})();
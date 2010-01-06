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
	
	var _SLIDER_CLASS_NAME_PREFIX			= "movi-zoomslider",
		_SLIDER_THUMB_CLASS_NAME			= "movi-zoomslider-thumb",
		_SLIDER_DEFAULT_LENGTH				= 100,
		_SLIDER_DEFAULT_STEP				= 1,
		_FULLSCREEN_LIGHTBOX_CLASS_NAME		= "movi-fullscreen-lightbox",
		_FULLSCREEN_CONTAINER_CLASS_NAME	= "movi-fullscreen-modelcontainer";
	
	/**
     * Add image to the host element and create the clipping rectangle element.
	 * Ensure that host element's position is not static to allow absolute
	 * positioning of the clipping rect relative to the host element
     * @method _setUpHostElementZoomSlider
     * @private
     */
	var _setUpHostElementZoomSlider = function() {
		
		var div1 = new YAHOO.util.Element(document.createElement("div"));
		div1.set("id", _SLIDER_CLASS_NAME_PREFIX + this.modelviewer.getIndex());
		div1.addClass(_SLIDER_CLASS_NAME_PREFIX  + "-" + this.orientation);
		div1.set("title", "Zoom Slider");
		
		var div2 = new YAHOO.util.Element(document.createElement("div"));
		div2.set("id", _SLIDER_THUMB_CLASS_NAME + this.modelviewer.getIndex());
		div2.addClass(_SLIDER_THUMB_CLASS_NAME);
		
		div1.appendChild(div2);
		this.appendChild(div1);
		
	};
	
	/**
	 * Calculate the minimal zoom level in percent (stop zooming out when model fits the model viewer in height and width)
	 * @method _getMinZoomLevel
	 * @private
	 */

	var _getMinZoomLevel = function(modelviewer) {		
		var scaleHorizontal = (modelviewer.getScrollboxEl().get("offsetWidth")-5) / modelviewer.getImgWidth();
		var scaleVertical = (modelviewer.getScrollboxEl().get("offsetHeight")-5) / modelviewer.getImgHeight();
		var scale = (scaleHorizontal < scaleVertical) ? scaleHorizontal : scaleVertical;
		if(scale>1)	scale = 1;
		return scale*100;
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
	 * @param {ModelViewer} modelviewer The ModelViewer for that zooming is enabled
	 * @param {Object} opt (optional) Options: 
     * <dl>
     * <dt>orientation</dt>
     * <dd>
     * expected values: "horizontal" (default), "vertical"
     * </dd>
     * <dt>trackLength</dt>
     * <dd>
     * The length of the slider track in pixels (default: 100)
     * </dd>
     * <dt>step</dt>
     * <dd>
     * The slider thumb should move this many pixels at a time (default: 1)
     * </dd>
     * <dt>reverse</dt>
     * <dd>
     * Set to true to invert to default direction of the slider which is left-to right, or
     * alternatively top-to-bottom (default: false)
     * </dd>
	 * </dl>
     */
    MOVI.widget.ZoomSlider = function(el, modelviewer, opt) {
	
		if(!modelviewer) {
			throw new Error("No model viewer specified for zoom slider", "zoom.js");
			return false;
		}
	
		this.modelviewer = modelviewer;
		
    	MOVI.widget.ZoomSlider.superclass.constructor.call(this, el);
    	
    	if(!opt) opt = {};
		
		this.orientation = opt.orientation || "horizontal";
		this.trackLength = opt.trackLength || _SLIDER_DEFAULT_LENGTH;
		this.step = opt.step || _SLIDER_DEFAULT_STEP;
		this.reverse = opt.reverse || false;
	
		_setUpHostElementZoomSlider.call(this);
		
		// instantiate the slider
		if(this.orientation=="vertical") {
		    this.slider = YAHOO.widget.Slider.getVertSlider(
    			_SLIDER_CLASS_NAME_PREFIX + this.modelviewer.getIndex(), 
    			_SLIDER_THUMB_CLASS_NAME + this.modelviewer.getIndex(), 
    			0, this.trackLength, this.step
    		);
		} else {
		    this.slider = YAHOO.widget.Slider.getHorizSlider(
    			_SLIDER_CLASS_NAME_PREFIX + this.modelviewer.getIndex(), 
    			_SLIDER_THUMB_CLASS_NAME + this.modelviewer.getIndex(), 
    			0, this.trackLength, this.step
    		);
		}
		
		this.update();
		
		this.slider.subscribe('change', this.onChange, this, true);
		this.slider.subscribe('slideStart', this._onSlideStart, this, true);
		this.slider.subscribe('slideEnd', this._onSlideEnd, this, true);
		
		this.modelviewer.onModelLoadEnd.subscribe(this.update, this, true);
	};
	
	MOVI.extend(MOVI.widget.ZoomSlider, YAHOO.util.Element, {
		
		/**
		 * The slider UI element
		 * @property slider
		 * @type YAHOO.widget.Slider
		 */
		slider: null,
		
		/**
		 * Callback that is executed when sliding starts
		 * @method _onSlideStart
		 * @private
		 */
		_onSlideStart: function() {
			this.modelviewer.onZoomLevelChangeStart.fire(this.modelviewer.getZoomLevel());
		},
		
		/**
		 * Callback that is executed when sliding ends
		 * @method _onSlideEnd
		 * @private
		 */
		_onSlideEnd: function() {
			this.modelviewer.onZoomLevelChangeEnd.fire(this.modelviewer.getZoomLevel());
		},
		
		/**
		 * Callback method that is executed when the slider is changed. Updates the zoom level of the model viewer.
		 * This method should also be called when the model viewer is resized.
		 * @method onChange
		 */
		onChange: function() {
			var minZoomLevel = _getMinZoomLevel(this.modelviewer);
			var maxZoomLevel = 100;
			var zoomStep = (maxZoomLevel-minZoomLevel) / this.trackLength;
			if(this.reverse)
			    this.modelviewer.setZoomLevel(minZoomLevel + (this.trackLength - this.slider.getValue()) * zoomStep, false);
			else
			    this.modelviewer.setZoomLevel(minZoomLevel + this.slider.getValue() * zoomStep, false);
		},
		
		/**
		 * Update the state of the slider control to current model zoom level.
		 * This method should be called when the model viewer's zoom level has been changed.
		 * @method update
		 */
		update: function() {
			var minZoomLevel = _getMinZoomLevel(this.modelviewer);
			var maxZoomLevel = 100;
			var zoomStep = (maxZoomLevel-minZoomLevel) / this.trackLength;
			if(this.reverse) 
			    this.slider.setValue(this.trackLength - (this.modelviewer.getZoomLevel() - minZoomLevel) / zoomStep, false, true, true);
			else
			    this.slider.setValue((this.modelviewer.getZoomLevel() - minZoomLevel) / zoomStep, false, true, true);
		}
		
	});
	
	
	
	/**
	 * Swap two nodes. In IE, we use the native method, for others we
	 * emulate the IE behavior
	 * @method _swapNode
	 * @param n1 the first node to swap
	 * @param n2 the other node to swap
	 * @private
	 */
	var _swapNode = function(n1, n2) {
	    if (n1.swapNode) {
	        n1.swapNode(n2);
	    } else {
	        var p = n2.parentNode;
	        var s = n2.nextSibling;

	        if (s == n1) {
	            p.insertBefore(n1, n2);
	        } else if (n2 == n1.nextSibling) {
	            p.insertBefore(n2, n1);
	        } else {
	            n1.parentNode.replaceChild(n2, n1);
	            p.insertBefore(n1, s);
	        }
	    }
	};
	
	/**
     * 
     * @method _setUpHostElementFullscreenViewer
     * @private
     */
	var _setUpHostElementFullscreenViewer = function() {
		var el = new YAHOO.util.Element(document.createElement('div'));
		el.set("innerHTML", 
			"<div class=\"hd\"></div>" +
		    "<div class=\"bd\"><div class=\"" + _FULLSCREEN_CONTAINER_CLASS_NAME + "\"></div></div>" +
		    "<div class=\"ft\"></div>");
		(new YAHOO.util.Element(document.body)).appendChild(el);
		return el;
	};
	
	/**
     * The FullscreenViewer widget is a UI widget that displays the complete model in a
	 * lightbox fullscreen view.
     * @namespace MOVI.widget
     * @class MOVI.widget.FullscreenViewer
     * @constructor
	 * @param {ModelViewer} modelviewer The ModelViewer for that is viewed in fullscreen mode
     */
    MOVI.widget.FullscreenViewer = function(modelviewer) {
	
		this.modelviewer = modelviewer;
		
		var el = _setUpHostElementFullscreenViewer();
		el.addClass(_FULLSCREEN_LIGHTBOX_CLASS_NAME);
		var elId = _FULLSCREEN_LIGHTBOX_CLASS_NAME+this.modelviewer.getIndex();
		el.set("id", elId);
		
		MOVI.widget.FullscreenViewer.superclass.constructor.call(this, el);
		
		this.dialog = new YAHOO.widget.Dialog(elId, {
			fixedcenter: true,
			visible: false,
			draggable: true,
			modal: true,
			close: true,
			constraintoviewport: true
		});
		
		this.dialog.render();
		
		// add listener to close button
		YAHOO.util.Event.on(
			YAHOO.util.Dom.getElementsByClassName("container-close", "a", elId), 
			"click",
			this.close, this, true
		);
		
		this._fullscreenContainer = new YAHOO.util.Element(el.getElementsByClassName(_FULLSCREEN_CONTAINER_CLASS_NAME)[0]);
		this._modelViewerPlaceholder = new YAHOO.util.Element(document.createElement("div"));
		this._fullscreenContainer.appendChild(this._modelViewerPlaceholder);
	};
	
	MOVI.extend(MOVI.widget.FullscreenViewer, YAHOO.util.Element, {
		
		/**
		 * The fullscreen model viewer conatainer element
		 * @property _fullscreenContainer
		 * @type YAHOO.util.Element
		 * @private
		 */
		_fullscreenContainer: null,
		
		/**
		 * A placeholder element that is swapped with the original model viewer element
		 * @property _modelViewerPlaceholder
		 * @type YAHOO.util.Element
		 * @private
		 */
		_modelViewerPlaceholder: null,
		
		/**
		 * The Dialog widget that represents the lightbox
		 * @property dialog
		 * @type YAHOO.widget.Dialog
		 */
		dialog: null,
		
		open: function() {
			this.dialog.cfg.setProperty("width", (YAHOO.util.Dom.getViewportWidth()-20)+"px");
			this.dialog.cfg.setProperty("height", (YAHOO.util.Dom.getViewportHeight()-20)+"px");
			_swapNode(this.modelviewer.getScrollboxEl().get("element"), this._modelViewerPlaceholder.get("element"));
			
			// zoom to fit to fullscreen
			var minZoomLevel = _getMinZoomLevel(this.modelviewer);
		
			// store original zoom factor
			this._originalZoomLevel = this.modelviewer.getZoomLevel();
			this.modelviewer.setZoomLevel(minZoomLevel);
			
			this.dialog.show();
		},
		
		close: function() {
			_swapNode(this.modelviewer.getScrollboxEl().get("element"), this._modelViewerPlaceholder.get("element"));
			this.modelviewer.setZoomLevel(this._originalZoomLevel);
			this.dialog.hide();
		}
		
	});
	
	
})();
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

MOVI.namespace("util");

(function() {
	
	var _BUBBLE_VISIBLE_CLASS_NAME = "movi-bubble-visible",
		_BUBBLE_HIDDEN_CLASS_NAME = "movi-bubble-hidden",
		_BUBBLE_UL_CLASS_NAME = "movi-bubble-ul",
		_BUBBLE_UR_CLASS_NAME = "movi-bubble-ur",
		_BUBBLE_LL_CLASS_NAME = "movi-bubble-ll",
		_BUBBLE_LR_CLASS_NAME = "movi-bubble-lr",
		_BUBBLE_BORDERTOP_CLASS_NAME = "movi-bubble-bt",
		_BUBBLE_BORDERBOTTOM_CLASS_NAME = "movi-bubble-bb",
		_BUBBLE_ARROW_CLASS_NAME = "movi-bubble-ar",
		_BUBBLE_CONTENT_CLASS_NAME = "movi-bubble-bc";
	
	/**
     * Create the host element.
     * @method _createHostElement
     * @private
     */
    var _createHostElement = function() {
        var el = document.createElement('div');
        return el;
    };

	/**
     * Create the bubble elements inside the host element.
     * @method _createBubble
 	 * @param {String} content The content to place inside the bubble
     * @private
     */
	var _createBubble = function(content) {
		this.set("className", _BUBBLE_HIDDEN_CLASS_NAME);
		this.set("innerHTML", 	
						"<div class=\"" + _BUBBLE_UL_CLASS_NAME + "\"><div class=\"" + _BUBBLE_UR_CLASS_NAME + "\">" +
					   	"<div class=\"" + _BUBBLE_LL_CLASS_NAME + "\"><div class=\"" + _BUBBLE_LR_CLASS_NAME + "\">" + 
					    	"<div class=\"" + _BUBBLE_BORDERTOP_CLASS_NAME + "\"></div>" +
					   		"<div class=\"" + _BUBBLE_CONTENT_CLASS_NAME + "\">" +
					        	content +
					        "</div>" +
					        "<div class=\"" + _BUBBLE_BORDERBOTTOM_CLASS_NAME + "\"></div>" +
					   	"</div></div>" +
					   	"</div></div>" +
					    "<div class=\"" + _BUBBLE_ARROW_CLASS_NAME + "\"/>");
	}
	
	/**
     * Create an Annotation for a Marker to enrich the model with additional information 
	 * and functionality. The Annotation renders a speech bubble containing arbitrary
	 * XHTML content and attachs it to the Marker.
     * @namespace MOVI.util
     * @class MOVI.util.Annotation
     * @extends YAHOO.util.Element
     * @constructor
	 * @param {Marker} marker The Marker to attach the Annotation to
	 * @param {String} content The Annotation's inner XHTML content 
     */
    MOVI.util.Annotation = function(marker, content) {
	
		if(!marker) {
			MOVI.log("No marker specified for annotation.", "error", "annotation.js");
			return false;
		}
		
		if(!YAHOO.lang.isString(content)) {
			MOVI.log("A String is expected for the annotation's content.", "error", "annotation.js");
		}
		
		// create host element
        el = _createHostElement.call(this);
		
		MOVI.util.Annotation.superclass.constructor.call(this, el, {}); 
		
		_createBubble.call(this, content);
		
		this._content = new YAHOO.util.Element(
			this.getElementsByClassName(_BUBBLE_CONTENT_CLASS_NAME)[0]);
		
		// attach to marker
		var appended = false;
		for(key in marker.shapeRects) {
			var rect = marker.shapeRects[key];
			
			if(!appended) { // only append to first shape rect of the marker
				rect.appendChild(this);
				appended = true;
			}
		}
	}

	MOVI.extend(MOVI.util.Annotation, YAHOO.util.Element, {
		
		/**
		 * The Element containing the specified content as inner HTML.
		 * @property _contentElement
		 * @type Element
		 * @private
		 */
		_contentElement: null,
		
		/**
	     * Show the annotation bubble
	     * @method show
	     */
		show: function() {
			this.set("className", _BUBBLE_VISIBLE_CLASS_NAME);
		},
		
		/**
	     * Hide the annotation bubble
	     * @method hide
	     */
		hide: function() {
			this.set("className", _BUBBLE_HIDDEN_CLASS_NAME);
		}
		
	});
	
})();
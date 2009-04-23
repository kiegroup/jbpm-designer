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
	
	var _SCROLLBOX_CLASS_NAME = "movi-scrollbox",
		_MODELIMG_CLASS_NAME = "movi-modelimg";
	
	/**
     * Array to keep track of all instances
	 * @property _instances
     * @private
     */
	var _instances = new Array();

    /**
     * A widget to display an Oryx model
     * @namespace MOVI.widget
     * @class MOVI.widget.ModelViewer
     * @extends YAHOO.util.Element
     * @constructor
     * @param {HTMLElement | String} el The id of the container DIV element that will 
 	 * wrap the ModelViewer, or a reference to a DIV element. The DIV element must
	 * exist in the document.
     */
    MOVI.widget.ModelViewer = function(el) {

		// register the new instance
		this._index = _instances.length;
		_instances[this._index] = this;
		
    	MOVI.widget.ModelViewer.superclass.constructor.call(this, el); 

		this.set("innerHTML", "");
		
	  	// create scrollbox element
		this._scrollbox = new YAHOO.util.Element(document.createElement("div"));
		this._scrollbox.set("className", _SCROLLBOX_CLASS_NAME);
		this._scrollbox.set("id", _SCROLLBOX_CLASS_NAME + this._index);
		this._scrollbox.set("innerHTML", "<img id=\"" + _MODELIMG_CLASS_NAME + this._index + 
							"\" alt=\"oryx model\" class=\"" + _MODELIMG_CLASS_NAME + "\" />");
		this.appendChild(this._scrollbox);
		
		this._image = new YAHOO.util.Element(_MODELIMG_CLASS_NAME + this._index);
    };

	/**
     * Returns the ModelViewer instance with the specified id.
     * @method getInstance
     * @param {Integer | String} id The id of the ModelViewer instance, 
	 * or the id of the instance's host DIV element.
     * @return {MOVI.widget.ModelViewer} A ModelViewer instance
     */
	MOVI.widget.ModelViewer.getInstance = function(id) {
		if(YAHOO.lang.isNumber(id)) {
			return _instances[id];
		} else {
			for(key in _instances) {
				if(_instances[key].get("id")==id)
					return _instances[key];
			}
		}
	};
	
 	MOVI.extend(MOVI.widget.ModelViewer, YAHOO.util.Element, {
	
		/**
	     * URI of the model to display
	     * @property _modelUri
		 * @type String
		 * @private
	     */
		_modelUri: "",
		
		/**
	     * Options passed to loadModel method
	     * @property _loadOptions
		 * @type Object
		 * @private
	     */
		_loadOptions: {},
		
		/**
	     * The canvas container Element realizing scrolling
	     * @property _scrollbox
		 * @type Element
		 * @private
	     */
		_scrollbox: null,
		
		/**
	     * The image element
	     * @property _image
		 * @type Element
		 * @private
	     */
		_image: null,
		
		/**
		 * The model's canvas
		 * @property canvas
		 * @type Canvas
		 */
		canvas: null,
		
		/**
	     * Callback that is executed when the model is finished
		 * loading.
	     * @method _onSuccess
		 * @private
	     */
		_onSuccess: function() {
			MOVI.log("Model loaded successfully.", "info", "modelviewer.js");
			var scope = this._loadOptions.scope || window;
			if(this._loadOptions.onSuccess)
				this._loadOptions.onSuccess.call(scope, this);
		},
		
		/**
	     * Callback for handling model load failures.
	     * @method _onLoadFailure
		 * @private
	     */
		_onLoadFailure: function() {
			MOVI.log("Could not load model.", "error", "modelviewer.js");
			var scope = this._loadOptions.scope || window;
			if(this._loadOptions.onFailure)
				this._loadOptions.onFailure.call(scope, this);
		},
		
		/**
	     * Callback for handling model load timeouts.
	     * @method _onLoadTimeout
		 * @private
	     */
		_onLoadTimeout: function() {
			MOVI.log("A timeout occured while trying to load model.", "error", "modelviewer.js");
			var scope = this._loadOptions.scope || window;
			if(this._loadOptions.onTimeout)
				this._loadOptions.onTimeout.call(scope, this);
			else if(this._loadOptions.onFailure)
				this._loadOptions.onFailure.call(scope, this);
		},
		
		/**
	     * Callback for handling stencil set load failures.
	     * @method _onStencilSetLoadFailure
		 * @private
	     */
		_onStencilSetLoadFailure: function() {
			MOVI.log("Could not load stencil set for model.", "error", "modelviewer.js");
			var scope = this._loadOptions.scope || window;
			if(this._loadOptions.onFailure)
				this._loadOptions.onFailure.call(scope, this);
		},
		
		/**
	     * Callback for handling stencil set load timeouts.
	     * @method _onStencilSetLoadTimeout
		 * @private
	     */
		_onStencilSetLoadTimeout: function() {
			MOVI.log("A timeout occured while trying to load stencil set for model.", "error", "modelviewer.js");
			var scope = this._loadOptions.scope || window;
			if(this._loadOptions.onTimeout)
				this._loadOptions.onTimeout.call(scope, this);
			else if(this._loadOptions.onFailure)
				this._loadOptions.onFailure.call(scope, this);
		},

		/**
	     * Loads the specified Oryx model in the viewer.
	     * @method loadModel
	     * @param {String} uri The URI of the model (without /self). 
		 * For example: 'http://oryx-editor.org/backend/poem/model/1234'
		 * @param {Object} opt Options: 
         * <dl>
         * <dt>onSuccess</dt>
         * <dd>
         * Callback to execute when the model is finished loading
         * The callback receives the ModelViewer instance back.
         * </dd>
         * <dt>onFailure</dt>
         * <dd>
         * Callback to execute when the model load operation fails
         * The callback receives the ModelViewer instance back.
         * </dd>
		 * <dt>onTimeout</dt>
         * <dd>
         * Callback to execute when a timeout occurs. If not set
		 * the onFailure callback will be executed on timeout.
         * The callback receives the ModelViewer instance back.
         * </dd>
 		 * <dt>scope</dt>
		 * <dd>The execution scope for the callbacks.</dd>
 	 	 * <dt>timeout</dt>
         * <dd>
         * Number of milliseconds to wait before aborting the loading
		 * of the model and executing the onFailure callback.
		 * The default value is 15000 (15 seconds).
         * </dd>
		 * </dl>
		 * @throws Exception, if passed URI is not valid
	     */
		loadModel: function(uri, opt) {
			
			if(!YAHOO.lang.isString(uri)) {
				throw new URIError("No valid URI passed to loadModel." , "modelviewer.js");
				this._onLoadFailure();
			}
			
			this._modelUri = uri;
			
			this._loadOptions = opt || {};
			if(!this._loadOptions.timeout)
				this._loadOptions.timeout = 15000; // default timeout
			
			var jsonp = encodeURIComponent(
				"MOVI.widget.ModelViewer.getInstance(" + 
				this._index + ").loadModelCallback");
			var url = uri + "/json?jsonp=" + jsonp;
			
			var transactionObj = YAHOO.util.Get.script(url, {
				onFailure: this._onLoadFailure, 
				onTimeout: this._onLoadTimeout,
				timeout  : this._loadOptions.timeout,
				scope    : this
			});
			
			this._loadImage(uri);
		},
		
		/**
	     * Load the PNG image representation of the model.
	     * @method _loadImage
		 * @param {String} uri The URI of the model (without /self).
		 * @private
	     */
		_loadImage: function(uri) {
			// append timestamp to allow reloads of the image
			this._image.set("src", uri + "/png?" + (new Date()).getTime());
		},
		
		/**
	     * JSONP callback to load model data from the JSON web service
	     * @method loadModelCallback
	     * @param jsonObj The delivered JSON Object
	     */
		loadModelCallback: function(jsonObj) {
			
			if(this.canvas!=null)
				this._scrollbox.removeChild(this.canvas);
			
			this.canvas = jsonObj;

			if(!this.canvas.stencilset) {
				MOVI.log("Could not find stencilset definition for model.", "error", "canvas.js");
				this._onFailure();
			}
			
			var jsonp = encodeURIComponent(
				"MOVI.widget.ModelViewer.getInstance(" + 
				this._index + ").loadStencilSetCallback");
			var i = this.canvas.stencilset.url.indexOf("/stencilsets/");
			var url = this.canvas.stencilset.url.substring(0, i) +
				"/jsonp?resource=" + encodeURIComponent(this.canvas.stencilset.url.substring(i+13)) + 
				"&jsonp=" + jsonp;

			var transactionObj = YAHOO.util.Get.script(url, {
				onFailure: this._onStencilSetLoadFailure, 
				onTimeout: this._onStencilSetLoadTimeout,
				data	 : this._loadOptions, 
				timeout  : this._loadOptions.timeout,
				scope    : this 
			});

		},
		
		/**
	     * JSONP callback to load stencilset data from the JSON web service
	     * @method loadStencilSetCallback
	     * @param jsonObj The delivered JSON Object
	     */
		loadStencilSetCallback: function(jsonObj) {
			var prefix = "movi_" + this._index + "-";
			try {
				this.canvas.stencilset = new MOVI.stencilset.Stencilset(jsonObj);
				this.canvas = new MOVI.model.Canvas(this, this.canvas, prefix);
			} catch(e) {
				MOVI.log("A " + e.name + " occured while trying to load model: " + 
							e.message, "error", "modelviewer.js");
				this._onLoadFailure();
				return;
			}
			this._scrollbox.appendChild(this.canvas);
			this._onSuccess();
		},
		
		/**
		 * Returns the width of the model image element in pixels
		 * @method getImgWidth
		 * @return {Integer} The image width
		 */
		getImgWidth: function() {
			return this._image.get("width");
		},
		
		/**
		 * Returns the height of the model image element in pixels
		 * @method getImgHeight
		 * @return {Integer} The image height
		 */
		getImgHeight: function() {
			return this._image.get("height");
		},
		
		/**
		 * Returns the absolute URI of the displayed model
		 * @method getImgHeight
		 * @return {String} The models absolute URI
		 */
		getModelUri: function() {
			return this._modelUri;
		},
		
		/**
		 * Returns the canvas container Element realizing scrolling
		 * @method getScrollboxEx
		 * @return {Element} The scrollbox Element
		 */
		getScrollboxEl: function() {
			return this._scrollbox;
		},
		
		/**
		 * Scrolls to the shape, i.e. centers the (canvas around the) shape 
		 * within the scrollbox.
		 * @method scrollToShape()
		 * @param {Shape} shape the shape or its resource id
		 * @return {Element} The shape element / null if it does not exist
		 * @throws Error if scrollbox dimensions cannot be calculated
		 */
		scrollToShape: function(shape) {
			if ("string" == typeof(shape)) {
				shape = this.canvas.getShape(shape);
				if (null == shape) {
					return null;
				}
			}
			
			if (null != (h = this.getScrollboxEl().getStyle("height").match(/(\d+)px/)) &&
			    null != (w = this.getScrollboxEl().getStyle("width").match(/(\d+)px/)))
			{
				var bounds = shape.getAbsBounds();			

				var origin = {
					x: bounds.upperLeft.x + (bounds.lowerRight.x - bounds.upperLeft.x)/2,
					y: bounds.upperLeft.y + (bounds.lowerRight.y - bounds.upperLeft.y)/2
				}

				var target = {
					x: parseInt(origin.x - parseInt(w[1])/2 ),
					y: parseInt(origin.y - parseInt(h[1])/2 )
				}

				this.getScrollboxEl().set("scrollTop", target.y);
				this.getScrollboxEl().set("scrollLeft", target.x);
			}
			else {
				throw new Error("Unable to calculate scrollbox dimensions", "modelviewer.js");
			}
			
			return shape;
		},
		
		/**
		 * Scrolls to the specified position, centers the (canvas around the) position 
		 * within the scrollbox.
		 * @method scrollToShape()
		 * @param {Integer} x The center's x position
		 * @param {Integer} y The center's y position
		 * @throws Error if scrollbox dimensions cannot be calculated
		 */
		centerScrollTo: function(x, y) {
			if(!YAHOO.lang.isNumber(x) || !YAHOO.lang.isNumber(y)) {
				throw new TypeError("The coordinates passed to centerScrollTo(x, y) have to be of type Integer.", 
									"modelviewer.js");
			}
			
			var left = Math.round(x - parseInt(this.getScrollboxEl().getStyle("width"), 10)/2),
				top  = Math.round(y - parseInt(this.getScrollboxEl().getStyle("height"), 10)/2);
			this.getScrollboxEl().set("scrollLeft", left);
			this.getScrollboxEl().set("scrollTop", top);
		}
		
	});
	

})();
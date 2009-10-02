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

		this.onZoomLevelChange = new YAHOO.util.CustomEvent("movi-zoomLevelChange", this); 
		this.onZoomLevelChangeStart = new YAHOO.util.CustomEvent("movi-zoomLevelChangeStart", this); 
		this.onZoomLevelChangeEnd = new YAHOO.util.CustomEvent("movi-zoomLevelChangeEnd", this); 

		var existingScrollboxArr = this.getElementsByClassName(_SCROLLBOX_CLASS_NAME);
		if(existingScrollboxArr.length==1) {
			// use existing scrollbox element if available
			this._scrollbox = new YAHOO.util.Element(existingScrollboxArr[0]);
		} else {
			// otherwise create the scrollbox element
			this._scrollbox = new YAHOO.util.Element(document.createElement("div"));
			this._scrollbox.addClass(_SCROLLBOX_CLASS_NAME);
			this.appendChild(this._scrollbox);
		}
		
		this._scrollbox.set("id", _SCROLLBOX_CLASS_NAME + this._index);
		this._scrollbox.set("innerHTML", "<img id=\"" + _MODELIMG_CLASS_NAME + this._index + 
							"\" class=\"" + _MODELIMG_CLASS_NAME + "\" />");
		
		this._image = new YAHOO.util.Element(_MODELIMG_CLASS_NAME + this._index);
		
		// callbacks for model dragging
		this.addListener("mousedown", this._onMouseDown, this, this, true);
		YAHOO.util.Event.addListener(document, "mouseup", this._onMouseUp, this, this, true);
		
		this._loadOptions = {};
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
			for(var i = 0; i < _instances.length; i++) {
				if(_instances[i].get("id")==id)
					return _instances[i];
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
		_loadOptions: null,
		
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
	     * The original width of the image
	     * @property _imageWidth
		 * @type Integer
		 * @private
	     */
		_imageWidth: 0,
		
		/**
	     * The original height of the image
	     * @property _imageHeight
		 * @type Integer
		 * @private
	     */
		_imageHeight: 0,
		
		/**
	     * The model zoom level in percent
	     * @property _zoomLevel
		 * @type Number
		 * @private
	     */
		_zoomLevel: 100,
		
		/**
	     * An array that stores tokens that identify resources to load. Loading of these resources is 
		 * synchronized before the model load success callback is executed
	     * @property _syncResources
		 * @type Array
		 * @private
	     */
		_syncResources: null,
		
		/**
		 * The model's canvas
		 * @property canvas
		 * @type Canvas
		 */
		canvas: null,
		
		/**
		 * The event that is triggered when the model zoom level changes
		 * @property onZoomLevelChange
		 * @type YAHOO.util.CustomEvent
		 */
		onZoomLevelChange: null,
		
		/**
		 * The event that is triggered when the model zoom level change starts
		 * (on zoom slider slideStart event)
		 * @property onZoomLevelChangeStart
		 * @type YAHOO.util.CustomEvent
		 */
		onZoomLevelChangeStart: null,
		
		/**
		 * The event that is triggered when the model zoom level change is finished
		 * (on zoom slider slideEnd event)
		 * @property onZoomLevelChangeEnd
		 * @type YAHOO.util.CustomEvent
		 */
		onZoomLevelChangeEnd: null,
		
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
	     * Synchronization of asynchronous resource loading. When all resources (model image and 
		 * model data) are loaded successfully this method will trigger the execution of the 
		 * model load success callback.
		 * @method _syncLoadingReady
	     * @param resource {String} A string that identifies the successfully loaded resource ("image" or "data")
		 * @private
	     */
		_syncLoadingReady: function(resource) {
			// get index of resource in array
			for(var i=0; i<this._syncResources.length; i++) {
				if( this._syncResources[i]==resource ||
				    (this._syncResources[i].indexOf("_")>0 && 
				    this._syncResources[i].substring(0, this._syncResources[i].indexOf("_"))==resource) ) 
				        break;
			}
			if(i>=0) {
				this._syncResources.splice(i, 1);
			}
			
			if(this._syncResources.length==0) {
			    if(this._initCanvas()) this._onSuccess();
			}
				
		},
		
		/**
		 * Initialize the Canvas object when all model, stencil set, and stencil
		 * set extension data are available.
		 * @method _initCanvas
		 * @private
		 */
		_initCanvas: function() {
		    
		    // first process all stencil set extensions
		    for(var i=0; i<this._ssextensions.length; i++) {
		        this.canvas.stencilset.addExtension(this._ssextensions[i]);
		    }
		    // then initialize the Canvas object
		    var prefix = "movi_" + this._index + "-";
		    try {
				this.canvas = new MOVI.model.Canvas(this, this.canvas, prefix);
			} catch(e) {
				MOVI.log("A " + e.name + " occured while trying to initialize the model: " + 
							e.message, "error", "modelviewer.js");
				this._onLoadFailure();
				return false;
			}
			this._scrollbox.appendChild(this.canvas);
			return true;
		},
		
		/**
	     * Returns the model viewer index (unique per page)
	     * @method getIndex
	     */
		getIndex: function() {
			return this._index;
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
         * <dt>urlModificationFunction</dt>
         * <dd>
         * Function that takes the URL of the model PNG and JSON representation 
         * and of the model's stencil set as parameter and returns a modificated 
         * URL string for the request.
         * The function is called in the specified scope with an additional
         * parameter that indicates the type of the requested resource 
         * ("png", "json", "stencilset")
         * </dd>
		 * </dl>
		 * @throws Exception, if passed URI is not valid
	     */
		loadModel: function(uri, opt) {
			
			if(!YAHOO.lang.isString(uri)) {
				throw new URIError("No valid URI passed to loadModel." , "modelviewer.js");
				this._onLoadFailure();
			}
			
			if(uri.charAt(uri.length-1)=="/") uri = uri.substr(0, uri.length-1);
			this._modelUri = uri;
			
			this._loadOptions = opt || {};
			if(!this._loadOptions.timeout)
				this._loadOptions.timeout = 15000; // default timeout
				
			this._syncResources = new Array();
			this._syncResources.push("data"); // include model data in synchronization
			
			var jsonp = encodeURIComponent(
				"MOVI.widget.ModelViewer.getInstance(" + 
				this._index + ").loadModelCallback");
			var url = uri + "/json?jsonp=" + jsonp;
			
			if(YAHOO.lang.isFunction(this._loadOptions.urlModificationFunction))
			    url = this._loadOptions.urlModificationFunction.call(this._loadOptions.scope || this, url, this, "json");
			
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
			
			this._syncResources.push("image"); // include image loading in synchronization
			
			// append timestamp to allow reloads of the image
			var imgUrl = uri + "/png?" + (new Date()).getTime();
			
			if(YAHOO.lang.isFunction(this._loadOptions.urlModificationFunction))
			    imgUrl = this._loadOptions.urlModificationFunction.call(this._loadOptions.scope || this, imgUrl, this, "png");
			
			var self = this;
			this._image.get("element").onload = function() {
				self._imageWidth = parseInt(self._image.getStyle("width"), 10) || self._image.get("element").offsetWidth;
				self._imageHeight = parseInt(self._image.getStyle("height"), 10) || self._image.get("element").offsetHeight;
				self._syncLoadingReady("image"); // notify successful loading of image
			};
			
			this._image.set("src", imgUrl);
			
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
				MOVI.log("Could not find stencil set definition for model.", "error", "canvas.js");
				this._onFailure();
			}
			
			// check if some stencil set extensions need to be loaded
			
			if(this.canvas.ssextensions) {
			    for(var i=0; i<this.canvas.ssextensions.length; i++) {
			        this._syncResources.push("ssextension_" + i); // add to resource loading sync
			    }
			}
			
			// load stencil set
			
			if(this.canvas.stencilset.url.substring(0, 7)!="http://") {
				// relative stencilset url, make absolute
				// guess server base url (model url substring until first slash)
				var index = this._modelUri.substring(7).indexOf("/");
				this.canvas.stencilset.url = this._modelUri.substring(0, 7+index) + this.canvas.stencilset.url;
			}
			
			var jsonp = encodeURIComponent(
				"MOVI.widget.ModelViewer.getInstance(" + 
				this._index + ").loadStencilSetCallback");
			var pathStringIndex = this.canvas.stencilset.url.indexOf("/stencilsets/");
			
			var url = this.canvas.stencilset.url.substring(0, pathStringIndex) +
				"/jsonp?resource=" + encodeURIComponent(this.canvas.stencilset.url.substring(pathStringIndex+13)) + 
				"&type=stencilset&jsonp=" + jsonp;
				
			if(YAHOO.lang.isFunction(this._loadOptions.urlModificationFunction))
    		    url = this._loadOptions.urlModificationFunction.call(this._loadOptions.scope || this, url, this, "stencilset");

			var transactionObj = YAHOO.util.Get.script(url, {
				onFailure: this._onStencilSetLoadFailure, 
				onTimeout: this._onStencilSetLoadTimeout,
				data	 : this._loadOptions, 
				timeout  : this._loadOptions.timeout,
				scope    : this 
			});
			
			// load all stencil set extensions
			
			this._ssextensions = new Array();
            if(this.canvas.ssextensions) {
			    for(var i=0; i<this.canvas.ssextensions.length; i++) {
			        var ssext_jsonp = encodeURIComponent(
        				"MOVI.widget.ModelViewer.getInstance(" + 
        				this._index + ").loadStencilSetExtensionCallback");
			        var ssext_url = this.canvas.stencilset.url.substring(0, pathStringIndex) +
        				"/jsonp?resource=" + encodeURIComponent(this.canvas.ssextensions[i]) + 
        				"&type=ssextension&jsonp=" + ssext_jsonp;
        				
        			if(YAHOO.lang.isFunction(this._loadOptions.urlModificationFunction))
                	    ssext_url = this._loadOptions.urlModificationFunction.call(this._loadOptions.scope || this, ssext_url, this, "ssextension");
			        
			        var ssext_transactionObj = YAHOO.util.Get.script(ssext_url, {
            			onFailure: this._onStencilSetLoadFailure, 
            			onTimeout: this._onStencilSetLoadTimeout,
            			data	 : this._loadOptions, 
            			timeout  : this._loadOptions.timeout,
            			scope    : this 
            		});
			    }
			}
		},
		
		/**
	     * JSONP callback to load stencilset data from the JSON web service
	     * @method loadStencilSetCallback
	     * @param jsonObj The delivered JSON Object
	     */
		loadStencilSetCallback: function(jsonObj) {
			this.canvas.stencilset = new MOVI.stencilset.Stencilset(jsonObj);
			this._syncLoadingReady("data"); // notify successful loading of data
		},
		
		/**
	     * JSONP callback to load stencilset extension data from the JSON web service
	     * @method loadStencilSetExtensionCallback
	     * @param jsonObj The delivered JSON Object
	     */
		loadStencilSetExtensionCallback: function(jsonObj) {
			this._ssextensions.push(jsonObj); 
			this._syncLoadingReady("ssextension"); // notify successful loading
		},
		
		/**
		 * Returns the original width (100% zoom level) of the model image element in pixels
		 * @method getImgWidth
		 * @return {Integer} The image width
		 */
		getImgWidth: function() {
			return this._imageWidth || this.getScrollboxEl().get("element").offsetWidth;
		},
		
		/**
		 * Returns the original height (100% zoom level) of the model image element in pixels
		 * @method getImgHeight
		 * @return {Integer} The image height
		 */
		getImgHeight: function() {
			return this._imageHeight || this.getScrollboxEl().get("element").offsetHeight;
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
		 * @method scrollToShape
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
				
				var zoomFactor = this.getZoomLevel()/100;

				var target = {
					x: parseInt(origin.x*zoomFactor - parseInt(w[1])/2 ),
					y: parseInt(origin.y*zoomFactor - parseInt(h[1])/2 )
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
		 * @method centerScrollTo
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
		},
		
		/**
		 * Set the model zoom level in percent (minimum: 0, maximum: 100)
		 * @method setZoomLevel
		 * @param {Number} percent The zoom level in percent
		 * @param {Boolean} notifyStartEnd (optional) If set to false markers, annotations and shape rects will 
		 * not be updated. Default is true.
		 */
		setZoomLevel: function(percent, notifyStartEnd) {
			if(!YAHOO.lang.isNumber(percent)) {
				throw new TypeError("The parameter passed to have to setZoomLevel has to be of type Number.", 
									"modelviewer.js");
			}
			if(percent<=0) {
				throw new RangeError("The zoom level must be greater than 0.", "modelviewer.js");
			} else if(percent>100) {
				throw new RangeError("The zoom level must not be greater than 100.", "modelviewer.js");
			}
			
			this._zoomLevel = percent;
			
			this._image.setStyle("width", Math.round(this.getImgWidth()*this._zoomLevel/100) + "px");
			this._image.setStyle("height", Math.round(this.getImgHeight()*this._zoomLevel/100) + "px");
			
			// fire events
			if(notifyStartEnd!==false) this.onZoomLevelChangeStart.fire(this._zoomLevel);
			this.onZoomLevelChange.fire(this._zoomLevel);
			if(notifyStartEnd!==false) this.onZoomLevelChangeEnd.fire(this._zoomLevel);
		},
		
		/**
		 * Returns the model zoom level in percent (minimum: 0, maximum: 100)
		 * @method getZoomLevel
		 * @return {Number} The current model zoom level in percent
		 */
		getZoomLevel: function() {
			return this._zoomLevel;
		},
		
		/**
		 * Set the model zoom level so that the model fits in height and width to the model viewer
		 * @method fitModelToViewer
		 */
		fitModelToViewer: function() {
			var scaleHorizontal = (this.getScrollboxEl().get("offsetWidth")-5) / this.getImgWidth();
			var scaleVertical = (this.getScrollboxEl().get("offsetHeight")-5) / this.getImgHeight();
			var scale = (scaleHorizontal < scaleVertical) ? scaleHorizontal : scaleVertical;
			if(scale>1)	scale = 1;
			this.setZoomLevel(scale*100);
		},
		
		/**
		 * @method _onMouseDown
		 * @private
		 */
		_onMouseDown: function(ev) {
			/*YAHOO.util.Event.preventDefault(ev);
			this._absXY = YAHOO.util.Dom.getXY(this);
			var mouseAbsXY = YAHOO.util.Event.getXY(ev);
			var modelviewerRectAbsXY = YAHOO.util.Dom.getXY(this);
			this._mouseOffset.x = mouseAbsXY[0] - modelviewerRectAbsXY[0];
			this._mouseOffset.y = mouseAbsXY[1] - modelviewerRectAbsXY[1];*/

			if (ev.target == this._scrollbox.get("element")) { return }
			
			YAHOO.util.Event.preventDefault(ev);
			YAHOO.util.Event.stopPropagation(ev);
			var mouseAbsXY = YAHOO.util.Event.getXY(ev);
			this._mouseCoords = { x: mouseAbsXY[0], y: mouseAbsXY[1] };
			this.addListener("mousemove", this._onModelDrag, this, this, true);
		},
		
		/**
		 * @method _onMouseUp
		 * @private
		 */
		_onMouseUp: function(ev) {
			YAHOO.util.Event.preventDefault(ev);
			this.removeListener("mousemove", this._onModelDrag);
		},
		
		/**
		 * Callback method that is executed when the model is dragged
		 * @method _onModelDrag
		 * @private
		 */
		_onModelDrag: function(ev) {
			YAHOO.util.Event.preventDefault(ev);
			var mouseAbsXY = YAHOO.util.Event.getXY(ev);
			var sl = this._scrollbox.get("scrollLeft"); 
			var st = this._scrollbox.get("scrollTop");
			this._scrollbox.set("scrollLeft", sl - (mouseAbsXY[0] - this._mouseCoords.x));
			this._scrollbox.set("scrollTop", st - (mouseAbsXY[1] - this._mouseCoords.y));
			this._mouseCoords.x = mouseAbsXY[0]; this._mouseCoords.y = mouseAbsXY[1];
		}
		
		
	});
	
})();
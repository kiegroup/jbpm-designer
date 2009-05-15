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

// Declare the MOVI global namespace
if (typeof MOVI == "undefined" || !MOVI) { var MOVI = {}; }

/**
 * Declare the movi module (needed for doc generation)
 * @module movi
 */


/**
 * Returns the namespace specified and creates it if it doesn't exist
 * <pre>
 * MOVI.namespace("property.package");
 * MOVI.namespace("MOVI.property.package");
 * </pre>
 * Either of the above would create MOVI.property, then
 * MOVI.property.package
 *
 * Be careful when naming packages. Reserved words may work in some browsers
 * and not others. For instance, the following will fail in Safari:
 * <pre>
 * MOVI.namespace("really.long.nested.namespace");
 * </pre>
 * This fails because "long" is a future reserved word in ECMAScript
 *
 * This method is taken from YUI's YAHOO object.
 *
 * @method namespace
 * @static
 * @param  {String*} arguments 1-n namespaces to create 
 * @return {Object}  A reference to the last namespace object created
 */
MOVI.namespace = function() {
    var a=arguments, o=null, i, j, d;
    for (i=0; i<a.length; i=i+1) {
        d=a[i].split(".");
        o=MOVI;

        // MOVI is implied, so it is ignored if it is included
        for (j=(d[0] == "MOVI") ? 1 : 0; j<d.length; j=j+1) {
            o[d[j]]=o[d[j]] || {};
            o=o[d[j]];
        }
    }

    return o;
};

// Adopt Yahoo's inheritance mechanism
MOVI.extend = YAHOO.extend;

// Adopt Yahoo's logger
MOVI.log = YAHOO.log;

/**
 * Loads the required YUI resources and all MOVI scripts. When loading
 * is finished the specified callback is executed.
 *
 * Use this method to initialize MOVI and wrap your MOVI specific code
 * in the callback function passed to this method.
 * <pre>
 * MOVI.init(function() {
 *     var modelViewer = new MOVI.widget.ModelViewer(YAHOO.util.Dom.get("myMoviDivId"));
 * }, "script/movi", setUpMyUI, ["editor"]);
 * </pre>
 *
 * @method init
 * @static
 * @param {Function} modelReadyCallback The function to call after loading of the model is finished
 * @param {String} moviBase The path to the base directory of MOVI.
 * @param {Function} yuiReadyCallback (optional) The function to call after loading of the YUI modules is finished
 * @param {String*} yuiModules (optional) Additional YUI modules to load. By default MOVI loads 'yahoo', 'dom', 'event', 
 * 'get', 'event', 'logger', 'resize'. Use this parameter if your script depends on other YUI modules.
 */
MOVI.init = function(callback, moviBase, yuiReadyCallback, yuiModules) {
	
	var _YUI_BASE_DIR = "http://yui.yahooapis.com/2.7.0/build/";
	
	if(!YAHOO.lang.isFunction(callback)) callback = function(){};
	
	if(!YAHOO.lang.isFunction(yuiReadyCallback)) yuiReadyCallback = undefined;
	
	if(!YAHOO.lang.isArray(yuiModules)) yuiModules = [];
	
	// load required YUI modules
	var yuiLoader = new YAHOO.util.YUILoader({
		require: yuiModules.concat(["yahoo", "dom", "element", "get", "event", "logger"]), 
		base: _YUI_BASE_DIR,
		loadOptional: true,
		filter: "RAW",
		onSuccess: function() {
			
			if(yuiReadyCallback)
				yuiReadyCallback();
			
			YAHOO.widget.Logger.enableBrowserConsole();  // remove this line for production use 
			
			if(!YAHOO.lang.isString(moviBase)) {
				throw new Error("MOVI base directory is not specified.", "movi.js");
				return false;
			}
			
			YAHOO.util.Get.css(moviBase + "/style/movi.css", {});
			if (YAHOO.env.ua.ie > 0) { 
				// load custom stylesheets for IE
				YAHOO.util.Get.css(moviBase + "/style/movi_ie.css", {});
			}
			
			// load MOVI modules in correct order to satisfy dependencies
			// path references are relative to YUI root directory
			YAHOO.util.Get.script([ moviBase + "/config.js", 
									moviBase + "/modelviewer.js", 
									moviBase + "/stencil.js", 
									moviBase + "/stencilset.js", 
									moviBase + "/shape.js", 
									moviBase + "/node.js", 
									moviBase + "/edge.js", 
									moviBase + "/canvas.js",
									moviBase + "/marker.js", 
									moviBase + "/annotation.js",
									moviBase + "/modelnavigator.js", 
									moviBase + "/shapeselect.js", 
									moviBase + "/toolbar.js" ], {
				onSuccess: callback,  // execute user specified callback
				onFailure: function() {
					throw new Error("Unable to load MOVI modules from base dir '" + moviBase + "'", "movi.js");
				}
			});
		},
		onFailure: function(msg, xhrobj) { 
			var m = "Unable to load YUI modules from base dir '" + _YUI_BASE_DIR + "': " + msg; 
			if (xhrobj) { 
				m += ", " + YAHOO.lang.dump(xhrobj); 
			} 
			throw new Error(m, "movi.js"); 
		}
	});
	
	yuiLoader.insert();
};
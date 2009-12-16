
/**
 * Copyright (c) 2009
 * Helen Kaltegaertner
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

GADGET_DIR = "/gadgets/files/gadgets/core/"
MULTIMODEL_DIR = "/gadgets/files/gadgets/multimodel/"
	
var multimodel = null;
	
// load required YUI modules	
new YAHOO.util.YUILoader({ 
    base: "http://yui.yahooapis.com/2.7.0/build/", 
    require: ["yahoo", "datatable","animation","paginator",
        		"fonts","grids","layout","reset","resize", 
        		"button", "connection", "container", "dragdrop"], 
    loadOptional: false, 
    filter: "RAW", 
    onSuccess: function() {
			getScripts();
    }, 
    onFailure: function() {
    	alert("Failed loading required YUI components");
    }
}).insert(); 


// load gadget specific scripts and create an instance of the current gadget type if successful
getScripts = function(){

	YAHOO.util.Get.script([  GADGET_DIR + "abstractGadget.js", 
	                         MULTIMODEL_DIR + "multimodel.js",
	                         MULTIMODEL_DIR + "connector.js",
	                         MULTIMODEL_DIR + "connection.js",
	                         MULTIMODEL_DIR + "discovery.js"],
			{
				onSuccess : function(){ 
							multimodel = new MultiModel(); 
				} ,
				onFailure : function(){ alert("Error loading scripts!"); }
			}
	);
	
} 
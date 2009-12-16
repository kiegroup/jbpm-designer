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

// parent directory for all local gadgets
GADGET_DIR = "/gadgets/files/gadgets/core/"
// subdirectory for specific viewer gadget
VIEWER_DIR = "/gadgets/files/gadgets/viewer/"
var viewer = null;
	
// load MOVI files
MOVI.init(
				
	function(){
		
	    new YAHOO.util.YUILoader({ 
			base: "http://yui.yahooapis.com/2.7.0/build/", 
	        require: ["yahoo", "fonts","grids","layout","reset","resize"],
	        loadOptional: false, 
	        combine: true, 
	        filter: "MIN", 
	        allowRollup: true, 
	        onSuccess: function(){ getScripts();}
	    }).insert(); 

	},
	"http://oryx-editor.googlecode.com/svn/trunk/poem-jvm/src/javascript/movi/src",
	undefined,
	["resize"]
);

/*
 * load relevant scripts from gadget directory and the viewer subdirectory
 * 
 */
getScripts = function(){
	
	YAHOO.util.Get.script([  GADGET_DIR + "abstractGadget.js", VIEWER_DIR + "viewer.js", VIEWER_DIR + "ViewerRpc.js" ],
			{
		 		// instantiate viewer gadget
				onSuccess : function(){ viewer = new Viewer(); } ,
				onFailure : function(){ alert("Error loading scripts!"); }
			}
	);
	
}


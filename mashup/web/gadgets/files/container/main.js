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


var Container = {};

Container.init = function(){
	
	Container.extractGadgeatData();
	
	document.write(unescape("%3Clink href='" + Container.ShindigBase + 'gadgets/files/container/gadgets.css' + "'rel='stylesheet'%3E%3C/link%3E"));
	
	// java script files from shindig
	var shindigFiles = ['gadgets/js/rpc.js?c=1&debug=1', 
				'gadgets/files/container/cookies.js',
				'gadgets/files/container/util.js',
				'gadgets/files/container/gadgets.js',
				'gadgets/files/container/cookiebaseduserprefstore.js'];

	// load files from shindig using configured shindig base
	for (var i = 0; i < shindigFiles.length; i++)
		document.write(unescape("%3Cscript src='" 
				+ Container.ShindigBase + shindigFiles[i]
				+ "'type='text/javascript'%3E%3C/script%3E"));	
};

Container._loadConfig = function() {
	
	// load configure xml
	var xmlDoc;

	if (window.XMLHttpRequest){
		xmlDoc = new window.XMLHttpRequest();
		xmlDoc.open("GET",'gadgets.xml',false);
		xmlDoc.send("");
		return xmlDoc.responseXML;
	}
	
	//IE
	else if (ActiveXObject("Microsoft.XMLDOM")){
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async = false;
		xmlDoc.load('gadgets.xml');
		return xmlDoc;
	}
	alert("Error loading document gadgets.xml");
	return null;
	
};

Container.extractGadgeatData = function(){
	
	Container.gadgetData = new Array();
	
	// extract paths from config xml
	var xml = Container._loadConfig();

	Container.ShindigBase 	= xml.getElementsByTagName("ShindigBase")[0].textContent;
	Container.relayUrl 		= xml.getElementsByTagName("ShindigBase")[1].textContent;
	
	var gadgets 			= xml.getElementsByTagName("Gadget");
	
	//extrat options, position and bounds
	for (var i = 0; i < gadgets.length; i++){
		
		var options = {};
		
		if ( gadgets[i].getElementsByTagName("options")[0] ){
			
			var optionsTmp = gadgets[i].getElementsByTagName("options")[0];
			
			if ( optionsTmp.getElementsByTagName("minHeight")[0] )
				options.minHeight = optionsTmp.getElementsByTagName("minHeight")[0].textContent;
			
			if ( optionsTmp.getElementsByTagName("minWidth")[0] )
				options.minWidth = optionsTmp.getElementsByTagName("minWidth")[0].textContent;
			
			if ( optionsTmp.getElementsByTagName("top")[0] )
				options.top = optionsTmp.getElementsByTagName("top")[0].textContent;
			
			if ( optionsTmp.getElementsByTagName("left")[0] )
				options.left = optionsTmp.getElementsByTagName("left")[0].textContent;
			
			if ( optionsTmp.getElementsByTagName("height")[0] )
				options.height = optionsTmp.getElementsByTagName("height")[0].textContent;
			
			if ( optionsTmp.getElementsByTagName("width")[0] )
				options.width = optionsTmp.getElementsByTagName("width")[0].textContent;
			
		}
		
		var url = gadgets[i].getElementsByTagName("url")[0].textContent;
		
		Container.gadgetData[i] = {
				url : 		url,
				options : 	options,
				icon : 		optionsTmp.getElementsByTagName("icon")[0] ? optionsTmp.getElementsByTagName("icon")[0].textContent : null,
				title : 	url.split("/")[ url.split("/").length - 2 ]
		};	
	}
	
};

Container.init();

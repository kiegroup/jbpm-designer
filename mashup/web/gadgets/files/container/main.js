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
	
	// extract paths from config xml
	var xml = Container._loadConfig();

	Container.ShindigBase 	= xml.getElementsByTagName("ShindigBase")[0].textContent;
	Container.relayUrl 		= xml.getElementsByTagName("ShindigBase")[1].textContent;
	Container.viewerUrl 	= xml.getElementsByTagName("Gadget")[0].textContent;
	Container.repositoryUrl	= xml.getElementsByTagName("Gadget")[1].textContent;
	Container.connectorUrl	= xml.getElementsByTagName("Gadget")[2].textContent;
	Container.toolUrl 		= xml.getElementsByTagName("Gadget")[3].textContent;

	document.write(unescape("%3Clink href='" + Container.ShindigBase + 'gadgets/files/container/gadgets.css' + "'rel='stylesheet'%3E%3C/link%3E"));
	
	// java script files from shindig
	var shindigFiles = ['gadgets/js/rpc.js?c=1&debug=1', 
				'/gadgets/files/container/cookies.js',
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
		xmlDoc.open("GET",'configShindig.xml',false);
		xmlDoc.send("");
		return xmlDoc.responseXML;
	}
	
	//IE
	else if (ActiveXObject("Microsoft.XMLDOM")){
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async = false;
		xmlDoc.load('configShindig.xml');
		return xmlDoc;
	}
	alert("Error loading document");
	return null;
	
};

Container.init();

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

if (! Container)
	var Container = {};

Container.LayoutManager = function() {

	gadgets.LayoutManager.call(this);

};

Container.LayoutManager.inherits(gadgets.LayoutManager);

Container.LayoutManager.prototype.getGadgetChrome = function(gadget) {
	
	var widgets = dashboard.getGadgets();
	for (var i in widgets) {
		if (widgets[i].__gadget && widgets[i].__gadget == gadget) {
			return widgets[i].body;
		}
	}
	throw "No dashboard widget found";
};

gadgets.container.layoutManager = new Container.LayoutManager();

// adds a new panel to the dashboard 
// gadget_url specifies the widget to be rendered inside the panel
Container.addGadget = function(gadget_url, options) {
	
	var gadget = gadgets.container.createGadget({ specUrl: gadget_url });
	gadget.setServerBase( Container.ShindigBase + 'gadgets/');
	
	var widget = dashboard.addGadget(gadget_url, options);
	widget.__gadget = gadget;
	
	/*			
	widget.onResize = function(args) {
	console.log("*");			
	console.log("*", widget.__gadget);			
	
		widget.__gadget.style.width = args.width + "px"
		widget.__gadget.style.height = args.height + "px"
	}
	*/	
	
	gadget.getTitleBarContent = function(continuation){
		continuation("");
	}
	
	gadgets.container.addGadget(gadget);
	gadgets.container.renderGadget(gadget);

	console.log("*", widget.__gadget, gadget);
	
	gadgets.rpc.setRelayUrl(
		gadget.getIframeId(), 
		Container.relayUrl
	);
	
	return gadget;
	
};

// adds a viewer widget to the dashboard
Container.addViewer = function() {
	
	var options = {};
	
	options.minHeight	= 300;
	options.minWidth 	= 450;
	options.top 		= 50;
	options.left 		= 500;
	options.height 		= 500;
	options.width 		= 450;
	
	return Container.addGadget( Container.viewerUrl, options );
	
};

// adds a repository widget (overview of the users processes) to the dashboard
Container.addRepository = function() {
	
	var options = {};

	options.top 		= 50;
	options.left 		= 200;
	options.height 		= 300;
	options.width 		= 450;
	
	Container.addGadget(Container.repositoryUrl, options);
};

// adds a connector widget to the dashboard to create associations between different models 
Container.addConnector = function() {
	
	var options = {};

	options.top 		= 50;
	options.left 		= 200;
	options.height 		= 300;
	options.width 		= 450;
	
	Container.addGadget(Container.connectorUrl, options);
};

// just to test RPC functionality
Container.addTestTool = function() {

	Container.addGadget( Container.toolUrl, {} );
	
};


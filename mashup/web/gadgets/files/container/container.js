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

SERVER_BASE = "http://localhost:8080"

Container.LayoutManager = function() {
	gadgets.LayoutManager.call(this);
};

Container.LayoutManager.inherits(gadgets.LayoutManager);

Container.LayoutManager.prototype.getGadgetChrome = function(gadget) {
	
	var widgets = dashboard.getGadgets();
	for (var i in widgets) {
		if (widgets[i].gadget && widgets[i].gadget == gadget) {
			return widgets[i].chromeEl;
		}
	}
	throw "No dashboard widget found";
};

gadgets.container.layoutManager = new Container.LayoutManager();

/**
 * adds a new panel to the dashboard 
 * gadget_url specifies the widget to be rendered inside the panel
 * arguments determined by eventhandler, last argument "args" customizable
 * 
 */
Container.addGadget = function(p_sType, p_aArgs, args) {

	gadget_url 	= SERVER_BASE + args.url;
	options 	= args.options;
	width 		= dashboard.columnWidth - 10;
	
	var gadget = gadgets.container.createGadget({ 
		specUrl: 	gadget_url,
		width: 		width,
		height: 	options.height || "500px"
	});
	gadget.setServerBase( Container.ShindigBase + 'gadgets/');
	
	var widget = dashboard.addGadget(args.title || "", gadget, options);
	
	// remove gadget title bar
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

/*
 * adds initial gadget to the dashboard allowing others to be added
 */
Container.loadGadgetRepository = function(){
	
	for (var i = 0; i < Container.gadgetData.length; i++) {
		
		if ( Container.gadgetData[i].title == "gadgetRepository" ){
			Container.addGadget( null, null, 
					{ title: "gadget repository", url: Container.gadgetData[i].url, options: Container.gadgetData[i].options } );
			return;
		}
		
	}
}

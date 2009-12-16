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

GadgetRepository = function(){
	
	GadgetRepository.superclass.constructor.call(this, "gadgetRepository");
	this.defaultIcon = this.GADGET_BASE + "gadgetRepository/icons/plugin.png";
	this.gadgets = [];
	this.currentGadget = null;
	this.init();
	
}

		
YAHOO.lang.extend( GadgetRepository, AbstractGadget, {		
	
	init : function(){
		
		// call for gadgets that can be added to the dashboard
		// returns their url, the location to an icon (optional) and a title
		gadgets.rpc.call(
			null,
			"dispatcher.getAvailableGadgets",
			function(reply){
				var _gadgets = reply.evalJSON();
				for (var i = 0; i < _gadgets.length; i++){
					// gadget repository has not to be displayed
					if (_gadgets[i].title != "gadgetRepository")
						this.gadgets.push(_gadgets[i]);
				}
				this.displayGadgets();
			}.bind(this),
			""	
		);
	},
	
	displayGadgets : function(){
		
		// selected items are displayed below the carousel 
		// larger image and a short description
		this.spotlight = YAHOO.util.Dom.get("spotlight");
	 
		var nGadgets = this.gadgets.length;

		//carousel to list the gadgets with small icons and their title
		this.carousel = new YAHOO.widget.Carousel("container", {
		  
			// number of items ever added to the carousel
			numItems: nGadgets
		});
	  
		// add gadgets to carousel
		for (var i = 0; i < this.gadgets.length; i++){
		  
			// if icon not specified, default icon wil be chosen
			var imgSrc = this.gadgets[i].icon ? this.gadgets[i].icon : this.defaultIcon;
			var item = "<img width='70' id='"+ this.gadgets[i].url +"' src='" + imgSrc + "'>"
							+ "<h3 class='item'>"+ this.gadgets[i].title + "</h3>";
		
			this.carousel.addItem(item);
				
		}
		
		// set the selection to the first gadget
		this.carousel.set("selectedItem", 0);
		this.currentGadget = this.gadgets[0];
		
		// add event listener
		// when a gadget is selected it will be displayed in the spotlight below
		this.carousel.on("itemSelected", function (index) {
			var selectedItem = this.carousel.getElementForItem(index);
			
			if (selectedItem) {
				
				var gadgetUrl = this.getGadget(selectedItem);
				var imgSrc;
				for (var i = 0; i < this.gadgets.length; i++){
					if (this.gadgets[i].url == gadgetUrl){
						this.currentGadget = this.gadgets[i];
						imgSrc = this.gadgets[i].icon ? this.gadgets[i].icon : this.defaultIcon;
					}
				} 
			    this.spotlight.innerHTML = 
			    	"<a src='#'onclick=\"gadgetRepository.loadGadget();\"> " +
			    	"<img width='200' src='" + imgSrc + "'> </a>"
			    	+ "<p width='200'>"+ this.currentGadget.title + "</p>";
			}
		}.bind(this));
		
		this.carousel.render();
		this.carousel.show();
		
		
	}, 
	
	// retreive url from id of img tag
	getGadget : function(parent) {
		var el = parent.firstChild;
		
		// search for image tab
		while (el) { 
			if (el.nodeName.toUpperCase() == "IMG") {
				return el.id;
			}
			el = el.nextSibling;
		}
		
	},
	
	loadGadget : function(){
		// add gadget to the dashboard
		gadgets.rpc.call(
			null,
			"dispatcher.loadGadget",
			function(reply) {return; },
			this.currentGadget.url
		);
	},
		
});


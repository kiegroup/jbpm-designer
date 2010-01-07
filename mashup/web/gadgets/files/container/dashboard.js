/**
 * Copyright (c) 2009
 * Matthias Kunze
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

// ensure dependencies are valid
if (!YAHOO || !YAHOO.util || !YAHOO.util.YUILoader) {
	throw "Missing Library: YAHOO.util.YUILoader";
}
 
var dashboard = (function(){

	var gadgets = [];   // stores all created gadgets, may contain empty items, use getGadgets()
	var _gadgets = [];  // stores gadgets added before initialization
	
	return {
		init: function() {
		
			YAHOO.util.Get.script("/oryx/lib/prototype-1.5.1.js",
				{onSuccess : function(){ 
					Container.login.init(); 
					$$(".gadget-column").forEach(function(col){
						colWidth = document.body.clientWidth / $$(".gadget-column").length - 10;
						col.setStyle({width: colWidth + "px"});
					});
				} 
			});
		
			// replace the dummy method
			this.addGadget = this._addGadget;
			
			// add the default YUI skin class to the body element
			if (!document.body.className.match(/yui-skin-.*/)) {
				document.body.className = document.body.className + " yui-skin-sam";
			}
			/*
			for (var i in _gadgets) {
				this.addGadget.apply(this, _gadgets[i]);
			}
			*/
		},
		
		// dummy method to be used while dashboard is not initiated
		addGadget: function() {
			_gadgets.push(arguments);
			
			console.warn("added gadget before dashboard initialization", arguments);
			return null;
		},
		
		// method to add a gadget
		// will be overridden after initialization
		_addGadget: function(title, options) {
			
			options = options || {};
		
			// set default dimensions
			options.minWidth 	=  parseInt(options.minWidth) || 200;
			options.minHeight 	= parseInt(options.minHeight) || 150;
			options.top 		= parseInt(options.top) || 50;
			options.left 		= parseInt(options.left) || 50;
			// set default dimensions obeying to the min values
			options.height 		= Math.max(options.minHeight, (parseInt(options.height) || 200));
			options.width  		= Math.max(options.minWidth, (parseInt(options.width) || 300));
		
			var chrome_el = document.createElement("div");
				chrome_el.className = "gadget-chrome";
			
			var title_el = document.createElement("div");
				title_el.className = "hd"
				title_el.innerHTML = "<span>"+title+"</span>";	
				
			var body_el = document.createElement("div");
				body_el.className = "bd";
		
//			var foot_el = document.createElement("div");
//			    foot_el.clasSName = "ft";
		
			document.body.appendChild(chrome_el);
			chrome_el.appendChild(title_el);
			chrome_el.appendChild(body_el);
		
			var panel = new YAHOO.widget.Panel(chrome_el, {
				draggable: true,
				x: options.left,
				y: options.top,
				width: options.width + "px",
				height: options.height + "px",
				underlay: "shadow",
				autofillheight: "body",
				constraintoviewport:true,
				context: ["showbtn", "tl", "bl"]
			});
			
			YAHOO.util.Event.addListener(panel, "hideEvent", function(){alert("h");}, true)
			
			// Create Resize instance, binding it to the 'resizablepanel' DIV 
			var resize = new YAHOO.util.Resize(chrome_el, {
				handles: ["br"],
				autoRatio: false,
				minWidth: options.minWidth,
				minHeight: options.minHeight,
				status: false 
			});

			// Setup startResize handler, to constrain the resize width/height
			// if the constraintoviewport configuration property is enabled.
			resize.on("startResize", function(args) {

				if (this.cfg.getProperty("constraintoviewport")) {
					var D = YAHOO.util.Dom;

					var clientRegion = D.getClientRegion();
					var elRegion = D.getRegion(this.element);

					resize.set("maxWidth", clientRegion.right - elRegion.left - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
					resize.set("maxHeight", clientRegion.bottom - elRegion.top - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
				} else {
					resize.set("maxWidth", null);
					resize.set("maxHeight", null);
				}

			}, panel, true);

			// Setup resize handler to update the Panel's 'height' configuration property 
			// whenever the size of the 'resizablepanel' DIV changes.

			// Setting the height configuration property will result in the 
			// body of the Panel being resized to fill the new height (based on the
			// autofillheight property introduced in 2.6.0) and the iframe shim and 
			// shadow being resized also if required (for IE6 and IE7 quirks mode).
			resize.on("resize", function(args) {
				var panelHeight = args.height;
				this.cfg.setProperty("height", panelHeight + "px");
			}, panel, true);
			
			var gadget = {
				title: title_el,
				body: body_el,
				panel: panel,
				setTitle: function(title) {
					this.title.innerHTML = "<span>"+title+"</span>&nbsp;";
				},
				destroy: function() {
					dashboard.removeGadget(this);
					var panel = this.panel;
					// Calling panel.destroy() within execution context
					// of its hide event fails. Thus we create an 
					// external context with some delay. However, the
					// panel will be hidden immediately.
					window.setTimeout(function() {
						panel.destroy()
					}, 100);
				}
			};
			
			gadget.setTitle(title);
			
			panel.hideEvent.subscribe(function() {
				gadget.destroy()
			}); 
			
			panel.dragEvent.subscribe(function(){ 
				panel.bringToTop();
			});
			
			panel.render();
			panel.bringToTop();
			
			gadgets.push(gadget);
			
			return gadget;
		},
		
		removeGadget: function(gadget) {
			for (var i in gadgets) {
				if (gadgets[i] == gadget) {
					
					if (gadgets[i].__gadget)
						dispatcher.deleteGadget(gadgets[i].__gadget);
					
					window.setTimeout(function() {
						(gadgets[i]);
					}, 100);
					
					return true;
				}
			}
			return false;
		},
		
		getGadgets: function() {
			var copy = [];
			for (var i in gadgets) {
				if (gadgets[i]) {
					copy.push(gadgets[i]);
				}
			}
			return copy;
		}
	}
})();

// load required YUI modules (JS && CSS)
new YAHOO.util.YUILoader({
	require: ["utilities","resize","fonts", "container","menu","reset", "button","connection"], 
	base: "http://yui.yahooapis.com/2.7.0/build/",
	loadOptional: true,
	filter: "RAW",
	onSuccess: function() {
		dashboard.init();
		//Container.loadMenubar();
	},
	onFailure: function() {
		alert("Failed loading required YUI components");
	}
}).insert();
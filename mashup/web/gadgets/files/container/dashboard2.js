/**
 * Copyright (c) 2009
 * Matthias Kunze, Helen Kaltegaertner
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
		
		columnWidth: 	410,
		columns:		[],
		count:			0,
		
		init: function() {
		
			YAHOO.util.Get.script(["/oryx/lib/prototype-1.5.1.js", "ddlist.js"],
				{onSuccess : function(){ 
					Container.login.init(); 
					dashboard.initColumns();
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
		
		initColumns: function(){
			
			new YAHOO.util.DDTarget("dashboard"); 
			
			var nColumns = Math.floor( document.body.clientWidth / dashboard.columnWidth );
			// set correct width so that columns fill browser
			for (var i = 0; i < nColumns; i++){
				dashboard.columns.push( dashboard.columnWidth * i + 4 * (i+1) );
			}

		},
		
		// dummy method to be used while dashboard is not initiated
		addGadget: function() {
			_gadgets.push(arguments);
			
			console.warn("added gadget before dashboard initialization", arguments);
			return null;
		},
		
		// method to add a gadget
		// will be overridden after initialization
		_addGadget: function(title, gadget, options) {
			
			options = options || {};
		
			// set default dimensions
			options.minHeight 	= parseInt(options.minHeight) || 150;

			var gadget_el = document.createElement("div");
			gadget_el.className = "gadget-el";
			gadget_el.id = "gadget" + dashboard.count;
			
			var title_el = document.createElement("div");
			title_el.className = "gadget-title decollapsed";
			gadget_el.appendChild(title_el);
			
			var title_content = document.createElement("p");
			title_content.innerHTML = title;
			title_el.appendChild(title_content);
			
			var delete_button = document.createElement("img");
			delete_button.src = "icons/delete.png";
			title_el.appendChild(delete_button);
			
			var minimize_button = document.createElement("img");
			minimize_button.src = "icons/arrow_inout.png";
			title_el.appendChild(minimize_button);
			
			var chrome_el = document.createElement("div");
			chrome_el.className = "gadget-chrome";
			gadget_el.appendChild(chrome_el);
			
//			var resize = new YAHOO.util.Resize(chrome_el, {
//		        handles: ["b"],
//				proxy: true,
//		        animate: true,
//		        minHeight: options.minHeight,
//		        animateDuration: .75,
//		        animateEasing: YAHOO.util.Easing.backBoth
//		    });
			
			$("dashboard").appendChild(gadget_el);
			dashboard.calculateXY(gadget_el);
			
			new DDList(gadget_el.id);
			
			Event.addListener(delete_button, "click", function(){gadgetInfo.destroy();});
			Event.addListener(minimize_button, "click", function(){
				
				chrome_el.toggle();
				// switch between rounded corners at every corner or just at the top
				if ( ! title_el.className.match(/decollapsed/) ){
					title_el.removeClassName("collapsed")
					title_el.addClassName("decollapsed")
				}
				else{
					title_el.removeClassName("decollapsed")
					title_el.addClassName("collapsed")
				}
			});
		
			var gadgetInfo = {
				count:		dashboard.count++,
				title: 		title,
				gadget: 	gadget,
				chromeEl:	chrome_el,
				setTitle: 	function(title) {
					title_content.innerHTML = title;
				},
				destroy: 	function() {
					dashboard.removeGadget(this);
				}
			};
			
			gadgets.push(gadgetInfo);			
			return gadgetInfo;
		},
		
		// set element to smallest column (considering height)
		calculateXY: function(el){
			var gadgets = $$(".gadget-el");			
			var columnHeights = [];
			
			for (var i = 0; i < dashboard.columns.length; i++) 
				columnHeights[i] = 5 +  Dom.getY("dashboard");
			
			for (var i= 0; i < gadgets.length; i++ ){
				if(gadgets[i] != el){
					var column = Math.round( ( Dom.getX(gadgets[i]) - 4) / dashboard.columnWidth );
					columnHeights[column] = columnHeights[column] + gadgets[i].clientHeight + 5;
				}
			}
			
			var destCol = 0;
			var minHeight = columnHeights[0];
			for (var i = 1; i < columnHeights.length; i++){
				if ( columnHeights[i] < minHeight){
					destCol = i;
					minHeight = columnHeights[i];
				}
			}
			 Dom.setXY(el, [dashboard.columns[destCol], minHeight]);
			
		},
		
		removeGadget: function(gadget) {
			for (var i in gadgets) {
				if (gadgets[i] == gadget) {
					if (gadgets[i].gadget){
						
						var el = $("gadget" + gadget.count);
						var allGadgets = $$(".gadget-el");			
						for (var j = 0; j < allGadgets.length; j++ ){
							if(  Dom.getX( allGadgets[j] ) ==  Dom.getX(el) 
									&&  Dom.getY( allGadgets[j]) >  Dom.getY(el)){
								 Dom.setY( allGadgets[j],  Dom.getY(allGadgets[j]) - el.clientHeight - 5 );
							}
						}
						dispatcher.deleteGadget(gadgets[i].gadget);
						el.remove();
					}
					
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
	require: ["dom", "element", "utilities", "resize", "event", "fonts", "container","reset", "animation", "dragdrop"], 
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
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

Container.loadMenubar = function () {

	// adding a widget, url taken from input dialog
	var handleSubmit = function(){
	
		// TODO validierung
		Container.addGadget(null, null, { url : panel.getData().url, options: {} } );
		panel.cancel();
		return true;
	};
	
	var handleCancel = function(){
		panel.cancel();
		return true;
	};
	
	//create menubar items 	for available gadgets 
	//using information from Container.gedgetData (extracted from configShindig.xml)
	var getGadgetMenu = function(){
    	
    	var gadgetItems = [];
    	
    	for (var i = 0; i < Container.gadgetData.length; i++){
    		
    		var splittedUrl = Container.gadgetData[i].url.split("/");
    		
    		gadgetItems[i] = { 
    				text: splittedUrl[splittedUrl.length - 2], 
    				onclick: { fn: Container.addGadget, obj: Container.gadgetData[i] }
    		}
    	}
    	return gadgetItems;
    };
	
	// panel content
	var content = '<form method="post">'+
						'<br><label for="url" > enter URL: </label>' +
						'<input type="text" id="input-url" name = "url" size = 45 value = "/gadgets/files/gadgets/tools/tool.xml">'+ 
						'</input> <br><br>'+
					'</form>';
	
	// dialog to add an abitrary widget
    var panel = new YAHOO.widget.Dialog("panel", 
		{ width:"400px", visible : false, constraintoviewport : true, x: 30, y: 40, 
		buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true }, 
					{ text:"Cancel", handler:handleCancel } ] 
	} ); 	
	
	panel.setHeader("Add your own Widget!");
	panel.setBody(content);

	panel.render("container");	
    
    var showUpload = function(){ panel.show(); };

    // menu top left
    var aItemData = [

        { 
        	text: "Widgets", 
            submenu: {  
                id: "widgetmenu", 
                itemdata: [
                           getGadgetMenu(),
                           [ {text: "Own Widget", onclick: { fn: showUpload } }]
               ] 
        	}
        }
    ];
    
    var menuBar = new YAHOO.widget.MenuBar("mymenubar", {lazyload: true, itemdata: aItemData});
    
    menuBar.render(document.getElementById("menu"));
 
    var onSubmenuShow = function() {

		var oIFrame,
			oElement,
            nOffsetWidth;

		if ((this.id == "widgetmenu") && YAHOO.env.ua.ie) {

            oElement = this.element;
            nOffsetWidth = oElement.offsetWidth;
            
            oElement.style.width = nOffsetWidth + "px";
            oElement.style.width = (nOffsetWidth - (oElement.offsetWidth - nOffsetWidth)) + "px";
        }
    };
    
    menuBar.subscribe("show", onSubmenuShow);
  

};




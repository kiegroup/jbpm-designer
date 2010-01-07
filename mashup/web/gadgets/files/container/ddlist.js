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


var Dom = YAHOO.util.Dom;
var Event = YAHOO.util.Event;
var DDM = YAHOO.util.DragDropMgr;

DDList = function(id, sGroup, config) {

    DDList.superclass.constructor.call(this, id, sGroup, config);

    var el = this.getDragEl();
    Dom.setStyle(el, "opacity", 0.67); // The proxy is slightly transparent

    this.goingUp = false;
    this.lastY = 0;

};

YAHOO.extend(DDList, YAHOO.util.DDProxy, {

    startDrag: function(x, y) {
		
        var dragEl = this.getDragEl();
        var clickEl = this.getEl();
        this.col = Math.round( (Dom.getX(clickEl) - 4) / dashboard.columnWidth );
        this.oldColumn = this.col;
        this.oldY = Dom.getY(clickEl);
        Dom.setStyle(clickEl, "visibility", "hidden");

        dragEl.innerHTML = clickEl.innerHTML;
        Dom.setStyle(dragEl, "opacity", "0.5");
        
    },
    
    // move gadget animated to its new position 
    endDrag: function(e) {
    	
    	var srcEl = this.getEl();
        var proxy = this.getDragEl();
        var dest;
        
        if (this.destination)
        	dest = this.destination;
        else {
        	// gadget will be moved to the bottom of the column
        	var allGadgets = $$(".gadget-el");
        	var col = Math.floor( (Event.getPageX(e) - 4) / dashboard.columnWidth ); 
        	var x = dashboard.columns[col];
        	var y = Dom.getY("dashboard") + 5;
        	for (var i = 0; i < allGadgets.length; i++){
        		if (Dom.getX(allGadgets[i]) == x && allGadgets[i] != srcEl)
        			y += allGadgets[i].clientHeight + 5;
        	}
        	dest = [x, y];
        }
        
        var proxyid = proxy.id;
        var thisid = this.id;
        this.destination = null;
        this.lastMoved = null;
        
        // Show the proxy element and animate it to the src element's location
        Dom.setStyle(proxy, "visibility", "");
        var a = new YAHOO.util.Motion( 
            proxy, { 
                points: { 
                    to: dest
                }
            }, 
            0.2, 
            YAHOO.util.Easing.easeOut 
        )
        var proxyid = proxy.id;
        var thisid = this.id;

        // Hide proxy and show the source element when finished with the animation
        a.onComplete.subscribe(function() {
        		Dom.setXY(srcEl, dest);
                Dom.setStyle(proxyid, "visibility", "hidden");
                Dom.setStyle(thisid, "visibility", "");
            });
        a.animate();
        
//        if (this.lastMoved)
//        	this._reorderLastMoved();
        
        this._rearrangeLastColumn(e)
        
    },
    
    _rearrangeLastColumn: function(e){
    	
    	var newColumn = Math.floor( ( Event.getPageX(e) - 4) / dashboard.columnWidth );
    	
    	// gadget moved to another column
    	if (this.oldColumn != newColumn){
	    	
    		var allGadgets = $$(".gadget-el");
	    	var gadgetsToMove = [];
	    	var x = dashboard.columns[this.oldColumn];
	    	var y = Dom.getY("dashboard") + 5;
	    	var endOfColumn = y;			// bottom of last gadgets in column
	    	
	    	// abstracting from the 5px buffer (not relevant if there are not at least 50 gadgets in the column)
	    	for (var i = 0; i < allGadgets.length; i++){
	    		if ( allGadgets[i] != this.getEl() && Dom.getX(allGadgets[i]) == x ){
	    			y += allGadgets[i].clientHeight + 5;
	    			if ( (Dom.getY(allGadgets[i]) + allGadgets[i].clientHeight) > endOfColumn )
	    				endOfColumn = Dom.getY(allGadgets[i]) + allGadgets[i].clientHeight;
	    			// in case of free space move only gadgets that where below the dragged one
	    			if ( Dom.getY(allGadgets[i]) > this.oldY )
	    				gadgetsToMove.push(allGadgets[i]);
	    		}
	    	}
	    	
	    	// if there is free space in the column move gadgets up
	    	if ( endOfColumn > y ){
	    		for (var i = 0; i < gadgetsToMove.length; i++){
		    		Dom.setY( gadgetsToMove[i], Dom.getY(gadgetsToMove[i]) - this.getEl().clientHeight - 5 );
		    	}
	    	}	
    	}
    },
    
    /* 
    onDragDrop: function(e, id) {

        // proxy was dropped either on the list
        // or on the current location of the source element
        if (DDM.interactionInfo.drop.length === 1) {

            // The position of the cursor at the time of the drop
            var pt = DDM.interactionInfo.point; 

            // region of the source element at the time of the drop
            var region = DDM.interactionInfo.sourceRegion; 

            // Check to see if we are over the source element's location
            // append to the bottom of the list once we are sure it was a drop below the list
            if (!region.intersect(pt)) {
                var destEl = Dom.get(id);
                var destDD = DDM.getDDById(id);
                destEl.appendChild(this.getEl());
                destDD.isEmpty = false;
                DDM.refreshCache();
            }
        }   
    },
    
    */
	
    onDrag: function(e) {

        // Keep track of the direction of the drag for use during onDragOver
        var y = Event.getPageY(e);
        
//        var currentCol = Math.floor( ( Event.getPageX(e) - 4) / dashboard.columnWidth );
//        if (currentCol != this.col){
//        	this.destination = null;
//        	this.col = currentCol;
//        }
        if (y < this.lastY) {
            this.goingUp = true;
        } else if (y > this.lastY) {
            this.goingUp = false;
        }

        this.lastY = y;
    },

    onDragOver: function(e, id) {
    
        var srcEl = this.getEl();
        var destEl = Dom.get(id);

        // We are only concerned with list items, we ignore the dragover
        // notifications for the empty dashboard
        if (destEl.className == "gadget-el") {
            
        	this._reorderLastMoved();
        	this._adaptCurrentColumnOrder(e, id)
        	
            DDM.refreshCache();
        }
    },
    
    // move elements in the column the cursor entered downwards
    _adaptCurrentColumnOrder: function(e, id){
    	
    	var x = Event.getPageX(e);
    	var y = Event.getPageY(e);
    	this.lastMoved = [];
    	var allGadgets = $$(".gadget-el");
    	
    	// destination on drop
    	// object has to fit into the column
    	this.destination =  Dom.getXY(id);
    	
    	for (var i = 0; i < allGadgets.length; i++){
    		var height = allGadgets[i].clientHeight;
    		var pt = Dom.getXY( allGadgets[i] );
    		// filter gadgets from other columns
    		if (  x >= pt[0] && ( (x - pt[0]) <= dashboard.columnWidth) ){
    			if ( (pt[1] + height) > y ) {
    				Dom.setY( allGadgets[i], pt[1] + this.getEl().clientHeight + 5);
    				this.lastMoved.push( allGadgets[i] );
    			}
    		}

    	}
    },
    
    // move elements that have been moved downwards back to their former position
    _reorderLastMoved: function(){
	    if (this.lastMoved){	
    		for (var i = 0; i < this.lastMoved.length; i++){
	    		Dom.setY( this.lastMoved[i], Dom.getY(this.lastMoved[i]) - this.getEl().clientHeight - 5);
	    	}
	    }
	    this.lastMoved = null;
    }
    
});

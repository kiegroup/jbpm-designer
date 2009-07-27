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

/*
 * GADGET CAN NOT YET BE USED!
 * Just the first lines of code, far away from being completed!
 * 
 */

connector = (function(){
	
	return {
	
		init: function() {
			
		// loads requested YUI modules
			new YAHOO.util.YUILoader({ 
		        base: "http://yui.yahooapis.com/2.7.0/build/", 
		        require: ["datatable","dragdrop", "animation","paginator",
		                  		"fonts","grids","layout","reset","resize", "button"], 
		        loadOptional: false, 
		        filter: "RAW", 
		        onSuccess: function() {
					connector._init();
		        }, 
		        onFailure: function() {
		        	alert("Failed loading required YUI components");
		        }
		    }).insert(); 
		},
	
		// set up layout in side the widget
		_init: function(){
		
			var layout = new YAHOO.widget.Layout({ 
				units: [ 
		            { position: 'top', header: 'connector', height: '80px', resize: false, body: 'top', gutter: '5px', collapse: true}, 
		            { position: 'center', body: 'center', gutter: '5px'} 
		            ] 
		     }); 
			
			layout.render();
			
			var connectButton = new YAHOO.widget.Button({ label:"connect", id:"connectButton", container:"button_group" });
			connectButton.on("click", connector.connect);

			this._initTable();
		},
		
		_initTable: function(){
			
			var sortComments = function(a, b, desc) {
	            // deal with empty values
	            if(!YAHOO.lang.isValue(a)) {
	                return (!YAHOO.lang.isValue(b)) ? 0 : 1;
	            }
	            else if(!YAHOO.lang.isValue(b)) {
	                return -1;
	            }

	            // first compare by comment
	            var comp = YAHOO.util.Sort.compare;
	            var compState = comp(a.getData("comment"), b.getData("comment"), desc);

	            // If titles are equal, then compare by url
	            return (compState !== 0) ? compState : comp(a.getData("modelA"), b.getData("modelB"), desc);
	        };
	        
	        
	        var columnDefs = [
	            {key:"modelA", label: 'modelA', width:100, resizeable:true, sortable:true},
	            {key:"modelB", label: 'modelB', width:100, resizeable:true, sortable:true},
	            {key:"comment", label: "comment", width: 100, resizeable: true, editor:"textbox", sortable: true, 
	                    sortOptions:{sortFunction:sortComments}}
	        ];

	        var data = new YAHOO.util.DataSource([]);
	        
	        data.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	        data.responseSchema = {
	            fields: ["modelA","modelB","comment"]
	        };
			
	        var configs = {
	            sortedBy:{key:"comment",dir:"asc"},
	            paginator: new YAHOO.widget.Paginator({
	                rowsPerPage: 5,
	                template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE,
	                rowsPerPageOptions: [5,10],
	                pageLinks: 3
	            }),
	            draggableColumns:true
	        }

	        connector.table = new YAHOO.widget.DataTable("table", columnDefs, data, configs);
	        
	        //enable row selection
	        connector.table.subscribe("rowClickEvent", connector.table.onEventSelectRow);
			connector.table.subscribe("cellDblclickEvent",connector.table.onEventShowCellEditor);
		},
				
		connect: function(){
			alert("dummy connection");
			
			var shapesA = "shape1";
			var shapesB = "shape2";
			
			connector.table.addRow({modelA: shapesA, modelB: shapesB});
		}
	}
})();

connector.init();


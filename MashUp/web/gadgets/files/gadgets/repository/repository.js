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

var repository = (function(){
	
	return {
		init: function() {
			new YAHOO.util.YUILoader({ 
		        base: "http://yui.yahooapis.com/2.7.0/build/", 
		        require: ["datatable","dragdrop", "animation","paginator",
		                  		"fonts","grids","layout","reset","resize"], 
		        loadOptional: false, 
		        filter: "RAW", 
		        onSuccess: function() {
					repository._init();
		        }, 
		        onFailure: function() {
		        	alert("Failed loading required YUI components");
		        }
		    }).insert(); 
		},
		
		_init: function(){
			
			var layout = new YAHOO.widget.Layout({ 
				units: [ 
		            { position: 'top', header: 'Process Repository', height: 30, resize: false, body: 'top', gutter: '5px', collapse: true}, 
		            { position: 'center', body: 'center', gutter: '5px'} 
		            ] 
		     }); 
			
			layout.render();
			
			this.loadProcesses();
		},
		
		loadProcesses: function(){
			
	        var sortTitles = function(a, b, desc) {
	            // Deal with empty values
	            if(!YAHOO.lang.isValue(a)) {
	                return (!YAHOO.lang.isValue(b)) ? 0 : 1;
	            }
	            else if(!YAHOO.lang.isValue(b)) {
	                return -1;
	            }

	            // First compare by title
	            var comp = YAHOO.util.Sort.compare;
	            var compState = comp(a.getData("title"), b.getData("title"), desc);

	            // If titles are equal, then compare by url
	            return (compState !== 0) ? compState : comp(a.getData("url"), b.getData("url"), desc);
	        };
	        
	        // data to test has to be replaced by user specific data
	        var sampleData = [       
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/1605"}, 
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/2321"},
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/1317"},
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/631"},
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/726"},
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/890"},
				{title: "any title", url: "http://oryx-editor.org/backend/poem/model/1046"},				
			];
	        
	        var formatUrl = function(elCell, oRecord, oColumn, sData) { 
	        	elCell.innerHTML = "<a href='#' onclick=" + 'repository.loadViewer("' + oRecord.getData('url') + '");' + ">" + sData + "</a>";
	        }; 

	        var columnDefs = [
	            {key:"title", label:"Title", width:50, resizeable:true, sortable:true},
	            {key:"url", label:"URL", width:300, resizeable:true, sortable:true, formatter: formatUrl, 
	                    sortOptions:{sortFunction:sortTitles}}
	        ];

	        var data = new YAHOO.util.DataSource(sampleData);
	        data.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	        data.responseSchema = {
	            fields: ["title","url"]
	        };

	        var configs = {
	            sortedBy:{key:"title",dir:"asc"},
	            paginator: new YAHOO.widget.Paginator({
	                rowsPerPage: 5,
	                template: YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE,
	                rowsPerPageOptions: [5,10],
	                pageLinks: 3
	            }),
	            draggableColumns:true
	        }

	        var table = new YAHOO.widget.DataTable("table", columnDefs, data, configs);
	        
	        //enable row selection
	        table.subscribe("rowClickEvent",table.onEventSelectRow);
			
		}, 
		
		// add viewer for selected row (process)		
		loadViewer: function(url){ 
			gadgets.rpc.call(
				null, 
				'dispatcher.displayModel', 
				function(reply){return}, 
				url);
	    }

	}

})();

repository.init();






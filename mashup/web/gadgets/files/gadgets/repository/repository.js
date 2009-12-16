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

Repository = function(){
	
	Repository.superclass.constructor.call(this, "repository");
	
	this.user =				"getopenid.com/helen88";
	this.publicModels =		false;
	this.filteredModels = 	new Array();
	this.currentSort =		(this.user == "public") ? "rating" : "lastChange";
	this.currentSortDir =	null;
	this.busyHandler = 		{ start: new EventHandler(), end: new EventHandler() };
	this.table =			null;
	
	this.init();
	
};

	
YAHOO.lang.extend( Repository, AbstractGadget, {
	
	// override superclass function
	init: function(){
		
		var layout = new YAHOO.widget.Layout({ 
			units: [ 
	            { position: 'top', header: 'Process Repository', height: '70px', resize: false, body: 'top', gutter: '5px', collapse: true}, 
	            { position: 'center', body: 'center', gutter: '5px'} 
	            ] 
	     }); 
		
		var buttonGroup = new YAHOO.widget.ButtonGroup({
			container : "button_group",
			id :		"button_group"
		});
		
		var publicButton = new YAHOO.widget.Button({ label:"public", value:"public", type: "radio"});
		var userButton = new YAHOO.widget.Button({ label:"user", value:"user", type:"radio", checked : true});
		publicButton.on("click", this.filterModels.bind(this));
		userButton.on("click", this.filterModels.bind(this));
		
		buttonGroup.addButton(publicButton);
		buttonGroup.addButton(userButton);
		
		layout.render();
		this.filterModels();
	},
	
	filterModels: function(){
		
		this.initTable();
		
		var params = ( this.user == "public" || this.publicModels ) 
							? new Hash() : new Hash({access:'owner,read,write',
														sort: this.currentSort});
		
		//change status (what models have to be displayed, public or private)
		if (this.publicModels) 
			{ this.publicModels = false; }
		else 
			{ this.publicModels = true; }
		
		this.doRequest(
			"/backend/poem/filter", 
			function(transport) {
				this.filteredModels = eval(transport.responseText);
				if( this.currentSortDir == "asc")
					this.filteredModels.reverse();								
				this.displayModels();
			}.bind(this), 
			params, 
			'get', 
			false 
		)
		
	},
	
	
	doRequest: function( url, successHandler, params,  method, asynchronous ){
		
		if(!url){
			return
		}
		// Set Busy
		this.busyHandler.start.invoke();
		
		// Define successcallback
		var callback = function(){
		
			this.busyHandler.end.invoke();
			
			if( successHandler )
				successHandler.apply( successHandler , arguments)
			
		}.bind(this);
		
		// Check URL
		method = method ? method : "get";
		if( method.toLowerCase() == "get" || method.toLowerCase() == "delete" ){
			url += params ? "?" + $H(params).toQueryString() : "";
			params = null;
		}				

		if( !asynchronous ){
			new Ajax.Request(url, 
				 {
					method			: "get",
					asynchronous 	: false,
					onSuccess		: callback,
					onFailure		: function(){
						alert('Server communication failed!')
					},
					parameters 		: params
				});
		} else {
			// Send request	
			new Ajax.Request({				    
					url		: url,
				    method	: method,
				    params	: params,
				    success	: callback,
					failure	: function(){
						alert('Server communication failed!')
					}
				});			
		}
	},
	
	displayModels: function(){
		
        for (var i = 0; i < this.filteredModels.length; i++){
        
	        var requestUrl = this.REPOSITORY_BASE + this.filteredModels[i] + "/meta";
			new Ajax.Request(requestUrl, 
					 {
						method			: "get",
						asynchronous 	: false,
						onSuccess		: function(response){
							var metaData = response.responseText.evalJSON();
							var row = {	farm : 			metaData.thumbnailUri, 
										title : 		metaData.title, 
										last_modified : metaData.lastUpdate, 
										url : 			this.filteredModels[i], 
										author : 		metaData.author };
							this.table.addRow(row);
						}.bind(this),

						onFailure		: function(){
							alert('Oryx','Server communication failed!');
						}
					});
        }
		
	},		
	
	// add viewer for selected row (process)		
	loadViewer: function(args){ 
		
		gadgets.rpc.call(
			null, 
			'dispatcher.displayModel', 
			function(reply){return}, 
			args );
    },
	
	initTable: function(){
		
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
		
        // cell with link to show viewer
		var formatUrl = function(elCell, oRecord, oColumn, sData) { 
			
			var args = this.SERVER_BASE + this.REPOSITORY_BASE + oRecord.getData('url') 
							+ '.' + oRecord.getData('title').replace(/\s/g, "_");
			
        	elCell.innerHTML = "<a href='#' onclick=" + 'repository.loadViewer("'
        					+ args + '");' + ">" + sData + "</a>";
        }.bind(this); 

        // cell with an icon (arrow) to expand the row
        // thumbnail will be shown
        var expansionFormatter  = function(elCell, oRecord, oColumn, oData) { 
        	var cell_element    = elCell.parentNode; 

        	//Set trigger 
        	if( oData ){		// Row is closed 
        		cell_element.innerHTML = '<img src= "'+ this.GADGET_BASE + 'repository' +'/icons/arrow_right.gif" />';
        		YAHOO.util.Dom.addClass( cell_element, 
                	"yui-dt-expandablerow-trigger" ); 
        	}
        }.bind(this); 
        
        var columnDefs = [
            {key:"farm", label:"", formatter: expansionFormatter}, 
            {key:"title", label:"Title", width:100, resizeable:true, sortable:true},
			{key:"last_modified", label:"last modiefied", width:150, resizeable:true, sortable:true},
            {key:"url", label:"URL", width:70, resizeable:true, sortable:true, formatter: formatUrl, 
                    sortOptions:{sortFunction:sortTitles}}
        ];
        
        // data definition for table
        var data = new YAHOO.util.DataSource([]);
        data.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        data.responseSchema = {
            fields: ["farm" ,"title", "last_modified", "url", "author"]
        };

        
        var configs = {
        	// configure what will be displayed when row is expanded
        	rowExpansionTemplate : '<img src={farm} height=100/><p>{author}</p>',
            sortedBy :			{key:"title",dir:"asc"},
            // 3 rows per page, can be changed to 6 or 9
            paginator: 			new YAHOO.widget.Paginator({
		                			rowsPerPage : 			3,
		                			template : 				YAHOO.widget.Paginator.TEMPLATE_ROWS_PER_PAGE,
		                			rowsPerPageOptions	: 	[3,6,9],
		                			pageLinks : 			3 }),
            draggableColumns :	false
        };

        this.table = new YAHOO.widget.DataTable("table", columnDefs, data, configs );
        
        //enable row selection
        this.table.subscribe( 'rowClickEvent', this.table.onEventSelectRow);
		//enable expansion
        this.table.subscribe( 'cellClickEvent', this.table.onEventToggleRowExpansion ); 
		
	},

});

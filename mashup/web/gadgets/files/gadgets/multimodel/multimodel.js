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

MultiModel = function(){
	
	MultiModel.superclass.constructor.call(this, "multiModel");
	this.table 				= null;
	this.connections 		= [];
	this.selectedViewers 	= [];
	this.connections 		= [];
	this.connectionMode 	= false;
	this.discoveryMode		= false;
	this.connector 			= null;
	this.init();
}


YAHOO.lang.extend( MultiModel, AbstractGadget, {	
	
	init: function() {
	
		var layout = new YAHOO.widget.Layout({ 
			units: [ 
	            { position: 'top', header: 'Multimodel - Menu', height: '70px', resize: false, body: 'top', gutter: '5px', collapse: true}, 
	            { position: 'center', body: 'center', header: 'Connections', gutter: '5px'} 
	            ] 
	     }); 
		
		layout.render();
		
		// enter and leave selection mode
		var connectButton = new YAHOO.widget.Button({
			id :		"connectButton", 
			container :	"button_group", 
			title : 	"create a new association between the currently selected shapes" 
		});
		connectButton.on("click", this.createConnection.bind(this));
		connectButton.setStyle("background", "url('" + this.GADGET_BASE + "multimodel/icons/cart_add.png') no-repeat center");
		
		var discoverButton = new YAHOO.widget.Button({
			id :		"discoverButton", 
			container :	"button_group", 
			title : 	"discover connections associated with a specific shape" 
		});
		discoverButton.on("click", this.enterDiscoveryMode.bind(this));
		discoverButton.setStyle("background", "url('" + this.GADGET_BASE + "multimodel/icons/magnifier.png') no-repeat center");
		
		//reset connections
		var deleteButton = new YAHOO.widget.Button({
			id :		"deleteButton", 
			container :	"button_group",
			title : 	"delete all connections"
		});
		deleteButton.on("click", this.resetConnections.bind(this));
		deleteButton.setStyle("background", "url('" + this.GADGET_BASE + "multimodel/icons/cancel.png') no-repeat center");
		
		//save connections permanently
		var saveButton = new YAHOO.widget.Button({ 
			id :		"saveButton", 
			container :	"button_group" ,
			title : 	"save connections"
		});
		saveButton.on("click", this.saveConnections.bind(this));
		saveButton.setStyle("background", "url('" + this.GADGET_BASE + "multimodel/icons/disk.png') no-repeat center");
		
		//this.initTable();
	},
	
	
	/*
	 * render table in which associations will be displayed
	 * 
	 */
	initTable: function(){
		
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
            {key:"modelA", label: 'modelA', width:90, resizeable:true, sortable:true},
            {key:"modelB", label: 'modelB', width:90, resizeable:true, sortable:true},
            {key:"comment", label: "comment", width: 200, resizeable: true, editor:"textbox", sortable: true, 
                    sortOptions:{sortFunction:sortComments}}
        ];

        var data = new YAHOO.util.DataSource([])
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
            draggableColumns:false
        }

        this.table = new YAHOO.widget.DataTable("table", columnDefs, data, configs);
        
        //enable row selection
        this.table.subscribe("rowClickEvent", this.table.onEventSelectRow);
		this.table.subscribe("cellDblclickEvent",this.table.onEventShowCellEditor);
	},
	
	/*
	 * show dialog to configure new connection
	 * 
	 */
	createConnection: function(){
		
		// leave discovery mode if currently active
		if ( this.discoveryMode ){
			this.discoveryMode = false;
			this.discovery = null;	
		}
		
		if ( this.connectionMode ){
			this.connectionMode = false;
			this.connector.stopSelectionMode();
			this.connector = null;
			
		}
		
		else {
			for (var i = 0; i < this.connections.length; i++){
				if (this.connections[i] && this.connections[i].isActive )
					this.connections[i].deselect();
			}
			this.connectionMode = true;
			this.connector = new Connector(this);
		}

	},

	addConnection : function(connection){
		this.connections.push(connection);
		connection.display();
	},
	
	/*
	 * store connections permanently
	 * 
	 */
	saveConnections: function(){
		
		alert("Not yet implmented!");
		
	},
	
	/*
	 * remove all connections
	 */
	resetConnections: function(){
		
		this.connections = [];
		$("connections").innerHTML = "";
	
		this.resetModels();
	},
	
	/*
	 * reset markers and selections to enter the discovery Mode
	 */
	
	enterDiscoveryMode : function(){
		
		// leave connection mode if currently active
		if ( this.connectionMode ){
			this.connectionMode = false;
			this.connector = null;	
		}
		
		this.resetModels();
		
		if ( this.discoveryMode ){
			this.discoveryMode = false;
			this.discovery.stopDiscoveryMode();
			this.discovery = null;
			
		}
		
		else {
			this.discoveryMode = true;
			this.discovery = new Discovery(this);
		}
		
	},
	
	/*
	 * remove shadows, markers and selections from all viewers
	 */
	resetModels : function(){
		
		var clearViewers = function(viewers){
			
			for (var i = 0; i < viewers.length; i++){
				this.removeMarker(viewers[i], "all");
				this.resetSelection(viewers[i]);
				this.undoGrey(viewers[i], "all");
			}
		};
	
		this.sendViewers(clearViewers, this);
	}
});
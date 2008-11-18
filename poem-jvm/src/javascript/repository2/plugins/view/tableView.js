/**
 * Copyright (c) 2008
 * Sven Wagner-Boysen
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

// define plugin namespace

if(!Repository.Plugins) Repository.Plugins = {};

Repository.Plugins.TableView = {
	construct: function(facade) {
		this.name = Repository.I18N.TableView.name;
		this.icon = '/backend/images/silk/application_view_columns.png';
		this.numOfDisplayedModels = 30;
		
		// define required data uris
		this.dataUris = ["/meta"];
		
		arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
		
		if( this.parentPanel.ownerCt ){
			this.parentPanel.ownerCt.addListener('resize', function(panel, adjWidth, adjHeigth, rawWidth, rawHeight){
												if (this.facade.getCurrentView() == this) {
													this.setWidth(adjWidth)
												}
											}.bind(this));
		}
	},
	
	
	
	render : function(modelData) {
		
		if( this.tablePanel ){
			this.panel.remove( this.tablePanel )
		}
		
		var data = [];
		modelData.each(function( pair ){
			var stencilset = pair.value.type;
			// Try to display stencilset title instead of uri
			this.facade.modelCache.getModelTypes().each(function(type){
				if (stencilset == type.namespace) {
					stencilset = type.title;
					return;
				}
			}.bind(this));
			
			pair.value.lastUpdate = pair.value.lastUpdate.substring(0,19);
			pair.value.creationDate = pair.value.creationDate.substring(0,19);
			
			data.push( [ pair.key, unescape(pair.value.title), stencilset, pair.value.author || 'Unknown', unescape(pair.value.summary).gsub('\n', ''), pair.value.creationDate, pair.value.lastUpdate ] )
		}.bind(this));
		
		/*var reader = new Ext.data.ArrayReader({},[
			{name: 'id'},
			{name: 'title'},
			{name: 'type'},
			{name: 'author'},
			{name: 'summary'},
			{name: 'creationDate', type: 'date', dateFormat: 'Y-M-D H:i:s.u'},
			{name: 'lastUpdate'}
		]);*/
		
		var store = new Ext.data.SimpleStore({
	        fields	: ['id', 'title', 'type', 'author', 'summary', 'creationDate', 'lastUpdate'],
	        //reader 	: reader,
			data	: data
	    });
		
		this.tablePanel = new Ext.grid.GridPanel({
			store		: store,
			border		:false,
			columns		: [ 
				{id: "title", header: Repository.I18N.TableView.columns.title, sortable: false, dataIndex: "title"},
				{id: "type", width: 50, header: Repository.I18N.TableView.columns.type, sortable: false, dataIndex: "type"},
				{id: "author", header: Repository.I18N.TableView.columns.author, sortable: false, dataIndex: "author"},
				{id: "summary", header: Repository.I18N.TableView.columns.summary, sortable: false, dataIndex: "summary"},
				{id: "creationDate", width: 70, header: Repository.I18N.TableView.columns.creationDate, sortable: false, dataIndex: "creationDate"},
				{id: "lastUpdate", width: 70, header: Repository.I18N.TableView.columns.lastUpdate, sortable: false, dataIndex: "lastUpdate"},
				{id: "id", width: 45, header: Repository.I18N.TableView.columns.id, sortable: false, dataIndex: "id"}
			],
			viewConfig: {
				forceFit 	: true
			},
			enableHdMenu 	: false,
			sm				: new Ext.grid.RowSelectionModel({listeners: {selectionchange: this._onSelectionChange.bind(this)}}),
			listeners		: {rowdblclick:this._onDblClick.bind(this)}		
		});
		
		this.panel.add(this.tablePanel);
		this.panel.doLayout(); // Force rendering to show the panel
		
		// Select the selected models
		var selIndicies = this.facade.getSelectedModels().map(function(modelid){ var id = this.facade.getDisplayedModels().indexOf(modelid); return id >= 0 ? id : null }.bind(this)).compact()
		this.tablePanel.getSelectionModel().selectRows( selIndicies )
		
	},
	
	setWidth: function(width){
		if( this.tablePanel ){
			this.tablePanel.setWidth( width );
			this.tablePanel.doLayout();
		}
	},
	
	_onSelectionChange: function(table){
		
		var ids = [];
		
		// Get the selection
		table.getSelections().each(function(entry){
			ids.push( entry.data.id );
		})
		
		// Change the selection
		this.facade.changeSelection( ids );
	},
	
	_onDblClick: function(grid, rowIndex, e){
		
		// Get the uri from the clicked model
		var id = grid.getStore().getAt(rowIndex).data.id;
		
		this.facade.openModelInEditor(id);
	}
};

Repository.Plugins.TableView = Repository.Core.ViewPlugin.extend(Repository.Plugins.TableView);

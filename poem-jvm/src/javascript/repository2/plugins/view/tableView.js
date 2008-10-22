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
			
			data.push( [ pair.key, pair.value.thumbnailUri, pair.value.title, stencilset, pair.value.author || 'Unknown' ] )
		}.bind(this));
		
		var store = new Ext.data.SimpleStore({
	        fields	: ['id', 'icon', 'title', 'type', 'author'],
	        data	: data
	    });
		
		this.tablePanel = new Ext.grid.GridPanel({
			store	: store,
			border	:false,
			columns: [ 
				{id: "id", header: "id", sortable: false, dataIndex: "id"},
				{id: "title", header: Repository.I18N.TableView.columns.title, sortable: false, dataIndex: "title"},
				{id: "type", header: Repository.I18N.TableView.columns.type, sortable: false, dataIndex: "type"},
				{id: "author", header: Repository.I18N.TableView.columns.author, sortable: false, dataIndex: "author"}
			],
			viewConfig: {
				forceFit : true
			},
			sm: new Ext.grid.RowSelectionModel({listeners: {selectionchange: this._onSelectionChange.bind(this)}}),
			listeners:{rowdblclick:this._onDblClick.bind(this)}		
		});
		
		this.panel.add(this.tablePanel);
		this.panel.doLayout(); // Force rendering to show the panel
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

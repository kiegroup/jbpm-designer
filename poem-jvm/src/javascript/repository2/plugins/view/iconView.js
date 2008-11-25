/**
 * Copyright (c) 2008
 * Willi Tscheschner
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

Repository.Plugins.IconView = {
	
	
	construct: function(facade) {
		this.name = Repository.I18N.IconView.name;
		
		this.icon = '/backend/images/silk/application_view_icons.png';
		this.numOfDisplayedModels = 12;
		
		// define required data uris
		this.dataUris = ["/meta"];
		
		arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
		
	},
	
	
	
	render : function(modelData) {
		
		this.isRendering = true;
				
		if( this.myPanel ){
			this.panel.remove( this.myPanel )
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
			
			data.push( [ pair.key, pair.value.thumbnailUri + "?" + Math.random(), unescape(pair.value.title), stencilset, Repository.Helper.CutOpenID(pair.value.author, 45) || 'Unknown' ] )
		}.bind(this));
		
		var store = new Ext.data.SimpleStore({
	        fields	: ['id', 'icon', 'title', 'type', 'author'],
	        data	: data
	    });
	
	    this.myPanel = new Ext.Panel({
			border	:false,
	        items	: new DataGridPanel({store: store, listeners:{click:this._onSelectionChange.bind(this), dblclick:this._onDblClick.bind(this)}})
	    });

		this.panel.add( this.myPanel );
		this.panel.doLayout(); 
		
		
		this.isRendering = false;
	},
	
	_onSelectionChange: function(dataGrid){

		if( this.isRendering || this.isSelecting ){ return }

		var ids = [];
		// Get the selection
		dataGrid.getSelectedRecords().each(function(data){
			ids.push( data.data.id )
		})
		
		// Change the selection
		this.facade.changeSelection( ids );
	},
	
	_onDblClick: function(dataGrid, index, node, e){
		
		// Get the uri from the clicked model
		var id 	= dataGrid.getRecord( node ).data.id
				
		// Select the new range
		dataGrid.selectRange(index, index)
		this.facade.changeSelection( [id] );
		
		// Open the model in Editor
		this.facade.openModelInEditor(id);
		
	},
	
	selectionChanged: function(modelIds) {
		
		if( !this.myPanel || this.isRendering ){ return }
		
		var dg		= this.myPanel.items.get(0);
		var data  	= dg.store.data;
		
		if( dg.events.click.firing ){ return }
		
		this.isSelecting = true;
		
		var selectingIndices = [];
		
		data.each(function(d, index ){
			if( modelIds.include( d.data.id )){ selectingIndices.push( index ) }
		})
		
		dg.select( selectingIndices );
		
		this.isSelecting = false;
	}	
};

Repository.Plugins.IconView = Repository.Core.ViewPlugin.extend(Repository.Plugins.IconView);


DataGridPanel = Ext.extend(Ext.DataView, {
	multiSelect		: true,
	//simpleSelect	: true, 
    cls				: 'repository_iconview',
    itemSelector	: 'dd',
    overClass		: 'over',
	selectedClass	: 'selected',
    tpl : new Ext.XTemplate(
        '<div>',
			'<dl>',
            '<tpl for=".">',
				'<dd>',
				'<div class="image"><img src="{icon}" title="{title}"/></div>',
	            '<div><span class="title" title="{[ values.title.length + (values.type.length*0.8) > 30 ? values.title : "" ]}">{[ values.title.truncate(30 - (values.type.length*0.8)) ]}</span><span class="author" unselectable="on">({type})</span></div>',
	            '<div><span class="type">{author}</span></div>',
				'</dd>',
            '</tpl>',
			'</dl>',
        '</div>'
    )
});


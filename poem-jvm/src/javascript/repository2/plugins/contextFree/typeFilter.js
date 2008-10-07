/**
 * Copyright (c) 2008
 * Bj�rn Wagner
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

// define namespace
if(!Repository) var Repository = {};
if(!Repository.Plugins) Repository.Plugins = {};

/**
 * Supplies filtering by model type (stencil set)
 * Note: Only stencil sets defined in the stencilsets.json can be selected as filter
 */

Repository.Plugins.TypeFilter = {
	
	filter : [],
	
	construct: function(facade) {
		this.name = Repository.I18N.TypeFilter.name;
		arguments.callee.$.construct.apply(this, arguments); // call Plugin super class

		var types =[];
		// Add buttons to the panel
		this.facade.modelCache.getModelTypes().each(function(stencilset) {
			var button = new Ext.LinkButton({ 
					text 		: stencilset.title, 
					toggle 		: true, 
					toggleStyle	: 'font-weight:bold;display:block;',
					namespace 	: stencilset.namespace,
					click		: this._onButtonClick.bind(this),
					style		: 'display:block;'
				});
				
			types.push( [ stencilset.namespace , stencilset.title] )

			//this.panel.add(button);
			
			
		}.bind(this));
		
		
		
		var sm 		= new Ext.grid.CheckboxSelectionModel({listeners :  { selectionchange: this._onButtonClick.bind(this) }});
		var store 	= new Ext.data.SimpleStore({
	        fields	: ['namespace', 'title'],
	        data	: types
	    });
		
	    var grid = new Ext.grid.GridPanel({
	        store	: store,
			width	: 200,
	        cm		: new Ext.grid.ColumnModel([
			            sm,
			            {
							dataIndex	: 'title',
							width		: 178
						}
			        ]),
	        sm		: sm,
			hideHeaders :true,
			border	: false
	    });


		this.panel.add( grid )
		this.panel.doLayout();
	},
	
	_onButtonClick : function( selectModel ) {
				
		var filter = $A(selectModel.selections.items).map(function(item){ return item.data.namespace });
		this.facade.applyFilter('type', filter.join(","));
		
	}
};

Repository.Plugins.TypeFilter = Repository.Core.ContextFreePlugin.extend(Repository.Plugins.TypeFilter);

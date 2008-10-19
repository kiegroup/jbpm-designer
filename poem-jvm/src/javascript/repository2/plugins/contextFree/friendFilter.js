/**
 * Copyright (c) 2008
 * Bjoern Wagner
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

Repository.Plugins.FriendFilter = {
	
	filter : [],
	
	construct: function(facade) {
		this.name = Repository.I18N.FriendFilter.name;
		arguments.callee.$.construct.apply(this, arguments); // call Plugin super class

		var types = this.facade.modelCache.getFriends().map(function(item) { return [ unescape(item) ];}.bind(this));
		
		var sm 		= new Ext.grid.CheckboxSelectionModel({listeners :  { selectionchange: this._onButtonClick.bind(this) }});
		var store 	= new Ext.data.SimpleStore({
	        fields	: ['tag'],
	        data	: types
	    });
		
	    var grid = new Ext.grid.GridPanel({
	        store	: store,
			width	: 200,
	        cm		: new Ext.grid.ColumnModel([
			            sm,
			            {
							dataIndex	: 'tag',
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
				
		var filter = $A(selectModel.selections.items).map(function(item){ return item.data.tag; });
		this.facade.applyFilter('friend', filter.join(","));	
	}
};

Repository.Plugins.FriendFilter = Repository.Core.ContextFreePlugin.extend(Repository.Plugins.FriendFilter);

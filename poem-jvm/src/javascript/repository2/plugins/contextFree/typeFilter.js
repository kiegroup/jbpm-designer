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
		
		// load types
		this.facade.modelCache.getModelTypes().each(function(stencilset) {
			types.push( [ stencilset.namespace , stencilset.title] )
		}.bind(this));
		
		
		// prepare data for dataView
		var store 	= new Ext.data.SimpleStore({
	        fields	: ['namespace', 'title'],
	        data	: types
	    });
		
		var tpl 	= new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="x-grid3-row" UNSELECTABLE = "on" style="clear:left;">',
					'<div class="x-grid3-row-checker" style="width: 18px; float:left;"></div>',
					'<div class="x-grid3-cell-inner x-grid3-col-1">{title}</div>',
				'</div>',
			'</tpl>'
		);
		
	    var grid = new Ext.DataView({
	        store			: store,
			tpl 			: tpl,
	       	autoHight		: true,
			itemSelector	: 'div.x-grid3-row',
    		overClass		: 'x-grid3-row-over',
			selectedClass	: 'x-grid3-row x-grid3-row-selected',
			multiSelect		: true,
			simpleSelect 	: true
	    });

		
		this.panel.add( grid )
		this.panel.doLayout();
		
		grid.addListener('selectionchange', this._onSelectionChange.bind(this) );
	},
	/**
	 * apply typ filter, if selection has changed 
	 * @param {Object} dataView
	 * @param {Object} selection
	 */
	_onSelectionChange : function( dataView, selection ) {
		
		var filter = $A(dataView.getSelectedRecords()).map(function(item){ return item.data.namespace });
		this.facade.applyFilter('type', filter.join(","));
		
	}
};

Repository.Plugins.TypeFilter = Repository.Core.ContextFreePlugin.extend(Repository.Plugins.TypeFilter);

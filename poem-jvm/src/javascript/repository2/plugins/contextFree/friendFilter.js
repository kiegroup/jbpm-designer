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


		if (!this.facade.isPublicUser()) {
			this._generateGUI();
			this.facade.modelCache.getUserUpdateHandler().registerCallback(this._generateGUI.bind(this));
		} else {
			this.panel.hide();
		}

	},
	
	_generateGUI: function(){
		
		var types = $H(this.facade.modelCache.getFriends()).map(function(item) { return Number(item.value) > 0 ? [ unescape(item.key), item.value ] : null }.bind(this)).compact();

		if( this.types && types && types instanceof Array && types.length > 0 && types.toString() == this.types.toString() ){
			if( this.panel && this.panel.body ){
				this.panel.body.hide();
			}
			return 
		}

		this.types = types;
				
		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
		this.deletePanelItems();

		// Hide the body if there are no friends
		if( types.length <= 0 && this.panel ){
				this.panel.collapse();
		}	
						
		
		var store 	= new Ext.data.SimpleStore({
	        fields	: ['friend', 'count'],
	        data	: types
	    });
		
	    var tpl 	= new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="x-grid3-row" UNSELECTABLE = "on" style="clear:left;">',
					'<div class="x-grid3-row-checker" style="width: 18px; float:left;"></div>',
					'<div class="x-grid3-cell-inner x-grid3-col-1">{friend} ({count})</div>',
				'</div>',
			'</tpl>'
		);
		
	    var grid = new Ext.DataView({
	        store	: store,
			tpl 	: tpl,
	       	autoHight: true,
			itemSelector	: 'div.x-grid3-row',
    		overClass		: 'x-grid3-row-over',
			selectedClass	: 'x-grid3-row x-grid3-row-selected',
			multiSelect	: true,
			simpleSelect : true
	    });

		this.panel.add( grid )
		this.panel.getEl().setHeight( )
		this.panel.doLayout();
		
		
		grid.addListener('selectionchange', this._onSelectionChange.bind(this) );
		
	},
	
	_onSelectionChange : function( dataView, selection ) {
				
		var filter = $A(dataView.getSelectedRecords()).map(function(item){ return item.data.friend; });
		this.facade.applyFilter('friend', filter.join(","));	
	}
};

Repository.Plugins.FriendFilter = Repository.Core.ContextFreePlugin.extend(Repository.Plugins.FriendFilter);

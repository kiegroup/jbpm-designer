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

// define namespace
if(!Repository) var Repository = {};
if(!Repository.Plugins) Repository.Plugins = {};

/**
 * Supplies filtering by model type (stencil set)
 * Note: Only stencil sets defined in the stencilsets.json can be selected as filter
 */

Repository.Plugins.RatingFilter = {
	
	filter : [],
	
	construct: function(facade) {
		this.name = Repository.I18N.RatingFilter.name;
		arguments.callee.$.construct.apply(this, arguments); // call Plugin super class
		
		this._generateGUI();
		
	
	},
	
	_generateGUI: function(){

		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
		this.deletePanelItems();
				
		
		var sm 		= new Ext.grid.CheckboxSelectionModel({listeners :  { selectionchange: this._onButtonClick.bind(this) }});
		var store 	= new Ext.data.SimpleStore({
	        fields	: ['rating'],
	        data	: [[]]
	    });
		
		this.editor = new Ext.Rating({	
										value		:0,
										editable	:true,
										changed		:this._setRatingFilter.bind(this)
									})
		
	    this.grid = new Ext.grid.EditorGridPanel({
	        store	: store,
			width	: 200,
			clicksToEdit : 0,
	        cm		: new Ext.grid.ColumnModel([
			            sm,
			            {
							editor		: this.editor,
							dataIndex	: 'rating',
							width		: 178
						}
			        ]),
	        sm		: sm,
			hideHeaders :true,
			border	: false
	    });

		// Add grid to panel
		this.panel.add( this.grid )
		// Do Layouting
		this.panel.getEl().setHeight()
		this.panel.doLayout();
		// Start Editing
		this.grid.startEditing(0, 1)
				
	},
	
	
	
	_setRatingFilter: function( num ){
		
		if( this.currentValue && this.currentValue == num ){
			return;
		}
		
		this.currentValue = num;
		
		this.facade.applyFilter('rating', num - 0.5);
		if( this.grid.getSelectionModel().getCount() <= 0)
			this.grid.getSelectionModel().selectFirstRow();
		
	},
	_onButtonClick : function( selectModel ) {
		
		var selValue = this.editor.value;
						
		if( selectModel.getCount() >= 1 && selValue > 0 ){
			
			if( this.currentValue && this.currentValue == selValue ){
				return;
			}
			this.currentValue = selValue;
			this.facade.applyFilter('rating', selValue - 0.5);	
			
		} else {
			this.currentValue = 0;
			this.editor.setValue(0);
			this.facade.applyFilter('rating', '');	
		}
	}
};

Repository.Plugins.RatingFilter = Repository.Core.ContextFreePlugin.extend(Repository.Plugins.RatingFilter);

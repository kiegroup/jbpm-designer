/***************************************
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
****************************************/

if(!Repository.Core) Repository.Core = {};

Repository.Core.ViewPlugin = {
	
	
		viewRegion : "view",
		
		numOfDisplayedModels : 10,
		
		icon : "/backend/images/silk/lightbulb.png",
		
		construct : function(facade) {
			arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
			
			this.parentPanel = this.panel;
			this.panel = new Ext.Panel({border: false, autoScroll: true, region: 'center'});
			this.parentPanel.add(this.panel);
			
			this.enabled = false;
			this.lastStartIndexOfDisplayedModel = 0; // index of last displayed model in filteredModels array
	
			this.facade.registerOnFilterChanged(this.filterChanged.bind(this));
			this.facade.registerOnViewChanged(this.viewChanged.bind(this));
			this.facade.registerOnSelectionChanged(this.selectionChanged.bind(this));
		},
		
		showNextDisplayedModels : function() {			
			this.showDisplayedModelsStartingFrom( this.lastStartIndexOfDisplayedModel + this.numOfDisplayedModels);
		},

		showPreviousDisplayedModels : function() {			
			this.showDisplayedModelsStartingFrom( this.lastStartIndexOfDisplayedModel - this.numOfDisplayedModels);
		},
		
		showDisplayedModelsStartingFrom: function( index ){

			var filteredModels 	= this.facade.getFilteredModels();
			var startIndex 	= Math.max( Math.min( index , filteredModels.length-1 ) , 0 );
			var endIndex 	= Math.max( Math.min( startIndex+this.numOfDisplayedModels, filteredModels.length ) , 0 );
			
			var newDisplayedModels = [];
			
			for(var i = startIndex; i < endIndex; i++ )
				newDisplayedModels.push(filteredModels[i]);
				
			this.lastStartIndexOfDisplayedModel = startIndex;
			this.facade.setDisplayedModels( newDisplayedModels );	
			
			
			// Reset the selection
			var ds = this.facade.getSelectedModels();
			var dd = this.facade.getDisplayedModels();
			
			var newSelection = ds.findAll(function(old){ return dd.include( old )})
			
			this.facade.changeSelection( newSelection );
				
		},	
			
		enable : function() {

			// make it visible
			this.enabled = true;
			this.panel.setVisible(true);
			//this.panel.doLayout();
			// determine models to display
			//this.showDisplayedModelsStartingFrom( 0 );
		},
		
		disable : function() {
			this.enabled = false;
			this.panel.setVisible(false);
			this.panel.doLayout();
		},
		
		filterChanged : function(modelIds) {
			
			if (this !== this.facade.getCurrentView()) {
				return
			}
				
			this.showDisplayedModelsStartingFrom( 0 );
		},

		viewChanged : function(modelIds) {
			
			if( this == this.facade.getCurrentView() ){
				this.preRender(modelIds);
			}
			
		},		
		
		selectionChanged: function( modelIds ) {
			
		},
		
		updateModels : function(modelIds) {
			modelIds.each(function(modelId) {
				if (this.facade.getDisplayedModels().indexOf(modelId) != -1) {
					this.preRender(this.facade.getDisplayedModels());
					return;
				}
			}.bind(this));
		}
		
		
};


Repository.Core.ViewPlugin = Repository.Core.Plugin.extend(Repository.Core.ViewPlugin);

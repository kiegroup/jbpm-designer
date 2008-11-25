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

Repository.Plugins.ModelRangeSelection = {
	
	viewRegion : "bottom",
	
	lessSpaceIndicator: 500,
	
	construct: function( facade ) {

		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 

		if( this.panel.ownerCt ){
			this.panel.ownerCt.addListener('resize', this._checkForLessSpace.bind(this));
		}
		
	},
	
	render: function( modelData ){

		// Try to removes the old child ...
		if( this.myPanel )
			this.panel.remove( this.myPanel );

		var isLessSpace = this.panel.getInnerWidth() < this.lessSpaceIndicator;

		var buttons 	= [];
		var buttonStyle	= "padding:5px;"
				
		var size		= this.facade.getFilteredModels().length;
		var shownModels	= this.facade.getDisplayedModels().length;
		var index		= this.facade.getCurrentView().lastStartIndexOfDisplayedModel;
		var pageSize	= this.facade.getCurrentView().numOfDisplayedModels;

		var isFirstPage	= index < pageSize;
		var isLastPage	= index >= size - pageSize;
				
		// Previous
		buttons.push( new Ext.LinkButton({text:Repository.I18N.ModelRangeSelection[isLessSpace ? 'previousSmall' : 'previous'], click:this._previous.bind(this), style:"position:absolute;top:1px;left:10px;", disabled:isFirstPage}) );
		
			
		// Generate Page Buttons

		var currentPage	= Math.max( Math.floor( index / pageSize ) , 0);
		var lastPage	= Math.max( Math.floor( (size-1) / pageSize ) , 0);
		var endPage		= Math.max( Math.min( currentPage + 2, lastPage ), 0);
		var startPage	= Math.max( endPage - 4, 0);
		endPage			= Math.max(  Math.min( startPage + 4, lastPage ), 0);
		
		if( startPage != 0 ){
		
			// First
			buttons.push( new Ext.LinkButton({text:"1", click:this._setRange.bind(this, 0), style:buttonStyle, disabled:isFirstPage}) );			
			// plus ...
			buttons.push( {xtype:'label', text:'...', style:buttonStyle} )
		}
		
		for( var i=startPage; i <= endPage; i++ ){
			var style = currentPage == i ? buttonStyle + "font-size:13px;font-weight:bold;color:#000000;": buttonStyle;
			buttons.push( new Ext.LinkButton({text:(i+1)+"", click:this._setRange.bind(this, i*pageSize), style:style, disabled: currentPage == i}) );
		}

		// Checks if the last shown page is really the last page
		if( endPage !== lastPage ){
			// ...
			buttons.push( {xtype:'label', text:'...', style:buttonStyle} )
			// Last
			buttons.push( new Ext.LinkButton({text:lastPage+1, click:this._setRange.bind(this, size - (size % pageSize)), style:buttonStyle, disabled:isLastPage}) );

		}
								
		var labelModelOf = 	size == 0 ?
								Repository.I18N.ModelRangeSelection.modelsOfZero :
								(shownModels == 1 ? 
									new Template(Repository.I18N.ModelRangeSelection.modelsOfOne).evaluate({size: size, from: index+1}) : 					
									new Template(Repository.I18N.ModelRangeSelection.modelsOfMore).evaluate({size: size, from: index+1, to: index+shownModels}) );
		buttons.push( {xtype:'label', text:labelModelOf, style:'margin-left:15px;'} )
		
		// Next
		buttons.push( new Ext.LinkButton({text:Repository.I18N.ModelRangeSelection[isLessSpace ? 'nextSmall' : 'next'], click:this._next.bind(this), style:"position:absolute;top:1px;right:10px;", disabled:isLastPage}) );
		

		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;white-space:nowrap;text-align:center;', 
					border	: false,
					items	: buttons,
					height	: 40
				})
						
		// ... before the new child gets added		
		this.panel.add( this.myPanel );
		// Update layouting
		this.panel.doLayout();

		this.buttons = buttons;
		
	},
	
	_checkForLessSpace: function() {
		
		if( !this.buttons && this.buttons.length <= 0 ){ return }
		
		var isLessSpace = this.panel.getInnerWidth() < this.lessSpaceIndicator;
		
		this.buttons[0].setText( Repository.I18N.ModelRangeSelection[isLessSpace ? 'previousSmall' : 'previous'] )
		this.buttons[this.buttons.length-1].setText( Repository.I18N.ModelRangeSelection[isLessSpace ? 'nextSmall' : 'next'] )
		
	},
	
	_previous: function(){
		this.facade.getCurrentView().showPreviousDisplayedModels();
	},
	
	_next: function(){
		this.facade.getCurrentView().showNextDisplayedModels();
	},
	
	_setRange: function( index ){
		this.facade.getCurrentView().showDisplayedModelsStartingFrom( index );
	}		
};

Repository.Plugins.ModelRangeSelection = Repository.Core.ContextPlugin.extend(Repository.Plugins.ModelRangeSelection);

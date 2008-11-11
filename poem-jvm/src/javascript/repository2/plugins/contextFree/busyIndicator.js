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

Repository.Plugins.BusyIndicator = {
	
	startedCount: 0,

	hidePanel: true,
		
	construct: function( facade ) {
		
		// define Create New Model menu
		this.facade 		= facade;

		this.indicator = new Ext.Toolbar.Button({
										iconCls		: 'some_class_that_does_not_exist_but_fixes-rendering', // do not remove!
										//icon 		: "/backend/images/large-loading.gif",
										region		: 'right',
										disabled	: true,
										disabledClass : 'another_style_class_so_that_the_image_is_shown_proper',
										hidden		: true,
										text		: '<img src="/backend/images/large-loading.gif" width="15" />'
									})


		this.toolbarButtons = [ this.indicator ];
		
		// Register Busy Start/End
		var bh = this.facade.modelCache.getBusyHandler();
		bh.start.registerCallback( this._start.bind(this) )
		bh.end.registerCallback( this._end.bind(this) )
							
		arguments.callee.$.construct.apply(this, arguments); //call Plugin super class	

	},
	
	_start: function(){
		this.indicator.setVisible( true )
		this.startedCount++
	},
	
	_end: function(){
		if( this.startedCount <= 1 )
			this.indicator.setVisible( false )
		
		this.startedCount--
	}	
	
};

Repository.Plugins.BusyIndicator = Repository.Core.ContextPlugin.extend(Repository.Plugins.BusyIndicator);

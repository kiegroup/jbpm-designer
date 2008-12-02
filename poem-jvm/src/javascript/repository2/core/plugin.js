/**
 * Copyright (c) 2008
 * Bj�rn Wagner, Sven Wagner-Boysen
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

/**
 * SuperClass for all Plugins.
 * @param {Object} facade
 */

// define namespace
if(!Repository) var Repository = {};
if(!Repository.Core) Repository.Core = {};

Repository.Core.Plugin = {
	
	panel 		: null,
	
	modelTypes 	: null, 
	
	viewRegion 	: "left",
	
	name		: null,
	
	dataUris	: [],
	
	hidePanel	: false,
	
	/**
	 * 
	 */
	construct: function(facade) {
		arguments.callee.$.construct.apply(this, arguments);
		this.facade = facade;
		
		if(!this.toolbarButtons) this.toolbarButtons = [];
		
		this.panel = this.facade.registerPlugin(this);
		
		if( this.hidePanel && this.panel){
			this.panel.hide();
		}
	},
	preRender: function(modelIds) {
		if( this.dataUris.length <= 0 ){
			var o = $H();
			modelIds.each(function(el){ o.set( el, null)})
			this.render( o )
		} else if( modelIds.length <= 0) {
			this.render( $H() )
		} else {
			this.facade.modelCache.getDataAsync(this.dataUris, modelIds, this.render.bind(this))	
		}
	},
	render: function(modelData) {
		
	},
	
	deletePanelItems: function( panel ){
		
		if( !panel )
			panel = this.panel;
			
		if( panel && panel.items ){
			panel.items.each(function(item){ panel.remove( item ) }.bind(this));
		}
			
	}

};


Repository.Core.Plugin = Clazz.extend(Repository.Core.Plugin);

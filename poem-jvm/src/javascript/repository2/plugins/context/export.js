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

Repository.Plugins.Export = {
	
	CONFIG_URL 	: "/config",
	extentions	: [],
	
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.Export.name;

		// Set the data uris
		this.dataUris = [];
		
		this.extentions = facade.modelCache.getAvailableExports()
		this.extentions.unshift( {name: 'Editor', uri: '/self', iconUrl: '/backend/images/silk/application_edit.png' } )
				
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 
		
		this._generateGUI();
		

	},
	
	render: function( modelData ){
			
		// If modelData hasnt changed, return
		if( !this.exportPanel ){ return }

		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
			
		// Try to removes the old child ...
		this._deleteItems( this.exportPanel );

		// Only one is selected
		var onlyOneIsSelected 	= $H(modelData).keys().length === 1;
		
		// IF so...
		if( onlyOneIsSelected ){
			// .. show the particular links for those
			var uri	= modelData.keys()[0].slice(1);			
			
			this.extentions.each(function( extention ){
				
				var newURI = uri + extention.uri
				var button = new Ext.LinkButton({text:newURI, href:newURI, style:"display:block;", click:this._openWindow.bind(this, newURI)})
				
				var panel = new Ext.Panel({
					layout	: 'absolute',
					height	: 35,
					border	: false,
					items	: [
								// IMAGE
								new Ext.Panel({border:false, html:"<img src='"+extention.iconUrl+"'/>", x:0,y:0}),
								// TITLE
								new Ext.Panel({border:false, items:[{xtype:'label', text:extention.name, style:'font-weight:bold;'}], x:25,y:0}),
								// LINK
								new Ext.Panel({border:false, items:[button], x:25,y:12})								
								]
				})
				
				this.exportPanel.add(panel)
				
			}.bind(this))
				
			
		// IF not, show a alert text	
		} else {
			this.exportPanel.add({text: Repository.I18N.Export.onlyOne, xtype:'label', style:"font-style:italic;color:gray;"})
		}

		
		this.panel.getEl().setHeight()
		this.exportPanel.doLayout();

	},
	
	_deleteItems: function( panel ){
		if( panel.items )
			panel.items.each(function(item){ panel.remove( item ) }.bind(this));
	},
			
	_openWindow: function( uri ){
		
		window.open( uri )
		
	},
	
	
	_generateGUI: function(){
				
		var label			= {text: Repository.I18N.Export.title, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:10px;"};		
		this.exportPanel 	= new Ext.Panel({border:false});

		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [label, this.exportPanel]
				})
				
		// Add the panel		
		this.panel.add( this.myPanel )
		this.panel.doLayout();
						
	}
};

Repository.Plugins.Export = Repository.Core.ContextPlugin.extend(Repository.Plugins.Export);

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

Repository.Plugins.AccessInfo = {
	
	ACCESS_URL : "/access",
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.AccessInfo.name;

		// Set the data uris
		this.dataUris = [this.ACCESS_URL];
				
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 

	},
	
	render: function( modelData ){

		console.log( modelData )
		return
	
		// Try to removes the old child ...
		if( this.myPanel )
			this.panel.remove( this.myPanel );
			
					
		var oneIsSelected 	= $H(modelData).keys().length !== 0;
		var buttons 		= [];
		
		// Add a Headline
		buttons.push( {text: 'Common Tags:', xtype:'label', style:"display:block;font-weight:bold;margin-bottom:5px;"} );
		
		// Find every tag which are available in all selected models
		var modelTags 		= []
		
		$H(modelData).each(function( pair ){ 
						pair.value.userTags.each(function( tag ){
							if( modelData.every(function( spair ){
									return spair.value.userTags.include( tag )
								}) ){
								modelTags.push( tag )
							} 
						})
					})
		
		modelTags = modelTags.uniq();
								
		modelTags.each(function(tag, index){
			
			var label = {text: tag, xtype:'label'};
			var image = new Ext.LinkButton({image:'../images/silk/cross.png', imageStyle:'width:12px; margin:0px 2px -2px 2px;', text:'Delete', click:this._onTagClick.bind(this, tag)})
			
			buttons.push( label );
			if (!this.facade.isPublicUser()) buttons.push( image ); // Don't display remove buttons to the public user
			
			if( index < modelTags.length-1 )
				buttons.push( {html:', ', width:10, xtype:'label'} );
				
		}.bind(this))

		if( buttons.length == 1 ){
			// Add a 'none'
			buttons.push( {text: 'none', xtype:'label', style:"font-style:italic;color:gray;"} );				
		}
	
		
		var buttonsPanel
		if( buttons.length > 0 ){
			// Generate a new panel for the buttons
			buttonsPanel = new Ext.Panel({
						items	: buttons,
						border	: false
					})			
		}

		// Generate a new panel for the add form
		var addPanel = new Ext.Panel({
					style	: 'padding-top:10px;',
					layout	: 'absolute',
					border	: false,
					height	: 40,
					items	: [
								new Ext.form.TextField({
											id		: 'repository_taginfo_textfield',
											x		: 0, 
											y		: 0, 
											width	: 100,
											emptyText : 'New Tag',
											disabled  : !oneIsSelected,  
										}),
								new Ext.Panel({
											x		: 105, 
											y		: 0,
											border	: false,
											items:[ new Ext.Button({
															text 		: 'Add',
															disabled 	: !oneIsSelected, 
															listeners	: {
																click : function(){
																	this._addTag(Ext.getCmp('repository_taginfo_textfield').getValue())
																}.bind(this)
															}
														}) ]})
							]
				});


		this.myPanel = new Ext.Panel({
					id		: 'repository_taginfo_mainpanel',
					style	: 'padding:10px;', 
					border	: false,
					items	: buttonsPanel ? [buttonsPanel, addPanel] : [addPanel]
				})
						
		// ... before the new child gets added		
		this.panel.add( this.myPanel );
		// Update layouting
		this.panel.doLayout();


	},
	
	_onTagClick: function( tag ){
		
		if( !tag || tag.length <= 0 ){ return }
		
		this.facade.getSelectedModels().each(function( id ){
			this.facade.modelCache.deleteData( id, this.TAG_URL, {tag_name:tag} )
		}.bind(this))
		
	},	
	
	_addTag: function( tagname ){
		
		if( !tagname || tagname.length <= 0 ){ return }
		
		this.facade.getSelectedModels().each(function( id ){
			this.facade.modelCache.setData( id, this.TAG_URL, {tag_name:tagname} )
		}.bind(this))
		
	}
};

Repository.Plugins.AccessInfo = Repository.Core.ContextPlugin.extend(Repository.Plugins.AccessInfo);

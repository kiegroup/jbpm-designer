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
		
		this._generateGUI();
		

	},
	
	render: function( modelData ){
	
		if( !this.myContrPanel || !this.myReadrPanel ){ return }
	
		// Try to removes the old child ...
		if( this.myContrPanel.items )
			this.myContrPanel.items.each(function(item){ this.myContrPanel.remove( item ) }.bind(this));
		if( this.myReadrPanel.items )
			this.myReadrPanel.items.each(function(item){ this.myReadrPanel.remove( item ) }.bind(this));
			
					
		var oneIsSelected 	= $H(modelData).keys().length !== 0;
		var isPublicUser	= this.facade.isPublicUser();
		var buttons 		= [];
		
		// Find every openids which are available in all selected models
		var contributers 	= [];
		var readers 		= [];
		var owner			= null;
		var isPublic		= false;
			
		$H(modelData).each(function( pair ){ 
						$H(pair.value).each(function( access ){
							if( modelData.every(function( spair ){
									return $H(spair.value).some(function(saccess){ return access.key == saccess.key && access.value == saccess.value})
								}) ){
								
								if( access.key == "public" ){
									isPublic = true;
								} else if( access.value == "write" ){
									contributers.push( access.key )
								} else if( access.value == "owner" ) {
									owner = access.key
								} else {
									readers.push( access.key )
								}
							} 
						})
					})
				
		// Set as uniq
		contributers 		= contributers.uniq();
		var contributerButtons = [];
		readers 			= readers.uniq();
		var readerButtons 	= [];
		
		// Set Contributer as Links		
		if( contributers.length <= 0 ){
			// If there is no contributer, add a 'none'
			contributerButtons.push( {text: 'none', xtype:'label', style:"font-style:italic;color:gray;"} );				
		}	
		
		// Generate Contributer Buttons			
		contributers.each(function(openid){
			
			var label = {text: openid, xtype:'label'};
			var image = new Ext.LinkButton({image:'../images/silk/cross.png', imageStyle:'width:12px; margin:0px 2px -2px 2px;', text:'Delete', click:this._onAccessClick.bind(this, openid)})

			contributerButtons.push( new Ext.Panel({border:false, items:[label, image], style:""}))			
				
		}.bind(this))

		if( isPublic ){
			readerButtons.push( new Ext.Panel({border:false, html:"<span>Is Public</span>", style:""}) );	
		}

		if( readers.length <= 0 && readerButtons.length <= 0 ){
			// If there is no readers, add a 'none'
			readerButtons.push( {text: 'none', xtype:'label', style:"font-style:italic;color:gray;"} );				
		}			
		// Generate Reader Buttons										
		readers.each(function(openid){
			
			var label = {text: openid, xtype:'label'};
			var image = new Ext.LinkButton({image:'../images/silk/cross.png', imageStyle:'width:12px; margin:0px 2px -2px 2px;', text:'Delete', click:this._onAccessClick.bind(this, openid)})
			
			readerButtons.push( new Ext.Panel({border:false, items:[label, image], style:""}))
				
		}.bind(this))
		

		// Enable/Disable the controls
		if( this.controls ){
			this.controls.each(function(co){
				co.setDisabled( isPublicUser || !oneIsSelected )
			}.bind(this))
			// Reset textfield
			this.controls[0].setValue("")
		}
		
		
		// Add the new buttons to the panels
		this.myContrPanel.add(new Ext.Panel({items: contributerButtons,border: false})) 
		this.myContrPanel.doLayout();

		this.myReadrPanel.add(new Ext.Panel({items: readerButtons,border: false})) 			
		this.myReadrPanel.doLayout();


	},
	
	_generateGUI: function(){
		
		var contr	= {text: 'Contributers:', xtype:'label', style:"display:block;font-weight:bold;margin-bottom:3px;"};
		var readr	= {text: 'Readers:', xtype:'label', style:"display:block;font-weight:bold;margin-bottom:3px;margin-top:8px;"};

		this.controls	= [		new Ext.form.TextField({
											id		: 'repository_accessinfo_textfield',
											x		: 0, 
											y		: 0, 
											width	: 160,
											emptyText : 'OpenID',
											disabled  : true,  
										}),
								 new Ext.Button({
											text 		: 'Add as Contributer',
											disabled 	: true, 
											listeners	: {
												click : function(){
													this._addOpenID(Ext.getCmp('repository_accessinfo_textfield').getValue(), "write")
												}.bind(this)
											}
										}),
								 new Ext.Button({
											text 		: 'Add as Reader',
											disabled 	: true, 
											listeners	: {
												click : function(){
													this._addOpenID(Ext.getCmp('repository_accessinfo_textfield').getValue(), "read")
												}.bind(this)
											}
										})			
							]
							
		// Generate a new panel for the add form
		var addPanel = new Ext.Panel({
					style	: 'padding-top:10px;',
					layout	: 'absolute',
					border	: false,
					height	: 90,
					items	: [
								this.controls[0],
								new Ext.Panel({
											x		: 0, 
											y		: 24,
											border	: false,
											items	: [this.controls[1]]
										}),
								new Ext.Panel({
											x		: 00, 
											y		: 48,
											border	: false,
											items	: [this.controls[2]]
										})		
								]
				});


		this.myContrPanel 	= new Ext.Panel({border:false});
		this.myReadrPanel	= new Ext.Panel({border:false});

		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: [contr, this.myContrPanel, readr, this.myReadrPanel, addPanel]
				})
				
		// Add the panel		
		this.panel.add( this.myPanel )
		this.panel.doLayout();
						
	},
	
	_onAccessClick: function( openID ){
		
		if( !openID ){ return }
		
		this.facade.getSelectedModels().each(function( id ){
			this.facade.modelCache.deleteData( id, this.ACCESS_URL, {subject:openID} )
		}.bind(this))
		
	},	
	
	_addOpenID: function( openid, access ){
		
		this.facade.getSelectedModels().each(function( id ){
			this.facade.modelCache.setData( id, this.ACCESS_URL, { subject:openid, predicate:access } )
		}.bind(this))
		
	}
};

Repository.Plugins.AccessInfo = Repository.Core.ContextPlugin.extend(Repository.Plugins.AccessInfo);

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

Repository.Plugins.TagInfo = {
	
	TAG_URL : "/tags",
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.TagInfo.name;

		this.dataUris = [this.TAG_URL];
										
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 
		
		
		if (!this.facade.isPublicUser()) {
			this._createDataStore();
			this._generateGUI();
		} else {
			this.panel.hide();
		}


	},
	
	render: function( modelData ){
		
		// If modelData hasnt changed, return
		if( this.facade.isPublicUser() || !this.tagPanel ){ return }
						
		// Find every tag which are available in all selected models
		var modelTags 		= []
		$H(modelData).each(function( pair ){ 
						pair.value.userTags.each(function( tag ){
							if( modelData.every(function( spair ){
									return spair.value.userTags.include( tag )
								}) ){
								modelTags.push( unescape( tag ) )
							} 
						})
					})
		
		modelTags = modelTags.uniq();


		var oneIsSelected 	= $H(modelData).keys().length !== 0;
		var isPublic		= this.facade.isPublicUser();
		
		// Hide/Show the controls
		if( this.controls ){
			this.controls.each(function(co){
				co.setDisabled( isPublic || !oneIsSelected )
			}.bind(this))
			
			this.controls[0].setValue("")
		}
		
		
		// Set the tags		
		this._setModelTag( modelTags );
	},
	
	_setModelTag: function( modelTags ){

		if( this._lastModelTags && this._lastModelTags.toString() == modelTags.toString() ){
			return
		} else {
			this._lastModelTags = modelTags;
		}

		var isPublic		= this.facade.isPublicUser();
		var buttons 		= [];
		
		// Hold the height of the panel
		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
		this.tagPanel.getEl().setHeight( this.tagPanel.getEl().getHeight() )
		
		// Try to removes the old child ...
		if( this.tagPanelContent )
			this.tagPanel.remove( this.tagPanelContent );
			
		// For each modeltag create a label						
		modelTags.each(function(tag, index){
			
			var label = {text: tag, xtype:'label'};
			var image = new Ext.LinkButton({image:'../images/silk/cross.png', imageStyle:'width:12px; margin:0px 2px -2px 2px;', text:Repository.I18N.TagInfo.deleteText, click:this._onTagClick.bind(this, tag)})
			
			buttons.push( label );
			if (!isPublic) buttons.push( image ); // Don't display remove buttons to the public user
			
			if( index < modelTags.length-1 )
				buttons.push( {html:', ', width:10, xtype:'label'} );
				
		}.bind(this))

		if( buttons.length == 0 ){
			// Add a 'none'
			buttons.push( {text: Repository.I18N.TagInfo.none, xtype:'label', style:"font-style:italic;color:gray;"} );				
		}
	
		
		this.tagPanelContent = new Ext.Panel({
									items	: buttons,
									border	: false
								})			


		// Add the new content
		this.tagPanel.add( this.tagPanelContent );
		
		// Reset the height
		this.tagPanel.getEl().setHeight( )
		this.panel.getEl().setHeight()
		
		// Force for layouting
		this.tagPanel.doLayout();
				
	},
	
	_createDataStore : function() {
		var types = this.facade.modelCache.getUserTags().map(function(item) { return [ unescape(item) ];}.bind(this));

		
		this.dataStore =  new Ext.data.SimpleStore({
	        fields	: ['tag'],
	        data	: types ? types : []
	    });
	},
	
	_generateGUI: function(){

		var label 		= {text: Repository.I18N.TagInfo.shared, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:5px;"};
		this.tagPanel	= new Ext.Panel({border:false})
		this.controls	= [
								new Ext.form.ComboBoxMulti({
											id			: 'repository_taginfo_textfield',
											x			: 0, 
											y			: 0, 
											width		: 150,
											emptyText 	: Repository.I18N.TagInfo.newTag ,
											disabled  	: true,
											mode 		: 'local',
											store 		: this.dataStore,
											displayField : 'tag',
											editable	: true,
											sep			: ","
										}),
								new Ext.LinkButton({
											image		:'../images/silk/add.png',
											text 		: Repository.I18N.TagInfo.addTag,
											click 		: function(){
																this._addTag(Ext.getCmp('repository_taginfo_textfield').getValue())
															}.bind(this)
											
										})
							]
							
		// Generate a new panel for the add form
		var addPanel = new Ext.Panel({
					style	: 'padding-top:10px;',
					layout	: 'absolute',
					border	: false,
					height	: 40,
					items	: [
								this.controls[0],
								new Ext.Panel({
											x		: 155, 
											y		: 3,
											border	: false,
											items	: [this.controls[1]]
										})
								]
				});

		var isPublicUser	= this.facade.isPublicUser();
		
		var panels	= [label, this.tagPanel]
		if( !isPublicUser ){
			panels.push( addPanel )
		}
		
		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: panels
				})
						
		// ... before the new child gets added		
		this.panel.add( this.myPanel );
		// Update layouting
		this.panel.doLayout();
				
	},
	
	_onTagClick: function( tag ){
		
		if( !tag || tag.length <= 0 ){ return }
		
		this.facade.modelCache.deleteData( this.facade.getSelectedModels(), this.TAG_URL, {tag_name:tag}, null, true )

	},	
	
	_addTag: function( tagname ){
		
		if( !tagname || tagname.length <= 0 ){ return }
		
		
		tagname = tagname.split(",").map(function(text){ return text.blank() ? null :  text.strip() }).compact().join(",")
		
		this.facade.modelCache.setData( this.facade.getSelectedModels(), this.TAG_URL, {tag_name:tagname}, null, true )
		
		// Add the new tags to the data store
		//	Create a new record-class
		var Tag = Ext.data.Record.create([{name: 'tag'}])
		//	Add every tag to the store
		tagname.split(",").each(function( tagText ){
			this.dataStore.add( new Tag({tag: unescape(tagText) }) );
		}.bind(this))
	
	}
};

Repository.Plugins.TagInfo = Repository.Core.ContextPlugin.extend(Repository.Plugins.TagInfo);

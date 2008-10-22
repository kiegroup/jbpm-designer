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

Repository.Plugins.Edit = {
	
	// This plugin is only enabled 
	// when user is logged on
	enabled:false,
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.Edit.name;

		// Set the data uris
		this.dataUris = ["/meta"];
				
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 
		
		if( !this.facade.isPublicUser() ){
			this.enabled = true;
			this._generateGUI();
			this.panel.collapse();
		} else {
			this.panel.hide();
		}

	},
	
	render: function( modelData ){
		
		if( !this.enabled ){ return }
		
		
		// Set absolute Height
		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
		
		var oneIsSelected 	= $H(modelData).keys().length == 1;
		var data			= oneIsSelected ? modelData.get( modelData.keys()[0] ) : {title:"",summary:""};
		
		// Set Textfields
		this._enableTextFields( oneIsSelected, data.title, data.summary );
		this._enableDelete( $H(modelData).keys().length,  modelData)
		
		// Reset Height;
		this.panel.getEl().setHeight();
		this.panel.doLayout();
		
	},
	
	_enableTextFields: function( enable, name, summary ){
		
		// Set initial values
		this.controls[0].setValue( enable ? name : '' )
		this.controls[1].setValue( enable ? summary : '' )
		
		// Enable/Disable Textfield
		this.controls[0].setDisabled( !enable )
		this.controls[1].setDisabled( !enable )
		
		// Enable/Disable Save-Button
		this.controls[2].setDisabled( !enable )
	},
	
	_enableDelete: function( size, modelData ){
		
		this.controls[3].setDisabled( size <= 0 )
		
		var innerText = '<b>' + Repository.I18N.Edit.deleteText + ':</b> ';
		if( size == 1 ){
			innerText += new Template(Repository.I18N.Edit.deleteOneText).evaluate({title: modelData.get( modelData.keys()[0] ).title });
		} else if( size > 1 ){
			innerText += new Template(Repository.I18N.Edit.deleteMoreText).evaluate({size: size });			
		} else {
			innerText = '<span style="font-style:italic;color:gray;">' + Repository.I18N.Edit.deleteText + '</span>'
		}
		
		if( this.controls[4].getEl() )
		this.controls[4].getEl().dom.innerHTML = innerText
	},
	
	_generateGUI: function(){
				
		this.controls	= [		
								new Ext.form.TextField({
											id			: 'repository_edit_textfield_name',
											x			: 0, 
											y			: 0, 
											width		: 105,
											fieldLabel	: Repository.I18N.Edit.nameText,
											labelStyle	: "font-size:11px;font-weight:bold;width:60px;",
											emptyText 	: Repository.I18N.Edit.editName ,
											disabled  	: true,  
										}),
								new Ext.form.TextField({
											id			: 'repository_edit_textfield_summary',
											x			: 0, 
											y			: 0, 
											width		: 105,
											fieldLabel	: Repository.I18N.Edit.summaryText,
											labelStyle	: "font-size:11px;font-weight:bold;width:60px;",
											emptyText 	: Repository.I18N.Edit.editSummary,
											disabled  	: true,  
										}),		
								new Ext.LinkButton({
											image		:'../images/silk/disk.png',
											text 		: Repository.I18N.Edit.editText,
											click		: function(){
															this._storeChanges(Ext.getCmp('repository_edit_textfield_name').getValue(), Ext.getCmp('repository_edit_textfield_summary').getValue())
														}.bind(this)
										}),											
								new Ext.LinkButton({
											image		:'../images/silk/cross.png',
											text 		: Repository.I18N.Edit.deleteText,
											click 		: this._deleteModels.bind(this)
											
										}),
								new Ext.form.Label({
											style		:'display:block;width:100%;text-align:left;',
											text		:''
										})
							]
							
		// Generate a new panel for the add form
		var panels = new Ext.Panel({
					style	: 'padding-top:10px;',
					border	: false,
					items	: [
								new Ext.form.FormPanel({
											border		: false,
											labelWidth 	: 60,
											items		: [this.controls[0],this.controls[1]]
										}),
								new Ext.Panel({
											width	: 170,
											style	: 'text-align:right;',
											border	: false,
											items	: [this.controls[2]]
										}),
								new Ext.Panel({
											style	: 'text-align:right;margin-top:20px;margin-right:5px;padding:5px 5px 5px 0px;border-top:1px solid silver;',
											border	: false,
											items	: [this.controls[4],this.controls[3]]
										})
								]
				});


		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: panels
				})
				
		// Add the panel		
		this.panel.add( this.myPanel )
		this.panel.doLayout();
						
	},
	
	_deleteModels: function() {
		
		this.facade.modelCache.deleteData( this.facade.getSelectedModels(), "/self", null, function(){ this.facade.applyFilter() }.bind(this) );
		 
	},
	
	_storeChanges: function(name, summary){

		this.facade.modelCache.setData( this.facade.getSelectedModels(), "/meta", { title:name, summary:summary },  function(){ this.facade.applyFilter() }.bind(this) )
		
	}
};

Repository.Plugins.Edit = Repository.Core.ContextPlugin.extend(Repository.Plugins.Edit);

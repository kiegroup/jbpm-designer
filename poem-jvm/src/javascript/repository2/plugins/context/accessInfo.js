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
	
	ACCESS_URL 	: "/access",
	PUBLIC_USER : "public",
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.AccessInfo.name;

		// Set the data uris
		this.dataUris = [this.ACCESS_URL];
				
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 
		
		this._generateData();
		this._generateGUI();
		

	},
	
	render: function( modelData ){
		
		// If modelData hasnt changed, return
		if( !this.myContrPanel || !this.myReadrPanel || !this.myPublicPanel ){ return }
		
		this.panel.getEl().setHeight( this.panel.getEl().getHeight() )
		

		var oneIsSelected 	= $H(modelData).keys().length !== 0;
		var isPublicUser	= this.facade.isPublicUser();
		
		// Find every openids which are available in all selected models
		var contributers 	= [];
		var readers 		= [];
		var owner			= "";
		var isPublic		= false;
			
		$H(modelData).each(function( pair ){ 
						$H(pair.value).each(function( access ){
							if( modelData.every(function( spair ){
									return $H(spair.value).some(function(saccess){ return access.key == saccess.key && access.value == saccess.value})
								}) ){
								
								var value = decodeURI( access.key );
								if( access.key == "public" ){
									isPublic = true;
								} else if( access.value == "write" ){
									contributers.push( value )
								} else if( access.value == "owner" ) {
									owner = value
								} else if( access.value == "read" ){
									readers.push( value )
								}
							} 
						})
					})
				
		
		this._setPublic( isPublic, oneIsSelected )
		this._setOwner( owner )
		this._setContributer( contributers.uniq() )
		this._setReader( readers.uniq() )		
	

		// Enable/Disable the controls
		if( this.controls ){
			this.controls.each(function(co){
				co.setDisabled( isPublicUser || !oneIsSelected )
			}.bind(this))
			// Reset textfield
			this.controls[0].setValue("")
		}

		// Reset Height;
		this.panel.getEl().setHeight()	
	},
	
	_setPublic: function( isPublic, oneIsSelected ){

		// Check is the values has been changed
		if( this._lastPublicAnSelected && this._lastPublicAnSelected == isPublic + "" + oneIsSelected ){
			return
		} else {
			this._lastPublicAnSelected = isPublic + "" + oneIsSelected;
		}
		
		// Remove children
		this._deleteItems( this.myPublicPanel );

		// Generate the public panel
		if( oneIsSelected ){
			var button, label;
			if( isPublic ){
				button 	= new Ext.LinkButton({image:'../images/silk/user_delete.png', imageStyle:'width:12px; margin:0px 2px -2px 10px;', text:Repository.I18N.AccessInfo.unPublish , click:this._deleteOpenID.bind(this, this.PUBLIC_USER)})
				label	= {text: Repository.I18N.AccessInfo.publicText, xtype:'label', style:"font-weight:bold;"};
			} else {
				button 	= new Ext.LinkButton({image:'../images/silk/user_add.png', imageStyle:'width:12px; margin:0px 2px -2px 10px;', text:Repository.I18N.AccessInfo.publish , click:this._addOpenID.bind(this, this.PUBLIC_USER, 'read')})
				label	= {text: Repository.I18N.AccessInfo.notPublicText , xtype:'label', style:"font-style:italic;color:gray;"};			
			}
			// Add the content to the panel		
			this._addItems( this.myPublicPanel, this.facade.isPublicUser() ? [label] : [label, button] )			
		} else {
			this._addItems( this.myPublicPanel, [ {text: Repository.I18N.AccessInfo.noneIsSelected, xtype:'label', style:"font-style:italic;color:gray;"}] )	
		}	
	},

	_setOwner: function( owner ){

		// Check is the values has been changed
		if( this._lastOwner && this._lastOwner.toString() == owner.toString() ){
			return
		} else {
			this._lastOwner = owner;
		}
		
				
		// Remove children
		this._deleteItems( this.myOwnerPanel );	
		
		// Set the owner
		if( owner ){
			this._addItems( this.myOwnerPanel, [ {text: owner, xtype:'label'}] )	
		} else {
			this._addItems( this.myOwnerPanel, [ {text: Repository.I18N.AccessInfo.none, xtype:'label', style:"font-style:italic;color:gray;"}] )	
		}
							
	},

	_setContributer: function( contributers ){
		
		// Check is the values has been changed
		if( this._lastContributers && this._lastContributers.toString() == contributers.toString() ){
			return
		} else {
			this._lastContributers = contributers;
		}
		
		// Remove children
		this._deleteItems( this.myContrPanel );

		var contributerButtons 	= this._generateButtons( contributers, !this.facade.isPublicUser() );
		this._addItems( this.myContrPanel,  contributerButtons )
						
	},

	_setReader: function( readers ){

		// Check is the values has been changed
		if( this._lastReaders && this._lastReaders.toString() == readers.toString() ){
			return
		} else {
			this._lastReaders = readers;
		}
				
		// Remove children
		this._deleteItems( this.myReadrPanel );		
		
		var readerButtons 		= this._generateButtons( readers, !this.facade.isPublicUser() );
		this._addItems( this.myReadrPanel,  readerButtons )
	},
				
	
	_addItems: function( panel, items ){
		panel.add(new Ext.Panel({items:items, border: false})) 		
		panel.getEl().setHeight()
		panel.doLayout();
	},
	
	_deleteItems: function( panel ){
		if( panel && panel.items ){
			panel.getEl().setHeight( panel.getEl().getHeight() )
			panel.items.each(function(item){ panel.remove( item ) }.bind(this));
		}
			
	},
	
	_generateButtons:function( data, editable ){
		
		var buttons = [];
		
		// Set Contributer as Links		
		if( data.length <= 0 ){
			// If there is no contributer, add a 'none'
			buttons.push( {text: Repository.I18N.AccessInfo.none, xtype:'label', style:"font-style:italic;color:gray;"} );				
		}	
		
		// Generate Contributer Buttons			
		data.each(function(openid){
			
			var label = {text: openid, xtype:'label'};
			var image = new Ext.LinkButton({image:'../images/silk/cross.png', imageStyle:'width:12px; margin:0px 2px -2px 10px;', text:Repository.I18N.AccessInfo.deleteText, click:this._deleteOpenID.bind(this, openid)})

			buttons.push( new Ext.Panel({border:false, items: editable ? [label, image] : [label] , style:""}))			
				
		}.bind(this))
		
		return buttons;
				
	},
	
	_generateData: function(){

		var types = this.facade.modelCache.getFriends().map(function(item) { return [ unescape(item) ]; }.bind(this));

		this.dataStore =  new Ext.data.SimpleStore({
	        fields	: ['friend'],
	        data	: types ? types : []
	    });
				
	},
	
	_generateGUI: function(){
				
		var owner	= {text: Repository.I18N.AccessInfo.owner, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:3px;"};
		var contr	= {text: Repository.I18N.AccessInfo.contributer, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:3px;margin-top:8px;"};
		var readr	= {text: Repository.I18N.AccessInfo.reader, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:3px;margin-top:8px;"};

		this.controls	= [		new Ext.form.ComboBoxMulti({
											id			: 'repository_accessinfo_textfield',
											x			: 0, 
											y			: 0, 
											width		: 160,
											height		: 39,
											grow		: true,
											growMax		: 120,
											growMin		: 45,
											emptyText 	: Repository.I18N.AccessInfo.openid,
											disabled  	: true,  
											mode 		: 'local',
											store 		: this.dataStore,
											displayField : 'friend',
											editable	: true,
											sep			: "\n",
											renderAsTextArea : true
										}),
								new Ext.LinkButton({
											image		: '../images/silk/page_white_magnify.png', 
											imageStyle	: 'margin:2px;',
											text		: Repository.I18N.AccessInfo.addReader, 
											click		:  function(){
																this._addOpenID(Ext.getCmp('repository_accessinfo_textfield').getValue(), "read")
															}.bind(this)
										}),
								new Ext.LinkButton({
											image		: '../images/silk/page_white_edit.png', 
											imageStyle	: 'margin:2px;',
											text		: Repository.I18N.AccessInfo.addContributer, 
											click		: function(){
																this._addOpenID(Ext.getCmp('repository_accessinfo_textfield').getValue(), "write")
															}.bind(this)
										})				
							]
							
		// Generate a new panel for the add form
		var addPanel = new Ext.Panel({
					style	: 'padding-top:10px;',
					border	: false,
					items	: [
								this.controls[0],
								new Ext.Panel({
											width	: 160,
											style	: 'text-align:right;',
											border	: false,
											items	: [this.controls[1], this.controls[2]]
										})
								]
				});

		var isPublicUser	= this.facade.isPublicUser();
		
		this.myPublicPanel 	= new Ext.Panel({border:false, height:20});
		this.myContrPanel 	= new Ext.Panel({border:false});
		this.myOwnerPanel 	= new Ext.Panel({border:false});
		this.myReadrPanel	= new Ext.Panel({border:false});

		var panels	= [this.myPublicPanel, owner, this.myOwnerPanel, contr, this.myContrPanel, readr, this.myReadrPanel]
		if( !isPublicUser ){
			panels.push( addPanel )
		}

		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: panels
				})
				
		// Add the panel		
		this.panel.add( this.myPanel )
		this.panel.doLayout();
						
	},
	
	_deleteOpenID: function( openID ){
		
		if( !openID ){ return }
		
		this.facade.modelCache.deleteData( this.facade.getSelectedModels(), this.ACCESS_URL, {subject:encodeURI(openID)}, null, true );
		
	},	
	
	_addOpenID: function( openid, access ){
		
		// Replaces ';' and '\n' to ',' ;and stripes and decodes 
		var decoded = openid.gsub(';', ',').gsub('\n', ',').split(',').map(function(s){ s = s.strip(); return s.blank() ? null : encodeURI(s)}).uniq().compact().join(',')
		
		if( decoded.length <= 0 ){ return }
		
		this.facade.modelCache.setData( this.facade.getSelectedModels(), this.ACCESS_URL, { subject:decoded, predicate:access }, null, true )

		// Add the new friends to the data store
		//	Create a new record-class
		var Tag = Ext.data.Record.create([{name: 'friend'}])
		//	Add every friend to the store
		decoded.split(",").each(function( friend ){
			this.dataStore.add( new Tag({friend: unescape(friend) }) );
		}.bind(this))
				
	}
};

Repository.Plugins.AccessInfo = Repository.Core.ContextPlugin.extend(Repository.Plugins.AccessInfo);

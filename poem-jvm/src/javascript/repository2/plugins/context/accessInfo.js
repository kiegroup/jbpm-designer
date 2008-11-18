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
		
		this.currentOpenIDs = {owner:null, reader:[], writer:[]};
		
		this._generateData();
		this._generateGUI();
		
		// To cut the open ids, register on resize
		if( this.panel && this.panel.ownerCt)
			this.panel.ownerCt.addListener('resize', function(obj, width){ this.cropOpenIDs()}.bind(this))

	},
	
	cropOpenIDs: function(){
		
		var width 	= this.panel.getInnerWidth()/6.4;
		width		-= 5;
		width		= Math.max( width, 6 )
		var all = this.currentOpenIDs.writer.concat(this.currentOpenIDs.reader)
		all.push(this.currentOpenIDs.owner)
		all = all.compact()
		
		all.each(function(label){
			label.getEl().dom.innerHTML = Repository.Helper.CutOpenID( label.originText, width || 50 );
		})
		
		
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
		
		var currentUser		= this.facade.getCurrentUser();
		var hasWriteAccess	= true;
		
		// FOR EACH Model			
		$H(modelData).each(function( pair ){ 	
		
						var hasWriteAccessForThatModel = false;
		
						// FOR EACH Access-Rights
						$H(pair.value).each(function( access ){
							
							var value = decodeURI( access.key );
							
							hasWriteAccessForThatModel = hasWriteAccessForThatModel || ( value == currentUser && (access.value == "write" || access.value == "owner" ))
							
							if( modelData.every(function( spair ){
									return $H(spair.value).some(function(saccess){ return access.key == saccess.key && access.value == saccess.value})
								}) ){
								
								if( access.key == "public" ){
									isPublic = true;
								} else if( access.value == "write" ){
									contributers.push( value )
								} else if( access.value == "read" ){
									readers.push( value )
								} else if( access.value == "owner" ) {
									owner = value
								}
							} 
							
						})
						
						hasWriteAccess = hasWriteAccess && hasWriteAccessForThatModel;
					})
				
		
		// Get the write access
		owner == this.facade.getCurrentUser() || contributers.include( this.facade.getCurrentUser() )
		
		this._setPublic( isPublic, oneIsSelected, !this.facade.isPublicUser() && hasWriteAccess )
		this._setOwner( owner, $H(modelData).keys().length > 1 )
		this._setContributer( contributers.uniq(), !this.facade.isPublicUser() &&  hasWriteAccess)
		this._setReader( readers.uniq(), !this.facade.isPublicUser() &&  hasWriteAccess )		
	

		// Enable/Disable the controls
		if( this.controls ){
			this.controls.each(function(co){
				co.setDisabled( isPublicUser || !oneIsSelected || !hasWriteAccess )
			}.bind(this))
			// Reset textfield
			this.controls[0].setValue( oneIsSelected && !hasWriteAccess ? Repository.I18N.AccessInfo.noWritePermission : '')
		}

		// Reset Height;
		this.panel.getEl().setHeight()	
		
		this.cropOpenIDs();
	},
	
	_setPublic: function( isPublic, oneIsSelected, writeAccess ){

		// Check is the values has been changed
		if( this._lastPublicAndSelected && this._lastPublicAndSelected == isPublic + "" + oneIsSelected + "" + writeAccess ){
			return
		} else {
			this._lastPublicAndSelected = isPublic + "" + oneIsSelected + "" + writeAccess;
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
			this._addItems( this.myPublicPanel, !writeAccess ? [label] : [label, button] )			
		} else {
			this._addItems( this.myPublicPanel, [ {text: Repository.I18N.AccessInfo.noneIsSelected, xtype:'label', style:"font-style:italic;color:gray;"}] )	
		}	
	},

	_setOwner: function( owner, moreThanOneIsSelected ){

		// Check is the values has been changed
		if( this._lastOwner && this._lastOwner.toString() == owner.toString() ){
			return
		} else {
			this._lastOwner = owner;
		}
		
				
		// Remove children
		this._deleteItems( this.myOwnerPanel );	
		this.currentOpenIDs.owner = null;
		
		// Set the owner
		if( owner ){
			var label = new Ext.form.Label( {text: owner, originText:owner, style:'white-space:nowrap;'});
			this._addItems( this.myOwnerPanel, [ label ] )	
			this.currentOpenIDs.owner = label;
		} else if( moreThanOneIsSelected ) {
			this._addItems( this.myOwnerPanel, [ {text: Repository.I18N.AccessInfo.several, xtype:'label', style:"font-style:italic;color:gray;"}] )	
		} else {
			this._addItems( this.myOwnerPanel, [ {text: Repository.I18N.AccessInfo.none, xtype:'label', style:"font-style:italic;color:gray;"}] )	
		}
		
							
	},

	_setContributer: function( contributers, writeAccess ){
		
		// Check is the values has been changed
		if( this._lastContributers && this._lastContributers.toString() == contributers.toString() && this._lastContributersWrite == writeAccess ){
			return
		} else {
			this._lastContributers 		= contributers;
			this._lastContributersWrite = writeAccess;
		}
		
		// Remove children
		this._deleteItems( this.myContrPanel );

		var contributerButtons 	= this._generateButtons( contributers, writeAccess );
		this._addItems( this.myContrPanel,  contributerButtons );
		
		// Set labels to eventiually cut those
		this.currentOpenIDs.writer = contributerButtons.map(function(el){return el instanceof Ext.Panel ? el.items.last() : null }).compact();
						
	},

	_setReader: function( readers, writeAccess ){

		// Check is the values has been changed
		if( this._lastReaders && this._lastReaders.toString() == readers.toString() && this._lastReadersWrite == writeAccess ){
			return
		} else {
			this._lastReaders 		= readers;
			this._lastReadersWrite 	= writeAccess;
		}
				
		// Remove children
		this._deleteItems( this.myReadrPanel );		
		
		var readerButtons = this._generateButtons( readers, writeAccess );
		this._addItems( this.myReadrPanel,  readerButtons );
		
		// Set labels to eventiually cut those
		this.currentOpenIDs.reader = readerButtons.map(function(el){return el instanceof Ext.Panel ? el.items.last() : null }).compact();
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
			
			var label = new Ext.form.Label({text: openid, originText: openid, xtype:'label', style:'white-space:nowrap;'});
			var image = new Ext.LinkButton({image:'../images/silk/cross.png', imageStyle:'width:12px; margin:0px 2px -2px 10px;position:absolute;right:0px;top:1px;', text:Repository.I18N.AccessInfo.deleteText, click:this._deleteOpenID.bind(this, openid)})

			buttons.push( new Ext.Panel({border:false, items: editable ? [image, label] : [label] , style:""}))			
				
		}.bind(this))
		
		return buttons;
				
	},
	
	_generateData: function(){

		var types = $H(this.facade.modelCache.getFriends()).keys().map(function(item) { return [ unescape(item) ]; }.bind(this));

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
											anyMatch	: true,
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
		
		this.myPublicPanel 	= new Ext.Panel({border:false, height:20, style:'white-space:nowrap;'});
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
		
		this.facade.modelCache.deleteData( this.facade.getSelectedModels(), this.ACCESS_URL, {subject:openID}, null, true );
		
	},	
	
	_addOpenID: function( openid, access ){
		
		// Replaces ';' and '\n' to ',' ;and stripes and decodes 
		var decoded = openid.gsub(';', ',').gsub('\n', ',').split(',').map(function(s){ s = s.strip(); return s.blank() ? null : s}).uniq().compact().join(',')
		
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

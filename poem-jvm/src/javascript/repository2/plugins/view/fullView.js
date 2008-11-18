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

// define plugin namespace

if(!Repository.Plugins) Repository.Plugins = {};

Repository.Plugins.FullView = {
	

	construct: function(facade) {
		this.name = Repository.I18N.FullView.name;
		
		this.icon = '/backend/images/silk/application_view_gallery.png';
		this.numOfDisplayedModels = 1;
		
		// define required data uris
		this.dataUris = ["/meta"];
		
		arguments.callee.$.construct.apply(this, arguments); // call superclass constructor

		if( this.parentPanel.ownerCt ){
			this.parentPanel.ownerCt.addListener('resize', function(){ if(this.facade.getCurrentView() == this){ this.setFrameHeight()} }.bind(this));
		}
				
	},
	
	
	render : function(modelData) {
		
		if( this.myPanel ){
			this.panel.remove( this.myPanel )
		}
		
		// If there are no models, return
		if( $H(modelData).keys().length <= 0 ){
			return
		}
		
		var rawData			= modelData.get( modelData.keys()[0] );
		
		var data			= {};
		
		var ssName			= this.facade.modelCache.getModelTypes().find(function(type){ return rawData.type == type.namespace }.bind(this));
		data.type 			= ssName ? ssName.title : rawData.type;
		
		data.title			= unescape(rawData.title)
		data.summary		= unescape(rawData.summary).gsub('\n', '<br/>');
		data.creationDate	= rawData.creationDate;
		data.lastUpdate		= rawData.lastUpdate;
		data.author			= rawData.author;
		data.pngUri			= rawData.pngUri;
		
		data.editUri			= Repository.Config.BACKEND_PATH + modelData.keys()[0] + "/self";
		
		// Define the labels
		data.createdLabel		= Repository.I18N.FullView.createdLabel;
		data.fromLabel			= Repository.I18N.FullView.fromLabel;
		data.descriptionLabel	= Repository.I18N.FullView.descriptionLabel;
		data.changeLabel		= Repository.I18N.FullView.changeLabel;
		data.editLabel			= Repository.I18N.FullView.editorLabel;
	
		var newHTML = new Ext.Template(
							    '<div>',
							   		'<div style="margin-bottom:10px;"><span style="font-weight:bold;font-size:15px;margin-right:5px;">{title}</span> ({type}) <a href="{editUri}" target="_blank" style="text-decoration:none;margin-left:20px;"><img src="/backend/images/silk/application_edit.png" style="margin-right:5px;margin-bottom:-4px;"/>{editLabel}</a></div>',
									'<div><span style="width:120px;font-weight:bold;display:inline-table;">{createdLabel}: </span>{creationDate} <span style="width:120px;font-weight:bold;margin-left:50px;display:inline-table;">{changeLabel}: </span>{lastUpdate}</div>',
							   		'<div><span style="width:120px;font-weight:bold;display:inline-table;">{fromLabel}: </span>{author}</div>',
									'<div><span style="width:120px;font-weight:bold;display:block;float:left">{descriptionLabel}: </span><div style="margin-left:120px;">{summary}</div></div>',
									'<iframe id="repository_fullview_pngframe" src="{pngUri}" title="{title}" style="width:99%;border:none;margin-top:20px;"/>',
							    '</div>'
							);
	
	    this.myPanel = new Ext.Panel({
			style		: 'padding:10px',
	        html		: newHTML.apply( data ),
			border		: false
		});
		
		this.panel.add( this.myPanel );
		this.panel.doLayout(); 
		
		this.setFrameHeight();
		
		this.facade.changeSelection( [modelData.keys()[0]] );

	},
	
	setFrameHeight: function(){
		
		// Set the height for the canvas
		var el = $('repository_fullview_pngframe');
		el.height = this.panel.getEl().parent().parent().parent().parent().getComputedHeight() - el.offsetTop - 20;
				
	}	
};

Repository.Plugins.FullView = Repository.Core.ViewPlugin.extend(Repository.Plugins.FullView);




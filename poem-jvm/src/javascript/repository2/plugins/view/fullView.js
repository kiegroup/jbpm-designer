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
		
	},
	
	
	render : function(modelData) {
		
		if( this.myPanel ){
			this.panel.remove( this.myPanel )
		}
		
		var data			= modelData.get( modelData.keys()[0] );
		data['typeShort'] 	= this.facade.modelCache.getModelTypes().find(function(type){ return data.type == type.namespace }.bind(this)).title;
		
		data['imgHeight']	= this.panel.getEl().parent().parent().parent().parent().getComputedHeight() - 100;
		
		var newHTML = new Ext.Template(
							    '<div>',
							   		'<div><span style="font-weight:bold;font-size:12px">{title}</span> ({typeShort})</div>',
									'<div><span style="width:100px;font-weight:bold;display:inline-table;">Created: </span>{creationDate} <span style="width:100px;font-weight:bold;margin-left:50px;display:inline-table;">Last Change: </span>{lastUpdate}</div>',
							   		'<div><span style="width:100px;font-weight:bold;display:inline-table;">From: </span>{author}</div>',
									'<div><span style="width:100px;font-weight:bold;display:inline-table;">Description: </span>{summary}</div>',
									'<iframe src="{pngUri}" title="{title}" style="width:99%;border:none;margin-top:20px;" height="{imgHeight}"/>',
							    '</div>'
							);
	
	    this.myPanel = new Ext.Panel({
			style		: 'padding:10px',
	        html		: newHTML.apply( data ),
			border		: false
		});
		
		this.panel.add( this.myPanel );
		this.panel.doLayout(); 
		
		
		this.facade.changeSelection( [modelData.keys()[0]] );

	}	
};

Repository.Plugins.FullView = Repository.Core.ViewPlugin.extend(Repository.Plugins.FullView);




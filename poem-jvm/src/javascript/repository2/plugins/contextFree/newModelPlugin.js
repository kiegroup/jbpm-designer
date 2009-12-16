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


// define namespace
if(!Repository) var Repository = {};
if(!Repository.Plugins) Repository.Plugins = {};

/**
 * renders the dropdown menu for modeltypes and calls the oryx editor with the choosen type
 */

Repository.Plugins.NewModel = {
	
	hidePanel: true,
	
	construct: function(facade) {

		//this.name = Repository.I18N.NewModel.name;

		// define Create New Model menu
		this.toolbarButtons = new Array();
		this.facade = facade;
		
		this.facade.modelCache.getModelTypes().each(function(type) {
			
			if( !type.visible ){
				return
			}
			
			this.toolbarButtons.push({
				text 		: type.title,
				menu 		: Repository.I18N.NewModel.name,
				menuIcon 	: "/backend/images/silk/shape_square_add.png",
				tooltipText : Repository.I18N.NewModel.tooltipText,
				icon 		: type.iconUrl,
				handler		: function(){
					this.facade.createNewModel(type.url)
				}.bind(this)				
			});
			
			if(type.title=="BPMN 2.0 Conversations"){
				this.toolbarButtons.push({
					text 		: "BPMN 2.0 Choreographies",
					menu 		: Repository.I18N.NewModel.name,
					menuIcon 	: "/backend/images/silk/shape_square_add.png",
					tooltipText : Repository.I18N.NewModel.tooltipText,
					icon 		: "/oryx/stencilsets/bpmn2.0/bpmn2.0.png",
					handler		: function(){
						this.facade.createNewModel(null,"bpmn2.0Choreography")
					}.bind(this)				
				});
				
			}
		}.bind(this));
		

		arguments.callee.$.construct.apply(this, arguments); //call Plugin super class
	}
};

Repository.Plugins.NewModel = Repository.Core.Plugin.extend(Repository.Plugins.NewModel);

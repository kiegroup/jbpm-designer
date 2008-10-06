/**
 * Copyright (c) 2008
 * Sven Wagner-Boysen
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

Repository.Plugins.TableView = {
	construct: function(fascade) {
		this.name = Repository.I18N.TableView.name;
		this.icon = '/backend/images/silk/table.png';
		this.numOfDisplayedModels = 30;
		
		// define required data uris
		this.dataUris = ["/meta"];
		
		arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
	},
	
	
	
	render : function(modelData) {
		
		if (this.panel.items) {
			this.panel.items.clear; // remove all items
		}
		if (this.panel.findById('debug_view_container')) {
			this.panel.remove('debug_view_container');
		}
		
		
		
		
		
		// this.viewPanel.getEl().update('');
		var container = new Ext.Panel({id : 'table_view_container'});
		console.log(modelData.values);
		modelData.each(function (pair){
			var damnButton = new Ext.Button({text : 'Select model!'});
			
			var dataPanel = new Ext.Panel({
				html: '<h1>Title: ' + pair.value.title + '</h1><img src="'+ pair.value.thumbnailUri +'" height="50" /> <br />', 
				modelId : pair.key, 
				facade : this.facade, // quick and dirty
				isSelected : this.facade.getSelectedModels().indexOf(pair.key) != -1 });
			
			damnButton.addListener('click', function() {
				if (this.isSelected) {					
					this.removeClass('test_selected_item');
					this.addClass('test_unselected_item');
					this.facade.changeSelection(this.facade.getSelectedModels().without(this.modelId));
				} else {
					this.removeClass('test_unselected_item');
					this.addClass('test_selected_item');	
					this.facade.getSelectedModels().push(this.modelId)
					this.facade.changeSelection(this.facade.getSelectedModels());
				}
				this.isSelected = !this.isSelected;
				this.doLayout();
			}.bind(dataPanel));
			
			dataPanel.add(damnButton);
			container.add(dataPanel); // v<br />'});
		}.bind(this));
		this.panel.add(container);
		this.panel.doLayout(); // Force rendering to show the panel
	},
};

Repository.Plugins.TableView = Repository.Core.ViewPlugin.extend(Repository.Plugins.TableView);

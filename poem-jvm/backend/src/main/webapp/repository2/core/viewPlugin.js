/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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
****************************************/

if(!Repository.Core) Repository.Core = {};

Repository.Core.ViewPlugin = {
		construct : function(facade) {
			arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
			this.enabled = false;
			this.lastDisplayedModel = -1; // index of last displayed model in filteredModels array
			this.numOfDisplayedModels = 10; //number of models to display in view
			if(!this.icon) this.icon = "/backend/images/silk/lightbulb.png";
	
			this.facade.registerOnFilterChanged(this.filterChanged.bind(this));
		},
		
		getNextDisplayedModels : function() {
			var newLast = this.lastDisplayedModel + this.numOfDisplayedModels;
			var filteredModels = this.facade.getFilteredModels();
			newLast = (newLast <= filteredModels.length) ? newLast : filteredModels.length;
			
			var newDisplayedModels = [];
			
			for(var i = this.lastDisplayed+1; i < newLast; i++ )
				newDisplayedModels.push(filteredModels[i]);
				
			this.lastDisplayed = newLast;
			this.facade.setDisplayedModels(newDisplayedModels);
		},
		
		enable : function() {
			// determine models to display
			this.lastDisplayed = -1;
			this.getNextDisplayedModels();
			// make it visible
			this.enabled = true;
			this.panel.setVisible(true);
		},
		
		disable : function() {
			this.enabled = false;
			this.panel.setVisible(false);
		},
		
		filterChanged : function(modelIds) {
			this.preRender(this.facade.getDisplayedModels());
		},
		
		updateModels : function(modelIds) {
			modelIds.each(function(modelId) {
				if (this.facade.getDisplayedModels().indexOf(modelId) != -1) {
					this.preRender(this.facade.getDisplayedModels());
					return;
				}
			}.bind(this));
		}
		
		
};


Repository.Core.ViewPlugin = Repository.Core.Plugin.extend(Repository.Core.ViewPlugin);

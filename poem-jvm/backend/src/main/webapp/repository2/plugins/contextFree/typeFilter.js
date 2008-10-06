/**
 * Copyright (c) 2008
 * Bjšrn Wagner
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

Repository.Plugins.TypeFilter = {
	construct: function(facade) {
		this.name = Repository.I18N.TypeFilter.name;
		arguments.callee.$.construct.apply(this, arguments); // call Plugin super class

		this.buttons = new Array();
		// Add buttons to the panel
		this.getModelTypes().each(function(stencilset) {
			var button = new Ext.Button({ 
				text : stencilset.title, 
				selected : false, 
				namespace : stencilset.namespace
				});
			button.addListener('click', this._onButtonClick.bind(this));
			this.panel.add(button);
			this.buttons.push(button);
		}.bind(this));
		this.panel.doLayout();
	},
	
	_onButtonClick : function(button, EventArgs) {
		if (button.selected) {
			button.addClass('test_unselected_text');
		} else {
			button.addClass('test_selected_text');
		}
		
		button.selected = !button.selected;
		var params = '';
		this.buttons.each(function(button) {
			if (button.selected) {
				params += button.namespace.escapeHTML() + ',';
			}
		}.bind(this));
		params[params.length - 1] = ' '; // remove last comma
		this.facade.applyFilter('type', params);
	}
};

Repository.Plugins.TypeFilter = Repository.Core.ContextFreePlugin.extend(Repository.Plugins.TypeFilter);

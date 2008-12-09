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

Repository.Plugins.LanguageSupport = {
	
	IMAGE_URLS: {
		de: '/backend/images/flags/de.png',
		en_us: '/backend/images/flags/us.png',
		en: '/backend/images/flags/us.png',
		ru: '/backend/images/flags/ru.png',
		es: '/backend/images/flags/es.png'
	},

	hidePanel: true,
	
	construct: function( facade ) {
		
		// define Create New Model menu
		this.toolbarButtons = [];
		this.facade 		= facade;
		
		var currentLanguage	= this.facade.modelCache.getLanguage();
		currentLanguage		= currentLanguage.languagecode + ( currentLanguage.countrycode ? "_" + currentLanguage.countrycode : "");
		
		
		this.facade.modelCache.getAvailableLanguages().each(function(type) {
			
			var language = type.languagecode + ( type.countrycode ? "_" + type.countrycode : "");
			
			this.toolbarButtons.push({
				text 		: Repository.I18N[language],
				region		: 'right',
				menu 		: Repository.I18N[currentLanguage],
				menuIcon 	: this.IMAGE_URLS[currentLanguage],
				tooltipText : Repository.I18N[currentLanguage],
				icon 		: this.IMAGE_URLS[language],
				handler		: this._setLanguage.bind(this, type)				
			});
		}.bind(this));
					
		arguments.callee.$.construct.apply(this, arguments); //call Plugin super class

	},
	
	_setLanguage: function( type ){
		this.facade.modelCache.setLanguage( type.languagecode, type.countrycode )
	}
};

Repository.Plugins.LanguageSupport = Repository.Core.ContextPlugin.extend(Repository.Plugins.LanguageSupport);

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

Repository.Plugins.Rating = {
	
	RATING_URL : "/rating",
	
	construct: function( facade ) {
		// Set the name
		this.name = Repository.I18N.Rating.name;

		this.dataUris = [this.RATING_URL];
										
		// call Plugin super class
		arguments.callee.$.construct.apply(this, arguments); 
		
		this._generateGUI();

	},
	
	render: function( modelData ){
		
		if( !this.controls ){
			return
		}
		
		var totalScore 	= 0;
		var userScore	= 0; 
		var totalVotes	= 0; 
		var modelCount	= $H(modelData).keys().length; 
		var voteCount	= 0;
		
		$H(modelData).values().each(function( score ){
			totalScore 	+= score.totalScore;
			totalVotes 	+= score.totalVotes;
			userScore 	+= score.userScore;
			
			if( score.totalScore > 0 )
				voteCount++
			
		});
		
		// Get the avarage
		totalScore 	/= voteCount || 1;
		totalVotes 	/= voteCount || 1;
		userScore 	/= voteCount || 1;
		
		// Define the text beneath the stars, depeding on the model vote counts
		var totalText;
		switch( modelCount * voteCount ){
			case 0:		totalText = Repository.I18N.Rating.totalNoneText;
						break;
			case 1: 	totalText = new Template(Repository.I18N.Rating.totalOneText).evaluate({totalScore: totalScore, totalVotes: totalVotes });
						break;
			default:	totalText = new Template(Repository.I18N.Rating.totalMoreText).evaluate({totalScore: totalScore, totalVotes: totalVotes, modelCount: modelCount, voteCount: voteCount });
						break;
		}

		this.controls[0].setDisabled( modelCount <= 0 )		
		this.controls[0].setValue( totalScore )
		this.controls[0].setText( modelCount <= 0 ? '' : totalText )
		
		if( this.controls[1].rendered ){
			this.controls[1].setDisabled( modelCount <= 0 )	
			this.controls[1].setValue( modelCount > 1 ? 0 : userScore )		
		}
		
	},
	
	
	_generateGUI: function(){

		var totalLabel 		= {text: Repository.I18N.Rating.total, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:5px;"};
		var myLabel 		= {text: Repository.I18N.Rating.my, xtype:'label', style:"display:block;font-weight:bold;margin-bottom:5px;margin-top:10px;"};
		
		this.controls	= [
								new Ext.Rating({
											value		: 0,
											editable	: false
										}),
								new Ext.Rating({
											value		: 0,
											editable	: true,
											changed 	: this._setUserRating.bind(this)
										})
							]

		var items = [];
		
		if( this.facade.isPublicUser() ){
			items = [totalLabel, this.controls[0]]
		} else {
			items = [totalLabel, this.controls[0], myLabel, this.controls[1]]
		}
		

		this.myPanel = new Ext.Panel({
					style	: 'padding:10px;', 
					border	: false,
					items	: items
				})
						
		// ... before the new child gets added		
		this.panel.add( this.myPanel );
		// Update layouting
		this.panel.doLayout();
				
	},
	
	_setUserRating: function( value ){
		
		if( !value ){ return }
		
		this.facade.modelCache.setData( this.facade.getSelectedModels(), this.RATING_URL, {userScore:value}, null, true )
		
	
	}
};

Repository.Plugins.Rating = Repository.Core.ContextPlugin.extend(Repository.Plugins.Rating);


Ext.namespace('Repository.Plugins');

Repository.Plugins.ModelTagInfo = function(facade) {
	this.name = 'Model Tags';
	this.facade = facade;
	this.panel = this.facade.registerPluginOnPanel(this.name, 'right');

	// Textbox
	this.field = new Ext.form.Field({id:'model_tags_add_tag'});
	this.panel.add(this.field);
	// Create button to add tags
	this.addTagsButton = new Ext.Button({ text : 'Add Tags' });
	this.addTagsButton.addListener('click', function() {
		// Check if their is some content in the text field
		if (this.field.getValue().length > 0) {
			// Iterate over all selected models
			this.facade.getSelectedModels().each(function(modelId){
				var uri = this.facade.modelCache.getModelUri(modelId).substring(1); // Remove leading slash
				// Make server request, comma separation is handled by the server
				Ext.Ajax.request({
					url : uri + '/tags', 
					method : 'post', 
					params : {tag_name : this.field.getValue()}, 
					success : function(response) {
						var data = new Hash(Ext.util.JSON.decode(response.responseText));
						data.each(function (pair) {
							this.facade.modelCache.updateObject('/tags', pair.key, pair.value);
						}.bind(this)) // End of succes function
					}.bind(this)}) // End of Ajax request
			}.bind(this)); // End of selected models iteration 
		} 
	}.bind(this)); // End of click handling function
	
	this.panel.add(this.addTagsButton);
	this.panel.doLayout(); // Force new rendering of the Panel
	// this.selectionChanged will handle selection changes
	this.facade.registerOnSelectionChanged(this.selectionChanged.bind(this));
	// Data Updates are handled by this.modelUpdate
	this.facade.modelCache.getUpdateHandler().registerCallback(this.modelUpdate.bind(this));
}

Repository.Plugins.ModelTagInfo.prototype = {
	render : function(modelData) {
		// Clear the panels
		if (this.userTagsPanel) {
			this.panel.remove(this.userTagsPanel);
		}
		if (this.publicTagsPanel) {
			this.panel.remove(this.publicTagsPanel);
		}
		// No items selected
		if (modelData == null) {
			this.addTagsButton.disabled = true;
			return; // Render nothing
		} else {
			this.addTagsButton.disabled = false;
		}
		this.userTagsPanel = new Ext.Panel({id: 'model_tags_user_tags_panel'});
		this.userTagsPanel.add({html : 'Your Tags:'});
		this.publicTagsPanel = new Ext.Panel({id: 'model_tags_public_tags_panel'});
		this.publicTagsPanel.add({html : 'Tags by other users:'});
		// returns an array with all user and public tags of the model
		var getAllTags = function(modelId) {
			var allTags = modelData.get(modelId).publicTags.clone();
			allTags.push(modelData.get(modelId).userTags);
			return allTags;
		}
		
		// commonTags is the intersection of all tags of all selected models
		var commonTags = getAllTags(modelData.keys()[0]);
		modelData.each(function(pair) {
			var modelTags = getAllTags(pair.key);
			commonTags.each(function(tag) {
				if (modelTags.indexOf(tag) == -1) {
					commonTags = commonTags.without(tag);
				}
			}.bind(this));		
		}.bind(this));
		
		if (commonTags.length > 1) {
			commomTags = commonTags.uniq(); // Remove doubles
		}
		
		commonTags.each(function(tag) {
			// Check if the user has at least an userTag for one model
			isUserTag = false;
			modelData.each(function(pair) {
				if (pair.value.userTags.indexOf(tag) != -1) {
					isUserTag =  true;
					return;
				}
			}.bind(this));
			
			/* If the user has at least tagged one selected model by itself, create a button with the
			 * tag name and the possibility to remove the tag by click. If the user hasn't tagged
			 * a selected model, then just create a label. 
			 */
			if (isUserTag) {
				// Create new button to show tag name 
				var button = new Ext.Button({text:tag});
				// Remove the tag from all selected models by clicking on the button
				button.addListener('click', function(sender, event) {
					// Iterate over all selected models
					this.facade.getSelectedModels().each(function(modelId) {
						var uri = this.facade.modelCache.getModelUri(modelId).substring(1); // Remove leading slash
						// Make server request to delete the tag
						Ext.Ajax.request({
							url : uri + '/tags?tag_name=' + sender.text, 
							method : 'delete', 
							success : function(response) {
								var data = new Hash(Ext.util.JSON.decode(response.responseText));
								data.each(function (pair) {
									this.facade.modelCache.updateObject('/tags', pair.key, pair.value);
								}.bind(this)) // End of Ajax success function
							}.bind(this)}) // End of Ajax request
					}.bind(this)); // End of selected models iteration 
				}.bind(this)); // End of click handling function
				
				this.userTagsPanel.add(button);
			} else {
				this.publicTagsPanel.add({html : tag + '<br />'});
			}
		}.bind(this));
		
		this.panel.add(this.publicTagsPanel);
		this.panel.add(this.userTagsPanel);
		this.panel.doLayout();
	},
	
	selectionChanged : function(modelIds) {
		if (modelIds.length != 0) {
			this.facade.modelCache.getDataAsync('/tags', modelIds, this.render.bind(this));
		} else {
			this.render(null); // Just clear the panel
		}
	},
	
	modelUpdate : function(modelId) {
		if (this.facade.getSelectedModels().indexOf(modelId) != -1) {
			var a = new Array();
			a.push(modelId);
			this.facade.modelCache.getDataAsync('/tags', a, this.render.bind(this));
		}
	}
}
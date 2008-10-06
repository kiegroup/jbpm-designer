
Ext.namespace('Repository.Plugins');

Repository.Plugins.DebugView = function(facade) {
	this.name = 'Debug View';
	this.facade = facade;
	this.viewPanel = this.facade.registerPluginOnView({name : this.name});
	this.facade.registerOnViewChanged(this.viewChanged.bind(this));
	this.facade.registerOnFilterChanged(this.filterChanged.bind(this));
	// Query meta data for all objects
	this.facade.modelCache.getDataAsync('/meta', this.facade.getFilteredModels(),  this.render.bind(this));

}

Repository.Plugins.DebugView.prototype = {
	viewChanged : function(name) {
		if (this.name == name) {
			// Query meta data for all objects
			this.facade.modelCache.getDataAsync('/meta', this.facade.getFilteredModels(),  this.render.bind(this));
		}
	},
	
	render : function(modelData) {
		// TODO check if this view is really selected
		if (this.viewPanel.items != undefined) {
			this.viewPanel.items.clear; // remove all items
		}
		if (this.viewPanel.findById('debug_view_container')) {
			this.viewPanel.remove('debug_view_container');
		}
		
		// this.viewPanel.getEl().update('');
		var container = new Ext.Panel({id : 'debug_view_container'});
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
		this.viewPanel.add(container);
		this.viewPanel.doLayout(); // Force rendering to show the panel
	},
	
	filterChanged : function(modelIds) {
		this.facade.modelCache.getDataAsync('/meta', modelIds,  this.render.bind(this));
	}
}
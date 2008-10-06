
Ext.namespace('Repository.Plugins');

Repository.Plugins.ModelTypeFilter = function(facade) {
	this.name = 'Filter by Type';
	this.facade = facade;
	this.panel = this.facade.registerPluginOnPanel('Filter by Type', 'left');
	this.modelTypes = [];
	// Textbox
	this.field = new Ext.form.Field({id:'field_model_type_filter'});
	this.panel.add(this.field);
	// Button
	this.testButton = new Ext.Button({ text : 'Tag Test' });
	this.testButton.addListener('click', function() {
		if (this.field.getValue().length > 0) {
			this.facade.applyFilter('tagfilter', this.field.getValue().split(','));
		} else {
			this.facade.removeFilter('tagfilter');
		}
	}.bind(this));
	this.panel.add(this.testButton);
	this.panel.doLayout();
}

Repository.Plugins.ModelTypeFilter.prototype = {
	render : function(modelData) {
		modelData.each(function(pair) {
			// Add unknown types
			if (this.modelTypes.indexOf(pair.value.type) == -1) {
				this.modelTypes.push(pair.value.type);
			}
		});
		
		if (this.panel.items != undefined) {
			this.panel.items.clear; // remove all items
		}
	}
	
}
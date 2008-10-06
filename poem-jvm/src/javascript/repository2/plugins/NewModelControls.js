Ext.namespace('Repository.Plugins');

Repository.Plugins.NewModelControls = function(facade) {
	this.name = 'New Model Controls';
	this.facade = facade;
	this.facade.registerPluginOnToolbar({
		text : 'BPMN', 
		menu : 'Create New Model',
		icon : '/backend/images/silk/lightbulb.png', 
		handler : function(event, options) {alert("YEAH!")}
	});
}

Repository.Plugins.NewModelControls.prototype = {
		
}
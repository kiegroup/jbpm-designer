if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.TaskPropertiesUpdater = Clazz.extend({
	construct: function(facade){
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
	},
	handlePropertyChanged : function(event) {
		if (event["key"] == "oryx-tasktype") {
			var propShape = event.elements[0];
			if(propShape) {
				this.facade.getCanvas().update();
			}
		}
	}
});


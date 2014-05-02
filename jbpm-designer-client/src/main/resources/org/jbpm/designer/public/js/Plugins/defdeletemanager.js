if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.DefDeleteManager = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        //this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DEF_DELETED, this.handledefdeleted.bind(this));
    },

    handledefdeleted : function(options) {
        //alert("LETS HANDLE TYPE: " + options.dtype + " -- for record " + options.rcd);
    }

});
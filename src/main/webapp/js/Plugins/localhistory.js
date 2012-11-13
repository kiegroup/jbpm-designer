if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.LocalStorage = Clazz.extend({
    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': "Local Storage",
            'functionality': this.displayLocalStorage.bind(this),
            'group': "localstorage",
            'icon': ORYX.PATH + "images/localhistory.png",
            'description': "Create a Workflow pattern from selection",
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                profileParamName = "profile";
                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
                regexa = new RegExp( regexSa );
                profileParams = regexa.exec( window.location.href );
                profileParamValue = profileParams[1];
                return profileParamValue == "jbpm";
            }.bind(this)
        });
    }
});
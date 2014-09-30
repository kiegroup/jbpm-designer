if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.ProcessInfo = Clazz.extend({
	construct: function(facade){
		this.facade = facade;

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name':ORYX.I18N.View.showInfo,
                'functionality': this.showInfo.bind(this),
                'group': ORYX.I18N.View.infogroup,
                'icon': ORYX.BASE_FILE_PATH + "images/information.png",
                'description': ORYX.I18N.View.showInfoDesc,
                'index': 1,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.READONLY != true;
    //				profileParamName = "profile";
    //				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //		        regexa = new RegExp( regexSa );
    //		        profileParams = regexa.exec( window.location.href );
    //		        profileParamValue = profileParams[1];
    //				return profileParamValue == "jbpm";
                }.bind(this)
            });
        }
	},
	showInfo : function() {
		window.alert("jBPM Designer Version: " + ORYX.VERSION);
	}
});
//window.frames[6].ORYX.EDITOR._canvas.nodes[3]._stencil.icon()


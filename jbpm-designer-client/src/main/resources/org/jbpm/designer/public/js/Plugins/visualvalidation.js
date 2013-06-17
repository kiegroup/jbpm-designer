if (!ORYX.Plugins) {
    ORYX.Plugins = new Object();
}

ORYX.Plugins.VisualValidation = ORYX.Plugins.AbstractPlugin.extend({

    construct: function(facade){
        this.facade = facade;
        this.active = false;
        this.vt;

        this.facade.offer({
            'name': ORYX.I18N.SyntaxChecker.name,
            'functionality': this.doValidation.bind(this),
            'group': ORYX.I18N.View.jbpmgroup,
            'icon': ORYX.BASE_FILE_PATH + "images/checker_syntax.png",
            'description': ORYX.I18N.SyntaxChecker.desc,
            'index': 6,
            'toggle': true,
            'minShape': 0,
            'maxShape': 0
        });

    },
    doValidation: function(button, pressed) {
        if (!pressed) {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : 'Stopping visual validation',
                title       : ''

            });
            this.setActivated(button, false);
            window.clearInterval(this.vt);
            this.stopValidate();
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : 'Starting visual validation',
                title       : ''

            });
            this.setActivated(button, true);
            this.vt = window.setInterval((function(){
               this.startValidate();
            }).bind(this), 3000);
        }

    },

    setActivated: function(button, activated){
        button.toggle(activated);
        if(activated === undefined){
            this.active = !this.active;
        } else {
            this.active = activated;
        }
    },

    startValidate: function() {
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        new Ajax.Request(ORYX.PATH + "syntaxcheck", {
            method: 'POST',
            asynchronous: false,
            parameters: {
                data: processJSON,
                profile: ORYX.PROFILE,
                pp: ORYX.PREPROCESSING,
                uuid: ORYX.UUID
            },
            onSuccess: function(request){
                alert("got validation results");
            }.bind(this),
            onFailure: function(){
                alert("failed to get validation results");
            }
        });
    },

    stopValidate: function() {

    }

});

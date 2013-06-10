if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.ActiveNodesHighlighter = Clazz.extend({
    construct: function(facade){
        this.facade = facade;

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.highlightnodes.bind(this));
    },
    highlightnodes: function(options) {
        ORYX.EDITOR._canvas.getChildren().each((function(child) {
            this.applyHighlightingToChild(child);
        }).bind(this));
    },
    applyHighlightingToChild: function(child) {
        if(ORYX.ACTIVENODES) {
            for(var i=0;i<ORYX.ACTIVENODES.length;i++) {
                if(child instanceof ORYX.Core.Node || child instanceof ORYX.Core.Edge) {
                    if(ORYX.ACTIVENODES[i] == child.resourceId) {
                        child.setProperty("oryx-bordercolor", "#FF0000");
                    }
                }
            }
        }
        if(ORYX.COMPLETEDNODES) {
            for(var i=0;i<ORYX.COMPLETEDNODES.length;i++) {
                if(child instanceof ORYX.Core.Node || child instanceof ORYX.Core.Edge) {
                    if(ORYX.COMPLETEDNODES[i] == child.resourceId) {
                        child.setProperty("oryx-bordercolor", "#A8A8A8");
                        child.setProperty("oryx-bgcolor", "#CDCDCD");
                    }
                }
            }
        }

        if(child instanceof ORYX.Core.Node || child instanceof ORYX.Core.Edge) {
            if(ORYX.READONLY == true) {
                child.setSelectable(false);
                child.setMovable(false);
                child.setProperty("oryx-isselectable", "false");

                if(child instanceof ORYX.Core.Edge) {
                    child.dockers.each((function(docker){
                        docker.setMovable(false);
                        docker.update();
                    }));
                }
            }
            child.refresh();
        }

        if(child && child.getChildren().size() > 0) {
            for (var i = 0; i < child.getChildren().size(); i++) {
                this.applyHighlightingToChild(child.getChildren()[i]);
            }
        }
    }
});
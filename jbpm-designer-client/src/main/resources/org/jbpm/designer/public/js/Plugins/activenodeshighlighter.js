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
                if(ORYX.ACTIVENODES[i] == child.resourceId) {
                    child.setProperty("oryx-bordercolor", "#8A2BE2");
                    child.setProperty("oryx-bgcolor", "#8A2BE2");
                    child.refresh();
                }
            }
        }
        if(child && child.getChildren().size() > 0) {
            for (var i = 0; i < child.getChildren().size(); i++) {
                this.applyHighlightingToChild(child.getChildren()[i]);
            }
        }
    }
});
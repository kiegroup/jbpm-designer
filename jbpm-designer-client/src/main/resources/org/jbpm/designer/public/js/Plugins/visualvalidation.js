if (!ORYX.Plugins) {
    ORYX.Plugins = new Object();
}

ORYX.Plugins.VisualValidation = ORYX.Plugins.AbstractPlugin.extend({

    construct: function(facade){
        this.facade = facade;
        this.active = false;
        this.vt;
        this.allErrors = {};
        this.errorDisplayView;

        this.facade.offer({
            'name': ORYX.I18N.SyntaxChecker.name,
            'functionality': this.doValidation.bind(this),
            'group': 'visualvalidation',
            'icon': ORYX.BASE_FILE_PATH + "images/visualvalidation.png",
            'description': ORYX.I18N.SyntaxChecker.desc,
            'index': 6,
            'toggle': true,
            'minShape': 0,
            'maxShape': 0
        });

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_CLICK, this.displayErrorsOnNode.bind(this));

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
            onSuccess: function(request) {
                this.allErrors = new Hash();
                this.resetBorderColors();
                var errors = request.responseText.evalJSON();
                if(!(errors instanceof Hash)){
                    errors = new Hash(errors);
                }
                this.allErrors = errors;
                errors.keys().each(function(value) {
                    var shape = this.facade.getCanvas().getChildShapeByResourceId(value);
                    if (shape) {
                        if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
                            shape.setProperty("oryx-bordercolor", "#FF6600");
                            shape.refresh();
                        }
                    }
                }.bind(this));

            }.bind(this),
            onFailure: function() {
                this.allErrors = new Hash();
            }
        });
    },

    stopValidate: function() {
        this.allErrors = new Hash();
        this.resetBorderColors();
    },

    resetBorderColors: function() {
        ORYX.EDITOR.getCanvas().children.each(function(shape) {
            this.resetShape(shape);
        }.bind(this));
    },

    resetShape: function(shape) {
        if(shape) {
            if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
                shape.setProperty("oryx-bordercolor", shape.properties["oryx-origbordercolor"]);
                shape.refresh();
            }
            if(shape.getChildren().size() > 0) {
                for (var i = 0; i < shape.getChildren().size(); i++) {
                    if(shape.getChildren()[i] instanceof ORYX.Core.Node || shape.getChildren()[i] instanceof ORYX.Core.Edge) {
                        this.resetShape(shape.getChildren()[i]);
                    }
                }
            }
        }
    },

    displayErrorsOnNode: function(evt, shape) {
        if(this.active) {
            if(this.allErrors instanceof Hash) {
            var ErrorData = Ext.data.Record.create([{
                name: 'name'
            }]);

            var errorDataProxy = new Ext.data.MemoryProxy({
                root: []
            });

            var errordata = new Ext.data.Store({
                autoDestroy: true,
                reader: new Ext.data.JsonReader({
                    root: "root"
                }, ErrorData),
                proxy: errorDataProxy,
                sorters: [{
                    property: 'name',
                    direction:'ASC'
                }]
            });
            errordata.load();

            var foundErrorsForNode = false;
            this.allErrors.keys().each(function(value) {
                if(value == shape.resourceId) {
                    foundErrorsForNode = true;
                    var ae = this.allErrors[value];
                    for (var i = 0; i < ae.length; i++){
                        errordata.add(new ErrorData({
                            name: ae[i]
                        }));
                    }
                }
            }.bind(this));

            if(foundErrorsForNode) {
                var grid = new Ext.grid.EditorGridPanel({
                    autoScroll: true,
                    autoHeight: true,
                    store: errordata,
                    stripeRows: true,
                    cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
                        id: 'name',
                        header: 'Description',
                        width: 500,
                        dataIndex: 'name',
                        editor: new Ext.form.TextField({ allowBlank: true, vtype: 'inputName', regex: /^[a-z0-9 \-\.\_]*$/i }),
                        renderer: Ext.util.Format.htmlEncode
                    }]),
                    autoHeight: true,
                    clicksToEdit: 1
                });


                var dialog = new Ext.Window({
                    layout		: 'anchor',
                    autoCreate	: true,
                    title		: 'Validation suggestions',
                    height		: 300,
                    width		: 500,
                    modal		: true,
                    collapsible	: false,
                    fixedcenter	: true,
                    shadow		: true,
                    resizable   : true,
                    proxyDrag	: true,
                    autoScroll  : true,
                    keys:[{
                        key	: 27,
                        fn	: function(){
                            dialog.hide()
                        }.bind(this)
                    }],
                    items		:[grid],
                    listeners	:{
                        hide: function(){
                            this.fireEvent('dialogClosed', this.value);
                            //this.focus.defer(10, this);
                            dialog.destroy();
                        }.bind(this)
                    },
                    buttons		: [{
                        text: 'Close',
                        handler: function(){
                            dialog.hide()
                        }.bind(this)
                    }]
                });

                dialog.show();

            }

        }
        }
    }
});

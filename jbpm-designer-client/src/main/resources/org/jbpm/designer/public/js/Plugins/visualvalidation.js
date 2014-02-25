if (!ORYX.Plugins) {
    ORYX.Plugins = new Object();
}

ORYX.Plugins.VisualValidation = ORYX.Plugins.AbstractPlugin.extend({

    construct: function(facade){
        this.facade = facade;
        this.vt;
        this.allErrors = {};
        this.errorDisplayView;
        ORYX.IS_VALIDATING_PROCESS = false;

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name': ORYX.I18N.SyntaxChecker.startValidating,
                'functionality': this.enableValidation.bind(this),
                'group': 'validationandsimulation',
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/visualvalidation.png",
                'description': ORYX.I18N.SyntaxChecker.startValidating_desc,
                'index': 1,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return !ORYX.IS_VALIDATING_PROCESS && ORYX.READONLY != true;
                }
            });

            this.facade.offer({
                'name': ORYX.I18N.SyntaxChecker.stopValidating,
                'functionality': this.disableValidation.bind(this),
                'group': 'validationandsimulation',
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/visualvalidation.png",
                'description': ORYX.I18N.SyntaxChecker.stopValidating_desc,
                'index': 2,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.IS_VALIDATING_PROCESS && ORYX.READONLY != true;
                }
            });

            this.facade.offer({
                'name': ORYX.I18N.SyntaxChecker.viewAllIssues,
                'functionality': this.viewAllValidation.bind(this),
                'group': 'validationandsimulation',
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/visualvalidation.png",
                'description': ORYX.I18N.SyntaxChecker.viewAllIssues_desc,
                'index': 3,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.READONLY != true;
                }
            });
        }

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_CLICK, this.displayErrorsOnNode.bind(this));

    },

    enableValidation: function() {
        ORYX.IS_VALIDATING_PROCESS = true;
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.SyntaxChecker.startingContinousVal,
            title       : ''

        });
        this.vt = window.setInterval((function(){
            this.startValidate(true);
        }).bind(this), 3000);
    },

    disableValidation: function() {
        ORYX.IS_VALIDATING_PROCESS = false;
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.SyntaxChecker.stoppingContinousVal,
            title       : ''

        });
        window.clearInterval(this.vt);
        this.stopValidate();
    },

    viewAllValidation: function() {
        this.startValidate(false);
        this.displayErrorsOnNode();

        this.disableValidation();
    },

    startValidate: function(updateshapes) {
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
                if(updateshapes) {
                    errors.keys().each(function(value) {
                        var shape = this.facade.getCanvas().getChildShapeByResourceId(value);
                        if (shape) {
                            if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
                                shape.setProperty("oryx-bordercolor", "#FF6600");
                                shape.refresh();
                            }
                        }
                    }.bind(this));
                }

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
            if(this.allErrors instanceof Hash) {
            var ErrorData = Ext.data.Record.create([{
                name: 'name',
                shapeid: 'shapeid',
                type: 'type'
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
            if(shape) {
                this.allErrors.keys().each(function(value) {
                    if(value == shape.resourceId) {
                        foundErrorsForNode = true;
                        var ae = this.allErrors[value];
                        for (var i = 0; i < ae.length; i++){
                            errordata.add(new ErrorData({
                                name: ae[i].error,
                                shapeid: ae[i].id,
                                type: ae[i].type
                            }));
                        }
                    }
                }.bind(this));
            } else {
                // dont filter (show all);
                this.allErrors.keys().each(function(value) {
                        var ae = this.allErrors[value];
                        for (var i = 0; i < ae.length; i++){
                            errordata.add(new ErrorData({
                                name: ae[i].error,
                                shapeid: ae[i].id,
                                type: ae[i].type
                            }));
                        }
                }.bind(this));
                foundErrorsForNode = true;
            }


            if(foundErrorsForNode) {
                var grid = new Ext.grid.EditorGridPanel({
                    autoScroll: true,
                    autoHeight: true,
                    store: errordata,
                    stripeRows: true,
                    cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),
                        {
                            id: 'type',
                            header: ORYX.I18N.SyntaxChecker.header_IssueType,
                            width: 100,
                            dataIndex: 'type',
                            sortable  : true,
                            editor: new Ext.form.TextField({ allowBlank: true, vtype: 'inputName', regex: /^[a-z0-9 \-\.\_]*$/i }),
                            renderer: Ext.util.Format.htmlEncode
                        },
                        {
                            id: 'name',
                            header: ORYX.I18N.SyntaxChecker.header_Description,
                            width: 500,
                            dataIndex: 'name',
                            sortable  : true,
                            editor: new Ext.form.TextField({ allowBlank: true, vtype: 'inputName', regex: /^[a-z0-9 \-\.\_]*$/i }),
                            renderer: Ext.util.Format.htmlEncode
                        },
                        {
                            id: 'shapeid',
                            header: ORYX.I18N.SyntaxChecker.header_ShapeId,
                            width: 100,
                            dataIndex: 'shapeid',
                            sortable  : true,
                            editor: new Ext.form.TextField({ allowBlank: true, vtype: 'inputName', regex: /^[a-z0-9 \-\.\_]*$/i }),
                            renderer: Ext.util.Format.htmlEncode
                        }
                    ]),
                    autoHeight: true,
                    clicksToEdit: 1
                });


                var dialog = new Ext.Window({
                    layout		: 'anchor',
                    autoCreate	: true,
                    title		: ORYX.I18N.SyntaxChecker.suggestions,
                    height		: 300,
                    width		: 700,
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
                            dialog.destroy();
                        }.bind(this)
                    },
                    buttons		: [{
                        text: ORYX.I18N.Save.close,
                        handler: function(){
                            dialog.hide()
                        }.bind(this)
                    }]
                });

                dialog.show();

            }

        }

    }
});

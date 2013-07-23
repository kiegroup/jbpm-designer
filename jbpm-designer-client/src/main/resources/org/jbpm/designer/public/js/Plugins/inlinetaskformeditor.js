if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.InlineTaskFormEditor = Clazz.extend({
    sourceMode: undefined,
    taskformeditor: undefined,
    taskformsourceeditor: undefined,
    taskformcolorsourceeditor: undefined,
    hlLine: undefined,

    construct: function(facade){
        this.facade = facade;
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TASKFORM_EDIT, this.chooseFormEditor.bind(this));
    },

    chooseFormEditor: function(options) {
        Ext.Msg.show({
            title : "Form Editor.",
            msg : "Select which Form Editor to use:",
            buttons : {yes : 'Graphical Modeler', no : 'Markup Editor', cancel : 'Cancel'},
            icon : Ext.MessageBox.QUESTION,
            fn : function(btn) {
                if(btn == 'yes'){
                    this.showTaskFormEditor("form", options);
                } else if(btn == 'no'){
                    this.showTaskFormEditor("ftl", options);
                }
            }.bind(this)
        });
    },

    showTaskFormEditor: function(formType, options) {
        if(options && options.tn) {
            // load form widgets first
            Ext.Ajax.request({
                url: ORYX.PATH + 'formwidget',
                method: 'POST',
                success: function(response) {
                    try {
                        var widgetJson = response.responseText.evalJSON();
                        // now the form editor
                        Ext.Ajax.request({
                            url: ORYX.PATH + 'taskformseditor',
                            method: 'POST',
                            success: function(response) {
                                try {
                                    if(formType == "form") {
                                        var responseParts = response.responseText.split("|");
                                        parent.designeropenintab(responseParts[0], responseParts[1]);
                                    } else {
                                        this._buildandshow(formType, options.tn, response.responseText, widgetJson);
                                    }
                                } catch(e) {
                                    this.facade.raiseEvent({
                                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                        ntype		: 'error',
                                        msg         : 'Error initiating Form Editor: ' + e,
                                        title       : ''

                                    });
                                }
                            }.bind(this),
                            failure: function(){
                                this.facade.raiseEvent({
                                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype		: 'error',
                                    msg         : 'Error initiating Form Editor',
                                    title       : ''

                                });
                            },
                            params: {
                                formtype: formType,
                                action: 'load',
                                taskname: options.tn,
                                profile: ORYX.PROFILE,
                                uuid : ORYX.UUID
                            }
                        });
                    } catch(e) {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : 'Error initiating Form Widgets: ' + e,
                            title       : ''

                        });
                    }
                }.bind(this),
                failure: function(){
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : 'Error initiating Form Widgets',
                        title       : ''

                    });
                },
                params: {
                    action: 'getwidgets',
                    profile: ORYX.PROFILE,
                    uuid: ORYX.UUID
                }
            });
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : 'Task Name not specified.',
                title       : ''

            });
        }
    },
    _buildandshow: function(formType, tn, defaultsrc, widgetJson) {
        var formvalue = "";
        if(defaultsrc && defaultsrc != "false") {
            formvalue = defaultsrc;
        }

        var widgetKeys = [];
        for (var key in widgetJson) {
            if (widgetJson.hasOwnProperty(key)) {
                widgetKeys.push(key);
            }
        }
        widgetKeys.sort();
        var displayWidgetKeys = [];
        for (var i = 0; i < widgetKeys.length; i++) {
            displayWidgetKeys[i] = [widgetKeys[i] + ""];
        }

        var widgetStore = new Ext.data.SimpleStore({
            fields: ["name"],
            data : displayWidgetKeys
        });

        var widgetCombo = new Ext.form.ComboBox({
            fieldLabel: 'Insert form widget',
            labelStyle: 'width:240px',
            hiddenName: 'widget_name',
            emptyText: 'Insert form widget...',
            store: widgetStore,
            displayField: 'name',
            valueField: 'name',
            mode: 'local',
            typeAhead: true,
            triggerAction: 'all',
            listeners:
            {
                select: {
                    fn:function(combo, value) {
                        if(this.taskformcolorsourceeditor) {
                            Ext.Ajax.request({
                                url: ORYX.PATH + 'formwidget',
                                method: 'POST',
                                success: function(response) {
                                    try {
                                        this.taskformcolorsourceeditor.replaceSelection(response.responseText, "end");
                                    } catch(e) {
                                        this.facade.raiseEvent({
                                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                            ntype		: 'error',
                                            msg         : 'Error inserting Form Widget: ' + e,
                                            title       : ''

                                        });
                                    }
                                }.bind(this),
                                failure: function(){
                                    this.facade.raiseEvent({
                                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                        ntype		: 'error',
                                        msg         : 'Error inserting Form Widget',
                                        title       : ''

                                    });
                                },
                                params: {
                                    action: 'getwidgetsource',
                                    profile: ORYX.PROFILE,
                                    widgetname: combo.getValue(),
                                    uuid: ORYX.UUID
                                }
                            });
                        } else {
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : 'Widget insertion is only possible in Source Mode',
                                title       : ''

                            });
                        }
                    }.bind(this)
                }
            }
        });

        var sourceeditorid = Ext.id();
        this.taskformsourceeditor = new Ext.form.TextArea({
            id: sourceeditorid,
            anchor: '100%',
            autoScroll: true,
            value: formvalue
        });

        var outterPanel = new Ext.Panel({
            header: false,
            anchor: '100%',
            layout:'column',
            autoScroll:true,
            border : false,
            layoutConfig: {
                columns: 2,
                pack:'center',
                align:'middle'
            },
            items: [
                {
                    columnWidth: .5,
                    items: this.taskformsourceeditor
                },{
                    columnWidth: .5,
                    items: [
                        {
                            xtype : "component",
                            id    : 'livepreviewpanel',
                            anchor: '100%',
                            autoScroll: true,
                            autoEl : {
                                tag : "iframe",
                                width: "100%",
                                height: "570",
                                frameborder: "0",
                                scrolling: "auto"
                            }
                        }]
                }
            ]
        });

        var itfe = new Ext.Window({
            id          : 'maineditorwindow',
            layout		: 'fit',
            autoCreate	: true,
            title		: 'Editing Form: ' + tn + ' - Press [Ctrl-Z] to activate auto-completion' ,
            height		: 570,
            width		: 930,
            modal		: true,
            collapsible	: false,
            fixedcenter	: true,
            shadow		: true,
            resizable   : true,
            proxyDrag	: true,
            keys:[{
                fn	: function(){
                    itfe.close();
                    itfe = null;
                }.bind(this)
            }],
            items		:[outterPanel],
            listeners	:{
                hide: function(){
                    itfe = null;
                }.bind(this)
            },
            buttons		: [{
                text: 'Save',
                handler: function(){
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'info',
                        msg         : 'Storing Task Form',
                        title       : ''

                    });

                    var tosaveValue = "";
                    tosaveValue = this.taskformcolorsourceeditor.getValue();

                    Ext.Ajax.request({
                        url: ORYX.PATH + 'taskformseditor',
                        method: 'POST',
                        success: function(request) {
                            try {
                                var generatedForm = request.responseText.evalJSON();
                                parent.designersignalassetadded(generatedForm.formid);
                                parent.designersignalassetupdate(generatedForm.formid);

                                itfe.close();
                                itfe = null;
                            } catch(e) {
                                this.facade.raiseEvent({
                                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype		: 'error',
                                    msg         : 'Error saving Task Form: ' + e,
                                    title       : ''

                                });
                            }
                        }.createDelegate(this),
                        failure: function(){
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : 'Error saving Task Form',
                                title       : ''

                            });
                        },
                        params: {
                            formtype: formType,
                            action: 'save',
                            taskname: tn,
                            profile: ORYX.PROFILE,
                            uuid : ORYX.UUID,
                            tfvalue: tosaveValue
                        }
                    });
                }.bind(this)
            },
                {
                    text: 'Cancel',
                    handler: function(){
                        itfe.close();
                        itfe = null;
                    }.bind(this)
                }],
            tbar: [
                widgetCombo
            ]
        });
        itfe.show();
        this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);
        var delay;
        this.taskformcolorsourceeditor = CodeMirror.fromTextArea(document.getElementById(sourceeditorid), {
            mode: "text/html",
            lineNumbers: true,
            lineWrapping: true,
            onGutterClick: this.foldFunc,
            extraKeys: {
                "'>'": function(cm) { cm.closeTag(cm, '>'); },
                "'/'": function(cm) { cm.closeTag(cm, '/'); },
                "Ctrl-Z": function(cm) {CodeMirror.hint(cm, CodeMirror.formsHint, outterPanel);}
            },
            onCursorActivity: function() {
                this.taskformcolorsourceeditor.setLineClass(this.hlLine, null, null);
                this.hlLine = this.taskformcolorsourceeditor.setLineClass(this.taskformcolorsourceeditor.getCursor().line, null, "activeline");
            }.bind(this),
            onChange: function() {
                clearTimeout(delay);
                delay = setTimeout(this.updatePreview.bind(this), 300);
            }.bind(this)
        });
        this.hlLine = this.taskformcolorsourceeditor.setLineClass(0, "activeline");
        setTimeout(this.updatePreview.bind(this), 300);
    },
    updatePreview: function() {
        var previewFrame = document.getElementById('livepreviewpanel');
        var preview =  previewFrame.contentDocument ||  previewFrame.contentWindow.document;
        preview.open();
        preview.write(this.taskformcolorsourceeditor.getValue());
        preview.close();
    }

});
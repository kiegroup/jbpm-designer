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
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TASKFORM_EDIT, this.chooseFormEditorLoad.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TASKFORM_GENERATE, this.chooseFormEditorStore.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TASKFORM_GENERATE_ALL, this.chooseFormEditorStoreAll.bind(this));
    },

    chooseFormEditorLoad : function(options) {
        this.chooseFormEditor(options, "load");
    },

    chooseFormEditorStore : function(options) {
        this.chooseFormEditor(options, "store");
    },

    chooseFormEditorStoreAll : function(options) {
        this.chooseFormEditor(options, "storeall");
    },

    chooseFormEditor: function(options, action) {
        // process must be saved before generating forms (JBPM-7119)
        // if its not we ask user to save before
        // generating forms
        if(!ORYX.PROCESS_SAVED) {
            Ext.Msg.show({
                title:ORYX.I18N.inlineTaskFormEditor.processMustBeSavedTitle,
                msg: ORYX.I18N.inlineTaskFormEditor.processMustBeSavedDesc,
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.INFO
            });
        } else {
            ORYX.FORMSTYPE = "frm";
            if(action == "load") {
                this.showTaskFormEditor(ORYX.FORMSTYPE, options);
            } else if(action == "store") {
                this.generateTaskForm(ORYX.FORMSTYPE, options);
            } else if(action == "storeall") {
                this.generateAllTaskForms(ORYX.FORMSTYPE, options);
            } else {
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.inlineTaskFormEditor.errorInitiatingEditor+'.',
                    title       : ''
                });
            }
        }
    },

    generateTaskForm: function(formType, options) {
        if(options && options.tn && options.taskid) {
            var taskname = options.tn;
            if (taskname && taskname.length > 0) {
                taskname = taskname.replace(/\&/g, "");
                taskname = taskname.replace(/\s/g, "");

                if (/^\w+$/.test(taskname)) {
                    Ext.Ajax.request({
                        url: ORYX.PATH + "taskforms",
                        method: 'POST',
                        success: function (response) {
                            if(response.responseText.length > 0 && response.responseText != "fail") {
                                this.facade.raiseEvent({
                                    type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype: 'success',
                                    msg: ORYX.I18N.forms.successGenTask,
                                    title: ''

                                });
                            } else {
                                this.facade.raiseEvent({
                                    type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype: 'error',
                                    msg: ORYX.I18N.forms.failGenTask,
                                    title: ''
                                });
                            }
                        }.createDelegate(this),
                        failure: function () {
                            this.facade.raiseEvent({
                                type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype: 'error',
                                msg: ORYX.I18N.forms.failGenTask,
                                title: ''
                            });
                        }.createDelegate(this),
                        params: {
                            profile: ORYX.PROFILE,
                            uuid: window.btoa(encodeURI(ORYX.UUID)),
                            json: ORYX.EDITOR.getSerializedJSON(),
                            ppdata: ORYX.PREPROCESSING,
                            taskid: options.taskid,
                            formtype: formType,
                            sessionid: ORYX.SESSION_ID
                        }
                    });
                } else {
                    ORYX.Config.FACADE.raiseEvent({
                        type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype: 'error',
                        msg: ORYX.I18N.forms.failInvalidTaskName,
                        title: ''

                    });
                }

            } else {
                ORYX.Config.FACADE.raiseEvent({
                    type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype: 'error',
                    msg: ORYX.I18N.forms.failNoTaskName,
                    title: ''

                });
            }
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.inlineTaskFormEditor.taskNameNotSpecified,
                title       : ''

            });
        }
    },

    generateAllTaskForms: function(formType, options) {
        Ext.Ajax.request({
            url: ORYX.PATH + "taskforms",
            method: 'POST',
            success: function(response){
                if(response.responseText.length > 0 && response.responseText != "fail") {
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'success',
                        msg         : ORYX.I18N.forms.successGenProcAndTask,
                        title       : ''

                    });
                } else {
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.forms.failGenProcAndTask,
                        title       : ''
                    });
                }
            }.createDelegate(this),
            failure: function(){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.forms.failGenProcAndTask,
                    title       : ''
                });
            }.createDelegate(this),
            params: {
                profile: ORYX.PROFILE,
                uuid :  window.btoa(encodeURI(ORYX.UUID)),
                json : ORYX.EDITOR.getSerializedJSON(),
                ppdata: ORYX.PREPROCESSING,
                formtype: formType,
                sessionid: ORYX.SESSION_ID
            }
        });


        ORYX.CONFIG.TASKFORMS_URL = function(uuid, profile) {
            if (uuid === undefined) {
                uuid = ORYX.UUID;
            }
            if (profile === undefined) {
                profile = ORYX.PROFILE;
            }
            return ORYX.PATH + "taskforms?uuid="+  window.btoa(encodeURI(uuid)) + "&profile=" + profile;
        };
    },

    showTaskFormEditor: function(formType, options) {
        if(options && options.tn) {
            Ext.Ajax.request({
                url: ORYX.PATH + 'taskformseditor',
                method: 'POST',
                success: function(response) {
                    try {
                        var responseParts = response.responseText.split("|");
                        parent.designeropenintab(responseParts[0], encodeURI(responseParts[1]));
                    } catch(e) {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.inlineTaskFormEditor.errorInitiatingEditor + ': ' + e,
                            title       : ''

                        });
                    }
                }.bind(this),
                failure: function(){
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.inlineTaskFormEditor.errorInitiatingEditor+'.',
                        title       : ''
                    });
                },
                params: {
                    formtype: formType,
                    action: 'load',
                    taskname: window.btoa(encodeURI(options.tn)),
                    profile: ORYX.PROFILE,
                    uuid :  window.btoa(encodeURI(ORYX.UUID)),
                    json : ORYX.EDITOR.getSerializedJSON(),
                    ppdata: ORYX.PREPROCESSING,
                    taskid : options.taskid,
                    sessionid: ORYX.SESSION_ID
                }
            });
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.inlineTaskFormEditor.taskNameNotSpecified,
                title       : ''

            });
        }
    }
});

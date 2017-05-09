if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.FormEditing = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        if(!(ORYX.READONLY == true || ORYX.VIEWLOCKED == true)) {
            // disable for ruleflow perspective preset
            if(ORYX.PRESET_PERSPECTIVE != "ruleflow") {
                this.facade.offer({
                    'name': ORYX.I18N.View.editProcessForm,
                    'functionality': this.editProcessForm.bind(this),
                    'group': "editprocessforms",
                    'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
                    dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
                    'description': ORYX.I18N.View.editProcessFormDesc ,
                    'index': 1,
                    'minShape': 0,
                    'maxShape': 0,
                    'isEnabled': function(){
                        return !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
                    }.bind(this)
                });

                this.facade.offer({
                    'name': ORYX.I18N.View.editTaskForm,
                    'functionality': this.editTaskForm.bind(this),
                    'group': "editprocessforms",
                    'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
                    dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
                    'description': ORYX.I18N.View.editTaskFormDesc,
                    'index': 2,
                    'minShape': 1,
                    'maxShape': 1,
                    'isEnabled': function(){
                        return !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
                    }.bind(this)
                });

                this.facade.offer({
                    'name': ORYX.I18N.forms.generateTaskForm,
                    'functionality': this.generateTaskForm.bind(this),
                    'group': "editprocessforms",
                    'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
                    dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
                    'description': ORYX.I18N.forms.generateTaskForm_desc,
                    'index': 3,
                    'minShape': 1,
                    'maxShape': 1,
                    'isEnabled': function(){
                        return !(ORYX.READONLY == true || ORYX.VIEWLOCKED);
                    }.bind(this)
                });

                this.facade.offer({
                    'name': ORYX.I18N.forms.generateAllForms,
                    'functionality': this.generateTaskForms.bind(this),
                    'group': "editprocessforms",
                    'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
                    dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
                    'description': ORYX.I18N.forms.generateAllForms_desc,
                    'index': 4,
                    'minShape': 0,
                    'maxShape': 0,
                    'isEnabled': function(){
                        return !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
                    }.bind(this)
                });
            }
        }
    },

    generateTaskForm: function() {
        var currentShapes = ORYX.Config.FACADE.getSelection();
        if(currentShapes) {
            if(currentShapes.length != 1) {
                ORYX.Config.FACADE.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.forms.invalidNumberNodes,
                    title       : ''

                });
            } else {
                var tasktype = currentShapes[0].properties['oryx-tasktype'];
                if(tasktype && tasktype == "User") {
                    var taskname = currentShapes[0].properties['oryx-taskname'];
                    ORYX.Config.FACADE.raiseEvent({
                        type: ORYX.CONFIG.EVENT_TASKFORM_GENERATE,
                        tn: taskname,
                        taskid: currentShapes[0].resourceId
                    });
                } else {
                    ORYX.Config.FACADE.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.forms.failNoUserTask,
                        title       : ''

                    });
                }
            }

        } else {
            ORYX.Config.FACADE.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.forms.failNoTaskSelected,
                title       : ''

            });
        }
    },

    editTaskForm: function() {
        var currentShapes = ORYX.Config.FACADE.getSelection();
        if(currentShapes) {
            if(currentShapes.length != 1) {
                ORYX.Config.FACADE.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.forms.invalidNumberNodes,
                    title       : ''

                });
            } else {
                var tasktype = currentShapes[0].properties['oryx-tasktype'];
                if(tasktype && tasktype == "User") {
                    var taskname = currentShapes[0].properties['oryx-taskname'];
                    if(taskname && taskname.length > 0) {
                        taskname =  taskname.replace(/\&/g, "");
                        taskname = taskname.replace(/\s/g, "");

                        if(/^\w+$/.test(taskname)) {
                            ORYX.Config.FACADE.raiseEvent({
                                type: ORYX.CONFIG.EVENT_TASKFORM_EDIT,
                                tn: taskname,
                                taskid : currentShapes[0].resourceId
                            });
                        } else {
                            ORYX.Config.FACADE.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.forms.failInvalidTaskName,
                                title       : ''

                            });
                        }
                    } else {
                        ORYX.Config.FACADE.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.forms.failNoTaskName,
                            title       : ''

                        });
                    }
                } else {
                    ORYX.Config.FACADE.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.forms.failNoUserTask,
                        title       : ''

                    });
                }
            }
        } else {
            ORYX.Config.FACADE.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.forms.failNoTaskSelected,
                title       : ''

            });
        }
    },

    editProcessForm : function() {
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        if(processId && processId != "") {
            ORYX.Config.FACADE.raiseEvent({
                type: ORYX.CONFIG.EVENT_TASKFORM_EDIT,
                tn: processId
            });
        } else {
            ORYX.Config.FACADE.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.forms.failProcIdUndef,
                title       : ''

            });
        }
    },
    generateTaskForms : function() {
        ORYX.Config.FACADE.raiseEvent({
            type: ORYX.CONFIG.EVENT_TASKFORM_GENERATE_ALL
        });
    }

});
if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.FormEditing = Clazz.extend({
    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': "Edit Process Form",
            'functionality': this.editProcessForm.bind(this),
            'group': "editprocessforms",
            'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
            dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
            'description': "Edit Process Form",
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                return true;
//                profileParamName = "profile";
//                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//                regexa = new RegExp( regexSa );
//                profileParams = regexa.exec( window.location.href );
//                profileParamValue = profileParams[1];
//                return profileParamValue == "jbpm" && ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });

        this.facade.offer({
            'name': "Edit Task Form",
            'functionality': this.editTaskForm.bind(this),
            'group': "editprocessforms",
            'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
            dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
            'description': "Edit Task Form",
            'index': 2,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                return true;
//                profileParamName = "profile";
//                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//                regexa = new RegExp( regexSa );
//                profileParams = regexa.exec( window.location.href );
//                profileParamValue = profileParams[1];
//                return profileParamValue == "jbpm" && !ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });

        this.facade.offer({
            'name': "Generate all Forms",
            'functionality': this.generateTaskForms.bind(this),
            'group': "editprocessforms",
            'icon': ORYX.BASE_FILE_PATH + "images/processforms.png",
            dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/processforms.png",
            'description': "Generate all Forms",
            'index': 3,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                return true;
//                profileParamName = "profile";
//                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//                regexa = new RegExp( regexSa );
//                profileParams = regexa.exec( window.location.href );
//                profileParamValue = profileParams[1];
//                return profileParamValue == "jbpm" && ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });
    },
    editTaskForm: function() {
        var currentShapes = ORYX.Config.FACADE.getSelection();
        if(currentShapes) {
            if(currentShapes.length != 1) {
                ORYX.Config.FACADE.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : 'Invalid number of nodes selected..',
                    title       : ''

                });
            } else {
                var tasktype = currentShapes[0].properties['oryx-tasktype'];
                if(tasktype && tasktype == "User") {
                    var taskname = currentShapes[0].properties['oryx-taskname'];
                    if(taskname && taskname.length > 0) {
                        taskname =  taskname.replace(/\&/g, "");
                        taskname = taskname.replace(/\s/g, "");
                        ORYX.Config.FACADE.raiseEvent({
                            type: ORYX.CONFIG.EVENT_TASKFORM_EDIT,
                            tn: taskname
                        });
                    } else {
                        ORYX.Config.FACADE.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : 'Task Name not specified.',
                            title       : ''

                        });
                    }
                } else {
                    ORYX.Config.FACADE.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : 'Selected node is not User Task.',
                        title       : ''

                    });
                }
            }
        } else {
            ORYX.Config.FACADE.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : 'No task selected.',
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
                msg         : 'Process Id not specified.',
                title       : ''

            });
        }
    },
    generateTaskForms : function() {
        Ext.Ajax.request({
            url: ORYX.PATH + "taskforms",
            method: 'POST',
            success: function(request){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'success',
                    msg         : 'Successfully generated process and task form templates.',
                    title       : ''

                });
                var generatedForms = request.responseText.evalJSON();
                for(var newform in generatedForms) {
                    parent.designersignalassetadded(generatedForms[newform].ftluri);
                    parent.designersignalassetadded(generatedForms[newform].formuri);

                    parent.designersignalassetupdate(generatedForms[newform].ftluri);
                    parent.designersignalassetupdate(generatedForms[newform].formuri);
                }
            }.createDelegate(this),
            failure: function(){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : '<p>Failed to generate process and task form templates.</p>',
                    title       : ''
                });
            }.createDelegate(this),
            params: {
                profile: ORYX.PROFILE,
                uuid : ORYX.UUID,
                json : ORYX.EDITOR.getSerializedJSON(),
                ppdata: ORYX.PREPROCESSING
            }
        });


        ORYX.CONFIG.TASKFORMS_URL = function(uuid, profile) {
            if (uuid === undefined) {
                uuid = ORYX.UUID;
            }
            if (profile === undefined) {
                profile = ORYX.PROFILE;
            }
            return ORYX.PATH + "taskforms?uuid="+ uuid + "&profile=" + profile;
        };
    }

});
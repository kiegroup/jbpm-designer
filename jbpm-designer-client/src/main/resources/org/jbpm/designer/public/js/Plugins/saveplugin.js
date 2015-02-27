if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.SavePlugin = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        this.vt;

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name': ORYX.I18N.Save.save,
                'functionality': this.saveWithMessage.bind(this),
                'group': ORYX.I18N.Save.group,
                'icon': ORYX.BASE_FILE_PATH + "images/disk.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.saveDesc,
                'index': 1,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.REPOSITORY_ID != "guvnor" && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });

            this.facade.offer({
                'name': ORYX.I18N.Save.enableAutosave,
                'functionality': this.enableautosave.bind(this),
                'group': ORYX.I18N.Save.group,
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.enableAutosave_desc,
                'index': 2,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return !ORYX.AUTOSAVE_ENABLED && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });

            this.facade.offer({
                'name': ORYX.I18N.Save.disableAutosave,
                'functionality': this.disableautosave.bind(this),
                'group': ORYX.I18N.Save.group,
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.disableAutosave_desc,
                'index': 3,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.AUTOSAVE_ENABLED && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });

            this.facade.offer({
                'name': ORYX.I18N.Save.copy,
                'functionality': this.copyassetnotify.bind(this),
                'group': ORYX.I18N.Save.group,
                'icon': ORYX.BASE_FILE_PATH + "images/page_copy.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.copy_desc,
                'index': 4,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.REPOSITORY_ID != "guvnor" && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });

            this.facade.offer({
                'name': ORYX.I18N.Save.rename,
                'functionality': this.renameassetnotify.bind(this),
                'group': ORYX.I18N.Save.group,
                'icon': ORYX.BASE_FILE_PATH + "images/rename.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.rename_desc,
                'index': 5,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.REPOSITORY_ID != "guvnor" && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });

            this.facade.offer({
                'name': ORYX.I18N.Save.delete_name,
                'functionality': this.deleteassetnotify.bind(this),
                'group': ORYX.I18N.Save.group,
                'icon': ORYX.BASE_FILE_PATH + "images/delete2.gif",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.delete_desc,
                'index': 6,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.REPOSITORY_ID != "guvnor" && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });
        }

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEUP, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_ROLLBACK, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_EXECUTE, this.setUnsaved.bind(this));

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DO_SAVE, this.handleEventDoSave.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_CANCEL_SAVE, this.handleEventCancelSave.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DO_RELOAD, this.handleEventDoRealod.bind(this));

        window.onunload = this.unloadWindow.bind(this);

    },

    setUnsaved: function() {
        ORYX.PROCESS_SAVED = false;
    },

    saveWithMessage: function() {
        // check with presenter if we can save
        var hasConcurrentUpdate = parent.designersignalassetupdate(ORYX.UUID);
        if(hasConcurrentUpdate && hasConcurrentUpdate == true) {
            // let the gwt code handle this from here on....
        } else {
            this.save(true);
        }
    },

    handleEventDoSave: function() {
        this.setUnsaved();
        this.save(true);
    },

    handleEventCancelSave: function() {
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.Save.saveCancelled,
            title       : ''
        });
    },

    handleEventDoRealod: function() {
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.Save.processReloading,
            title       : ''
        });

        new Ajax.Request(ORYX.CONFIG.UUID_URL(), {
            encoding: 'UTF-8',
            method: 'GET',
            onSuccess: function(transport) {
                response = transport.responseText;
                try {
                    if (response.length != 0) {
                        if(response.startsWith("error:")) {
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.Save.unableReloadContent,
                                title       : ''
                            });
                        } else {
                            this.updateProcessOnReload(response.evalJSON());
                        }
                    } else {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.Save.invalidContent,
                            title       : ''
                        });
                    }
                } catch(err) {
                    ORYX.LOG.error(err);
                }
            }.createDelegate(this),
            onFailure: function(transport) {
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.Save.couldNotReload,
                    title       : ''
                });
            }
        });

        ORYX.PROCESS_SAVED = false;
    },

    save : function(showCommit) {
        if(!ORYX.PROCESS_SAVED) {
            // save process bpmn2 and svg
            var commitMessage = "";
            if(showCommit && showCommit == true) {
                commitMessage = prompt("Save this item", "Check in comment");
                if(commitMessage == null) {
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'info',
                        msg         : ORYX.I18N.Save.saveCancelled,
                        title       : ''
                    });
                    return;
                }
            }

            parent.designersignalexpectconcurrentupdate(ORYX.UUID);

            Ext.Ajax.request({
                url: ORYX.PATH + 'assetservice',
                method: 'POST',
                success: function(response) {
                    try {
                        if(response.responseText && response.responseText.length > 0) {
                            var saveResponse = response.responseText.evalJSON();
                            if(saveResponse.errors && saveResponse.errors.length > 0) {
                                var errors = saveResponse.errors;
                                for(var j=0; j < errors.length; j++) {
                                    var errormessageobj = errors[j];
                                    this.facade.raiseEvent({
                                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                        ntype		: 'error',
                                        msg         : errormessageobj.message,
                                        title       : ''
                                    });
                                }
                            } else {
                                this.facade.raiseEvent({
                                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype		: 'success',
                                    msg         : ORYX.I18N.Save.saveSuccess,
                                    title       : '',
                                    timeOut: 1000,
                                    extendedTimeOut: 1000
                                });

                                // set the designer flag
                                ORYX.PROCESS_SAVED = true;

                                if(ORYX.CONFIG.STORESVGONSAVE && ORYX.CONFIG.STORESVGONSAVE == "true") {
                                    // svg save
                                    var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
                                    var rawSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getRootNode().cloneNode(true));
                                    var processJSON = ORYX.EDITOR.getSerializedJSON();
                                    var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
                                    Ext.Ajax.request({
                                        url: ORYX.PATH + "transformer",
                                        method: 'POST',
                                        success: function(request) {
                                            this.facade.raiseEvent({
                                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                                ntype		: 'success',
                                                msg         : ORYX.I18N.Save.saveImageSuccess,
                                                title       : ''
                                            });
                                        }.bind(this),
                                        failure:function(response, opts){
                                            this.facade.raiseEvent({
                                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                                ntype		: 'error',
                                                msg         : ORYX.I18N.Save.saveImageFailed,
                                                title       : ''
                                            });
                                        }.bind(this),
                                        params: {
                                            fsvg: Base64.encode(formattedSvgDOM),
                                            rsvg: Base64.encode(rawSvgDOM),
                                            uuid:  window.btoa(encodeURI(ORYX.UUID)),
                                            profile: ORYX.PROFILE,
                                            transformto: 'svg',
                                            processid: processId
                                        }
                                    });
                                }
                            }
                        } else {
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.Save.unableToSave + ': ' + e,
                                title       : ''
                            });
                        }
                    } catch(e) {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.Save.unableToSave + ': ' + e,
                            title       : ''
                        });
                    }
                }.bind(this),
                failure: function(){
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.Save.unableToSave+'.',
                        title       : ''
                    });
                }.bind(this),
                params: {
                    action: 'updateasset',
                    profile: ORYX.PROFILE,
                    assetcontent: window.btoa(encodeURIComponent(ORYX.EDITOR.getSerializedJSON())),
                    pp: ORYX.PREPROCESSING,
                    assetid: window.btoa(encodeURI(ORYX.UUID)),
                    assetcontenttransform: 'jsontobpmn2',
                    commitmessage: commitMessage,
                    sessionid: ORYX.SESSION_ID
                }
            });
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : ORYX.I18N.Save.noChanges,
                title       : ''
            });
        }
    },

    saveSync : function() {
        if(!ORYX.PROCESS_SAVED) {
            // save process bpmn2 and svg
            var processJSON = ORYX.EDITOR.getSerializedJSON();
            var saveAjaxObj = new XMLHttpRequest;
            var saveURL = ORYX.PATH + "assetservice";
            var saveParams  = "action=updateasset&profile=" + ORYX.PROFILE + "&pp=" + ORYX.PREPROCESSING + "&assetid=" + window.btoa(encodeURI(ORYX.UUID)) + "&assetcontenttransform=jsontobpmn2&assetcontent=" + window.btoa(encodeURIComponent(processJSON));
            saveAjaxObj.open("POST",saveURL,false);
            saveAjaxObj.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            saveAjaxObj.send(saveParams);
            if(saveAjaxObj.status == 200) {
                try {
                    if(saveAjaxObj.responseText && saveAjaxObj.responseText.length > 0) {
                        var saveResponse = saveAjaxObj.responseText.evalJSON();
                        if(saveResponse.errors && saveResponse.errors.lengt > 0) {
                            var errors = saveResponse.errors;
                            for(var j=0; j < errors.length; j++) {
                                var errormessageobj = errors[j];
                                this.facade.raiseEvent({
                                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype		: 'error',
                                    msg         : errormessageobj.message,
                                    title       : ''
                                });
                            }
                        } else {
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'success',
                                msg         : ORYX.I18N.Save.saveSuccess,
                                title       : '',
                                timeOut: 1000,
                                extendedTimeOut: 1000
                            });

                            // set the designer flag
                            ORYX.PROCESS_SAVED = true;
                        }
                    } else {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         :  ORYX.I18N.Save.unableToSave+': ' + e,
                            title       : ''
                        });
                    }
                } catch(e) {
                   // swallow errors for now
                    alert("error : " + e);
                }
            }
        }
    },

    enableautosave: function() {
        ORYX.AUTOSAVE_ENABLED = true;
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
        this.vt = window.setInterval((function(){
            this.save(false);
        }).bind(this), 30000);
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.Save.autosaveEnabled,
            title       : ''
        });
    },

    disableautosave: function() {
        ORYX.AUTOSAVE_ENABLED = false;
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
        window.clearInterval(this.vt);
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.Save.autosaveDisabled,
            title       : ''
        });
    },

    deleteassetnotify: function() {
        Ext.MessageBox.confirm(
            ORYX.I18N.Save.deleteConfirm_title,
            ORYX.I18N.Save.deleteConfirm_msg,
            function(btn){
                if (btn == 'yes') {
                    // send UF asset delete event
                    // to close tab and show UF notication
                    parent.designersignalassetdelete(ORYX.UUID);
                }
            }.bind(this)
        );
    },

    copyassetnotify: function() {
        Ext.MessageBox.confirm(
            ORYX.I18N.Save.copyConfirm_title,
            ORYX.I18N.Save.copyConfirm_msg,
            function(btn){
                if (btn == 'yes') {
                    this.save(true);
                    parent.designersignalassetcopy(ORYX.UUID);
                } else {
                    parent.designersignalassetcopy(ORYX.UUID);
                }
            }.bind(this)
        );
    },

    renameassetnotify: function() {
        if(ORYX.Editor.checkIfSaved()) {
            parent.designersignalassetrename(ORYX.UUID);
        } else {
            Ext.MessageBox.confirm(
                ORYX.I18N.Save.renameConfirm_title,
                ORYX.I18N.Save.renameConfirm_msg,
                function(btn){
                    if (btn == 'yes') {
                        this.save(true);
                        parent.designersignalassetrename(ORYX.UUID);
                    } else {
                        parent.designersignalassetrename(ORYX.UUID);
                    }
                }.bind(this)
            );
        }
    },

    unloadWindow: function() {
        this.saveSync(false);
    },

    clearCanvas: function() {
        ORYX.EDITOR.getCanvas().nodes.each(function(node) {
            ORYX.EDITOR.deleteShape(node);
        }.bind(this));

        ORYX.EDITOR.getCanvas().edges.each(function(edge) {
            ORYX.EDITOR.deleteShape(edge);
        }.bind(this));
    },

    updateProcessOnReload: function( jsonString ){
        if (jsonString) {
            try {
                this.clearCanvas();
                this.facade.importJSON(jsonString);
                ORYX.PROCESS_SAVED = false;
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'success',
                    msg         : ORYX.I18N.Save.reloadSuccess,
                    title       : ''

                });
            } catch(err) {
                this.facade.importJSON(currentJSON);
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.Save.reloadFail,
                    title       : ''

                });
            }
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.Save.processReloadedInvalid,
                title       : ''
            });
        }
    }
});

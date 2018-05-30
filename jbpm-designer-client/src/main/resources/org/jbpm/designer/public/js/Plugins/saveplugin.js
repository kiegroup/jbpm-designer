if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.SavePlugin = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        this.vt;
        this.editorLocked = false;

        if(!(ORYX.READONLY == true || ORYX.VIEWLOCKED == true)) {
//            this.facade.offer({
//                'name': ORYX.I18N.Save.save,
//                'functionality': this.saveWithMessage.bind(this),
//                'group': ORYX.I18N.Save.group,
//                'icon': ORYX.BASE_FILE_PATH + "images/disk.png",
//                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
//                'description': ORYX.I18N.Save.saveDesc,
//                'index': 1,
//                'minShape': 0,
//                'maxShape': 0,
//                'isEnabled': function(){
//                    return ORYX.REPOSITORY_ID != "guvnor" && !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
//    //                profileParamName = "profile";
//    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//    //                regexa = new RegExp( regexSa );
//    //                profileParams = regexa.exec( window.location.href );
//    //                profileParamValue = profileParams[1];
//    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
//                }.bind(this)
//            });

            this.facade.offer({
                'name': ORYX.I18N.Save.enableAutosave,
                'functionality': this.enableautosave.bind(this),
                'group': ORYX.I18N.Save.group,
                'icon': ORYX.BASE_FILE_PATH + "images/disk.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
                'description': ORYX.I18N.Save.enableAutosave_desc,
                'index': 2,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return !ORYX.AUTOSAVE_ENABLED && !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
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
                    return ORYX.AUTOSAVE_ENABLED && !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
                }.bind(this)
            });

//            this.facade.offer({
//                'name': ORYX.I18N.Save.copy,
//                'functionality': this.copyassetnotify.bind(this),
//                'group': ORYX.I18N.Save.group,
//                'icon': ORYX.BASE_FILE_PATH + "images/page_copy.png",
//                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
//                'description': ORYX.I18N.Save.copy_desc,
//                'index': 4,
//                'minShape': 0,
//                'maxShape': 0,
//                'isEnabled': function(){
//                    return ORYX.REPOSITORY_ID != "guvnor" && !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
//    //                profileParamName = "profile";
//    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//    //                regexa = new RegExp( regexSa );
//    //                profileParams = regexa.exec( window.location.href );
//    //                profileParamValue = profileParams[1];
//    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
//                }.bind(this)
//            });

//            this.facade.offer({
//                'name': ORYX.I18N.Save.rename,
//                'functionality': this.renameassetnotify.bind(this),
//                'group': ORYX.I18N.Save.group,
//                'icon': ORYX.BASE_FILE_PATH + "images/rename.png",
//                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
//                'description': ORYX.I18N.Save.rename_desc,
//                'index': 5,
//                'minShape': 0,
//                'maxShape': 0,
//                'isEnabled': function(){
//                    return ORYX.REPOSITORY_ID != "guvnor" && !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
//    //                profileParamName = "profile";
//    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//    //                regexa = new RegExp( regexSa );
//    //                profileParams = regexa.exec( window.location.href );
//    //                profileParamValue = profileParams[1];
//    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
//                }.bind(this)
//            });

//            this.facade.offer({
//                'name': ORYX.I18N.Save.delete_name,
//                'functionality': this.deleteassetnotify.bind(this),
//                'group': ORYX.I18N.Save.group,
//                'icon': ORYX.BASE_FILE_PATH + "images/delete2.gif",
//                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/disk.png",
//                'description': ORYX.I18N.Save.delete_desc,
//                'index': 6,
//                'minShape': 0,
//                'maxShape': 0,
//                'isEnabled': function(){
//                    return ORYX.REPOSITORY_ID != "guvnor" && !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
//    //                profileParamName = "profile";
//    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//    //                regexa = new RegExp( regexSa );
//    //                profileParams = regexa.exec( window.location.href );
//    //                profileParamValue = profileParams[1];
//    //                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
//                }.bind(this)
//            });
        }

        // all events on which we set the process as unsaved
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_ROLLBACK, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_EXECUTE, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAGDROP_END, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_RESIZE_END, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPE_ADDED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPE_CREATED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHAPE_DELETED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAGDOCKER_MOVE_FINISHED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DRAGDOCKER_DOCKED, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DOCKERDRAG, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DOCKER_EVENT, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UPDATE_TASK_TYPE, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PASTE_NOTEMPTY_END, this.setUnsaved.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_KEYBIND_MOVE_FINISHED, this.setUnsaved.bind(this));


        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DO_SAVE, this.handleEventDoSave.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DO_UPDATE, this.handleEventDoUpdate.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DO_CHECKSAVE, this.handleEventDoCheckSave.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_CANCEL_SAVE, this.handleEventCancelSave.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_DO_RELOAD, this.handleEventDoRealod.bind(this));

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.handlerErrorsAndUnknownExporter.bind(this));

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UPDATE_LOCK, this.handleEventUpdateLock.bind(this));



        window.onunload = this.unloadWindow.bind(this);

    },

    handleEventUpdateLock: function() {
        if ( typeof parent.acquireLock === "function" ) {
            if (this.editorLocked && !parent.isLockedByCurrentUser()) {
                this.editorLocked = false;
            } else if (!this.editorLocked && !parent.isLocked()) {
                ORYX.EDITOR.updateViewLockState(true);
            }
        }
    },

    setUnsaved: function() {
        ORYX.PROCESS_SAVED = false;
        
        ORYX.EDITOR.updateViewLockState(true);
                        
        if(!this.editorLocked) {            
            if ( typeof parent.acquireLock === "function" ) {
                if (!parent.isLockedByCurrentUser()) {
                    parent.acquireLock();   
                }                
                this.editorLocked = true;
            }
        }

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

    handleEventDoUpdate: function() {
        this.setUnsaved();
        this.save(false);
    },

    handleEventDoCheckSave : function(options) {
        this.save(true, options.pathuri);
    },

    handleEventCancelSave: function() {
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.Save.saveCancelled,
            title       : ''
        });
    },

    handlerErrorsAndUnknownExporter: function() {
        // errors
        if(ORYX.LOADING_ERRORS == true) {
            Ext.MessageBox.confirm(
                "Unable to open Process",
                "Open Process Sources with the XML Editor?",
                function(btn){
                    if (btn == 'yes') {
                        parent.designeropeninxmleditortab(ORYX.UUID);
                    }
                }.bind(this)
            );
        }
        // reset ORYX.LOADING_ERRORS
        ORYX.LOADING_ERRORS = false;

        // exporter
        try {
            var processJSON = ORYX.EDITOR.getSerializedJSON();
            var processExporter = jsonPath(processJSON.evalJSON(), "$.properties.exporter");
            if (processExporter && processExporter != "jBPM Designer") {
                if (!ORYX.JSON_UPDATED) {
                    this.facade.setSelection(this.facade.getCanvas().getChildShapes(true));
                    var currentJSON = ORYX.EDITOR.getSerializedJSON();
                    var selection = this.facade.getSelection();
                    var clipboard = new ORYX.Plugins.Edit.ClipBoard();
                    clipboard.refresh(selection, this.getAllShapesToConsider(selection, true));
                    var command = new ORYX.Plugins.Edit.DeleteCommand(clipboard, this.facade);
                    this.facade.executeCommands([command]);

                    this.facade.raiseEvent({
                        type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype: 'info',
                        msg: ORYX.I18N.view.exporterUpdate,
                        title: ''

                    });

                    // import updated json
                    this.facade.importJSON(currentJSON);

                    // set as updated
                    ORYX.JSON_UPDATED = true;

                    // deselect nodes on canvas
                    this.facade.setSelection([]);

                    // do not set the process as unsaved
                    ORYX.PROCESS_SAVED = true;

                }
            }
        } catch (err) {
            ORYX.LOG.error(err);
        }

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

    save : function(showCommit, pathuri) {
        var isLatest = parent.designerIsLatest();
        if(!isLatest) {
            ORYX.PROCESS_SAVED = false;
        }
        if(!ORYX.PROCESS_SAVED) {
            // save process bpmn2 and svg
            var usePathURI = "";
            if(pathuri) {
                usePathURI = pathuri;
            }

            var commitMessage = "";
            if(showCommit && showCommit == true) {
                Ext.MessageBox.prompt(
                        ORYX.I18N.Save.saveItem,
                        ORYX.I18N.Save.saveCheckInComment,
                        function(btn, commitMessage){
                            if (btn == 'ok') {
                                this.doSave(usePathURI, commitMessage);
                            } else {
                                this.facade.raiseEvent({
                                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype		: 'info',
                                    msg         : ORYX.I18N.Save.saveCancelled,
                                    title       : ''
                                });
                            }
                        }.bind(this)
                );
            } else {
                this.doSave(usePathURI, commitMessage);
            }
        } else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : ORYX.I18N.Save.noChanges,
                title       : ''
            });
        }
    },

    doSave : function(usePathURI, commitMessage) {
        // turn off validation
        ORYX.IS_VALIDATING_PROCESS = false;
        ORYX.EDITOR._pluginFacade.resetAllShapeColors();

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
                                var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(true));
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
                sessionid: ORYX.SESSION_ID,
                latestpath: usePathURI
            }
        });
    },

    saveSync : function() {
        ORYX.EDITOR.updateViewLockState(false);

        // the view-locked-by-current-user logic is already determined by updateViewLockState so here if viewlocked is true we are sure its locked by some other user and not us
        if(!ORYX.PROCESS_SAVED && ORYX.VIEWLOCKED != true) {
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
        this.saveSync();
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
    },

    getAllShapesToConsider: function(shapes, considerConnections){

        var shapesToConsider = []; // only top-level shapes
        var childShapesToConsider = []; // all child shapes of top-level shapes

        shapes.each(function(shape){
            //Throw away these shapes which have a parent in given shapes
            isChildShapeOfAnother = shapes.any(function(s2){
                return s2.hasChildShape(shape);
            });
            if(isChildShapeOfAnother) return;

            // This shape should be considered
            shapesToConsider.push(shape);
            // Consider attached nodes (e.g. intermediate events)
            if (shape instanceof ORYX.Core.Node) {
                var attached = shape.getOutgoingNodes();
                attached = attached.findAll(function(a){ return !shapes.include(a) });
                shapesToConsider = shapesToConsider.concat(attached);
            }
            childShapesToConsider = childShapesToConsider.concat(shape.getChildShapes(true));


            if (considerConnections && !(shape instanceof ORYX.Core.Edge)){
                //concat all incoming and outgoing shapes
                var connections = shape.getIncomingShapes().concat(shape.getOutgoingShapes());

                connections.each(function(s) {
                    //we don't want to delete sequence flows with
                    //an existing 'conditionexpression'
                    //console.log(s);
                    if (s instanceof ORYX.Core.Edge && s.properties["oryx-conditionexpression"] && s.properties["oryx-conditionexpression"] != ""){
                        return;
                    }
                    shapesToConsider.push(s);
                }.bind(this));

            }

        }.bind(this));

        // All edges between considered child shapes should be considered
        // Look for these edges having incoming and outgoing in childShapesToConsider
        var edgesToConsider = this.facade.getCanvas().getChildEdges().select(function(edge){
            // Ignore if already added
            if(shapesToConsider.include(edge)) return false;
            // Ignore if there are no docked shapes
            if(edge.getAllDockedShapes().size() === 0) return false;
            // True if all docked shapes are in considered child shapes
            return edge.getAllDockedShapes().all(function(shape){
                // Remember: Edges can have other edges on outgoing, that is why edges must not be included in childShapesToConsider
                return shape instanceof ORYX.Core.Edge || childShapesToConsider.include(shape);
            });
        });
        shapesToConsider = shapesToConsider.concat(edgesToConsider);

        return shapesToConsider;
    }
});

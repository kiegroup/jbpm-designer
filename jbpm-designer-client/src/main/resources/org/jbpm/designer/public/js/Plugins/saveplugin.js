if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.SavePlugin = Clazz.extend({
    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': ORYX.I18N.Save.save,
            'functionality': this.save.bind(this),
            'group': ORYX.I18N.Save.group,
            'icon': ORYX.BASE_FILE_PATH + "images/disk.png",
            'description': ORYX.I18N.Save.saveDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                return ORYX.REPOSITORY_ID != "guvnor";
//                profileParamName = "profile";
//                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
//                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
//                regexa = new RegExp( regexSa );
//                profileParams = regexa.exec( window.location.href );
//                profileParamValue = profileParams[1];
//                return profileParamValue == "jbpm" && ORYX.REPOSITORY_ID != "guvnor";
            }.bind(this)
        });
    },
    save : function() {
        // save process bpmn2 and svg
        Ext.Ajax.request({
            url: ORYX.PATH + 'assetservice',
            method: 'POST',
            success: function(response) {
                try {
                    if(response.responseText && response.responseText.length > 0) {
                        var saveResponse = response.responseText.evalJSON();
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
                                msg         : 'Successfully saved business process',
                                title       : ''
                            });
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
                                        msg         : 'Successfully saved business process image',
                                        title       : ''
                                    });
                                }.bind(this),
                                failure: function(){
                                    this.facade.raiseEvent({
                                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                        ntype		: 'error',
                                        msg         : 'Unable to save business process image.',
                                        title       : ''
                                    });
                                }.bind(this),
                                params: {
                                    fsvg: formattedSvgDOM,
                                    rsvg: rawSvgDOM,
                                    uuid: ORYX.UUID,
                                    profile: ORYX.PROFILE,
                                    transformto: 'svg',
                                    processid: processId
                                }
                            });
                        }
                    } else {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : 'Unable to save: ' + e,
                            title       : ''
                        });
                    }
                } catch(e) {
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : 'Unable to save: ' + e,
                        title       : ''
                    });
                }
            }.bind(this),
            failure: function(){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : 'Unable to save.',
                    title       : ''
                });
            }.bind(this),
            params: {
                action: 'updateasset',
                profile: ORYX.PROFILE,
                assetcontent: ORYX.EDITOR.getSerializedJSON(),
                pp: ORYX.PREPROCESSING,
                assetid: ORYX.UUID,
                assetcontenttransform: 'jsontobpmn2'
            }
        });
    }
});
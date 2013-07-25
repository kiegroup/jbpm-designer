        ORYX.VERSION = "$designerversion$";

        ORYX.CONFIG.SSET = "$stencilset$";
        ORYX.CONFIG.DEV = $debug$;
        if (ORYX.CONFIG.DEV) {
            ORYX_LOGLEVEL = 4;
        }

        ORYX.PREPROCESSING = "$preprocessing$";
        ORYX.EXTERNAL_PROTOCOL = "$externalprotocol$";
        ORYX.EXTERNAL_HOST = "$externalhost$";
        ORYX.EXTERNAL_SUBDOMAIN = "$externalsubdomain$";
        ORYX.REPOSITORY_ID = "$repositoryid$";

        ORYX.UUID = "$editoruuid$";
        ORYX.PROFILE = "$editorprofile$";
        ORYX.EDITORID = "$editorid$";
        ORYX.ACTIVENODES = $activenodes$;
        ORYX.COMPLETEDNODES = $completednodes$;
        ORYX.READONLY = $readonly$;

        ORYX.LOCAL_HISTORY_ENABLED = $localhistoryenabled$;
        ORYX.LOCAL_HISTORY_TIMEOUT = $localhistorytimeout$;

        ORYX.CONFIG.ROOT_PATH = "$contextroot$";
        ORYX.PATH = ORYX.CONFIG.ROOT_PATH + "/";
        ORYX.BASE_FILE_PATH = ORYX.CONFIG.ROOT_PATH + "/org.jbpm.designer.jBPMDesigner/";

        ORYX.PROCESS_SAVED = true;
        ORYX.AUTOSAVE_ENABLED = false;
        ORYX.CONFIG.STORESVGONSAVE = "$storesvgonsave$";



        ORYX.CONFIG.SSEXTS = [$ssextensions$].
        map(function (ssext) {
            // for each of the extensions, we get the extension file and return its contents.
            var contents = null;
            new Ajax.Request(ORYX.CONFIG.ROOT_PATH + "/stencilset/" + ssext, {
                asynchronous:false,
                method:'get',
                contentType:'application/json',
                onSuccess:function (result) {
                    contents = result.responseText.evalJSON();
                },
                onFailure:function (result) {
                    ORYX.LOG.error("Could not load Process Designer");
                }
            });
            return contents;
        });

        new Ajax.Request(ORYX.CONFIG.ROOT_PATH + "/plugins", {
            asynchronous:false,
            method:'get',
            contentType:'application/json',
            onSuccess:function (result) {
                var allPlugins = {};
                result.responseText.evalJSON().each(function (p) {
                    allPlugins[p.name] = p;
                }.bind(allPlugins));
                // install the current plugins
                ORYX.availablePlugins = [];
                [$profileplugins$].
                each(function (pluginName) {
                    p = allPlugins[pluginName];
                    if (p) {
                        ORYX.availablePlugins.push(p);
                    } else {
                        ORYX.Log.error("missing plugin " + pluginName);
                    }
                }.bind(allPlugins));

            },
            onFailure:function (result) {
                ORYX.LOG.error("Could not load Process Designer: " + result.responseText);
            }
        });

       function startEditorInstance() {
           var stencilset = ORYX.Utils.getParamFromUrl('stencilset') || ORYX.CONFIG.SSET;
           var editor_parameters = {
               fullscreen: true,
               stencilset: {
                   url: stencilset
               }
           };

           if(typeof parent.designerprocesssources == 'function') {
               var dps = parent.designerprocesssources();
               parent.designerprocesssources = function (){ return ""; }
               if(dps && dps.length > 0) {
                    new Ajax.Request(ORYX.PATH + "transformer", {
                                   encoding: 'UTF-8',
                                   method: 'POST',
                                   parameters: {uuid: ORYX.UUID, profile: ORYX.PROFILE, pp: ORYX.PREPROCESSING, bpmn2: dps, transformto: 'bpmn2json', enc: 'true'},
                                   onSuccess: function(transport) {
                                       response = transport.responseText;
                                       model = response.evalJSON();
                                       if (model) {
                                           try {
                                               editor_parameters.model = model;
                                               ORYX.EDITOR = new ORYX.Editor(editor_parameters);
                                           } catch(err) {
                                               ORYX.LOG.error(err);
                                           }
                                       }

                                   },
                                   onFailure: function(transport) {
                                       ORYX.LOG.error("Could not load the model");
                                   }
                     });
               } else {
                    if(!(ORYX.UUID === undefined)) {
                       //load the model from the repository from its uuid
                       new Ajax.Request(ORYX.CONFIG.UUID_URL(), {
                           encoding: 'UTF-8',
                           method: 'GET',
                           onSuccess: function(transport) {
                               response = transport.responseText;
                               if (response.length != 0) {
                                   try {
                                       model = response.evalJSON();
                                       editor_parameters.model = model;
                                       ORYX.EDITOR = new ORYX.Editor(editor_parameters);
                                   } catch(err) {
                                       ORYX.LOG.error(err);
                                   }
                               }

                           },
                           onFailure: function(transport) {
                               ORYX.LOG.error("Could not load the model for uuid " + ORYX.UUID);
                           }
                       });
                   }
               }
           } else {
               if(!(ORYX.UUID === undefined)) {
                   //load the model from the repository from its uuid
                   new Ajax.Request(ORYX.CONFIG.UUID_URL(), {
                       encoding: 'UTF-8',
                       method: 'GET',
                       onSuccess: function(transport) {
                           response = transport.responseText;
                           if (response.length != 0) {
                               try {
                                   model = response.evalJSON();
                                   editor_parameters.model = model;
                                   ORYX.EDITOR = new ORYX.Editor(editor_parameters);
                               } catch(err) {
                                   ORYX.LOG.error(err);
                               }
                           }

                       },
                       onFailure: function(transport) {
                           ORYX.LOG.error("Could not load the model for uuid " + ORYX.UUID);
                       }
                   });
               }
           }
       }

    function startit() {
        startEditorInstance();
        Kickstart.load();
    }

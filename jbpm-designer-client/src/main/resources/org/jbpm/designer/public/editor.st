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
        ORYX.ACTIVENODES;
        ORYX.COMPLETEDNODES;
        ORYX.READONLY = $readonly$;

        ORYX.LOCAL_HISTORY_ENABLED = $localhistoryenabled$;
        ORYX.LOCAL_HISTORY_TIMEOUT = $localhistorytimeout$;

        ORYX.CONFIG.ROOT_PATH = "$contextroot$";
        ORYX.PATH = ORYX.CONFIG.ROOT_PATH + "/";
        ORYX.BASE_FILE_PATH = ORYX.CONFIG.ROOT_PATH + "/org.jbpm.designer.jBPMDesigner/";

        ORYX.PROCESS_SAVED = true;
        ORYX.AUTOSAVE_ENABLED = false;
        ORYX.CONFIG.STORESVGONSAVE = "$storesvgonsave$";
        ORYX.PRESET_PERSPECTIVE = "$presetperspective$";

        ORYX.DEFAULT_JSON_MODEL = {"resourceId":"Definition","properties":{"namespaces":"","name":"","expressionlanguage":"http://www.mvel.org/2.0","executable":"true","package":"org.jbpm","vardefs":"","adhocprocess":"false","imports":"","globals":"","id":"defaultprocess","version":"1.0","timeunit":"min","currency":"","targetnamespace":"http://www.omg.org/bpmn20","typelanguage":"http://www.java.com/javaTypes","processn":"","documentation":""},"stencil":{"id":"BPMNDiagram"},"childShapes":[],"bounds":{"lowerRight":{"x":3000,"y":2000},"upperLeft":{"x":0,"y":0}},"stencilset":{"url":"/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json","namespace":"http://b3mn.org/stencilset/bpmn2.0#"},"ssextensions":[]};



        ORYX.CONFIG.SSEXTS = [$ssextensions$].
        map(function (ssext) {
            // for each of the extensions, we get the extension file and return its contents.
            var contents = null;
            new Ajax.Request(ORYX.CONFIG.ROOT_PATH + "/stencilset/" + ssext, {
                parameters: {ts:new Date().getTime()},
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
            parameters: {timestamp:new Date().getTime()},
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

           if(typeof parent.designeractivenodes == 'function') {
              try {
                ORYX.ACTIVENODES = parent.designeractivenodes().evalJSON();
                parent.designeractivenodes = function (){ return ""; }
              } catch(err) {
                parent.designersignalcloseplace();
              }
           }

           if(typeof parent.designercompletednodes == 'function') {
             try {
                ORYX.COMPLETEDNODES = parent.designercompletednodes().evalJSON();
                parent.designercompletednodes = function (){ return ""; }
             } catch(err) {
                parent.designersignalcloseplace();
             }
           }

           if(typeof parent.designerprocesssources == 'function') {
               var dps = parent.designerprocesssources();
               parent.designerprocesssources = function (){ return ""; }
               if(dps && dps.length > 0) {
                    ORYX.PREPROCESSING = "ReadOnlyService";
                    new Ajax.Request(ORYX.PATH + "transformer", {
                                   encoding: 'UTF-8',
                                   method: 'POST',
                                   parameters: {uuid: ORYX.UUID, profile: ORYX.PROFILE, pp: ORYX.PREPROCESSING, bpmn2: dps, transformto: 'bpmn2json', enc: 'true', convertservicetasks: 'true', ts: new Date().getTime()},
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
                               try {
                                   if (response.length != 0) {
                                        if(response.startsWith("error:")) {
                                            editor_parameters.error = response.substr(7, response.length);
                                            model = ORYX.DEFAULT_JSON_MODEL;
                                            editor_parameters.model = model;
                                        } else {
                                            model = response.evalJSON();
                                            editor_parameters.model = model;
                                        }
                                   } else {
                                        editor_parameters.error = "Server returned no response.";
                                        model = ORYX.DEFAULT_JSON_MODEL;
                                        editor_parameters.model = model;
                                   }
                                   ORYX.EDITOR = new ORYX.Editor(editor_parameters);
                               } catch(err) {
                                    ORYX.LOG.error(err);
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
                          try {
                              if (response.length != 0) {
                                   if(response.startsWith("error:")) {
                                       editor_parameters.error = response.substr(7, response.length);
                                       model = ORYX.DEFAULT_JSON_MODEL;
                                       editor_parameters.model = model;
                                   } else {
                                       model = response.evalJSON();
                                       editor_parameters.model = model;
                                   }
                              } else {
                                   editor_parameters.error = "Server returned no response.";
                                   model = ORYX.DEFAULT_JSON_MODEL;
                                   editor_parameters.model = model;
                              }
                              ORYX.EDITOR = new ORYX.Editor(editor_parameters);
                          } catch(err) {
                               ORYX.LOG.error(err);
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

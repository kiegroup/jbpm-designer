if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.LocalHistory = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        this.historyEntry;
        this.historyProxy;
        this.historyStore;
        this.storage;
        this.fail;
        this.uid;
        this.historyInterval;
        this.mygrid;

        if(this.haveSupportForLocalHistory()) {
            this.setupAndLoadHistoryData();
            this.enableLocalHistory();
            //this.startStoring();
        }

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name': ORYX.I18N.LocalHistory.display,
                'functionality': this.displayLocalHistory.bind(this),
                'group': "localstorage",
                'icon': ORYX.BASE_FILE_PATH + "images/view.png",
                 dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/localhistory.png",
                'description': ORYX.I18N.LocalHistory.display_desc,
                'index': 1,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.LOCAL_HISTORY_ENABLED && ORYX.READONLY != true;
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
                'name': ORYX.I18N.LocalHistory.clear,
                'functionality': this.clearLocalHistory.bind(this),
                'group': "localstorage",
                'icon': ORYX.BASE_FILE_PATH + "images/clear.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/localhistory.png",
                'description': ORYX.I18N.LocalHistory.clear_desc,
                'index': 2,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.LOCAL_HISTORY_ENABLED && ORYX.READONLY != true;
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
                'name': ORYX.I18N.LocalHistory.config,
                'functionality': this.configureSnapshotInterval.bind(this),
                'group': "localstorage",
                'icon': ORYX.BASE_FILE_PATH + "images/clock.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/localhistory.png",
                'description': ORYX.I18N.LocalHistory.config_desc,
                'index': 3,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.LOCAL_HISTORY_ENABLED && ORYX.READONLY != true;
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
                'name': ORYX.I18N.LocalHistory.enable,
                'functionality': this.enableLocalHistory.bind(this),
                'group': "localstorage",
                'icon': ORYX.BASE_FILE_PATH + "images/enable.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/localhistory.png",
                'description': ORYX.I18N.LocalHistory.enable_desc,
                'index': 3,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return !ORYX.LOCAL_HISTORY_ENABLED && ORYX.READONLY != true;
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
                'name': ORYX.I18N.LocalHistory.disable,
                'functionality': this.disableLocalHistory.bind(this),
                'group': "localstorage",
                'icon': ORYX.BASE_FILE_PATH + "images/disable.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/localhistory.png",
                'description': ORYX.I18N.LocalHistory.disable_desc,
                'index': 4,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.LOCAL_HISTORY_ENABLED && ORYX.READONLY != true;
    //                profileParamName = "profile";
    //                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //                regexa = new RegExp( regexSa );
    //                profileParams = regexa.exec( window.location.href );
    //                profileParamValue = profileParams[1];
    //                return profileParamValue == "jbpm" && ORYX.LOCAL_HISTORY_ENABLED;
                }.bind(this)
            });
        }

        window.onbeforeunload = function(){
            this.stopStoring();
        }.bind(this);
    },
    displayLocalHistory : function() {
        var gridId = Ext.id();
        this.mygrid = new Ext.grid.EditorGridPanel({
            autoScroll: true,
            autoHeight: true,
            store: this.historyStore,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),
            {
                id: 'pid',
                header: ORYX.I18N.LocalHistory.headertxt.id,
                width: 100,
                dataIndex: 'processid',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'pname',
                header: ORYX.I18N.LocalHistory.headertxt.name,
                width: 100,
                dataIndex: 'processname',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'ppkg',
                header: ORYX.I18N.LocalHistory.headertxt.Package,
                width: 100,
                dataIndex: 'processpkg',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'pver',
                header: ORYX.I18N.LocalHistory.headertxt.Version,
                width: 100,
                dataIndex: 'processversion',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'tms',
                header: ORYX.I18N.LocalHistory.headertxt.TimeStamp,
                width: 200,
                dataIndex: 'timestamp',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },{
                id: 'pim',
                header: ORYX.I18N.LocalHistory.headertxt.ProcessImage,
                width: 150,
                dataIndex: 'svg',
                renderer: function(val) {
                    if(val && val.length > 0) {
                        return '<center><img src="'+ ORYX.BASE_FILE_PATH +'images/page_white_picture.png" onclick="resetSVGView(\''+val+'\');new SVGViewer({title: \'Local History Process Image\', width: \'650\', height: \'450\', autoScroll: true, fixedcenter: true, src: \''+'\',hideAction: \'close\'}).show();" alt="Click to view Process Image"/></center>';
                    } else {
                        return ORYX.I18N.LocalHistory.headertxt.ProcessImage_NoAvailable;
                    }
                    return "";
                }
            }])
        });

        var localHistoryPanel = new Ext.Panel({
            id: 'localHistoryPanel',
            title: ORYX.I18N.LocalHistory.localHistoryPanel.title,
            layout:'column',
            items:[
                this.mygrid
            ],
            layoutConfig: {
                columns: 1
            },
            defaults: {
                columnWidth: 1.0
            }
        });

        var dialog = new Ext.Window({
            layout		: 'anchor',
            autoCreate	: true,
            title		: ORYX.I18N.LocalHistory.LocalHistoryView.title,
            height		: 350,
            width		: 780,
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
            items		:[localHistoryPanel],
            listeners	:{
                hide: function(){
                    dialog.destroy();
                }.bind(this)
            },
            buttons		: [{
                text: ORYX.I18N.LocalHistory.LocalHistoryView.restore,
                handler: function(){
                    if(this.mygrid.getSelectionModel().getSelectedCell() != null) {
                        var selectedIndex = this.mygrid.getSelectionModel().getSelectedCell()[0];
                        var outValue = this.historyStore.getAt(selectedIndex).data['json'];
                        if(outValue && outValue.length > 0) {
                            outValue = Base64.decode(outValue);
                            this.clearCanvas();
                            var outObj = outValue.evalJSON();
                            this.facade.importJSON(outObj);
                        } else {
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.LocalHistory.LocalHistoryView.invalidProcessInfo,
                                title       : ''
                            });
                        }
                        dialog.hide()
                    } else {
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'info',
                            msg         : ORYX.I18N.LocalHistory.LocalHistoryView.msg,
                            title       : ''
                        });
                    }
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
                    dialog.hide()
                }.bind(this)
            }]
        });

        dialog.show();
        this.mygrid.render();
        this.mygrid.focus( false, 100 );
    },
    setupAndLoadHistoryData : function() {
        this.historyEntry = Ext.data.Record.create(
            [
                {name: 'processid' },
                {name: 'processname' },
                {name: 'processpkg'},
                {name: 'processversion'},
                {name: 'timestamp'},
                {name: 'json' },
                {name: 'svg' }
            ]
        );
        this.historyProxy = new Ext.data.MemoryProxy({root: []});
        this.historyStore = new Ext.data.Store({
            autoDestroy: false,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, this.historyEntry),
            proxy: this.historyProxy
        });
        this.historyStore.load();
        if(this.storage) {
            var processJSON = ORYX.EDITOR.getSerializedJSON();
            var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
            var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
            var processHistory = this.storage.getItem(processPackage + "_" + processId);
            if(processHistory) {
                var history = processHistory.evalJSON();
                for (var i = 0; i < history.length; i ++) {
                    var item = history[i];
                    this.addToStore(item);
                }
            }
        }
    },
    addToStore : function(item) {
        if(this.historyStore.data.length > 0) {
            if(this.historyStore.getAt(0).data['json'] != item.json) {
                this.historyStore.insert(0, new this.historyEntry({
                    processid: item.processid,
                    processname: item.processname,
                    processpkg: item.processpkg,
                    processversion: item.processversion,
                    timestamp: new Date(item.timestamp).format("d.m.Y H:i:s"),
                    json: item.json,
                    svg:  item.svg
                }));
                this.historyStore.commitChanges();
                if(this.mygrid) {
                    this.mygrid.getView().refresh(false);
                }
            }
        } else {
            this.historyStore.insert(0, new this.historyEntry({
                processid: item.processid,
                processname: item.processname,
                processpkg: item.processpkg,
                processversion: item.processversion,
                timestamp: new Date(item.timestamp).format("d.m.Y H:i:s"),
                json: item.json,
                svg:  item.svg
            }));
            this.historyStore.commitChanges();
        }
    },
    clearLocalHistory : function() {
        this.historyStore.removeAll();
        this.historyStore.commitChanges();
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
        this.storage.removeItem(processPackage + "_" + processId);
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.LocalHistory.clearLocalHistory.msg,
            title       : ''
        });
    },
    enableLocalHistory : function() {
        this.setupAndLoadHistoryData();
    },
    haveSupportForLocalHistory : function() {
        try {
            this.uid = new Date;
            (this.storage = window.localStorage).setItem(this.uid, this.uid);
            this.fail = this.storage.getItem(this.uid) != this.uid;
            this.storage.removeItem(this.uid);
            this.fail && (this.storage = false);
        } catch(e) {}

        var localHistoryCookieVal = this._readCookie("designerlocalhistory");
        var enabledFromCookie = false;
        if(localHistoryCookieVal != null && localHistoryCookieVal == "true") {
            enabledFromCookie = true;
            return this.storage && enabledFromCookie;
        }
        return this.storage && ORYX.LOCAL_HISTORY_ENABLED;
    },
    addToHistory : function() {
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
        var processName = jsonPath(processJSON.evalJSON(), "$.properties.processn");
        var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var processVersion = jsonPath(processJSON.evalJSON(), "$.properties.version");

        var item = {
            processid: processId,
            processname: processName,
            processpkg: processPackage,
            processversion: processVersion,
            timestamp: new Date().getTime(),
            json: Base64.encode(processJSON),
            svg: Base64.encode(formattedSvgDOM)
        };

        try {
            var processHistory = this.storage.getItem(processPackage + "_" + processId);
            if(processHistory) {
                var pobject = processHistory.evalJSON();
                pobject.push(item);
                this.storage.setItem(processPackage + "_" + processId, eval(JSON.stringify(pobject)));
            } else {
                var addArray = new Array();
                addArray.push(item);
                this.storage.setItem(processPackage + "_" + processId, eval(JSON.stringify(addArray)));
            }
            this.addToStore(item);
        } catch (e) {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : ORYX.I18N.LocalHistory.addQuotaexceed,
                title       : ''
            });
            this.clearLocalHistory();

        }
    },
    clearCanvas: function() {
        ORYX.EDITOR.getCanvas().nodes.each(function(node) {
            ORYX.EDITOR.deleteShape(node);
        }.bind(this));

        ORYX.EDITOR.getCanvas().edges.each(function(edge) {
            ORYX.EDITOR.deleteShape(edge);
        }.bind(this));
    },
    disableLocalHistory: function() {
        ORYX.LOCAL_HISTORY_ENABLED = false;
        this._createCookie("designerlocalhistory", "false", 365);
        this.stopStoring();
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.LocalHistory.historyDisabled,
            title       : ''
        });
    },
    enableLocalHistory: function() {
        ORYX.LOCAL_HISTORY_ENABLED = true;
        this._createCookie("designerlocalhistory", "true", 365);
        this.setupAndLoadHistoryData();
        this.startStoring();
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.LocalHistory.historyEnabled,
            title       : ''
        });
    },
    startStoring: function() {
        this.historyInterval = setInterval(this.addToHistory.bind(this), ORYX.LOCAL_HISTORY_TIMEOUT);
    },
    stopStoring: function() {
        clearInterval(this.historyInterval);
    },
    _createCookie: function(name, value, days) {
        if (days) {
            var date = new Date();
            date.setTime(date.getTime()+(days*24*60*60*1000));
            var expires = "; expires="+date.toGMTString();
        }
        else {
            var expires = "";
        }

        document.cookie = name+"="+value+expires+"; path=/";
    },
    _readCookie: function(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return null;
    },
    configureSnapshotInterval : function() {
        var siform = new Ext.form.FormPanel({
            baseCls: 		'x-plain',
            labelWidth: 	150,
            defaultType: 	'numberfield',
            items: [
                {
                    fieldLabel: ORYX.I18N.View.sim.interval,
                    name: 'interval',
                    allowBlank:false,
                    allowDecimals:false,
                    minValue:1,
                    width: 120
                },
                {
                    xtype: 'combo',
                    name: 'intervalunits',
                    store: new Ext.data.SimpleStore({
                        fields: ['units','value'],
                        data: [['millisecond',ORYX.I18N.LocalHistory.unitsMillisecond],
                               ['seconds',ORYX.I18N.LocalHistory.unitsSeconds],
                               ['minutes',ORYX.I18N.LocalHistory.unitsMinutes],
                               ['hours',ORYX.I18N.LocalHistory.unitsHours],
                               ['days',ORYX.I18N.LocalHistory.unitsDays]]
                    }),
                    allowBlank: false,
                    displayField: 'value',
                    valueField: 'units',
                    mode: 'local',
                    typeAhead: true,
                    value: "minutes",
                    triggerAction: 'all',
                    fieldLabel: ORYX.I18N.LocalHistory.intervalUnits,
                    width: 120
                }
            ]
        });

        var dialog = new Ext.Window({
            autoCreate: true,
            layout: 	'fit',
            plain:		true,
            bodyStyle: 	'padding:5px;',
            title: 		ORYX.I18N.LocalHistory.ConfigureSnapshotInterval,
            height: 	300,
            width:		350,
            modal:		true,
            fixedcenter:true,
            shadow:		true,
            proxyDrag: 	true,
            resizable:	true,
            items: 		[siform],
            buttons:[
                {
                    text:ORYX.I18N.LocalHistory.set,
                    handler:function(){
                        dialog.hide();
                        var intervalInput = siform.items.items[0].getValue();
                        var intervalUnit = siform.items.items[1].getValue();
                        if(intervalInput && intervalUnit && intervalInput > 0) {
                            if(intervalUnit == "seconds") {
                                intervalInput = intervalInput*1000;
                            } else if(intervalUnit == "minutes") {
                                intervalInput = intervalInput*1000*60;
                            } else if(intervalUnit == "hours") {
                                intervalInput = intervalInput*1000*60*60;
                            } else if(intervalUnit == "days") {
                                intervalInput = intervalInput*1000*60*60*24;
                            } else {
                                // default to milliseconds
                            }
                            this.stopStoring();
                            ORYX.LOCAL_HISTORY_TIMEOUT = intervalInput;
                            this.startStoring();
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'info',
                                msg         : ORYX.I18N.LocalHistory.UpdatedSnapshotInterval,
                                title       : ''
                            });
                        } else {
                            this.facade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.LocalHistory.InvalidInput,
                                title       : ''
                            });
                        }

                    }.bind(this)
                },{
                    text: ORYX.I18N.Save.close,
                    handler:function(){
                        dialog.hide();
                    }.bind(this)
                }
            ]
        });
        dialog.on('hide', function(){
            dialog.destroy(true);
            delete dialog;
        });
        dialog.show();

    }
});

var Base64 = {
    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    encode: function(input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output + this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output;
    },


    decode: function(input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        }

        output = Base64._utf8_decode(output);

        return output;

    },

    _utf8_encode: function(string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

    _utf8_decode: function(utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while (i < utftext.length) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }

}
function resetSVGView(encodedVal) {
    ORYX.EDITOR.localStorageSVG = Base64.decode(encodedVal);
}

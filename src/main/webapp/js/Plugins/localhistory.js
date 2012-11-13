if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

function updateHistorySVG() {
    document.getElementById('svgviewerid').innerHTML = ORYX.EDITOR.localStorageSVG;
}

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

        if(this.haveSupportForLocalHistory()) {
            this.setupAndLoadHistoryData();
            this.startStoring();
        }

        this.facade.offer({
            'name': "Display Local History",
            'functionality': this.displayLocalHistory.bind(this),
            'group': "localstorage",
            'icon': ORYX.PATH + "images/view.png",
             dropDownGroupIcon : ORYX.PATH + "images/localhistory.png",
            'description': "Display Local History",
            'index': 1,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                profileParamName = "profile";
                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
                regexa = new RegExp( regexSa );
                profileParams = regexa.exec( window.location.href );
                profileParamValue = profileParams[1];
                return profileParamValue == "jbpm" && ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });

        this.facade.offer({
            'name': "Clear Local History",
            'functionality': this.clearLocalHistory.bind(this),
            'group': "localstorage",
            'icon': ORYX.PATH + "images/clear.png",
            dropDownGroupIcon : ORYX.PATH + "images/localhistory.png",
            'description': "Clear Local History",
            'index': 2,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                profileParamName = "profile";
                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
                regexa = new RegExp( regexSa );
                profileParams = regexa.exec( window.location.href );
                profileParamValue = profileParams[1];
                return profileParamValue == "jbpm" && ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });

        this.facade.offer({
            'name': "Enable Local History",
            'functionality': this.enableLocalHistory.bind(this),
            'group': "localstorage",
            'icon': ORYX.PATH + "images/enable.png",
            dropDownGroupIcon : ORYX.PATH + "images/localhistory.png",
            'description': "Enable Local History",
            'index': 3,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                profileParamName = "profile";
                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
                regexa = new RegExp( regexSa );
                profileParams = regexa.exec( window.location.href );
                profileParamValue = profileParams[1];
                return profileParamValue == "jbpm" && !ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });

        this.facade.offer({
            'name': "Disable Local History",
            'functionality': this.disableLocalHistory.bind(this),
            'group': "localstorage",
            'icon': ORYX.PATH + "images/disable.png",
            dropDownGroupIcon : ORYX.PATH + "images/localhistory.png",
            'description': "Disable Local History",
            'index': 4,
            'minShape': 0,
            'maxShape': 0,
            'isEnabled': function(){
                profileParamName = "profile";
                profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
                regexa = new RegExp( regexSa );
                profileParams = regexa.exec( window.location.href );
                profileParamValue = profileParams[1];
                return profileParamValue == "jbpm" && ORYX.LOCAL_HISTORY_ENABLED;
            }.bind(this)
        });

        window.onbeforeunload = function(){
            this.stopStoring();
        }.bind(this);
    },
    displayLocalHistory : function() {
        var gridId = Ext.id();
        var grid = new Ext.grid.EditorGridPanel({
            autoScroll: true,
            autoHeight: true,
            store: this.historyStore,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(),
            {
                id: 'pid',
                header: 'Id',
                width: 100,
                dataIndex: 'processid',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'pname',
                header: 'Name',
                width: 100,
                dataIndex: 'processname',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'ppkg',
                header: 'Package',
                width: 100,
                dataIndex: 'processpkg',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'pver',
                header: 'Version',
                width: 100,
                dataIndex: 'processversion',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },
            {
                id: 'tms',
                header: 'Time Stamp',
                width: 200,
                dataIndex: 'timestamp',
                editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
            },{
                id: 'pim',
                header: 'Process Image',
                width: 150,
                dataIndex: 'svg',
                renderer: function(val) {
                    if(val && val.length > 0) {
                        ORYX.EDITOR.localStorageSVG = val;
                        return '<center><img src="'+ORYX.PATH+'images/page_white_picture.png" onclick="new SVGViewer({title: \'Local History Process Image\', width: \'650\', height: \'450\', autoScroll: true, fixedcenter: true, src: \''+'\',hideAction: \'close\'}).show(); updateHistorySVG();" alt="Click to view Process Image"/></center>';
                    } else {
                        return "<center>Process image not available.</center>";
                    }
                    return "";
                }
            }])
        });

        var localHistoryPanel = new Ext.Panel({
            id: 'localHistoryPanel',
            title: '<center>Select Process Id and click "Restore" to restore.</center>',
            layout:'column',
            items:[
                grid
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
            title		: 'Local History View',
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
                text: 'Restore',
                handler: function(){
                    if(grid.getSelectionModel().getSelectedCell() != null) {
                        var selectedIndex = grid.getSelectionModel().getSelectedCell()[0];
                        var outValue = this.historyStore.getAt(selectedIndex).data['json'];
                        if(outValue && outValue.length > 0) {
                            this.clearCanvas();
                            var outObj = outValue.evalJSON();
                            this.facade.importJSON(outObj);
                        } else {
                            Ext.Msg.minWidth = 400;
                            Ext.Msg.alert("Invalid Process info. Unable to restore.");
                        }
                        dialog.hide()
                    } else {
                        Ext.Msg.alert('Please select a process id');
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
        grid.render();
        grid.focus( false, 100 );
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
            autoDestroy: true,
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
    },
    clearLocalHistory : function() {
        this.historyStore.removeAll();
        this.historyStore.commitChanges();
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        this.storage.removeItem(processId);
        Ext.Msg.minWidth = 400;
        Ext.Msg.alert("Local History has been cleared.");
    },
    enableLocalHistory : function() {
        this.setupAndLoadHistoryData();
        Ext.Msg.minWidth = 400;
        Ext.Msg.alert("Local History has been enabled.");
    },
    disableLocalHistory : function() {
        Ext.Msg.minWidth = 400;
        Ext.Msg.alert("Local History has been disabled.");
    },
    haveSupportForLocalHistory : function() {
        try {
            this.uid = new Date;
            (this.storage = window.localStorage).setItem(this.uid, this.uid);
            this.fail = this.storage.getItem(this.uid) != this.uid;
            this.storage.removeItem(this.uid);
            this.fail && (this.storage = false);
        } catch(e) {}
        return this.storage && ORYX.LOCAL_HISTORY_ENABLED;
    },
    addToHistory : function() {
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var formattedSvgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(false));
        var processName = jsonPath(processJSON.evalJSON(), "$.properties.name");
        var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var processVersion = jsonPath(processJSON.evalJSON(), "$.properties.version");

        var item = {
            processid: processId,
            processname: processName,
            processpkg: processPackage,
            processversion: processVersion,
            timestamp: new Date().getTime(),
            json: processJSON,
            svg: formattedSvgDOM
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
            Ext.Msg.minWidth = 500;
            Ext.Msg.alert("Local History quota exceeded. Clearing local history.");
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
        this.stopStoring();
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
    },
    enableLocalHistory: function() {
        ORYX.LOCAL_HISTORY_ENABLED = true;
        this.setupAndLoadHistoryData();
        this.startStoring();
        this.facade.raiseEvent({type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED});
    },
    startStoring: function() {
        this.historyInterval = setInterval(this.addToHistory.bind(this), ORYX.LOCAL_HISTORY_TIMEOUT);
    },
    stopStoring: function() {
        clearInterval(this.historyInterval);
    }

});
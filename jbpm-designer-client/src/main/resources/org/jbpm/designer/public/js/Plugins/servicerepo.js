if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.ServiceRepoIntegration = Clazz.extend({
    repoDialog: undefined,
    repoContent: undefined,

    construct: function(facade){
        this.facade = facade;

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name':ORYX.I18N.View.connectServiceRepo,
                'functionality': this.jbpmServiceRepoConnect.bind(this),
                'group': "servicerepogroup",
                'icon': ORYX.BASE_FILE_PATH + "images/repository_rep.gif",
                'description': ORYX.I18N.View.connectServiceRepoDesc,
                'index': 4,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.READONLY != true;
                    //				profileParamName = "profile";
                    //				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
                    //				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
                    //		        regexa = new RegExp( regexSa );
                    //		        profileParams = regexa.exec( window.location.href );
                    //		        profileParamValue = profileParams[1];
                    //				return profileParamValue == "jbpm";
                }.bind(this)
            });
        }
    },
    jbpmServiceRepoConnect : function() {
        this._showInitialRepoScreen();
    },
    _showInitialRepoScreen : function() {
        this.repoContent = new Ext.Panel({
            layout:'table',
            html: '<br/><br/><br/><br/><center>'+ORYX.I18N.View.noServiceSpecified+'</center>'
        });

        var connectToRepo = new Ext.Button({
            text: ORYX.I18N.View.connect,
            handler: function(){
                this._updateRepoDialog(Ext.getCmp('serviceurlfield').getValue());
            }.bind(this)
        });

        this.repoDialog = new Ext.Window({
            autoCreate: true,
            autoScroll:true,
            layout: 	'fit',
            plain:		true,
            bodyStyle: 	'padding:5px;',
            title: 		ORYX.I18N.View.connectServiceRepoDataTitle,
            height: 	440,
            width:		600,
            modal:		true,
            fixedcenter:true,
            shadow:		true,
            proxyDrag: 	true,
            resizable:	true,
            items: 		[this.repoContent],
            tbar: [
                {
                    id: 'serviceurlfield',
                    xtype: 'textfield',
                    fieldLabel: 'URL',
                    name: 'repourl',
                    width: '300',
                    value: ORYX.I18N.View.enterServiceURL,
                    handleMouseEvents: true,
                    listeners: {
                        render: function() {
                            this.getEl().on('mousedown', function(e, t, eOpts) {Ext.getCmp('serviceurlfield').setValue("");});
                        }
                    }
                },
                connectToRepo
            ],
            buttons:[
                {
                    text:ORYX.I18N.jPDLSupport.close,
                    handler:function(){
                        this.repoDialog.hide();
                    }.bind(this)
                }
            ]
        });
        this.repoDialog.on('hide', function(){
            if(this.repoDialog) {
                this.repoDialog.destroy(true);
                delete this.repoDialog;
            }
        });
        this.repoDialog.show();
    },
    _updateRepoDialog : function(serviceRepoURL) {
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.View.connectServiceRepoConnecting,
            title       : ''

        });
        Ext.Ajax.request({
            url: ORYX.PATH + "jbpmservicerepo",
            method: 'POST',
            success: function(request){
                try {
                    if(request.responseText == "false") {
                        this.repoDialog.remove(this.repoContent, true);
                        this.repoContent = new Ext.Panel({
                            layout:'table',
                            html: '<br/><br/><br/><br/><center>'+ORYX.I18N.View.noServiceSpecified+'.</center>'
                        });
                        this.repoDialog.add(this.repoContent);
                        this.repoDialog.doLayout();
                        ORYX.EDITOR._pluginFacade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.View.failConnectService,
                            title       : ''
                        });
                    } else {
                        this._showJbpmServiceInfo(request.responseText, serviceRepoURL);
                    }
                } catch(e) {
                    this.repoDialog.remove(this.repoContent, true);
                    this.repoContent = new Ext.Panel({
                        layout:'table',
                        html: '<br/><br/><br/><br/><center>'+ORYX.I18N.View.noServiceSpecified+'</center>'
                    });
                    this.repoDialog.add(this.repoContent);
                    this.repoDialog.doLayout();
                    ORYX.EDITOR._pluginFacade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.View.failConnectService+':' + e,
                        title       : ''
                    });
                }
            }.createDelegate(this),
            failure: function(){
                this.repoDialog.remove(this.repoContent, true);
                this.repoContent = new Ext.Panel({
                    layout:'table',
                    html: '<br/><br/><br/><br/><center>'+ORYX.I18N.View.noServiceSpecified+'</center>'
                });
                this.repoDialog.add(this.repoContent);
                this.repoDialog.doLayout();
                ORYX.EDITOR._pluginFacade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.View.failConnectService+'.',
                    title       : ''
                });
            },
            params: {
                action: 'display',
                profile: ORYX.PROFILE,
                uuid : window.btoa(encodeURI(ORYX.UUID)),
                repourl : serviceRepoURL
            }
        });
    },
    _showJbpmServiceInfo : function(jsonString, serviceRepoURL) {
        var jsonObj = jsonString.evalJSON();

        var myData = [];
        var j = 0;
        for (var key in jsonObj) {
            myData[j] = jsonObj[key];
            j++;
        }

        var store = new Ext.data.SimpleStore({
            fields: [{name: 'name'},
                {name: 'displayName'},
                {name: 'icon'},
                {name: 'category'},
                {name: 'explanation'},
                {name: 'documentation'},
                {name: 'inputparams'},
                {name: 'results'}
            ],
            data : myData
        });

        var grid = new 	Ext.grid.GridPanel({
            store: store,
            columns: [
                {header: ORYX.I18N.View.headerIcon, width: 50, sortable: true, dataIndex: 'icon', renderer: this._renderIcon},
                {header: ORYX.I18N.View.headerName, width: 100, sortable: true, dataIndex: 'displayName'},
                {header: ORYX.I18N.View.headerExplanation, width: 100, sortable: true, dataIndex: 'explanation'},
                {header: ORYX.I18N.View.headerDocumentation, width: 100, sortable: true, dataIndex: 'documentation', renderer: this._renderDocs},
                {header: ORYX.I18N.View.headerInput, width: 200, sortable: true, dataIndex: 'inputparams'},
                {header: ORYX.I18N.View.headerResults, width: 200, sortable: true, dataIndex: 'results'},
                {header: ORYX.I18N.View.headerCategory, width: 100, sortable: true, dataIndex: 'category'}
            ],
            title: ORYX.I18N.View.clickOnRowToInstall,
            autoScroll : true,
            viewConfig : {
                autoFit : true
            }
        });

        grid.on('rowdblclick', function(g, i, e) {
            // g is the grid
            // i is the index
            // e is the event

            ORYX.EDITOR._pluginFacade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : ORYX.I18N.View.installingRepoItem,
                title       : ''

            });

            var aname = g.getStore().getAt(i).get('name');
            var acategory = g.getStore().getAt(i).get('category');
            // send request to server to install the selected service node
            Ext.Ajax.request({
                url: ORYX.PATH + 'jbpmservicerepo',
                method: 'POST',
                success: function(request) {
                    try {
                        if(request.responseText == "false") {
                            ORYX.EDITOR._pluginFacade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.View.failInstallation,
                                title       : ''
                            });
                        } else {
                            ORYX.EDITOR._pluginFacade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'success',
                                msg         : ORYX.I18N.View.successInstall,
                                title       : ''
                            });
                        }
                    } catch(e) {
                        ORYX.EDITOR._pluginFacade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.View.failAssetsInstallation+': ' + e,
                            title       : ''
                        });
                    }
                }.createDelegate(this),
                failure: function(){
                    ORYX.EDITOR._pluginFacade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.View.failAssetsInstallation+'.',
                        title       : ''
                    });
                }.createDelegate(this),
                params: {
                    action: 'install',
                    profile: ORYX.PROFILE,
                    uuid : window.btoa(encodeURI(ORYX.UUID)),
                    asset : aname,
                    category : acategory,
                    repourl : serviceRepoURL
                }
            });

        });

        this.repoDialog.remove(this.repoContent, true);
        this.repoContent = new Ext.TabPanel({
            activeTab: 0,
            border: false,
            width:'100%',
            height:'100%',
            tabPosition: 'top',
            layoutOnTabChange: true,
            deferredRender : false,
            items: [{
                title: ORYX.I18N.View.serviceNodes,
                autoScroll : true,
                items: [grid],
                layout: 'fit',
                margins: '10 10 10 10'
            }]
        });
        this.repoDialog.add(this.repoContent);
        this.repoDialog.doLayout();
    },
    _renderIcon: function(val) {
        return '<img src="' + val + '"/>';
    },

    _renderDocs: function(val) {
        return '<a href="' + val + '" target="_blank">link</a>';
    }
});
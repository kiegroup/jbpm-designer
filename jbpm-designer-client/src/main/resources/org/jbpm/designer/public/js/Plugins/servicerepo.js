if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.ServiceRepoIntegration = Clazz.extend({
    repoDialog: undefined,
    repoContent: undefined,

    construct: function(facade){
        this.facade = facade;

        if(!(ORYX.READONLY == true || ORYX.VIEWLOCKED == true)) {
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
                    return !(ORYX.READONLY == true || ORYX.VIEWLOCKED == true);
                }.bind(this)
            });
        }

        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_INSTALL_WORKITEM, this.installworkitem.bind(this));
    },

    jbpmServiceRepoConnect : function() {
        this._showInitialRepoScreen();
    },

    installworkitem : function(event) {
        ORYX.EDITOR._pluginFacade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.View.installingRepoItem,
            title       : ''

        });

        var aname = event.mn;
        var acategory = event.cat;

        // send request to server to install the selected service node
        Ext.Ajax.request({
            url: ORYX.PATH + 'jbpmservicerepo',
            method: 'POST',
            success: function(request) {
                try {
                    if(request.responseText == "false") {
                        ORYX.EDITOR._pluginFacade.raiseEvent({
                            type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype: 'error',
                            msg: ORYX.I18N.View.failInstallation,
                            title: ''
                        });
                    } else if(request.responseText == "alreadyinstalled") {
                        ORYX.EDITOR._pluginFacade.raiseEvent({
                            type: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype: 'info',
                            msg: ORYX.I18N.View.alreadyInstalled,
                            title: ''
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
                uuid :  window.btoa(encodeURI(ORYX.UUID)),
                asset : aname,
                category : acategory,
                repourl : this.selectedrepourl
            }
        });

    },

    _showInitialRepoScreen : function() {
        this.repoContent = new Ext.Panel({
            layout:'table',
            html: '<br/><br/><br/><br/><center>'+ORYX.I18N.View.noServiceSpecified+'</center>'
        });

        var dialogSize = ORYX.Utils.getDialogSize(440, 600);
        var connectToRepo = new Ext.Button({
            text: ORYX.I18N.View.connect,
            handler: function(){
                var tosaveVal = "";
                var repoURLsCookieValue = this._readCookie("designerservicerepos");
                var serviceUrlField = Ext.getCmp('serviceurlfield').getRawValue().replace(/</g,'&lt;').replace(/>/g,'&gt;');
                if(repoURLsCookieValue != null) {
                    if(repoURLsCookieValue.indexOf(serviceUrlField) == -1) {
                        tosaveVal = repoURLsCookieValue + "," + serviceUrlField;
                    } else {
                        tosaveVal = repoURLsCookieValue;
                    }
                } else {
                    tosaveVal = serviceUrlField;
                }

                this._createCookie("designerservicerepos", tosaveVal, 365);
                this._updateRepoDialog(serviceUrlField, dialogSize.width);
                this.selectedrepourl = serviceUrlField;
            }.bind(this)
        });

        var getRepoComboWidth = dialogSize.width / 2;
        this.repoDialog = new Ext.Window({
            autoCreate: true,
            autoScroll:true,
            layout: 	'fit',
            plain:		true,
            bodyStyle: 	'padding:5px;',
            title: 		ORYX.I18N.View.connectServiceRepoDataTitle,
            height: 	dialogSize.height,
            width:		dialogSize.width,
            modal:		true,
            fixedcenter:true,
            shadow:		true,
            proxyDrag: 	true,
            resizable:	true,
            items: 		[this.repoContent],
            tbar: [
                this._getRepoCombo(getRepoComboWidth),
                connectToRepo
            ],
            buttons:[
                {
                    text:ORYX.I18N.jPDLSupport.close,
                    handler:function(){
                        this.repoDialog.hide();
                        this.repoDialog.destroy(true);
                        delete this.repoDialog;
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
    _getRepoCombo : function(comboWidth) {
        var repoLocationData = new Array();
        var repoLocationStore = new Ext.data.SimpleStore({
            fields: [
                'url',
                'value'
            ],
            data: [[]]
        });
        var repoURLsCookieValue = this._readCookie("designerservicerepos");

        if(ORYX.SERVICE_REPO !== undefined && ORYX.SERVICE_REPO.length > 0) {
            var defaultRepo = new Array();
            defaultRepo.push(ORYX.SERVICE_REPO);
            defaultRepo.push(ORYX.SERVICE_REPO);
            repoLocationData.push(defaultRepo);

            repoLocationStore.loadData(repoLocationData);
            repoLocationStore.commitChanges();
        }

        if(repoURLsCookieValue != null) {
            if (repoURLsCookieValue.startsWith(",")) {
                repoURLsCookieValue = repoURLsCookieValue.substr(1, repoURLsCookieValue.length);
            }
            if (repoURLsCookieValue.endsWith(",")) {
                repoURLsCookieValue = repoURLsCookieValue.substr(0, repoURLsCookieValue.length - 1);
            }
            var valueParts = repoURLsCookieValue.split(",");
            for(var i = 0; i < valueParts.length; i++) {
                var nextPart = valueParts[i];
                if(nextPart.length >= 0 && nextPart != ORYX.SERVICE_REPO) {
                    var nextPartArray = new Array();
                    nextPartArray.push(nextPart);
                    nextPartArray.push(nextPart);
                    repoLocationData.push(nextPartArray);
                }
            }
            repoLocationStore.loadData(repoLocationData);
            repoLocationStore.commitChanges();
        } else {
            // add the community defaults
            var communityRepoOne = new Array();
            communityRepoOne.push("http://people.redhat.com/tsurdilo/repository");
            communityRepoOne.push("http://people.redhat.com/tsurdilo/repository");
            repoLocationData.push(communityRepoOne);
            var communityRepoTwo = new Array();
            communityRepoTwo.push("http://people.redhat.com/kverlaen/repository");
            communityRepoTwo.push("http://people.redhat.com/kverlaen/repository");
            repoLocationData.push(communityRepoTwo);

            repoLocationStore.loadData(repoLocationData);
            repoLocationStore.commitChanges();
        }

        var repoUrlCombo = new Ext.form.ComboBox({
            id: 'serviceurlfield',
            name: 'repourl',
            forceSelection: false,
            editable: true,
            allowBlank: false,
            displayField: 'url',
            valueField: 'value',
            mode: 'local',
            queryMode: 'local',
            typeAhead: true,
            value: "",
            triggerAction: 'all',
            fieldLabel: 'Location',
            width: comboWidth,
            store: repoLocationStore
        });

        return repoUrlCombo;
    },

    _updateRepoDialog : function(serviceRepoURL, dialogWidth) {
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
                    if( (request.responseText == "false") || (request.responseText.startsWith("false||"))) {
                        if(this.repoDialog) {
                            this.repoDialog.remove(this.repoContent, true);
                        }
                        this.repoContent = new Ext.Panel({
                            layout:'table',
                            html: '<br/><br/><br/><br/><center>'+ORYX.I18N.View.noServiceSpecified+'.</center>'
                        });
                        this.repoDialog.add(this.repoContent);
                        this.repoDialog.doLayout();

                        if(request.responseText.startsWith("false||")) {
                            var errorParts = request.responseText.split("||");
                            ORYX.EDITOR._pluginFacade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.View.failConnectService + " - " + errorParts[1],
                                title       : ''
                            });
                        } else {
                            ORYX.EDITOR._pluginFacade.raiseEvent({
                                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                ntype		: 'error',
                                msg         : ORYX.I18N.View.failConnectService,
                                title       : ''
                            });
                        }
                    } else {
                        this._showJbpmServiceInfo(request.responseText, serviceRepoURL, dialogWidth);
                    }
                } catch(e) {
                    if(this.repoDialog) {
                        this.repoDialog.remove(this.repoContent, true);
                    }
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
                if(this.repoDialog) {
                    this.repoDialog.remove(this.repoContent, true);
                }
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
                uuid :  window.btoa(encodeURI(ORYX.UUID)),
                repourl : serviceRepoURL
            }
        });
    },
    _showJbpmServiceInfo : function(jsonString, serviceRepoURL, dialogWidth) {
        var jsonObj = jsonString.evalJSON();

        var myData = [];
        var j = 0;
        for (var key in jsonObj) {
            myData[j] = jsonObj[key];
            j++;
        }

        this.mystore = new Ext.data.SimpleStore({
            fields: [
                {name: 'name'},
                {name: 'displayName'},
                {name: 'icon'},
                {name: 'category'},
                {name: 'explanation'},
                {name: 'documentation'},
                {name: 'inputparams'},
                {name: 'results'},
                {name: 'defaulthandler'}
            ],
            data : myData
        });

        var singleColWidth = dialogWidth / 19;
        var gridId = Ext.id();
        var workiteminstaller = new Extensive.grid.WorkitemInstaller();
        this.mygrid = new Ext.grid.EditorGridPanel({
            autoScroll: true,
            autoHeight: true,
            store: this.mystore,
            id: gridId,
            stripeRows: true,
            selModel: workiteminstaller,
            cm: new Ext.grid.ColumnModel([
                workiteminstaller,
                {
                    id: 'icon', header: ORYX.I18N.View.headerIcon, width: singleColWidth, sortable: true, dataIndex: 'icon', renderer: this._renderIcon.bind(this)
                },
                {
                    id: 'displayName',
                    header: ORYX.I18N.View.headerName, width: singleColWidth * 2, sortable: true, dataIndex: 'displayName',
                    editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
                },
                {
                    id: 'category',
                    header: ORYX.I18N.View.headerCategory, width: singleColWidth * 2, sortable: true, dataIndex: 'category',
                    editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
                },
                {
                    id: 'defaulthandler',
                    header: ORYX.I18N.View.headerHandler, width: singleColWidth * 2, sortable: true, dataIndex: 'defaulthandler',
                    editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
                },
                {
                    id: 'explanation',
                    header: ORYX.I18N.View.headerExplanation, width: singleColWidth * 2, sortable: true, dataIndex: 'explanation',
                    editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
                },
                {
                    id: 'inputparams',
                    header: ORYX.I18N.View.headerInput, width: singleColWidth * 4, sortable: true, dataIndex: 'inputparams',
                    editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
                },
                {
                    id: 'results',
                    header: ORYX.I18N.View.headerResults, width: singleColWidth * 4, sortable: true, dataIndex: 'results',
                    editor: new Ext.form.TextField({ allowBlank: true, disabled: true })
                },
                {
                    id: 'documentation',
                    header: ORYX.I18N.View.headerDocumentation, width: singleColWidth * 2, sortable: true, dataIndex: 'documentation', renderer: this._renderDocs
                }
            ])
        });

        if(this.repoDialog) {
            this.repoDialog.remove(this.repoContent, true);
        }
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
                items: [this.mygrid],
                layout: 'fit',
                margins: '10 10 10 10'
            }]
        });
        this.repoDialog.add(this.repoContent);
        this.repoDialog.doLayout();
    },
    _renderIcon: function(val) {
        if(this.selectedrepourl.startsWith("file:")) {
            return '<img src="data:image/png;base64,' + val + '"/>';
        } else {
            return '<img src="' + val + '"/>';
        }
    },

    _renderDocs: function(val) {
        return '<a href="' + val + '" target="_blank">link</a>';
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
    }
});
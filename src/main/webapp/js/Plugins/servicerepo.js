if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.ServiceRepoIntegration = Clazz.extend({
	repoDialog: undefined,
	repoContent: undefined,
	
	construct: function(facade){
		this.facade = facade;
		this.facade.offer({
			'name':ORYX.I18N.View.connectServiceRepo,
			'functionality': this.jbpmServiceRepoConnect.bind(this),
			'group': ORYX.I18N.View.jbpmgroup,
			'icon': ORYX.PATH + "images/repository_rep.gif",
			'description': ORYX.I18N.View.connectServiceRepoDesc,
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
				return profileParamValue == "jbpm";
			}.bind(this)
		});
	},
	jbpmServiceRepoConnect : function() {
		this._showInitialRepoScreen();
	},
	_showInitialRepoScreen : function() {
		this.repoContent = new Ext.Panel({
			layout:'table',
		    html: '<br/><br/><br/><br/><center>No Service Repository specified.</center>'
		});
		
		var connectToRepo = new Ext.Button({
    		text: 'Connect',
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
				    value: 'Enter Service Repository URL',
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
			this.repoDialog.destroy(true);
			delete this.repoDialog;
		});
		this.repoDialog.show();
	},
	_updateRepoDialog : function(serviceRepoURL) {
		var repoLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.View.connectServiceRepoConnecting});
		repoLoadMask.show();
		Ext.Ajax.request({
	      url: ORYX.PATH + "jbpmservicerepo",
	      method: 'POST',
	      success: function(request){
		   		try {
		   			repoLoadMask.hide();
		   			if(request.responseText == "false") {
		   				this.repoDialog.remove(this.repoContent, true);
		   				this.repoContent = new Ext.Panel({
		   					layout:'table',
		   				    html: '<br/><br/><br/><br/><center>No Service Repository specified.</center>'
		   				});
		   				this.repoDialog.add(this.repoContent);
		   				this.repoDialog.doLayout();
		   				Ext.Msg.alert("Failed to connect to the Service Repository");
		   			} else {
		   				this._showJbpmServiceInfo(request.responseText, serviceRepoURL);
		   			}
		   		} catch(e) {
		   			repoLoadMask.hide();
		   			this.repoDialog.remove(this.repoContent, true);
		   			this.repoContent = new Ext.Panel({
		   				layout:'table',
		   			    html: '<br/><br/><br/><br/><center>No Service Repository specified.</center>'
		   			});
		   			this.repoDialog.add(this.repoContent);
		   			this.repoDialog.doLayout();
		   			Ext.Msg.alert("Connecting the the Service Repository failed :\n" + e);
		   		}
	      }.createDelegate(this),
	      failure: function(){
	      	repoLoadMask.hide();
	      	this.repoDialog.remove(this.repoContent, true);
	      	this.repoContent = new Ext.Panel({
				layout:'table',
			    html: '<br/><br/><br/><br/><center>No Service Repository specified.</center>'
			});
	      	this.repoDialog.add(this.repoContent);
			this.repoDialog.doLayout();
			Ext.Msg.alert("Failed to connect to the Service Repository");
	      },
	      params: {
	      	action: 'display',
	      	profile: ORYX.PROFILE,
	      	uuid : ORYX.UUID,
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
	            {header: 'ICON', width: 50, sortable: true, dataIndex: 'icon', renderer: this._renderIcon},
	            {header: 'NAME', width: 100, sortable: true, dataIndex: 'displayName'},
	            {header: 'EXPLANATION', width: 100, sortable: true, dataIndex: 'explanation'},
	            {header: 'DOCUMENTATION', width: 100, sortable: true, dataIndex: 'documentation', renderer: this._renderDocs},
	            {header: 'INPUT PARAMETERS', width: 200, sortable: true, dataIndex: 'inputparams'},
	            {header: 'RESULTS', width: 200, sortable: true, dataIndex: 'results'},
	            {header: 'CATEGORY', width: 100, sortable: true, dataIndex: 'category'}
	        ],
	        title: 'Service Nodes. Double-click on a row to install.',
	        autoHeight:true,
	        frame:true
	    });
		
		grid.on('rowdblclick', function(g, i, e) {
			// g is the grid
			// i is the index
			// e is the event
			var installLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.View.installingRepoItem});
			installLoadMask.show();
			var aname = g.getStore().getAt(i).get('name');
			var acategory = g.getStore().getAt(i).get('category');
			// send request to server to install the selected service node
			Ext.Ajax.request({
	            url: ORYX.PATH + 'jbpmservicerepo',
	            method: 'POST',
	            success: function(request) {
	    	   		try {
	    	   			installLoadMask.hide();
	    	   			if(request.responseText == "false") {
			   				Ext.Msg.alert("Failed to install the repository assets");
			   			} else {
			   				Ext.Msg.minWidth = 600;
			   				Ext.Msg.alert('Installation was successful. Please save your process to see the installed assets.');
			   			}
	    	   		} catch(e) {
                        installLoadMask.hide();
	    	   			Ext.Msg.minWidth = 600;
	    	   			Ext.Msg.alert('Installing the repository assets failed :\n' + e);
	    	   		}
	            }.createDelegate(this),
	            failure: function(){
                    installLoadMask.hide();
	            	Ext.Msg.minWidth = 600;
	            	Ext.Msg.alert('Failed to install the repository assets');
	            },
	            params: {
	            	action: 'install',
	            	profile: ORYX.PROFILE,
	            	uuid : ORYX.UUID,
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
	        defaults:{autoHeight: true, autoScroll: true},
	        items: [{
	            title: 'Service Nodes',
	            anchor: '100%',
                autoScroll   : true,
                layout       : 'table',
	            items: [grid],
	            margins: '10 10 10 10',
	            listeners: {
	            	'tabchange': function(tp, p) {
	            		tabs.doLayout();
	            }}
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
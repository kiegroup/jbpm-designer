if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.PluginLoader = Clazz.extend({
	
    facade: undefined,
	mask: undefined,
	processURI: undefined,
	
    construct: function(facade){
		this.facade = facade;
		
		this.facade.offer({
			'name': ORYX.I18N.PluginLoad.AddPluginButtonName,
			'functionality': this.showManageDialog.bind(this),
			'group': ORYX.I18N.SSExtensionLoader.group,
			'icon': ORYX.PATH + "images/labs/script_add.png",
			'description': ORYX.I18N.PluginLoad.AddPluginButtonDesc,
			'index': 8,
			'minShape': 0,
			'maxShape': 0
		});},
	showManageDialog: function(){
			this.mask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.Oryx.pleaseWait});
			this.mask.show();
	var data=[];
	//(var plugins=this.facade.getAvailablePlugins();
	var plugins=[];
	var loadedStencilSetsNamespaces = this.facade.getStencilSets().keys();
	//get all plugins which could be acivated
	this.facade.getAvailablePlugins().each(function(match) {
	if ((!match.requires 	|| !match.requires.namespaces 	
			|| match.requires.namespaces.any(function(req){ return loadedStencilSetsNamespaces.indexOf(req) >= 0 }) )
		&&(!match.notUsesIn 	|| !match.notUsesIn.namespaces 	
				|| !match.notUsesIn.namespaces.any(function(req){ return loadedStencilSetsNamespaces.indexOf(req) >= 0 }))){
		plugins.push( match );

	}});
	
	plugins.each(function(plugin){
			data.push([plugin.name, plugin.engaged===true]);
			})
		if(data.length==0){return};
		var reader = new Ext.data.ArrayReader({}, [
        {name: 'name'},
		{name: 'engaged'} ]);
		
		var sm = new Ext.grid.CheckboxSelectionModel({
			listeners:{
			beforerowselect: function(sm,nbr,exist,rec){
			this.mask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.Oryx.pleaseWait});
			this.mask.show();
				this.facade.activatePluginByName(rec.data.name, 
						function(sucess,err){
						this.mask.hide();

							if(!!sucess){
								sm.suspendEvents();
								sm.selectRow(nbr, true);
								sm.resumeEvents();
							}else{
								Ext.Msg.show({
		   							   title: ORYX.I18N.PluginLoad.loadErrorTitle,
									   msg: ORYX.I18N.PluginLoad.loadErrorDesc + ORYX.I18N.PluginLoad[err],
									   buttons: Ext.MessageBox.OK
									});
							}}.bind(this));
				return false;
				}.bind(this),
			rowdeselect: function(sm,nbr,rec){
						sm.suspendEvents();
						sm.selectRow(nbr, true);
						sm.resumeEvents();
					}
			}});
	    var grid2 = new Ext.grid.GridPanel({
	    		store: new Ext.data.Store({
		            reader: reader,
		            data: data
		        	}),
		        cm: new Ext.grid.ColumnModel([
		            
		            {id:'name',width:390, sortable: true, dataIndex: 'name'},
					sm]),
			sm: sm,
	        width:450,
	        height:250,
	        frame:true,
			hideHeaders:true,
	        iconCls:'icon-grid',
			listeners : {
				render: function() {
					var recs=[];
					this.grid.getStore().each(function(rec){

						if(rec.data.engaged){
							recs.push(rec);
						}
					}.bind(this));
					this.suspendEvents();
					this.selectRecords(recs);
					this.resumeEvents();
				}.bind(sm)
			}
	    });

		var newURLWin = new Ext.Window({
					title:		ORYX.I18N.PluginLoad.WindowTitle, 
					//bodyStyle:	"background:white;padding:0px", 
					width:		'auto', 
					height:		'auto',
					modal:		true
					//html:"<div style='font-weight:bold;margin-bottom:10px'></div><span></span>",
				});
		newURLWin.add(grid2);
		newURLWin.show();
		this.mask.hide();

		}
		})
			
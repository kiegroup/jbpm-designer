
Ext.BLANK_IMAGE_URL = 'lib/ext-2.0.2/resources/images/default/s.gif';
Ext.engine_url = 'http://localhost:3000/';


function loadStoreForCase(val) {
	Ext.task_grid.loadMask.msg = "Loading tasks for Case #"+val +"...";							
	
	Ext.store2.proxy.conn.url = '/oryx/engineproxy?url=' + Ext.engine_url + 'cases/' + val;	
	Ext.store2.reload();
	Ext.getCmp('top-panel').setTitle('Case List for Case #' + val);
	Ext.task_grid.render();
}


Ext.onReady(function() {
var xg = Ext.grid;

Ext.button_toolbar = new Ext.Toolbar({width:'100%', height:'100%', autoFit: true});

Ext.store = new Ext.data.Store({
    url: '/oryx/engineproxy?url=' + Ext.engine_url + 'cases',

    reader: new Ext.data.XmlReader({
           record: 'task',
           id: 'case_id',
           totalRecords: '@total'
       }, [
           'case_id' , 'net_name'        
       ])
});

Ext.store2 = new Ext.data.GroupingStore({
    url: '/oryx/engineproxy?url=' + Ext.engine_url + 'cases/217',
	groupField: 'transition_name',
	sortInfo: {field: 'transition_name', direction: 'ASC'},
    reader: new Ext.data.XmlReader({
           record: 'task',
           id: 'transition_name',
           totalRecords: '@total'
       }, [
           'transition_id', 'transition_name' , 'action', 'case_id', 'attractivity', 'pending_time'
       ])
});

Ext.store3 = new Ext.data.GroupingStore({
    url: '/oryx/engineproxy?url=' + Ext.engine_url + 'cases/0',
	groupField: 'transition_id',
	sortInfo: {field: 'transition_id', direction: 'ASC'},
    reader: new Ext.data.XmlReader({
           record: 'task',
           id: 'transition_id',
           totalRecords: '@total'
       }, [
           'transition_id', 'net_name'
       ])
});

Ext.case_grid = new Ext.grid.GridPanel({
						        store: Ext.store,
						        columns: [
						            {header: "case_id", dataIndex: 'case_id', sortable: true, renderer: caseLinkRenderer},
						            {header: "name", dataIndex: 'net_name', sortable: true}
						        ],
						        width: '100%',
						        height: 400,
						        loadMask : {msg:"Loading cases..."},
				                viewConfig: {
										        forceFit: true,
										        autoFit:true
										    }
						        });
		
Ext.task_grid = new xg.GridPanel({
								store: Ext.store2,							
								columns: [ 
//									{id: 'transition_id', header: 'transition id', sortable: true, dataIndex: 'transition_id', renderer: transitionLinkRenderer},
									{header: 'transition name', sortable: true, dataIndex: 'transition_name'},
									{header: 'attractivity', sortable: true, dataIndex: 'attractivity', renderer: attractivityRenderer},
									{header: 'pending_time', sortable: true, dataIndex: 'pending_time'}
//									,
//									{header: 'action', sortable: true, dataIndex: 'action'}
								],
						        loadMask : {msg:"Loading tasks..."},							
				                viewConfig: {
										        forceFit: true,
										        autoFit:true
										    },
							         height: 200,
							         collapsible: false,
							         iconCls: 'icon-grid',
									 onClick: taskOnClick
							   });				        

Ext.spawn_grid = new Ext.grid.GridPanel({
						        store: Ext.store3,
						        columns: [
						            {header: "id", dataIndex: 'transition_id', sortable: true, renderer: spawnLinkRenderer},
						            {header: "name", dataIndex: 'net_name', sortable: true}
						        ],
						        width: '100%',
						        height: 400,
						        loadMask : {msg:"Loading deployed petrinets..."},
				                viewConfig: {
										        forceFit: true,
										        autoFit:true
										    }
						        });

function transitionLinkRenderer(value){
	return String.format('<a href="' + Ext.engine_url + '/transitions/{0}" target="form_frame">DO</a>', value);
}

function spawnLinkRenderer(value){
	return String.format('<a href="' + Ext.engine_url + '/transitions/{0}" target="form_frame">{0}</a>', value);
}

function caseLinkRenderer(value) {
	return String.format('<a href="javascript:loadStoreForCase({0})">{0}</a>', value); 
}

function floattorgb(val) {
	return parseInt(parseFloat(val)*2.55);
}

function attractivityRenderer(value) {
	return '<span style="color: rgb(' + (255 - floattorgb(value)) + ','+floattorgb(value)+', 0);">' + value + '</span>'; 
}

function taskOnClick(blub) {
	Ext.spawnBtn.setDisabled(true);
	Ext.allocateBtn.setDisabled(true);
	Ext.submitBtn.setDisabled(true);
	Ext.suspendBtn.setDisabled(true);
	Ext.resumeBtn.setDisabled(true);
	Ext.delegateBtn.setDisabled(true);
	Ext.reviewBtn.setDisabled(true);
	Ext.skipBtn.setDisabled(true);
	
	Ext.task_grid.loadMask.msg = "Loading actions for selected task...";							
	Ext.task_grid.loadMask.show();
	
	Ext.actionsStore = new Ext.data.Store({
	    url: '/oryx/engineproxy?url=' + Ext.engine_url + 'cases/'+Ext.task_grid.selModel.getSelected().data.case_id,
	    reader: new Ext.data.XmlReader({
	           record: 'task',
	           id: 'transition_id',
	           totalRecords: '@total'
	       }, [
	           'action', 'transition_name', 'transition_id'
	       ])
	});
	
	Ext.actionsStore.on('load', function(a,b,c) {
		Ext.actionsStore.each(function(rec) {
		if (Ext.task_grid.selModel.getSelected().data.transition_name == rec.data.transition_name) {
			switch (rec.data.action)
				{
				case 'allocate':
				  Ext.allocateBtn.transition_id = rec.data.transition_id;
				  Ext.allocateBtn.setDisabled(false);
				  break
				case 'submit':
				  Ext.submitBtn.transition_id = rec.data.transition_id;
				  Ext.submitBtn.setDisabled(false);
				  break
				case 'suspend':
				  Ext.suspendBtn.transition_id = rec.data.transition_id;
				  Ext.suspendBtn.setDisabled(false);
				  break
				case 'resume':
				  Ext.resumeBtn.transition_id = rec.data.transition_id;
				  Ext.resumeBtn.setDisabled(false);
				  break
				case 'delegate':
				  Ext.delegateBtn.transition_id = rec.data.transition_id;
				  Ext.delegateBtn.setDisabled(false);
				  break
				case 'review':
				  Ext.reviewBtn.transition_id = rec.data.transition_id;
				  Ext.reviewBtn.setDisabled(false);
				  break
				case 'skip':
				  Ext.skipBtn.transition_id = rec.data.transition_id;
				  Ext.skipBtn.setDisabled(false);
				  break
				case '':
				  Ext.spawnBtn.transition_id = rec.data.transition_id;
				  Ext.spawnBtn.setDisabled(false);
				  break
				  }
			}
		});
		
		Ext.task_grid.loadMask.hide();
		
	}, this);
	
	Ext.actionsStore.load();
	
	
//	Ext.msg.alert('info', this.selModel.getSelected().data.action);
//	Ext.reviewBtn.setDisabled(Ext.reviewBtn.disabled == true ? false : true);
}

function buttonOnClick(blub) {
	var transition_id = this.transition_id;
	
	form_frame.location = Ext.engine_url + 'transitions/' + transition_id;
}

Ext.viewport = new Ext.Viewport({
            layout:'border',
            items:[{
                region:'west',
                id:'west-panel',
                title:'Navigation',
                split:true,
                width: 200,
                minSize: 175,
                maxSize: 400,
                collapsible: true,
                layout:'accordion',
                layoutConfig:{
                    animate:true
                },
                items: [  {
			                region:'west1',
			                id:'west1-panel',
			                title:'Cases',
			                split:true,
			                collapsible: true,
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                width: '100%',
			                height: '100%',			                
			                buttons: [
			                	new Ext.Button({id: 'reload_cases', text: 'Reload', onClick: function() {
			                		Ext.store.reload();
									Ext.case_grid.render();											                	
			                	}})
			                ],
			                items: Ext.case_grid
			                },{
			                region:'west2',
			                id:'west2-panel',
			                title:'Monitoring & Analysis',
			                split:true,
			                collapsible: true,
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                layout:'accordion',
			                layoutConfig:{
			                    animate:true
			                },
			                html: 'leer' 
			                
			                
			                },{
			                region:'west3',
			                id:'west3-panel',
			                title:'User Management',
			                split:true,
			                collapsible: true,
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                layout:'accordion',
			                layoutConfig:{
			                    animate:true
			                }
			                },
			                {
			                region:'west4',
			                id:'west4-panel',
			                title:'Spawn Processes',
			                split:true,
			                collapsible: true,
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                width: '100%',
			                height: '100%',			                
			                buttons: [
			                	new Ext.Button({id: 'reload_spawns', text: 'Reload', onClick: function() {
			                		Ext.store3.reload();
									Ext.spawn_grid.render();											                	
			                	}})
			                ],
			                items: Ext.spawn_grid
			                }
			               ]
                
                },
                {
                layout:'column',
                region:'center',
                items: [  {
			                region:'top',
			                id:'top-panel',
			                title:'Case List',
			                split:false,
			                height: 225,
			                minSize: 175,
			                maxSize: 400,
//			                autoScroll: true,
			                collapsible: true,
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                items: Ext.task_grid
			                },
			                {
			                region:'button_toolbar',
			                id:'toolbar-panel',
			                split:false,
			                height:30,
			                width: '100%',
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                items: Ext.button_toolbar
			                },
			                {
			                region:'bottom',
			                id:'bottom-panel',
			                title:'Task Execution',
			                split:false,
			                width: '100%',
			                margins:'35 0 5 5',
			                cmargins:'35 5 5 5',
			                html: '<iframe style="border: 0px none" name="form_frame" width="100%" height="100%" src="about:blank" />'
			                }
                ]
                }]
                
        });
        
        Ext.store.load();        
        Ext.store2.load();
        Ext.store3.load();
        
        Ext.spawnBtn = new Ext.Button({id: 'spawn', text: 'spawn', onClick: buttonOnClick});
        Ext.allocateBtn = new Ext.Button({id: 'allocate', text: 'allocate', onClick: buttonOnClick});
        Ext.submitBtn = new Ext.Button({id: 'submit', text: 'submit', onClick: buttonOnClick});
        Ext.suspendBtn = new Ext.Button({id: 'suspend', text: 'suspend', onClick: buttonOnClick});
        Ext.resumeBtn = new Ext.Button({id: 'resume', text: 'resume', onClick: buttonOnClick});
        Ext.delegateBtn = new Ext.Button({id: 'delegate', text: 'delegate', onClick: buttonOnClick});
        Ext.reviewBtn = new Ext.Button({id: 'review', text: 'review', onClick: buttonOnClick});
        Ext.skipBtn = new Ext.Button({id: 'skip', text: 'skip', onClick: buttonOnClick});
        
        Ext.button_toolbar.add(Ext.spawnBtn);
        Ext.button_toolbar.add(Ext.allocateBtn);
        Ext.button_toolbar.add(Ext.submitBtn);
        Ext.button_toolbar.add(Ext.suspendBtn);
        Ext.button_toolbar.add(Ext.resumeBtn);
        Ext.button_toolbar.add(Ext.delegateBtn);
        Ext.button_toolbar.add(Ext.reviewBtn);
        Ext.button_toolbar.add(Ext.skipBtn);
           
});

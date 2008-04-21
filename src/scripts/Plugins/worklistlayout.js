
Ext.BLANK_IMAGE_URL = 'lib/ext-2.0.2/resources/images/default/s.gif';

function loadStoreForCase(val) {
	Ext.store2.proxy.conn.url = '/oryx/engineproxy?url=http://localhost:3000/cases/' + val;
	Ext.store2.reload();
	Ext.getCmp('top-panel').setTitle('Case List for Case #' + val);
	Ext.task_grid.render();
}

Ext.onReady(function() {
var xg = Ext.grid;

Ext.button_toolbar = new Ext.Toolbar({width:'100%', height:'100%', autoFit: true});

var store = new Ext.data.Store({
    url: '/oryx/engineproxy?url=http://localhost:3000/cases',

    reader: new Ext.data.XmlReader({
           record: 'task',
           id: 'case_id',
           totalRecords: '@total'
       }, [
           'case_id'           
       ])
});

var store2 = new Ext.data.Store({
    url: '/oryx/engineproxy?url=http://localhost:3000/cases/217',

    reader: new Ext.data.XmlReader({
           record: 'task',
           id: 'transition_id',
           totalRecords: '@total'
       }, [
           'transition_id', 'transition_name', 'action'
       ])
});

Ext.case_grid = new Ext.grid.GridPanel({
						        store: store,
						        columns: [
						            {header: "case_id", dataIndex: 'case_id', sortable: true, renderer: caseLinkRenderer}
						        ],
						        width: 200,
						        height: 400,
				                viewConfig: {
										        forceFit: true,
										        autoFit:true
										    }
						        });
		
Ext.task_grid = 			                new xg.GridPanel({
								store: store2,							
								columns: [ 
									{id: 'transition_id', header: 'transition id', sortable: true, dataIndex: 'transition_id', renderer: myCustomRenderer},
									{header: 'transition name', sortable: true, dataIndex: 'transition_name'},
									{header: 'action', sortable: true, dataIndex: 'action'}
								],
				                viewConfig: {
										        forceFit: true,
										        autoFit:true
										    },
							         height: 200,
							         collapsible: false,
							         iconCls: 'icon-grid'
							   });				        

function myCustomRenderer(value){
	return String.format('<a href="http://localhost:3000/transitions/{0}" target="form_frame">DO</a>', value);
}

function caseLinkRenderer(value) {
	return String.format('<a href="javascript:loadStoreForCase({0})">Case {0}</a>', value); 
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
			                html: '<a href="javascript:loadStoreForCase(2)">blub</a>' 
			                
			                
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
        
        store.load();        
        store2.load();
        Ext.store = store;
        Ext.store2 = store2;     
        
        Ext.button_toolbar.add(new Ext.Button({text: 'allocate'}));
        Ext.button_toolbar.add(new Ext.Button({text: 'submit'}));
        Ext.button_toolbar.add(new Ext.Button({text: 'suspend'}));
        Ext.button_toolbar.add(new Ext.Button({text: 'resume'}));
        Ext.button_toolbar.add(new Ext.Button({text: 'delegate'}));
        Ext.button_toolbar.add(new Ext.Button({text: 'review'}));
           
});

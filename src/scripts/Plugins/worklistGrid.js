Ext.BLANK_IMAGE_URL = 'lib/ext-2.0.2/resources/images/default/s.gif';
Ext.onReady(function(){

var xg = Ext.grid;  

var reader = new Ext.data.ArrayReader({}, [ 
					 {name: 'case_id', type: 'int'},
					 {name: 'transition_id'}, 
					 {name: 'transition_name'}, 
					 {name: 'action'} 
				]); 

var grid = new xg.GridPanel({
	store: new Ext.data.GroupingStore({
				groupField:'case_id',
				reader: reader,
				data: xg.dummyData,
				sortInfo:{field: 'case_id', direction: 'ASC'}
	}),

	columns: [ 
		{id:'case_id', header: 'case_id', sortable: true, dataIndex: 'case_id'},
		{header: 'transition id', sortable: true, dataIndex: 'transition_id'},
		{header: 'transition name', sortable: true, dataIndex: 'transition_name'},
		{header: 'action', sortable: true, dataIndex: 'action'}
	],

	view: new Ext.grid.GroupingView({
	         forceFit:true,
	         groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Activities" : "Activity"]})'
			}),
         frame:true,
         width: 700,
         height: 450,
         collapsible: true,
         animCollapse: false,
         title: 'Worklist',
         iconCls: 'icon-grid',
         renderTo: 'worklist-grid'
   });
 });

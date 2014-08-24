/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

/*
 * Ext JS Library 2.1
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.onReady(function(){
  
    var myData = {
		records : [
			{ name : "Rec 0", column1 : "0", column2 : "0" },
			{ name : "Rec 1", column1 : "1", column2 : "1" },
			{ name : "Rec 2", column1 : "2", column2 : "2" },
			{ name : "Rec 3", column1 : "3", column2 : "3" },
			{ name : "Rec 4", column1 : "4", column2 : "4" },
			{ name : "Rec 5", column1 : "5", column2 : "5" },
			{ name : "Rec 6", column1 : "6", column2 : "6" },
			{ name : "Rec 7", column1 : "7", column2 : "7" },
			{ name : "Rec 8", column1 : "8", column2 : "8" },
			{ name : "Rec 9", column1 : "9", column2 : "9" }
		]
	};


	// Generic fields array to use in both store defs.
	var fields = [
	   {name: 'name', mapping : 'name'},
	   {name: 'column1', mapping : 'column1'},
	   {name: 'column2', mapping : 'column2'}
	];
	
    // create the data store
    var firstGridStore = new Ext.data.JsonStore({
        fields : fields,
		data   : myData,
		root   : 'records'
    });
	

	// Column Model shortcut array
	var cols = [
		{ id : 'name', header: "Record Name", width: 160, sortable: true, dataIndex: 'name'},
		{header: "column1", width: 50, sortable: true, dataIndex: 'column1'},
		{header: "column2", width: 50, sortable: true, dataIndex: 'column2'}
	];
    
	// declare the source Grid
    var firstGrid = new Ext.grid.GridPanel({
		ddGroup          : 'secondGridDDGroup',
        store            : firstGridStore,
        columns          : cols,
		enableDragDrop   : true,
        stripeRows       : true,
        autoExpandColumn : 'name',
        width            : 325,
		region           : 'west',
        title            : 'First Grid'
    });

    var secondGridStore = new Ext.data.JsonStore({
        fields : fields,
		root   : 'records'
    });
	
    // create the destination Grid
    var secondGrid = new Ext.grid.GridPanel({
		ddGroup          : 'firstGridDDGroup',
        store            : secondGridStore,
        columns          : cols,
		enableDragDrop   : true,
        stripeRows       : true,
        autoExpandColumn : 'name',
        width            : 325,
		region           : 'center',
        title            : 'Second Grid'
    });

	
	//Simple 'border layout' panel to house both grids
	var displayPanel = new Ext.Panel({
		width    : 650,
		height   : 300,
		layout   : 'border',
		renderTo : 'panel',
		items    : [
			firstGrid,
			secondGrid
		],
		bbar    : [
			'->', // Fill
			{
				text    : 'Reset both grids',
				handler : function() {
					//refresh source grid
					firstGridStore.loadData(myData);
					
					//purge destination grid
					secondGridStore.removeAll();
				}
			}
		]
	});

	// used to add records to the destination stores
	var blankRecord =  Ext.data.Record.create(fields);

	/****
	* Setup Drop Targets
	***/
	// This will make sure we only drop to the view container
	var firstGridDropTargetEl =  firstGrid.getView().el.dom.childNodes[0].childNodes[1];
	var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
		ddGroup    : 'firstGridDDGroup',
		copy       : true,
		notifyDrop : function(ddSource, e, data){
			
			// Generic function to add records.
			function addRow(record, index, allItems) {
				
				// Search for duplicates
				var foundItem = firstGridStore.find('name', record.data.name);
				// if not found
				if (foundItem  == -1) {
					firstGridStore.add(record);
					
					// Call a sort dynamically
					firstGridStore.sort('name', 'ASC');
					
					//Remove Record from the source
					ddSource.grid.store.remove(record);
				}
			}

			// Loop through the selections
			Ext.each(ddSource.dragData.selections ,addRow);
			return(true);
		}
	}); 	

	
	// This will make sure we only drop to the view container
	var secondGridDropTargetEl = secondGrid.getView().el.dom.childNodes[0].childNodes[1]
	
	var destGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl, {
		ddGroup    : 'secondGridDDGroup',
		copy       : false,
		notifyDrop : function(ddSource, e, data){
			
			// Generic function to add records.
			function addRow(record, index, allItems) {
				
				// Search for duplicates
				var foundItem = secondGridStore.find('name', record.data.name);
				// if not found
				if (foundItem  == -1) {
					secondGridStore.add(record);
					// Call a sort dynamically
					secondGridStore.sort('name', 'ASC');
			
					//Remove Record from the source
					ddSource.grid.store.remove(record);
				}
			}
			// Loop through the selections
			Ext.each(ddSource.dragData.selections ,addRow);
			return(true);
		}
	}); 
});

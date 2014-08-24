/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.onReady(function(){
	new App.EmployeeStore({
		storeId: 'employeeStore',
		url: 'loadStore.php'
	});
	Ext.ux.ComponentLoader.load({
		url: 'sampleApp.php',
		params: {
			testing: 'Testing params'
		}
	});
});

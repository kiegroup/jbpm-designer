/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.ns('App');

App.EmployeeDetailsTab = Ext.extend(Ext.TabPanel, {
	load: function(employeeId) {
		this.items.each(function(i) {
			if (i.load) {
				i.load({
					params: {
						employeeId: employeeId
					}
				});				
			}
		});
	}
});
Ext.reg('employeedetailstab', App.EmployeeDetailsTab);

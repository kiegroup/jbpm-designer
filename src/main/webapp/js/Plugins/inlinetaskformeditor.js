if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.InlineTaskFormEditor = Clazz.extend({
	construct: function(facade){
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TASKFORM_EDIT, this.showTaskFormEditor.bind(this));
	},
	showTaskFormEditor: function(options) {
		if(options && options.tn) {
			Ext.Ajax.request({
	            url: ORYX.PATH + 'taskformseditor',
	            method: 'POST',
	            success: function(response) {
	    	   		try {
	    	   			this._buildandshow(options.tn, response.responseText);
	    	   		} catch(e) {
	    	   			Ext.Msg.alert('Error initiating Form Editor :\n' + e);
	    	   		}
	            }.bind(this),
	            failure: function(){
	            	Ext.Msg.alert('Error initiating Form Editor.');
	            },
	            params: {
	            	action: 'load',
	            	taskname: options.tn,
	            	profile: ORYX.PROFILE,
	            	uuid : ORYX.UUID
	            }
	        });
		} else {
			Ext.Msg.alert('Task Name not specified.');
		}
	},
	_buildandshow: function(tn, defaultsrc) {
		var formvalue = "";
		if(defaultsrc && defaultsrc != "false") {
			formvalue = defaultsrc;
		}
		var taskformeditor = new Ext.form.HtmlEditor({
	         //width:     650,
	         //height:    400,
	         value:     formvalue,
	         enableColors: true,
	         enableAlignments: false,
	         autoScroll: true
	       });
		
		var dialog = new Ext.Window({ 
			layout		: 'fit',
			autoCreate	: true, 
			title		: 'Editing Task Form: ' + tn , 
			height		: 530, 
			width		: 700, 
			modal		: true,
			collapsible	: false,
			fixedcenter	: true, 
			shadow		: true, 
			resizable   : true,
			proxyDrag	: true,
			keys:[{
				key	: 27,
				fn	: function(){
						dialog.hide()
				}.bind(this)
			}],
			items		:[taskformeditor],
			listeners	:{
				hide: function(){
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: 'Save',
                handler: function(){
                    var saveLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:'Storing Task Form'});
                    saveLoadMask.show();
                    Ext.Ajax.request({
        	            url: ORYX.PATH + 'taskformseditor',
        	            method: 'POST',
        	            success: function(request) {
        	    	   		try {
        	    	   			saveLoadMask.hide();
        	    	   			dialog.hide();
        	    	   		} catch(e) {
        	    	   			Ext.Msg.alert('Error saving Task Form:\n' + e);
        	    	   		}
        	            }.createDelegate(this),
        	            failure: function(){
        	            	Ext.Msg.alert('Error saving Task Form');
        	            },
        	            params: {
        	            	action: 'save',
        	            	taskname: tn,
        	            	profile: ORYX.PROFILE,
        	            	uuid : ORYX.UUID,
        	            	tfvalue: taskformeditor.getValue()
        	            }
        	        });
                }.bind(this)
            }, {
                text: 'Cancel',
                handler: function(){
                	dialog.hide()
                }.bind(this)
            }]
		});		
		dialog.show();
	}
});
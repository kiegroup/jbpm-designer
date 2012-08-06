if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.InlineTaskFormEditor = Clazz.extend({
	sourceMode: undefined,
	taskformeditor: undefined,
	taskformsourceeditor: undefined,
	taskformcolorsourceeditor: undefined,
	dialog: undefined,
	hlLine: undefined,
	
	construct: function(facade){
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_TASKFORM_EDIT, this.showTaskFormEditor.bind(this));
	},
	showTaskFormEditor: function(options) {
		if(options && options.tn) {
			// load form widgets first
			Ext.Ajax.request({
	            url: ORYX.PATH + 'formwidget',
	            method: 'POST',
	            success: function(response) {
	    	   		try {
	    	   			var widgetJson = response.responseText.evalJSON();
	    	   			// now the form editor
	    	   			Ext.Ajax.request({
	    		            url: ORYX.PATH + 'taskformseditor',
	    		            method: 'POST',
	    		            success: function(response) {
	    		    	   		try {
	    		    	   			this._buildandshow(options.tn, response.responseText, widgetJson);
	    		    	   		} catch(e) {
	    		    	   			Ext.Msg.minWidth = 360;
	    		    	   			Ext.Msg.alert('Error initiating Form Editor :\n' + e);
	    		    	   		}
	    		            }.bind(this),
	    		            failure: function(){
	    		            	Ext.Msg.minWidth = 360;
	    		            	Ext.Msg.alert('Error initiating Form Editor.');
	    		            },
	    		            params: {
	    		            	action: 'load',
	    		            	taskname: options.tn,
	    		            	profile: ORYX.PROFILE,
	    		            	uuid : ORYX.UUID
	    		            }
	    		        });
	    	   		} catch(e) {
	    	   			Ext.Msg.minWidth = 360;
	    	   			Ext.Msg.alert('Error initiating Form Widgets :\n' + e);
	    	   		}
	            }.bind(this),
	            failure: function(){
	            	Ext.Msg.minWidth = 360;
	            	Ext.Msg.alert('Error initiating Form Widgets.');
	            },
	            params: {
	            	action: 'getwidgets',
	            	profile: ORYX.PROFILE
	            }
	        });
		} else {
			Ext.Msg.minWidth = 360;
			Ext.Msg.alert('Task Name not specified.');
		}
	},
	_buildandshow: function(tn, defaultsrc, widgetJson) {
		var formvalue = "";
		if(defaultsrc && defaultsrc != "false") {
			formvalue = defaultsrc;
		}
		
		var widgetKeys = [];
	    for (var key in widgetJson) {
	      if (widgetJson.hasOwnProperty(key)) {
	    	  widgetKeys.push(key);
	      }
	    }
	    widgetKeys.sort();
	    var displayWidgetKeys = [];
	    for (var i = 0; i < widgetKeys.length; i++) {
	    	displayWidgetKeys[i] = [widgetKeys[i] + ""];
	    }
	    
	    var widgetStore = new Ext.data.SimpleStore({
			fields: ["name"],
			data : displayWidgetKeys 
		});
	    
	    var widgetCombo = new Ext.form.ComboBox({
	    	fieldLabel: 'Insert form widget',
            labelStyle: 'width:240px',
            hiddenName: 'widget_name',
            emptyText: 'Insert form widget...',
            store: widgetStore,
            displayField: 'name',
            valueField: 'name',
            mode: 'local',
            typeAhead: true,
            triggerAction: 'all',
            listeners: 
              { 
            	select: { 
            		fn:function(combo, value) {
            			if(this.taskformcolorsourceeditor) {
            				Ext.Ajax.request({
    	    		            url: ORYX.PATH + 'formwidget',
    	    		            method: 'POST',
    	    		            success: function(response) {
    	    		    	   		try {
    	    		    	   			this.taskformcolorsourceeditor.replaceSelection(response.responseText, "end");
    	    		    	   		} catch(e) {
    	    		    	   			Ext.Msg.minWidth = 360;
    	    		    	   			Ext.Msg.alert('Error inserting Form Widget :\n' + e);
    	    		    	   		}
    	    		            }.bind(this),
    	    		            failure: function(){
    	    		            	Ext.Msg.minWidth = 360;
    	    		            	Ext.Msg.alert('Error inserting Form Widget.');
    	    		            },
    	    		            params: {
    	    		            	action: 'getwidgetsource',
    	    		            	profile: ORYX.PROFILE,
    	    		            	widgetname: combo.getValue()
    	    		            }
    	    		        });
            			} else {
            				Ext.Msg.minWidth = 360;
            				Ext.Msg.alert('Widget insertion is only possible in Source Mode');
            			}
                    }.bind(this)
                }  
             }
	    });
	    
	  var sourceeditorid = Ext.id();
  	  this.taskformsourceeditor = new Ext.form.TextArea({
			id: sourceeditorid,
			anchor: '100%',
	        autoScroll: true,
	        value: formvalue
	      });
	    
	    var outterPanel = new Ext.Panel({
	    	header: false,
    		anchor: '100%',
    	    layout:'column',
    	    autoScroll:true,
    	    border : false,
    	    layoutConfig: {
    			columns: 2,
    			pack:'center',
    		    align:'middle'
    		},
    	    items: [
    	            {
    	              columnWidth: .5,
    	              items: this.taskformsourceeditor
    	            },{
    	              columnWidth: .5,
    	              items: [
								{
								    xtype : "component",
								    id    : 'livepreviewpanel',
								    anchor: '100%',
								    autoScroll: true,
								    autoEl : {
								        tag : "iframe",
								        width: "100%",
    	   					            height: "570",
    	   					            frameborder: "0",
    	   					            scrolling: "auto"
								    }
								}]
    	            }
    	          ]
    	});
		
		this.dialog = new Ext.Window({
			id          : 'maineditorwindow',
			layout		: 'fit',
			autoCreate	: true, 
			title		: 'Editing Form: ' + tn + ' - Press [Ctrl-Z] to activate auto-completion' , 
			height		: 570, 
			width		: 930, 
			modal		: true,
			collapsible	: false,
			fixedcenter	: true, 
			shadow		: true, 
			resizable   : true,
			proxyDrag	: true,
			keys:[{
				key	: 27,
				fn	: function(){
						this.dialog.hide();
				}.bind(this)
			}],
			items		:[outterPanel],
			listeners	:{
				hide: function(){
					this.dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: 'Save',
                handler: function(){
                    var saveLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:'Storing Task Form'});
                    saveLoadMask.show();
                    var tosaveValue = "";
                    tosaveValue = this.taskformcolorsourceeditor.getValue();
                    
                    Ext.Ajax.request({
        	            url: ORYX.PATH + 'taskformseditor',
        	            method: 'POST',
        	            success: function(request) {
        	    	   		try {
        	    	   			saveLoadMask.hide();
        	    	   			this.dialog.hide();
        	    	   		} catch(e) {
        	    	   			Ext.Msg.minWidth = 360;
        	    	   			Ext.Msg.alert('Error saving Task Form:\n' + e);
        	    	   		}
        	            }.createDelegate(this),
        	            failure: function(){
        	            	Ext.Msg.minWidth = 360;
        	            	Ext.Msg.alert('Error saving Task Form');
        	            },
        	            params: {
        	            	action: 'save',
        	            	taskname: tn,
        	            	profile: ORYX.PROFILE,
        	            	uuid : ORYX.UUID,
        	            	tfvalue: tosaveValue
        	            }
        	        });
                }.bind(this)
            },
            {
                text: 'Cancel',
                handler: function(){
                	this.dialog.hide()
                }.bind(this)
            }],
            tbar: [
                     widgetCombo
                  ]
		});		
		this.dialog.show();
		this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);
		var delay;
	  	  this.taskformcolorsourceeditor = CodeMirror.fromTextArea(document.getElementById(sourceeditorid), {
				  mode: "text/html",
				  lineNumbers: true,
				  lineWrapping: true,
				  onGutterClick: this.foldFunc,
				  extraKeys: {
					"'>'": function(cm) { cm.closeTag(cm, '>'); },
					"'/'": function(cm) { cm.closeTag(cm, '/'); },
					"Ctrl-Z": function(cm) {CodeMirror.hint(cm, CodeMirror.formsHint, outterPanel);}
				  },
				  onCursorActivity: function() {
					 this.taskformcolorsourceeditor.setLineClass(this.hlLine, null, null);
				     this.hlLine = this.taskformcolorsourceeditor.setLineClass(this.taskformcolorsourceeditor.getCursor().line, null, "activeline");
				  }.bind(this),
				  onChange: function() {
			          clearTimeout(delay);
			          delay = setTimeout(this.updatePreview.bind(this), 300);
			        }.bind(this)
				});
	  	    this.hlLine = this.taskformcolorsourceeditor.setLineClass(0, "activeline");
	        setTimeout(this.updatePreview.bind(this), 300);
	},
	updatePreview: function() {
        var previewFrame = document.getElementById('livepreviewpanel');
        var preview =  previewFrame.contentDocument ||  previewFrame.contentWindow.document;
        preview.open();
        preview.write(this.taskformcolorsourceeditor.getValue());
        preview.close();
      }
	
});
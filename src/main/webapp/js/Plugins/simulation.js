if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.Simulation = Clazz.extend({
	construct: function(facade){
		this.facade = facade;
		
		this.facade.offer({
			'name': "Process Paths",
			'functionality': this.findPaths.bind(this),
			'group': "simulation",
			'icon': ORYX.PATH + "images/path.png",
			dropDownGroupIcon : ORYX.PATH + "images/lightbulb.gif",
			'description': "Display Process Paths",
			'index': 1,
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
		
		this.facade.offer({
			'name': "Run Simulation",
			'functionality': this.runSimulation.bind(this),
			'group': "simulation",
			'icon': ORYX.PATH + "images/control_play.png",
			dropDownGroupIcon : ORYX.PATH + "images/lightbulb.gif",
			'description': "Run Process Simulation",
			'index': 2,
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
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_BUILD_PATH_SVG, this.autoDisplayPath.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_CLEAR_PATH_SVG, this.resetNodeColors.bind(this));
	},
	autoDisplayPath : function(options) {
		if(options && options.pid) {
			var pathid = options.pid;
			var loadPathsMask = new Ext.LoadMask(Ext.getBody(), {msg:'Creating path image'});
			loadPathsMask.show();
			
			Ext.Ajax.request({
	            url: ORYX.PATH + 'simulation',
	            method: 'POST',
	            success: function(response) {
	    	   		try {
	    	   			if(response.responseText && response.responseText.length > 0) {
	    	   				var pathjson = response.responseText.evalJSON();
	    	   				var pathobj = pathjson["paths"];
	    	   				for(var key in pathobj) {
	    	   					if(key == pathid) {
	    	   						var color = this.getDisplayColor(0);
	    	   						var val = pathobj[key];
		    	   		    		this.setNodeColors(key, color, val);
	    	   					}
	    	   				}
	    	   				this.facade.raiseEvent({
	    	   		            type: ORYX.CONFIG.EVENT_SIMULATION_PATH_SVG_GENERATED
	    	   				});
	    	   			} else {
	    	   				Ext.MessageBox.minWidth = 200;
	    	   				Ext.Msg.alert('Invalid Path data.');
	    	   			}
	    	   		} catch(e) {
	    	   			Ext.MessageBox.minWidth = 200;
	    	   			Ext.Msg.alert('Error finding Paths:\n' + e);
	    	   		}
	            }.bind(this),
	            failure: function(){
	            	Ext.Msg.alert('Error finding Paths.');
	            },
	            params: {
	            	action: 'getpathinfo',
	            	profile: ORYX.PROFILE,
	            	json: ORYX.EDITOR.getSerializedJSON(),
	            	ppdata: ORYX.PREPROCESSING,
	            	sel: ""
	            }
	        });
			
			loadPathsMask.hide();
		} else {
			Ext.MessageBox.minWidth = 200;
			Ext.Msg.alert('Unknown path id.');
		}
	},
	findPaths: function() {
		var loadPathsMask = new Ext.LoadMask(Ext.getBody(), {msg:'Calculating process paths'});
		loadPathsMask.show();
		var selection = this.facade.getSelection();
		var selectedId = "";
		var wintitle = "Process Paths";
		if(selection.length == 1) {
			selection.each(function(shape) {
				if(shape.getStencil().title() == "Embedded Subprocess") {
					selectedId = shape.resourceId;
					wintitle = "Subprocess Paths";
				}
			});
		} 
		
		Ext.Ajax.request({
            url: ORYX.PATH + 'simulation',
            method: 'POST',
            success: function(response) {
    	   		try {
    	   			if(response.responseText && response.responseText.length > 0) {
    	   				var pathjson = response.responseText.evalJSON();
    	   				var pathobj = pathjson["paths"];
    	   				var ProcessPathsDef = Ext.data.Record.create([{
    	   		            name: 'display'
    	   		        }, {
    	   		        	name: 'numele'
    	   		        }, {
    	   		            name: 'pid'
    	   		        }]);
    	   		    	
    	   		    	var processpathsProxy = new Ext.data.MemoryProxy({
    	   		            root: []
    	   		        });
    	   		    	
    	   		    	var processpathsStore = new Ext.data.Store({
    	   		    		autoDestroy: true,
    	   		            reader: new Ext.data.JsonReader({
    	   		                root: "root"
    	   		            }, ProcessPathsDef),
    	   		            proxy: processpathsProxy,
    	   		            sorters: [{
    	   		                property: 'display',
    	   		                direction:'ASC'
    	   		            }]
    	   		        });
    	   		    	processpathsStore.load();
    	   		    	
    	   		    	var cindex = 0;
    	   		    	for(var key in pathobj) {
    	   		    		var val = pathobj[key];
    	   		    		var valParts = val.split("|");
    	   		    		processpathsStore.add(new ProcessPathsDef({
    	   		    			display: this.getDisplayColor(cindex),
    	   		    			numele: valParts.length,
                                pid: key
                            }));
    	   		    		cindex++;
    	   		    	}
    	   		    	processpathsStore.commitChanges();
    		            
    		            var gridId = Ext.id();
    		        	var grid = new Ext.grid.EditorGridPanel({
    		                store: processpathsStore,
    		                id: gridId,
    		                stripeRows: true,
    		                cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
    		                	id: 'display',
    		                    header: 'Display Color',
    		                    width: 90,
    		                    dataIndex: 'display',
    		                    renderer: function(val) {
		                    	  if(val) { 
		                    		return '<center><div width="20px" height="8px" style="width:20px;height:8px;background-color:' + val + '"></div></center>';
		                    	  } else {
		                    		return "<center>None</center>";
		                    	  }
		                        }
    		                }, {
    		                	id: 'numele',
    		                    header: 'Number of Elements',
    		                    width: 130,
    		                    dataIndex: 'numele',
    		                    renderer: function(val) {
  		                    	  if(val) { 
  		                    		return '<center>' + val + '</center>';
  		                    	  } else {
  		                    		return "<center>0</center>";
  		                    	  }
  		                        }
    		                }]),
    		                autoHeight: true
    		            });
    		        	
    	   				var processPathsPanel = new Ext.Panel({
    		        		id: 'processPathsPanel',
    		        		title: '<center>Select ' + wintitle + ' and click "Show Path" to display it.</center>',
    		        		layout:'column',
    		        		items:[
    		        		       grid
    		                      ],
    		        		layoutConfig: {
    		        			columns: 1
    		        		},
    		        		defaults: {
    		        	        columnWidth: 1.0
    		        	    }
    		        	});
    	   				
    	   				
    	   				var dialog = new Ext.Window({ 
    		    			layout		: 'anchor',
    		    			autoCreate	: true, 
    		    			title		: wintitle, 
    		    			height		: 200, 
    		    			width		: 300, 
    		    			modal		: true,
    		    			collapsible	: false,
    		    			fixedcenter	: true, 
    		    			shadow		: true, 
    		    			resizable   : true,
    		    			proxyDrag	: true,
    		    			autoScroll  : true,
    		    			keys:[{
    		    				key	: 27,
    		    				fn	: function(){
    		    						dialog.hide();
    		    				}.bind(this)
    		    			}],
    		    			items		:[processPathsPanel],
    		    			listeners	:{
    		    				hide: function(){
    		    					this.resetNodeColors();
    		    					dialog.destroy();
    		    				}.bind(this)				
    		    			},
    		    			buttons		: [{
    		                    text: 'Show Path',
    		                    handler: function(){
    		                    	if(grid.getSelectionModel().getSelectedCell() != null) {
    		                    		var selectedIndex = grid.getSelectionModel().getSelectedCell()[0];
    		                    		var outValue = processpathsStore.getAt(selectedIndex).data['pid'];
    		                        	this.setNodeColors(outValue, this.getDisplayColor(selectedIndex), pathobj[outValue]);
    		                    	} else {
    		                    		Ext.Msg.alert('Plese select a process path.');
    		                    	}
    		                    }.bind(this)
    		                }, {
    		                    text: 'Close',
    		                    handler: function(){
    		                    	this.resetNodeColors();
    		                    	dialog.hide();
    		                    }.bind(this)
    		                }]
    		    		});	
    	   				loadPathsMask.hide();
    		    		dialog.show();
    	   				
    	   			} else {
    	   				loadPathsMask.hide();
    	   				Ext.MessageBox.minWidth = 200;
    	   				Ext.Msg.alert('Invalid Path data.');
    	   			}
    	   		} catch(e) {
    	   			loadPathsMask.hide();
    	   			Ext.MessageBox.minWidth = 200;
    	   			Ext.Msg.alert('Error finding Paths:\n' + e);
    	   		}
            }.bind(this),
            failure: function(){
                loadPathsMask.hide();
            	Ext.Msg.alert('Error finding Paths.');
            },
            params: {
            	action: 'getpathinfo',
            	profile: ORYX.PROFILE,
            	json: ORYX.EDITOR.getSerializedJSON(),
            	ppdata: ORYX.PREPROCESSING,
            	sel: selectedId
            }
        });
	},
	getDisplayColor : function(cindex) {
		var colors = ["#3399FF", "#FFCC33", "#FF99FF", "#6666CC", "#CCCCCC", "#66FF00", "#FFCCFF", "#0099CC", "#CC66FF", "#FFFF00", "#993300", "#0000CC", "#3300FF","#990000","#33CC00"];
		return colors[cindex];
	},
	resetNodeColors : function() {
		ORYX.EDITOR._canvas.getChildren().each((function(child) {
				this.setOriginalValues(child);
		}).bind(this));
	},
	setOriginalValues : function(shape) {
		if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
    		shape.setProperty("oryx-bordercolor", shape.properties["oryx-origbordercolor"]);
    		shape.setProperty("oryx-bgcolor", shape.properties["oryx-origbgcolor"]);
    	}
		shape.refresh();
		if(shape.getChildren().size() > 0) {
			for (var i = 0; i < shape.getChildren().size(); i++) {
				if(shape.getChildren()[i] instanceof ORYX.Core.Node || shape.getChildren()[i] instanceof ORYX.Core.Edge) {
					this.setOriginalValues(shape.getChildren()[i]);
				}
			}
		}
	},
	setNodeColors : function(pathid, pathcolor, pathelements) {
		this.resetNodeColors();
		ORYX.EDITOR._canvas.getChildren().each((function(child) {
				this.applyPathColors(child, pathcolor, pathelements);
		}).bind(this));
	},
	applyPathColors : function(shape, color, elements) {
		var elementsParts = elements.split("|");
		if(shape instanceof ORYX.Core.Node || shape instanceof ORYX.Core.Edge) {
			for(var i=0; i < elementsParts.length; i++) {
    			var nextPart = elementsParts[i];
    			if(shape.resourceId == nextPart) {
    				shape.setProperty("oryx-bordercolor", color);
    	    		shape.setProperty("oryx-bgcolor", color);
    			}
    		}
    	}
		shape.refresh();
		if(shape.getChildren().size() > 0) {
			for (var i = 0; i < shape.getChildren().size(); i++) {
				if(shape.getChildren()[i] instanceof ORYX.Core.Node || shape.getChildren()[i] instanceof ORYX.Core.Edge) {
					this.applyPathColors(shape.getChildren()[i], color, elements);
				}
			}
		}
	},
	runSimulation : function() {
		var simform = new Ext.form.FormPanel({
			baseCls: 		'x-plain',
	        labelWidth: 	150,
	        defaultType: 	'numberfield',
	        items: [{
	        	fieldLabel: 'Number of instances',
	            name: 'instances',
	            allowBlank:false,
	            allowDecimals:false,
	            minValue:1,
	            width: 120
	        },
	        {
	        	fieldLabel: 'Inteval',
	            name: 'interval',
	            allowBlank:false,
	            allowDecimals:false,
	            minValue:1,
	            width: 120
	        },
	        {
                xtype: 'combo',
                name: 'intervalunits',
                store: new Ext.data.SimpleStore({
                    fields: ['units'],
                    data: [['millisecond'], ['seconds'], ['minutes'], ['hours'], ['days']]
                }),
                allowBlank: false,
                displayField: 'units',
                valueField: 'units',
                mode: 'local',
                typeAhead: true,
                value: "minutes",
                triggerAction: 'all',
                fieldLabel: 'Interval units',
                width: 120
            }
	        ]
	    });
		
		
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		"Run Process Simulation", 
			height: 	170, 
			width:		350,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[simform],
			buttons:[
				{
					text:"Run Simulation",
					handler:function(){
						dialog.hide();
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Running Process Simulation..."});
						loadMask.show();
						var instancesInput = simform.items.items[0].getValue();
						var intervalInput = simform.items.items[1].getValue();
						var intervalUnit = simform.items.items[2].getValue();
						Ext.Ajax.request({
				            url: ORYX.PATH + 'simulation',
				            method: 'POST',
				            success: function(response) {
				    	   		try {
				    	   			if(response.responseText && response.responseText.length > 0 && response.responseText != "{}") {
				    	   				loadMask.hide();
				    	   				this.facade.raiseEvent({
				    	   		            type: ORYX.CONFIG.EVENT_SIMULATION_SHOW_RESULTS,
				    	   		            results: response.responseText
				    	   		        });
				    	   			} else {
				    	   				loadMask.hide();
				    	   				Ext.MessageBox.minWidth = 300;
				    	   				Ext.Msg.alert('Simulation engine did not return results.');
				    	   			}
				    	   		} catch(e) {
				    	   			loadMask.hide();
				    	   			Ext.MessageBox.minWidth = 300;
				    	   			Ext.Msg.alert('Unable to perform simulation:\n' + e);
				    	   		}
				            }.bind(this),
				            failure: function(){
				            	loadMask.hide();
				            	Ext.Msg.alert('Unable to perform simulation.');
				            },
				            params: {
				            	action: 'runsimulation',
				            	profile: ORYX.PROFILE,
				            	json: ORYX.EDITOR.getSerializedJSON(),
				            	ppdata: ORYX.PREPROCESSING,
				            	numinstances: instancesInput,
				            	interval: intervalInput,
				            	intervalunit: intervalUnit
				            }
				        });
					}.bind(this)
				},{
					text:ORYX.I18N.FromBPMN2Support.close,
					handler:function(){
						dialog.hide();
					}.bind(this)
				}
			]
		});
		// Destroy the panel when hiding
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		// Show the panel
		dialog.show();
	}
});
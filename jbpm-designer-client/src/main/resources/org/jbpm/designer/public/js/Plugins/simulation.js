if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Plugins.Simulation = Clazz.extend({
	construct: function(facade){
		this.facade = facade;

        if(ORYX.READONLY != true) {
            this.facade.offer({
                'name': ORYX.I18N.View.sim.processPathsTitle,
                'functionality': this.findPaths.bind(this),
                'group': "validationandsimulation",
                'icon': ORYX.BASE_FILE_PATH + "images/path.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/simulation.png",
                'description': ORYX.I18N.View.sim.processPaths,
                'index': 1,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.READONLY != true;
    //				profileParamName = "profile";
    //				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //		        regexa = new RegExp( regexSa );
    //		        profileParams = regexa.exec( window.location.href );
    //		        profileParamValue = profileParams[1];
    //				return profileParamValue == "jbpm";
                }.bind(this)
            });

            this.facade.offer({
                'name': ORYX.I18N.View.sim.runSim,
                'functionality': this.runSimulation.bind(this),
                'group': "validationandsimulation",
                'icon': ORYX.BASE_FILE_PATH + "images/control_play.png",
                dropDownGroupIcon : ORYX.BASE_FILE_PATH + "images/simulation.png",
                'description': ORYX.I18N.View.sim.runSim,
                'index': 2,
                'minShape': 0,
                'maxShape': 0,
                'isEnabled': function(){
                    return ORYX.READONLY != true;
    //				profileParamName = "profile";
    //				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    //				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
    //		        regexa = new RegExp( regexSa );
    //		        profileParams = regexa.exec( window.location.href );
    //		        profileParamValue = profileParams[1];
    //				return profileParamValue == "jbpm";
                }.bind(this)
            });
        }
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_BUILD_PATH_SVG, this.autoDisplayPath.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SIMULATION_CLEAR_PATH_SVG, this.resetNodeColors.bind(this));
	},
	autoDisplayPath : function(options) {
		if(options && options.pid) {
			var pathid = options.pid;

            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'info',
                msg         : ORYX.I18N.View.sim.creatingPathImage,
                title       : ''

            });

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
                               this.facade.raiseEvent({
                                   type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                   ntype		: 'error',
                                   msg         : ORYX.I18N.View.sim.errorInvalidData,
                                   title       : ''

                               });
	    	   			}
	    	   		} catch(e) {
                           this.facade.raiseEvent({
                               type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                               ntype		: 'error',
                               msg         : ORYX.I18N.View.sim.errorFindingPath+':\n' + e,
                               title       : ''

                           });
	    	   		}
	            }.bind(this),
	            failure: function(){
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.View.sim.errorFindingPath + '.',
                        title       : ''

                    });
	            },
	            params: {
	            	action: 'getpathinfo',
	            	profile: ORYX.PROFILE,
	            	json: ORYX.EDITOR.getSerializedJSON(),
	            	ppdata: ORYX.PREPROCESSING,
	            	sel: ""
	            }
	        });
		} else {
            this.facade.raiseEvent({
                type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                ntype		: 'error',
                msg         : ORYX.I18N.View.sim.errorUnknownPathId,
                title       : ''

            });
		}
	},
	findPaths: function() {
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'info',
            msg         : ORYX.I18N.View.sim.calculatingPaths,
            title       : ''

        });

		var selection = this.facade.getSelection();
		var selectedId = "";
		var wintitle = ORYX.I18N.View.sim.processPathsTitle;
		if(selection.length == 1) {
			selection.each(function(shape) {
				if(shape.getStencil().title() == "Embedded Subprocess") {
					selectedId = shape.resourceId;
					wintitle = ORYX.I18N.View.sim.subProcessPathsTitle;
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
    		                    header: ORYX.I18N.View.sim.dispColor,
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
    		                    header: ORYX.I18N.View.sim.numElements,
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
    		        		title: '<center>' + ORYX.I18N.View.sim.select + wintitle + ORYX.I18N.View.sim.display + '</center>',
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
    		                    text: ORYX.I18N.View.sim.showPath,
    		                    handler: function(){
    		                    	if(grid.getSelectionModel().getSelectedCell() != null) {
    		                    		var selectedIndex = grid.getSelectionModel().getSelectedCell()[0];
    		                    		var outValue = processpathsStore.getAt(selectedIndex).data['pid'];
    		                        	this.setNodeColors(outValue, this.getDisplayColor(selectedIndex), pathobj[outValue]);
    		                    	} else {
                                        this.facade.raiseEvent({
                                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                            ntype		: 'info',
                                            msg         : ORYX.I18N.View.sim.selectPath,
                                            title       : ''

                                        });
    		                    	}
    		                    }.bind(this)
    		                }, {
    		                    text: ORYX.I18N.Save.close,
    		                    handler: function(){
    		                    	this.resetNodeColors();
    		                    	dialog.hide();
    		                    }.bind(this)
    		                }]
    		    		});	
    		    		dialog.show();
    	   				
    	   			} else {
                           this.facade.raiseEvent({
                               type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                               ntype		: 'error',
                               msg         : ORYX.I18N.View.sim.errorInvalidData,
                               title       : ''

                           });
    	   			}
    	   		} catch(e) {
                       this.facade.raiseEvent({
                           type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                           ntype		: 'error',
                           msg         : ORYX.I18N.View.sim.errorFindingPath+':\n' + e,
                           title       : ''

                       });

    	   		}
            }.bind(this),
            failure: function(){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.View.sim.errorFindingPath+'.',
                    title       : ''

                });
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
	        	fieldLabel: ORYX.I18N.View.sim.numInstances,
	            name: 'instances',
	            allowBlank:false,
	            allowDecimals:false,
	            minValue:1,
	            width: 120
	        },
	        {
	        	fieldLabel: ORYX.I18N.View.sim.interval,
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
                    fields: ['units','value'],
                    data: [['millisecond',ORYX.I18N.LocalHistory.unitsMillisecond],
                        ['seconds',ORYX.I18N.LocalHistory.unitsSeconds],
                        ['minutes',ORYX.I18N.LocalHistory.unitsMinutes],
                        ['hours',ORYX.I18N.LocalHistory.unitsHours],
                        ['days',ORYX.I18N.LocalHistory.unitsDays]]
                }),
                allowBlank: false,
                displayField: 'value',
                valueField: 'units',
                mode: 'local',
                typeAhead: true,
                value: "minutes",
                triggerAction: 'all',
                fieldLabel: ORYX.I18N.View.sim.intervalUnits,
                width: 120
            }
	        ]
	    });
		
		
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			layout: 	'fit',
			plain:		true,
			bodyStyle: 	'padding:5px;',
			title: 		ORYX.I18N.View.sim.runSim,
			height: 	300,
			width:		350,
			modal:		true,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	true,
			items: 		[simform],
			buttons:[
				{
					text: ORYX.I18N.View.sim.runSim,
					handler:function(){
						dialog.hide();

                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'info',
                            msg         : ORYX.I18N.View.sim.runningSim,
                            title       : ''

                        });

						var instancesInput = simform.items.items[0].getValue();
						var intervalInput = simform.items.items[1].getValue();
						var intervalUnit = simform.items.items[2].getValue();
						Ext.Ajax.request({
				            url: ORYX.PATH + 'simulation',
				            method: 'POST',
				            success: function(response) {
				    	   		try {
				    	   			if(response.responseText && response.responseText.length > 0 && response.status == 200) {
				    	   				this.facade.raiseEvent({
				    	   		            type: ORYX.CONFIG.EVENT_SIMULATION_SHOW_RESULTS,
				    	   		            results: response.responseText
				    	   		        });
				    	   			} else {
                                           this.facade.raiseEvent({
                                               type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                               ntype		: 'error',
                                               msg         : ORYX.I18N.View.sim.simNoResults + response.statusText,
                                               title       : ''

                                           });
				    	   			}
				    	   		} catch(e) {
                                       this.facade.raiseEvent({
                                           type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                           ntype		: 'error',
                                           msg         : ORYX.I18N.View.sim.unableToPerform + e,
                                           title       : ''

                                       });
				    	   		}
				            }.bind(this),
				            failure: function(response){
                                this.facade.raiseEvent({
                                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                                    ntype		: 'error',
                                    msg         : ORYX.I18N.View.sim.unableToPerform + response.responseText,
                                    title       : ''

                                });
				            }.bind(this),
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
					text: ORYX.I18N.Save.close,
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
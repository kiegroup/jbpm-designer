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
	}
});
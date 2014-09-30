
/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

//TODO this one fails when importing a stencilset that is already loaded. Hoewver, since an asynchronous callback throws the error, the user doesn#t recognize it.
ORYX.Plugins.SSExtensionLoader = {

    /**
     *	Constructor
     *	@param {Object} Facade: The Facade of the Editor
     */
    construct: function(facade){
        this.facade = facade;
        
        this.facade.offer({
            'name': ORYX.I18N.SSExtensionLoader.add,
            'functionality': this.addSSExtension.bind(this),
            'group': ORYX.I18N.SSExtensionLoader.group,
            'icon': ORYX.BASE_FILE_PATH + "images/add.png",
            'description': ORYX.I18N.SSExtensionLoader.addDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    addSSExtension: function(facade){
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
            text: ORYX.I18N.SSExtensionLoader.loading
        });
        
        var url = ORYX.BASE_FILE_PATH + "stencilsets/extensions/extensions.json";

        new Ajax.Request(url, {
            method: 'GET',
            asynchronous: false,
            onSuccess: (function(transport){
            
                try {
                    eval("var jsonObject = " + transport.responseText);
                    
					var stencilsets = this.facade.getStencilSets();
                    
                    var validExtensions = jsonObject.extensions.findAll(function(extension){
                        var stencilset = stencilsets[extension["extends"]];
						
						if(stencilset) return true;
						else return false;
                    });     
                    
                    var loadedExtensions = validExtensions.findAll(function(extension) {
                    	return stencilsets.values().any(function(ss) { 
                    		if(ss.extensions()[extension.namespace]) return true;
                    		else return false;
                    	})
                    });

					if (validExtensions.size() == 0)
                        this.facade.raiseEvent({
                            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                            ntype		: 'error',
                            msg         : ORYX.I18N.SSExtensionLoader.noExt,
                            title       : ''

                        });
					else
                    	this._showPanel(validExtensions, loadedExtensions, this._loadExtensions.bind(this));
                    
                } 
                catch (e) {
                    this.facade.raiseEvent({
                        type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : ORYX.I18N.SSExtensionLoader.failed1,
                        title       : ''

                    });
				}
                
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });
                
            }).bind(this),
            onFailure: (function(transport){
                this.facade.raiseEvent({
                    type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : ORYX.I18N.SSExtensionLoader.failed2,
                    title       : ''

                });

                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });
            }).bind(this)
        });
    },
	
	_loadExtensions: function(extensions) {
		var stencilsets = this.facade.getStencilSets();
		
		var atLeastOne = false;
		
		// unload unselected extensions
		stencilsets.values().each(function(stencilset) {
			var unselected = stencilset.extensions().values().select(function(ext) { return extensions[ext.namespace] == undefined }); 
			unselected.each(function(ext) {
				stencilset.removeExtension(ext.namespace);
				atLeastOne = true;
			});
		});
		
		// load selected extensions
		extensions.each(function(extension) {
			var stencilset = stencilsets[extension["extends"]];
			
			if(stencilset) {
				stencilset.addExtension(ORYX.CONFIG.SS_EXTENSIONS_FOLDER + extension.definition);
				atLeastOne = true;
			}
		}.bind(this));
		
		if (atLeastOne) {
			stencilsets.values().each(function(stencilset) {
				this.facade.getRules().initializeRules(stencilset);
			}.bind(this));
			this.facade.raiseEvent({
				type: ORYX.CONFIG.EVENT_STENCIL_SET_LOADED,
				lazyLoaded : true
			});
			var selection = this.facade.getSelection();
			this.facade.setSelection();
			this.facade.setSelection(selection);
		}
	},
    
    _showPanel: function(validExtensions, loadedExtensions, successCallback){
    
        // Extract the data
        var data = [];
        validExtensions.each(function(value){
            data.push([value.title, value.definition, value["extends"]])
        });
        
        // Create a new Selection Model
        var sm = new Ext.grid.CheckboxSelectionModel();
        
        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
        	deferRowRender: false,
            id: 'oryx_new_stencilset_extention_grid',
            store: new Ext.data.SimpleStore({
                fields: ['title', 'definition', 'extends']
            }),
            cm: new Ext.grid.ColumnModel([sm, {
                header: ORYX.I18N.SSExtensionLoader.panelTitle,
                width: 200,
                sortable: true,
                dataIndex: 'title'
            }]),
            sm: sm,
            frame: true,
            width: 200,
            height: 200,
            iconCls: 'icon-grid',
            listeners: {
                "render": function(){
                    this.getStore().loadData(data);
                    selectItems.defer(1);
                }
            }
        });
        
        function selectItems() {
        	// Select loaded extensions
    		var selectedRecords = new Array();
    		grid.store.each(function(rec) {
    			if(loadedExtensions.any(function(ext) { return ext.definition == rec.get('definition') }))
    				selectedRecords.push(rec);
    		});
    		sm.selectRecords(selectedRecords);
        }
        
       /* grid.store.on("load", function() { 
        	console.log("okay"); 
        	grid.getSelectionModel().selectRecords(selectedRecords);
        }, this, {delay:500});*/
        
        
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: ORYX.I18N.SSExtensionLoader.panelText,
                style: 'margin:10px;display:block'
            }, grid],
            frame: true,
            buttons: [{
                text: ORYX.I18N.SSExtensionLoader.labelImport,
                handler: function(){
                    var selectionModel = Ext.getCmp('oryx_new_stencilset_extention_grid').getSelectionModel();
                    var result = selectionModel.selections.items.collect(function(item){
                        return item.data;
                    })
                    Ext.getCmp('oryx_new_stencilset_extention_window').close();
                    successCallback(result);
                }.bind(this)
            }, {
                text: ORYX.I18N.SSExtensionLoader.labelCancel,
                handler: function(){
                    Ext.getCmp('oryx_new_stencilset_extention_window').close();
                }.bind(this)
            }]
        })
        
        // Create a new Window
        var window = new Ext.Window({
            id: 'oryx_new_stencilset_extention_window',
            width: 227,
            title: ORYX.I18N.Oryx.title,
            floating: true,
            shim: true,
            modal: true,
            resizable: false,
            autoHeight: true,
            items: [panel]
        })
        
        // Show the window
        window.show();
        
    }
};
ORYX.Plugins.SSExtensionLoader = Clazz.extend(ORYX.Plugins.SSExtensionLoader);

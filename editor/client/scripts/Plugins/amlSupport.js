
/**
 * Copyright (c) 2008
 * Willi Tscheschner
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

/**
 * Supports EPCs by offering a syntax check and export and import ability..
 *
 *
 */
ORYX.Plugins.AMLSupport = Clazz.extend({

    facade: undefined,
    
    /**
     * Offers the plugin functionality:
     *
     */
    construct: function(facade){
        this.facade = facade;
        
        this.facade.offer({
            'name': ORYX.I18N.AMLSupport.imp,
            'functionality': this.importAML.bind(this),
            'group': ORYX.I18N.AMLSupport.group,
            'icon': ORYX.PATH + "images/aris_import_icon.png",
            'description': ORYX.I18N.AMLSupport.impDesc,
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });
        
        
        this.AMLServletURL = '/amlsupport';
    },
    
    
    /**
     * Imports an AML description
     *
     */
    importAML: function(){
        this._showUploadDialog(this.loadDiagrams.bind(this));
    },
    
    
    /**
     * Shows all included diagrams and imports them
     *
     */
    loadDiagrams: function(erdf){
		
		//if parameter does not start with <, it is an error message.
		if(!erdf.startsWith("<")) {
			Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.AMLSupport.failed + erdf);
            ORYX.Log.warn("Import AML failed: " + erdf);
			return;
		}
		
        var doc;
        try {
			// Get the dom-structure
            doc = this.parseToDoc(erdf);
            
            
            // Get the several process diagrams
            var values = $A(doc.firstChild.childNodes).collect(function(node){
                return {
                    title: this.getChildNodesByClassName(node.firstChild, 'oryx-title')[0].textContent,
                    data: node
                }
            }.bind(this))
			
            // Sort the values
            values.sort(function(a, b){
                return a.title > b.title
            })
            
            this._showPanel(values, function(result){
            
                if (result.length > 1) {
                
                    var requestsSuccessfull = true;
                    
                    var loadedDiagrams = [];
                    
                    // Generate for every diagram a new url
                    result.each(function(item){
                    
                        // Set url, dummy data, and params for the request, to get a new url
                        var url = '/backend/poem' + ORYX.CONFIG.ORYX_NEW_URL + "?stencilset=/stencilsets/epc/epc.json";
                        var dummyData = '<div class="processdata"><div class="-oryx-canvas" id="oryx-canvas123" style="display: none; width:1200px; height:600px;"><a href="/stencilsets/epc/epc.json" rel="oryx-stencilset"></a><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span></div></div>';
                        var dummySVG = '<svg/>';
                        var params = {
                            data: dummyData,
                            svg: dummySVG,
                            title: item.name,
                            summary: "",
                            type: "http://b3mn.org/stencilset/epc#"
                        };
                        
                        // Send the request
                        requestsSuccessfull = this.sendRequest(url, params, function(transport){
                        
                            var loc = transport.getResponseHeader('location');
                            var id = this.getNodesByClassName(item.data, "div", "-oryx-canvas")[0].getAttribute("id");
                            
                            loadedDiagrams.push({
                                name: item.name,
                                data: item.data,
                                url: loc,
                                id: id
                            });
                            
                        }.bind(this));
                        
                        // If an error during the reqest occurs, return
                        if (!requestsSuccessfull) {
                            throw $break
                        }
                        
                    }.bind(this));
                    
                    // If an error during the reqest occurs, return
                    if (!requestsSuccessfull) {
                        return
                    }
                    
                    
                    // Replace all IDs within every process diagrams with the new url
                    // First, find all 'oryx-uriref' spans
                    var allURIRefs = loadedDiagrams.collect(function(item){
                        return $A(this.getNodesByClassName(item.data, "span", "oryx-refuri"))
                    }.bind(this)).flatten()
					
                    // Second, replace it, if there is a url for it, otherwise, delete the link
                    allURIRefs.each(function(uriRef){
                    
                        if (uriRef.textContent.length == 0) {
                            return
                        }
                        
                        var findURL = loadedDiagrams.find(function(item){
                            return uriRef.textContent == item.id
                        })
                        
                        uriRef.textContent = findURL ? findURL.url : "";
                        
                    })
                    
                    
                    
                    // Send all diagrams to the server
                    loadedDiagrams.each(function(item){
                    
                        // Get the URL
                        var url = item.url;
                        // Define the svg
                        var dummySVG = '<svg/>';
                        // Get the data
                        var data = DataManager.serialize(item.data);
                        data = "<div " + data.slice(data.search("class"));
                        // Set the parameter for the request
                        var params = {
                            data: data,
                            svg: dummySVG
                        };
                        
                        // Send the request
                        requestsSuccessfull = this.sendRequest(url, params);
                        
                        
                        // If an error during the reqest occurs, return
                        if (!requestsSuccessfull) {
                            throw $break
                        }
                        
                    }.bind(this));
                    
                    // If an error during the reqest occurs, return
                    if (!requestsSuccessfull) {
                        return
                    }
                    
                    // Show the results	
                    this._showResultPanel(loadedDiagrams.collect(function(item){
                        return {
                            name: item.name,
                            url: item.url
                        }
                    }));
                    
                }
                else {
                
                    var erdfDOM = result[0].data;
                    
					// Delete all uri-refs
                    $A(this.getNodesByClassName(erdfDOM, "span", "oryx-refuri")).each(function(node){
                        node.textContent = ""
                    });
					
					// Import the erdf strucutre
					this.facade.importERDF(erdfDOM);
                
                }  
                
            }.bind(this))
            
        } 
        catch (e) {
            Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.AMLSupport.failed2 + e);
            ORYX.Log.warn("Import AML failed: " + e);
        }
        
    },
    
    
    /**
     *
     *
     * @param {Object} url
     * @param {Object} params
     * @param {Object} successcallback
     */
    sendRequest: function(url, params, successcallback){
    
        var suc = false;
        
        new Ajax.Request(url, {
            method: 'POST',
            asynchronous: false,
            parameters: params,
            onSuccess: function(transport){
            
                suc = true;
                
                if (successcallback) {
                    successcallback(transport)
                }
                
            }
.bind(this)            ,
            
            onFailure: function(transport){
            
                Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.AMLSupport.failed2);
                ORYX.Log.warn("Import AML failed: " + transport.responseText);
                
            }
.bind(this)            ,
            
            on403: function(transport){
            
                Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.AMLSupport.noRights);
                ORYX.Log.warn("Import AML failed: " + transport.responseText);
                
            }
.bind(this)
        });
        
        
        return suc;
        
    },
    
    
    /**
     * Give all child nodes with the given class name
     *
     * @param {Object} doc
     * @param {Object} id
     */
    getChildNodesByClassName: function(doc, id){
    
        return $A(doc.childNodes).findAll(function(el){
            return $A(el.attributes).any(function(attr){
                return attr.nodeName == 'class' && attr.nodeValue == id
            })
        })
        
    },
    
    /**
     * Give all child nodes with the given class name
     *
     * @param {Object} doc
     * @param {Object} id
     */
    getNodesByClassName: function(doc, tagName, className){
    
        return $A(doc.getElementsByTagName(tagName)).findAll(function(el){
            return $A(el.attributes).any(function(attr){
                return attr.nodeName == 'class' && attr.nodeValue == className
            })
        })
        
    },
    
    /**
     * Parses the erdf string to an xml-document
     *
     * @param {Object} erdfString
     */
    parseToDoc: function(erdfString){
    
        erdfString = erdfString.startsWith('<?xml') ? erdfString : '<?xml version="1.0" encoding="utf-8"?>' + erdfString + '';
        
        var parser = new DOMParser();
        
        return parser.parseFromString(erdfString, "text/xml");
        
    },
    
    
    /**
     * Opens a upload dialog.
     *
     */
    _showUploadDialog: function(successCallback){
    
        var form = new Ext.form.FormPanel({
            frame: true,
            bodyStyle: 'padding:5px;',
            defaultType: 'textfield',
            labelAlign: 'left',
            buttonAlign: 'right',
            fileUpload: true,
            enctype: 'multipart/form-data',
            items: [{
                text: ORYX.I18N.AMLSupport.panelText,
                style: 'font-size:12px;margin-bottom:10px;display:block;',
                xtype: 'label'
            }, {
                fieldLabel: ORYX.I18N.AMLSupport.file,
                inputType: 'file',
                labelStyle: 'width:50px;',
                itemCls: 'ext_specific_window_overflow'
            }]
        });
        
        var dialog = new Ext.Window({
            autoCreate: true,
            title: ORYX.I18N.AMLSupport.importBtn,
            height: 'auto',
            width: 420,
            modal: true,
            collapsible: false,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: false,
            items: [form],
            buttons: [{
                text: ORYX.I18N.AMLSupport.impText,
                handler: function(){
                
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: ORYX.I18N.AMLSupport.get
                    });
                    loadMask.show();
                    
                    form.form.submit({
                        url: ORYX.PATH + this.AMLServletURL,
                        success: function(f, a){
                        
                            loadMask.hide();
                            dialog.hide();
                            successCallback(a.result);
                            
                        }
.bind(this)                        ,
                        failure: function(f, a){
                        
                            loadMask.hide();
                            dialog.hide();
                            
                            Ext.MessageBox.show({
                                title: 'Error',
                                msg: a.response.responseText.substring(a.response.responseText.indexOf("content:'") + 9, a.response.responseText.indexOf("'}")),
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.ERROR
                            });
                        }
                    });
                }
.bind(this)
            }, {
                text: ORYX.I18N.AMLSupport.close,
                handler: function(){
                    dialog.hide();
                }
.bind(this)
            }]
        });
        
        dialog.on('hide', function(){
            dialog.destroy(true);
            delete dialog;
        });
        dialog.show();
    },
    
    _showPanel: function(values, successCallback){
    
    
        // Extract the data
        var data = [];
        values.each(function(value){
            data.push([value.title, value.data])
        });
        
        // Create a new Selection Model
        var sm = new Ext.grid.CheckboxSelectionModel({
            header: '',
            //singleSelect	:true
        });
        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
            //ddGroup          	: 'gridPanel',
            //enableDragDrop   	: true,
            //cls				: 'ext_specialize_gridPanel_aml',
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['title']
            }),
            cm: new Ext.grid.ColumnModel([sm, {
                header: ORYX.I18N.AMLSupport.title,
                width: 260,
                sortable: true,
                dataIndex: 'title'
            }, ]),
            sm: sm,
            frame: true,
            width: 300,
            height: 300,
            iconCls: 'icon-grid',
            //draggable: true
        });
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                html: ORYX.I18N.AMLSupport.selectDiagrams,
                style: 'margin:5px;display:block'
            }, grid],
            height: 'auto',
            frame: true
        })
        
        // Create a new Window
        var extWindow = new Ext.Window({
            width: 327,
            height: 'auto',
            title: ORYX.I18N.Oryx.title,
            floating: true,
            shim: true,
            modal: true,
            resizable: false,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: ORYX.I18N.AMLSupport.impText,
                handler: function(){
                
                    var loadMask = new Ext.LoadMask(Ext.getBody(), {
                        msg: ORYX.I18N.AMLSupport.impProgress
                    });
                    loadMask.show();
                    
                    var selectionModel = grid.getSelectionModel();
                    var result = selectionModel.selections.items.collect(function(item){
                        return {
                            name: item.json[0],
                            data: item.json[1]
                        };
                    })
                    extWindow.close();
                    
                    window.setTimeout(function(){
                    
                        successCallback(result);
                        loadMask.hide();
                        
                    }
.bind(this), 100);
                    
                    
                }
.bind(this)
            }, {
                text: ORYX.I18N.AMLSupport.cancel,
                handler: function(){
                    extWindow.close();
                }
.bind(this)
            }]
        })
        
        // Show the window
        extWindow.show();
        
    },
    
    _showResultPanel: function(values){
    
    
        // Extract the data
        var data = [];
        values.each(function(value){
            data.push([value.name, '<a href="' + value.url + '" target="_blank">' + value.url + '</a>'])
        });
        
        
        // Create a new Grid with a selection box
        var grid = new Ext.grid.GridPanel({
            store: new Ext.data.SimpleStore({
                data: data,
                fields: ['name', 'url']
            }),
            cm: new Ext.grid.ColumnModel([{
                header: ORYX.I18N.AMLSupport.name,
                width: 260,
                sortable: true,
                dataIndex: 'name'
            }, {
                header: "URL",
                width: 300,
                sortable: true,
                dataIndex: 'url'
            }]),
            frame: true,
            width: 500,
            height: 300,
            iconCls: 'icon-grid'
        });
        
        // Create a new Panel
        var panel = new Ext.Panel({
            items: [{
                xtype: 'label',
                text: ORYX.I18N.AMLSupport.allImported,
                style: 'margin:5px;display:block'
            }, grid],
            height: 'auto',
            frame: true
        })
        
        // Create a new Window
        var extWindow2 = new Ext.Window({
            width: 'auto',
            title: ORYX.I18N.Oryx.title,
            floating: true,
            shim: true,
            modal: true,
            resizable: false,
            autoHeight: true,
            items: [panel],
            buttons: [{
                text: ORYX.I18N.AMLSupport.ok,
                handler: function(){
                
                    extWindow2.close()
                    
                }
.bind(this)
            }]
        })
        
        // Show the window
        extWindow2.show();
        
    },
    /**
     *
     * @param {Object} message
     */
    throwErrorMessage: function(message){
        Ext.Msg.alert(ORYX.I18N.Oryx.title, message)
    },

});

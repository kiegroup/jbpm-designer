
/**
 * Copyright (c) 2008, Gero Decker, refactored by Kai Schlichting
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
    ORYX.Plugins = {};


ORYX.Plugins.FeedbackPlugin = ORYX.Plugins.AbstractPlugin.extend({
    construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        
        this.facade.offer({
            'name': "Feedback",
            'functionality': this.showWindow.bind(this),
            'group': "Help",
            'description': "Contact us for any kind of feedback!",
            'icon': ORYX.PATH + "images/feedback.png",
            'index': 0,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    showWindow: function(){
        var window = new Ext.Window({
            title: "Contact us for any kind of feedback!",
            width: '50%',
            closable: true,
            items: [{
                xtype: 'form',
                url: ORYX.CONFIG.ROOT_PATH + "feedback",
                frame: true,
                labelAlign: 'top',
                items: [{
                    layout: 'column',
                    items: [{
                        columnWidth: 0.5,
                        layout: 'form',
                        items: {
                            xtype: 'textfield',
                            name: 'name',
                            fieldLabel: 'Name',
                            anchor: '98%'
                        }
                    }, {
                        columnWidth: 0.5,
                        layout: 'form',
                        items: {
                            xtype: 'textfield',
                            name: 'email',
                            fieldLabel: 'E-Mail',
                            vtype: 'email',
                            anchor: '100%'
                        }
                    }]
                }, {
                    xtype: 'combo',
                    anchor: '100%',
                    name: 'subject',
                    store: new Ext.data.SimpleStore({
                        fields: ['title'],
                        data: [['[Feedback] '], ['[Bug Report] '], ['[Feature Request] ']]
                    }),
                    allowBlank: false,
                    displayField: 'title',
                    mode: 'local',
                    typeAhead: true,
                    triggerAction: 'all',
                    fieldLabel: 'Subject *'
                }, {
                    xtype: 'textarea',
                    name: 'description',
                    allowBlank: false,
                    fieldLabel: 'Description/ Message *',
                    emptyText: '* Please provide as detailed information as possible so that we can understand your request.\n* For bug reports, please list the steps how to reproduce the problem and describe the output you expected.',
                    height: 200,
                    anchor: '100%'
                }, {
                    xtype: 'checkbox',
                    boxLabel: 'Attach current model <img src="' + ORYX.CONFIG.ROOT_PATH + '/images/information.png" ext:qtip="This information can be helpful for debugging purposes. If your model contains some sensible data, remove it before or uncheck this behavior."/>',
                    hideLabel: true,
                    checked: true,
                    listeners: {
                        check: function(checkbox, checked){
                            var hidden = window.find('itemId', 'hiddenModel')[0];
                            if (checked) {
                                hidden.setValue(this.facade.getSerializedJSON());
                            }
                            else {
                                hidden.setValue(undefined);
                            }
                        }
.bind(this)
                    }
                }, {
                    xtype: 'hidden',
                    itemId: 'hiddenModel',
                    name: 'model'
                }, {
                    xtype: 'textarea',
                    fieldLabel: 'Information about your browser and environment <img src="' + ORYX.CONFIG.ROOT_PATH + '/images/information.png" ext:qtip="This information has been auto-detected from your browser. It can be helpful if you encountered a bug associated with browser-specific behavior."/>',
                    anchor: '100%',
                    name: 'environment',
                    value: this.getEnv()
                }],
                buttons: [{
                    text: 'Submit',
                    handler: function(button){
                        button.ownerCt.form.submit();
                    }
                }]
            }]
        });
        
        window.show();
    },
    
    getEnv: function(){
        var env = "";
        
        env += "Browser: " + navigator.userAgent;
        
        env += "\n\nBrowser Plugins: ";
        if (navigator.plugins) {
            for (var i = 0; i < navigator.plugins.length; i++) {
                var plugin = navigator.plugins[i];
                env += plugin.name + ", ";
            }
        }
        
        if ((typeof(screen.width) != "undefined") && (screen.width && screen.height)) 
            env += "\n\nScreen Resolution: " + screen.width + 'x' + screen.height;
        
        return env;
    }
});

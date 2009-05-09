
/**
 * Copyright (c) 2009, Kai Schlichting
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
            'name': ORYX.I18N.Feedback.name,
            'functionality': this.showWindow.bind(this),
            'group': "Help",
            'description': ORYX.I18N.Feedback.desc,
            'icon': ORYX.PATH + "images/feedback.png",
            'index': 0,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    showWindow: function(){
        var window = new Ext.Window({
            title: ORYX.I18N.Feedback.pTitle,
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
                            fieldLabel: ORYX.I18N.Feedback.pName,
                            anchor: '98%'
                        }
                    }, {
                        columnWidth: 0.5,
                        layout: 'form',
                        items: {
                            xtype: 'textfield',
                            name: 'email',
                            fieldLabel: ORYX.I18N.Feedback.pEmail,
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
                    fieldLabel: ORYX.I18N.Feedback.pSubject + ' *'
                }, {
                    xtype: 'textarea',
                    name: 'description',
                    allowBlank: false,
                    fieldLabel: ORYX.I18N.Feedback.pMsg + ' *',
                    emptyText: ORYX.I18N.Feedback.pEmpty,
                    height: 200,
                    anchor: '100%'
                }, {
                    xtype: 'checkbox',
                    boxLabel: ORYX.I18N.Feedback.pAttach + ' <img src="' + ORYX.CONFIG.ROOT_PATH + '/images/information.png" ext:qtip="' + ORYX.I18N.Feedback.pAttachDesc + '"/>',
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
                    fieldLabel: ORYX.I18N.Feedback.pBrowser + ' <img src="' + ORYX.CONFIG.ROOT_PATH + '/images/information.png" ext:qtip="' + ORYX.I18N.Feedback.pBrowserDesc + '"/>',
                    anchor: '100%',
                    name: 'environment',
                    value: this.getEnv()
                }],
                buttons: [{
                    text: ORYX.I18N.Feedback.submit,
                    handler: function(button){
                        button.ownerCt.form.submit({
                            waitMsg: ORYX.I18N.Feedback.sending, 
                            success: function(form, action){
                                Ext.Msg.alert(ORYX.I18N.Feedback.success, ORYX.I18N.Feedback.successMsg);
                                window.close();
                            },
                            failure: function(form, action){
                                Ext.Msg.alert(ORYX.I18N.Feedback.failure, ORYX.I18N.Feedback.failureMsg);
                            }
                        });
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

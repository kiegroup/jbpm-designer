/**
 * Copyright (c) 2008
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Bruno Colaço, Zhen Peng
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();
	
ORYX.Plugins.BpelImporter = Clazz.extend({

	facade: undefined,
	linksWriten: undefined,

	construct: function(facade) {
		this.facade = facade;

        this.facade.offer({

			'name':"ImportBPEL",
			'functionality': this.openUploadDialog.bind(this),
			'group': "BPEL",
			'icon': ORYX.PATH + "images/folder_page_white.png",
			'description': "Import a BPEL file",
			'index': 1,
			'minShape': 0,
			'maxShape': 0}); 
	},
	
	/**
	 * Opens a upload dialog.
	 * 
	 */
	
	openUploadDialog: function(){
		
        // simple array store
	    var types = [['BPEL']];
	    
        var store = new Ext.data.SimpleStore({
            fields: ['typeId'],
            data: types
        })
        
	    var combo = new Ext.form.ComboBox({
	        store: store,
	        displayField:'typeId',
	        mode: 'local',
	        triggerAction: 'all',
	        emptyText:'Select a type...',
	        selectOnFocus:true,
	        editable:false,
	        fieldLabel: 'Type',
	        allowBlank: false
	    });

		
		var form = new Ext.form.FormPanel({
			frame : true,
			defaultType : 'textfield',
		 	waitMsgTarget : true,
		  	labelAlign : 'left',
		  	buttonAlign: 'right',
		  	fileUpload : true,
		  	enctype : 'multipart/form-data',
		  	items : [
		  	{
		  	    fieldLabel: 'Name', 
		  	    name: 'name',
		  	    allowBlank: false
		  	}, combo,
		  	{
		    	fieldLabel : 'File',
		    	inputType : 'file',
		    	allowBlank: false
		  	}]
		});

		var submit =form.addButton({
			text:"Submit",
			handler: function()
			{
				form.form.submit({
		      		url: ORYX.PATH + '/bpelimporter',
		      		waitMsg: "Importing...",
		      		success: function(f,a){
		      			Ext.Msg.alert('Status', 'success');
						/*dialog.hide();
						var erdf = a.response.responseText;
						var parsedErdf = parser.parseFromString('<?xml version="1.0" encoding="utf-8"?><html>'+erdf+'</html>',"text/xml");	
						alert(erdf);*/
		      		},
					failure: function(f,a){
						Ext.Msg.alert('Status', 'failure');
					//  dialog.hide();
					/*	Ext.MessageBox.show({
           					title: 'Error',
          	 				msg: a.response.responseText.substring(a.response.responseText.indexOf("content:'")+ 9, a.response.responseText.indexOf("'}")),
           					buttons: Ext.MessageBox.OK,
           					icon: Ext.MessageBox.ERROR
       					});*/

		      		}
		  		});
		  	}
		})


		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: 'Import from file...', 
			height: 180, 
			width: 350, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			proxyDrag: true,
			resizable:false,
			items: [form]
		});
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.show();
		
	}
	
});
	
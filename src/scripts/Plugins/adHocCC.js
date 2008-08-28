/**
 * Copyright (c) 2008
 * Stefan Krumnow
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

/**
 * Supports creating and editing ad-hoc completion conditions
 * 
 */
ORYX.Plugins.AdHocCC = Clazz.extend({

	facade: undefined,
	UNSAVED_RESOURCE: "unsaved",

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;
		
		this.facade.offer({
			'name':ORYX.I18N.AdHocCC.compl,
			'functionality': this.editCC.bind(this),
			'group': ORYX.I18N.AdHocCC.group,
			'icon': ORYX.PATH + "images/adhoc.gif",
			'description': ORYX.I18N.AdHocCC.complDesc,
			'index': 0,
			'minShape': 1,
			'maxShape': 1
			// ISSUE: Should the Context Area this Plugin is creating be removed?
		});
	},
	
	
	/**
	 * Opens a Dialog that can be used to edit an ad-hoc activity's completion condition
	 * 
	 */
	editCC: function(){	
	
		/*
		 * 	check pre conditions
		 */ 
		var elements = this.facade.getSelection();
		if (elements.length != 1) {
			// Should not happen!
			this.openErroDialog(ORYX.I18N.AdHocCC.notOne);
			return ; 
		}
		var adHocActivity = elements[0];
		if (adHocActivity._stencil.id() != "http://b3mn.org/stencilset/bpmnexec#Subprocess" || !adHocActivity.properties['oryx-isadhoc']){
			this.openErroDialog(ORYX.I18N.AdHocCC.nodAdHocCC); 
			return ;
		}
	
		/*
		 * 	load relevant data
		 */ 	
		var oldCC = adHocActivity.properties['oryx-adhoccompletioncondition'];
		var taskArrayFields = ['resourceID', 'resourceName'];
		var taskArray = []; 
		var stateArrayFields = ['state'];
		var stateArray = [ ['ready'], ['skipped'], ['completed'] ];
		var dataArrayFields = ['resourceID_FieldName', 'dataNameAndFieldName'];
		var dataArray = [];

		var parser = new DOMParser();
		
		var childNodes = adHocActivity.getChildNodes();
		for (var i = 0; i < childNodes.length; i++) {
			var child = childNodes[i];
			if (child._stencil.id() == "http://b3mn.org/stencilset/bpmnexec#Task") {
				var resourceName = child.properties['oryx-name'];
				var resourceID = child.resourceId;
				if (typeof resourceID == "undefined") {
					DataManager.__persistDOM(this.facade);
					resourceID = child.resourceId;
					if (typeof resourceID == "undefined") {
						resourceID = this.UNSAVED_RESOURCE;
						resourceName = resourceName + " (unsaved)";
					}
				}
				taskArray.push([resourceID, resourceName]);
			}
			else if (child._stencil.id() == "http://b3mn.org/stencilset/bpmnexec#DataObject") {
				var resourceName = child.properties['oryx-name'];
				var resourceID = child.resourceId;
				if (typeof resourceID == "undefined") {
					DataManager.__persistDOM(this.facade);
					resourceID = child.resourceId;
					if (typeof resourceID == "undefined") {
						resourceID = this.UNSAVED_RESOURCE;
						resourceName = resourceName + " (unsaved)";
					}
				}
				var dataModelString = child.properties['oryx-datamodel'];
				var dataModel = parser.parseFromString(dataModelString,"text/xml");
				var rootXMLNode = dataModel.childNodes[0];
				if (rootXMLNode != null){
					var childXMLNodes = rootXMLNode.childNodes;
					for (var j = 0; j < childXMLNodes.length; j++) {
						var dataFieldTagName = childXMLNodes[j].attributes['name'].nodeValue;
						if (dataFieldTagName != null) {
							dataArray.push([[resourceID, dataFieldTagName], resourceName + "/" + dataFieldTagName]);
						}
					}
				}
			}
		}
				
		/*
		 * 	initialiaze UI
		 */ 
		var taskStore = new Ext.data.SimpleStore({
   			fields: taskArrayFields,
    		data : taskArray
		});
		
		var stateStore = new Ext.data.SimpleStore({
   			fields: stateArrayFields,
    		data : stateArray
		});
		
		var dataStore = new Ext.data.SimpleStore({
   			fields: dataArrayFields,
    		data : dataArray
		});
		
		var taskCombo = new Ext.form.ComboBox({
    		store: taskStore,
			valueField: taskArrayFields[0],
			displayField: taskArrayFields[1],
    		emptyText: ORYX.I18N.AdHocCC.selectTask,
			typeAhead: true,
    		mode: 'local',
    		triggerAction: 'all',
   			selectOnFocus: true,
			editable: false,
			width: 180
		});
		
		var stateCombo = new Ext.form.ComboBox({
    		store: stateStore,
    		displayField: stateArrayFields[0],
    		emptyText: ORYX.I18N.AdHocCC.selectState,
			typeAhead: true,
    		mode: 'local',
    		triggerAction: 'all',
   			selectOnFocus: true,
			editable: false,
			width: 180
		});

		var addStateExprButton = new Ext.Button({
			text: ORYX.I18N.AdHocCC.addExp,
			handler: function(){
				var task = taskCombo.getValue();
				var state = stateCombo.getValue();
				if (task != this.UNSAVED_RESOURCE && task != "" && state != "") {
					this.addStringToTextArea(textArea, "stateExpression('"+task+"', '"+state+"')");
					taskCombo.setValue("");
					stateCombo.setValue("");
				}
			}.bind(this)
		});
		
		var dataCombo = new Ext.form.ComboBox({
    		store: dataStore,
			valueField: dataArrayFields[0],
    		displayField: dataArrayFields[1],
    		emptyText: ORYX.I18N.AdHocCC.selectDataField,
			typeAhead: true,
    		mode: 'local',
    		triggerAction: 'all',
   			selectOnFocus: true,
			editable: false,
			width: 180
		});
		
		var valueField = new Ext.form.TextField({
			width: 180,
			emptyText: ORYX.I18N.AdHocCC.enterEqual,
		});
		
		var addDataExprButton = new Ext.Button({
			text: ORYX.I18N.AdHocCC.addExp,
			handler: function(){
				var data = dataCombo.getValue();
				var value = valueField.getValue();
				if (data != null && data[0] != this.UNSAVED_RESOURCE && value != "") {
					this.addStringToTextArea(textArea, "dataExpression('"+data[0]+"', '"+data[1]+"', '"+value+"')");
					dataCombo.setValue("");
					valueField.setValue("");
				}
			}.bind(this)
		});
		
		var addAndButton = new Ext.Button({
			text: ORYX.I18N.AdHocCC.and, 
			minWidth: 50,
			handler: function(){
				this.addStringToTextArea(textArea, "&");
			}.bind(this)
		});
					
		var addOrButton = new Ext.Button({
			text: ORYX.I18N.AdHocCC.or, 
			minWidth: 50,
			handler: function(){
				this.addStringToTextArea(textArea, "|");
			}.bind(this)
		});
		
		var addLPButton = new Ext.Button({
			text: "(", 
			minWidth: 50,
			handler: function(){
				this.addStringToTextArea(textArea, "(");
			}.bind(this)
		});
					
		var addRPButton = new Ext.Button({
			text: ")", 
			minWidth: 50,
			handler: function(){
				this.addStringToTextArea(textArea, ")");
			}.bind(this)
		});
		
		var addNotButton = new Ext.Button({
			text: ORYX.I18N.AdHocCC.not, 
			minWidth: 50,
			handler: function(){
				this.addStringToTextArea(textArea, "!");
			}.bind(this)
		});
		
		var textArea = new Ext.form.TextArea({
			width: 418,
			height: 100,
			value: oldCC
		});
		
		var clearButton = new Ext.Button({
			text: ORYX.I18N.AdHocCC.clearCC,
			handler: function(){
				textArea.setValue("");
			}
		});
		
		var win = new Ext.Window({ 
			width: 450,
			//minWidth: 400,
			height: 485,
			//minHeight: 450,
			resizable: false,
			minimizable: false,
			modal: true,
			autoScroll: true,
			title: ORYX.I18N.AdHocCC.editCC,
			layout: 'table',
			defaults: {
		        bodyStyle:'padding:3px;background-color:transparent;border-width:0px'
		    },
			layoutConfig: {
		        columns: 7
		    },
			items: [
				{ items: [new Ext.form.Label({text: ORYX.I18N.AdHocCC.addExecState, style: 'font-size:12px;'})], colspan: 7},
				{}, {items: [taskCombo], colspan: 6},
				{}, {items: [stateCombo], colspan: 4}, {items: [addStateExprButton]}, {},
				{colspan: 7},
				{ items: [new Ext.form.Label({text: ORYX.I18N.AdHocCC.addDataExp, style: 'font-size:12px;'})], colspan: 7},	
				{}, {items: [dataCombo], colspan: 6},
				{}, {items: [valueField], colspan: 4}, {items: [addDataExprButton]}, {},
				{colspan: 7},
				{ items: [new Ext.form.Label({text: ORYX.I18N.AdHocCC.addLogOp, style: 'font-size:12px;'})], colspan: 7},	
				{}, {items: [addAndButton]}, {items: [addOrButton]}, {items: [addLPButton]}, {items: [addRPButton]}, {items: [addNotButton]}, {},
				{colspan: 7},
				{ items: [new Ext.form.Label({text: ORYX.I18N.AdHocCC.curCond, style: 'font-size:12px;'})], colspan: 7},
				{}, {items: [textArea], colspan: 5}, {},
				{colspan: 5}, {items: [clearButton]}, {}
			],
			buttons: [
				{
		        	text: 'Apply',
		        	handler: function(){
		            	win.hide();
						adHocActivity.properties['oryx-adhoccompletioncondition'] = textArea.getValue();
						// ISSUE: This might be done more elegant using a refresh-event implemented in the property window plugin
						this.facade.setSelection([]);
						this.facade.setSelection(elements);
		        	}.bind(this)
		    	},
				{
		        	text: 'Cancel',
		        	handler: function(){win.hide();}
		    	}
			],
	    	keys: [{
	        	key: 27,  // Esc
	        	fn: function(){win.hide();}
	    	}]
		});
		win.show();	
	},
	
	
	/**
	 * Adds an string into a text area
	 * 
	 * NOTE: This implementation does only work with Gecko browsers (e.g. Mozilla Firefox)
	 * 
	 * @param {TextField} textArea
	 * @param {String} string
	 */
	addStringToTextArea: function(textArea, string){
		var selectionStart = textArea.getEl().dom.selectionStart;
		var selectionEnd = textArea.getEl().dom.selectionEnd;
		var currentValue = textArea.getValue();
		textArea.setValue(currentValue.substring(0, selectionStart)+string+currentValue.substring(selectionEnd));
		textArea.getEl().dom.selectionStart = selectionStart + string.length;
		textArea.getEl().dom.selectionEnd = textArea.getEl().dom.selectionStart;
	},
	
	/**
	 * Opens an error dialog
	 * 
	 * @param {String} errorMsg
	 */
	openErroDialog: function(errorMsg){
		Ext.MessageBox.show({
           title: 'Error',
           msg: errorMsg,
           buttons: Ext.MessageBox.OK,
           icon: Ext.MessageBox.ERROR
       });
	}	
	
});

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


if(!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}

if (!ORYX.FieldEditors) {
	ORYX.FieldEditors = {};
}

if (!ORYX.AssociationEditors) {
	ORYX.AssociationEditors = {};
}

if (!ORYX.LabelProviders) {
    ORYX.LabelProviders = {};
}



ORYX.Plugins.PropertyWindow = {

	facade: undefined,

	construct: function(facade) {
		// Reference to the Editor-Interface
		this.facade = facade;

		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_SHOW_PROPERTYWINDOW, this.init.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.onSelectionChanged.bind(this));
		this.init();
	},
	
	init: function(){
		// The parent div-node of the grid
		this.node = ORYX.Editor.graft("http://www.w3.org/1999/xhtml",
			null,
			['div']);

		// If the current property in focus is of type 'Date', the date format
		// is stored here.
		this.currentDateFormat;

		// the properties array
		this.popularProperties = [];
		this.simulationProperties = [];
		this.properties = [];
		
		/* The currently selected shapes whos properties will shown */
		this.shapeSelection = new Hash();
		this.shapeSelection.shapes = new Array();
		this.shapeSelection.commonProperties = new Array();
		this.shapeSelection.commonPropertiesValues = new Hash();
		
		this.updaterFlag = false;

		// creating the column model of the grid.
		this.columnModel = new Ext.grid.ColumnModel([
			{
				//id: 'name',
				header: ORYX.I18N.PropertyWindow.name,
				dataIndex: 'name',
				width: 90,
				sortable: true,
				renderer: this.tooltipRenderer.bind(this)
			}, {
				//id: 'value',
				header: ORYX.I18N.PropertyWindow.value,
				dataIndex: 'value',
				id: 'propertywindow_column_value',
				width: 110,
				editor: new Ext.form.TextField({
					allowBlank: false
				}),
				renderer: this.renderer.bind(this)
			},
			{
				header: "Desk",
				dataIndex: 'groupname',
				hidden: true,
				sortable: true
			}
		]);

		// creating the store for the model.
        this.dataSource = new Ext.data.GroupingStore({
			proxy: new Ext.data.MemoryProxy(this.properties),
			reader: new Ext.data.ArrayReader({}, [
				{name: 'groupname'},
				{name: 'name'},
				{name: 'value'},
				{name: 'icons'},
				{name: 'gridProperties'}
			]),
			sortInfo: {field: 'name', direction: "ASC"},
			groupField: 'groupname'
        });
		this.dataSource.load();
		
		this.grid = new Ext.grid.EditorGridPanel({
			clicksToEdit: 1,
			stripeRows: true,
			autoExpandColumn: "propertywindow_column_value",
			width:'auto',
			// the column model
			colModel: this.columnModel,
			enableHdMenu: false,
			view: new Ext.grid.GroupingView({
				forceFit: true ,
				groupTextTpl: '{[values.rs.first().data.groupname]}'
			}),
			
			// the data store
			store: this.dataSource
			
		});

		region = this.facade.addToRegion('east', new Ext.Panel({
			width: 220,
			layout: "fit",
			border: false,
			//title: 'Properties',
			items: [
				this.grid 
			]
		}), ORYX.I18N.PropertyWindow.title)

		// Register on Events
		this.grid.on('beforeedit', this.beforeEdit, this, true);
		this.grid.on('afteredit', this.afterEdit, this, true);
		this.grid.view.on('refresh', this.hideMoreAttrs, this, true);
		
		//this.grid.on(ORYX.CONFIG.EVENT_KEYDOWN, this.keyDown, this, true);
		
		// Renderer the Grid
		this.grid.enableColumnMove = false;
		//this.grid.render();

		// Sort as Default the first column
		//this.dataSource.sort('name');

	},
	
	// Select the Canvas when the editor is ready
	selectDiagram: function() {
		this.shapeSelection.shapes = [this.facade.getCanvas()];
		
		this.setPropertyWindowTitle();
		this.identifyCommonProperties();
		this.createProperties();
	},
	
	specialKeyDown: function(field, event) {
		// If there is a TextArea and the Key is an Enter
		if(field instanceof Ext.form.TextArea && event.button == ORYX.CONFIG.KEY_Code_enter) {
			// Abort the Event
			return false
		}
	},
	tooltipRenderer: function(value, p, record) {
		/* Prepare tooltip */
		p.cellAttr = 'title="' + record.data.gridProperties.tooltip + '"';
		return value;
	},
	
	renderer: function(value, p, record) {
		this.tooltipRenderer(value, p, record);
		
		if (record.data.gridProperties.labelProvider) {
		    // there is a label provider to render the value.
		    // we pass it the value
		    return record.data.gridProperties.labelProvider(value);
		}
				
		if(value instanceof Date) {
			// TODO: Date-Schema is not generic
			value = value.dateFormat(ORYX.I18N.PropertyWindow.dateFormat);
		} else if(String(value).search("<a href='") < 0) {
			// Shows the Value in the Grid in each Line
			value = String(value).gsub("<", "&lt;");
			value = String(value).gsub(">", "&gt;");
			value = String(value).gsub("%", "&#37;");
			value = String(value).gsub("&", "&amp;");

			if(record.data.gridProperties.type == ORYX.CONFIG.TYPE_COLOR) {
				value = "<div class='prop-background-color' style='background-color:" + value + "' />";
			}			

			record.data.icons.each(function(each) {
				if(each.name == value) {
					if(each.icon) {
						value = "<img src='" + each.icon + "' /> " + value;
					}
				}
			});
		}

		return value;
	},

	beforeEdit: function(option) {
			var editorGrid 		= this.dataSource.getAt(option.row).data.gridProperties.editor;
			var editorRenderer 	= this.dataSource.getAt(option.row).data.gridProperties.renderer;
	
			if(editorGrid) {
				// Disable KeyDown
				this.facade.disableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
	
				option.grid.getColumnModel().setEditor(1, editorGrid);
				
				editorGrid.field.row = option.row;
				// Render the editor to the grid, therefore the editor is also available 
				// for the first and last row
				editorGrid.render(this.grid);
				
				//option.grid.getColumnModel().setRenderer(1, editorRenderer);
				editorGrid.setSize(option.grid.getColumnModel().getColumnWidth(1), editorGrid.height);
			} else {
				return false;
			}
			
			var key = this.dataSource.getAt(option.row).data.gridProperties.propId;
			
			this.oldValues = new Hash();
			this.shapeSelection.shapes.each(function(shape){
				this.oldValues[shape.getId()] = shape.properties[key];
			}.bind(this)); 
	},

	afterEdit: function(option) {
		//Ext1.0: option.grid.getDataSource().commitChanges();
		option.grid.getStore().commitChanges();

		var key 			 = option.record.data.gridProperties.propId;
		var selectedElements = this.shapeSelection.shapes;
		
		var oldValues 	= this.oldValues;	
		
		var newValue	= option.value;
		var facade		= this.facade;
		

		// Implement the specific command for property change
		var commandClass = ORYX.Core.Command.extend({
			construct: function(){
				this.key 		= key;
				this.selectedElements = selectedElements;
				this.oldValues = oldValues;
				this.newValue 	= newValue;
				this.facade		= facade;
			},			
			execute: function(){
				this.selectedElements.each(function(shape){
					if(!shape.getStencil().property(this.key).readonly()) {
						shape.setProperty(this.key, this.newValue);
					}
				}.bind(this));
				this.facade.setSelection(this.selectedElements);
				this.facade.getCanvas().update();
				this.facade.updateSelection();
			},
			rollback: function(){
				this.selectedElements.each(function(shape){
					shape.setProperty(this.key, this.oldValues[shape.getId()]);
				}.bind(this));
				this.facade.setSelection(this.selectedElements);
				this.facade.getCanvas().update();
				this.facade.updateSelection();
			}
		})		
		// Instanciated the class
		var command = new commandClass();
		
		// Execute the command
		this.facade.executeCommands([command]);


		// extended by Kerstin (start)
//
		this.facade.raiseEvent({
			type 		: ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, 
			elements	: selectedElements,
			key			: key,
			value		: option.value
		});
		// extended by Kerstin (end)
	},
	
	// Changes made in the property window will be shown directly
	editDirectly:function(key, value){
		this.shapeSelection.shapes.each(function(shape){
			if(!shape.getStencil().property(key).readonly()) {
				shape.setProperty(key, value);
				//shape.update();
			}
		}.bind(this));
		
		/* Propagate changed properties */
		var selectedElements = this.shapeSelection.shapes;
		
		this.facade.raiseEvent({
			type 		: ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, 
			elements	: selectedElements,
			key			: key,
			value		: value
		});

		this.facade.getCanvas().update();
		
	},
	
	// if a field becomes invalid after editing the shape must be restored to the old value
	updateAfterInvalid : function(key) {
		this.shapeSelection.shapes.each(function(shape) {
			if(!shape.getStencil().property(key).readonly()) {
				shape.setProperty(key, this.oldValues[shape.getId()]);
				shape.update();
			}
		}.bind(this));
		
		this.facade.getCanvas().update();
	},

	// extended by Kerstin (start)	
	dialogClosed: function(data) {
		var row = this.field ? this.field.row : this.row 
		this.scope.afterEdit({
			grid:this.scope.grid, 
			record:this.scope.grid.getStore().getAt(row), 
			//value:this.scope.grid.getStore().getAt(this.row).get("value")
			value: data
		})
		// reopen the text field of the complex list field again
		this.scope.grid.startEditing(row, this.col);
	},
	// extended by Kerstin (end)
	
	/**
	 * Changes the title of the property window panel according to the selected shapes.
	 */
	setPropertyWindowTitle: function() {
		if(this.shapeSelection.shapes.length == 1) {
			// add the name of the stencil of the selected shape to the title
				region.setTitle(ORYX.I18N.PropertyWindow.title +' ('+this.shapeSelection.shapes.first().getStencil().title()+')' );
		} else {
			region.setTitle(ORYX.I18N.PropertyWindow.title +' ('
							+ this.shapeSelection.shapes.length
							+ ' '
							+ ORYX.I18N.PropertyWindow.selected 
							+')');
		}
	},
	/**
	 * Sets this.shapeSelection.commonPropertiesValues.
	 * If the value for a common property is not equal for each shape the value
	 * is left empty in the property window.
	 */
	setCommonPropertiesValues: function() {
		this.shapeSelection.commonPropertiesValues = new Hash();
		this.shapeSelection.commonProperties.each(function(property){
			var key = property.prefix() + "-" + property.id();
			var emptyValue = false;
			var firstShape = this.shapeSelection.shapes.first();
			
			this.shapeSelection.shapes.each(function(shape){
				if(firstShape.properties[key] != shape.properties[key]) {
					emptyValue = true;
				}
			}.bind(this));
			
			/* Set property value */
			if(!emptyValue) {
				this.shapeSelection.commonPropertiesValues[key]
					= firstShape.properties[key];
			}
		}.bind(this));
	},
	
	/**
	 * Returns the set of stencils used by the passed shapes.
	 */
	getStencilSetOfSelection: function() {
		var stencils = new Hash();
		
		this.shapeSelection.shapes.each(function(shape) {
			stencils[shape.getStencil().id()] = shape.getStencil();
		})
		return stencils;
	},
	
	/**
	 * Identifies the common Properties of the selected shapes.
	 */
	identifyCommonProperties: function() {
		this.shapeSelection.commonProperties.clear();
		
		/* 
		 * A common property is a property, that is part of 
		 * the stencil definition of the first and all other stencils.
		 */
		var stencils = this.getStencilSetOfSelection();
		var firstStencil = stencils.values().first();
		var comparingStencils = stencils.values().without(firstStencil);
		
		
		if(comparingStencils.length == 0) {
			this.shapeSelection.commonProperties = firstStencil.properties();
		} else {
			var properties = new Hash();
			
			/* put all properties of on stencil in a Hash */
			firstStencil.properties().each(function(property){
				properties[property.namespace() + '-' + property.id() 
							+ '-' + property.type()] = property;
			});
			
			/* Calculate intersection of properties. */
			
			comparingStencils.each(function(stencil){
				var intersection = new Hash();
				stencil.properties().each(function(property){
					if(properties[property.namespace() + '-' + property.id()
									+ '-' + property.type()]){
						intersection[property.namespace() + '-' + property.id()
										+ '-' + property.type()] = property;
					}
				});
				properties = intersection;	
			});
			
			this.shapeSelection.commonProperties = properties.values();
		}
	},
	
	onSelectionChanged: function(event) {
		/* Event to call afterEdit method */
		this.grid.stopEditing();
		
		/* Selected shapes */
		this.shapeSelection.shapes = event.elements;
		
		/* Case: nothing selected */
		if(event.elements) {
			if(event.elements.length == 0) {
				this.shapeSelection.shapes = [this.facade.getCanvas()];
			}
		} else {
			this.shapeSelection.shapes = [this.facade.getCanvas()];
		}
		
		/* subselection available */
		if(event.subSelection){
			this.shapeSelection.shapes = [event.subSelection];
		}
		
		this.setPropertyWindowTitle();
		this.identifyCommonProperties();
		this.setCommonPropertiesValues();
		
		// Create the Properties
		this.createProperties();
	},
	
	/**
	 * Creates the properties for the ExtJS-Grid from the properties of the
	 * selected shapes.
	 */
	createProperties: function() {
		this.properties = [];
		this.popularProperties = [];
		this.simulationProperties = [];
		
		if(this.shapeSelection.commonProperties) {
			
			// add new property lines
			this.shapeSelection.commonProperties.each((function(pair, index) {

				var key = pair.prefix() + "-" + pair.id();
				
				// Get the property pair
				var name		= pair.title();
				var icons		= [];
				var attribute	= this.shapeSelection.commonPropertiesValues[key];
				
				var editorGrid = undefined;
				var editorRenderer = null;
				
				var refToViewFlag = false;

				var editorClass = ORYX.FieldEditors[pair.type()];
				 
				if (editorClass !== undefined) {
					editorGrid = editorClass.init.bind(this, key, pair, icons, index)();
					if (editorGrid == null) {
						return; // don't insist, the editor won't be created this time around.
					}
					// Register Event to enable KeyDown
					editorGrid.on('beforehide', this.facade.enableEvent.bind(this, ORYX.CONFIG.EVENT_KEYDOWN));
					editorGrid.on('specialkey', this.specialKeyDown.bind(this));
				} else {
					if(!pair.readonly()){
						switch(pair.type()) {
						case ORYX.CONFIG.TYPE_STRING:
							// If the Text is MultiLine
							if(pair.wrapLines()) {
								// Set the Editor as TextArea
								var editorTextArea = new Ext.form.TextArea({alignment: "tl-tl", allowBlank: pair.optional(),  msgTarget:'title', maxLength:pair.length()});
								editorTextArea.on('keyup', function(textArea, event) {
									this.editDirectly(key, textArea.getValue());
								}.bind(this));								

								editorGrid = new Ext.Editor(editorTextArea);
							} else {
								// If not, set the Editor as InputField
								var editorInput = new Ext.form.TextField({allowBlank: pair.optional(),  msgTarget:'title', maxLength:pair.length()});
								editorInput.on('keyup', function(input, event) {
									this.editDirectly(key, input.getValue());
								}.bind(this));

								// reverts the shape if the editor field is invalid
								editorInput.on('blur', function(input) {
									if(!input.isValid(false))
										this.updateAfterInvalid(key);
								}.bind(this));

								editorInput.on("specialkey", function(input, e) {
									if(!input.isValid(false))
										this.updateAfterInvalid(key);
								}.bind(this));

								editorGrid = new Ext.Editor(editorInput);
							}
							break;
						case ORYX.CONFIG.TYPE_BOOLEAN:
							// Set the Editor as a CheckBox
							var editorCheckbox = new Ext.form.Checkbox();
							editorCheckbox.on('check', function(c,checked) {
								this.editDirectly(key, checked);
							}.bind(this));

							editorGrid = new Ext.Editor(editorCheckbox);
							break;
						case ORYX.CONFIG.TYPE_INTEGER:
							// Set as an Editor for Integers
							var numberField = new Ext.form.NumberField({allowBlank: pair.optional(), allowDecimals:false, msgTarget:'title', minValue: pair.min(), maxValue: pair.max()});
							numberField.on('keyup', function(input, event) {
								this.editDirectly(key, input.getValue());
							}.bind(this));							

							editorGrid = new Ext.Editor(numberField);
							break;
						case ORYX.CONFIG.TYPE_FLOAT:
							// Set as an Editor for Float
							var numberField = new Ext.form.NumberField({ allowBlank: pair.optional(), allowDecimals:true, msgTarget:'title', minValue: pair.min(), maxValue: pair.max()});
							numberField.on('keyup', function(input, event) {
								this.editDirectly(key, input.getValue());
							}.bind(this));

							editorGrid = new Ext.Editor(numberField);

							break;
						case ORYX.CONFIG.TYPE_COLOR:
							// Set as a ColorPicker
							// Ext1.0 editorGrid = new gEdit(new form.ColorField({ allowBlank: pair.optional(),  msgTarget:'title' }));

							var editorPicker = new Ext.ux.ColorField({ allowBlank: pair.optional(),  msgTarget:'title', facade: this.facade });

							/*this.facade.registerOnEvent(ORYX.CONFIG.EVENT_COLOR_CHANGE, function(option) {
								this.editDirectly(key, option.value);
							}.bind(this));*/

							editorGrid = new Ext.Editor(editorPicker);

							break;
						case ORYX.CONFIG.TYPE_CHOICE:
							var items = pair.items();

							var options = [];
							items.each(function(value) {
								if(value.value() == attribute)
									attribute = value.title();

								if(value.refToView()[0])
									refToViewFlag = true;

								options.push([value.icon(), value.title(), value.value()]);

								icons.push({
									name: value.title(),
									icon: value.icon()
								});
							});

							var store = new Ext.data.SimpleStore({
								fields: [{name: 'icon'},
								         {name: 'title'},
								         {name: 'value'}	],
								         data : options
							});

							// Set the grid Editor

							var editorCombo = new Ext.form.ComboBox({
								editable: false,
								tpl: '<tpl for="."><div class="x-combo-list-item">{[(values.icon) ? "<img src=\'" + values.icon + "\' />" : ""]} {title}</div></tpl>',
								store: store,
								displayField:'title',
								valueField: 'value',
								typeAhead: true,
								mode: 'local',
								triggerAction: 'all',
								selectOnFocus:true
							});

							editorCombo.on('select', function(combo, record, index) {
								this.editDirectly(key, combo.getValue());
							}.bind(this))

							editorGrid = new Ext.Editor(editorCombo);

							break;
						case ORYX.CONFIG.TYPE_DATE:
							var currFormat = ORYX.I18N.PropertyWindow.dateFormat
							if(!(attribute instanceof Date))
								attribute = Date.parseDate(attribute, currFormat)
								editorGrid = new Ext.Editor(new Ext.form.DateField({ allowBlank: pair.optional(), format:currFormat,  msgTarget:'title'}));
							break;

						case ORYX.CONFIG.TYPE_TEXT:

							var cf = new Ext.form.ComplexTextField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;

						case ORYX.CONFIG.TYPE_VARDEF:
							var cf = new Ext.form.ComplexVardefField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_EXPRESSION:
							var cf = new Ext.form.ComplexExpressionField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_CALLEDELEMENT:
							var cf = new Ext.form.ComplexCalledElementField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_CUSTOM:
							var cf = new Ext.form.ComplexCustomField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade,
								title:pair.title(),
								attr:attribute
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_ACTION:
							var cf = new Ext.form.ComplexActionsField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
						
						case ORYX.CONFIG.TYPE_GLOBAL:
							var cf = new Ext.form.ComplexGlobalsField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_IMPORT:
							var cf = new Ext.form.ComplexImportsField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
						
						case ORYX.CONFIG.TYPE_DATAINPUT:
                                                        var cf = new Ext.form.ComplexDataInputField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_DATAOUTPUT:
                                                        var cf = new Ext.form.ComplexDataOutputField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
						case ORYX.CONFIG.TYPE_DATAASSIGNMENT:
							var cf = new Ext.form.ComplexDataAssignmenField({
								allowBlank: pair.optional(),
								dataSource:this.dataSource,
								grid:this.grid,
								row:index,
								facade:this.facade,
								shapes:this.shapeSelection.shapes
							});
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							
							// extended by Kerstin (start)
						case ORYX.CONFIG.TYPE_COMPLEX:

							var cf = new Ext.form.ComplexListField({ allowBlank: pair.optional()}, pair.complexItems(), key, this.facade);
							cf.on('dialogClosed', this.dialogClosed, {scope:this, row:index, col:1,field:cf});							
							editorGrid = new Ext.Editor(cf);
							break;
							// extended by Kerstin (end)

							// extended by Gerardo (Start)
						case "CPNString":
							var editorInput = new Ext.form.TextField(
									{
										allowBlank: pair.optional(),
										msgTarget:'title', 
										maxLength:pair.length(), 
										enableKeyEvents: true
									});

							editorInput.on('keyup', function(input, event) {
								this.editDirectly(key, input.getValue());
							}.bind(this));

							editorGrid = new Ext.Editor(editorInput);							
							break;
							// extended by Gerardo (End)

						default:
							var editorInput = new Ext.form.TextField({ allowBlank: pair.optional(),  msgTarget:'title', maxLength:pair.length(), enableKeyEvents: true});
						editorInput.on('keyup', function(input, event) {
							this.editDirectly(key, input.getValue());
						}.bind(this));

						editorGrid = new Ext.Editor(editorInput);
						}


						// Register Event to enable KeyDown
						editorGrid.on('beforehide', this.facade.enableEvent.bind(this, ORYX.CONFIG.EVENT_KEYDOWN));
						editorGrid.on('specialkey', this.specialKeyDown.bind(this));

					} else if(pair.type() === ORYX.CONFIG.TYPE_URL || pair.type() === ORYX.CONFIG.TYPE_DIAGRAM_LINK){
						attribute = String(attribute).search("http") !== 0 ? ("http://" + attribute) : attribute;
						attribute = "<a href='" + attribute + "' target='_blank'>" + attribute.split("://")[1] + "</a>"
					}
				}
				
				// Push to the properties-array
				if(pair.visible() && (pair.id() != "origbordercolor" && pair.id() != "origbgcolor" && pair.id() != "isselectable")) {
					var proceed = true;
					if(this.shapeSelection.shapes.length == 1 && this.shapeSelection.shapes.first().getStencil().idWithoutNs() == "Task") {
						if(pair.fortasktypes() && pair.fortasktypes().length > 0) {
							var foundtasktype = false;
							var tts = pair.fortasktypes().split("|");
							for (i = 0; i < tts.size(); i++) {
								if(tts[i] == this.shapeSelection.shapes.first().properties["oryx-tasktype"]) {
									foundtasktype = true;
								}
							}
							if(!foundtasktype) {
								proceed = false;
							}
						}
					}
					
					if(proceed) {
						// Popular Properties are those with a refToView set or those which are set to be popular
						if (pair.refToView()[0] || refToViewFlag || pair.popular()) {
							pair.setPopular();
						}
						
						if (pair.simulation()) {
							pair.setSimulation();
						}
						
						
						if(pair.popular()) {
							this.popularProperties.push([ORYX.I18N.PropertyWindow.oftenUsed, name, attribute, icons, {
								editor: editorGrid,
								propId: key,
								type: pair.type(),
								tooltip: pair.description(),
								renderer: editorRenderer,
								labelProvider: this.getLabelProvider(pair)
							}]);
						} else if(pair.simulation()) {
							this.simulationProperties.push([ORYX.I18N.PropertyWindow.simulationProps, name, attribute, icons, {
								editor: editorGrid,
								propId: key,
								type: pair.type(),
								tooltip: pair.description(),
								renderer: editorRenderer,
								labelProvider: this.getLabelProvider(pair)
							}]);
						} else {	
							this.properties.push([ORYX.I18N.PropertyWindow.moreProps, name, attribute, icons, {
								editor: editorGrid,
								propId: key,
								type: pair.type(),
								tooltip: pair.description(),
								renderer: editorRenderer,
								labelProvider: this.getLabelProvider(pair)
							}]);
						}
					}
				}

			}).bind(this));
		}

		this.setProperties();
	},
	
	/**
	 * Gets a label provider from the registered label providers
	 * according to the id of the label provider registered on the stencil.
	 */
    getLabelProvider: function(stencil) {
       lp = ORYX.LabelProviders[stencil.labelProvider()];
       if (lp) {
           return lp(stencil);
       }
       return null;
    },
	
	hideMoreAttrs: function(panel) {
		// TODO: Implement the case that the canvas has no attributes
		if (this.properties.length <= 0){ return }
		
		// collapse the "more attr" group
		this.grid.view.toggleGroup(this.grid.view.getGroupId(this.properties[0][0]), false);
		
		// prevent the more attributes pane from closing after a attribute has been edited
		this.grid.view.un("refresh", this.hideMoreAttrs, this);
	},

	setProperties: function() {
		var partProps = this.popularProperties.concat(this.properties);
		var props = partProps.concat(this.simulationProperties);
		this.dataSource.loadData(props);
	}
}
ORYX.Plugins.PropertyWindow = Clazz.extend(ORYX.Plugins.PropertyWindow);

/**
 * Editor for complex type
 * 
 * When starting to edit the editor, it creates a new dialog where new attributes
 * can be specified which generates json out of this and put this 
 * back to the input field.
 * 
 * This is implemented from Kerstin Pfitzner
 * 
 * @param {Object} config
 * @param {Object} items
 * @param {Object} key
 * @param {Object} facade
 */


Ext.form.ComplexListField = function(config, items, key, facade){
    Ext.form.ComplexListField.superclass.constructor.call(this, config);
	this.items 	= items;
	this.key 	= key;
	this.facade = facade;
};

/**
 * This is a special trigger field used for complex properties.
 * The trigger field opens a dialog that shows a list of properties.
 * The entered values will be stored as trigger field value in the JSON format.
 */
Ext.extend(Ext.form.ComplexListField, Ext.form.TriggerField,  {
	/**
     * @cfg {String} triggerClass
     * An additional CSS class used to style the trigger button.  The trigger will always get the
     * class 'x-form-trigger' and triggerClass will be <b>appended</b> if specified.
     */
    triggerClass:	'x-form-complex-trigger',
	readOnly:		true,
	emptyText: 		ORYX.I18N.PropertyWindow.clickIcon,
		
	/**
	 * Builds the JSON value from the data source of the grid in the dialog.
	 */
	buildValue: function() {
		var ds = this.grid.getStore();
		ds.commitChanges();
		
		if (ds.getCount() == 0) {
			return "";
		}
		
		var jsonString = "[";
		for (var i = 0; i < ds.getCount(); i++) {
			var data = ds.getAt(i);		
			jsonString += "{";	
			for (var j = 0; j < this.items.length; j++) {
				var key = this.items[j].id();
				jsonString += key + ':' + ("" + data.get(key)).toJSON();
				if (j < (this.items.length - 1)) {
					jsonString += ", ";
				}
			}
			jsonString += "}";
			if (i < (ds.getCount() - 1)) {
				jsonString += ", ";
			}
		}
		jsonString += "]";
		
		jsonString = "{'totalCount':" + ds.getCount().toJSON() + 
			", 'items':" + jsonString + "}";
		return Object.toJSON(jsonString.evalJSON());
	},
	
	/**
	 * Returns the field key.
	 */
	getFieldKey: function() {
		return this.key;
	},
	
	/**
	 * Returns the actual value of the trigger field.
	 * If the table does not contain any values the empty
	 * string will be returned.
	 */
    getValue : function(){
		// return actual value if grid is active
		if (this.grid) {
			return this.buildValue();			
		} else if (this.data == undefined) {
			return "";
		} else {
			return this.data;
		}
    },
	
	/**
	 * Sets the value of the trigger field.
	 * In this case this sets the data that will be shown in
	 * the grid of the dialog.
	 * 
	 * @param {Object} value The value to be set (JSON format or empty string)
	 */
	setValue: function(value) {	
		if (value.length > 0) {
			// set only if this.data not set yet
			// only to initialize the grid
			if (this.data == undefined) {
				this.data = value;
			}
		}
	},
	
	/**
	 * Returns false. In this way key events will not be propagated
	 * to other elements.
	 * 
	 * @param {Object} event The keydown event.
	 */
	keydownHandler: function(event) {
		return false;
	},
	
	/**
	 * The listeners of the dialog. 
	 * 
	 * If the dialog is hidded, a dialogClosed event will be fired.
	 * This has to be used by the parent element of the trigger field
	 * to reenable the trigger field (focus gets lost when entering values
	 * in the dialog).
	 */
    dialogListeners : {
        show : function(){ // retain focus styling
            this.onFocus();	
			this.facade.registerOnEvent(ORYX.CONFIG.EVENT_KEYDOWN, this.keydownHandler.bind(this));
			this.facade.disableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
			return;
        },
        hide : function(){

            var dl = this.dialogListeners;
            this.dialog.un("show", dl.show,  this);
            this.dialog.un("hide", dl.hide,  this);
			
			this.dialog.destroy(true);
			this.grid.destroy(true);
			delete this.grid;
			delete this.dialog;
			
			this.facade.unregisterOnEvent(ORYX.CONFIG.EVENT_KEYDOWN, this.keydownHandler.bind(this));
			this.facade.enableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
			
			// store data and notify parent about the closed dialog
			// parent has to handel this event and start editing the text field again
			this.fireEvent('dialogClosed', this.data);
			
			Ext.form.ComplexListField.superclass.setValue.call(this, this.data);
        }
    },	
	
	/**
	 * Builds up the initial values of the grid.
	 * 
	 * @param {Object} recordType The record type of the grid.
	 * @param {Object} items      The initial items of the grid (columns)
	 */
	buildInitial: function(recordType, items) {
		var initial = new Hash();
		
		for (var i = 0; i < items.length; i++) {
			var id = items[i].id();
			initial[id] = items[i].value();
		}
		
		var RecordTemplate = Ext.data.Record.create(recordType);
		return new RecordTemplate(initial);
	},
	
	/**
	 * Builds up the column model of the grid. The parent element of the
	 * grid.
	 * 
	 * Sets up the editors for the grid columns depending on the 
	 * type of the items.
	 * 
	 * @param {Object} parent The 
	 */
	buildColumnModel: function(parent) {
		var cols = [];
		for (var i = 0; i < this.items.length; i++) {
			var id 		= this.items[i].id();
			var header 	= this.items[i].name();
			var width 	= this.items[i].width();
			var type 	= this.items[i].type();
			var editor;
			
			if (type == ORYX.CONFIG.TYPE_STRING) {
				editor = new Ext.form.TextField({ allowBlank : this.items[i].optional(), width : width});
			} else if (type == ORYX.CONFIG.TYPE_CHOICE) {				
				var items = this.items[i].items();
				var select = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", parent, ['select', {style:'display:none'}]);
				var optionTmpl = new Ext.Template('<option value="{value}">{value}</option>');
				items.each(function(value){ 
					optionTmpl.append(select, {value:value.value()}); 
				});				
				
				editor = new Ext.form.ComboBox(
					{ editable: false, typeAhead: true, triggerAction: 'all', transform:select, lazyRender:true,  msgTarget:'title', width : width});			
			} else if (type == ORYX.CONFIG.TYPE_BOOLEAN) {
				editor = new Ext.form.Checkbox( { width : width } );
			} else if (type == "xpath") {
				//TODO set the xpath type as string, same editor as string.
				editor = new Ext.form.TextField({ allowBlank : this.items[i].optional(), width : width});
			}
					
			cols.push({
				id: 		id,
				header: 	header,
				dataIndex: 	id,
				resizable: 	true,
				editor: 	editor,
				width:		width
	        });
			
		}
		return new Ext.grid.ColumnModel(cols);
	},
	
	/**
	 * After a cell was edited the changes will be commited.
	 * 
	 * @param {Object} option The option that was edited.
	 */
	afterEdit: function(option) {
		option.grid.getStore().commitChanges();
	},
		
	/**
	 * Before a cell is edited it has to be checked if this 
	 * cell is disabled by another cell value. If so, the cell editor will
	 * be disabled.
	 * 
	 * @param {Object} option The option to be edited.
	 */
	beforeEdit: function(option) {

		var state = this.grid.getView().getScrollState();
		
		var col = option.column;
		var row = option.row;
		var editId = this.grid.getColumnModel().config[col].id;
		// check if there is an item in the row, that disables this cell
		for (var i = 0; i < this.items.length; i++) {
			// check each item that defines a "disable" property
			var item = this.items[i];
			var disables = item.disable();
			if (disables != undefined) {
				
				// check if the value of the column of this item in this row is equal to a disabling value
				var value = this.grid.getStore().getAt(row).get(item.id());
				for (var j = 0; j < disables.length; j++) {
					var disable = disables[j];
					if (disable.value == value) {
						
						for (var k = 0; k < disable.items.length; k++) {
							// check if this value disables the cell to select 
							// (id is equals to the id of the column to edit)
							var disItem = disable.items[k];
							if (disItem == editId) {
								this.grid.getColumnModel().getCellEditor(col, row).disable();
								return;
							}
						}
					}
				}		
			}
		}
		this.grid.getColumnModel().getCellEditor(col, row).enable();
		//this.grid.getView().restoreScroll(state);
	},
	
    /**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }	
		
		//if(!this.dialog) { 
		
			var dialogWidth = 0;
			var recordType 	= [];
			
			for (var i = 0; i < this.items.length; i++) {
				var id 		= this.items[i].id();
				var width 	= this.items[i].width();
				var type 	= this.items[i].type();	
					
				if (type == ORYX.CONFIG.TYPE_CHOICE) {
					type = ORYX.CONFIG.TYPE_STRING;
				}
						
				dialogWidth += width;
				recordType[i] = {name:id, type:type};
			}			
			
			if (dialogWidth > 800) {
				dialogWidth = 800;
			}
			dialogWidth += 22;
			
			var data = this.data;
			if (data == "") {
				// empty string can not be parsed
				data = "{}";
			}
			
			
			var ds = new Ext.data.Store({
		        proxy: new Ext.data.MemoryProxy(eval("(" + data + ")")),				
				reader: new Ext.data.JsonReader({
		            root: 'items',
		            totalProperty: 'totalCount'
		        	}, recordType)
	        });
			ds.load();
					
				
			var cm = this.buildColumnModel();
			
			this.grid = new Ext.grid.EditorGridPanel({
				store:		ds,
		        cm:			cm,
				stripeRows: true,
				clicksToEdit : 1,
				autoHeight:true,
		        selModel: 	new Ext.grid.CellSelectionModel()
		    });	
			
									
			//var gridHead = this.grid.getView().getHeaderPanel(true);
			var toolbar = new Ext.Toolbar(
			[{
				text: ORYX.I18N.PropertyWindow.add,
				handler: function(){
					var ds = this.grid.getStore();
					var index = ds.getCount();
					this.grid.stopEditing();
					var p = this.buildInitial(recordType, this.items);
					ds.insert(index, p);
					ds.commitChanges();
					this.grid.startEditing(index, 0);
				}.bind(this)
			},{
				text: ORYX.I18N.PropertyWindow.rem,
		        handler : function(){
					var ds = this.grid.getStore();
					var selection = this.grid.getSelectionModel().getSelectedCell();
					if (selection == undefined) {
						return;
					}
					this.grid.getSelectionModel().clearSelections();
		            this.grid.stopEditing();					
					var record = ds.getAt(selection[0]);
					ds.remove(record);
					ds.commitChanges();           
				}.bind(this)
			}]);			
		
			// Basic Dialog
			this.dialog = new Ext.Window({ 
				autoScroll: true,
				autoCreate: true, 
				title: ORYX.I18N.PropertyWindow.complex, 
				height: 350, 
				width: dialogWidth, 
				modal:true,
				collapsible:false,
				fixedcenter: true, 
				shadow:true, 
				proxyDrag: true,
				keys:[{
					key: 27,
					fn: function(){
						this.dialog.hide
					}.bind(this)
				}],
				items:[toolbar, this.grid],
				bodyStyle:"background-color:#FFFFFF",
				buttons: [{
	                text: ORYX.I18N.PropertyWindow.ok,
	                handler: function(){
	                	this.grid.getView().refresh();
	                    this.grid.stopEditing();	
						// store dialog input
						this.data = this.buildValue();
						this.dialog.hide()
	                }.bind(this)
	            }, {
	                text: ORYX.I18N.PropertyWindow.cancel,
	                handler: function(){
	                	this.dialog.hide()
	                }.bind(this)
	            }]
			});		
				
			this.dialog.on(Ext.apply({}, this.dialogListeners, {
	       		scope:this
	        }));
		
			this.dialog.show();	
		
	
			this.grid.on('beforeedit', 	this.beforeEdit, 	this, true);
			this.grid.on('afteredit', 	this.afterEdit, 	this, true);
			
			this.grid.render();			
	    
		/*} else {
			this.dialog.show();		
		}*/
		
	}
});


Ext.form.ComplexTextField = Ext.extend(Ext.form.TriggerField,  {

	defaultAutoCreate : {tag: "textarea", rows:1, style:"height:16px;overflow:hidden;" },

    /**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function(){
		
        if(this.disabled){
            return;
        }	
		        
		var grid = new Ext.form.TextArea({
	        anchor		: '100% 100%',
			value		: this.value,
			listeners	: {
				focus: function(){
					this.facade.disableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
				}.bind(this)
			}
		})
		
		
		// Basic Dialog
		var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: ORYX.I18N.PropertyWindow.text, 
			height		: 500, 
			width		: 500, 
			modal		: true,
			collapsible	: false,
			fixedcenter	: true, 
			shadow		: true, 
			proxyDrag	: true,
			keys:[{
				key	: 27,
				fn	: function(){
						dialog.hide()
				}.bind(this)
			}],
			items		:[grid],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					//this.focus.defer(10, this);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
                	// store dialog input
					var value = grid.getValue();
					this.setValue(value);
					
					this.dataSource.getAt(this.row).set('value', value)
					this.dataSource.commitChanges()

					dialog.hide()
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide()
                }.bind(this)
            }]
		});		
				
		dialog.show();		
		grid.render();

		this.grid.stopEditing();
		grid.focus( false, 100 );
		
	}
});

Ext.form.ComplexCustomField = Ext.extend(Ext.form.TriggerField,  {
	onTriggerClick : function() {
    	if(this.disabled){
            return;
        }
    	
    	Ext.Ajax.request({
            url: ORYX.PATH + 'customeditors',
            method: 'POST',
            success: function(response) {
    	   		try {
    	   			if(response.responseText && response.responseText.length > 0) {
    	   				var customEditorsJSON = response.responseText.evalJSON();
    	   				var customEditorsObj = customEditorsJSON["editors"];
    	   				if(customEditorsObj[this.title]) {
    	   					var dialog = new Ext.Window({ 
    	   						layout		: 'anchor',
    	   						autoCreate	: true, 
    	   						title		: 'Custom Editor for ' + this.title, 
    	   						height		: 300, 
    	   						width		: 450, 
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
    	   									dialog.hide()
    	   							}.bind(this)
    	   						}],
    	   						items : [{
    	   					        xtype : "component",
    	   					        id    : 'customeditorswindow',
    	   					        autoEl : {
    	   					            tag : "iframe",
    	   					            src : customEditorsObj[this.title],
    	   					            width: "100%",
    	   					            height: "100%"
    	   					        }
    	   					    }],
    	   						listeners : {
    	   							hide: function(){
    	   								this.fireEvent('dialogClosed', this.value);
    	   								dialog.destroy();
    	   							}.bind(this)				
    	   						},
    	   						buttons		: [{
    	   			                text: ORYX.I18N.PropertyWindow.ok,
    	   			                handler: function(){	 
    	   			                	var outValue = document.getElementById('customeditorswindow').contentWindow.getEditorValue();
    	   			                	this.setValue(outValue);
    	   								this.dataSource.getAt(this.row).set('value', outValue)
    	   								this.dataSource.commitChanges()
    	   								dialog.hide();
    	   			                }.bind(this)
    	   			            }, {
    	   			                text: ORYX.I18N.PropertyWindow.cancel,
    	   			                handler: function(){
    	   								this.setValue(this.value);
    	   			                	dialog.hide()
    	   			                }.bind(this)
    	   			            }]
    	   					});		
    	   					dialog.show();		
    	   					this.grid.stopEditing();
    	   				} else {
    	   					Ext.Msg.alert('Unable to find custom editor info for' + this.title);
    	   				}
    	   			} else {
    	   				Ext.Msg.alert('Invalid Custom Editors data.');
    	   			}
    	   		} catch(e) {
    	   			Ext.Msg.alert('Error applying Custom Editor data:\n' + e);
    	   		}
            }.bind(this),
            failure: function(){
            	Ext.Msg.alert('Error applying Custom Editor data.');
            },
            params: {
            	profile: ORYX.PROFILE
            }
        });
	}
});

Ext.form.ComplexImportsField = Ext.extend(Ext.form.TriggerField,  {
	/**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function() {
    	if(this.disabled){
            return;
        }
    	
    	var ImportDef = Ext.data.Record.create([{
            name: 'import'
        }]);
    	
    	var importsProxy = new Ext.data.MemoryProxy({
            root: []
        });
    	
    	var imports = new Ext.data.Store({
    		autoDestroy: true,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, ImportDef),
            proxy: importsProxy,
            sorters: [{
                property: 'import',
                direction:'ASC'
            }]
        });
    	imports.load();
    	
    	if(this.value.length > 0) {
    		var valueParts = this.value.split(",");
    		for(var i=0; i < valueParts.length; i++) {
    			var nextPart = valueParts[i];
    			imports.add(new ImportDef({
                    'import': nextPart
                }));
    		}
    	}
    	
    	var itemDeleter = new Extensive.grid.ItemDeleter();
    	
    	var gridId = Ext.id();
    	var grid = new Ext.grid.EditorGridPanel({
            store: imports,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
            	id: 'import',
                header: 'Import',
                width: 360,
                dataIndex: 'import',
                editor: new Ext.form.TextField({ allowBlank: false })
            },itemDeleter]),
    		selModel: itemDeleter,
            autoHeight: true,
            tbar: [{
                text: 'Add Import',
                handler : function(){
                	imports.add(new ImportDef({
                        'import': ''
                    }));
                    grid.fireEvent('cellclick', grid, imports.getCount()-1, 1, null);
                }
            }],
            clicksToEdit: 1
        });
    	
    	var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: 'Editor for Imports', 
			height		: 300, 
			width		: 450, 
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
						dialog.hide()
				}.bind(this)
			}],
			items		:[grid],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					//this.focus.defer(10, this);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
                	var outValue = "";
                	grid.getView().refresh();
                	grid.stopEditing();
                	imports.data.each(function() {
                		if(this.data['import'].length > 0) {
                			outValue += this.data['import'] + ",";
                		}
                    });
                	if(outValue.length > 0) {
                		outValue = outValue.slice(0, -1)
                	}
					this.setValue(outValue);
					this.dataSource.getAt(this.row).set('value', outValue)
					this.dataSource.commitChanges()

					dialog.hide()
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide()
                }.bind(this)
            }]
		});		
				
		dialog.show();		
		grid.render();

		this.grid.stopEditing();
		grid.focus( false, 100 );
    	
    }
});

Ext.form.ComplexActionsField = Ext.extend(Ext.form.TriggerField,  {
	/**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function() {
    	if(this.disabled){
            return;
        }
    	
    	var ActionDef = Ext.data.Record.create([{
            name: 'action'
        }]);
    	
    	var actionsProxy = new Ext.data.MemoryProxy({
            root: []
        });
    	
    	var actions = new Ext.data.Store({
    		autoDestroy: true,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, ActionDef),
            proxy: actionsProxy,
            sorters: [{
                property: 'action',
                direction:'ASC'
            }]
        });
    	actions.load();
    	
    	if(this.value.length > 0) {
    		var valueParts = this.value.split("|");
    		for(var i=0; i < valueParts.length; i++) {
    			var nextPart = valueParts[i];
    			actions.add(new ActionDef({
                    action: nextPart
                }));
    		}
    	}
    	
    	var itemDeleter = new Extensive.grid.ItemDeleter();
    	
    	var gridId = Ext.id();
    	var grid = new Ext.grid.EditorGridPanel({
            store: actions,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
            	id: 'action',
                header: 'Action',
                width: 360,
                dataIndex: 'action',
                editor: new Ext.form.TextField({ allowBlank: false })
            },itemDeleter]),
    		selModel: itemDeleter,
            autoHeight: true,
            tbar: [{
                text: 'Add Action',
                handler : function(){
                	actions.add(new ActionDef({
                        action: ''
                    }));
                    grid.fireEvent('cellclick', grid, actions.getCount()-1, 1, null);
                }
            }],
            clicksToEdit: 1
        });
    	
    	var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: 'Editor for Actions', 
			height		: 300, 
			width		: 450, 
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
						dialog.hide()
				}.bind(this)
			}],
			items		:[grid],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					//this.focus.defer(10, this);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
                	var outValue = "";
                	grid.getView().refresh();
                	grid.stopEditing();
                	actions.data.each(function() {
                		if(this.data['action'].length > 0) {
                			outValue += this.data['action'] + "|";
                		}
                    });
                	if(outValue.length > 0) {
                		outValue = outValue.slice(0, -1)
                	}
					this.setValue(outValue);
					this.dataSource.getAt(this.row).set('value', outValue)
					this.dataSource.commitChanges()

					dialog.hide()
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide()
                }.bind(this)
            }]
		});		
				
		dialog.show();		
		grid.render();

		this.grid.stopEditing();
		grid.focus( false, 100 );
    	
    }
});

Ext.form.ComplexDataAssignmenField = Ext.extend(Ext.form.TriggerField,  {
    /**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function(){
		
        if(this.disabled){
            return undefined;
        }
        
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processVars = jsonPath(processJSON.evalJSON(), "*..['vardefs']");
        var varData = new Array();
        var varDataTitle = new Array();
        
        var dataTypeMap = new Hash();
        
        varDataTitle.push("");
        varDataTitle.push("** Variable Definitions **");
        varData.push(varDataTitle);
        if(processVars) {
        	processVars.forEach(function(item){
            	if(item.length > 0) {
	        		var valueParts = item.split(",");
	        		for(var i=0; i < valueParts.length; i++) {
	        			var innerVal = new Array();
	        			var nextPart = valueParts[i];
	        			if(nextPart.indexOf(":") > 0) {
	        				var innerParts = nextPart.split(":");
	        				innerVal.push(innerParts[0]);
	        				innerVal.push(innerParts[0]);
                                                dataTypeMap[innerParts[0]] = innerParts[1];
	        			} else {
	        				innerVal.push(nextPart);
	        				innerVal.push(nextPart);
                                                dataTypeMap[nextPart] = "java.lang.String";
	        			}
	        			varData.push(innerVal);
	        		}
        	    }
        	});
        }

        var dataInputsTitle = new Array();
        dataInputsTitle.push("");
        dataInputsTitle.push("** Data Inputs **");
        varData.push(dataInputsTitle);
        Ext.each(this.dataSource.data.items, function(item){
        	if(item.data.gridProperties.propId == "oryx-datainputset") {
        		var valueParts = item.data['value'].split(",");
        		for(var di=0; di < valueParts.length; di++) {
        			var nextPart = valueParts[di];
                                var innerVal = new Array();
                                if(nextPart.indexOf(":") > 0) {
                                        var innerParts = nextPart.split(":");
                                        innerVal.push(innerParts[0]);
                                        innerVal.push(innerParts[0]);
                                        dataTypeMap[innerParts[0]] = innerParts[1];
                                } else {
                                        innerVal.push(nextPart);
                                        innerVal.push(nextPart);
                                        dataTypeMap[nextPart] = "java.lang.String";
                                }
    				varData.push(innerVal);
        		}
        	} 
        });
        
        var dataOutputsTitle = new Array();
        dataOutputsTitle.push("");
        dataOutputsTitle.push("** Data Outputs **");
        varData.push(dataOutputsTitle);
        Ext.each(this.dataSource.data.items, function(item){
        	if(item.data.gridProperties.propId == "oryx-dataoutputset") {
        		var valueParts = item.data['value'].split(",");
        		for(var dou=0; dou < valueParts.length; dou++) {
        			var nextPart = valueParts[dou];
                                var innerVal = new Array();
                                if(nextPart.indexOf(":") > 0) {
                                        var innerParts = nextPart.split(":");
                                        innerVal.push(innerParts[0]);
                                        innerVal.push(innerParts[0]);
                                        dataTypeMap[innerParts[0]] = innerParts[1];
                                } else {
                                        innerVal.push(nextPart);
                                        innerVal.push(nextPart);
                                        dataTypeMap[nextPart] = "java.lang.String";
                                }
    				varData.push(innerVal);
        		}
        	} 
        });
        
    	var DataAssignment = Ext.data.Record.create([{
            name: 'from'
        }, {
            name: 'type'
        }, {
        	name: 'to'
        }, {
        	name: 'tostr'
        }, {
                name: 'dataType'
        }
        ]);
    	
    	var dataassignmentProxy = new Ext.data.MemoryProxy({
            root: []
        });
    	
    	var dataassignments = new Ext.data.Store({
    		autoDestroy: true,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, DataAssignment),
            proxy: dataassignmentProxy,
            sorters: [{
                property: 'from',
                direction:'ASC'
            }, {
            	property: 'to',
            	direction: 'ASC'
            }, {
            	property: 'tostr',
            	direction: 'ASC'
            }
            ]
        });
    	dataassignments.load();
    	
    	if(this.value.length > 0) {
    		var valueParts = this.value.split(",");
    		for(var i=0; i < valueParts.length; i++) {
    			var nextPart = valueParts[i];
    			if(nextPart.indexOf("=") > 0) {
                            var innerParts = nextPart.split("=");
                            var dataType = dataTypeMap[innerParts[0]];
                            if (!dataType){
                                dataType = "java.lang.String";
                            }
            				var escapedp = innerParts[1].replace(/\#\#/g , ",");
                            dataassignments.add(new DataAssignment({
                                from: innerParts[0],
                                type: "is equal to",
                                to: "",
                                tostr: escapedp,
                                dataType: dataType
                            }));
    			} else if(nextPart.indexOf("->") > 0) {
                            var innerParts = nextPart.split("->");
                            var dataType = dataTypeMap[innerParts[0]];
                            if (!dataType){
                                dataType = "java.lang.String";
                            }
                            dataassignments.add(new DataAssignment({
                                from: innerParts[0],
                                type: "is mapped to",
                                to: innerParts[1],
                                tostr: "",
                                dataType: dataType
                            }));
    			} else {
    				// default to equality
    				var dataType = dataTypeMap[nextPart];
                    if (!dataType){
                        dataType = "java.lang.String";
                    }
                    dataassignments.add(new DataAssignment({
                        from: nextPart,
                        type: "is equal to",
                        to: "",
                        tostr: "",
                        dataType: dataType
                    }));
    			}
    		}
    	}
        
        //keep sync between from and dataType
        dataassignments.on('update', function(store, record, operation){
            if (operation == "edit"){
                var newType = dataTypeMap[record.get("from")];
                if (!newType){
                    newType = "java.lang.String";
                }
                record.set("dataType", newType);
            }
        });
    	
    	var itemDeleter = new Extensive.grid.ItemDeleter();
    	var gridId = Ext.id();
    	var grid = new Ext.grid.EditorGridPanel({
            store: dataassignments,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
                    id: 'valueType',
                    header: 'Data Type',
	            width: 180,
                    dataIndex: 'dataType',
                    hidden: 'true'
                },{
            	id: 'from',
	            header: 'From Object',
	            width: 180,
	            dataIndex: 'from',
	            editor: new Ext.form.ComboBox({
	            	id: 'fromCombo',
	            	valueField:'name',
	            	displayField:'value',
	            	typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        selectOnFocus:true,
                        store: new Ext.data.SimpleStore({
                            fields: [
                                        'name',
                                        'value'
                                    ],
                            data: varData
                        })
	            })
            }, {
            	id: 'type',
                header: 'Assignment Type',
                width: 100,
                dataIndex: 'type',
                editor: new Ext.form.ComboBox({
                	id: 'typeCombo',
                	valueField:'name',
                	displayField:'value',
                	typeAhead: true,
					mode: 'local',
					triggerAction: 'all',
					selectOnFocus:true,
					store: new Ext.data.SimpleStore({
				        fields: [
				                  'name',
				                  'value'
				                ],
				        data: [
	                	        ['is mapped to','is mapped to'],
	                	        ['is equal to','is equal to']
	                	       ]
				    })
                })
            }, {
            	id: 'to',
                header: 'To Object',
                width: 180,
                dataIndex: 'to',
                editor: new Ext.form.ComboBox({
                	id: 'toCombo',
                	valueField:'name',
                	displayField:'value',
                	typeAhead: true,
					mode: 'local',
					triggerAction: 'all',
					selectOnFocus:true,
					store: new Ext.data.SimpleStore({
				        fields: [
				                  'name',
				                  'value'
				                ],
				        data: varData
				    })
                })
            }, {
            	id: 'tostr',
                header: 'To Value',
                width: 180,
                dataIndex: 'tostr',
                editor: new Ext.form.TextField({ allowBlank: true }),
                renderer: Ext.util.Format.htmlEncode
    		}, itemDeleter]),
    		selModel: itemDeleter,
            autoHeight: true,
            tbar: [{
                text: 'Add Assignment',
                handler : function(){
                	dataassignments.add(new DataAssignment({
                        from: '',
                        type: '',
                        to: '',
                        tostr: ''
                    }));
                    grid.fireEvent('cellclick', grid, dataassignments.getCount()-1, 1, null);
                }
            }],
            clicksToEdit: 1
        });
    	
		var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: 'Editor for Data Assignments', 
			height		: 350, 
			width		: 730, 
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
						dialog.hide()
				}.bind(this)
			}],
			items		:[grid],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
                	var outValue = "";
                	grid.getView().refresh();
                	grid.stopEditing();
                	dataassignments.data.each(function() {
                		if(this.data['from'].length > 0 && this.data["type"].length > 0) {
                			if(this.data["type"] == "is mapped to") {
                				outValue += this.data['from'] + "->" + this.data['to'] + ",";
                			} else if(this.data["type"] == "is equal to") {
                				var escapedc = this.data['tostr'].replace(/,/g , "##");
                				outValue += this.data['from'] + "=" + escapedc + ",";
                			}
                		}
                    });
                	if(outValue.length > 0) {
                		outValue = outValue.slice(0, -1);
                	}
					this.setValue(outValue);
					this.dataSource.getAt(this.row).set('value', outValue);
					this.dataSource.commitChanges();
					dialog.hide();
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide();
                }.bind(this)
            }]
		});		
				
		dialog.show();		
		grid.render();

		this.grid.stopEditing();
		grid.focus( false, 100 );
	
                return grid;
	}
});


Ext.form.NameTypeEditor = Ext.extend(Ext.form.TriggerField,  {

    windowTitle : "",
    addButtonLabel : "",
    /**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function(){
		
        if(this.disabled){
            return;
        }
        
    	var VarDef = Ext.data.Record.create([{
            name: 'name'
        }, {
            name: 'stype'
        }, {
            name: 'ctype'
        }]);
    	
    	var vardefsProxy = new Ext.data.MemoryProxy({
            root: []
        });
    	
    	var vardefs = new Ext.data.Store({
    		autoDestroy: true,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, VarDef),
            proxy: vardefsProxy,
            sorters: [{
                property: 'name',
                direction:'ASC'
            }]
        });
    	vardefs.load();
    	
    	if(this.value.length > 0) {
    		var valueParts = this.value.split(",");
    		for(var i=0; i < valueParts.length; i++) {
    			var nextPart = valueParts[i];
    			if(nextPart.indexOf(":") > 0) {
    				var innerParts = nextPart.split(":");
    				if(innerParts[1] == "String" || innerParts[1] == "Integer" || innerParts[1] == "Boolean" || innerParts[1] == "Float") {
    					vardefs.add(new VarDef({
                            name: innerParts[0],
                            stype: innerParts[1],
                            ctype: ''
                        }));
    				} else {
    					if(innerParts[1] != "Object") {
    						vardefs.add(new VarDef({
                                name: innerParts[0],
                                stype: 'Object',
                                ctype: innerParts[1]
                            }));
    					} else {
    						vardefs.add(new VarDef({
                                name: innerParts[0],
                                stype: innerParts[1],
                                ctype: ''
                            }));
    					}
    				}
    			} else {
    				vardefs.add(new VarDef({
                        name: nextPart,
                        stype: '',
                        ctype: ''
                    }));
    			}
    		}

    	}
    	
    	var itemDeleter = new Extensive.grid.ItemDeleter();
    	
    	var typeData = new Array();
    	var stringType = new Array();
    	stringType.push("String");
    	stringType.push("String");
    	typeData.push(stringType);
    	var integerType = new Array();
    	integerType.push("Integer");
    	integerType.push("Integer");
    	typeData.push(integerType);
    	var booleanType = new Array();
    	booleanType.push("Boolean");
    	booleanType.push("Boolean");
    	typeData.push(booleanType);
    	var floatType = new Array();
    	floatType.push("Float");
    	floatType.push("Float");
    	typeData.push(floatType);
    	var objectType = new Array();
    	objectType.push("Object");
    	objectType.push("Object");
    	typeData.push(objectType);
    	
    	var gridId = Ext.id();
    	var grid = new Ext.grid.EditorGridPanel({
            store: vardefs,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
            	id: 'name',
                header: 'Name',
                width: 100,
                dataIndex: 'name',
                editor: new Ext.form.TextField({ allowBlank: false })
            }, {
            	id: 'stype',
                header: 'Standard Type',
                width: 100,
                dataIndex: 'stype',
                editor: new Ext.form.ComboBox({
                	id: 'typeCombo',
                	valueField:'name',
                	displayField:'value',
                	labelStyle:'display:none',
                	submitValue : true,
                	typeAhead: false,
                	queryMode: 'local',
                	mode: 'local',
					triggerAction: 'all',
					selectOnFocus:true,
					hideTrigger: false,
					forceSelection: false,
					selectOnFocus:true,
					autoSelect:false,
					store: new Ext.data.SimpleStore({
				        fields: [
				                  'name',
				                  'value'
				                ],
				        data: typeData
				    })
                })
            },{
            	id: 'ctype',
                header: 'Custom Type',
                width: 200,
                dataIndex: 'ctype',
                editor: new Ext.form.TextField({ allowBlank: false })
            }, itemDeleter]),
    		selModel: itemDeleter,
            autoHeight: true,
            tbar: [{
                text: this.addButtonLabel,
                handler : function(){
                	vardefs.add(new VarDef({
                        name: '',
                        stype: '',
                        ctype: ''
                    }));
                    grid.fireEvent('cellclick', grid, vardefs.getCount()-1, 1, null);
                }
            }],
            clicksToEdit: 1
        });
    	
		var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: this.windowTitle, 
			height		: 300, 
			width		: 500, 
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
						dialog.hide()
				}.bind(this)
			}],
			items		:[grid],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					//this.focus.defer(10, this);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
                	var outValue = "";
                	grid.stopEditing();
                	grid.getView().refresh();
                	vardefs.data.each(function() {
                		if(this.data['name'].length > 0) {
                			if(this.data['stype'].length > 0) {
                				if(this.data['stype'] == "Object" && this.data['ctype'].length > 0) {
                					outValue += this.data['name'] + ":" + this.data['ctype'] + ",";
                				} else {
                					outValue += this.data['name'] + ":" + this.data['stype'] + ",";
                				}
                			} else if(this.data['ctype'].length > 0) { 
                				outValue += this.data['name'] + ":" + this.data['ctype'] + ",";
                			} else {
                				outValue += this.data['name'] + ",";
                			}
                		}
                    });
                	if(outValue.length > 0) {
                		outValue = outValue.slice(0, -1)
                	}
					this.setValue(outValue);
					this.dataSource.getAt(this.row).set('value', outValue)
					this.dataSource.commitChanges()

					dialog.hide()
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide()
                }.bind(this)
            }]
		});		
				
		dialog.show();		
		grid.render();

		this.grid.stopEditing();
		grid.focus( false, 100 );
		
	}
});

Ext.form.ComplexVardefField = Ext.extend(Ext.form.NameTypeEditor,  {
     windowTitle : 'Editor for Variable Definitions',
     addButtonLabel : 'Add Variable'
});

Ext.form.ComplexDataInputField = Ext.extend(Ext.form.NameTypeEditor,  {
     windowTitle : 'Editor for Data Input',
     addButtonLabel : 'Add Data Input'
});

Ext.form.ComplexDataOutputField = Ext.extend(Ext.form.NameTypeEditor,  {
     windowTitle : 'Editor for Data Output',
     addButtonLabel : 'Add Data Output'
});


Ext.form.ComplexExpressionField = Ext.extend(Ext.form.TriggerField,  {
	onTriggerClick : function(){
		if(this.disabled){
            return;
        }
		var ceta = new Ext.form.TextArea({
            id: Ext.id(),
            fieldLabel: "Expression Editor",
            value: this.value,
            autoScroll: true
            });
		
		var sourceEditor;
		var hlLine;
		
		var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: 'Expression Editor - Press [Ctrl-Z] to activate auto-completion', 
			height		: 430, 
			width		: 550, 
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
						dialog.hide()
				}.bind(this)
			}],
			items		:[ceta],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
					this.setValue(sourceEditor.getValue());
					this.dataSource.getAt(this.row).set('value', sourceEditor.getValue());
					this.dataSource.commitChanges();
					dialog.hide()
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide()
                }.bind(this)
            }]
		});	
		dialog.show();		
		this.foldFunc = CodeMirror.newFoldFunction(CodeMirror.braceRangeFinder);
		sourceEditor = CodeMirror.fromTextArea(document.getElementById(ceta.getId()), {
			  mode: "text/x-java",
			  lineNumbers: true,
			  lineWrapping: true,
			  matchBrackets: true,
			  onGutterClick: this.foldFunc,
			  extraKeys: {"Ctrl-Z": function(cm) {CodeMirror.hint(cm, CodeMirror.jbpmHint, dialog);}},
			  onCursorActivity: function() {
				  sourceEditor.setLineClass(hlLine, null, null);
	 			     hlLine = sourceEditor.setLineClass(sourceEditor.getCursor().line, null, "activeline");
	 		  }.bind(this)
			});
		hlLine = sourceEditor.setLineClass(0, "activeline");
		this.grid.stopEditing();
	}
});
Ext.form.ComplexCalledElementField = Ext.extend(Ext.form.TriggerField,  {
	onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        
        var CallElementDef = Ext.data.Record.create([{
            name: 'name'
        }, {
        	name: 'pkgname'
        }, {
            name: 'imgsrc'
        }]);
    	
    	var calldefsProxy = new Ext.data.MemoryProxy({
            root: []
        });
    	
    	var calldefs = new Ext.data.Store({
    		autoDestroy: true,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, CallElementDef),
            proxy: calldefsProxy,
            sorters: [{
                property: 'name',
                direction:'ASC'
            }]
        });
    	calldefs.load();
        
        var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var loadProcessesMask = new Ext.LoadMask(Ext.getBody(), {msg:'Loading Process Information'});
        loadProcessesMask.show();
        Ext.Ajax.request({
            url: ORYX.PATH + 'calledelement',
            method: 'POST',
            success: function(response) {
    	   		try {
    	   			loadProcessesMask.hide();
    	   			if(response.responseText.length > 0 && response.responseText != "false") {
    	   				var responseJson = Ext.decode(response.responseText);
    		            for(var key in responseJson){
    		            	var keyParts = key.split("|");
    		            	calldefs.add(new CallElementDef({
                                name: keyParts[0],
                                pkgname: keyParts[1],
                                imgsrc: responseJson[key]
                            }));
    		            }
    		            calldefs.commitChanges();
    		            
    		            var gridId = Ext.id();
    		        	var grid = new Ext.grid.EditorGridPanel({
    		                store: calldefs,
    		                id: gridId,
    		                stripeRows: true,
    		                cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
    		                	id: 'pid',
    		                    header: 'Process Id',
    		                    width: 200,
    		                    dataIndex: 'name',
    		                    editor: new Ext.form.TextField({ allowBlank: false, disabled: true })
    		                }, {
    		                	id: 'pkgn',
    		                    header: 'Package Name',
    		                    width: 200,
    		                    dataIndex: 'pkgname',
    		                    editor: new Ext.form.TextField({ allowBlank: false, disabled: true })
    		                },{
    		                	id: 'pim',
    		                    header: 'Process Image',
    		                    width: 250,
    		                    dataIndex: 'imgsrc',
    		                    renderer: function(val) {
    		                    	if(val && val.length > 0) { 
    		                    		return '<center><img src="'+ORYX.PATH+'images/page_white_picture.png" onclick="new ImageViewer({title: \'Process Image\', width: \'650\', height: \'450\', autoScroll: true, fixedcenter: true, src: \''+val+'\',hideAction: \'close\'}).show();" alt="Click to view Process Image"/></center>';
    		                    	} else {
    		                    		return "<center>Process image not available.</center>";
    		                    	}
    		                    }
    		                }]),
    		                autoHeight: true
    		            });
    		        	
    		        	grid.on('afterrender', function(e) {
    		        		if(this.value.length > 0) {
	    		        		var index = 0;
	    		        		var val = this.value;
	    		        		var mygrid = grid;
	    		        		calldefs.data.each(function() {
	    	                		if(this.data['name'] == val) {
	    	                			mygrid.getSelectionModel().select(index, 1);
	    	                		}
	    	                		index++;
	    	                    });
	    		        	}
    		        		}.bind(this));
    		        	
    		        	var calledElementsPanel = new Ext.Panel({
    		        		id: 'calledElementsPanel',
    		        		title: '<center>Select Process Id and click "Save" to select.</center>',
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
    		    			title		: 'Editor for Called Elements', 
    		    			height		: 350, 
    		    			width		: 680, 
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
    		    						dialog.hide()
    		    				}.bind(this)
    		    			}],
    		    			items		:[calledElementsPanel],
    		    			listeners	:{
    		    				hide: function(){
    		    					this.fireEvent('dialogClosed', this.value);
    		    					dialog.destroy();
    		    				}.bind(this)				
    		    			},
    		    			buttons		: [{
    		                    text: 'Save',
    		                    handler: function(){
    		                    	if(grid.getSelectionModel().getSelectedCell() != null) {
    		                    		var selectedIndex = grid.getSelectionModel().getSelectedCell()[0];
    		                    		var outValue = calldefs.getAt(selectedIndex).data['name'];
    		                    		grid.stopEditing();
    		                        	grid.getView().refresh();
    		        					this.setValue(outValue);
    		        					this.dataSource.getAt(this.row).set('value', outValue)
    		        					this.dataSource.commitChanges()
    		        					dialog.hide()
    		                    	} else {
    		                    		Ext.Msg.alert('Plese select a process id.');
    		                    	}
    		                    }.bind(this)
    		                }, {
    		                    text: ORYX.I18N.PropertyWindow.cancel,
    		                    handler: function(){
    		    					this.setValue(this.value);
    		                    	dialog.hide()
    		                    }.bind(this)
    		                }]
    		    		});		
    		    				
    		    		dialog.show();		
    		    		grid.render();
    		    		grid.fireEvent('afterrender');
    		    		this.grid.stopEditing();
    		    		grid.focus( false, 100 );
    		        } else {
    		        	Ext.Msg.alert('Unable to find other processes in pacakge.');
    		        }
    	   		} catch(e) {
    	   			Ext.Msg.alert('Error resolving other process info :\n' + e);
    	   		}
            }.bind(this),
            failure: function(){
            	loadProcessesMask.hide();
            	Ext.Msg.alert('Error resolving other process info.');
            },
            params: {
            	profile: ORYX.PROFILE,
            	uuid : ORYX.UUID,
            	ppackage: processPackage,
            	pid: processId
            }
        });
	}
});


Ext.form.ComplexGlobalsField = Ext.extend(Ext.form.TriggerField,  {

    /**
     * If the trigger was clicked a dialog has to be opened
     * to enter the values for the complex property.
     */
    onTriggerClick : function(){
		
        if(this.disabled){
            return;
        }
        
    	var GlobalDef = Ext.data.Record.create([{
            name: 'name'
        }, {
            name: 'type'
        }]);
    	
    	var globalsProxy = new Ext.data.MemoryProxy({
            root: []
        });
    	
    	var globals = new Ext.data.Store({
    		autoDestroy: true,
            reader: new Ext.data.JsonReader({
                root: "root"
            }, GlobalDef),
            proxy: globalsProxy, 
            sorters: [{
                property: 'name',
                direction:'ASC'
            }]
        });
    	globals.load();
    	
    	if(this.value.length > 0) {
    		var valueParts = this.value.split(",");
    		for(var i=0; i < valueParts.length; i++) {
    			var nextPart = valueParts[i];
    			if(nextPart.indexOf(":") > 0) {
    				var innerParts = nextPart.split(":");
    				globals.add(new GlobalDef({
                        name: innerParts[0],
                        type: innerParts[1]
                    }));
    			} else {
    				globals.add(new GlobalDef({
                        name: nextPart,
                        type: ''
                    }));
    			}
    		}

    	}
    	
    	var itemDeleter = new Extensive.grid.ItemDeleter();
    	
    	var gridId = Ext.id();
    	var grid = new Ext.grid.EditorGridPanel({
            store: globals,
            id: gridId,
            stripeRows: true,
            cm: new Ext.grid.ColumnModel([new Ext.grid.RowNumberer(), {
            	id: 'name',
                header: 'Name',
                width: 100,
                dataIndex: 'name',
                editor: new Ext.form.TextField({ allowBlank: false })
            }, {
            	id: 'type',
                header: 'Type',
                width: 200,
                dataIndex: 'type',
                editor: new Ext.form.TextField({ allowBlank: true })
            }, itemDeleter]),
    		selModel: itemDeleter,
            autoHeight: true,
            tbar: [{
                text: 'Add Global',
                handler : function(){
                	globals.add(new GlobalDef({
                        name: '',
                        type: ''
                    }));
                    grid.fireEvent('cellclick', grid, globals.getCount()-1, 1, null);
                }
            }],
            clicksToEdit: 1
        });
    	
		var dialog = new Ext.Window({ 
			layout		: 'anchor',
			autoCreate	: true, 
			title		: 'Editor for Globals', 
			height		: 300, 
			width		: 400, 
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
						dialog.hide()
				}.bind(this)
			}],
			items		:[grid],
			listeners	:{
				hide: function(){
					this.fireEvent('dialogClosed', this.value);
					//this.focus.defer(10, this);
					dialog.destroy();
				}.bind(this)				
			},
			buttons		: [{
                text: ORYX.I18N.PropertyWindow.ok,
                handler: function(){	 
                	grid.stopEditing();
                	grid.getView().refresh();
                	var outValue = "";
                	globals.data.each(function() {
                		if(this.data['name'].length > 0) {
                			if(this.data['type'].length > 0) {
                				outValue += this.data['name'] + ":" + this.data['type'] + ",";
                			} else {
                				outValue += this.data['name'] + ",";
                			}
                		}
                    });
                	if(outValue.length > 0) {
                		outValue = outValue.slice(0, -1)
                	}
					this.setValue(outValue);
					this.dataSource.getAt(this.row).set('value', outValue)
					this.dataSource.commitChanges()

					dialog.hide()
                }.bind(this)
            }, {
                text: ORYX.I18N.PropertyWindow.cancel,
                handler: function(){
					this.setValue(this.value);
                	dialog.hide()
                }.bind(this)
            }]
		});		
				
		dialog.show();		
		grid.render();

		this.grid.stopEditing();
		grid.focus( false, 100 );
	}
});
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

ORYX.Plugins.Toolbar = Clazz.extend({

	facade: undefined,
	plugs:	[],

	construct: function(facade, ownPluginData) {
		this.facade = facade;
		
		this.groupIndex = new Hash();
		
		
		if (ORYX.CONFIG.MENU_INDEX) {
		  this.groupIndex = ORYX.CONFIG.MENU_INDEX;
		} else {
		  ownPluginData.properties.each((function(value){
		    if(value.group && value.index != undefined) {
			  this.groupIndex[value.group] = value.index;
			}
		  }).bind(this));
		}
		
		Ext.QuickTips.init();

		this.buttons = [];
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_BUTTON_UPDATE, this.onButtonUpdate.bind(this));
        this.facade.registerOnEvent(ORYX.CONFIG.EVENT_STENCIL_SET_LOADED, this.onSelectionChanged.bind(this));
	},
    
    /**
     * Can be used to manipulate the state of a button.
     * @example
     * this.facade.raiseEvent({
     *   type: ORYX.CONFIG.EVENT_BUTTON_UPDATE,
     *   id: this.buttonId, // have to be generated before and set in the offer method
     *   pressed: true
     * });
     * @param {Object} event
     */
    onButtonUpdate: function(event){
        var button = this.buttons.find(function(button){
            return button.id === event.id;
        });
        
        if(event.pressed !== undefined){
            button.buttonInstance.toggle(event.pressed);
        }
    },

	registryChanged: function(pluginsData) {
        // Sort plugins by group and index
		var newPlugs =  pluginsData.sortBy((function(value) {
			return ((this.groupIndex[value.group] != undefined ? this.groupIndex[value.group] : "" ) + value.group + "" + value.index).toLowerCase();
		}).bind(this));
		
		var plugs = $A(newPlugs).findAll(function(value){
										if(value.group && value.group.indexOf("footer") === 0) {
											return false;
										}
										return !this.plugs.include( value )
									}.bind(this));
		if(plugs.length<1)
			return;

		this.buttons = [];

		ORYX.Log.trace("Creating a toolbar.")
		if(!this.toolbar){
			this.toolbar = new Ext.ux.SlicedToolbar({
			height: 24
		});
				var region = this.facade.addToRegion("north", this.toolbar, "Toolbar");
		}
		
		
		var currentGroupsName = this.plugs.last()?this.plugs.last().group:plugs[0].group;
        
        // Map used to store all drop down buttons of current group
        var currentGroupsDropDownButton = {};

        if(!(ORYX.READONLY == true || ORYX.VIEWLOCKED == true)) {
            if(('webkitSpeech' in document.createElement('input'))) {
                var micfield = new Ext.form.TextField({
                    id: 'micinput'
                });
                this.toolbar.add(micfield);
                this.toolbar.add('-');

                var attrib = {'x-webkit-speech':'true'};
                Ext.get('micinput').set(attrib);

                var mic = document.getElementById('micinput');
                mic.onfocus = mic.blur;
                mic.onwebkitspeechchange = function(e) {
                    var val = mic.value;
                    ORYX.EDITOR._pluginFacade.raiseEvent({
                        type: ORYX.CONFIG.EVENT_VOICE_COMMAND,
                        entry: val
                    });
                    mic.blur;
                    mic.value = "";
                };
            }
        }
		
		plugs.each((function(value) {
			if(!value.name) {return}
			this.plugs.push(value);
            // Add seperator if new group begins
			if(currentGroupsName != value.group) {
			    this.toolbar.add('-');
				currentGroupsName = value.group;
                currentGroupsDropDownButton = {};
			}
			//add eventtracking
			var tmp = value.functionality;
			value.functionality = function(){
				 if ("undefined" != typeof(pageTracker) && "function" == typeof(pageTracker._trackEvent) )
				 {
					pageTracker._trackEvent("ToolbarButton",value.name)
				}
				return tmp.apply(this, arguments);

			}
			
            // If an drop down group text is provided, a split button should be used
            if(value.dropdownGroupText){
                var splitButton = currentGroupsDropDownButton[value.dropdownGroupText];
                
                // Create a new split button if this is the first plugin using it - drop-down toolbar button
                if(splitButton === undefined){
                    if(value.dropdownGroupText) {
                        splitButton = currentGroupsDropDownButton[value.dropdownGroupText] = new Ext.Toolbar.SplitButton({
                            //cls: "x-btn-icon", //show icon only
                            //icon: value.dropDownGroupIcon,
                            text: value.dropdownGroupText,
                            menu: new Ext.menu.Menu({
                                items: [] // items are added later on
                            }),
                            listeners: {
                                click: function(button, event){
                                    // The "normal" button should behave like the arrow button
                                    if(!button.menu.isVisible() && !button.ignoreNextClick){
                                        button.showMenu();
                                    } else {
                                        button.hideMenu();
                                    }
                                }
                            }
                        });
                    } else {
                        splitButton = currentGroupsDropDownButton[value.dropdownGroupText] = new Ext.Toolbar.SplitButton({
                            //cls: "x-btn-icon", //show icon only
                            //icon: value.dropDownGroupIcon,
                            iconCls: window.SpriteUtils.toUniqueId(value.dropDownGroupIcon), // set iconCls to sprite css class
                            menu: new Ext.menu.Menu({
                                items: [] // items are added later on
                            }),
                            listeners: {
                                click: function(button, event){
                                    // The "normal" button should behave like the arrow button
                                    if(!button.menu.isVisible() && !button.ignoreNextClick){
                                        button.showMenu();
                                    } else {
                                        button.hideMenu();
                                    }
                                }
                            }
                        });
                    }
                    
                    this.toolbar.add(splitButton);
                }
                
                // General config button which will be used either to create a normal button
                // or a check button (if toggling is enabled) - used for menu items in drop-down list
                var buttonCfg = {
                    text: value.name,
                    itemId: value.id,
                    handler: value.toggle ? undefined : value.functionality,
                    checkHandler: value.toggle ? value.functionality : undefined,
                    listeners: {
                        render: function(item){
                            // After rendering, a tool tip should be added to component
                            if (value.description) {
                                new Ext.ToolTip({
                                    target: item.getEl(),
                                    title: value.description
                                })
                            }
                        }
                    }
                }
                
                // Create buttons depending on toggle
                if(value.toggle) {
                    var button = new Ext.menu.CheckItem(buttonCfg);
                } else {
                    var button = new Ext.menu.Item(buttonCfg);
                }
                
                splitButton.menu.add(button);
                
            } else { // create normal, simple button - used by top level toolbar buttons
                var button = new Ext.Toolbar.Button({
                    itemId:         value.id,
					tooltip:        value.description,  // Set the tooltip
                    tooltipType:    'title',            // Tooltip will be shown as in the html-title attribute
                    handler:        value.toggle ? null : value.functionality,  // Handler for mouse click
                    enableToggle:   value.toggle, // Option for enabling toggling
                    toggleHandler:  value.toggle ? value.functionality : null // Handler for toggle (Parameters: button, active)
                }); 
                
                this.toolbar.add(button);

                button.getEl().onclick = function() {this.blur()}
            }
			     
			value['buttonInstance'] = button;
			this.buttons.push(value);
			
		}).bind(this));

		this.enableButtons([]);
        this.toolbar.calcSlices();
		window.addEventListener("resize", function(event){this.toolbar.calcSlices()}.bind(this), false);
		window.addEventListener("onresize", function(event){this.toolbar.calcSlices()}.bind(this), false);

	},
	
	onSelectionChanged: function(event) {
		if(!event.elements){
			this.enableButtons([]);
		}else{
			this.enableButtons(event.elements);
		}
	},

	enableButtons: function(elements) {
		// Show the Buttons
		this.buttons.each((function(value){
			value.buttonInstance.enable();
						
			// If there is less elements than minShapes
			if(value.minShape && value.minShape > elements.length)
				value.buttonInstance.disable();
			// If there is more elements than minShapes
			if(value.maxShape && value.maxShape < elements.length)
				value.buttonInstance.disable();	
			// If the plugin is not enabled	
			if(value.isEnabled && !value.isEnabled())
				value.buttonInstance.disable();
			
		}).bind(this));		
	}
});

Ext.ns("Ext.ux");
Ext.ux.SlicedToolbar = Ext.extend(Ext.Toolbar, {
    currentSlice: 0,
    iconStandardWidth: 22, //22 px 
    seperatorStandardWidth: 2, //2px, minwidth for Ext.Toolbar.Fill
    toolbarStandardPadding: 2,
    
    initComponent: function(){
        Ext.apply(this, {
        });
        Ext.ux.SlicedToolbar.superclass.initComponent.apply(this, arguments);
    },
    
    onRender: function(){
        Ext.ux.SlicedToolbar.superclass.onRender.apply(this, arguments);
    },
    
    onResize: function(){
        Ext.ux.SlicedToolbar.superclass.onResize.apply(this, arguments);
    },
    
    calcSlices: function(){
        var slice = 0;
        this.sliceMap = {};
        var sliceWidth = 0;
        var toolbarWidth = this.getEl().getWidth();
        var addedPrev = false;
        var addedNext = false;
        var itemIndex = -1;

        this.items.getRange().each(function(item, index) {
            //Remove all next and prev buttons
            if (item.helperItem) {
                item.destroy();
                return;
            }
            
            var itemWidth = item.getEl().getWidth();
            
            if(sliceWidth + itemWidth + 5 * this.iconStandardWidth > toolbarWidth){
                itemIndex = this.items.indexOf(item);
                this.insertSlicingButton("next", slice, itemIndex);
                addedNext = true;
                
                if (slice !== 0) {
                    this.insertSlicingButton("prev", slice, itemIndex);
                    addedPrev = true;
                }
                
                this.insertSlicingSeperator(slice, itemIndex);

                slice += 1;
                sliceWidth = 0;
            }
            
            this.sliceMap[item.id] = slice;
            sliceWidth += itemWidth;
        }.bind(this));

        if(!addedPrev) {
            this.insertSlicingButton("prev", slice, itemIndex);
        }

        if(!addedNext) {
            if(addedPrev) {
                this.insertSlicingButton("next", slice, itemIndex+1);
            } else {
                this.insertSlicingButton("next", slice, itemIndex);
            }
        }

        
        // Add prev button at the end
        if(slice > 0){
            // BZ1192460 - IE10 designer hangs on load - set index to -1 for append
            this.insertSlicingSeperator(slice, -1);
            this.insertSlicingButton("prev", slice, -1);
            var spacer = new Ext.Toolbar.Spacer();
            this.insertSlicedHelperButton(spacer, slice, -1);
            Ext.get(spacer.id).setWidth(this.iconStandardWidth);
        }
        
        this.maxSlice = slice;
        
        // Update view
        this.setCurrentSlice(this.currentSlice);
    },
    
    insertSlicedButton: function(button, slice, index){
        if (index == -1) {
            this.addButton(button);
        }
        else {
            this.insertButton(index, button);
        }
        this.sliceMap[button.id] = slice;
    },
    
    insertSlicedHelperButton: function(button, slice, index){
        button.helperItem = true;
        this.insertSlicedButton(button, slice, index);
    },
    
    insertSlicingSeperator: function(slice, index){
        // Align right
        this.insertSlicedHelperButton(new Ext.Toolbar.Fill(), slice, index);
    },
    
    // type => next or prev
    insertSlicingButton: function(type, slice, index){
        // var nextHandler = function(){this.setCurrentSlice(this.currentSlice+1)}.bind(this);
        // var prevHandler = function(){this.setCurrentSlice(this.currentSlice-1)}.bind(this);
        // var button = new Ext.Toolbar.Button({
        //     cls: "x-btn-icon",
        //     icon: ORYX.BASE_FILE_PATH + "images/toolbar_"+type+".png",
        //     handler: (type === "next") ? nextHandler : prevHandler
        // });
        //
        // this.insertSlicedHelperButton(button, slice, index);
    },
    
    setCurrentSlice: function(slice){
        if(slice > this.maxSlice || slice < 0) return;
        
        this.currentSlice = slice;

        this.items.getRange().each(function(item){
            item.setVisible(slice === this.sliceMap[item.id]);
        }.bind(this));
    }
});
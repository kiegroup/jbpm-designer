/**
 * Copyright (c) 2009
 * Jan-Felix Schwarz
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


// Declare the MOVI.widget namespace
MOVI.namespace("widget");

(function() {
	
	var _TOOLBAR_CLASS_NAME 			= "movi-toolbar",
		_TOOLBAR_BUTTON_CLASS_NAME		= "movi-toolbar-button",
		_TOOLBAR_GROUP_CLASS_NAME		= "movi-toolbar-group";
		_TOOLBAR_GROUP_LABEL_CLASS_NAME	= "movi-toolbar-group-label"
	
	var _DEFAULT_GROUP_NAME 			= "movi-toolbar-default-group";
	
	/**
     * The Toolbar widget is a UI control that hosts buttons to trigger certain
	 * functionality. It allows generic registration of new toolbar buttons.
     * @namespace MOVI.widget
     * @class MOVI.widget.Toolbar
     * @extends YAHOO.util.Element
     * @constructor
     * @param {HTMLElement | String } el The id of the container DIV element that will 
	 * wrap the Toolbar, or a reference to a DIV element. The DIV element must
	 * exist in the document.
	 * @modelviewer {ModelViewer} modelviewer The ModelViewer that is controlled 
     */
    MOVI.widget.Toolbar = function(el, modelviewer) {
	
		this.modelviewer = modelviewer;
		
		this._groups = {};
		
		MOVI.widget.Toolbar.superclass.constructor.call(this, el); 
		
		this.addClass(_TOOLBAR_CLASS_NAME);
		
	};
	
	MOVI.extend(MOVI.widget.Toolbar, YAHOO.util.Element, {
		
		/**
		 * A key map containing all group objects with their name as keys
		 * @property groups
		 * @type {Object}
		 * @private
		 */
		_groups: null,
		
		/**
		 * The ModelViewer that is controlled
		 * @property modelviewer
		 * @type ModelViewer
		 */
		modelviewer: null,
		
		/**
		 * Update the toolbar DOM node 
		 * @method _update
		 * @private
		 */
		_update: function() {
			
			this.set("innerHTML", "");
			
			for(key in this._groups) {
				var group = this._groups[key];
				
				var groupEl = new YAHOO.util.Element(document.createElement("div"));
				groupEl.set("className", _TOOLBAR_GROUP_CLASS_NAME);
				this.appendChild(groupEl);
				
				if(this._showGroupCaptions) {
					var groupLabelEl = new YAHOO.util.Element(document.createElement("div"));
					groupLabelEl.set("className", _TOOLBAR_GROUP_LABEL_CLASS_NAME);
					groupEl.appendChild(groupLabelEl);
					if(key!=_DEFAULT_GROUP_NAME) groupLabelEl.set("innerHTML", key);
				}
				
				for(var i=0; i<group.length; i++) {
					if(group[i]) groupEl.appendChild(group[i]);
				}
			}
			
			
			var clearDiv = new YAHOO.util.Element(document.createElement("div"));
			clearDiv.setStyle("clear", "left");
			this.appendChild(clearDiv);
			
		},
		
		/**
		 * Add a new button to the toolbar
		 * @param {Object} config Button configuration: 
		 * <dl>
		 * <dt>{String} icon (optional)</dt>
		 * <dd>
		 * The URL of an icon image
		 * </dd>
		 * <dt>{String} caption (optional)</dt>
		 * <dd>
		 * The caption of the button
		 * </dd>
		 * <dt>{String} tooltip (optional)</dt>
		 * <dd>
		 * A tooltip message for the button
		 * </dd>
		 * <dt>{String} group (optional)</dt>
		 * <dd>
		 * The button group to append the new button to. If no such group exists
		 * it will be created. Groups are positioned in the order they were created.
		 * </dd>
		 * <dt>{Function} callback</dt>
		 * <dd>The callback method when the button is triggered.</dd>
		 * <dt>{Object} scope (optional)</dt>
		 * <dd>The execution scope for the callback.</dd>
		 * <dt>{Any} data (optional)</dt>
		 * <dd>
		 * An optional data object to pass to the callback method
		 * </dd>
		 * </dl>
		 * @method addButton
		 * @return {String} An index to reference the button in the toolbar
		 */
		addButton: function(config) {
			
			if(!YAHOO.lang.isFunction(config.callback)) {
				throw new Error("No valid callback method specified", "toolbar.js");
				return null;
			}
			
			// create the button
			var button = new YAHOO.util.Element(document.createElement("div"));
			button.set("className", _TOOLBAR_BUTTON_CLASS_NAME);
			
			var scope = config.scope ? config.scope : this;
			button.on("click", config.callback, config.data, scope);
			
			if(config.icon) {
				if(!YAHOO.lang.isString(config.icon)) {
					throw new TypeError("The icon has to be a String value", "toolbar.js");
					return null;
				}
				var iconEl = new YAHOO.util.Element(document.createElement("img"));
				iconEl.set("src", config.icon);
				button.appendChild(iconEl);
			}
			if(config.caption) {
				if(!YAHOO.lang.isString(config.caption)) {
					throw new TypeError("The caption has to be a String value", "toolbar.js");
					return null;
				}
				var captionEl = new YAHOO.util.Element(document.createElement("span"));
				captionEl.set("html", config.caption);
			}
			if(!config.icon && !config.caption) {
				throw new Error("At least one - icon or caption - must be specified", "toolbar.js");
				return false;
			}
			
			if(config.tooltip) {
				if(!YAHOO.lang.isString(config.tooltip)) {
					throw new TypeError("The tooltip has to be a String value", "toolbar.js");
					return null;
				}
				button.set("title", config.tooltip);
				if(iconEl) iconEl.set("alt", config.tooltip);
			}
			
			if(config.group) {
				if(!YAHOO.lang.isString(config.group)) {
					throw new TypeError("The group has to be a String value", "toolbar.js");
					return null;
				}
				
				var groupName = config.group;
				var buttonId = groupName;
			} else {
				var groupName = _DEFAULT_GROUP_NAME;
				var buttonId = "";
			}
			
			// if group doesn't exist create it
			if(!this._groups[groupName])
				this._groups[groupName] = new Array();
			
			this._groups[groupName].push(button);
			buttonId += "_" + this._groups[groupName].length-1;
			
			this._update();
			
			return buttonId;
		},
		
		/**
		 * Remove the button with the specified id from the toolbar
		 * @param {String} orientation The orientation of the icon to remove.
		 * @method addButton
		 * @return {Integer} A unique id to reference the button in the toolbar
		 */
		removeButton: function(id) {
			
			var i = id.lastIndexOf("_");
			var groupName = id.substring(0, i-1);
			var index = id.substring(i+1);
			
			this._groups[groupName][index] = undefined;
			this._update();
		},
		
		/**
		 * Show captions for the button groups. (Per default captions are not shown)
		 * @method showGroupCaptions
		 */
		showGroupCaptions: function() {
			this._showGroupCaptions = true;
			this._update();
		},
		
		/**
		 * Hide captions for the button groups. (Per default captions are not shown)
		 * @method hideGroupCaptions
		 */
		hideGroupCaptions: function() {
			this._showGroupCaptions = false;
			this._update();
		}
	
	});
	
	
})();
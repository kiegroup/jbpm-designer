/**
 * Copyright (c) 2010
 * Intalio, Inc
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
*/

if (!ORYX.Plugins) {
	ORYX.Plugins = {};
}  

if (!ORYX.Config) {
	ORYX.Config = {};
} 

// needed to change icons dynamically:
Ext.override(Ext.Button, {
	setIcon: function(url){
		if (this.rendered){
			var btnEl = this.getEl().child(this.buttonSelector);
			btnEl.setStyle('background-image', 'url(' +url+')');
		}
 	}
});

// needed to change tooltips dynamically
Ext.Button.override({
    setTooltip: function(qtipText) {
        var btnEl = this.getEl().child(this.buttonSelector)
        Ext.QuickTips.register({
            target: btnEl.id,
            text: qtipText
        });             
    }
});

ORYX.Plugins.UUIDRepositorySave = ORYX.Plugins.AbstractPlugin.extend({
	
    facade: undefined,
	
    construct: function(facade){
		this.facade = facade;
		this.facade.offer({
			'name': ORYX.I18N.Save.save,
			'functionality': this.save.bind(this),
			'group': ORYX.I18N.Save.group,
			'icon': ORYX.PATH + "images/disk.png",
			'description': ORYX.I18N.Save.saveDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0
		});
		
		//capability to set autosave on or off
		if (ORYX.CONFIG.UUID_AUTOSAVE_DEFAULT === undefined) {
			ORYX.CONFIG.UUID_AUTOSAVE_DEFAULT = true;
		}
		autosaveicon = ORYX.PATH + "images/disk_multiple_disabled.png";
		autosavetip = ORYX.I18N.Save.autosaveDesc_off;

		if (ORYX.CONFIG.UUID_AUTOSAVE_DEFAULT) {
			autosaveicon = ORYX.PATH + "images/disk_multiple.png";
			autosavetip = ORYX.I18N.Save.autosaveDesc_on;
		}
					
		autosavecfg = {
			'name': ORYX.I18N.Save.autosave,
			'group': ORYX.I18N.Save.group,
			'functionality': function(context) {
			   this.setautosave(ORYX.CONFIG.UUID_AUTOSAVE_INTERVAL);
			   if (this.autosaving) {
				   context.setIcon(ORYX.PATH + "images/disk_multiple.png"); 
				   context.setTooltip(ORYX.I18N.Save.autosaveDesc_on);
			   } else {
				   context.setIcon(ORYX.PATH + "images/disk_multiple_disabled.png");
				   context.setTooltip(ORYX.I18N.Save.autosaveDesc_off);
			   }
			   context.hide();
			   context.show();
		    }.bind(this),
			'icon': autosaveicon,
			'description': autosavetip,
			'index': 2,
			'minShape': 0,
			'maxShape': 0
		};
		this.facade.offer(autosavecfg);

		// ask before closing the window
		this.changeDifference = 0;		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_EXECUTE, function(){ this.changeDifference++; });
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_EXECUTE_COMMANDS, function(){this.changeDifference++; });
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_UNDO_ROLLBACK, function(){this.changeDifference--; });
		
		window.onbeforeunload = function(){
			if (this.changeDifference > 0){
				return ORYX.I18N.Save.unsavedData;
			}
		}.bind(this);
		
		// let's set autosave on.
		this.autosaveFunction = function() { if (/*savePlugin.changeDifference != 0*/true) { this._save(this, true, true); }}.bind(this, autosavecfg);
		this.setautosave(ORYX.CONFIG.UUID_AUTOSAVE_INTERVAL);
	},
	
	/**
	 * Switches autosave on or off.
	 * @param savePlugin the button.
	 */
	setautosave: function(interval) {
		if (this.autosaving === undefined) {
			this.autosaving = !ORYX.CONFIG.UUID_AUTOSAVE_DEFAULT;
		}
		
		value = !this.autosaving;
		if (value) {
			this.autosaveInternalId = self.setInterval(this.autosaveFunction, interval);
		} else {
			self.clearInterval(this.autosaveInternalId);
		}
		
		this.autosaving = value;
	},
	
	/**
	 * Saves the current model.
	 */
	save: function() {
		this._save(this, false, false);
	},
	
	/**
	 * Saves data by calling the backend.
	 * @param asynchronous whether saving should occur asynchronously
	 */
	_save: function(savePlugin, asynchronous, asave) {
		this.showSaveStatus(savePlugin, asynchronous);
		var svgDOM = DataManager.serialize(this.facade.getCanvas().getSVGRepresentation(true));
		var serializedDOM = Ext.encode(this.facade.getJSON());
		var rdf = this.getRDFFromDOM();

		// Send the request to the server.
		new Ajax.Request(ORYX.CONFIG.UUID_URL(), {
                method: 'POST',
                asynchronous: asynchronous,
                postBody: Ext.encode({data: serializedDOM, svg : svgDOM, uuid: ORYX.UUID, rdf: rdf, profile: ORYX.PROFILE, savetype: asave}),
			onSuccess: (function(transport) {
				//show saved status
				this.facade.raiseEvent({
						type:ORYX.CONFIG.EVENT_LOADING_STATUS,
						text:ORYX.I18N.Save.saved
					});
			}).bind(this),
			onFailure: (function(transport) {
				// raise loading disable event.
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });


				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Save.failed);
				
				ORYX.log.warn("Saving failed: " + transport.responseText);
			}).bind(this),
			on403: (function(transport) {
				// raise loading disable event.
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });


				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Save.noRights);
				
				ORYX.log.warn("Saving failed (403): " + transport.responseText);
			}).bind(this)
		});
		this.hideSaveStatus(savePlugin, asynchronous);
		return true;
	},
	
	/**
	 * Shows the saving status
	 * @param asynchronous whether the save is synchronous or asynchronous.
	 */
	showSaveStatus: function(savePlugin, asynchronous) {
		if (asynchronous) {
			//show an icon and a message in the toolbar
			autosavecfg.buttonInstance.setIcon(ORYX.PATH + "images/ajax-loader.gif");
		}
	},
	
	/**
	 * Shows the saving status
	 * @param asynchronous whether the save is synchronous or asynchronous.
	 */
	hideSaveStatus: function(asynchronous) {
		if (asynchronous) {
			//show an icon and a message in the toolbar
			autosavecfg.buttonInstance.setIcon(ORYX.PATH + "images/disk_multiple.png");
		}
	}
});

/**
 * Method to load model or create new one
 * (moved from editor handler)
 */
window.onOryxResourcesLoaded = function() {
	var stencilset = ORYX.Utils.getParamFromUrl('stencilset') || ORYX.CONFIG.SSET;
	var editor_parameters = {
		id: ORYX.UUID,
		stencilset: {
			url: stencilset
		}
	};
	if(!(ORYX.UUID === undefined)) {
		
 		//load the model from the repository from its uuid
		new Ajax.Request(ORYX.CONFIG.UUID_URL(), {
            asynchronous: false,
            method: 'get',
            onSuccess: function(transport) {
				response = transport.responseText;
				
				if (response.length != 0) {
				    try {
					    model = response.evalJSON();
					    editor_parameters.model = model;
				    } catch(err) {
				    	ORYX.LOG.error(err);
				    }
				}
				
			},
            onFailure: function(transport) {
            	ORYX.LOG.error("Could not load the model for uuid " + ORYX.UUID);
			}
        });
	}
	// finally open the editor:
	new ORYX.Editor(editor_parameters);
};
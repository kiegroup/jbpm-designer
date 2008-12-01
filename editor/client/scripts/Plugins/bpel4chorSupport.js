/**
 * Copyright (c) 2008
 * Zhen Peng
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
	
ORYX.Plugins.BPEL4ChorSupport = Clazz.extend({

	facade: undefined,
	
	dialogSupport: undefined,

	/**
	 * Offers the plugin functionality:
	 */
	construct: function(facade) {
		
		this.facade = facade;

		this.dialogSupport = new ORYX.Plugins.TransformationDownloadDialog();
		
	    this.facade.offer({
			'name':ORYX.I18N.BPEL4ChorSupport.exp,
			'functionality': this.exportProcess.bind(this),
			'group': ORYX.I18N.BPEL4ChorSupport.group,
			'icon': ORYX.PATH + "images/bpel4chor_export_icon.png",
			'description': ORYX.I18N.BPEL4chorSupport.expDesc,
			'index': 0,
			'minShape': 0,
			'maxShape': 0});
			
        this.facade.offer({
			'name':ORYX.I18N.BPEL4ChorSupport.imp,
			'functionality': this.importProcess.bind(this),
			'group': ORYX.I18N.BPEL4ChorSupport.group,
			'icon': ORYX.PATH + "images/bpel4chor_import_icon.png",
			'description': ORYX.I18N.BPEL4ChorSupport.impDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0});
		},
	
	exportProcess: function(){
	
		// raise loading enable event
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE
        });
            
		// asynchronously ...
        window.setTimeout((function(){
			
			// ... save synchronously
            this.exportSynchronously();
			
			// raise loading disable event.
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_LOADING_DISABLE
            });
			
        }).bind(this), 10);

		return true;
    },
    
    exportSynchronously: function() {
    
	},
	
	importProcess: function(){
		this.openUploadDialog ();
	},
	
	/**
	 * Opens a upload dialog.
	 */
	openUploadDialog: function(){
		
	},
	
	loadERDF: function(erdfString){
								
		var parser = new DOMParser();			
		var doc    = parser.parseFromString(erdfString ,"text/xml");
		
		alert(erdfString);
		this.facade.importERDF( doc );

	}
	
});
	
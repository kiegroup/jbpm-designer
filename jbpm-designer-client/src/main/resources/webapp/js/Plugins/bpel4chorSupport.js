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
	
ORYX.Plugins.BPEL4ChorSupport = ORYX.Plugins.AbstractPlugin.extend({

	/**
	 * Offers the plugin functionality:
	 */
    construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);

		this.dialogSupport = new ORYX.Plugins.TransformationDownloadDialog();
		
	    this.facade.offer({
			'name':ORYX.I18N.BPEL4ChorSupport.exp,
			'functionality': this.exportProcess.bind(this),
			'group': ORYX.I18N.JSONSupport.exp.group,
			dropDownGroupIcon: ORYX.PATH + "images/export2.png",
			'icon':  ORYX.PATH + "images/bpel4chor_export_icon.png",
			'description': ORYX.I18N.BPEL4ChorSupport.expDesc,
			'index': 0,
			'minShape': 0,
			'maxShape': 0
		});
			
        this.facade.offer({
			'name':ORYX.I18N.BPEL4ChorSupport.imp,
			'functionality': this.importProcess.bind(this),
			'group': ORYX.I18N.JSONSupport.imp.group,
			dropDownGroupIcon: ORYX.PATH + "images/import.png",
			'icon':  ORYX.PATH + "images/bpel4chor_import_icon.png",
			'description': ORYX.I18N.BPEL4ChorSupport.impDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){ return false}
		});
	},
	
	/***************************** export **********************************/	
		
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

        var resource = location.href;
		
		
		try {
			var serialized_rdf = this.getRDFFromDOM();
			if (!serialized_rdf.startsWith("<?xml")) {
				serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;
			}
			  
			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.BPEL4CHOR_EXPORT_URL, {
				method: 'POST',
				asynchronous: false,
				parameters: {
					resource: resource,
					data: serialized_rdf
				},
                onSuccess: function(response){
                	this.displayResult(response.responseText);
				}.bind(this)
			});
                	
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
	 	}
    
	},
	
	
	/**
	 * Builds up the data that will be shown in the result dialog of
	 * the BPEL4Chor transformation.
	 * For this purpose the process names are determined and
	 * it is checked if the topology and process were generated
	 * successfully.
	 * 
	 * @param {String}   topology     The generated topology 
	 * @param {String}   grounding    The generated grounding 
	 * @param {String[]} processes    The generated processes
	 */
	buildTransData: function(topology, grounding, processes) {
		var data = [["topology", topology, this.dialogSupport.getResultInfo(topology)]];
		
		var counter = 1;
		
		//if (grounding.indexOf("operation")>0){
		if (grounding.indexOf("grounding")>0){
			data[1]= ["grounding", grounding, this.dialogSupport.getResultInfo(grounding)];
			counter++;
		};
		
		for (var i = 0; i < processes.length; i++) {
			var name = this.dialogSupport.getProcessName(processes[i]);
			if (name == undefined) {
				name = "Process " + (i+1);
			}
			data[i+counter] = [name, processes[i], this.dialogSupport.getResultInfo(processes[i])];
		}	
		
		return data;
	},
	
	
	/**
	 * Analyzes the result of the servlet call.
	 * 
	 * If an fault occured or the answer is undefined, the error is shown
	 * using a message dialog.
	 * 
	 * If the first result starts with "ParserError" the error is shown using an 
	 * error dialog. Otherwise the result is shown using the result dialog.
	 * 
	 * @param {Object} result - the result of the transformation servlet (JSON)
	 */
	displayResult: function(result) {
		this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});

		var resultString = '(' + result + ')';
		
		//alert (resultString);
		
		var resultObject;
		
		try {
			resultObject = eval(resultString);
		} catch (e1) {
			alert("Error during evaluation of result: " + e1 + "\r\n" + resultString);
		}
		
		if ((!resultObject.res) || (resultObject.res.length == 0)) {
			this.dialogSupport.openMessageDialog(ORYX.I18N.TransformationDownloadDialog.error,ORYX.I18N.TransformationDownloadDialog.noResult);
		} else if (resultObject.res[0].content.indexOf("Parser Error")>0) {
			this.dialogSupport.openErrorDialog(resultObject.res[0].content);
		} else {
			var topology = resultObject.res[0].content;
			var grounding = resultObject.res[1].content;
			var processes = new Array();
			for (var i = 2; i < resultObject.res.length; i++) {
				processes[i-2] = resultObject.res[i].content;
			}
			var data = this.buildTransData(topology,grounding,processes);
			
			this.dialogSupport.openResultDialog(data);
		}
	},
	
	/***************************** import **********************************/
	
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
	
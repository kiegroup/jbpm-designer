/**
 * Copyright (c) 2010
 * Changhua Li
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
	
ORYX.Plugins.BPEL4Chor2BPELSupport = ORYX.Plugins.AbstractPlugin.extend({

	/**
	 * Offers the plugin functionality:
	 */
    construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);

        this.dialog2BPELSupport = new ORYX.Plugins.TransformationDownloadDialogForBPEL4Chor();
        
	    this.facade.offer({
			'name':ORYX.I18N.BPEL4Chor2BPELSupport.exp,
			'functionality': this.exportProcess.bind(this),
			'group': ORYX.I18N.JSONSupport.exp.group,
			dropDownGroupIcon: ORYX.PATH + "images/export2.png",
			'icon':  ORYX.PATH + "images/bpel4chor2bpel_export_icon.png",
			'description': ORYX.I18N.BPEL4Chor2BPELSupport.expDesc,
			'index': 0,
			'minShape': 0,
			'maxShape': 0
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
			new Ajax.Request(ORYX.CONFIG.BPEL4CHOR2BPEL_EXPORT_URL, {
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
	 * the BPEL4Chor to BPEL transformation.
	 * For this purpose the process and wsdl names are determined 
	 * it is checked if them were generated successfully.
	 * 
	 * @param {String[]} bpelArray    The generated process array of bpel processes
	 * @param {String[]} wsdlArray    The generated wsdl array of bpel processes 
	 */
	buildTransData: function(bpelArray, wsdlArray) {
		var data = new Array();
		for(var i = 0; i < bpelArray.length; i++){
			var name = this.dialog2BPELSupport.getBPELName(bpelArray[i]);
			if(name == undefined){
				name = "Process " + (i+1);
			}
			data[i] = [name, bpelArray[i], this.dialog2BPELSupport.getResultInfo(bpelArray[i])];
		}
		
		for(var i = 0; i < wsdlArray.length; i++){
			var name = this.dialog2BPELSupport.getBPELName(bpelArray[i]);	// name of wsdl file should be same with bpel file 
			name = name + "-wsdl";
			if(name == undefined){
				name = "WSDL " + (i+1);
			}
			data[i+bpelArray.length] = [name, wsdlArray[i], this.dialog2BPELSupport.getResultInfo(wsdlArray[i])];
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
			this.dialog2BPELSupport.openMessageDialog(ORYX.I18N.TransformationDownloadDialog.error,ORYX.I18N.TransformationDownloadDialog.noResult);
		} else if (resultObject.res[0].contentBPEL.indexOf("Parser Error")>0) {
			this.dialog2BPELSupport.openErrorDialog(resultObject.res[0].contentBPEL);
		} else {
			var bpelArray = new Array();
			var wsdlArray = new Array();
			for (var i = 0; i < resultObject.res.length; i++) {
				bpelArray[i] = resultObject.res[i].contentBPEL;
				wsdlArray[i] = resultObject.res[i].contentWSDL;
			}
			var data = this.buildTransData(bpelArray, wsdlArray);
			
			//alert(data);
			
			this.dialog2BPELSupport.openResultDialog(data);
		}
	}
});
	
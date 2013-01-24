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


if(!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}


ORYX.Plugins.TransformationDownloadDialogForBPEL4Chor = {
		
	construct: function() {
		arguments.callee.$.construct.apply(this, arguments);
	},
	/**
	 * Opens a dialog that presents the results of a transformation.
	 * The dialog shows a list containing the resulting XML files.
	 * Each file can be shown in a new window or downloaded.
     *
	 * @param {Object} data The data to be shown in the dialog
	 * Format: array with three elements: 
	 *   * file - the file
	 *   * result - the content of file, may also be an error message.
	 *   * info - status of the result: "success" or "error"
	 */
	openResultDialog: function(data) {

		var ds = new Ext.data.Store({
	        proxy: new Ext.data.MemoryProxy(data),
	        reader: new Ext.data.ArrayReader({}, [
	               {name: 'file', type: 'string'},
	               {name: 'result', type: 'string'},
	               {name: 'info', type: 'string'}
	        	])
		});
		
		ds.load();

		// renderer
		var infoRenderer = function (val){
            if(val == "success"){
                return '<span style="color:green;">' + val + '</span>';
            }else if(val == "error"){
                return '<span style="color:red;">' + val + '</span>';
            }
            return val;
        };
	
		var cm = new Ext.grid.ColumnModel([
		    {id:'file',header: "File", width: 200, sortable: false, dataIndex: 'file', resizable: false},
		    {header: "Info", width: 75, sortable: false, dataIndex: 'info', renderer: infoRenderer, resizable: false} 
		]);
				
		var grid = new Ext.grid.GridPanel({
			store:ds,
	        cm: cm,
	        sm: new Ext.grid.RowSelectionModel({ 	singleSelect:true }),
			autoWidth: true
	    });
		
	    var toolbar = new Ext.Toolbar();
		
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: ORYX.I18N.TransformationDownloadDialog.transResult, 
			autoHeight: true, 
			width: 297, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			proxyDrag: true,
			resizable:false,
			items:[toolbar, grid]
		});
	
		dialog.on('hide', function(){
			dialog.destroy(true);
			grid.destroy(true);
			delete dialog;
			delete grid;
		});
		dialog.show();
		
		toolbar.add({
			icon: 'images/view.png', // icons can also be specified inline
	        cls: 'x-btn-icon',
    	    tooltip: ORYX.I18N.TransformationDownloadDialog.showFile,
			handler: function() {
				var ds = grid.getStore();
				var selection = grid.getSelectionModel().getSelected();
				if (selection == undefined) {
					return;
				}
				var show = selection.get("result");
				if (selection.get("info") == "success") {
					this.openXMLWindow(show);
				} else {
					this.openErrorWindow(show);
				}
			}.bind(this)
		});
		toolbar.add({
			icon: 'images/disk.png', // icons can also be specified inline
	        cls: 'x-btn-icon',
    	    tooltip: ORYX.I18N.TransformationDownloadDialog.downloadFile,
			handler: function() {
				var ds = grid.getStore();
				var selection = grid.getSelectionModel().getSelected();
				if (selection == undefined) {
					return;
				}
				this.openDownloadWindow(selection, false);
			}.bind(this)
		});
		toolbar.add({
			icon: 'images/disk_multi.png', // icons can also be specified inline
	        cls: 'x-btn-icon',
    	    tooltip: ORYX.I18N.TransformationDownloadDialog.downloadAll,
			handler: function() {
				var ds = grid.getStore();				
				this.openDownloadWindow(ds.getRange(0, ds.getCount()), true);
			}.bind(this)
		});			

		// Select the first row
		grid.getSelectionModel().selectFirstRow();

	},
	
	/**
	 * Opens a download window for downloading the given content.
	 * 
	 * Creates a submit form to send the contents to the 
	 * Oryx Legacy File Download Servlet (MultiDownloader).
	 * 
	 * @param {Object} content The content to be downloaded. If it is a zip 
	 *                         file, then this should be an array of contents.
	 * @param {Object} zip     True, if it is a zip file, false otherwise
	 */
	openDownloadWindow: function(content, zip) {
		var win = window.open("");
		if (win != null) {
			win.document.open();
			win.document.write("<html><body>");
			var submitForm = win.document.createElement("form");
			win.document.body.appendChild(submitForm);
			
			try {
				if (zip) {
					for (var i = 0; i < content.length; i++) {
						var file = this.addFileExtension(content[i].get("file"));
						if(file.include("-wsdl")){
							file = file.replace("-wsdl","");
						}	
						submitForm.appendChild( this.createHiddenElement("download_" + i, content[i].get("result")));
						submitForm.appendChild( this.createHiddenElement("file_" + i, file));
					}
				} else {
					var file = this.addFileExtension(content.get("file"));
					if(file.include("-wsdl")){
						file = file.replace("-wsdl","");
					}
					submitForm.appendChild( this.createHiddenElement("download", content.get("result")));
					submitForm.appendChild( this.createHiddenElement("file", file));
				}
			} catch (error){
				this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
				Ext.Msg.alert(ORYX.I18N.Oryx.title, error);
			}
			
			submitForm.method = "POST";
			win.document.write("</body></html>");
			win.document.close();
			submitForm.action= "download";
			submitForm.submit();
		}		
	},
	
	/**
	 * Adds a file extension to the given file name. If the file
	 * include the name "wsdl", an .wsdl extension will
	 * be added. Otherwise a .bpel extension will be added
	 * 
	 * @param {Object} file The file name to add the extension to.
	 */
	addFileExtension: function(file) {
		if (file.include("wsdl")) {
			return file + ".wsdl";
		} else {
			return file + ".bpel";
		}
	},
	
	/**
	 * Determines if the result is an XML file or not.
	 * For this purpose it is determined if the given
	 * result starts with "<?xml".
	 * 
	 * @param {Object} result The result to be checked.
	 * @return "success" if it is an XML file, "error" otherwise
	 */
	getResultInfo: function(result) {
		if (!result) {
			return "error";
		} else if (result.substr(0, 5) == "<?xml") {
			return "success";
		}
		
		return "error";
	},
	
	/**
	 * Determines the process name for a given bpel
	 * string. 
	 * 
	 * @param {String} process The BPEL process.
	 */
	getBPELName: function(bpel) {
		var parser	= new DOMParser();
		var doc		= parser.parseFromString(bpel,"text/xml");
		var name 	= doc.documentElement.getAttribute("name");
		return name;
	}
}	

ORYX.Plugins.TransformationDownloadDialogForBPEL4Chor = ORYX.Plugins.TransformationDownloadDialog.extend(ORYX.Plugins.TransformationDownloadDialogForBPEL4Chor);
/**
 * Copyright (c) 2007
 * Kerstin Pfitzner
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


ORYX.Plugins.TransformationDownloadDialog = {

	construct: function() {
	},
	
	

	
	/**
	 * Opens a message dialog with the given title that shows
	 * the content. The dialog just shows a message and has a 
	 * "OK" button to be closed.
	 * 
	 * @param {String} title   The title of the dialog
	 * @param {String} content The content to be shown in the dialog
	 */
	openMessageDialog: function(title, content) {
		
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: title, 
			modal:true,
			height: 120,
			width: 400,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			resizable:true,
			proxyDrag: true,
			autoScroll:true,
			buttonAlign:"center",
            bodyStyle:'padding:10px',
            html:'<span class="ext-mb-text">' + content + '</span>'
                        
		});
		//dialog.addKeyListener(27, dialog.hide, dialog);
		dialog.addButton('OK', dialog.hide, dialog);
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		
		dialog.show();
	},
	
	
	/**
	 * Opens an error dialog that shows the given content.
	 * The error is shown in a text area.
	 * 
	 * @param {String} content The error to be shown
	 */
	openErrorDialog: function(content) {
		// Basic Dialog
		var text = new Ext.form.TextArea({
			id:'error-field',
			fieldLabel: ORYX.I18N.TransformationDownloadDialog.error,
			name: 'desc',
			height: 405,
			width: 633,
			preventScrollbars: true,
			value: content,
			readOnly:true
        });
		
		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: ORYX.I18N.TransformationDownloadDialog.errorParsing, 
			modal:true,
			height: 450,
			width: 650,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			resizable:false,
			proxyDrag: true,
			autoScroll:false
		});
		//dialog.addKeyListener(27, dialog.hide, dialog);
		dialog.on('hide', function(){
			dialog.destroy(true);
			text.destroy(true);
			delete dialog;
			delete text;
		});
		text.render(dialog.body);
		
		dialog.show();
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
	 * Opens a new window that shows the given XML content.
	 * 
	 * @param {Object} content The XML content to be shown.
	 */
	openXMLWindow: function(content) {
		var win = window.open(
		   'data:application/xml,' + encodeURIComponent([
		     content
		   ].join('\r\n')),
		   '_blank', "resizable=yes,width=600,height=600,toolbar=0,scrollbars=yes"
		);
	},
	
	
	/**
	 * Opens a window that shows the given text content.
	 * 
	 * @param {Object} content The text content to be shown.
	 */
	openErrorWindow: function(content) {
		var win = window.open(
		   'data:text/html,' + encodeURIComponent([
		     "<html><body><pre>" + content + "</pre></body></html>"
		   ].join('\r\n')),
		   '_blank', "resizable=yes,width=800,height=300,toolbar=0,scrollbars=yes"
		);
	},
	
	
	/**
	 * Creates a hidden form element to communicate parameter values
	 * to a php file.
	 * 
	 * @param {Object} name  The name of the hidden field
	 * @param {Object} value The value of the hidden field
	 */
	createHiddenElement: function(name, value) {
		var newElement = document.createElement("input");
		newElement.name=name;
		newElement.type="hidden";
		newElement.value = value;
		return newElement
	},
	
	
	/**
	 * Adds a file extension to the given file name. If the file
	 * has the name "topology" or "XPDL4Chor" an .xml extension will
	 * be added. Otherwise a .bpel extension will be added
	 * 
	 * @param {Object} file The file name to add the extension to.
	 */
	addFileExtension: function(file) {
		if ((file.toLowerCase() == "topology") || (file == "XPDL4Chor")) {
			return file + ".xml";
		} else {
			return file + ".bpel";
		}
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
			
			if (zip) {
				for (var i = 0; i < content.length; i++) {
					var file = this.addFileExtension(content[i].get("file"));
					submitForm.appendChild( this.createHiddenElement("download_" + i, content[i].get("result")));
					submitForm.appendChild( this.createHiddenElement("file_" + i, file));
				}
			} else {
				var file = this.addFileExtension(content.get("file"));
				submitForm.appendChild( this.createHiddenElement("download", content.get("result")));
				submitForm.appendChild( this.createHiddenElement("file", file));
			}
			
			submitForm.method = "POST";
			win.document.write("</body></html>");
			win.document.close();
			submitForm.action= "download";
			submitForm.submit();
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
	 * Determines the process name for a given process
	 * string. 
	 * 
	 * @param {String} process The BPEL4Chor process.
	 */
	getProcessName: function(process) {
		var parser	= new DOMParser();
		var doc		= parser.parseFromString(process,"text/xml");
		var name 	= doc.documentElement.getAttribute("name");
		return name;
	}
}

ORYX.Plugins.TransformationDownloadDialog = Clazz.extend(ORYX.Plugins.TransformationDownloadDialog);
/**
 * Copyright (c) 2008
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

if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

ORYX.Plugins.XFormsExport = Clazz.extend({
	
	CSS_URL: "/oryx/css/xforms_default.css",

	facade: undefined,

	construct: function(facade) {
		this.facade = facade;

		this.facade.offer({
			'name':ORYX.I18N.XFormsSerialization.exportXForms,
			'functionality': this.exportIt.bind(this),
			'group': ORYX.I18N.XFormsSerialization.group,
			'icon': ORYX.PATH + "images/xforms_export.png",
			'description': ORYX.I18N.XFormsSerialization.exportXFormsDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0});
	},

	exportIt: function(){

		// raise loading enable event
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE
        });
		
		//this.checkClientXFormsSupport();
            
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
		
		//get current DOM content
		var serializedDOM = DataManager.__persistDOM(this.facade);

		//add namespaces
		serializedDOM = '<?xml version="1.0" encoding="utf-8"?>' +
		'<html xmlns="http://www.w3.org/1999/xhtml" ' +
		'xmlns:b3mn="http://b3mn.org/2007/b3mn" ' +
		'xmlns:ext="http://b3mn.org/2007/ext" ' +
		'xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" ' +
		'xmlns:atom="http://b3mn.org/2007/atom+xhtml">' +
		'<head profile="http://purl.org/NET/erdf/profile">' +
		'<link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />' +
		'<link rel="schema.dcTerms" href="http://purl.org/dc/terms/ " />' +
		'<link rel="schema.b3mn" href="http://b3mn.org" />' +
		'<link rel="schema.oryx" href="http://oryx-editor.org/" />' +
		'<link rel="schema.raziel" href="http://raziel.org/" />' +
		'<base href="' +
		location.href.split("?")[0] +
		'" />' +
		'</head><body>' +
		serializedDOM +
		'</body></html>';
		//convert to RDF
		var parser = new DOMParser();
		var parsedDOM = parser.parseFromString(serializedDOM, "text/xml");
		parsedDOM.normalize();
		
		var xsltPath = ORYX.PATH + "lib/extract-rdf.xsl";
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		xsltProcessor.importStylesheet(xslRef);
		try {
			var rdf = xsltProcessor.transformToDocument(parsedDOM);
			var serialized_rdf 	= (new XMLSerializer()).serializeToString(rdf);
			serialized_rdf 		= serialized_rdf.startsWith("<?xml") ? serialized_rdf : "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;

			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.XFORMS_EXPORT_URL, {
				method: 'POST',
				asynchronous: false,
				parameters: {
					resource: resource,
					data: serialized_rdf,
					css: this.CSS_URL
				},
				onSuccess: function(request){
					
						var win = window.open("data:text/xml," +
								request.responseText, 
								"_blank", 
								"resizable=yes,width=640,height=480,toolbar=0,scrollbars=yes");
						
				}
			});
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert("Oryx", error);
	 	}
	},
	
	checkClientXFormsSupport: function() {
		if(!clientSupportsXForms) {
			
			var output = ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc;

			var win = new Ext.Window({
                            width: 320,
                            height: 240,
                            resizable: false,
                            minimizable: false,
                            modal: true,
                            autoScroll: true,
                            title: ORYX.I18N.XFormsSerialization.noClientXFormsSupport,
                            html: output,
                            buttons: [{
                                text: ORYX.I18N.XFormsSerialization.ok,
                                handler: function(){
                                    win.hide();
                                }
                            }]
                        });
        	win.show();
			
		}
	}

});


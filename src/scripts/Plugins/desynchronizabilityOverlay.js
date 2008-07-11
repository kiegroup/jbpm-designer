
/**
 * Copyright (c) 2008, Gero Decker
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
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.DesynchronizabilityOverlay = Clazz.extend({

    facade: undefined,
    
    construct: function(facade){
		
        this.facade = facade;
        
		this.active = false;
		this.el 	= undefined;
		this.callback = undefined;
		
        this.facade.offer({
            'name': "Desynchronizability Checker",
            'functionality': this.showOverlay.bind(this),
            'group': "Overlay",
            'icon': ORYX.PATH + "images/bpmn2pn.png",
            'description': "Desynchronizability Checker",
            'index': 3,
            'minShape': 0,
            'maxShape': 0
        });
		
    },
    
	showOverlay: function(){

		if (this.active) {
			
			this.facade.raiseEvent({
				type: 	"overlay.hide",
				id: 	"desynchronizability"
			});
			this.active = !this.active;				

		} else {
			
			// Force to set all resource IDs
			var serializedDOM = DataManager.serializeDOM( this.facade );

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
		var xsltPath = ORYX.PATH + "lib/extract-rdf.xsl";
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		xsltProcessor.importStylesheet(xslRef);
		try {
			var rdf = xsltProcessor.transformToDocument(parsedDOM);
			var serialized_rdf = (new XMLSerializer()).serializeToString(rdf);
			serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;
			
			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.DESYNCHRONIZABILITY_URL, {
				method: 'POST',
				asynchronous: false,
				parameters: {
					resource: location.href,
					data: serialized_rdf
				},
				onSuccess: function(request){
						var resp = request.responseText.evalJSON();

						if (resp.conflicttransitions) {
						if (resp.conflicttransitions.length > 0) {
						
							// Get all Valid ResourceIDs and collect all shapes
							var transitionshapes = resp.conflicttransitions.collect(function(res){ return this.facade.getCanvas().getChildShapeByResourceId( res ) }.bind(this)).compact();

							this.facade.raiseEvent({
								type: 			"overlay.show",
								id: 			"desynchronizability",
								shapes: 		transitionshapes,
								attributes: 	{fill: "red", stroke: "black"}
							});

							this.active = !this.active;				

						} else {

							Ext.Msg.alert("Oryx", "The net is desynchronizable.");
							// var win = window.open('data:text/plain,' +request.responseText, '_blank', "resizable=yes,width=640,height=480,toolbar=0,scrollbars=yes");
						}
						} else if (resp.syntaxerrors) {

							// Get all Valid ResourceIDs and collect all shapes
//							var shapes = transitions.collect(function(res){ return this.facade.getCanvas().getChildShapeByResourceId( res ) }.bind(this)).compact();

							Ext.Msg.alert("Oryx", "The net has "+resp.syntaxerrors.length+" syntax errors.");

//							this.active = !this.active;				

						} else {
							Ext.Msg.alert("Oryx", "Invalid answer from server.");
						}
				}.bind(this)
			});
			
		} catch (error){
			this.facade.raiseEvent({type:'loading.disable'});
			Ext.Msg.alert("Oryx", error);
	 	}

		}
		
	}	
    
});

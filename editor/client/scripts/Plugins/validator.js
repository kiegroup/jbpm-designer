
/**
 * Copyright (c) 2008, Kai Schlichting
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

ORYX.Plugins.Validator = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        
        this.active = false;
        this.raisedEventIds = [];
        
        this.facade.offer({
            'name': "BPMN Validator",
            'functionality': this.load.bind(this),
            'group': "Verification",
            'icon': ORYX.PATH + "images/checker_validation.png",
            'description': "Validate",
            'index': 1,
            'toggle': true,
            'minShape': 0,
            'maxShape': 0
        });
    },
    
    load: function(button, pressed){
        if (!pressed) {
            this.hideOverlays();
            this.active = !this.active;
        }
        else {
            this.validate();
        }
    },
    
    hideOverlays: function(){
        this.raisedEventIds.each(function(id){
            this.facade.raiseEvent({
                type: ORYX.CONFIG.EVENT_OVERLAY_HIDE,
                id: id
            });
        }
.bind(this));
        
        this.raisedEventIds = [];
    },
    
    getRdf: function(){
        // Force to set all resource IDs
        var serializedDOM = DataManager.serializeDOM(this.facade);
        
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
        
        var rdf = xsltProcessor.transformToDocument(parsedDOM);
        var serialized_rdf = (new XMLSerializer()).serializeToString(rdf);
        
        return serialized_rdf;
    },
    
    validate: function(){
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_LOADING_ENABLE,
            text: ORYX.I18N.Validator.checking
        });
      
        // Send the request to the server.
        new Ajax.Request(ORYX.CONFIG.VALIDATOR_URL, {
            method: 'POST',
            asynchronous: false,
            parameters: {
                resource: location.href,
                data: this.getRdf()
            },
            onSuccess: function(request){
                var result = Ext.decode(request.responseText);
                
                // This should be implemented by child instances of validator 
                this.handleValidationResponse(result);
                
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });
            }
.bind(this),
            onFailure: function(){
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });
            }.bind(this)
        });
    },
    
    showOverlay: function(shape, errorMsg){
    
        var id = "syntaxchecker." + this.raisedEventIds.length;
        
        var cross = ORYX.Editor.graft("http://www.w3.org/2000/svg", null, ['path', {
            "title": errorMsg,
            "stroke-width": 5.0,
            "stroke": "red",
            "d": "M20,-5 L5,-20 M5,-5 L20,-20",
            "line-captions": "round"
        }]);
        
        this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id: id,
            shapes: [shape],
            node: cross,
            nodePosition: shape instanceof ORYX.Core.Edge ? "START" : "NW"
        });
        
        this.raisedEventIds.push(id);
        
        return cross;
    }
});

ORYX.Plugins.BPMNValidator = Ext.extend(ORYX.Plugins.Validator, {
    handleValidationResponse: function(result){
        var conflictingNodes = result.conflictingNodes;
        var leadsToEnd = result.leadsToEnd;
        
        if (!leadsToEnd) {
            Ext.Msg.alert("Oryx", "The process will never reach a final state!");
        }
        else if (conflictingNodes.size() > 0) {
            conflictingNodes.each(function(node){
                sh = this.facade.getCanvas().getChildShapeByResourceId(node.id);
                if (sh) {
                    this.showOverlay(sh, "Some following pathes will never reach a final state!");
                }
            }
.bind(this));
            this.active = !this.active;
        }
        else {
            Ext.Msg.alert("Oryx", "No validation errors found!")
        }
    }
});

ORYX.Plugins.EPCValidator = Ext.extend(ORYX.Plugins.Validator, {
  getLabelOfShape: function(node){
    if(node.properties["oryx-title"] === ""){
      return node.id;
    } else {
      return node.properties["oryx-title"];
    }
  },
  findShapeById: function(id){
    return this.facade.getCanvas().getChildShapeByResourceId(id);
  },
  
    handleValidationResponse: function(result){
      //TODO use Ext XTemplate
        var isSound = result.isSound;
        var badStartArcs = result.badStartArcs;
        var badEndArcs = result.badEndArcs;
        var goodInitialMarkings = result.goodInitialMarkings;
        var goodFinalMarkings = result.goodFinalMarkings;
        
        var message = "";
        
        if (isSound) {
          message += "<p><b>The EPC is sound, no problems found!</b></p>";
        } else {
          message += "<p><b>The EPC is <i>NOT</i> sound!</b></p>";
        }
        
        message += "<hr />";
        
        var arrayOfArraysToMessage = function(arrayOfArrays, formatter){
          var message = "<ul>"
          arrayOfArrays.each(function(array){
            message += "<li> - ";
            array.each(function(element){
              message += '"' + formatter(element) + '" ';
            });
            message += "</li>";
          });
          message += "</ul>";
          return message;
        }
        var arrayToMessage = function(array, formatter){
          var message = "<ul>"
          array.each(function(element){
            message += "<li> - " + formatter(element) + "</li>";
          });
          message += "</ul>";
          return message;
        }
        
        message += "<p>There are "+ goodInitialMarkings.length +" initial markings which does not run into a deadlock.</p>";
        message += arrayOfArraysToMessage(goodInitialMarkings, function(arc){
          return this.getLabelOfShape(this.findShapeById(arc.id).getIncomingShapes()[0]);
        }.createDelegate(this));
        message += "<p>The initial markings do not include "+ badStartArcs.length +" start nodes.</p>";
        message += arrayToMessage(badStartArcs, function(arc){
          return this.getLabelOfShape(this.findShapeById(arc.id).getIncomingShapes()[0]);
        }.createDelegate(this));
        
        message += "<hr />";
        
        message += "<p>There are "+ goodFinalMarkings.length +" final markings which can be reached from the initial markings.</p>";
        message += arrayOfArraysToMessage(goodFinalMarkings, function(arc){
          return this.getLabelOfShape(this.findShapeById(arc.id).getOutgoingShapes()[0]);
        }.createDelegate(this));
        message += "<p>The final markings do not include "+ badEndArcs.length +" end nodes.</p>";
        message += arrayToMessage(badEndArcs, function(arc){
          return this.getLabelOfShape(this.findShapeById(arc.id).getOutgoingShapes()[0]);
        }.createDelegate(this))
        
        message += "<hr />";
        
        message += "<p><i>Remark: Set titles of functions and events to get some nicer output (names instead of ids)</i></p>"
        
        Ext.Msg.alert('Validation Result', message);
    }
});
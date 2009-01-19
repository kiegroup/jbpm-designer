
/**
 * Copyright (c) 2008
 * Kai Schlichting
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
Ext.ns("Oryx.Plugins");

ORYX.Plugins.BPMN2PN = Clazz.extend({
    converterUrl: "/oryx/bpmn2pn",
    
    // Offers the plugin functionality
    construct: function(facade){
    
        this.facade = facade;
       
        this.importBpmn();
    },
    
    /**
     * General helper method for parsing a param out of current location url
     * E.g. "http://oryx.org?param=value", getParamFromUrl("param") => "value"
     * @param {Object} name
     */
    getParamFromUrl: function(name){
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(window.location.href);
        if (results == null) {
            return null;
        }
        else {
            return results[1];
        }
    },
    
    /**
     * Posts rdf (BPNM) to server and loads erdf (Petri net) into canvas
     * @param {Object} bpmnRdf
     */
    bpmnToPn: function(bpmnRdf){
        Ext.Ajax.request({
            url: this.converterUrl,
            method: 'POST',
            success: function(request){
                var parser = new DOMParser();
                var doc = parser.parseFromString(request.responseText, "text/xml");
                this.facade.importERDF(doc);
            }.createDelegate(this),
            failure: function(){
                Ext.Msg.alert('Error', "Couldn't import BPNM diagram.");
            },
            params: {
                rdf: bpmnRdf
            }
        });
    },
    
    /**
     * Loads rdf of given bpmn url
     */
    importBpmn: function(){
        var importBPMNUrl = this.getParamFromUrl("importBPMN");
        if (importBPMNUrl) {
            Ext.Ajax.request({
                url: this.getRdfUrl(importBPMNUrl),
                success: function(request){
                    var bpmnRdf = request.responseText;
                    this.bpmnToPn(bpmnRdf);
                }.createDelegate(this),
                failure: function(request){
                    Ext.Msg.alert('Error', "Don't you have read permissions on given model?")
                },
                method: "GET"
            })
        }
    },
    
    /**
     * getRdfUrl("http://localhost:8080/backend/poem/model/7/self") 
     * => "http://localhost:8080/backend/poem/model/7/rdf"
     * getRdfUrl("http://localhost:8080/backend/poem/model/7/rdf" => "http://localhost:8080/backend/poem/model/7/rdf")
     * @param {String} url
     */
    getRdfUrl: function(url){
        return url.replace(/\/self(\/)?$/, "/rdf")
    }
});

/**
 * Copyright (c) 2008
 * Stefan Krumnow
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

/**
 * Supports EPCs by offering a syntax check and export and import ability..
 * 
 * 
 */
ORYX.Plugins.EPCSupport = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		this.facade = facade;

//		Syntax Check has been migrated to syntaxchecker.js-Framework		

//		this.facade.offer({
//			'name':"Check EPC's Syntax",
//			'functionality': this.checkEPC.bind(this),
//			'group': "epc",
//			'icon': ORYX.PATH + "images/epc_check.png",
//			'description': "Perfom an EPC syntax check",
//			'index': 1,
//			'minShape': 0,
//			'maxShape': 0});

		this.facade.offer({
			'name':"Import EPC",
			'functionality': this.importEPC.bind(this),
			'group': "epc",
			'icon': ORYX.PATH + "images/epc_import.png",
			'description': "Import an AML or EPML file",
			'index': 2,
			'minShape': 0,
			'maxShape': 0});
			
		this.facade.offer({
			'name':"Export EPC",
			'functionality': this.exportEPC.bind(this),
			'group': "epc",
			'icon': ORYX.PATH + "images/epc_export.png",
			'description': "Export diagram to AML or EPML",
			'index': 1,
			'minShape': 0,
			'maxShape': 0});
			
	},
	
//		Syntax Check has been migrated to syntaxchecker.js-Framework	
	
//	/**
//	 * Perfoms a EPC syntax check
//	 */
//	checkEPC: function(){	
//		
//		this.facade.raiseEvent({type:'loading.enable', text:'Checking model'});
//		
//		var checkResult = this.doSyntaxCheck();
//		var errorsArray = checkResult[0];
//		var warningsArray = checkResult[1];
//		
//		/*
//		 * 		display results
//		 */
//		
//		var title = "Syntax Check";
//		var content;
//		
//		if (errorsArray.length > 0){
//			content = "Syntax check failed: Found "+errorsArray.length+" errors (and "+warningsArray.length+" warnings)!";
//		} else if (warningsArray.length > 0) {
//			content = "Syntax check was successful (but "+warningsArray.length+" warnings occured)!";
//		} else {
//			content = "Syntax check was successful!";
//		}
//		
//		array = new Array(content);
//		array = array.concat(errorsArray);
//		array = array.concat(warningsArray);
//		
//		this.facade.raiseEvent({type:'loading.disable'});
//		
//		this.openMultiLineMessageDialog(title, array, 250, 400);
//	},
	
	/**
	 * Imports an AML or EPML description
	 */
	importEPC: function(){
		this.openUploadDialog();
	},		

	
	/**
	 * Exports the diagram into an AML or EPML file
	 */
	exportEPC: function(){

		this.facade.raiseEvent({type:'loading.enable', text:'Exporting model'});
		var xmlSerializer = new XMLSerializer();
		var index = location.href.lastIndexOf("/");

		/*
		 * Syntax - Check:
		 */
		var checkResult = this.doSyntaxCheck();
		var errorsArray = checkResult[0];
		var content;
		if (errorsArray.length > 0) {
			var title = "Export - Syntax Check";
			content = "Syntax check failed: Found " + errorsArray.length + " errors!";
			var array = new Array(content);
			array = array.concat(errorsArray);
			this.facade.raiseEvent({type:'loading.disable'});
			this.openMultiLineMessageDialog(title, array, 250, 400);
			return ;
		}
		
		/*
		 * Save diagram
		 */
		
		this.save();
		
		/*
		 * Transform eRDF -> RDF
		 */
		 
		// get process' name
		var resource = location.search.split("resource=");
		resource = resource[1].split("&")[0];

		// get the current process ...
		var currentDOM = document.implementation.createDocument("", "", null);
		currentDOM.async = false;
		currentDOM.load(location.href);
		
		// ... and add name + id (workaround)           
		var infoNode = document.createElement("div");
		infoNode.setAttribute("id", "generatedProcessInfos");

		var idNode = document.createElement("span");
		idNode.setAttribute("class", "oryx-id");
		idNode.appendChild(document.createTextNode(resource));
		infoNode.appendChild(idNode);

		var nameNode = document.createElement("span");
		nameNode.setAttribute("class", "oryx-name");
		nameNode.appendChild(document.createTextNode(resource));
		infoNode.appendChild(nameNode);

		currentDOM.getElementsByTagName("html")[0].getElementsByTagName("body")[0].appendChild(infoNode);


		var erdf2rdfXslt = location.href.substring(0, index) + "/lib/extract-rdf.xsl";

		var rdfResultString;
		rdfResult = this.transformDOM(currentDOM, erdf2rdfXslt, true);
		if (rdfResult instanceof String) {
			rdfResultString = rdfResult;
			rdfResult = null;
		} else {
			rdfResultString = '<?xml version="1.0" encoding="utf-8"?>' +
				xmlSerializer.serializeToString(rdfResult);
		}
		
		/*
		 * Transform RDF -> EPML
		 */
		var rdf2epmlXslt = location.href.substring(0, index) + "/xslt/RDF2EPML.xslt";
		
		var epmlResult = this.transformDOM(rdfResult, rdf2epmlXslt, true);
		var epmlResultString;
		if (epmlResult instanceof String) {
			epmlResultString = epmlResult;
			epmlResult = null;
		} else {
			epmlResultString = '<?xml version="1.0" encoding="utf-8"?>' +
				xmlSerializer.serializeToString(epmlResult);
		}
		
		/*
		 * Transform EPML -> AML
		 */

// Note: This is uncommented due to the fact, that the epml2amlXslt does not work properly
//      When the XSLT will be fixed, remove the uncomment marks and within the result array below!
//
//		var epml2amlXslt = location.href.substring(0, index) + "/xslt/EPML2AML_2.xslt";
//		
//		var amlResultString = this.transformString(epmlResultString, epml2amlXslt, false);
//		if (amlResultString.substr(0, 12) != "Parse Error:") {
//			amlResultString = '<?xml version="1.0" encoding="utf-8"?>\n' + 
//				'<!DOCTYPE AML SYSTEM "ARIS-Export.dtd" [\n	<!ENTITY LocaleId.DEde "1031">\n	<!ENTITY Codepage.DEde "1252">\n]>\n' + 
//				amlResultString;
//		}

		/*
		 * Show result
		 */
		
		this.facade.raiseEvent({type:'loading.disable'});
		
//		var currentDOMString = xmlSerializer.serializeToString(currentDOM)
		
		var result =  [ 
//			["Serialized DOM (eRDF)", currentDOMString, this.getResultInfo(currentDOMString)],
//			["RDF", rdfResultString, this.getResultInfo(rdfResultString)],
			["export.epml", epmlResultString, this.getResultInfo(epmlResultString)],
//			["export.xml (AML)", amlResultString, this.getResultInfo(amlResultString)]
		];
        this.openResultDialog(result);

    },
	
//		Syntax Check has been migrated to syntaxchecker.js-Framework		
	
//	/**
//	 * 		Syntax Check - Functions:
//	 */	
//	
//	
//	/**
//	 * Performs the actual syntax check.
//	 * 
//	 * @return an array containing 2 arrays:
//	 *  [ errorsArray, warningsArray ]
//	 */
//	doSyntaxCheck: function(){
//
//		var errorsArray = new Array();
//		var warningsArray = new Array();
//		
//		/*
//		 * 		actual checks
//		 */ 
//		
//		// 1st check: Startevents
//		var numberOfStartEvents = 0;
//		// 2nd check: End-events or -functions
//		var numberOfEndNodes = 0;
//		
//		
//		var nodes = this.facade.getCanvas().getChildNodes();
//		var edges = this.facade.getCanvas().getChildEdges();
//		
//		// 3rd check: empty diagram?
//		if (nodes.length == 0 && edges.length == 0){
//			warningsArray.push(" - Warning: The diagram contains no elements.")
//		}
//		
//		for (var i = 0; i < nodes.size(); i++) {
//			
//			var node = nodes[i];
//			var stencil = node.getStencil();
//			var incoming = node.getIncomingShapes();
//			var outgoing = node.getOutgoingShapes();
//			var stencilName = stencil.title();
//			var title = node.properties["oryx-title"];
//	
//			
//			
//			if (stencil.id() == stencil.namespace() + "Event") {
//			
//				// 4th check: not connected node
//				if (this.numberOfSequenceFlows(incoming) + this.numberOfSequenceFlows(outgoing) == 0){
//					errorsArray.push(" - Error: The event '"+title+"' has no incoming and outgoing control flow.");
//					continue;
//				}
//				
//				if (this.numberOfSequenceFlows(incoming) == 0) {
//					numberOfStartEvents += 1;
//				}
//				if (this.numberOfSequenceFlows(outgoing) == 0) {
//					numberOfEndNodes += 1;
//				}
//				// 5th check: to many connections?
//				if (this.numberOfSequenceFlows(incoming) > 1) {
//					errorsArray.push(" - Error: The event '"+title+"' has more than one incoming connections.");
//				}
//				if (this.numberOfSequenceFlows(outgoing) > 1) {
//					errorsArray.push(" - Error: The event '"+title+"' has more than one outgoing connections.");
//				}
//				// 6th check: bipartite?
//				var nextArray = this.getNextEventsOrFunctionsShapes(outgoing);
//				for (var x = 0; x < nextArray.size(); x++){
//					var next = nextArray[x];
//					if (next.getStencil().id() == stencil.namespace() + "Event"){
//						errorsArray.push(" - Error: The event '"+title+"' is followed by another Event.");
//						break;
//					}
//				}							
//			}
//			else if (stencil.id() == stencil.namespace() + "Function") {
//				// 4th check: not connected node
//				if (this.numberOfSequenceFlows(incoming) + this.numberOfSequenceFlows(outgoing) == 0){
//					errorsArray.push(" - Error: The function '"+title+"' has no incoming and outgoing control flow.");
//					continue;
//				}
//				
//				// 7th check: Function without input?
//				if (this.numberOfSequenceFlows(incoming) == 0) {
//					errorsArray.push(" - Error: The function '" + title + "' has no incoming control flow.");
//				}
//				if (this.numberOfSequenceFlows(outgoing) == 0) {
//					errorsArray.push(" - Error: The function '" + title + "' has no outgoing control flow.");
//				}
//				// 5th check: to many connections?
//				if (this.numberOfSequenceFlows(incoming) > 1) {
//					errorsArray.push(" - Error: The function '"+title+"' has more than one incoming control flow.");
//				}
//				if (this.numberOfSequenceFlows(outgoing) > 1) {
//					errorsArray.push(" - Error: The function '"+title+"' has more than one outgoing control flow.");
//				}
//				// 6th check: bipartite?
//				var nextArray = this.getNextEventsOrFunctionsShapes(outgoing);
//				for (var x = 0; x < nextArray.size(); x++){
//					var next = nextArray[x];
//					if (next.getStencil().id() == stencil.namespace() + "Function"){
//						errorsArray.push(" - Error: The Function '"+title+"' is followed by another Function.");
//						break;
//					}
//				}
//			} else if (stencil.id() == stencil.namespace() + "ProcessInterface") {
//				// 4th check: not connected node
//				if (this.numberOfSequenceFlows(incoming) + this.numberOfSequenceFlows(outgoing) == 0){
//					errorsArray.push(" - Error: The process interface '"+title+"' has no incoming and outgoing control flow.");
//					continue;
//				}
//				// 5th check: to many connections?
//				if (this.numberOfSequenceFlows(incoming) > 1) {
//					errorsArray.push(" - Error: The process interface '"+title+"' has more than one incoming control flow.");
//				}
//				if (this.numberOfSequenceFlows(outgoing) > 1) {
//					errorsArray.push(" - Error: The process interface '"+title+"' has more than one outgoing control flow.");
//				}
//			} else if (stencil.id() == stencil.namespace() + "AndConnector" ||
//						stencil.id() == stencil.namespace() + "XorConnector" ||
//						stencil.id() == stencil.namespace() + "OrConnector") {
//							
//				// 4th check: not connected node
//				if (this.numberOfSequenceFlows(incoming) + this.numberOfSequenceFlows(outgoing) == 0){
//					errorsArray.push(" - Error: There is a "+stencilName+" which has no incoming and outgoing control flow.");
//					continue;
//				}
//				// 8th check: no input or output?
//				if (this.numberOfSequenceFlows(incoming) == 0) {
//					errorsArray.push(" - Error: There is a "+stencilName+" without incoming control flow.");
//					continue;
//				}
//				if (this.numberOfSequenceFlows(outgoing) == 0) {
//					errorsArray.push(" - Error: There is a "+stencilName+" without outgoing control flow.");
//					continue;
//				}
//				// 9th check - split or join?
//				if (this.numberOfSequenceFlows(incoming) == 1 && this.numberOfSequenceFlows(outgoing) == 1){
//					warningsArray.push(" - Warning: There is a "+stencilName+" which is neither a split or a join.");
//				}
//				if (this.numberOfSequenceFlows(incoming) > 1 && this.numberOfSequenceFlows(outgoing) > 1){
//					errorsArray.push(" - Error: There is a "+stencilName+" which is both - a split and a join.");
//					continue;
//				}
//				// 5th check: to many connections?
//				if (this.numberOfSequenceFlows(incoming) > 2) {
//					warningsArray.push(" - Warning: There is a "+stencilName+" with more than two incoming control flows.");
//				}
//				if (this.numberOfSequenceFlows(outgoing) > 2) {
//					warningsArray.push(" - Warning: There is a "+stencilName+" with more than two outgoing control flows.");
//				}
//				// 10th check - event before or/xor split?	
//				if (this.numberOfSequenceFlows(outgoing) > 1 && (stencil.id() == stencil.namespace() + "XorConnector" ||
//																stencil.id() == stencil.namespace() + "OrConnector") ){
//					var nextArray = this.getNextEventsOrFunctionsShapes(outgoing);
//
//					for (var x = 0; x < nextArray.size(); x++){
//						var next = nextArray[x];
//						if (next.getStencil().id() == stencil.namespace() + "Function"){
//							errorsArray.push(" - Error: There is a "+stencilName+" split node which is followed by a function");
//							break;
//						}
//					}							
//				}		
//			}
//		}
//		
//		for (var i = 0; i < edges.size(); i++) {
//			
//			var edge = edges[i];
//			var stencil = edge.getStencil();
//			var incoming = edge.getIncomingShapes();
//			var outgoing = edge.getOutgoingShapes();
//			var stencilName = stencil.title();
//			var title = edge.properties["oryx-title"];
//			
//			// 6th check: not connected edges
//			if (incoming.length + outgoing.length == 0){
//				errorsArray.push(" - Error: There is a "+stencilName+" edge without incoming and outgoing connections.");
//				continue;
//			}
//			if (incoming.length == 0){
//				errorsArray.push(" - Error: There is a "+stencilName+" edge without incoming connection.");
//				continue;
//			}
//			if (outgoing.length == 0){
//				errorsArray.push(" - Error: There is a "+stencilName+" edge without outgoing connections.");
//				continue;
//			}
//		}
//		
//		
//		// evaluation of 1st and 2nd checks:
//		if (numberOfStartEvents > 1){
//			errorsArray.push(" - Error: There are several start events. Should be one.");
//		}
//		if (numberOfEndNodes > 1){
//			errorsArray.push(" - Error: There are several end events. Should be one.");
//		}
//		
//		/*
//		 * Return
//		 */
//		
//		return [ errorsArray, warningsArray ];
//	},
//	
//	/**
//	 * returns the number of sequence flows within the edges array
//	 * 
//	 * @param {Array} edges
//	 */
//	numberOfSequenceFlows: function(edges){
//		var count = 0;
//		for (var i = 0; i < edges.size(); i++ ){
//			var edge = edges[i];
//			var stencil = edge.getStencil();
//			if (stencil.id() == stencil.namespace() + "ControlFlow"){
//				count += 1;
//			}
//		}
//		return count;
//	},
//	
//	/**
//	 * returns the next event's or function's shapes
//	 *
//	 * @param {Array} edges
//	 */
//	getNextEventsOrFunctionsShapes: function(edges){
//		newEdges = [];
//		result = []
//		for (var i = 0; i < edges.size(); i++) {
//			var edge
//			newEdges.push(edges[i]);
//		}
//		return this.getNextEventsOrFunctionsShapes_(newEdges, 0, result);
//	},
//
//	/**
//	 * intern - getNextEventsOrFunctionsShapes_
//	 *
//	 * @param {Object} edges
//	 * @param {Object} count
//	 * @param {Object} result
//	 */
//	getNextEventsOrFunctionsShapes_: function(edges, count, result){
//		var newCount = edges.size();
//		for (var i = count; i < edges.size(); i++){
//			var edge = edges[i];
//			var outgoing = edge.getOutgoingShapes();
//			for (var j = 0; j < outgoing.size(); j++){
//				var node = outgoing[j];
//				var stencil = node.getStencil();
//				if (stencil.id() == stencil.namespace() + "Function" ||
//						stencil.id() == stencil.namespace() + "Event"  ){
//					result.push(node);
//				} else {
//					var nextOutgoing = node.getOutgoingShapes();
//					for (var x = 0; x < nextOutgoing.size(); x++){
//						var nextEdge = nextOutgoing[x];
//						var nextStencil = nextEdge.getStencil();
//						if (nextStencil.id() == stencil.namespace() + "ControlFlow"){
//							if (edges.indexOf(nextEdge) == -1){
//								edges.push(nextEdge);
//							}
//						}
//					}
//				}
//			}
//		}
//		if (newCount < edges.size()){
//			return this.getNextEventsOrFunctionsShapes_(edges, count, result);
//		} else {
//			return result;
//		}
//	},

	/**
	 * 
	 * 
	 * 		Export - Functions:
	 * 
	 * 
	 */	
	
	
	/**
	 * Transforms the given string via xslt.
	 * 
	 * @param {String} string
	 * @param {String} xsltPath
	 * @param {Boolean} getDOM
	 */
	transformString: function(string_, xsltPath, getDOM){
		var parser = new DOMParser();
		var parsedDOM = parser.parseFromString(string_,"text/xml");	
		
		return this.transformDOM(parsedDOM, xsltPath, getDOM);
	},
	
	/**
	 * Transforms the given dom via xslt.
	 * 
	 * @param {Object} domContent
	 * @param {String} xsltPath
	 * @param {Boolean} getDOM
	 */
	transformDOM: function(domContent, xsltPath, getDOM){	
		if (domContent == null) {
			return new String("Parse Error: \nThe given dom content is null.");
		}
		var result;
		var resultString;
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		
		xsltProcessor.importStylesheet(xslRef);
		try {
			result = xsltProcessor.transformToDocument(domContent);
		} catch (error){
			return new String("Parse Error: "+error.name + "\n" + error.message);
		}
		if (getDOM){
			return result;
		}
		resultString = (new XMLSerializer()).serializeToString(result);
		return resultString;
	},
	
	/**
	 * Saves the current diagram . Currently a workaround
	 */
	save : function(){
		
		var resource = location.search.split("resource=");	
		resource = resource[1].split("&")[0];
		var serializedDOM = DataManager.__persistDOM(this.facade);
		   new Ajax.Request(location.href, 
		 	{
				method:'POST',
				asynchronous: false, 
				parameters:	{ 	resource: resource, 
							 	data: serializedDOM 
							}
			});			
	},
	
		
	/**
	 * 
	 * 
	 * 		UI - Functions:
	 * 
	 * 
	 */	

	
	/**
	 * Opens a message dialog with the given title that shows
	 * the content. The dialog just shows a message and has a 
	 * "OK" button to be closed.
	 * 
	 * @param {String} title   The title of the dialog
	 * @param {String} content The content to be shown in the dialog
	 */
	openMessageDialog: function(title, content) {
		this.openMultiLineMessageDialog(title, new Array(content), 120, 400);
	},

	/**
	 * Opens a upload dialog.
	 * 
	 */
	openUploadDialog: function(){
		dialog = new Ext.BasicDialog("result-dialog", { 
			autoCreate: true, 
			title: 'Upload File', 
			height: 180, 
			width: 300, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			proxyDrag: true,
			resizable:false
		});
		dialog.addKeyListener(27, dialog.hide, dialog);
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.body.createChild({
            	tag:"div",
            	html:'<span class="ext-mb-text" style="font-family: Verdana; font-size: 9pt;" >Select an EPML (.epml) or AML (.xml) file and import it.<br /><br /></span>'
        	});
        var resource = location.search.split("resource=");
		resource = resource[1].split("&")[0];
		dialog.body.createChild({
            	tag:"div",
            	html:'<form action="./epc-upload?resource='+resource+'" enctype="multipart/form-data" method="post"><input type="file" name="uploadfile" /><br /><br /><input type="submit" value="Import EPC" />     <input type="button" onclick="dialog.hide()" value="Cancel" /></form>'
        	});
		dialog.show(this.el, "tl-bl?");
	},



	/**
	 *  
	 *  
	 *  The following functions are originally from the bpel4chor-Plugin
	 *    written by Kerstin Pfitzner.
	 *  There are some minor changes.
	 *  
	 *
	 *  
	 *  
	 *  
	 */






	/**
	 * Opens a message dialog with the given title that shows
	 * the content. The dialog just shows a message and has a 
	 * "OK" button to be closed.
	 * 
	 * @param {String} title   The title of the dialog
	 * @param {Array} content The content to be shown in the dialog
	 * @param {int} height_
	 * @param {int} width_
	 */
	openMultiLineMessageDialog: function(title, content, height_, width_) {
		dialog = new Ext.BasicDialog("message-dialog", { 
			autoCreate: true, 
			title: title, 
			modal:true,
			height: height_,
			width: width_,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			resizable:true,
			proxyDrag: true,
			autoScroll:true,
			buttonAlign:"center"
		});
		dialog.addKeyListener(27, dialog.hide, dialog);
		dialog.addButton('OK', dialog.hide, dialog);
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		for (var i = 0; i < content.length; i++) {
			dialog.body.createChild({
            	tag:"div",
            	html:'<span class="ext-mb-text" style="font-family: Verdana; font-size: 9pt;" >' + content[i] + '</span>'
        	});
		}
		dialog.show(this.el, "tl-bl?");
	},
	
	/**
	 * Opens a dialog that presents the results of a transformation.
	 * The dialog shows a list containing the resulting XML files.
	 * Each file can be shown in a new window or downloaded.
     *
	 * @param {Object} data The data to be shown in the dialgo
	 */
	openResultDialog: function(data) {
		dialog = new Ext.BasicDialog("result-dialog", { 
			autoCreate: true, 
			title: 'Transformation Results', 
			height: 250, 
			width: 297, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			proxyDrag: true,
			resizable:false
		});
		dialog.addKeyListener(27, dialog.hide, dialog);			
		
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

		var gridNode =	dialog.body.createChild({tag:'div', id:'grid'});
		dialog.body.setStyle("background-color", "#FFFFFF");
				
		grid = new Ext.grid.EditorGrid('grid', {
			ds:ds,
	        cm: cm,
	        selModel: new Ext.grid.RowSelectionModel({ 	singleSelect:true }),
			autoWidth: true	
	    });
		grid.render();
		grid.getSelectionModel().selectFirstRow();
		
		var gridHead = grid.getView().getHeaderPanel(true);
	    var toolbar = new Ext.Toolbar(gridHead);
		toolbar.add({
			icon: 'images/view.png', // icons can also be specified inline
	        cls: 'x-btn-icon',
    	    tooltip: 'Show the result file',
			handler: function() {
				var ds = grid.getDataSource();
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
    	    tooltip: 'Download the result file',
			handler: function() {
				var ds = grid.getDataSource();
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
    	    tooltip: 'Download all result files',
			handler: function() {
				var ds = grid.getDataSource();				
				this.openDownloadWindow(ds.getRange(0, ds.getCount()), true);
			}.bind(this)
		});
		
		dialog.on('hide', function(){
			dialog.destroy(true);
			grid.destroy(true);
			delete dialog;
			delete grid;
		});
		dialog.show(this.el, "tl-bl?");
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
	 * Returns the file name to the given result-entry.
	 * 
	 * @param {String} entry.
	 */
	getFileName: function(entry) {
		var l = entry.length;
		if (l > 5){
			if (entry.substr(l-5, 5) == "(AML)"){
				return entry.substr(0, l-6);
			}
		}
		return entry
	},
	
	/**
	 * Opens a download window for downloading the given content.
	 * 
	 * Creates a submit form to communicate the contents to the 
	 * download.php file.
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
					var file = this.getFileName(content[i].get("file"));
					submitForm.appendChild( this.createHiddenElement("download_" + i, content[i].get("result")));
					submitForm.appendChild( this.createHiddenElement("file_" + i, file));
				}
			} else {
				var file = this.getFileName(content.get("file"));
				submitForm.appendChild( this.createHiddenElement("download", content.get("result")));
				submitForm.appendChild( this.createHiddenElement("file", file));
			}
			
			submitForm.method = "POST";
			win.document.write("</body></html>");
			win.document.close();
			submitForm.action= "./download";
			submitForm.submit();
		}		
	}
	
});

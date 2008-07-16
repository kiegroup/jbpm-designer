/**
 * Copyright (c) 2008
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Bruno Colaço, Zhen Peng
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

ORYX.Plugins.BpelGenerator = Clazz.extend({

	facade: undefined,
	linksWriten: undefined,

	construct: function(facade) {
		this.facade = facade;

        this.facade.offer({
			'name':"ImportBPEL",
			'functionality': this.openUploadDialog.bind(this),
			'group': "BPEL",
			'icon': ORYX.PATH + "images/folder_page_white.png",
			'description': "Import a BPEL file",
			'index': 1,
			'minShape': 0,
			'maxShape': 0}); 
	

	    this.facade.offer({
			'name':"ExportBPEL",
			'functionality': this.download.bind(this),
			'group': "BPEL",
			'icon': ORYX.PATH + "images/BPEL.png",
			'description': "Export BPEL to XML file",
			'index': 2,
			'minShape': 0,
			'maxShape': 0});
			

	},
	
	
    /**
	 * Opens a upload dialog.
	 * 
	 */
	openUploadDialog: function(){
		
		var form = new Ext.form.FormPanel({
			frame : true,
			defaultType : 'textfield',
		 	waitMsgTarget : true,
		  	labelAlign : 'left',
		  	buttonAlign: 'right',
		  	fileUpload : true,
		  	enctype : 'multipart/form-data',
		  	items : [
		  	{
		    	fieldLabel : 'File',
		    	inputType : 'file'
		  	}]
		});

		var submit =form.addButton({
			text:"Submit",
			handler: function()
			{
				form.form.submit({
		      		url: ORYX.PATH + '/bpelimporter',
		      		waitMsg: "Importing...",
		      		success: function(f,a){
						dialog.hide();
/*
						Ext.MessageBox.show({
           					title: 'Success',
          	 				msg: a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
           					buttons: Ext.MessageBox.OK,
           					icon: Ext.MessageBox.ERROR
       					});
*/
						//var erdf = a.response.responseText;
						//var parsedErdf = parser.parseFromString('<?xml version="1.0" encoding="utf-8"?><html>'+erdf+'</html>',"text/xml");	
						//alert(erdf);
		      		},
					failure: function(f,a){
						dialog.hide();
/*
						Ext.MessageBox.show({
           					title: 'Error',
          	 				msg: a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
           					buttons: Ext.MessageBox.OK,
           					icon: Ext.MessageBox.ERROR
       					});
*/
		      		}
		  		});
		  	}
		})


		var dialog = new Ext.Window({ 
			autoCreate: true, 
			title: 'Upload File', 
			height: 130, 
			width: 400, 
			modal:true,
			collapsible:false,
			fixedcenter: true, 
			shadow:true, 
			proxyDrag: true,
			resizable:false,
			items: [new Ext.form.Label({text: "Select a file and upload it.", style: 'font-size:12px;'}),form]
		});
		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		dialog.show();
	},
	
	

//	save:function(){ 
	   // Ext.MessageBox.show({
       // title:'Save Changes?',
       // msg: 'Your are closing a tab that has unsaved changes. Would you like to save your changes?',
       // buttons: Ext.MessageBox.YESNOCANCEL,
       // fn: function(){alert("xpro");},
       // animEl: 'mb4'
       // });
    //   this.doit();
	//},
	
	download: function() {	

			this.facade.raiseEvent({type:'loading.enable'});
		
			this.linksWriten= new Array();
			var BpelCount=0;
			var filename= "default";
			var avoidServer = "true";
			
			//sets the name of the zip file, in filename, with the Project Name
			
			var CanvasStencilProper = this.facade.getCanvas().getStencil().properties();
			var canvasShapeProper= this.facade.getCanvas().properties;
			CanvasStencilProper.each((function(propertie){
				propertieName = propertie.prefix()+"-"+propertie.id();
				if(propertieName==="oryx-title" && canvasShapeProper[propertieName]!=="")
					filename=canvasShapeProper[propertieName];
				if(propertieName==="oryx-avoidServer" && canvasShapeProper[propertieName]!=="")
					avoidServer=canvasShapeProper[propertieName];
			}).bind(this));
			
			//holds the name of the output files
			var outputFiles = [];
			//holds the name of the output data (XML for each BPEL)
			var outputData = [];
			
			
			
			//do the XML generator for each BPEL 
			//get the Name of the current Bpel shape .........
			this.facade.getCanvas().getChildShapes().each((function(children){
				var stencilName = children.getStencil().id();
				var stencilProper = children.getStencil().properties();
				var shapeProper= children.properties;
				var namespace = children.getStencil().namespace();
				//if the stencil name is BPEL, in this case is necessary to add the name of the stencil in the outputfiles and generate the##################
				if( (namespace+ "BPEL") ==stencilName){

				
					var stencilProper = children.getStencil().properties();
					var shapeProper= children.properties;
					stencilProper.each((function(propertie){
						propertieName = propertie.prefix()+"-"+propertie.id();
						if(propertieName ==="oryx-name"){
							if(shapeProper[propertieName]!="")
								outputFiles.push(shapeProper[propertieName]);
							else
								outputFiles.push("default");
						}
					}).bind(this));
					
					var myBpel= document.createElementNS("","process");
					var xmlobj = this.ParsingElement(myBpel,children);	
			
					// var _xml =new XMLWriter();
					// _xml.BeginNode("Process");
					// this.toXML(_xml,xmlobj);
					// _xml.Close();
					 BpelCount++;
					// var tmpXml=_xml.ToString();
					var XMLserial = new XMLSerializer();
					var tmpXml= XMLserial.serializeToString(myBpel);
					//use document.evaluate to the XPATH.... new XMLSerializer().serializeToStream(d)   d.appendChild(document.createElement("Variables"))
					// view rules of XPATH and <process xmlns="http://www.w3.org/1999/xhtml" FirePHP-Window-6="ss" name="fdfss"/>
					//do the restrictions of the json is necessary to change some characters to known available strings ( to be changed in the PHP file)
					
					outputData.push(tmpXml);
				}
			}).bind(this));
			
			//starts download process
			if(avoidServer && outputData.length === 1){
                var win = window.open('data:application/xhtml+xml,' + outputData[0], 'Bpel file');	
			} else {			
				if (outputData.length == 1){
					this.openDownloadWindow(outputFiles[0], outputData[0], false);
				} else {
					this.openDownloadWindow(outputFiles, outputData, true);
				}		
			}	
			
			this.facade.raiseEvent({type:'loading.disable'});
	},
	

	
	/**
	 * Adds a file extension to the given file name. If the file
	 * has the name "topology" or "XPDL4Chor" an .xml extension will
	 * be added. Otherwise a .bpel extension will be added
	 * 
	 * @param {Object} file The file name to add the extension to.
	 */
	addFileExtension: function(file) {
		return file + ".bpel";
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
	 * Opens a download window for downloading the given content.
	 * 
	 * Creates a submit form to communicate the contents to the 
	 * download.php file.
	 * 
	 * @param {Object} outputFile  The name of the file to be downloaded. If it is 
	 *                             a zip file, then this should be an array.
	 *                             
	 * @param {Object} outputData  data of the file to be downloaded. If it is a
	 *                             zip file, then this should be an array.
	 *                                                     
	 * @param {Object} zip         True, if it is a zip file, false otherwise
	 */
	openDownloadWindow: function(outputFile, outputData, zip) {	
    	var win = window.open("");

		if (win != null) {
			win.document.open();
			win.document.write("<html><body>");
			var submitForm = win.document.createElement("form");
			win.document.body.appendChild(submitForm);
			

			if (zip) {
				for (var i = 0; i < outputFile.length; i++) {
				    var file = this.addFileExtension(outputFile[i]);
					submitForm.appendChild( this.createHiddenElement("download_" + i, outputData[i]));
					submitForm.appendChild( this.createHiddenElement("file_" + i, file));
				}
			} else {
				var file = this.addFileExtension(outputFile);
				submitForm.appendChild( this.createHiddenElement("download", outputData));
				submitForm.appendChild( this.createHiddenElement("file", file));
			}

			submitForm.method = "POST";
			win.document.write("</body></html>");
			win.document.close();
			submitForm.action= ORYX.PATH + "/download";
			submitForm.submit();
		}		
	},
	

	
	ParsingProperties: function(myXmlElem,element){
	    var stencilProper = element.getStencil().properties();
		var shapeProper= element.properties;
		stencilProper._each((function(propertie){
			propertieName = propertie.prefix()+"-"+propertie.id();
			var value = shapeProper[propertieName];
			//REMEMBER THE OTHER ELEMENTS TO BE BUILD WITH THE ANALISE OF THE GRAF, EXAMPLE ( FLOW LINKS, activities targets/sources, 
			if((value !="" || (value =="" && !propertie.optional()) ) && value !="fromParent"){
				switch(propertie.id()){
					
					case "If_opaque":	//related with the bool_expression of the if element
						if(value==false){
							var booleanExp =this.getPropertie(element, "bool_expression");
							if(booleanExp)
								myXmlElem.appendChild(document.createTextNode(booleanExp));
						}else
							myXmlElem.setAttribute("opaque","yes");
						
						break;
					

					case "documentation":
						var xtraElement = document.createElementNS("",propertie.id());
						xtraElement.appendChild(document.createTextNode(value))
						myXmlElem.appendChild(xtraElement);
						break;
				
				
				//complex fields objects, is necessary to create another element
					case "import": //import is more "basic" only need to create a defined amount of element ( import)(not necessary a parent container)
						var JSONValue = shapeProper[propertieName].evalJSON();
						if(JSONValue.totalCount!==0){
						
							JSONValue.items.each((function(JsonItem){
								var subElem = document.createElementNS("","import");
								for( propertie in JsonItem){
									if(JsonItem[propertie]!=="")
										subElem.setAttribute(propertie,JsonItem[propertie]);
								}
								myXmlElem.appendChild(subElem);
							}).bind(this));
						}
						break;
					
					//USES fromspec/tospec
					case  "copy":
						if(value && value !=""){
							var JSONobj =eval("("+value+")");
								if (JSONobj.totalCount>0){

							//this definition should be on the JSON file 
								var fromFields = new Array("fromspecvariablename","fromspecpart","fromspecproperty","fromspecquery","fromspecquerylanguage","fromspecexpressionlanguage","fromspecexpression","fromspecpartnerLink","fromspecendpointReference","fromspecliteral", "fromspectype");
								var toFields = new Array("tospecvariablename","tospecpart","tospecproperty","tospecquerylanguage","tospecquery","tospecexpressionlanguage","tospecexpression","tospecpartnerLink", "tospectype");
								
								JSONobj.items.each((function(entrie){
								
									var fromEntrie = new Hash();
									var toEntrie = new Hash();
									fromFields.each((function(field){
										fromEntrie[field]=entrie[field];
									}).bind(this));
									
									toFields.each((function(field){
										toEntrie[field]=entrie[field];
									}).bind(this));
									
									
								
										//add the attributes
										var subElem=document.createElementNS("","copy");
										
										if(entrie["keepSrcElementName"]!="" && entrie["keepSrcElementName"]!="fromParent")
											subElem.setAttribute("keepSrcElementName",entrie["keepSrcElementName"]);
										if(entrie["ignoreMissingFromData"]!="" && entrie["ignoreMissingFromData"]!="fromParent")
											subElem.setAttribute("ignoreMissingFromData",entrie["ignoreMissingFromData"]);
										
										//create fromspec
										if (entrie["fromspectype"]!="Empty"){
											var fromElem = document.createElementNS("","from");
											this.spec(fromElem,fromEntrie,propertie,"fromspectype");
										}
											subElem.appendChild(fromElem);
											
										//createtospec
										if (entrie["tospectype"]!="Empty"){
											var toElem = document.createElementNS("","to");
											this.spec(toElem,toEntrie,propertie,"tospectype");
											subElem.appendChild(toElem);
										}
										myXmlElem.appendChild(subElem);
									}).bind(this));
									
								}
						}else
							myXmlElem.appendChild(document.createElementNS("",propertie.id()));
						
						break;	
						
					case "variables":	//the variables can be of several types ... for that we need to check the fields value and react with that... ( is not a standard one)
						var JSONobj =eval("("+value+")");
						if (JSONobj.totalCount>0){
							var mainElem =document.createElementNS("",propertie.id());
							
							//for each entrie 
							JSONobj.items.each((function(entrie){
								
								//add the attributes
								var subElem=document.createElementNS("","variable");
								subElem.setAttribute("name",entrie["name"]);
								if(entrie["messageType"]!="")
									subElem.setAttribute("messageType",entrie["messageType"]);
								if(entrie["type"]!="")
									subElem.setAttribute("type",entrie["type"]);
								if(entrie["element"]!="")
									subElem.setAttribute("element",entrie["element"]);
								
								//create fromspec
								if (entrie["fromspectype"]!="Empty"){
									var from = document.createElementNS("","from");
									this.spec(from,entrie,propertie,"fromspectype");
									subElem.appendChild(from);
								}
								mainElem.appendChild(subElem);
							}).bind(this));
							myXmlElem.appendChild(mainElem);
						}
						break;
						
					
					case "extensions":	
					case "PartnerLinks":
					case "CorrelationSets":
					case "messageExchanges":
					case "Correlations":
					case "toParts":
					case "fromParts":
						var JSONValue = shapeProper[propertieName].evalJSON();
							if(JSONValue.totalCount!==0){
								//for all of this elements there isa a parent element ( container) that  the only diference for the inner elements is a "s", the last character of the string
								//so we can make it generic if we take only the last character
								var innerElement =(propertie.id().substring(0,propertie.id().length-1)); 
								var elementContainer =this.createInnerElements(JSONValue.items, innerElement, propertie.id());
								myXmlElem.appendChild(elementContainer);
							}
						break;
						
					
					//some elements that uses other attrubutes
					case "ForUntil_opaque"://uses the current attribute value to see if is opaque ,comum_expressionLanguage and expressionForOrUntil, comum_expressionLanguage (creates for or until element)
						var opaqueValue=value;
						var type =this.getPropertie(element, "ForOrUntil");
						var subElem=document.createElementNS("",type);
						if(opaqueValue==false){
							var propValueNode = this.getPropertie(element, "expressionForOrUntil");
							if(typeof propValueNode!= 'undefined')
								subElem.appendChild(document.createTextNode(propValueNode));
						}else
							subElem.setAttribute("opaque","yes");
							
						var propValue = this.getPropertie(element, "comum_expressionLanguage");
						if(typeof propValue != 'undefined')
							subElem.setAttribute("expressionLanguage",propValue);
					    	myXmlElem.appendChild(subElem);
						break;
						
						
					case "condition_opaque": //uses the current attribute value to see if is opaque, the condition_booleanExpression and condition_expressionLanguage ( creates condition element)
						var opaqueValue=value;
						
						var propValueLang = this.getPropertie(element, "condition_expressionLanguage");
						var propValueNode = this.getPropertie(element, "condition_booleanExpression");
						var subElem=document.createElementNS("","condition");
						
						if(typeof propValueLang != 'undefined')
								subElem.setAttribute("expressionLanguage",propValueLang);

						if(opaqueValue==false && typeof propValueNode  != 'undefined'){		
							subElem.appendChild(document.createTextNode(propValueNode));
						}
						
						if(opaqueValue==true){
							subElem.setAttribute("opaque","yes");							
						}	
						
						myXmlElem.appendChild(subElem);
						break;
					
					
					case "condition_booleanExpression": 
						var subElem=document.createElementNS("","condition");
						subElem.appendChild(document.createTextNode(value));
						var propValue = this.getPropertie(element, "condition_expressionLanguage");
						if(typeof propValue != 'undefined')
							subElem.setAttribute("expressionLanguage",propValue);
						myXmlElem.appendChild(subElem);
						break;
						
					case "repeat_opaque": //uses the current attribute value and  repeatExpressionLanguage( creates element repeatEvery)
						
						var opaqueValue=value;
						
						var propValueLang = this.getPropertie(element, "repeatExpressionLanguage");
						var propValueNode = this.getPropertie(element, "repeatTimeExpression");
						if(opaqueValue==false && typeof propValueNode  != 'undefined'){
								var subElem=document.createElementNS("","repeatEvery");
								
								if(typeof propValueLang != 'undefined')
									subElem.setAttribute("expressionLanguage",propValueLang);
								
								subElem.appendChild(document.createTextNode(propValueNode));
								myXmlElem.appendChild(subElem);
							
						}
						
						if(opaqueValue==true){
							var subElem=document.createElementNS("","repeatEvery");
							if(typeof propValueLang != 'undefined')
								subElem.setAttribute("expressionLanguage",propValueLang);
							subElem.setAttribute("opaque","yes");
							myXmlElem.appendChild(subElem);	
						}	
						break;
					
					//custom attributes ( need to change something) is not standard
					case "otherxmlns": //uses complex 
						var JSONValue = shapeProper[propertieName].evalJSON();
						if(JSONValue.totalCount!==0)
						JSONValue.items.each((function(JsonElement){
							myXmlElem.setAttribute("xmlns:"+JsonElement.prefix, JsonElement.namespace);
						}).bind(this));
						break;
					
					case "choiceType": //uses the current attribute value and  choiceValue creates ONLY ONE attibute (messageType or Element) with the name specified in choiceType, with the value present on choiceValue
					var propValue = this.getPropertie(element, "choiceValue");
						if(typeof propValue != 'undefined'&& propValue != "Empty" )
							subElem.setAttribute(value, propValue);
						break;
					
					case "TC_opaque": // only for EDGES, creates transition condition if exists, and gets the value of the properitie TC_expressionLanguage as attributes
						
						
						
						var opaqueValue=value;
						var expValue= this.getPropertie(element, "transition_expression")
						var propValue = this.getPropertie(element, "TC_expressionLanguage");
						
						
						
						if (expValue!="" && opaqueValue===false ){
							var subElem = document.createElementNS("", "transitionCondition");
							if(typeof propValue != 'undefined')
								subElem.setAttribute("expressionLanguage",propValue);
							subElem.appendChild(document.createTextNode(expValue));
							myXmlElem.appendChild(subElem);
						}
						
						if(opaqueValue===true){
							var subElem = document.createElementNS("", "transitionCondition");
							subElem.setAttribute("opaque","yes");
							if(typeof propValue != 'undefined')
								subElem.setAttribute("expressionLanguage",propValue);
							myXmlElem.appendChild(subElem);
							
						}
						
						
						break;
					
					//attribute variables  in the validate Element, the several values are contained in a JSON complex, and will be disposed in a string separeted with space
					case "checkVariables":
						var stringVariables=""; 
						if(value!=""){
							var JSONobj =eval("("+value+")");
								JSONobj.items.each((function(entrie){
									stringVariables+=" "+entrie["variables"];
								}).bind(this));
								
								//stringVariables.substring has to remove the first space
						}
						myXmlElem.setAttribute("variables",stringVariables);
						break;
					//standard attributes in this case the restrictions are garanteed by the stencil set roles, so is only necessary to create a attribute to the present element with the defined value
					case "linkName":
					case "messageExchange":
					case "faultVariable":
					case "partnerLink":
					case "validate":
					case "inputVariable":
					case "outputVariable":
					case "variable":
					case "faultName":
					case "name":
					case "portType":
					case "operation":
					case "xmlns":
					case "name":
					case "targetNamespace":
					case "abstractProcessProfile":
					case "queryLanguage":
					case "expressionLanguage":
					case "exitOnStandardFault":
					case "target":
					case "faultMessageType":
					case "faultElement":
						myXmlElem.setAttribute(propertie.id(),value);
					
					default :
					//if we want we can create a custom namespace of the ORYX elements
				}
			}
			
		}).bind(this));
	
	
	},
	
	ParsingElement: function(myXmlElem, element){
		var stencilProper = element.getStencil().properties();
		var shapeProper= element.properties;
		
		this.ParsingProperties(myXmlElem, element);
		
		
		//set the elements targets ( and their subelements)/incoming
		if(this.getEdgeNumberWithRole(element.incoming, "sequenceFlow")>0){
			mainSubElement = document.createElementNS("","targets");
			myXmlElem.appendChild(mainSubElement);
			//checks if there is Join condition boolean expression is not empty
			var valueJC = this.getPropertie(element,"JoinCond_boolExp");
			var JCOpaque = this.getPropertie(element,"JoinCond_opaque");
			if(typeof valueJC != 'undefined' && !JCOpaque){
				var joinElem = document.createElementNS("","joinCondition");
				var expLang = this.getPropertie(element,"JC_expLang");
				if(typeof expLang != 'undefined')
					joinElem.setAttribute("expressionLanguage", expLang);
				joinElem.appendChild(document.createTextNode(valueJC));
				mainSubElement.appendChild(joinElem);
			}
			if(JCOpaque){
				var joinElem = document.createElementNS("","joinCondition");
				var expLang = this.getPropertie(element,"JC_expLang");
				if(typeof expLang != 'undefined')
					joinElem.setAttribute("expressionLanguage", expLang);
				joinElem.setAttribute("opaque", "yes");
				mainSubElement.appendChild(joinElem);
			}
		
			element.incoming.each((function(edge){
				var stencil =edge.getStencil();
				if((stencil.namespace()+"sequenceFlow")===stencil.id()){
					var subElem = document.createElementNS("","target");
					subElem.setAttribute("linkName",this.getPropertie(edge,"linkName"));
					mainSubElement.appendChild(subElem);
				}

			}).bind(this));
		}	
		
		
		//set the elements sources ( and their subelements) /outgoing
		if(this.getEdgeNumberWithRole(element.outgoing, "sequenceFlow")>0){
			mainSubElement = document.createElementNS("","sources");
			myXmlElem.appendChild(mainSubElement);
		
			element.outgoing.each((function(edge){
				var stencil =edge.getStencil();
				if((stencil.namespace()+"sequenceFlow")===stencil.id()){
					var subElem = document.createElementNS("","source");
					this.ParsingElement(subElem, edge);
					mainSubElement.appendChild(subElem);
				}

			}).bind(this));
		}
		
		
		//HERE is going to be called the childs
		//to get the name of the element without the namspace
		var currentstencilName = element.getStencil().id().split("#")[1];
			//since there are diferent kind of childs, we can group it by the ones that matters the sequence ( sequence, ifelse?!) and the others 
			switch(currentstencilName ){
				//in this group of elements the order is not necessary, so we can call each inner element independent of the order
				case "BPEL":
				case "invoke":
				case "receive":
				case "reply":
				case "assign":
				case "empty":
				case "opaqueActivity":
				case "validate":
				case "throw":
				case "exit":
				case "rethrow":
				case "wait":
				case "scope":
				case "while":	
				case "repeatUntil":
				case "forEach":
			    case "completionCondition":
				case "pick":
				case "onMessage":
				case "onAlarm":
				case "eventHandlers":
				case "onEvent":
				case "compensante":
				case "compensanteScope":
				case "compensationHandler":
				case "terminationHandler":
				case "faultHandlers":
				case "catch":
				case "catchAll":
				case "flow":
					element.getChildShapes().each((function(children){
						this.createChild(children, myXmlElem);
					}).bind(this));
					break;
					
				case "sequence":
					var currentChild;
					//gets the child that has one outgoing sequenceOrder and no incoming sequenceOrder  ( is the first one)
					element.getChildShapes().any((function(child){
						if(this.getEdgeNumberWithRole(child.outgoing, "sequenceOrder")==1 && 
							this.getEdgeNumberWithRole(child.incoming, "sequenceOrder")==0){
							currentChild= child ;
							return 	true
						}
						else
							return false;
						
					}).bind(this));
					//checks if there any more child to be created, if yes create a new inner Element and gets the nextChild
					//stops when there is no more next child ( the sequence is over)
					while(currentChild!=undefined){
						this.createChild(currentChild, myXmlElem);
						currentChild=this.getNextOrderNode(currentChild);
						}
					break;
					
				case "if":
					var condChild;
					var activityChild;
						
						//gets the first element of the graf... in this case is the first condition without any sequenceif edges
						element.getChildShapes().any((function(child){
							if(this.getEdgeNumberWithRole(child.outgoing, "sequenceIfTrue")==1&&
							this.getEdgeNumberWithRole(child.incoming, "sequenceIf")==0){
								condChild= child ;
								return 	true
							}
							else
								return false;
						}).bind(this));

						
						//the first element is a special case, since there is no inner xml element, the condition and the condition lays on the main element(if), this one has also some special properties
						var ifElem = myXmlElem;
						this.ParsingProperties(myXmlElem, element);// since the element if has properties is necessary to make the parsing of his attributes
						if(!condChild) break;
						//creates the condition Element
						var condElem = document.createElementNS("","condition");
						this.ParsingElement(condElem, condChild);
						ifElem.appendChild(condElem);
						
						//create the Activity Element
						//this.createChild(activityChild, ifElem);
						var ifTrue,  ifFalse, trueActivity, false_cond ;
						ifTrue = this.getFirstEdgeWithRole(condChild.outgoing,"sequenceIfTrue");
						ifFalse = this.getFirstEdgeWithRole(condChild.outgoing,"sequenceIfFalse");
						
						//needs to have a sequenceIfTrue Edge
						if (!ifTrue)
							break;
						
						//if there is no Activity fior the ifTrue
						trueActivity = this.getFirstEdgeWithRole(ifTrue.outgoing,"Activity");
						if (!trueActivity)
							break;
						if(ifFalse) 
							false_cond = this.getFirstEdgeWithRole(ifFalse.outgoing,"if_condition");
						else
							ifFalse=undefined;
						
						//since the first element is a if it would be treated like a "special case"
						
						this.createChild(trueActivity, ifElem);
						
						
						
						
						while(false_cond){
							if(!trueActivity)
								break;
							
															
							
							var subElem= document.createElementNS("","elseif");
							ifElem.appendChild(subElem);
							
							var condElem = document.createElementNS("","condition");
							this.ParsingElement(condElem, false_cond);
							subElem.appendChild(condElem);	
							
							      
							//setting the new condition shape
							ifTrue = this.getFirstEdgeWithRole(false_cond.outgoing,"sequenceIfTrue");
							ifFalse = this.getFirstEdgeWithRole(false_cond.outgoing,"sequenceIfFalse");
							
							//needs to have a sequenceIfTrue Edge
							if (!ifTrue) break;
							trueActivity = this.getFirstEdgeWithRole(ifTrue.outgoing,"Activity");
							
							//if there is no Activity for the ifTrue
							if (!trueActivity) break;
							
							if(ifFalse)
								false_cond = this.getFirstEdgeWithRole(ifFalse.outgoing,"if_condition");
							else
								false_cond=undefined; //there is no more conditionelements
							
							this.createChild(trueActivity, subElem);
						}
						
						
						//for the else activity since 
						if(ifFalse){
							false_cond = this.getFirstEdgeWithRole(ifFalse.outgoing,"Activity");
							if(false_cond){
								var subElem= document.createElementNS("","else");
								ifElem.appendChild(subElem);
								
								this.createChild(false_cond, subElem);
							}
						}
						
						break;
						
				default:
					break;
					
			}
	},
	
	//pass the cmplex stencil in parameters to validade and see the definitions ( optional?!)
	createInnerElements: function(JsonOBJ, elementName,mainElement){
		var MainElement = document.createElementNS("", mainElement);
		JsonOBJ.each((function(JsonItem){
			var subElem = document.createElementNS("",elementName);
			for( propertie in JsonItem){
				if(JsonItem[propertie]!=="")
					subElem.setAttribute(propertie,JsonItem[propertie]);
			}
			MainElement.appendChild(subElem);
		}).bind(this));
		return MainElement;
	},

	searchAndAddLinks:function(element,DOMElem){
		var Links=new Array();
		this.searchInShape(element, Links);
		var linkholder = document.createElementNS("","links");
		if(Links.length >0){
			Links.each((function(link){
				this.linksWriten.push(link);
				var subElem = document.createElementNS("","link");
				var properValue = this.getPropertie(link,"linkName");
				if(typeof properValue != 'undefined')
					subElem.setAttribute("linkName", properValue);
				linkholder.appendChild(subElem);
			}).bind(this));
			
			DOMElem.appendChild(linkholder);
		}
	
	},
	
	//adds to the linkContainer the Edges valid ( that they were not yet Writen, and are SequenceFlow)
	searchInShape:function(element, linkContainer){
	
		//searches in the incoming Edges the  Edges that are SequenceFlow, not yet in the LinkContainer (, and they were not writen before ( checks the linksWriten array that contains all of the writen links)
		element.incoming.each((function(edge){
		// sees if there is the refered link in the array that contains the "already writen" links
			var stencil = edge.getStencil();
			var exists = linkContainer.member(edge);
			
			// sees if there is the refered link in the array that contains the "already writen" links
			if(!exists  && (stencil.namespace()+"sequenceFlow")===stencil.id()){
				exists = this.linksWriten.member(edge);
				if(!exists)
					linkContainer.push(edge);
			}
		}).bind(this));
		
		
		element.outgoing.each((function(edge){
		// sees if there is the refered link in the array that contains the "already writen" links
			var stencil = edge.getStencil();
			var exists = linkContainer.member(edge);
			
			// sees if there is the refered link in the array that contains the "already writen" links
			if(!exists && (stencil.namespace()+"sequenceFlow")===stencil.id()){
				exists = this.linksWriten.member(edge);
				if(!exists)
					linkContainer.push(edge);
			}
		}).bind(this));
		
		element.getChildShapes().each((function(children){
			if(children.getStencil().id() != (children.getStencil().namespace() + "flow")){
				this.searchInShape(children,linkContainer);
				}
		}).bind(this));
						
	},
	
	spec:function(xmlElem, JsonObj, propertie, type){
			JsonObj= new Hash(JsonObj);
			var valueType = JsonObj[type];
			var disabledItems;
			propertieFromSpec =propertie.complexItem(type);
			propertie.complexItems("disable").any((function(choice){
				if(choice.id()==type)
					return propertieFromSpec._jsonItem.disable.any((function(propToRemove){
						if(propToRemove.value===valueType){
							disabledItems= propToRemove.items;
							return true;
						}else
							return false;
				}).bind(this));
			}).bind(this));
			
			propertie.complexItems().each((function(propertie){
				var id = propertie.id();
				var value = JsonObj[id];
				
				if(JsonObj.keys().member(id) ){
					if(!disabledItems.member(id)){
						if(((!propertie.optional() && value=="") || value!="") &&value !="fromParent"){
							switch(id){
								case "fromspecvariablename":
								case "tospecvariablename":
									xmlElem.setAttribute("variable",value);
									break;
								case "fromspecpart":
								case "tospecpart":
									xmlElem.setAttribute("part",value);
									break;
								case "fromspecproperty":
								case "tospecproperty":
								
								//inner element, this value is analysed in the query 
								// case "fromspecquerylanguage":
								// case "tospecquerylanguage":
								case "fromspecquery":
								case "tospecquery":
									var subElem = document.createElementNS("","query");
									if (JsonObj["fromspecquerylanguage"] && JsonObj["fromspecquerylanguage"]!=""){
										subElem.setAttribute("queryLanguage",JsonObj["fromspecquerylanguage"]);
									}else
										if (JsonObj["tospecquerylanguage"] && JsonObj["tospecquerylanguage"]!=""){
											subElem.setAttribute("queryLanguage",JsonObj["tospecquerylanguage"]);
										}
									subElem.appendChild(document.createTextNode(value))
									xmlElem.appendChild(subElem);
									break;
								
								case "fromspecexpressionlanguage":
								case "tospecexpressionlanguage":
									xmlElem.setAttribute("expressionLanguage",value);
									break;
								case "fromspecexpression":
								case "tospecexpression":
									xmlElem.appendChild(document.createTextNode(value))
									break;
								
								case "fromspecpartnerLink":
								case "tospecpartnerLink":
									xmlElem.setAttribute("partnerLink",value);
									break;
								
								case "fromspecendpointReference":
									xmlElem.setAttribute("endpointReference",value);
									break;
								case "fromspecliteral":
									
									var subElem = document.createElementNS("","literal");
									
									var innerLiteral = (new DOMParser).parseFromString(value,"text/xml").documentElement;
									if(innerLiteral.nodeName=="parsererror") 
										innerLiteral = document.createTextNode(value);
									subElem.appendChild(innerLiteral)
									xmlElem.appendChild(subElem);
									break;
								default:
							
							}
						}
					}
				}
			}).bind(this));
			
			
			
	
	},
	createChild:function(children, myXmlElem){
		var targetContainer = [];
			
			var mainSubElement;
			
			
			stencilName = children.getStencil().id().split("#")[1];
			switch(stencilName){

				//standard behavior
				case "BPEL":
				case "invoke":
				case "receive":
				case "reply":
				case "assign":
				case "empty":
				case "opaqueActivity":
				case "validate":
				case "throw":
				case "exit":
				case "rethrow":
				case "wait":
				case "scope":
				case "while":
				case "repeatUntil":
				case "forEach":
				case "completionCondition":
				case "pick":
				case "onMessage":
				case "onAlarm":
				case "eventHandlers":
				case "onEvent":
				case "compensante":
				case "compensanteScope":
				case "compensationHandler":
				case "terminationHandler":
				case "faultHandlers":
				case "catch":
				case "catchAll":
				case "if":
				case "sequence":
					var newElem = document.createElementNS("",stencilName);
					myXmlElem.appendChild(newElem);
				
					this.ParsingElement(newElem, children);
					break;
				
				//special behaviors
				//is necessary to add some th
				
				case "flow":
					var flowElem = document.createElementNS("",stencilName);
					this.searchAndAddLinks(children,flowElem);
					myXmlElem.appendChild(flowElem);
					this.ParsingElement(flowElem, children);
					break;
					
				case "extensionActivity":
					var newElem = document.createElementNS("",stencilName);
					myXmlElem.appendChild(newElem);
					var propValueNode = this.getPropertie(children, "elementName");
						if(typeof propValueNode == 'undefined'|| propValueNode=="")
							propValueNode= "unnamed";
					
					var extElem = document.createElementNS("",propValueNode);
					newElem.appendChild(extElem);
					this.ParsingElement(extElem, children);
					break;
			
				default:
				break;
			}
		},
	
	
	
	getEdgeNumberWithRole:function(edgeArray, role){
		var ocurrence = 0;
		edgeArray.each((function(edge){
			var stencil =edge.getStencil();
			if(edge.getStencil().roles().member((edge.getStencil().namespace()+role)))
				ocurrence++;
		}).bind(this));
		return ocurrence;
	},
	getFirstEdgeWithRole:function(edgeArray, role){
		var returnvalue;
		edgeArray.any((function(edge){
			var stencil =edge.getStencil();
			if(edge.getStencil().roles().member((edge.getStencil().namespace()+role))){
				returnvalue=edge;
				return true;
			}else
				return false;
		}).bind(this));
		return returnvalue;
	},
	
	getNextOrderNode:function(node){
		var nextElem;
		node.outgoing.each((function(edge){
			var stencil =edge.getStencil();
			if((stencil.namespace()+"sequenceOrder")===stencil.id())
				if(edge.outgoing.length==1)
					nextElem= edge.outgoing[0];
		}).bind(this));
		return nextElem;
	
	},
	
	getPropertie:function(element,name){
		var stencilProper = element.getStencil().properties();
		var shapeProper= element.properties;
		var value ;
		stencilProper._each((function(propertie){
			if(propertie.id()==name){
				propertieName = propertie.prefix()+"-"+name;
				var tmpvalue = shapeProper[propertieName];
				if((tmpvalue !="" || (tmpvalue =="" && !propertie.optional()) ) && tmpvalue !="fromParent")
					value=tmpvalue;
					
			}
		}).bind(this));
		return value;
	
	},
	
	toXML : function(_xml,obj){
	
		if (!arguments.length) return trace("Error: toXML needs atleast one (1) argument.")

		var type;
	
		for (var p in obj){
			type = typeof obj[p];
			if (type == "function") continue;
			if (type == "object") {_xml.BeginNode(p); this.toXML(_xml,obj[p]) ; continue;}
			if (type == "String") _xml.WriteString(obj[p]);
				else _xml.Attrib(p, obj[p]);
		}
		_xml.EndNode();
	}

});

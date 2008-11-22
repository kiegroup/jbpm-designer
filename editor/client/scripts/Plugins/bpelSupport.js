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
	
ORYX.Plugins.BPELSupport = Clazz.extend({

	facade: undefined,

	/**
	 * Offers the plugin functionality:
	 * 
	 */
	construct: function(facade) {
		
		this.facade = facade;

	    this.facade.offer({
			'name':ORYX.I18N.BPELSupport.exp,
			'functionality': this.exportProcess.bind(this),
			'group': ORYX.I18N.BPELSupport.group,
			'icon': ORYX.PATH + "images/bpel_export_icon.png",
			'description': ORYX.I18N.BPELSupport.expDesc,
			'index': 0,
			'minShape': 0,
			'maxShape': 0});
			
        this.facade.offer({
			'name':ORYX.I18N.BPELSupport.imp,
			'functionality': this.importProcess.bind(this),
			'group': ORYX.I18N.BPELSupport.group,
			'icon': ORYX.PATH + "images/bpel_import_icon.png",
			'description': ORYX.I18N.BPELSupport.impDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0});

		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL, this.handleLayoutEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_VERTICAL, this.handleLayoutVerticalEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL, this.handleLayoutHorizontalEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD, this.handleSingleChildLayoutEvent.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE, this.handleAutoResizeLayoutEvent.bind(this));
	},
	
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
		var xsltPath = ORYX.PATH + "lib/extract-rdf.xsl";
		var xsltProcessor = new XSLTProcessor();
		var xslRef = document.implementation.createDocument("", "", null);
		xslRef.async = false;
		xslRef.load(xsltPath);
		xsltProcessor.importStylesheet(xslRef);
		try {
			var rdf = xsltProcessor.transformToDocument(parsedDOM);
			var serialized_rdf = (new XMLSerializer()).serializeToString(rdf);
			if (!serialized_rdf.startsWith("<?xml")) {
				serialized_rdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + serialized_rdf;
			}
			  
			// Send the request to the server.
			new Ajax.Request(ORYX.CONFIG.BPEL_EXPORT_URL, {
				method: 'POST',
				asynchronous: false,
				parameters: {
					resource: resource,
					data: serialized_rdf
				},
                onSuccess: function(request){                
                	var win = window.open('data:text/xml,' +request.responseText, '_blank', "resizable=yes,width=640,height=480,toolbar=0,scrollbars=yes");
                } 
			});
                	
			
		} catch (error){
			this.facade.raiseEvent({type:ORYX.CONFIG.EVENT_LOADING_DISABLE});
			Ext.Msg.alert("Oryx", error);
	 	}
    
	},
	
	importProcess: function(){
		this.openUploadDialog ();
	},
	
	/**
	 * Opens a upload dialog.
	 * 
	 */
	openUploadDialog: function(){
		
		var form = new Ext.form.FormPanel({
			frame : 		true,
			bodyStyle:		'padding:5px;',
			defaultType : 	'textfield',
		  	labelAlign : 	'left',
		  	buttonAlign: 	'right',
		  	fileUpload : 	true,
		  	enctype : 		'multipart/form-data',
		  	items : [
		  	{
		    	text : 		ORYX.I18N.BPELSupport.selectFile, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
				xtype : 	'label'
		  	},{
		    	fieldLabel : 	ORYX.I18N.BPELSupport.file,
		    	inputType : 	'file',
				labelStyle :	'width:50px;',
				itemCls :		'ext_specific_window_overflow'
		  	}]
		});


		var displayPanel = new Ext.form.FormPanel({
			frame : 		true,
			bodyStyle:		'padding:5px;',
			defaultType : 	'textfield',
		  	labelAlign : 	'left',
		  	buttonAlign: 	'right',
		  	fileUpload : 	true,
		  	enctype : 		'multipart/form-data',
		  	items : [
		  	{
		    	text : 		ORYX.I18N.BPELSupport.content, 
				style : 	'font-size:12px;margin-bottom:10px;display:block;',
				xtype : 	'label'
		  	}, {
	            xtype: 'textarea',
	            width: '160',
	            height: '350',
	            hideLabel: true,
	            anchor: '100% -63'
	        }]
		});
		
		var dialog = new Ext.Window({ 
			autoCreate:     true, 
			title: 		ORYX.I18N.BPELSupport.impPanel, 
			height: 	'auto', 
			width: 		'auto', 
			modal:		true,
			collapsible:false,
			fixedcenter:true, 
			shadow:		true, 
			proxyDrag: 	true,
			resizable:	false,
			items: [form, displayPanel],
			buttons:[
				{
					text:ORYX.I18N.BPELSupport.impBtn,
					handler: function(){
						
							
						var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:ORYX.I18N.BPELSupport.progressImp});
						loadMask.show();
												
						form.form.submit({
				      		url: ORYX.PATH + '/bpelimporter',
				      		success: function(f,a){
								
								dialog.hide();
								// Get the erdf string					
								var erdf = a.result;
								erdf = erdf.startsWith('<?xml') ? erdf : '<?xml version="1.0" encoding="utf-8"?><div>'+erdf+'</div>';	
								// Load the eRDF to the editor
								this.loadERDF(erdf);
								// Hide the waiting panel
								loadMask.hide();
								
				      		}.bind(this),
							failure: function(f,a){
								dialog.hide();
								loadMask.hide();
								Ext.MessageBox.show({
		           					title: ORYX.I18N.BPELSupport.error,
		          	 				msg: ORYX.I18N.BPELSupport.impFailed + a.response.responseText.substring(a.response.responseText.indexOf("content:'")+9, a.response.responseText.indexOf("'}")),
		           					buttons: Ext.MessageBox.OK,
		           					icon: Ext.MessageBox.ERROR
		       					});
				      		}
				  		});
					}.bind(this)
				},{
					text:ORYX.I18N.BPELSupport.close,
					handler:function(){
						dialog.hide();
					}.bind(this)
				}
			]
		});

		dialog.on('hide', function(){
			dialog.destroy(true);
			delete dialog;
		});
		
		dialog.show();
	
		// Adds the change event handler to file upload filed 
		form.items.items[1].getEl().dom.addEventListener('change',function(evt){
				var text = evt.target.files[0].getAsBinary();
				displayPanel.items.items[1].setValue( text );
			}, true)
	},
	
	loadERDF: function(erdfString){
								
		var parser = new DOMParser();			
		var doc    = parser.parseFromString(erdfString ,"text/xml");
		
		alert(erdfString);
		this.facade.importERDF( doc );

	},
	
	
	/**************************** Layout ****************************/
	
	
	dropShapesDown: function(){
		alert("dropShapesDown");
				
        /*this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_MOUSEDOWN
        });*/
		return;
		
	}, 
	
	/**
	 *  realize special BPEL layouting:
	 *  main activity: placed left,
	 *  Handler: placed right.
	 */
	handleLayoutEvent: function(event) {
		/*alert("handleLayoutEvent");
		this.dropShapesDown();
		
     	var shape = event.shape;
     	var elements = shape.getChildShapes(false);
     	
     	var activity;
     	var eventHandlers;
     	var faultHandlers;
     	var compensationHandler;
     	var terminationHandler;
     	
     	elements.each(function(element) {
     		if (element.getStelcil().roles()=="activity"){
     			activity = element;
     		};
     		if (element.getStelcil().id()=="eventHandlers"){
     			eventHandlers = element;
     		};
     		if (element.getStelcil().id()=="faultHandlers"){
     			faultHandlers = element;
     		};
     		if (element.getStelcil().id()=="compensationHandler"){
     			compensationHandler = element;
     		};
     		if (element.getStelcil().id()=="terminationHandler"){
     			terminationHandler = element;
     		};
		});
		
		var nextLeftBound = shape.bounds.upperLeft().x + 30;
		var nextUpperBound = shape.bounds.upperLeft().y + 30;
		
		// handle Activity
		if (activity !== null){
			activity.bound.moveTo(nextLeftBound, nextUpperBound);
			nextLeftBound = activity.bounds.lowerRight().x + 30;
		}
		// handle EventHanlders
		if (eventHandlers !== null){
			eventHandlers.bound.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = eventHandlers.bounds.lowerRight().y;
		}
		// handle FaultHandlers
		if (faultHandlers !== null){
			faultHandlers.bound.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = faultHandlers.bounds.lowerRight().y;
		}
		// handle CompensationHandler
		if (compensationHandler !== null){
			compensationHandler.bound.moveTo(nextLeftBound, nextUpperBound);
			nextUpperBound = compensationHandler.bounds.lowerRight().y;
		}
		// handle TerminationHandler
     	if (terminationHandler !== null){
			terminationHandler.bound.moveTo(nextLeftBound, nextUpperBound);
		}
		
		this.autoResizeLayout(event);
		*/
		return;
		
	},
	
	handleLayoutVerticalEvent: function(event) {
		/*this.dropShapesDown();
		
		var shape= event.shape;
		var elements = shape.getChildShapes(false);
		
		if (elements == null){
			return;
		}
		
		// Sort top-down
		elements = elements.sortBy(function(element) {
			return element.bounds.upperLeft().y;
		});
	
		var shapeUL = shape.bounds.upperLeft();
		var lastUpperYPosition = shapeUL.y;
		
		elements.each(function(element) {
		   element.bounds.moveTo(shapeUL.x + 30,lastUpperYPosition + 30);
		   lastUpperYPosition = element.bounds.lowerRight().y;
		});
		
		this.autoResizeLayout(event);
		*/
		return;
	},
	
	handleLayoutHorizontalEvent: function(event) {
		/*this.dropShapesDown();
		
		var shape= event.shape;
		var elements = shape.getChildShapes(false);
		
		if (elements == null){
			return;
		}
		
		// Sort left-right
		elements = elements.sortBy(function(element) {
			return element.bounds.upperLeft().x;
		});
		
		var i = 0;
		var shapeUL = shape.bounds.upperLeft();
		var lastLeftXPosition = shapeUL.x;
		
		elements.each(function(element) {
		   i++;
		   element.bounds.moveTo(lastLeftXPosition + 30, shapeUL.y + 30);
		   lastLeftXPosition = element.bounds.lowerRight().x;
		});
		
		this.autoResizeLayout(event);
		*/
		return;
	},
	
	
	
	handleSingleChildLayoutEvent: function(event) {
     	/*//alert("handleSingleChildLayoutEvent");
     	
     	this.dropShapesDown();
     	
		var shape = event.shape;
		var element = shape.getChildShapes(false).first();
		
		if (element==null){
			return;
		}
	
		var shapeUL = shape.bounds.upperLeft();
		
		element.bounds.moveTo(shapeUL.x + 30, shapeUL.y + 30);
		
		var elementLR = element.bounds.lowerRight();
		
		shape.bounds.set(shapeUL.x, shapeUL.y, elementLR.x + 30, elementLR.y + 30);
        */
		return;
	},
	
	handleAutoResizeLayoutEvent: function(event) {
		/*this.dropShapesDown();
		
		this.autoResizeLayout(event);*/
	},
	
	/**
	 * Resizes the shape to the bounds of the child shapes
	 */
	autoResizeLayout: function(event) {
		
		//alert("handleAutoResizeLayoutEvent");

		var children = event.shape.getChildShapes(false);
		
		//alert("handleAutoResizeLayoutEvent 2");
		
		if(children.length > 0) {
			//alert("handleAutoResizeLayoutEvent 3");
			var leftBound = 10000;
			var upperBound = 10000;
			var rightBound = 0;
			var lowerBound = 0;
	
			children.each(function(child) {
				var ul = child.bounds.upperLeft();
				var lr = child.bounds.lowerRight();
				
				if (ul.x < leftBound)  leftBound  = ul.x;
				if (ul.y < upperBound) upperBound = ul.y;
				if (lr.x > rightBound) rightBound = lr.x;
				if (lr.y > lowerBound) lowerBound = lr.y;
			});		
			
			leftBound = leftBound - 30;
			upperBound = upperBound - 30;
			rightBound = rightBound + 30;
			lowerBound = lowerBound + 30;
			
			event.shape.bounds.set(leftBound, upperBound, rightBound, lowerBound);
		};
		
		return;
	}


	
	
});
	
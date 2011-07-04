/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
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

/**
 * @namespace Oryx name space for plugins
 * @name ORYX.Plugins
*/
if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * The view plugin offers all of zooming functionality accessible over the 
 * tool bar. This are zoom in, zoom out, zoom to standard, zoom fit to model.
 * 
 * @class ORYX.Plugins.View
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
*/
ORYX.Plugins.View = {
	/** @lends ORYX.Plugins.View.prototype */
	facade: undefined,

	construct: function(facade, ownPluginData) {
		this.facade = facade;
		//Standard Values
		this.zoomLevel = 1.0;
		this.maxFitToScreenLevel=1.5;
		this.minZoomLevel = 0.1;
		this.maxZoomLevel = 2.5;
		this.diff=5; //difference between canvas and view port, s.th. like toolbar??
		
		//Read properties
		if (ownPluginData.properties) {
			ownPluginData.properties.each( function(property) {			
				if (property.zoomLevel) {this.zoomLevel = Number(1.0);}		
				if (property.maxFitToScreenLevel) {this.maxFitToScreenLevel=Number(property.maxFitToScreenLevel);}
				if (property.minZoomLevel) {this.minZoomLevel = Number(property.minZoomLevel);}
				if (property.maxZoomLevel) {this.maxZoomLevel = Number(property.maxZoomLevel);}
			}.bind(this));
		}

		
		/* Register zoom in */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomIn,
			'functionality': this.zoom.bind(this, [1.0 + ORYX.CONFIG.ZOOM_OFFSET]),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/magnifier_zoom_in.png",
			'description': ORYX.I18N.View.zoomInDesc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){return this.zoomLevel < this.maxZoomLevel }.bind(this)});
		
		/* Register zoom out */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomOut,
			'functionality': this.zoom.bind(this, [1.0 - ORYX.CONFIG.ZOOM_OFFSET]),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/magnifier_zoom_out.png",
			'description': ORYX.I18N.View.zoomOutDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){ return this._checkSize() }.bind(this)});
		
		/* Register zoom standard */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomStandard,
			'functionality': this.setAFixZoomLevel.bind(this, 1),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/zoom_standard.png",
			'cls' : 'icon-large',
			'description': ORYX.I18N.View.zoomStandardDesc,
			'index': 3,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){return this.zoomLevel != 1}.bind(this)
		});
		
		/* Register zoom fit to model */
		this.facade.offer({
			'name':ORYX.I18N.View.zoomFitToModel,
			'functionality': this.zoomFitToModel.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/image.png",
			'description': ORYX.I18N.View.zoomFitToModelDesc,
			'index': 4,
			'minShape': 0,
			'maxShape': 0
		});
		
		/* Register popout to model */
		this.facade.offer({
			'name':ORYX.I18N.View.showInPopout,
			'functionality': this.showInPopout.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/popup.gif",
			'description': ORYX.I18N.View.showInPopoutDesc,
			'index': 5,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register pdfview to model */
		this.facade.offer({
			'name':ORYX.I18N.View.convertToPDF,
			'functionality': this.showAsPDF.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/pdf.gif",
			'description': ORYX.I18N.View.convertToPDFDesc,
			'index': 6,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register pngview to model */
		this.facade.offer({
			'name':ORYX.I18N.View.convertToPNG,
			'functionality': this.showAsPNG.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/png.gif",
			'description': ORYX.I18N.View.convertToPNGDesc,
			'index': 7,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register task form generation to model */
		this.facade.offer({
			'name':ORYX.I18N.View.generateTaskForms,
			'functionality': this.generateTaskForms.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/human_task.gif",
			'description': ORYX.I18N.View.generateTaskFormsDesc,
			'index': 8,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
		
		/* Register information view to model */
		this.facade.offer({
			'name':ORYX.I18N.View.showInfo,
			'functionality': this.showInfo.bind(this),
			'group': ORYX.I18N.View.group,
			'icon': ORYX.PATH + "images/information.png",
			'description': ORYX.I18N.View.showInfoDesc,
			'index': 9,
			'minShape': 0,
			'maxShape': 0,
			'isEnabled': function(){
				profileParamName = "profile";
				profileParamName = profileParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
				regexSa = "[\\?&]"+profileParamName+"=([^&#]*)";
		        regexa = new RegExp( regexSa );
		        profileParams = regexa.exec( window.location.href );
		        profileParamValue = profileParams[1]; 
				return profileParamValue == "jbpm";
			}.bind(this)
		});
	},
	
	/**
	 * It sets the zoom level to a fix value and call the zooming function.
	 * 
	 * @param {Number} zoomLevel
	 * 			the zoom level
	 */
	setAFixZoomLevel : function(zoomLevel) {
		this.zoomLevel = zoomLevel;
		this._checkZoomLevelRange();
		this.zoom(1);
	},
	
	/**
	 * Shows the canvas in bigger popout window.
	 * 
	 */
	showInPopout : function() {
		uuidParamName = "uuid";
        uuidParamName = uuidParamName.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
        regexS = "[\\?&]"+uuidParamName+"=([^&#]*)";
        regex = new RegExp( regexS );
        uuidParams = regex.exec( window.location.href );

        uuidParamValue = uuidParams[1]; 
        window.open (window.location.protocol + "//" + window.location.host + "/drools-guvnor/org.drools.guvnor.Guvnor/standaloneEditorServlet?assetsUUIDs=" + uuidParamValue + "&client=oryx" , "Process Editor","status=0,toolbar=0,menubar=0,resizable=0,location=no,width=1400,height=1000");
	},
	
	/**
	 * Converts the process to pdf format.
	 * 
	 */
	showAsPDF : function() {
		var transformval = 'pdf';
		var svgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(true));
		
		var method ="post";
		var form = document.createElement("form");
		form.setAttribute("name", "transformerform");
		form.setAttribute("method", method);
		form.setAttribute("action", ORYX.CONFIG.TRANSFORMER_URL());
		form.setAttribute("target", "_blank");
		
		var hfSVG = document.createElement("input");
		hfSVG.setAttribute("type", "hidden");
		hfSVG.setAttribute("name", "svg");
		hfSVG.setAttribute("value", svgDOM);
        form.appendChild(hfSVG);
        
        var hfUUID = document.createElement("input");
        hfUUID.setAttribute("type", "hidden");
        hfUUID.setAttribute("name", "uuid");
        hfUUID.setAttribute("value", ORYX.UUID);
        form.appendChild(hfUUID);
        
        var hfPROFILE = document.createElement("input");
        hfPROFILE.setAttribute("type", "hidden");
        hfPROFILE.setAttribute("name", "profile");
        hfPROFILE.setAttribute("value", ORYX.PROFILE);
        form.appendChild(hfPROFILE);
        
        var hfTRANSFORMTO = document.createElement("input");
        hfTRANSFORMTO.setAttribute("type", "hidden");
        hfTRANSFORMTO.setAttribute("name", "transformto");
        hfTRANSFORMTO.setAttribute("value", transformval);
        form.appendChild(hfTRANSFORMTO);
        
        document.body.appendChild(form);
        form.submit();	 
	},
	
	/**
	 * Shows designer version info.
	 * 
	 */
	showInfo : function() {
		window.alert("Version: " + ORYX.VERSION);
	},
	
	/**
	 * Converts the process to png format.
	 * 
	 */
	showAsPNG : function() {
		var transformval = 'png';
		var svgDOM = DataManager.serialize(ORYX.EDITOR.getCanvas().getSVGRepresentation(true));
		
		var method ="post";
		var form = document.createElement("form");
		form.setAttribute("name", "transformerform");
		form.setAttribute("method", method);
		form.setAttribute("action", ORYX.CONFIG.TRANSFORMER_URL());
		form.setAttribute("target", "_blank");
		
		var hfSVG = document.createElement("input");
		hfSVG.setAttribute("type", "hidden");
		hfSVG.setAttribute("name", "svg");
		hfSVG.setAttribute("value", svgDOM);
        form.appendChild(hfSVG);
        
        var hfUUID = document.createElement("input");
        hfUUID.setAttribute("type", "hidden");
        hfUUID.setAttribute("name", "uuid");
        hfUUID.setAttribute("value", ORYX.UUID);
        form.appendChild(hfUUID);
        
        var hfPROFILE = document.createElement("input");
        hfPROFILE.setAttribute("type", "hidden");
        hfPROFILE.setAttribute("name", "profile");
        hfPROFILE.setAttribute("value", ORYX.PROFILE);
        form.appendChild(hfPROFILE);
        
        var hfTRANSFORMTO = document.createElement("input");
        hfTRANSFORMTO.setAttribute("type", "hidden");
        hfTRANSFORMTO.setAttribute("name", "transformto");
        hfTRANSFORMTO.setAttribute("value", transformval);
        form.appendChild(hfTRANSFORMTO);
        
        document.body.appendChild(form);
        form.submit();	 
	},
	
	generateTaskForms : function() {
		
		var processJSON = ORYX.EDITOR.getSerializedJSON();
		var preprocessingData = ORYX.PREPROCESSING;
		
		var method ="post";
		var form = document.createElement("form");
		form.setAttribute("name", "taskformsform");
		form.setAttribute("method", method);
		form.setAttribute("action", ORYX.CONFIG.TASKFORMS_URL());
		form.setAttribute("target", "_blank");
		
		var hfSVG = document.createElement("input");
		hfSVG.setAttribute("type", "hidden");
		hfSVG.setAttribute("name", "json");
		hfSVG.setAttribute("value", processJSON);
        form.appendChild(hfSVG);
        
        var hfSVG = document.createElement("input");
		hfSVG.setAttribute("type", "hidden");
		hfSVG.setAttribute("name", "ppdata");
		hfSVG.setAttribute("value", preprocessingData);
        form.appendChild(hfSVG);
        
        var hfUUID = document.createElement("input");
        hfUUID.setAttribute("type", "hidden");
        hfUUID.setAttribute("name", "uuid");
        hfUUID.setAttribute("value", ORYX.UUID);
        form.appendChild(hfUUID);
        
        var hfPROFILE = document.createElement("input");
        hfPROFILE.setAttribute("type", "hidden");
        hfPROFILE.setAttribute("name", "profile");
        hfPROFILE.setAttribute("value", ORYX.PROFILE);
        form.appendChild(hfPROFILE);
        
        document.body.appendChild(form);
        form.submit();
	},
	
	/**
	 * It does the actual zooming. It changes the viewable size of the canvas 
	 * and all to its child elements.
	 * 
	 * @param {Number} factor
	 * 		the factor to adjust the zoom level
	 */
	zoom: function(factor) {
		// TODO: Zoomen auf allen Objekten im SVG-DOM
		
		this.zoomLevel *= factor;
		var scrollNode 	= this.facade.getCanvas().getHTMLContainer().parentNode.parentNode;
		var canvas 		= this.facade.getCanvas();
		var newWidth 	= canvas.bounds.width()  * this.zoomLevel;
		var newHeight 	= canvas.bounds.height() * this.zoomLevel;
		
		/* Set new top offset */
		var offsetTop = (canvas.node.parentNode.parentNode.parentNode.offsetHeight - newHeight) / 2.0;	
		offsetTop = offsetTop > 20 ? offsetTop - 20 : 0;
		canvas.node.parentNode.parentNode.style.marginTop = offsetTop + "px";
		offsetTop += 5;
		canvas.getHTMLContainer().style.top = offsetTop + "px";
		
		/*readjust scrollbar*/
		var newScrollTop=	scrollNode.scrollTop - Math.round((canvas.getHTMLContainer().parentNode.getHeight()-newHeight) / 2)+this.diff;
		var newScrollLeft=	scrollNode.scrollLeft - Math.round((canvas.getHTMLContainer().parentNode.getWidth()-newWidth) / 2)+this.diff;
		
		/* Set new Zoom-Level */
		canvas.setSize({width: newWidth, height: newHeight}, true);
		
		/* Set Scale-Factor */
		canvas.node.setAttributeNS(null, "transform", "scale(" +this.zoomLevel+ ")");	

		/* Refresh the Selection */
		this.facade.updateSelection();
		scrollNode.scrollTop=newScrollTop;
		scrollNode.scrollLeft=newScrollLeft;
		
		/* Update the zoom-level*/
		canvas.zoomLevel = this.zoomLevel;
	},
	
	
	/**
	 * It calculates the zoom level to fit whole model into the visible area
	 * of the canvas. Than the model gets zoomed and the position of the 
	 * scroll bars are adjusted.
	 * 
	 */
	zoomFitToModel: function() {
		
		/* Get the size of the visible area of the canvas */
		var scrollNode 	= this.facade.getCanvas().getHTMLContainer().parentNode.parentNode;
		var visibleHeight = scrollNode.getHeight() - 30;
		var visibleWidth = scrollNode.getWidth() - 30;
		
		var nodes = this.facade.getCanvas().getChildShapes();
		
		if(!nodes || nodes.length < 1) {
			return false;			
		}
			
		/* Calculate size of canvas to fit the model */
		var bounds = nodes[0].absoluteBounds().clone();
		nodes.each(function(node) {
			bounds.include(node.absoluteBounds().clone());
		});
		
		
		/* Set new Zoom Level */
		var scaleFactorWidth =  visibleWidth / bounds.width();
		var scaleFactorHeight = visibleHeight / bounds.height();
		
		/* Choose the smaller zoom level to fit the whole model */
		var zoomFactor = scaleFactorHeight < scaleFactorWidth ? scaleFactorHeight : scaleFactorWidth;
		
		/*Test if maximum zoom is reached*/
		if(zoomFactor>this.maxFitToScreenLevel){zoomFactor=this.maxFitToScreenLevel}
		/* Do zooming */
		this.setAFixZoomLevel(zoomFactor);
		
		/* Set scroll bar position */
		scrollNode.scrollTop = Math.round(bounds.upperLeft().y * this.zoomLevel) - 5;
		scrollNode.scrollLeft = Math.round(bounds.upperLeft().x * this.zoomLevel) - 5;
		
	},
	
	/**
	 * It checks if the zoom level is less or equal to the level, which is required
	 * to schow the whole canvas.
	 * 
	 * @private
	 */
	_checkSize:function(){
		var canvasParent=this.facade.getCanvas().getHTMLContainer().parentNode;
		var minForCanvas= Math.min((canvasParent.parentNode.getWidth()/canvasParent.getWidth()),(canvasParent.parentNode.getHeight()/canvasParent.getHeight()));
		return 1.05 > minForCanvas;
		
	},
	/**
	 * It checks if the zoom level is included in the definined zoom
	 * level range.
	 * 
	 * @private
	 */
	_checkZoomLevelRange: function() {
		/*var canvasParent=this.facade.getCanvas().getHTMLContainer().parentNode;
		var maxForCanvas= Math.max((canvasParent.parentNode.getWidth()/canvasParent.getWidth()),(canvasParent.parentNode.getHeight()/canvasParent.getHeight()));
		if(this.zoomLevel > maxForCanvas) {
			this.zoomLevel = maxForCanvas;			
		}*/
		if(this.zoomLevel < this.minZoomLevel) {
			this.zoomLevel = this.minZoomLevel;			
		}
		
		if(this.zoomLevel > this.maxZoomLevel) {
			this.zoomLevel = this.maxZoomLevel;			
		}
	}
};

ORYX.Plugins.View = Clazz.extend(ORYX.Plugins.View);

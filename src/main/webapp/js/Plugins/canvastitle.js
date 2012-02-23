if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Config.CanvasTitle = {
}

ORYX.Plugins.CanvasTitle = {
	titleNode: undefined,
	facade: undefined,
	titleID: undefined,
	textID: undefined,
	
	construct: function(facade){
		this.facade = facade;
		this.titleID = "canvasTitleId";
		this.textID = ORYX.Editor.provideId();
		this.titleNode = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
                ['text', {"id":this.textID, 
                	       "style": "stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold", 
                	       "font-size": 14, 
                	       "onclick": "ORYX.Plugins.CanvasTitle.openTextualAnalysis()",
                	       "onmouseover": "ORYX.Plugins.CanvasTitle.addToolTip('"+this.textID+"')"}]
        );
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.showTitle.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.updateTitle.bind(this));
	},
	showTitle : function() {
		this.titleNode.textContent = this._getTitleFromJSON();
		
		this.facade.raiseEvent({
            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
            id: this.titleID,
            shapes: [this.facade.getCanvas()],
            node: this.titleNode,
            nodePosition: "CANVAS_TITLE"
        });
	},
	updateTitle: function() {
		this.facade.raiseEvent({
			type: 	ORYX.CONFIG.EVENT_OVERLAY_HIDE,
			id: 	this.titleID
		});
		this.showTitle();
	},
	_getTitleFromJSON: function() {
		var processJSON = ORYX.EDITOR.getSerializedJSON();
        var processName = jsonPath(processJSON.evalJSON(), "$.properties.name");
        var processPackage = jsonPath(processJSON.evalJSON(), "$.properties.package");
        var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
        var processVersion = jsonPath(processJSON.evalJSON(), "$.properties.version");
        var retValue = "";
        
        if(processName && processName != "") {
        	retValue += processName[0];
        	if(processVersion && processVersion != "") {
        		retValue += " v." + processVersion[0];
        	}
        	if(processId && processId != "" && processPackage && processPackage != "") {
        		retValue += " (" + processId[0] + ")";
        	}
        	return retValue;
        } else {
        	return "";
        }
	}
};

ORYX.Plugins.CanvasTitle = Clazz.extend(ORYX.Plugins.CanvasTitle);
ORYX.Plugins.CanvasTitle.openTextualAnalysis = function() {
	// TODO FINISH
};
ORYX.Plugins.CanvasTitle.addToolTip = function(objectid) {
		// TODO FINISH!
//		var titleNodeToolTip = new Ext.ToolTip({
//		target: objectid,
//		title: 'Click to open Textual Analysis',
//		plain: true,
//		showDelay: 50,
//		width: 200
//	});
};
if (!ORYX.Plugins) 
    ORYX.Plugins = {};

if (!ORYX.Config)
	ORYX.Config = {};

ORYX.Config.CanvasTitle = {
}

ORYX.Config.FACADE = {
		
}

ORYX.Plugins.CanvasTitle = {
    facade: undefined,
	titleNode: undefined,
	processFormP1: undefined,
	processFormP2: undefined,
	processFormP3: undefined,
	processFormP4: undefined,
	processFormP5: undefined,
	facade: undefined,
	titleID: undefined,
	textID: undefined,
	formID: undefined,
	
	construct: function(facade){
		this.facade = facade;
		ORYX.Config.FACADE = facade;
		this.titleID = "canvasTitleId";
		this.titleFormID = " canvasTitleFormId";
		this.textID = ORYX.Editor.provideId();
		this.formID = ORYX.Editor.provideId();
		
		this.titleNode = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
                ['text', {"id":this.textID, 
                	       "style": "stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold", 
                	       "font-size": 12, 
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
ORYX.Plugins.CanvasTitle.editProcessForm = function() {
	var processJSON = ORYX.EDITOR.getSerializedJSON();
	var processId = jsonPath(processJSON.evalJSON(), "$.properties.id");
	if(processId && processId != "") {
		ORYX.Config.FACADE.raiseEvent({
            type: ORYX.CONFIG.EVENT_TASKFORM_EDIT,
            tn: processId 
        });
	} else {
		Ext.Msg.alert('Process Id not specified.');
	}
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

ORYX.Plugins.CanvasTitle.addFormToolTip = function(objectid) {
	var formNodeToolTip = new Ext.ToolTip({
	target: objectid,
	title: 'Click to edit Process Form',
	plain: true,
	showDelay: 50,
	width: 200
    });
};
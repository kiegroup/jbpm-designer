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
//	processFormP1: undefined,
//	processFormP2: undefined,
//	processFormP3: undefined,
//	processFormP4: undefined,
//	processFormP5: undefined,
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
		
//		this.processFormP1 = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
//                ['path', {"id":"processFormP1",
//                		   "title":"Edit Process Form",
//                	       "style": "opacity:1;fill:#339966;stroke:#000000",
//                	       "d": "M0.585,24.167h24.083v-7.833c0,0-2.333-3.917-7.083-5.167h-9.25 c-4.417,1.333-7.833,5.75-7.833,5.75L0.585,24.167z",
//                	       "onclick": "ORYX.Plugins.CanvasTitle.editProcessForm()"
//                	       }]
//        );
//
//		this.processFormP2 = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
//                ['path', {"id":"processFormP2",
//                		   "title":"Edit Process Form",
//                	       "style": "opacity:1;fill:none;stroke:#000000",
//                	       "d": "M 6 20 L 6 24",
//                	       "onclick":"ORYX.Plugins.CanvasTitle.editProcessForm()"
//                	       }]
//        );
//
//		this.processFormP3 = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
//                ['path', {"id":"processFormP3",
//                		   "title":"Edit Process Form",
//                	       "style": "opacity:1;fill:none;stroke:#000000",
//                	       "d": "M 20 20 L 20 24",
//                	       "onclick": "ORYX.Plugins.CanvasTitle.editProcessForm()"
//                	       }]
//        );
//
//		this.processFormP4 = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
//                ['circle', {"id":"processFormP4",
//                		   "title":"Edit Process Form",
//                		   "fill": "#000000",
//                		   "stroke": "#000000",
//                	       "cx": "13.002",
//                	       "cy": "5.916",
//                	       "r": "5.417",
//                	       "onclick": "ORYX.Plugins.CanvasTitle.editProcessForm()"
//                	       }]
//        );
//
//		this.processFormP5 = ORYX.Editor.graft("http://www.w3.org/2000/svg", null,
//                ['path', {"id":"processFormP5",
//                		   "title":"Edit Process Form",
//                	       "style": "opacity:1;fill:#FFCC99;stroke:#000000",
//                	       "d": "M8.043,7.083c0,0,2.814-2.426,5.376-1.807s4.624-0.693,4.624-0.693 c0.25,1.688,0.042,3.75-1.458,5.584c0,0,1.083,0.75,1.083,1.5s0.125,1.875-1,3s-5.5,1.25-6.75,0S8.668,12.834,8.668,12 s0.583-1.25,1.25-1.917C8.835,9.5,7.419,7.708,8.043,7.083z",
//                	       "onclick": "ORYX.Plugins.CanvasTitle.editProcessForm()"
//                	       }]
//        );
		
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.showTitle.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.updateTitle.bind(this));
	},
	showTitle : function() {
		this.titleNode.textContent = this._getTitleFromJSON();
		
//		this.facade.raiseEvent({
//            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
//            id: this.titleFormID,
//            shapes: [this.facade.getCanvas()],
//            node: this.processFormP1,
//            nodePosition: "CANVAS_TITLE_FORM"
//        });
//		this.facade.raiseEvent({
//            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
//            id: this.titleFormID,
//            shapes: [this.facade.getCanvas()],
//            node: this.processFormP2,
//            nodePosition: "CANVAS_TITLE_FORM"
//        });
//		this.facade.raiseEvent({
//            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
//            id: this.titleFormID,
//            shapes: [this.facade.getCanvas()],
//            node: this.processFormP3,
//            nodePosition: "CANVAS_TITLE_FORM"
//        });
//		this.facade.raiseEvent({
//            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
//            id: this.titleFormID,
//            shapes: [this.facade.getCanvas()],
//            node: this.processFormP4,
//            nodePosition: "CANVAS_TITLE_FORM"
//        });
//		this.facade.raiseEvent({
//            type: ORYX.CONFIG.EVENT_OVERLAY_SHOW,
//            id: this.titleFormID,
//            shapes: [this.facade.getCanvas()],
//            node: this.processFormP5,
//            nodePosition: "CANVAS_TITLE_FORM"
//        });
//
		
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
        var processName = jsonPath(processJSON.evalJSON(), "$.properties.processn");
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
        this.facade.raiseEvent({
            type 		: ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
            ntype		: 'error',
            msg         : 'Process Id not specified.',
            title       : ''

        });
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
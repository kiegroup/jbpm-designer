/**
 * @author martin.czuchra
 */
ORYX.CONFIG = {
	
	/* Editor-Mode */
	MODE_READONLY:				"readonly",
	MODE_FULLSCREEN:			"fullscreen",
		
	/* Plugins */
	PLUGINS_ENABLED:			true,
	PLUGINS_CONFIG:				"plugins.xml",
	PLUGINS_FOLDER:				"Plugins/",
	PDF_EXPORT_URL:				"/oryx/pdf",
	PNML_EXPORT_URL:			"/oryx/pnml",
	SIMPLE_PNML_EXPORT_URL:		"/oryx/simplepnmlexporter",
	DESYNCHRONIZABILITY_URL:	"/oryx/desynchronizability",
	SYNTAXCHECKER_URL:			"/oryx/syntaxchecker",
	VALIDATOR_URL:				"/oryx/bpmnvalidator",
	AUTO_LAYOUTER_URL:			"/oryx/layouter",
	SS_EXTENSIONS_FOLDER:		"/oryx/stencilsets/extensions/",
	SS_EXTENSIONS_CONFIG:		"/oryx/stencilsets/extensions/extensions.json",	
	ORYX_NEW_URL:				"/new",	
	STEP_THROUGH:				"/oryx/stepthrough",
	STEP_THROUGH_CHECKER:		"/oryx/stepthroughchecker",
	XFORMS_EXPORT_URL:			"/oryx/xformsexport",
	XFORMS_IMPORT_URL:			"/oryx/xformsimport",
	BPEL_EXPORT_URL:			"/oryx/bpelexporter",
	TREEGRAPH_SUPPORT:			"/oryx/treegraphsupport",
	
	/* Namespaces */
	NAMESPACE_ORYX:				"http://www.b3mn.org/oryx",
	NAMESPACE_SVG:				"http://www.w3.org/2000/svg",

	/* UI */
	CANVAS_WIDTH:				1485, 
	CANVAS_HEIGHT:				1050,
	CANVAS_RESIZE_INTERVAL:		300,
	SELECTED_AREA_PADDING:		4,
	CANVAS_BACKGROUND_COLOR:	"none",
	GRID_DISTANCE:				30,
	GRID_ENABLED:				true,
	ZOOM_OFFSET:				0.1,
	DEFAULT_SHAPE_MARGIN:		60,
	SCALERS_SIZE:				7,
	MINIMUM_SIZE:				20,
	MAXIMUM_SIZE:				10000,
	OFFSET_MAGNET:				15,
	OFFSET_EDGE_LABEL_TOP:		14,
	OFFSET_EDGE_LABEL_BOTTOM:	12,
	COPY_MOVE_OFFSET:			30,
	
	BORDER_OFFSET:				14,

	/* Shape-Menu Align */
	SHAPEMENU_RIGHT:			"Oryx_Right",
	SHAPEMENU_BOTTOM:			"Oryx_Bottom",
	SHAPEMENU_LEFT:				"Oryx_Left",
	SHAPEMENU_TOP:				"Oryx_Top",

	/* Property type names */
	TYPE_STRING:				"string",
	TYPE_BOOLEAN:				"boolean",
	TYPE_INTEGER:				"integer",
	TYPE_FLOAT:					"float",
	TYPE_COLOR:					"color",
	TYPE_DATE:					"date",
	TYPE_CHOICE:				"choice",
	TYPE_URL:					"url",
	TYPE_COMPLEX:				"complex",
	
	/* Vertical line distance of multiline labels */
	LABEL_LINE_DISTANCE:		2,
	LABEL_DEFAULT_LINE_HEIGHT:	12,

	/* Editor constants come here */
	EDITOR_ALIGN_BOTTOM:		0x01,
	EDITOR_ALIGN_MIDDLE:		0x02,
	EDITOR_ALIGN_TOP:			0x04,
	EDITOR_ALIGN_LEFT:			0x08,
	EDITOR_ALIGN_CENTER:		0x10,
	EDITOR_ALIGN_RIGHT:			0x20,

	/* Event types */
	EVENT_MOUSEDOWN:			"mousedown",
	EVENT_MOUSEUP:				"mouseup",
	EVENT_MOUSEOVER:			"mouseover",
	EVENT_MOUSEOUT:				"mouseout",
	EVENT_MOUSEMOVE:			"mousemove",
	EVENT_DBLCLICK:				"dblclick",
	EVENT_KEYDOWN:				"keydown",
	EVENT_KEYUP:				"keyup",
	
	EVENT_EXECUTE_COMMANDS:			"executeCommands",
	EVENT_STENCIL_SET_LOADED:		"stencilSetLoaded",
	EVENT_SELECTION_CHANGED:		"selectionchanged",
	
	EVENT_PROPERTY_CHANGED:			"propertyChanged",
	EVENT_DRAGDROP_START:			"dragdrop.start",
	EVENT_DRAGDROP_END:				"dragdrop.end",
	EVENT_DRAGDOCKER_DOCKED:		"dragDocker.docked",
	EVENT_HIGHLIGHT_SHOW:			"highlight.showHighlight",
	EVENT_HIGHLIGHT_HIDE:			"highlight.hideHighlight",
	EVENT_LOADING_ENABLE:			"loading.enable",
	EVENT_LOADING_DISABLE:			"loading.disable",
	EVENT_LOADING_STATUS:			"loading.status",
	EVENT_OVERLAY_SHOW:				"overlay.show",
	EVENT_OVERLAY_HIDE:				"overlay.hide",
	EVENT_ARRANGEMENT_TOP:			"arrangement.setToTop",
	EVENT_ARRANGEMENT_BACK:			"arrangement.setToBack",
	EVENT_ARRANGEMENT_FORWARD:		"arrangement.setForward",
	EVENT_ARRANGEMENT_BACKWARD:		"arrangement.setBackward",
	EVENT_PROPWINDOW_PROP_CHANGED:	"propertyWindow.propertyChanged",
	EVENT_LAYOUT_ROWS:				"layout.rows",
	EVENT_AUTOLAYOUT_LAYOUT:		"autolayout.layout",
	
	/* Selection Shapes Highlights */
	SELECTION_HIGHLIGHT_SIZE:				5,
	SELECTION_HIGHLIGHT_COLOR:				"#4444FF",
	SELECTION_HIGHLIGHT_COLOR2:				"#9999FF",
	
	SELECTION_HIGHLIGHT_STYLE_CORNER: 		"corner",
	SELECTION_HIGHLIGHT_STYLE_RECTANGLE: 	"rectangle",
	
	SELECTION_VALID_COLOR:					"#00FF00",
	SELECTION_INVALID_COLOR:				"#FF0000",


	DOCKER_DOCKED_COLOR:		"#00FF00",
	DOCKER_UNDOCKED_COLOR:		"#FF0000",
	DOCKER_SNAP_OFFSET:			10,
		
	/* Copy & Paste */
	EDIT_OFFSET_PASTE:			10,

	/* Key-Codes */
	KEY_CODE_X: 				88,
	KEY_CODE_C: 				67,
	KEY_CODE_V: 				86,
	KEY_CODE_DELETE: 			46,
	KEY_CODE_META:				224,
	KEY_CODE_BACKSPACE:			8,
	KEY_CODE_LEFT:				37,
	KEY_CODE_RIGHT:				39,
	KEY_CODE_UP:				38,
	KEY_CODE_DOWN:				40,

	// TODO Determine where the lowercase constants are still used and remove them from here.
	KEY_Code_enter:				12,
	KEY_Code_left:				37,
	KEY_Code_right:				39,
	KEY_Code_top:				38,
	KEY_Code_bottom:			40,
	
	/* Transformation to BPEL4Chor web service */
	TRANS_URI: 					"http://localhost/BPMN2BPEL4Chor/services/BPMN2BPEL4Chor",
	TRANS_SERVER:				"http://localhost"
};
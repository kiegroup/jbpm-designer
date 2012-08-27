/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
if(!ORYX) var ORYX = {};

if(!ORYX.CONFIG) ORYX.CONFIG = {};

ORYX.CONFIG.WEB_URL = "designer";



ORYX.CONFIG.MENU_INDEX = {"File" : 1, "Edit" : 2, "Z-Order" : 3, "Undo" : 4, "Docker" : 5, "Help" : "ZZZZZZ"};

ORYX.CONFIG.UUID_URL = function(uuid, profile) {
  if (uuid === undefined) {
    uuid = ORYX.UUID;
  }
  if (profile === undefined) {
    profile = ORYX.PROFILE;
  }
  return ORYX.PATH + "uuidRepository?uuid="+ uuid + "&profile=" + profile + "&pp=" + ORYX.PREPROCESSING;
};

ORYX.CONFIG.TRANSFORMER_URL = function(uuid, profile) {
   if (uuid === undefined) {
      uuid = ORYX.UUID;
   }
   if (profile === undefined) {
      profile = ORYX.PROFILE;
   }
   return ORYX.PATH + "transformer?uuid="+ uuid + "&profile=" + profile;
};

ORYX.CONFIG.TASKFORMS_URL = function(uuid, profile) {
	if (uuid === undefined) {
		uuid = ORYX.UUID;
	}
	if (profile === undefined) {
	   profile = ORYX.PROFILE;
	}
	return ORYX.PATH + "taskforms?uuid="+ uuid + "&profile=" + profile;
};
ORYX.CONFIG.UUID_AUTOSAVE_INTERVAL = 120000;
ORYX.CONFIG.UUID_AUTOSAVE_DEFAULT = false;
	
ORYX.CONFIG.VERSION_URL =				ORYX.CONFIG.ROOT_PATH + "VERSION";
ORYX.CONFIG.LICENSE_URL =				ORYX.CONFIG.ROOT_PATH + "LICENSE";

ORYX.CONFIG.SERVER_HANDLER_ROOT = 		"";

ORYX.CONFIG.STENCILSET_HANDLER = 		ORYX.CONFIG.SERVER_HANDLER_ROOT + "";
    
	/* Editor-Mode */
ORYX.CONFIG.MODE_READONLY =				"readonly";
ORYX.CONFIG.MODE_FULLSCREEN =			"fullscreen";
	
		
	/* Show grid line while dragging */
ORYX.CONFIG.SHOW_GRIDLINE = true;
ORYX.CONFIG.DISABLE_GRADIENT = false;

	/* Plugins */
ORYX.CONFIG.PLUGINS_ENABLED =			true;
ORYX.CONFIG.PLUGINS_CONFIG =			ORYX.CONFIG.ROOT_PATH + "plugins";
ORYX.CONFIG.PROFILE_PATH =				ORYX.CONFIG.ROOT_PATH + "profiles/";
ORYX.CONFIG.PLUGINS_FOLDER =			"Plugins/";
ORYX.CONFIG.PDF_EXPORT_URL =			ORYX.CONFIG.ROOT_PATH + "pdf";
ORYX.CONFIG.PNML_EXPORT_URL =			ORYX.CONFIG.ROOT_PATH + "pnml";
ORYX.CONFIG.SIMPLE_PNML_EXPORT_URL =	ORYX.CONFIG.ROOT_PATH + "simplepnmlexporter";
ORYX.CONFIG.DESYNCHRONIZABILITY_URL =	ORYX.CONFIG.ROOT_PATH + "desynchronizability";
ORYX.CONFIG.IBPMN2BPMN_URL =			ORYX.CONFIG.ROOT_PATH + "ibpmn2bpmn";
ORYX.CONFIG.BPMN2YAWL_URL =				ORYX.CONFIG.ROOT_PATH + "bpmn2yawl";
ORYX.CONFIG.QUERYEVAL_URL =             ORYX.CONFIG.ROOT_PATH + "query";
ORYX.CONFIG.SYNTAXCHECKER_URL =			ORYX.CONFIG.ROOT_PATH + "syntaxchecker";
ORYX.CONFIG.VALIDATOR_URL =				ORYX.CONFIG.ROOT_PATH + "validator";
ORYX.CONFIG.AUTO_LAYOUTER_URL =			ORYX.CONFIG.ROOT_PATH + "layouter";
ORYX.CONFIG.SS_EXTENSIONS_FOLDER =		ORYX.CONFIG.ROOT_PATH + "stencilsets/extensions/";
ORYX.CONFIG.SS_EXTENSIONS_CONFIG =		ORYX.CONFIG.ROOT_PATH + "stencilsets/extensions/extensions.json";	
ORYX.CONFIG.ORYX_NEW_URL =				"/new";	
ORYX.CONFIG.STEP_THROUGH =				ORYX.CONFIG.ROOT_PATH + "stepthrough";
ORYX.CONFIG.STEP_THROUGH_CHECKER =		ORYX.CONFIG.ROOT_PATH + "stepthroughchecker";
ORYX.CONFIG.XFORMS_EXPORT_URL =			ORYX.CONFIG.ROOT_PATH + "xformsexport";
ORYX.CONFIG.XFORMS_EXPORT_ORBEON_URL =	ORYX.CONFIG.ROOT_PATH + "xformsexport-orbeon";
ORYX.CONFIG.XFORMS_IMPORT_URL =			ORYX.CONFIG.ROOT_PATH + "xformsimport";
ORYX.CONFIG.BPEL_EXPORT_URL =			ORYX.CONFIG.ROOT_PATH + "bpelexporter";
ORYX.CONFIG.BPEL4CHOR_EXPORT_URL =		ORYX.CONFIG.ROOT_PATH + "bpel4chorexporter";
ORYX.CONFIG.BPEL4CHOR2BPEL_EXPORT_URL =	ORYX.CONFIG.ROOT_PATH + "bpel4chor2bpelexporter";
ORYX.CONFIG.TREEGRAPH_SUPPORT =			ORYX.CONFIG.ROOT_PATH + "treegraphsupport";
ORYX.CONFIG.XPDL4CHOR2BPEL4CHOR_TRANSFORMATION_URL = ORYX.CONFIG.ROOT_PATH + "xpdl4chor2bpel4chor";
ORYX.CONFIG.RESOURCE_LIST =				ORYX.CONFIG.ROOT_PATH + "resourceList";
ORYX.CONFIG.BPMN_LAYOUTER =				ORYX.CONFIG.ROOT_PATH + "bpmnlayouter";
ORYX.CONFIG.EPC_LAYOUTER =				ORYX.CONFIG.ROOT_PATH + "epclayouter";
ORYX.CONFIG.BPMN2MIGRATION =			ORYX.CONFIG.ROOT_PATH + "bpmn2migration";
ORYX.CONFIG.BPMN20_SCHEMA_VALIDATION_ON = true;
ORYX.CONFIG.JPDLIMPORTURL =				ORYX.CONFIG.ROOT_PATH + "jpdlimporter";
ORYX.CONFIG.JPDLEXPORTURL =				ORYX.CONFIG.ROOT_PATH + "jpdlexporter";
ORYX.CONFIG.CPNTOOLSEXPORTER = 			ORYX.CONFIG.ROOT_PATH + "cpntoolsexporter";
ORYX.CONFIG.CPNTOOLSIMPORTER = 			ORYX.CONFIG.ROOT_PATH + "cpntoolsimporter";
ORYX.CONFIG.BPMN2XPDLPATH =				ORYX.CONFIG.ROOT_PATH + "bpmn2xpdl";
ORYX.CONFIG.TBPMIMPORT =				ORYX.CONFIG.ROOT_PATH + "tbpmimport";



	/* Namespaces */
ORYX.CONFIG.NAMESPACE_ORYX =			"http://www.b3mn.org/oryx";
ORYX.CONFIG.NAMESPACE_SVG =				"http://www.w3.org/2000/svg";

	/* UI */
ORYX.CONFIG.CANVAS_WIDTH =				3000; 
ORYX.CONFIG.CANVAS_HEIGHT =				2000;
ORYX.CONFIG.CANVAS_RESIZE_INTERVAL =	300;
ORYX.CONFIG.SELECTED_AREA_PADDING =		4;
ORYX.CONFIG.CANVAS_BACKGROUND_COLOR =	"none";
ORYX.CONFIG.GRID_DISTANCE =				30;
ORYX.CONFIG.GRID_ENABLED =				true;
ORYX.CONFIG.ZOOM_OFFSET =				0.1;
ORYX.CONFIG.DEFAULT_SHAPE_MARGIN =		60;
ORYX.CONFIG.SCALERS_SIZE =				7;
ORYX.CONFIG.MINIMUM_SIZE =				20;
ORYX.CONFIG.MAXIMUM_SIZE =				10000;
ORYX.CONFIG.OFFSET_MAGNET =				15;
ORYX.CONFIG.OFFSET_EDGE_LABEL_TOP =		14;
ORYX.CONFIG.OFFSET_EDGE_LABEL_BOTTOM =	12;
ORYX.CONFIG.OFFSET_EDGE_BOUNDS =		5;
ORYX.CONFIG.COPY_MOVE_OFFSET =			30;
ORYX.CONFIG.SHOW_GRIDLINE =             true;

ORYX.CONFIG.BORDER_OFFSET =				14;

ORYX.CONFIG.MAX_NUM_SHAPES_NO_GROUP	=	14;

ORYX.CONFIG.SHAPEMENU_CREATE_OFFSET_CORNER = 30;
ORYX.CONFIG.SHAPEMENU_CREATE_OFFSET = 45;

	/* Shape-Menu Align */
ORYX.CONFIG.SHAPEMENU_RIGHT =			"Oryx_Right";
ORYX.CONFIG.SHAPEMENU_BOTTOM =			"Oryx_Bottom";
ORYX.CONFIG.SHAPEMENU_LEFT =			"Oryx_Left";
ORYX.CONFIG.SHAPEMENU_TOP =				"Oryx_Top";

	/* Morph-Menu Item */
ORYX.CONFIG.MORPHITEM_DISABLED =		"Oryx_MorphItem_disabled";

	/* Property type names */
ORYX.CONFIG.TYPE_STRING =				"string";
ORYX.CONFIG.TYPE_BOOLEAN =				"boolean";
ORYX.CONFIG.TYPE_INTEGER =				"integer";
ORYX.CONFIG.TYPE_FLOAT =				"float";
ORYX.CONFIG.TYPE_COLOR =				"color";
ORYX.CONFIG.TYPE_DATE =					"date";
ORYX.CONFIG.TYPE_CHOICE =				"choice";
ORYX.CONFIG.TYPE_URL =					"url";
ORYX.CONFIG.TYPE_DIAGRAM_LINK =			"diagramlink";
ORYX.CONFIG.TYPE_COMPLEX =				"complex";
ORYX.CONFIG.TYPE_TEXT =					"text";
ORYX.CONFIG.TYPE_VARDEF =               "vardef";
ORYX.CONFIG.TYPE_EXPRESSION =           "expression";
ORYX.CONFIG.TYPE_ACTION =               "action";
ORYX.CONFIG.TYPE_GLOBAL =               "global";
ORYX.CONFIG.TYPE_IMPORT =               "import";
ORYX.CONFIG.TYPE_DATAINPUT =            "datainput";
ORYX.CONFIG.TYPE_DATAOUTPUT =           "dataoutput";
ORYX.CONFIG.TYPE_DATAINPUT_SINGLE =     "datainputsingle";
ORYX.CONFIG.TYPE_DATAOUTPUT_SINGLE =    "dataoutputsingle";
ORYX.CONFIG.TYPE_DATAASSIGNMENT =       "dataassignment";
ORYX.CONFIG.TYPE_CALLEDELEMENT  =       "calledelement";
ORYX.CONFIG.TYPE_CUSTOM =               "custom";
	
/* Vertical line distance of multiline labels */
ORYX.CONFIG.LABEL_LINE_DISTANCE =		2;
ORYX.CONFIG.LABEL_DEFAULT_LINE_HEIGHT =	12;

ORYX.CONFIG.ENABLE_MORPHMENU_BY_HOVER = true;

	/* Editor constants come here */
ORYX.CONFIG.EDITOR_ALIGN_BOTTOM =		0x01;
ORYX.CONFIG.EDITOR_ALIGN_MIDDLE =		0x02;
ORYX.CONFIG.EDITOR_ALIGN_TOP =			0x04;
ORYX.CONFIG.EDITOR_ALIGN_LEFT =			0x08;
ORYX.CONFIG.EDITOR_ALIGN_CENTER =		0x10;
ORYX.CONFIG.EDITOR_ALIGN_RIGHT =		0x20;
ORYX.CONFIG.EDITOR_ALIGN_SIZE =			0x30;

	/* Event types */
ORYX.CONFIG.EVENT_MOUSEDOWN =			"mousedown";
ORYX.CONFIG.EVENT_MOUSEUP =				"mouseup";
ORYX.CONFIG.EVENT_MOUSEOVER =			"mouseover";
ORYX.CONFIG.EVENT_MOUSEOUT =			"mouseout";
ORYX.CONFIG.EVENT_MOUSEMOVE =			"mousemove";
ORYX.CONFIG.EVENT_DBLCLICK =			"dblclick";
ORYX.CONFIG.EVENT_CLICK =			    "click";
ORYX.CONFIG.EVENT_KEYDOWN =				"keydown";
ORYX.CONFIG.EVENT_KEYUP =				"keyup";

ORYX.CONFIG.EVENT_LOADED =				"editorloaded";
	
ORYX.CONFIG.EVENT_EXECUTE_COMMANDS =		"executeCommands";
ORYX.CONFIG.EVENT_STENCIL_SET_LOADED =		"stencilSetLoaded";
ORYX.CONFIG.EVENT_SELECTION_CHANGED =		"selectionchanged";
ORYX.CONFIG.EVENT_SHAPEADDED =				"shapeadded";
ORYX.CONFIG.EVENT_PROPERTY_CHANGED =		"propertyChanged";
ORYX.CONFIG.EVENT_DRAGDROP_START =			"dragdrop.start";
ORYX.CONFIG.EVENT_SHAPE_MENU_CLOSE =		"shape.menu.close";
ORYX.CONFIG.EVENT_DRAGDROP_END =			"dragdrop.end";
ORYX.CONFIG.EVENT_RESIZE_START =			"resize.start";
ORYX.CONFIG.EVENT_RESIZE_END =				"resize.end";
ORYX.CONFIG.EVENT_DRAGDOCKER_DOCKED =		"dragDocker.docked";
ORYX.CONFIG.EVENT_HIGHLIGHT_SHOW =			"highlight.showHighlight";
ORYX.CONFIG.EVENT_HIGHLIGHT_HIDE =			"highlight.hideHighlight";
ORYX.CONFIG.EVENT_LOADING_ENABLE =			"loading.enable";
ORYX.CONFIG.EVENT_LOADING_DISABLE =			"loading.disable";
ORYX.CONFIG.EVENT_LOADING_STATUS =			"loading.status";
ORYX.CONFIG.EVENT_OVERLAY_SHOW =			"overlay.show";
ORYX.CONFIG.EVENT_OVERLAY_HIDE =			"overlay.hide";
ORYX.CONFIG.EVENT_DICTIONARY_ADD =          "dictionary.add";
ORYX.CONFIG.EVENT_TASKFORM_EDIT =           "taskform.edit";
ORYX.CONFIG.EVENT_ARRANGEMENT_TOP =			"arrangement.setToTop";
ORYX.CONFIG.EVENT_ARRANGEMENT_BACK =		"arrangement.setToBack";
ORYX.CONFIG.EVENT_ARRANGEMENT_FORWARD =		"arrangement.setForward";
ORYX.CONFIG.EVENT_ARRANGEMENT_BACKWARD =	"arrangement.setBackward";
ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED =	"propertyWindow.propertyChanged";
ORYX.CONFIG.EVENT_LAYOUT_ROWS =				"layout.rows";
ORYX.CONFIG.EVENT_LAYOUT_BPEL =				"layout.BPEL";
ORYX.CONFIG.EVENT_LAYOUT_BPEL_VERTICAL =    "layout.BPEL.vertical";
ORYX.CONFIG.EVENT_LAYOUT_BPEL_HORIZONTAL =  "layout.BPEL.horizontal";
ORYX.CONFIG.EVENT_LAYOUT_BPEL_SINGLECHILD = "layout.BPEL.singlechild";
ORYX.CONFIG.EVENT_LAYOUT_BPEL_AUTORESIZE =	"layout.BPEL.autoresize";
ORYX.CONFIG.EVENT_AUTOLAYOUT_LAYOUT =		"autolayout.layout";
ORYX.CONFIG.EVENT_UNDO_EXECUTE =			"undo.execute";
ORYX.CONFIG.EVENT_UNDO_ROLLBACK =			"undo.rollback";
ORYX.CONFIG.EVENT_BUTTON_UPDATE =           "toolbar.button.update";
ORYX.CONFIG.EVENT_LAYOUT = 					"layout.dolayout";
ORYX.CONFIG.EVENT_COLOR_CHANGE = 			"color.change";
ORYX.CONFIG.EVENT_DOCKERDRAG = 				"dragTheDocker";	
ORYX.CONFIG.EVENT_SHOW_PROPERTYWINDOW =		"propertywindow.show";
ORYX.CONFIG.EVENT_DRAG_TRACKER_DRAG =       "dragTracker.drag";
ORYX.CONFIG.EVENT_DRAG_TRACKER_RESIZE =     "dragTracker.resize";
ORYX.CONFIG.EVENT_DROP_SHAPE =				"drop.shape";
ORYX.CONFIG.EVENT_SHAPE_DELETED =				"shape.deleted";
ORYX.CONFIG.EVENT_FACADE_SELECTION_DELETION_REQUEST =				"facade_selection.deletion.request";
ORYX.CONFIG.EVENT_NODEXML_SHOW = "nodexml.show";
ORYX.CONFIG.EVENT_VOICE_COMMAND = "voice.command";

// voice commands
ORYX.CONFIG.VOICE_COMMAND_GENERATE_FORMS = "voice.command.generate.forms";
ORYX.CONFIG.VOICE_COMMAND_VALIDATE = "voice.command.validate";
ORYX.CONFIG.VOICE_COMMAND_GENERATE_IMAGE = "voice.command.generate.image";
ORYX.CONFIG.VOICE_COMMAND_VIEW_SOURCE = "voice.command.view.source";
ORYX.CONFIG.VOICE_COMMAND_ADD_TASK = "voice.command.add.task";
ORYX.CONFIG.VOICE_COMMAND_ADD_GATEWAY = "voice.command.add.gateway";
ORYX.CONFIG.VOICE_COMMAND_ADD_START_EVENT = "voice.command.add.start.event";
ORYX.CONFIG.VOICE_COMMAND_ADD_END_EVENT = "voice.command.add.end.event";
ORYX.CONFIG.VOICE_COMMAND_TASK_TYPE_USER = "voice.command.task.type.user";
ORYX.CONFIG.VOICE_COMMAND_TASK_TYPE_SCRIPT = "voice.command.task.type.script";
ORYX.CONFIG.VOICE_COMMAND_GATEWAY_TYPE_PARALLEL = "voice.command.gateway.type.parallel"; 

// voice entries
ORYX.CONFIG.VOICE_ENTRY_GENERATE_FORMS = "create forms";
ORYX.CONFIG.VOICE_ENTRY_VALIDATE = "validate";
ORYX.CONFIG.VOICE_ENTRY_GENERATE_IMAGE = "create image";
ORYX.CONFIG.VOICE_ENTRY_VIEW_SOURCE = "show bpmn";
ORYX.CONFIG.VOICE_ENTRY_ADD_TASK = "task,test,text,that,map,10,chat,pet";
ORYX.CONFIG.VOICE_ENTRY_ADD_GATEWAY = "gateway";
ORYX.CONFIG.VOICE_ENTRY_ADD_START_EVENT = "start,bart,dark";
ORYX.CONFIG.VOICE_ENTRY_ADD_END_EVENT = "end,and";
ORYX.CONFIG.VOICE_ENTRY_TASK_TYPE_USER = "user,used";
ORYX.CONFIG.VOICE_ENTRY_TASK_TYPE_SCRIPT = "script,strip,red";
ORYX.CONFIG.VOICE_ENTRY_GATEWAY_TYPE_PARALLEL = "parallel";
	
	/* Selection Shapes Highlights */
ORYX.CONFIG.SELECTION_HIGHLIGHT_SIZE =				5;
ORYX.CONFIG.SELECTION_HIGHLIGHT_COLOR =				"#4444FF";
ORYX.CONFIG.SELECTION_HIGHLIGHT_COLOR2 =			"#9999FF";
	
ORYX.CONFIG.SELECTION_HIGHLIGHT_STYLE_CORNER = 		"corner";
ORYX.CONFIG.SELECTION_HIGHLIGHT_STYLE_RECTANGLE = 	"rectangle";
	
ORYX.CONFIG.SELECTION_VALID_COLOR =					"#00FF00";
ORYX.CONFIG.SELECTION_INVALID_COLOR =				"#FF0000";


ORYX.CONFIG.DOCKER_DOCKED_COLOR =		"#00FF00";
ORYX.CONFIG.DOCKER_UNDOCKED_COLOR =		"#FF0000";
ORYX.CONFIG.DOCKER_SNAP_OFFSET =		10;
		
	/* Copy & Paste */
ORYX.CONFIG.EDIT_OFFSET_PASTE =			10;


	/* Key-Codes */
ORYX.CONFIG.KEY_CODE_X = 				88;
ORYX.CONFIG.KEY_CODE_C = 				67;
ORYX.CONFIG.KEY_CODE_V = 				86;
ORYX.CONFIG.KEY_CODE_DELETE = 			46;
ORYX.CONFIG.KEY_CODE_META =				224;
ORYX.CONFIG.KEY_CODE_BACKSPACE =		8;
ORYX.CONFIG.KEY_CODE_LEFT =				37;
ORYX.CONFIG.KEY_CODE_RIGHT =			39;
ORYX.CONFIG.KEY_CODE_UP =				38;
ORYX.CONFIG.KEY_CODE_DOWN =				40;

	// TODO Determine where the lowercase constants are still used and remove them from here.
ORYX.CONFIG.KEY_Code_enter =			12;
ORYX.CONFIG.KEY_Code_left =				37;
ORYX.CONFIG.KEY_Code_right =			39;
ORYX.CONFIG.KEY_Code_top =				38;
ORYX.CONFIG.KEY_Code_bottom =			40;

/* Supported Meta Keys */
	
ORYX.CONFIG.META_KEY_META_CTRL = 		"metactrl";
ORYX.CONFIG.META_KEY_ALT = 				"alt";
ORYX.CONFIG.META_KEY_SHIFT = 			"shift";

/* Key Actions */

ORYX.CONFIG.KEY_ACTION_DOWN = 			"down";
ORYX.CONFIG.KEY_ACTION_UP = 			"up";

ORYX.CONFIG.PANEL_RIGHT_COLLAPSED = true;
ORYX.CONFIG.PANEL_LEFT_COLLAPSED = true;

ORYX.CONFIG.STENCIL_MAX_ORDER = 999;
ORYX.CONFIG.STENCIL_GROUP_ORDER = function() {
	var stencilsHash = {};
	// jbpm full perspective
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Activities"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Artifacts"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Catching Intermediate Events"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Connecting Objects"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Data Objects"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["End Events"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Gateways"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Service Tasks"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Start Events"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Swimlanes"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Throwing Intermediate Events"] = {};
	
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Start Events"] = 1;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Catching Intermediate Events"] = 2;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Throwing Intermediate Events"] = 3;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["End Events"] = 4;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Gateways"] = 5;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Activities"] = 6;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Service Tasks"] = 7;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Connecting Objects"] = 8;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Data Objects"] = 9;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Swimlanes"] = 10;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Artifacts"] = 11;
	
	
	// jbpm minimal perspective
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Task"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Reusable Subprocess"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Multiple instances"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Embedded Subprocess"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Data-based Exclusive (XOR) Gateway"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Start Event"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Timer Intermediate Event"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Signal Intermediate Event"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["End Event"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Error End Event"] = {};
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Sequence Flow"] = {};
	
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Task"] = 7;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Reusable Subprocess"] = 8;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Multiple instances"] = 9;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Embedded Subprocess"] = 10;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Data-based Exclusive (XOR) Gateway"] = 6;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Start Event"] = 1;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Timer Intermediate Event"] = 2;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Signal Intermediate Event"] = 3;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["End Event"] = 4;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Error End Event"] = 5;
	stencilsHash["http://b3mn.org/stencilset/bpmn2.0#"]["Sequence Flow"] = 11;
	return stencilsHash;
};

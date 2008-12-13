/**
 * @author nicolas.peters
 * 
 * Contains all strings for the default language (en-us).
 * Version 1 - 08/29/08
 */
if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "en_us"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.pleaseWait = "Please wait while Oryx is loading...";

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = "Docker";
ORYX.I18N.AddDocker.add = "Add Docker";
ORYX.I18N.AddDocker.addDesc = "Add a Docker to an edge, by clicking on it";
ORYX.I18N.AddDocker.del = "Delete Docker";
ORYX.I18N.AddDocker.delDesc = "Delete a Docker";

if(!ORYX.I18N.SSExtensionLoader) ORYX.I18N.SSExtensionLoader = {};

ORYX.I18N.SSExtensionLoader.group = "Stencil Set";
ORYX.I18N.SSExtensionLoader.add = "Add Stencil Set Extension";
ORYX.I18N.SSExtensionLoader.addDesc = "Add a stencil set extension";
ORYX.I18N.SSExtensionLoader.loading = "Loading Stencil Set Extension";
ORYX.I18N.SSExtensionLoader.noExt = "There are no extensions available or all available extensions are already loaded.";
ORYX.I18N.SSExtensionLoader.failed1 = "Loading stencil set extensions configuration failed. The response is not a valid configuration file.";
ORYX.I18N.SSExtensionLoader.failed2 = "Loading stencil set extension configuration file failed. The request returned an error.";
ORYX.I18N.SSExtensionLoader.panelTitle = "Stencil Set Extensions";
ORYX.I18N.SSExtensionLoader.panelText = "Select the stencil set extensions you want to load.";

if(!ORYX.I18N.AdHocCC) ORYX.I18N.AdHocCC = {};

ORYX.I18N.AdHocCC.group = "Ad Hoc";
ORYX.I18N.AdHocCC.compl = "Edit Completion Condition";
ORYX.I18N.AdHocCC.complDesc = "Edit an Ad Hoc Activity's Completion Condition";
ORYX.I18N.AdHocCC.notOne = "Not exactly one element selected!";
ORYX.I18N.AdHocCC.nodAdHocCC = "Selected element has no ad hoc completion condition!";
ORYX.I18N.AdHocCC.selectTask = "Select a task...";
ORYX.I18N.AdHocCC.selectState = "Select a state...";
ORYX.I18N.AdHocCC.addExp = "Add Expression";
ORYX.I18N.AdHocCC.selectDataField = "Select a data field...";
ORYX.I18N.AdHocCC.enterEqual = "Enter a value that must equal...";
ORYX.I18N.AdHocCC.and = "and";
ORYX.I18N.AdHocCC.or = "or";
ORYX.I18N.AdHocCC.not = "not";
ORYX.I18N.AdHocCC.clearCC = "Clear Completion Condition";
ORYX.I18N.AdHocCC.editCC = "Edit Ad-Hoc Completion Condtions";
ORYX.I18N.AdHocCC.addExecState = "Add Execution State Expression: ";
ORYX.I18N.AdHocCC.addDataExp = "Add Data Expression: ";
ORYX.I18N.AdHocCC.addLogOp = "Add Logical Operators: ";
ORYX.I18N.AdHocCC.curCond = "Current Completion Condition: ";

if(!ORYX.I18N.AMLSupport) ORYX.I18N.AMLSupport = {};

ORYX.I18N.AMLSupport.group = "EPC";
ORYX.I18N.AMLSupport.imp = "Import AML file";
ORYX.I18N.AMLSupport.impDesc = "Import an Aris 7 AML file";
ORYX.I18N.AMLSupport.failed = "Importing AML file failed. Please check, if the selected file is a valid AML file. Error message: ";
ORYX.I18N.AMLSupport.failed2 = "Importing AML file failed: ";
ORYX.I18N.AMLSupport.noRights = "You have no rights to import multiple EPC-Diagrams (Login required).";
ORYX.I18N.AMLSupport.panelText = "Select an AML (.xml) file to import.";
ORYX.I18N.AMLSupport.file = "File";
ORYX.I18N.AMLSupport.importBtn = "Import AML-File";
ORYX.I18N.AMLSupport.get = "Get diagrams...";
ORYX.I18N.AMLSupport.close = "Close";
ORYX.I18N.AMLSupport.title = "Title";
ORYX.I18N.AMLSupport.selectDiagrams = "Select the diagram(s) you want to import. <br/> If one model is selected, it will be imported in the current editor, if more than one is selected, those models will directly be stored in the repository.";
ORYX.I18N.AMLSupport.impText = "Import";
ORYX.I18N.AMLSupport.impProgress = "Importing...";
ORYX.I18N.AMLSupport.cancel = "Cancel";
ORYX.I18N.AMLSupport.name = "Name";
ORYX.I18N.AMLSupport.allImported = "All imported diagrams.";
ORYX.I18N.AMLSupport.ok = "Ok";

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};

ORYX.I18N.Arrangement.groupZ = "Z-Order";
ORYX.I18N.Arrangement.btf = "Bring To Front";
ORYX.I18N.Arrangement.btfDesc = "Bring to Front";
ORYX.I18N.Arrangement.btb = "Bring To Back";
ORYX.I18N.Arrangement.btbDesc = "Bring To Back";
ORYX.I18N.Arrangement.bf = "Bring Forward";
ORYX.I18N.Arrangement.bfDesc = "Bring Forward";
ORYX.I18N.Arrangement.bb = "Bring Backward";
ORYX.I18N.Arrangement.bbDesc = "Bring Backward";
ORYX.I18N.Arrangement.groupA = "Alignment";
ORYX.I18N.Arrangement.ab = "Alignment Bottom";
ORYX.I18N.Arrangement.abDesc = "Bottom";
ORYX.I18N.Arrangement.am = "Alignment Middle";
ORYX.I18N.Arrangement.amDesc = "Middle";
ORYX.I18N.Arrangement.at = "Alignment Top";
ORYX.I18N.Arrangement.atDesc = "Top";
ORYX.I18N.Arrangement.al = "Alignment Left";
ORYX.I18N.Arrangement.alDesc = "Left";
ORYX.I18N.Arrangement.ac = "Alignment Center";
ORYX.I18N.Arrangement.acDesc = "Center";
ORYX.I18N.Arrangement.ar = "Alignment Right";
ORYX.I18N.Arrangement.arDesc = "Right";

if(!ORYX.I18N.BPELSupport) ORYX.I18N.BPELSupport = {};

ORYX.I18N.BPELSupport.group = "BPEL";
ORYX.I18N.BPELSupport.exp = "Export BPEL";
ORYX.I18N.BPELSupport.expDesc = "Export diagram to BPEL";
ORYX.I18N.BPELSupport.imp = "Import BPEL";
ORYX.I18N.BPELSupport.impDesc = "Import a BPEL file";
ORYX.I18N.BPELSupport.selectFile = "Select a BPEL file to import";
ORYX.I18N.BPELSupport.file = "file";
ORYX.I18N.BPELSupport.impPanel = "Import BPEL file";
ORYX.I18N.BPELSupport.impBtn = "Import";
ORYX.I18N.BPELSupport.content = "content";
ORYX.I18N.BPELSupport.close = "Close";
ORYX.I18N.BPELSupport.error = "Error";
ORYX.I18N.BPELSupport.progressImp = "Import...";
ORYX.I18N.BPELSupport.progressExp = "Export...";
ORYX.I18N.BPELSupport.impFailed = "An error while importing occurs! <br/>Please check error message: <br/><br/>";

if(!ORYX.I18N.BPELLayout) ORYX.I18N.BPELLayout = {};

ORYX.I18N.BPELLayout.group = "BPELLayout";
ORYX.I18N.BPELLayout.disable = "disable layout";
ORYX.I18N.BPELLayout.disDesc = "disable auto layout plug-in";
ORYX.I18N.BPELLayout.enable = "enable layout";
ORYX.I18N.BPELLayout.enDesc = "enable auto layout plug-in";

if(!ORYX.I18N.BPEL4ChorSupport) ORYX.I18N.BPEL4ChorSupport = {};

ORYX.I18N.BPEL4ChorSupport.group = "BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.exp = "Export BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.expDesc = "Export diagram to BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.imp = "Import BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.impDesc = "Import a BPEL4Chor file";
ORYX.I18N.BPEL4ChorSupport.selectFile = "Select a BPEL4Chor file to import";
ORYX.I18N.BPEL4ChorSupport.file = "file";
ORYX.I18N.BPEL4ChorSupport.impPanel = "Import BPEL4Chor file";
ORYX.I18N.BPEL4ChorSupport.impBtn = "Import";
ORYX.I18N.BPEL4ChorSupport.content = "content";
ORYX.I18N.BPEL4ChorSupport.close = "Close";
ORYX.I18N.BPEL4ChorSupport.error = "Error";
ORYX.I18N.BPEL4ChorSupport.progressImp = "Import...";
ORYX.I18N.BPEL4ChorSupport.progressExp = "Export...";
ORYX.I18N.BPEL4ChorSupport.impFailed = "An error while importing occurs! <br/>Please check error message: <br/><br/>";

if(!ORYX.I18N.Bpel4ChorTransformation) ORYX.I18N.Bpel4ChorTransformation = {};

ORYX.I18N.Bpel4ChorTransformation.group = "BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportBPEL = "Export BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportBPELDesc = "Export diagram to BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportXPDL = "Export XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportXPDLDesc = "Export diagram to XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.warning = "Warning";
ORYX.I18N.Bpel4ChorTransformation.wrongValue = 'The changed name must have the value "1" to avoid errors during the transformation to BPEL4Chor';
ORYX.I18N.Bpel4ChorTransformation.loopNone = 'The loop type of the receive task must be "None" to be transformable to BPEL4Chor';
ORYX.I18N.Bpel4ChorTransformation.error = "Error";
ORYX.I18N.Bpel4ChorTransformation.noSource = "1 with id 2 has no source object.";
ORYX.I18N.Bpel4ChorTransformation.noTarget = "1 with id 2 has no target object.";
ORYX.I18N.Bpel4ChorTransformation.transCall = "An error occured during the transformation call. 1:2";
ORYX.I18N.Bpel4ChorTransformation.loadingXPDL4ChorExport = "Export to XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.loadingBPEL4ChorExport = "Export to BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.noGen = "The transformation input could not be generated: 1\n2\n";

if(!ORYX.I18N.TransformationDownloadDialog) ORYX.I18N.TransformationDownloadDialog = {};

ORYX.I18N.TransformationDownloadDialog.error = "Error";
ORYX.I18N.TransformationDownloadDialog.noResult = "The transformation web service did not return a result.";
ORYX.I18N.TransformationDownloadDialog.errorParsing = "Error During the Parsing of the Diagram.";
ORYX.I18N.TransformationDownloadDialog.transResult = "Transformation Results";
ORYX.I18N.TransformationDownloadDialog.showFile = "Show the result file";
ORYX.I18N.TransformationDownloadDialog.downloadFile = "Download the result file";
ORYX.I18N.TransformationDownloadDialog.downloadAll = "Download all result files";

if(!ORYX.I18N.DesynchronizabilityOverlay) ORYX.I18N.DesynchronizabilityOverlay = {};
//TODO desynchronizability is not a correct term
ORYX.I18N.DesynchronizabilityOverlay.group = "Overlay";
ORYX.I18N.DesynchronizabilityOverlay.name = "Desynchronizability Checker";
ORYX.I18N.DesynchronizabilityOverlay.desc = "Desynchronizability Checker";
ORYX.I18N.DesynchronizabilityOverlay.sync = "The net is desynchronizable.";
ORYX.I18N.DesynchronizabilityOverlay.error = "The net has 1 syntax errors.";
ORYX.I18N.DesynchronizabilityOverlay.invalid = "Invalid answer from server.";

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

ORYX.I18N.Edit.group = "Edit";
ORYX.I18N.Edit.cut = "Cut";
ORYX.I18N.Edit.cutDesc = "Cuts the selection into an Oryx clipboard";
ORYX.I18N.Edit.copy = "Copy";
ORYX.I18N.Edit.copyDesc = "Copies the selection into an Oryx clipboard";
ORYX.I18N.Edit.paste = "Paste";
ORYX.I18N.Edit.pasteDesc = "Pastes the Oryx clipboard to the canvas";
ORYX.I18N.Edit.del = "Delete";
ORYX.I18N.Edit.delDesc = "Deletes all selected shapes";

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

ORYX.I18N.EPCSupport.group = "EPC";
ORYX.I18N.EPCSupport.exp = "Export EPC";
ORYX.I18N.EPCSupport.expDesc = "Export diagram to EPML";
ORYX.I18N.EPCSupport.imp = "Import EPC";
ORYX.I18N.EPCSupport.impDesc = "Import an EPML file";
ORYX.I18N.EPCSupport.progressExp = "Exporting model";
ORYX.I18N.EPCSupport.selectFile = "Select an EPML (.empl) file to import.";
ORYX.I18N.EPCSupport.file = "File";
ORYX.I18N.EPCSupport.impPanel = "Import EPML File";
ORYX.I18N.EPCSupport.impBtn = "Import";
ORYX.I18N.EPCSupport.close = "Close";
ORYX.I18N.EPCSupport.error = "Error";
ORYX.I18N.EPCSupport.progressImp = "Import...";

if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.group = "ERDF";
ORYX.I18N.ERDFSupport.exp = "Export to ERDF";
ORYX.I18N.ERDFSupport.expDesc = "Export to ERDF";
ORYX.I18N.ERDFSupport.imp = "Import from ERDF";
ORYX.I18N.ERDFSupport.impDesc = "Import from ERDF";
ORYX.I18N.ERDFSupport.impFailed = "Request for import of ERDF failed.";
ORYX.I18N.ERDFSupport.impFailed2 = "An error while importing occurs! <br/>Please check error message: <br/><br/>";
ORYX.I18N.ERDFSupport.error = "Error";
ORYX.I18N.ERDFSupport.noCanvas = "The xml document has no Oryx canvas node included!";
ORYX.I18N.ERDFSupport.noSS = "The Oryx canvas node has no stencil set definition included!";
ORYX.I18N.ERDFSupport.wrongSS = "The given stencil set does not fit to the current editor!";
ORYX.I18N.ERDFSupport.selectFile = "Select an ERDF (.xml) file or type in the ERDF to import it!";
ORYX.I18N.ERDFSupport.file = "File";
ORYX.I18N.ERDFSupport.impERDF = "Import ERDF";
ORYX.I18N.ERDFSupport.impBtn = "Import";
ORYX.I18N.ERDFSupport.impProgress = "Importing...";
ORYX.I18N.ERDFSupport.close = "Close";

if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

ORYX.I18N.Save.group = "File";
ORYX.I18N.Save.save = "Save";
ORYX.I18N.Save.saveDesc = "Save";
ORYX.I18N.Save.saveAs = "Save As...";
ORYX.I18N.Save.saveAsDesc = "Save As...";
ORYX.I18N.Save.unsavedData = "There are unsaved data, please save before you leave, otherwise your changes get lost!";
ORYX.I18N.Save.newProcess = "New Process";
ORYX.I18N.Save.saveAsTitle = "Save as...";
ORYX.I18N.Save.saveBtn = "Save";
ORYX.I18N.Save.close = "Close";
ORYX.I18N.Save.savedAs = "Saved As";
ORYX.I18N.Save.saved = "Saved!";
ORYX.I18N.Save.failed = "Saving failed.";
ORYX.I18N.Save.noRights = "You have no rights to save changes.";
ORYX.I18N.Save.saving = "Saving";

if(!ORYX.I18N.File) ORYX.I18N.File = {};

ORYX.I18N.File.group = "File";
ORYX.I18N.File.print = "Print";
ORYX.I18N.File.printDesc = "Print current model";
ORYX.I18N.File.pdf = "Export as PDF";
ORYX.I18N.File.pdfDesc = "Export as PDF";
ORYX.I18N.File.info = "Info";
ORYX.I18N.File.infoDesc = "Info";
ORYX.I18N.File.genPDF = "Generating PDF";
ORYX.I18N.File.genPDFFailed = "Generating PDF failed.";
ORYX.I18N.File.printTitle = "Print";
ORYX.I18N.File.printMsg = "We are currently experiencing problems with the printing function. We recommend using the PDF Export to print the diagram. Do you really want to continue printing?";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "Grouping";
ORYX.I18N.Grouping.group = "Group";
ORYX.I18N.Grouping.groupDesc = "Groups all selected shapes";
ORYX.I18N.Grouping.ungroup = "Ungroup";
ORYX.I18N.Grouping.ungroupDesc = "Deletes the group of all selected Shapes";

if(!ORYX.I18N.IBPMN2BPMN) ORYX.I18N.IBPMN2BPMN = {};

ORYX.I18N.IBPMN2BPMN.group ="Export";
ORYX.I18N.IBPMN2BPMN.name ="IBPMN 2 BPMN Mapping";
ORYX.I18N.IBPMN2BPMN.desc ="Convert IBPMN to BPMN";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="Please wait...";

if(!ORYX.I18N.Pnmlexport) ORYX.I18N.Pnmlexport = {};

ORYX.I18N.Pnmlexport.group ="Export";
ORYX.I18N.Pnmlexport.name ="BPMN to PNML";
ORYX.I18N.Pnmlexport.desc ="Export as executable PNML and deploy";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "Name";
ORYX.I18N.PropertyWindow.value = "Value";
ORYX.I18N.PropertyWindow.clickIcon = "Click Icon";
ORYX.I18N.PropertyWindow.add = "Add";
ORYX.I18N.PropertyWindow.rem = "Remove";
ORYX.I18N.PropertyWindow.complex = "Editor for a Complex Type";
ORYX.I18N.PropertyWindow.ok = "Ok";
ORYX.I18N.PropertyWindow.cancel = "Cancel";
ORYX.I18N.PropertyWindow.dateFormat = "m/d/y";

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = "Drag";
ORYX.I18N.ShapeMenuPlugin.clickDrag = "Click or drag";

if(!ORYX.I18N.SimplePnmlexport) ORYX.I18N.SimplePnmlexport = {};

ORYX.I18N.SimplePnmlexport.group = "Export";
ORYX.I18N.SimplePnmlexport.name = "Simple BPMN to PNML Export";
ORYX.I18N.SimplePnmlexport.desc = "Export as PNML";

if(!ORYX.I18N.StepThroughPlugin) ORYX.I18N.StepThroughPlugin = {};

ORYX.I18N.StepThroughPlugin.group = "Step Through";
ORYX.I18N.StepThroughPlugin.stepThrough = "Step Through";
ORYX.I18N.StepThroughPlugin.stepThroughDesc = "Step through your BPMN model";
ORYX.I18N.StepThroughPlugin.undo = "Undo";
ORYX.I18N.StepThroughPlugin.undoDesc = "Undo one Step";
ORYX.I18N.StepThroughPlugin.error = "Can't step through this diagram.";
ORYX.I18N.StepThroughPlugin.executing = "Executing";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = "Verification";
ORYX.I18N.SyntaxChecker.name = "Syntax Checker";
ORYX.I18N.SyntaxChecker.desc = "Check Syntax";
ORYX.I18N.SyntaxChecker.noErrors = "There are no syntax errors.";
ORYX.I18N.SyntaxChecker.invalid = "Invalid answer from server.";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = "Undo";
ORYX.I18N.Undo.undo = "Undo";
ORYX.I18N.Undo.undoDesc = "Undo the last action";
ORYX.I18N.Undo.redo = "Redo";
ORYX.I18N.Undo.redoDesc = "Redo the last undone action";

if(!ORYX.I18N.Validator) ORYX.I18N.Validator = {};
ORYX.I18N.Validator.checking = "Checking";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = "Zoom";
ORYX.I18N.View.zoomIn = "Zoom In";
ORYX.I18N.View.zoomInDesc = "Zoom into the model";
ORYX.I18N.View.zoomOut = "Zoom Out";
ORYX.I18N.View.zoomOutDesc = "Zoom out of the model";


if(!ORYX.I18N.XFormsSerialization) ORYX.I18N.XFormsSerialization = {};

ORYX.I18N.XFormsSerialization.group = "XForms Serialization";
ORYX.I18N.XFormsSerialization.exportXForms = "XForms Export";
ORYX.I18N.XFormsSerialization.exportXFormsDesc = "Export XForms+XHTML markup";
ORYX.I18N.XFormsSerialization.importXForms = "XForms Import";
ORYX.I18N.XFormsSerialization.importXFormsDesc = "Import XForms+XHTML markup";
ORYX.I18N.XFormsSerialization.noClientXFormsSupport = "No XForms support";
ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc = "<h2>Your browser does not support XForms. Please install the <a href=\"https://addons.mozilla.org/firefox/addon/824\" target=\"_blank\">Mozilla XForms Add-on</a> for Firefox.</h2>";
ORYX.I18N.XFormsSerialization.ok = "Ok";
ORYX.I18N.XFormsSerialization.selectFile = "Select a XHTML (.xhtml) file or type in the XForms+XHTML markup to import it!";
ORYX.I18N.XFormsSerialization.file = "File";
ORYX.I18N.XFormsSerialization.impFailed = "Request for import of document failed.";
ORYX.I18N.XFormsSerialization.impTitl = "Import XForms+XHTML document";
ORYX.I18N.XFormsSerialization.impButton = "Import";
ORYX.I18N.XFormsSerialization.impProgress = "Importing...";
ORYX.I18N.XFormsSerialization.close = "Close";


if(!ORYX.I18N.TreeGraphSupport) ORYX.I18N.TreeGraphSupport = {};

ORYX.I18N.TreeGraphSupport.syntaxCheckName = "Syntax Check";
ORYX.I18N.TreeGraphSupport.group = "Tree Graph Support";
ORYX.I18N.TreeGraphSupport.syntaxCheckDesc = "Check the syntax of an tree graph structure";



/** New Language Properties: 08.12.2008 **/

ORYX.I18N.PropertyWindow.title = "Properties";

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = "Shape Repository";

ORYX.I18N.Save.dialogDesciption = "Please enter a name and a description.";
ORYX.I18N.Save.dialogLabelTitle = "Title";
ORYX.I18N.Save.dialogLabelDesc = "Description";
ORYX.I18N.Save.dialogLabelType = "Type";

ORYX.I18N.Validator.name = "BPMN Validator";
ORYX.I18N.Validator.description = "Validation for BPMN";

ORYX.I18N.SSExtensionLoader.labelImport = "Import";
ORYX.I18N.SSExtensionLoader.labelCancel = "Cancel";

Ext.MessageBox.buttonText.yes = "Yes";
Ext.MessageBox.buttonText.no = "No";
Ext.MessageBox.buttonText.cancel = "Cancel";
Ext.MessageBox.buttonText.ok = "OK";

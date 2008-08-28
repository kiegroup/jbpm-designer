/**
 * @author nicolas.peters
 * 
 * Contains all strings for the default language (en-us).
 */
if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "en_us"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = "Docker";
ORYX.I18N.AddDocker.add = "Add Docker";
ORYX.I18N.AddDocker.addDesc = "Add a Docker to an edge, by clicking on it";
ORYX.I18N.AddDocker.del = "Delete Docker";
ORYX.I18N.AddDocker.delDesc = "Delete a Docker";

if(!ORYX.I18N.SSExtensionLoader) ORYX.I18N.SSExtensionLoader = {};

ORYX.I18N.SSExtensionLoader.group = "StencilSet";
ORYX.I18N.SSExtensionLoader.add = "Add Stencil Set Extension";
ORYX.I18N.SSExtensionLoader.addDesc = "Add a stencil set extension";
ORYX.I18N.SSExtensionLoader.loading = "Loading Stencil Set Extension";
ORYX.I18N.SSExtensionLoader.noExt = "There are no extensions available or all available extensions are already loaded.";
ORYX.I18N.SSExtensionLoader.failed1 = "Loading stencil set extensions configuration failed. The response is not a valid configuration file.";
ORYX.I18N.SSExtensionLoader.failed2 = "Loading stencil set extension configuration file failed. The request returned an error.";
ORYX.I18N.SSExtensionLoader.panelTitle = "StencilSet Extensions";
ORYX.I18N.SSExtensionLoader.panelText = "Select the stencil set extensions you want to load.";

if(!ORYX.I18N.AdHocCC) ORYX.I18N.AdHocCC = {};

ORYX.I18N.AdHocCC.group = "adhoc";
ORYX.I18N.AdHocCC.compl = "Edit Completion Condition";
ORYX.I18N.AdHocCC.complDesc = "Edit an Ad-Hoc Activity's Completion Condition";
ORYX.I18N.AdHocCC.notOne = "Not exactly one element selected!";
ORYX.I18N.AdHocCC.nodAdHocCC = "Selected element has no ad-hoc completion condition!";
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

ORYX.I18N.AMLSupport.group = "epc";
ORYX.I18N.AMLSupport.imp = "Import AML file";
ORYX.I18N.AMLSupport.impDesc = "Import an Aris 7 AML file";
ORYX.I18N.AMLSupport.failed = "Importing AML file failed. Please check, if the selected file is a valid AML file. Error message: ";
ORYX.I18N.AMLSupport.failed2 = "Importing AML file failed: ";
ORYX.I18N.AMLSupport.noRights = "You have no rights to import multiple EPC-Diagrams.";
ORYX.I18N.AMLSupport.panelText = "Select an AML (.xml) file to import it!";
ORYX.I18N.AMLSupport.file = "File";
ORYX.I18N.AMLSupport.importBtn = "Import AML-File";
ORYX.I18N.AMLSupport.get = "Get diagrams...";
ORYX.I18N.AMLSupport.close = "Close";
ORYX.I18N.AMLSupport.title = "Title";
ORYX.I18N.AMLSupport.selectDiagrams = "Select the diagram(s) you want to import! <br/> If one model is selected, it will be imported in the current editor, if more than one is selected, those models will directly be stored in the repository.";
ORYX.I18N.AMLSupport.impText = "Import";
ORYX.I18N.AMLSupport.impProgress = "Importing...";
ORYX.I18N.AMLSupport.cancel = "Cancel";
ORYX.I18N.AMLSupport.name = "Name";
ORYX.I18N.AMLSupport.allImported = "All imported diagrams!";
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

if(!ORYX.I18N.BpelGenerator) ORYX.I18N.BpelGenerator = {};

ORYX.I18N.BpelGenerator.group = "BPEL";
ORYX.I18N.BpelGenerator.name = "Export BPEL";
ORYX.I18N.BpelGenerator.desc = "Export BPEL to XML file";

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
ORYX.I18N.Bpel4ChorTransformation.noSource = "The 1 with id 2 has no source object.";
ORYX.I18N.Bpel4ChorTransformation.noTarget = "The 1 with id 2 has no target object.";
ORYX.I18N.Bpel4ChorTransformation.transCall = "An error occured during the transformation call. 1:2";
ORYX.I18N.Bpel4ChorTransformation.noResult = "The transformation web service did not return a result.";
ORYX.I18N.Bpel4ChorTransformation.errorParsing = "Error During the Parsing of the Diagram.";
ORYX.I18N.Bpel4ChorTransformation.transResult = "Transformation Results";
ORYX.I18N.Bpel4ChorTransformation.showFile = "Show the result file";
ORYX.I18N.Bpel4ChorTransformation.downloadFile = "Download the result file";
ORYX.I18N.Bpel4ChorTransformation.downloadAll = "Download all result files";
ORYX.I18N.Bpel4ChorTransformation.loadingExport = "Export to XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.noGen = "The transformation input could not be generated: 1\n2\n";

if(!ORYX.I18N.DesynchronizabilityOverlay) ORYX.I18N.DesynchronizabilityOverlay = {};

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
ORYX.I18N.EPCSupport.selectFile = "Select an EPML (.empl) file to import it!";
ORYX.I18N.EPCSupport.file = "File";
ORYX.I18N.EPCSupport.impPanel = "Import EPML File";
ORYX.I18N.EPCSupport.impBtn = "Import";
ORYX.I18N.EPCSupport.close = "Close";
ORYX.I18N.EPCSupport.error = "Error";
ORYX.I18N.EPCSupport.progressImp = "Import...";



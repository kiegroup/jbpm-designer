/**
 * @author nicolas.peters
 * 
 * Contains all strings for German language.
 * Version 1 - 08/29/08
 */
ORYX.I18N.Language = "de_DE"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = "Docker";
ORYX.I18N.AddDocker.add = "Docker Hinzufügen";
ORYX.I18N.AddDocker.addDesc = "Fügen Sie einer Kante einen Docker hinzu, indem Sie auf die Kante klicken";
ORYX.I18N.AddDocker.del = "Docker Löschen";
ORYX.I18N.AddDocker.delDesc = "Löscht einen Docker durch Klicken auf den zu löschenden Docker";

if(!ORYX.I18N.SSExtensionLoader) ORYX.I18N.SSExtensionLoader = {};

ORYX.I18N.SSExtensionLoader.group = "Stencil Set";
ORYX.I18N.SSExtensionLoader.add = "Stencil Set Erweiterungen hinzufügen";
ORYX.I18N.SSExtensionLoader.addDesc = "Stencil Set Erweiterungen hinzufügen";
ORYX.I18N.SSExtensionLoader.loading = "Stencil Set Erweiterungen wird geladen";
ORYX.I18N.SSExtensionLoader.noExt = "Es sind keine Erweiterungen verfügbar oder alle verfügbaren Erweiterungen wurden bereits geladen.";
ORYX.I18N.SSExtensionLoader.failed1 = "Das Laden der Konfigurationsdatei ist fehlgeschlagen. Die Antwort des Servers ist keine gültige Konfigurationsdatei.";
ORYX.I18N.SSExtensionLoader.failed2 = "Das Laden der Konfigurationsdatei ist fehlgeschlagen. Der Server hat mit einer Fehlermeldung geantwortet.";
ORYX.I18N.SSExtensionLoader.panelTitle = "Stencil Set Erweiterungen";
ORYX.I18N.SSExtensionLoader.panelText = "Wählen Sie die zu ladenden Stencil Set Erweiterungen aus.";

if(!ORYX.I18N.AdHocCC) ORYX.I18N.AdHocCC = {};

ORYX.I18N.AdHocCC.group = "Ad Hoc";
ORYX.I18N.AdHocCC.compl = "Endbedingung bearbeiten";
ORYX.I18N.AdHocCC.complDesc = "Endbedingung einer Ad Hoc Aktivität bearbeiten";
ORYX.I18N.AdHocCC.notOne = "Es ist nicht genau ein Element ausgewählt!";
ORYX.I18N.AdHocCC.nodAdHocCC = "Das ausgewählte Element hat keine Ad Hoc Endbedingung!";
ORYX.I18N.AdHocCC.selectTask = "Aktivität auswählen...";
ORYX.I18N.AdHocCC.selectState = "Zustand auswählen...";
ORYX.I18N.AdHocCC.addExp = "Ausdruck hinzufügen";
ORYX.I18N.AdHocCC.selectDataField = "Datenfeld auswählen...";
ORYX.I18N.AdHocCC.enterEqual = "Vergleichswert eingeben...";
ORYX.I18N.AdHocCC.and = "und";
ORYX.I18N.AdHocCC.or = "oder";
ORYX.I18N.AdHocCC.not = "nicht";
ORYX.I18N.AdHocCC.clearCC = "Endbedingung löschen";
ORYX.I18N.AdHocCC.editCC = "Ad Hoc Endbedingungen bearbeiten";
ORYX.I18N.AdHocCC.addExecState = "Ausführungszustandsausdruck hinzufügen: ";
ORYX.I18N.AdHocCC.addDataExp = "Datenausdruck hinzufügen: ";
ORYX.I18N.AdHocCC.addLogOp = "Logische Operatoren hinzufügen: ";
ORYX.I18N.AdHocCC.curCond = "Aktuelle Endbedingung: ";

if(!ORYX.I18N.AMLSupport) ORYX.I18N.AMLSupport = {};

ORYX.I18N.AMLSupport.group = "EPC";
ORYX.I18N.AMLSupport.imp = "AML Datei importieren";
ORYX.I18N.AMLSupport.impDesc = "Aris 7 AML Datei importieren";
ORYX.I18N.AMLSupport.failed = "Importieren der AML Datei ist fehlgeschlagen. Bitte vergewissern Sie sich, daß die ausgewählte Datei eine gültige AML Datei ist. Fehlermeldung: ";
ORYX.I18N.AMLSupport.failed2 = "Importieren der AML Datei ist fehlgeschlagen: ";
ORYX.I18N.AMLSupport.noRights = "Sie haben nicht die erforderlichen Rechte, um mehrere Diagramme zu importieren (Login erforderlich).";
ORYX.I18N.AMLSupport.panelText = "Wählen Sie eine AML Datei (.xml) aus, die Sie importieren möchten.";
ORYX.I18N.AMLSupport.file = "Datei";
ORYX.I18N.AMLSupport.importBtn = "AML Datei importieren";
ORYX.I18N.AMLSupport.get = "Lade Diagramme...";
ORYX.I18N.AMLSupport.close = "Schließen";
ORYX.I18N.AMLSupport.title = "Titel";
ORYX.I18N.AMLSupport.selectDiagrams = "Wählen Sie die Diagramme aus, die Sie importieren möchten. <br/> Wenn Sie nur ein Diagramm auswählen, wird dieses im geöffneten Editor importiert. Bei der Auswahl von mehreren Diagrammen werden diese direkt gespeichert.";
ORYX.I18N.AMLSupport.impText = "Importieren";
ORYX.I18N.AMLSupport.impProgress = "Importierung wird ausgeführt...";
ORYX.I18N.AMLSupport.cancel = "Abbrechen";
ORYX.I18N.AMLSupport.name = "Name";
ORYX.I18N.AMLSupport.allImported = "Alle importierten Diagramme.";
ORYX.I18N.AMLSupport.ok = "Ok";

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};

ORYX.I18N.Arrangement.groupZ = "Z-Order";
ORYX.I18N.Arrangement.btf = "In den Vordergrund";
ORYX.I18N.Arrangement.btfDesc = "In den Vordergrund";
ORYX.I18N.Arrangement.btb = "In den Hintergrund";
ORYX.I18N.Arrangement.btbDesc = "In den Hintergrund";
ORYX.I18N.Arrangement.bf = "Eine Ebene nach Vorne";
ORYX.I18N.Arrangement.bfDesc = "Eine Ebene nach Vorne";
ORYX.I18N.Arrangement.bb = "Eine Ebene nach Hinten";
ORYX.I18N.Arrangement.bbDesc = "Eine Ebene nach Hinten";
ORYX.I18N.Arrangement.groupA = "Alignment";
ORYX.I18N.Arrangement.ab = "Unten ausrichten";
ORYX.I18N.Arrangement.abDesc = "Unten ausrichten";
ORYX.I18N.Arrangement.am = "Horizontal mittig ausrichten";
ORYX.I18N.Arrangement.amDesc = "Horizontal mittig ausrichten";
ORYX.I18N.Arrangement.at = "Oben ausrichten";
ORYX.I18N.Arrangement.atDesc = "Oben ausrichten";
ORYX.I18N.Arrangement.al = "Links ausrichten";
ORYX.I18N.Arrangement.alDesc = "Links ausrichten";
ORYX.I18N.Arrangement.ac = "Vertikal mittig ausrichten";
ORYX.I18N.Arrangement.acDesc = "Vertikal mittig ausrichten";
ORYX.I18N.Arrangement.ar = "Rechts ausrichten";
ORYX.I18N.Arrangement.arDesc = "Rechts ausrichten";

if(!ORYX.I18N.BpelGenerator) ORYX.I18N.BpelGenerator = {};

ORYX.I18N.BpelGenerator.group = "BPEL";
ORYX.I18N.BpelGenerator.name = "BPEL Export";
ORYX.I18N.BpelGenerator.desc = "Als BPEL Datei exportieren";

if(!ORYX.I18N.Bpel4ChorTransformation) ORYX.I18N.Bpel4ChorTransformation = {};

ORYX.I18N.Bpel4ChorTransformation.group = "BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportBPEL = "BPEL4Chor Eport";
ORYX.I18N.Bpel4ChorTransformation.exportBPELDesc = "Exportieren nach BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.exportXPDL = "XPDL4Chor Export";
ORYX.I18N.Bpel4ChorTransformation.exportXPDLDesc = "Exportieren nach XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.warning = "Warnung";
ORYX.I18N.Bpel4ChorTransformation.wrongValue = 'Der geänderte Name muß den Wert "1" haben, um Fehler während der Transformation zu BPEL4Chor zu vermeiden.';
ORYX.I18N.Bpel4ChorTransformation.loopNone = 'Der Schleifentyp (Loop Type) empfangsbereiten Task muss für die Transformation zu BPEL4Chor "None" sein.';
ORYX.I18N.Bpel4ChorTransformation.error = "Fehler";
ORYX.I18N.Bpel4ChorTransformation.noSource = "1 mit der Id 2 hat kein Quellobjekt.";
ORYX.I18N.Bpel4ChorTransformation.noTarget = "1 mit der Id 2 hat kein Zielobjekt.";
ORYX.I18N.Bpel4ChorTransformation.transCall = "Während der Transformation ist ein Fehler aufgetreten. 1:2";
ORYX.I18N.Bpel4ChorTransformation.noResult = "Der Transformations Web Service hat kein Ergebnis zurückgeliefert.";
ORYX.I18N.Bpel4ChorTransformation.errorParsing = "Während der Analyse des Diagramms ist ein Fehler aufgetreten.";
ORYX.I18N.Bpel4ChorTransformation.transResult = "Transformationsergebnisse";
ORYX.I18N.Bpel4ChorTransformation.showFile = "Ergebnisdatei anzeigen";
ORYX.I18N.Bpel4ChorTransformation.downloadFile = "Ergebnisdatei herunterladen";
ORYX.I18N.Bpel4ChorTransformation.downloadAll = "Alle Ergebnisdateien herunterladen";
ORYX.I18N.Bpel4ChorTransformation.loadingExport = "Exportieren nach XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.noGen = "Die Transformationseingabedaten konnten nicht erzeugt werden: 1\n2\n";

if(!ORYX.I18N.DesynchronizabilityOverlay) ORYX.I18N.DesynchronizabilityOverlay = {};
//TODO translate
ORYX.I18N.DesynchronizabilityOverlay.group = "Overlay";
ORYX.I18N.DesynchronizabilityOverlay.name = "Desynchronizability Checker";
ORYX.I18N.DesynchronizabilityOverlay.desc = "Desynchronizability Checker";
ORYX.I18N.DesynchronizabilityOverlay.sync = "The net is desynchronizable.";
ORYX.I18N.DesynchronizabilityOverlay.error = "The net has 1 syntax errors.";
ORYX.I18N.DesynchronizabilityOverlay.invalid = "Invalid answer from server.";

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

ORYX.I18N.Edit.group = "Edit";
ORYX.I18N.Edit.cut = "Ausschneiden";
ORYX.I18N.Edit.cutDesc = "Ausschneiden der selektierten Elemente";
ORYX.I18N.Edit.copy = "Kopieren";
ORYX.I18N.Edit.copyDesc = "Kopieren der selektierten Elemente";
ORYX.I18N.Edit.paste = "Einfügen";
ORYX.I18N.Edit.pasteDesc = "Einfügen von kopierten/ausgeschnittenen Elementen";
ORYX.I18N.Edit.del = "Löschen";
ORYX.I18N.Edit.delDesc = "Löschen der selektierten Elemente";

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

ORYX.I18N.EPCSupport.group = "EPC";
ORYX.I18N.EPCSupport.exp = "EPML Export";
ORYX.I18N.EPCSupport.expDesc = "Exportieren nach EPML";
ORYX.I18N.EPCSupport.imp = "EPML Import";
ORYX.I18N.EPCSupport.impDesc = "Importieren einer EPML Datei";
ORYX.I18N.EPCSupport.progressExp = "Exportiere Modell";
ORYX.I18N.EPCSupport.selectFile = "Wählen Sie eine EPML Datei aus, die Sie importieren möchten.";
ORYX.I18N.EPCSupport.file = "Datei";
ORYX.I18N.EPCSupport.impPanel = "EPML Datei importieren";
ORYX.I18N.EPCSupport.impBtn = "Importieren";
ORYX.I18N.EPCSupport.close = "Schließen";
ORYX.I18N.EPCSupport.error = "Fehler";
ORYX.I18N.EPCSupport.progressImp = "Importiere...";

if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.group = "ERDF";
ORYX.I18N.ERDFSupport.exp = "ERDF Export";
ORYX.I18N.ERDFSupport.expDesc = "Exportieren nach ERDF";
ORYX.I18N.ERDFSupport.imp = "ERDF Import";
ORYX.I18N.ERDFSupport.impDesc = "ERDF Datei importieren";
ORYX.I18N.ERDFSupport.impFailed = "Anfrage für den Import der ERDF Datei ist fehlgeschlagen.";
ORYX.I18N.ERDFSupport.impFailed2 = "Während des Importierens ist ein Fehler aufgetreten. <br/>Fehlermeldung: <br/><br/>";
ORYX.I18N.ERDFSupport.error = "Fehler";
ORYX.I18N.ERDFSupport.noCanvas = "Das XML Dokument enthält keinen Oryx Canvas Knoten.";
ORYX.I18N.ERDFSupport.noSS = "Im XML Dokument ist kein Stencil Set referenziert.";
ORYX.I18N.ERDFSupport.wrongSS = "Das im XML Dokument referenzierte Stencil Set passt nicht zu dem im Editor geladenen Stencil Set.";
ORYX.I18N.ERDFSupport.selectFile = "Wählen sie eine ERDF Datei (.xml) aus oder geben Sie den ERDF Code im Textfeld ein.";
ORYX.I18N.ERDFSupport.file = "Datei";
ORYX.I18N.ERDFSupport.impERDF = "ERDF importieren";
ORYX.I18N.ERDFSupport.impBtn = "Importieren";
ORYX.I18N.ERDFSupport.impProgress = "Importiere...";
ORYX.I18N.ERDFSupport.close = "Schließen";

if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

ORYX.I18N.Save.group = "File";
ORYX.I18N.Save.save = "Speichern";
ORYX.I18N.Save.saveDesc = "Speichern";
ORYX.I18N.Save.saveAs = "Speichern als...";
ORYX.I18N.Save.saveAsDesc = "Speichern als...";
ORYX.I18N.Save.unsavedData = "Das Diagramm enthält nicht gespeicherte Daten. Sind Sie sicher, daß Sie den Editor schließen möchten?";
ORYX.I18N.Save.newProcess = "Neuer Prozess";
ORYX.I18N.Save.saveAsTitle = "Speichern als...";
ORYX.I18N.Save.saveBtn = "Speichern";
ORYX.I18N.Save.close = "Schließen";
ORYX.I18N.Save.savedAs = "Gespeichert als";
ORYX.I18N.Save.saved = "Gespeichert";
ORYX.I18N.Save.failed = "Das Speichern ist fehlgeschlagen.";
ORYX.I18N.Save.noRights = "Sie haben nicht die erforderlichen Rechte, um Änderungen zu speichern.";
ORYX.I18N.Save.saving = "Speichern";

if(!ORYX.I18N.File) ORYX.I18N.File = {};

ORYX.I18N.File.group = "File";
ORYX.I18N.File.print = "Drucken";
ORYX.I18N.File.printDesc = "Drucken";
ORYX.I18N.File.pdf = "PDF Export";
ORYX.I18N.File.pdfDesc = "Exportieren nach PDF";
ORYX.I18N.File.info = "Über";
ORYX.I18N.File.infoDesc = "Über";
ORYX.I18N.File.genPDF = "PDF wird generiert";
ORYX.I18N.File.genPDFFailed = "Die Generierung der PDF Datei ist fehlgeschlagen.";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "Grouping";
ORYX.I18N.Grouping.group = "Gruppieren";
ORYX.I18N.Grouping.groupDesc = "Gruppierung der selektierten Elemente";
ORYX.I18N.Grouping.ungroup = "Gruppierung aufheben";
ORYX.I18N.Grouping.ungroupDesc = "Aufheben aller Gruppierungen der selektierten Elemente";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="Bitte warten...";

if(!ORYX.I18N.Pnmlexport) ORYX.I18N.Pnmlexport = {};

ORYX.I18N.Pnmlexport.group ="Export";
ORYX.I18N.Pnmlexport.name ="PNML Export";
ORYX.I18N.Pnmlexport.desc ="Exportieren nach ausführbarem PNML und Deployen";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "Name";
ORYX.I18N.PropertyWindow.value = "Wert";
ORYX.I18N.PropertyWindow.clickIcon = "Symbol anklicken";
ORYX.I18N.PropertyWindow.add = "Hinzufügen";
ORYX.I18N.PropertyWindow.rem = "Löschen";
ORYX.I18N.PropertyWindow.complex = "Editor für komplexe Eigenschaft";
ORYX.I18N.PropertyWindow.ok = "Ok";
ORYX.I18N.PropertyWindow.cancel = "Abbrechen";

if(!ORYX.I18N.SimplePnmlexport) ORYX.I18N.SimplePnmlexport = {};

ORYX.I18N.SimplePnmlexport.group = "Export";
ORYX.I18N.SimplePnmlexport.name = "Einfacher PNML Export";
ORYX.I18N.SimplePnmlexport.desc = "Exportieren nach PNML";

if(!ORYX.I18N.StepThroughPlugin) ORYX.I18N.StepThroughPlugin = {};

ORYX.I18N.StepThroughPlugin.group = "Step Through";
ORYX.I18N.StepThroughPlugin.stepThrough = "Schrittweise Ausführung";
ORYX.I18N.StepThroughPlugin.stepThroughDesc = "Schrittweise Ausführung des BPMN Modells";
ORYX.I18N.StepThroughPlugin.undo = "Rückgängig";
ORYX.I18N.StepThroughPlugin.undoDesc = "Rückgängig";
ORYX.I18N.StepThroughPlugin.error = "Ausführung des Modells nicht möglich.";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = "Verification";
ORYX.I18N.SyntaxChecker.name = "Syntax Prüfer";
ORYX.I18N.SyntaxChecker.desc = "Überprüfung der Syntax";
ORYX.I18N.SyntaxChecker.noErrors = "Es wurden keine Syntaxfehler gefunden.";
ORYX.I18N.SyntaxChecker.invalid = "Ungültige Antwort vom Server.";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = "Edit";
ORYX.I18N.Undo.undo = "Rückgängig";
ORYX.I18N.Undo.undoDesc = "Rückgängig";
ORYX.I18N.Undo.redo = "Wiederherstellen";
ORYX.I18N.Undo.redoDesc = "Wiederherstellen";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = "Zoom";
ORYX.I18N.View.zoomIn = "Vergrößern";
ORYX.I18N.View.zoomInDesc = "Vergrößern";
ORYX.I18N.View.zoomOut = "Verkleinern";
ORYX.I18N.View.zoomOutDesc = "Verkleinern";


if(!ORYX.I18N.XFormsExport) ORYX.I18N.XFormsExport = {};

ORYX.I18N.XFormsExport.group = "Export";
ORYX.I18N.XFormsExport.name = "XForms Export";
ORYX.I18N.XFormsExport.desc = "Exportieren nach XForms";

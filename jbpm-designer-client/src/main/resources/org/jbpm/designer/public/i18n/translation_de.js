/**
 * @author nicolas.peters
 * 
 * Contains all strings for German language.
 * Version 1 - 08/29/08
 */
if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "de_DE"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.pleaseWait = "Editor wird geladen. Bitte warten...";
ORYX.I18N.Oryx.notLoggedOn = "Nicht angemeldet";
ORYX.I18N.Oryx.noBackendDefined	= "Achtung! \n Es wurde kein Repository definiert.\n Ihr Model kann nicht geladen werden. Bitte nutzen sie eine Editor Konfiguration mit einem Speicher Plugin.";

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
ORYX.I18N.Arrangement.am = "Horizontal ausrichten";
ORYX.I18N.Arrangement.amDesc = "Horizontal ausrichten";
ORYX.I18N.Arrangement.at = "Oben ausrichten";
ORYX.I18N.Arrangement.atDesc = "Oben ausrichten";
ORYX.I18N.Arrangement.al = "Links ausrichten";
ORYX.I18N.Arrangement.alDesc = "Links ausrichten";
ORYX.I18N.Arrangement.ac = "Vertikal ausrichten";
ORYX.I18N.Arrangement.acDesc = "Vertikal ausrichten";
ORYX.I18N.Arrangement.ar = "Rechts ausrichten";
ORYX.I18N.Arrangement.arDesc = "Rechts ausrichten";
ORYX.I18N.Arrangement.as = "Größenangleichung";
ORYX.I18N.Arrangement.asDesc = "Größenangleichung";

if(!ORYX.I18N.BPELSupport) ORYX.I18N.BPELSupport = {};

ORYX.I18N.BPELSupport.group = "BPEL";
ORYX.I18N.BPELSupport.exp = "BPEL Export";
ORYX.I18N.BPELSupport.expDesc = "Exportieren nach BPEL";
ORYX.I18N.BPELSupport.imp = "BPEL Import";
ORYX.I18N.BPELSupport.impDesc = "Importieren einer BPEL Datei";
ORYX.I18N.BPELSupport.selectFile = "Wählen Sie eine BPEL Datei aus, die Sie importieren möchten.";
ORYX.I18N.BPELSupport.file = "Datei";
ORYX.I18N.BPELSupport.impPanel = "BPEL Datei importieren";
ORYX.I18N.BPELSupport.impBtn = "Importieren";
ORYX.I18N.BPELSupport.content = "Inhalt";
ORYX.I18N.BPELSupport.close = "Schließen";
ORYX.I18N.BPELSupport.error = "Fehler";
ORYX.I18N.BPELSupport.progressImp = "Importiere...";
ORYX.I18N.BPELSupport.progressExp = "Exportiere...";
ORYX.I18N.BPELSupport.impFailed = "Während des Importierens ist ein Fehler aufgetreten. <br/>Fehlermeldung: <br/><br/>";

if(!ORYX.I18N.BPELLayout) ORYX.I18N.BPELLayout = {};

ORYX.I18N.BPELLayout.group = "BPELLayout";
ORYX.I18N.BPELLayout.disable = "disable layout";
ORYX.I18N.BPELLayout.disDesc = "disable auto layout plug-in";
ORYX.I18N.BPELLayout.enable = "enable layout";
ORYX.I18N.BPELLayout.enDesc = "enable auto layout plug-in";

if(!ORYX.I18N.BPEL4ChorSupport) ORYX.I18N.BPEL4ChorSupport = {};

ORYX.I18N.BPEL4ChorSupport.group = "BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.exp = "BPEL4Chor Export";
ORYX.I18N.BPEL4ChorSupport.expDesc = "Exportieren nach BPEL4Chor";
ORYX.I18N.BPEL4ChorSupport.imp = "BPEL4Chor Import";
ORYX.I18N.BPEL4ChorSupport.impDesc = "Importieren einer BPEL4Chor Datei";
ORYX.I18N.BPEL4ChorSupport.gen = "BPEL4Chor Generator";
ORYX.I18N.BPEL4ChorSupport.genDesc = "Generieren Werte einiger BPEL4Chor Eigenschaften sofern sie schon bekannt sind (z.B. Sender von messageLink)";
ORYX.I18N.BPEL4ChorSupport.selectFile = "Wählen Sie eine BPEL4Chor Datei aus, die Sie importieren möchten.";
ORYX.I18N.BPEL4ChorSupport.file = "Datei";
ORYX.I18N.BPEL4ChorSupport.impPanel = "BPEL4Chor Datei importieren";
ORYX.I18N.BPEL4ChorSupport.impBtn = "Importieren";
ORYX.I18N.BPEL4ChorSupport.content = "Inhalt";
ORYX.I18N.BPEL4ChorSupport.close = "Schließen";
ORYX.I18N.BPEL4ChorSupport.error = "Fehler";
ORYX.I18N.BPEL4ChorSupport.progressImp = "Importiere...";
ORYX.I18N.BPEL4ChorSupport.progressExp = "Exportiere...";
ORYX.I18N.BPEL4ChorSupport.impFailed = "Während des Importierens ist ein Fehler aufgetreten. <br/>Fehlermeldung: <br/><br/>";

if(!ORYX.I18N.Bpel4ChorTransformation) ORYX.I18N.Bpel4ChorTransformation = {};

ORYX.I18N.Bpel4ChorTransformation.group = "Export";
ORYX.I18N.Bpel4ChorTransformation.exportBPEL = "BPEL4Chor Export";
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
ORYX.I18N.Bpel4ChorTransformation.loadingXPDL4ChorExport = "Exportiere nach XPDL4Chor";
ORYX.I18N.Bpel4ChorTransformation.loadingBPEL4ChorExport = "Exportiere nach BPEL4Chor";
ORYX.I18N.Bpel4ChorTransformation.noGen = "Die Transformationseingabedaten konnten nicht erzeugt werden: 1\n2\n";

ORYX.I18N.BPMN2PNConverter = {
  name: "Konvertiere zu Petrinetz",
  desc: "Konvertiert BPMN-Diagramme in Petrinetze",
  group: "Export",
  error: "Fehler",
  errors: {
    server: "BPMN Diagramm konnte nicht importiert werden!",
    noRights: "Es sind keine Leserechte für das importierte Diagramm vorhanden!",
    notSaved: "Das Diagramm wurde noch nicht gespeichert und/ oder muss neu geöffnet werden!"
  },
  progress: {
      status: "Status",
      importingModel: "Importiere BPMN Model",
      fetchingModel: "Lade",
      convertingModel: "Konvertiere",
      renderingModel: "Zeige an"
  }
}

if(!ORYX.I18N.TransformationDownloadDialog) ORYX.I18N.TransformationDownloadDialog = {};

ORYX.I18N.TransformationDownloadDialog.error = "Fehler";
ORYX.I18N.TransformationDownloadDialog.noResult = "Der Transformationsservice hat kein Ergebnis zurückgeliefert.";
ORYX.I18N.TransformationDownloadDialog.errorParsing = "Während der Analyse des Diagramms ist ein Fehler aufgetreten.";
ORYX.I18N.TransformationDownloadDialog.transResult = "Transformationsergebnisse";
ORYX.I18N.TransformationDownloadDialog.showFile = "Ergebnisdatei anzeigen";
ORYX.I18N.TransformationDownloadDialog.downloadFile = "Ergebnisdatei herunterladen";
ORYX.I18N.TransformationDownloadDialog.downloadAll = "Alle Ergebnisdateien herunterladen";

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
ORYX.I18N.ERDFSupport.deprTitle = "Wirklich nach eRDF exportieren?";
ORYX.I18N.ERDFSupport.deprText = "Der Export nach eRDF wird nicht empfohlen, da dieses Format in zukünftigen Versionen des Oryx Editors nicht mehr unterstützt wird. Verwenden Sie statt dessen den Export nach JSON, falls möglich. Wollen Sie dennoch das Model nach eRDF exportieren?";

if(!ORYX.I18N.jPDLSupport) ORYX.I18N.jPDLSupport = {};

ORYX.I18N.jPDLSupport.group = "ExecBPMN";
ORYX.I18N.jPDLSupport.exp = "jPDL Export";
ORYX.I18N.jPDLSupport.expDesc = "Exportieren nach jPDL";
ORYX.I18N.jPDLSupport.imp = "jPDL Import";
ORYX.I18N.jPDLSupport.impDesc = "jPDL Datei importieren";
ORYX.I18N.jPDLSupport.impFailedReq = "Anfrage für den Import der jPDL Datei ist fehlgeschlagen.";
ORYX.I18N.jPDLSupport.impFailedJson = "Transformation der jPDL Datei ist fehlgeschlagen.";
ORYX.I18N.jPDLSupport.impFailedJsonAbort = "Import abgebrochen.";
ORYX.I18N.jPDLSupport.loadSseQuestionTitle = "Stencil Set Erweiterung für jBPM muss geladen werden"; 
ORYX.I18N.jPDLSupport.loadSseQuestionBody = "Um jPDL importieren zu können, muss die Stencil Set Erweiterung für jBPM geladen werden. Möchten Sie fortfahren?";
ORYX.I18N.jPDLSupport.expFailedReq = "Anfrage für den Export des Models ist fehlgeschlagen.";
ORYX.I18N.jPDLSupport.expFailedXml = "Export nach jPDL ist fehlgeschlagen. Exporter meldet: ";
ORYX.I18N.jPDLSupport.error = "Fehler";
ORYX.I18N.jPDLSupport.selectFile = "Wählen sie eine jPDL Datei (.xml) aus oder geben Sie den jPDL Code im Textfeld ein.";
ORYX.I18N.jPDLSupport.file = "Datei";
ORYX.I18N.jPDLSupport.impJPDL = "jPDL importieren";
ORYX.I18N.jPDLSupport.impBtn = "Importieren";
ORYX.I18N.jPDLSupport.impProgress = "Importiere...";
ORYX.I18N.jPDLSupport.close = "Schließen";

if(!ORYX.I18N.cpntoolsSupport) ORYX.I18N.cpntoolsSupport = {};

ORYX.I18N.cpntoolsSupport.serverConnectionFailed = "Anfrage an den Oryx Server ist fehlgeschlagen.";
ORYX.I18N.cpntoolsSupport.importTask = "Wähle eine CPN Datei (.cpn) aus oder tippe die CPN XML Struktur ein  um es zu importieren!";
ORYX.I18N.cpntoolsSupport.file = "Datei:";
ORYX.I18N.cpntoolsSupport.cpn = "CPN";
ORYX.I18N.cpntoolsSupport.title = "CPN Oryx";
ORYX.I18N.cpntoolsSupport.importLable = "Importieren";
ORYX.I18N.cpntoolsSupport.close = "Schließen";
ORYX.I18N.cpntoolsSupport.wrongCPNFile = "Keine richtige CPN - Datei ausgewählt.";
ORYX.I18N.cpntoolsSupport.noPageSelection = "Es wurde kein Netze ausgewählt.";
ORYX.I18N.cpntoolsSupport.importProgress = "Importieren ...";
ORYX.I18N.cpntoolsSupport.exportProgress = "Exportieren ...";
ORYX.I18N.cpntoolsSupport.exportDescription = "Exportieren nach CPN Tools";
ORYX.I18N.cpntoolsSupport.importDescription = "Importieren von CPN Tools";

if(!ORYX.I18N.Bpmn2Bpel) ORYX.I18N.Bpmn2Bpel = {};

ORYX.I18N.Bpmn2Bpel.group = "ExecBPMN";
ORYX.I18N.Bpmn2Bpel.show = "Transformiertes BPEL anzeigen";
ORYX.I18N.Bpmn2Bpel.download = "Transformiertes BPEL herunterladen";
ORYX.I18N.Bpmn2Bpel.deploy = "Transformiertes BPEL bereitstellen";
ORYX.I18N.Bpmn2Bpel.showDesc = "Transformiert BPMN in BPEL und zeigt das Ergebnis an.";
ORYX.I18N.Bpmn2Bpel.downloadDesc = "Transformiert BPMN in BPEL und bietet das Ergebnis zum Download an.";
ORYX.I18N.Bpmn2Bpel.deployDesc = "Transformiert BPMN in BPEL und stellt den Prozess auf der BPEL-Engine Apache ODE bereit.";
ORYX.I18N.Bpmn2Bpel.transfFailed = "Die Anfrage zur Transformation in BPEL ist fehlgeschlagen.";

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
ORYX.I18N.Save.saveAsHint = "Das Diagramm wurde unter folgendem Link gespeichert:";

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
ORYX.I18N.File.printTitle = "Drucken";
ORYX.I18N.File.printMsg = "Leider arbeitet die Druckfunktion zur Zeit nicht immer korrekt. Bitte nutzen Sie den PDF Export, und drucken Sie das PDF Dokument aus. Möchten Sie dennoch mit dem Drucken fortfahren?";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "Grouping";
ORYX.I18N.Grouping.group = "Gruppieren";
ORYX.I18N.Grouping.groupDesc = "Gruppierung der selektierten Elemente";
ORYX.I18N.Grouping.ungroup = "Gruppierung aufheben";
ORYX.I18N.Grouping.ungroupDesc = "Aufheben aller Gruppierungen der selektierten Elemente";

if(!ORYX.I18N.IBPMN2BPMN) ORYX.I18N.IBPMN2BPMN = {};

ORYX.I18N.IBPMN2BPMN.group ="Export";
ORYX.I18N.IBPMN2BPMN.name ="IBPMN 2 BPMN Mapping";
ORYX.I18N.IBPMN2BPMN.desc ="IBPMN nach BPMN konvertieren";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="Bitte warten...";

if(!ORYX.I18N.Pnmlexport) ORYX.I18N.Pnmlexport = {};

ORYX.I18N.Pnmlexport.group ="Export";
ORYX.I18N.Pnmlexport.name ="Nach PNML exportieren";
ORYX.I18N.Pnmlexport.desc ="Exportieren nach ausführbarem PNML und Deployen";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "Name";
ORYX.I18N.PropertyWindow.value = "Wert";
ORYX.I18N.PropertyWindow.selected = "ausgewählt";
ORYX.I18N.PropertyWindow.clickIcon = "Symbol anklicken";
ORYX.I18N.PropertyWindow.add = "Hinzufügen";
ORYX.I18N.PropertyWindow.rem = "Löschen";
ORYX.I18N.PropertyWindow.complex = "Editor für komplexe Eigenschaft";
ORYX.I18N.PropertyWindow.text = "Editor für einen Text";
ORYX.I18N.PropertyWindow.ok = "Ok";
ORYX.I18N.PropertyWindow.cancel = "Abbrechen";
ORYX.I18N.PropertyWindow.dateFormat = "d/m/y";

if (!ORYX.I18N.ConditionExpressionEditorField) ORYX.I18N.ConditionExpressionEditorField = {};

ORYX.I18N.ConditionExpressionEditorField.simpleTitle = "Expression Editor - Press [Ctrl-Z] to activate auto-completion";
ORYX.I18N.ConditionExpressionEditorField.sequenceFlowTitle = "Sequence Flow Conditions";
ORYX.I18N.ConditionExpressionEditorField.sequenceFlowFullTitle = "Sequence Flow Conditions - Press [Ctrl-Z] to activate auto-completion";
ORYX.I18N.ConditionExpressionEditorField.scriptTab = "Script";
ORYX.I18N.ConditionExpressionEditorField.editorTab = "Editor";
ORYX.I18N.ConditionExpressionEditorField.editorDescription = "Run sequence flow if the following conditions are met."
ORYX.I18N.ConditionExpressionEditorField.processVariable = "Process variable:"
ORYX.I18N.ConditionExpressionEditorField.condition = "Condition:"
ORYX.I18N.ConditionExpressionEditorField.between = "between";
ORYX.I18N.ConditionExpressionEditorField.contains = "contains";
ORYX.I18N.ConditionExpressionEditorField.endsWith = "ends with";
ORYX.I18N.ConditionExpressionEditorField.equalsTo = "is equal to";
ORYX.I18N.ConditionExpressionEditorField.greaterThan = "is greater than";
ORYX.I18N.ConditionExpressionEditorField.greaterThanOrEqual = "is greater than or equal to";
ORYX.I18N.ConditionExpressionEditorField.isEmpty = "is empty";
ORYX.I18N.ConditionExpressionEditorField.isFalse = "is false";
ORYX.I18N.ConditionExpressionEditorField.isNull = "is null";
ORYX.I18N.ConditionExpressionEditorField.isTrue = "is true";
ORYX.I18N.ConditionExpressionEditorField.lessThan = "is less than";
ORYX.I18N.ConditionExpressionEditorField.lessThanOrEqual = "is less than or equal to";
ORYX.I18N.ConditionExpressionEditorField.startsWith = "starts with";
ORYX.I18N.ConditionExpressionEditorField.paramsError = "Unable to generate Script expression, please fill correctly the form params.";
ORYX.I18N.ConditionExpressionEditorField.saveError = "Unable to save property value, please check the value and try again.";
ORYX.I18N.ConditionExpressionEditorField.scriptParseError = "Error found parsing script: <br/>{0}<br/><br/>Press OK to go to the Expression Editor screen and loose the current Script or Cancel to go back to the Script Editor.";
ORYX.I18N.ConditionExpressionEditorField.scriptGenerationError = "Error found generating script: <br/>{0}<br/><br/>Please check the data entered on the Expression Editor.";
ORYX.I18N.ConditionExpressionEditorField.nonExistingVariable = "The process does not contain any variable called \"{0}\".";

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = "Ziehen";
ORYX.I18N.ShapeMenuPlugin.clickDrag = "Klicken oder ziehen";
ORYX.I18N.ShapeMenuPlugin.morphMsg = "Shape morphen";

if(!ORYX.I18N.SimplePnmlexport) ORYX.I18N.SimplePnmlexport = {};

ORYX.I18N.SimplePnmlexport.group = "Export";
ORYX.I18N.SimplePnmlexport.name = "Nach PNML exportieren";
ORYX.I18N.SimplePnmlexport.desc = "Exportieren nach PNML";

if(!ORYX.I18N.StepThroughPlugin) ORYX.I18N.StepThroughPlugin = {};

ORYX.I18N.StepThroughPlugin.group = "Step Through";
ORYX.I18N.StepThroughPlugin.stepThrough = "Schrittweise Ausführung";
ORYX.I18N.StepThroughPlugin.stepThroughDesc = "Schrittweise Ausführung des Modells";
ORYX.I18N.StepThroughPlugin.undo = "Rückgängig";
ORYX.I18N.StepThroughPlugin.undoDesc = "Rückgängig";
ORYX.I18N.StepThroughPlugin.error = "Ausführung des Modells nicht möglich.";
ORYX.I18N.StepThroughPlugin.executing = "Führe aus";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = "Verification";
ORYX.I18N.SyntaxChecker.name = "Syntax-Checker";
ORYX.I18N.SyntaxChecker.desc = "Überprüfung der Syntax";
ORYX.I18N.SyntaxChecker.noErrors = "Es wurden keine Syntaxfehler gefunden.";
ORYX.I18N.SyntaxChecker.invalid = "Ungültige Antwort vom Server.";
ORYX.I18N.SyntaxChecker.checkingMessage = "Überprüfung wird durchgeführt ...";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = "Undo";
ORYX.I18N.Undo.undo = "Rückgängig";
ORYX.I18N.Undo.undoDesc = "Rückgängig";
ORYX.I18N.Undo.redo = "Wiederherstellen";
ORYX.I18N.Undo.redoDesc = "Wiederherstellen";

if(!ORYX.I18N.Validator) ORYX.I18N.Validator = {};
ORYX.I18N.Validator.checking = "Prüfe";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = "Zoom";
ORYX.I18N.View.zoomIn = "Vergrößern";
ORYX.I18N.View.zoomInDesc = "Vergrößern";
ORYX.I18N.View.zoomOut = "Verkleinern";
ORYX.I18N.View.zoomOutDesc = "Verkleinern";
ORYX.I18N.View.zoomStandard = "Originalgröße";
ORYX.I18N.View.zoomStandardDesc = "Originalgröße";
ORYX.I18N.View.zoomFitToModel = "Modelgröße";
ORYX.I18N.View.zoomFitToModelDesc = "Modelgröße";
ORYX.I18N.View.showInPopout = "Popout";
ORYX.I18N.View.showInPopoutDesc = "Zeige in popup";
ORYX.I18N.View.convertToPDF = "PDF";
ORYX.I18N.View.convertToPDFDesc = "PDF";
ORYX.I18N.View.convertToPNG = "PNG";
ORYX.I18N.View.convertToPNGDesc = "PNG";
ORYX.I18N.View.generateTaskForms = "Generate Task Form Templates";
ORYX.I18N.View.generateTaskFormsDesc = "Generate Task Form Templates";
ORYX.I18N.View.showInfo = "Info";
ORYX.I18N.View.showInfoDesc = "Info";
ORYX.I18N.View.jbpmgroup = "jBPM";
ORYX.I18N.View.migratejPDL = "Migrate jPDL 3.2 to BPMN2";
ORYX.I18N.View.migratejPDLDesc = "Migrate jPDL 3.2 to BPMN2";
ORYX.I18N.View.viewDiff = "View diff";
ORYX.I18N.View.viewDiffDesc = "View diff between different versions of the process";
ORYX.I18N.View.viewDiffLoadingVersions = "Loading process versions...";
ORYX.I18N.View.connectServiceRepo = "Connect to jBPM service repository";
ORYX.I18N.View.connectServiceRepoDesc = "Connect to jBPM service repository";
ORYX.I18N.View.connectServiceRepoDataTitle = "jBPM Service Repository Data";
ORYX.I18N.View.connectServiceRepoConnecting = "Connecting to jBPM Service Repository...";
ORYX.I18N.View.installingRepoItem = "Instaling items from repository...";
ORYX.I18N.View.shareProcess = "Share your process";
ORYX.I18N.View.shareProcessDesc = "Share your process";
ORYX.I18N.View.infogroup = "info";

if(!ORYX.I18N.View.tabs) ORYX.I18N.View.tabs = {};
ORYX.I18N.View.tabs.modelling = "Process Modelling";
ORYX.I18N.View.tabs.simResults = "Simulation Results";

if(!ORYX.I18N.View.sim) ORYX.I18N.View.sim = {};
ORYX.I18N.View.sim.processPaths = "Display Process Paths";
ORYX.I18N.View.sim.runSim = "Run Process Simulation";
ORYX.I18N.View.sim.calculatingPaths = "Calculating process paths.";
ORYX.I18N.View.sim.dispColor = "Display Color";
ORYX.I18N.View.sim.numElements = "Number of Elements";
ORYX.I18N.View.sim.processPathsTitle = "Process Paths";
ORYX.I18N.View.sim.subProcessPathsTitle = "Subprocess Paths";
ORYX.I18N.View.sim.select = "Select ";
ORYX.I18N.View.sim.display = " and click Show Path to display it.";
ORYX.I18N.View.sim.showPath = "Show Path";
ORYX.I18N.View.sim.selectPath = "Please select a process path.";
ORYX.I18N.View.sim.numInstances = "Number of instances";
ORYX.I18N.View.sim.interval = "Interval";
ORYX.I18N.View.sim.intervalUnits = "Interval units";
ORYX.I18N.View.sim.runSim = "Run Process Simulation";
ORYX.I18N.View.sim.runningSim = "Running Process Simulation...";
ORYX.I18N.View.sim.simNoResults = "Simulation engine did not return results: ";
ORYX.I18N.View.sim.unableToPerform = "Unable to perform simulation:";
ORYX.I18N.View.sim.resultsInfo = "Simulation Info";
ORYX.I18N.View.sim.resultsGraphs = "Simulation Graphs";
ORYX.I18N.View.sim.resultsProcessId = "Process id: ";
ORYX.I18N.View.sim.resultsProcessName = "Process name: ";
ORYX.I18N.View.sim.resultsProcessVersion = "Process version: ";
ORYX.I18N.View.sim.resultsSimStartTime = "Simulation start: ";
ORYX.I18N.View.sim.resultsSimEndTime = "Simulation end: ";
ORYX.I18N.View.sim.resultsNumOfExecutions = "Num. of Executions: ";
ORYX.I18N.View.sim.resultsInterval = "Interval "
ORYX.I18N.View.sim.resultsGroupProcess = "Process";
ORYX.I18N.View.sim.resultsGroupProcessElements = "Process Elements";
ORYX.I18N.View.sim.resultsGroupProcessPaths = "Paths";
ORYX.I18N.View.sim.resultsTitlesProcessSimResults = "Process Simulation Results";
ORYX.I18N.View.sim.resultsTitlesTaskSimResults = "Task Simulation Results";
ORYX.I18N.View.sim.resultsTitlesHumanTaskSimResults = "Human Task Simulation Results";
ORYX.I18N.View.sim.resultsTitlesPathExecutionInfo = "Path Execution Info";
ORYX.I18N.View.sim.chartsExecutionTimes = "Execution Times";
ORYX.I18N.View.sim.chartsActivityInstances = "Activity Instances";
ORYX.I18N.View.sim.chartsTotalCost = "Total Cost";
ORYX.I18N.View.sim.chartsResourceUtilization = "Resource Utilization";
ORYX.I18N.View.sim.chartsResourceCost = "Resource Cost";
ORYX.I18N.View.sim.chartsPathImage = "Path Image";
ORYX.I18N.View.sim.chartsPathInstanceExecution = "Path Instance Execution";

if(!ORYX.I18N.XFormsSerialization) ORYX.I18N.XFormsSerialization = {};

ORYX.I18N.XFormsSerialization.group = "XForms Serialisierung";
ORYX.I18N.XFormsSerialization.exportXForms = "XForms Export";
ORYX.I18N.XFormsSerialization.exportXFormsDesc = "Export als XForms+XHTML Markup";
ORYX.I18N.XFormsSerialization.importXForms = "XForms Import";
ORYX.I18N.XFormsSerialization.importXFormsDesc = "Import von XForms+XHTML Markup";
ORYX.I18N.XFormsSerialization.noClientXFormsSupport = "Keine XForms Unterstützung";
ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc = "<h2>Ihr Browser unterstützt XForms nicht. Bitte installieren Sie das <a href=\"https://addons.mozilla.org/firefox/addon/824\" target=\"_blank\">Mozilla XForms Add-on</a> für Firefox.</h2>";
ORYX.I18N.XFormsSerialization.ok = "Ok";
ORYX.I18N.XFormsSerialization.selectFile = "Wählen sie eine XHTML Datei (.xhtml) aus oder geben Sie das XForms+XHTML Markup im Textfeld ein.";
ORYX.I18N.XFormsSerialization.selectCss = "Optional: Geben sie die URL der gewünschten css Datei an.";
ORYX.I18N.XFormsSerialization.file = "Datei";
ORYX.I18N.XFormsSerialization.impFailed = "Anfrage für den Import des Dokuments ist fehlgeschlagen.";
ORYX.I18N.XFormsSerialization.impTitle = "XForms+XHTML Dokument importieren";
ORYX.I18N.XFormsSerialization.expTitle = "XForms+XHTML Dokument exportieren";
ORYX.I18N.XFormsSerialization.impButton = "Importieren";
ORYX.I18N.XFormsSerialization.impProgress = "Importiere...";
ORYX.I18N.XFormsSerialization.close = "Schließen";

/** New Language Properties: 08.12.2008 **/

ORYX.I18N.PropertyWindow.title = "Eigenschaften";

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = "Shape Verzeichnis";

ORYX.I18N.Save.dialogDesciption = "Bitte geben Sie einen Namen, eine Beschreibung und einen Kommentar ein.";
ORYX.I18N.Save.dialogLabelTitle = "Titel";
ORYX.I18N.Save.dialogLabelDesc = "Beschreibung";
ORYX.I18N.Save.dialogLabelType = "Typ";
ORYX.I18N.Save.dialogLabelComment = "Revisionskommentar";

ORYX.I18N.Validator.name = "BPMN Überprüfung";
ORYX.I18N.Validator.description = "Überprüfung von BPMN Modellen";

ORYX.I18N.SSExtensionLoader.labelImport = "Import";
ORYX.I18N.SSExtensionLoader.labelCancel = "Abbrechen";

Ext.MessageBox.buttonText.yes = "Ja";
Ext.MessageBox.buttonText.no = "Nein";
Ext.MessageBox.buttonText.cancel = "Abbrechen";
Ext.MessageBox.buttonText.ok = "OK";



/** New Language Properties: 28.01.2009 */
ORYX.I18N.BPMN2XPDL.group = "Export";
ORYX.I18N.BPMN2XPDL.xpdlExport = "Nach XPDL exportieren";

/** Resource Perspective Additions: 24 March 2009 */
if(!ORYX.I18N.ResourcesSoDAdd) ORYX.I18N.ResourcesSoDAdd = {};

ORYX.I18N.ResourcesSoDAdd.name = "Erstelle Separation of Duties Abhängigkeit";
ORYX.I18N.ResourcesSoDAdd.group = "Resource Perspective";
ORYX.I18N.ResourcesSoDAdd.desc = "Erstelle eine Separation of Duties Abhängigkeit für die selektierten Tasks";

if(!ORYX.I18N.ResourcesSoDShow) ORYX.I18N.ResourcesSoDShow = {};

ORYX.I18N.ResourcesSoDShow.name = "Zeige Separation of Duties Abhängigkeiten";
ORYX.I18N.ResourcesSoDShow.group = "Resource Perspective";
ORYX.I18N.ResourcesSoDShow.desc = "Zeige Separation of Duties Abhängigkeiten der selektierten Task";

if(!ORYX.I18N.ResourcesBoDAdd) ORYX.I18N.ResourcesBoDAdd = {};

ORYX.I18N.ResourcesBoDAdd.name = "Erstelle Binding of Duties Abhängigkeit";
ORYX.I18N.ResourcesBoDAdd.group = "Resource Perspective";
ORYX.I18N.ResourcesBoDAdd.desc = "Erstelle eine Binding of Duties Abhängigkeit für die selektierten Tasks";

if(!ORYX.I18N.ResourcesBoDShow) ORYX.I18N.ResourcesBoDShow = {};

ORYX.I18N.ResourcesBoDShow.name = "Zeige Binding of Duties Abhängikeiten";
ORYX.I18N.ResourcesBoDShow.group = "Resource Perspective";
ORYX.I18N.ResourcesBoDShow.desc = "Zeige Binding of Duties Abhängigkeiten für die selektierte Task";

if(!ORYX.I18N.ResourceAssignment) ORYX.I18N.ResourceAssignment = {};

ORYX.I18N.ResourceAssignment.name = "Ressourcenzuweisung";
ORYX.I18N.ResourceAssignment.group = "Resource Perspective";
ORYX.I18N.ResourceAssignment.desc = "Weise der/ den selektierten Task(s) Ressourcen zu";

if(!ORYX.I18N.ClearSodBodHighlights) ORYX.I18N.ClearSodBodHighlights = {};

ORYX.I18N.ClearSodBodHighlights.name = "Entferne Highlights und Overlays";
ORYX.I18N.ClearSodBodHighlights.group = "Resource Perspective";
ORYX.I18N.ClearSodBodHighlights.desc = "Entferne alle Separation und Binding of Duties Highlights/ Overlays";

if(!ORYX.I18N.Perspective) ORYX.I18N.Perspective = {};
ORYX.I18N.Perspective.no = "Keine Perspektive"
ORYX.I18N.Perspective.noTip = "Zurücksetzen der aktuellen Perspektive"

/** New Language Properties: 21.04.2009 */
ORYX.I18N.JSONSupport = {
    imp: {
        name: "JSON importieren",
        desc: "Importiert ein neues Modell aus JSON",
        group: "Export",
        selectFile: "Wählen Sie eine JSON-Datei (*.json) aus, die Sie importieren möchten, oder fügen Sie JSON in das Textfeld ein.",
        file: "Datei",
        btnImp: "Importieren",
        btnClose: "Schließen",
        progress: "Importieren ...",
        syntaxError: "Syntaxfehler"
    },
    exp: {
        name: "Nach JSON exportieren",
        desc: "Exportiert das aktuelle Modell nach JSON",
        group: "Export"
    }
};

/** New Language Properties: 08.05.2009 */
if(!ORYX.I18N.BPMN2XHTML) ORYX.I18N.BPMN2XHTML = {};
ORYX.I18N.BPMN2XHTML.group = "Export";
ORYX.I18N.BPMN2XHTML.XHTMLExport = "XHTML Dokumentation exportieren";

/** New Language Properties: 09.05.2009 */
if(!ORYX.I18N.JSONImport) ORYX.I18N.JSONImport = {};

ORYX.I18N.JSONImport.title = "JSON Import";
ORYX.I18N.JSONImport.wrongSS = "Das Stencil Set der importierten Datei ({0}) entspricht nicht dem geladenen Stencil Set ({1})."
ORYX.I18N.JSONImport.invalidJSON = "Das zu importierende JSON ist ungültig.";

if(!ORYX.I18N.Feedback) ORYX.I18N.Feedback = {};

ORYX.I18N.Feedback.name = "Feedback";
ORYX.I18N.Feedback.desc = "Bitte senden Sie uns Ihr Feedback!";
ORYX.I18N.Feedback.pTitle = "Bitte senden Sie uns Ihr Feedback!";
ORYX.I18N.Feedback.pName = "Name";
ORYX.I18N.Feedback.pEmail = "E-Mail";
ORYX.I18N.Feedback.pSubject = "Betreff";
ORYX.I18N.Feedback.pMsg = "Nachricht";
ORYX.I18N.Feedback.pEmpty = "* Bitte geben Sie uns so genaue Informationen wie möglich.\n* Falls Sie einen Fehler gefunden haben, beschreiben Sie bitte die Schritte, die den Fehler verursacht haben.";
ORYX.I18N.Feedback.pAttach = "Ihr Model der Nachricht anfügen";
ORYX.I18N.Feedback.pAttachDesc = "Ihr Modell kann uns bei der Fehlersuche helfen. Der Inhalt des Modells wird vertraulich behandelt. Wenn Sie nicht möchten, daß das Modell an uns geschickt wird, entfernen Sie bitte das Häckchen.";
ORYX.I18N.Feedback.pBrowser = "Browser- und Systeminformationen";
ORYX.I18N.Feedback.pBrowserDesc = "Diese Informationen wurde automatisch von Ihrem Browser ermittelt und können uns helfen, den Fehler zu finden.";
ORYX.I18N.Feedback.submit = "Feedback senden";
ORYX.I18N.Feedback.sending = "Sende Nachricht ...";
ORYX.I18N.Feedback.success = "Nachricht gesendet";
ORYX.I18N.Feedback.successMsg = "Vielen Dank. Ihr Feedback wurde an uns übermittelt.";
ORYX.I18N.Feedback.failure = "Fehler";
ORYX.I18N.Feedback.failureMsg = "Beim Senden der Nachricht ist ein Fehler aufgetreten.";


ORYX.I18N.Feedback.name = "Feedback";
ORYX.I18N.Feedback.failure = "Fehler";
ORYX.I18N.Feedback.failureMsg = "Leider konnte das Feedback nciht versandt werden. Das ist unsere Schuld! Bitte versuchen Sie es erneut, oder kontaktieren Sie einen Entwickler auf http://code.google.com/p/oryx-editor/";

ORYX.I18N.Feedback.emailDesc = "Ihre E-Mailadresse";
ORYX.I18N.Feedback.titleDesc = "Betreff Ihres Feedbacks";
ORYX.I18N.Feedback.descriptionDesc = "Schreiben Sie usn über Ideen, Fragen, oder Probleme."
ORYX.I18N.Feedback.info = '<p>Oryx ist eine wissenschaftliche Modellierungsplattform, die darauf abzielt, Forschung im Bereich Business Process Management und darüber hinaus zu unterstützen.</p><p><a href="http://bpt.hpi.uni-potsdam.de/Oryx/DeveloperNetwork" target="_blank">Wir</a> sind stolz darauf, Ihnen <a href="http://bpt.hpi.uni-potsdam.de/Oryx/ReleaseNotes" target="_blank">neueste Methoden und Technologien</a> zur Verfügung zu stellen und bemühen uns, die Plattform stets zuverlässig zu betreiben.</p><p>Wenn Sie Ideen haben, wie man Oryx verbessern kann, Fragen oder Probleme auftreten, <strong>lassen Sie uns davon wissen!</strong></p>';// general info will be shown, if no subject specific info is given
// list subjects in reverse order of appearance!
ORYX.I18N.Feedback.subjects = [
    {
    	id: "question",   // ansi-compatible name
    	name: "Frage", // natural name
    	description: "Stellen Sie Ihre Frage!\nBitte schildern Sie die Umstände genau, damit wir Ihnen eine möglichst präzise Antwort geben können.", // default text for the description text input field
    	info: "", // optional field to give more info
    },
    {
    	id: "problem",   // ansi-compatible name
    	name: "Problem", // natural name
    	description: "Bitte entschuldigen Sie die Unannehmlichkeiten. Beschreiben Sie das Problem bitte so genau wie möglich, damit wir es nachvollziehen und schnell beheben können.", // default text for the description text input field
    	info: "", // optional field to give more info
    },
    {
    	id: "idea",   // ansi-compatible name
    	name: "Idee", // natural name
    	description: "Teilen Sie uns Ihre Ideen und Gedanken zu Oryx mit.", // default text for the description text input field
    	info: "", // optional field to give more info
    }
];

/** New Language Properties: 11.05.2009 */
if(!ORYX.I18N.BPMN2DTRPXMI) ORYX.I18N.BPMN2DTRPXMI = {};
ORYX.I18N.BPMN2DTRPXMI.group = "Export";
ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExport = "Nach XMI exportieren (Design Thinking)";
ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExportDescription = "Exportiert das aktuelle Model nach XMI (erfordert die Stencil Set Extension 'BPMN Subset for Design Thinking')";

/** New Language Properties: 14.05.2009 */
if(!ORYX.I18N.RDFExport) ORYX.I18N.RDFExport = {};
ORYX.I18N.RDFExport.group = "Export";
ORYX.I18N.RDFExport.rdfExport = "Nach RDF exportieren";
ORYX.I18N.RDFExport.rdfExportDescription = "Exportiert das aktuelle Model in die XML-Serialisierung des Resource Description Frameworks (RDF)";

/** New Language Properties: 15.05.2009*/
if(!ORYX.I18N.SyntaxChecker.BPMN) ORYX.I18N.SyntaxChecker.BPMN={};
ORYX.I18N.SyntaxChecker.BPMN_NO_SOURCE = "Eine Kante muss einen Ursprung haben.";
ORYX.I18N.SyntaxChecker.BPMN_NO_TARGET = "Eine Kante muss ein Ziel haben.";
ORYX.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = "Ursprungs- und Zielknoten müssen im gleichen Prozess sein.";
ORYX.I18N.SyntaxChecker.BPMN_SAME_PROCESS = "Ursprungs- und Zielknoten müssen in verschiedenen Pools enthalten sein.";
ORYX.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "Ein Kontrollflussobjekt muss sich in einem Prozess befinden.";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "Ein End-Ereignis muss einen eingehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Ein Start-Ereignis muss einen ausgehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "Start-Ereignisse dürfen keinen eingehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "Angeheftete Zwischen-Ereignisse dürfen keinen eingehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Angeheftete Zwischen-Ereignisse müssen genau einen ausgehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "End-Ereignisse dürfen keinen ausgehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = "Auf Ereignis-basierte Gateways dürfen weder Gateways noch Subprozesse folgen.";
ORYX.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = "Knotentyp ist nicht erlaubt.";

if(!ORYX.I18N.SyntaxChecker.IBPMN) ORYX.I18N.SyntaxChecker.IBPMN={};
ORYX.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = "Für Interaktionen muss ein Sender und ein Empfänger definiert sein.";
ORYX.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = "Dieser Knoten muss eingehenden Sequenzfluss besitzen.";
ORYX.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = "Dieser Knoten muss ausgehenden Sequenzfluss besitzen.";

if(!ORYX.I18N.SyntaxChecker.InteractionNet) ORYX.I18N.SyntaxChecker.InteractionNet={};
ORYX.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = "Sender ist nicht definiert";
ORYX.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = "Empfänger ist nicht definiert";
ORYX.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = "Nachrichtentyp ist nicht definiert.";
ORYX.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = "Rolle ist nicht definiert.";

if(!ORYX.I18N.SyntaxChecker.EPC) ORYX.I18N.SyntaxChecker.EPC={};
ORYX.I18N.SyntaxChecker.EPC_NO_SOURCE = "Eine Kante muss einen Ursprung haben.";
ORYX.I18N.SyntaxChecker.EPC_NO_TARGET = "Eine Kante muss ein Ziel haben.";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED = "Dieser Knoten muss eingehende oder ausgehende Kanten besitzen.";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = "Dieser Knoten muss sowohl eingehende als auch ausgehende Knoten besitzen.";
ORYX.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = "Knoten ist mit zu vielen Kanten verbunden.";
ORYX.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = "Knoten ist kein korrekter Konnektor.";
ORYX.I18N.SyntaxChecker.EPC_MANY_STARTS = "Es darf nur ein Start-Ereignis geben.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = "Funktionen hinter einem OR-/XOR-Split sind nicht erlaubt.";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_OR = "Prozessschnittstellen hinter einem OR-/XOR-Split ist nicht erlaubt.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION =  "Auf eine Funktion darf keine Funktion folgen.";
ORYX.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT =  "Auf ein Ereignis darf kein Ereignis folgen.";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION =  "Auf eine Funktion darf keine Prozessschnittstelle folgen.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI =  "Auf eine Prozessschnittstelle darf keine Funktion folgen.";

if(!ORYX.I18N.SyntaxChecker.PetriNet) ORYX.I18N.SyntaxChecker.PetriNet={};
ORYX.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = "Der Graph ist nicht bepartit.";
ORYX.I18N.SyntaxChecker.PetriNet_NO_LABEL = "Bezeichnung für einen bezeichnete Transition ist nicht gesetzt.";
ORYX.I18N.SyntaxChecker.PetriNet_NO_ID = "Ein Knoten besitzt keine ID.";
ORYX.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = "Zwei Flussbeziehungen besitzen den gleichen Ursprung und das gleiche Ziel.";
ORYX.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = "Ein Knoten ist nicht definiert für einen Flussbeziehung.";

/** New Language Properties: 02.06.2009*/
ORYX.I18N.Edge = "Kante";
ORYX.I18N.Node = "Knoten";

/** New Language Properties: 02.06.2009*/
ORYX.I18N.SyntaxChecker.notice = "Bitte bewegen Sie den Mauszeiger über ein rotes Kreuz, um die Details zu erfahren.";

ORYX.I18N.Validator.result = "Validierungsergebnis";
ORYX.I18N.Validator.noErrors = "Es wurden keine Fehler gefunden.";
ORYX.I18N.Validator.bpmnDeadlockTitle = "Deadlock";
ORYX.I18N.Validator.bpmnDeadlock = "Dieses Element verursacht einen Deadlock, d.h. in manchen Situationen können nicht alle eingehenden Kanten und Nachrichtenflüsse aktiviert werden.";
ORYX.I18N.Validator.bpmnUnsafeTitle = "Fehlende Synchronisation";
ORYX.I18N.Validator.bpmnUnsafe = "Es findet eine mehrfache Aktivierung statt. Dadurch ist Synchronisierung nicht gewährleistet.";
ORYX.I18N.Validator.bpmnLeadsToNoEndTitle = "Validierungsergebnis";
ORYX.I18N.Validator.bpmnLeadsToNoEnd = "Dieser Prozess wird niemals einen Endzustand erreichen.";


ORYX.I18N.Validator.syntaxErrorsTitle = "Syntaxfehler";
ORYX.I18N.Validator.syntaxErrorsMsg = "Das Modell kann nicht validiert werden, weil es Syntaxfehler enthält.";
	
ORYX.I18N.Validator.error = "Validierung fehlgeschlagen";
ORYX.I18N.Validator.errorDesc = 'Es tut uns leid, aber die Valdierung konnte nicht durchgeführt werden. Wenn Sie uns über die Supportanfrage-Funktion Ihr Modell zuschicken, hilft es uns, den Fehler zu identifizieren und zu beheben.';

ORYX.I18N.Validator.epcIsSound = "<p><b>The EPC is sound, no problems found!</b></p>";
ORYX.I18N.Validator.epcNotSound = "<p><b>The EPC is <i>NOT</i> sound!</b></p>";

/** New Language Properties: 15.07.2009*/
if(!ORYX.I18N.Layouting) ORYX.I18N.Layouting ={};
ORYX.I18N.Layouting.doing = "Layouten...";

/** New Language Properties: 18.08.2009*/
ORYX.I18N.SyntaxChecker.MULT_ERRORS = "Mehrere Fehler";

/** New Language Properties: 08.09.2009*/
if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = "Hauptattribute";
ORYX.I18N.PropertyWindow.moreProps = "Mehr Attribute";

/** New Language Properties 01.10.2009 */
if(!ORYX.I18N.SyntaxChecker.BPMN2) ORYX.I18N.SyntaxChecker.BPMN2 = {};

ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "Ein Dateninput darf keine ausgehenden Datenassoziationen haben.";
ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "Ein Datenoutput darf keine eingehenden Datenassoziationen haben.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "Ziele von Ereignis-basierten Gateways dürfen nicht mehr als einen eingehenden Sequenzfluss haben.";

/** New Language Properties 02.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "Ein Ereignis-basiertes Gateway muss 2 oder mehr ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "Wenn Nachrichten-Zwischenereignisse im Diagramm verwendet werden, dann dürfen Receive Tasks nicht verwendet werden und umgekehrt.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "Nur die folgenden Zwischen-Ereignis-Auslöser sind hier zulässig: Nachricht, Signal, Timer, Bedingungs und Mehrfach.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "Die ausgehenden Sequenzflüsse eines Ereignis-Gateways dürfen keinen Bedingungsausdruck besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "Das Gateway erfüllt nicht die Voraussetzungen um den Prozess zu instantiieren. Bitte verwenden Sie ein Start-Ereignis oder setzen Sie die Instanziierungs-Attribute korrekt.";

/** New Language Properties 05.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "Das Gateway muss mehrere eingehende und ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "Das Gateway muss mehrere eingehende aber darf keine mehrfache ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "Das Gateway darf keine mehrfachen eingehenden aber muss mehrfache ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "Ein Gateway muss mindestens einen ausgehenden Sequenzfluss besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "Empfangende Tasks, die in Ereignis-Gateway-Konfigurationen benutzt werden, dürfen keine angehefteten Zwischen-Ereignisse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "Ein Ereignis-Unterprozess darf keinen eingehenden oder ausgehenden Sequenzfluss besitzen.";

/** New Language Properties 13.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "Mindestens ein Ende des Nachrichtenflusses muss mit einem anderen Objekt verbunden sein.";

/** New Language Properties 05.11.2009 */
if(!ORYX.I18N.RESIZE) ORYX.I18N.RESIZE = {};
ORYX.I18N.RESIZE.tipGrow = "Zeichenfläche vergrößern:";
ORYX.I18N.RESIZE.tipShrink = "Zeichenfläche verkleinern:";
ORYX.I18N.RESIZE.N = "Nach oben";
ORYX.I18N.RESIZE.W = "Nach links";
ORYX.I18N.RESIZE.S ="Nach unten";
ORYX.I18N.RESIZE.E ="Nach rechts";

/** New Language Properties 24.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = "Eine Choreographie-Aktivität darf nur eine initiierende Nachricht besitzen.";
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "Ein Nachrichtenfluss ist an dieser Stelle nicht erlaubt.";

/** New Language Properties 27.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "Ein Ereignis-basiertes Gateway, dass nicht instanziierend ist, muss mindestens einen eingehenden Kontrollfluss besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "Eine Choreographie-Aktivität musst genau einen initiierenden Teilnehmer (weiß) besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "Eine Choreographie-Aktivität darf nicht mehr als einen initiierenden Teilnehmer (weiß) besitzen."

ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "Die Kommunikation oder Sub-Konversation muss mit mindestens zwei Teilnehmern verbunden sein.";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "Die Nachrichtenflussquelle muss ein Teilnehmer sein.";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "Das Nachrichtenflussziel muss ein Teilnehmer sein.";
ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "Der Konversationslink muss eine Kommunikation oder Sub-Konversation mit einem Teilnehmer verbinden.";

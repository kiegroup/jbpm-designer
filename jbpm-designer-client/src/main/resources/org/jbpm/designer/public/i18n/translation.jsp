<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<%
    Locale locale= request.getLocale();
    try{
        String localeStr=request.getParameter("locale");
        if(localeStr!= null && localeStr.length()==2){
            locale = new Locale(request.getParameter("locale"));
        }else if(localeStr!= null && localeStr.length()==5){

            locale = new Locale(localeStr.substring(0,2),localeStr.substring(3));
        }

    } catch(Exception e){

    }
%>
<i18n:bundle id="bundle" baseName="org.jbpm.designer.resources.i18n.DesignerConstants"
             locale='<%= locale%>' />
<script type="text/javascript">
/**
 * @author nicolas.peters
 * 
 * Contains all strings for the default language (en-us).
 * Version 1 - 08/29/08
 */
if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = '<i18n:message key="ORYX.I18N.Language">en_us</i18n:message>';

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.title		= ORYX.TITLE;
ORYX.I18N.Oryx.noBackendDefined	= '<i18n:message key="ORYX.I18N.Oryx.noBackendDefined">Caution! \nNo Backend defined.\n The requested model cannot be loaded. Try to load a configuration with a save plugin.</i18n:message>';
//ORYX.I18N.Oryx.pleaseWait 	= "Please wait while loading...";
ORYX.I18N.Oryx.pleaseWait  = '<center>  <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAMPklEQVR4nGL8////f4YhDAAAAAD//2IaaAdQCgAAAAD//xryHgAAAAD//xryHgAAAAD//xryHgAAAAD//xryHgAAAAD//2IZGGv/QxC8AITRjHA5BiZGBkZGwuELAAAA//+0lKENACAAw9r9fzJkCIJAoZhv0pr5/UZbeqQAFPWNAZ0DCiYY2YH3FgAAAP//rNAxEkAwFEXRm6iMyozC6HQWYP9bEgmGn4yvSFRaG7jzzvsZoOSalq3fB1UhyUU8POIXZHNIcMR9JQbHLSfNMNJOM3XXY19MShgDxlalnTEPAAAA//+kkrEKwjAUAI8OXUoFpeDSraOCYyf//09aiMGUak1C8nwOWdz7Awd33O6Fir8CpWzhFnjOibg6wmLZHhPezgRn8E9DejnEv5GwIfGD5gQiaI4gQt0eOQxXutud02WkOfdUwFcV/pr/AAAA//+ckjsKwkAUACdp0giJSO5gGUhqG+9/DBWMEljNavZ91iLYWHqAgYGZvwp8kd8V0vtFnEYe1xPzeCbeLizhjjzDKiYJ3ClwcAMzcAVTsgquilvCRclLRGMgq1A1Ldt9z6470A5Hqk291ihKPgAAAP//jNFLCgIxEAbhSoeAMOIxPID3v4orGRFGFB+ome4/cSW69AYfVX8f+EV/4OHO43rmfjxwm8Zv2fqEcFIX1kQpBSXoyZDPKJymTg/RFUQI1MgSpiBrBkvYsKJ5pU479uOW1+XEcr0hLwZyNswSbwAAAP//fNMxCsJAEEDRv8uIiZ3gGSwsLexyCO/fi2BYiJqgOzszFrbiFf7j/xX4VVrrm3kqPG5X7uOF11SoyxOrFW2NZo65oWpoa1gkIiWc9D0jZTwC84BwiMDcaVqhKbnOdEuhn0e6pSA5s9kf2Q1ntocTkmAlwrrrERE+AAAA//9slEsKwlAQwDIzzmu1ILrt1vufS0W0rVKo7fu4EEHBKyQkfw2UUn5Jx4VHf6M7H+mvJ8ahY56eZKCIIdUebQLqgToEVh7wUOHuqBmqhpogCKYK8rXQ8g4/p0jOmZgSKWXiPMH9QrPdUbcHTJVlHND1Btz5jOIFAAD//3SUwQqDQAxEX5IlW/3/r9QKLbYeRJMeFlwvvQ/MMMO8vwtEBOv65jlPvJaZ7fvB1Che8WHkMYxXSFNBRVBtzMgI8o7PTLpJO3wmIB2v18oiSJOQapxnwLFjpeBe8Vp7OWr8AAAA//98lb0OgzAMhD87TpFaeP+3pIUpEandwTCw9KTb70enuxnwCFrvrJ83+7Zx9EY1Y37NTNOD7zhQgtEb4YPwgGLU53KK9kxcFFFFrxYlT0kkBf5HgJPm3TnXSiklaRUzy1ZV+QEAAP//bJQ5DkBQFEXPe8ZIJDRWoLX/5SgUGhGF8QtfQYSwgZt7cocXwGQMTdviO0oUBHiej6owjgN1VWL6jm2ezpGaBbvOgCVKM9K8IIwTxO647lWdJwAC8n2uf4YrsUc3RAUVPU2r3joHAAAA//+MlbkNgEAQA4c/JEUipAQaoP8ykKAB3rs9dgmAACKcOLNkayS/CpjZdeaq+CCEIHjnGYcedQvrPGOyU5ii4lBxmN841ok0L6jajrJuSExJs5w4ie/lozcmP/Ql+5vx+AkAAP//YkLX9OfvX4bf0AzFxMTM8OnLZ4Zfv34y3Lj7kOHK7QcM7IISDBxCYpDkA0nIDExsHAx/vn9leHxwA8P7+9cY/jOzMPz79xcix8QEx7CCAVkMG8amBiaGXLgwMDAwAAAAAP//QvEAQiMzAysrK8OfP38Zvnz9wnDt5h2GZy9eMRgaGjIIi4gwsAqIMvDIKMGjm+H/PwZGZhYGhv//GZ4cWM/w5vYlhj//GRj+/P7J8A+aoWHmExMLMHXoGBsAAAAA///CaKwwQjMcMxMzw7efPxjuP3jI8OLFSwZdXR0GcXExhl+/fjGwMDEy8IhJM/DLqzH8h4Y0w///0HbKf4Yn+9YwvHtwg+HPP0aG379+MPz794+go8kFAAAAAP//rNKxDcIwEEDRf45kiJKO0FAZKlbPDpnGyAUiFeXZvmOAtPwV3j8IAIQgmDvvfSfnF9dl4ZESqkqMJ8Zx4hwjl/Rkvt2xpiCCuyHDgPdK2Va+n0LtTquKWT98/Y9+AAAA//+s1j0SQDAUAOGNEUkotFpKrVu7g8swWoXCXx5RqMwo7Rm2+D7A/YBsF6HvB7Z1o6pKtNYoFWGMxTqHcynWGIq6IclywikoFOEKRLHGzxNj17KvC94fiMhrp7+6AQAA///C4gFIxvzw6TPDy5cvGXj5eBkkxcUZ/v79y8DKxsrAysaOqFBYWBi4+YUYRDWNGf7//8cAa4X+//efgZmDi+Hrg2sMz49tYfj9n4Hh988fDP/+QmKBmp4AAAAA//+s1z0OQEAUAOGRt0/Y6CSoJEr3L11Bo3UCJEu9WX/ddjpHmORr5nN5lm1ld466qrA2B0A1RVNFjCIiGKMID2XXUzQtV/CR4XNfSGZx48AxT5wkhOAjpb8iXgAAAP//wuqB/wwMDN++fmUQFRZmUJCTY2BgYGBgZmFhYGFlgziaCVq+Q0ssNjZ2BjFtMwZGZmYG1NqHkYHh3z+GF/tXMXz7+J7h9+8/DH9+/6ZqDAAAAAD//6SYsRGCQBAA15//OSAktwxrcGzC+miBMoihAxKMfO4PzsBhDAxpYZPd2b8WOuiUoqiurDljRYkpIVIjVUWM6UfanX3bMHfGvmOZBgIXdn3jJeNm2Gumvd25Pp5IAKmbr63D+afwAQAA///CWowyMDBAqm1GZgZmaNOVnZ2TgZWVjYGZiRmlbIZV7SwsLAziOmYMjCyskHYMLD/8/8vAzMHN8P7MHoYP9y5Dk9Ivhn9//1KleAUAAAD//8IaBDCHsbKyMHCwczCwc3AysLKxM7CwsDAwYgk1JiYmBoZ/fxn4ZZQZ+GRUUPICxC9MDP9+/WR4tXcVw4/v3xj+/PrN8PfvH6rkBQAAAAD//6zVsRFAUBAG4b3/7h4j14RY+zqQyLUgE2EQC16mhm92tmpoEh5JZqGUhsxA7tUryoyIpB9GzPRR4LnxtmNfF7Z54jJxHv8E/QIAAP//whkDsFhgZmFhYIZ2SnA5npGREVIL//vDICCvzsAjLsvw7/cvlKbzf4b/DEwsLAxvDq1j+Pr2JcPvv/8Y/vz+TXEyAgAAAP//wh0DJLRHkAEbByeDsIYxtImBBP7/Y2Bk5WD4+eoRw5tjmxn+MEBjgcK6AQAAAP//otrQIqwNxfDvH4OQqj4DO78wpHZG9vT/fwxMbJwM745tYfjy7B6kwfcHFgvkeQAAAAD//6Lq2CgkM/9j4BIQZhBQ1mX4i5aMGP7/Z2BkYWX48/E1w+uDayE19K+fDH///mX4R2YsAAAAAP//ovrgLqS3xMAgomnCwMzKhumof/8YmDm4GT6c2snw5eENhj8MjAx/f/9i+PeXvHYSAAAA//+ivgcYGRn+//3LwCejzMAtqcDw7/dPjMzMwMzC8Pfre4bXh9Yy/PnHQFGJBAAAAP//ookHGBn+M7CysUEy89+/kBIKeZz03z8GJg5uho+ndzF8eXid4Q8jE7SJQXqJBAAAAP//osn8ACMjEwPD//8MQmqGDCycPAy/v3xg+P/7FwN0qBnSQWdhZ/j75SPD64NrIJn51y+Gv3//Mvz/T1pSAgAAAP//os0EB7QRxykkyqAeVcQgZuXDwC4ux8DAxMzw/+dXhr/fPjP8/faJgeHfX4Z3h9czfH1yh+EvIxPD3z9/SE5CAAAAAP//oskEByMjI2Rs8x8jg4CCFgOHlDLD1/dvGL68eMTw/dl9hl+vHjL8fv+K4f/vnwwcsuoMTJy8DH///GL4z8oCzwfEjmAAAAAA//+0mbsRACAMQrmLJ/vPGyIWfmobmYCOB3w7OI4RbarNzJW+NkqFUYIBRDSEBXJV1U7e0epFEwAA//+i2RQTLASZWVgZ2BgZGZiZWRhYoGNN/xj+M/xjYoFm2n8MzGwcDKxsbHibK7gAAAAA//+i6RQTzGhIbPxj+PcPgf8j9Y9hYz/MzPgbjNgAAAAA//+i+RwZsvGwwV5ISQMRYWBggA47ktbmggEAAAAA//+i/SQfGiBkHSmOZ2BgYAAAAAD//6L7NCupDiQEAAAAAP//GvIT3QAAAAD//xryHgAAAAD//wMAIbECF8xAqXsAAAAASUVORK5CYII=" width="24px" height="24px"> <b><i18n:message key="ORYX.I18N.Oryx.pleaseWait"></i18n:message></b></center>';
ORYX.I18N.Oryx.notLoggedOn = '<i18n:message key="ORYX.I18N.Oryx.notLoggedOn">Not logged on</i18n:message>';
ORYX.I18N.Oryx.editorOpenTimeout = '<i18n:message key="ORYX.I18N.Oryx.editorOpenTimeout">The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.</i18n:message>';

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = '<i18n:message key="ORYX.I18N.AddDocker.group">Docker</i18n:message>';
ORYX.I18N.AddDocker.add = '<i18n:message key="ORYX.I18N.AddDocker.add">Add Docker</i18n:message>';
ORYX.I18N.AddDocker.addDesc = '<i18n:message key="ORYX.I18N.AddDocker.addDesc">Add a Docker to an edge, by clicking on it</i18n:message>';
ORYX.I18N.AddDocker.del = '<i18n:message key="ORYX.I18N.AddDocker.del">Delete Docker</i18n:message>';
ORYX.I18N.AddDocker.delDesc = '<i18n:message key="ORYX.I18N.AddDocker.delDesc">Delete a Docker</i18n:message>';

if(!ORYX.I18N.ShapeConnector) ORYX.I18N.ShapeConnector = {};

ORYX.I18N.ShapeConnector.group = '<i18n:message key="ORYX.I18N.ShapeConnector.group">Connector</i18n:message>';
ORYX.I18N.ShapeConnector.add = '<i18n:message key="ORYX.I18N.ShapeConnector.add">Connect Shapes</i18n:message>';
ORYX.I18N.ShapeConnector.addDesc = '<i18n:message key="ORYX.I18N.ShapeConnector.addDesc">Connect several nodes by marking them in the desired order</i18n:message>';

if(!ORYX.I18N.SSExtensionLoader) ORYX.I18N.SSExtensionLoader = {};

ORYX.I18N.SSExtensionLoader.group = '<i18n:message key="ORYX.I18N.SSExtensionLoader.group">Stencil Set</i18n:message>';
ORYX.I18N.SSExtensionLoader.add = '<i18n:message key="ORYX.I18N.SSExtensionLoader.add">Add Stencil Set Extension</i18n:message>';
ORYX.I18N.SSExtensionLoader.addDesc = '<i18n:message key="ORYX.I18N.SSExtensionLoader.addDesc">Add a stencil set extension</i18n:message>';
ORYX.I18N.SSExtensionLoader.loading = '<i18n:message key="ORYX.I18N.SSExtensionLoader.loading">Loading Stencil Set Extension</i18n:message>';
ORYX.I18N.SSExtensionLoader.noExt = '<i18n:message key="ORYX.I18N.SSExtensionLoader.noExt">There are no extensions available or all available extensions are already loaded.</i18n:message>';
ORYX.I18N.SSExtensionLoader.failed1 = '<i18n:message key="ORYX.I18N.SSExtensionLoader.failed1">Loading stencil set extensions configuration failed. The response is not a valid configuration file.</i18n:message>';
ORYX.I18N.SSExtensionLoader.failed2 = '<i18n:message key="ORYX.I18N.SSExtensionLoader.failed2">Loading stencil set extension configuration file failed. The request returned an error.</i18n:message>';
ORYX.I18N.SSExtensionLoader.panelTitle = '<i18n:message key="ORYX.I18N.SSExtensionLoader.panelTitle">Stencil Set Extensions</i18n:message>';
ORYX.I18N.SSExtensionLoader.panelText = '<i18n:message key="ORYX.I18N.SSExtensionLoader.panelText">Select the stencil set extensions you want to load.</i18n:message>';

if(!ORYX.I18N.AdHocCC) ORYX.I18N.AdHocCC = {};

ORYX.I18N.AdHocCC.group = '<i18n:message key="ORYX.I18N.AdHocCC.group">Ad Hoc</i18n:message>';
ORYX.I18N.AdHocCC.compl = '<i18n:message key="ORYX.I18N.AdHocCC.compl">Edit Completion Condition</i18n:message>';
ORYX.I18N.AdHocCC.complDesc = '<i18n:message key="ORYX.I18N.AdHocCC.complDesc">Edit an Ad Hoc Activity\\\'s Completion Condition</i18n:message>';
ORYX.I18N.AdHocCC.notOne = '<i18n:message key="ORYX.I18N.AdHocCC.notOne">Not exactly one element selected!</i18n:message>';
ORYX.I18N.AdHocCC.nodAdHocCC = '<i18n:message key="ORYX.I18N.AdHocCC.nodAdHocCC">Selected element has no ad hoc completion condition!</i18n:message>';
ORYX.I18N.AdHocCC.selectTask = '<i18n:message key="ORYX.I18N.AdHocCC.selectTask">Select a task...</i18n:message>';
ORYX.I18N.AdHocCC.selectState = '<i18n:message key="ORYX.I18N.AdHocCC.selectState">Select a state...</i18n:message>';
ORYX.I18N.AdHocCC.addExp = '<i18n:message key="ORYX.I18N.AdHocCC.addExp">Add Expression</i18n:message>';
ORYX.I18N.AdHocCC.selectDataField = '<i18n:message key="ORYX.I18N.AdHocCC.selectDataField">Select a data field...</i18n:message>';
ORYX.I18N.AdHocCC.enterEqual = '<i18n:message key="ORYX.I18N.AdHocCC.enterEqual">Enter a value that must equal...</i18n:message>';
ORYX.I18N.AdHocCC.and = '<i18n:message key="ORYX.I18N.AdHocCC.and">and</i18n:message>';
ORYX.I18N.AdHocCC.or = '<i18n:message key="ORYX.I18N.AdHocCC.or">or</i18n:message>';
ORYX.I18N.AdHocCC.not = '<i18n:message key="ORYX.I18N.AdHocCC.not">not</i18n:message>';
ORYX.I18N.AdHocCC.clearCC = '<i18n:message key="ORYX.I18N.AdHocCC.clearCC">Clear Completion Condition</i18n:message>';
ORYX.I18N.AdHocCC.editCC = '<i18n:message key="ORYX.I18N.AdHocCC.editCC">Edit Ad-Hoc Completion Condtions</i18n:message>';
ORYX.I18N.AdHocCC.addExecState = '<i18n:message key="ORYX.I18N.AdHocCC.addExecState">Add Execution State Expression: </i18n:message>';
ORYX.I18N.AdHocCC.addDataExp = '<i18n:message key="ORYX.I18N.AdHocCC.addDataExp">Add Data Expression: </i18n:message>';
ORYX.I18N.AdHocCC.addLogOp = '<i18n:message key="ORYX.I18N.AdHocCC.addLogOp">Add Logical Operators: </i18n:message>';
ORYX.I18N.AdHocCC.curCond = '<i18n:message key="ORYX.I18N.AdHocCC.curCond">Current Completion Condition: </i18n:message>';

if(!ORYX.I18N.AMLSupport) ORYX.I18N.AMLSupport = {};

ORYX.I18N.AMLSupport.group = '<i18n:message key="ORYX.I18N.AMLSupport.group">EPC</i18n:message>';
ORYX.I18N.AMLSupport.imp = '<i18n:message key="ORYX.I18N.AMLSupport.imp">Import AML file</i18n:message>';
ORYX.I18N.AMLSupport.impDesc = '<i18n:message key="ORYX.I18N.AMLSupport.impDesc">Import an Aris 7 AML file</i18n:message>';
ORYX.I18N.AMLSupport.failed = '<i18n:message key="ORYX.I18N.AMLSupport.failed">Importing AML file failed. Please check, if the selected file is a valid AML file. Error message: </i18n:message>';
ORYX.I18N.AMLSupport.failed2 = '<i18n:message key="ORYX.I18N.AMLSupport.failed2">Importing AML file failed: </i18n:message>';
ORYX.I18N.AMLSupport.noRights = '<i18n:message key="ORYX.I18N.AMLSupport.noRights">You have no rights to import multiple EPC-Diagrams (Login required).</i18n:message>';
ORYX.I18N.AMLSupport.panelText = '<i18n:message key="ORYX.I18N.AMLSupport.panelText">Select an AML (.xml) file to import.</i18n:message>';
ORYX.I18N.AMLSupport.file = '<i18n:message key="ORYX.I18N.AMLSupport.file">File</i18n:message>';
ORYX.I18N.AMLSupport.importBtn = '<i18n:message key="ORYX.I18N.AMLSupport.importBtn">Import AML-File</i18n:message>';
ORYX.I18N.AMLSupport.get = '<i18n:message key="ORYX.I18N.AMLSupport.get">Get diagrams...</i18n:message>';
ORYX.I18N.AMLSupport.close = '<i18n:message key="ORYX.I18N.AMLSupport.close">Close</i18n:message>';
ORYX.I18N.AMLSupport.title = '<i18n:message key="ORYX.I18N.AMLSupport.title">Title</i18n:message>';
ORYX.I18N.AMLSupport.selectDiagrams = '<i18n:message key="ORYX.I18N.AMLSupport.selectDiagrams">Select the diagram(s) you want to import. <br/> If one model is selected, it will be imported in the current editor, if more than one is selected, those models will directly be stored in the repository.</i18n:message>';
ORYX.I18N.AMLSupport.impText = '<i18n:message key="ORYX.I18N.AMLSupport.impText">Import</i18n:message>';
ORYX.I18N.AMLSupport.impProgress = '<i18n:message key="ORYX.I18N.AMLSupport.impProgress">Importing...</i18n:message>';
ORYX.I18N.AMLSupport.cancel = '<i18n:message key="ORYX.I18N.AMLSupport.cancel">Cancel</i18n:message>';
ORYX.I18N.AMLSupport.name = '<i18n:message key="ORYX.I18N.AMLSupport.name">Name</i18n:message>';
ORYX.I18N.AMLSupport.allImported = '<i18n:message key="ORYX.I18N.AMLSupport.allImported">All imported diagrams.</i18n:message>';
ORYX.I18N.AMLSupport.ok = '<i18n:message key="ORYX.I18N.AMLSupport.ok">Ok</i18n:message>';

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};


ORYX.I18N.Arrangement.groupZ = '<i18n:message key="ORYX.I18N.Arrangement.groupZ">Z-Order</i18n:message>';
ORYX.I18N.Arrangement.btf = '<i18n:message key="ORYX.I18N.Arrangement.btf">Bring To Front</i18n:message>';
ORYX.I18N.Arrangement.btfDesc = '<i18n:message key="ORYX.I18N.Arrangement.btfDesc">Bring to Front</i18n:message>';
ORYX.I18N.Arrangement.btb = '<i18n:message key="ORYX.I18N.Arrangement.btb">Bring To Back</i18n:message>';
ORYX.I18N.Arrangement.btbDesc = '<i18n:message key="ORYX.I18N.Arrangement.btbDesc">Bring To Back</i18n:message>';
ORYX.I18N.Arrangement.bf = '<i18n:message key="ORYX.I18N.Arrangement.bf">Bring Forward</i18n:message>';
ORYX.I18N.Arrangement.bfDesc = '<i18n:message key="ORYX.I18N.Arrangement.bfDesc">Bring Forward</i18n:message>';
ORYX.I18N.Arrangement.bb = '<i18n:message key="ORYX.I18N.Arrangement.bb">Bring Backward</i18n:message>';
ORYX.I18N.Arrangement.bbDesc = '<i18n:message key="ORYX.I18N.Arrangement.bbDesc">Bring Backward</i18n:message>';
ORYX.I18N.Arrangement.groupA = '<i18n:message key="ORYX.I18N.Arrangement.groupA">Alignment</i18n:message>';
ORYX.I18N.Arrangement.ab = '<i18n:message key="ORYX.I18N.Arrangement.ab">Alignment Bottom</i18n:message>';
ORYX.I18N.Arrangement.abDesc = '<i18n:message key="ORYX.I18N.Arrangement.abDesc">Bottom</i18n:message>';
ORYX.I18N.Arrangement.am = '<i18n:message key="ORYX.I18N.Arrangement.am">Alignment Middle</i18n:message>';
ORYX.I18N.Arrangement.amDesc = '<i18n:message key="ORYX.I18N.Arrangement.amDesc">Middle</i18n:message>';
ORYX.I18N.Arrangement.at = '<i18n:message key="ORYX.I18N.Arrangement.at">Alignment Top</i18n:message>';
ORYX.I18N.Arrangement.atDesc = '<i18n:message key="ORYX.I18N.Arrangement.atDesc">Top</i18n:message>';
ORYX.I18N.Arrangement.al = '<i18n:message key="ORYX.I18N.Arrangement.al">Alignment Left</i18n:message>';
ORYX.I18N.Arrangement.alDesc = '<i18n:message key="ORYX.I18N.Arrangement.alDesc">Left</i18n:message>';
ORYX.I18N.Arrangement.ac = '<i18n:message key="ORYX.I18N.Arrangement.ac">Alignment Center</i18n:message>';
ORYX.I18N.Arrangement.acDesc = '<i18n:message key="ORYX.I18N.Arrangement.acDesc">Center</i18n:message>';
ORYX.I18N.Arrangement.ar = '<i18n:message key="ORYX.I18N.Arrangement.ar">Alignment Right</i18n:message>';
ORYX.I18N.Arrangement.arDesc = '<i18n:message key="ORYX.I18N.Arrangement.arDesc">Right</i18n:message>';
ORYX.I18N.Arrangement.as = '<i18n:message key="ORYX.I18N.Arrangement.as">Alignment Same Size</i18n:message>';
ORYX.I18N.Arrangement.asDesc = '<i18n:message key="ORYX.I18N.Arrangement.asDesc">Same Size</i18n:message>';

if(!ORYX.I18N.BPELSupport) ORYX.I18N.BPELSupport = {};


ORYX.I18N.BPELSupport.group = '<i18n:message key="ORYX.I18N.BPELSupport.group">BPEL</i18n:message>';
ORYX.I18N.BPELSupport.exp = '<i18n:message key="ORYX.I18N.BPELSupport.exp">Export BPEL</i18n:message>';
ORYX.I18N.BPELSupport.expDesc = '<i18n:message key="ORYX.I18N.BPELSupport.expDesc">Export diagram to BPEL</i18n:message>';
ORYX.I18N.BPELSupport.imp = '<i18n:message key="ORYX.I18N.BPELSupport.imp">Import BPEL</i18n:message>';
ORYX.I18N.BPELSupport.impDesc = '<i18n:message key="ORYX.I18N.BPELSupport.impDesc">Import a BPEL file</i18n:message>';
ORYX.I18N.BPELSupport.selectFile = '<i18n:message key="ORYX.I18N.BPELSupport.selectFile">Select a BPEL file to import</i18n:message>';
ORYX.I18N.BPELSupport.file = '<i18n:message key="ORYX.I18N.BPELSupport.file">file</i18n:message>';
ORYX.I18N.BPELSupport.impPanel = '<i18n:message key="ORYX.I18N.BPELSupport.impPanel">Import BPEL file</i18n:message>';
ORYX.I18N.BPELSupport.impBtn = '<i18n:message key="ORYX.I18N.BPELSupport.impBtn">Import</i18n:message>';
ORYX.I18N.BPELSupport.content = '<i18n:message key="ORYX.I18N.BPELSupport.content">content</i18n:message>';
ORYX.I18N.BPELSupport.close = '<i18n:message key="ORYX.I18N.BPELSupport.close">Close</i18n:message>';
ORYX.I18N.BPELSupport.error = '<i18n:message key="ORYX.I18N.BPELSupport.error">Error</i18n:message>';
ORYX.I18N.BPELSupport.progressImp = '<i18n:message key="ORYX.I18N.BPELSupport.progressImp">Import...</i18n:message>';
ORYX.I18N.BPELSupport.progressExp = '<i18n:message key="ORYX.I18N.BPELSupport.progressExp">Export...</i18n:message>';
ORYX.I18N.BPELSupport.impFailed = '<i18n:message key="ORYX.I18N.BPELSupport.impFailed">An error while importing occurs! <br/>Please check error message: <br/><br/></i18n:message>';
ORYX.I18N.BPELSupport.selectBPELFile='<i18n:message key="ORYX.I18N.BPELSupport.selectBPELFile">Select a BPEL (.bpel) file and transform it to BPMN.</i18n:message>';
ORYX.I18N.BPELSupport.uploadBPELFile='<i18n:message key="ORYX.I18N.BPELSupport.uploadBPELFile">Upload BPEL File</i18n:message>';
ORYX.I18N.BPELSupport.transformBPELToBPMN='<i18n:message key="ORYX.I18N.BPELSupport.transformBPELToBPMN">Transform BPEL into BPMN</i18n:message>';
ORYX.I18N.BPELSupport.transformBPELToBPMN_desc='<i18n:message key="ORYX.I18N.BPELSupport.transformBPELToBPMN_desc">Transform a BPEL process into its BPMN representation</i18n:message>';
ORYX.I18N.BPELSupport.submit='<i18n:message key="ORYX.I18N.BPELSupport.submit">Submit</i18n:message>';
ORYX.I18N.BPELSupport.transforming='<i18n:message key="ORYX.I18N.BPELSupport.transforming">Transforming...</i18n:message>';
ORYX.I18N.BPELSupport.errorImporting='<i18n:message key="ORYX.I18N.BPELSupport.errorImporting">The BPEL file could not be imported.</i18n:message>';
ORYX.I18N.BPELSupport.noComply='<i18n:message key="ORYX.I18N.BPELSupport.noComply">Your BPEL file does not comply with the XML schema definition.</i18n:message>';
ORYX.I18N.BPELSupport.errorMessage='<i18n:message key="ORYX.I18N.BPELSupport.errorMessage">Error message:</i18n:message>';


if(!ORYX.I18N.BPELLayout) ORYX.I18N.BPELLayout = {};

ORYX.I18N.BPELLayout.group = '<i18n:message key="ORYX.I18N.BPELLayout.group">BPELLayout</i18n:message>';
ORYX.I18N.BPELLayout.disable = '<i18n:message key="ORYX.I18N.BPELLayout.disable">disable layout</i18n:message>';
ORYX.I18N.BPELLayout.disDesc = '<i18n:message key="ORYX.I18N.BPELLayout.disDesc">disable auto layout plug-in</i18n:message>';
ORYX.I18N.BPELLayout.enable = '<i18n:message key="ORYX.I18N.BPELLayout.enable">enable layout</i18n:message>';
ORYX.I18N.BPELLayout.enDesc = '<i18n:message key="ORYX.I18N.BPELLayout.enDesc">enable auto layout plug-in</i18n:message>';

if(!ORYX.I18N.BPEL4Chor2BPELSupport) ORYX.I18N.BPEL4Chor2BPELSupport = {};

ORYX.I18N.BPEL4Chor2BPELSupport.group = '<i18n:message key="ORYX.I18N.BPEL4Chor2BPELSupport.group">BPEL4Chor</i18n:message>';
ORYX.I18N.BPEL4Chor2BPELSupport.exp = '<i18n:message key="ORYX.I18N.BPEL4Chor2BPELSupport.exp">Export to BPEL</i18n:message>';
ORYX.I18N.BPEL4Chor2BPELSupport.expDesc = '<i18n:message key="ORYX.I18N.BPEL4Chor2BPELSupport.expDesc">Export diagram to BPEL</i18n:message>';

if(!ORYX.I18N.BPEL4ChorSupport) ORYX.I18N.BPEL4ChorSupport = {};

ORYX.I18N.BPEL4ChorSupport.group = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.group">BPEL4Chor</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.exp = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.exp">Export BPEL4Chor</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.expDesc = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.expDesc">Export diagram to BPEL4Chor</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.imp = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.imp">Import BPEL4Chor</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.impDesc = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.impDesc">Import a BPEL4Chor file</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.gen = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.gen">BPEL4Chor generator</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.genDesc = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.genDesc">generate values of some BPEL4Chor properties if they are already known(e.g. sender of messageLink)</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.selectFile = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.selectFile">Select a BPEL4Chor file to import</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.file = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.file">file</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.impPanel = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.impPanel">Import BPEL4Chor file</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.impBtn = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.impBtn">Import</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.content = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.content">content</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.close = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.close">Close</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.error = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.error">Error</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.progressImp = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.progressImp">Import...</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.progressExp = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.progressExp">Export...</i18n:message>';
ORYX.I18N.BPEL4ChorSupport.impFailed = '<i18n:message key="ORYX.I18N.BPEL4ChorSupport.impFailed">An error while importing occurs! <br/>Please check error message: <br/><br/></i18n:message>';

if(!ORYX.I18N.Bpel4ChorTransformation) ORYX.I18N.Bpel4ChorTransformation = {};

ORYX.I18N.Bpel4ChorTransformation.group = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.group">Export</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.exportBPEL = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.exportBPEL">Export BPEL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.exportBPELDesc = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.exportBPELDesc">Export diagram to BPEL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.exportXPDL = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.exportXPDL">Export XPDL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.exportXPDLDesc = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.exportXPDLDesc">Export diagram to XPDL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.warning = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.warning">Warning</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.wrongValue = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.wrongValue">The changed name must have the value \"1\" to avoid errors during the transformation to BPEL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.loopNone = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.loopNone">The loop type of the receive task must be \"None\" to be transformable to BPEL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.error = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.error">Error</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.noSource = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.noSource">1 with id 2 has no source object.</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.noTarget = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.noTarget">1 with id 2 has no target object.</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.transCall = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.transCall">An error occured during the transformation call. 1:2</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.loadingXPDL4ChorExport = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.loadingXPDL4ChorExport">Export to XPDL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.loadingBPEL4ChorExport = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.loadingBPEL4ChorExport">Export to BPEL4Chor</i18n:message>';
ORYX.I18N.Bpel4ChorTransformation.noGen = '<i18n:message key="ORYX.I18N.Bpel4ChorTransformation.noGen">The transformation input could not be generated: 1<br>n2<br></i18n:message>';

ORYX.I18N.BPMN2PNConverter = {
  name: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_name">Convert to Petri net</i18n:message>',
  desc: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_desc">Converts BPMN diagrams to Petri nets</i18n:message>',
  group: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_group">Export</i18n:message>',
  error: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_error">Error</i18n:message>',
  errors: {
    server: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_server">Couldn\\\'t import BPNM diagram.</i18n:message>',
    noRights: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_noRights">Don\\\'t you have read permissions on given model?</i18n:message>',
    notSaved: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_notSaved">Model must be saved and reopened for using Petri net exporter!</i18n:message>'
  },
  progress: {
      status: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_status">Status</i18n:message>',
      importingModel: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_importingModel">Importing BPMN Model</i18n:message>',
      fetchingModel: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_fetchingModel">Fetching</i18n:message>',
      convertingModel: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_convertingModel">Converting</i18n:message>',
      renderingModel: '<i18n:message key="ORYX.I18N.BPMN2PNConverter_renderingModel">Rendering</i18n:message>'
  }
}

if(!ORYX.I18N.TransformationDownloadDialog) ORYX.I18N.TransformationDownloadDialog = {};

ORYX.I18N.TransformationDownloadDialog.error = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.error">Error</i18n:message>';
ORYX.I18N.TransformationDownloadDialog.noResult = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.noResult">The transformation service did not return a result.</i18n:message>';
ORYX.I18N.TransformationDownloadDialog.errorParsing = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.errorParsing">Error During the Parsing of the Diagram.</i18n:message>';
ORYX.I18N.TransformationDownloadDialog.transResult = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.transResult">Transformation Results</i18n:message>';
ORYX.I18N.TransformationDownloadDialog.showFile = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.showFile">Show the result file</i18n:message>';
ORYX.I18N.TransformationDownloadDialog.downloadFile = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.downloadFile">Download the result file</i18n:message>';
ORYX.I18N.TransformationDownloadDialog.downloadAll = '<i18n:message key="ORYX.I18N.TransformationDownloadDialog.downloadAll">Download all result files</i18n:message>';

if(!ORYX.I18N.DesynchronizabilityOverlay) ORYX.I18N.DesynchronizabilityOverlay = {};
//TODO desynchronizability is not a correct term
ORYX.I18N.DesynchronizabilityOverlay.group = '<i18n:message key="ORYX.I18N.DesynchronizabilityOverlay.group">Overlay</i18n:message>';
ORYX.I18N.DesynchronizabilityOverlay.name = '<i18n:message key="ORYX.I18N.DesynchronizabilityOverlay.name">Desynchronizability Checker</i18n:message>';
ORYX.I18N.DesynchronizabilityOverlay.desc = '<i18n:message key="ORYX.I18N.DesynchronizabilityOverlay.desc">Desynchronizability Checker</i18n:message>';
ORYX.I18N.DesynchronizabilityOverlay.sync = '<i18n:message key="ORYX.I18N.DesynchronizabilityOverlay.sync">The net is desynchronizable.</i18n:message>';
ORYX.I18N.DesynchronizabilityOverlay.error = '<i18n:message key="ORYX.I18N.DesynchronizabilityOverlay.error">The net has 1 syntax errors.</i18n:message>';
ORYX.I18N.DesynchronizabilityOverlay.invalid = '<i18n:message key="ORYX.I18N.DesynchronizabilityOverlay.invalid">Invalid answer from server.</i18n:message>';

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

ORYX.I18N.Edit.group = '<i18n:message key="ORYX.I18N.Edit.group">Edit</i18n:message>';
ORYX.I18N.Edit.cut = '<i18n:message key="ORYX.I18N.Edit.cut">Cut</i18n:message>';
ORYX.I18N.Edit.cutDesc = '<i18n:message key="ORYX.I18N.Edit.cutDesc">Cut the selection into a Designer clipboard</i18n:message>';
ORYX.I18N.Edit.copy = '<i18n:message key="ORYX.I18N.Edit.copy">Copy</i18n:message>';
ORYX.I18N.Edit.copyDesc = '<i18n:message key="ORYX.I18N.Edit.copyDesc">Copy the selection into an Designer clipboard</i18n:message>';
ORYX.I18N.Edit.paste = '<i18n:message key="ORYX.I18N.Edit.paste">Paste</i18n:message>';
ORYX.I18N.Edit.pasteDesc = '<i18n:message key="ORYX.I18N.Edit.pasteDesc">Paste the Designer clipboard to the canvas</i18n:message>';
ORYX.I18N.Edit.del = '<i18n:message key="ORYX.I18N.Edit.del">Delete</i18n:message>';
ORYX.I18N.Edit.delDesc = '<i18n:message key="ORYX.I18N.Edit.delDesc">Delete all selected shapes</i18n:message>';

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

ORYX.I18N.EPCSupport.group = '<i18n:message key="ORYX.I18N.EPCSupport.group">EPC</i18n:message>';
ORYX.I18N.EPCSupport.exp = '<i18n:message key="ORYX.I18N.EPCSupport.exp">Export EPC</i18n:message>';
ORYX.I18N.EPCSupport.expDesc = '<i18n:message key="ORYX.I18N.EPCSupport.expDesc">Export diagram to EPML</i18n:message>';
ORYX.I18N.EPCSupport.imp = '<i18n:message key="ORYX.I18N.EPCSupport.imp">Import EPC</i18n:message>';
ORYX.I18N.EPCSupport.impDesc = '<i18n:message key="ORYX.I18N.EPCSupport.impDesc">Import an EPML file</i18n:message>';
ORYX.I18N.EPCSupport.progressExp = '<i18n:message key="ORYX.I18N.EPCSupport.progressExp">Exporting model</i18n:message>';
ORYX.I18N.EPCSupport.selectFile = '<i18n:message key="ORYX.I18N.EPCSupport.selectFile">Select an EPML (.empl) file to import.</i18n:message>';
ORYX.I18N.EPCSupport.file = '<i18n:message key="ORYX.I18N.EPCSupport.file">File</i18n:message>';
ORYX.I18N.EPCSupport.impPanel = '<i18n:message key="ORYX.I18N.EPCSupport.impPanel">Import EPML File</i18n:message>';
ORYX.I18N.EPCSupport.impBtn = '<i18n:message key="ORYX.I18N.EPCSupport.impBtn">Import</i18n:message>';
ORYX.I18N.EPCSupport.close = '<i18n:message key="ORYX.I18N.EPCSupport.close">Close</i18n:message>';
ORYX.I18N.EPCSupport.error = '<i18n:message key="ORYX.I18N.EPCSupport.error">Error</i18n:message>';
ORYX.I18N.EPCSupport.progressImp = '<i18n:message key="ORYX.I18N.EPCSupport.progressImp">Import...</i18n:message>';
ORYX.I18N.EPCSupport.epcToBPMN='<i18n:message key="ORYX.I18N.EPCSupport.epcToBPMN">EPC to BPMN transform</i18n:message>';
ORYX.I18N.EPCSupport.epcToBPMN_desc='<i18n:message key="ORYX.I18N.EPCSupport.epcToBPMN_desc">EPC to BPMN transform</i18n:message>';
ORYX.I18N.EPCSupport.pleaseWait='<i18n:message key="ORYX.I18N.EPCSupport.pleaseWait">Please wait while importing..</i18n:message>';
ORYX.I18N.EPCSupport.errorImporting='<i18n:message key="ORYX.I18N.EPCSupport.errorImporting">An Error is occured while importing!</i18n:message>';
ORYX.I18N.EPCSupport.requestFailed='<i18n:message key="ORYX.I18N.EPCSupport.requestFailed">Request to server failed!</i18n:message>';
ORYX.I18N.EPCSupport.setTheURL='<i18n:message key="ORYX.I18N.EPCSupport.setTheURL">For the import and transformation from EPC to BPMN please set the URL to the EPC model.</i18n:message>';
ORYX.I18N.EPCSupport.url='<i18n:message key="ORYX.I18N.EPCSupport.url">URL</i18n:message>';
ORYX.I18N.EPCSupport.eventMapping='<i18n:message key="ORYX.I18N.EPCSupport.eventMapping">Event-Mapping</i18n:message>';
ORYX.I18N.EPCSupport.giveKeyword='<i18n:message key="ORYX.I18N.EPCSupport.giveKeyword">If you like to transform individual event from EPC to event in BPMN, please give keyword regarding to these (separated with a \\\';\\\')</i18n:message>';
ORYX.I18N.EPCSupport.organization='<i18n:message key="ORYX.I18N.EPCSupport.organization">Organization</i18n:message>';
ORYX.I18N.EPCSupport.askIfOUandRolesMapped='<i18n:message key="ORYX.I18N.EPCSupport.askIfOUandRolesMapped">Should the organizational units and roles mapped to a pool/lane? (Required Auto-Layout)</i18n:message>';
ORYX.I18N.EPCSupport.autoLayout='<i18n:message key="ORYX.I18N.EPCSupport.autoLayout">Auto-Layout</i18n:message>';
ORYX.I18N.EPCSupport.autoLayout_desc='<i18n:message key="ORYX.I18N.EPCSupport.autoLayout_desc">By enable the Auto-Layout, the model will be auto layouted afterwards with the Auto-Layout Plugin. (Needs a while)</i18n:message>';
ORYX.I18N.EPCSupport.advancedSettings='<i18n:message key="ORYX.I18N.EPCSupport.advancedSettings">Advanced Settings</i18n:message>';
ORYX.I18N.EPCSupport.transformEPCToBPMN='<i18n:message key="ORYX.I18N.EPCSupport.transformEPCToBPMN">Transform EPC to BPMN</i18n:message>';
ORYX.I18N.EPCSupport.autoLayouting='<i18n:message key="ORYX.I18N.EPCSupport.autoLayouting">Auto Layouting</i18n:message>';
ORYX.I18N.EPCSupport.autoLayoutPlugin='<i18n:message key="ORYX.I18N.EPCSupport.autoLayoutPlugin">AutoLayout</i18n:message>';
ORYX.I18N.EPCSupport.autoLayoutPlugin_desc='<i18n:message key="ORYX.I18N.EPCSupport.autoLayoutPlugin_desc">Automatic layouting</i18n:message>';
ORYX.I18N.EPCSupport.recomendationBeforeAutoLayouting='<i18n:message key="ORYX.I18N.EPCSupport.recomendationBeforeAutoLayouting">It is recommended to save the current model before running the automatic layouting, since it may produce unwanted results!\\nStart layouting?</i18n:message>';
ORYX.I18N.EPCSupport.errorOccurredServer='<i18n:message key="ORYX.I18N.EPCSupport.errorOccurredServer">An error occurred in the server</i18n:message>';
ORYX.I18N.EPCSupport.failAutoLayouting='<i18n:message key="ORYX.I18N.EPCSupport.failAutoLayouting">Layouting failed.</i18n:message>';


if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.exp = '<i18n:message key="ORYX.I18N.ERDFSupport.exp">Export to ERDF</i18n:message>';
ORYX.I18N.ERDFSupport.expDesc = '<i18n:message key="ORYX.I18N.ERDFSupport.expDesc">Export to ERDF</i18n:message>';
ORYX.I18N.ERDFSupport.imp = '<i18n:message key="ORYX.I18N.ERDFSupport.imp">Import from ERDF</i18n:message>';
ORYX.I18N.ERDFSupport.impDesc = '<i18n:message key="ORYX.I18N.ERDFSupport.impDesc">Import from ERDF</i18n:message>';
ORYX.I18N.ERDFSupport.impFailed = '<i18n:message key="ORYX.I18N.ERDFSupport.impFailed">"Request for import of ERDF failed.</i18n:message>';
ORYX.I18N.ERDFSupport.impFailed2 = '<i18n:message key="ORYX.I18N.ERDFSupport.impFailed2">An error while importing occurs! <br/>Please check error message: <br/><br/></i18n:message>';
ORYX.I18N.ERDFSupport.error = '<i18n:message key="ORYX.I18N.ERDFSupport.error">Error</i18n:message>';
ORYX.I18N.ERDFSupport.noCanvas = '<i18n:message key="ORYX.I18N.ERDFSupport.noCanvas">The xml document has no Oryx canvas node included!</i18n:message>';
ORYX.I18N.ERDFSupport.noSS = '<i18n:message key="ORYX.I18N.ERDFSupport.noSS">The Oryx canvas node has no stencil set definition included!</i18n:message>';
ORYX.I18N.ERDFSupport.wrongSS = '<i18n:message key="ORYX.I18N.ERDFSupport.wrongSS">The given stencil set does not fit to the current editor!</i18n:message>';
ORYX.I18N.ERDFSupport.selectFile = '<i18n:message key="ORYX.I18N.ERDFSupport.selectFile">Select an ERDF (.xml) file or type in the ERDF to import it!</i18n:message>';
ORYX.I18N.ERDFSupport.file = '<i18n:message key="ORYX.I18N.ERDFSupport.file">File</i18n:message>';
ORYX.I18N.ERDFSupport.impERDF = '<i18n:message key="ORYX.I18N.ERDFSupport.impERDF">Import ERDF</i18n:message>';
ORYX.I18N.ERDFSupport.impBtn = '<i18n:message key="ORYX.I18N.ERDFSupport.impBtn">Import</i18n:message>';
ORYX.I18N.ERDFSupport.impProgress = '<i18n:message key="ORYX.I18N.ERDFSupport.impProgress">Importing...</i18n:message>';
ORYX.I18N.ERDFSupport.close = '<i18n:message key="ORYX.I18N.ERDFSupport.close">Close</i18n:message>';
ORYX.I18N.ERDFSupport.deprTitle = '<i18n:message key="ORYX.I18N.ERDFSupport.deprTitle">Really export to eRDF?</i18n:message>';
ORYX.I18N.ERDFSupport.deprText = '<i18n:message key="ORYX.I18N.ERDFSupport.deprText">Exporting to eRDF is not recommended anymore because the support will be stopped in future versions of the Oryx editor. If possible, export the model to JSON. Do you want to export anyway?</i18n:message>';

if(!ORYX.I18N.jPDLSupport) ORYX.I18N.jPDLSupport = {};

ORYX.I18N.jPDLSupport.group = '<i18n:message key="ORYX.I18N.jPDLSupport.group">Export</i18n:message>';
ORYX.I18N.jPDLSupport.exp = '<i18n:message key="ORYX.I18N.jPDLSupport.exp">Export to jPDL</i18n:message>';
ORYX.I18N.jPDLSupport.expDesc = '<i18n:message key="ORYX.I18N.jPDLSupport.expDesc">Export to jPDL</i18n:message>';
ORYX.I18N.jPDLSupport.imp = '<i18n:message key="ORYX.I18N.jPDLSupport.imp">Import from jPDL</i18n:message>';
ORYX.I18N.jPDLSupport.impDesc = '<i18n:message key="ORYX.I18N.jPDLSupport.impDesc">Migrate a jPDL File to BPMN2</i18n:message>';
ORYX.I18N.jPDLSupport.impFailedReq = '<i18n:message key="ORYX.I18N.jPDLSupport.impFailedReq">Request for migration of jPDL failed.</i18n:message>';
//ORYX.I18N.jPDLSupport.impFailedJson = "Transformation of jPDL failed.";
ORYX.I18N.jPDLSupport.impFailedJsonAbort = '<i18n:message key="ORYX.I18N.jPDLSupport.impFailedJsonAbort">Migration aborted.</i18n:message>';
ORYX.I18N.jPDLSupport.loadSseQuestionTitle = '<i18n:message key="ORYX.I18N.jPDLSupport.loadSseQuestionTitle">jBPM stencil set extension needs to be loaded</i18n:message>';
ORYX.I18N.jPDLSupport.loadSseQuestionBody = '<i18n:message key="ORYX.I18N.jPDLSupport.loadSseQuestionBody">In order to migrate jPDL, the stencil set extension has to be loaded. Do you want to proceed?</i18n:message>';
ORYX.I18N.jPDLSupport.expFailedReq = '<i18n:message key="ORYX.I18N.jPDLSupport.expFailedReq">Request for export of model failed.</i18n:message>';
ORYX.I18N.jPDLSupport.expFailedXml = '<i18n:message key="ORYX.I18N.jPDLSupport.expFailedXml">Export to jPDL failed. Exporter reported: </i18n:message>';
ORYX.I18N.jPDLSupport.error = '<i18n:message key="ORYX.I18N.jPDLSupport.error">Error</i18n:message>';
ORYX.I18N.jPDLSupport.selectFile = '<i18n:message key="ORYX.I18N.jPDLSupport.selectFile">1. Select a jPDL processdefinition.xml file (or type it in)</i18n:message>';
ORYX.I18N.jPDLSupport.selectGpdFile = '<i18n:message key="ORYX.I18N.jPDLSupport.selectGpdFile">2. Select a jPDL gpd.xml file (or type it in)</i18n:message>';
ORYX.I18N.jPDLSupport.file = '<i18n:message key="ORYX.I18N.jPDLSupport.file">Definition file</i18n:message>';
ORYX.I18N.jPDLSupport.gpdfile = '<i18n:message key="ORYX.I18N.jPDLSupport.gpdfile">GPD file</i18n:message>';
ORYX.I18N.jPDLSupport.impJPDL = '<i18n:message key="ORYX.I18N.jPDLSupport.impJPDL">Migrate to BPMN2</i18n:message>';
ORYX.I18N.jPDLSupport.impBtn = '<i18n:message key="ORYX.I18N.jPDLSupport.impBtn">Migrate</i18n:message>';
ORYX.I18N.jPDLSupport.impProgress = '<i18n:message key="ORYX.I18N.jPDLSupport.impProgress">Migrating...</i18n:message>';
ORYX.I18N.jPDLSupport.close = '<i18n:message key="ORYX.I18N.jPDLSupport.close">Close</i18n:message>';

if(!ORYX.I18N.FromBPMN2Support) ORYX.I18N.FromBPMN2Support = {};

ORYX.I18N.FromBPMN2Support.selectFile = '<i18n:message key="ORYX.I18N.FromBPMN2Support.selectFile">Select an BPMN2 file or type in the BPMN2 to import it!</i18n:message>';
ORYX.I18N.FromBPMN2Support.file = '<i18n:message key="ORYX.I18N.FromBPMN2Support.file">File</i18n:message>';
ORYX.I18N.FromBPMN2Support.impBPMN2 = '<i18n:message key="ORYX.I18N.FromBPMN2Support.impBPMN2">Import BPMN2</i18n:message>';
ORYX.I18N.FromBPMN2Support.impBtn = '<i18n:message key="ORYX.I18N.FromBPMN2Support.impBtn">Import</i18n:message>';
ORYX.I18N.FromBPMN2Support.impProgress = '<i18n:message key="ORYX.I18N.FromBPMN2Support.impProgress">Importing...</i18n:message>';
ORYX.I18N.FromBPMN2Support.close = '<i18n:message key="ORYX.I18N.FromBPMN2Support.close">Close</i18n:message>';

if(!ORYX.I18N.FromJSONSupport) ORYX.I18N.FromJSONSupport = {};
ORYX.I18N.FromJSONSupport.selectFile = '<i18n:message key="ORYX.I18N.FromJSONSupport.selectFile">Select an JSON file or type in the JSON to import it!</i18n:message>';
ORYX.I18N.FromJSONSupport.file = '<i18n:message key="ORYX.I18N.FromJSONSupport.file">File</i18n:message>';
ORYX.I18N.FromJSONSupport.impBPMN2 = '<i18n:message key="ORYX.I18N.FromJSONSupport.impBPMN2">Import JSON</i18n:message>';
ORYX.I18N.FromJSONSupport.impBtn = '<i18n:message key="ORYX.I18N.FromJSONSupport.impBtn">Import</i18n:message>';
ORYX.I18N.FromJSONSupport.impProgress = '<i18n:message key="ORYX.I18N.FromJSONSupport.impProgress">Importing...</i18n:message>';
ORYX.I18N.FromJSONSupport.close = '<i18n:message key="ORYX.I18N.FromJSONSupport.close">Close</i18n:message>';

if(!ORYX.I18N.Bpmn2Bpel) ORYX.I18N.Bpmn2Bpel = {};

ORYX.I18N.Bpmn2Bpel.group = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.group">ExecBPMN</i18n:message>';
ORYX.I18N.Bpmn2Bpel.show = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.show">Show transformed BPEL</i18n:message>';
ORYX.I18N.Bpmn2Bpel.download = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.download">Download transformed BPEL</i18n:message>';
ORYX.I18N.Bpmn2Bpel.deploy = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.deploy">Deploy transformed BPEL</i18n:message>';
ORYX.I18N.Bpmn2Bpel.showDesc = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.showDesc">Transforms BPMN to BPEL and shows the result in a new window.</i18n:message>';
ORYX.I18N.Bpmn2Bpel.downloadDesc = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.downloadDesc">Transforms BPMN to BPEL and offers to download the result.</i18n:message>';
ORYX.I18N.Bpmn2Bpel.deployDesc = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.deployDesc">Transforms BPMN to BPEL and deploys the business process on the BPEL-Engine Apache ODE</i18n:message>';
ORYX.I18N.Bpmn2Bpel.transfFailed = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.transfFailed">Request for transformation to BPEL failed.</i18n:message>';
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputTitle = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputTitle">Apache ODE URL</i18n:message>';
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputLabelDeploy = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputLabelDeploy">Deploy Process</i18n:message>';
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputLabelCancel = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputLabelCancel">Cancel</i18n:message>';
ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputPanelText = '<i18n:message key="ORYX.I18N.Bpmn2Bpel.ApacheOdeUrlInputPanelText">Please type-in the URL to the Apache ODE BPEL-Engine. E.g.: http://myserver:8080/ode</i18n:message>';


if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

ORYX.I18N.Save.group = '<i18n:message key="ORYX.I18N.Save.group">File</i18n:message>';
ORYX.I18N.Save.save = '<i18n:message key="ORYX.I18N.Save.save">Save</i18n:message>';
ORYX.I18N.Save.autosave = '<i18n:message key="ORYX.I18N.Save.autosave">Autosave</i18n:message>';
ORYX.I18N.Save.saveDesc = '<i18n:message key="ORYX.I18N.Save.saveDesc">Save</i18n:message>';
ORYX.I18N.Save.autosaveDesc='<i18n:message key="ORYX.I18N.Save.autosaveDesc">Autosave</i18n:message>';
ORYX.I18N.Save.autosaveDesc_on = '<i18n:message key="ORYX.I18N.Save.autosaveDesc_on">Autosave (on)</i18n:message>';
ORYX.I18N.Save.autosaveDesc_off = '<i18n:message key="ORYX.I18N.Save.autosaveDesc_off">Autosave (off)</i18n:message>';
ORYX.I18N.Save.saveAs = '<i18n:message key="ORYX.I18N.Save.saveAs">Save As...</i18n:message>';
ORYX.I18N.Save.saveAsDesc = '<i18n:message key="ORYX.I18N.Save.saveAsDesc">Save As...</i18n:message>';
ORYX.I18N.Save.unsavedData = '<i18n:message key="ORYX.I18N.Save.unsavedData">There are unsaved data, please save before you leave, otherwise your changes get lost!</i18n:message>';
ORYX.I18N.Save.newProcess = '<i18n:message key="ORYX.I18N.Save.newProcess">New Process</i18n:message>';
ORYX.I18N.Save.saveAsTitle = '<i18n:message key="ORYX.I18N.Save.saveAsTitle">Save as...</i18n:message>';
ORYX.I18N.Save.saveBtn = '<i18n:message key="ORYX.I18N.Save.saveBtn">Save</i18n:message>';
ORYX.I18N.Save.close = '<i18n:message key="ORYX.I18N.Save.close">Close</i18n:message>';
ORYX.I18N.Save.savedAs = '<i18n:message key="ORYX.I18N.Save.savedAs">Saved As</i18n:message>';
ORYX.I18N.Save.saved = '<i18n:message key="ORYX.I18N.Save.saved">Saved!</i18n:message>';
ORYX.I18N.Save.failed = '<i18n:message key="ORYX.I18N.Save.failed">Saving failed.</i18n:message>';
ORYX.I18N.Save.noRights = '<i18n:message key="ORYX.I18N.Save.noRights">You have no rights to save changes.</i18n:message>';
ORYX.I18N.Save.saving = '<i18n:message key="ORYX.I18N.Save.saving">Saving</i18n:message>';
ORYX.I18N.Save.saveAsHint = '<i18n:message key="ORYX.I18N.Save.saveAsHint">The process diagram is stored under:</i18n:message>';

if(!ORYX.I18N.File) ORYX.I18N.File = {};

ORYX.I18N.File.group = '<i18n:message key="ORYX.I18N.File.group">File</i18n:message>';
ORYX.I18N.File.print = '<i18n:message key="ORYX.I18N.File.print">Print</i18n:message>';
ORYX.I18N.File.printDesc = '<i18n:message key="ORYX.I18N.File.printDesc">Print current model</i18n:message>';
ORYX.I18N.File.pdf = '<i18n:message key="ORYX.I18N.File.pdf">Export as PDF</i18n:message>';
ORYX.I18N.File.pdfDesc = '<i18n:message key="ORYX.I18N.File.pdfDesc">Export as PDF</i18n:message>';
ORYX.I18N.File.info = '<i18n:message key="ORYX.I18N.File.info">Info</i18n:message>';
ORYX.I18N.File.infoDesc = '<i18n:message key="ORYX.I18N.File.infoDesc">Info</i18n:message>';
ORYX.I18N.File.genPDF = '<i18n:message key="ORYX.I18N.File.genPDF">Generating PDF</i18n:message>';
ORYX.I18N.File.genPDFFailed = '<i18n:message key="ORYX.I18N.File.genPDFFailed">Generating PDF failed.</i18n:message>';
ORYX.I18N.File.printTitle = '<i18n:message key="ORYX.I18N.File.printTitle">Print</i18n:message>';
ORYX.I18N.File.printMsg = '<i18n:message key="ORYX.I18N.File.printMsg">We are currently experiencing problems with the printing function. We recommend using the PDF Export to print the diagram. Do you really want to continue printing?</i18n:message>';

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = '<i18n:message key="ORYX.I18N.Grouping.grouping">Grouping</i18n:message>';
ORYX.I18N.Grouping.group = '<i18n:message key="ORYX.I18N.Grouping.group">Group</i18n:message>';
ORYX.I18N.Grouping.groupDesc = '<i18n:message key="ORYX.I18N.Grouping.groupDesc">Groups all selected shapes</i18n:message>';
ORYX.I18N.Grouping.ungroup = '<i18n:message key="ORYX.I18N.Grouping.ungroup">Ungroup</i18n:message>';
ORYX.I18N.Grouping.ungroupDesc = '<i18n:message key="ORYX.I18N.Grouping.ungroupDesc">Deletes the group of all selected Shapes</i18n:message>';

if(!ORYX.I18N.IBPMN2BPMN) ORYX.I18N.IBPMN2BPMN = {};

ORYX.I18N.IBPMN2BPMN.group = '<i18n:message key="ORYX.I18N.IBPMN2BPMN.group">Export</i18n:message>';
ORYX.I18N.IBPMN2BPMN.name = '<i18n:message key="ORYX.I18N.IBPMN2BPMN.name">IBPMN 2 BPMN Mapping</i18n:message>';
ORYX.I18N.IBPMN2BPMN.desc = '<i18n:message key="ORYX.I18N.IBPMN2BPMN.desc">Convert IBPMN to BPMN</i18n:message>';

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting = '<i18n:message key="ORYX.I18N.Loading.waiting">Please wait...</i18n:message>';

if(!ORYX.I18N.Pnmlexport) ORYX.I18N.Pnmlexport = {};

ORYX.I18N.Pnmlexport.group = '<i18n:message key="ORYX.I18N.Pnmlexport.group">Export</i18n:message>';
ORYX.I18N.Pnmlexport.name = '<i18n:message key="ORYX.I18N.Pnmlexport.name">BPMN to PNML</i18n:message>';
ORYX.I18N.Pnmlexport.desc = '<i18n:message key="ORYX.I18N.Pnmlexport.desc">Export as executable PNML and deploy</i18n:message>';
ORYX.I18N.Pnmlexport.process='<i18n:message key="ORYX.I18N.Pnmlexport.process">Process</i18n:message>';
ORYX.I18N.Pnmlexport.deploySuccess='<i18n:message key="ORYX.I18N.Pnmlexport.deploySuccess">Deployment successful</i18n:message>';


if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = '<i18n:message key="ORYX.I18N.PropertyWindow.name">Name</i18n:message>';
ORYX.I18N.PropertyWindow.value = '<i18n:message key="ORYX.I18N.PropertyWindow.value">Value</i18n:message>';
ORYX.I18N.PropertyWindow.selected = '<i18n:message key="ORYX.I18N.PropertyWindow.selected">selected</i18n:message>';
ORYX.I18N.PropertyWindow.clickIcon = '<i18n:message key="ORYX.I18N.PropertyWindow.clickIcon">Click Icon</i18n:message>';
ORYX.I18N.PropertyWindow.add = '<i18n:message key="ORYX.I18N.PropertyWindow.add">Add</i18n:message>';
ORYX.I18N.PropertyWindow.rem = '<i18n:message key="ORYX.I18N.PropertyWindow.rem">Remove</i18n:message>';
ORYX.I18N.PropertyWindow.complex = '<i18n:message key="ORYX.I18N.PropertyWindow.complex">Editor for a Complex Type</i18n:message>';
ORYX.I18N.PropertyWindow.text = '<i18n:message key="ORYX.I18N.PropertyWindow.text">Editor for a Text Type</i18n:message>';
ORYX.I18N.PropertyWindow.ok = '<i18n:message key="ORYX.I18N.PropertyWindow.ok">Ok</i18n:message>';
ORYX.I18N.PropertyWindow.cancel = '<i18n:message key="ORYX.I18N.PropertyWindow.cancel">Cancel</i18n:message>';
ORYX.I18N.PropertyWindow.dateFormat = '<i18n:message key="ORYX.I18N.PropertyWindow.dateFormat">m/d/y</i18n:message>';
ORYX.I18N.PropertyWindow.desk='<i18n:message key="ORYX.I18N.PropertyWindow.desk">Desk</i18n:message>';
ORYX.I18N.PropertyWindow.noDataAvailableForProp='<i18n:message key="ORYX.I18N.PropertyWindow.noDataAvailableForProp">No data available for property.</i18n:message>';
ORYX.I18N.PropertyWindow.errorDetOutConnections='<i18n:message key="ORYX.I18N.PropertyWindow.errorDetOutConnections">Error determining outgoing connections.</i18n:message>';
ORYX.I18N.PropertyWindow.customEditorFor='<i18n:message key="ORYX.I18N.PropertyWindow.customEditorFor">Custom Editor for</i18n:message>';
ORYX.I18N.PropertyWindow.unableFindCustomEditor='<i18n:message key="ORYX.I18N.PropertyWindow.unableFindCustomEditor">Unable to find custom editor info for</i18n:message>';
ORYX.I18N.PropertyWindow.invalidCustomEditorData='<i18n:message key="ORYX.I18N.PropertyWindow.invalidCustomEditorData">Invalid Custom Editors data.</i18n:message>';
ORYX.I18N.PropertyWindow.errorApplyingCustomEditor='<i18n:message key="ORYX.I18N.PropertyWindow.errorApplyingCustomEditor">Error applying Custom Editor data</i18n:message>';
ORYX.I18N.PropertyWindow.toUsers='<i18n:message key="ORYX.I18N.PropertyWindow.toUsers">To Users</i18n:message>';
ORYX.I18N.PropertyWindow.toGroups='<i18n:message key="ORYX.I18N.PropertyWindow.toGroups">To Groups</i18n:message>';
ORYX.I18N.PropertyWindow.replyTo='<i18n:message key="ORYX.I18N.PropertyWindow.replyTo">Reply To</i18n:message>';
ORYX.I18N.PropertyWindow.subject='<i18n:message key="ORYX.I18N.PropertyWindow.subject">Subject</i18n:message>';
ORYX.I18N.PropertyWindow.body='<i18n:message key="ORYX.I18N.PropertyWindow.body">Body</i18n:message>';
ORYX.I18N.PropertyWindow.addNotification='<i18n:message key="ORYX.I18N.PropertyWindow.addNotification">Add Notification</i18n:message>';
ORYX.I18N.PropertyWindow.addNotificationInstructions='<i18n:message key="ORYX.I18N.PropertyWindow.addNotificationInstructions">Enter Notification body message.</i18n:message>';
ORYX.I18N.PropertyWindow.editorForNotifications='<i18n:message key="ORYX.I18N.PropertyWindow.editorForNotifications">Editor for Notifications</i18n:message>';
ORYX.I18N.PropertyWindow.users='<i18n:message key="ORYX.I18N.PropertyWindow.users">Users</i18n:message>';
ORYX.I18N.PropertyWindow.groups='<i18n:message key="ORYX.I18N.PropertyWindow.groups">Groups</i18n:message>';
ORYX.I18N.PropertyWindow.expiresAt='<i18n:message key="ORYX.I18N.PropertyWindow.expiresAt">Expires At</i18n:message>';
ORYX.I18N.PropertyWindow.from='<i18n:message key="ORYX.I18N.PropertyWindow.from">From</i18n:message>';
ORYX.I18N.PropertyWindow.type='<i18n:message key="ORYX.I18N.PropertyWindow.type">Type</i18n:message>';
ORYX.I18N.PropertyWindow.addReassignment='<i18n:message key="ORYX.I18N.PropertyWindow.addReassignment">Add Reassignment</i18n:message>';
ORYX.I18N.PropertyWindow.editorForReassignment='<i18n:message key="ORYX.I18N.PropertyWindow.editorForReassignment">Editor for Reassignments</i18n:message>';
ORYX.I18N.PropertyWindow.importType='<i18n:message key="ORYX.I18N.PropertyWindow.importType">Import Type</i18n:message>';
ORYX.I18N.PropertyWindow.className='<i18n:message key="ORYX.I18N.PropertyWindow.className">Class Name</i18n:message>';
ORYX.I18N.PropertyWindow.wsdlLocation='<i18n:message key="ORYX.I18N.PropertyWindow.wsdlLocation">WSDL Location</i18n:message>';
ORYX.I18N.PropertyWindow.wsdlNamespace='<i18n:message key="ORYX.I18N.PropertyWindow.wsdlNamespace">WSDL Namespace</i18n:message>';
ORYX.I18N.PropertyWindow.addImport='<i18n:message key="ORYX.I18N.PropertyWindow.addImport">Add Import</i18n:message>';
ORYX.I18N.PropertyWindow.editorForImports='<i18n:message key="ORYX.I18N.PropertyWindow.editorForImports">Editor for Imports</i18n:message>';
ORYX.I18N.PropertyWindow.action='<i18n:message key="ORYX.I18N.PropertyWindow.action">Action</i18n:message>';
ORYX.I18N.PropertyWindow.addAction='<i18n:message key="ORYX.I18N.PropertyWindow.addAction">Add Action</i18n:message>';
ORYX.I18N.PropertyWindow.editorForActions='<i18n:message key="ORYX.I18N.PropertyWindow.editorForActions">Editor for Actions</i18n:message>';
ORYX.I18N.PropertyWindow.dataType='<i18n:message key="ORYX.I18N.PropertyWindow.dataType">Data Type</i18n:message>';
ORYX.I18N.PropertyWindow.fromObject='<i18n:message key="ORYX.I18N.PropertyWindow.fromObject">From Object</i18n:message>';
ORYX.I18N.PropertyWindow.assignmentType='<i18n:message key="ORYX.I18N.PropertyWindow.assignmentType">Assignment Type</i18n:message>';
ORYX.I18N.PropertyWindow.toObject='<i18n:message key="ORYX.I18N.PropertyWindow.toObject">To Object</i18n:message>';
ORYX.I18N.PropertyWindow.toValue='<i18n:message key="ORYX.I18N.PropertyWindow.toValue">To Value</i18n:message>';
ORYX.I18N.PropertyWindow.addAssignment='<i18n:message key="ORYX.I18N.PropertyWindow.addAssignment">Add Assignment</i18n:message>';
ORYX.I18N.PropertyWindow.editorForDataAssignments='<i18n:message key="ORYX.I18N.PropertyWindow.editorForDataAssignments">Editor for Data Assignments</i18n:message>';
ORYX.I18N.PropertyWindow.standardType='<i18n:message key="ORYX.I18N.PropertyWindow.standardType">Standard Type</i18n:message>';
ORYX.I18N.PropertyWindow.customType='<i18n:message key="ORYX.I18N.PropertyWindow.customType">Custom Type</i18n:message>';
ORYX.I18N.PropertyWindow.OnlySingleEntry='<i18n:message key="ORYX.I18N.PropertyWindow.OnlySingleEntry">Only single entry allowed.</i18n:message>';
ORYX.I18N.PropertyWindow.editorForVariableDefinitions='<i18n:message key="ORYX.I18N.PropertyWindow.editorForVariableDefinitions">Editor for Variable Definitions</i18n:message>';
ORYX.I18N.PropertyWindow.addVariable='<i18n:message key="ORYX.I18N.PropertyWindow.addVariable">Add Variable</i18n:message>';
ORYX.I18N.PropertyWindow.editorForDataInput='<i18n:message key="ORYX.I18N.PropertyWindow.editorForDataInput">Editor for Data Input</i18n:message>';
ORYX.I18N.PropertyWindow.addDataInput='<i18n:message key="ORYX.I18N.PropertyWindow.addDataInput">Add Data Input</i18n:message>';
ORYX.I18N.PropertyWindow.editorForDataOutput='<i18n:message key="ORYX.I18N.PropertyWindow.editorForDataOutput">Editor for Data Output</i18n:message>';
ORYX.I18N.PropertyWindow.addDataOutput='<i18n:message key="ORYX.I18N.PropertyWindow.addDataOutput">Add Data Output</i18n:message>';
ORYX.I18N.PropertyWindow.editorForGlobals='<i18n:message key="ORYX.I18N.PropertyWindow.editorForGlobals">Editor for Globals</i18n:message>';
ORYX.I18N.PropertyWindow.addGlobal='<i18n:message key="ORYX.I18N.PropertyWindow.addGlobal">Add Global</i18n:message>';
ORYX.I18N.PropertyWindow.expressionEditor='<i18n:message key="ORYX.I18N.PropertyWindow.expressionEditor">Expression Editor</i18n:message>';
ORYX.I18N.PropertyWindow.loadingProcessInf='<i18n:message key="ORYX.I18N.PropertyWindow.loadingProcessInf">Loading Process Information.</i18n:message>';
ORYX.I18N.PropertyWindow.processId='<i18n:message key="ORYX.I18N.PropertyWindow.processId">Process Id</i18n:message>';
ORYX.I18N.PropertyWindow.packageName='<i18n:message key="ORYX.I18N.PropertyWindow.packageName">Package Name</i18n:message>';
ORYX.I18N.PropertyWindow.selectProcessId='<i18n:message key="ORYX.I18N.PropertyWindow.selectProcessId">Select Process Id and click \"Save\" to select</i18n:message>';
ORYX.I18N.PropertyWindow.editorForCalledEvents='<i18n:message key="ORYX.I18N.PropertyWindow.editorForCalledEvents">Editor for Called Elements</i18n:message>';
ORYX.I18N.PropertyWindow.unableToFindOtherProcess='<i18n:message key="ORYX.I18N.PropertyWindow.unableToFindOtherProcess">Unable to find other processes in package.</i18n:message>';
ORYX.I18N.PropertyWindow.errorResolvingOtherProcessInfo='<i18n:message key="ORYX.I18N.PropertyWindow.errorResolvingOtherProcessInfo">Error resolving other process info</i18n:message>';
ORYX.I18N.PropertyWindow.editorVisualDataAssociations='<i18n:message key="ORYX.I18N.PropertyWindow.editorVisualDataAssociations">Visual data associations Editor</i18n:message>';
ORYX.I18N.PropertyWindow.isMappedTo='<i18n:message key="ORYX.I18N.PropertyWindow.isMappedTo">is mapped to</i18n:message>';
ORYX.I18N.PropertyWindow.isEqualTo='<i18n:message key="ORYX.I18N.PropertyWindow.isEqualTo">is equal to</i18n:message>';


if (!ORYX.I18N.ConditionExpressionEditorField) ORYX.I18N.ConditionExpressionEditorField = {};

ORYX.I18N.ConditionExpressionEditorField.simpleTitle = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.simpleTitle">Expression Editor - Press [Ctrl-Z] to activate auto-completion</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.sequenceFlowTitle = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.sequenceFlowTitle">Sequence Flow Conditions</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.sequenceFlowFullTitle = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.sequenceFlowFullTitle">Sequence Flow Conditions - Press [Ctrl-Z] to activate auto-completion</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.scriptTab = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.scriptTab">Script</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.editorTab = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.editorTab">Editor</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.editorDescription = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.editorDescription">Run sequence flow if the following conditions are met.</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.processVariable = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.processVariable">Process variable:</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.condition = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.condition">Condition:</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.between = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.between">between</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.contains = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.contains">contains</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.endsWith = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.endsWith">ends with</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.equalsTo = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.equalsTo">is equal to</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.greaterThan = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.greaterThan">is greater than</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.greaterThanOrEqual = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.greaterThanOrEqual">is greater than or equal to</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.isEmpty = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.isEmpty">is empty</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.isFalse = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.isFalse">is false</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.isNull = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.isNull">is null</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.isTrue = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.isTrue">is true</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.lessThan = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.lessThan">is less than</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.lessThanOrEqual = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.lessThanOrEqual">is less than or equal to</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.startsWith = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.startsWith">starts with</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.paramsError = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.paramsError">Unable to generate Script expression, please fill correctly the form params.</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.saveError = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.saveError">Unable to save property value, please check the value and try again.</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.scriptParseError = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.scriptParseError">Error found parsing script: <br/>{0}<br/><br/>Press OK to go to the Expression Editor screen and loose the current Script or Cancel to go back to the Script Editor.</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.scriptGenerationError = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.scriptGenerationError">Error found generating script: <br/>{0}<br/><br/>Please check the data entered on the Expression Editor.</i18n:message>';
ORYX.I18N.ConditionExpressionEditorField.nonExistingVariable = '<i18n:message key="ORYX.I18N.ConditionExpressionEditorField.nonExistingVariable">The process does not contain any variable called \"{0}\".</i18n:message>';

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = '<i18n:message key="ORYX.I18N.ShapeMenuPlugin.drag">Drag</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.clickDrag = '<i18n:message key="ORYX.I18N.ShapeMenuPlugin.clickDrag">Click or drag</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.morphMsg = '<i18n:message key="ORYX.I18N.ShapeMenuPlugin.morphMsg">Morph shape</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.addTpProcessDic='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.addTpProcessDic">Add to Process Dictionary</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.viewSourceNode='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.viewSourceNode">View Node Source</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.nameNotSpecified='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.nameNotSpecified">Name not specified.</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.unableToFindNodeSource='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.unableToFindNodeSource">Unable to find node source.</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.userTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.userTask">User Task</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.sendTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.sendTask">Send Task</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.receiveTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.receiveTask">Receive Task</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.manualTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.manualTask">Manual Task</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.serviceTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.serviceTask">Service Task</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.businessRuleTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.businessRuleTask">Business Rule Task</i18n:message>';
ORYX.I18N.ShapeMenuPlugin.scriptTask='<i18n:message key="ORYX.I18N.ShapeMenuPlugin.scriptTask">Script Task</i18n:message>';

if(!ORYX.I18N.SimplePnmlexport) ORYX.I18N.SimplePnmlexport = {};

ORYX.I18N.SimplePnmlexport.group = '<i18n:message key="ORYX.I18N.SimplePnmlexport.group">Export</i18n:message>';
ORYX.I18N.SimplePnmlexport.name = '<i18n:message key="ORYX.I18N.SimplePnmlexport.name">Export to PNML</i18n:message>';
ORYX.I18N.SimplePnmlexport.desc = '<i18n:message key="ORYX.I18N.SimplePnmlexport.desc">Export to PNML</i18n:message>';

if(!ORYX.I18N.StepThroughPlugin) ORYX.I18N.StepThroughPlugin = {};

ORYX.I18N.StepThroughPlugin.group = '<i18n:message key="ORYX.I18N.StepThroughPlugin.group">Step Through</i18n:message>';
ORYX.I18N.StepThroughPlugin.stepThrough = '<i18n:message key="ORYX.I18N.StepThroughPlugin.stepThrough">Step Through</i18n:message>';
ORYX.I18N.StepThroughPlugin.stepThroughDesc = '<i18n:message key="ORYX.I18N.StepThroughPlugin.stepThroughDesc">Step through your model</i18n:message>';
ORYX.I18N.StepThroughPlugin.undo = '<i18n:message key="ORYX.I18N.StepThroughPlugin.undo">Undo</i18n:message>';
ORYX.I18N.StepThroughPlugin.undoDesc = '<i18n:message key="ORYX.I18N.StepThroughPlugin.undoDesc">Undo one Step</i18n:message>';
ORYX.I18N.StepThroughPlugin.error = '<i18n:message key="ORYX.I18N.StepThroughPlugin.error">Can\\\'t step through this diagram.</i18n:message>';
ORYX.I18N.StepThroughPlugin.executing = '<i18n:message key="ORYX.I18N.StepThroughPlugin.executing">Executing</i18n:message>';

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = '<i18n:message key="ORYX.I18N.SyntaxChecker.group">Verification</i18n:message>';
ORYX.I18N.SyntaxChecker.name = '<i18n:message key="ORYX.I18N.SyntaxChecker.name">Validate Process</i18n:message>';
ORYX.I18N.SyntaxChecker.desc = '<i18n:message key="ORYX.I18N.SyntaxChecker.desc">Validate Process</i18n:message>';
ORYX.I18N.SyntaxChecker.noErrors = '<i18n:message key="ORYX.I18N.SyntaxChecker.noErrors">There are no validation errors.</i18n:message>';
ORYX.I18N.SyntaxChecker.hasErrors = '<i18n:message key="ORYX.I18N.SyntaxChecker.hasErrors">Validation error(s) found.</i18n:message>';
ORYX.I18N.SyntaxChecker.invalid = '<i18n:message key="ORYX.I18N.SyntaxChecker.invalid">Invalid answer from server.</i18n:message>';
ORYX.I18N.SyntaxChecker.checkingMessage = '<i18n:message key="ORYX.I18N.SyntaxChecker.checkingMessage">Validating ...</i18n:message>';

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = '<i18n:message key="ORYX.I18N.Undo.group">Undo</i18n:message>';
ORYX.I18N.Undo.undo = '<i18n:message key="ORYX.I18N.Undo.undo">Undo</i18n:message>';
ORYX.I18N.Undo.undoDesc = '<i18n:message key="ORYX.I18N.Undo.undoDesc">Undo the last action</i18n:message>';
ORYX.I18N.Undo.redo = '<i18n:message key="ORYX.I18N.Undo.redo">Redo</i18n:message>';
ORYX.I18N.Undo.redoDesc = '<i18n:message key="ORYX.I18N.Undo.redoDesc">Redo the last undone action</i18n:message>';

if(!ORYX.I18N.Validator) ORYX.I18N.Validator = {};
ORYX.I18N.Validator.checking = '<i18n:message key="ORYX.I18N.Validator.checking">Checking</i18n:message>';

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = '<i18n:message key="ORYX.I18N.View.group">Zoom</i18n:message>';
ORYX.I18N.View.zoomIn = '<i18n:message key="ORYX.I18N.View.zoomIn">Zoom In</i18n:message>';
ORYX.I18N.View.zoomInDesc = '<i18n:message key="ORYX.I18N.View.zoomInDesc">Zoom into the model</i18n:message>';
ORYX.I18N.View.zoomOut = '<i18n:message key="ORYX.I18N.View.zoomOut">Zoom Out</i18n:message>';
ORYX.I18N.View.zoomOutDesc = '<i18n:message key="ORYX.I18N.View.zoomOutDesc">Zoom out of the model</i18n:message>';
ORYX.I18N.View.zoomStandard = '<i18n:message key="ORYX.I18N.View.zoomStandard">Zoom Standard</i18n:message>';
ORYX.I18N.View.zoomStandardDesc = '<i18n:message key="ORYX.I18N.View.zoomStandardDesc">Zoom to the standard level</i18n:message>';
ORYX.I18N.View.zoomFitToModel = '<i18n:message key="ORYX.I18N.View.zoomFitToModel">Zoom fit to model</i18n:message>';
ORYX.I18N.View.zoomFitToModelDesc = '<i18n:message key="ORYX.I18N.View.zoomFitToModelDesc">Zoom to fit the model size</i18n:message>';
ORYX.I18N.View.showInPopout = '<i18n:message key="ORYX.I18N.View.showInPopout">Popout</i18n:message>';
ORYX.I18N.View.showInPopoutDesc = '<i18n:message key="ORYX.I18N.View.showInPopoutDesc">Show in pop out window</i18n:message>';
ORYX.I18N.View.convertToPDF = '<i18n:message key="ORYX.I18N.View.convertToPDF">PDF</i18n:message>';
ORYX.I18N.View.convertToPDFDesc = '<i18n:message key="ORYX.I18N.View.convertToPDFDesc">Convert to PDF</i18n:message>';
ORYX.I18N.View.convertToPNG = '<i18n:message key="ORYX.I18N.View.convertToPNG">PNG</i18n:message>';
ORYX.I18N.View.convertToPNGDesc = '<i18n:message key="ORYX.I18N.View.convertToPNGDesc">Convert to PNG</i18n:message>';
ORYX.I18N.View.generateTaskForms = '<i18n:message key="ORYX.I18N.View.generateTaskForms">Generate Task Form Templates</i18n:message>';
ORYX.I18N.View.editProcessForm = '<i18n:message key="ORYX.I18N.View.editProcessForm">Edit Process Form</i18n:message>';
ORYX.I18N.View.editTaskForm = '<i18n:message key="ORYX.I18N.View.editTaskForm">Edit Task Form</i18n:message>';
ORYX.I18N.View.generateTaskFormsDesc = '<i18n:message key="ORYX.I18N.View.generateTaskFormsDesc">Generate Task Form Templates</i18n:message>';
ORYX.I18N.View.editProcessFormDesc = '<i18n:message key="ORYX.I18N.View.editProcessFormDesc">Edit Process Form</i18n:message>';
ORYX.I18N.View.editTaskFormDesc = '<i18n:message key="ORYX.I18N.View.editTaskFormDesc">Edit Task Form</i18n:message>';
ORYX.I18N.View.showInfo = '<i18n:message key="ORYX.I18N.View.showInfo">Info</i18n:message>';
ORYX.I18N.View.showInfoDesc = '<i18n:message key="ORYX.I18N.View.showInfoDesc">Info</i18n:message>';
ORYX.I18N.View.jbpmgroup = '<i18n:message key="ORYX.I18N.View.jbpmgroup">jBPM</i18n:message>';
ORYX.I18N.View.migratejPDL = '<i18n:message key="ORYX.I18N.View.migratejPDL">Migrate jPDL 3.2 to BPMN2</i18n:message>';
ORYX.I18N.View.migratejPDLDesc = '<i18n:message key="ORYX.I18N.View.migratejPDLDesc">Migrate jPDL 3.2 to BPMN2</i18n:message>';
ORYX.I18N.View.viewDiff = '<i18n:message key="ORYX.I18N.View.viewDiff">View diff</i18n:message>';
ORYX.I18N.View.viewDiffDesc = '<i18n:message key="ORYX.I18N.View.viewDiffDesc">View diff between different versions of the process</i18n:message>';
ORYX.I18N.View.viewDiffLoadingVersions = '<i18n:message key="ORYX.I18N.View.viewDiffLoadingVersions">Loading process versions...</i18n:message>';
ORYX.I18N.View.connectServiceRepo = '<i18n:message key="ORYX.I18N.View.connectServiceRepo">Connect to jBPM service repository</i18n:message>';
ORYX.I18N.View.connectServiceRepoDesc = '<i18n:message key="ORYX.I18N.View.connectServiceRepoDesc">Connect to a Service Repository</i18n:message>';
ORYX.I18N.View.connectServiceRepoDataTitle = '<i18n:message key="ORYX.I18N.View.connectServiceRepoDataTitle">Service Repository Connection</i18n:message>';
ORYX.I18N.View.connectServiceRepoConnecting = '<i18n:message key="ORYX.I18N.View.connectServiceRepoConnecting">Connecting to a Service Repository...</i18n:message>';
ORYX.I18N.View.connect='<i18n:message key="ORYX.I18N.View.connect">Connect</i18n:message>';
ORYX.I18N.View.noServiceSpecified='<i18n:message key="ORYX.I18N.View.noServiceSpecified">No Service Repository specified.</i18n:message>';
ORYX.I18N.View.enterServiceURL='<i18n:message key="ORYX.I18N.View.enterServiceURL">Enter Service Repository URL</i18n:message>';
ORYX.I18N.View.failConnectService='<i18n:message key="ORYX.I18N.View.failConnectService">Failed to connect to the Service Repository</i18n:message>';
ORYX.I18N.View.connectingService='<i18n:message key="ORYX.I18N.View.connectingService">Connecting the the Service Repository failed</i18n:message>';
ORYX.I18N.View.headerIcon='<i18n:message key="ORYX.I18N.View.headerIcon">ICON</i18n:message>';
ORYX.I18N.View.headerName='<i18n:message key="ORYX.I18N.View.headerName">NAME</i18n:message>';
ORYX.I18N.View.headerExplanation='<i18n:message key="ORYX.I18N.View.headerExplanation">EXPLANATION</i18n:message>';
ORYX.I18N.View.headerDocumentation='<i18n:message key="ORYX.I18N.View.headerDocumentation">DOCUMENTATION</i18n:message>';
ORYX.I18N.View.headerInput='<i18n:message key="ORYX.I18N.View.headerInput">INPUT PARAMETERS</i18n:message>';
ORYX.I18N.View.headerResults='<i18n:message key="ORYX.I18N.View.headerResults">RESULTS</i18n:message>';
ORYX.I18N.View.headerCategory='<i18n:message key="ORYX.I18N.View.headerCategory">CATEGORY</i18n:message>';
ORYX.I18N.View.clickOnRowToInstall='<i18n:message key="ORYX.I18N.View.clickOnRowToInstall">Service Nodes. doud-click on a row to install.</i18n:message>';
ORYX.I18N.View.failInstallation='<i18n:message key="ORYX.I18N.View.failInstallation">Failed to install the repository assets.</i18n:message>';
ORYX.I18N.View.successInstall='<i18n:message key="ORYX.I18N.View.successInstall">Assets successfully installed. Save and re-open your process to start using them.</i18n:message>';
ORYX.I18N.View.failAssetsInstallation='<i18n:message key="ORYX.I18N.View.failAssetsInstallation">Installing the repository assets failed</i18n:message>';
ORYX.I18N.View.serviceNodes='<i18n:message key="ORYX.I18N.View.serviceNodes">Service Nodes</i18n:message>';

ORYX.I18N.View.installingRepoItem = '<i18n:message key="ORYX.I18N.View.installingRepoItem">Instaling assets from the Service Repository...</i18n:message>';
ORYX.I18N.View.shareProcess = '<i18n:message key="ORYX.I18N.View.shareProcess">Share your process</i18n:message>';
ORYX.I18N.View.shareProcessDesc = '<i18n:message key="ORYX.I18N.View.shareProcessDesc">Share your process</i18n:message>';
ORYX.I18N.View.infogroup = '<i18n:message key="ORYX.I18N.View.infogroup">info</i18n:message>';

if(!ORYX.I18N.View.tabs) ORYX.I18N.View.tabs = {};
ORYX.I18N.View.tabs.modelling = '<i18n:message key="ORYX.I18N.View.tabs.modelling">Process Modelling</i18n:message>';
ORYX.I18N.View.tabs.simResults = '<i18n:message key="ORYX.I18N.View.tabs.simResults">Simulation Results</i18n:message>';

if(!ORYX.I18N.View.sim) ORYX.I18N.View.sim = {};
ORYX.I18N.View.sim.processPaths = '<i18n:message key="ORYX.I18N.View.sim.processPaths">Display Process Paths</i18n:message>';
ORYX.I18N.View.sim.runSim = '<i18n:message key="ORYX.I18N.View.sim.runSim">Run Process Simulation</i18n:message>';
ORYX.I18N.View.sim.calculatingPaths = '<i18n:message key="ORYX.I18N.View.sim.calculatingPaths">Calculating process paths.</i18n:message>';
ORYX.I18N.View.sim.dispColor = '<i18n:message key="ORYX.I18N.View.sim.dispColor">Display Color</i18n:message>';
ORYX.I18N.View.sim.numElements = '<i18n:message key="ORYX.I18N.View.sim.numElements">Number of Elements</i18n:message>';
ORYX.I18N.View.sim.processPathsTitle = '<i18n:message key="ORYX.I18N.View.sim.processPathsTitle">Process Paths</i18n:message>';
ORYX.I18N.View.sim.subProcessPathsTitle = '<i18n:message key="ORYX.I18N.View.sim.subProcessPathsTitle">Subprocess Paths</i18n:message>';
ORYX.I18N.View.sim.select = '<i18n:message key="ORYX.I18N.View.sim.select">Select </i18n:message>';
ORYX.I18N.View.sim.display = '<i18n:message key="ORYX.I18N.View.sim.display"> and click Show Path to display it.</i18n:message>';
ORYX.I18N.View.sim.showPath = '<i18n:message key="ORYX.I18N.View.sim.showPath">Show Path</i18n:message>';
ORYX.I18N.View.sim.selectPath = '<i18n:message key="ORYX.I18N.View.sim.selectPath">Please select a process path.</i18n:message>';
ORYX.I18N.View.sim.numInstances = '<i18n:message key="ORYX.I18N.View.sim.numInstances">Number of instances</i18n:message>';
ORYX.I18N.View.sim.interval = '<i18n:message key="ORYX.I18N.View.sim.interval">Interval</i18n:message>';
ORYX.I18N.View.sim.intervalUnits = '<i18n:message key="ORYX.I18N.View.sim.intervalUnits">Interval units</i18n:message>';
ORYX.I18N.View.sim.runSim = '<i18n:message key="ORYX.I18N.View.sim.runSim">Run Process Simulation</i18n:message>';
ORYX.I18N.View.sim.runningSim = '<i18n:message key="ORYX.I18N.View.sim.runningSim">Running Process Simulation...</i18n:message>';
ORYX.I18N.View.sim.simNoResults = '<i18n:message key="ORYX.I18N.View.sim.simNoResults">Simulation engine did not return results: </i18n:message>';
ORYX.I18N.View.sim.unableToPerform = '<i18n:message key="ORYX.I18N.View.sim.unableToPerform">Unable to perform simulation:</i18n:message>';
ORYX.I18N.View.sim.resultsInfo = '<i18n:message key="ORYX.I18N.View.sim.resultsInfo">Simulation Info</i18n:message>';
ORYX.I18N.View.sim.resultsGraphs = '<i18n:message key="ORYX.I18N.View.sim.resultsGraphs">Simulation Graphs</i18n:message>';
ORYX.I18N.View.sim.resultsProcessId = '<i18n:message key="ORYX.I18N.View.sim.resultsProcessId">Process id: </i18n:message>';
ORYX.I18N.View.sim.resultsProcessName = '<i18n:message key="ORYX.I18N.View.sim.resultsProcessName">Process name: </i18n:message>';
ORYX.I18N.View.sim.resultsProcessVersion = '<i18n:message key="ORYX.I18N.View.sim.resultsProcessVersion">Process version: </i18n:message>';
ORYX.I18N.View.sim.resultsSimStartTime = '<i18n:message key="ORYX.I18N.View.sim.resultsSimStartTime">Simulation start: </i18n:message>';
ORYX.I18N.View.sim.resultsSimEndTime = '<i18n:message key="ORYX.I18N.View.sim.resultsSimEndTime">Simulation end: </i18n:message>';
ORYX.I18N.View.sim.resultsNumOfExecutions = '<i18n:message key="ORYX.I18N.View.sim.resultsNumOfExecutions">Num. of Executions: </i18n:message>';
ORYX.I18N.View.sim.resultsInterval = '<i18n:message key="ORYX.I18N.View.sim.resultsInterval">Interval </i18n:message>';
ORYX.I18N.View.sim.resultsGroupProcess = '<i18n:message key="ORYX.I18N.View.sim.resultsGroupProcess">Process</i18n:message>';
ORYX.I18N.View.sim.resultsGroupProcessElements = '<i18n:message key="ORYX.I18N.View.sim.resultsGroupProcessElements">Process Elements</i18n:message>';
ORYX.I18N.View.sim.resultsGroupProcessPaths = '<i18n:message key="ORYX.I18N.View.sim.resultsGroupProcessPaths">Paths</i18n:message>';
ORYX.I18N.View.sim.resultsTitlesProcessSimResults = '<i18n:message key="ORYX.I18N.View.sim.resultsTitlesProcessSimResults">Process Simulation Results</i18n:message>';
ORYX.I18N.View.sim.resultsTitlesTaskSimResults = '<i18n:message key="ORYX.I18N.View.sim.resultsTitlesTaskSimResults">Task Simulation Results</i18n:message>';
ORYX.I18N.View.sim.resultsTitlesHumanTaskSimResults = '<i18n:message key="ORYX.I18N.View.sim.resultsTitlesHumanTaskSimResults">Human Task Simulation Results</i18n:message>';
ORYX.I18N.View.sim.resultsTitlesPathExecutionInfo = '<i18n:message key="ORYX.I18N.View.sim.resultsTitlesPathExecutionInfo">Path Execution Info</i18n:message>';
ORYX.I18N.View.sim.chartsExecutionTimes = '<i18n:message key="ORYX.I18N.View.sim.chartsExecutionTimes">Execution Times</i18n:message>';
ORYX.I18N.View.sim.chartsActivityInstances = '<i18n:message key="ORYX.I18N.View.sim.chartsActivityInstances">Activity Instances</i18n:message>';
ORYX.I18N.View.sim.chartsTotalCost = '<i18n:message key="ORYX.I18N.View.sim.chartsTotalCost">Total Cost</i18n:message>';
ORYX.I18N.View.sim.chartsTotalResourceUtilization = '<i18n:message key="ORYX.I18N.View.sim.chartsTotalResourceUtilization">Total Resource Utilization</i18n:message>';
ORYX.I18N.View.sim.chartsResourceUtilization = '<i18n:message key="ORYX.I18N.View.sim.chartsResourceUtilization">Resource Utilization</i18n:message>';
ORYX.I18N.View.sim.chartsResourceCost = '<i18n:message key="ORYX.I18N.View.sim.chartsResourceCost">Resource Cost</i18n:message>';
ORYX.I18N.View.sim.chartsPathImage = '<i18n:message key="ORYX.I18N.View.sim.chartsPathImage">Path Image</i18n:message>';
ORYX.I18N.View.sim.chartsPathInstanceExecution = '<i18n:message key="ORYX.I18N.View.sim.chartsPathInstanceExecution">Path Instance Execution</i18n:message>';


if(!ORYX.I18N.XFormsSerialization) ORYX.I18N.XFormsSerialization = {};

ORYX.I18N.XFormsSerialization.group = '<i18n:message key="ORYX.I18N.XFormsSerialization.group">XForms Serialization</i18n:message>';
ORYX.I18N.XFormsSerialization.exportXForms = '<i18n:message key="ORYX.I18N.XFormsSerialization.exportXForms">XForms Export</i18n:message>';
ORYX.I18N.XFormsSerialization.exportXFormsDesc = '<i18n:message key="ORYX.I18N.XFormsSerialization.exportXFormsDesc">Export XForms+XHTML markup</i18n:message>';
ORYX.I18N.XFormsSerialization.importXForms = '<i18n:message key="ORYX.I18N.XFormsSerialization.importXForms">XForms Import</i18n:message>';
ORYX.I18N.XFormsSerialization.importXFormsDesc = '<i18n:message key="ORYX.I18N.XFormsSerialization.importXFormsDesc">Import XForms+XHTML markup</i18n:message>';
ORYX.I18N.XFormsSerialization.noClientXFormsSupport = '<i18n:message key="ORYX.I18N.XFormsSerialization.noClientXFormsSupport">No XForms support</i18n:message>';
ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc = '<i18n:message key="ORYX.I18N.XFormsSerialization.noClientXFormsSupportDesc"><h2>Your browser does not support XForms. Please install the <a href=\"https://addons.mozilla.org/firefox/addon/824\" target=\"_blank\">Mozilla XForms Add-on</a> for Firefox.</h2></i18n:message>';
ORYX.I18N.XFormsSerialization.ok = '<i18n:message key="ORYX.I18N.XFormsSerialization.ok">Ok</i18n:message>';
ORYX.I18N.XFormsSerialization.selectFile = '<i18n:message key="ORYX.I18N.XFormsSerialization.selectFile">Select a XHTML (.xhtml) file or type in the XForms+XHTML markup to import it!</i18n:message>';
ORYX.I18N.XFormsSerialization.selectCss = '<i18n:message key="ORYX.I18N.XFormsSerialization.selectCss">Please insert url of css file</i18n:message>';
ORYX.I18N.XFormsSerialization.file = '<i18n:message key="ORYX.I18N.XFormsSerialization.file">File</i18n:message>';
ORYX.I18N.XFormsSerialization.impFailed = '<i18n:message key="ORYX.I18N.XFormsSerialization.impFailed">Request for import of document failed.</i18n:message>';
ORYX.I18N.XFormsSerialization.impTitle = '<i18n:message key="ORYX.I18N.XFormsSerialization.impTitle">Import XForms+XHTML document</i18n:message>';
ORYX.I18N.XFormsSerialization.expTitle = '<i18n:message key="ORYX.I18N.XFormsSerialization.expTitle">Export XForms+XHTML document</i18n:message>';
ORYX.I18N.XFormsSerialization.impButton = '<i18n:message key="ORYX.I18N.XFormsSerialization.impButton">Import</i18n:message>';
ORYX.I18N.XFormsSerialization.impProgress = '<i18n:message key="ORYX.I18N.XFormsSerialization.impProgress">Importing...</i18n:message>';
ORYX.I18N.XFormsSerialization.close = '<i18n:message key="ORYX.I18N.XFormsSerialization.close">Close</i18n:message>';


if(!ORYX.I18N.TreeGraphSupport) ORYX.I18N.TreeGraphSupport = {};

ORYX.I18N.TreeGraphSupport.syntaxCheckName = '<i18n:message key="ORYX.I18N.TreeGraphSupport.syntaxCheckName">Syntax Check</i18n:message>';
ORYX.I18N.TreeGraphSupport.group = '<i18n:message key="ORYX.I18N.TreeGraphSupport.group">Tree Graph Support</i18n:message>';
ORYX.I18N.TreeGraphSupport.syntaxCheckDesc = '<i18n:message key="ORYX.I18N.TreeGraphSupport.syntaxCheckDesc">Check the syntax of an tree graph structure</i18n:message>';

if(!ORYX.I18N.QueryEvaluator) ORYX.I18N.QueryEvaluator = {};

ORYX.I18N.QueryEvaluator.name = '<i18n:message key="ORYX.I18N.QueryEvaluator.name">Query Evaluator</i18n:message>';
ORYX.I18N.QueryEvaluator.group = '<i18n:message key="ORYX.I18N.QueryEvaluator.group">Verification</i18n:message>';
ORYX.I18N.QueryEvaluator.desc = '<i18n:message key="ORYX.I18N.QueryEvaluator.desc">Evaluate query</i18n:message>';
ORYX.I18N.QueryEvaluator.noResult = '<i18n:message key="ORYX.I18N.QueryEvaluator.noResult">Query resulted in no match.</i18n:message>';
ORYX.I18N.QueryEvaluator.invalidResponse = '<i18n:message key="ORYX.I18N.QueryEvaluator.invalidResponse">Invalid answer from server.</i18n:message>';
ORYX.I18N.QueryEvaluator.abort='<i18n:message key="ORYX.I18N.QueryEvaluator.abort">Abort</i18n:message>';
ORYX.I18N.QueryEvaluator.modelId='<i18n:message key="ORYX.I18N.QueryEvaluator.modelId">Model ID</i18n:message>';
ORYX.I18N.QueryEvaluator.queryOpts='<i18n:message key="ORYX.I18N.QueryEvaluator.queryOpts">Query options</i18n:message>';
ORYX.I18N.QueryEvaluator.processQuery='<i18n:message key="ORYX.I18N.QueryEvaluator.processQuery">Process query</i18n:message>';
ORYX.I18N.QueryEvaluator.queryType='<i18n:message key="ORYX.I18N.QueryEvaluator.queryType">Query Type</i18n:message>';
ORYX.I18N.QueryEvaluator.processComplianceQuery='<i18n:message key="ORYX.I18N.QueryEvaluator.processComplianceQuery">Process Compliance Query</i18n:message>';
ORYX.I18N.QueryEvaluator.runQueryAgainstModel='<i18n:message key="ORYX.I18N.QueryEvaluator.runQueryAgainstModel">Run query against specific model</i18n:message>';
ORYX.I18N.QueryEvaluator.runComplianceAgainstModel='<i18n:message key="ORYX.I18N.QueryEvaluator.runComplianceAgainstModel">Run compliance query against specific model</i18n:message>';
ORYX.I18N.QueryEvaluator.stop='<i18n:message key="ORYX.I18N.QueryEvaluator.stop">Stop after first match in a model was found</i18n:message>';
ORYX.I18N.QueryEvaluator.processingQuery='<i18n:message key="ORYX.I18N.QueryEvaluator.processingQuery">Processing query</i18n:message>';
ORYX.I18N.QueryEvaluator.serverError='<i18n:message key="ORYX.I18N.QueryEvaluator.serverError">Server encountered an error</i18n:message>';
ORYX.I18N.QueryEvaluator.noMatch='<i18n:message key="ORYX.I18N.QueryEvaluator.noMatch">Found no matching processes!</i18n:message>';
ORYX.I18N.QueryEvaluator.queryResults='<i18n:message key="ORYX.I18N.QueryEvaluator.queryResults">Query Result</i18n:message>';
ORYX.I18N.QueryEvaluator.errorLoading='<i18n:message key="ORYX.I18N.QueryEvaluator.errorLoading">Error loading model meta data.</i18n:message>';

// if(!ORYX.I18N.QueryResultHighlighter) ORYX.I18N.QueryResultHighlighter = {};
// 
// ORYX.I18N.QueryResultHighlighter.name = "Query Result Highlighter";

/** New Language Properties: 08.12.2008 */

ORYX.I18N.PropertyWindow.title = '<i18n:message key="ORYX.I18N.PropertyWindow.title">Properties</i18n:message>';

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = '<i18n:message key="ORYX.I18N.ShapeRepository.title">Object Library</i18n:message>';

ORYX.I18N.Save.dialogDesciption = '<i18n:message key="ORYX.I18N.Save.dialogDesciption">Please enter a name, a description and a comment.</i18n:message>';
ORYX.I18N.Save.dialogLabelTitle = '<i18n:message key="ORYX.I18N.Save.dialogLabelTitle">Title</i18n:message>';
ORYX.I18N.Save.dialogLabelDesc = '<i18n:message key="ORYX.I18N.Save.dialogLabelDesc">Description</i18n:message>';
ORYX.I18N.Save.dialogLabelType = '<i18n:message key="ORYX.I18N.Save.dialogLabelType">Type</i18n:message>';
ORYX.I18N.Save.dialogLabelComment = '<i18n:message key="ORYX.I18N.Save.dialogLabelComment">Revision comment</i18n:message>';

ORYX.I18N.Validator.name = '<i18n:message key="ORYX.I18N.Validator.name">BPMN Validator</i18n:message>';
ORYX.I18N.Validator.description = '<i18n:message key="ORYX.I18N.Validator.description">Validation for BPMN</i18n:message>';

ORYX.I18N.SSExtensionLoader.labelImport = '<i18n:message key="ORYX.I18N.SSExtensionLoader.labelImport">Import</i18n:message>';
ORYX.I18N.SSExtensionLoader.labelCancel = '<i18n:message key="ORYX.I18N.SSExtensionLoader.labelCancel">Cancel</i18n:message>';
ORYX.I18N.SSExtensionLoader.chooseLibrary='<i18n:message key="ORYX.I18N.SSExtensionLoader.chooseLibrary">Choose library set:</i18n:message>';

Ext.MessageBox.buttonText.yes = '<i18n:message key="Ext.MessageBox.buttonText.yes">Yes</i18n:message>';
Ext.MessageBox.buttonText.no = '<i18n:message key="Ext.MessageBox.buttonText.no">No</i18n:message>';
Ext.MessageBox.buttonText.cancel = '<i18n:message key="Ext.MessageBox.buttonText.cancel">Cancel</i18n:message>';
Ext.MessageBox.buttonText.ok = '<i18n:message key="Ext.MessageBox.buttonText.ok">OK</i18n:message>';


/** New Language Properties: 28.01.2009 */
if(!ORYX.I18N.BPMN2XPDL) ORYX.I18N.BPMN2XPDL = {};
ORYX.I18N.BPMN2XPDL.group = '<i18n:message key="ORYX.I18N.BPMN2XPDL.group">Export</i18n:message>';
ORYX.I18N.BPMN2XPDL.xpdlExport = '<i18n:message key="ORYX.I18N.BPMN2XPDL.xpdlExport">Export to XPDL</i18n:message>';
ORYX.I18N.BPMN2XPDL.xpdlImport = '<i18n:message key="ORYX.I18N.BPMN2XPDL.xpdlImport">Import from XPDL</i18n:message>';
ORYX.I18N.BPMN2XPDL.importGroup = '<i18n:message key="ORYX.I18N.BPMN2XPDL.importGroup">Import</i18n:message>';
ORYX.I18N.BPMN2XPDL.selectFile = '<i18n:message key="ORYX.I18N.BPMN2XPDL.selectFile">Select a XPDL (.xml) file or type in the XPDL to import it!</i18n:message>';
ORYX.I18N.BPMN2XPDL.file = '<i18n:message key="ORYX.I18N.BPMN2XPDL.file">File</i18n:message>';
ORYX.I18N.BPMN2XPDL.impXPDL = '<i18n:message key="ORYX.I18N.BPMN2XPDL.impXPDL">Import XPDL</i18n:message>';
ORYX.I18N.BPMN2XPDL.impBtn = '<i18n:message key="ORYX.I18N.BPMN2XPDL.impBtn">Import</i18n:message>';
ORYX.I18N.BPMN2XPDL.impProgress = '<i18n:message key="ORYX.I18N.BPMN2XPDL.impProgress">Importing...</i18n:message>';
ORYX.I18N.BPMN2XPDL.close = '<i18n:message key="ORYX.I18N.BPMN2XPDL.close">Close</i18n:message>';


/** Resource Perspective Additions: 24 March 2009 */
if(!ORYX.I18N.ResourcesSoDAdd) ORYX.I18N.ResourcesSoDAdd = {};

ORYX.I18N.ResourcesSoDAdd.name = '<i18n:message key="ORYX.I18N.ResourcesSoDAdd.name">Define Separation of Duties Contraint</i18n:message>';
ORYX.I18N.ResourcesSoDAdd.group = '<i18n:message key="ORYX.I18N.ResourcesSoDAdd.group">Resource Perspective</i18n:message>';
ORYX.I18N.ResourcesSoDAdd.desc = '<i18n:message key="ORYX.I18N.ResourcesSoDAdd.desc">Define a Separation of Duties constraint for the selected tasks</i18n:message>';

if(!ORYX.I18N.ResourcesSoDShow) ORYX.I18N.ResourcesSoDShow = {};

ORYX.I18N.ResourcesSoDShow.name = '<i18n:message key="ORYX.I18N.ResourcesSoDShow.name">Show Separation of Duties Constraints</i18n:message>';
ORYX.I18N.ResourcesSoDShow.group = '<i18n:message key="ORYX.I18N.ResourcesSoDShow.group">Resource Perspective</i18n:message>';
ORYX.I18N.ResourcesSoDShow.desc = '<i18n:message key="ORYX.I18N.ResourcesSoDShow.desc">Show Separation of Duties constraints of the selected task</i18n:message>';

if(!ORYX.I18N.ResourcesBoDAdd) ORYX.I18N.ResourcesBoDAdd = {};

ORYX.I18N.ResourcesBoDAdd.name = '<i18n:message key="ORYX.I18N.ResourcesBoDAdd.name">Define Binding of Duties Constraint</i18n:message>';
ORYX.I18N.ResourcesBoDAdd.group = '<i18n:message key="ORYX.I18N.ResourcesBoDAdd.group">Resource Perspective</i18n:message>';
ORYX.I18N.ResourcesBoDAdd.desc = '<i18n:message key="ORYX.I18N.ResourcesBoDAdd.desc">Define a Binding of Duties Constraint for the selected tasks</i18n:message>';

if(!ORYX.I18N.ResourcesBoDShow) ORYX.I18N.ResourcesBoDShow = {};

ORYX.I18N.ResourcesBoDShow.name = '<i18n:message key="ORYX.I18N.ResourcesBoDShow.name">Show Binding of Duties Constraints</i18n:message>';
ORYX.I18N.ResourcesBoDShow.group = '<i18n:message key="ORYX.I18N.ResourcesBoDShow.group">Resource Perspective</i18n:message>';
ORYX.I18N.ResourcesBoDShow.desc = '<i18n:message key="ORYX.I18N.ResourcesBoDShow.desc">Show Binding of Duties constraints of the selected task</i18n:message>';

if(!ORYX.I18N.ResourceAssignment) ORYX.I18N.ResourceAssignment = {};

ORYX.I18N.ResourceAssignment.name = '<i18n:message key="ORYX.I18N.ResourceAssignment.name">Resource Assignment</i18n:message>';
ORYX.I18N.ResourceAssignment.group = '<i18n:message key="ORYX.I18N.ResourceAssignment.group">Resource Perspective</i18n:message>';
ORYX.I18N.ResourceAssignment.desc = '<i18n:message key="ORYX.I18N.ResourceAssignment.desc">Assign resources to the selected task(s)</i18n:message>';
ORYX.I18N.ResourceAssignment.chooseResource='<i18n:message key="ORYX.I18N.ResourceAssignment.chooseResource">Please choose resources for assignment</i18n:message>';
ORYX.I18N.ResourceAssignment.wrongEntry='<i18n:message key="ORYX.I18N.ResourceAssignment.wrongEntry">wrong entry, please try again</i18n:message>';



if(!ORYX.I18N.ClearSodBodHighlights) ORYX.I18N.ClearSodBodHighlights = {};

ORYX.I18N.ClearSodBodHighlights.name = '<i18n:message key="ORYX.I18N.ClearSodBodHighlights.name">Clear Highlights and Overlays</i18n:message>';
ORYX.I18N.ClearSodBodHighlights.group = '<i18n:message key="ORYX.I18N.ClearSodBodHighlights.group">Resource Perspective</i18n:message>';
ORYX.I18N.ClearSodBodHighlights.desc = '<i18n:message key="ORYX.I18N.ClearSodBodHighlights.desc">Remove all Separation and Binding of Duties Highlights/ Overlays</i18n:message>';


if(!ORYX.I18N.Perspective) ORYX.I18N.Perspective = {};
ORYX.I18N.Perspective.no = '<i18n:message key="ORYX.I18N.Perspective.no">No Perspective</i18n:message>';
ORYX.I18N.Perspective.noTip = '<i18n:message key="ORYX.I18N.Perspective.noTip">Unload the current perspective</i18n:message>';


/** New Language Properties: 21.04.2009 */
ORYX.I18N.JSONSupport = {
    imp: {
        name: '<i18n:message key="ORYX.I18N.JSONSupport_imp_name">Import from JSON</i18n:message>',
        desc: '<i18n:message key="ORYX.I18N.JSONSupport_imp_desc">Imports a model from JSON</i18n:message>',
        group: '<i18n:message key="ORYX.I18N.JSONSupport_imp_group">Export</i18n:message>',
        selectFile: '<i18n:message key="ORYX.I18N.JSONSupport_imp_selectFile">Select an JSON (.json) file or type in JSON to import it!</i18n:message>',
        file: '<i18n:message key="ORYX.I18N.JSONSupport_imp_file">File</i18n:message>',
        btnImp: '<i18n:message key="ORYX.I18N.JSONSupport_imp_btnImp">Import</i18n:message>',
        btnClose: '<i18n:message key="ORYX.I18N.JSONSupport_imp_btnClose">Close</i18n:message>',
        progress: '<i18n:message key="ORYX.I18N.JSONSupport_imp_progress">Importing ...</i18n:message>',
        syntaxError: '<i18n:message key="ORYX.I18N.JSONSupport_imp_syntaxError">Syntax error</i18n:message>'
    },
    exp: {
        name: '<i18n:message key="ORYX.I18N.JSONSupport_exp_name">Export to JSON</i18n:message>',
        desc: '<i18n:message key="ORYX.I18N.JSONSupport_exp_desc">Exports current model to JSON</i18n:message>',
        group: '<i18n:message key="ORYX.I18N.JSONSupport_exp_group">Export</i18n:message>'
    }
};

ORYX.I18N.TBPMSupport = {
		imp: {
        name: '<i18n:message key="ORYX.I18N.TBPMSupport_name">Import from PNG/JPEG</i18n:message>',
        desc: '<i18n:message key="ORYX.I18N.TBPMSupport_desc">Imports a model from a TPBM photo</i18n:message>',
        group: '<i18n:message key="ORYX.I18N.TBPMSupport_group">Export</i18n:message>',
        selectFile: '<i18n:message key="ORYX.I18N.TBPMSupport_selectFile">Select an image (.png/.jpeg) file!</i18n:message>',
        file: '<i18n:message key="ORYX.I18N.TBPMSupport_file">File</i18n:message>',
        btnImp: '<i18n:message key="ORYX.I18N.TBPMSupport_btnImp">Import</i18n:message>',
        btnClose: '<i18n:message key="ORYX.I18N.TBPMSupport_btnClose">Close</i18n:message>',
        progress: '<i18n:message key="ORYX.I18N.TBPMSupport_progress">Importing ...</i18n:message>',
        syntaxError: '<i18n:message key="ORYX.I18N.TBPMSupport_syntaxError">Syntax error</i18n:message>',
        impFailed: '<i18n:message key="ORYX.I18N.TBPMSupport_impFailed">Request for import of document failed.</i18n:message>',
        confirm: '<i18n:message key="ORYX.I18N.TBPMSupport_confirm">Confirm import of highlighted shapes!</i18n:message>'
    }
};

/** New Language Properties: 08.05.2009 */
if(!ORYX.I18N.BPMN2XHTML) ORYX.I18N.BPMN2XHTML = {};
ORYX.I18N.BPMN2XHTML.group = '<i18n:message key="ORYX.I18N.BPMN2XHTML.group">Export</i18n:message>';
ORYX.I18N.BPMN2XHTML.XHTMLExport = '<i18n:message key="ORYX.I18N.BPMN2XHTML.XHTMLExport">Export XHTML Documentation</i18n:message>';

/** New Language Properties: 09.05.2009 */
if(!ORYX.I18N.JSONImport) ORYX.I18N.JSONImport = {};

ORYX.I18N.JSONImport.title = '<i18n:message key="ORYX.I18N.JSONImport.title">JSON Import</i18n:message>';
ORYX.I18N.JSONImport.wrongSS = '<i18n:message key="ORYX.I18N.JSONImport.wrongSS">The stencil set of the imported file ({0}) does not match to the loaded stencil set ({1}).</i18n:message>';
ORYX.I18N.JSONImport.invalidJSON = '<i18n:message key="ORYX.I18N.JSONImport.invalidJSON">The JSON to import is invalid.</i18n:message>';

if(!ORYX.I18N.Feedback) ORYX.I18N.Feedback = {};

ORYX.I18N.Feedback.name = '<i18n:message key="ORYX.I18N.Feedback.name">Feedback</i18n:message>';
ORYX.I18N.Feedback.desc = '<i18n:message key="ORYX.I18N.Feedback.desc">Contact us for any kind of feedback!</i18n:message>';
ORYX.I18N.Feedback.pTitle = '<i18n:message key="ORYX.I18N.Feedback.pTitle">Contact us for any kind of feedback!</i18n:message>';
ORYX.I18N.Feedback.pName = '<i18n:message key="ORYX.I18N.Feedback.pName">Name</i18n:message>';
ORYX.I18N.Feedback.pEmail = '<i18n:message key="ORYX.I18N.Feedback.pEmail">E-Mail</i18n:message>';
ORYX.I18N.Feedback.pSubject = '<i18n:message key="ORYX.I18N.Feedback.pSubject">Subject</i18n:message>';
ORYX.I18N.Feedback.pMsg = '<i18n:message key="ORYX.I18N.Feedback.pMsg">Description/Message</i18n:message>';
ORYX.I18N.Feedback.pEmpty = '<i18n:message key="ORYX.I18N.Feedback.pEmpty">* Please provide as detailed information as possible so that we can understand your request.\n* For bug reports, please list the steps how to reproduce the problem and describe the output you expected.</i18n:message>';
ORYX.I18N.Feedback.pAttach = '<i18n:message key="ORYX.I18N.Feedback.pAttach">Attach current model</i18n:message>';
ORYX.I18N.Feedback.pAttachDesc = '<i18n:message key="ORYX.I18N.Feedback.pAttachDesc">This information can be helpful for debugging purposes. If your model contains some sensitive data, remove it before or uncheck this behavior.</i18n:message>';
ORYX.I18N.Feedback.pBrowser = '<i18n:message key="ORYX.I18N.Feedback.pBrowser">Information about your browser and environment</i18n:message>';
ORYX.I18N.Feedback.pBrowserDesc = '<i18n:message key="ORYX.I18N.Feedback.pBrowserDesc">This information has been auto-detected from your browser. It can be helpful if you encountered a bug associated with browser-specific behavior.</i18n:message>';
ORYX.I18N.Feedback.submit = '<i18n:message key="ORYX.I18N.Feedback.submit">Send Message</i18n:message>';
ORYX.I18N.Feedback.sending = '<i18n:message key="ORYX.I18N.Feedback.sending">Sending message ...</i18n:message>';
ORYX.I18N.Feedback.success = '<i18n:message key="ORYX.I18N.Feedback.success">Success</i18n:message>';
ORYX.I18N.Feedback.successMsg = '<i18n:message key="ORYX.I18N.Feedback.successMsg">Thank you for your feedback!</i18n:message>';
ORYX.I18N.Feedback.failure = '<i18n:message key="ORYX.I18N.Feedback.failure">Failure</i18n:message>';
ORYX.I18N.Feedback.failureMsg = '<i18n:message key="ORYX.I18N.Feedback.failureMsg">Unfortunately, the message could not be sent. This is our fault! Please try again or contact someone at http://code.google.com/p/oryx-editor/</i18n:message>';


ORYX.I18N.Feedback.name = '<i18n:message key="ORYX.I18N.Feedback.name">Feedback</i18n:message>';
ORYX.I18N.Feedback.failure = '<i18n:message key="ORYX.I18N.Feedback.failure">Failure</i18n:message>';
ORYX.I18N.Feedback.failureMsg = '<i18n:message key="ORYX.I18N.Feedback.failureMsg">Unfortunately, the message could not be sent. This is our fault! Please try again or contact someone at http://code.google.com/p/oryx-editor/</i18n:message>';
ORYX.I18N.Feedback.submit = '<i18n:message key="ORYX.I18N.Feedback.submit">Send Message</i18n:message>';

ORYX.I18N.Feedback.emailDesc = '<i18n:message key="ORYX.I18N.Feedback.emailDesc">Your e-mail address?</i18n:message>';
ORYX.I18N.Feedback.titleDesc = '<i18n:message key="ORYX.I18N.Feedback.titleDesc">Summarize your message with a short title</i18n:message>';
ORYX.I18N.Feedback.descriptionDesc = '<i18n:message key="ORYX.I18N.Feedback.descriptionDesc">Describe your idea, question, or problem.</i18n:message>';
ORYX.I18N.Feedback.info = '<i18n:message key="ORYX.I18N.Feedback.info"><p>Oryx is a research platform intended to support scientists in the field of business process management and beyond with a flexible, extensible tool to validate research theses and conduct experiments.</p><p>We are happy to provide you with the <a href=\"http://bpt.hpi.uni-potsdam.de/Oryx/ReleaseNotes\" target=\"_blank\"> latest technology and advancements</a> of our platform. <a href=\"http://bpt.hpi.uni-potsdam.de/Oryx/DeveloperNetwork\" target=\"_blank\">We</a> work hard to provide you with a reliable system, even though you may experience small hiccups from time to time.</p><p>If you have ideas how to improve Oryx, have a question related to the platform, or want to report a problem: <strong>Please, let us know. Here.</strong></p></i18n:message>'; // general info will be shown, if no subject specific info is given
ORYX.I18N.Feedback.typeFeedback='<i18n:message key="ORYX.I18N.Feedback.typeFeedback">Feedback</i18n:message>';
ORYX.I18N.Feedback.typeBug='<i18n:message key="ORYX.I18N.Feedback.typeBug">Bug Report</i18n:message>';
ORYX.I18N.Feedback.typeFeatureReq='<i18n:message key="ORYX.I18N.Feedback.typeFeatureReq">Feature Request</i18n:message>';

// list subjects in reverse order of appearance!
ORYX.I18N.Feedback.subjects = [
    {
    	id: '<i18n:message key="ORYX.I18N.Feedback.subjects_first_id">question</i18n:message>',   // ansi-compatible name
    	name: '<i18n:message key="ORYX.I18N.Feedback.subjects_first_name">Question</i18n:message>', // natural name
    	description: '<i18n:message key="ORYX.I18N.Feedback.subjects_first_description">Ask your question here! \\nPlease give us as much information as possible, so we don\\\'t have to bother you with more questions, before we can give an answer.</i18n:message>', // default text for the description text input field
    	info: '<i18n:message key="ORYX.I18N.Feedback.subjects_first_info"/>' // optional field to give more info
    },
    {
    	id: '<i18n:message key="ORYX.I18N.Feedback.subjects_second_id">problem</i18n:message>',   // ansi-compatible name
    	name: '<i18n:message key="ORYX.I18N.Feedback.subjects_second_name">Problem</i18n:message>', // natural name
    	description: '<i18n:message key="ORYX.I18N.Feedback.subjects_second_description">We\\\'re sorry for the inconvenience. Give us feedback on the problem, and we\\\'ll try to solve it for you. Describe it as detailed as possible, please.</i18n:message>', // default text for the description text input field
    	info: '<i18n:message key="ORYX.I18N.Feedback.subjects_second_info"/>' // optional field to give more info
    },
    {
    	id: '<i18n:message key="ORYX.I18N.Feedback.subjects_third_id">idea</i18n:message>',   // ansi-compatible name
    	name: '<i18n:message key="ORYX.I18N.Feedback.subjects_third_name">Idea</i18n:message>', // natural name
    	description: '<i18n:message key="ORYX.I18N.Feedback.subjects_third_description">Share your ideas and thoughts here!</i18n:message>', // default text for the description text input field
    	info: '<i18n:message key="ORYX.I18N.Feedback.subjects_third_info"/>' // optional field to give more info
    }
];

/** New Language Properties: 11.05.2009 */
if(!ORYX.I18N.BPMN2DTRPXMI) ORYX.I18N.BPMN2DTRPXMI = {};
ORYX.I18N.BPMN2DTRPXMI.group = '<i18n:message key="ORYX.I18N.BPMN2DTRPXMI.group">Export</i18n:message>';
ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExport = '<i18n:message key="ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExport">Export to XMI (Design Thinking)</i18n:message>';
ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExportDescription = '<i18n:message key="ORYX.I18N.BPMN2DTRPXMI.DTRPXMIExportDescription">Exports current model to XMI (requires stencil set extension \\\'BPMN Subset for Design Thinking\\\')</i18n:message>';

/** New Language Properties: 14.05.2009 */
if(!ORYX.I18N.RDFExport) ORYX.I18N.RDFExport = {};
ORYX.I18N.RDFExport.group = '<i18n:message key="ORYX.I18N.RDFExport.group">Export</i18n:message>';
ORYX.I18N.RDFExport.rdfExport = '<i18n:message key="ORYX.I18N.RDFExport.rdfExport">Export to RDF</i18n:message>';
ORYX.I18N.RDFExport.rdfExportDescription = '<i18n:message key="ORYX.I18N.RDFExport.rdfExportDescription">Exports current model to the XML serialization defined for the Resource Description Framework (RDF)</i18n:message>';

/** New Language Properties: 15.05.2009*/
if(!ORYX.I18N.SyntaxChecker.BPMN) ORYX.I18N.SyntaxChecker.BPMN={};
ORYX.I18N.SyntaxChecker.BPMN_NO_SOURCE = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_NO_SOURCE">An edge must have a source.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_NO_TARGET = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_NO_TARGET">An edge must have a target.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS">Source and target node must be contained in the same process.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_SAME_PROCESS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_SAME_PROCESS">Source and target node must be contained in different pools.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS">A flow object must be contained in a process.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW">An end event must have an incoming sequence flow.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW">A start event must have an outgoing sequence flow.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW">Start events must not have incoming sequence flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW">Attached intermediate events must not have incoming sequence flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW">Attached intermediate events must have exactly one outgoing sequence flow.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW">End events must not have outgoing sequence flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION">Event-based gateways must not be followed by gateways or subprocesses.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED">Node type is not allowed.</i18n:message>';

if(!ORYX.I18N.SyntaxChecker.IBPMN) ORYX.I18N.SyntaxChecker.IBPMN={};
ORYX.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = '<i18n:message key="ORYX.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET">Interactions must have a sender and a receiver role set</i18n:message>';
ORYX.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW">This node must have incoming sequence flow.</i18n:message>';
ORYX.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW">This node must have outgoing sequence flow.</i18n:message>';

if(!ORYX.I18N.SyntaxChecker.InteractionNet) ORYX.I18N.SyntaxChecker.InteractionNet={};
ORYX.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = '<i18n:message key="ORYX.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET">Sender not set</i18n:message>';
ORYX.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = '<i18n:message key="ORYX.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET">Receiver not set</i18n:message>';
ORYX.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = '<i18n:message key="ORYX.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET">Message type not set</i18n:message>';
ORYX.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = '<i18n:message key="ORYX.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET">Role not set</i18n:message>';

if(!ORYX.I18N.SyntaxChecker.EPC) ORYX.I18N.SyntaxChecker.EPC={};
ORYX.I18N.SyntaxChecker.EPC_NO_SOURCE = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_NO_SOURCE">Each edge must have a source.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_NO_TARGET = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_NO_TARGET">Each edge must have a target.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED">Node must be connected with edges.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2">Node must be connected with more edges.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES">Node has too many connected edges.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR">Node is no correct connector.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_MANY_STARTS = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_MANY_STARTS">There must be only one start event.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR">There must be no functions after a splitting OR/XOR.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_OR = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_OR">There must be no process interface after a splitting OR/XOR.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION">There must be no function after a function.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT">There must be no event after an event.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION">There must be no process interface after a function.</i18n:message>';
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI = '<i18n:message key="ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI">There must be no function after a process interface.</i18n:message>';

if(!ORYX.I18N.SyntaxChecker.PetriNet) ORYX.I18N.SyntaxChecker.PetriNet={};
ORYX.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = '<i18n:message key="ORYX.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE">The graph is not bipartite</i18n:message>';
ORYX.I18N.SyntaxChecker.PetriNet_NO_LABEL = '<i18n:message key="ORYX.I18N.SyntaxChecker.PetriNet_NO_LABEL">Label not set for a labeled transition</i18n:message>';
ORYX.I18N.SyntaxChecker.PetriNet_NO_ID = '<i18n:message key="ORYX.I18N.SyntaxChecker.PetriNet_NO_ID">There is a node without id</i18n:message>';
ORYX.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = '<i18n:message key="ORYX.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET">Two flow relationships have the same source and target</i18n:message>';
ORYX.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = '<i18n:message key="ORYX.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET">A node is not set for a flowrelationship</i18n:message>';

/** New Language Properties: 02.06.2009*/
ORYX.I18N.Edge = '<i18n:message key="ORYX.I18N.Edge">Edge</i18n:message>';
ORYX.I18N.Node = '<i18n:message key="ORYX.I18N.Node">Node</i18n:message>';

/** New Language Properties: 03.06.2009*/
ORYX.I18N.SyntaxChecker.notice = '<i18n:message key="ORYX.I18N.SyntaxChecker.notice">Move the mouse over a red cross icon to see the error message.</i18n:message>';

ORYX.I18N.Validator.result = '<i18n:message key="ORYX.I18N.Validator.result">Validation Result</i18n:message>';
ORYX.I18N.Validator.noErrors = '<i18n:message key="ORYX.I18N.Validator.noErrors">No validation errors found.</i18n:message>';
ORYX.I18N.Validator.bpmnDeadlockTitle = '<i18n:message key="ORYX.I18N.Validator.bpmnDeadlockTitle">Deadlock</i18n:message>';
ORYX.I18N.Validator.bpmnDeadlock = '<i18n:message key="ORYX.I18N.Validator.bpmnDeadlock">This node results in a deadlock. There are situations where not all incoming branches are activated.</i18n:message>';
ORYX.I18N.Validator.bpmnUnsafeTitle = '<i18n:message key="ORYX.I18N.Validator.bpmnUnsafeTitle">Lack of synchronization</i18n:message>';
ORYX.I18N.Validator.bpmnUnsafe = '<i18n:message key="ORYX.I18N.Validator.bpmnUnsafe">This model suffers from lack of synchronization. The marked element is activated from multiple incoming branches.</i18n:message>';
ORYX.I18N.Validator.bpmnLeadsToNoEndTitle = '<i18n:message key="ORYX.I18N.Validator.bpmnLeadsToNoEndTitle">Validation Result</i18n:message>';
ORYX.I18N.Validator.bpmnLeadsToNoEnd = '<i18n:message key="ORYX.I18N.Validator.bpmnLeadsToNoEnd">The process will never reach a final state.</i18n:message>';

ORYX.I18N.Validator.syntaxErrorsTitle = '<i18n:message key="ORYX.I18N.Validator.syntaxErrorsTitle">Syntax Error</i18n:message>';
ORYX.I18N.Validator.syntaxErrorsMsg = '<i18n:message key="ORYX.I18N.Validator.syntaxErrorsMsg">The process cannot be validated because it contains syntax errors.</i18n:message>';

ORYX.I18N.Validator.error = '<i18n:message key="ORYX.I18N.Validator.error">Validation failed</i18n:message>';
ORYX.I18N.Validator.errorDesc = '<i18n:message key="ORYX.I18N.Validator.errorDesc">We are sorry, but the validation of your process failed. It would help us identifying the problem, if you sent us your process model via the \"Send Feedback\" function.</i18n:message>';

ORYX.I18N.Validator.epcIsSound = '<i18n:message key="ORYX.I18N.Validator.epcIsSound"><p><b>The EPC is sound, no problems found!</b></p></i18n:message>';
ORYX.I18N.Validator.epcNotSound = '<i18n:message key="ORYX.I18N.Validator.epcNotSound"><p><b>The EPC is <i>NOT</i> sound!</b></p></i18n:message>';

/** New Language Properties: 05.06.2009*/
if(!ORYX.I18N.RESIZE) ORYX.I18N.RESIZE = {};
ORYX.I18N.RESIZE.tipGrow = '<i18n:message key="ORYX.I18N.RESIZE.tipGrow">Increase canvas size:</i18n:message>';
ORYX.I18N.RESIZE.tipShrink = '<i18n:message key="ORYX.I18N.RESIZE.tipShrink">Decrease canvas size:</i18n:message>';
ORYX.I18N.RESIZE.N = '<i18n:message key="ORYX.I18N.RESIZE.N">Top</i18n:message>';
ORYX.I18N.RESIZE.W = '<i18n:message key="ORYX.I18N.RESIZE.W">Left</i18n:message>';
ORYX.I18N.RESIZE.S = '<i18n:message key="ORYX.I18N.RESIZE.S">Down</i18n:message>';
ORYX.I18N.RESIZE.E = '<i18n:message key="ORYX.I18N.RESIZE.E">Right</i18n:message>';
/** New Language Properties: 14.08.2009*/
if(!ORYX.I18N.PluginLoad) ORYX.I18N.PluginLoad = {};
ORYX.I18N.PluginLoad.AddPluginButtonName = '<i18n:message key="ORYX.I18N.PluginLoad.AddPluginButtonName">Add Plugins</i18n:message>';
ORYX.I18N.PluginLoad.AddPluginButtonDesc = '<i18n:message key="ORYX.I18N.PluginLoad.AddPluginButtonDesc">Add additional Plugins dynamically</i18n:message>';
ORYX.I18N.PluginLoad.loadErrorTitle = '<i18n:message key="ORYX.I18N.PluginLoad.loadErrorTitle">Loading Error</i18n:message>';
ORYX.I18N.PluginLoad.loadErrorDesc = '<i18n:message key="ORYX.I18N.PluginLoad.loadErrorDesc">Unable to load Plugin. \n Error:\n</i18n:message>';
ORYX.I18N.PluginLoad.WindowTitle = '<i18n:message key="ORYX.I18N.PluginLoad.WindowTitle">Add additional Plugins</i18n:message>';

ORYX.I18N.PluginLoad.NOTUSEINSTENCILSET = '<i18n:message key="ORYX.I18N.PluginLoad.NOTUSEINSTENCILSET">Not allowed in this Stencilset!</i18n:message>';
ORYX.I18N.PluginLoad.REQUIRESTENCILSET = '<i18n:message key="ORYX.I18N.PluginLoad.REQUIRESTENCILSET">Require another Stencilset!</i18n:message>';
ORYX.I18N.PluginLoad.NOTFOUND = '<i18n:message key="ORYX.I18N.PluginLoad.NOTFOUND">Pluginname not found!</i18n:message>';
ORYX.I18N.PluginLoad.YETACTIVATED = '<i18n:message key="ORYX.I18N.PluginLoad.YETACTIVATED">Plugin is yet activated!</i18n:message>';

/** New Language Properties: 15.07.2009*/
if(!ORYX.I18N.Layouting) ORYX.I18N.Layouting ={};
ORYX.I18N.Layouting.doing = '<i18n:message key="ORYX.I18N.Layouting.doing">Layouting...</i18n:message>';

/** New Language Properties: 18.08.2009*/
ORYX.I18N.SyntaxChecker.MULT_ERRORS = '<i18n:message key="ORYX.I18N.SyntaxChecker.MULT_ERRORS">Multiple Errors</i18n:message>';

/** New Language Properties: 08.09.2009*/
if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = '<i18n:message key="ORYX.I18N.PropertyWindow.oftenUsed">Core Properties</i18n:message>';
ORYX.I18N.PropertyWindow.moreProps = '<i18n:message key="ORYX.I18N.PropertyWindow.moreProps">Extra Properties</i18n:message>';
ORYX.I18N.PropertyWindow.simulationProps = '<i18n:message key="ORYX.I18N.PropertyWindow.simulationProps">Simulation Properties</i18n:message>';
ORYX.I18N.PropertyWindow.displayProps = '<i18n:message key="ORYX.I18N.PropertyWindow.displayProps">Graphical Settings</i18n:message>';

/** New Language Properties: 17.09.2009*/
if(!ORYX.I18N.Bpmn2_0Serialization) ORYX.I18N.Bpmn2_0Serialization = {};
ORYX.I18N.Bpmn2_0Serialization.show = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.show">Show BPMN 2.0 DI XML</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.showDesc = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.showDesc">Show BPMN 2.0 DI XML of the current BPMN 2.0 model</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.download = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.download">Download BPMN 2.0 DI XML</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.downloadDesc = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.downloadDesc">Download BPMN 2.0 DI XML of the current BPMN 2.0 model</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.serialFailed = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.serialFailed">An error occurred while generating the BPMN 2.0 DI XML Serialization.</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.group = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.group">BPMN 2.0</i18n:message>';

/** New Language Properties 01.10.2009 */
if(!ORYX.I18N.SyntaxChecker.BPMN2) ORYX.I18N.SyntaxChecker.BPMN2 = {};

ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION">A Data Input must not have any incoming Data Associations.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION">A Data Output must not have any outgoing Data Associations.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS">Targets of Event-based Gateways may only have one incoming Sequence Flow.</i18n:message>';


/** New Language Properties 02.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS">An Event-based Gateway must have two or more outgoing Sequence Flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION">If Message Intermediate Events are used in the configuration, then Receive Tasks must not be used and vice versa.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER">Only the following Intermediate Event triggers are valid: Message, Signal, Timer, Conditional and Multiple.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION">The outgoing Sequence Flows of the Event Gateway must not have a condition expression.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING">The Gateway does not meet the conditions to instantiate the process. Please use a start event or an instantiating attribute for the gateway.</i18n:message>';

/** New Language Properties 05.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE">The Gateway must have both multiple incoming and outgoing Sequence Flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE">The Gateway must have multiple incoming but most NOT have multiple outgoing Sequence Flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE">The Gateway must NOT have multiple incoming but must have multiple outgoing Sequence Flows.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW">A Gateway must have a minimum of one outgoing Sequence Flow.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT">Receive Tasks used in Event Gateway configurations must not have any attached Intermediate Events.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION">An Event Subprocess must not have any incoming or outgoing Sequence Flow.</i18n:message>';

/** New Language Properties 13.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED">At least one side of the Message Flow has to be connected.</i18n:message>';

/** New Language Properties 19.10.2009 */
ORYX.I18N.Bpmn2_0Serialization['import'] = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization__import__">Import from BPMN 2.0 DI XML</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.importDesc = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.importDesc">Import a BPMN 2.0 model from a file or XML String</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.selectFile = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.selectFile">Select a (*.bpmn) file or type in BPMN 2.0 DI XML to import it!</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.file = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.file">File:</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.name = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.name">Import from BPMN 2.0 DI XML</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.btnImp = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.btnImp">Import</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.progress = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.progress">Importing BPMN 2.0 DI XML ...</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.btnClose = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.btnClose">Close</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.error = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.error">An error occurred while importing BPMN 2.0 DI XML</i18n:message>';

/** New Language Properties 24.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES">A Choreography Activity may only have one initiating message.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED">A Message Flow is not allowed here.</i18n:message>';

/** New Language Properties 27.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS">An Event-based Gateway that is not instantiating must have a minimum of one incoming Sequence Flow.</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS">A Choreography Activity must have one initiating Participant (white).</i18n:message>';
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = '<i18n:message key="ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS">A Choreography Activity must not have more than one initiating Participant (white).</i18n:message>';

ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = '<i18n:message key="ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS">The communication must be connected to at least two participants.</i18n:message>';
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = '<i18n:message key="ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT">The message flow\\\'s source must be a participant.</i18n:message>';
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = '<i18n:message key="ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT">The message flow\\\'s target must be a participant.</i18n:message>';
ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = '<i18n:message key="ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES">The conversation link must connect a communication or sub conversation node with a participant.</i18n:message>';

/** New Language Properties 30.12.2009 */
ORYX.I18N.Bpmn2_0Serialization.xpdlShow = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.xpdlShow">Show XPDL 2.2</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.xpdlShowDesc = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.xpdlShowDesc">Shows the XPDL 2.2 based on BPMN 2.0 XML (by XSLT)</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.xpdlDownload = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.xpdlDownload">Download as XPDL 2.2</i18n:message>';
ORYX.I18N.Bpmn2_0Serialization.xpdlDownloadDesc = '<i18n:message key="ORYX.I18N.Bpmn2_0Serialization.xpdlDownloadDesc">Download the XPDL 2.2 based on BPMN 2.0 XML (by XSLT)</i18n:message>';


if(!ORYX.I18N.cpntoolsSupport) ORYX.I18N.cpntoolsSupport = {};

ORYX.I18N.cpntoolsSupport.serverConnectionFailed = '<i18n:message key="ORYX.I18N.cpntoolsSupport.serverConnectionFailed">Connection to server failed.</i18n:message>';
ORYX.I18N.cpntoolsSupport.importTask = '<i18n:message key="ORYX.I18N.cpntoolsSupport.importTask">Select an CPN file (.cpn) or type in the CPN XML structure in order to import it!</i18n:message>';
ORYX.I18N.cpntoolsSupport.File = '<i18n:message key="ORYX.I18N.cpntoolsSupport.File">File:</i18n:message>';
ORYX.I18N.cpntoolsSupport.cpn = '<i18n:message key="ORYX.I18N.cpntoolsSupport.cpn">CPN</i18n:message>';
ORYX.I18N.cpntoolsSupport.title = '<i18n:message key="ORYX.I18N.cpntoolsSupport.title">CPN Oryx</i18n:message>';
ORYX.I18N.cpntoolsSupport.importLable = '<i18n:message key="ORYX.I18N.cpntoolsSupport.importLable">Import</i18n:message>';
ORYX.I18N.cpntoolsSupport.close = '<i18n:message key="ORYX.I18N.cpntoolsSupport.close">Close</i18n:message>';
ORYX.I18N.cpntoolsSupport.wrongCPNFile = '<i18n:message key="ORYX.I18N.cpntoolsSupport.wrongCPNFile">Not chosen correct CPN - File.</i18n:message>';
ORYX.I18N.cpntoolsSupport.noPageSelection = '<i18n:message key="ORYX.I18N.cpntoolsSupport.noPageSelection">No page has been selected.</i18n:message>';
ORYX.I18N.cpntoolsSupport.group = '<i18n:message key="ORYX.I18N.cpntoolsSupport.group">Export</i18n:message>';
ORYX.I18N.cpntoolsSupport.importProgress = '<i18n:message key="ORYX.I18N.cpntoolsSupport.importProgress">Importing ...</i18n:message>';
ORYX.I18N.cpntoolsSupport.exportProgress = '<i18n:message key="ORYX.I18N.cpntoolsSupport.exportProgress">Exporting ...</i18n:message>';
ORYX.I18N.cpntoolsSupport.exportDescription = '<i18n:message key="ORYX.I18N.cpntoolsSupport.exportDescription">Export to CPN Tools</i18n:message>';
ORYX.I18N.cpntoolsSupport.importDescription = '<i18n:message key="ORYX.I18N.cpntoolsSupport.importDescription">Import from CPN Tools</i18n:message>';
ORYX.I18N.cpntoolsSupport.cpnToolsPage='<i18n:message key="ORYX.I18N.cpntoolsSupport.cpnToolsPage">CPNTools Page</i18n:message>';

if(!ORYX.I18N.BPMN2YAWLMapper) ORYX.I18N.BPMN2YAWLMapper = {};

ORYX.I18N.BPMN2YAWLMapper.group = '<i18n:message key="ORYX.I18N.BPMN2YAWLMapper.group">Export</i18n:message>';
ORYX.I18N.BPMN2YAWLMapper.name = '<i18n:message key="ORYX.I18N.BPMN2YAWLMapper.name">YAWL Export</i18n:message>';
ORYX.I18N.BPMN2YAWLMapper.desc = '<i18n:message key="ORYX.I18N.BPMN2YAWLMapper.desc">Map this diagram to YAWL and export it, please ensure \"BPMN Subset for mapping to YAWL\" is loaded</i18n:message>';

if(!ORYX.I18N.LocalHistory) ORYX.I18N.LocalHistory = {};

ORYX.I18N.LocalHistory.display='<i18n:message key="ORYX.I18N.LocalHistory.display">Display Local History</i18n:message>';
ORYX.I18N.LocalHistory.display_desc='<i18n:message key="ORYX.I18N.LocalHistory.display_desc">Display Local History</i18n:message>';
ORYX.I18N.LocalHistory.clear='<i18n:message key="ORYX.I18N.LocalHistory.clear">Clear Local History</i18n:message>';
ORYX.I18N.LocalHistory.clear_desc='<i18n:message key="ORYX.I18N.LocalHistory.clear_desc">Clear Local History</i18n:message>';
ORYX.I18N.LocalHistory.config='<i18n:message key="ORYX.I18N.LocalHistory.config">Configure Snaphot Interval</i18n:message>';
ORYX.I18N.LocalHistory.config_desc='<i18n:message key="ORYX.I18N.LocalHistory.config_desc">Configure Snaphot Interval</i18n:message>';
ORYX.I18N.LocalHistory.enable='<i18n:message key="ORYX.I18N.LocalHistory.enable">Enable Local History</i18n:message>';
ORYX.I18N.LocalHistory.enable_desc='<i18n:message key="ORYX.I18N.LocalHistory.enable_desc">Enable Local History</i18n:message>';
ORYX.I18N.LocalHistory.disable='<i18n:message key="ORYX.I18N.LocalHistory.disable">Disable Local History</i18n:message>';
ORYX.I18N.LocalHistory.disable_desc='<i18n:message key="ORYX.I18N.LocalHistory.disable_desc">Disable Local History</i18n:message>';

if(!ORYX.I18N.LocalHistory.headertxt) ORYX.I18N.LocalHistory.headertxt = {};

ORYX.I18N.LocalHistory.headertxt.id='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.id">Id</i18n:message>';
ORYX.I18N.LocalHistory.headertxt.name='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.name">Name</i18n:message>';
ORYX.I18N.LocalHistory.headertxt.Package='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.Package">Package</i18n:message>';
ORYX.I18N.LocalHistory.headertxt.Version='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.Version">Version</i18n:message>';
ORYX.I18N.LocalHistory.headertxt.TimeStamp='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.TimeStamp">Time Stamp</i18n:message>';
ORYX.I18N.LocalHistory.headertxt.ProcessImage='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.ProcessImage">Process Image</i18n:message>';
ORYX.I18N.LocalHistory.headertxt.ProcessImage_NoAvailable='<i18n:message key="ORYX.I18N.LocalHistory.headertxt.ProcessImage.NoAvailable"><center>Process image not available.</center></i18n:message>';

if(!ORYX.I18N.LocalHistory.localHistoryPanel) ORYX.I18N.LocalHistory.localHistoryPanel = {};
ORYX.I18N.LocalHistory.localHistoryPanel.title='<i18n:message key="ORYX.I18N.LocalHistory.localHistoryPanel.title"><center>Select Process Id and click \"Restore\" to restore.</center></i18n:message>';


if(!ORYX.I18N.LocalHistory.LocalHistoryView) ORYX.I18N.LocalHistory.LocalHistoryView = {};
ORYX.I18N.LocalHistory.LocalHistoryView.title='<i18n:message key="ORYX.I18N.LocalHistory.LocalHistoryView.title">Local History View</i18n:message>';
ORYX.I18N.LocalHistory.LocalHistoryView.restore='<i18n:message key="ORYX.I18N.LocalHistory.LocalHistoryView.restore">Restore</i18n:message>';
ORYX.I18N.LocalHistory.LocalHistoryView.invalidProcessInfo='<i18n:message key="ORYX.I18N.LocalHistory.LocalHistoryView.invalidProcessInfo">Invalid Process info. Unable to restore.</i18n:message>';
ORYX.I18N.LocalHistory.LocalHistoryView.msg='<i18n:message key="ORYX.I18N.LocalHistory.LocalHistoryView.msg">Please select a process id.</i18n:message>';

ORYX.I18N.LocalHistory.clearLocalHistoryMsg='<i18n:message key="ORYX.I18N.LocalHistory.clearLocalHistory.msg">Local History has been cleared.</i18n:message>';
ORYX.I18N.LocalHistory.addQuotaexceed='<i18n:message key="ORYX.I18N.LocalHistory.addQuotaexceed">Local History quota exceeded. Clearing local history.</i18n:message>';
ORYX.I18N.LocalHistory.historyDisabled='<i18n:message key="ORYX.I18N.LocalHistory.historyDisabled">Local History has been disabled.</i18n:message>';
ORYX.I18N.LocalHistory.historyEnabled='<i18n:message key="ORYX.I18N.LocalHistory.historyEnabled">Local History has been enabled.</i18n:message>';
ORYX.I18N.LocalHistory.intervalUnits='<i18n:message key="ORYX.I18N.LocalHistory.intervalUnits">Interval units</i18n:message>';
ORYX.I18N.LocalHistory.ConfigureSnapshotInterval='<i18n:message key="ORYX.I18N.LocalHistory.ConfigureSnapshotInterval">Configure Snapshot Interval</i18n:message>';
ORYX.I18N.LocalHistory.UpdatedSnapshotInterval='<i18n:message key="ORYX.I18N.LocalHistory.UpdatedSnapshotInterval">Updated Snapshot Interval</i18n:message>';
ORYX.I18N.LocalHistory.InvalidInput='<i18n:message key="ORYX.I18N.LocalHistory.InvalidInput">Invalid input specified</i18n:message>';
ORYX.I18N.LocalHistory.set='<i18n:message key="ORYX.I18N.LocalHistory.set">Set</i18n:message>';
ORYX.I18N.LocalHistory.unitsMillisecond='<i18n:message key="ORYX.I18N.LocalHistory.unitsMillisecond">millisecond</i18n:message>';
ORYX.I18N.LocalHistory.unitsSeconds='<i18n:message key="ORYX.I18N.LocalHistory.unitsSeconds">seconds</i18n:message>';
ORYX.I18N.LocalHistory.unitsMinutes='<i18n:message key="ORYX.I18N.LocalHistory.unitsMinutes">minutes</i18n:message>';
ORYX.I18N.LocalHistory.unitsHours='<i18n:message key="ORYX.I18N.LocalHistory.unitsHours">hours</i18n:message>';
ORYX.I18N.LocalHistory.unitsDays='<i18n:message key="ORYX.I18N.LocalHistory.unitsDays">days</i18n:message>';


if(!ORYX.I18N.theme) ORYX.I18N.theme = {};
ORYX.I18N.theme.Apply='<i18n:message key="ORYX.I18N.theme.Apply">Apply</i18n:message>';
ORYX.I18N.theme.ColorTheme='<i18n:message key="ORYX.I18N.theme.ColorTheme">Color Theme</i18n:message>';
ORYX.I18N.theme.invalidColorTheme='<i18n:message key="ORYX.I18N.theme.invalidColorTheme">Invalid Color Theme data.</i18n:message>';
ORYX.I18N.theme.errorApplying='<i18n:message key="ORYX.I18N.theme.errorApplying">Error applying Color Theme</i18n:message>';

if(!ORYX.I18N.forms) ORYX.I18N.forms = {};
ORYX.I18N.forms.generateTaskForm='<i18n:message key="ORYX.I18N.forms.generateTaskForm">Generate Task Form</i18n:message>';
ORYX.I18N.forms.generateTaskForm_desc='<i18n:message key="ORYX.I18N.forms.generateTaskForm_desc">Generate Task Form</i18n:message>';
ORYX.I18N.forms.generateAllForms='<i18n:message key="ORYX.I18N.forms.generateAllForms">Generate all Forms</i18n:message>';
ORYX.I18N.forms.generateAllForms_desc='<i18n:message key="ORYX.I18N.forms.generateAllForms_desc">Generate all Forms</i18n:message>';
ORYX.I18N.forms.invalidNumberNodes='<i18n:message key="ORYX.I18N.forms.invalidNumberNodes">Invalid number of nodes selected..</i18n:message>';
ORYX.I18N.forms.successGenTask='<i18n:message key="ORYX.I18N.forms.successGenTask">Successfully generated task form template.</i18n:message>';
ORYX.I18N.forms.failGenTask='<i18n:message key="ORYX.I18N.forms.failGenTask"><p>Failed to generate task form template.</p></i18n:message>';
ORYX.I18N.forms.failNoTaskName='<i18n:message key="ORYX.I18N.forms.failNoTaskName">Task Name not specified.</i18n:message>';
ORYX.I18N.forms.failNoUserTask='<i18n:message key="ORYX.I18N.forms.failNoUserTask">Selected node is not User Task.</i18n:message>';
ORYX.I18N.forms.failNoTaskSelected='<i18n:message key="ORYX.I18N.forms.failNoTaskSelected">No task selected.</i18n:message>';
ORYX.I18N.forms.failProcIdUndef='<i18n:message key="ORYX.I18N.forms.failProcIdUndef">Process Id not specified.</i18n:message>';
ORYX.I18N.forms.successGenProcAndTask='<i18n:message key="ORYX.I18N.forms.successGenProcAndTask">Successfully generated process and task form templates</i18n:message>';
ORYX.I18N.forms.failGenProcAndTask='<i18n:message key="ORYX.I18N.forms.failGenProcAndTask"><p>Failed to generate process and task form templates.</p></i18n:message>';


if(!ORYX.I18N.view) ORYX.I18N.view = {};
ORYX.I18N.view.showFullScreen='<i18n:message key="ORYX.I18N.view.showFullScreen">Show in full screen</i18n:message>';
ORYX.I18N.view.showFullScreen_desc='<i18n:message key="ORYX.I18N.view.showFullScreen_desc">Show in full screen mode</i18n:message>';
ORYX.I18N.view.failShowFullScreen='<i18n:message key="ORYX.I18N.view.failShowFullScreen">Browser does not support full screen mode.</i18n:message>';

ORYX.I18N.view.shareProcessImg='<i18n:message key="ORYX.I18N.view.shareProcessImg">Share Process Image</i18n:message>';
ORYX.I18N.view.shareProcessImg_desc='<i18n:message key="ORYX.I18N.view.shareProcessImg_desc">Share Process Image</i18n:message>';
ORYX.I18N.view.shareProcessPDF='<i18n:message key="ORYX.I18N.view.shareProcessPDF">Share Process PDF</i18n:message>';
ORYX.I18N.view.shareProcessPDF_desc='<i18n:message key="ORYX.I18N.view.shareProcessPDF_desc">Share Process PDF</i18n:message>';
ORYX.I18N.view.importFromBPMN2='<i18n:message key="ORYX.I18N.view.importFromBPMN2">Import from BPMN2</i18n:message>';
ORYX.I18N.view.importFromBPMN2_desc='<i18n:message key="ORYX.I18N.view.importFromBPMN2_desc">Import from BPMN2</i18n:message>';
ORYX.I18N.view.importFromJSON='<i18n:message key="ORYX.I18N.view.importFromJSON">Import from JSON</i18n:message>';
ORYX.I18N.view.importFromJSON_desc='<i18n:message key="ORYX.I18N.view.importFromJSON_desc">Import from existing JSON</i18n:message>';
ORYX.I18N.view.downloadProcPDF='<i18n:message key="ORYX.I18N.view.downloadProcPDF">Download Process PDF</i18n:message>';
ORYX.I18N.view.downloadProcPDF_desc='<i18n:message key="ORYX.I18N.view.downloadProcPDF_desc">Download Process PDF</i18n:message>';
ORYX.I18N.view.downloadProcPNG='<i18n:message key="ORYX.I18N.view.downloadProcPNG">Download Process PNG</i18n:message>';
ORYX.I18N.view.downloadProcPNG_desc='<i18n:message key="ORYX.I18N.view.downloadProcPNG_desc">Download Process PNG</i18n:message>';
ORYX.I18N.view.viewProcSources='<i18n:message key="ORYX.I18N.view.viewProcSources">View Process Sources</i18n:message>';
ORYX.I18N.view.viewProcSources_desc='<i18n:message key="ORYX.I18N.view.viewProcSources_desc">View Process Sources</i18n:message>';
ORYX.I18N.view.importFromBPMN2ErrorCheckLogs='<i18n:message key="ORYX.I18N.view.importFromBPMN2ErrorCheckLogs"><p>Check server logs for more details.</p></i18n:message>';
ORYX.I18N.view.importFromBPMN2Error='<i18n:message key="ORYX.I18N.view.importFromBPMN2Error"><p>Failed to import BPMN2</p></i18n:message>';
ORYX.I18N.view.creatingEmbeddableProc='<i18n:message key="ORYX.I18N.view.creatingEmbeddableProc">Creating Embeddable Process...</i18n:message>';
ORYX.I18N.view.enbedableProc='<i18n:message key="ORYX.I18N.view.enbedableProc">Embeddable Process</i18n:message>';
ORYX.I18N.view.enbedableProcFailCreate='<i18n:message key="ORYX.I18N.view.enbedableProcFailCreate">Failed to create embeddable process code</i18n:message>';
ORYX.I18N.view.importFromJSONError='<i18n:message key="ORYX.I18N.view.importFromJSONError">Failed to import JSON</i18n:message>';
ORYX.I18N.view.creatingProcPDF='<i18n:message key="ORYX.I18N.view.creatingProcPDF">Creating the process PDF</i18n:message>';
ORYX.I18N.view.processImgPDF='<i18n:message key="ORYX.I18N.view.processImgPDF">Process Image PDF</i18n:message>';
ORYX.I18N.view.processPDFurl='<i18n:message key="ORYX.I18N.view.processPDFurl">Process PDF URL</i18n:message>';
ORYX.I18N.view.processPDFFail='<i18n:message key="ORYX.I18N.view.processPDFFail">Failed to create the process PDF</i18n:message>';
ORYX.I18N.view.processCreatingImg='<i18n:message key="ORYX.I18N.view.processCreatingImg">Creating the process image...</i18n:message>';
ORYX.I18N.view.processImgUrl='<i18n:message key="ORYX.I18N.view.processImgUrl">Process Image URL</i18n:message>';
ORYX.I18N.view.processImgFail='<i18n:message key="ORYX.I18N.view.processImgFail">Failed to create the process image</i18n:message>';
ORYX.I18N.view.versionsFail='<i18n:message key="ORYX.I18N.view.versionsFail">Failed to retrieve process version information</i18n:message>';
ORYX.I18N.view.versionsNotfound='<i18n:message key="ORYX.I18N.view.versionsNotfound">Unable to find process versions</i18n:message>';
ORYX.I18N.view.versionsSelect='<i18n:message key="ORYX.I18N.view.versionsSelect">Select process version</i18n:message>';
ORYX.I18N.view.creatingDiff='<i18n:message key="ORYX.I18N.view.creatingDiff">Creating diff</i18n:message>';
ORYX.I18N.view.failRetrieveVersionsSource='<i18n:message key="ORYX.I18N.view.failRetrieveVersionsSource">Failed to retrieve process version source</i18n:message>';
ORYX.I18N.view.convertingToBPMN2Fail='<i18n:message key="ORYX.I18N.view.convertingToBPMN2Fail">Converting to BPMN2 failed</i18n:message>';
ORYX.I18N.view.compareBPMN2PReviousVersions='<i18n:message key="ORYX.I18N.view.compareBPMN2PReviousVersions">Compare process BPMN2 with previous versions</i18n:message>';
ORYX.I18N.view.replaceExistingModel='<i18n:message key="ORYX.I18N.view.replaceExistingModel">Replace existing model?</i18n:message>';
ORYX.I18N.view.importSuccess='<i18n:message key="ORYX.I18N.view.importSuccess">Successfully imported</i18n:message>';
ORYX.I18N.view.unableImportProvided='<i18n:message key="ORYX.I18N.view.unableImportProvided">Unable to import provided</i18n:message>';
ORYX.I18N.view.processSVGSource='<i18n:message key="ORYX.I18N.view.processSVGSource">Process SVG Source</i18n:message>';
ORYX.I18N.view.erdfSource='<i18n:message key="ORYX.I18N.view.erdfSource">ERDF Source</i18n:message>';
ORYX.I18N.view.jsonSource='<i18n:message key="ORYX.I18N.view.jsonSource">JSON Source</i18n:message>';
ORYX.I18N.view.bpmn2Source='<i18n:message key="ORYX.I18N.view.bpmn2Source">BPMN2 Source</i18n:message>';
ORYX.I18N.view.saveToFile='<i18n:message key="ORYX.I18N.view.saveToFile">Save to file</i18n:message>';
ORYX.I18N.view.downloadBPMN2='<i18n:message key="ORYX.I18N.view.downloadBPMN2">Download BPMN2</i18n:message>';
ORYX.I18N.view.processSources='<i18n:message key="ORYX.I18N.view.processSources">Process Sources</i18n:message>';

ORYX.I18N.Save.enableAutosave='<i18n:message key="ORYX.I18N.Save.enableAutosave">Enable autosave</i18n:message>';
ORYX.I18N.Save.enableAutosave_desc='<i18n:message key="ORYX.I18N.Save.enableAutosave_desc">Enable autosave</i18n:message>';
ORYX.I18N.Save.disableAutosave='<i18n:message key="ORYX.I18N.Save.disableAutosave">Disable autosave</i18n:message>';
ORYX.I18N.Save.disableAutosave_desc='<i18n:message key="ORYX.I18N.Save.disableAutosave_desc">Disable autosave</i18n:message>';
ORYX.I18N.Save.copy='<i18n:message key="ORYX.I18N.Save.copy">Copy</i18n:message>';
ORYX.I18N.Save.copy_desc='<i18n:message key="ORYX.I18N.Save.copy_desc">Copy asset</i18n:message>';
ORYX.I18N.Save.rename='<i18n:message key="ORYX.I18N.Save.rename">Rename</i18n:message>';
ORYX.I18N.Save.rename_desc='<i18n:message key="ORYX.I18N.Save.rename_desc">Rename asset</i18n:message>';
ORYX.I18N.Save.delete_name='<i18n:message key="ORYX.I18N.Save.delete_name">Delete</i18n:message>';
ORYX.I18N.Save.delete_desc='<i18n:message key="ORYX.I18N.Save.delete_desc">Delete asset</i18n:message>';
ORYX.I18N.Save.saveCancelled='<i18n:message key="ORYX.I18N.Save.saveCancelled">Save operation has been cancelled.</i18n:message>';
ORYX.I18N.Save.processReloading='<i18n:message key="ORYX.I18N.Save.processReloading">Reloading process content.</i18n:message>';
ORYX.I18N.Save.unableReloadContent='<i18n:message key="ORYX.I18N.Save.unableReloadContent">Unable to reload process content.</i18n:message>';
ORYX.I18N.Save.invalidContent='<i18n:message key="ORYX.I18N.Save.invalidContent">Invalid content.</i18n:message>';
ORYX.I18N.Save.couldNotReload='<i18n:message key="ORYX.I18N.Save.couldNotReload">Could not reload process content.</i18n:message>';
ORYX.I18N.Save.saveSuccess='<i18n:message key="ORYX.I18N.Save.saveSuccess">Successfully saved business process</i18n:message>';
ORYX.I18N.Save.saveImageSuccess='<i18n:message key="ORYX.I18N.Save.saveImageSuccess">Successfully saved business process image</i18n:message>';
ORYX.I18N.Save.saveImageFailed='<i18n:message key="ORYX.I18N.Save.saveImageFailed">Unable to save business process image.</i18n:message>';
ORYX.I18N.Save.unableToSave='<i18n:message key="ORYX.I18N.Save.unableToSave">Unable to save</i18n:message>';
ORYX.I18N.Save.noChanges='<i18n:message key="ORYX.I18N.Save.noChanges">Process contains no changes since last save.</i18n:message>';

ORYX.I18N.Save.autosaveEnabled='<i18n:message key="ORYX.I18N.Save.autosaveEnabled">Autosave has been enabled.</i18n:message>';
ORYX.I18N.Save.autosaveDisabled='<i18n:message key="ORYX.I18N.Save.autosaveDisabled">Autosave has been disabled.</i18n:message>';
ORYX.I18N.Save.deleteConfirm_title='<i18n:message key="ORYX.I18N.Save.deleteConfirm_title">Delete process confirmation</i18n:message>';
ORYX.I18N.Save.deleteConfirm_msg='<i18n:message key="ORYX.I18N.Save.deleteConfirm_msg">Are you sure you want to delete this process?</i18n:message>';
ORYX.I18N.Save.copyConfirm_title='<i18n:message key="ORYX.I18N.Save.copyConfirm_title">Copy process confirmation</i18n:message>';
ORYX.I18N.Save.copyConfirm_msg='<i18n:message key="ORYX.I18N.Save.copyConfirm_msg">Would you like to save your changes before copying?</i18n:message>';
ORYX.I18N.Save.renameConfirm_title='<i18n:message key="ORYX.I18N.Save.renameConfirm_title">Rename process confirmation</i18n:message>';
ORYX.I18N.Save.renameConfirm_msg='<i18n:message key="ORYX.I18N.Save.renameConfirm_msg">Would you like to save your changes before renaming?</i18n:message>';
ORYX.I18N.Save.reloadSuccess='<i18n:message key="ORYX.I18N.Save.reloadSuccess">Successfully reloaded process.</i18n:message>';
ORYX.I18N.Save.reloadFail='<i18n:message key="ORYX.I18N.Save.reloadFail">Unable to reload process.</i18n:message>';
ORYX.I18N.Save.processReloadedInvalid='<i18n:message key="ORYX.I18N.Save.processReloadedInvalid">Process content to be reloaded is invalid.</i18n:message>';

ORYX.I18N.SyntaxChecker.startValidating='<i18n:message key="ORYX.I18N.SyntaxChecker.startValidating">Start validating</i18n:message>';
ORYX.I18N.SyntaxChecker.startValidating_desc='<i18n:message key="ORYX.I18N.SyntaxChecker.startValidating_desc">Start validating</i18n:message>';
ORYX.I18N.SyntaxChecker.stopValidating='<i18n:message key="ORYX.I18N.SyntaxChecker.stopValidating">Stop validating</i18n:message>';
ORYX.I18N.SyntaxChecker.stopValidating_desc='<i18n:message key="ORYX.I18N.SyntaxChecker.stopValidating_desc">Stop validating</i18n:message>';
ORYX.I18N.SyntaxChecker.viewAllIssues='<i18n:message key="ORYX.I18N.SyntaxChecker.viewAllIssues">View all issues</i18n:message>';
ORYX.I18N.SyntaxChecker.viewAllIssues_desc='<i18n:message key="ORYX.I18N.SyntaxChecker.viewAllIssues_desc">View all issues</i18n:message>';
ORYX.I18N.SyntaxChecker.startingContinousVal='<i18n:message key="ORYX.I18N.SyntaxChecker.startingContinousVal">Starting continuous visual validation.</i18n:message>';
ORYX.I18N.SyntaxChecker.stoppingContinousVal='<i18n:message key="ORYX.I18N.SyntaxChecker.stoppingContinousVal">Stopping continuous visual validation.</i18n:message>';
ORYX.I18N.SyntaxChecker.header_IssueType='<i18n:message key="ORYX.I18N.SyntaxChecker.header_IssueType">Issue Type</i18n:message>';
ORYX.I18N.SyntaxChecker.header_Description='<i18n:message key="ORYX.I18N.SyntaxChecker.header_Description">Description</i18n:message>';
ORYX.I18N.SyntaxChecker.header_ShapeId='<i18n:message key="ORYX.I18N.SyntaxChecker.header_ShapeId">Shape ID</i18n:message>';
ORYX.I18N.SyntaxChecker.suggestions='<i18n:message key="ORYX.I18N.SyntaxChecker.suggestions">Validation Suggestions</i18n:message>';

ORYX.I18N.View.sim.creatingPathImage='<i18n:message key="ORYX.I18N.View.sim.creatingPathImage">Creating path image.</i18n:message>';
ORYX.I18N.View.sim.errorInvalidData='<i18n:message key="ORYX.I18N.View.sim.errorInvalidData">Invalid Path data.</i18n:message>';
ORYX.I18N.View.sim.errorFindingPath='<i18n:message key="ORYX.I18N.View.sim.errorFindingPath">Error finding Paths</i18n:message>';
ORYX.I18N.View.sim.errorUnknownPathId='<i18n:message key="ORYX.I18N.View.sim.errorUnknownPathId">Unknown path id.</i18n:message>';

if(!ORYX.I18N.Dictionary) ORYX.I18N.Dictionary = {}

ORYX.I18N.Dictionary.dictionary='<i18n:message key="ORYX.I18N.Dictionary.dictionary">Dictionary</i18n:message>';
ORYX.I18N.Dictionary.processDictionary='<i18n:message key="ORYX.I18N.Dictionary.processDictionary">Process dictionary</i18n:message>';
ORYX.I18N.Dictionary.errorReadingProcDic='<i18n:message key="ORYX.I18N.Dictionary.errorReadingProcDic">Error reading Process Dictionary. Invalid item</i18n:message>';
ORYX.I18N.Dictionary.errorLoadingProcDic='<i18n:message key="ORYX.I18N.Dictionary.errorLoadingProcDic">Error loading Process Dictionary</i18n:message>';
ORYX.I18N.Dictionary.header_name='<i18n:message key="ORYX.I18N.Dictionary.header_name">Name</i18n:message>';
ORYX.I18N.Dictionary.headerAliases='<i18n:message key="ORYX.I18N.Dictionary.headerAliases">Aliases</i18n:message>';
ORYX.I18N.Dictionary.headerDesc='<i18n:message key="ORYX.I18N.Dictionary.headerDesc">Description</i18n:message>';
ORYX.I18N.Dictionary.addNewEntry='<i18n:message key="ORYX.I18N.Dictionary.addNewEntry">Add New Entry</i18n:message>';
ORYX.I18N.Dictionary.noProcDoc='<i18n:message key="ORYX.I18N.Dictionary.noProcDoc">No process documentation specified.</i18n:message>';
ORYX.I18N.Dictionary.procDoc='<i18n:message key="ORYX.I18N.Dictionary.procDoc">Process Documentation</i18n:message>';
ORYX.I18N.Dictionary.fromDoc='<i18n:message key="ORYX.I18N.Dictionary.fromDoc"><center>From Documentation</center></i18n:message>';
ORYX.I18N.Dictionary.fromFile='<i18n:message key="ORYX.I18N.Dictionary.fromFile"><center>From File</center></i18n:message>';
ORYX.I18N.Dictionary.select='<i18n:message key="ORYX.I18N.Dictionary.select">Select</i18n:message>';
ORYX.I18N.Dictionary.highlightText='<i18n:message key="ORYX.I18N.Dictionary.highlightText"><center>Highlight text and click on \\\"Add\\\"</center></i18n:message>';
ORYX.I18N.Dictionary.add='<i18n:message key="ORYX.I18N.Dictionary.add">Add</i18n:message>';
ORYX.I18N.Dictionary.extractDicEntries='<i18n:message key="ORYX.I18N.Dictionary.extractDicEntries"><center>Extract Dictionary entries</center></i18n:message>';
ORYX.I18N.Dictionary.procDicEditor='<i18n:message key="ORYX.I18N.Dictionary.procDicEditor">Process Dictionary Editor</i18n:message>';
ORYX.I18N.Dictionary.Save='<i18n:message key="ORYX.I18N.Dictionary.Save">Save</i18n:message>';
ORYX.I18N.Dictionary.storingDic='<i18n:message key="ORYX.I18N.Dictionary.storingDic">Storing Process Dictionary.</i18n:message>';
ORYX.I18N.Dictionary.errorSavingDic='<i18n:message key="ORYX.I18N.Dictionary.errorSavingDic">Error saving Process Dictionary</i18n:message>';
ORYX.I18N.Dictionary.cancel='<i18n:message key="ORYX.I18N.Dictionary.cancel">Cancel</i18n:message>';

if(!ORYX.I18N.inlineTaskFormEditor) ORYX.I18N.inlineTaskFormEditor = {}

ORYX.I18N.inlineTaskFormEditor.formEditor='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.formEditor">Form Editor</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.selectForm='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.selectForm">Select which Form Editor to use:</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.graphicalModeler='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.graphicalModeler">Graphical Modeler</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.markupEditor='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.markupEditor">Markup Editor</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.errorInitiatingEditor='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.errorInitiatingEditor">Error initiating Form Editor</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.errorInitiatingWidgets='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.errorInitiatingWidgets">Error initiating Form Widgets</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.taskNameNotSpecified='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.taskNameNotSpecified">Task Name not specified.</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.insertFormWidget='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.insertFormWidget">Insert form widget</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.errorInsertingFormWidget='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.errorInsertingFormWidget">Error inserting Form Widget</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.widgetInsertionSourceMode='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.widgetInsertionSourceMode">Widget insertion is only possible in Source Mode</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.editingForm='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.editingForm">Editing Form:</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.completionInst='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.completionInst">Press [Ctrl-Z] to activate auto-completion</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.storingForm='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.storingForm">Storing Task Form</i18n:message>';
ORYX.I18N.inlineTaskFormEditor.errorSavingForm='<i18n:message key="ORYX.I18N.inlineTaskFormEditor.errorSavingForm">Error saving Task Form</i18n:message>';

if(!ORYX.I18N.PetriNetSoundness) ORYX.I18N.PetriNetSoundness = {}

ORYX.I18N.PetriNetSoundness.checkSoundness='<i18n:message key="ORYX.I18N.PetriNetSoundness.checkSoundness">Check soundness</i18n:message>';
ORYX.I18N.PetriNetSoundness.checkSoundness_desc='<i18n:message key="ORYX.I18N.PetriNetSoundness.checkSoundness_desc">Checks current Petri net for different soundness criteria.</i18n:message>';
ORYX.I18N.PetriNetSoundness.tipTerminationCriteria='<i18n:message key="ORYX.I18N.PetriNetSoundness.tipTerminationCriteria"><b>Termination Criteria</b>: Makes sure that any process instance that starts in the initial state will eventually reach the final state. If any dead locks are detected, click to show one counter example.</i18n:message>';
ORYX.I18N.PetriNetSoundness.thereIs='<i18n:message key="ORYX.I18N.PetriNetSoundness.thereIs">There is</i18n:message>';
ORYX.I18N.PetriNetSoundness.no='<i18n:message key="ORYX.I18N.PetriNetSoundness.no">no</i18n:message>';
ORYX.I18N.PetriNetSoundness.a='<i18n:message key="ORYX.I18N.PetriNetSoundness.a">a</i18n:message>';
ORYX.I18N.PetriNetSoundness.pathsDeadLock='<i18n:message key="ORYX.I18N.PetriNetSoundness.pathsDeadLock">path that leads to a deadlock.</i18n:message>';
ORYX.I18N.PetriNetSoundness.tipProperTerminationCriteria='<i18n:message key="ORYX.I18N.PetriNetSoundness.tipProperTerminationCriteria"><b>Proper Termination Criteria</b>: The final state is the only state reachable from the initial state in which there is a token in the final place. If any improper terminating states are detected, click to show one counter example.</i18n:message>';
ORYX.I18N.PetriNetSoundness.thereAre='<i18n:message key="ORYX.I18N.PetriNetSoundness.thereAre">There are</i18n:message>';
ORYX.I18N.PetriNetSoundness.markings='<i18n:message key="ORYX.I18N.PetriNetSoundness.markings">markings covering the final marking.</i18n:message>';
ORYX.I18N.PetriNetSoundness.tipDeadTransitionsCriteria='<i18n:message key="ORYX.I18N.PetriNetSoundness.tipDeadTransitionsCriteria"><b>No Dead Transitions Criteria</b>: Each transition can contribute to at least one process instance. Click to see all dead transitions.</i18n:message>';
ORYX.I18N.PetriNetSoundness.deadTransitions='<i18n:message key="ORYX.I18N.PetriNetSoundness.deadTransitions">dead transitions</i18n:message>';
ORYX.I18N.PetriNetSoundness.tipTransitionParticipationCriteria='<i18n:message key="ORYX.I18N.PetriNetSoundness.tipTransitionParticipationCriteria"><b>Transition Participation Criteria</b>: Each transition participates in at least one process instance that starts in the initial state and reaches the final state. Click to see all transitions not participating in any process instance.</i18n:message>';
ORYX.I18N.PetriNetSoundness.transtionsNoParticipants='<i18n:message key="ORYX.I18N.PetriNetSoundness.transtionsNoParticipants">transitions that cannot participate in a properly terminating firing sequence.</i18n:message>';
ORYX.I18N.PetriNetSoundness.soundnessChecker='<i18n:message key="ORYX.I18N.PetriNetSoundness.soundnessChecker">Soundness Checker</i18n:message>';
ORYX.I18N.PetriNetSoundness.check='<i18n:message key="ORYX.I18N.PetriNetSoundness.check">Check</i18n:message>';
ORYX.I18N.PetriNetSoundness.hideErrors='<i18n:message key="ORYX.I18N.PetriNetSoundness.hideErrors">Hide Errors</i18n:message>';
ORYX.I18N.PetriNetSoundness.syntaxErrors='<i18n:message key="ORYX.I18N.PetriNetSoundness.syntaxErrors">Some syntax errors have been found, please correct them!</i18n:message>';
ORYX.I18N.PetriNetSoundness.checks='<i18n:message key="ORYX.I18N.PetriNetSoundness.checks">Checks</i18n:message>';
ORYX.I18N.PetriNetSoundness.structuralSound='<i18n:message key="ORYX.I18N.PetriNetSoundness.structuralSound">Structural Sound (Workflow Net)</i18n:message>';
ORYX.I18N.PetriNetSoundness.exactlyOneInitialPlace='<i18n:message key="ORYX.I18N.PetriNetSoundness.exactlyOneInitialPlace">There must be exactly one initial place, which is the only place without any incoming edges.</i18n:message>';
ORYX.I18N.PetriNetSoundness.initialPlacesFound='<i18n:message key="ORYX.I18N.PetriNetSoundness.initialPlacesFound">initial places found.</i18n:message>';
ORYX.I18N.PetriNetSoundness.exactlyOneFinalPlace='<i18n:message key="ORYX.I18N.PetriNetSoundness.exactlyOneFinalPlace">There must be exactly one final place, which is the only place without any outgoing edges.</i18n:message>';
ORYX.I18N.PetriNetSoundness.finalPlacesFound='<i18n:message key="ORYX.I18N.PetriNetSoundness.finalPlacesFound">final places found.</i18n:message>';
ORYX.I18N.PetriNetSoundness.eachNode='<i18n:message key="ORYX.I18N.PetriNetSoundness.eachNode">Each node in the process model is on the path from the initial node to the final node.</i18n:message>';
ORYX.I18N.PetriNetSoundness.exactlyOneInitAndFinalPlace='<i18n:message key="ORYX.I18N.PetriNetSoundness.exactlyOneInitAndFinalPlace">There must be exactly one initial and final place to perform further checks!</i18n:message>';
ORYX.I18N.PetriNetSoundness.nodesNoInPath='<i18n:message key="ORYX.I18N.PetriNetSoundness.nodesNoInPath">nodes that aren\\\'t on any path from beginning to end found.</i18n:message>';
ORYX.I18N.PetriNetSoundness.sound='<i18n:message key="ORYX.I18N.PetriNetSoundness.sound">Sound</i18n:message>';
ORYX.I18N.PetriNetSoundness.weakSound='<i18n:message key="ORYX.I18N.PetriNetSoundness.weakSound">Weak Sound</i18n:message>';
ORYX.I18N.PetriNetSoundness.relaxedSound='<i18n:message key="ORYX.I18N.PetriNetSoundness.relaxedSound">Relaxed Sound</i18n:message>';

if(!ORYX.I18N.main) ORYX.I18N.main = {}

ORYX.I18N.main.errorDetails='<i18n:message key="ORYX.I18N.main.errorDetails">Error Details</i18n:message>';
ORYX.I18N.main.details='<i18n:message key="ORYX.I18N.main.details">Details</i18n:message>';
ORYX.I18N.main.errorOpening='<i18n:message key="ORYX.I18N.main.errorOpening"><p/><p/><center>Could not open requested business process due to processing errors. <br/> Empty process was loaded instead.<br/> Click on the Details tab below to view error details.</center></i18n:message>';
ORYX.I18N.main.errorLoadingProc='<i18n:message key="ORYX.I18N.main.errorLoadingProc">Process loading errors</i18n:message>';
ORYX.I18N.main.shapeRepo='<i18n:message key="ORYX.I18N.main.shapeRepo">Shape Repository</i18n:message>';
ORYX.I18N.main.failSave='<i18n:message key="ORYX.I18N.main.failSave">Failed to save process SVG.</i18n:message>';
ORYX.I18N.main.unableUserAction='<i18n:message key="ORYX.I18N.main.unableUserAction">Unable to perform user action due to error(s).<br/>Validate your process before saving, and view server logs to see error details.</i18n:message>';

if(!ORYX.I18N.constraintExpr) ORYX.I18N.constraintExpr = {}
ORYX.I18N.constraintExpr.errorPropertyMissing='<i18n:message key="ORYX.I18N.constraintExpr.errorPropertyMissing">Error reading definition of showConstraintEditorWhen: \\\'property\\\' is missing!</i18n:message>';
ORYX.I18N.constraintExpr.errorValueIsMissing='<i18n:message key="ORYX.I18N.constraintExpr.errorValueIsMissing">Error reading definition of showConstraintEditorWhen: \\\'value\\\' is missing!</i18n:message>';
ORYX.I18N.constraintExpr.configureProcess='<i18n:message key="ORYX.I18N.constraintExpr.configureProcess">Please configure Process \\\'package\\\' attribute first</i18n:message>';
ORYX.I18N.constraintExpr.defineOneModel='<i18n:message key="ORYX.I18N.constraintExpr.defineOneModel">You must define at least 1 Model Entity in your process!</i18n:message>';
ORYX.I18N.constraintExpr.factNameMandatory='<i18n:message key="ORYX.I18N.constraintExpr.factNameMandatory">Fact Name is mandatory</i18n:message>';
ORYX.I18N.constraintExpr.mustSpecifyField='<i18n:message key="ORYX.I18N.constraintExpr.mustSpecifyField">You must specify a field for</i18n:message>';
ORYX.I18N.constraintExpr.modelEntity='<i18n:message key="ORYX.I18N.constraintExpr.modelEntity">Model Entity</i18n:message>';
ORYX.I18N.constraintExpr.mustSpecifyValue='<i18n:message key="ORYX.I18N.constraintExpr.mustSpecifyValue">You must specify a value for</i18n:message>';
ORYX.I18N.constraintExpr.errorGettingWS='<i18n:message key="ORYX.I18N.constraintExpr.errorGettingWS">Error getting Working Set Definition</i18n:message>';

if(!ORYX.I18N.lockNode) ORYX.I18N.lockNode = {}

ORYX.I18N.lockNode.lock='<i18n:message key="ORYX.I18N.lockNode.lock">Lock</i18n:message>';
ORYX.I18N.lockNode.lock_desc='<i18n:message key="ORYX.I18N.lockNode.lock_desc">Lock Elements</i18n:message>';
ORYX.I18N.lockNode.unlock='<i18n:message key="ORYX.I18N.lockNode.unlock">Unlock</i18n:message>';
ORYX.I18N.lockNode.unlock_desc='<i18n:message key="ORYX.I18N.lockNode.unlock_desc">Unlock Elements</i18n:message>';
ORYX.I18N.lockNode.nodeSource='<i18n:message key="ORYX.I18N.lockNode.nodeSource">Node Source</i18n:message>';
ORYX.I18N.lockNode.nodeSourceNoSpecified='<i18n:message key="ORYX.I18N.lockNode.nodeSourceNoSpecified">Node source was not specified.</i18n:message>';

ORYX.I18N.paint_name='<i18n:message key="ORYX.I18N.paint_name">Paint</i18n:message>';
ORYX.I18N.paint_desc='<i18n:message key="ORYX.I18N.paint_desc">Paint</i18n:message>';

if(!ORYX.I18N.patternCreator) ORYX.I18N.patternCreator = {}
ORYX.I18N.patternCreator.errorAttaching='<i18n:message key="ORYX.I18N.patternCreator.errorAttaching">Cannot attach Pattern to selected node(s).</i18n:message>';
ORYX.I18N.patternCreator.invalidData='<i18n:message key="ORYX.I18N.patternCreator.invalidData">Invalid pattern data.</i18n:message>';
ORYX.I18N.patternCreator.patternName='<i18n:message key="ORYX.I18N.patternCreator.patternName">Pattern Name</i18n:message>';
ORYX.I18N.patternCreator.create='<i18n:message key="ORYX.I18N.patternCreator.create">Create a new Workflow Pattern</i18n:message>';
ORYX.I18N.patternCreator.invalidSelect='<i18n:message key="ORYX.I18N.patternCreator.invalidSelect">Invalid selection.</i18n:message>';
ORYX.I18N.patternCreator.noNodesSel='<i18n:message key="ORYX.I18N.patternCreator.noNodesSel">No nodes selected</i18n:message>';

if(!ORYX.I18N.voiceCommand) ORYX.I18N.voiceCommand = {}
ORYX.I18N.voiceCommand.commandNotFound='<i18n:message key="ORYX.I18N.voiceCommand.commandNotFound">Cannot find voice command</i18n:message>';
ORYX.I18N.voiceCommand.invalidcommand='<i18n:message key="ORYX.I18N.voiceCommand.invalidcommand">Invalid voice command.</i18n:message>';

ORYX.I18N.imageViewer='<i18n:message key="ORYX.I18N.imageViewer">Image Viewer</i18n:message>';
ORYX.I18N.svgViewer='<i18n:message key="ORYX.I18N.svgViewer">SVG Viewer</i18n:message>';

if(!ORYX.I18N.propertyNames) ORYX.I18N.propertyNames = {}

ORYX.I18N.propertyNames['bgcolor'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_bgColor">Background Color</i18n:message>';
ORYX.I18N.propertyNames['bgcolor_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_bgColor_desc">Background Color</i18n:message>';
ORYX.I18N.propertyNames['bordercolor'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_borderColor">Border Color</i18n:message>';
ORYX.I18N.propertyNames['bordercolor_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_borderColor_desc">Border Color</i18n:message>';
ORYX.I18N.propertyNames['fontcolor'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_fontcolor">Font color</i18n:message>';
ORYX.I18N.propertyNames['fontcolor_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_fontcolor_desc">Font color</i18n:message>';
ORYX.I18N.propertyNames['fontsize'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_fontsize">Font Size</i18n:message>';
ORYX.I18N.propertyNames['fontsize_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_BGCOLOR_fontsize_desc">Font Size</i18n:message>';

ORYX.I18N.propertyNames['min'] = '<i18n:message key="ORYX.I18N.propertyNames_min">Processing time (min)</i18n:message>';
ORYX.I18N.propertyNames['min_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_min_desc">Processing time (min)</i18n:message>';
ORYX.I18N.propertyNames['max'] = '<i18n:message key="ORYX.I18N.propertyNames_max">Processing time (max)</i18n:message>';
ORYX.I18N.propertyNames['max_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_max_desc">Processing time (max)</i18n:message>';
ORYX.I18N.propertyNames['standarddeviation'] = '<i18n:message key="ORYX.I18N.propertyNames_standarddeviation">Standard Deviation</i18n:message>';
ORYX.I18N.propertyNames['standarddeviation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_standarddeviation_desc">Standard Deviation</i18n:message>';
ORYX.I18N.propertyNames['mean'] = '<i18n:message key="ORYX.I18N.propertyNames_mean">Processing time (mean)</i18n:message>';
ORYX.I18N.propertyNames['mean_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_mean_desc">Processing time (mean)</i18n:message>';
ORYX.I18N.propertyNames['distributiontype'] = '<i18n:message key="ORYX.I18N.propertyNames_distributiontype">Distribution Type</i18n:message>';
ORYX.I18N.propertyNames['distributiontype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_distributiontype_desc">Distribution Type</i18n:message>';
ORYX.I18N.propertyNames['quantity'] = '<i18n:message key="ORYX.I18N.propertyNames_quantity">Staff availability</i18n:message>';
ORYX.I18N.propertyNames['quantity_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_quantity_desc">Staff availability</i18n:message>';
ORYX.I18N.propertyNames['workinghours'] = '<i18n:message key="ORYX.I18N.propertyNames_workinghours">Working Hours</i18n:message>';
ORYX.I18N.propertyNames['workinghours_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_workinghours_desc">Working Hours</i18n:message>';
ORYX.I18N.propertyNames['unitcost'] = '<i18n:message key="ORYX.I18N.propertyNames_unitcost">Cost per time unit</i18n:message>';
ORYX.I18N.propertyNames['unitcost_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_unitcost_desc">Cost per time unit</i18n:message>';

ORYX.I18N.propertyNames['multipleinstance'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstance">Multiple Instance</i18n:message>';
ORYX.I18N.propertyNames['multipleinstance_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstance_desc">Specifies whether the task is a multiple instance task.</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancecollectioninput'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancecollectioninput">MI collection input</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancecollectioninput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancecollectioninput_desc">Sets the multiple instances collection input.</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancecollectionoutput'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancecollectionoutput">MI collection output</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancecollectionoutput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancecollectionoutput_desc">Sets the multiple instances collection output.</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancedatainput'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancedatainput">MI data input</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancedatainput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancedatainput_desc">Sets a data input for MI.</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancedataoutput'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancedataoutput">MI data output</i18n:message>';
ORYX.I18N.propertyNames['multipleinstancedataoutput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_multipleinstancedataoutput_desc">Sets a data output for MI.</i18n:message>';
ORYX.I18N.propertyNames['isselectable'] = '<i18n:message key="ORYX.I18N.propertyNames_isselectable">Selectable</i18n:message>';
ORYX.I18N.propertyNames['isselectable_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_isselectable_desc">This attribute is used to set selectable properties for every node and edges.</i18n:message>';
ORYX.I18N.propertyNames['name'] = '<i18n:message key="ORYX.I18N.propertyNames_name">Name</i18n:message>';
ORYX.I18N.propertyNames['name_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_name_desc">The descriptive name of the BPMN element.</i18n:message>';
ORYX.I18N.propertyNames['documentation'] = '<i18n:message key="ORYX.I18N.propertyNames_documentation">Documentation</i18n:message>';
ORYX.I18N.propertyNames['documentation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_documentation_desc">This attribute is used to annotate the BPMN element, such as descriptions and other documentation.</i18n:message>';

ORYX.I18N.propertyNames['taskName'] = '<i18n:message key="ORYX.I18N.propertyNames_taskName">Task Name</i18n:message>';
ORYX.I18N.propertyNames['taskName_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_taskName_desc">Task Name</i18n:message>';
ORYX.I18N.propertyNames['taskname'] = '<i18n:message key="ORYX.I18N.propertyNames_taskName">Task Name</i18n:message>';
ORYX.I18N.propertyNames['taskname_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_taskName_desc">Task Name</i18n:message>';

ORYX.I18N.propertyNames['datainputset'] = '<i18n:message key="ORYX.I18N.propertyNames_datainputset">DataInputSet</i18n:message>';
ORYX.I18N.propertyNames['datainputset_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_datainputset_desc">An InputSet is a collection of DataInput elements that together define a valid set of data inputs.</i18n:message>';
ORYX.I18N.propertyNames['dataoutputset'] = '<i18n:message key="ORYX.I18N.propertyNames_dataoutputset">DataOutputSet</i18n:message>';
ORYX.I18N.propertyNames['dataoutputset_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_dataoutputset_desc">An OutputSet is a collection of DataOutputs elements that together may be produced as output from an Activity or Event.</i18n:message>';
ORYX.I18N.propertyNames['assignments'] = '<i18n:message key="ORYX.I18N.propertyNames_assignments">Assignments</i18n:message>';
ORYX.I18N.propertyNames['assignments_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_assignments_desc">Assignments</i18n:message>';
ORYX.I18N.propertyNames['onEntryActions'] = '<i18n:message key="ORYX.I18N.propertyNames_onEntryActions">On Entry Actions</i18n:message>';
ORYX.I18N.propertyNames['onEntryActions_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_onEntryActions_desc">On Entry Actions</i18n:message>';
ORYX.I18N.propertyNames['onExitActions'] = '<i18n:message key="ORYX.I18N.propertyNames_onExitActions">On Exit Actions</i18n:message>';
ORYX.I18N.propertyNames['onExitActions_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_onExitActions_desc">On Exit Actions</i18n:message>';
ORYX.I18N.propertyNames['onentryactions'] = '<i18n:message key="ORYX.I18N.propertyNames_onEntryActions">On Entry Actions</i18n:message>';
ORYX.I18N.propertyNames['onentryactions_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_onEntryActions_desc">On Entry Actions</i18n:message>';
ORYX.I18N.propertyNames['onexitactions'] = '<i18n:message key="ORYX.I18N.propertyNames_onExitActions">On Exit Actions</i18n:message>';
ORYX.I18N.propertyNames['onexitactions_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_onExitActions_desc">On Exit Actions</i18n:message>';

ORYX.I18N.propertyNames['script_language'] = '<i18n:message key="ORYX.I18N.propertyNames_script_language">Script Language</i18n:message>';
ORYX.I18N.propertyNames['script_language_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_script_language_desc">Defines the script language.</i18n:message>';
ORYX.I18N.propertyNames['vardefs'] = '<i18n:message key="ORYX.I18N.propertyNames_vardefs">Variable Definitions</i18n:message>';
ORYX.I18N.propertyNames['vardefs_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_vardefs_desc">Comma-separated variable definitions</i18n:message>';
ORYX.I18N.propertyNames['actors'] = '<i18n:message key="ORYX.I18N.propertyNames_actors">Actors</i18n:message>';
ORYX.I18N.propertyNames['actors_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_actors_desc">Comma-separated list of actors</i18n:message>';
ORYX.I18N.propertyNames['groupid'] = '<i18n:message key="ORYX.I18N.propertyNames_groupid">Groups</i18n:message>';
ORYX.I18N.propertyNames['groupid_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_groupid_desc">The group id that is responsible for executing the human task</i18n:message>';
ORYX.I18N.propertyNames['tasktype'] = '<i18n:message key="ORIX.I18N.propertyNames_tasktype">Task Type</i18n:message>';
ORYX.I18N.propertyNames['tasktype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_tasktype_desc">Task Type</i18n:message>';
ORYX.I18N.propertyNames['comment'] = '<i18n:message key="ORYX.I18N.propertyNames_comment">Comment</i18n:message>';
ORYX.I18N.propertyNames['comment_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_comment_desc">Comment</i18n:message>';
ORYX.I18N.propertyNames['content'] = '<i18n:message key="ORYX.I18N.propertyNames_content">Content</i18n:message>';
ORYX.I18N.propertyNames['content_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_content_desc">The data associated with this task</i18n:message>';
ORYX.I18N.propertyNames['createdby'] = '<i18n:message key="ORYX.I18N.propertyNames_createdby">Created by</i18n:message>';
ORYX.I18N.propertyNames['createdby_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_createdby_desc">User name of Task creator</i18n:message>';


ORYX.I18N.propertyNames['boundarycancelactivity'] = '<i18n:message key="ORYX.I18N.propertyNames_boundarycancelactivity">CancelActivity</i18n:message>';
ORYX.I18N.propertyNames['boundarycancelactivity_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_boundarycancelactivity_desc">Denotes whether the activity should be cancelled or not, i.e., whether the boundary catch event acts as an error or an escalation. If the activity is not cancelled, multiple instances of that handler can run concurrently.</i18n:message>';
ORYX.I18N.propertyNames['dataoutputassociations'] = '<i18n:message key="ORYX.I18N.propertyNames_dataoutputassociations">DataOutputAssociations</i18n:message>';
ORYX.I18N.propertyNames['dataoutputassociations_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_dataoutputassociations_desc">The Data Associations of the catch Event.</i18n:message>';
ORYX.I18N.propertyNames['dataoutput'] = '<i18n:message key="ORYX.I18N.propertyNames_dataoutput">DataOutput</i18n:message>';
ORYX.I18N.propertyNames['dataoutput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_dataoutput_desc">The Data Associations of the catch Event.</i18n:message>';
ORYX.I18N.propertyNames['probability'] = '<i18n:message key="ORYX.I18N.propertyNames_probability">Probability (Boundary Event only)</i18n:message>';
ORYX.I18N.propertyNames['probability_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_probability_desc">Probability</i18n:message>';
ORYX.I18N.propertyNames['eventdefinitions'] = '<i18n:message key="ORYX.I18N.propertyNames_eventdefinitions">EventDefinitions</i18n:message>';
ORYX.I18N.propertyNames['eventdefinitions_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_eventdefinitions_desc">EventDefinitions (EventDefinition) is an attribute that defines the type of contained triggers expected for a catch Event.</i18n:message>';
ORYX.I18N.propertyNames['datainputassociations'] = '<i18n:message key="ORYX.I18N.propertyNames_datainputassociations">DataInputAssociations</i18n:message>';
ORYX.I18N.propertyNames['datainputassociations_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_datainputassociations_desc">The Data Associations of the throw Event.</i18n:message>';
ORYX.I18N.propertyNames['datainput'] = '<i18n:message key="ORYX.I18N.propertyNames_datainput">Data Input</i18n:message>';
ORYX.I18N.propertyNames['datainput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_datainput_desc">The Data Associations of the throw Event.</i18n:message>';
ORYX.I18N.propertyNames['waitforcompletion'] = '<i18n:message key="ORYX.I18N.propertyNames_waitforcompletion">Wait for completion</i18n:message>';
ORYX.I18N.propertyNames['waitforcompletion_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_waitforcompletion_desc">Determine whether a throw compensation is performed synchronously or asynchronously.</i18n:message>';
ORYX.I18N.propertyNames['activityref'] = '<i18n:message key="ORYX.I18N.propertyNames_activityref">Activity reference</i18n:message>';
ORYX.I18N.propertyNames['activityref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_activityref_desc">The activity related to the compensation event</i18n:message>';
ORYX.I18N.propertyNames['isinterrupting'] = '<i18n:message key="ORYX.I18N.propertyNames_isinterrupting">Is Interrupting</i18n:message>';
ORYX.I18N.propertyNames['isinterrupting_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_isinterrupting_desc">This attribute denotes whether the Sub-Process encompassing the Event Sub-Process should be cancelled or not.</i18n:message>';
ORYX.I18N.propertyNames['executable'] = '<i18n:message key="ORYX.I18N.propertyNames_executable">Executable</i18n:message>';
ORYX.I18N.propertyNames['executable_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_executable_desc">Executable</i18n:message>';
ORYX.I18N.propertyNames['package'] = '<i18n:message key="ORYX.I18N.propertyNames_package">Package</i18n:message>';
ORYX.I18N.propertyNames['package_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_package_desc">Package</i18n:message>';
ORYX.I18N.propertyNames['adhocprocess'] = '<i18n:message key="ORYX.I18N.propertyNames_adhocprocess">AdHoc</i18n:message>';
ORYX.I18N.propertyNames['adhocprocess_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_adhocprocess_desc">Defines an AdHoc process</i18n:message>';
ORYX.I18N.propertyNames['imports'] = '<i18n:message key="ORYX.I18N.propertyNames_imports">Imports</i18n:message>';
ORYX.I18N.propertyNames['imports_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_imports_desc">Comma-separated imports</i18n:message>';
ORYX.I18N.propertyNames['globals'] = '<i18n:message key="ORYX.I18N.propertyNames_globals">Globals</i18n:message>';
ORYX.I18N.propertyNames['globals_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_globals_desc">Comma-separated globals</i18n:message>';
ORYX.I18N.propertyNames['id'] = '<i18n:message key="ORYX.I18N.propertyNames_id">ID</i18n:message>';
ORYX.I18N.propertyNames['id_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_id_desc">ID</i18n:message>';
ORYX.I18N.propertyNames['version'] = '<i18n:message key="ORYX.I18N.propertyNames_version">Version</i18n:message>';
ORYX.I18N.propertyNames['version_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_version_desc">This defines the Version number of the Diagram.</i18n:message>';
ORYX.I18N.propertyNames['timeunit'] = '<i18n:message key="ORYX.I18N.propertyNames_timeunit">Base time unit</i18n:message>';
ORYX.I18N.propertyNames['timeunit_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_timeunit_desc">Time unit options (seconds, minutes, hours)</i18n:message>';
ORYX.I18N.propertyNames['currency'] = '<i18n:message key="ORYX.I18N.propertyNames_currency">Base Currency</i18n:message>';
ORYX.I18N.propertyNames['currency_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_currency_desc">Base currency (ISO 4217)</i18n:message>';
ORYX.I18N.propertyNames['targetnamespace'] = '<i18n:message key="ORYX.I18N.propertyNames_targetnamespace">Target Namespace</i18n:message>';
ORYX.I18N.propertyNames['targetnamespace_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_targetnamespace_desc">Defines the XML namespace of the elements inside the document.</i18n:message>';
ORYX.I18N.propertyNames['typelanguage'] = '<i18n:message key="ORYX.I18N.propertyNames_typelanguage">TypeLanguage</i18n:message>';
ORYX.I18N.propertyNames['typelanguage_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_typelanguage_desc">This attribute identifies the type system used by the elements of this Definition.</i18n:message>';
ORYX.I18N.propertyNames['itemsubjectref'] = '<i18n:message key="ORYX.I18N.propertyNames_itemsubjectref">ItemSubjectRef</i18n:message>';
ORYX.I18N.propertyNames['itemsubjectref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_itemsubjectref_desc">Specification of the items that are stored or conveyed by the ItemAwareElement</i18n:message>';
ORYX.I18N.propertyNames['datastate'] = '<i18n:message key="ORYX.I18N.propertyNames_datastate">DataState</i18n:message>';
ORYX.I18N.propertyNames['datastate_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_datastate_desc">A reference to the DataState, which defines certain states for the data contained in the item.</i18n:message>';
ORYX.I18N.propertyNames['isforcompensation'] = '<i18n:message key="ORYX.I18N.propertyNames_isforcompensation">Is for Compensation</i18n:message>';
ORYX.I18N.propertyNames['isforcompensation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_isforcompensation_desc">A flag that identifies whether this activity is intended for the purposes of compensation.</i18n:message>';
ORYX.I18N.propertyNames['operationname'] = '<i18n:message key="ORYX.I18N.propertyNames_operationname">OperationName</i18n:message>';
ORYX.I18N.propertyNames['operationname_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_operationname_desc">The descriptive name for the operation element.</i18n:message>';
ORYX.I18N.propertyNames['inmessagename'] = '<i18n:message key="ORYX.I18N.propertyNames_inmessagename">InMessageName</i18n:message>';
ORYX.I18N.propertyNames['inmessagename_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_inmessagename_desc">The descriptive name for the InMessage element</i18n:message>';
ORYX.I18N.propertyNames['inmsgitemkind'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgitemkind">InMessageItemKind</i18n:message>';
ORYX.I18N.propertyNames['inmsgitemkind_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgitemkind_desc">This defines the nature of the Item. Possible values are Physical or Information. The default value is Information.</i18n:message>';
ORYX.I18N.propertyNames['inmsgstructure'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgstructure">InMessageStructure</i18n:message>';
ORYX.I18N.propertyNames['inmsgstructure_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgstructure_desc">This defines the nature of the Item. Possible values are Physical or Information. The default value is Information.</i18n:message>';
ORYX.I18N.propertyNames['inmsgimport'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgimport">InMessageImport</i18n:message>';
ORYX.I18N.propertyNames['inmsgimport_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgimport_desc">Identifies the location of the data structure and its format. If the importType attribute is left unspecified, the typeLanguage specified in the Definitions is assumed.</i18n:message>';
ORYX.I18N.propertyNames['inmsgiscollection'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgiscollection">InMessageIsCollection</i18n:message>';
ORYX.I18N.propertyNames['inmsgiscollection_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_inmsgiscollection_desc">Setting this flag to true indicates that the actual data type is a collection.</i18n:message>';
ORYX.I18N.propertyNames['outmessagename'] = '<i18n:message key="ORYX.I18N.propertyNames_outmessagename">OutMessageName</i18n:message>';
ORYX.I18N.propertyNames['outmessagename_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_outmessagename_desc">The descriptive name for the OutMessage element</i18n:message>';
ORYX.I18N.propertyNames['outmsgitemkind'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgitemkind">OutMessageItemKind</i18n:message>';
ORYX.I18N.propertyNames['outmsgitemkind_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgitemkind_desc">This defines the nature of the Item. Possible values are Physical or Information. The default value is Information.</i18n:message>';
ORYX.I18N.propertyNames['outmsgstructure'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgstructure">OutMessageStructure</i18n:message>';
ORYX.I18N.propertyNames['outmsgstructure_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgstructure_desc">This defines the nature of the Item. Possible values are Physical or Information. The default value is Information.</i18n:message>';
ORYX.I18N.propertyNames['outmsgimport'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgimport">OutMessageImport</i18n:message>';
ORYX.I18N.propertyNames['outmsgimport_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgimport_desc">Identifies the location of the data structure and its format. If the importType attribute is left unspecified, the typeLanguage specified in the Definitions is assumed.</i18n:message>';
ORYX.I18N.propertyNames['outmsgiscollection'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgiscollection">OutMessageIsCollection</i18n:message>';
ORYX.I18N.propertyNames['outmsgiscollection_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_outmsgiscollection_desc">Setting this flag to true indicates that the actual data type is a collection.</i18n:message>';
ORYX.I18N.propertyNames['loopmaximum'] = '<i18n:message key="ORYX.I18N.propertyNames_loopmaximum">LoopMaximum</i18n:message>';
ORYX.I18N.propertyNames['loopmaximum_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_loopmaximum_desc">LoopMaximum</i18n:message>';
ORYX.I18N.propertyNames['loopcardinality'] = '<i18n:message key="ORYX.I18N.propertyNames_loopcardinality">LoopCardinality</i18n:message>';
ORYX.I18N.propertyNames['loopcardinality_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_loopcardinality_desc">A numeric Expression that controls the number of Activity instances that will be created. This Expression MUST evaluate to an integer.</i18n:message>';
ORYX.I18N.propertyNames['loopdatainput'] = '<i18n:message key="ORYX.I18N.propertyNames_loopdatainput">LoopDataInput</i18n:message>';
ORYX.I18N.propertyNames['loopdatainput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_loopdatainput_desc">A reference to a DataInput which is part of the Activity InputOutputSpecification. This DataInput is used to determine the number of Activity instances, one Activity instance per item in the collection of data stored in that DataInput element.</i18n:message>';
ORYX.I18N.propertyNames['loopdataoutput'] = '<i18n:message key="ORYX.I18N.propertyNames_loopdataoutput">LoopDataOutput</i18n:message>';
ORYX.I18N.propertyNames['loopdataoutput_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_loopdataoutput_desc">A reference to a DataOutput which is part of the Activity InputOutputSpecification.</i18n:message>';
ORYX.I18N.propertyNames['inputdataitem'] = '<i18n:message key="ORYX.I18N.propertyNames_inputdataitem">InputDataItem</i18n:message>';
ORYX.I18N.propertyNames['inputdataitem_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_inputdataitem_desc">A Property, representing for every Activity instance the single item of the collection stored in the loopDataInput.</i18n:message>';
ORYX.I18N.propertyNames['outputdataitem'] = '<i18n:message key="ORYX.I18N.propertyNames_outputdataitem">OutputDataItem</i18n:message>';
ORYX.I18N.propertyNames['outputdataitem_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_outputdataitem_desc">A Property, representing for every Activity instance the single item of the collection stored in the loopDataOutput.</i18n:message>';
ORYX.I18N.propertyNames['complexbehaviordefinition'] = '<i18n:message key="ORYX.I18N.propertyNames_complexbehaviordefinition">ComplexBehaviorDefinition:</i18n:message>';
ORYX.I18N.propertyNames['complexbehaviordefinition_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_complexbehaviordefinition_desc">Controls when and which Events are thrown in case behavior is set to complex.</i18n:message>';
ORYX.I18N.propertyNames['onebehavioreventref'] = '<i18n:message key="ORYX.I18N.propertyNames_onebehavioreventref">OneBehaviorEventRef</i18n:message>';
ORYX.I18N.propertyNames['onebehavioreventref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_onebehavioreventref_desc">The EventDefinition which is thrown when behavior is set to one and the first internal Activity instance has completed.</i18n:message>';
ORYX.I18N.propertyNames['nonebehavioreventref'] = '<i18n:message key="ORYX.I18N.propertyNames_nonebehavioreventref">NoneBehaviorEventRef</i18n:message>';
ORYX.I18N.propertyNames['nonebehavioreventref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_nonebehavioreventref_desc">NoneBehaviorEventRef</i18n:message>';
ORYX.I18N.propertyNames['completioncondition'] = '<i18n:message key="ORYX.I18N.propertyNames_completioncondition">CompletionCondition:</i18n:message>';
ORYX.I18N.propertyNames['completioncondition_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_completioncondition_desc">This attribute defines a Boolean Expression that when evaluated to true, cancels the remaining Activity instances and produces a token.</i18n:message>';
ORYX.I18N.propertyNames['messageref'] = '<i18n:message key="ORYX.I18N.propertyNames_messageref">MessageRef</i18n:message>';
ORYX.I18N.propertyNames['messageref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_messageref_desc">MessageRef</i18n:message>';
ORYX.I18N.propertyNames['script'] = '<i18n:message key="ORYX.I18N.propertyNames_script">Script</i18n:message>';
ORYX.I18N.propertyNames['script_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_script_desc">Script that can be run when the Task is performed. Related to the Script TaskType, if a script is not included, then the Task will act equivalent to a TaskType of None.</i18n:message>';
ORYX.I18N.propertyNames['isadhoc'] = '<i18n:message key="ORYX.I18N.propertyNames_isadhoc">isAdHoc</i18n:message>';
ORYX.I18N.propertyNames['isadhoc_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_isadhoc_desc">isAdHoc</i18n:message>';
ORYX.I18N.propertyNames['adhocordering'] = '<i18n:message key="ORYX.I18N.propertyNames_adhocordering">AdHocOrdering</i18n:message>';
ORYX.I18N.propertyNames['adhocordering_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_adhocordering_desc">AdHocOrdering</i18n:message>';
ORYX.I18N.propertyNames['adhoccompletioncondition'] = '<i18n:message key="ORYX.I18N.propertyNames_adhoccompletioncondition">AdHocCompletionCondition</i18n:message>';
ORYX.I18N.propertyNames['adhoccompletioncondition_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_adhoccompletioncondition_desc">AdHocCompletionCondition</i18n:message>';
ORYX.I18N.propertyNames['adhoccancelremaininginstances'] = '<i18n:message key="ORYX.I18N.propertyNames_adhoccancelremaininginstances">AdhocCancelRemainingInstances</i18n:message>';
ORYX.I18N.propertyNames['adhoccancelremaininginstances_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_adhoccancelremaininginstances_desc">This attribute is used only if ordering is parallel. It determines whether running instances are cancelled when the completionCondition becomes true.</i18n:message>';
ORYX.I18N.propertyNames['processtype'] = '<i18n:message key="ORYX.I18N.propertyNames_processtype">ProcessType</i18n:message>';
ORYX.I18N.propertyNames['processtype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_processtype_desc">The processType attribute Provides additional information about the level of abstraction modeled by this Process.</i18n:message>';
ORYX.I18N.propertyNames['isclosed'] = '<i18n:message key="ORYX.I18N.propertyNames_isclosed">isClosed</i18n:message>';
ORYX.I18N.propertyNames['isclosed_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_isclosed_desc">A Boolean value specifying whether interactions, such as sending and receiving Messages and Events, not modeled in the Process can occur when the Process is executed or performed. If the value is true, they MAY NOT occur. If the value is false, they MAY occur.</i18n:message>';
ORYX.I18N.propertyNames['multiinstance'] = '<i18n:message key="ORYX.I18N.propertyNames_multiinstance">is Multi Instance Participant</i18n:message>';
ORYX.I18N.propertyNames['multiinstance_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_multiinstance_desc">is Multi Instance Participant</i18n:message>';
ORYX.I18N.propertyNames['boundaryvisible'] = '<i18n:message key="ORYX.I18N.propertyNames_boundaryvisible">BoundaryVisible</i18n:message>';
ORYX.I18N.propertyNames['boundaryvisible_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_boundaryvisible_desc">Defines if the rectangular boundary for the Pool is visible.</i18n:message>';
ORYX.I18N.propertyNames['processname'] = '<i18n:message key="ORYX.I18N.propertyNames_processname">ProcessName</i18n:message>';
ORYX.I18N.propertyNames['processname_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_processname_desc">ProcessName</i18n:message>';
ORYX.I18N.propertyNames['status'] = '<i18n:message key="ORYX.I18N.propertyNames_status">Status</i18n:message>';
ORYX.I18N.propertyNames['status_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_status_desc">Status</i18n:message>';
ORYX.I18N.propertyNames['adhoc'] = '<i18n:message key="ORYX.I18N.propertyNames_adhoc">AdHoc</i18n:message>';
ORYX.I18N.propertyNames['adhoc_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_adhoc_desc">AdHoc</i18n:message>';
ORYX.I18N.propertyNames['suppressjoinfailure'] = '<i18n:message key="ORYX.I18N.propertyNames_suppressjoinfailure">SuppressJoinFailure</i18n:message>';
ORYX.I18N.propertyNames['suppressjoinfailure_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_suppressjoinfailure_desc">SuppressJoinFailure</i18n:message>';
ORYX.I18N.propertyNames['enableinstancecompensation'] = '<i18n:message key="ORYX.I18N.propertyNames_enableinstancecompensation">EnableBooleanCompensation</i18n:message>';
ORYX.I18N.propertyNames['enableinstancecompensation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_enableinstancecompensation_desc">EnableBooleanCompensation</i18n:message>';
ORYX.I18N.propertyNames['processcategories'] = '<i18n:message key="ORYX.I18N.propertyNames_processcategories">ProcessCategories</i18n:message>';
ORYX.I18N.propertyNames['processcategories_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_processcategories_desc">ProcessCategories</i18n:message>';
ORYX.I18N.propertyNames['processdocumentation'] = '<i18n:message key="ORYX.I18N.propertyNames_processdocumentation">Process Documentation</i18n:message>';
ORYX.I18N.propertyNames['processdocumentation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_processdocumentation_desc">Process Documentation</i18n:message>';
ORYX.I18N.propertyNames['transformation'] = '<i18n:message key="ORYX.I18N.propertyNames_transformation">Transformation</i18n:message>';
ORYX.I18N.propertyNames['transformation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_transformation_desc">Transformation</i18n:message>';

ORYX.I18N.propertyNames['BPMNDiagram'] = '<i18n:message key="ORYX.I18N.propertyNames_BPMNDiagram">BPMN-Diagram</i18n:message>';
ORYX.I18N.propertyNames['BPMNDiagram_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_BPMNDiagram_desc">A BPMN 2.0 Diagram.</i18n:message>';
ORYX.I18N.propertyNames['processn'] = '<i18n:message key="ORYX.I18N.propertyNames_processn">Process Name</i18n:message>';
ORYX.I18N.propertyNames['processn_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_processn_desc">Process Name</i18n:message>';
ORYX.I18N.propertyNames['UserTask'] = '<i18n:message key="ORYX.I18N.propertyNames_UserTask">User</i18n:message>';
ORYX.I18N.propertyNames['UserTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_UserTask_desc">User Task.</i18n:message>';
ORYX.I18N.propertyNames['SendTask'] = '<i18n:message key="ORYX.I18N.propertyNames_SendTask">Send</i18n:message>';
ORYX.I18N.propertyNames['SendTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_SendTask_desc">Send Task.</i18n:message>';
ORYX.I18N.propertyNames['ReceiveTask'] = '<i18n:message key="ORYX.I18N.propertyNames_ReceiveTask">Receive</i18n:message>';
ORYX.I18N.propertyNames['ReceiveTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ReceiveTask_desc">Receive Task.</i18n:message>';
ORYX.I18N.propertyNames['ManualTask'] = '<i18n:message key="ORYX.I18N.propertyNames_ManualTask">Manual</i18n:message>';
ORYX.I18N.propertyNames['ManualTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ManualTask_desc">Manual Task.</i18n:message>';
ORYX.I18N.propertyNames['ServiceTask'] = '<i18n:message key="ORYX.I18N.propertyNames_ServiceTask">Service</i18n:message>';
ORYX.I18N.propertyNames['ServiceTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ServiceTask_desc">Service Task.</i18n:message>';
ORYX.I18N.propertyNames['Business RuleTask'] = '<i18n:message key="ORYX.I18N.propertyNames_BusinessRuleTask">Business Rule</i18n:message>';
ORYX.I18N.propertyNames['Business RuleTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_BusinessRuleTask_desc">Business Rule Task.</i18n:message>';
ORYX.I18N.propertyNames['ScriptTask'] = '<i18n:message key="ORYX.I18N.propertyNames_ScriptTask">Script</i18n:message>';
ORYX.I18N.propertyNames['ScriptTask_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ScriptTask_desc">Script Task.</i18n:message>';
ORYX.I18N.propertyNames['Task'] = '<i18n:message key="ORYX.I18N.propertyNames_Task">None</i18n:message>';
ORYX.I18N.propertyNames['Task_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Task_desc">A task is a unit of work - the job to be performed.</i18n:message>';
ORYX.I18N.propertyNames['nomorph'] = '<i18n:message key="ORYX.I18N.propertyNames_nomorph">nomorph</i18n:message>';
ORYX.I18N.propertyNames['nomorph_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_nomorph_desc">nomorph</i18n:message>';
ORYX.I18N.propertyNames['origbordercolor'] = '<i18n:message key="ORYX.I18N.propertyNames_origbordercolor">Original Border Color</i18n:message>';
ORYX.I18N.propertyNames['origbordercolor_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_origbordercolor_desc">Original Border Color</i18n:message>';
ORYX.I18N.propertyNames['ruleFlowGroup'] = '<i18n:message key="ORYX.I18N.propertyNames_ruleFlowGroup">Ruleflow Group</i18n:message>';
ORYX.I18N.propertyNames['ruleFlowGroup_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ruleFlowGroup_desc">Ruleflow Group</i18n:message>';
ORYX.I18N.propertyNames['serviceoperation'] = '<i18n:message key="ORYX.I18N.propertyNames_serviceoperation">Service Operation</i18n:message>';
ORYX.I18N.propertyNames['serviceoperation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_serviceoperation_desc">Service Operation</i18n:message>';
ORYX.I18N.propertyNames['serviceinterface'] = '<i18n:message key="ORYX.I18N.propertyNames_serviceinterface">Service Interface</i18n:message>';
ORYX.I18N.propertyNames['serviceinterface_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_serviceinterface_desc">Service Interface</i18n:message>';
ORYX.I18N.propertyNames['serviceimplementation'] = '<i18n:message key="ORYX.I18N.propertyNames_serviceimplementation">Service Implementation</i18n:message>';
ORYX.I18N.propertyNames['serviceimplementation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_serviceimplementation_desc">Specifies the service implementation type</i18n:message>';
ORYX.I18N.propertyNames['reassignment'] = '<i18n:message key="ORYX.I18N.propertyNames_reassignment">Reassignment</i18n:message>';
ORYX.I18N.propertyNames['reassignment_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_reassignment_desc">Task reassignments</i18n:message>';
ORYX.I18N.propertyNames['notifications'] = '<i18n:message key="ORYX.I18N.propertyNames_notifications">Notifications</i18n:message>';
ORYX.I18N.propertyNames['notifications_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_notifications_desc">Task notifications</i18n:message>';
ORYX.I18N.propertyNames['locale'] = '<i18n:message key="ORYX.I18N.propertyNames_locale">Locale</i18n:message>';
ORYX.I18N.propertyNames['locale_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_locale_desc">The task i18n locale</i18n:message>';
ORYX.I18N.propertyNames['skippable'] = '<i18n:message key="ORYX.I18N.propertyNames_skippable">Skippable</i18n:message>';
ORYX.I18N.propertyNames['skippable_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_skippable_desc">Specifies whether the human task can be skipped</i18n:message>';
ORYX.I18N.propertyNames['priority'] = '<i18n:message key="ORYX.I18N.propertyNames_priority">Priority</i18n:message>';
ORYX.I18N.propertyNames['priority_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_priority_desc">An integer indicating the priority of the human task</i18n:message>';
ORYX.I18N.propertyNames['ReusableSubprocess'] = '<i18n:message key="ORYX.I18N.propertyNames_ReusableSubprocess">Reusable</i18n:message>';
ORYX.I18N.propertyNames['ReusableSubprocess_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ReusableSubprocess_desc">A reusable subprocess. It can be used to invoke another process.</i18n:message>';
ORYX.I18N.propertyNames['activitytype'] = '<i18n:message key="ORYX.I18N.propertyNames_activitytype">ActivityType</i18n:message>';
ORYX.I18N.propertyNames['activitytype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_activitytype_desc">The Type of Activity.</i18n:message>';
ORYX.I18N.propertyNames['calledelement'] = '<i18n:message key="ORYX.I18N.propertyNames_calledelement">Called Element</i18n:message>';
ORYX.I18N.propertyNames['calledelement_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_calledelement_desc">Called Element</i18n:message>';
ORYX.I18N.propertyNames['independent'] = '<i18n:message key="ORYX.I18N.propertyNames_independent">Independent</i18n:message>';
ORYX.I18N.propertyNames['independent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_independent_desc">If set to true the child process is started as an independent process.</i18n:message>';
ORYX.I18N.propertyNames['MultipleInstanceSubprocess'] = '<i18n:message key="ORYX.I18N.propertyNames_MultipleInstanceSubprocess">Multiple instances</i18n:message>';
ORYX.I18N.propertyNames['MultipleInstanceSubprocess_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_MultipleInstanceSubprocess_desc">A multiple instances subprocess. Allows to execute the contained process segment multiple times.</i18n:message>';
ORYX.I18N.propertyNames['collectionexpression'] = '<i18n:message key="ORYX.I18N.propertyNames_collectionexpression">CollectionExpression</i18n:message>';
ORYX.I18N.propertyNames['collectionexpression_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_collectionexpression_desc">Name of a variable that represents the collection of elements that should be iterated over.</i18n:message>';
ORYX.I18N.propertyNames['variablename'] = '<i18n:message key="ORYX.I18N.propertyNames_variablename">Variable Name</i18n:message>';
ORYX.I18N.propertyNames['variablename_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_variablename_desc">Name of the variable to contain the current element from the collection.</i18n:message>';
ORYX.I18N.propertyNames['Subprocess'] = '<i18n:message key="ORYX.I18N.propertyNames_Subprocess">Embedded</i18n:message>';
ORYX.I18N.propertyNames['Subprocess_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Subprocess_desc">A subprocess is a decomposable activity. An expanded subprocess contains a valid BPMN diagram.</i18n:message>';
ORYX.I18N.propertyNames['AdHocSubprocess'] = '<i18n:message key="ORYX.I18N.propertyNames_AdHocSubprocess">Ad-Hoc</i18n:message>';
ORYX.I18N.propertyNames['AdHocSubprocess_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_AdHocSubprocess_desc">A subprocess is a decomposable activity. An expanded subprocess contains a valid BPMN diagram.</i18n:message>';
ORYX.I18N.propertyNames['origbgcolor'] = '<i18n:message key="ORYX.I18N.propertyNames_origbgcolor">Original Background Color</i18n:message>';
ORYX.I18N.propertyNames['origbgcolor_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_origbgcolor_desc">Original Background Color</i18n:message>';
ORYX.I18N.propertyNames['EventSubprocess'] = '<i18n:message key="ORYX.I18N.propertyNames_EventSubprocess">Event</i18n:message>';
ORYX.I18N.propertyNames['EventSubprocess_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EventSubprocess_desc">An Event-Subprocess is placed within another Subprocess. It becomes active when its start event gets triggered and can interrupt the Subprocess context or run in parallel (non-interrupting).</i18n:message>';
ORYX.I18N.propertyNames['Exclusive_Databased_Gateway'] = '<i18n:message key="ORYX.I18N.propertyNames_Exclusive_Databased_Gateway">Data-based Exclusive (XOR)</i18n:message>';
ORYX.I18N.propertyNames['Exclusive_Databased_Gateway_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Exclusive_Databased_Gateway_desc">When splitting, it routes the sequence flow to exactly one of the outgoing branches based on conditions. When merging, it awaits one incoming branch to complete before triggering the outgoing flow.</i18n:message>';
ORYX.I18N.propertyNames['gatewaytype'] = '<i18n:message key="ORYX.I18N.propertyNames_gatewaytype">Gateway type</i18n:message>';
ORYX.I18N.propertyNames['gatewaytype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_gatewaytype_desc">Gateway type</i18n:message>';
ORYX.I18N.propertyNames['xortype'] = '<i18n:message key="ORYX.I18N.propertyNames_xortype">XOR type</i18n:message>';
ORYX.I18N.propertyNames['xortype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_xortype_desc">XOR type</i18n:message>';
ORYX.I18N.propertyNames['markervisible'] = '<i18n:message key="ORYX.I18N.propertyNames_markervisible">X-Marker visible</i18n:message>';
ORYX.I18N.propertyNames['markervisible_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_markervisible_desc">X-Marker visible</i18n:message>';
ORYX.I18N.propertyNames['defaultgate'] = '<i18n:message key="ORYX.I18N.propertyNames_defaultgate">Default gate</i18n:message>';
ORYX.I18N.propertyNames['defaultgate_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_defaultgate_desc">The default gateway connection.</i18n:message>';
ORYX.I18N.propertyNames['EventbasedGateway'] = '<i18n:message key="ORYX.I18N.propertyNames_EventbasedGateway">Event-based</i18n:message>';
ORYX.I18N.propertyNames['EventbasedGateway_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EventbasedGateway_desc">Is always followed by catching events or receive tasks. Sequence flow is routed to the subsequent event/task which happens first.</i18n:message>';
ORYX.I18N.propertyNames['eventtype'] = '<i18n:message key="ORYX.I18N.propertyNames_eventtype">Type</i18n:message>';
ORYX.I18N.propertyNames['eventtype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_eventtype_desc">Type</i18n:message>';
ORYX.I18N.propertyNames['ParallelGateway'] = '<i18n:message key="ORYX.I18N.propertyNames_ParallelGateway">Parallel</i18n:message>';
ORYX.I18N.propertyNames['ParallelGateway_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ParallelGateway_desc">When used to split the sequence flow, all outgoing branches are activated simultaneously. When merging parallel branches it waits for all incoming branches to complete before triggering the outgoing flow.</i18n:message>';
ORYX.I18N.propertyNames['InclusiveGateway'] = '<i18n:message key="ORYX.I18N.propertyNames_InclusiveGateway">Inclusive</i18n:message>';
ORYX.I18N.propertyNames['InclusiveGateway_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_InclusiveGateway_desc">When splitting, one or more branches are activated based on branching conditions. When merging, it awaits all active incoming branches to complete.</i18n:message>';
ORYX.I18N.propertyNames['Lane'] = '<i18n:message key="ORYX.I18N.propertyNames_Lane">Lane</i18n:message>';
ORYX.I18N.propertyNames['Lane_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Lane_desc">Pools and Lanes represent responsibilities for activities in a process. A pool or a lane can be an organization, a role, or a system. Lanes sub-divide pools or other lanes hierarchically.</i18n:message>';
ORYX.I18N.propertyNames['showcaption'] = '<i18n:message key="ORYX.I18N.propertyNames_showcaption">ShowCaption</i18n:message>';
ORYX.I18N.propertyNames['showcaption_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_showcaption_desc">ShowCaption</i18n:message>';
ORYX.I18N.propertyNames['Group'] = '<i18n:message key="ORYX.I18N.propertyNames_Group">Group</i18n:message>';
ORYX.I18N.propertyNames['Group_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Group_desc">An arbitrary set of objects can be defined as a Group to show that they logically belong together.</i18n:message>';
ORYX.I18N.propertyNames['artifacttype'] = '<i18n:message key="ORYX.I18N.propertyNames_artifacttype">ArtifactType</i18n:message>';
ORYX.I18N.propertyNames['artifacttype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_artifacttype_desc">ArtifactType</i18n:message>';
ORYX.I18N.propertyNames['TextAnnotation'] = '<i18n:message key="ORYX.I18N.propertyNames_TextAnnotation">Text Annotation</i18n:message>';
ORYX.I18N.propertyNames['TextAnnotation_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_TextAnnotation_desc">Any object can be associated with a Text Annotation to provide additional documentation.</i18n:message>';
ORYX.I18N.propertyNames['DataObject'] = '<i18n:message key="ORYX.I18N.propertyNames_DataObject">Data Object</i18n:message>';
ORYX.I18N.propertyNames['DataObject_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_DataObject_desc">A Data Object represents information flowing through the process, such as business documents, e-mails or letters.</i18n:message>';
ORYX.I18N.propertyNames['input_output'] = '<i18n:message key="ORYX.I18N.propertyNames_input_output">Input/Output</i18n:message>';
ORYX.I18N.propertyNames['input_output_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_input_output_desc">Input/Output</i18n:message>';
ORYX.I18N.propertyNames['standardtype'] = '<i18n:message key="ORYX.I18N.propertyNames_standardtype">Standard Type</i18n:message>';
ORYX.I18N.propertyNames['standardtype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_standardtype_desc">Standard Type</i18n:message>';
ORYX.I18N.propertyNames['customtype'] = '<i18n:message key="ORYX.I18N.propertyNames_customtype">Custom Type</i18n:message>';
ORYX.I18N.propertyNames['customtype_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_customtype_desc">Custom Type</i18n:message>';
ORYX.I18N.propertyNames['StartNoneEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartNoneEvent">None</i18n:message>';
ORYX.I18N.propertyNames['StartNoneEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartNoneEvent_desc">Untyped start event.</i18n:message>';
ORYX.I18N.propertyNames['StartMessageEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartMessageEvent">Message</i18n:message>';
ORYX.I18N.propertyNames['StartMessageEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartMessageEvent_desc">A process instance is started on receive of a message.</i18n:message>';
ORYX.I18N.propertyNames['StartTimerEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartTimerEvent">Timer</i18n:message>';
ORYX.I18N.propertyNames['StartTimerEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartTimerEvent_desc">A process instance is started on cyclic timer events, points in time, after time spans or timeouts.</i18n:message>';
ORYX.I18N.propertyNames['timeduration'] = '<i18n:message key="ORYX.I18N.propertyNames_timeduration">Time Duration</i18n:message>';
ORYX.I18N.propertyNames['timeduration_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_timeduration_desc">Time Duration</i18n:message>';
ORYX.I18N.propertyNames['timecycle'] = '<i18n:message key="ORYX.I18N.propertyNames_timecycle">Time Cycle</i18n:message>';
ORYX.I18N.propertyNames['timecycle_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_timecycle_desc">Time Cycle</i18n:message>';
ORYX.I18N.propertyNames['timecyclelanguage'] = '<i18n:message key="ORYX.I18N.propertyNames_timecyclelanguage">Time Cycle Language</i18n:message>';
ORYX.I18N.propertyNames['timecyclelanguage_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_timecyclelanguage_desc">Time Cycle Language</i18n:message>';
ORYX.I18N.propertyNames['StartEscalationEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartEscalationEvent">Escalation</i18n:message>';
ORYX.I18N.propertyNames['StartEscalationEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartEscalationEvent_desc">Reacts on an escalation to another role in the organization. This event is only used inside of a event subprocess.</i18n:message>';
ORYX.I18N.propertyNames['escalationcode'] = '<i18n:message key="ORYX.I18N.propertyNames_escalationcode">EscalationCode</i18n:message>';
ORYX.I18N.propertyNames['escalationcode_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_escalationcode_desc">Escalation code</i18n:message>';
ORYX.I18N.propertyNames['StartConditionalEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartConditionalEvent">Conditional</i18n:message>';
ORYX.I18N.propertyNames['StartConditionalEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartConditionalEvent_desc">A process instance is started based on changed business conditions or matching business rules.</i18n:message>';
ORYX.I18N.propertyNames['conditionlanguage'] = '<i18n:message key="ORYX.I18N.propertyNames_conditionlanguage">Language</i18n:message>';
ORYX.I18N.propertyNames['conditionlanguage_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_conditionlanguage_desc">Defines the condition language.</i18n:message>';
ORYX.I18N.propertyNames['conditionexpression'] = '<i18n:message key="ORYX.I18N.propertyNames_conditionexpression">Expression</i18n:message>';
ORYX.I18N.propertyNames['conditionexpression_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_conditionexpression_desc">Expression</i18n:message>';
ORYX.I18N.propertyNames['StartErrorEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartErrorEvent">Error</i18n:message>';
ORYX.I18N.propertyNames['StartErrorEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartErrorEvent_desc">Catches named errors. This event is only used inside of a event subprocess.</i18n:message>';
ORYX.I18N.propertyNames['errorref'] = '<i18n:message key="ORYX.I18N.propertyNames_errorref">ErrorRef</i18n:message>';
ORYX.I18N.propertyNames['errorref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_errorref_desc">ErrorRef</i18n:message>';
ORYX.I18N.propertyNames['StartCompensationEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartCompensationEvent">Compensation</i18n:message>';
ORYX.I18N.propertyNames['StartCompensationEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartCompensationEvent_desc">Compensation handling. This event is only used inside of a event subprocess.</i18n:message>';
ORYX.I18N.propertyNames['StartSignalEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_StartSignalEvent">Signal</i18n:message>';
ORYX.I18N.propertyNames['StartSignalEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_StartSignalEvent_desc">A process instance is started based on signalling across different processes. (One signal thrown can be caught multiple times)</i18n:message>';
ORYX.I18N.propertyNames['signalref'] = '<i18n:message key="ORYX.I18N.propertyNames_signalref">SignalRef</i18n:message>';
ORYX.I18N.propertyNames['signalref_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_signalref_desc">SignalRef</i18n:message>';
ORYX.I18N.propertyNames['IntermediateMessageEventCatching'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateMessageEventCatching">Message</i18n:message>';
ORYX.I18N.propertyNames['IntermediateMessageEventCatching_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateMessageEventCatching_desc">This event reacts on the arrival of a message.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateTimerEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateTimerEvent">Timer</i18n:message>';
ORYX.I18N.propertyNames['IntermediateTimerEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateTimerEvent_desc">Process execution is delayed until a certain point in time is reached or a particular duration is over.</i18n:message>';
ORYX.I18N.propertyNames['waittime'] = '<i18n:message key="ORYX.I18N.propertyNames_waittime">Wait Time</i18n:message>';
ORYX.I18N.propertyNames['waittime_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_waittime_desc">Wait Time</i18n:message>';
ORYX.I18N.propertyNames['IntermediateEscalationEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateEscalationEvent">Escalation</i18n:message>';
ORYX.I18N.propertyNames['IntermediateEscalationEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateEscalationEvent_desc">This event reacts on the escalation of a case. It needs to be attached to the boundary of an activity.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateConditionalEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateConditionalEvent">Conditional</i18n:message>';
ORYX.I18N.propertyNames['IntermediateConditionalEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateConditionalEvent_desc">Process execution is delayed until a changed business condition or business rule matches.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateErrorEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateErrorEvent">Error</i18n:message>';
ORYX.I18N.propertyNames['IntermediateErrorEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateErrorEvent_desc">Catches a named error, which was thrown be an inner scope (e.g. subprocess). This event needs to be attached to the boundary of an activity.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateCompensationEventCatching'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateCompensationEventCatching">Compensation</i18n:message>';
ORYX.I18N.propertyNames['IntermediateCompensationEventCatching_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateCompensationEventCatching_desc">Compensation handling in case of partially failed operations. This event needs to be attached to the boundary of an activity.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateSignalEventCatching'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateSignalEventCatching">Signal</i18n:message>';
ORYX.I18N.propertyNames['IntermediateSignalEventCatching_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateSignalEventCatching_desc">Process execution is delayed until a particular signal is catched. Signalling can happen across different processes.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateMessageEventThrowing'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateMessageEventThrowing">Message</i18n:message>';
ORYX.I18N.propertyNames['IntermediateMessageEventThrowing_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateMessageEventThrowing_desc">The throwing message event sends a message to a communication partner and afterwards continues process execution.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateEscalationEventThrowing'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateEscalationEventThrowing">Escalation</i18n:message>';
ORYX.I18N.propertyNames['IntermediateEscalationEventThrowing_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateEscalationEventThrowing_desc">This event triggers the escalation of the case to another role in the organisation. After this, process execution is resumed.</i18n:message>';
ORYX.I18N.propertyNames['IntermediateSignalEventThrowing'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateSignalEventThrowing">Signal</i18n:message>';
ORYX.I18N.propertyNames['IntermediateSignalEventThrowing_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateSignalEventThrowing_desc">The throwing signal event fires up a signal. Afterwards it continues process execution. One signal thrown can be caught multiple times by different catching signal events.</i18n:message>';
ORYX.I18N.propertyNames['EndNoneEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndNoneEvent">None</i18n:message>';
ORYX.I18N.propertyNames['EndNoneEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndNoneEvent_desc">The untyped end event typically marks the standard end of a process.</i18n:message>';
ORYX.I18N.propertyNames['EndMessageEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndMessageEvent">Message</i18n:message>';
ORYX.I18N.propertyNames['EndMessageEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndMessageEvent_desc">At the end of the process, a message is sent.</i18n:message>';
ORYX.I18N.propertyNames['EndEscalationEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndEscalationEvent">Escalation</i18n:message>';
ORYX.I18N.propertyNames['EndEscalationEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndEscalationEvent_desc">The case is escalated with the end of the process.</i18n:message>';
ORYX.I18N.propertyNames['EndErrorEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndErrorEvent">Error</i18n:message>';
ORYX.I18N.propertyNames['EndErrorEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndErrorEvent_desc">The process ends in an error state. As result a named error is thrown.</i18n:message>';
ORYX.I18N.propertyNames['EndCancelEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndCancelEvent">Cancel></i18n:message>';
ORYX.I18N.propertyNames['EndCancelEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndCancelEvent_desc">Triggering cancellation of a transaction.</i18n:message>';
ORYX.I18N.propertyNames['EndCompensationEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndCompensationEvent">Compensation</i18n:message>';
ORYX.I18N.propertyNames['EndCompensationEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndCompensationEvent_desc">Triggering compensation as final process step.</i18n:message>';
ORYX.I18N.propertyNames['EndSignalEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndSignalEvent">Signal</i18n:message>';
ORYX.I18N.propertyNames['EndSignalEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndSignalEvent_desc">At the end of the process, a signal is thrown. (One signal thrown can be caught multiple times)</i18n:message>';
ORYX.I18N.propertyNames['EndTerminateEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_EndTerminateEvent">Terminate</i18n:message>';
ORYX.I18N.propertyNames['EndTerminateEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_EndTerminateEvent_desc">Triggering the immediate termination of a process instance. All steps still in execution in parallel branches are terminated.</i18n:message>';
ORYX.I18N.propertyNames['SequenceFlow'] = '<i18n:message key="ORYX.I18N.propertyNames_SequenceFlow">Sequence Flow</i18n:message>';
ORYX.I18N.propertyNames['SequenceFlow_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_SequenceFlow_desc">Sequence Flow defines the execution order of activities.</i18n:message>';
ORYX.I18N.propertyNames['ConditionType'] = '<i18n:message key="ORYX.I18N.propertyNames_ConditionType">ConditionType</i18n:message>';
ORYX.I18N.propertyNames['ConditionType_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_ConditionType_desc">Determine the type of the flow object.</i18n:message>';
ORYX.I18N.propertyNames['conditionexpressionlanguage'] = '<i18n:message key="ORYX.I18N.propertyNames_conditionexpressionlanguage">Condition Expression Language</i18n:message>';
ORYX.I18N.propertyNames['conditionexpressionlanguage_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_conditionexpressionlanguage_desc">Condition Expression Language</i18n:message>';
ORYX.I18N.propertyNames['isimmediate'] = '<i18n:message key="ORYX.I18N.propertyNames_isimmediate">isImmediate</i18n:message>';
ORYX.I18N.propertyNames['isimmediate_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_isimmediate_desc">An optional Boolean value specifying whether Activities or Choreography Activities not in the model containing the Sequence Flow can occur between the elements connected by the Sequence Flow. If the value is true, they MAY NOT occur. If the value is false, they MAY occur. Also see the isClosed attribute on Process, Choreography, and Collaboration.</i18n:message>';
ORYX.I18N.propertyNames['showdiamondmarker'] = '<i18n:message key="ORYX.I18N.propertyNames_showdiamondmarker">is conditional flow</i18n:message>';
ORYX.I18N.propertyNames['showdiamondmarker_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_showdiamondmarker_desc">System intern variable to set the Diamond invisible, if sourceShape is a gateway and ConditionType is set to Expression</i18n:message>';
ORYX.I18N.propertyNames['Association_Undirected'] = '<i18n:message key="ORYX.I18N.propertyNames_Association_Undirected">Association (undirected)</i18n:message>';
ORYX.I18N.propertyNames['Association_Undirected_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Association_Undirected_desc">Attaching a data object with an Undirected Association to a sequence flow indicates hand-over of information between the activities involved.</i18n:message>';
ORYX.I18N.propertyNames['type'] = '<i18n:message key="ORYX.I18N.propertyNames_type">Responsibilities</i18n:message>';
ORYX.I18N.propertyNames['type_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_type_desc">Describes the type of the responsibility according to RACI.</i18n:message>';
ORYX.I18N.propertyNames['Association_Unidirectional'] = '<i18n:message key="ORYX.I18N.propertyNames_Association_Unidirectional">Association (unidirectional)</i18n:message>';
ORYX.I18N.propertyNames['Association_Unidirectional_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Association_Unidirectional_desc">A Directed Association indicates information flow. A data object can be read at the start of an activity or written upon completion.</i18n:message>';
ORYX.I18N.propertyNames['testbefore'] = '<i18n:message key="ORYX.I18N.propertyNames_testbefore">TestBefore</i18n:message>';
ORYX.I18N.propertyNames['testbefore_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_testbefore_desc">Flag that controls whether the loop condition is evaluated at the beginning (testBefore = true) or at the end (testBefore = false)of the loop iteration.</i18n:message>';
ORYX.I18N.propertyNames['loopcondition'] = '<i18n:message key="ORYX.I18N.propertyNames_loopcondition">LoopCondition</i18n:message>';
ORYX.I18N.propertyNames['loopcondition_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_loopcondition_desc">LoopCondition</i18n:message>';
ORYX.I18N.propertyNames['Log'] = '<i18n:message key="ORYX.I18N.propertyNames_Log">Log</i18n:message>';
ORYX.I18N.propertyNames['Log_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Log_desc">Log</i18n:message>';

ORYX.I18N.propertyNames['Email'] = '<i18n:message key="ORYX.I18N.propertyNames_Email">Email</i18n:message>';
ORYX.I18N.propertyNames['Email_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_Email_desc">Email</i18n:message>';

ORYX.I18N.propertyNames['wp-simplemerge'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-simplemerge">Simple Merge</i18n:message>';
ORYX.I18N.propertyNames['wp-simplemerge_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-simplemerge_desc">Simple Merge Pattern</i18n:message>';

ORYX.I18N.propertyNames['wp-implicittermination'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-implicittermination">Implicit Termination</i18n:message>';
ORYX.I18N.propertyNames['wp-implicittermination_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-implicittermination_desc">Implicit Termination Pattern</i18n:message>';

ORYX.I18N.propertyNames['wp-deferredchoice'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-deferredchoice">Deferred Choice</i18n:message>';
ORYX.I18N.propertyNames['wp-deferredchoice_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-deferredchoice_desc">Deferred Choice Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-synchronization'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-synchronization">Synchronization</i18n:message>';
ORYX.I18N.propertyNames['wp-synchronization_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-synchronization_desc">Synchronization Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-miwithoutsynchronization'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-miwithoutsynchronization">MI Without Synchronization</i18n:message>';
ORYX.I18N.propertyNames['wp-miwithoutsynchronization_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-miwithoutsynchronization_desc">Multiple Instance Without Synchronization Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-synchronizingmerge'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-synchronizingmerge">Synchronizing Merge</i18n:message>';
ORYX.I18N.propertyNames['wp-synchronizingmerge_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-synchronizingmerge_desc">Synchronizing Merge Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-exclusivechoice'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-exclusivechoice">Exclusive Choice</i18n:message>';
ORYX.I18N.propertyNames['wp-exclusivechoice_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-exclusivechoice_desc">Exclusive Choice Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-parallelsplit'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-parallelsplit">Parallel Split</i18n:message>';
ORYX.I18N.propertyNames['wp-parallelsplit_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-parallelsplit_desc">Parallel Split Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-xorsplit'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-xorsplit">XOR Split</i18n:message>';
ORYX.I18N.propertyNames['wp-xorsplit_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-xorsplit_desc">XOR Split Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-sequence'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-sequence">Sequence</i18n:message>';
ORYX.I18N.propertyNames['wp-sequence_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-sequence_desc">Sequence Pattern</i18n:message>';
ORYX.I18N.propertyNames['wp-arbitrarycycles'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-arbitrarycycles">Arbitrary Cycles</i18n:message>';
ORYX.I18N.propertyNames['wp-arbitrarycycles_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_wp-arbitrarycycles_desc">Arbitrary Cycles Pattern</i18n:message>';
ORYX.I18N.propertyNames['IntermediateEvent'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateEvent">Intermediate Event</i18n:message>';
ORYX.I18N.propertyNames['IntermediateEvent_desc'] = '<i18n:message key="ORYX.I18N.propertyNames_IntermediateEvent_desc">This event marks the occurrence of a particular business event. Process execution is not delayed</i18n:message>';


if(!ORYX.I18N.propertyNamesValue) ORYX.I18N.propertyNamesValue = {}
ORYX.I18N.propertyNamesValue['normal'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_distype_normal">normal</i18n:message>';
ORYX.I18N.propertyNamesValue['uniform'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_distype_uniform">uniform</i18n:message>';
ORYX.I18N.propertyNamesValue['poisson'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_distype_poison">poisson</i18n:message>';

ORYX.I18N.propertyNamesValue['None'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_None">None</i18n:message>';
ORYX.I18N.propertyNamesValue['Send'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_Send">Send</i18n:message>';
ORYX.I18N.propertyNamesValue['Receive'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_Receive">Receive</i18n:message>';
ORYX.I18N.propertyNamesValue['User'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_User">User</i18n:message>';
ORYX.I18N.propertyNamesValue['Manual'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_Manual">Manual</i18n:message>';
ORYX.I18N.propertyNamesValue['Service'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_Service">Service</i18n:message>';
ORYX.I18N.propertyNamesValue['Business Rule'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_BusinessRule">Business Rule</i18n:message>';
ORYX.I18N.propertyNamesValue['Script'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_taskType_Script">Script</i18n:message>';

ORYX.I18N.propertyNamesValue['true']= '<i18n:message key="ORYX.I18N.propertyNamesValue_true">true</i18n:message>';
ORYX.I18N.propertyNamesValue['false'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_false">false</i18n:message>';
ORYX.I18N.propertyNamesValue['vardefs']= '<i18n:message key="ORYX.I18N.propertyNamesValue_vardefs">vardefs</i18n:message>';
ORYX.I18N.propertyNamesValue['globals'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_globals">globals</i18n:message>';
ORYX.I18N.propertyNamesValue['java']= '<i18n:message key="ORYX.I18N.propertyNamesValue_java">java</i18n:message>';
ORYX.I18N.propertyNamesValue['mvel'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_mvel">mvel</i18n:message>';

ORYX.I18N.propertyNamesValue['milliseconds'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_milliseconds">milliseconds</i18n:message>';
ORYX.I18N.propertyNamesValue['seconds'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_seconds">seconds</i18n:message>';
ORYX.I18N.propertyNamesValue['minutes'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_minutes">minutes</i18n:message>';
ORYX.I18N.propertyNamesValue['hours'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_hours">hours</i18n:message>';
ORYX.I18N.propertyNamesValue['days'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_days">days</i18n:message>';
ORYX.I18N.propertyNamesValue['years'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_years">years</i18n:message>';
ORYX.I18N.propertyNamesValue['Message'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Message">Message</i18n:message>';
ORYX.I18N.propertyNamesValue['Escalation'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Escalation">Escalation</i18n:message>';
ORYX.I18N.propertyNamesValue['Error'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Error">Error</i18n:message>';
ORYX.I18N.propertyNamesValue['Cancel'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Cancel">Cancel</i18n:message>';
ORYX.I18N.propertyNamesValue['Compensation'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Compensation">Compensation</i18n:message>';
ORYX.I18N.propertyNamesValue['Signal'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Signal">Signal</i18n:message>';
ORYX.I18N.propertyNamesValue['Terminate'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Terminate">Terminate</i18n:message>';
ORYX.I18N.propertyNamesValue['Sequential'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Sequential">Sequential</i18n:message>';
ORYX.I18N.propertyNamesValue['Parallel'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Parallel">Parallel</i18n:message>';
ORYX.I18N.propertyNamesValue['Executable'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Executable">Executable</i18n:message>';
ORYX.I18N.propertyNamesValue['Non-Executable'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Non-Executable">Non-Executable</i18n:message>';
ORYX.I18N.propertyNamesValue['Public'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Public">Public</i18n:message>';
ORYX.I18N.propertyNamesValue['Ready'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Ready">Ready</i18n:message>';
ORYX.I18N.propertyNamesValue['Active'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Active">Active</i18n:message>';
ORYX.I18N.propertyNamesValue['Cancelled'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Cancelled">Cancelled</i18n:message>';
ORYX.I18N.propertyNamesValue['Aborting'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Aborting">Aborting</i18n:message>';
ORYX.I18N.propertyNamesValue['Aborted'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Aborted">Aborted</i18n:message>';
ORYX.I18N.propertyNamesValue['Completing'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Completing">Completing</i18n:message>';
ORYX.I18N.propertyNamesValue['Completed'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Completed">Completed</i18n:message>';


ORYX.I18N.propertyNamesValue['Physical'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Physical">Physical</i18n:message>';
ORYX.I18N.propertyNamesValue['Information'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Information">Information</i18n:message>';
ORYX.I18N.propertyNamesValue['None'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_None">None</i18n:message>';
ORYX.I18N.propertyNamesValue['Standard'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Standard">Standard</i18n:message>';
ORYX.I18N.propertyNamesValue['MI Parallel'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_MI_Parallel">MI Parallel</i18n:message>';
ORYX.I18N.propertyNamesValue['MI Sequential'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_MI_Sequential">MI Sequential</i18n:message>';

ORYX.I18N.propertyNamesValue['Webservice'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Webservice">Webservice</i18n:message>';
ORYX.I18N.propertyNamesValue['Exclusive (standard)'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Exclusive_standard">Exclusive (standard)</i18n:message>';
ORYX.I18N.propertyNamesValue['Exclusive Instantiation'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Exclusive_Instantiation">Exclusive Instantiation</i18n:message>';
ORYX.I18N.propertyNamesValue['Parallel Instantiation'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Parallel_Instantiation">Parallel Instantiation</i18n:message>';
ORYX.I18N.propertyNamesValue['Input'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Input">Input</i18n:message>';
ORYX.I18N.propertyNamesValue['Output'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Output">Output</i18n:message>';
ORYX.I18N.propertyNamesValue['Object'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Object">Object</i18n:message>';
ORYX.I18N.propertyNamesValue['Boolean'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Boolean">Boolean</i18n:message>';
ORYX.I18N.propertyNamesValue['Float'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Float">Float</i18n:message>';
ORYX.I18N.propertyNamesValue['Integer'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Integer">Integer</i18n:message>';
ORYX.I18N.propertyNamesValue['List'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_List">List</i18n:message>';
ORYX.I18N.propertyNamesValue['String'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_String">String</i18n:message>';
ORYX.I18N.propertyNamesValue['drools'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_drools">drools</i18n:message>';
ORYX.I18N.propertyNamesValue['Cron'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Cron">Cron</i18n:message>';
ORYX.I18N.propertyNamesValue['Conditional Flow'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_ConditionalFlow">Conditional Flow</i18n:message>';
ORYX.I18N.propertyNamesValue['Default Flow'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_DefaultFlow">Default Flow</i18n:message>';
ORYX.I18N.propertyNamesValue['responsible'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_responsible">responsible</i18n:message>';
ORYX.I18N.propertyNamesValue['accountable'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_accountable">accountable</i18n:message>';
ORYX.I18N.propertyNamesValue['consulted'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_consulted">consulted</i18n:message>';
ORYX.I18N.propertyNamesValue['informed'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_informed">informed</i18n:message>';
ORYX.I18N.propertyNamesValue['One'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_One">One</i18n:message>';
ORYX.I18N.propertyNamesValue['All'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_All">All</i18n:message>';
ORYX.I18N.propertyNamesValue['Complex'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Complex">Complex</i18n:message>';
ORYX.I18N.propertyNamesValue['Performer'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Performer">Performer</i18n:message>';
ORYX.I18N.propertyNamesValue['HumanPerformer'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_HumanPerformer">HumanPerformer</i18n:message>';
ORYX.I18N.propertyNamesValue['PotentialOwner'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_PotentialOwner">PotentialOwner</i18n:message>';
ORYX.I18N.propertyNamesValue['HumanTaskWebService'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_HumanTaskWebService">HumanTaskWebService</i18n:message>';
ORYX.I18N.propertyNamesValue['BuisnessRuleWebService'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_BuisnessRuleWebService">BuisnessRuleWebService</i18n:message>';
ORYX.I18N.propertyNamesValue['WebService'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_WebService">WebService</i18n:message>';
ORYX.I18N.propertyNamesValue['Other'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Other">Other</i18n:message>';
ORYX.I18N.propertyNamesValue['Unspecified'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Unspecified">Unspecified</i18n:message>';
ORYX.I18N.propertyNamesValue['Embedded'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Embedded">Embedded</i18n:message>';
ORYX.I18N.propertyNamesValue['Independent'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Independent">Independent</i18n:message>';
ORYX.I18N.propertyNamesValue['Reusable'] = '<i18n:message key="ORYX.I18N.propertyNamesValue_Reusable">Reusable</i18n:message>';


ORYX.I18N.propertyNames['Tasks'] = '<i18n:message key="ORYX.I18N.propertyNames_Tasks">Tasks</i18n:message>';
ORYX.I18N.propertyNames['Subprocesses'] = '<i18n:message key="ORYX.I18N.propertyNames_Subprocesses">Subprocesses</i18n:message>';
ORYX.I18N.propertyNames['Start Events'] = '<i18n:message key="ORYX.I18N.propertyNames_StartEvents">Start Events</i18n:message>';
ORYX.I18N.propertyNames['End Events'] = '<i18n:message key="ORYX.I18N.propertyNames_EndEvents">End Events</i18n:message>';
ORYX.I18N.propertyNames['Catching Intermediate Events'] = '<i18n:message key="ORYX.I18N.propertyNames_CatchingIntermediateEvents">Catching Intermediate Events</i18n:message>';
ORYX.I18N.propertyNames['Throwing Intermediate Events'] = '<i18n:message key="ORYX.I18N.propertyNames_ThrowingIntermediateEvents">Throwing Intermediate Events</i18n:message>';
ORYX.I18N.propertyNames['Gateways'] = '<i18n:message key="ORYX.I18N.propertyNames_Gateways">Gateways</i18n:message>';
ORYX.I18N.propertyNames['Service Tasks'] = '<i18n:message key="ORYX.I18N.propertyNames_ServiceTasks">Service Tasks</i18n:message>';
ORYX.I18N.propertyNames['Connecting Objects'] = '<i18n:message key="ORYX.I18N.propertyNames_ConnectingObjects">Connecting Objects</i18n:message>';
ORYX.I18N.propertyNames['Data Objects'] = '<i18n:message key="ORYX.I18N.propertyNames_DataObjects">Data Objects</i18n:message>';
ORYX.I18N.propertyNames['Swimlanes'] = '<i18n:message key="ORYX.I18N.propertyNames_Swimlanes">Swimlanes</i18n:message>';
ORYX.I18N.propertyNames['Artifacts'] = '<i18n:message key="ORYX.I18N.propertyNames_Artifacts">Artifacts</i18n:message>';
ORYX.I18N.propertyNames['Workflow Patterns'] = '<i18n:message key="ORYX.I18N.propertyNames_WorkflowPatterns">Workflow Patterns</i18n:message>';

ORYX.I18N.propertyNames['Full'] = '<i18n:message key="ORYX.I18N.propertyNames_Full">Full</i18n:message>';
ORYX.I18N.propertyNames['Simple'] = '<i18n:message key="ORYX.I18N.propertyNames_Simple">Simple</i18n:message>';
ORYX.I18N.propertyNames['RuleFlow'] = '<i18n:message key="ORYX.I18N.propertyNames_RuleFlow">RuleFlow</i18n:message>';

ORYX.I18N.SortAscending = '<i18n:message key="ORYX.I18N.SortAscending">Sort Ascending</i18n:message>';
ORYX.I18N.SortDescending = '<i18n:message key="ORYX.I18N.SortDescending">Sort Descending</i18n:message>';
ORYX.I18N.Columns = '<i18n:message key="ORYX.I18N.Columns">Columns</i18n:message>';

</script>
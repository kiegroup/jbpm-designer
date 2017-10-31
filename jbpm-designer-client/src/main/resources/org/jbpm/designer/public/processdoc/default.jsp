<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Process Documentation</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/patternfly.min.css" rel="stylesheet" media="screen, print">
    <link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/styles.css" rel="stylesheet" media="screen, print">

    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-2.2.0.min.js" charset="utf-8"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/html2canvas.js" charset="utf-8"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jspdf.min.js" charset="utf-8"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/bootstrap.min.js" charset="utf-8"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/patternfly.min.js" charset="utf-8"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/handlebars-v4.0.5.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/swag.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/jsonpath.js"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/documentation/docstructure.js"></script>
    <script>
        var ctx = "<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/";
        Swag.registerHelpers(Handlebars);
    </script>
    <style>
        .printonly {display: none;}
        @media print {
            .noprint{
                display: none !important;
            }
            .printonly {
                display: block;
            }
        }
        .pidiv {
            width: 520px;
            height: 350px;
            border: thin solid black;
            overflow: scroll;
        }

        .pprintdiv {
            width: 1000px;
            height: 1000px;
            overflow: scroll;
        }

        .label-pill {
            background-color:white;
            color:black;
        }
    </style>
</head>
<body class="cards-pf">
<div class="container" id="pagecontainer">
    <div class="row">
        <div class="col-sm-9">
            <p><div id="pagebuttons" class="well noprint" align="right">
                <button type="button" id="docspngbutton" class="btn btn-default btn-sm" onclick="createDocsPNG()">Doc PNG</button>&nbsp;&nbsp;
                <button type="button" id="docspdfbutton" class="btn btn-default btn-sm" onclick="showAsPDF()">PDF</button>&nbsp;&nbsp;
                <button type="button" id="docsprintbutton" class="btn btn-default btn-sm" onclick="window.print();">Print</button>
            </div></p>

            <div id="pagecontainercore">
                <p><h1 class="page-header" id="process-documentation">Process Documentation</h1></p>

                <h2 id="overview"><span class="badge badge-inverse">1.0</span> Process Overview</h2>
                <p> <h3 id="process-info"><span class="badge badge-inverse">1.1</span> General</h3></p>
                <p id='processinfocontent'></p>
                <p><h3 id="process-titals"><span class="badge badge-inverse">1.2</span> Data Totals</h3></p>
                <p id="processdatatotals"></p>
                <p><h3 id="process-vars"><span class="badge badge-inverse">1.3</span> Variables</h3></p>
                <p id='processvarcontent'></p>
                <p><h3 id="process-globals"><span class="badge badge-inverse">1.4</span> Globals</h3></p>
                <p id='processglobalcontent'></p>
                <p><h3 id="process-imports"><span class="badge badge-inverse">1.5</span> Imports</h3></p>
                <p id='processimportcontent'></p>

                <h2 id="element-details"><span class="badge badge-inverse">2.0</span> Element Details</h2>
                <p><h3 id="element-totals"><span class="badge badge-inverse">2.1</span> Totals</h3></p>
                <p id="processelementtotals"></p>
                <p><h3 id="elemen-info"><span class="badge badge-inverse">2.2</span> Elements</h3></p>
                <p id="processelementdetails"></p>
                </div>

            <div class="noprint" id="processimgdiv">
                <h2 id="process-image"><span class="badge badge-inverse">3.0</span> Process Image</h2>
                <div id="processimgdivdisplay" style="width:100%;position:relative;overflow:auto;"></div>
                <iframe id="processimgdivdisplayframe" width="100%" height="600"></iframe>
            </div>

        </div>
    </div>
    <div class="row printonly">
        <div class="col-sm-9 pprintdiv" id="processimageprintdisplay"></div>
    </div>
    <div id="processmodelimgdiv" style="width:100%;"></div>
</div>

<script id="elementdetailstemplate" type="text/x-handlebars-template">
    <div class="list-group">
        {{#each this}}
        {{#if this.length}}
        <span class="list-group-item">
            <h3 id="{{this.0.group}}" class="list-group-item-heading">{{this.0.groupdispname}}</h3>
            <p class="list-group-item-text">
                {{#each this}}
                {{#if this.showindocumentation}}
                    <div id="{{id}}" class="panel panel-default">
                        <div class="panel-heading"><img src="{{icon}}"> <b>Name:</b> {{nodename}} <b>Type:</b> {{dispname}}  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-secondary btn-sm" onclick="showElementInModel( '{{id}}' );return false;">View in Model</button></div>
                        <table class="table table-inverse">
                            <thead>
                            <tr>
                                <th>Property Name</th>
                                <th>Property Value</th>
                            </tr>
                            </thead>
                            <tbody>
                            {{#properties}}
                            <tr>
                                <td>{{name}}</td>
                                <td>{{{newLineToBr value}}}</td>
                            </tr>
                            {{/properties}}
                            </tbody>
                        </table>
                    </div>
                {{/if}}
                {{/each}}
            </p>
        </span>
        {{/if}}
        {{/each}}
    </div>
</script>

<script id="elementstotalstemplate" type="text/x-handlebars-template">
    <ul class="list-group">
        {{#each this}}
        {{#if this.length}}
        <li class="list-group-item">
            <img src="{{this.0.groupicon}}" alt="{{this.0.groupdispname}}"> {{this.0.groupdispname}}
            <span class="label label-default label-pill pull-xs-right">{{this.length}}</span>
        </li>
        {{/if}}
        {{/each}}
    </ul>
</script>

<script id="processtotalstemplate" type="text/x-handlebars-template">
    <ul class="list-group">
        {{#processdatatotals}}
        <li class="list-group-item">
            {{name}}
            <span class="label label-default label-pill pull-xs-right">{{count}}</span>
        </li>
        {{/processdatatotals}}
    </ul>
</script>

<script id="processinfotemplate" type="text/x-handlebars-template">
<table class="table table-inverse">
    <tbody>
    {{#processinfos}}
    <tr>
        <td><b>{{name}}</b></td>
        <td>{{value}}</td>
    </tr>
    {{/processinfos}}
    </tbody>
</table>
</script>

<script id="processvarsglobalstemplate" type="text/x-handlebars-template">
    <table class="table table-inverse">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Type</th>
            <th>KPI</th>
        </tr>
        </thead>
        <tbody>
        {{#processvarsglobals}}
        <tr>
            <th scope="row">{{count}}</th>
            <td>{{name}}</td>
            <td>{{type}}</td>
            <td>{{kpi}}</td>
        </tr>
        {{/processvarsglobals}}
        </tbody>
    </table>
</script>

<script id="processimportstemplate" type="text/x-handlebars-template">
    <table class="table table-inverse">
        <thead>
        <tr>
            <th>#</th>
            <th>Type</th>
            <th>Class Name</th>
            <th>WSDL Location</th>
            <th>WSDL Namespace</th>
        </tr>
        </thead>
        <tbody>
        {{#processimports}}
        <tr>
            <th scope="row">{{count}}</th>
            <td>{{type}}</td>
            <td>{{classname}}</td>
            <td>{{wsdllocation}}</td>
            <td>{{wsdlnamespace}}</td>
        </tr>
        {{/processimports}}
        </tbody>
    </table>
</script>


<script>
    function showProcessDocs() {
        // show or hide pdf doc generation button
        if(parent.ORYX.SHOWPDFDOC && parent.ORYX.SHOWPDFDOC == true) {
            $("#docspdfbutton").show();
        } else {
            $("#docspdfbutton").hide();
        }

        var processJSON = parent.ORYX.EDITOR.getSerializedJSON();
        var processDataTotals = {
            "processdatatotals":[
            ]
        };
        showProcessGeneral(processJSON);
        processDataTotals['processdatatotals'].push({"name":"Variables","count":showProcessVars(processJSON)});
        processDataTotals['processdatatotals'].push({"name":"Globals","count":showProcessGlobals(processJSON)});
        processDataTotals['processdatatotals'].push({"name":"Imports","count":showProcessImports(processJSON)});
        showProcessTotals(processDataTotals, processJSON);
        showProcessElementsInfo(processJSON);
        displayProcessImg();
        scrollToElement();
    }

    function showProcessElementsInfo(processJSON) {
        var elementsInfo = processElementInfo(processJSON);
        showElementsTotals(elementsInfo, processJSON);
        showElementsDetails(elementsInfo, processJSON);

    }

    function showElementsDetails(elementsInfo, processJSON) {
        var elementsDetailsSource = $("#elementdetailstemplate").html();
        var elementsDetailsTemplate = Handlebars.compile(elementsDetailsSource);
        $("#processelementdetails").html(elementsDetailsTemplate(elementsInfo));
    }

    function showElementsTotals(elementsInfo, processJSON) {
        var elementsTotalsSource = $("#elementstotalstemplate").html();
        var elementsTotalsTemplate = Handlebars.compile(elementsTotalsSource);
        $("#processelementtotals").html(elementsTotalsTemplate(elementsInfo));
    }

    function showProcessTotals(dataTotals, processJSON) {
        var processTotalsSource = $("#processtotalstemplate").html();
        var processTotalsTemplate = Handlebars.compile(processTotalsSource);
        $("#processdatatotals").html(processTotalsTemplate(dataTotals));
    }

    function showProcessGeneral(processJSON) {
        var processName = jsonPath(JSON.parse(processJSON), "$.properties.processn");
        var processID = jsonPath(JSON.parse(processJSON), "$.properties.id");
        var processPackage = jsonPath(JSON.parse(processJSON), "$.properties.package");
        var processExecutable = jsonPath(JSON.parse(processJSON), "$.properties.executable");
        var processAdHoc = jsonPath(JSON.parse(processJSON), "$.properties.adhocprocess");
        var processVersion = jsonPath(JSON.parse(processJSON), "$.properties.version");
        var processDocumentation = jsonPath(JSON.parse(processJSON), "$.properties.documentation");

        var processInfoSource = $("#processinfotemplate").html();
        var processInfoTemplate = Handlebars.compile(processInfoSource);

        var processInfoData = {
            "processinfos":[
                {
                    "name":"ID","value":processID,"count":"1"
                },
                {
                    "name":"Package","value":processPackage,"count":"2"
                },
                {
                    "name":"Name", "value":processName,"count":"3"
                },
                {
                    "name":"Is executable","value":processExecutable,"count":"4"
                },
                {
                    "name":"Is AdHoc","value":processAdHoc,"count":"5"
                },
                {
                    "name":"Version","value":processVersion,"count":"6"
                },
                {
                    "name":"Documentation","value":processDocumentation,"count":"6"
                }
            ]
        };

        $("#processinfocontent").html(processInfoTemplate(processInfoData));

    }

    function showProcessVars(processJSON) {
        var processVars = jsonPath(JSON.parse(processJSON), "$.properties.vardefs");
        var pcount = 1;

        if(processVars) {
            var processVarSource = $("#processvarsglobalstemplate").html();
            var processVarTemplate = Handlebars.compile(processVarSource);
            var processVarData = {
                "processvarsglobals":[
                ]
            };

            processVars.forEach(function(item) {
                if(item.length > 0) {
                    var valueParts = item.split(",");
                    for(var i=0; i < valueParts.length; i++) {
                        var nextPart = valueParts[i];
                        if(nextPart.indexOf(":") > 0) {
                            var innerParts = nextPart.split(":");
                            if(innerParts.length == 2) {
                                if(innerParts[1] != "false" && innerParts[1] != "true") {
                                    processVarData['processvarsglobals'].push({"name":innerParts[0],"type":innerParts[1],"kpi":"false","count":pcount});
                                } else {
                                    processVarData['processvarsglobals'].push({"name":innerParts[0],"type":"no defined type","kpi":innerParts[1],"count":pcount});
                                }
                            } else if(innerParts.length == 3) {
                                processVarData['processvarsglobals'].push({"name":innerParts[0],"type":innerParts[1],"kpi":innerParts[2],"count":pcount});
                            }
                        } else {
                            processVarData['processvarsglobals'].push({"name":nextPart,"type":"no defined type","kpi":"false","count":pcount});
                        }
                        pcount++;
                    }
                }
            });

            $("#processvarcontent").html(processVarTemplate(processVarData));
        } else {
            $("#processvarcontent").html("No Process Variables present");
        }

        return pcount-1;
    }

    function showProcessGlobals(processJSON) {
        var processGlobals = jsonPath(JSON.parse(processJSON), "$.properties.globals");
        var pcount = 1;

        if(processGlobals) {
            var processGlobalSource = $("#processvarsglobalstemplate").html();
            var processGlobalTemplate = Handlebars.compile(processGlobalSource);
            var processGlobalData = {
                "processvarsglobals":[
                ]
            };

            processGlobals.forEach(function(item) {
                if(item.length > 0) {
                    var valueParts = item.split(",");
                    for(var i=0; i < valueParts.length; i++) {
                        var nextPart = valueParts[i];
                        if(nextPart.indexOf(":") > 0) {
                            var innerParts = nextPart.split(":");
                            processGlobalData['processvarsglobals'].push({"name":innerParts[0],"type":innerParts[1],"kpi":"false","count":pcount});
                        } else {
                            processGlobalData['processvarsglobals'].push({"name":nextPart,"type":"java.lang.String","kpi":"false","count":pcount});
                        }
                        pcount++;
                    }
                }
            });

            $("#processglobalcontent").html(processGlobalTemplate(processGlobalData));
        } else {
            $("#processglobalcontent").html("No Process Globals present");;
        }
        return pcount-1;
    }

    function showProcessImports(processJSON) {
        var processImports = jsonPath(JSON.parse(processJSON), "$.properties.imports");
        var pcount = 1;

        if(processImports) {
            var processImportSource = $("#processimportstemplate").html();
            var processImportTemplate = Handlebars.compile(processImportSource);
            var processImportData = {
                "processimports":[
                ]
            };

            processImports.forEach(function(item) {
                if(item.length > 0) {
                    var valueParts = item.split(",");
                    for(var i=0; i < valueParts.length; i++) {
                        var nextPart = valueParts[i];
                        if(nextPart.indexOf("|") > 0) {
                            var innerParts = nextPart.split("|");
                            if(innerParts[1] == "default") {
                                processImportData['processimports'].push({"type":"default","classname":innerParts[0],"wsdllocation":"","wsdlnamespace":"","count":pcount});
                            } else {
                                processImportData['processimports'].push({"type":"wsdl","classname":"","wsdllocation":innerParts[0],"wsdlnamespace":innerParts[1],"count":pcount});
                            }
                        }
                        pcount++;
                    }
                }
            });

            $("#processimportcontent").html(processImportTemplate(processImportData));
        } else {
            $("#processimportcontent").html("No Process Imports present");;
        }
        return pcount-1;
    }

    function createDocsPNG() {
        $("#pagenav").hide();
        $("#pagebuttons").hide();
        $("#processimgdiv").hide();
        html2canvas($("#pagecontainer"), {
            onrendered: function(canvas) {
                var docImage = canvas.toDataURL("image/png");
                window.open(docImage);
                $("#pagenav").show();
                $("#pagebuttons").show();
                $("#processimgdiv").show();
            }
        });
    }

</script>
</body>
</html>

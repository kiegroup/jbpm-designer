<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/nv-core.css" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/simulationcharts.css" rel="stylesheet" type="text/css">
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/d3.v2.min.js"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/nv.min.js"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/chartutils-min.js"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/handlebars-1.0.0.beta.6.js" type="text/javascript"></script>
    <script>
        function clearChart() {
            var cnt = "<h2>" + parent.ORYX.I18N.View.sim.chartsExecutionTimes + "</h2> \
  	<p id='chartcontent1'> \
  	<svg id='chart1' style='height:400px;width:400px'></svg> \
	</p> \
	<h2>" + parent.ORYX.I18N.View.sim.chartsActivityInstances + "</h2> \
  	<p id='chartcontent2'> \
  	<svg id='chart2' style='height:400px;width:400px'></svg> \
	</p>  \
	<h2>" + parent.ORYX.I18N.View.sim.chartsTotalCost + "</h2> \
    <p id='chartcontent3'> \
    <svg id='chart3' style='height:400px;width:400px'></svg> \
    </p> \
    <h2>" + parent.ORYX.I18N.View.sim.chartsTotalResourceUtilization + "</h2> \
    <p id='chartcontent4'> \
    <svg id='chart4' style='height:400px;width:400px'></svg> \
    </p>";

            document.getElementById('outterchart').innerHTML = cnt;
        }

        function clearChartForLine() {
            var cnt = "<h2>" + parent.ORYX.I18N.View.sim.chartsProcessExecutionTimesDuringSimulation + "</h2> \
	  	<p id='chartcontent1'> \
	  	<svg id='chart1' style='height:400px;width:400px'></svg> \
		</p>";

            document.getElementById('outterchart').innerHTML = cnt;
        }

    </script>
</head>
<body onload="showBarChart();">
<center>
    <div style="margin:20;padding:0;">
        <div class="lcharticon"><a href="#" onclick="clearChartForLine(); showLineChart(); return false;"><img id="linecharticonimg" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/linecharticon.png" title="Line Chart"/></a></div>
        <script>document.getElementById('linecharticonimg').title = parent.ORYX.I18N.View.sim.LineChart;</script>
        <div class="timelineicon"><a href="#" onclick="clearChart(); showTimeline(); return false;"><img id="timelineiconimg" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/timelineicon.png" title="Timeline"/></a></div>
        <script>document.getElementById('timelineiconimg').title = parent.ORYX.I18N.View.sim.Timeline;</script>
        <div class="tableicon"><a href="#" onclick="clearChart(); showTable(); return false;"><img id="tableiconimg" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/tableicon.png" title="Table"/></a></div>
        <script>document.getElementById('tableiconimg').title = parent.ORYX.I18N.View.sim.Table;</script>
        <div class="pcharticon"><a href="#" onclick="clearChart(); showPieChart(); return false;"><img id="piecharticonimg" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/piecharticon.png" title="Pie Chart"/></a></div>
        <script>document.getElementById('piecharticonimg').title = parent.ORYX.I18N.View.sim.PieChart;</script>
        <div class="hbcharticon"><a href="#" onclick="clearChart(); showHBarChart(); return false;"><img id="hbarcharticonimg" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/hbarcharticon.png" title="Horizontal Bar Chart"/></a></div>
        <script>document.getElementById('hbarcharticonimg').title = parent.ORYX.I18N.View.sim.HorizontalBarChart;</script>
        <div class="bcharticon"><a href="#" onclick="clearChart(); showBarChart(); return false;"><img id="barcharticonimg" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/barcharticon.png" title="Bar Chart"/></a></div>
        <script>document.getElementById('barcharticonimg').title = parent.ORYX.I18N.View.sim.BarChart;</script>
        <div class="charttitle"><script>document.write(parent.ORYX.EDITOR.simulationChartTitle +  " (" + parent.ORYX.EDITOR.simulationChartNodeName + ")");</script></div>
    </div><br/>
    <div id="outterchart" class="outterchart">
        <h2 id="chart1label"><script>document.write(parent.ORYX.I18N.View.sim.chartsExecutionTimes);</script></h2>
        <p id="chartcontent1">
            <svg id="chart1" style='height:400px;width:400px'></svg>
        </p>
        <h2 id="chart2label"><script>document.write(parent.ORYX.I18N.View.sim.chartsActivityInstances);</script></h2>
        <p id="chartcontent2">
            <svg id="chart2" style='height:400px;width:400px'></svg>
        </p>
        <h2 id="chart3label"><script>document.write(parent.ORYX.I18N.View.sim.chartsTotalCost);</script></h2>
        <p id="chartcontent3">
            <svg id="chart3" style='height:400px;width:400px'></svg>
        </p>
        <h2 id="chart4label"><script>document.write(parent.ORYX.I18N.View.sim.chartsTotalResourceUtilization);</script></h2>
        <p id="chartcontent4">
            <svg id="chart4" style='height:400px;width:400px'></svg>
        </p>
    </div>
</center>
<script id="tabletemplate" type="text/x-handlebars-template">
    <center>
        <table id="box-table" summary="Process Execution">
            <thead>
            <tr>
                {{#values}}
                <th scope="col" align="center">{{label}} ({{timeunit }})</th>
                {{/values}}
            </tr>
            </thead>
            <tbody>
            <tr>
                {{#values}}
                <td align="center">{{value}}</td>
                {{/values}}
            </tr>
            </tbody>
        </table>
    </center>
</script>
<script id="tableinstancestemplate" type="text/x-handlebars-template">
    <center>
        <table id="box-table" summary="Activity Instances">
            <thead>
            <tr>
                {{#values}}
                <th scope="col" align="center">{{label}} (#)</th>
                {{/values}}
            </tr>
            </thead>
            <tbody>
            <tr>
                {{#values}}
                <td align="center">{{value}}</td>
                {{/values}}
            </tr>
            </tbody>
        </table>
    </center>
</script>
<script id="tablecosttemplate" type="text/x-handlebars-template">
    <center>
        <table id="box-table" summary="Total Cost">
            <thead>
            <tr>
                {{#values}}
                <th scope="col" align="center">{{label}}</th>
                {{/values}}
            </tr>
            </thead>
            <tbody>
            <tr>
                {{#values}}
                <td align="center">{{value}}</td>
                {{/values}}
            </tr>
            </tbody>
        </table>
    </center>
</script>
<script id="tableresourcestemplate" type="text/x-handlebars-template">
    <center>
        <table id="box-table" summary="Total Resource Utilization">
            <thead>
            <tr>
                {{#values}}
                <th scope="col" align="center">{{label}} (%)</th>
                {{/values}}
            </tr>
            </thead>
            <tbody>
            <tr>
                {{#values}}
                <td align="center">{{value}}</td>
                {{/values}}
            </tr>
            </tbody>
        </table>
    </center>
</script>
<script>
function getEventData() {
    return parent.ORYX.EDITOR.simulationEventData;
}
function getEventAggregationData() {
    return parent.ORYX.EDITOR.simulationEventAggregationData;
}
function showBarChart() {
    var chartData = parent.ORYX.EDITOR.simulationChartData;
    if(chartData && chartData.length > 0) {
        simChartSetProcessAveragesLabels(chartData[0], parent.ORYX.I18N);
    }
    var instanceData = parent.ORYX.EDITOR.simulationInstancesData;
    if(instanceData && instanceData.length > 0) {
        var iData = instanceData[0];
        iData.key = parent.ORYX.I18N.View.sim.chartsActivityInstances;
    }
    nv.addGraph(function() {
        var chart = nv.models.discreteBarChart().x(function(d) {
            return d.label
        }).y(function(d) {
                    return d.value
                }).staggerLabels(true)
                .tooltips(true).showValues(true);
        chart.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsExecutionTimesTime + ' (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')');

        d3.select('#chart1').datum(chartData).transition().duration(500)
                .call(chart);

        nv.utils.windowResize(chart.update);

        return chart;
    });

    nv.addGraph(function() {
        var chart2 = nv.models.discreteBarChart().x(function(d) {
            return d.label
        }).y(function(d) {
                    return d.value
                }).staggerLabels(true)
                .tooltips(true).showValues(true);
        chart2.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsActivityInstancesInstances + ' (#)');

        d3.select('#chart2').datum(instanceData).transition().duration(500)
                .call(chart2);

        nv.utils.windowResize(chart2.update);
        updateChartIfNoData("chart2");
        return chart2;
    });

    var htCostData = parent.ORYX.EDITOR.simulationHTCostData;
    if(htCostData && htCostData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        for(var i=0; i < htCostData.length; i++ ) {
            var nextCostObj = htCostData[i];
            max += nextCostObj.values[0].value;
            min += nextCostObj.values[1].value;
            avg += nextCostObj.values[2].value;
        }

        var costData = [
            {
                "values":[
                    {
                        "value":max,
                        "label":parent.ORYX.I18N.View.sim.chartsMax
                    },
                    {
                        "value":min,
                        "label":parent.ORYX.I18N.View.sim.chartsMin
                    },
                    {
                        "value":avg,
                        "label":parent.ORYX.I18N.View.sim.chartsAverage
                    }
                ],
                "color":"#1f77b4",
                "key":parent.ORYX.I18N.View.sim.chartsTotalCost
            }
        ];

        nv.addGraph(function() {
            var chart3 = nv.models.discreteBarChart().x(function(d) {
                return d.label
            }).y(function(d) {
                        return d.value
                    }).staggerLabels(true)
                    .tooltips(true).showValues(true);
            chart3.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsTotalCostCost + ' (' + parent.ORYX.I18N.View.sim.chartsTotalCostCurrency + ')');

            d3.select('#chart3').datum(costData).transition().duration(500)
                    .call(chart3);

            nv.utils.windowResize(chart3.update);

            return chart3;
        });
    }


    var htResourceData = parent.ORYX.EDITOR.simulationHTResourceData;
    if(htResourceData && htResourceData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        var i;
        for(i = 0; i < htResourceData.length; i++ ) {
            var nextResourceObj = htResourceData[i];
            max += nextResourceObj.values[0].value;
            min += nextResourceObj.values[1].value;
            avg += nextResourceObj.values[2].value;
        }

        if(max > 0) {
            max = max / i;
        }
        if(min > 0) {
            min = min / i;
        }
        if(avg > 0) {
            avg = avg / i;
        }

        var resourceData = [
            {
                "values":[
                    {
                        "value":max,
                        "label":parent.ORYX.I18N.View.sim.chartsMax
                    },
                    {
                        "value":min,
                        "label":parent.ORYX.I18N.View.sim.chartsMin
                    },
                    {
                        "value":avg,
                        "label":parent.ORYX.I18N.View.sim.chartsAverage
                    }
                ],
                "color":"#1f77b4",
                "key":parent.ORYX.I18N.View.sim.chartsTotalResourceUtilization
            }
        ];

        nv.addGraph(function() {
            var chart4 = nv.models.discreteBarChart().x(function(d) {
                return d.label
            }).y(function(d) {
                        return d.value
                    }).staggerLabels(true)
                    .tooltips(true).showValues(true);
            chart4.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsTotalResourceUtilizationPercentages + ' (%)');

            d3.select('#chart4').datum(resourceData).transition().duration(500)
                    .call(chart4);

            nv.utils.windowResize(chart4.update);

            return chart4;
        });
    }

}

function showHBarChart() {
    var chartData = parent.ORYX.EDITOR.simulationChartData;
    var instanceData = parent.ORYX.EDITOR.simulationInstancesData;
    nv.addGraph(function() {
        var chart = nv.models.multiBarHorizontalChart()
                .x(function(d) { return d.label })
                .y(function(d) { return d.value })
                .margin({top: 30, right: 20, bottom: 50, left: 175})
                .showValues(true)
                .tooltips(true)
                .showControls(false);

        //chart.yAxis
        //		.tickFormat(d3.format(',.2f'));

        chart.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsExecutionTimesTime + ' (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')')

        d3.select('#chart1')
                .datum(chartData)
                .transition().duration(500)
                .call(chart);

        nv.utils.windowResize(chart.update);

        return chart;
    });

    nv.addGraph(function() {
        var chart2 = nv.models.multiBarHorizontalChart()
                .x(function(d) { return d.label })
                .y(function(d) { return d.value })
                .margin({top: 30, right: 20, bottom: 50, left: 175})
                .showValues(true)
                .tooltips(true)
                .showControls(false);

        //chart2.yAxis
        //	.tickFormat(d3.format(',.2f'));

        chart2.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsActivityInstancesInstances + ' (#)')

        d3.select('#chart2')
                .datum(instanceData)
                .transition().duration(500)
                .call(chart2);

        nv.utils.windowResize(chart2.update);

        updateChartIfNoData("chart2");
        return chart2;
    });

    var htCostData = parent.ORYX.EDITOR.simulationHTCostData;
    if(htCostData && htCostData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        for(var i=0; i < htCostData.length; i++ ) {
            var nextCostObj = htCostData[i];
            max += nextCostObj.values[0].value;
            min += nextCostObj.values[1].value;
            avg += nextCostObj.values[2].value;
        }

        var costData = [
            {
                "values":[
                    {
                        "value":max,
                        "label":parent.ORYX.I18N.View.sim.chartsMax
                    },
                    {
                        "value":min,
                        "label":parent.ORYX.I18N.View.sim.chartsMin
                    },
                    {
                        "value":avg,
                        "label":parent.ORYX.I18N.View.sim.chartsAverage
                    }
                ],
                "color":"#1f77b4",
                "key":parent.ORYX.I18N.View.sim.chartsTotalCost
            }
        ];

        nv.addGraph(function() {
            var chart3 = nv.models.multiBarHorizontalChart()
                    .x(function(d) { return d.label })
                    .y(function(d) { return d.value })
                    .margin({top: 30, right: 20, bottom: 50, left: 175})
                    .showValues(true)
                    .tooltips(true)
                    .showControls(false);

            //chart2.yAxis
            //	.tickFormat(d3.format(',.2f'));

            chart3.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsTotalCost + ' (' + parent.ORYX.I18N.View.sim.chartsTotalCostCurrency + ')')

            d3.select('#chart3')
                    .datum(costData)
                    .transition().duration(500)
                    .call(chart3);

            nv.utils.windowResize(chart3.update);

            return chart3;
        });
    }

    var htResourceData = parent.ORYX.EDITOR.simulationHTResourceData;
    if(htResourceData && htResourceData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        var i;
        for(i = 0; i < htResourceData.length; i++ ) {
            var nextResourceObj = htResourceData[i];
            max += nextResourceObj.values[0].value;
            min += nextResourceObj.values[1].value;
            avg += nextResourceObj.values[2].value;
        }

        if(max > 0) {
            max = max / i;
        }
        if(min > 0) {
            min = min / i;
        }
        if(avg > 0) {
            avg = avg / i;
        }

        var resourceData = [
            {
                "values":[
                    {
                        "value":max,
                        "label":parent.ORYX.I18N.View.sim.chartsMax
                    },
                    {
                        "value":min,
                        "label":parent.ORYX.I18N.View.sim.chartsMin
                    },
                    {
                        "value":avg,
                        "label":parent.ORYX.I18N.View.sim.chartsAverage
                    }
                ],
                "color":"#1f77b4",
                "key":parent.ORYX.I18N.View.sim.chartsTotalResourceUtilization
            }
        ];

        nv.addGraph(function() {
            var chart4 = nv.models.multiBarHorizontalChart()
                    .x(function(d) { return d.label })
                    .y(function(d) { return d.value })
                    .margin({top: 30, right: 20, bottom: 50, left: 175})
                    .showValues(true)
                    .tooltips(true)
                    .showControls(false);

            //chart2.yAxis
            //	.tickFormat(d3.format(',.2f'));

            chart4.yAxis.axisLabel(parent.ORYX.I18N.View.sim.chartsTotalResourceUtilizationPercentages + ' (%)')

            d3.select('#chart4')
                    .datum(resourceData)
                    .transition().duration(500)
                    .call(chart4);

            nv.utils.windowResize(chart4.update);

            return chart4;
        });
    }

}

function showPieChart() {
    var chartData = parent.ORYX.EDITOR.simulationChartData;
    var instanceData = parent.ORYX.EDITOR.simulationInstancesData;
    nv.addGraph(function() {
        var width = 400,
                height = 400;
        var chart = nv.models.pieChart()
                .x(function(d) { return d.label })
                .y(function(d) { return d.value })
                .showLabels(false)
                .color(d3.scale.category10().range())
                .width(width)
                .height(height);

        d3.select("#chart1")
                .datum(chartData)
                .transition().duration(500)
                .attr('width', width)
                .attr('height', height)
                .call(chart);

        return chart;
    });

    nv.addGraph(function() {
        var width = 400,
                height = 400;
        var chart = nv.models.pieChart()
                .x(function(d) { return d.label })
                .y(function(d) { return d.value })
                .showLabels(false)
                .color(d3.scale.category10().range())
                .width(width)
                .height(height);

        d3.select("#chart2")
                .datum(instanceData)
                .transition().duration(500)
                .attr('width', width)
                .attr('height', height)
                .call(chart);

        return chart;
    });


    var htCostData = parent.ORYX.EDITOR.simulationHTCostData;
    if(htCostData && htCostData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        for(var i=0; i < htCostData.length; i++ ) {
            var nextCostObj = htCostData[i];
            max += nextCostObj.values[0].value;
            min += nextCostObj.values[1].value;
            avg += nextCostObj.values[2].value;
        }

        var costData = [
            {
                "values":[
                    {
                        "value":max,
                        "label":parent.ORYX.I18N.View.sim.chartsMax
                    },
                    {
                        "value":min,
                        "label":parent.ORYX.I18N.View.sim.chartsMin
                    },
                    {
                        "value":avg,
                        "label":parent.ORYX.I18N.View.sim.chartsAverage
                    }
                ],
                "color":"#1f77b4",
                "key":parent.ORYX.I18N.View.sim.chartsTotalCost
            }
        ];

        nv.addGraph(function() {
            var width = 400,
                    height = 400;
            var chart = nv.models.pieChart()
                    .x(function(d) { return d.label })
                    .y(function(d) { return d.value })
                    .showLabels(false)
                    .color(d3.scale.category10().range())
                    .width(width)
                    .height(height);

            d3.select("#chart3")
                    .datum(costData)
                    .transition().duration(500)
                    .attr('width', width)
                    .attr('height', height)
                    .call(chart);

            return chart;
        });
    }

    var htResourceData = parent.ORYX.EDITOR.simulationHTResourceData;
    if(htResourceData && htResourceData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        var i;
        for(i = 0; i < htResourceData.length; i++ ) {
            var nextResourceObj = htResourceData[i];
            max += nextResourceObj.values[0].value;
            min += nextResourceObj.values[1].value;
            avg += nextResourceObj.values[2].value;
        }

        if(max > 0) {
            max = max / i;
        }
        if(min > 0) {
            min = min / i;
        }
        if(avg > 0) {
            avg = avg / i;
        }

        var resourceData = [
            {
                "values":[
                    {
                        "value":max,
                        "label":parent.ORYX.I18N.View.sim.chartsMax
                    },
                    {
                        "value":min,
                        "label":parent.ORYX.I18N.View.sim.chartsMin
                    },
                    {
                        "value":avg,
                        "label":parent.ORYX.I18N.View.sim.chartsAverage
                    }
                ],
                "color":"#1f77b4",
                "key":parent.ORYX.I18N.View.sim.chartsTotalResourceUtilization
            }
        ];

        nv.addGraph(function() {
            var width = 400,
                    height = 400;
            var chart = nv.models.pieChart()
                    .x(function(d) { return d.label })
                    .y(function(d) { return d.value })
                    .showLabels(false)
                    .color(d3.scale.category10().range())
                    .width(width)
                    .height(height);

            d3.select("#chart4")
                    .datum(resourceData)
                    .transition().duration(500)
                    .attr('width', width)
                    .attr('height', height)
                    .call(chart);

            return chart;
        });
    }
}

function showTable() {
    var tableData = parent.ORYX.EDITOR.simulationChartData;
    var tableInstanceData = parent.ORYX.EDITOR.simulationInstancesData;
    var tableSource = $("#tabletemplate").html();
    var tableInstanceSource = $("#tableinstancestemplate").html();
    var tableCostSource = $("#tablecosttemplate").html();
    var tableResourcesSource = $("#tableresourcestemplate").html();

    Handlebars.registerHelper('timeunit', function(options) {
        return parent.ORYX.EDITOR.simulationChartTimeUnit;
    });

    var tableTempplate = Handlebars.compile(tableSource);
    var tableInstanceTemplate = Handlebars.compile(tableInstanceSource);
    var tableCostTemplate =  Handlebars.compile(tableCostSource);
    var tableResourcesTempate = Handlebars.compile(tableResourcesSource);

    $("#chartcontent1").html(tableTempplate(tableData[0]));
    $("#chartcontent2").html(tableInstanceTemplate(tableInstanceData[0]));

    var htCostData = parent.ORYX.EDITOR.simulationHTCostData;
    if(htCostData && htCostData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        for(var i=0; i < htCostData.length; i++ ) {
            var nextCostObj = htCostData[i];
            max += nextCostObj.values[0].value;
            min += nextCostObj.values[1].value;
            avg += nextCostObj.values[2].value;
        }

        var costData = {
            "values":[
                {
                    "value":max,
                    "label":parent.ORYX.I18N.View.sim.chartsMax  + ' (' + parent.ORYX.I18N.View.sim.chartsTotalCostCurrency + ')'
                },
                {
                    "value":min,
                    "label":parent.ORYX.I18N.View.sim.chartsMin  + ' (' + parent.ORYX.I18N.View.sim.chartsTotalCostCurrency + ')'
                },
                {
                    "value":avg,
                    "label":parent.ORYX.I18N.View.sim.chartsAverage  + ' (' + parent.ORYX.I18N.View.sim.chartsTotalCostCurrency + ')'
                }
            ],
            "color":"#1f77b4",
            "key":parent.ORYX.I18N.View.sim.chartsTotalCost
        };

        $("#chartcontent3").html(tableCostTemplate(costData));
    }

    var htResourceData = parent.ORYX.EDITOR.simulationHTResourceData;
    if(htResourceData && htResourceData.length > 0) {
        var max = 0;
        var min = 0;
        var avg = 0;
        var i;
        for(i = 0; i < htResourceData.length; i++ ) {
            var nextResourceObj = htResourceData[i];
            max += nextResourceObj.values[0].value;
            min += nextResourceObj.values[1].value;
            avg += nextResourceObj.values[2].value;
        }

        if(max > 0) {
            max = max / i;
        }
        if(min > 0) {
            min = min / i;
        }
        if(avg > 0) {
            avg = avg / i;
        }

        var resourceData = {
            "values":[
                {
                    "value":max,
                    "label":parent.ORYX.I18N.View.sim.chartsMax
                },
                {
                    "value":min,
                    "label":parent.ORYX.I18N.View.sim.chartsMin
                },
                {
                    "value":avg,
                    "label":parent.ORYX.I18N.View.sim.chartsAverage
                }
            ],
            "color":"#1f77b4",
            "key":parent.ORYX.I18N.View.sim.chartsTotalResourceUtilization
        };

        $("#chartcontent4").html(tableResourcesTempate(resourceData));
    }

}

function showLineChart() {
    var aggregationData = parent.ORYX.EDITOR.simulationEventAggregationData;
    var minArray = [],
            maxArray = [],
            avgArray = [];
    var timeunit = "";
    for (var i = 0; i < aggregationData.length; i++) {
        var nextData = aggregationData[i];
        var dataTimeSinceStart = nextData.timesincestart;
        var dataValues = nextData.values;

        maxArray.push({x: dataTimeSinceStart, y: dataValues[0].value});
        minArray.push({x: dataTimeSinceStart, y: dataValues[1].value});
        avgArray.push({x: dataTimeSinceStart, y: dataValues[2].value});
        timeunit = nextData.timeunit;
    }

    var chartData = [
        {
            values: maxArray,
            key: parent.ORYX.I18N.View.sim.chartsMax,
            color: '#ff7f0e'
        },
        {
            values: minArray,
            key: parent.ORYX.I18N.View.sim.chartsMin,
            color: '#4876FF'
        },
        {
            values: avgArray,
            key: parent.ORYX.I18N.View.sim.chartsAverage,
            color: '#2ca02c'
        }
    ];

    nv.addGraph(function() {
        var chart = nv.models.lineWithFocusChart();
        var i18nTimeunit = timeunit;
        if (parent.ORYX.I18N.propertyNamesValue[timeunit] !== undefined) {
            i18nTimeunit = parent.ORYX.I18N.propertyNamesValue[timeunit];
        }
        chart.xAxis
                .axisLabel(parent.ORYX.I18N.View.sim.chartsSimulationTime + ' (' + i18nTimeunit + ')')
                .tickFormat(d3.format(',r'));
        chart.yAxis
                .axisLabel(parent.ORYX.I18N.View.sim.chartsExecutionTimes + ' (' + i18nTimeunit + ')')
                .tickFormat(d3.format('.02f'));
        d3.select('#chart1')
                .datum(chartData)
                .transition().duration(500)
                .call(chart);
        nv.utils.windowResize(function() { d3.select('#chart1').call(chart) });
        return chart;
    });

}

function showTimeline() {
    var cont = "<div style='margin:0;padding:0;'> \
				<div class='tlineswitch'><a href='#' onclick='document.getElementById(\"processevents\").contentWindow.switchDisplay(\"chart\"); return false;'>" + parent.ORYX.I18N.View.sim.Chart + "</a> | <a href='#' onclick='document.getElementById(\"processevents\").contentWindow.switchDisplay(\"model\"); return false;'>" + parent.ORYX.I18N.View.sim.Model + "</a></div> \
				</div> \
				<iframe id='processevents' name='' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/simulation/processevents.jsp' width='100%' height='500' scrolling='no' frameBorder='0'></iframe> \
				";

    document.getElementById('outterchart').innerHTML = cont;
}

function updateChartIfNoData(chartid) {
    var chartEl = document.getElementById(chartid);
    if (chartEl.firstChild.innerHTML === "No Data Available.") {
        chartEl.firstChild.innerHTML = parent.ORYX.I18N.View.sim.NoDataAvailable;
    }
}

// document.getElementById('iframeid').contentWindow.myFunc();
</script>
</body>
</html>

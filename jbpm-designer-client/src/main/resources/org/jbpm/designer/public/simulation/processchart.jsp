<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/nv-core.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/simulationcharts.css" rel="stylesheet" type="text/css">
<script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/d3.v2.min.js"></script>
<script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/nv.min.js"></script>
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
    </p>";
	
	document.getElementById('outterchart').innerHTML = cnt;
}

function clearChartForLine() {
	var cnt = "<h2>Process execution times during Simulation</h2> \
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
	<div class="lcharticon"><a href="#" onclick="clearChartForLine(); showLineChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/linecharticon.png" title="Line Chart"/></a></div>
	<div class="timelineicon"><a href="#" onclick="clearChart(); showTimeline(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/timelineicon.png" title="Timeline"/></a></div>
	<div class="tableicon"><a href="#" onclick="clearChart(); showTable(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/tableicon.png" title="Table"/></a></div>
    <div class="pcharticon"><a href="#" onclick="clearChart(); showPieChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/piecharticon.png" title="Pie Chart"/></a></div>
    <div class="hbcharticon"><a href="#" onclick="clearChart(); showHBarChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/hbarcharticon.png" title="Horizontal Bar Chart"/></a></div>
    <div class="bcharticon"><a href="#" onclick="clearChart(); showBarChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/barcharticon.png" title="Bar Chart"/></a></div>
    <div class="charttitle"><script>document.write(parent.ORYX.EDITOR.simulationChartTitle +  " (" + parent.ORYX.EDITOR.simulationChartNodeName + ")");</script></div>
</div><br/>
<div id="outterchart" class="outterchart">
	<h2 id="chart1label">Execution Times</h2>
  	<p id="chartcontent1">
  	<svg id="chart1" style='height:400px;width:400px'></svg>
	</p>
	<h2 id="chart2label"><script>document.write(parent.ORYX.I18N.View.sim.chartsActivityInstances);</script></h2>
  	<p id="chartcontent2">
  	<svg id="chart2" style='height:400px;width:400px'></svg>
	</p>
    <h2 id="chart3label">Total Cost</h2>
    <p id="chartcontent3">
        <svg id="chart3" style='height:400px;width:400px'></svg>
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
                <th scope="col" align="center">{{label}} (USD)</th>
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
			var instanceData = parent.ORYX.EDITOR.simulationInstancesData;
			nv.addGraph(function() {
				var chart = nv.models.discreteBarChart().x(function(d) {
					return d.label
				}).y(function(d) {
					return d.value
				}).staggerLabels(true)
				.tooltips(true).showValues(true);
				chart.yAxis.axisLabel('Time (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')')
	
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
				chart2.yAxis.axisLabel('Intances (#)')
	
				d3.select('#chart2').datum(instanceData).transition().duration(500)
						.call(chart2);
	
				nv.utils.windowResize(chart2.update);
	
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
                                "label":"Max"
                            },
                            {
                                "value":min,
                                "label":"Min"
                            },
                            {
                                "value":avg,
                                "label":"Average"
                            }
                        ],
                        "color":"#1f77b4",
                        "key":"Total Cost"
                    }
                 ];

                nv.addGraph(function() {
                    var chart3 = nv.models.discreteBarChart().x(function(d) {
                        return d.label
                    }).y(function(d) {
                                return d.value
                            }).staggerLabels(true)
                            .tooltips(true).showValues(true);
                    chart3.yAxis.axisLabel('Cost (USD)')

                    d3.select('#chart3').datum(costData).transition().duration(500)
                            .call(chart3);

                    nv.utils.windowResize(chart3.update);

                    return chart3;
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
				
				chart.yAxis.axisLabel('Time (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')')
				
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
				
				chart2.yAxis.axisLabel('Instances (#)')
				
				d3.select('#chart2')
					.datum(instanceData)
					.transition().duration(500)
					.call(chart2);

				nv.utils.windowResize(chart2.update);
			
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
                                "label":"Max"
                            },
                            {
                                "value":min,
                                "label":"Min"
                            },
                            {
                                "value":avg,
                                "label":"Average"
                            }
                        ],
                        "color":"#1f77b4",
                        "key":"Total Cost"
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

                    chart3.yAxis.axisLabel('Total Cost (USD)')

                    d3.select('#chart3')
                            .datum(costData)
                            .transition().duration(500)
                            .call(chart3);

                    nv.utils.windowResize(chart3.update);

                    return chart3;
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
                                "label":"Max"
                            },
                            {
                                "value":min,
                                "label":"Min"
                            },
                            {
                                "value":avg,
                                "label":"Average"
                            }
                        ],
                        "color":"#1f77b4",
                        "key":"Total Cost"
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
		}
		
		function showTable() {
			var tableData = parent.ORYX.EDITOR.simulationChartData;
			var tableInstanceData = parent.ORYX.EDITOR.simulationInstancesData;
			var tableSource = $("#tabletemplate").html();
			var tableInstanceSource = $("#tableinstancestemplate").html();
            var tableCostSource = $("#tablecosttemplate").html();

            Handlebars.registerHelper('timeunit', function(options) {
                return parent.ORYX.EDITOR.simulationChartTimeUnit;
            });

	    	var tableTempplate = Handlebars.compile(tableSource);
	    	var tableInstanceTemplate = Handlebars.compile(tableInstanceSource);
            var tableCostTemplate =  Handlebars.compile(tableCostSource);

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
                                "label":"Max"
                            },
                            {
                                "value":min,
                                "label":"Min"
                            },
                            {
                                "value":avg,
                                "label":"Average"
                            }
                        ],
                        "color":"#1f77b4",
                        "key":"Total Cost"
                    };

                $("#chartcontent3").html(tableCostTemplate(costData));
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
			                	 key: 'Max',
			                	 color: '#ff7f0e'
			                 },
			                 {
			                	 values: minArray,
			                	 key: 'Min',
			                	 color: '#4876FF'
			                 },
			                 {
			                	 values: avgArray,
			                	 key: 'Avgerage',
			                	 color: '#2ca02c'
			                 }
			                 ];
			
			nv.addGraph(function() {  
				 var chart = nv.models.lineWithFocusChart();
				 chart.xAxis
				        .axisLabel('Simulation Time (' + timeunit + ')')
				        .tickFormat(d3.format(',r'));
				 chart.yAxis
				        .axisLabel('Execution Times (' + timeunit + ')')
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
				<div class='tlineswitch'><a href='#' onclick='document.getElementById(\"processevents\").contentWindow.switchDisplay(\"chart\"); return false;'>Chart</a> | <a href='#' onclick='document.getElementById(\"processevents\").contentWindow.switchDisplay(\"model\"); return false;'>Model</a></div> \
				</div> \
				<iframe id='processevents' name='' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/simulation/processevents.jsp' width='100%' height='500' scrolling='no' frameBorder='0'></iframe> \
				";
			
			document.getElementById('outterchart').innerHTML = cont;
		}
		
		// document.getElementById('iframeid').contentWindow.myFunc();
</script>
</body>
</html>

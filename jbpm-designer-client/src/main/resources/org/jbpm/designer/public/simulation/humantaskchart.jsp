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
	document.getElementById('chartcontent1').innerHTML = "<svg id='chart1' style='height:400px;width:400px'></svg>";
	document.getElementById('chartcontent2').innerHTML = "<svg id='chart2' style='height:400px;width:400px'></svg>";
	document.getElementById('chartcontent3').innerHTML = "<svg id='chart3' style='height:400px;width:400px'></svg>";
}
</script>
</head>
<body onload="showBarCharts();">
<center>
<div style="margin:20;padding:0;">
	<!-- <div class="timelineicon"><a href="#" onclick="clearChart(); showTimeline(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/timelineicon.png" title="Timeline"/></a></div> -->
	<div class="tableicon"><a href="#" onclick="clearChart(); showTables(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/tableicon.png" title="Tables"/></a></div>
    <div class="hbcharticon"><a href="#" onclick="clearChart(); showBarCharts(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/hbarcharticon.png" title="Bar Charts"/></a></div>
    <div class="charttitle"><script>document.write(parent.ORYX.EDITOR.simulationChartTitle +  " (" + parent.ORYX.EDITOR.simulationChartNodeName + ")");</script></div>
</div><br/>
<div class="outterchart">
    <h2><script>document.write(parent.ORYX.I18N.View.sim.chartsExecutionTimes);</script></h2>
  	<p id="chartcontent1">
  	<svg id="chart1" style='height:300px;width:450px'></svg>
	</p>
	<h2><script>document.write(parent.ORYX.I18N.View.sim.chartsResourceUtilization);</script></h2>
  	<p id="chartcontent2">
  	<svg id="chart2" style='height:300px;width:450px'></svg>
	</p>
	<h2><script>document.write(parent.ORYX.I18N.View.sim.chartsResourceCost);</script></h2>
  	<p id="chartcontent3">
  	<svg id="chart3" style='height:300px;width:450px'></svg>
	</p>
</div>
</center>
<script id="tabletemplateexecution" type="text/x-handlebars-template">
		<center>
		<table id="box-table" summary="Execution Times">
		<thead>
            <tr>
				{{#each elements}}
				<th colspan="3" align="center">{{key}} ({{timeunit }})</th>
				{{/each}}
			</tr>
			<tr>
				{{#each elements}}
				{{#this.values}}
				<th scope="col" align="center">{{label}}</th>
				{{/this.values}}
				{{/each}}
			</tr>
		</thead>
		<tbody>
			<tr>
			{{#each elements}}
			{{#this.values}}
				<td align="center">{{value}}</td>
			{{/this.values}}
			{{/each}}
            </tr>
		</tbody>
	    </table>
        </center>
</script>
<script id="tabletemplateresource" type="text/x-handlebars-template">
		<center>
		<table id="box-table" summary="Resource Utilization">
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
<script id="tabletemplatecost" type="text/x-handlebars-template">
		<center>
		<table id="box-table" summary="Resource Cost">
		<thead>
			<tr>
				{{#values}}
				<th scope="col" align="center">{{label}} ($)</th>
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
		function showBarCharts() {
			var chartData = parent.ORYX.EDITOR.simulationChartData;
			nv.addGraph(function() {
				 var chart = nv.models.multiBarHorizontalChart()
				 	.x(function(d) { return d.label })
				 	.y(function(d) { return d.value })
				 	.margin({top: 30, right: 20, bottom: 50, left: 175})
				 	 .showValues(true)
				 	 .tooltips(true)
				 	 .showControls(false);
				chart.yAxis
				.tickFormat(d3.format(',.2f'));
				chart.yAxis.axisLabel('Time (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')')
				d3.select('#chart1')
				.datum(chartData.timevalues)
				.transition().duration(500)
				.call(chart);
				nv.utils.windowResize(chart.update);
				return chart;
				});
			
			 nv.addGraph(function() {
				 var chart = nv.models.multiBarHorizontalChart()
				 	.x(function(d) { return d.label })
				 	.y(function(d) { return d.value })
				 	.showValues(true)
				 	.tooltips(true)
				 	.showControls(false);
	
				chart.yAxis
					.tickFormat(d3.format(',.2f'));
				
				chart.yAxis.axisLabel('Percentage (%)')
				var dw = [];
				dw.push(chartData.resourcevalues);
				d3.select('#chart2')
					.datum(dw)
					.transition().duration(500)
					.call(chart);

				nv.utils.windowResize(chart.update);
			
				return chart;
			}); 
			 
			 nv.addGraph(function() {
				 var chart = nv.models.multiBarHorizontalChart()
				 	.x(function(d) { return d.label })
				 	.y(function(d) { return d.value })
				 	.showValues(true)
				 	.tooltips(true)
				 	.showControls(false);
	
				chart.yAxis
					.tickFormat(d3.format(',.2f'));
				
				chart.yAxis.axisLabel('Cost ($)')
				var dw = [];
				dw.push(chartData.costvalues);
				d3.select('#chart3')
					.datum(dw)
					.transition().duration(500)
					.call(chart);

				nv.utils.windowResize(chart.update);
			
				return chart;
			}); 
		}
		
		function showTables() {
			var chartData = parent.ORYX.EDITOR.simulationChartData;
            Handlebars.registerHelper('timeunit', function(options) {
                return parent.ORYX.EDITOR.simulationChartTimeUnit;
            });
			
			var executionTimesFormattedData = { 'elements' : [chartData.timevalues[0], chartData.timevalues[1]] };
			var executiontimesTableSource = $("#tabletemplateexecution").html();
	    	var executiontimesTableTemplate = Handlebars.compile(executiontimesTableSource);
	    	$("#chartcontent1").html(executiontimesTableTemplate(executionTimesFormattedData));
			
			var resourceTableSource = $("#tabletemplateresource").html();
	    	var resourceTableTemplate = Handlebars.compile(resourceTableSource);
	    	$("#chartcontent2").html(resourceTableTemplate(chartData.resourcevalues));
	    	
	    	var costTableSource = $("#tabletemplatecost").html();
	    	var costTableTemplate = Handlebars.compile(costTableSource);
	    	$("#chartcontent3").html(costTableTemplate(chartData.costvalues));
		}
		
		function showTimeline() {
			//alert("showing timeline!");
		}
</script>
</body>
</html>

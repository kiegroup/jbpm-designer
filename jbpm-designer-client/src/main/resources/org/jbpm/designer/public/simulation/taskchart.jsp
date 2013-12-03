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
	document.getElementById('chartcontent').innerHTML = "<svg id='chart' style='height:400px;width:400px'></svg>";
}
</script>
</head>
<body onload="showBarChart();">
<center>
<div style="margin:20;padding:0;">
	<!-- <div class="timelineicon"><a href="#" onclick="clearChart(); showTimeline(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/timelineicon.png" title="Timeline"/></a></div> -->
	<div class="tableicon"><a href="#" onclick="clearChart(); showTable(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/tableicon.png" title="Table"/></a></div>
    <div class="pcharticon"><a href="#" onclick="clearChart(); showPieChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/piecharticon.png" title="Pie Chart"/></a></div>
    <div class="hbcharticon"><a href="#" onclick="clearChart(); showHBarChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/hbarcharticon.png" title="Horizontal Bar Chart"/></a></div>
    <div class="bcharticon"><a href="#" onclick="clearChart(); showBarChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/barcharticon.png" title="Bar Chart"/></a></div>
    <div class="charttitle"><script>document.write(parent.ORYX.EDITOR.simulationChartTitle +  " (" + parent.ORYX.EDITOR.simulationChartNodeName + ")");</script></div>
</div><br/>
<div class="outterchart">
    <h2><script>document.write(parent.ORYX.I18N.View.sim.chartsExecutionTimes);</script></h2>
  	<p id="chartcontent">
  	<svg id="chart" style='height:400px;width:400px'></svg>
	</p>
</div>
</center>
<script id="tabletemplate" type="text/x-handlebars-template">
		<center>
		<table id="box-table" summary="Execution Times">
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
<script>
		function getEventData() {
			return parent.ORYX.EDITOR.simulationEventData;
		}
		function showBarChart() {
			var chartData = parent.ORYX.EDITOR.simulationChartData;
			nv.addGraph(function() {
				var chart = nv.models.discreteBarChart().x(function(d) {
					return d.label
				}).y(function(d) {
					return d.value
				}).staggerLabels(true)
				//.staggerLabels(historicalBarChart[0].values.length > 8)
				.tooltips(true).showValues(true);
				chart.yAxis.axisLabel('Time (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')')
	
				d3.select('#chart').datum(chartData).transition().duration(500)
						.call(chart);
	
				nv.utils.windowResize(chart.update);
	
				return chart;
			}); 
		}
		
		function showHBarChart() {
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
				
				d3.select('#chart')
					.datum(chartData)
					.transition().duration(500)
					.call(chart);

				nv.utils.windowResize(chart.update);
			
				return chart;
			}); 
		}
		
		function showPieChart() {
			var chartData = parent.ORYX.EDITOR.simulationChartData;
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

			      d3.select("#chart")
			          .datum(chartData)
			        .transition().duration(500)
			        .attr('width', width)
          			.attr('height', height)
			          .call(chart);

			    return chart;
			});
		}
		
		function showTable() {
			var tableData = parent.ORYX.EDITOR.simulationChartData;
			var tableSource = $("#tabletemplate").html();
            Handlebars.registerHelper('timeunit', function(options) {
                return parent.ORYX.EDITOR.simulationChartTimeUnit;
            });
	    	var tableTempplate = Handlebars.compile(tableSource);
	    	$("#chartcontent").html(tableTempplate(tableData[0]));
		}
		
		function showTimeline() {
			alert("showing timeline!");
		}
</script>
</body>
</html>

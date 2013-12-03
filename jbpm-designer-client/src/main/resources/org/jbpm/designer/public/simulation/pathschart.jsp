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
	var cnt = "<h2>" + parent.ORYX.I18N.View.sim.chartsPathImage + "</h2> \
  			   <p id='chartcontent1'> \
			   </p> \
			   <h2>" + parent.ORYX.I18N.View.sim.chartsPathInstanceExecution + "</h2> \
  			   <p id='chartcontent2'> \
  			   <svg id='chart2' style='height:300px;width:450px'></svg> \
			   </p>";
		
	document.getElementById('outterchart').innerHTML = cnt;
}
</script>
</head>
<body onload="showPieChart();">
<center>
<div style="margin:20;padding:0;">
	<!-- <div class="timelineicon"><a href="#" onclick="clearChart(); showTimeline(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/timelineicon.png" title="Timeline"/></a></div> -->
	<div class="tableicon"><a href="#" onclick="clearChart(); showTable(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/tableicon.png" title="Table"/></a></div>
    <div class="pcharticon"><a href="#" onclick="clearChart(); showPieChart(); return false;"><img src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/simulation/piecharticon.png" title="Pie Chart"/></a></div>
    <div class="charttitle"><script>document.write(parent.ORYX.EDITOR.simulationChartTitle);</script></div>
</div><br/>
<div id="outterchart" class="outterchart">
    <h2><script>document.write(parent.ORYX.I18N.View.sim.chartsPathImage);</script></h2>
  	<p id="chartcontent1">
	</p>
	<h2><script>document.write(parent.ORYX.I18N.View.sim.chartsPathInstanceExecution);</script></h2>
  	<p id="chartcontent2">
  	<svg id="chart2" style='height:300px;width:450px'></svg>
	</p>
</div>
</center>
<script id="tabletemplate" type="text/x-handlebars-template">
		<center>
		<table id="box-table" summary="Path Instances">
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
<script>
		function getPathData() {
			return parent.ORYX.EDITOR.simulationPathData;
		}
		
		function getPathId() {
			return parent.ORYX.EDITOR.simulationPathId;
		}
		
		function getPathSVG() {
			return parent.ORYX.EDITOR.simulationPathSVG;
		}
		
		function getToShowData() {
			var pathData = parent.ORYX.EDITOR.simulationPathData;
			var pathid = parent.ORYX.EDITOR.simulationPathId;
			var toshowData = [
			                  {
			                      "values":[
			                      ],
			                      "name":"paths",
			                      "key":"Process Paths"
			                   }
			                ];
			for (var i = 0; i < pathData.length; i++) {
				var nextData = pathData[i];
				if(nextData.id == pathid) {
					var inc1 = {
				               "value":nextData.numinstances,
				               "label": nextData.id 
				              };
					var inc2 = {
				               	"value": nextData.totalinstances - nextData.numinstances,
				               	"label":"Other Paths"
				               };
					toshowData[0].values[0] = inc1;
					toshowData[0].values[1] = inc2;
				}
			}
			return toshowData;
		}
		
		function showPieChart() {
			displayPathImage();
			nv.addGraph(function() {
				var width = 350,
		            height = 300;
			    var chart = nv.models.pieChart()
			        .x(function(d) { return d.label })
			        .y(function(d) { return d.value })
			        .showLabels(false)
			        .color(d3.scale.category10().range())
			        .width(width)
        			.height(height);

			      d3.select("#chart2")
			          .datum(getToShowData())
			        .transition().duration(500)
			        .attr('width', width)
          			.attr('height', height)
			          .call(chart);

			    return chart;
			});
		}
		
		function showTable() {
			displayPathImage();
			var tableSource = $("#tabletemplate").html();
	    	var tableTempplate = Handlebars.compile(tableSource);
	    	var data = getToShowData();
	    	$("#chartcontent2").html(tableTempplate(data[0]));
		}
		
		function displayPathImage() {
			document.getElementById('chartcontent1').innerHTML = "<iframe id='processimageframe' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/simulation/pathimage.jsp' width='100%' height='300' scrolling='auto' frameBorder='0'></iframe>";
		}
</script>
</body>
</html>

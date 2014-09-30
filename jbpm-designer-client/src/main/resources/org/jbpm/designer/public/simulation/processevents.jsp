<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/timeline.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/timeline-ext.css" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/simulation/nv-core.css" rel="stylesheet" type="text/css">
	<script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/d3.v2.min.js"></script>
	<script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/nv.min.js"></script>
	<script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-1.7.2.min.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/simulation/timeline-min.js" type="text/javascript"></script>
</head>
<body>
<div id="timeline-embed">
	<div id="timelinejs">
	</div>
</div>
<script>
	var data = parent.getEventData();
	var aggregatedData = parent.getEventAggregationData();
	var currentEventNum = 0;
	var currentEventId = "";
	var displayType = "chart";

	parent.parent.ORYX.EDITOR._pluginFacade.registerOnEvent('simulation.showannotatedprocess', showModelData);

    function getModelSVG() {
        return modelSVG;
    }

	function clearDisplay() {
        var cnt = "";
		if(displayType == "chart") {
			cnt = "<svg id='chart' style='height:290px;width:400px'></svg>";
			document.getElementById('chartcontent').innerHTML = cnt;
		} else if(displayType == "model") {
            cnt = "<iframe id='processeventmodelframe' name='processeventmodelframe' src='<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/simulation/modelimage.jsp' width='100%' height='300' scrolling='auto' frameBorder='0'></iframe>";
            document.getElementById('chartcontent').innerHTML = cnt;
        }
	}
	function displayChartForEvent(eventNum, eventId) {
		clearDisplay();
		if(eventNum) {
			currentEventNum = eventNum;
			currentEventId = eventId;
			var datawrapper = [aggregatedData[eventNum-1]];
			nv.addGraph(function() {
				var chart = nv.models.discreteBarChart().x(function(d) {
					return d.label
				}).y(function(d) {
					return d.value
				}).staggerLabels(true)
				.tooltips(true).showValues(true);
				chart.yAxis.axisLabel('Time (' + parent.ORYX.EDITOR.simulationChartTimeUnit + ')')
	
				d3.select('#chart').datum(datawrapper).transition().duration(500)
						.call(chart);
	
				nv.utils.windowResize(chart.update);
	
				return chart;
			}); 
		}
	}
	
	function displayModelForEvent(eventNum,eventId) {
		clearDisplay();
		if(eventNum && eventId) {
			currentEventNum = eventNum;
			currentEventId = eventId;
			parent.parent.ORYX.EDITOR._pluginFacade.raiseEvent({
	            type: 'simulation.annotateprocess',
                nodeid: eventId,
                eventnum: eventNum,
                data: aggregatedData[eventNum-1]
	    	});
		}
	}
	
	function showModelData(options) {
        if(document.getElementById('processeventmodelframe')) {
           options.wind.setTimeout(function() {document.getElementById('processeventmodelframe').contentWindow.showModelImage(options.data);},500);
        } else {
           clearDisplay();
           options.wind.setTimeout(function() {document.getElementById('processeventmodelframe').contentWindow.showModelImage(options.data);},500);
        }
	}

	function displayDataForEvent(eventNum, eventId) {
		if(displayType == "chart") {
			displayChartForEvent(eventNum,eventId);
		} else if(displayType == "model") {
			displayModelForEvent(eventNum,eventId)
		}
	}
	
	function switchDisplay(disp) {
		displayType = disp;
		displayDataForEvent(currentEventNum, currentEventId);
	}
	var timeline = new VMM.Timeline;
	timeline.init(data);
</script>
</body>
</html>
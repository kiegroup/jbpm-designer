function simChartSetProcessAveragesLabels(cData, i18n) {
    cData.key = i18n.View.sim.chartsProcessAverages;
    if (cData.values && cData.values.length == 3) {
        cData.values[0].label = i18n.View.sim.chartsMaxExecutionTime;
        cData.values[1].label = i18n.View.sim.chartsMinExecutionTime;
        cData.values[2].label = i18n.View.sim.chartsAvgExecutionTime;
    }
}

function simChartSetMinMaxAvgLabels(values, i18n) {
    if (values && values.length == 3) {
        values[0].label = i18n.View.sim.chartsMax;
        values[1].label = i18n.View.sim.chartsMin;
        values[2].label = i18n.View.sim.chartsAverage;
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.Scenario;
import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.codec.binary.Base64;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.simulation.AggregatedSimulationEvent;
import org.jbpm.simulation.PathFinder;
import org.jbpm.simulation.PathFinderFactory;
import org.jbpm.simulation.SimulationEvent;
import org.jbpm.simulation.SimulationInfo;
import org.jbpm.simulation.SimulationRepository;
import org.jbpm.simulation.SimulationRunner;
import org.jbpm.simulation.converter.JSONPathFormatConverter;
import org.jbpm.simulation.impl.WorkingMemorySimulationRepository;
import org.jbpm.simulation.impl.events.ActivitySimulationEvent;
import org.jbpm.simulation.impl.events.AggregatedActivitySimulationEvent;
import org.jbpm.simulation.impl.events.AggregatedProcessSimulationEvent;
import org.jbpm.simulation.impl.events.EndSimulationEvent;
import org.jbpm.simulation.impl.events.GatewaySimulationEvent;
import org.jbpm.simulation.impl.events.GenericSimulationEvent;
import org.jbpm.simulation.impl.events.HTAggregatedSimulationEvent;
import org.jbpm.simulation.impl.events.HumanTaskActivitySimulationEvent;
import org.jbpm.simulation.impl.events.StartSimulationEvent;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "Simulation", name = "SimulationServlet",
        urlPatterns = "/simulation")
public class SimulationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory.getLogger(SimulationServlet.class);
    protected static final String ACTION_GETPATHINFO = "getpathinfo";
    protected static final String ACTION_RUNSIMULATION = "runsimulation";
    private ServletConfig config;
    private List<SimulationEvent> eventAggregations = new ArrayList<SimulationEvent>();
    private List<Long> eventAggregationsTimes = new ArrayList<Long>();
    private Map<String, Integer> pathInfoMap = null;
    private DateTime simTime = null;

    private static final String MAX = "Max";
    private static final String MIN = "Min";
    private static final String AVERAGE = "Average";
    private static final String MAX_EXECUTION_TIME = "Max Execution Time";
    private static final String MIN_EXECUTION_TIME = "Min Execution Time";
    private static final String AVG_EXECUTION_TIME = "Avg Execution Time";
    private static final String ACTIVITY_INSTANCES = "Activity Instances";
    private static final String PROCESS_AVERAGES = "Process Averages";
    private static final String EXECUTION_TIMES = "Execution Times";
    private static final String WAIT_TIMES = "Wait Times";
    private static final String RESOURCE_ALLOCATIONS = "Resource Allocations";
    private static final String RESOURCE_COST = "Resource Cost";
    private static final String HUMAN_TASK_AVERAGES = "Human Task Averages";
    private static final String TASK_AVERAGES = "Task Averages";
    private static final String SIMULATION_EVENTS = "Simulation Events";

    @Inject
    private IDiagramProfileService _profileService = null;

    private IDiagramProfile profile;

    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String json = UriUtils.decode(Utils.getEncodedParam(req,
                                                            "json"));
        String action = req.getParameter("action");
        String preprocessingData = req.getParameter("ppdata");
        String selectionId = req.getParameter("sel");
        String numInstances = req.getParameter("numinstances");
        String interval = req.getParameter("interval");
        String intervalUnit = req.getParameter("intervalunit");
        String language = req.getParameter("language");
        String simTestStartTime = req.getParameter("simteststarttime");
        String simTestEndTime = req.getParameter("simtestendtime");

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }

        if (action != null && action.equals(ACTION_GETPATHINFO)) {
            try {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();

                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                Definitions def = ((Definitions) unmarshaller.unmarshall(json,
                                                                         preprocessingData).getContents().get(0));
                PathFinder pfinder = null;
                if (selectionId != null && selectionId.length() > 0) {
                    // find the embedded, event subprocess
                    SubProcess selectedContainer = null;
                    List<RootElement> rootElements = def.getRootElements();
                    for (RootElement root : rootElements) {
                        if (root instanceof Process) {
                            Process process = (Process) root;
                            selectedContainer = findSelectedContainer(selectionId,
                                                                      process);
                            if (selectedContainer != null) {
                                pfinder = PathFinderFactory.getInstance(selectedContainer);
                            } else {
                                _logger.error("Could not find selected contaner with id: " + selectionId);
                            }
                        }
                    }
                }
                if (pfinder == null) {
                    pfinder = PathFinderFactory.getInstance(def);
                }
                JSONObject pathjson = pfinder.findPaths(new JSONPathFormatConverter());
                PrintWriter pw = resp.getWriter();
                resp.setContentType("text/plain");
                resp.setCharacterEncoding("UTF-8");
                pw.write(pathjson.toString());
            } catch (Exception e) {
                _logger.error("Error during path finding",
                              e);
                // need to return error code to use failure callback on client (js) side
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter pw = resp.getWriter();
                pw.write(e.getMessage());
            }
        } else if (action != null && action.equals(ACTION_RUNSIMULATION)) {
            try {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();

                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                Definitions def = ((Definitions) unmarshaller.unmarshall(json,
                                                                         preprocessingData).getContents().get(0));
                String processXML = profile.createMarshaller().parseModel(json,
                                                                          preprocessingData);
                // find the process id
                List<RootElement> rootElements = def.getRootElements();
                String processId = "";
                for (RootElement root : rootElements) {
                    if (root instanceof Process) {
                        processId = ((Process) root).getId();
                    }
                }

                int baseTimeUnit = 2; // default to minutes
                if (def.getRelationships() != null && def.getRelationships().size() > 0) {
                    Relationship relationship = def.getRelationships().get(0);
                    for (ExtensionAttributeValue extattrval : relationship.getExtensionValues()) {
                        FeatureMap extensionElements = extattrval.getValue();
                        @SuppressWarnings("unchecked")
                        List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>) extensionElements.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA,
                                                                                                          true);
                        if (bpsimExtensions != null && bpsimExtensions.size() > 0) {
                            BPSimDataType processAnalysis = bpsimExtensions.get(0);
                            if (processAnalysis.getScenario() != null && processAnalysis.getScenario().size() > 0) {
                                Scenario simulationScenario = processAnalysis.getScenario().get(0);
                                baseTimeUnit = simulationScenario.getScenarioParameters().getBaseTimeUnit().getValue();
                            }
                        }
                    }
                }

                if (numInstances == null || numInstances.length() < 1) {
                    numInstances = "1";
                }
                if (interval == null || interval.length() < 1) {
                    interval = "1";
                }
                if (intervalUnit == null || intervalUnit.length() < 1) {
                    intervalUnit = "seconds";
                }

                int intervalInt = Integer.parseInt(interval);
                if (intervalUnit.equals("seconds")) {
                    intervalInt = intervalInt * 1000;
                } else if (intervalUnit.equals("minutes")) {
                    intervalInt = intervalInt * 1000 * 60;
                } else if (intervalUnit.equals("hours")) {
                    intervalInt = intervalInt * 1000 * 60 * 60;
                } else if (intervalUnit.equals("days")) {
                    intervalInt = intervalInt * 1000 * 60 * 60 * 24;
                } else {
                    // default to milliseconds
                }

                this.eventAggregations = new ArrayList<SimulationEvent>();
                this.simTime = new DateTime();
                SimulationRepository repo = SimulationRunner.runSimulation(processId,
                                                                           processXML,
                                                                           Integer.parseInt(numInstances),
                                                                           intervalInt,
                                                                           true,
                                                                           "onevent.simulation.rules.drl");
                WorkingMemorySimulationRepository wmRepo = (WorkingMemorySimulationRepository) repo;
                // start evaluating all the simulation events generated
                // wmRepo.fireAllRules();
                List<SimulationEvent> allEvents = new ArrayList<SimulationEvent>(wmRepo.getEvents());
                wmRepo.getSession().execute(new InsertElementsCommand((Collection) wmRepo.getAggregatedEvents()));
                wmRepo.fireAllRules();
                List<AggregatedSimulationEvent> aggEvents = (List<AggregatedSimulationEvent>) wmRepo.getGlobal("summary");
                SimulationInfo simInfo = wmRepo.getSimulationInfo();
                wmRepo.close();

                Map<String, Double> numInstanceData = new HashMap<String, Double>();
                JSONObject parentJSON = new JSONObject();
                JSONArray simInfoJSONArray = new JSONArray();
                JSONArray aggProcessSimulationJSONArray = new JSONArray();
                JSONArray aggNumActivityInstancesJSONArray = new JSONArray();
                JSONArray aggHTSimulationJSONArray = new JSONArray();
                JSONArray aggTaskSimulationJSONArray = new JSONArray();

                JSONObject simInfoKeys = new JSONObject();
                simInfoKeys.put("id",
                                simInfo.getProcessId() == null ? "" : simInfo.getProcessId());
                simInfoKeys.put("name",
                                simInfo.getProcessName() == null ? "" : simInfo.getProcessName());
                simInfoKeys.put("executions",
                                simInfo.getNumberOfExecutions());
                SimpleDateFormat infoDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
                                                                       getLocale(language));
                long startTime = simInfo.getStartTime();
                if (simTestStartTime != null && simTestStartTime.length() > 0) {
                    startTime = Long.parseLong(simTestStartTime);
                }
                long endTime = simInfo.getEndTime();
                if (simTestEndTime != null && simTestEndTime.length() > 0) {
                    endTime = Long.parseLong(simTestEndTime);
                }
                String simStartStr = infoDateFormat.format(new Date(startTime));
                String simEndStr = infoDateFormat.format(new Date(endTime));
                simInfoKeys.put("starttime",
                                simStartStr);
                simInfoKeys.put("endtime",
                                simEndStr);
                simInfoKeys.put("version",
                                simInfo.getProcessVersion() == null ? "" : simInfo.getProcessVersion());
                simInfoKeys.put("interval",
                                presentInterval((int) simInfo.getInterval(),
                                                intervalUnit));
                simInfoJSONArray.put(simInfoKeys);

                for (AggregatedSimulationEvent aggEvent : aggEvents) {
                    if (aggEvent instanceof AggregatedProcessSimulationEvent) {
                        AggregatedProcessSimulationEvent event = (AggregatedProcessSimulationEvent) aggEvent;
                        JSONObject processSimKeys = new JSONObject();
                        processSimKeys.put("key",
                                           PROCESS_AVERAGES);
                        processSimKeys.put("id",
                                           event.getProcessId());
                        processSimKeys.put("name",
                                           event.getProcessName());
                        JSONArray processSimValues = new JSONArray();
                        JSONObject obj1 = new JSONObject();
                        obj1.put("label",
                                 MAX_EXECUTION_TIME);
                        obj1.put("value",
                                 adjustToBaseTimeUnit(event.getMaxExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj2 = new JSONObject();
                        obj2.put("label",
                                 MIN_EXECUTION_TIME);
                        obj2.put("value",
                                 adjustToBaseTimeUnit(event.getMinExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj3 = new JSONObject();
                        obj3.put("label",
                                 AVG_EXECUTION_TIME);
                        obj3.put("value",
                                 adjustToBaseTimeUnit(event.getAvgExecutionTime(),
                                                      baseTimeUnit));
                        processSimValues.put(obj1);
                        processSimValues.put(obj2);
                        processSimValues.put(obj3);
                        processSimKeys.put("values",
                                           processSimValues);
                        aggProcessSimulationJSONArray.put(processSimKeys);
                        // process paths
                        this.pathInfoMap = event.getPathNumberOfInstances();
                    } else if (aggEvent instanceof HTAggregatedSimulationEvent) {
                        HTAggregatedSimulationEvent event = (HTAggregatedSimulationEvent) aggEvent;
                        numInstanceData.put(event.getActivityName(),
                                            (double) event.getNumberOfInstances());
                        JSONObject allValues = new JSONObject();
                        JSONObject resourceValues = new JSONObject();
                        JSONObject costValues = new JSONObject();

                        allValues.put("key",
                                      HUMAN_TASK_AVERAGES);
                        allValues.put("id",
                                      event.getActivityId());
                        allValues.put("name",
                                      event.getActivityName());

                        JSONArray innerExecutionValues = new JSONArray();
                        JSONObject obj1 = new JSONObject();
                        obj1.put("label",
                                 MAX);
                        obj1.put("value",
                                 adjustToBaseTimeUnit(event.getMaxExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj2 = new JSONObject();
                        obj2.put("label",
                                 MIN);
                        obj2.put("value",
                                 adjustToBaseTimeUnit(event.getMinExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj3 = new JSONObject();
                        obj3.put("label",
                                 AVERAGE);
                        obj3.put("value",
                                 adjustToBaseTimeUnit(event.getAvgExecutionTime(),
                                                      baseTimeUnit));
                        innerExecutionValues.put(obj1);
                        innerExecutionValues.put(obj2);
                        innerExecutionValues.put(obj3);
                        JSONObject valuesObj = new JSONObject();
                        valuesObj.put("key",
                                      EXECUTION_TIMES);
                        valuesObj.put("color",
                                      "#1f77b4");
                        valuesObj.put("values",
                                      innerExecutionValues);

                        JSONArray innerExecutionValues2 = new JSONArray();
                        JSONObject obj4 = new JSONObject();
                        obj4.put("label",
                                 MAX);
                        obj4.put("value",
                                 adjustToBaseTimeUnit(event.getMaxWaitTime(),
                                                      baseTimeUnit));
                        JSONObject obj5 = new JSONObject();
                        obj5.put("label",
                                 MIN);
                        obj5.put("value",
                                 adjustToBaseTimeUnit(event.getMinWaitTime(),
                                                      baseTimeUnit));
                        JSONObject obj6 = new JSONObject();
                        obj6.put("label",
                                 AVERAGE);
                        obj6.put("value",
                                 adjustToBaseTimeUnit(event.getAvgWaitTime(),
                                                      baseTimeUnit));
                        innerExecutionValues2.put(obj4);
                        innerExecutionValues2.put(obj5);
                        innerExecutionValues2.put(obj6);
                        JSONObject valuesObj2 = new JSONObject();
                        valuesObj2.put("key",
                                       WAIT_TIMES);
                        valuesObj2.put("color",
                                       "#d62728");
                        valuesObj2.put("values",
                                       innerExecutionValues2);

                        JSONArray timeValuesInner = new JSONArray();
                        timeValuesInner.put(valuesObj);
                        timeValuesInner.put(valuesObj2);
                        allValues.put("timevalues",
                                      timeValuesInner);

                        resourceValues.put("key",
                                           RESOURCE_ALLOCATIONS);
                        resourceValues.put("id",
                                           event.getActivityId());
                        resourceValues.put("name",
                                           event.getActivityName());
                        JSONArray htSimValues2 = new JSONArray();
                        JSONObject obj7 = new JSONObject();
                        obj7.put("label",
                                 MAX);
                        obj7.put("value",
                                 adjustDouble(event.getMaxResourceUtilization()));
                        JSONObject obj8 = new JSONObject();
                        obj8.put("label",
                                 MIN);
                        obj8.put("value",
                                 adjustDouble(event.getMinResourceUtilization()));
                        JSONObject obj9 = new JSONObject();
                        obj9.put("label",
                                 AVERAGE);
                        obj9.put("value",
                                 adjustDouble(event.getAvgResourceUtilization()));
                        htSimValues2.put(obj7);
                        htSimValues2.put(obj8);
                        htSimValues2.put(obj9);
                        resourceValues.put("values",
                                           htSimValues2);
                        allValues.put("resourcevalues",
                                      resourceValues);

                        costValues.put("key",
                                       RESOURCE_COST);
                        costValues.put("id",
                                       event.getActivityId());
                        costValues.put("name",
                                       event.getActivityName());
                        JSONArray htSimValues3 = new JSONArray();
                        JSONObject obj10 = new JSONObject();
                        obj10.put("label",
                                  MAX);
                        obj10.put("value",
                                  adjustDouble(event.getMaxResourceCost()));
                        JSONObject obj11 = new JSONObject();
                        obj11.put("label",
                                  MIN);
                        obj11.put("value",
                                  adjustDouble(event.getMinResourceCost()));
                        JSONObject obj12 = new JSONObject();
                        obj12.put("label",
                                  AVERAGE);
                        obj12.put("value",
                                  adjustDouble(event.getAvgResourceCost()));
                        htSimValues3.put(obj10);
                        htSimValues3.put(obj11);
                        htSimValues3.put(obj12);
                        costValues.put("values",
                                       htSimValues3);
                        allValues.put("costvalues",
                                      costValues);

                        // single events
//                        JSONObject taskEvents = getTaskEventsFromAllEvents(event, allEvents);
//                        if(taskEvents != null) {
//                            allValues.put("timeline", taskEvents);
//                            aggHTSimulationJSONArray.put(allValues);
//                        }
                        aggHTSimulationJSONArray.put(allValues);
                    } else if (aggEvent instanceof AggregatedActivitySimulationEvent) {
                        AggregatedActivitySimulationEvent event = (AggregatedActivitySimulationEvent) aggEvent;
                        numInstanceData.put(event.getActivityName(),
                                            new Long(event.getNumberOfInstances()).doubleValue());

                        JSONObject taskSimKeys = new JSONObject();
                        taskSimKeys.put("key",
                                        TASK_AVERAGES);
                        taskSimKeys.put("id",
                                        event.getActivityId());
                        taskSimKeys.put("name",
                                        event.getActivityName());
                        JSONArray taskSimValues = new JSONArray();
                        JSONObject obj1 = new JSONObject();
                        obj1.put("label",
                                 MAX_EXECUTION_TIME);
                        obj1.put("value",
                                 adjustToBaseTimeUnit(event.getMaxExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj2 = new JSONObject();
                        obj2.put("label",
                                 MIN_EXECUTION_TIME);
                        obj2.put("value",
                                 adjustToBaseTimeUnit(event.getMinExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj3 = new JSONObject();
                        obj3.put("label",
                                 AVG_EXECUTION_TIME);
                        obj3.put("value",
                                 adjustToBaseTimeUnit(event.getAvgExecutionTime(),
                                                      baseTimeUnit));
                        taskSimValues.put(obj1);
                        taskSimValues.put(obj2);
                        taskSimValues.put(obj3);
                        taskSimKeys.put("values",
                                        taskSimValues);
                        // single events
//                        JSONObject taskEvents = getTaskEventsFromAllEvents(event, allEvents);
//                        if(taskEvents != null) {
//                            taskSimKeys.put("timeline", taskEvents);
//                        }
                        aggTaskSimulationJSONArray.put(taskSimKeys);
                    }
                }

                JSONObject numInstancesSimKeys = new JSONObject();
                numInstancesSimKeys.put("key",
                                        ACTIVITY_INSTANCES);
                numInstancesSimKeys.put("id",
                                        ACTIVITY_INSTANCES);
                numInstancesSimKeys.put("name",
                                        ACTIVITY_INSTANCES);
                JSONArray numInstancesValues = new JSONArray();
                Iterator<String> iter = numInstanceData.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Double value = numInstanceData.get(key);
                    JSONObject entryObject = new JSONObject();
                    entryObject.put("label",
                                    key);
                    entryObject.put("value",
                                    value);
                    numInstancesValues.put(entryObject);
                }
                numInstancesSimKeys.put("values",
                                        numInstancesValues);
                aggNumActivityInstancesJSONArray.put(numInstancesSimKeys);

                parentJSON.put("siminfo",
                               simInfoJSONArray);
                parentJSON.put("processsim",
                               aggProcessSimulationJSONArray);
                parentJSON.put("activityinstances",
                               aggNumActivityInstancesJSONArray);
                parentJSON.put("htsim",
                               aggHTSimulationJSONArray);
                parentJSON.put("tasksim",
                               aggTaskSimulationJSONArray);
                parentJSON.put("timeline",
                               getTaskEventsFromAllEvents(null,
                                                          allEvents,
                                                          intervalUnit,
                                                          req.getContextPath()));
                // event aggregations
                JSONArray aggEventProcessSimulationJSONArray = new JSONArray();
                int c = 0;
                for (SimulationEvent simEve : this.eventAggregations) {
                    AggregatedProcessSimulationEvent aggProcessEve = (AggregatedProcessSimulationEvent) (((GenericSimulationEvent) simEve).getAggregatedEvent());
                    if (aggProcessEve != null) {
                        JSONObject eventProcessSimKeys = new JSONObject();
                        eventProcessSimKeys.put("key",
                                                PROCESS_AVERAGES);
                        eventProcessSimKeys.put("id",
                                                aggProcessEve.getProcessId());
                        eventProcessSimKeys.put("name",
                                                aggProcessEve.getProcessName());
                        eventProcessSimKeys.put("timesincestart",
                                                this.eventAggregationsTimes.get(c));
                        eventProcessSimKeys.put("timeunit",
                                                intervalUnit);
                        JSONArray eventProcessSimValues = new JSONArray();
                        JSONObject obj1 = new JSONObject();
                        obj1.put("label",
                                 MAX_EXECUTION_TIME);
                        obj1.put("value",
                                 adjustToBaseTimeUnit(aggProcessEve.getMaxExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj2 = new JSONObject();
                        obj2.put("label",
                                 MIN_EXECUTION_TIME);
                        obj2.put("value",
                                 adjustToBaseTimeUnit(aggProcessEve.getMinExecutionTime(),
                                                      baseTimeUnit));
                        JSONObject obj3 = new JSONObject();
                        obj3.put("label",
                                 AVG_EXECUTION_TIME);
                        obj3.put("value",
                                 adjustToBaseTimeUnit(aggProcessEve.getAvgExecutionTime(),
                                                      baseTimeUnit));
                        eventProcessSimValues.put(obj1);
                        eventProcessSimValues.put(obj2);
                        eventProcessSimValues.put(obj3);
                        eventProcessSimKeys.put("values",
                                                eventProcessSimValues);
                        aggEventProcessSimulationJSONArray.put(eventProcessSimKeys);
                        c++;
                    }
                }
                parentJSON.put("eventaggregations",
                               aggEventProcessSimulationJSONArray);
                // process paths
                JSONArray processPathsJSONArray = new JSONArray();
                if (this.pathInfoMap != null) {
                    Iterator<String> pathKeys = this.pathInfoMap.keySet().iterator();
                    while (pathKeys.hasNext()) {
                        JSONObject pathsSimKeys = new JSONObject();
                        String pkey = pathKeys.next();
                        Integer pvalue = this.pathInfoMap.get(pkey);
                        pathsSimKeys.put("id",
                                         pkey);
                        pathsSimKeys.put("numinstances",
                                         pvalue);
                        pathsSimKeys.put("totalinstances",
                                         Integer.parseInt(numInstances));
                        processPathsJSONArray.put(pathsSimKeys);
                    }
                    parentJSON.put("pathsim",
                                   processPathsJSONArray);
                }

                PrintWriter pw = resp.getWriter();
                resp.setContentType("text/json");
                resp.setCharacterEncoding("UTF-8");
                pw.write(Base64.encodeBase64String(UriUtils.encode(parentJSON.toString()).getBytes("UTF-8")));
            } catch (Exception e) {
                _logger.error("Error during simulation",
                              e);
                // need to return error code to use failure callback on client (js) side
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter pw = resp.getWriter();
                pw.write(e.getMessage());
            } catch (Throwable t) {
                _logger.error("Error during simulation",
                              t);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter pw = resp.getWriter();
                pw.write("showinvalid");
            }
        }
    }

    private double adjustToSecs(double in) throws ParseException {
        if (in > 0) {
            in = in / 1000;
        }
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        String formattedValue = twoDForm.format(in);
        return twoDForm.parse(formattedValue).doubleValue();
    }

    private double adjustToBaseTimeUnit(double in,
                                        int baseTime) throws ParseException {
        if (in > 0) {
            if (baseTime == 1) {
                in = in / 1000;
            } else if (baseTime == 2) {
                in = in / (1000 * 60);
            } else if (baseTime == 3) {
                in = in / (1000 * 60 * 60);
            } else if (baseTime == 4) {
                in = in / (1000 * 60 * 60 * 24);
            } else if (baseTime == 5) {
                in = in / (1000 * 60 * 60 * 24 * 365);
            }
        }

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        String formattedValue = twoDForm.format(in);
        return twoDForm.parse(formattedValue).doubleValue();
    }

    private double adjustDouble(double in) throws ParseException {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        String formattedValue = twoDForm.format(in);
        return twoDForm.parse(formattedValue).doubleValue();
    }

    private SubProcess findSelectedContainer(String id,
                                             FlowElementsContainer container) {
        if (container instanceof SubProcess && container.getId().equals(id)) {
            return (SubProcess) container;
        } else {
            for (FlowElement fe : container.getFlowElements()) {
                if (fe instanceof SubProcess) {
                    if (fe.getId().equals(id)) {
                        return (SubProcess) fe;
                    } else {
                        return findSelectedContainer(id,
                                                     (FlowElementsContainer) fe);
                    }
                }
            }
        }
        return null;
    }

    private String getEventName(SimulationEvent se) {
        if (se != null) {
            if (se instanceof ActivitySimulationEvent) {
                return "Activity";
            } else if (se instanceof EndSimulationEvent) {
                return "End Event";
            } else if (se instanceof GatewaySimulationEvent) {
                return "Gateway";
            } else if (se instanceof HumanTaskActivitySimulationEvent) {
                return "Human Task";
            } else if (se instanceof StartSimulationEvent) {
                return "Start Event";
            } else {
                return "Event";
            }
        } else {
            return "Event";
        }
    }

    private String getDateString(long seDate) {
        Date d = new Date(seDate);
        DateTime dt = new DateTime(seDate);
        StringBuffer retBuf = new StringBuffer();
        retBuf.append(dt.getYear()).append(",");
        retBuf.append(dt.getMonthOfYear()).append(",");
        retBuf.append(dt.getDayOfMonth()).append(",");
        retBuf.append(dt.getHourOfDay()).append(",");
        retBuf.append(dt.getMinuteOfHour()).append(",");
        retBuf.append(dt.getSecondOfMinute()).append(",");
        retBuf.append(dt.getMillisOfSecond());
        return retBuf.toString();
    }

    private String getIcon(SimulationEvent se,
                           String contextPath) {
        if (se != null) {
            if (se instanceof ActivitySimulationEvent) {
                return contextPath + "/org.jbpm.designer.jBPMDesigner/images/simulation/timeline/activity.png";
            } else if (se instanceof EndSimulationEvent) {
                return contextPath + "/org.jbpm.designer.jBPMDesigner/images/simulation/timeline/endevent.png";
            } else if (se instanceof GatewaySimulationEvent) {
                return contextPath + "/org.jbpm.designer.jBPMDesigner/images/simulation/timeline/gateway.png";
            } else if (se instanceof HumanTaskActivitySimulationEvent) {
                return contextPath + "/org.jbpm.designer.jBPMDesigner/images/simulation/timeline/humantask.png";
            } else if (se instanceof StartSimulationEvent) {
                return contextPath + "/org.jbpm.designer.jBPMDesigner/images/simulation/timeline/startevent.png";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private JSONObject getTaskEventsFromAllEvents(AggregatedSimulationEvent event,
                                                  List<SimulationEvent> allEvents,
                                                  String intervalUnit,
                                                  String contextPath) throws Exception {
        JSONObject allEventsObject = new JSONObject();
        allEventsObject.put("headline",
                            SIMULATION_EVENTS);
        allEventsObject.put("type",
                            "default");
        allEventsObject.put("text",
                            SIMULATION_EVENTS);
        JSONArray allEventsDataArray = new JSONArray();
        for (SimulationEvent se : allEvents) {
            // for now only include end and activity events
            if ((se instanceof EndSimulationEvent) || (se instanceof ActivitySimulationEvent) || (se instanceof HumanTaskActivitySimulationEvent)) {
                if (event != null) {
                    String seActivityId = getSingleEventActivityId(se);
                    String eventActivitytId = getAggregatedEventActivityId(event);
                    if (eventActivitytId.equals(seActivityId)) {
                        allEventsDataArray.put(getTimelineEventObject(se,
                                                                      intervalUnit,
                                                                      contextPath));
                    }
                } else {
                    allEventsDataArray.put(getTimelineEventObject(se,
                                                                  intervalUnit,
                                                                  contextPath));
                }
            }
        }
        allEventsObject.put("date",
                            allEventsDataArray);
        // sort the time values
        Collections.sort(this.eventAggregationsTimes);
        return allEventsObject;
    }

    private JSONObject getTimelineEventObject(SimulationEvent se,
                                              String intervalUnit,
                                              String contextPath) throws Exception {
        JSONObject seObject = new JSONObject();
        seObject.put("id",
                     se.getUUID().toString());
        seObject.put("startDate",
                     getDateString(se.getStartTime()));
        seObject.put("endDate",
                     getDateString(se.getEndTime()));
        if (se instanceof EndSimulationEvent) {
            seObject.put("headline",
                         ((EndSimulationEvent) se).getActivityName());
            seObject.put("activityid",
                         ((EndSimulationEvent) se).getActivityId());
        } else if (se instanceof ActivitySimulationEvent) {
            seObject.put("headline",
                         ((ActivitySimulationEvent) se).getActivityName());
            seObject.put("activityid",
                         ((ActivitySimulationEvent) se).getActivityId());
        } else if (se instanceof HumanTaskActivitySimulationEvent) {
            seObject.put("headline",
                         ((HumanTaskActivitySimulationEvent) se).getActivityName());
            seObject.put("activityid",
                         ((HumanTaskActivitySimulationEvent) se).getActivityId());
        }
        seObject.put("text",
                     "");
        seObject.put("tag",
                     "");
        JSONObject seAsset = new JSONObject();
        seAsset.put("media",
                    "");
        seAsset.put("thumbnail",
                    getIcon(se,
                            contextPath));
        seAsset.put("credit",
                    "");
        seAsset.put("caption",
                    "");
        seObject.put("asset",
                     seAsset);

        // add aggregated events as well
        this.eventAggregations.add(se);
        Interval eventinterval = new Interval(this.simTime.getMillis(),
                                              se.getEndTime());

        long durationvalue = eventinterval.toDurationMillis();
        if (intervalUnit.equals("seconds")) {
            durationvalue = durationvalue / 1000;
        } else if (intervalUnit.equals("minutes")) {
            durationvalue = durationvalue / (1000 * 60);
        } else if (intervalUnit.equals("hours")) {
            durationvalue = durationvalue / (1000 * 60 * 60);
        } else if (intervalUnit.equals("days")) {
            durationvalue = durationvalue / (1000 * 60 * 60 * 24);
        } else {
            // default to milliseconds
        }

        this.eventAggregationsTimes.add(durationvalue);
        return seObject;
    }

    private String getSingleEventActivityId(SimulationEvent event) {
        if (event != null) {
            if (event instanceof ActivitySimulationEvent) {
                return ((ActivitySimulationEvent) event).getActivityId();
            } else if (event instanceof EndSimulationEvent) {
                return ((EndSimulationEvent) event).getActivityId();
            } else if (event instanceof GatewaySimulationEvent) {
                return ((GatewaySimulationEvent) event).getActivityId();
            } else if (event instanceof HumanTaskActivitySimulationEvent) {
                return ((HumanTaskActivitySimulationEvent) event).getActivityId();
            } else if (event instanceof StartSimulationEvent) {
                return ((StartSimulationEvent) event).getActivityId();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private String getAggregatedEventActivityId(AggregatedSimulationEvent event) {
        if (event instanceof AggregatedProcessSimulationEvent) {
            return ((AggregatedProcessSimulationEvent) event).getProcessId();
        } else if (event instanceof HTAggregatedSimulationEvent) {
            return ((HTAggregatedSimulationEvent) event).getActivityId();
        } else if (event instanceof AggregatedActivitySimulationEvent) {
            return ((AggregatedActivitySimulationEvent) event).getActivityId();
        } else {
            return "";
        }
    }

    private String presentInterval(int interval,
                                   String intervalUnit) {
        String retVal;
        if (intervalUnit.equals("seconds")) {
            interval = interval / 1000;
            retVal = interval + " seconds";
        } else if (intervalUnit.equals("minutes")) {
            interval = interval / (1000 * 60);
            retVal = interval + " minutes";
        } else if (intervalUnit.equals("hours")) {
            interval = interval / (1000 * 60 * 60);
            retVal = interval + " hours";
        } else if (intervalUnit.equals("days")) {
            interval = interval / (1000 * 60 * 60 * 24);
            retVal = interval + " days";
        } else {
            retVal = interval + " milliseconds";
        }
        return retVal;
    }

    private Locale getLocale(String language) {
        if (language != null && language.length() > 0) {
            int iUnderscore = language.indexOf('_');
            if (iUnderscore > 0) {
                String lang = language.substring(0,
                                                 iUnderscore);
                String region = language.substring(iUnderscore + 1).toUpperCase();
                return new Locale(lang,
                                  region);
            } else {
                return new Locale(language);
            }
        } else {
            return Locale.getDefault();
        }
    }
}


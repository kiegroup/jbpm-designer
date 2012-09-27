package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SubProcess;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.simulation.AggregatedSimulationEvent;
import org.jbpm.simulation.PathFinder;
import org.jbpm.simulation.PathFinderFactory;
import org.jbpm.simulation.SimulationEvent;
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

/**
 * Servlet for simulation actions.
 * 
 * @author Tihomir Surdilovic
 */
public class SimulationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(SimulationServlet.class);
	private static final String ACTION_GETPATHINFO = "getpathinfo";
	private static final String ACTION_RUNSIMULATION = "runsimulation";
	private ServletConfig config;
	private List<SimulationEvent> eventAggregations = new ArrayList<SimulationEvent>();
	private List<Long> eventAggregationsTimes = new ArrayList<Long>();
	private Map<String, Integer> pathInfoMap = null;
	private DateTime simTime = null;
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String profileName = req.getParameter("profile");
		String json = req.getParameter("json");
		String action = req.getParameter("action");
		String preprocessingData = req.getParameter("ppdata");
		String selectionId = req.getParameter("sel");
		String numInstances = req.getParameter("numinstances");
		String interval = req.getParameter("interval");
		String intervalUnit = req.getParameter("intervalunit");
		
		IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        
        if(action != null && action.equals(ACTION_GETPATHINFO)) {
        	DroolsFactoryImpl.init();
        	Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
            Definitions def = ((Definitions) unmarshaller.unmarshall(json, preprocessingData).getContents().get(0));
            PathFinder pfinder = null;
            if(selectionId != null && selectionId.length() > 0) {
            	// find the embedded subprocess
            	SubProcess selectedContainer = null;
            	List<RootElement> rootElements =  def.getRootElements();
                for(RootElement root : rootElements) {
                	if(root instanceof Process) {
                		Process process = (Process) root;
                		selectedContainer = findSelectedContainer(selectionId, process);
                		if(selectedContainer != null) {
                        	pfinder = PathFinderFactory.getInstance(selectedContainer);
                        }
                		else {
                        	_logger.error("Could not find selected contaner with id: " + selectionId);
                        }
                	}
                }
            } 
            if(pfinder == null) {
            	pfinder = PathFinderFactory.getInstance(def);
            }
            JSONObject pathjson =  pfinder.findPaths(new JSONPathFormatConverter());
            PrintWriter pw = resp.getWriter();
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			pw.write(pathjson.toString());
        } else if(action != null && action.equals(ACTION_RUNSIMULATION)) {
        	try {
				DroolsFactoryImpl.init();
				Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
				Definitions def = ((Definitions) unmarshaller.unmarshall(json, preprocessingData).getContents().get(0));
				String processXML = profile.createMarshaller().parseModel(json, preprocessingData);
				// find the process id
				List<RootElement> rootElements =  def.getRootElements();
				String processId = "";
				for(RootElement root : rootElements) {
					if(root instanceof Process) {
						processId = ((Process) root).getId();
					}
				}
				
				if(numInstances == null || numInstances.length() < 1) {
					numInstances = "1";
				}
				if(interval == null || interval.length() < 1) {
					interval = "1";
				}
				if(intervalUnit == null || intervalUnit.length() < 1) {
					intervalUnit = "seconds";
				}
				
				int intervalInt = Integer.parseInt(interval);
				if(intervalUnit.equals("seconds")) {
					intervalInt = intervalInt*1000;
				} else if(intervalUnit.equals("minutes")) {
					intervalInt = intervalInt*1000*60;
				} else if(intervalUnit.equals("hours")) {
					intervalInt = intervalInt*1000*60*60;
				} else if(intervalUnit.equals("days")) {
					intervalInt = intervalInt*1000*60*60*24;
				} else {
					// default to milliseconds
				}

				this.eventAggregations = new ArrayList<SimulationEvent>();
				this.simTime = new DateTime();
				SimulationRepository repo = SimulationRunner.runSimulation(processId, processXML, Integer.parseInt(numInstances), intervalInt, true, "onevent.simulation.rules.drl");
				WorkingMemorySimulationRepository wmRepo = (WorkingMemorySimulationRepository) repo;
				// start evaluating all the simulation events generated
				// wmRepo.fireAllRules();
				List<SimulationEvent> allEvents = new ArrayList<SimulationEvent>(wmRepo.getEvents());
				wmRepo.getSession().execute(new InsertElementsCommand((Collection)wmRepo.getAggregatedEvents()));
		        wmRepo.fireAllRules();
		        List<AggregatedSimulationEvent> aggEvents = (List<AggregatedSimulationEvent>) wmRepo.getGlobal("summary");
				wmRepo.close();
				
				Map<String, Double> numInstanceData = new HashMap<String, Double>();
				JSONObject parentJSON = new JSONObject();
				JSONArray aggProcessSimulationJSONArray = new JSONArray();
				JSONArray aggNumActivityInstancesJSONArray = new JSONArray();
				JSONArray aggHTSimulationJSONArray = new JSONArray();
				JSONArray aggTaskSimulationJSONArray = new JSONArray();
				for(AggregatedSimulationEvent aggEvent : aggEvents) {
					if(aggEvent instanceof AggregatedProcessSimulationEvent) {
						AggregatedProcessSimulationEvent event = (AggregatedProcessSimulationEvent) aggEvent;
						JSONObject processSimKeys = new JSONObject();
						processSimKeys.put("key", "Process Avarages");
						processSimKeys.put("id", event.getProcessId());
						processSimKeys.put("name", event.getProcessName());
						JSONArray processSimValues = new JSONArray();
						JSONObject obj1 = new JSONObject();
						obj1.put("label", "Max Execution Time");
						obj1.put("value", adjustToMins(event.getMaxExecutionTime()));
						JSONObject obj2 = new JSONObject();
						obj2.put("label", "Min Execution Time");
						obj2.put("value", adjustToMins(event.getMinExecutionTime()));
						JSONObject obj3 = new JSONObject();
						obj3.put("label", "Avg. Execution Time");
						obj3.put("value", adjustToMins(event.getAvgExecutionTime()));
						processSimValues.put(obj1);
						processSimValues.put(obj2);
						processSimValues.put(obj3);
						processSimKeys.put("values", processSimValues);
						aggProcessSimulationJSONArray.put(processSimKeys);
						// process paths
						this.pathInfoMap = event.getPathNumberOfInstances();
					} else if(aggEvent instanceof HTAggregatedSimulationEvent) {
						HTAggregatedSimulationEvent event = (HTAggregatedSimulationEvent) aggEvent;
						numInstanceData.put(event.getActivityName(), new Long(event.getNumberOfInstances()).doubleValue());
						JSONObject allValues = new JSONObject();
						JSONObject resourceValues = new JSONObject();
						JSONObject costValues = new JSONObject();
						
						allValues.put("key", "Human Task Avarages");
						allValues.put("id", event.getActivityId());
						allValues.put("name", event.getActivityName());
						
						JSONArray innerExecutionValues = new JSONArray();
						JSONObject obj1 = new JSONObject();
						obj1.put("label", "Max");
						obj1.put("value", adjustToMins(event.getMaxExecutionTime()));
						JSONObject obj2 = new JSONObject();
						obj2.put("label", "Min");
						obj2.put("value", adjustToMins(event.getMinExecutionTime()));
						JSONObject obj3 = new JSONObject();
						obj3.put("label", "Average");
						obj3.put("value", adjustToMins(event.getAvgExecutionTime()));
						innerExecutionValues.put(obj1);
						innerExecutionValues.put(obj2);
						innerExecutionValues.put(obj3);
						JSONObject valuesObj = new JSONObject();
						valuesObj.put("key", "Execution Times");
						valuesObj.put("color", "#1f77b4");
						valuesObj.put("values", innerExecutionValues);
						
						JSONArray innerExecutionValues2 = new JSONArray();
						JSONObject obj4 = new JSONObject();
						obj4.put("label", "Max");
						obj4.put("value", adjustToMins(event.getMaxWaitTime()));
						JSONObject obj5 = new JSONObject();
						obj5.put("label", "Min");
						obj5.put("value", adjustToMins(event.getMinWaitTime()));
						JSONObject obj6 = new JSONObject();
						obj6.put("label", "Average");
						obj6.put("value", adjustToMins(event.getAvgWaitTime()));
						innerExecutionValues2.put(obj4);
						innerExecutionValues2.put(obj5);
						innerExecutionValues2.put(obj6);
						JSONObject valuesObj2 = new JSONObject();
						valuesObj2.put("key", "Wait Times");
						valuesObj2.put("color", "#d62728");
						valuesObj2.put("values", innerExecutionValues2);
						
						
						JSONArray timeValuesInner = new JSONArray();
						timeValuesInner.put(valuesObj);
						timeValuesInner.put(valuesObj2);
						allValues.put("timevalues", timeValuesInner);
						
						resourceValues.put("key", "Resource Allocations");
						resourceValues.put("id", event.getActivityId());
						resourceValues.put("name", event.getActivityName());
						JSONArray htSimValues2 = new JSONArray();
						JSONObject obj7 = new JSONObject();
						obj7.put("label", "Max");
						obj7.put("value", adjustDouble(event.getMaxResourceUtilization()));
						JSONObject obj8 = new JSONObject();
						obj8.put("label", "Min");
						obj8.put("value", adjustDouble(event.getMinResourceUtilization()));
						JSONObject obj9 = new JSONObject();
						obj9.put("label", "Average");
						obj9.put("value", adjustDouble(event.getAvgResourceUtilization()));
						htSimValues2.put(obj7);
						htSimValues2.put(obj8);
						htSimValues2.put(obj9);
						resourceValues.put("values", htSimValues2);
						allValues.put("resourcevalues", resourceValues);
						
						
						costValues.put("key", "Resource Cost");
						costValues.put("id", event.getActivityId());
						costValues.put("name", event.getActivityName());
						JSONArray htSimValues3 = new JSONArray();
						JSONObject obj10 = new JSONObject();
						obj10.put("label", "Max");
						obj10.put("value", adjustDouble(event.getMaxResourceCost()));
						JSONObject obj11 = new JSONObject();
						obj11.put("label", "Min");
						obj11.put("value", adjustDouble(event.getMinResourceCost()));
						JSONObject obj12 = new JSONObject();
						obj12.put("label", "Average");
						obj12.put("value", adjustDouble(event.getAvgResourceCost()));
						htSimValues3.put(obj10);
						htSimValues3.put(obj11);
						htSimValues3.put(obj12);
						costValues.put("values", htSimValues3);
						allValues.put("costvalues", costValues);
						
						// single events
//						JSONObject taskEvents = getTaskEventsFromAllEvents(event, allEvents);
//						if(taskEvents != null) {
//							allValues.put("timeline", taskEvents);
//							aggHTSimulationJSONArray.put(allValues);
//						}
						aggHTSimulationJSONArray.put(allValues);
					} else if(aggEvent instanceof AggregatedActivitySimulationEvent) {
						AggregatedActivitySimulationEvent event = (AggregatedActivitySimulationEvent) aggEvent;
						numInstanceData.put(event.getActivityName(), new Long(event.getNumberOfInstances()).doubleValue());
						
						JSONObject taskSimKeys = new JSONObject();
						taskSimKeys.put("key", "Task Avarages");
						taskSimKeys.put("id", event.getActivityId());
						taskSimKeys.put("name", event.getActivityName());
						JSONArray taskSimValues = new JSONArray();
						JSONObject obj1 = new JSONObject();
						obj1.put("label", "Max. Execution Time");
						obj1.put("value", adjustToMins(event.getMaxExecutionTime()));
						JSONObject obj2 = new JSONObject();
						obj2.put("label", "Min. Execution Time");
						obj2.put("value", adjustToMins(event.getMinExecutionTime()));
						JSONObject obj3 = new JSONObject();
						obj3.put("label", "Avg. Execution Time");
						obj3.put("value", adjustToMins(event.getAvgExecutionTime()));
						taskSimValues.put(obj1);
						taskSimValues.put(obj2);
						taskSimValues.put(obj3);
						taskSimKeys.put("values", taskSimValues);
						// single events
//						JSONObject taskEvents = getTaskEventsFromAllEvents(event, allEvents);
//						if(taskEvents != null) {
//							taskSimKeys.put("timeline", taskEvents);
//						}
						aggTaskSimulationJSONArray.put(taskSimKeys);
					}
				}
				
				JSONObject numInstancesSimKeys = new JSONObject();
				numInstancesSimKeys.put("key", "Activity Instances");
				numInstancesSimKeys.put("id", "Activity Instances");
				numInstancesSimKeys.put("name", "Activity Instances");
				JSONArray numInstancesValues = new JSONArray();
				Iterator<String> iter = numInstanceData.keySet().iterator();
				while(iter.hasNext()) {
					String key = iter.next();
					Double value = numInstanceData.get(key);
					JSONObject entryObject = new JSONObject();
					entryObject.put("label", key);
					entryObject.put("value", value);
					numInstancesValues.put(entryObject);
				}
				numInstancesSimKeys.put("values", numInstancesValues);
				aggNumActivityInstancesJSONArray.put(numInstancesSimKeys);
				
				parentJSON.put("processsim", aggProcessSimulationJSONArray);
				parentJSON.put("activityinstances", aggNumActivityInstancesJSONArray);
				parentJSON.put("htsim", aggHTSimulationJSONArray);
				parentJSON.put("tasksim", aggTaskSimulationJSONArray);
				parentJSON.put("timeline", getTaskEventsFromAllEvents(null, allEvents, intervalUnit));
				// event aggregations
				JSONArray aggEventProcessSimulationJSONArray = new JSONArray();
				int c = 0;
				for(SimulationEvent simEve : this.eventAggregations) {
					AggregatedProcessSimulationEvent aggProcessEve = (AggregatedProcessSimulationEvent) (((GenericSimulationEvent) simEve).getAggregatedEvent());
					JSONObject eventProcessSimKeys = new JSONObject();
					eventProcessSimKeys.put("key", "Process Avarages");
					eventProcessSimKeys.put("id", aggProcessEve.getProcessId());
					eventProcessSimKeys.put("name", aggProcessEve.getProcessName());
					eventProcessSimKeys.put("timesincestart", this.eventAggregationsTimes.get(c));
					eventProcessSimKeys.put("timeunit", intervalUnit);
					JSONArray eventProcessSimValues = new JSONArray();
					JSONObject obj1 = new JSONObject();
					obj1.put("label", "Max Execution Time");
					obj1.put("value", adjustToMins(aggProcessEve.getMaxExecutionTime()));
					JSONObject obj2 = new JSONObject();
					obj2.put("label", "Min Execution Time");
					obj2.put("value", adjustToMins(aggProcessEve.getMinExecutionTime()));
					JSONObject obj3 = new JSONObject();
					obj3.put("label", "Avg. Execution Time");
					obj3.put("value", adjustToMins(aggProcessEve.getAvgExecutionTime()));
					eventProcessSimValues.put(obj1);
					eventProcessSimValues.put(obj2);
					eventProcessSimValues.put(obj3);
					eventProcessSimKeys.put("values", eventProcessSimValues);
					aggEventProcessSimulationJSONArray.put(eventProcessSimKeys);
					c++;
				}
				parentJSON.put("eventaggregations", aggEventProcessSimulationJSONArray);
				// process paths
				JSONArray processPathsJSONArray = new JSONArray();
				if(this.pathInfoMap != null) {
					Iterator<String> pathKeys =  this.pathInfoMap.keySet().iterator();
					while(pathKeys.hasNext()) {
						JSONObject pathsSimKeys = new JSONObject();
						String pkey = pathKeys.next();
						Integer pvalue = this.pathInfoMap.get(pkey);
						pathsSimKeys.put("id", pkey);
						pathsSimKeys.put("numinstances", pvalue);
						pathsSimKeys.put("totalinstances", Integer.parseInt(numInstances));
						processPathsJSONArray.put(pathsSimKeys);
					}
					parentJSON.put("pathsim", processPathsJSONArray);
				}

				PrintWriter pw = resp.getWriter();
	    		resp.setContentType("text/json");
	    		resp.setCharacterEncoding("UTF-8");
	    		pw.write(parentJSON.toString());
			} catch (Exception e) {
				PrintWriter pw = resp.getWriter();
	    		resp.setContentType("text/json");
	    		resp.setCharacterEncoding("UTF-8");
	    		pw.write("{}");
			}
            
        }
	}
	
	private double adjustToSecs(double in) {
		if(in > 0) {
			in = in / 1000;
		}
		DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(in));
	}
	
	private double adjustToMins(double in) {
		if(in > 0) {
			in = in / (1000 * 60);
		}
		DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(in));
	}
	
	private double adjustDouble(double in) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(in));
	}
	
	private SubProcess findSelectedContainer(String id, FlowElementsContainer container) {
		if(container instanceof SubProcess && container.getId().equals(id)) {
			return (SubProcess) container;
		} else {
			for(FlowElement fe : container.getFlowElements()) {
				if(fe instanceof SubProcess) {
					if(fe.getId().equals(id)) {
						return (SubProcess) fe;
					} else {
						return findSelectedContainer(id, (FlowElementsContainer) fe);
					}
				}
			}
		}
		return null;
	}
	
	private String getEventName(SimulationEvent se) {
		if(se != null) {
			if(se instanceof ActivitySimulationEvent) {
				return "Activity";
			} else if(se instanceof EndSimulationEvent) {
				return "End Event";
			} else if(se instanceof GatewaySimulationEvent) {
				return "Gateway";
			} else if(se instanceof HumanTaskActivitySimulationEvent) {
				return "Human Task";
			} else if(se instanceof StartSimulationEvent) {
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
	
	private String getIcon(SimulationEvent se) {
		if(se != null) {
			if(se instanceof ActivitySimulationEvent) {
				return "/designer/images/simulation/timeline/activity.png";
			} else if(se instanceof EndSimulationEvent) {
				return "/designer/images/simulation/timeline/endevent.png";
			} else if(se instanceof GatewaySimulationEvent) {
				return "/designer/images/simulation/timeline/gateway.png";
			} else if(se instanceof HumanTaskActivitySimulationEvent) {
				return "/designer/images/simulation/timeline/humantask.png";
			} else if(se instanceof StartSimulationEvent) {
				return "/designer/images/simulation/timeline/startevent.png";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	private JSONObject getTaskEventsFromAllEvents(AggregatedSimulationEvent event, List<SimulationEvent> allEvents, String intervalUnit) throws Exception {
		JSONObject allEventsObject = new JSONObject();
		allEventsObject.put("headline", "Simulation Events");
		allEventsObject.put("type","default");
		allEventsObject.put("text","Simulation Events");
		JSONArray allEventsDataArray = new JSONArray();
		for(SimulationEvent se : allEvents) {
			// for now only include end and activity events
			if ((se instanceof EndSimulationEvent) || (se instanceof ActivitySimulationEvent) || (se instanceof HumanTaskActivitySimulationEvent)) {
				if(event != null) {
					String seActivityId = getSingleEventActivityId(se);
					String eventActivitytId = getAggregatedEventActivityId(event);
					if(eventActivitytId.equals(seActivityId)) {
						allEventsDataArray.put(getTimelineEventObject(se, intervalUnit));
					}
				} else {
					allEventsDataArray.put(getTimelineEventObject(se, intervalUnit));
				}
			}
		}
		allEventsObject.put("date", allEventsDataArray);
		// sort the time values
		Collections.sort(this.eventAggregationsTimes);
		return allEventsObject;
	}
	
	private JSONObject getTimelineEventObject(SimulationEvent se, String intervalUnit) throws Exception{
		JSONObject seObject = new JSONObject();
		seObject.put("id", se.getUUID().toString());
		seObject.put("startDate", getDateString(se.getStartTime()));
		seObject.put("endDate", getDateString(se.getEndTime()));
		if(se instanceof EndSimulationEvent) {
			seObject.put("headline", ((EndSimulationEvent) se).getActivityName());
			seObject.put("activityid", ((EndSimulationEvent) se).getActivityId());
		} else if(se instanceof ActivitySimulationEvent) {
			seObject.put("headline", ((ActivitySimulationEvent) se).getActivityName());
			seObject.put("activityid", ((ActivitySimulationEvent) se).getActivityId());
		} else if(se instanceof HumanTaskActivitySimulationEvent) {
			seObject.put("headline", ((HumanTaskActivitySimulationEvent) se).getActivityName());
			seObject.put("activityid", ((HumanTaskActivitySimulationEvent) se).getActivityId());
		}
		seObject.put("text", "");
		seObject.put("tag", "");
		JSONObject seAsset = new JSONObject();
		seAsset.put("media", "");
		seAsset.put("thumbnail", getIcon(se));
		seAsset.put("credit", "");
		seAsset.put("caption", "");
		seObject.put("asset", seAsset);
		
		// add aggregated events as well
		this.eventAggregations.add(se);
		Interval eventinterval = new Interval(this.simTime.getMillis(), se.getEndTime());
		
		long durationvalue = eventinterval.toDurationMillis();
		if(intervalUnit.equals("seconds")) {
			durationvalue = durationvalue / 1000;
		} else if(intervalUnit.equals("minutes")) {
			durationvalue = durationvalue / (1000*60);
		} else if(intervalUnit.equals("hours")) {
			durationvalue = durationvalue / (1000*60*60);
		} else if(intervalUnit.equals("days")) {
			durationvalue = durationvalue / (1000*60*60*24);
		} else {
			// default to milliseconds
		}
		
		this.eventAggregationsTimes.add(durationvalue);
		return seObject;
	}
	
	private String getSingleEventActivityId(SimulationEvent event) {
		if(event != null) {
			if(event instanceof ActivitySimulationEvent) {
				return ((ActivitySimulationEvent)event).getActivityId();
			} else if(event instanceof EndSimulationEvent) {
				return ((EndSimulationEvent)event).getActivityId();
			} else if(event instanceof GatewaySimulationEvent) {
				return((GatewaySimulationEvent)event).getActivityId();
			} else if(event instanceof HumanTaskActivitySimulationEvent) {
				return((HumanTaskActivitySimulationEvent)event).getActivityId();
			} else if(event instanceof StartSimulationEvent) {
				return((StartSimulationEvent)event).getActivityId();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	private String getAggregatedEventActivityId(AggregatedSimulationEvent event) {
		if(event instanceof AggregatedProcessSimulationEvent) {
			return ((AggregatedProcessSimulationEvent)event).getProcessId();
		} else if(event instanceof HTAggregatedSimulationEvent) {
			return ((HTAggregatedSimulationEvent)event).getActivityId();
		} else if(event instanceof AggregatedActivitySimulationEvent) {
			return ((AggregatedActivitySimulationEvent)event).getActivityId();
		} else {
			return "";
		}
	}
}


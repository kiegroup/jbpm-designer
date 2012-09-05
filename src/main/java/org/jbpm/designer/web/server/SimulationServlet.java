package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import org.jbpm.simulation.impl.events.HTAggregatedSimulationEvent;
import org.jbpm.simulation.impl.events.HumanTaskActivitySimulationEvent;
import org.jbpm.simulation.impl.events.StartSimulationEvent;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
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
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
	
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

				SimulationRepository repo = SimulationRunner.runSimulation(processId, processXML, Integer.parseInt(numInstances), intervalInt, "default.simulation.rules.drl");
				WorkingMemorySimulationRepository wmRepo = (WorkingMemorySimulationRepository) repo;
				// start evaluating all the simulation events generated
				wmRepo.fireAllRules();
				List<AggregatedSimulationEvent> aggEvents = wmRepo.getAggregatedEvents();
				List<SimulationEvent> allEvents = new ArrayList<SimulationEvent>(wmRepo.getEvents());
				wmRepo.close();
				
				JSONObject parentJSON = new JSONObject();
				JSONArray aggProcessSimulationJSONArray = new JSONArray();
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
						obj1.put("value", adjustToSecs(event.getMaxExecutionTime()));
						JSONObject obj2 = new JSONObject();
						obj2.put("label", "Min Execution Time");
						obj2.put("value", adjustToSecs(event.getMinExecutionTime()));
						JSONObject obj3 = new JSONObject();
						obj3.put("label", "Avg. Execution Time");
						obj3.put("value", adjustToSecs(event.getAvgExecutionTime()));
						processSimValues.put(obj1);
						processSimValues.put(obj2);
						processSimValues.put(obj3);
						processSimKeys.put("values", processSimValues);
						
						// TODO add specific process events from all events
						// when that gets added!
						
						aggProcessSimulationJSONArray.put(processSimKeys);
						
					} else if(aggEvent instanceof HTAggregatedSimulationEvent) {
						HTAggregatedSimulationEvent event = (HTAggregatedSimulationEvent) aggEvent;
						
						JSONObject allValues = new JSONObject();
						JSONObject resourceValues = new JSONObject();
						
						allValues.put("key", "Human Task Avarages");
						allValues.put("id", event.getActivityId());
						allValues.put("name", event.getActivityName());
						
						JSONArray innerExecutionValues = new JSONArray();
						JSONObject obj1 = new JSONObject();
						obj1.put("label", "Max");
						obj1.put("value", adjustToSecs(event.getMaxExecutionTime()));
						JSONObject obj2 = new JSONObject();
						obj2.put("label", "Min");
						obj2.put("value", adjustToSecs(event.getMinExecutionTime()));
						JSONObject obj3 = new JSONObject();
						obj3.put("label", "Average");
						obj3.put("value", adjustToSecs(event.getAvgExecutionTime()));
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
						obj4.put("value", adjustToSecs(event.getMaxWaitTime()));
						JSONObject obj5 = new JSONObject();
						obj5.put("label", "Min");
						obj5.put("value", adjustToSecs(event.getMinWaitTime()));
						JSONObject obj6 = new JSONObject();
						obj6.put("label", "Average");
						obj6.put("value", adjustToSecs(event.getAvgWaitTime()));
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
						
						// single events
						JSONObject taskEvents = getTaskEventsFromAllEvents(event, allEvents);
						if(taskEvents != null) {
							allValues.put("timeline", taskEvents);
						}
						
						aggHTSimulationJSONArray.put(allValues);
					} else if(aggEvent instanceof AggregatedActivitySimulationEvent) {
						AggregatedActivitySimulationEvent event = (AggregatedActivitySimulationEvent) aggEvent;
						JSONObject taskSimKeys = new JSONObject();
						taskSimKeys.put("key", "Task Avarages");
						taskSimKeys.put("id", event.getActivityId());
						taskSimKeys.put("name", event.getActivityName());
						JSONArray taskSimValues = new JSONArray();
						JSONObject obj1 = new JSONObject();
						obj1.put("label", "Max. Execution Time");
						obj1.put("value", adjustToSecs(event.getMaxExecutionTime()));
						JSONObject obj2 = new JSONObject();
						obj2.put("label", "Min. Execution Time");
						obj2.put("value", adjustToSecs(event.getMinExecutionTime()));
						JSONObject obj3 = new JSONObject();
						obj3.put("label", "Avg. Execution Time");
						obj3.put("value", adjustToSecs(event.getAvgExecutionTime()));
						taskSimValues.put(obj1);
						taskSimValues.put(obj2);
						taskSimValues.put(obj3);
						taskSimKeys.put("values", taskSimValues);
						// single events
						JSONObject taskEvents = getTaskEventsFromAllEvents(event, allEvents);
						if(taskEvents != null) {
							taskSimKeys.put("timeline", taskEvents);
						}
						aggTaskSimulationJSONArray.put(taskSimKeys);
					}
				}
				
				parentJSON.put("processsim", aggProcessSimulationJSONArray);
				parentJSON.put("htsim", aggHTSimulationJSONArray);
				parentJSON.put("tasksim", aggTaskSimulationJSONArray);
				parentJSON.put("timeline", getTaskEventsFromAllEvents(null, allEvents));
				System.out.println("******* JSON: " + parentJSON.toString());
				
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
		DateTime dt = new DateTime(seDate);
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(dt.getYear()).append(",");
		retBuf.append(dt.getMonthOfYear()).append(",");
		retBuf.append(dt.getDayOfMonth()).append(",");
		retBuf.append(dt.getMonthOfYear()).append(",");
		retBuf.append(dt.getHourOfDay()).append(",");
		retBuf.append(dt.getMinuteOfHour()).append(",");
		retBuf.append(dt.getSecondOfMinute());
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
	
	private JSONObject getTaskEventsFromAllEvents(AggregatedSimulationEvent event, List<SimulationEvent> allEvents) throws Exception {
		JSONObject allEventsObject = new JSONObject();
		allEventsObject.put("headline", "Simulation Events");
		allEventsObject.put("type","default");
		allEventsObject.put("text","Simulation Events");
		JSONArray allEventsDateArray = new JSONArray();
		for(SimulationEvent se : allEvents) {
			if(event != null) {
				String seActivityId = getSingleEventActivityId(se);
				String eventActivitytId = getAggregatedEventActivityId(event);
				if(eventActivitytId.equals(seActivityId)) {
					allEventsDateArray.put(getTimelineEventObject(se));
				}
			} else {
				// add all
				allEventsDateArray.put(getTimelineEventObject(se));
			}
		}
		allEventsObject.put("date", allEventsDateArray);
		return allEventsObject;
	}
	
	private JSONObject getTimelineEventObject(SimulationEvent se) throws Exception{
		JSONObject seObject = new JSONObject();
		seObject.put("id", se.getUUID().toString());
		seObject.put("startDate", getDateString(se.getStartTime()));
		seObject.put("endDate", getDateString(se.getEndTime()));
		seObject.put("headline", getEventName(se));
		seObject.put("text", "");
		seObject.put("tag", "");
		JSONObject seAsset = new JSONObject();
		seAsset.put("media", "");
		seAsset.put("thumbnail", getIcon(se));
		seAsset.put("credit", "");
		seAsset.put("caption", "");
		seObject.put("asset", seAsset);
		
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


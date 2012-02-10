package org.jbpm.designer.bpmn2.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.json.JSONObject;

import sun.misc.BASE64Encoder;


public class BPMN2SyntaxChecker implements SyntaxChecker {
	public static final String EXT_BPMN = "bpmn";
    public static final String EXT_BPMN2 = "bpmn2";
    
	protected Map<String, List<String>> errors = new HashMap<String, List<String>>();
	private String json;
	private String preprocessingData;
	private IDiagramProfile profile;
	private String defaultResourceId = "";
	private String uuid;
	
    private static final Logger _logger = Logger.getLogger(BPMN2SyntaxChecker.class);
	
	public BPMN2SyntaxChecker(String json, String preprocessingData, IDiagramProfile profile, String uuid) {
		this.json = json;
		this.preprocessingData = preprocessingData;
		this.profile = profile;
		this.uuid = uuid;
	}
	
	public void checkSyntax() {
		Definitions def = profile.createMarshaller().getDefinitions(json, preprocessingData);
		
		List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
        	if(root instanceof Process) {
        		Process process = (Process) root;
        		if(process.getFlowElements() != null && process.getFlowElements().size() > 0) {
        			defaultResourceId = process.getFlowElements().get(0).getId();
        		}
        		
        		if(isEmpty(process.getId())) {
        			addError(defaultResourceId, "Process has no id.");
        		} else {
        			if(!SyntaxCheckerUtils.isNCName(process.getId())) {
        				addError(defaultResourceId, "Invalid process id. See http://www.w3.org/TR/REC-xml-names/#NT-NCName for more info.");
        			}
        		}
        		
        		String pname = null;
        		Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        		boolean foundPackageName = false;
                while(iter.hasNext()) {
                    FeatureMap.Entry entry = iter.next();
                    if(entry.getEStructuralFeature().getName().equals("packageName")) {
                    	foundPackageName = true;
                        pname = (String) entry.getValue();
                        if(isEmpty(pname)) {
                        	addError(defaultResourceId, "Process has no package name.");
                        }
                    }
                }
                if(!foundPackageName) {
                	addError(defaultResourceId, "Process has no package name.");
                } else {
                	if(!isEmpty(pname)) {
                		String[] packageAssetInfo = findPackageAndAssetInfo(uuid, profile);
                		String guvnorPackageName = packageAssetInfo[0];
                		if(!guvnorPackageName.equals(pname)) {
                			addError(defaultResourceId, "Process package name is not valid.");
                		}
                	}
                }
                
                if(isEmpty(process.getName())) {
        			addError(defaultResourceId, "Process has no name.");
        		}
                
                boolean foundStartEvent = false;
                boolean foundEndEvent = false;
        		List<FlowElement> flowElements =  process.getFlowElements();
        		for(FlowElement fe : flowElements) {
        			if(fe instanceof StartEvent) {
        				foundStartEvent = true;
        			}
        			if(fe instanceof EndEvent) {
        				foundEndEvent = true;
        			}
        		}
        		if(!foundStartEvent && !isAdHocProcess(process)) {
        			addError(defaultResourceId, "Process has no start node.");
        		}
        		if(!foundEndEvent && !isAdHocProcess(process)) {
        			addError(defaultResourceId, "Process has no end node.");
        		}
        		
        		for(FlowElement fe : flowElements) {
        			if(fe instanceof StartEvent) {
        				StartEvent se = (StartEvent) fe;
        				if(se.getOutgoing() == null && se.getOutgoing().size() < 1) {
        					addError(se, "Start node has no outgoing connections");
        				}
        			} else if (fe instanceof EndEvent) {
        				EndEvent ee = (EndEvent) fe;
        				if(ee.getIncoming() == null && ee.getIncoming().size() < 1) {
        					addError(ee, "End node has no outgoing connections");
        				}
        			} else {
        				if(fe instanceof FlowNode) {
        					FlowNode fn = (FlowNode) fe;
        					if(fn.getOutgoing() == null && fn.getOutgoing().size() < 1) {
            					addError(fn, "Node has no outgoing connections");
            				}
        					if(fn.getIncoming() == null && fn.getIncoming().size() < 1) {
            					addError(fn, "Node has no outgoing connections");
            				}
        				}
        			}
        			
        			if(fe instanceof BusinessRuleTask) {
        				BusinessRuleTask bt = (BusinessRuleTask) fe;
        				Iterator<FeatureMap.Entry> biter = bt.getAnyAttribute().iterator();
        				boolean foundRuleflowGroup = false;
        	            while(biter.hasNext()) {
        	                FeatureMap.Entry entry = biter.next();
        	                if(entry.getEStructuralFeature().getName().equals("ruleFlowGroup")) {
        	                	foundRuleflowGroup = true;
        	                	String ruleflowGroup = (String) entry.getValue();
        	                	if(isEmpty(ruleflowGroup)) {
        	                		addError(bt, "Business Rule Task has no ruleflow-group.");
        	                	}
        	                }
        	            }
        	            if(!foundRuleflowGroup) {
        	            	addError(bt, "Business Rule Task has no ruleflow-group.");
        	            }
        			}
        			
        			if(fe instanceof ScriptTask) {
        				ScriptTask st = (ScriptTask) fe;
        				if(isEmpty(st.getScript())) {
        					addError(st, "Script Task has no script.");
        				}
        				if(isEmpty(st.getScriptFormat())) {
        					addError(st, "Script Task has no script format.");
        				}
        			}
        			
        			if(fe instanceof SendTask) {
        				SendTask st = (SendTask) fe;
        				if(st.getOperationRef() == null) {
        					addError(st, "Send Task has no operation.");
        				}
        				if(st.getMessageRef() == null) {
        					addError(st, "Send Task has no message.");
        				}
        			}
        			
        			if(fe instanceof UserTask) {
        				UserTask ut = (UserTask) fe;
        				String taskName = null;
        				Iterator<FeatureMap.Entry> utiter = ut.getAnyAttribute().iterator();
        				boolean foundTaskName = false;
        		        while(utiter.hasNext()) {
        		            FeatureMap.Entry entry = utiter.next();
        		            if(entry.getEStructuralFeature().getName().equals("taskName")) {
        		            	foundTaskName = true;
        		            	taskName = (String) entry.getValue();
        		            	if(isEmpty(taskName)) {
        		            		addError(ut, "User Task has no task name.");
        		            	}
        		            }
        		        }
        		        if(!foundTaskName) {
        		        	addError(ut, "User Task has no task name.");
        		        } else {
        		        	if(taskName != null) {
        		        		String[] packageAssetInfo = findPackageAndAssetInfo(uuid, profile);
        		        		String packageName = packageAssetInfo[0];
        		        		String assetName = packageAssetInfo[1];
        		        		String taskFormName = taskName + "-taskform";
        		        		if(!taskFormExistsInGuvnor(packageName, assetName, taskFormName, profile)) {
        		        			addError(ut, "User Task has no task form defined.");
        		        		}
        		        	} 
        		        }
        			}
        			
        			if(fe instanceof CatchEvent) {
        				CatchEvent event = (CatchEvent) fe;
        				List<EventDefinition> eventdefs = event.getEventDefinitions();
        				for(EventDefinition ed : eventdefs) {
	        				if(ed instanceof TimerEventDefinition) {
	        	                TimerEventDefinition ted = (TimerEventDefinition) ed;
	        	                if(ted.getTimeDate() == null) {
	        	                	addError(event, "Catch Event has no timedate.");
	        	                }
	        	                if(ted.getTimeDuration() == null) {
	        	                	addError(event, "Catch Event has no timeduration.");
	        	                }
	        	                if(ted.getTimeCycle() == null) {
	        	                	addError(event, "Catch Event has no timecycle.");
	        	                }
	        	            } else if( ed instanceof SignalEventDefinition) {
	        	                if(((SignalEventDefinition) ed).getSignalRef() == null) {
	        	                	addError(event, "Catch Event has no signalref.");
	        	                }
	        	            } else if( ed instanceof ErrorEventDefinition) {
	        	                if(((ErrorEventDefinition) ed).getErrorRef() == null || ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() == null) {
	        	                	addError(event, "Catch Event has no errorref.");
	        	                }
	        	            } else if( ed instanceof ConditionalEventDefinition ) {
	        	                FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
	        	                if(conditionalExp.getBody() == null) {
	        	                	addError(event, "Catch Event has no conditionexpression.");
	        	                }
	        	            } else if( ed instanceof EscalationEventDefinition ) {
	        	                if(((EscalationEventDefinition) ed).getEscalationRef() == null) {
	        	                	addError(event, "Catch Event has no escalationref.");
	        	                }
	        	            } else if( ed instanceof MessageEventDefinition) {
	        	                if(((MessageEventDefinition) ed).getMessageRef() == null) {
	        	                    addError(event, "Catch Event has no messageref.");
	        	                }
	        	            }  else if( ed instanceof CompensateEventDefinition) {
	        	                if(((CompensateEventDefinition) ed).getActivityRef() == null) {
	        	                	addError(event, "Catch Event has no activityref.");
	        	                }
	        	            } 
        				}
        			}
        			
        			if(fe instanceof ThrowEvent) {
        				ThrowEvent event = (ThrowEvent) fe;
        				List<EventDefinition> eventdefs = event.getEventDefinitions();
        		        for(EventDefinition ed : eventdefs) {
        		            if(ed instanceof TimerEventDefinition) {
        		                TimerEventDefinition ted = (TimerEventDefinition) ed;
        		                if(ted.getTimeDate() == null) {
        		                	addError(event, "Throw Event has no timedate.");
        		                }
        		                if(ted.getTimeDuration() == null) {
        		                	addError(event, "Throw Event has no timeduration.");
        		                }
        		                if(ted.getTimeCycle() != null) {
        		                	addError(event, "Throw Event has no timecycle.");
        		                }
        		            } else if( ed instanceof SignalEventDefinition) {
        		                if(((SignalEventDefinition) ed).getSignalRef() == null) {
        		                	addError(event, "Throw Event has no signalref.");
        		                }
        		            } else if( ed instanceof ErrorEventDefinition) {
        		                if(((ErrorEventDefinition) ed).getErrorRef() == null || ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() == null) {
        		                	addError(event, "Throw Event has no errorref.");
        		                }
        		            } else if( ed instanceof ConditionalEventDefinition ) {
        		                FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
        		                if(conditionalExp.getBody() == null) {
        		                	addError(event, "Throw Event has no conditional expression.");
        		                }
        		            } else if( ed instanceof EscalationEventDefinition ) {
        		                if(((EscalationEventDefinition) ed).getEscalationRef() == null) {
        		                	addError(event, "Throw Event has no conditional escalationref.");
        		                }
        		            } else if( ed instanceof MessageEventDefinition) {
        		                if(((MessageEventDefinition) ed).getMessageRef() == null) {
        		                	addError(event, "Throw Event has no conditional messageref.");
        		                }
        		            }  else if( ed instanceof CompensateEventDefinition) {
        		                if(((CompensateEventDefinition) ed).getActivityRef() == null) {
        		                	addError(event, "Throw Event has no conditional activityref.");
        		                }
        		            }  
        		        }
        			}
        			
        			if(fe instanceof SequenceFlow) {
        				SequenceFlow sf = (SequenceFlow) fe;
        				if(sf.getSourceRef() == null) {
        					addError((SequenceFlow) fe, "An Edge must have a source node.");
        				}
        				if(sf.getTargetRef() == null) {
        					addError((SequenceFlow) fe, "An Edge must have a target node.");
        				}
        			}
        			
        			if(fe instanceof Gateway) {
        				Gateway gw = (Gateway) fe;
        				if(gw.getGatewayDirection() == null) {
        					addError((Gateway) fe, "Gateway has no direction.");
        				}
        			}
        			
        			if(fe instanceof CallActivity) {
        				CallActivity ca = (CallActivity) fe;
        				if(ca.getCalledElement() == null || ca.getCalledElement().length() < 1) {
        					addError((CallActivity) fe, "Reusable Subprocess has no called element specified.");
        				} else {
        					String[] packageAssetInfo = findPackageAndAssetInfo(uuid, profile);
    		        		String packageName = packageAssetInfo[0];
    		        		List<String> allProcessesInPackage = getAllProcessesInPackage(packageName, profile);
    		        		boolean foundCalledElementProcess = false;
    		        		for(String p : allProcessesInPackage) {
    		        			String processContent = getProcessSourceContent(packageName, p, profile);
    		        			Pattern pattern = Pattern.compile("<\\S*process[\\s\\S]*id=\"" + ca.getCalledElement() + "\"", Pattern.MULTILINE);
    		                    Matcher m = pattern.matcher(processContent);
    		                    if(m.find()) {
    		                    	foundCalledElementProcess = true;
    		                    	break;
    		                    }
    		        		}
    		        		if(!foundCalledElementProcess) {
    		        			addError((CallActivity) fe, "No existing process with id=" + ca.getCalledElement() + " could be found.");
    		        		}
        				}
        			}
        			
        			if(fe instanceof DataObject) {
        				DataObject dao = (DataObject) fe;
        				if(dao.getName() == null || dao.getName().length() < 1) {
        					addError((DataObject) fe, "Data Object has no name defined.");
        				}
        			}
        		}
        	}
        }
	}

	public Map<String, List<String>> getErrors() {
		return errors;
	}

	public JSONObject getErrorsAsJson() {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String,List<String>> error: this.getErrors().entrySet()) {
			try {
				jsonObject.put(error.getKey(), error.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	public boolean errorsFound() {
		return errors.size() > 0;
	}

	public void clearErrors() {
		errors.clear();
	}
	
	private void addError(BaseElement element, String error) {
		addError(element.getId(), error);
	}
	
	private void addError(String resourceId, String error) {
		if(errors.containsKey(resourceId) && errors.get(resourceId) != null) {
			errors.get(resourceId).add(error);
		} else {
			List<String> value = new ArrayList<String>();
			value.add(error);
			errors.put(resourceId, value);
		}
	}
	
	private static boolean isEmpty(final CharSequence str) {
		if ( str == null || str.length() == 0 ) {
			return true;
	    }
	    for ( int i = 0, length = str.length(); i < length; i++ ) {
	    	if ( str.charAt( i ) != ' ' ) {
	    		return false;
	        }
	    }
	    return true;
	}
	
	private String[] findPackageAndAssetInfo(String uuid,
            IDiagramProfile profile) {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/";
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(getInputStreamForURL(packagesURL,
                            "GET", profile));
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        packages.add(reader.getElementText());
                    }
                }
            }
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }

        boolean gotPackage = false;
        String[] pkgassetinfo = new String[2];
        for (String nextPackage : packages) {
            String packageAssetURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + nextPackage + "/assets/";
            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory
                        .createXMLStreamReader(getInputStreamForURL(
                                packageAssetURL, "GET", profile));
                String title = "";
                while (reader.hasNext()) {
                    int next = reader.next();
                    if (next == XMLStreamReader.START_ELEMENT) {
                        if ("title".equals(reader.getLocalName())) {
                            title = reader.getElementText();
                        }
                        if ("uuid".equals(reader.getLocalName())) {
                            String eleText = reader.getElementText();
                            if (uuid.equals(eleText)) {
                                pkgassetinfo[0] = nextPackage;
                                pkgassetinfo[1] = title;
                                gotPackage = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            }
            if (gotPackage) {
                // noo need to loop through rest of packages
                break;
            }
        }
        return pkgassetinfo;
    }
	
	private InputStream getInputStreamForURL(String urlLocation,
            String requestMethod, IDiagramProfile profile) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection
                .setRequestProperty("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setReadTimeout(5 * 1000);

        applyAuth(profile, connection);

        connection.connect();

        BufferedReader sreader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        return new ByteArrayInputStream(stringBuilder.toString().getBytes(
                "UTF-8"));
    }
    
    private void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = enc.encode(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }
    
    private boolean taskFormExistsInGuvnor(String packageName, String assetName, String taskFormName, IDiagramProfile profile) {
    	try {	
    		String formURL = ExternalInfo.getExternalProtocol(profile)
    	        + "://"
    	        + ExternalInfo.getExternalHost(profile)
    	        + "/"
    	        + profile.getExternalLoadURLSubdomain().substring(0,
    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
    	        + "/rest/packages/" + packageName + "/assets/" + URLEncoder.encode(taskFormName, "UTF-8");
    	
    	
			URL checkURL = new URL(formURL);
			HttpURLConnection checkConnection = (HttpURLConnection) checkURL
			        .openConnection();
			applyAuth(profile, checkConnection);
			checkConnection.setRequestMethod("GET");
			checkConnection
			        .setRequestProperty("Accept", "application/atom+xml");
			checkConnection.connect();
			_logger.info("check connection response code: " + checkConnection.getResponseCode());
			if (checkConnection.getResponseCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
        return false;
    }
    
    public List<String> getAllProcessesInPackage(String pkgName, IDiagramProfile profile) {
        List<String> processes = new ArrayList<String>();
        String assetsURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/"
                + pkgName
                + "/assets/";
        
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(getInputStreamForURL(assetsURL, "GET", profile));

            String format = "";
            String title = ""; 
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("format".equals(reader.getLocalName())) {
                        format = reader.getElementText();
                    } 
                    if ("title".equals(reader.getLocalName())) {
                        title = reader.getElementText();
                    }
                    if ("asset".equals(reader.getLocalName())) {
                        if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                            processes.add(title);
                            title = "";
                            format = "";
                        }
                    }
                }
            }
            // last one
            if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                processes.add(title);
            }
        } catch (Exception e) {
        	_logger.error("Error finding processes in package: " + e.getMessage());
        } 
        return processes;
    }
    
    private String getProcessSourceContent(String packageName, String assetName, IDiagramProfile profile) {
        String assetSourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + packageName + "/assets/" + assetName
                + "/source/";

        try {
            InputStream in = getInputStreamForURL(assetSourceURL, "GET", profile);
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer);
            return writer.toString();
        } catch (Exception e) {
        	_logger.error("Error retrieving asset content: " + e.getMessage());
            return "";
        }
    }
    
    private boolean isAdHocProcess(Process process) {
        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("adHoc")) {
            	return Boolean.parseBoolean(((String)entry.getValue()).trim());
            }
        }
        return false;
    }
}

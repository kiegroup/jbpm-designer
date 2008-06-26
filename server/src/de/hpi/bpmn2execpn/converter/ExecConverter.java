package de.hpi.bpmn2execpn.converter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.ExecDataObject;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn2execpn.model.ExecConversionContext;
import de.hpi.bpmn2execpn.model.ExecTask;
import de.hpi.bpmn2pn.converter.Converter;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.bpmn2pn.model.SubProcessPlaces;
import de.hpi.execpn.AutomaticTransition;
import de.hpi.execpn.ExecFlowRelationship;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.FormTransition;
import de.hpi.execpn.TransformationTransition;
import de.hpi.execpn.impl.ExecPNFactoryImpl;
import de.hpi.execpn.pnml.Locator;
import de.hpi.petrinet.ExecPlace;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class ExecConverter extends Converter {

	private static final String baseXsltURL = "http://localhost:3000/examples/contextPlace/";
	private static final String copyXsltURL = baseXsltURL + "copy_xslt.xsl";
	private static final String extractDataURL = baseXsltURL + "extract_processdata.xsl";
	private static final String extractFormDataURL = baseXsltURL + "extract_init_processdata.xsl";
	private static final String extractAllocateDataURL = baseXsltURL + "extract_allocate_processdata.xsl";
	private static final String extractFinishDataURL = baseXsltURL + "extract_finish_processdata.xsl";
	private static String enginePostURL;
	private String contextPath;
	protected String standardModel;
	protected String baseFileName;

	public ExecConverter(BPMNDiagram diagram, String modelURL, String contextPath) {
		super(diagram, new ExecPNFactoryImpl(modelURL));
		this.standardModel = modelURL;
		this.contextPath = contextPath;
	}
	
	public void setBaseFileName(String basefilename) {
		this.baseFileName = basefilename;
	}
		
	@Override
	protected void handleDiagram(PetriNet net, ConversionContext c) {
		((ExecPetriNet) net).setName(diagram.getTitle());
	}

	@Override
	protected void createStartPlaces(PetriNet net, ConversionContext c) {
		// do nothing...: we want start transitions instead of start places
	}

	// TODO this is a dirty hack...
	@Override
	protected void handleTask(PetriNet net, Task task, ConversionContext c) {
		ExecTask exTask = new ExecTask();
		exTask.setId(task.getId());
		exTask.setLabel(task.getLabel());
		exTask.setResourceId(task.getResourceId());
		String taskDesignation = exTask.getTaskDesignation();
		
		// create model, form and bindings
		String model = null;
		String form = null;
		String bindings = null;
		
		exTask.pl_inited = addPlace(net, "pl_inited_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_ready = addPlace(net, "pl_ready_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_running = addPlace(net, "pl_running_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_deciding = addPlace(net, "pl_deciding_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_suspended = addPlace(net, "pl_suspended_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_complete = addPlace(net, "pl_complete_" + task.getId(), ExecPlace.Type.flow);	
		exTask.pl_context = addPlace(net, "pl_context_" + task.getId(), ExecPlace.Type.context);

		// create transitions
		AutomaticTransition enable = addAutomaticTransition(net, "tr_enable_" + task.getId(), taskDesignation);
		FormTransition submit = addFormTransition(net, "tr_submit_" + task.getId(), task.getLabel());
		FormTransition delegate = addFormTransition(net, "tr_delegate_" + task.getId(), taskDesignation);
		FormTransition review = addFormTransition(net, "tr_review_" + task.getId(), taskDesignation);
		
		exTask.tr_init = addAutomaticTransition(net, "tr_init_" + task.getId(), taskDesignation);
		exTask.tr_enable = enable;
		exTask.tr_allocate = addTransformationTransition(net, "tr_allocate_" + task.getId(), taskDesignation,"allocate", copyXsltURL);
		exTask.tr_submit = submit;
		exTask.tr_delegate = delegate;
		exTask.tr_review = review;
		exTask.tr_done = addAutomaticTransition(net, "tr_done_" + task.getId(), taskDesignation);
		exTask.tr_suspend = addTransformationTransition(net, "tr_suspend_" + task.getId(), taskDesignation, "suspend", copyXsltURL);
		exTask.tr_resume = addTransformationTransition(net, "tr_resume_" + task.getId(), taskDesignation, "resume", copyXsltURL);
		exTask.tr_finish = addAutomaticTransition(net, "tr_finish_" + task.getId(), taskDesignation);
	
		exTask.tr_init.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_enable.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_submit.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_allocate.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_delegate.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_review.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_done.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_suspend.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_resume.setContextPlaceID(exTask.pl_context.getId());
		exTask.tr_finish.setContextPlaceID(exTask.pl_context.getId());
		
		// create Documents for model, form, bindings
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance (  ) ; 
		
		try {
			DocumentBuilder parser = factory.newDocumentBuilder (  ) ; 
		
			Document modelDoc = parser.newDocument();
			//modelDoc.appendChild(modelDoc.createElement("engine-info"));
			Node places = modelDoc.appendChild(modelDoc.createElement("places"));
			Node data = places.appendChild(modelDoc.createElement("data")); 
			//Node metaData = places.appendChild(modelDoc.createElement("metadata")); 
			//Node processData = places.appendChild(modelDoc.createElement("processdata"));

			// initialize engine Post URL
			PropertiesConfiguration config = new PropertiesConfiguration("pnengine.properties");
			this.enginePostURL = config.getString("pnengine.url") + "/documents/";
			
			// interrogate all incoming data objects for task, create DataPlaces for them and create Task model
			HashMap<String, Node> processdataMap = new HashMap<String, Node>();
			List<Edge> edges_in = task.getIncomingEdges();
			for (Edge edge : edges_in) {
				if (edge.getSource() instanceof ExecDataObject) {
					ExecDataObject dataObject = (ExecDataObject)edge.getSource();
					Place dataPlace = ExecTask.getDataPlace(dataObject.getId());
					// for incoming data objects of task: create read dependencies
					addReadOnlyExecFlowRelationship(net, dataPlace, exTask.tr_enable, null);
					// create XML Structure for Task
					String modelXML = dataObject.getModel();
					/*modelXML =
						"<schema xmlns=\"http://www.w3.org/1999/xhtml\""+
						      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
						      "xsi:schemaLocation=\"http://www.w3.org/1999/xhtml"+
						      "http://www.w3.org/1999/xhtml.xsd\">" +
						      modelXML +
						      "</schema>";*/
					
					// attach to task model
					Element dataEl = modelDoc.createElement("data");
					dataEl.setAttribute("place_id", dataObject.getId());
					//placesEl.appendChild(dataEl);
					
					try {
						Document doc = parser.parse(new InputSource(new StringReader(modelXML)));
						NodeList elements = doc.getElementsByTagName("xsd:element");
						//Node processdata = doc.getDocumentElement().getFirstChild().getFirstChild();
						if (elements.getLength() > 0){
							processdataMap.put(dataObject.getId(), elements.item(0));
							// if contained in an ad-hoc subprocess, add
							//	data dependency
							Container parent = task.getParent();
							if (parent instanceof SubProcess && ((SubProcess)parent).isAdhoc()){ 
								// TODO: Use Node List??? - ugly solution
								//addDataDependeny(net, exTask, dataPlace, elements);
							}
						}
					} catch (Exception io) {
						io.printStackTrace();
					}
				}
			}
			
			// interrogate all outgoing data objects for task, create DataPlaces for them and create Task model
			List<Edge> edges_out = task.getOutgoingEdges();
			for (Edge edge : edges_out) {
				if (edge.getTarget() instanceof ExecDataObject) {
					ExecDataObject dataObject = (ExecDataObject)edge.getTarget();
					// for incoming data objects of task: create read dependencies
					if (!isContainedIn(dataObject, processdataMap)) {

						// create XML Structure for Task
						String modelXML = dataObject.getModel();
						modelXML =
							"<html xmlns=\"http://www.w3.org/1999/xhtml\""+
							      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
							      "xsi:schemaLocation=\"http://www.w3.org/1999/xhtml"+
							      "http://www.w3.org/1999/xhtml.xsd\">" +
							      modelXML;
						
						try {
							Document doc = parser.parse(new InputSource(new StringReader(modelXML)));
							NodeList processdata = doc.getElementsByTagName("xsd:element");
							//Node processdata = doc.getDocumentElement().getFirstChild().getFirstChild();
							if (processdata.getLength() > 0)
								processdataMap.put(dataObject.getId(), processdata.item(0));						
						} catch (Exception io) {
							io.printStackTrace();
						}
						addReadOnlyExecFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_enable, null);
					}
					addExecFlowRelationship(net, exTask.tr_finish, ExecTask.getDataPlace(dataObject.getId()), create_extract_processdata_xsl(dataObject, parser));
					addFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_finish);
				}
			}
			

			// build form Document template without attributes
			Document formDoc = buildFormTemplate(parser);
			// adds form fields of necessary attributes
			formDoc = addFormFields(formDoc, processdataMap);
			// build bindings Document for attributes
			Document bindDoc = buildBindingsDocument(parser);
			// adds binding attributes for tags
			bindDoc = addBindings(bindDoc, processdataMap);
			

			
				// persist form and bindings and save URL
				model = this.postDataToURL(domToString(modelDoc),enginePostURL);
				form = this.postDataToURL(domToString(formDoc),enginePostURL);
				
				String bindingsString = domToString(bindDoc);
				bindings = this.postDataToURL(
						bindingsString.replace("<bindings>", "").replace("</bindings>", ""),
						enginePostURL);
				
				// set URLs for transitions
				submit.setModelURL(model);
				submit.setFormURL(form);
				submit.setBindingsURL(bindings);
				delegate.setModelURL(model);
				delegate.setFormURL(form);
				delegate.setBindingsURL(bindings);
				review.setModelURL(model);
				review.setFormURL(form);
				review.setBindingsURL(bindings);

				
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} catch (SAXException sax) {
			sax.printStackTrace();
		}
		
		
		// add role dependencies
		String rolename = task.getRolename();
		
		// integrate context place
		exTask.pl_context.addLocator(new Locator("startTime", "xsd:string", "/data/metadata/startTime"));
		exTask.pl_context.addLocator(new Locator("endTime", "xsd:string", "/data/metadata/endTime"));
		exTask.pl_context.addLocator(new Locator("status", "xsd:string", "/data/metadata/status"));
		exTask.pl_context.addLocator(new Locator("owner", "xsd:string", "/data/executiondata/owner"));
		exTask.pl_context.addLocator(new Locator("isDelegated", "xsd:string", "/data/metadata/isDelegated"));
		exTask.pl_context.addLocator(new Locator("isReviewed", "xsd:string", "/data/metadata/isReviewed"));
		exTask.pl_context.addLocator(new Locator("reviewRequested", "xsd:string", "/data/metadata/reviewRequested"));
		exTask.pl_context.addLocator(new Locator("startTime", "xsd:string", "/data/metadata/firstOwner"));
		exTask.pl_context.addLocator(new Locator("actions", "xsd:string", "/data/metadata/actions"));

		//init transition
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), exTask.tr_init);
		addFlowRelationship(net, exTask.tr_init, exTask.pl_inited);
		
		//enable transition
		//note: structure of context place must be initialized by engine
		//addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), exTask.tr_enable);
		addFlowRelationship(net, exTask.pl_inited, exTask.tr_enable);
		addExecFlowRelationship(net, exTask.tr_enable, exTask.pl_ready, extractFormDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_enable);
		addExecFlowRelationship(net, exTask.tr_enable, exTask.pl_context, baseXsltURL + "context_enable.xsl");
		enable.setModelURL(model);
		
		// allocate Transition
		addFlowRelationship(net, exTask.pl_ready, exTask.tr_allocate);
		addExecFlowRelationship(net, exTask.tr_allocate, exTask.pl_running, extractAllocateDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_allocate);
		addExecFlowRelationship(net, exTask.tr_allocate, exTask.pl_context, baseXsltURL + "context_allocate.xsl");
		exTask.tr_allocate.setRolename(rolename);
		
		if (task.isSkippable()) {
			// skip Transition
			exTask.setSkippable(true);
			exTask.tr_skip = addTransformationTransition(net, "tr_skip_" + task.getId(), taskDesignation, "skip", copyXsltURL);
			exTask.tr_skip.setContextPlaceID(exTask.pl_context.getId());
			addFlowRelationship(net, exTask.pl_ready, exTask.tr_skip);
			addExecFlowRelationship(net, exTask.tr_skip, exTask.pl_complete, extractDataURL);
			addFlowRelationship(net, exTask.pl_context, exTask.tr_skip);
			addExecFlowRelationship(net, exTask.tr_skip, exTask.pl_context, baseXsltURL + "context_skip.xsl");
		}
		
		// submit Transition
		submit.setAction("submit");
		addFlowRelationship(net, exTask.pl_running, exTask.tr_submit);
		addExecFlowRelationship(net, exTask.tr_submit, exTask.pl_deciding, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_submit);
		addExecFlowRelationship(net, exTask.tr_submit, exTask.pl_context, baseXsltURL + "context_submit.xsl");
		exTask.tr_submit.setRolename(rolename);
		submit.setFormURL(form);
		submit.setBindingsURL(bindings);

		// delegate Transition
		delegate.setAction("delegate");
		delegate.setGuard(exTask.pl_context.getId() + ".isDelegated == 'true'");
		addFlowRelationship(net, exTask.pl_deciding, exTask.tr_delegate);
		addExecFlowRelationship(net, exTask.tr_delegate, exTask.pl_running, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_delegate);
		addExecFlowRelationship(net, exTask.tr_delegate, exTask.pl_context, baseXsltURL + "context_delegate.xsl");
		exTask.tr_delegate.setRolename(rolename);
		delegate.setFormURL(form);
		delegate.setBindingsURL(bindings);
		
		// review Transition
		review.setAction("review");
		review.setGuard(exTask.pl_context.getId() + ".isDelegated != 'true' && " + exTask.pl_context.getId() + ".reviewRequested == 'true'");
		addFlowRelationship(net, exTask.pl_deciding, exTask.tr_review);
		addExecFlowRelationship(net, exTask.tr_review, exTask.pl_complete, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_review);
		addExecFlowRelationship(net, exTask.tr_review, exTask.pl_context, baseXsltURL + "context_review.xsl");
		exTask.tr_review.setRolename(rolename);
		review.setFormURL(form);
		review.setBindingsURL(bindings);
		
		// done Transition
		addFlowRelationship(net, exTask.pl_deciding, exTask.tr_done);
		addFlowRelationship(net, exTask.tr_done, exTask.pl_complete);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_done);
		addExecFlowRelationship(net, exTask.tr_done, exTask.pl_context, baseXsltURL + "context_done.xsl");
		exTask.tr_done.setGuard(exTask.pl_context.getId() + ".isDelegated != 'true' && " + exTask.pl_context.getId() + ".reviewRequested != 'true'");
		
		// suspend
		addFlowRelationship(net, exTask.pl_running, exTask.tr_suspend);
		addExecFlowRelationship(net, exTask.tr_suspend, exTask.pl_suspended, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_suspend);
		addExecFlowRelationship(net, exTask.tr_suspend, exTask.pl_context, baseXsltURL + "context_suspend.xsl");
		exTask.tr_suspend.setRolename(rolename);
		
		// resume
		addFlowRelationship(net, exTask.pl_suspended, exTask.tr_resume);
		addExecFlowRelationship(net, exTask.tr_resume, exTask.pl_running, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_resume);
		addExecFlowRelationship(net, exTask.tr_resume, exTask.pl_context, baseXsltURL + "context_resume.xsl");
		exTask.tr_resume.setRolename(rolename);
		
		// finish transition
		addFlowRelationship(net, exTask.pl_complete, exTask.tr_finish);
		addExecFlowRelationship(net, exTask.tr_finish, c.map.get(getOutgoingSequenceFlow(task)), extractFinishDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_finish);
		addExecFlowRelationship(net, exTask.tr_finish, exTask.pl_context, baseXsltURL + "context_finish.xsl");	
			
		assert(c instanceof ExecConversionContext);
		((ExecConversionContext)c).addToSubprocessToExecTasksMap(task.getParent(), exTask);
		
		handleMessageFlow(net, task, exTask.tr_allocate, exTask.tr_submit, c);
		if (c.ancestorHasExcpH)
			handleExceptions(net, task, exTask.tr_submit, c);

		for (IntermediateEvent event : task.getAttachedEvents())
			handleAttachedIntermediateEventForTask(net, event, c);
		
	}
	
	@Override
	protected void handleSubProcess(PetriNet net, SubProcess process,
			ConversionContext c) {
		super.handleSubProcess(net, process, c);
		if (process.isAdhoc()) {
			handleSubProcessAdHoc(net, process, c);
		}
	}
	
	/* TODO:   There are a number of tasks that remain to be done here:
	 * 
	 * - Integrating data dependencies as guards
	 * - Connecting auto skip with context places
	 * 
	 * - Test Data CC and Skipper ..
	 */
	protected void handleSubProcessAdHoc(PetriNet net, SubProcess process, ConversionContext c) {
		SubProcessPlaces pl = c.getSubprocessPlaces(process);
		boolean isParallel = process.isParallelOrdering();	

		// start and end transitions
		Transition startT = addTauTransition(net, "ad-hoc_start_" + process.getId());
		Transition endT = addTauTransition(net, "ad-hoc_end_" + process.getId());
		Transition defaultEndT = addTauTransition(net, "ad-hoc_defaultEnd_" + process.getId());
		
		addFlowRelationship(net, pl.startP, startT);
		addFlowRelationship(net, defaultEndT, pl.endP);
		addFlowRelationship(net, endT, pl.endP);
		
		// standard completion condition check
		Place updatedState = addPlace(net, "ad-hoc_updatedState_" + process.getId());
		
		String completionConditionString = getCompletionConditionString(process);
		if (completionConditionString.length() == 0) {
			completionConditionString = "false";
		}
		Transition finalize = addTauTransition(net, "ad-hoc_finalize_" + process.getId());
		finalize.setGuard(completionConditionString);
		Transition resume = addTauTransition(net, "ad-hoc_resume_" + process.getId());
		resume.setGuard("!("+completionConditionString+")" );

		// synchronization and completionCondition checks(synch, corresponds to enableStarting)
		Place synch = addPlace(net, "ad-hoc_synch_" + process.getId());
		addFlowRelationship(net, startT, synch);
		addFlowRelationship(net, synch, defaultEndT);

		addFlowRelationship(net, updatedState, resume); 
		addFlowRelationship(net, updatedState, finalize); 
		
		if (isParallel){
			addFlowRelationship(net, synch, finalize);
		} else {
			addFlowRelationship(net, resume, synch);
		}
		
		// data place specific constructs
		List<Place> dataObjectPlaces = getAccessedDataObjectsPlaces(process);
		for (Place place : dataObjectPlaces){
			addReadOnlyFlowRelationship(net, place, resume);
			addReadOnlyFlowRelationship(net, place, finalize); 
		}

		// task specific constructs
		assert (c instanceof ExecConversionContext);
		List<ExecTask> taskList = ((ExecConversionContext)c).subprocessToExecTasksMap.get(process);
		if (taskList != null) {
			for (ExecTask exTask : taskList) {
				Place enabled = addPlace(net, "ad-hoc_task_enabled_" + exTask.getId());
				Place executed = addPlace(net, "ad-hoc_task_executed_" + exTask.getId());
				addFlowRelationship(net, startT, enabled);
				addFlowRelationship(net, enabled, exTask.tr_init);
				if (isParallel){
					addReadOnlyFlowRelationship(net, synch, exTask.tr_allocate);
				} else {
					addFlowRelationship(net, synch, exTask.tr_allocate);
				}
				addFlowRelationship(net, exTask.tr_finish, executed);
				addFlowRelationship(net, exTask.tr_finish, updatedState);
				addFlowRelationship(net, executed, defaultEndT);
				if (exTask.isSkippable()) {
					addFlowRelationship(net, synch, exTask.tr_skip);
				}
	
				// let completioncondition cheks access the context place
				addReadOnlyFlowRelationship(net, exTask.pl_context, resume);
				addReadOnlyFlowRelationship(net, exTask.pl_context, finalize);
					
				// finishing construct(finalize with skip, finish and abort)
				Place enableFinalize = addPlace(net, "ad-hoc_enable_finalize_task_" + exTask.getId());
				Place taskFinalized = addPlace(net, "ad-hoc_task_finalized_" + exTask.getId());
				Transition skipReady = addTauTransition(net, "ad-hoc_skipready_task_" + exTask.getId());
				Transition skipEnabled = addTauTransition(net, "ad-hoc_skipenabled_task_" + exTask.getId());
				Transition finish = addTauTransition(net, "ad-hoc_finish_task_" + exTask.getId());
				
				addFlowRelationship(net, finalize, enableFinalize);
					
				addFlowRelationship(net, enableFinalize, skipReady);
				addFlowRelationship(net, exTask.pl_ready, skipReady);
				addFlowRelationship(net, skipReady, taskFinalized);
				
				addFlowRelationship(net, enableFinalize, skipEnabled);
				addFlowRelationship(net, enabled, skipEnabled);
				addFlowRelationship(net, skipEnabled, taskFinalized);
					
				addFlowRelationship(net, enableFinalize, finish);
				addFlowRelationship(net, executed, finish);
				addFlowRelationship(net, finish, taskFinalized);
							
				addFlowRelationship(net, taskFinalized, endT);	
			}
		}
	}
	
	private String getCompletionConditionString(SubProcess adHocSubprocess){
		assert (adHocSubprocess.isAdhoc());
		
		StringBuffer result = new StringBuffer();
		String input = adHocSubprocess.getCompletionCondition();
		if (input != null && !input.equals("")){
	
			Pattern pattern = Pattern.compile(
					"stateExpression\\( *'(\\w*)' *, *'(\\w*)' *\\)|"+
					"dataExpression\\( *'(\\w*)' *, *'(\\w*)' *, *'(\\w*)' *\\)|"+
					"(&)|(\\|)|(\\()|(\\))|(!)");
			Matcher matcher = pattern.matcher(input);
	
			while (matcher.find()) {
				
				String groupStateExpr1 = matcher.group(1);
				String groupStateExpr2 = matcher.group(2);
				
				String groupDataExpr1 = matcher.group(3);
				String groupDataExpr2 = matcher.group(4);
				String groupDataExpr3 = matcher.group(5);
				
				String groupAnd = matcher.group(6);
				String groupOr = matcher.group(7);
				String groupLP = matcher.group(8);
				String groupRP = matcher.group(9);
				String groupNot = matcher.group(10);
	
				if (groupStateExpr1 != null && groupStateExpr2 != null){
					result.append("place_pl_context_"+groupStateExpr1+".status=='"+groupStateExpr2+"'");
				} else if (groupDataExpr1 != null && groupDataExpr2 != null && groupDataExpr3 != null){
					result.append("place_pl_data_"+groupDataExpr1+"."+groupDataExpr2+"=='"+groupDataExpr3+"'");
				} else if (groupAnd != null) {
					result.append("&&");
				} else if (groupOr != null) {
					result.append("||");
				} else if (groupLP != null) {
					result.append(groupLP);
				} else if (groupRP != null) {
					result.append(groupRP);
				} else if (groupNot != null) {
					result.append(groupNot);
				}
			}
		}
		return result.toString();
	}

	private List<Place> getAccessedDataObjectsPlaces(SubProcess adHocSubprocess){
		assert (adHocSubprocess.isAdhoc());
		List<Place> list = new ArrayList<Place>();
		String input = adHocSubprocess.getCompletionCondition();
		
		if (input != null && !input.equals("")){
	
			Pattern pattern = Pattern.compile("dataExpression\\( *'(\\w*)' *, *'(\\w*)' *, *'(\\w*)' *\\)|");
			Matcher matcher = pattern.matcher(input);
	
			while (matcher.find()) {
				String groupDataExpr1 = matcher.group(1);
				String groupDataExpr2 = matcher.group(2);
				String groupDataExpr3 = matcher.group(3);
	
				if (groupDataExpr1 != null && groupDataExpr2 != null && groupDataExpr3 != null){
					Place place = ExecTask.getDataPlace(groupDataExpr1);
					if (place != null){
						list.add(place);
					}
				}
			}
		}
		return list;
	}
	
	private void addDataDependeny(PetriNet net, ExecTask execTask, Place dataPlace, NodeList elements){
		
		StringBuffer guardStringBuffer = new StringBuffer();
		for (int i = 0 ; i<elements.getLength(); i++){
			String name = elements.item(i).getAttributes().getNamedItem("name").getNodeValue();
			if (guardStringBuffer.length() > 0){
				guardStringBuffer.append("&&");
			}
			guardStringBuffer.append(dataPlace.getId()+"."+name+"!=''");
		}
		if (guardStringBuffer.length() > 0){
			String formerGuard = execTask.tr_allocate.getGuard();
			if (formerGuard != null && formerGuard.length() > 0){
				guardStringBuffer.append("&&("+formerGuard+")");
			}
			String guardString = guardStringBuffer.toString();
			execTask.tr_allocate.setGuard(guardString);
			addReadOnlyFlowRelationship(net, dataPlace, execTask.tr_allocate);
		}
		
	}

	
	
	
	@Override
	protected void handleDataObject(PetriNet net, DataObject object, ConversionContext c){
		
		try {
			if (object instanceof ExecDataObject) {
				ExecDataObject dataobject = (ExecDataObject) object;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance (  ) ; 
				DocumentBuilder parser = factory.newDocumentBuilder (  ) ; 
				
				//create data place for Task
				ExecPlace dataPlace = addPlace(net,"pl_data_"+dataobject.getId(), ExecPlace.Type.data);
				dataPlace.setName(dataobject.getId());
				ExecTask.addDataPlace(dataPlace);
				
				// create Document for data places 
				Document placesDoc = parser.newDocument();
				Element data = placesDoc.createElement("data");
				placesDoc.appendChild(data);
				Element processData = placesDoc.createElement("processdata");
				data.appendChild(processData);
				processData.setAttribute("name",dataobject.getId());
				
				// receive elements
				String modelXML = dataobject.getModel();
				Document doc = parser.parse(new InputSource(new StringReader(modelXML)));
				Node dataElement = (Element) doc.getDocumentElement().getElementsByTagName("xsd:element").item(0);
				
				for (; dataElement != null; dataElement = dataElement.getNextSibling()) {
					// fetch elements
					if (dataElement.getNodeType() != Node.ELEMENT_NODE) continue;
					String nodeName = dataElement.getAttributes().getNamedItem("name").getTextContent().replaceAll(" " , "");
					String nodeType = dataElement.getAttributes().getNamedItem("type").getTextContent();
					
					// append dataElements to dataPlaceModel
					Element element = placesDoc.createElement(nodeName);
					element.setAttribute("type", nodeType);
					processData.appendChild(element);
					dataPlace.setModel(domToString(placesDoc));
					
					// for dataplaces add locators
					dataPlace.addLocator(new Locator(
							nodeName, nodeType, "/data/processdata/"+nodeName));
				};
			}
		} catch (Exception io) {
			io.printStackTrace();
		}
	}
	
	public TransformationTransition addTransformationTransition(PetriNet net, String id, String task, String action, String xsltURL) {
		TransformationTransition t =((ExecPNFactoryImpl) pnfactory).createTransformationTransition();
		t.setId(id);
		t.setLabel(id);
		t.setTask(task);
		t.setAction(action);
		t.setXsltURL(xsltURL);
		net.getTransitions().add(t);
		return t;
	}
	
	public ExecFlowRelationship addExecFlowRelationship(PetriNet net,
			de.hpi.petrinet.Node source, de.hpi.petrinet.Node target, String xsltURL) {
		if (source == null || target == null)
			return null;
		ExecFlowRelationship rel = ((ExecPNFactoryImpl) pnfactory).createExecFlowRelationship();
		rel.setSource(source);
		rel.setTarget(target);
		rel.setTransformationURL(xsltURL);
		net.getFlowRelationships().add(rel);
		return rel;
	}
	
	public ExecFlowRelationship addReadOnlyExecFlowRelationship(PetriNet net,
			de.hpi.petrinet.Place source, de.hpi.petrinet.Transition target, String xsltURL) {	
		ExecFlowRelationship rel = addExecFlowRelationship(net, source, target, xsltURL);
		if (rel == null){
			return null;
		}
		rel.setMode(FlowRelationship.RELATION_MODE_READTOKEN);
		return rel;
	}
	
	public FormTransition addFormTransition(PetriNet net, String id, String task) {
		FormTransition t = ((ExecPNFactoryImpl)pnfactory).createFormTransition();
		t.setId(id);
		t.setLabel(id);
		t.setTask(task);
		net.getTransitions().add(t);
		return t;
	}

	public AutomaticTransition addAutomaticTransition(PetriNet net, String id, String task) {
		AutomaticTransition t = ((ExecPNFactoryImpl)pnfactory).createAutomaticTransition();
		t.setId(id);
		t.setLabel(id);
		t.setTask(task);
		t.setXsltURL(copyXsltURL);
		net.getTransitions().add(t);
		return t;
	}
	
	public ExecPlace addPlace(PetriNet net, String id, ExecPlace.Type type) {
		ExecPlace p = ((ExecPNFactoryImpl)pnfactory).createPlace();
		p.setId(id);
		if (type != null)
			p.setType(type);
		net.getPlaces().add(p);
		return p;
	}
	
	Document buildFormTemplate(DocumentBuilder parser)
				throws IOException, SAXException{
		Document template = parser.parse(new File(contextPath+"execution/form.xml"));
		return template;
	}
	
	// build Structure for forms: go for all data/processdata child nodes
	Document addFormFields(Document doc, HashMap<String, Node> processdataMap) {
		Node root = doc.getFirstChild();
		
		Element div = doc.createElement("div");
		div.setAttribute("class", "formatted");
		root.appendChild(div);
		
		div.appendChild(doc.createElement("br"));

		Element table = doc.createElement("table");
		div.appendChild(table);
		
		Element trhead = doc.createElement("tr");
		table.appendChild(trhead);
		
		Element th1 = doc.createElement("th");
		trhead.appendChild(th1);
		Element th2 = doc.createElement("th");
		trhead.appendChild(th2);
		Element th3 = doc.createElement("th");
		trhead.appendChild(th3);
		Element th4 = doc.createElement("th");
		trhead.appendChild(th4);
		Element th5 = doc.createElement("th");
		trhead.appendChild(th5);
		
		th3.setAttribute("align", "left");
		th3.setTextContent("Values");
		
		Element xgroup1 = doc.createElement("x:group");
		th5.appendChild(xgroup1);
		xgroup1.setAttribute("ref", "instance('ui_settings')/delegationstate");
		xgroup1.setTextContent("Visibility Rights");
		
		Element xgroup2 = doc.createElement("x:group");
		th5.appendChild(xgroup2);
		xgroup2.setAttribute("ref", "instance('ui_settings')/reevaluationstate");
		xgroup2.setTextContent("Recent Values");

		for (String dataObjectName : processdataMap.keySet()){
			Node processDataChild = processdataMap.get(dataObjectName);
			do {
				if (processDataChild == null) break;
				if (processDataChild.getNodeType() != Node.ELEMENT_NODE) continue;
				String attributeName, attributeType;
				Node name = processDataChild.getAttributes().getNamedItem("name");
				if (name == null) continue;
				attributeName = name.getNodeValue().replaceAll(" " , "");
				Node type = processDataChild.getAttributes().getNamedItem("type");
				if (type == null) continue;
				attributeType = type.getNodeValue();
				
				// for template watch in engine folder "/public/examples/delegation/formulartemplate"
				// ==========  create Document  ===========			

				Element trbody = doc.createElement("tr");
				table.appendChild(trbody);
				
				Element td1 = doc.createElement("td");
				trbody.appendChild(td1);
				Element td2 = doc.createElement("td");
				trbody.appendChild(td2);
				Element td3 = doc.createElement("td");
				trbody.appendChild(td3);
				Element td4 = doc.createElement("td");
				trbody.appendChild(td4);
				Element td5 = doc.createElement("td");
				trbody.appendChild(td5);
				
				td1.setTextContent(name.getNodeValue());
				
				Element nameinput = doc.createElement("x:input");
				nameinput.setAttribute("ref", "instance('output-token')/places/processdata[@name='"+dataObjectName+"']/" + attributeName);
				nameinput.setAttribute("class", "inputclass");
				td3.appendChild(nameinput);
				
				Element buttongroup = doc.createElement("x:group");
				buttongroup.setAttribute("ref", "instance('ui_settings')/delegationstate");
				td5.appendChild(buttongroup);
				
				// SWITCH Button
				Element switch1 = doc.createElement("x:switch");
				buttongroup.appendChild(switch1);	
				
				Element case1 = doc.createElement("x:case");
				case1.setAttribute("id", attributeName + "_writable");
				switch1.appendChild(case1);	
				
					Element xtrigger1 = doc.createElement("x:trigger");
					xtrigger1.setAttribute("appearance", "minimal");
					case1.appendChild(xtrigger1);
				
					Element image1 = doc.createElement("img");
					image1.setAttribute("src", "/images/buttons_writable.png");
					xtrigger1.appendChild(image1);
					
					Element xtoggle1 = doc.createElement("x:toggle");
					xtoggle1.setAttribute("case", attributeName + "_readonly");
					xtoggle1.setAttribute("ev:event", "DOMActivate");
					xtrigger1.appendChild(xtoggle1);
					
					Element setvalue1_1 = doc.createElement("x:setvalue");
					setvalue1_1.setAttribute("bind", dataObjectName+"_"+attributeName +".futurereadonly");
					setvalue1_1.setTextContent("true");
					xtrigger1.appendChild(setvalue1_1);
					
					Element setvalue1_2 = doc.createElement("x:setvalue");
					setvalue1_2.setAttribute("bind", dataObjectName+"_"+attributeName +".futurewritable");
					setvalue1_2.setTextContent("false");
					xtrigger1.appendChild(setvalue1_2);
				
				Element case2 = doc.createElement("x:case");
				case2.setAttribute("id", attributeName + "_readonly");
				switch1.appendChild(case2);	
				
					Element xtrigger2 = doc.createElement("x:trigger");
					xtrigger2.setAttribute("appearance", "minimal");
					case2.appendChild(xtrigger2);
				
					Element image2 = doc.createElement("img");
					image2.setAttribute("src", "/images/buttons_readonly.png");
					xtrigger2.appendChild(image2);
					
					Element xtoggle2 = doc.createElement("x:toggle");
					xtoggle2.setAttribute("case", attributeName + "_invisible");
					xtoggle2.setAttribute("ev:event", "DOMActivate");
					xtrigger2.appendChild(xtoggle2);
					
					Element setvalue2_1 = doc.createElement("x:setvalue");
					setvalue2_1.setAttribute("bind", dataObjectName+"_"+attributeName +".futurereadonly");
					setvalue2_1.setTextContent("false");
					xtrigger2.appendChild(setvalue2_1);
					
					Element setvalue2_2 = doc.createElement("x:setvalue");
					setvalue2_2.setAttribute("bind", dataObjectName+"_"+attributeName +".futurewritable");
					setvalue2_2.setTextContent("false");
					xtrigger2.appendChild(setvalue2_2);
				
				Element case3 = doc.createElement("x:case");
				case3.setAttribute("id", attributeName + "_invisible");
				switch1.appendChild(case3);	
				
					Element xtrigger3 = doc.createElement("x:trigger");
					xtrigger3.setAttribute("appearance", "minimal");
					case3.appendChild(xtrigger3);
				
					Element image3 = doc.createElement("img");
					image3.setAttribute("src", "/images/buttons_invisible.png");
					xtrigger3.appendChild(image3);
					
					Element xtoggle3 = doc.createElement("x:toggle");
					xtoggle3.setAttribute("case", attributeName + "_writable");
					xtoggle3.setAttribute("ev:event", "DOMActivate");
					xtrigger3.appendChild(xtoggle3);
					
					Element setvalue3_1 = doc.createElement("x:setvalue");
					setvalue3_1.setAttribute("bind", dataObjectName+"_"+attributeName +".futurereadonly");
					setvalue3_1.setTextContent("false");
					xtrigger3.appendChild(setvalue3_1);
					
					Element setvalue3_2 = doc.createElement("x:setvalue");
					setvalue3_2.setAttribute("bind", dataObjectName+"_"+attributeName +".futurewritable");
					setvalue3_2.setTextContent("true");
					xtrigger3.appendChild(setvalue3_2);
				// end SWITCH Button
				
				Element reevaluationgroup = doc.createElement("x:group");
				reevaluationgroup.setAttribute("ref", "instance('ui_settings')/reevaluationstate");
				td5.appendChild(reevaluationgroup);
				
				// REEVALUATION values displaying
				
				// end REEVALUATION values displaying
				
				div.appendChild(doc.createElement("br"));
				div.appendChild(doc.createElement("br"));
				
				
			} while ((processDataChild = processDataChild.getNextSibling()) != null);
		}
		
		{
			Element group = doc.createElement("x:group");
			group.setAttribute("bind", "delegationstate");
			root.appendChild(group);
			
			//delegateButton
			Element deltrigger = doc.createElement("x:trigger");
			deltrigger.setAttribute("ref", "instance('ui_settings')/delegatebutton");
			group.appendChild(deltrigger);
			
			Element dellabel = doc.createElement("x:label");
			dellabel.setTextContent("Delegate");
			deltrigger.appendChild(dellabel);
			
			Element delaction = doc.createElement("x:action");
			delaction.setAttribute("ev:event", "DOMActivate");
			deltrigger.appendChild(delaction);
			
			Element delaction2 = doc.createElement("x:action");
			delaction2.setAttribute("ev:event", "DOMActivate");
			deltrigger.appendChild(delaction2);
			
			Element send = doc.createElement("x:send");
			send.setAttribute("submission", "form1");
			delaction2.appendChild(send);
			
				//values
				Element value1 = doc.createElement("x:setvalue");
				value1.setAttribute("bind", "isDelegated");
				value1.setTextContent("false");
				delaction.appendChild(value1);
				
				Element value2 = doc.createElement("x:setvalue");
				value2.setAttribute("bind", "isReviewed");
				value2.setTextContent("false");
				delaction.appendChild(value2);
				
				Element value3 = doc.createElement("x:setvalue");
				value3.setAttribute("bind", "delegatedFrom");
				value3.setAttribute("value", "instance('output-token')/places/metadata/owner");
				delaction.appendChild(value3);
				
				Element value4 = doc.createElement("x:setvalue");
				value4.setAttribute("bind", "firstOwner");
				value4.setAttribute("value", "instance('output-token')/places/metadata/owner");
				delaction.appendChild(value4);
				
				Element value5 = doc.createElement("x:setvalue");
				value5.setAttribute("bind", "firstOwner.readonly");
				value5.setTextContent("true");
				delaction.appendChild(value5);
				
				Element value6 = doc.createElement("x:setvalue");
				value6.setAttribute("bind", "owner");
				value6.setAttribute("value", "instance('output-token')/places/metadata/delegate");
				delaction.appendChild(value6);
				
				for (String dataObjectName : processdataMap.keySet()){
					Node processDataChild = processdataMap.get(dataObjectName);
					do {
						if (processDataChild == null) break;
						if (processDataChild.getNodeType() != Node.ELEMENT_NODE) continue;
						String attributeName, attributeType;
						Node name = processDataChild.getAttributes().getNamedItem("name");
						if (name == null) continue;
						attributeName = name.getNodeValue().replaceAll(" " , "");
						Node type = processDataChild.getAttributes().getNamedItem("type");
						if (type == null) continue;
						attributeType = type.getNodeValue();
			
					Element valuereadonly = doc.createElement("x:setvalue");
					valuereadonly.setAttribute("bind", dataObjectName+"_"+attributeName + ".readonly");
					valuereadonly.setAttribute("value", "instance('ui_settings')/"+dataObjectName+"_"+attributeName+"/@futurereadonly = 'true'");
					delaction.appendChild(valuereadonly);
					
					Element valuewritable = doc.createElement("x:setvalue");
					valuewritable.setAttribute("bind", dataObjectName+"_"+attributeName + ".writable");
					valuewritable.setAttribute("value", "instance('ui_settings')/"+dataObjectName+"_"+attributeName+"/@futurewritable = 'true'");
					delaction.appendChild(valuewritable);
					} while ((processDataChild = processDataChild.getNextSibling()) != null);
				
				}
			
			//cancelButton
			Element canceltrigger = doc.createElement("x:trigger");
			canceltrigger.setAttribute("ref", "instance('ui_settings')/cancelbutton");
			group.appendChild(canceltrigger);
			
			Element cancellabel = doc.createElement("x:label");
			cancellabel.setTextContent("Cancel");
			canceltrigger.appendChild(cancellabel);
			
			Element cancelaction = doc.createElement("x:action");
			cancelaction.setAttribute("ev:event", "DOMActivate");
			canceltrigger.appendChild(cancelaction);
			
			Element cancelaction2 = doc.createElement("x:action");
			cancelaction2.setAttribute("ev:event", "DOMActivate");
			canceltrigger.appendChild(cancelaction2);
			
			Element cancelsend = doc.createElement("x:send");
			cancelsend.setAttribute("submission", "form1");
			cancelaction2.appendChild(cancelsend);
			
				//values
				Element cancelvalue1 = doc.createElement("x:setvalue");
				cancelvalue1.setAttribute("bind", "isDelegated");
				cancelvalue1.setTextContent("false");
				cancelaction.appendChild(cancelvalue1);
				
				Element cancelvalue2 = doc.createElement("x:setvalue");
				cancelvalue2.setAttribute("bind", "isReviewed");
				cancelvalue2.setTextContent("false");
				cancelaction.appendChild(cancelvalue2);
					
		}
		
		{
			Element group = doc.createElement("x:group");
			group.setAttribute("bind", "reviewstate");
			root.appendChild(group);
			
			//reviewsubmitbutton
			Element reviewtrigger = doc.createElement("x:trigger");
			reviewtrigger.setAttribute("ref", "instance('ui_settings')/reviewsubmitbutton");
			group.appendChild(reviewtrigger);
			
			Element reviewlabel = doc.createElement("x:label");
			reviewlabel.setTextContent("Review");
			reviewtrigger.appendChild(reviewlabel);
			
			Element reviewaction = doc.createElement("x:action");
			reviewaction.setAttribute("ev:event", "DOMActivate");
			reviewtrigger.appendChild(reviewaction);
			
			Element reviewaction2 = doc.createElement("x:action");
			reviewaction2.setAttribute("ev:event", "DOMActivate");
			reviewtrigger.appendChild(reviewaction2);
			
			Element reviewsend = doc.createElement("x:send");
			reviewsend.setAttribute("submission", "form1");
			reviewaction2.appendChild(reviewsend);
			
				//values
				Element reviewvalue1 = doc.createElement("x:setvalue");
				reviewvalue1.setAttribute("bind", "wantToReview");
				reviewvalue1.setTextContent("false");
				reviewaction.appendChild(reviewvalue1);
				
				Element reviewvalue2 = doc.createElement("x:setvalue");
				reviewvalue2.setAttribute("bind", "isReviewed");
				reviewvalue2.setTextContent("false");
				reviewaction.appendChild(reviewvalue2);
			
			
		}
		
		{
			Element group = doc.createElement("x:group");
			group.setAttribute("bind", "executionstate");
			root.appendChild(group);
			
			//submitButton
			Element submittrigger = doc.createElement("x:trigger");
			submittrigger.setAttribute("ref", "instance('ui_settings')/submitbutton");
			group.appendChild(submittrigger);
			
			Element submitlabel = doc.createElement("x:label");
			submitlabel.setTextContent("Submit");
			submittrigger.appendChild(submitlabel);
			
			Element submitaction = doc.createElement("x:action");
			submitaction.setAttribute("ev:event", "DOMActivate");
			submittrigger.appendChild(submitaction);
			
			Element submitaction2 = doc.createElement("x:action");
			submitaction2.setAttribute("ev:event", "DOMActivate");
			submittrigger.appendChild(submitaction2);
			
			Element send = doc.createElement("x:send");
			send.setAttribute("submission", "form1");
			submitaction2.appendChild(send);
			
				//values
				Element value1 = doc.createElement("x:setvalue");
				value1.setAttribute("bind", "delegatedFrom");
				value1.setTextContent("");
				submitaction.appendChild(value1);
				
				Element value2 = doc.createElement("x:setvalue");
				value2.setAttribute("bind", "isDelegated");
				value2.setTextContent("false");
				submitaction.appendChild(value2);
				
				Element value3 = doc.createElement("x:setvalue");
				value3.setAttribute("bind", "isReviewed");
				value3.setTextContent("true");
				submitaction.appendChild(value3);
				
				Element value4 = doc.createElement("x:setvalue");
				value4.setAttribute("bind", "owner");
				value4.setAttribute("value", "instance('output-token')/places/metadata/firstOwner");
				submitaction.appendChild(value4);
				
				for (String dataObjectName : processdataMap.keySet()){
					Node processDataChild = processdataMap.get(dataObjectName);
					do {
						if (processDataChild == null) break;
						if (processDataChild.getNodeType() != Node.ELEMENT_NODE) continue;
						String attributeName, attributeType;
						Node name = processDataChild.getAttributes().getNamedItem("name");
						if (name == null) continue;
						attributeName = name.getNodeValue().replaceAll(" " , "");
						Node type = processDataChild.getAttributes().getNamedItem("type");
						if (type == null) continue;
						attributeType = type.getNodeValue();
				
					Element valuereadonly = doc.createElement("x:setvalue");
					valuereadonly.setAttribute("bind", dataObjectName+"_"+attributeName + ".readonly");
					valuereadonly.setTextContent("false");
					submitaction.appendChild(valuereadonly);
					
					Element valuewritable = doc.createElement("x:setvalue");
					valuewritable.setAttribute("bind", dataObjectName+"_"+attributeName + ".writable");
					valuewritable.setTextContent("true");
					submitaction.appendChild(valuewritable);
					
					} while ((processDataChild = processDataChild.getNextSibling()) != null);
				
				}
			
			// interactButton
			Element interacttrigger = doc.createElement("x:trigger");
			interacttrigger.setAttribute("ref", "instance('ui_settings')/interactbutton");
			group.appendChild(interacttrigger);
			
			Element interactlabel = doc.createElement("x:label");
			interactlabel.setTextContent("Interact");
			interacttrigger.appendChild(interactlabel);
			
			Element interactaction = doc.createElement("x:action");
			interactaction.setAttribute("ev:event", "DOMActivate");
			interacttrigger.appendChild(interactaction);
			
			Element interactaction2 = doc.createElement("x:action");
			interactaction2.setAttribute("ev:event", "DOMActivate");
			interacttrigger.appendChild(interactaction2);
			
			Element interactsend = doc.createElement("x:send");
			interactsend.setAttribute("submission", "form1");
			interactaction2.appendChild(interactsend);
			
				//values
				Element interactvalue1 = doc.createElement("x:setvalue");
				interactvalue1.setAttribute("bind", "isDelegated");
				interactvalue1.setTextContent("true");
				interactaction.appendChild(interactvalue1);
				
				Element interactvalue2 = doc.createElement("x:setvalue");
				interactvalue2.setAttribute("bind", "isReviewed");
				interactvalue2.setTextContent("false");
				interactaction.appendChild(interactvalue2);
		}
		
			// ==========  end of creation ============
		
		
		return doc;

	}
	
	Document buildBindingsDocument(DocumentBuilder parser) 
				throws IOException, SAXException{
		Document bindDoc = parser.parse(new File(contextPath+"execution/bindings.xml"));
		return bindDoc;
		
	}
	
	// build structure for bindings
	Document addBindings(Document doc, HashMap<String, Node> processdataMap) {
		Node root = doc.getFirstChild();
		
		//add necessary ui_settings nodes
		NodeList list = doc.getDocumentElement().getElementsByTagName("x:instance");
		for (int i = 0; i < list.getLength(); i++) {
			NamedNodeMap attributes = list.item(i).getAttributes();
			if (attributes.getNamedItem("id").getNodeValue().equals("ui_settings")) {
				
				for (String dataObjectName : processdataMap.keySet()){
					Node processDataChild = processdataMap.get(dataObjectName);
					do {
						if (processDataChild == null) break;
						if (processDataChild.getNodeType() != Node.ELEMENT_NODE) continue;
						String attributeName, attributeType;
						Node name = processDataChild.getAttributes().getNamedItem("name");
						if (name == null) continue;
						attributeName = name.getNodeValue().replaceAll(" " , "");
						Node type = processDataChild.getAttributes().getNamedItem("type");
						if (type == null) continue;
						attributeType = type.getNodeValue();
						
						Element property = doc.createElement(dataObjectName + "_" + attributeName);
						property.setAttribute("futurereadonly", "false");
						property.setAttribute("futurewritable", "false");
						for (Node ui_setting_nodes = list.item(i).getFirstChild(); ; ui_setting_nodes = ui_setting_nodes.getNextSibling()) {
							if (ui_setting_nodes.getNodeName().equals("#text")) continue;
							ui_setting_nodes.appendChild(property);
							break;
						}
						
					} while ((processDataChild = processDataChild.getNextSibling()) != null);
					
				}
			}
		}
		
		// add necessary token bindings
		for (String dataObjectName : processdataMap.keySet()){
			Node processDataChild = processdataMap.get(dataObjectName);
			do {
				if (processDataChild == null) break;
				if (processDataChild.getNodeType() != Node.ELEMENT_NODE) continue;
				String attributeName, attributeType;
				Node name = processDataChild.getAttributes().getNamedItem("name");
				if (name == null) continue;
				attributeName = name.getNodeValue().replaceAll(" " , "");
				Node type = processDataChild.getAttributes().getNamedItem("type");
				if (type == null) continue;
				attributeType = type.getNodeValue();
			
				Element bind1 = doc.createElement("x:bind");
				bind1.setAttribute("id", dataObjectName+"_"+attributeName);
				bind1.setAttribute("nodeset", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName);
				root.appendChild(bind1);
				
				Element bind2 = doc.createElement("x:bind");
				bind2.setAttribute("id", dataObjectName+"_"+attributeName+".readonly");
				bind2.setAttribute("nodeset", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName + "/@readonly");
				root.appendChild(bind2);
				
				Element bind3 = doc.createElement("x:bind");
				bind3.setAttribute("id", dataObjectName+"_"+attributeName+".writable");
				bind3.setAttribute("nodeset", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@writable");
				root.appendChild(bind3);
				
				Element bind4 = doc.createElement("x:bind");
				bind4.setAttribute("type", "xsd:boolean");
				bind4.setAttribute("nodeset", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@readonly");
				root.appendChild(bind4);
				
				Element bind5 = doc.createElement("x:bind");
				bind5.setAttribute("type", "xsd:boolean");
				bind5.setAttribute("nodeset", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@writable");
				root.appendChild(bind5);
				
				Element bind6 = doc.createElement("x:bind");
				bind6.setAttribute("type", "xsd:boolean");
				bind6.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName + "_" +attributeName +"/@futurereadonly");
				bind6.setAttribute("id", dataObjectName + "_" + attributeName + ".futurereadonly");
				root.appendChild(bind6);
				
				Element bind7 = doc.createElement("x:bind");
				bind7.setAttribute("type", "xsd:boolean");
				bind7.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName + "_" +attributeName +"/@futurewritable");
				bind7.setAttribute("id", dataObjectName + "_" + attributeName + ".futurewritable");
				root.appendChild(bind7);
				
				Element bind8 = doc.createElement("x:bind");
				bind8.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName+"_"+attributeName +"/@futurewritable");
				bind8.setAttribute("relevant", "instance('output-token')/places/metadata/isDelegated = 'true' and instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@writable = 'true'");
				root.appendChild(bind8);
				
				Element bind9 = doc.createElement("x:bind");
				bind9.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName+"_"+attributeName +"/@futurereadonly");
				bind9.setAttribute("relevant", "instance('output-token')/places/metadata/isDelegated = 'true' and ((instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@readonly = 'true' and instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@writable != 'true') or instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@writable = 'true')");
				root.appendChild(bind9);
				
				Element bind10 = doc.createElement("x:bind");
				bind10.setAttribute("nodeset", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName);
				bind10.setAttribute("readonly", "instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@readonly = 'true'");
				root.appendChild(bind10);
				
				Element bind11 = doc.createElement("x:bind");
				bind11.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName+"_"+attributeName);
				bind11.setAttribute("relevant", "not(instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@writable != 'true' and instance('output-token')/places/processdata[" + "@name='"+dataObjectName+"'" + "]/" +attributeName +"/@readonly != 'true')");
				bind11.setAttribute("id", "fade." + dataObjectName+"_"+attributeName);
				root.appendChild(bind11);
				
				Element bind12 = doc.createElement("x:bind");
				bind12.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName+"_"+attributeName +"/@futurewritable");
				bind12.setAttribute("readonly", "instance('ui_settings')/" + dataObjectName+"_"+attributeName +"/@futurereadonly = 'true'");
				root.appendChild(bind12);
				
				Element bind13 = doc.createElement("x:bind");
				bind13.setAttribute("nodeset", "instance('ui_settings')/" + dataObjectName+"_"+attributeName +"/@futurereadonly");
				bind13.setAttribute("readonly", "instance('ui_settings')/" + dataObjectName+"_"+attributeName +"/@futurewritable = 'true'");
				root.appendChild(bind13);
				
			} while ((processDataChild = processDataChild.getNextSibling()) != null);
		}
			
		return doc;
		
	}
	
	@Override
	protected ConversionContext setupConversionContext() {
		return new ExecConversionContext();
	}
	
    /** 
     * Writes *requestData* to url *targetURL* as form-data 
     * This is the same as pressing submit on a POST type HTML form 
     * Throws: 
     * MalformedURLException for bad URL String 
     * IOException when connection can not be made, or error when 
     * reading response 
     * TODO: HOW DOES IT WORK KAI??
     * 
     * @param requestData string to POST to the URL. 
     * empty for no post data 
     * @param targetURL string of url to post to 
     * and read response. 
     * @return string response from url. 
     */ 
     private String postDataToURL(String requestData, 
                                     String targetURL) 
                                 throws MalformedURLException, IOException { 
                String URLResponse = ""; 
                // open the connection and prepare it to POST 
                URL url = new URL(targetURL); 
                HttpURLConnection URLconn = (HttpURLConnection) url.openConnection(); 
                URLconn.setDoOutput(true); 
                URLconn.setDoInput(true); 
                URLconn.setAllowUserInteraction(false); 
                URLconn.setUseCaches (false);
                URLconn.setRequestMethod("POST");
                URLconn.setRequestProperty("Content-Length", "" + requestData.length());
                URLconn.setRequestProperty("Content-Language", "en-US");  
                URLconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                URLconn.setRequestProperty("Authorization", "Basic a2Fp0g==");
                DataOutputStream dataOutStream = 
                        new DataOutputStream(URLconn.getOutputStream()); 
                // Send the data 
                dataOutStream.writeBytes(requestData); 
                dataOutStream.flush();
                dataOutStream.close(); 
                // Read the response 
                int resp = URLconn.getResponseCode();
                if (URLconn.getResponseCode() == HttpURLConnection.HTTP_OK)
                	URLResponse = URLconn.getURL().toString(); 
                else
                	throw new IOException("ResponseCode for post to engine was not OK!");
//                while((nextLine = reader.readLine()) != null) { 
//                	URLResponse += nextLine; 
//                } 
//                reader.close(); 
                 
                return URLResponse; 
        } 
     
     /**
      * checks if such a <processdata id="..."> tag exists, that conforms to dataobject id 
      * @param dataObject DataObject, that could be already mapped to the <data> childs
      * @param list List of <data> childs
      * @return is tag existent?
      */
		private boolean isContainedIn(ExecDataObject dataObject, NodeList list){
			for (int procdata = 0; procdata<list.getLength(); procdata++) {
				NamedNodeMap attributeList = list.item(procdata).getAttributes();
				for (int attr = 0; attr<attributeList.getLength(); attr++) {
					Node attribute = attributeList.item(attr);
					if (list.item(procdata).getNodeName() == "processData" && attribute.equals(dataObject.getId()))
						return true;
				}
			}
			return false;
		}
		
		private boolean isContainedIn(ExecDataObject dataObject, HashMap<String, Node> map){
			Set<String> list = map.keySet();
			if (list.contains(dataObject.getId()))
				return true;
			return false;
		}
		
		/**
		 * returns a map with key dataPlace (conforms to dataObject id) and attributes of dataObject
		 * @param data
		 * @return
		 */
		private HashMap<String, NodeList> getProcessDataNodes(Node data) {
			HashMap<String, NodeList> targetMap = new HashMap<String, NodeList>();
			NodeList list = data.getChildNodes();
			for (int i = 0; i<list.getLength(); i++) {
				if (list.item(i).getNodeName().equals("processdata")) {
					String id = list.item(i).getAttributes().getNamedItem("id").getNodeName();
					NodeList attributes = list.item(i).getChildNodes();
					targetMap.put(id, attributes);
				}
			}
			return targetMap;
		}
		
		private String domToString(Document document) {
			// Normalizing the DOM
	        document.getDocumentElement().normalize();
            StringWriter domAsString = new StringWriter();
	        
	        try {
	            // Prepare the DOM document for writing
	            Source source = new DOMSource(document);

	            // Prepare the output file
	            Result result = new StreamResult(domAsString);

	            // Write the DOM document to the file
	            // Get Transformer
	            Transformer transformer = TransformerFactory.newInstance()
	                    .newTransformer();
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

	            // Write to a String
	            transformer.transform(source, result);

	        } catch (TransformerConfigurationException e) {
	            System.out.println("TransformerConfigurationException: " + e);
	        } catch (TransformerException e) {
	            System.out.println("TransformerException: " + e);
	        }
	        
	        return domAsString.toString();
		}
		
		String create_extract_processdata_xsl(ExecDataObject dataObject, DocumentBuilder parser)
															throws IOException{
			Document xslDoc = parser.newDocument();
			
			Element stylesheet = xslDoc.createElement("xsl:stylesheet");
			stylesheet.setAttribute("version", "1.0");
			stylesheet.setAttribute("xmlns:xsl", "http://www.w3.org/1999/XSL/Transform");
			xslDoc.appendChild(stylesheet);
			
			/* */
			Element template = xslDoc.createElement("xsl:template");
			template.setAttribute("match", "/");
			stylesheet.appendChild(template);
			
			Element data = xslDoc.createElement("data");
			template.appendChild(data);
			
			Element apply = xslDoc.createElement("xsl:apply-templates");
			template.setAttribute("select", "/places/processdata");
			data.appendChild(apply);
			
			
			Element template2 = xslDoc.createElement("xsl:template");
			template2.setAttribute("match", "processdata[@target_place=\'pl_data_"+ dataObject.getId() +"\']");
			stylesheet.appendChild(template2);
			
			Element processdata = xslDoc.createElement("processdata");
			template2.appendChild(processdata);
			
			Element attribute1 = xslDoc.createElement("xsl:attribute");
			attribute1.setAttribute("name", "target_place");
			processdata.appendChild(attribute1);
			Element value1 = xslDoc.createElement("xsl:value-of");
			value1.setAttribute("select", "@target_place");
			attribute1.appendChild(value1);
			
			Element attribute2 = xslDoc.createElement("xsl:attribute");
			attribute2.setAttribute("name", "place_id");
			processdata.appendChild(attribute2);
			Element value2 = xslDoc.createElement("xsl:value-of");
			value2.setAttribute("select", "@place_id");
			attribute2.appendChild(value2);
			
			Element attribute3 = xslDoc.createElement("xsl:attribute");
			attribute3.setAttribute("name", "name");
			processdata.appendChild(attribute3);
			Element value3 = xslDoc.createElement("xsl:value-of");
			value3.setAttribute("select", "@name");
			attribute3.appendChild(value3);
			
			Element copy = xslDoc.createElement("xsl:copy-of");
			copy.setAttribute("select", "./*");
			processdata.appendChild(copy);
			
			
			Element template3 = xslDoc.createElement("xsl:template");
			template3.setAttribute("match", "metadata/*");
			stylesheet.appendChild(template3);
			
			Element template4 = xslDoc.createElement("xsl:template");
			template4.setAttribute("match", "processdata/*");
			stylesheet.appendChild(template4);
			
			/* Test 
			Element template = xslDoc.createElement("xsl:template");
			template.setAttribute("match", "/");
			stylesheet.appendChild(template);
			
			Element data = xslDoc.createElement("data");
			template.appendChild(data);

			Element copy = xslDoc.createElement("xsl:copy-of");
			copy.setAttribute("select", "*");
			data.appendChild(copy);
			*/
			
			return postDataToURL(domToString(xslDoc),enginePostURL);
		}

}

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
import java.util.List;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
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
	// TODO: Resolve this by using pnengine.properties - Engine does not have to be local!
	private static final String enginePostURL = "http://localhost:3000/documents/";
	private static String contextPath;
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
	
	/*
	@Override
	public PetriNet convert () {
		// !! no start place for petrinet
		// but create a start transition 
		ExecPetriNet pn = (ExecPetriNet) super.convert();
		AutomaticTransition startTransition = addAutomaticTransition(pn, "tr_initProcess", "", "initProcess", copyXsltURL, false);
		pn.setStartTransition(startTransition);
		Vector <Place> dataPlaces = ExecTask.pl_dataPlaces;
		for (Place place : dataPlaces)
			addFlowRelationship(pn, startTransition, place);
		return pn;
	}
	// connect startTransition with contextplaces
	*/

	// TODO this is a dirty hack...
	@Override
	protected void handleTask(PetriNet net, Task task, ConversionContext c) {
		ExecTask exTask = new ExecTask();
		exTask.setId(task.getId());
		exTask.setLabel(task.getLabel());
		exTask.setResourceId(task.getResourceId());
		String taskDesignation = exTask.getTaskDesignation();
		
		// create proper model, form and bindings
		String model = null;
		String form = null;
		String bindings = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance (  ) ; 
		
		try {
			DocumentBuilder parser = factory.newDocumentBuilder (  ) ; 
		
			Document modelDoc = parser.newDocument();
			Element dataEl = modelDoc.createElement("data");
			Node data = modelDoc.appendChild(dataEl);
			Node metaData = data.appendChild(modelDoc.createElement("metadata"));
			Node processData = data.appendChild(modelDoc.createElement("processdata"));			
			
			//TODO: use metadata_model.xml instead of building it by hand
			// create MetaData Layout for Task model
			// (take attention to the fact, that the attributes are again defined in the engine)
			/*Node startTime 		=*/ metaData.appendChild(modelDoc.createElement("startTime"));
			/*Node endTime 			=*/ metaData.appendChild(modelDoc.createElement("endTime"));
			/*Node status 			=*/ metaData.appendChild(modelDoc.createElement("status"));
			/*Node owner 			=*/ metaData.appendChild(modelDoc.createElement("owner"));
			/*Node isDelegated 		=*/ metaData.appendChild(modelDoc.createElement("isDelegated"));
			/*Node reviewRequested 	=*/ metaData.appendChild(modelDoc.createElement("reviewRequested"));
			/*Node firstOwner 		=*/ metaData.appendChild(modelDoc.createElement("firstOwner"));
			/*Node actions 			=*/ metaData.appendChild(modelDoc.createElement("actions"));
			
			// create MetaData Layout Actions, that will be logged --> here not regarded
			
			// interrogate all incoming data objects for task, create DataPlaces for them and create Task model
			List<Edge> edges_in = task.getIncomingEdges();
			for (Edge edge : edges_in) {
				if (edge.getSource() instanceof ExecDataObject) {
					ExecDataObject dataObject = (ExecDataObject)edge.getSource();
					// for incoming data objects of task: create read dependencies
					addReadOnlyExecFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_enable, null);
					// create XML Structure for Task
					String modelXML = dataObject.getModel();
					try {
						Document doc = parser.parse(new InputSource(new StringReader(modelXML)));
						Node dataObjectId = processData.appendChild(modelDoc.createElement(dataObject.getId()));
						Node dataTagOfDataModel = doc.getDocumentElement().getFirstChild();
						Node child = dataTagOfDataModel.getNextSibling();
						while (child != null) {
							Node attributeToAttach = modelDoc.importNode(child.cloneNode(true), true);
							dataObjectId.appendChild(attributeToAttach);
							child = child.getNextSibling();
						};
					} catch (Exception io) {
						io.printStackTrace();
					}
				}
			}
			
			// interrogate all outgoing data objects for task and create flow relationships for them
			List<Edge> edges_out = task.getOutgoingEdges();
			for (Edge edge : edges_out) {
				if (edge.getTarget() instanceof ExecDataObject) {
					ExecDataObject dataObject = (ExecDataObject)edge.getTarget();
					// for outgoing data objects of task: create read/write dependencies
					NodeList list = processData.getChildNodes();
					String modelXML = dataObject.getModel();
					if (!isContainedIn(dataObject, list)) {
						// if object is already read, then not neccessary
						Document doc = parser.parse(new InputSource(new StringReader(modelXML)));
						Node dataObjectId = processData.appendChild(modelDoc.createElement(dataObject.getId()));
						Node dataTagOfDataModel = doc.getDocumentElement().getFirstChild();
						Node child = dataTagOfDataModel.getNextSibling();
						while (child != null) {
							Node attributeToAttach = modelDoc.importNode(child.cloneNode(true), true);
							dataObjectId.appendChild(attributeToAttach);
							child = child.getNextSibling();
						};
						addReadOnlyExecFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_enable, null);
					}
					addFlowRelationship(net, exTask.tr_finish, ExecTask.getDataPlace(dataObject.getId()));
					addFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_finish);
				}
			}
			

			// build form Document template without attributes
			Document formDoc = buildFormTemplate(parser);
			// adds form fields of necessary attributes
			formDoc = addFormFields(formDoc, processData);
			// build bindings Document for attributes
			Document bindDoc = buildBindingsDocument(parser);
			// adds binding attributes for tags
			bindDoc = addBindings(bindDoc, processData);
			
			// append metadata for internal tokenmodel of transitions
			Element message = modelDoc.createElement("message");
			Element deadline = modelDoc.createElement("deadline");
			Element wantToReview = modelDoc.createElement("wantToReview");
			
			processData.appendChild(message);
			processData.appendChild(deadline);
			processData.appendChild(wantToReview);
			
			message.setAttribute("readonly", "true");
			deadline.setAttribute("readonly", "true");
			wantToReview.setTextContent("false");
			
			// persist form and bindings and save URL
			model = this.postDataToURL(domToString(modelDoc),enginePostURL);
			form = this.postDataToURL(domToString(formDoc),enginePostURL);
			
			String bindingsString = domToString(bindDoc);
			bindings = this.postDataToURL(
					bindingsString.replace("<bindings>", "").replace("</bindings>", ""),
					enginePostURL);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} catch (SAXException sax) {
			sax.printStackTrace();
		}
		
		exTask.pl_inited = addPlace(net, "pl_inited_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_ready = addPlace(net, "pl_ready_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_running = addPlace(net, "pl_running_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_deciding = addPlace(net, "pl_deciding_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_suspended = addPlace(net, "pl_suspended_" + task.getId(), ExecPlace.Type.flow);
		exTask.pl_complete = addPlace(net, "pl_complete_" + task.getId(), ExecPlace.Type.flow);	
		exTask.pl_context = addPlace(net, "pl_context_" + task.getId(), ExecPlace.Type.context);

		// add role dependencies
		String rolename = task.getRolename();
		
		// integrate context place
		exTask.pl_context.addLocator(new Locator("startTime", "xsd:string", "/data/metadata/startTime"));
		exTask.pl_context.addLocator(new Locator("endTime", "xsd:string", "/data/metadata/endTime"));
		exTask.pl_context.addLocator(new Locator("status", "xsd:string", "/data/metadata/status"));
		exTask.pl_context.addLocator(new Locator("owner", "xsd:string", "/data/metadata/owner"));
		exTask.pl_context.addLocator(new Locator("isDelegated", "xsd:string", "/data/metadata/isDelegated"));
		exTask.pl_context.addLocator(new Locator("isReviewed", "xsd:string", "/data/metadata/isReviewed"));
		exTask.pl_context.addLocator(new Locator("reviewRequested", "xsd:string", "/data/metadata/reviewRequested"));
		exTask.pl_context.addLocator(new Locator("startTime", "xsd:string", "/data/metadata/firstOwner"));
		exTask.pl_context.addLocator(new Locator("actions", "xsd:string", "/data/metadata/actions"));

		//init transition
		exTask.tr_init = addAutomaticTransition(net, "tr_init_" + task.getId(), taskDesignation);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), exTask.tr_init);
		addFlowRelationship(net, exTask.tr_init, exTask.pl_inited);
		
		//enable transition
		//note: structure of context place must be initialized by engine
		AutomaticTransition enable = addAutomaticTransition(net, "tr_enable_" + task.getId(), taskDesignation);
		exTask.tr_enable = enable;
		//addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), exTask.tr_enable);
		addFlowRelationship(net, exTask.pl_inited, exTask.tr_enable);
		addExecFlowRelationship(net, exTask.tr_enable, exTask.pl_ready, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_enable);
		addExecFlowRelationship(net, exTask.tr_enable, exTask.pl_context, baseXsltURL + "context_enable.xsl");
		enable.setModelURL(model);
		
		// allocate Transition
		exTask.tr_allocate = addTransformationTransition(net, "tr_allocate_" + task.getId(), taskDesignation,"allocate", copyXsltURL);
		addFlowRelationship(net, exTask.pl_ready, exTask.tr_allocate);
		addExecFlowRelationship(net, exTask.tr_allocate, exTask.pl_running, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_allocate);
		addExecFlowRelationship(net, exTask.tr_allocate, exTask.pl_context, baseXsltURL + "context_allocate.xsl");
		exTask.tr_allocate.setRolename(rolename);
		
		if (task.isSkippable()) {
			// skip Transition
			exTask.setSkippable(true);
			exTask.tr_skip = addTransformationTransition(net, "tr_skip_" + task.getId(), taskDesignation, "skip", copyXsltURL);
			addFlowRelationship(net, exTask.pl_ready, exTask.tr_skip);
			addExecFlowRelationship(net, exTask.tr_skip, exTask.pl_complete, extractDataURL);
			addFlowRelationship(net, exTask.pl_context, exTask.tr_skip);
			addExecFlowRelationship(net, exTask.tr_skip, exTask.pl_context, baseXsltURL + "context_skip.xsl");
		}
		
		// submit Transition
		FormTransition submit = addFormTransition(net, "tr_submit_" + task.getId(), task.getLabel(), model, form, bindings);
		submit.setAction("submit");
		exTask.tr_submit = submit;
		addFlowRelationship(net, exTask.pl_running, exTask.tr_submit);
		addExecFlowRelationship(net, exTask.tr_submit, exTask.pl_deciding, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_submit);
		addExecFlowRelationship(net, exTask.tr_submit, exTask.pl_context, baseXsltURL + "context_submit.xsl");
		exTask.tr_submit.setRolename(rolename);
		submit.setFormURL(form);
		submit.setBindingsURL(bindings);
				
		// delegate Transition
		FormTransition delegate = addFormTransition(net, "tr_delegate_" + task.getId(), taskDesignation, model, form, bindings);
		delegate.setAction("delegate");
		delegate.setGuard(exTask.pl_context.getId() + ".isDelegated == 'true'");
		exTask.tr_delegate = delegate;
		addFlowRelationship(net, exTask.pl_deciding, exTask.tr_delegate);
		addExecFlowRelationship(net, exTask.tr_delegate, exTask.pl_running, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_delegate);
		addExecFlowRelationship(net, exTask.tr_delegate, exTask.pl_context, baseXsltURL + "context_delegate.xsl");
		exTask.tr_delegate.setRolename(rolename);
		delegate.setFormURL(form);
		delegate.setBindingsURL(bindings);
		
		// review Transition
		FormTransition review = addFormTransition(net, "tr_review_" + task.getId(), taskDesignation,model,form,bindings);
		review.setAction("review");
		review.setGuard(exTask.pl_context.getId() + ".isDelegated != 'true' && " + exTask.pl_context.getId() + ".reviewRequested == 'true'");
		exTask.tr_review = review;
		addFlowRelationship(net, exTask.pl_deciding, exTask.tr_review);
		addExecFlowRelationship(net, exTask.tr_review, exTask.pl_complete, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_review);
		addExecFlowRelationship(net, exTask.tr_review, exTask.pl_context, baseXsltURL + "context_review.xsl");
		exTask.tr_review.setRolename(rolename);
		review.setFormURL(form);
		review.setBindingsURL(bindings);
		
		// done Transition
		exTask.tr_done = addAutomaticTransition(net, "tr_done_" + task.getId(), taskDesignation);
		addFlowRelationship(net, exTask.pl_deciding, exTask.tr_done);
		addFlowRelationship(net, exTask.tr_done, exTask.pl_complete);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_done);
		addExecFlowRelationship(net, exTask.tr_done, exTask.pl_context, baseXsltURL + "context_done.xsl");
		exTask.tr_done.setGuard(exTask.pl_context.getId() + ".isDelegated != 'true' && " + exTask.pl_context.getId() + ".reviewRequested != 'true'");
		
		// suspend
		exTask.tr_suspend = addTransformationTransition(net, "tr_suspend_" + task.getId(), taskDesignation, "suspend", copyXsltURL);
		addFlowRelationship(net, exTask.pl_running, exTask.tr_suspend);
		addExecFlowRelationship(net, exTask.tr_suspend, exTask.pl_suspended, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_suspend);
		addExecFlowRelationship(net, exTask.tr_suspend, exTask.pl_context, baseXsltURL + "context_suspend.xsl");
		exTask.tr_suspend.setRolename(rolename);
		
		// resume
		exTask.tr_resume = addTransformationTransition(net, "tr_resume_" + task.getId(), taskDesignation, "resume", copyXsltURL);
		addFlowRelationship(net, exTask.pl_suspended, exTask.tr_resume);
		addExecFlowRelationship(net, exTask.tr_resume, exTask.pl_running, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_resume);
		addExecFlowRelationship(net, exTask.tr_resume, exTask.pl_context, baseXsltURL + "context_resume.xsl");
		exTask.tr_resume.setRolename(rolename);
		
		// finish transition
		exTask.tr_finish = addAutomaticTransition(net, "tr_finish_" + task.getId(), taskDesignation);
		addFlowRelationship(net, exTask.pl_complete, exTask.tr_finish);
		addExecFlowRelationship(net, exTask.tr_finish, c.map.get(getOutgoingSequenceFlow(task)), extractDataURL);
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
	 * - Adding second parallel mode
	 * - Connecting auto skip with context places
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
		System.out.println(completionConditionString);
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
				Transition skip = addTauTransition(net, "ad-hoc_skip_task_"	+ exTask.getId());
				Transition finish = addTauTransition(net, "ad-hoc_finish_task_" + exTask.getId());
				
				addFlowRelationship(net, finalize, enableFinalize);
					
				addFlowRelationship(net, enableFinalize, skip);
				addFlowRelationship(net, exTask.pl_ready, skip);
				addFlowRelationship(net, skip, taskFinalized);
					
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
					result.append("true");
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

	
	
	
	@Override
	protected void handleDataObject(PetriNet net, DataObject object, ConversionContext c){
		
		try {
			if (object instanceof ExecDataObject) {
				ExecDataObject dataobject = (ExecDataObject) object;
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance (  ) ; 
				DocumentBuilder parser = factory.newDocumentBuilder (  ) ; 
				
				//create data place for Task
				ExecPlace dataPlace = addPlace(net,"pl_data_"+dataobject.getId(), ExecPlace.Type.data);
				ExecTask.addDataPlace(dataPlace);
				
				// for data place add locators
				String modelXML = dataobject.getModel();
				dataPlace.setModel(modelXML);
				Document doc = parser.parse(new InputSource(new StringReader(modelXML)));
				Node dataTagOfDataModel = doc.getDocumentElement().getFirstChild();
				Node child = dataTagOfDataModel.getFirstChild();
				while (child != null) {
					dataPlace.addLocator(new Locator(
							child.getNodeName(),
							"xsd:string",
							"/data/processdata/"+dataobject.getId()+"/"+child.getNodeName()));
					child = child.getNextSibling();
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
	
	public FormTransition addFormTransition(PetriNet net, String id, String task, String model, String form, String bindings) {
		FormTransition t = ((ExecPNFactoryImpl)pnfactory).createFormTransition();
		t.setId(id);
		t.setLabel(id);
		t.setTask(task);
		t.setFormURL(form);
		t.setBindingsURL(bindings);
		t.setModelURL(model);
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
	
	// build Structure for forms
	Document addFormFields(Document doc, Node processData) {
		NodeList list = processData.getChildNodes();
		Node root = doc.getFirstChild();
		for (int i = 0; i<list.getLength(); i++) {
			String attributeName = list.item(i).getNodeName();
			
			// for template watch in engine folder "/public/examples/delegation/formulartemplate"
			// ==========  create Document  ===========			
			Element div = doc.createElement("div");
			div.setAttribute("class", "formatted");
			root.appendChild(div);
			
			div.appendChild(doc.createElement("br"));
			
			Element div2 = doc.createElement("div");
			div2.setAttribute("id", attributeName + "_grp");
			div.appendChild(div2);
			
			Element input = doc.createElement("x:input");
			input.setAttribute("ref", "instance('ui_settings')/"+ attributeName +"/@futurereadonly");
			input.setAttribute("class", "leftcheckbox");
			div.appendChild(input);
			
			Element input2 = doc.createElement("x:input");
			input2.setAttribute("ref", "instance('ui_settings')/"+ attributeName +"/@futurevisible");
			input2.setAttribute("class", "leftcheckbox");
			div.appendChild(input2);
			
			Element group = doc.createElement("x:group");
			group.setAttribute("bind", "fade."+ attributeName);
			group.setAttribute("class", "fieldgroup");
			div.appendChild(group);
			
			Element label = doc.createElement("x:label");
			Element nameinput = doc.createElement("x:input");
			label.setTextContent(attributeName+": ");
			nameinput.setAttribute("ref", "instance('output-token')/data/processdata/" + attributeName);
			nameinput.setAttribute("class", "inputclass");
			group.appendChild(label);
			
			div.appendChild(doc.createElement("br"));
			div.appendChild(doc.createElement("br"));
			
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
			dellabel.setNodeValue("Delegate");
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
				value1.setNodeValue("false");
				delaction.appendChild(value1);
				
				Element value2 = doc.createElement("x:setvalue");
				value2.setAttribute("bind", "isReviewed");
				value2.setNodeValue("false");
				delaction.appendChild(value2);
				
				Element value3 = doc.createElement("x:setvalue");
				value3.setAttribute("bind", "delegatedFrom");
				value3.setAttribute("value", "instance('output-token')/data/metadata/owner");
				delaction.appendChild(value3);
				
				Element value4 = doc.createElement("x:setvalue");
				value4.setAttribute("bind", "firstOwner");
				value4.setAttribute("value", "instance('output-token')/data/metadata/owner");
				delaction.appendChild(value4);
				
				Element value5 = doc.createElement("x:setvalue");
				value5.setAttribute("bind", "owner.readonly");
				value5.setNodeValue("true");
				delaction.appendChild(value5);
				
				Element value6 = doc.createElement("x:setvalue");
				value6.setAttribute("bind", "owner");
				value6.setAttribute("value", "instance('output-token')/data/metadata/delegate");
				delaction.appendChild(value6);
				
				for (int i = 0; i<list.getLength(); i++) {
					String attributeName = list.item(i).getNodeName();
				
				Element valuereadonly = doc.createElement("x:setvalue");
				valuereadonly.setAttribute("bind", attributeName + ".readonly");
				valuereadonly.setAttribute("value", "instance('ui_settings')/"+attributeName+"/@futurereadonly = 'true'");
				delaction.appendChild(valuereadonly);
				
				Element valuevisible = doc.createElement("x:setvalue");
				valuevisible.setAttribute("bind", attributeName + ".visible");
				valuevisible.setAttribute("value", "instance('ui_settings')/"+attributeName+"/@futurevisible = 'true'");
				delaction.appendChild(valuevisible);
				
				}
			
			//cancelButton
			Element canceltrigger = doc.createElement("x:trigger");
			canceltrigger.setAttribute("ref", "instance('ui_settings')/cancelbutton");
			group.appendChild(canceltrigger);
			
			Element cancellabel = doc.createElement("x:label");
			cancellabel.setNodeValue("Cancel");
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
				cancelvalue1.setNodeValue("false");
				cancelaction.appendChild(cancelvalue1);
				
				Element cancelvalue2 = doc.createElement("x:setvalue");
				cancelvalue2.setAttribute("bind", "isReviewed");
				cancelvalue2.setNodeValue("false");
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
			reviewlabel.setNodeValue("Review");
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
				reviewvalue1.setNodeValue("false");
				reviewaction.appendChild(reviewvalue1);
				
				Element reviewvalue2 = doc.createElement("x:setvalue");
				reviewvalue2.setAttribute("bind", "isReviewed");
				reviewvalue2.setNodeValue("false");
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
			submitlabel.setNodeValue("Submit");
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
				value1.setNodeValue("");
				submitaction.appendChild(value1);
				
				Element value2 = doc.createElement("x:setvalue");
				value2.setAttribute("bind", "isDelegated");
				value2.setNodeValue("false");
				submitaction.appendChild(value2);
				
				Element value3 = doc.createElement("x:setvalue");
				value3.setAttribute("bind", "isReviewed");
				value3.setNodeValue("true");
				submitaction.appendChild(value3);
				
				Element value4 = doc.createElement("x:setvalue");
				value4.setAttribute("bind", "owner");
				value4.setAttribute("value", "instance('output-token')/data/metadata/firstOwner");
				submitaction.appendChild(value4);
				
				for (int i = 0; i<list.getLength(); i++) {
					String attributeName = list.item(i).getNodeName();
				
				Element valuereadonly = doc.createElement("x:setvalue");
				valuereadonly.setAttribute("bind", attributeName + ".readonly");
				valuereadonly.setNodeValue("false");
				submitaction.appendChild(valuereadonly);
				
				Element valuevisible = doc.createElement("x:setvalue");
				valuevisible.setAttribute("bind", attributeName + ".visible");
				valuevisible.setNodeValue("true");
				submitaction.appendChild(valuevisible);
				
				}
			
			// cancelButton
			Element canceltrigger = doc.createElement("x:trigger");
			canceltrigger.setAttribute("ref", "instance('ui_settings')/cancelbutton");
			group.appendChild(canceltrigger);
			
			Element cancellabel = doc.createElement("x:label");
			cancellabel.setNodeValue("Cancel");
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
				cancelvalue1.setNodeValue("true");
				cancelaction.appendChild(cancelvalue1);
				
				Element cancelvalue2 = doc.createElement("x:setvalue");
				cancelvalue2.setAttribute("bind", "isReviewed");
				cancelvalue2.setNodeValue("false");
				cancelaction.appendChild(cancelvalue2);
				
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
	Document addBindings(Document doc, Node processData) {
		NodeList list = processData.getChildNodes();
		Node root = doc.getFirstChild();
		for (int i = 0; i<list.getLength(); i++) {
			String attributeName = list.item(i).getNodeName();
		
			Element bind1 = doc.createElement("x:bind");
			bind1.setAttribute("id", attributeName);
			bind1.setAttribute("nodeset", "instance('output-token')/data/" + attributeName);
			root.appendChild(bind1);
			
			Element bind2 = doc.createElement("x:bind");
			bind2.setAttribute("id", attributeName+".readonly");
			bind2.setAttribute("nodeset", "instance('output-token')/data/" + attributeName + "/@readonly");
			root.appendChild(bind2);
			
			Element bind3 = doc.createElement("x:bind");
			bind3.setAttribute("id", attributeName+".visible");
			bind3.setAttribute("nodeset", "instance('output-token')/data/" + attributeName +"/@visible");
			root.appendChild(bind3);
			
			Element bind4 = doc.createElement("x:bind");
			bind4.setAttribute("type", "xsd:boolean");
			bind4.setAttribute("nodeset", "instance('output-token')/data/" + attributeName +"/@readonly");
			root.appendChild(bind4);
			
			Element bind5 = doc.createElement("x:bind");
			bind5.setAttribute("type", "xsd:boolean");
			bind5.setAttribute("nodeset", "instance('output-token')/data/" + attributeName +"/@visible");
			root.appendChild(bind5);
			
			Element bind6 = doc.createElement("x:bind");
			bind6.setAttribute("type", "xsd:boolean");
			bind6.setAttribute("nodeset", "instance('ui_settings')/" + attributeName +"/@futurereadonly");
			root.appendChild(bind6);
			
			Element bind7 = doc.createElement("x:bind");
			bind7.setAttribute("type", "xsd:boolean");
			bind7.setAttribute("nodeset", "instance('ui_settings')/" + attributeName +"/@futurevisible");
			root.appendChild(bind7);
			
			Element bind8 = doc.createElement("x:bind");
			bind8.setAttribute("nodeset", "instance('ui_settings')/" + attributeName +"/@futurevisible");
			bind8.setAttribute("relevant", "instance('output-token')/data/metadata/isDelegated = 'true' and instance('output-token')/data/processdata/" + attributeName +"/@visible = 'true'");
			root.appendChild(bind8);
			
			Element bind9 = doc.createElement("x:bind");
			bind9.setAttribute("nodeset", "instance('ui_settings')/" + attributeName +"/@futurereadonly");
			bind9.setAttribute("relevant", "instance('output-token')/data/metadata/isDelegated = 'true' and ((instance('output-token')/data/processdata/" + attributeName +"/@readonly = 'true' and instance('output-token')/data/processdata/" + attributeName +"/@visible != 'true') or instance('output-token')/data/processdata/" + attributeName +"/@visible = 'true')");
			root.appendChild(bind9);
			
			Element bind10 = doc.createElement("x:bind");
			bind10.setAttribute("nodeset", "instance('output-token')/data/processdata/" + attributeName);
			bind10.setAttribute("readonly", "instance('output-token')/data/processdata/" + attributeName +"/@readonly = 'true'");
			root.appendChild(bind10);
			
			Element bind11 = doc.createElement("x:bind");
			bind11.setAttribute("nodeset", "instance('ui_settings')/" + attributeName);
			bind11.setAttribute("relevant", "not(instance('output-token')/data/processdata/" + attributeName +"/@visible != 'true' and instance('output-token')/data/processdata/" + attributeName +"/@readonly != 'true')");
			bind11.setAttribute("id", "fade." + attributeName);
			root.appendChild(bind11);
			
			Element bind12 = doc.createElement("x:bind");
			bind12.setAttribute("nodeset", "instance('ui_settings')/" + attributeName +"/@futurevisible");
			bind12.setAttribute("readonly", "instance('ui_settings')/" + attributeName +"/@futurereadonly = 'true'");
			root.appendChild(bind12);
			
			Element bind13 = doc.createElement("x:bind");
			bind13.setAttribute("nodeset", "instance('ui_settings')/" + attributeName +"/@futurereadonly");
			bind13.setAttribute("readonly", "instance('ui_settings')/" + attributeName +"/@futurevisible = 'true'");
			root.appendChild(bind13);
			
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
     
		private boolean isContainedIn(ExecDataObject dataObject, NodeList list){
			for (int i = 0; i<list.getLength(); i++) {
				if (list.item(i).getNodeName().equals(dataObject.getId()))
					return true;
			}
			return false;
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

}

package de.hpi.bpmn2execpn.converter;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.ExecDataObject;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
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
	protected String standardModel;
	protected String baseFileName;
	private List<ExecTask> taskList;

	public ExecConverter(BPMNDiagram diagram, String modelURL) {
		super(diagram, new ExecPNFactoryImpl(modelURL));
		this.standardModel = modelURL;
		this.taskList = new ArrayList<ExecTask>();
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
					StringBufferInputStream in = new StringBufferInputStream(modelXML);
					try {
						//TODO why is Parser not working?
						Document doc = parser.parse(in);
						Node dataObjectId = processData.appendChild(modelDoc.createElement(dataObject.getId()));
						Node dataTagOfDataModel = doc.getDocumentElement().getFirstChild();
						Node child = dataTagOfDataModel.getFirstChild();
						while (child != null) {
							dataObjectId.appendChild(child.cloneNode(true));
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
				if (edge.getSource() instanceof ExecDataObject) {
					ExecDataObject dataObject = (ExecDataObject)edge.getSource();
					// for outgoing data objects of task: create read/write dependencies
					addReadOnlyExecFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_enable, null);
					addFlowRelationship(net, exTask.tr_finish, ExecTask.getDataPlace(dataObject.getId()));
					addFlowRelationship(net, ExecTask.getDataPlace(dataObject.getId()), exTask.tr_finish);
				}
			}
			
			// persist model and deliver URL
			try {
				//DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
				//DOMImplementationLS implLS = (DOMImplementationLS)registry.getDOMImplementation("LS");
				
				//LSSerializer dom3Writer = implLS.createLSSerializer();
				//LSOutput output=implLS.createLSOutput();
				//OutputStream outputStream = new FileOutputStream(new File(this.baseFileName+"_"+ task.getId() +"_model"+".xml"));
				//output.setByteStream(outputStream);
				//dom3Writer.write(modelDoc,output);
			} catch (Exception e) {
				System.out.println("Model could not be persisted");
				e.printStackTrace();
			}
			
//			// from Task model (modelDoc) extract formular and bindings 
//			Document formDoc = parser.newDocument();
//			Document bindDoc = parser.newDocument();
//			// create structure of form
//			// TODO how can process be shown up? Delegated from? Delegate?
//			Text text = formDoc.createTextNode(
//				"<b> Logged in as user: </b>/n"+
//				"<x:output ref=\"instance('output-token')/data/metadata/owner\" />/n"+
//				"<x:group>/n"+
//				"	<x:output bind=\"delegationstate\"><h2> Delegate task </h2></x:output>/n"+
//				"	<x:output bind=\"executionstate\"><h2> Execute task </h2></x:output>/n"+
//				"	<x:output bind=\"reviewstate\"><h2> Review task </h2></x:output>/n"+
//				"</x:group>/n"+
//				"<br /><br />/n"+
//				"<x:group ref=\"instance('ui_settings')/delegategroup\" >/n"+
//			    "    <x:select1 ref=\"instance('output-token')/data/metadata/delegate\" class=\"delegator\">  /n"+
//			    "        <x:label> Delegate to: </x:label>/n"+
//			    "            <x:item>/n"+
//			    "                <x:label>Adam</x:label>/n"+
//			    "                <x:value>Adam</x:value> /n"+
//			    "            </x:item>/n"+
//			    "            <x:item>/n"+
//			    "                <x:label>Bert</x:label>/n"+
//			    "                <x:value>Bert</x:value>/n"+
//			    "            </x:item>/n"+
//			    "            <x:item>/n"+
//			    "                <x:label>Hugo</x:label>/n"+
//			    "                <x:value>Hugo</x:value>/n"+
//			    "            </x:item>/n"+
//			    "            <x:item>/n"+
//			    "                <x:label>Rudi</x:label>/n"+
//			    "                <x:value>Rudi</x:value>/n"+
//			    "            </x:item>           /n"+
//			    "</x:select1>/n"+
//			    "</x:group>/n"+
//			    "<br />/n"+
//				"<x:group bind=\"fade.delegatedfrom\"><x:output ref=\"instance('output-token')/data/metadata/delegatedfrom\" class=\"delegator\">/n"+
//				"		<x:label> Delegated from: </x:label>/n"+
//				"</x:output></x:group>/n"+
//				"<br/><br />/n"+
//				"<x:group bind=\"fade.message\"><x:input ref=\"instance('output-token')/data/processdata/message\" class=\"metainfo\">/n"+
//				"		<x:label> Message: </x:label>/n"+
//				"</x:input></x:group>/n"+
//				"<br/><br />/n"+
//				"<x:group bind=\"fade.deadline\"><x:input ref=\"instance('output-token')/data/processdata/deadline\" class=\"metainfo\">/n"+
//				"		<x:label> Deadline: </x:label>/n"+
//				"</x:input></x:group>/n"+
//				"<br/><br />/n"+
//				"<x:group bind=\"wantToReviewGroup\">/n"+
//				"            <x:input ref=\"instance('output-token')/data/metadata/reviewRequested\" class=\"metainfo\">/n"+
//				"		<x:label> Responsible: </x:label>/n"+
//				"            </x:input>/n"+
//				"</x:group>/n"+
//				"<br/><br />/n"+
//				"/n"+
//				"<x:group ref=\"instance('ui_settings')/delegationstate\">/n"+
//				"	<br/><br />/n"+
//				"   <div class=\"labels\"> &nbsp;<b>readonly readable</b> </div>/n"+
//				"   <br/>/n"+
//				"</x:group>/n"
//			);
//			formDoc.appendChild(text);
			
			// build form fields of necessary attributes
			
			// TODO persist form and bindings and save URL
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
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
		exTask.pl_context.addLocator(new Locator("reviewRequested", "xsd:string", "/data/metadata/reviewRequested"));
		exTask.pl_context.addLocator(new Locator("startTime", "xsd:string", "/data/metadata/firstOwner"));
		exTask.pl_context.addLocator(new Locator("actions", "xsd:string", "/data/metadata/actions"));

		
		//enable transition
		//note: structure of context place must be initialized by engine
		exTask.tr_enable = addAutomaticTransition(net, "tr_enable_" + task.getId(), taskDesignation);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), exTask.tr_enable);
		addExecFlowRelationship(net, exTask.tr_enable, exTask.pl_ready, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_enable);
		addExecFlowRelationship(net, exTask.tr_enable, exTask.pl_context, baseXsltURL + "context_enable.xsl");
		
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
		// TODO: This is just for the moment (as long as no forms are used witg submit transistions)
		//FormTransition submit = addFormTransition(net, "tr_submit_" + task.getId(), task.getLabel(), model, form, bindings);
		//submit.setAction("submit");
		//exTask.tr_submit = submit;
		exTask.tr_submit = addTransformationTransition(net, "tr_submit_" + task.getId(), taskDesignation, "submit", copyXsltURL);
		addFlowRelationship(net, exTask.pl_running, exTask.tr_submit);
		addExecFlowRelationship(net, exTask.tr_submit, exTask.pl_deciding, extractDataURL);
		addFlowRelationship(net, exTask.pl_context, exTask.tr_submit);
		addExecFlowRelationship(net, exTask.tr_submit, exTask.pl_context, baseXsltURL + "context_submit.xsl");
		exTask.tr_submit.setRolename(rolename);
		
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
		
		taskList.add(exTask);
		
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
	 * - Integrating data dependencies as guards
	 * - Make CC String running
	 * - Transforming an XML CC into an Ruby String
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
		
//		String completionConditionString = getCompletionConditionString(process);
		// TODO (Stefan)!!!
		Transition finalize = addFormTransition(net, "ad-hoc_finalize_" + process.getId(), "FINALIZE", null, null, null);
//		finalize.setGuard("true");
		Transition resume = addFormTransition(net, "ad-hoc_resume_" + process.getId(), "RESUME", null, null, null);
//		resume.setGuard("false" );
		

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
		for (ExecTask exTask : taskList) {
			Place enabled = addPlace(net, "ad-hoc_task_enabled_" + exTask.getId());
			Place executed = addPlace(net, "ad-hoc_task_executed_" + exTask.getId());
			addFlowRelationship(net, startT, enabled);
			addFlowRelationship(net, enabled, exTask.tr_enable);
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
				StringBufferInputStream in = new StringBufferInputStream(modelXML);
				Document doc = parser.parse(in);
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

	
	private String getCompletionConditionString(SubProcess adHocSubprocess){
		assert (adHocSubprocess.isAdhoc());
		return adHocSubprocess.getCompletionCondition();
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

}

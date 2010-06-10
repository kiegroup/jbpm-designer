package de.hpi.bpmn.serialization.erdf;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Association;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.CollapsedSubprocess;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.EndCancelEvent;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndLinkEvent;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.EndMultipleEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndSignalEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.IntermediateConditionalEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateLinkEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateMultipleEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartConditionalEvent;
import de.hpi.bpmn.StartLinkEvent;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.StartMultipleEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.StartSignalEvent;
import de.hpi.bpmn.StartTimerEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.TextAnnotation;
import de.hpi.bpmn.UndirectedAssociation;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.serialization.BPMNSerialization;
import de.hpi.bpmn.serialization.erdf.templates.ANDGatewayTemplate;
import de.hpi.bpmn.serialization.erdf.templates.AssociationTemplate;
import de.hpi.bpmn.serialization.erdf.templates.CollapsedSubprocessTemplate;
import de.hpi.bpmn.serialization.erdf.templates.ComplexGatewayTemplate;
import de.hpi.bpmn.serialization.erdf.templates.DataObjectTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndCancelEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndCompensationEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndErrorEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndLinkEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndMessageEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndMultipleEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndPlainEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndSignalEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.EndTerminateEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateCancelEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateCompensationEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateConditionalEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateErrorEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateLinkEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateMessageEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateMultipleEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediatePlainEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateSignalEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.IntermediateTimerEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.LaneTemplate;
import de.hpi.bpmn.serialization.erdf.templates.MessageFlowTemplate;
import de.hpi.bpmn.serialization.erdf.templates.ORGatewayTemplate;
import de.hpi.bpmn.serialization.erdf.templates.PoolTemplate;
import de.hpi.bpmn.serialization.erdf.templates.SequenceFlowTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartConditionalEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartLinkEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartMessageEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartMultipleEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartPlainEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartSignalEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.StartTimerEventTemplate;
import de.hpi.bpmn.serialization.erdf.templates.SubProcessTemplate;
import de.hpi.bpmn.serialization.erdf.templates.TaskTemplate;
import de.hpi.bpmn.serialization.erdf.templates.TextAnnotationTemplate;
import de.hpi.bpmn.serialization.erdf.templates.UndirectedAssociationTemplate;
import de.hpi.bpmn.serialization.erdf.templates.XORDataBasedGatewayTemplate;
import de.hpi.bpmn.serialization.erdf.templates.XOREventBasedGatewayTemplate;

public class BPMNeRDFSerialization implements BPMNSerialization {
	
	ERDFSerializationContext context;
	
	public BPMNeRDFSerialization(BPMNDiagram bpmnDiagram) {
		this.context = new ERDFSerializationContext(bpmnDiagram);
	}
	
	public StringBuilder getSerializationHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"oryx-canvas123\" class=\"-oryx-canvas\">");
		sb.append("<span class=\"oryx-type\">http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram</span>");
		sb.append("<span class=\"oryx-id\">" + (this.context.getDiagramId() == null ? "" : this.context.getDiagramId()) + "</span>");
		sb.append("<span class=\"oryx-name\">" + this.context.getDiagramName() + "</span>");
		sb.append("<span class=\"oryx-version\"></span><span class=\"oryx-author\"></span><span class=\"oryx-language\">English</span>");
		sb.append("<span class=\"oryx-expressionlanguage\"></span><span class=\"oryx-querylanguage\"></span>");
		
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy");
		
		sb.append("<span class=\"oryx-creationdate\">"+f.format(d)+"</span><span class=\"oryx-modificationdate\">"+f.format(d)+"</span>");
		sb.append("<span class=\"oryx-pools\"></span><span class=\"oryx-documentation\"></span><span class=\"oryx-mode\">writable</span>");
		sb.append("<span class=\"oryx-mode\">fullscreen</span>");
		sb.append("<a rel=\"oryx-stencilset\" href=\"/oryx/stencilsets/bpmn1.1/bpmn1.1.json\"/>");

		
		for (String i : this.context.getResourceIDs()){
			sb.append("<a rel=\"oryx-render\" href=\"#resource" + i + "\"/>");
		}
		sb.append("</div>");
		
		return sb;
	}
	
	public StringBuilder getSerializationFooter() {
		return new StringBuilder("");
	}

	public StringBuilder getSerializationForDiagramObject(
			Task task) {
		return TaskTemplate.getInstance().getCompletedTemplate(task, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartMessageEvent startMessageEvent) {
		return StartMessageEventTemplate.getInstance().getCompletedTemplate(startMessageEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartPlainEvent startPlainEvent) {
		return StartPlainEventTemplate.getInstance().getCompletedTemplate(startPlainEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(ANDGateway andGateway) {
		return ANDGatewayTemplate.getInstance().getCompletedTemplate(andGateway, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			Association association) {
		return AssociationTemplate.getInstance().getCompletedTemplate(association, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			ComplexGateway complexGateway) {
		return ComplexGatewayTemplate.getInstance().getCompletedTemplate(complexGateway, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(DataObject dataObject) {
		return DataObjectTemplate.getInstance().getCompletedTemplate(dataObject, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndCancelEvent endCancelEvent) {
		return EndCancelEventTemplate.getInstance().getCompletedTemplate(endCancelEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndCompensationEvent endCompensationEvent) {
		return EndCompensationEventTemplate.getInstance().getCompletedTemplate(endCompensationEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndErrorEvent endErrorEvent) {
		return EndErrorEventTemplate.getInstance().getCompletedTemplate(endErrorEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndMessageEvent endMessageEvent) {
		return EndMessageEventTemplate.getInstance().getCompletedTemplate(endMessageEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndMultipleEvent endMultipleEvent) {
		return EndMultipleEventTemplate.getInstance().getCompletedTemplate(endMultipleEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndPlainEvent endPlainEvent) {
		return EndPlainEventTemplate.getInstance().getCompletedTemplate(endPlainEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndSignalEvent endSignalEvent) {
		return EndSignalEventTemplate.getInstance().getCompletedTemplate(endSignalEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			EndTerminateEvent endTerminateEvent) {
		return EndTerminateEventTemplate.getInstance().getCompletedTemplate(endTerminateEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateCancelEvent intermediateCancelEvent) {
		return IntermediateCancelEventTemplate.getInstance().getCompletedTemplate(intermediateCancelEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateCompensationEvent intermediateCompensationEvent) {
		return IntermediateCompensationEventTemplate.getInstance().getCompletedTemplate(intermediateCompensationEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateConditionalEvent intermediateConditionalEvent) {
		return IntermediateConditionalEventTemplate.getInstance().getCompletedTemplate(intermediateConditionalEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateErrorEvent intermediateErrorEvent) {
		return IntermediateErrorEventTemplate.getInstance().getCompletedTemplate(intermediateErrorEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateLinkEvent intermediateLinkEvent) {
		return IntermediateLinkEventTemplate.getInstance().getCompletedTemplate(intermediateLinkEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateMessageEvent intermediateMessageEvent) {
		return IntermediateMessageEventTemplate.getInstance().getCompletedTemplate(intermediateMessageEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateMultipleEvent intermediateMultipleEvent) {
		return IntermediateMultipleEventTemplate.getInstance().getCompletedTemplate(intermediateMultipleEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediatePlainEvent intermediatePlainEvent) {
		return IntermediatePlainEventTemplate.getInstance().getCompletedTemplate(intermediatePlainEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateSignalEvent intermediateSignalEvent) {
		return IntermediateSignalEventTemplate.getInstance().getCompletedTemplate(intermediateSignalEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			IntermediateTimerEvent intermediateTimerEvent) {
		return IntermediateTimerEventTemplate.getInstance().getCompletedTemplate(intermediateTimerEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(Lane lane) {
		return LaneTemplate.getInstance().getCompletedTemplate(lane, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			MessageFlow messageFlow) {
		return MessageFlowTemplate.getInstance().getCompletedTemplate(messageFlow, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(ORGateway orGateway) {
		return ORGatewayTemplate.getInstance().getCompletedTemplate(orGateway, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(Pool pool) {
		return PoolTemplate.getInstance().getCompletedTemplate(pool, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			SequenceFlow sequenceFlow) {
		return SequenceFlowTemplate.getInstance().getCompletedTemplate(sequenceFlow, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartConditionalEvent startConditionalEvent) {
		return StartConditionalEventTemplate.getInstance().getCompletedTemplate(startConditionalEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartLinkEvent startLinkEvent) {
		return StartLinkEventTemplate.getInstance().getCompletedTemplate(startLinkEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartMultipleEvent startMultipleEvent) {
		return StartMultipleEventTemplate.getInstance().getCompletedTemplate(startMultipleEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartSignalEvent startSignalEvent) {
		return StartSignalEventTemplate.getInstance().getCompletedTemplate(startSignalEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			StartTimerEvent startTimerEvent) {
		return StartTimerEventTemplate.getInstance().getCompletedTemplate(startTimerEvent, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(SubProcess subProcess) {
		return SubProcessTemplate.getInstance().getCompletedTemplate(subProcess, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(CollapsedSubprocess collapsedSubprocess) {
		return CollapsedSubprocessTemplate.getInstance().getCompletedTemplate(collapsedSubprocess, this.context);
	}
	
	public StringBuilder getSerializationForDiagramObject(
			TextAnnotation textAnnotation) {
		return TextAnnotationTemplate.getInstance().getCompletedTemplate(textAnnotation, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			UndirectedAssociation undirectedAssociation) {
		return UndirectedAssociationTemplate.getInstance().getCompletedTemplate(undirectedAssociation, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			XORDataBasedGateway xorDataBasedGateway) {
		return XORDataBasedGatewayTemplate.getInstance().getCompletedTemplate(xorDataBasedGateway, this.context);
	}

	public StringBuilder getSerializationForDiagramObject(
			XOREventBasedGateway xorEventBasedGateway) {
		return XOREventBasedGatewayTemplate.getInstance().getCompletedTemplate(xorEventBasedGateway, this.context);
	}
	
	public StringBuilder getSerializationForDiagramObject(
			EndLinkEvent endLinkEvent) {
		return EndLinkEventTemplate.getInstance().getCompletedTemplate(endLinkEvent, this.context);
	}

}

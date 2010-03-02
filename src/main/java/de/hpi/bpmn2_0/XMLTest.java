package de.hpi.bpmn2_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.diagram.EventShape;
import de.hpi.bpmn2_0.model.diagram.LaneCompartment;
import de.hpi.bpmn2_0.model.diagram.ProcessDiagram;
import de.hpi.bpmn2_0.model.diagram.activity.ActivityShape;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;

public class XMLTest {
	
	public static void main(String[] args) throws JAXBException {
		System.out.println("hallo");
//		testUnmarshal();
		testMarshal();
	}
	
	public static void testMarshal() throws JAXBException {
		// Process
		StartEvent startEvent = new StartEvent();
		startEvent.setId("st1");
		startEvent.setName("my Start Event");
		
		Task task = new Task();
		task.setId("t1");
		task.setName("my task");
		
		BoundaryEvent be = new BoundaryEvent();
		be.setAttachedToRef(task);
		be.setId("be1");
		
		BoundaryEvent be2 = new BoundaryEvent();
		be2.setAttachedToRef(task);
		be2.setId("be2");
		
		task.getBoundaryEventRefs().add(be);
		task.getBoundaryEventRefs().add(be2);
		
		EndEvent endEvent = new EndEvent();
		endEvent.setId("ev1");
		endEvent.setName("my end event");
		
		SequenceFlow seq1 = new SequenceFlow();
		seq1.setId("seqA");
		seq1.setSourceRef(startEvent);
		seq1.setTargetRef(task);
		
		SequenceFlow seq2 = new SequenceFlow();
		seq2.setId("seqB");
		seq2.setSourceRef(task);
		seq2.setTargetRef(endEvent);
		
		Process process = new Process();
		process.getFlowElement().add(startEvent);
		process.getFlowElement().add(task);
		
		process.getFlowElement().add(endEvent);
		process.getFlowElement().add(seq1);
		process.getFlowElement().add(seq2);
		process.getFlowElement().add(be);
		process.getFlowElement().add(be2);
		process.setId("myProcess");
		
		// DI
		Definitions def = new Definitions();
		ProcessDiagram pdt = new ProcessDiagram();
		LaneCompartment lct = new LaneCompartment();
		EventShape es = new EventShape();
		es.setName("myfirstEvent");

		ActivityShape actShape = new ActivityShape();
		
		actShape.setActivityRef(task);
		lct.getBpmnShape().add(actShape);
		
		lct.getBpmnShape().add(es);
		pdt.getLaneCompartment().add(lct);
		pdt.setProcessRef(process);
		def.getDiagram().add(pdt);
		def.getRootElement().add(process);
		
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(def, System.out);
	}
	
	public static void testUnmarshal() {
		String path = "C:\\Dokumente und Einstellungen\\Sven\\workspace\\oryx_bpmn2.0_2\\editor\\server\\src\\de\\hpi\\bpmn2_0\\";
//		ObjectFactory ob = new ObjectFactory();
		
		try {
			JAXBContext context = JAXBContext.newInstance(Definitions.class);
			Unmarshaller u = context.createUnmarshaller();
			File xml = new File(path + "bpmntest.xml");
			BufferedReader br = new BufferedReader(new FileReader(xml));
//			String bpmnXml = "";
//			String line;
//			while((line = br.readLine()) != null) {
//				bpmnXml += line;
//			}
//			System.out.println(bpmnXml);
			Definitions d = (Definitions) u.unmarshal(xml);
			 
			
			ProcessDiagram scrPdt = (ProcessDiagram) d.getDiagram().get(0);
			LaneCompartment lane1 = scrPdt.getLaneCompartment().get(0);
			String name = lane1.getBpmnShape().get(0).getName();
			
			System.out.println(name);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}

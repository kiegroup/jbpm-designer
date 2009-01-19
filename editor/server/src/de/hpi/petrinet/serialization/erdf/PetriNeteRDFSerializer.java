package de.hpi.petrinet.serialization.erdf;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import de.hpi.petrinet.PetriNet;


public class PetriNeteRDFSerializer {
	ServletContext servletContext;
	
	public PetriNeteRDFSerializer(ServletContext servletContext){
		this.servletContext = servletContext;
	}
	
	public String serializeDiagram(PetriNet petrinet ) {
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setApplicationAttribute("javax.servlet.ServletContext", servletContext);
	        ve.setProperty("resource.loader", "webapp");
	        ve.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader");
	        ve.setProperty("webapp.resource.loader.path", "/WEB-INF/classes/de/hpi/petrinet/serialization/erdf");
	        Template t = ve.getTemplate( "petrinet.erdf.vm" );
	        
	        /* create a context and add data */
	        VelocityContext context = new VelocityContext();
	        context.put("transitions", petrinet.getTransitions());
	        context.put("places", petrinet.getPlaces());
	        context.put("arcs", petrinet.getFlowRelationships());
	        
	        /* now render the template into a StringWriter */
	        StringWriter writer = new StringWriter();
			t.merge( context, writer );

	        return writer.toString();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (MethodInvocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
        
		/*
		StringBuilder eRDF = new StringBuilder();
		
		PetriNeteRDFSerialization serialization = new PetriNeteRDFSerialization(bpmnDiagram);
		
		eRDF.append(serialization.getSerializationHeader());

		for(Edge edge : bpmnDiagram.getEdges()) {
			eRDF.append(edge.getSerialization(serialization));
		}
		
		for(Node node : bpmnDiagram.getChildNodes()) {
			eRDF.append(node.getSerialization(serialization));
		}

		eRDF.append(serialization.getSerializationFooter());

		return eRDF.toString();*/
		
	}

}

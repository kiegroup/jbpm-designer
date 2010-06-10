package de.hpi.petrinet.serialization.erdf;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;


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
	        
	        List<LabeledTransition> labeledTransitions = new LinkedList<LabeledTransition>();
	        List<Transition> nopTransitions = new LinkedList<Transition>();
	        
	        prepareTransitions(petrinet.getTransitions(), labeledTransitions, nopTransitions);

	        context.put("labeledTransitions", labeledTransitions);
	        context.put("nopTransitions", nopTransitions);
	        context.put("places", petrinet.getPlaces());
	        context.put("arcs", petrinet.getFlowRelationships());
	        context.put("marking", petrinet.getInitialMarking());
	        
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
	}
	
	/**
	 * Sorts given transition list and set null labels to empty strings
	 * @param transitions List of transitions which should be prepared
	 * @param labeledTransitions Target list which will be filled with labeled transitions
	 * @param nopTransitions Target list which will be filled with nop transitions
	 */
	protected void prepareTransitions(List<Transition> transitions, List<LabeledTransition> labeledTransitions, List<Transition> nopTransitions){
        for(Transition trans : transitions){
        	if(trans instanceof LabeledTransition){
        		LabeledTransition lTrans = (LabeledTransition) trans;
        		if(lTrans.getLabel() == null ) lTrans.setLabel(""); //needed for template
        		labeledTransitions.add(lTrans);
        	} else {
        		nopTransitions.add(trans);
        	}
        }
	}

}

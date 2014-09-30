package org.jbpm.designer.epn.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.dd.di.DiagramElement;
import org.jbpm.designer.web.profile.IDiagramProfile;


/**
 * @author Tihomir Surdilovic
 * 
 * a marshaller to transform EPN elements into JSON format.
 *
 */
public class EpnJsonMarshaller {
    private Map<String, DiagramElement> _diagramElements = new HashMap<String, DiagramElement>();
    private IDiagramProfile profile;
    
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }
    
    public String marshall(String definitions) throws IOException { //TODO fix this when we have the EPN ecore model
        StringWriter writer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(writer);
        //TODO do the heavy lifting here passing in the writer and the json generator
        generator.close();
        return writer.toString();
    }
}

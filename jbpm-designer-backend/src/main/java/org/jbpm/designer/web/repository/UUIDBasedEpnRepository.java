package org.jbpm.designer.web.repository;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;


public class UUIDBasedEpnRepository implements IUUIDBasedRepository {

    private static final Logger _logger = Logger.getLogger(UUIDBasedEpnRepository.class);
    private final static String DEFAULTS_PATH = "defaults";
    
    private String _defaultsPath;
    
    public void configure(HttpServlet servlet) {
        _defaultsPath = servlet.getServletContext().getRealPath("/" + DEFAULTS_PATH);
    }

    public byte[] load(HttpServletRequest req, String uuid,
            IDiagramProfile profile, ServletContext servletContext) throws Exception {
        // TODO hook up with Guvnor
        return new byte[0];
    }

    public void save(HttpServletRequest req, String uuid, String json,
            String svg, IDiagramProfile profile, Boolean autosave) {
        //TODO hook up with Guvnor 
    }

    public String toXML(String json, IDiagramProfile profile,String preProcessingData) {
        return profile.createMarshaller().parseModel(json, preProcessingData);
    }
    
}

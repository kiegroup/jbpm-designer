package org.jbpm.designer.web.repository.impl;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;


public class UUIDBasedJbpmRepository implements IUUIDBasedRepository {

    private static final Logger _logger = Logger.getLogger(UUIDBasedJbpmRepository.class);
    private final static String DEFAULTS_PATH = "defaults";
    
    private String _defaultsPath;

    public void configure(HttpServlet servlet) {
        _defaultsPath = servlet.getServletContext().getRealPath("/" + DEFAULTS_PATH);
    }

    public byte[] load(HttpServletRequest req, String uuid, IDiagramProfile profile, ServletContext servletContext) throws Exception {
        Repository repository = profile.getRepository();
        String processjson = "";
        String preProcessingParam = req.getParameter("pp");
        // check with Guvnor to see what it has for this uuid for us
        String processxml = ((Asset<String>)repository.loadAsset(uuid)).getAssetContent();
        if(processxml != null && processxml.length() > 0) {
        	DroolsPackageImpl.init();
            processjson = profile.createUnmarshaller().parseModel(processxml, profile, preProcessingParam);
            return processjson.getBytes("UTF-8");
        } else {
            return new byte[0];
        }
    }

    public void save(HttpServletRequest req, String uuid, String json,
            String svg, IDiagramProfile profile, Boolean autosave) {
        // Guvnor is responsible for saving 
    }
    
    public String toXML(String json, IDiagramProfile profile, String preProcessingData) {
        return profile.createMarshaller().parseModel(json, preProcessingData);
    }
}

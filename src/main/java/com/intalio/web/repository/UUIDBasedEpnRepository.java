package com.intalio.web.repository;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.intalio.web.profile.IDiagramProfile;

public class UUIDBasedEpnRepository implements IUUIDBasedRepository {

    private static final Logger _logger = Logger.getLogger(UUIDBasedEpnRepository.class);
    private final static String DEFAULTS_PATH = "defaults";
    
    private String _defaultsPath;
    
    @Override
    public void configure(HttpServlet servlet) {
        _defaultsPath = servlet.getServletContext().getRealPath("/" + DEFAULTS_PATH);
    }

    @Override
    public byte[] load(HttpServletRequest req, String uuid,
            IDiagramProfile profile) {
        // TODO hook up with Guvnor
        return new byte[0];
    }

    @Override
    public void save(HttpServletRequest req, String uuid, String json,
            String svg, IDiagramProfile profile, Boolean autosave) {
        //TODO hook up with Guvnor 
    }

    @Override
    public String toXML(String json, IDiagramProfile profile) {
        return profile.createMarshaller().parseModel(json);
    }
    
}

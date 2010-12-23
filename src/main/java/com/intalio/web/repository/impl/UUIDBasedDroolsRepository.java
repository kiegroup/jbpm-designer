package com.intalio.web.repository.impl;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.repository.IUUIDBasedRepository;

public class UUIDBasedDroolsRepository implements IUUIDBasedRepository {

    private static final Logger _logger = Logger.getLogger(UUIDBasedDroolsRepository.class);
    
    @Override
    public void configure(HttpServlet servlet) {
        
    }

    @Override
    public byte[] load(HttpServletRequest req, String uuid, String ext) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        return output.toByteArray();
    }

    @Override
    public void save(HttpServletRequest req, String uuid, String json,
            String svg, IDiagramProfile profile, Boolean autosave) {
   
    }
    
}

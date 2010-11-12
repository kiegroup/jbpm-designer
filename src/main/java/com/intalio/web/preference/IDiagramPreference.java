package com.intalio.web.preference;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.intalio.web.profile.IDiagramProfile;

public interface IDiagramPreference {
	/**
     * Configure the repository in the context of the servlet
     * @param servlet the servlet which will use this repository.
     */
    public void configure(HttpServlet servlet);
    
    /**
     * @param req the request from the user.
     * @param uuid the id of the model.
     * @param ext the file extension to apply to the model.
     * @return the model as a set of bytes.
     */
    public String loadPreference(HttpServletRequest req);
    
    /**
     * Saves the model inside the repository.
     * @param req the request from the user.
     * @param uuid the id of the model
     * @param json the json model
     * @param svg the svg representation of the model
     * @param profile the profile
     */
    public void savePreference(HttpServletRequest req, String preference);
}

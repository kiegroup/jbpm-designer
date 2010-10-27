/***************************************
 * Copyright (c) 2008-2010
 * Philipp Berger 2009
 * Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package org.oryxeditor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.impl.*;
import com.intalio.web.plugin.impl.*;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.Attribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Servlet to load plugin and Oryx stencilset
 */

public class EditorHandler extends HttpServlet {

    private static final long serialVersionUID = -7439613152623067053L;

    private static final Logger _logger = Logger.getLogger(EditorHandler.class);
    
    /**
     * The base path under which the application will be made available at runtime.
     * This constant should be used throughout the application. Eventually, it could be derived from the manifest values
     * or 
     */
	public static final String oryx_path = "/designer/";
	
	/*
	 * 
	 */
    private ProfileServiceImpl _profileService = null;
    private PluginServiceImpl _pluginService = null;
    
    /*
     * JDOM related object. They are initialized while reading "editor.html"
     */
    private Document _doc = null;		//JDOM object
    private Element _root = null;		// root element
    private Element _head = null;		// head element
    private int _lastscriptidx = 0;		// the index of the last script element
    
	public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _profileService = new ProfileServiceImpl(config.getServletContext());
        _pluginService = new PluginServiceImpl(config.getServletContext());      
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    FileInputStream input = new FileInputStream(getServletContext().getRealPath("/editor.html"));

	    _lastscriptidx = 0;
	    
	    String profileName = request.getParameter("profile");
	    IDiagramProfile profile = _profileService.findProfile(request, profileName);
	    if (profile == null) {
	    	_logger.error("Empty profile object");
	    	throw new ServletException(); // TODO
	    }

	    String editor_file = getServletContext().getRealPath("/editor.html");
	    _doc = readDocument(editor_file);
	    if (_doc == null) {
	    	_logger.error("Invalid editor.html. Don't touch this file");
	    	throw new ServletException(); // TODO
	    }
	    
	    _root = _doc.getRootElement();
	    searchHead(_root);		// search for the head element
	    if (_head == null) {
	    	_logger.error("Invalid editor.html. No html or head tag");
	    	throw new ServletException(); // TODO
	    }

	    // create the script tags for environment
	    FileInputStream core_scripts = new FileInputStream(getServletContext().getRealPath("/js/js_files.json"));
	    String contents = null;
	    
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = core_scripts.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            contents = stream.toString();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
            	_logger.error(e.getMessage(), e);
            }
        }

        /*
         * generate the script for each file in js_files.json 
         */
	    JSONObject obj;
		try {
			obj = new JSONObject(contents);
		
			JSONArray array = obj.getJSONArray("files");
			for (int i = 0 ; i < array.length() ; i++) {
				addscript(oryx_path + array.getString(i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			_logger.error("invalide js_files.json");
			_logger.error(e.getMessage(), e);
		}
		
	    // generate script to setup the languages
	    addscript(oryx_path + "i18n/translation_en_us.js");
	    
	    // plugins.
	    for (String plugin : profile.getPlugins()) {
	        _pluginService.findPlugin(request, plugin);
	        addscript(oryx_path + "plugin/" + plugin + ".js");
	    }

	    // send the updated editor.html to client 
	    try {
	    	XMLOutputter outputter = new XMLOutputter();
	    	Format fm = outputter.getFormat();
	    	outputter.setFormat(outputter.getFormat().setExpandEmptyElements(true));
	    	String finalDoc = outputter.outputString(_doc);
	    	finalDoc = finalDoc.replaceAll("xmlns=\"\"", "");
	    	finalDoc = finalDoc.replace("@title@", profile.getTitle());
	    	finalDoc = finalDoc.replace("@stencilset@", profile.getStencilSet());    
	    	response.getWriter().write(finalDoc);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	_logger.error(e.getMessage(), e);
	    }
	}
	
	private static Document readDocument(String name) {
        try {
            SAXBuilder builder = new SAXBuilder(false); 
            
            // no DTD validation
            builder.setValidation(false);
            builder.setFeature("http://xml.org/sax/features/validation", false);
            builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            
            Document anotherDocument = builder.build(new File(name));
            return anotherDocument;
        } catch(JDOMException e) {
        	_logger.error(e.getMessage(), e);
        } catch(NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
        	_logger.error(e.getMessage(), e);
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
		}
        
        return null;
    }
	
	private void addscript(String src) {
        Element script = new Element("script");
        // set the attributes
        script.setAttribute("src", src);
        script.setAttribute("type", "text/javascript");
        // put it to the right place
        _head.addContent(_lastscriptidx-1, script);
        _lastscriptidx ++;

		return;
	}
	
	/*
	 * Search for the head element, as well as the last script element
	 * in the file
	 */
	private void searchHead(Element current) {
		if (current.getName() == "head") {
			_head = current;
		}
		if (current.getName() == "script") {
			int index = _head.indexOf(current);
			if (index > _lastscriptidx) {
				_lastscriptidx = index;
			}
		}
	    List children = current.getChildren();
	    Iterator iterator = children.iterator();
	    while (iterator.hasNext()) {
	      Element child = (Element) iterator.next();
	      searchHead(child);
	    }	    
	}
}
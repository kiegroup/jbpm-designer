/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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

package org.b3mn.poem.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.json.JSONArray;
import org.json.JSONException;


@HandlerWithoutModelContext(uri="/model")
public class CollectionHandler extends HandlerBase {
	
    // Returns a date object representing the input string 
	// TODO: just make it better
	protected Date parseDate(String strDate, boolean set1970OnError) {
    	if (strDate != null){
    		try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");			
    			return formatter.parse(strDate);
			} catch (ParseException e) {
				Calendar c = Calendar.getInstance();
				c.set(1970, 1, 1);
				return c.getTime();
			}
    	}
    	else {
			if (set1970OnError) {
	    		Calendar c = Calendar.getInstance();
				c.set(1970, 1, 1);
				return c.getTime();
			}
			else {
				Date date = new Date();
				// TODO: Find the damn time bug. You have to add 24h to get all the models. Why ever...
				date.setTime(date.getTime() + 24 * 3600 * 1000); 
				return date; 
			}
    	}
    }
	
	protected boolean checkParameter(HttpServletRequest req, String parameter) {
		if (req.getParameter(parameter) != null) {
			return req.getParameter(parameter).equals("true");
		}
		else {
			return false;
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws IOException {
    	
    	Date from  = this.parseDate(req.getParameter("from"), true);
    	Date to = this.parseDate(req.getParameter("to"), false);
   
    	boolean owner = this.checkParameter(req, "owner");
    	boolean is_shared = this.checkParameter(req, "is_shared");
    	boolean contributor = this.checkParameter(req, "contributor");
    	boolean reader= this.checkParameter(req, "contributor");
    	boolean is_public = this.checkParameter(req, "is_public");
    	
        String type = null;
        if (req.getParameter("type") != null) {
        	type = req.getParameter("type");
        }
        else {
        	type = "%";
        }
        
        // Run query
        List<Representation> models = subject.getModels(type, from, to, owner, 
        		is_shared, is_public, contributor, reader);
        
        // Collect meta data as json
        JSONArray output = new JSONArray();
        for (Representation model : models) {
        	output.put(this.getModelMetaData(subject, Identity.instance(model.getIdent_id()), req));
        }
        // Write json to output stream
        try {
			output.write(res.getWriter());
		} catch (JSONException e) {e.printStackTrace();}
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws IOException {
    	Identity identity = Identity.newModel(subject, 
    			req.getParameter("title"), 
    			req.getParameter("type"), 
    			req.getParameter("summary"), 
    			req.getParameter("svg"), 
    			req.getParameter("content")); 
    			
    	res.setStatus(201); // Http status "created"
    	res.setHeader("location", identity.getUri()); // return the model uri to the editor
	}
}
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class EditorHandler extends HttpServlet {

	/**
	 * A special flag to set when running in development mode to use the disassembled resources,
	 * loaded from js_files.json
	 */
	public static final String DEV_MODE = "designer.dev";
	
    private static final Logger _logger = Logger.getLogger(EditorHandler.class);
    
    /**
     * The base path under which the application will be made available at runtime.
     * This constant should be used throughout the application. Eventually, it could be derived from the manifest values
     * or 
     */
	public static final String oryx_path = "/designer/";
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    if (System.getProperty(DEV_MODE) != null) {
	        response.addCookie(new Cookie("designer.dev", ""));
	    }
	    FileInputStream input = new FileInputStream(getServletContext().getRealPath("/editor.html"));
        try {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, read);
            }
            response.setContentType("application/xhtml+xml");
        } catch (IOException e) {
            _logger.error(e.getMessage(), e);
        } finally {
            if (input != null) { try { input.close(); } catch(IOException e) {}};
        }
	}
}

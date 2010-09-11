/***************************************
 * Copyright (c) Intalio, Inc 2010
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
package com.intalio.web.server;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.oryxeditor.server.EditorHandler;

/**
 * Gives the paths of the core files to load: either each of them or a compressed file.
 * 
 * @author Antoine Toulme
 *
 */
public class ProcessDesignerEnvironmentServlet extends HttpServlet {
    
    private static final long serialVersionUID = -7382042081451404613L;
    
    private static final Logger _logger = Logger.getLogger(ProcessDesignerEnvironmentServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (System.getProperty(EditorHandler.DEV_MODE) != null) {
            FileInputStream input = new FileInputStream(getServletContext().getRealPath("/js/js_files.json"));
            try {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    resp.getOutputStream().write(buffer, 0, read);
                }
                resp.setContentType("application/json");
            } catch (IOException e) {
                _logger.error(e.getMessage(), e);
            } finally {
                if (input != null) { try { input.close(); } catch(IOException e) {}};
            }
        } else {
            
        }
    }
}

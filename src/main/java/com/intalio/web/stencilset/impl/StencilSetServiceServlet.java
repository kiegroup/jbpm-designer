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
package com.intalio.web.stencilset.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.web.stencilset.IDiagramStencilSet;
import com.intalio.web.stencilset.IDiagramStencilSetService;

/**
 * A servlet to serve stencilsets from the StencilSetService.
 * @author Antoine Toulme
 *
 */
public class StencilSetServiceServlet extends HttpServlet {

private static final Logger _logger = LoggerFactory.getLogger(StencilSetServiceServlet.class);
    
    private static final long serialVersionUID = -2024110864538877629L;
    
    private IDiagramStencilSetService _pluginService;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _pluginService = new StencilSetServiceImpl(config.getServletContext());
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        // urls should be of type: /designer/stencilset/bpmn2.0
        // /designer/stencilset/{name}/{resource}
        String[] segments = requestURI.split("/");
        if (segments.length < 2) {
            throw new IllegalArgumentException("No name provided");
        }
        String name = segments[3];
        
        IDiagramStencilSet stencilset = _pluginService.findStencilSet(req, name);
        if (stencilset == null) {
            throw new IllegalArgumentException("No stencilset by the name of " + name);
        }
        InputStream input = null;
        if (segments.length > 4) { //looking for a resource under the stencilset.
            String path = requestURI.substring(requestURI.indexOf(segments[3]) + segments[3].length() + 1);
            input = stencilset.getResourceContents(path);
        } else {
            input = stencilset.getContents();
            resp.setContentType("application/json");
        }
        
        try {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, read);
            }
        } catch (IOException e) {
            _logger.error(e.getMessage(), e);
        } finally {
            if (input != null) { try { input.close(); } catch(IOException e) {}};
        }
        }
}

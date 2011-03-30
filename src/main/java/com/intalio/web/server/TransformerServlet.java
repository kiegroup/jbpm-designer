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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.fop.svg.PDFTranscoder;

/**
 * @author Tihomir Surdilovic
 * transformer for svg process representation to various formats.
 *
 */
public class TransformerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger.getLogger(TransformerServlet.class);
    private static final String TO_PDF = "pdf";
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String svg = req.getParameter("svg");
        String uuid = req.getParameter("uuid");
        String profile = req.getParameter("profile");
        String transformto = req.getParameter("transformto");
        
        if(transformto != null && transformto.equals(TO_PDF)) {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" +uuid + ".pdf\"");
            try {
                PDFTranscoder t = new PDFTranscoder();
                TranscoderInput input = new TranscoderInput(new StringReader(svg));
                TranscoderOutput output = new TranscoderOutput(resp.getOutputStream());
                t.transcode(input, output);
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
            resp.getOutputStream().flush();
        }
    }
}

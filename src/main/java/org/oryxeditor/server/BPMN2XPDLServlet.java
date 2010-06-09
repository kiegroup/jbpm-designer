package org.oryxeditor.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;

import de.hpi.bpmn2xpdl.BPMN2XPDLConverter;

/**
 * Copyright (c) 2010 Markus Goetz
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BPMN2XPDLServlet extends HttpServlet {
	private static final long serialVersionUID = -8374877061121257562L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/xml");
		String data = req.getParameter("data");
		
		
		String action = req.getParameter("action");
		
		if ("Export".equals(action)) {
			BPMN2XPDLConverter converter = new BPMN2XPDLConverter();
			try {
				res.getWriter().print(converter.exportXPDL(data));
			} catch (Exception e) {
				res.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} else if ("Import".equals(action)){
			BPMN2XPDLConverter converter = new BPMN2XPDLConverter();
			res.getWriter().print(converter.importXPDL(data));
		} else {
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
}

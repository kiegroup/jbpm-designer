/**
 * Copyright (c) 2009 
 * Philipp Giese
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

package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.bpmn2_0.exceptions.BpmnMigrationException;
import de.hpi.bpmn2_0.migration.BPMN2Migrator;

/**
 * @author Philipp Giese
 *
 */
public class BPMN2MigrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1787260917443129385L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			response.setContentType("text/json");
			response.setCharacterEncoding("UTF-8");
			
			
			String json = request.getParameter("data");
			String path = request.getParameter("path");
			
			migrateDocument(json, path, response.getWriter());
		} catch(Exception e) {
			try {
				response.sendError(500, e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

	private void migrateDocument(String json, String path, PrintWriter writer) throws BpmnMigrationException {
		BPMN2Migrator migrator = new BPMN2Migrator(json);
		
		String realPath = getServletContext().getRealPath(path);
		
		writer.print(migrator.migrate(realPath));
	}
}

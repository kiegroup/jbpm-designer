/**
 * Copyright (c) 2008, 2009 Steffen Ryll
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bpmnq.AbstractQueryProcessor;
import com.bpmnq.FileFormatException;
import com.bpmnq.GraphBuilder;
import com.bpmnq.Match;
import com.bpmnq.MemoryQueryProcessor;
import com.bpmnq.OryxMemoryQueryProcessor;
import com.bpmnq.ProcessGraph;
import com.bpmnq.QueryGraph;
import com.bpmnq.QueryGraphBuilderRDF;
import com.bpmnq.Utilities;
import com.bpmnq.QueryGraphBuilderRDF.RdfSyntax;

public class QueryEvalServlet extends HttpServlet {
    private static final long serialVersionUID = -7946509291423453168L;
    private static final boolean useDataBaseConnection = false;
    private Logger log = Logger.getLogger(this.getClass());

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String rdf = req.getParameter("data");
        InputStream rdfStream = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
        log.debug("reading in rdfStream as UTF-8.");
        log.trace("read following RDF: " + rdf);
        
        // initialize BPMNQ processor
        try {
            Utilities util = Utilities.getInstance();
            if (useDataBaseConnection && !Utilities.isConnectionOpen()) {
                Utilities.openConnection();
            }

        } catch (Exception ex) {
            // rethrow as ServletException
            throw new ServletException("Cannot communicate with BPMN-Q database", ex);
        }
        
        GraphBuilder gBuilder = getGraphBuilderFor(rdfStream, "RDF/XML", req);
        QueryGraph query = null;
        try {
            query = gBuilder.buildGraph();
            // Added for Debugging
            System.out.println("########################################## QUERY #################################");
            query.print(System.out);
            System.out.println("########################################## QUERY #################################");
        } catch (FileFormatException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        
        resp.setContentType("text/xml");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter respWriter = resp.getWriter();
        
        AbstractQueryProcessor qProcessor;
        if (useDataBaseConnection) {
            qProcessor = new MemoryQueryProcessor(respWriter);
        } else {
            qProcessor = new OryxMemoryQueryProcessor(respWriter);  
                    //"http://localhost:8080/backend/poem");
        }
        
        String stopOption = req.getParameter("stopAtFirstMatch");
        if (stopOption != null && stopOption.toLowerCase().matches("on|true")) {
            qProcessor.stopAtFirstMatch = true;
        }
        // Added by Ahmed
        qProcessor.allowGenericShapeToEvaluateToNone = false;
        String command = req.getParameter("command");
        String modelID = req.getParameter("modelID");
        try {
            if ("testQueryAgainstModel".equalsIgnoreCase(command)) {
                ProcessGraph match = null;
                boolean doesMatch = qProcessor.testQueryAgainstModel(query, modelID, match);
            } else if ("runQueryAgainstModel".equalsIgnoreCase(command)) {
                ProcessGraph match = qProcessor.runQueryAgainstModel(query, modelID);
            } else if ("processMultiQuery".equalsIgnoreCase(command)) {
                List<Match> matches = qProcessor.processMultiQuery(query);
            } else { // default case, if no (or unknown) command was specified
                
                List<String> matchedModels = qProcessor.processQuery(query);
            }
        } catch (Exception e) {
            try {
                resp.sendError(500, "Query processing failed with an internal error");
            } catch (IllegalStateException e1) { // ok, it was already too late, headers were sent out
            }

            e.printStackTrace();
            log.error("Query evaluation failed with an exception", e);
        }
        
        try {
            Utilities.closeConnection();
        } catch (SQLException e) {
            log("Closing DB connection failed " + e.getMessage(), e);
        }
    }

    private GraphBuilder getGraphBuilderFor(InputStream graph, String format,
            HttpServletRequest req) throws IOException {
        
        String referrerUri = req.getHeader("Referer");

        QueryGraphBuilderRDF queryGraphBuilder = new QueryGraphBuilderRDF(
                graph, RdfSyntax.RDF_XML, referrerUri);

        return queryGraphBuilder;
    }
    
}

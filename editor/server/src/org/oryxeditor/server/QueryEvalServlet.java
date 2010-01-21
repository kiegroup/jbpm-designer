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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bpmnq.AbstractQueryProcessor;
import com.bpmnq.FileFormatException;
import com.bpmnq.GraphBuilder;
import com.bpmnq.Match;

import com.bpmnq.OryxMemoryQueryProcessor;
import com.bpmnq.ProcessGraph;
import com.bpmnq.QueryGraph;
import com.bpmnq.QueryGraphBuilderRDF;
import com.bpmnq.Utilities;
import com.bpmnq.AbstractQueryProcessor.ProcessorCommand;
import com.bpmnq.QueryGraphBuilderRDF.RdfSyntax;
import com.bpmnq.compliancechecker.ComplianceViolationExplanator;
import com.bpmnq.compliancechecker.ModelChecker;
import com.bpmnq.compliancechecker.TemporalQueryGraph;

public class QueryEvalServlet extends HttpServlet {
    private static final long serialVersionUID = -7946509291423453168L;
    private static final boolean useDataBaseConnection = false;
    private Logger log = Logger.getLogger(this.getClass());
    private Map<QueryGraph,String> queryMatches = new HashMap<QueryGraph,String>(); 
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long startTime,endTime;
        startTime = System.currentTimeMillis();
        String rdf = req.getParameter("data");
        InputStream rdfStream = new ByteArrayInputStream(rdf.getBytes("UTF-8"));
//        log.debug("reading in rdfStream as UTF-8.");
//        log.trace("read following RDF: " + rdf);
        
        // initialize BPMNQ processor
        try {
            Utilities util = Utilities.getInstance();
            if (useDataBaseConnection && !Utilities.isConnectionOpen()) {
                Utilities.openConnection();
                System.out.println(" +++++++++++++++ DB Connection has been opened ++++++++++++");
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
//            System.out.print("####### Servlet path");
//            System.out.println((String) this.getServletContext().getRealPath("."));
            
//            System.out.println("########################################## QUERY #################################");
//            log.info("########################################## QUERY #################################");
//            log.info(query.toString());
//            query.print(System.out);
//            System.out.println("########################################## QUERY #################################");
        } catch (FileFormatException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        
        resp.setContentType("text/xml");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter respWriter = resp.getWriter();
        
        AbstractQueryProcessor qProcessor;
//        if (useDataBaseConnection) {
//            qProcessor = new MemoryQueryProcessor(respWriter);
//        } else {
            qProcessor = new OryxMemoryQueryProcessor(respWriter);  
                    //"http://localhost:8080/backend/poem");
//        }
        
        String stopOption = req.getParameter("stopAtFirstMatch");
        if (stopOption != null && stopOption.toLowerCase().matches("on|true")) {
            qProcessor.stopAtFirstMatch = true;
        }
        // Added by Ahmed
        qProcessor.allowGenericShapeToEvaluateToNone = false;
        String command = req.getParameter("command");
        String modelID = req.getParameter("modelID");
        try {
        	if ("processComplianceQuery".equalsIgnoreCase(command))
        	{
        		qProcessor.includeEnclosingAndSplits= true;
//        		respWriter.println("<ServletPath>"+(String) this.getServletContext().getRealPath(".")+"</ServletPath>");
//        		respWriter.println("<UserDirectory>"+System.getProperty("user.dir")+"</UserDirectory>");
        		System.out.println("########################################## PROCESS COMPLIANCE QUERY #################################");
        		TemporalQueryGraph tqry = query.getTemporalQueryGraph();
        		System.out.println("%%%%%%%%%%%%%%%%%%%% Temporal Query Graph");
        		tqry.print(System.out);
        		System.out.println("%%%%%%%%%%%%%%%%%%%% Temporal Query Graph");
        		ModelChecker mc = new ModelChecker(tqry,respWriter);
        		mc.queryProc = new OryxMemoryQueryProcessor(respWriter);
        		
        		List<String> mdls =null;
        		try{
        		   mdls = qProcessor.findRelevantProcessModels(query);
        		}
        		catch(IOException sle)
        		{
        		    log.error("Could not determine process models relevant to this query.", sle);
        		}
        		mc.queryProc.procCmd = ProcessorCommand.ComplianceQuery;
        		respWriter.println("<query-result>");
//        		System.out.println("<query-result>");
        		boolean generateAntiPattern = false;
        		List<String> compliantModels = new ArrayList<String>();
        		List<ProcessGraph> nonCompliantProcesses = new ArrayList<ProcessGraph>();
        		for (int i = 0; i < mdls.size();i++)
        		{
        			
        			try
        			{
        				System.out.println("############# Checking model "+mdls.get(i));
        				int result = mc.checkModelWithoutReduction(mdls.get(i));
        				System.out.println("############# Model Checker Result is "+result );
        				if (result == ModelChecker.RET_NO_NET_MATCHES)
        				{
        					generateAntiPattern = true;
        					System.out.println("Query didnt find a match -> Does not comply :( ...");
        					nonCompliantProcesses.add(mc.queryProc.getInspectedProcess());

        				}
        				else if (result == ModelChecker.RET_NET_DOESNT_COMPLY)
        				{
        					generateAntiPattern = true;
        					System.out.println("Does not Comply :( ...");
        					nonCompliantProcesses.add(mc.queryProc.getInspectedProcess());
        				}
        				else if (result == ModelChecker.RET_NET_COMPLIES)
        					
        				{
        					compliantModels.add(mdls.get(i));
        				}
        			}
        			catch(Exception e)
        			{
        				respWriter.println("<Exception>"+e.getMessage()+"</Exception>");
        			}
        		}
        		if (generateAntiPattern)
        		{
//      			ComplianceViolationExplanator cve = new ComplianceViolationExplanator(mdls.get(i));
        			
        			for (ProcessGraph tmp : nonCompliantProcesses)
        			{
        				System.out.println("%%%%%%%%%%%%%%%%%%%% Non Compliant Process Model "+tmp.modelURI);
        				mc.getProcessGraph().print(System.out);
        				System.out.println("%%%%%%%%%%%%%%%%%%%% ");
        				ComplianceViolationExplanator cve = new ComplianceViolationExplanator(tmp);
        				List<QueryGraph> antiPatterns =  cve.explainViolation(tqry);//tqry.generateAntiPatternQueries();// cve.explainViolation(tqry);

        				for (QueryGraph q : antiPatterns)
        				{
        					System.out.println("########################################## ANTI PATTERN QUERY #################################");
        					q.print(System.out);
        					System.out.println("########################################## ANTI PATTERN QUERY #################################");
        					// this has to be changed
        					List<String> mdls2 = qProcessor.findRelevantProcessModels(q);

        					for (String s : mdls2)
        					{
        						if (compliantModels.contains(s))
        							continue;

        						boolean insert = false;
        						ProcessGraph matchAntiPattern=null;
        						QueryGraph cln = (QueryGraph) q.clone();
        						matchAntiPattern = qProcessor.runQueryAgainstModel(cln, s);
        						if(matchAntiPattern.nodes.size() > 0)
        						{

//      							boolean qFound= false;
//      							for (QueryGraph qq : queryMatches.keySet())
//      							{
//      							if (qq.getSignature().containsAll(q.getSignature()))
//      							{
//      							qFound = true;
//      							String mdlls = queryMatches.get(qq);
//      							if (!mdlls.contains(s))
//      							{
//      							mdlls +=","+s;
//      							queryMatches.put(qq, mdlls);
//      							insert = true;
//      							}
//      							}
//      							}
//      							if (!qFound)
//      							{
//      							insert = true;
//      							queryMatches.put(q, s);
//      							}
//      							System.out.println("############################### Insert value is "+insert);
//      							if (insert)
//      							{
        							matchAntiPattern.modelURI = s;
        							matchAntiPattern.exportXML(respWriter,"<match>antipattern</match>\n<diagnosis>violation scenario</diagnosis>");
//      							matchAntiPattern.print(System.out);
        							System.out.println("<match>antipattern</match>\n<diagnosis>violation scenario</diagnosis>");
//      							}

        						}
        						else
        						{
        							System.out.println("Anti Pattern Query didnt find a match");
        						}
        					}
        				}
        			}
        		}
        		respWriter.println("</query-result>");
        		System.out.println("########################################## PROCESS COMPLIANCE QUERY #################################");
        	}
        	else if ("runComplianceQueryAgainstModel".equalsIgnoreCase(command)) {
//                ProcessGraph match = null;
//                boolean doesMatch = qProcessor.testQueryAgainstModel(query, modelID, match);
        		qProcessor.includeEnclosingAndSplits= true;
                
                TemporalQueryGraph tqry = query.getTemporalQueryGraph();
            	ModelChecker mc = new ModelChecker(tqry,respWriter);
            	mc.queryProc = new OryxMemoryQueryProcessor(respWriter);
            	
            	mc.queryProc.procCmd = ProcessorCommand.ComplianceCheckWithViolationExplanation;
            	respWriter.println("<query-result>");
            	boolean generateAntiPattern = false;
            	int result = mc.checkModelWithoutReduction(modelID);
            	if (result == ModelChecker.RET_NO_NET_MATCHES)
            	{
            	    generateAntiPattern = true;
            	    System.out.println("Query didnt find a match -> Does not comply :( ...");
//            	    this.qProcessor.printMessage("Model " + modelId +" is not compliant");
            	}
            	else if (result == ModelChecker.RET_NET_DOESNT_COMPLY)
            	{
            	    generateAntiPattern = true;
            	    
//            	    this.qProcessor.printMessage("Model " + modelId +" is not compliant");
            	}
            	if (generateAntiPattern)
            	{
            		ComplianceViolationExplanator cve = new ComplianceViolationExplanator(mc.getProcessGraph());
					
					List<QueryGraph> antiPatterns =  cve.explainViolation(tqry);// tqry.generateAntiPatternQueries();
            		ProcessGraph matchAntiPattern=null;
            		for (QueryGraph q : antiPatterns)
            		{
            			matchAntiPattern = qProcessor.runQueryAgainstModel(q, modelID);
            			if(matchAntiPattern.nodes.size() > 0)
            			{
            				System.out.println("############ Anti Pattern Query found a match");
            				matchAntiPattern.modelURI = modelID;
            				matchAntiPattern.exportXML(respWriter,"<match>antipattern</match>\n<diagnosis>violation scenario</diagnosis>");
            			}
            		}

            	}
            	respWriter.println("</query-result>");

            } else if ("runQueryAgainstModel".equalsIgnoreCase(command)) {
                ProcessGraph match=null;
                qProcessor.includeEnclosingAndSplits= false;
                qProcessor.testQueryAgainstModel(query, modelID,match);
            } else if ("processMultiQuery".equalsIgnoreCase(command)) {
            	qProcessor.includeEnclosingAndSplits= false;
                List<Match> matches = qProcessor.processMultiQuery(query);
            } else { // default case, if no (or unknown) command was specified
            	qProcessor.includeEnclosingAndSplits= false;
                List<String> matchedModels = qProcessor.processQuery(query);
            }
        } catch (Exception e) {
            try {
                resp.sendError(500, "Query processing failed with an internal error: "+e.getMessage());
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
        endTime = System.currentTimeMillis();
        log.info("Total processing time "+ (endTime - startTime) + " ms");
    }

    private GraphBuilder getGraphBuilderFor(InputStream graph, String format,
            HttpServletRequest req) throws IOException {
        
        String referrerUri = req.getHeader("Referer");

        QueryGraphBuilderRDF queryGraphBuilder = new QueryGraphBuilderRDF(
                graph, RdfSyntax.RDF_XML, referrerUri);

        return queryGraphBuilder;
    }
    
}

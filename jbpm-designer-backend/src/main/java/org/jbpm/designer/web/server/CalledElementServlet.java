/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.util.Base64Backport;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.json.JSONObject;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueRuleAttributeValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sevlet for resolving called elements.
 * 
 * @author Tihomir Surdilovic
 */
public class CalledElementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CalledElementServlet.class);
	private ServletConfig config;

    @Inject
    private IDiagramProfileService _profileService = null;

    @Inject
    private RefactoringQueryService queryService;
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String profileName = req.getParameter("profile");
        String uuid = Utils.getUUID(req);
        String processPackage = req.getParameter("ppackage");
        String processId = req.getParameter("pid");
        String action = req.getParameter("action");
        
        IDiagramProfile profile = _profileService.findProfile(req, profileName);
        if(action != null && action.equals("openprocessintab")) {
        	String retValue = "";
        	List<String> allPackageNames = ServletUtil.getPackageNamesFromRepository(profile);
        	if(allPackageNames != null && allPackageNames.size() > 0) {
        		for(String packageName : allPackageNames) {
        			List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName, profile);
        			if(allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
        				for(String p : allProcessesInPackage) {
                			Asset<String> processContent = ServletUtil.getProcessSourceContent(p, profile);
                			Pattern idPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^\"]+)\"", Pattern.MULTILINE);
        		            Matcher idMatcher = idPattern.matcher(processContent.getAssetContent());
        		            if(idMatcher.find()) {
        		            	String pid = idMatcher.group(1);
        		            	String pidcontent = ServletUtil.getProcessImageContent(packageName, pid, profile);
        		            	if(pid != null && pid.equals(processId)) {
                                    String uniqueId = processContent.getUniqueId();
                                    if (Base64Backport.isBase64(uniqueId)) {
                                        byte[] decoded = Base64.decodeBase64(uniqueId);
                                        try {
                                            uniqueId =  new String(decoded, "UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    retValue = processContent.getName() + "." +processContent.getAssetType() + "|" + uniqueId;
        		            		break;
        		            	}
        		            }
                		}
        			}
        		}
        	}
        	resp.setCharacterEncoding("UTF-8");
	        resp.setContentType("text/plain");
	        resp.getWriter().write(retValue);
        } else if(action != null && action.equals("showruleflowgroups")) {
            //Query for RuleFlowGroups
            final List<RefactoringPageRow> results = queryService.query("FindRuleFlowNamesQuery",
                    new HashSet<ValueIndexTerm>() {{
                        add(new ValueRuleAttributeIndexTerm("ruleflow-group"));
                        add(new ValueRuleAttributeValueIndexTerm("*"));
                    }},
                    true);

            final List<String> ruleFlowGroupNames = new ArrayList<String>();
            for ( RefactoringPageRow row : results ) {
                ruleFlowGroupNames.add( (String) row.getValue() );
            }
            Collections.sort( ruleFlowGroupNames );

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(getRuleFlowGroupsInfoAsJSON(ruleFlowGroupNames).toString());

        } else if(action != null && action.equals("showdatatypes")) {
            final List<RefactoringPageRow> results2 = queryService.query("DesignerFindTypesQuery",
                    new HashSet<ValueIndexTerm>() {{
                        add(new ValueTypeIndexTerm("*"));
                    }},
                    true);
            final List<String> dataTypeNames = new ArrayList<String>();
            for ( RefactoringPageRow row : results2 ) {
                dataTypeNames.add( (String) row.getValue() );
            }
            Collections.sort( dataTypeNames );
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(getDataTypesInfoAsJSON(dataTypeNames).toString());
        } else {
	        String retValue = "false";
	        List<String> allPackageNames = ServletUtil.getPackageNamesFromRepository(profile);
	        Map<String, String> processInfo = new HashMap<String, String>();
	        if(allPackageNames != null && allPackageNames.size() > 0) {
	        	for(String packageName : allPackageNames) {
	        		List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName, profile);
	        		if(allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
	    				for(String p : allProcessesInPackage) {
	    					Asset<String> processContent = ServletUtil.getProcessSourceContent(p, profile);
	    					Pattern idPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^\"]+)\"", Pattern.MULTILINE);
	    		            Matcher idMatcher = idPattern.matcher(processContent.getAssetContent());
	    		            if(idMatcher.find()) {
	    		            	String pid = idMatcher.group(1);
	    		            	String pidcontent = ServletUtil.getProcessImageContent(processContent.getAssetLocation(), pid, profile);
	    		            	if(pid != null && !(packageName.equals(processPackage) && pid.equals(processId))) {
	    		            		processInfo.put(pid+"|"+processContent.getAssetLocation(), pidcontent != null ? pidcontent : "");
	    		            	}
	    		            }
	    				}

	    			}
	        	}
	        }
            retValue = getProcessInfoAsJSON(processInfo).toString();
			resp.setCharacterEncoding("UTF-8");
	        resp.setContentType("application/json");
	        resp.getWriter().write(retValue);
        }
	}

    public JSONObject getRuleFlowGroupsInfoAsJSON(List<String> ruleFlowGroupsInfo) {
        JSONObject jsonObject = new JSONObject();
        for(String infoEntry : ruleFlowGroupsInfo) {
            try {
                jsonObject.put(infoEntry, infoEntry);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public JSONObject getDataTypesInfoAsJSON(List<String> ruleFlowGroupsInfo) {
        JSONObject jsonObject = new JSONObject();
        for(String infoEntry : ruleFlowGroupsInfo) {
            try {
                jsonObject.put(infoEntry, infoEntry);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

	public JSONObject getProcessInfoAsJSON(Map<String, String> processInfo) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String,String> error: processInfo.entrySet()) {
			try {
				jsonObject.put(error.getKey(), error.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
}

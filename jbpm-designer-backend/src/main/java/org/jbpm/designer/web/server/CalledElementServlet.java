/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.query.DesignerFindDataTypesQuery;
import org.jbpm.designer.query.DesignerFindRuleFlowNamesQuery;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.json.JSONObject;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueBranchNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.java.nio.base.SegmentedPath;

@WebServlet(displayName = "CalledElement", name = "CalledElementServlet",
        urlPatterns = "/calledelement")
public class CalledElementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CalledElementServlet.class);

    protected IDiagramProfile profile;

    @Inject
    private IDiagramProfileService _profileService = null;

    @Inject
    protected RefactoringQueryService queryService;

    @Inject
    protected VFSService vfsServices;

    @Inject
    protected KieProjectService projectService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String processPackage = req.getParameter("ppackage");
        String processId = req.getParameter("pid");
        String action = req.getParameter("action");

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        if (action != null && action.equals("openprocessintab")) {
            String retValue = "";
            List<String> allPackageNames = ServletUtil.getPackageNamesFromRepository(profile);
            if (allPackageNames != null && allPackageNames.size() > 0) {
                for (String packageName : allPackageNames) {
                    List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName,
                                                                                              profile);
                    if (allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
                        for (String p : allProcessesInPackage) {
                            Asset<String> processContent = ServletUtil.getProcessSourceContent(p,
                                                                                               profile);
                            Pattern idPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^\"]+)\"",
                                                                Pattern.MULTILINE);
                            Matcher idMatcher = idPattern.matcher(processContent.getAssetContent());
                            if (idMatcher.find()) {
                                String pid = idMatcher.group(1);
                                String pidcontent = ServletUtil.getProcessImageContent(packageName,
                                                                                       pid,
                                                                                       profile);
                                if (pid != null && pid.equals(processId)) {
                                    String uniqueId = processContent.getUniqueId();
                                    if (Base64.isBase64(uniqueId)) {
                                        byte[] decoded = Base64.decodeBase64(uniqueId);
                                        try {
                                            uniqueId = new String(decoded,
                                                                  "UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    retValue = processContent.getName() + "." + processContent.getAssetType() + "|" + uniqueId;
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
        } else if (action != null && action.equals("showruleflowgroups")) {

            List<String> ruleFlowGroupNames = getRuleFlowNames(req);

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(getRuleFlowGroupsInfoAsJSON(ruleFlowGroupNames).toString());
        } else if (action != null && action.equals("showdatatypes")) {

            List<String> dataTypeNames = getJavaTypeNames(req);

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(getDataTypesInfoAsJSON(dataTypeNames).toString());
        } else {
            String retValue = "false";
            List<String> allPackageNames = ServletUtil.getPackageNamesFromRepository(profile);
            Map<String, String> processInfo = new HashMap<String, String>();
            if (allPackageNames != null && allPackageNames.size() > 0) {
                for (String packageName : allPackageNames) {
                    List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName,
                                                                                              profile);
                    if (allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
                        for (String p : allProcessesInPackage) {
                            Asset<String> processContent = ServletUtil.getProcessSourceContent(p,
                                                                                               profile);
                            Pattern idPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^\"]+)\"",
                                                                Pattern.MULTILINE);
                            Matcher idMatcher = idPattern.matcher(processContent.getAssetContent());
                            if (idMatcher.find()) {
                                String pid = idMatcher.group(1);
                                String pidcontent = ServletUtil.getProcessImageContent(processContent.getAssetLocation(),
                                                                                       pid,
                                                                                       profile);
                                if (pid != null && !(packageName.equals(processPackage) && pid.equals(processId))) {
                                    processInfo.put(pid + "|" + processContent.getAssetLocation(),
                                                    pidcontent != null ? pidcontent : "");
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

    // package scope in order to test the method
    List<String> getRuleFlowNames(HttpServletRequest req) {
        final String[] projectAndBranch = getProjectAndBranchNames(req);

        // Query RuleFlowGroups for asset project and branch
        List<RefactoringPageRow> results = queryService.query(
                DesignerFindRuleFlowNamesQuery.NAME,
                new HashSet<ValueIndexTerm>() {{
                    add(new ValueSharedPartIndexTerm("*",
                                                     PartType.RULEFLOW_GROUP,
                                                     TermSearchType.WILDCARD));
                    add(new ValueProjectNameIndexTerm(projectAndBranch[0]));
                    if (projectAndBranch[1] != null) {
                        add(new ValueBranchNameIndexTerm(projectAndBranch[1]));
                    }
                }});

        final List<String> ruleFlowGroupNames = new ArrayList<String>();
        for (RefactoringPageRow row : results) {
            ruleFlowGroupNames.add((String) row.getValue());
        }
        Collections.sort(ruleFlowGroupNames);

        // Query RuleFlowGroups for all projects and branches
        results = queryService.query(
                DesignerFindRuleFlowNamesQuery.NAME,
                new HashSet<ValueIndexTerm>() {{
                    add(new ValueSharedPartIndexTerm("*",
                                                     PartType.RULEFLOW_GROUP,
                                                     TermSearchType.WILDCARD));
                }});
        final List<String> otherRuleFlowGroupNames = new LinkedList<String>();
        for (RefactoringPageRow row : results) {
            String ruleFlowGroupName = (String) row.getValue();
            if (!ruleFlowGroupNames.contains(ruleFlowGroupName)) {
                // but only add the new ones
                otherRuleFlowGroupNames.add(ruleFlowGroupName);
            }
        }
        Collections.sort(otherRuleFlowGroupNames);

        ruleFlowGroupNames.addAll(otherRuleFlowGroupNames);

        return ruleFlowGroupNames;
    }

    // package scope in order to test the method
    List<String> getJavaTypeNames(HttpServletRequest req) {
        final String[] projectAndBranch = getProjectAndBranchNames(req);

        // Query RuleFlowGroups for asset project and branch
        List<RefactoringPageRow> results = queryService.query(
                DesignerFindDataTypesQuery.NAME,
                new HashSet<ValueIndexTerm>() {{
                    add(new ValueResourceIndexTerm("*",
                                                   ResourceType.JAVA,
                                                   TermSearchType.WILDCARD));
                    add(new ValueProjectNameIndexTerm(projectAndBranch[0]));
                    if (projectAndBranch[1] != null) {
                        add(new ValueBranchNameIndexTerm(projectAndBranch[1]));
                    }
                }});

        final List<String> dataTypeNames = new ArrayList<String>();
        for (RefactoringPageRow row : results) {
            dataTypeNames.add((String) row.getValue());
        }
        Collections.sort(dataTypeNames);

        // Query RuleFlowGroups for all projects and branches
        results = queryService.query(
                DesignerFindDataTypesQuery.NAME,
                new HashSet<ValueIndexTerm>() {{
                    add(new ValueResourceIndexTerm("*",
                                                   ResourceType.JAVA,
                                                   TermSearchType.WILDCARD));
                }});
        final List<String> otherDataTypeNames = new LinkedList<String>();
        for (RefactoringPageRow row : results) {
            String ruleFlowGroupName = (String) row.getValue();
            if (!dataTypeNames.contains(ruleFlowGroupName)) {
                // but only add the new ones
                otherDataTypeNames.add(ruleFlowGroupName);
            }
        }
        Collections.sort(otherDataTypeNames);

        dataTypeNames.addAll(otherDataTypeNames);

        return dataTypeNames;
    }

    private String[] getProjectAndBranchNames(HttpServletRequest req) {
        // Get info about project and branch
        String uuid = Utils.getUUID(req);
        Path myPath = vfsServices.get(uuid.replaceAll("\\s",
                                                      "%20"));
        KieProject project = projectService.resolveProject(myPath);
        final String projectName = project.getProjectName();
        String branchName = null;
        if (myPath instanceof SegmentedPath) {
            branchName = ((SegmentedPath) myPath).getSegmentId();
        }

        return new String[]{projectName, branchName};
    }

    public JSONObject getRuleFlowGroupsInfoAsJSON(List<String> ruleFlowGroupsInfo) {
        JSONObject jsonObject = new JSONObject();
        for (String infoEntry : ruleFlowGroupsInfo) {
            try {
                jsonObject.put(infoEntry,
                               infoEntry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public JSONObject getDataTypesInfoAsJSON(List<String> ruleFlowGroupsInfo) {
        JSONObject jsonObject = new JSONObject();
        for (String infoEntry : ruleFlowGroupsInfo) {
            try {
                jsonObject.put(infoEntry,
                               infoEntry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public JSONObject getProcessInfoAsJSON(Map<String, String> processInfo) {
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, String> error : processInfo.entrySet()) {
            try {
                jsonObject.put(error.getKey(),
                               error.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}

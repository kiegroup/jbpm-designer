package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.drools.core.util.ConfFileUtils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServletUtil.UrlType;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.json.JSONObject;

/**
 * Servlet for interaction with the jbpm service repository.
 * 
 * @author tsurdilo
 */
public class JbpmServiceRepositoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger.getLogger(JbpmServiceRepositoryServlet.class);
    private static final String displayRepoContent = "display";
    private static final String installRepoContent = "install";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String action = req.getParameter("action");
        String assetsToInstall = req.getParameter("asset");
        String categoryToInstall = req.getParameter("category");
        String repoURL = req.getParameter("repourl");

        if (repoURL == null || repoURL.length() < 1) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write("false");
            return;
        }

        try {
            URL url = new URL(repoURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.connect();
            if (conn.getResponseCode() != 200) {
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write("false");
                return;
            }
        } catch (Exception e) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write("false");
            return;
        }

        if (repoURL.endsWith("/")) {
            repoURL = repoURL.substring(0, repoURL.length() - 1);
        }

        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        Map<String, WorkDefinitionImpl> workitemsFromRepo = WorkItemRepository.getWorkDefinitions(repoURL);
        if (action != null && action.equalsIgnoreCase(displayRepoContent)) {
            if (workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
                String jsonObjectString = convertWorkItemsInfoToJsonString(workitemsFromRepo, repoURL);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write(jsonObjectString);
            } else {
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write("false");
                return;
            }
        } else if (action != null && action.equalsIgnoreCase(installRepoContent)) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            if (workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
                for (String key : workitemsFromRepo.keySet()) {
                    if (key.equals(assetsToInstall) && categoryToInstall.equals(workitemsFromRepo.get(key).getCategory())) {
                        String workitemDefinitionURL 
                            = repoURL + "/" + workitemsFromRepo.get(key).getName() + "/" + workitemsFromRepo.get(key).getName() + ".wid";
                        String iconFileURL = repoURL + "/" + workitemsFromRepo.get(key).getName() + "/"
                                + workitemsFromRepo.get(key).getIcon();
                        String workItemDefinitionContent = ConfFileUtils.URLContentsToString(new URL(workitemDefinitionURL));
                        String iconName = workitemsFromRepo.get(key).getIcon();
                        String widName = workitemsFromRepo.get(key).getName();
                        
                        byte[] iconContent = null;
                        try {
                            iconContent = getImageBytes(new URL(iconFileURL).openStream());
                        } catch (Exception e1) {
                            _logger.error("Could not read icon image: " + e1.getMessage());
                        }

                        String [] pkgAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
                        String packageName = pkgAssetInfo[0]; 
                        if (packageName != null) { 
                            // install wid and icon to guvnor
                            installWorkItemDefinitionAndIcon(profile, packageName, widName, iconName, workItemDefinitionContent, iconContent, resp);
                        } else {
                            _logger.error("Could not find the package for uuid: " + uuid);
                            resp.setCharacterEncoding("UTF-8");
                            resp.setContentType("application/json");
                            resp.getWriter().write("false");
                            return;
                        }
                    }
                }
            } else {
                _logger.error("Invalid or empty service repository.");
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write("false");
                return;
            }
        }
    }

    public static String convertWorkItemsInfoToJsonString(Map<String, WorkDefinitionImpl> workitemsFromRepo, String repoURL) { 
        Map<String, List<String>> retMap = new HashMap<String, List<String>>();
        for (String key : workitemsFromRepo.keySet()) {
            WorkDefinitionImpl wd = workitemsFromRepo.get(key);
            List<String> keyList = new ArrayList<String>();
            keyList.add(wd.getName() == null ? "" : wd.getName());
            keyList.add(wd.getDisplayName() == null ? "" : wd.getDisplayName());
            keyList.add(repoURL + "/" + wd.getName() + "/" + wd.getIcon());
            keyList.add(wd.getCategory() == null ? "" : wd.getCategory());
            keyList.add(wd.getExplanationText() == null ? "" : wd.getExplanationText());
            keyList.add(repoURL + "/" + wd.getName() + "/" + wd.getDocumentation());
            
            StringBuffer bn = new StringBuffer();
            if (wd.getParameterNames() != null) {
                String delim = "";
                for (String name : wd.getParameterNames()) {
                    bn.append(delim).append(name);
                    delim = ",";
                }
            }
            keyList.add(bn.toString());
            StringBuffer br = new StringBuffer();
            if (wd.getResultNames() != null) {
                String delim = "";
                for (String resName : wd.getResultNames()) {
                    br.append(delim).append(resName);
                    delim = ",";
                }
            }
            keyList.add(br.toString());
            retMap.put(key, keyList);
        }
        
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, List<String>> retMapKey : retMap.entrySet()) {
            try {
                jsonObject.put(retMapKey.getKey(), retMapKey.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public static void installWorkItemDefinitionAndIcon(IDiagramProfile profile, String pkg, String widName, String iconName, String workItemDefinitionContent,
            byte[] iconContent, HttpServletResponse resp) throws IOException {
        // GUVNOR JbpmServiceRepositoryServlet
        String widURL = ServletUtil.getUrl(profile, pkg, widName + ".wid", UrlType.Normal);
        String iconURL = ServletUtil.getUrl(profile, pkg, iconName, UrlType.Normal);
        String packageAssetsURL = ServletUtil.getUrl(profile, pkg, "", UrlType.Normal);

        // check if the wid already exists
        URL checkWidURL = new URL(widURL);
        HttpURLConnection checkWidConnection = (HttpURLConnection) checkWidURL.openConnection();
        ServletUtil.applyAuth(profile, checkWidConnection);
        checkWidConnection.setRequestMethod("GET");
        checkWidConnection.setRequestProperty("Accept", "application/atom+xml");
        checkWidConnection.connect();
        _logger.info("check wid connection response code: " + checkWidConnection.getResponseCode());
        if (checkWidConnection.getResponseCode() == 200) {
            URL deleteAssetURL = new URL(widURL);
            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL.openConnection();
            ServletUtil.applyAuth(profile, deleteConnection);
            deleteConnection.setRequestMethod("DELETE");
            deleteConnection.connect();
            _logger.info("delete wid response code: " + deleteConnection.getResponseCode());
        }

        // check if icon already exists
        URL checkIconURL = new URL(iconURL);
        HttpURLConnection checkIconConnection = (HttpURLConnection) checkIconURL.openConnection();
        ServletUtil.applyAuth(profile, checkIconConnection);
        checkIconConnection.setRequestMethod("GET");
        checkIconConnection.setRequestProperty("Accept", "application/atom+xml");
        checkIconConnection.connect();
        _logger.info("check icon connection response code: " + checkIconConnection.getResponseCode());
        if (checkIconConnection.getResponseCode() == 200) {
            URL deleteAssetURL = new URL(iconURL);
            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL.openConnection();
            ServletUtil.applyAuth(profile, deleteConnection);
            deleteConnection.setRequestMethod("DELETE");
            deleteConnection.connect();
            _logger.info("delete icon response code: " + deleteConnection.getResponseCode());
        }

        // replace the icon value of the workitem config to include the guvnor
        // rest url
        workItemDefinitionContent = workItemDefinitionContent.replaceAll("(\"icon\"\\s*\\:\\s*\")(.*?)(\")", "$1"
                + (packageAssetsURL + iconName.substring(0, iconName.indexOf(".")) + "/binary") + "$3");
        // write to guvnor
        URL createWidURL = new URL(packageAssetsURL);
        HttpURLConnection createWidConnection = (HttpURLConnection) createWidURL.openConnection();
        ServletUtil.applyAuth(profile, createWidConnection);
        createWidConnection.setRequestMethod("POST");
        createWidConnection.setRequestProperty("Content-Type", "application/octet-stream");
        createWidConnection.setRequestProperty("Accept", "application/atom+xml");
        createWidConnection.setRequestProperty("Slug", widName + ".wid");
        createWidConnection.setDoOutput(true);
        createWidConnection.getOutputStream().write(workItemDefinitionContent.getBytes("UTF-8"));
        createWidConnection.connect();
        System.out.println("created wid configuration:" + createWidConnection.getResponseCode());

        URL createIconURL = new URL(packageAssetsURL);
        HttpURLConnection createIconConnection = (HttpURLConnection) createIconURL.openConnection();
        ServletUtil.applyAuth(profile, createIconConnection);
        createIconConnection.setRequestMethod("POST");
        createIconConnection.setRequestProperty("Content-Type", "application/octet-stream");
        createIconConnection.setRequestProperty("Accept", "application/atom+xml");
        createIconConnection.setRequestProperty("Slug", URLEncoder.encode(iconName, "UTF-8"));
        createIconConnection.setDoOutput(true);
        createIconConnection.getOutputStream().write(iconContent);
        createIconConnection.connect();
        _logger.info("icon creation response code: " + createIconConnection.getResponseCode());
        System.out.println("created icon:" + createIconConnection.getResponseCode());
        
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().write("true");
        
        return;
    }

    private static byte[] getImageBytes(InputStream is) throws Exception {
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new Exception("Error creating image byte array.");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}

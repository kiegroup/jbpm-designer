package org.jbpm.designer.web.server;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.drools.core.util.ConfFileUtils;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Servlet for interaction with the jbpm service repository.
 * @author tsurdilo
 */
public class JbpmServiceRepositoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger
			.getLogger(JbpmServiceRepositoryServlet.class);
	private static final String displayRepoContent = "display";
	private static final String installRepoContent = "install";

    private IDiagramProfile profile;
    // this is here just for unit testing purpose
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uuid = req.getParameter("uuid");
		String profileName = req.getParameter("profile");
		String action = req.getParameter("action");
		String assetsToInstall = req.getParameter("asset");
		String categoryToInstall = req.getParameter("category");
		String repoURL = req.getParameter("repourl");
		
		
		if(repoURL == null || repoURL.length() < 1) {
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
		    if(conn.getResponseCode() != 200) {
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
		
		if(repoURL.endsWith("/")) {
			repoURL = repoURL.substring(0, repoURL.length() - 1);
		}


        if (profile == null) {
            profile = ServletUtil.getProfile(req, profileName, getServletContext());
        }
        Repository repository = profile.getRepository();

		Map<String, WorkDefinitionImpl> workitemsFromRepo = WorkItemRepository.getWorkDefinitions(repoURL);
		if(action != null && action.equalsIgnoreCase(displayRepoContent)) {
			if(workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
				Map<String, List<String>> retMap = new HashMap<String, List<String>>();
				for(String key : workitemsFromRepo.keySet()) {
					WorkDefinitionImpl wd = workitemsFromRepo.get(key);
					List<String> keyList = new ArrayList<String>();
					keyList.add(wd.getName() == null ? "" : wd.getName());
					keyList.add(wd.getDisplayName() == null ? "" : wd.getDisplayName());
					keyList.add(repoURL + "/" + wd.getName() + "/" + wd.getIcon());
					keyList.add(wd.getCategory() == null ? "" : wd.getCategory());
					keyList.add(wd.getExplanationText() == null ? "" : wd.getExplanationText());
					keyList.add(repoURL + "/" + wd.getName() + "/" + wd.getDocumentation());
					StringBuffer bn = new StringBuffer();
					if(wd.getParameterNames() != null) {
						String delim = "";
					    for (String name : wd.getParameterNames()) {
					        bn.append(delim).append(name);
					        delim = ",";
					    }
					}
					keyList.add(bn.toString());
					StringBuffer br = new StringBuffer();
					if(wd.getResultNames() != null) {
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
				for (Entry<String,List<String>> retMapKey : retMap.entrySet()) {
					try {
						jsonObject.put(retMapKey.getKey(), retMapKey.getValue());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("application/json");
				resp.getWriter().write(jsonObject.toString());
			} else {
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("application/json");
				resp.getWriter().write("false");
				return;
			}
		} else if(action != null && action.equalsIgnoreCase(installRepoContent)) {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");
			if(workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
				boolean gotPackage = false;
				String pkg = "";
				for(String key : workitemsFromRepo.keySet()) {
					if(key.equals(assetsToInstall) && categoryToInstall.equals(workitemsFromRepo.get(key).getCategory())) {
						String workitemDefinitionURL = repoURL + "/" + workitemsFromRepo.get(key).getName() + "/" + workitemsFromRepo.get(key).getName() + ".wid";
						String iconFileURL = repoURL + "/" + workitemsFromRepo.get(key).getName() + "/" + workitemsFromRepo.get(key).getIcon();
						String workItemDefinitionContent = ConfFileUtils.URLContentsToString(new URL(workitemDefinitionURL));
						String iconName = workitemsFromRepo.get(key).getIcon();
						String widName = workitemsFromRepo.get(key).getName();
						byte[] iconContent = null;
						try {
							iconContent = getImageBytes(new URL(iconFileURL)
							.openStream());
						} catch (Exception e1) {
							_logger.error("Could not read icon image: " + e1.getMessage());
						}
						// install wid and icon to guvnor

                        repository.deleteAsset(profile.getRepositoryGlobalDir() + "/" +  widName + ".wid");

                        AssetBuilder widAssetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
                        widAssetBuilder.name(widName)
                                       .location(profile.getRepositoryGlobalDir() + "/")
                                       .type("wid")
                                       .content(workItemDefinitionContent);

                        repository.createAsset(widAssetBuilder.getAsset());

                        AssetBuilder iconAssetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
                        String iconExtension = iconName.substring(iconName.lastIndexOf(".") + 1);
                        String iconFileName = iconName.substring(0, iconName.lastIndexOf("."));
                        iconAssetBuilder.name(iconFileName)
                                .location(profile.getRepositoryGlobalDir() + "/")
                                .type(iconExtension)
                                .content(iconContent);

                        repository.createAsset(iconAssetBuilder.getAsset());
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

	private byte[] getImageBytes(InputStream is) throws Exception {
		try {
			return IOUtils.toByteArray(is);
		}
		catch (IOException e) {
			throw new Exception("Error creating image byte array.");
		}
		finally {
			if (is != null) { is.close(); }
		}
	}

}

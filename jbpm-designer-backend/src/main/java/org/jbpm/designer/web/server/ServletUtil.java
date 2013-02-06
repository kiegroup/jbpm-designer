package org.jbpm.designer.web.server;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetNotFoundException;
import org.jbpm.designer.repository.Directory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.filters.FilterByFileName;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jbpm.designer.web.profile.impl.RepositoryInfo;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for web servlets.
 * 
 * @author Tihomir Surdilovic
 *
 */
public class ServletUtil {
	private static final Logger _logger = Logger.getLogger(ServletUtil.class);
	public static final String EXT_BPMN = "bpmn";
    public static final String EXT_BPMN2 = "bpmn2";
	private ServletUtil(){}

	
	public static List<String> getFormWidgetList(IDiagramProfile profile, Repository repository) {
		List<String> widgets = new ArrayList<String>();
        try {
            Collection<Asset> formWidgets = repository.listAssets(profile.getRepositoryGlobalDir(), new FilterByExtension("fw"));
            for (Asset widget : formWidgets) {
                widgets.add(widget.getName());
            }

        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
		return widgets;
	}
	
	public static String[] findPackageAndAssetInfo(String uuid, IDiagramProfile profile) {

        Repository repository = profile.getRepository();

        String[] pkgassetinfo = new String[2];
        try {
        Asset asset = repository.loadAsset(uuid);

        pkgassetinfo[0] = asset.getAssetLocation();
        pkgassetinfo[1] = asset.getName();
        } catch (AssetNotFoundException e) {
            _logger.error("Asset " + uuid + " not found");
        }

        return pkgassetinfo;
    }
	
	public static InputStream getInputStreamForURL(String urlLocation,
            String requestMethod, IDiagramProfile profile) throws Exception {
			URL url = new URL(urlLocation);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod(requestMethod);
			connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml,application/json,application/octet-stream,text/json,text/plain;q=0.9,*/*;q=0.8");

			connection.setRequestProperty("charset", "UTF-8");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setReadTimeout(5 * 1000);

			ServletUtil.applyAuth(profile, connection);

			connection.connect();

			BufferedReader sreader = new BufferedReader(new InputStreamReader(
			        connection.getInputStream(), "UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();

			String line = null;
			while ((line = sreader.readLine()) != null) {
			    stringBuilder.append(line + "\n");
			}
			
			return new ByteArrayInputStream(stringBuilder.toString().getBytes(
			        "UTF-8"));
    }
	
	public static void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
		if (RepositoryInfo.getRepositoryUsr(profile) != null && RepositoryInfo.getRepositoryUsr(profile).trim().length() > 0
				&& RepositoryInfo.getRepositoryPwd(profile) != null
				&& RepositoryInfo.getRepositoryPwd(profile).trim().length() > 0) {
			String auth = RepositoryInfo.getRepositoryUsr(profile) + ":" + RepositoryInfo.getRepositoryPwd(profile);
	        connection.setRequestProperty("Authorization", "Basic "
	                + Base64.encodeBase64String(auth.getBytes()));
		}
    }
	
	public static boolean assetExistsInRepository(String packageName, String assetName, IDiagramProfile profile) {
    	try {
            Repository repository = profile.getRepository();

            return repository.assetExists(packageName + "/" + assetName);

		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
        return false;
    }
	
	public static boolean existsProcessImageInRepository(String assetURL, IDiagramProfile profile) {
		try {	
			return profile.getRepository().assetExists(assetURL);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
        return false;
	}
	
	public static List<String> getPackageNamesFromRepository(IDiagramProfile profile) {
        List<String> packages = new ArrayList<String>();

        Repository repository = profile.getRepository();
        Collection<Directory> directories = repository.listDirectories("/");
        for (Directory directory : directories) {
            packages.add(directory.getLocation() + directory.getName());
        }

        return packages;
    }
	
	public static List<String> getAllProcessesInPackage(String pkgName, IDiagramProfile profile) {

        Repository repository = profile.getRepository();
        Collection<Asset> processesAssets = repository.listAssetsRecursively(pkgName, new FilterByExtension(EXT_BPMN));
        processesAssets.addAll(repository.listAssetsRecursively(pkgName, new FilterByExtension(EXT_BPMN2)));


        List<String> processes = new ArrayList<String>();

        for (Asset processAsset : processesAssets) {
            processes.add(processAsset.getUniqueId());
        }
        return processes;
    }
	
	public static String getProcessImageContent(String packageName, String processid, IDiagramProfile profile) {

        Repository repository = profile.getRepository();

        Collection<Asset> imageAssets = repository.listAssets(packageName, new FilterByFileName(processid + "-svg.svg"));
        if (imageAssets != null && imageAssets.size() > 0) {
            Asset image = imageAssets.iterator().next();
            try {
                Asset toGetAsset = profile.getRepository().loadAsset(image.getUniqueId());
                return  Base64.encodeBase64String( ((String) toGetAsset.getAssetContent()).getBytes("UTF-8") );
            } catch (Exception e) {
                _logger.error(e.getMessage());
                return "";
            }
        } else {
            return "";
        }
	}

    public static Collection<Asset> findAssetsInRepository(String assetName, IDiagramProfile profile) {

        Repository repository = profile.getRepository();

        return repository.listAssetsRecursively("/", new FilterByFileName(assetName));
    }
	
	public static Asset getProcessSourceContent(String uuid, IDiagramProfile profile) {
		try {
            Repository repository = profile.getRepository();

            Asset<String> processAsset = repository.loadAsset(uuid);

            return processAsset;
        } catch (Exception e) {
        	_logger.error("Error retrieving asset content: " + e.getMessage());
            return null;
        }
    }
	
	public static String streamToString(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			return sb.toString();
		} catch (Exception e) {
			_logger.error("Error converting input stream to string: "
					+ e.getMessage());
			return "";
		}
	}
}

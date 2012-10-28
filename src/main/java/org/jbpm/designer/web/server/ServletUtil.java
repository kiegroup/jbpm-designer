package org.jbpm.designer.web.server;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;

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
	
	public static IDiagramProfile getProfile(HttpServletRequest req,
            String profileName, ServletContext context) {
        IDiagramProfile profile = null;

        IDiagramProfileService service = new ProfileServiceImpl();
        service.init(context);
        profile = service.findProfile(req, profileName);
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Cannot determine the profile to use for interpreting models");
        }
        return profile;
    }
	
	public static String[] findPackageAndAssetInfo(String uuid, IDiagramProfile profile) {
        List<String> packages = getPackageNames(profile);
        return findPackageAndAssetInfo(uuid, packages, profile);
	}
	
	public static String[] findPackageAndAssetInfo(String uuid, List<String> packages, IDiagramProfile profile) {
        // GUVNOR ServletUtil
        boolean gotPackage = false;
        String[] pkgassetinfo = new String[2];
        for (String nextPackage : packages) {
        	try {
	        	String packageAssetURL = GuvnorUtil.getUrl(profile, nextPackage, "", UrlType.Normal);
	        	String content = GuvnorUtil.readStringContentFromUrl(packageAssetURL, "GET", profile);
	        	
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));
                
                String title = "";
                String readuuid = "";
                while (reader.hasNext()) {
                    int next = reader.next();
                    if (next == XMLStreamReader.START_ELEMENT) {
                        if ("title".equals(reader.getLocalName())) {
                            title = reader.getElementText();
                        }
                        if ("uuid".equals(reader.getLocalName())) {
                        	readuuid = reader.getElementText();
                        }
                    }
                    if (next == XMLStreamReader.END_ELEMENT) {
                    	if ("asset".equals(reader.getLocalName())) {
                    		if(title.length() > 0 && readuuid.length() > 0 && uuid.equals(readuuid)) {
                    			pkgassetinfo[0] = nextPackage;
                                pkgassetinfo[1] = title;
                                gotPackage = true;
                    		}
                    	}
                    }
                }
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            }
            if (gotPackage) {
                // noo need to loop through rest of packages
                break;
            }
        }
        
        return pkgassetinfo;
    }
	
	//OCRAM: move this to GuvnorUtil
	public static boolean assetExistsInGuvnor(String packageName, String assetName, IDiagramProfile profile) {
    	try {	
            // GUVNOR ServletUtil
    		String formURL = GuvnorUtil.getUrl(profile, packageName, assetName, UrlType.Normal);
    		return GuvnorUtil.readCheckAssetExists(formURL, profile);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
        return false;
    }
	
	public static List<String> getPackageNames(IDiagramProfile profile) {
        // GUVNOR ServletUtil
        List<String> packages = new ArrayList<String>();

        try {
            String packagesURL = GuvnorUtil.getUrl(profile, "", null, UrlType.Normal);
            String content = GuvnorUtil.readStringContentFromUrl(packagesURL, "GET", profile);
            
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                    	String pname = reader.getElementText();
                    	if(!pname.equalsIgnoreCase("Packages")) {
                    		 packages.add(pname);
                    	}
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("Error retriving packages from guvnor: " + e.getMessage());
        }
        return packages;
    }
	
	public static List<String> getAllProcessesInPackage(String pkgName, IDiagramProfile profile) {
        List<String> processes = new ArrayList<String>();
        // GUVNOR ServletUtil
        try {
	        String assetsURL = GuvnorUtil.getUrl(profile, pkgName, "", UrlType.Normal);
	        String content = GuvnorUtil.readStringContentFromUrl(assetsURL, "GET", profile);
            
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));

            String format = "";
            String title = ""; 
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("format".equals(reader.getLocalName())) {
                        format = reader.getElementText();
                    } 
                    if ("title".equals(reader.getLocalName())) {
                        title = reader.getElementText();
                    }
                    if ("asset".equals(reader.getLocalName())) {
                        if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                            processes.add(title);
                            title = "";
                            format = "";
                        }
                    }
                }
            }
            // last one
            if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                processes.add(title);
            }
        } catch (Exception e) {
        	_logger.error("Error finding processes in package: " + e.getMessage());
        } 
        return processes;
    }
	
	public static String getProcessSourceContent(String packageName, String assetName, IDiagramProfile profile) {
        // GUVNOR ServletUtil
		try {	
			String assetSourceURL = GuvnorUtil.getUrl(profile, packageName, assetName, UrlType.Source);
            return GuvnorUtil.readStringContentFromUrl(assetSourceURL, "GET", profile);
        } catch (Exception e) {
        	_logger.error("Error retrieving asset content: " + e.getMessage());
            return "";
        }
    }

}

package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.*;

import java.net.URL;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ProcessDiffServiceServlet;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class ProcessDiffServiceServletTest extends AbstractGuvnorIntegrationTest {

    private static final String GET_VERSION_ACTION = "getversion";
    private static final String VERSION_ONE_STRING = "versionOne";

    private static MockHttpServletRequest fillRequest(ServletContext servletContext, String action, String uuid, String version) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setParameter("profile", "jbpm");
        request.setMethod("POST");

        request.setParameter("action", action);
        request.setParameter("uuid", uuid);
        request.setParameter("version", version);

        return request;
    }

    /**
     * Method to fill Guvnor with information needed to test methods.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    protected static Map<String, String> setupGuvnorAndReturnInfo(URL guvnorUrl, IDiagramProfile profile) throws Exception {
        HashMap<String, String> info = new HashMap<String, String>();
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);

        String packageName = "processDiffTest" + sdf.format(new Date());
        info.put(PACKAGE_NAME, packageName);
        guvnor.createPackageViaAtom(packageName);
        String assetName = "testProc";

        // Create multiple versions of a process
        byte[] assetContentBytes = VERSION_ONE_STRING.getBytes();
        String uuid = guvnor.createBpmn2AssetAndReturnAssetUuid(packageName, assetName, assetContentBytes);
        info.put(UUID_STRING, uuid);
        guvnor.createBinaryAsset(packageName, getProcessId(assetName) + "-image");
        
        String processSource = getBpmn2ProcessWithProcessIdAsString(getProcessId(assetName));
        info.put(ASSET_CONTENT, processSource);
        assetContentBytes = processSource.getBytes();
        guvnor.updateAssetSource(packageName, assetName, ".bpmn2", assetContentBytes);
        guvnor.updateAssetSource(packageName, assetName, ".bpmn2", assetContentBytes);
        
        return info;
    }

    @Test
    public void getAssetVerionSourceAndVersionsTest() throws Exception {
        runGetAssetVerionSourceAndVersionsTest(guvnorUrl, profile, servletContext);
    }

    public static void runGetAssetVerionSourceAndVersionsTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext)
            throws Exception {
        // Setup Url
        Map<String, String> info = setupGuvnorAndReturnInfo(guvnorUrl, profile);

        String uuid = info.get(UUID_STRING);
      
        // check retrieving versions
        HttpServletRequest request = fillRequest(servletContext, GET_VERSION_ACTION, uuid, "1");
        String response = callServletServiceMethodAndGetResponse(ProcessDiffServiceServlet.class, request, servletContext);
        assertTrue("Unexpected content: " + response, response.startsWith(VERSION_ONE_STRING));
        
        request = fillRequest(servletContext, GET_VERSION_ACTION, uuid, "2");
        response = callServletServiceMethodAndGetResponse(ProcessDiffServiceServlet.class, request, servletContext);
        assertTrue("Unexpected content: " + response, response.startsWith(info.get(ASSET_CONTENT)));
        
        // Run method: get version list
        request = fillRequest(servletContext, null, uuid, null);
        response = callServletServiceMethodAndGetResponse(ProcessDiffServiceServlet.class, request, servletContext);
        
        // Check result
        JSONObject root = new JSONObject(new JSONTokener(response));
        List<String> versions = new ArrayList<String>();
        @SuppressWarnings("rawtypes")
        Iterator iter = root.keys();
        while( iter.hasNext() ) { 
            versions.add((String) iter.next());
        }
        assertTrue( "Unexpected response: " + response, versions.size() == 3);
        assertTrue( "Unexpected versions: " + versions.get(2) + ", " + versions.get(1) + ", " + versions.get(0), 
                "1".equals(versions.get(2)) && "2".equals(versions.get(1)) && "3".equals(versions.get(0)));
    }

}

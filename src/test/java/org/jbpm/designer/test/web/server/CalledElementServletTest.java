package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.*;

import java.net.URL;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.CalledElementServlet;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class CalledElementServletTest extends AbstractGuvnorIntegrationTest {

    private static MockHttpServletRequest fillRequest(ServletContext servletContext, String profileName, String processPackage, String processId, String action) { 
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setMethod("POST");
        
        request.setParameter("profile", profileName);
        request.setParameter("ppackage", processPackage);
        request.setParameter("pid", processId);
        request.setParameter("action", action);
        
        return request;
    }
    
    @Test
    public void findProcessImagePathInPackageTest() throws Exception { 
        runFindProcessImagePathInPackageTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runFindProcessImagePathInPackageTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        setupGuvnor(guvnorUrl, profile);
        
        // Setup
        String processPackage = packageNameList[2];
        String processId = getProcessId(packageToAssetNameListMap.get(processPackage).get(1));
        HttpServletRequest request = fillRequest(servletContext, "jbpm", processPackage, processId, null);
        
        // Run test method
        String jsonProcessPidPaths = callServletServiceMethodAndGetResponse(CalledElementServlet.class, request, servletContext);
        assertTrue("JSON process image path info was empty.", jsonProcessPidPaths != null && ! jsonProcessPidPaths.isEmpty() );

        // Parse return value from method
        HashMap<String, String> otherProcessPidPaths = new HashMap<String, String>();
        HashMap<String, String> otherProcessPidPkgs = new HashMap<String, String>();
        JSONObject root = new JSONObject(new JSONTokener(jsonProcessPidPaths));
        @SuppressWarnings("rawtypes")
        Iterator iter = root.keys();
        while( iter.hasNext() ) { 
            String key = (String) iter.next();
            Object val = root.get(key);
            
            String pid = key.replaceFirst("\\|.*", "");
            if( val.toString().isEmpty() ) { 
                continue;
            }
            otherProcessPidPaths.put(pid, val.toString());
            String pkg = key.replaceFirst(".*\\|", "");
            otherProcessPidPkgs.put(pid, pkg);
        }

        // Test content from return value
        HashSet<String> otherPkgs = new HashSet<String>();
        HashSet<String> otherPids = new HashSet<String>();
        assertTrue( otherProcessPidPaths.keySet().size() >= 3 );
        for( String otherPid : otherProcessPidPaths.keySet() ) { 
            String otherPidPkg = otherProcessPidPkgs.get(otherPid);
            assertTrue( "Found matchin process id, but shouldn't have: " + otherPid + "|" + otherPidPkg,
                    ! processId.equals(otherPid) || ! processPackage.equals(otherPidPkg) );
            String otherProcessImagePath = otherProcessPidPaths.get(otherPid);
            assertTrue( "Process image path URL is empty.", otherProcessImagePath != null && ! otherProcessImagePath.isEmpty() );
            otherPkgs.add(otherPidPkg);
            otherPids.add(otherPid);
            
        }
        assertTrue("Did not retrieve process images from package " + packageNameList[1], otherPkgs.contains(packageNameList[1]));
        assertTrue("Did not retrieve process images from package " + packageNameList[2], otherPkgs.contains(packageNameList[2]));
        
        String [] presentPidList = { 
                getProcessId(packageToAssetNameListMap.get(packageNameList[1]).get(0)),
                getProcessId(packageToAssetNameListMap.get(packageNameList[2]).get(0)),
                getProcessId(packageToAssetNameListMap.get(packageNameList[2]).get(2)) };
                
        for( String presentPid : presentPidList ) { 
            assertTrue("Did not retrieve process image for asset " + presentPid, otherPids.contains(presentPid));
        }
    }
    
    @Test
    public void findProcessImagePathTest() throws Exception { 
        runFindProcessImagePathTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runFindProcessImagePathTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        setupGuvnor(guvnorUrl, profile);
        
        // Setup
        String processPackage = packageNameList[2];
        List<String> assets = packageToAssetNameListMap.get(processPackage);
        assertNotNull("No assets could be found for package " + processPackage, assets);
        String processId = getProcessId(assets.get(1));
        HttpServletRequest request = fillRequest(servletContext, "jbpm", processPackage, processId, "imageview");
        
        // Run method
        String processPidPath = callServletServiceMethodAndGetResponse(CalledElementServlet.class, request, servletContext);
        
        // Test return value
        assertTrue("Process PID path empty.", processPidPath != null && ! processPidPath.isEmpty());
        assertTrue("Process PID path does not contain package name " + processPackage, processPidPath.contains(processPackage));
        assertTrue("Process PID path does not contain pid" + processId,  processPidPath.contains(processId));
    }
    
}

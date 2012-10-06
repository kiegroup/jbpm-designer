package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.*;

import java.net.URL;
import java.util.*;

import javax.servlet.ServletContext;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ProcessInfoServlet;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.icu.text.SimpleDateFormat;

@RunWith(Arquillian.class)
@RunAsClient
public class ProcessInfoServletTest extends AbstractGuvnorIntegrationTest {
    
    protected static HashMap<String, String> setupGuvnorAndReturnInfo(URL guvnorUrl, IDiagramProfile profile) throws Exception { 
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);

        String pkgName = "test" + sdf.format(new Date());
        guvnor.createPackageViaAtom(pkgName);
        
        String assetName = "proc" + sdf.format(new Date());
        String processId = getProcessId(assetName);
        byte [] assetContentBytes = getBpmn2ProcessWithProcessIdAsString(processId).getBytes();
        String uuid = guvnor.createBpmn2AssetAndReturnAssetUuid(pkgName, assetName, assetContentBytes);
        guvnor.createBinaryAsset(pkgName, processId + "-image");
       
        HashMap<String, String> info = new HashMap<String, String>();
        info.put(UUID_STRING, uuid);
        info.put(PACKAGE_NAME, pkgName);
        info.put(ASSET_NAME, assetName);
        
        return info;
    }

    /**
     * This tests the following classes and methods: 
     * <ul>
     * <li>{@link ProcessInfoServlet}</li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void getProcessInfoTest() throws Exception { 
        runGetProcessInfoTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runGetProcessInfoTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        HashMap<String, String> info = setupGuvnorAndReturnInfo(guvnorUrl, profile);
      
        Map<String, String> infoMap = ProcessInfoServlet.getProcessInfo(info.get(PACKAGE_NAME), info.get(ASSET_NAME), info.get(UUID_STRING), profile);
        String [] params = { "Name", "Package", "Format", "Created", "Created By", "Last Modified", "Comment", "Version" };
        for( String param : params ) { 
            assertTrue( "Does not contain key " + param, infoMap.containsKey(param) );
        } 
        
        String paramVal = infoMap.get("Name");
        assertEquals("Name is incorrect.", info.get(ASSET_NAME), paramVal);
        paramVal = infoMap.get("Package");
        assertEquals("Package is incorrect.", info.get(PACKAGE_NAME), paramVal);
        
        paramVal = infoMap.get("Format");
        assertTrue("Format should be bpmn2, not " + paramVal, "bpmn2".equals(paramVal));
        paramVal = infoMap.get("Created");
        SimpleDateFormat sdfCreated = new SimpleDateFormat("yyyy-MM-dd");
        assertTrue("Created date is not correct: '" + paramVal + "'", 
                paramVal != null && !paramVal.isEmpty() && paramVal.startsWith(sdfCreated.format(new Date())) );
        
    }
    
}

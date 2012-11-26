package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.assertTrue;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ThemeServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

@RunWith(Arquillian.class)
@RunAsClient
public class ThemeServletTest extends AbstractGuvnorIntegrationTest {

    private static final String ACTION_GETTHEMENAMES = "getThemeNames";
    private static final String ACTION_GETTHEMEJSON = "getThemeJSON";
    
    private static MockHttpServletRequest fillRequest(ServletContext servletContext, String action) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setParameter("profile", "jbpm");
        request.setParameter("action", action);
        request.setMethod("POST");
        
        return request;
    }
    
    /**
     * Method to fill Guvnor with information needed to test methods.
     * @throws Exception When something goes wrong. 
     */
    protected static void setupGuvnor(URL guvnorUrl, IDiagramProfile profile) throws Exception {
        String packageName = GLOBAL_AREA;
        String assetName = "themes";
        
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        if( guvnor.checkIfAssetExists(packageName, assetName) ) { 
            guvnor.deleteAsset(packageName, assetName, true);
        }
        
        String themesJson = readFile("/guvnor-integration/themes.json").toString();
        guvnor.createAsset(packageName, assetName + ".json", themesJson.getBytes());
    }
    
    @Test
    public void getThemeNamesTest() throws Exception { 
        runGetThemeNamesTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runGetThemeNamesTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        // Setup Url
        setupGuvnor(guvnorUrl, profile);
       
        // Make sure servlet doesn't have access to webapp files
        servletContext = new MockServletContext("guvnor-integration");
        
        // Run test method
        HttpServletRequest request = fillRequest(servletContext, ACTION_GETTHEMENAMES);
        String responseString = callServletServiceMethodAndGetResponse(ThemeServlet.class, request, servletContext);
        assertTrue( "Incorrect theme names retrieved: " + responseString, responseString.contains("Marceline"));

        // Run test method
        request = fillRequest(servletContext, ACTION_GETTHEMEJSON);
        responseString = callServletServiceMethodAndGetResponse(ThemeServlet.class, request, servletContext);
        assertTrue( "Incorrect theme names retrieved: " + responseString, responseString.contains("Marceline"));
    }

}

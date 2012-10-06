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
import org.jbpm.designer.web.server.CustomEditorsServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

@RunWith(Arquillian.class)
@RunAsClient
public class CustomEditorsServletTest extends AbstractGuvnorIntegrationTest {

    private static final String jsonObjectName = CustomEditorsServletTest.class.getSimpleName();
    
    private static MockHttpServletRequest fillRequest(ServletContext servletContext) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setParameter("profile", "jbpm");
        request.setMethod("POST");
        
        return request;
    }
    
    /**
     * Method to fill Guvnor with information needed to test methods.
     * @throws Exception When something goes wrong. 
     */
    protected static void setupGuvnor(URL guvnorUrl, IDiagramProfile profile) throws Exception {
        String packageName = GLOBAL_AREA;
        String assetName = "customeditors";
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        if( ! guvnor.checkIfAssetExists(packageName, assetName) ) { 
            guvnor.createJsonAsset(packageName, assetName, jsonObjectName);
        }
    }
    
    @Test
    public void getCustomEditorsJSONTest() throws Exception { 
        runGetCustomEditorsJSONTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runGetCustomEditorsJSONTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        // Setup Url
        setupGuvnor(guvnorUrl, profile);
        
        // Run test method
        HttpServletRequest request = fillRequest(servletContext);
        servletContext = new MockServletContext("guvnor-integration");
        String customEditorsJson = callServletServiceMethodAndGetResponse(CustomEditorsServlet.class, request, servletContext);

        // jsonObjectname or this? 
        assertTrue( "String does not contain '" + jsonObjectName + "' : [" + customEditorsJson + "]",
        		customEditorsJson.contains("This String Verifies That The Correct Custom Editor was Loaded")
        		|| customEditorsJson.contains(jsonObjectName));
    }
    
}

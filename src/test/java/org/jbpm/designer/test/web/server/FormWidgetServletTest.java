package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.FormWidgetServlet;
import org.jbpm.designer.web.server.ServletUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class FormWidgetServletTest extends AbstractGuvnorIntegrationTest {

    private static final String GET_WIDGET = "getwidgets";
    private static final String GET_SOURCE = "getwidgetsource";
    
    private static MockHttpServletRequest fillRequest(ServletContext servletContext, String action, String widgetName) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setParameter("profile", "jbpm");
        request.setMethod("POST");
        request.setParameter("action", action);
        request.setParameter("widgetname", widgetName);
        
        return request;
    }
    
    protected static HashMap<String, String> setupGuvnorAndReturnInfo(URL guvnorUrl, IDiagramProfile profile) throws IOException { 
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);

        HashMap<String, String> assets = new HashMap<String, String>();
        for( int i = 0; i < 3; ++i ) { 
           String assetName = "fw" + i + "-" + sdf.format(new Date());
           String content = guvnor.createFormWidgetAsset(assetName);
           assets.put(assetName, content);
        }
        return assets;
    }

    /**
     * This tests the following classes and methods: 
     * <ul>
     * <li>{@link ServletUtil#getFormWidgetList(org.jbpm.designer.web.profile.IDiagramProfile)}</li>
     * <li>{@link FormWidgetServlet}</li>
     * </ul>
     * @throws Exception
     */
    @Test
    public void getFormWidgetListTest() throws Exception { 
        runGetFormWidgetListtest(guvnorUrl, profile, servletContext);
    }
    
    public static void runGetFormWidgetListtest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        HashMap<String, String> assets = setupGuvnorAndReturnInfo(guvnorUrl, profile);
        
        List<String> retrievedFormWidgets = FormWidgetServlet.getFormWidgetList(profile);
        for( String fwName : assets.keySet() ) { 
            assertTrue( "Retrieved form list does not contain '" + fwName + "'", retrievedFormWidgets.contains(fwName) );
        }
        
        HttpServletRequest request = fillRequest(servletContext, GET_WIDGET, "not-used-in-this-request");
        String responseString = callServletServiceMethodAndGetResponse(FormWidgetServlet.class, request, servletContext);
        for( String fwName : assets.keySet() ) { 
            assertTrue( "Retrieved JSON form list does not contain '" + fwName + "': " + responseString, 
                    responseString.contains(fwName));
        }

        String fwAssetName = assets.keySet().iterator().next();
        request = fillRequest(servletContext, GET_SOURCE, fwAssetName);
        responseString = callServletServiceMethodAndGetResponse(FormWidgetServlet.class, request, servletContext);
        // Guvnor always adds a new line to the source
        assertEquals(fwAssetName + " source is not the same.", assets.get(fwAssetName) + "\n", responseString);
    }
    
}

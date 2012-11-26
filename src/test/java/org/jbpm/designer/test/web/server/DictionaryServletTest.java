package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.*;

import java.net.URL;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.DictionaryServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class DictionaryServletTest extends AbstractGuvnorIntegrationTest {

    private static final String ACTION_LOAD = "load";
    private static final String ACTION_SAVE = "save";
    private static final String DICTIONARY_FNAME = "processdictionary";
    
    private static MockHttpServletRequest fillRequest(ServletContext servletContext, String... params) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setParameter("profile", "jbpm");
        request.setMethod("POST");
        request.setParameter("action", params[0]);
        if( params.length == 2 ) { 
            request.setParameter("dvalue", params[1]);
        }
        
        return request;
    }
    
    @Test
    public void storeAndGetDictionaryTest() throws Exception { 
        runStoreAndGetDictionaryTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runStoreAndGetDictionaryTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception { 
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        guvnor.deleteAsset(GLOBAL_AREA, DICTIONARY_FNAME, false);

        String jsonAssetContentString = "{ \"" + DictionaryServletTest.class.getSimpleName() + "\": \"" + sdf.format(new Date()) + "\" }"; 
        
        // Run test method
        HttpServletRequest request = fillRequest(servletContext, ACTION_SAVE, jsonAssetContentString);
        String responseString = callServletServiceMethodAndGetResponse(DictionaryServlet.class, request, servletContext);
        assertTrue( "String does not contain 'saved'", responseString.contains("saved") );
        
        request = fillRequest(servletContext, ACTION_LOAD);
        responseString = callServletServiceMethodAndGetResponse(DictionaryServlet.class, request, servletContext);
        assertTrue("Retrieved dictionary value is not the same: [" + responseString + "] <-> [" + jsonAssetContentString + "]", 
                responseString.startsWith(jsonAssetContentString));
    }
    
}

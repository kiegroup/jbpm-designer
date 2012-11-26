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
import org.jbpm.designer.web.server.TaskFormsServlet;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class TaskFormsServletTest extends AbstractGuvnorIntegrationTest {

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

        String packageName = "taskFormTest" + sdf.format(new Date());
        info.put(PACKAGE_NAME, packageName);
        guvnor.createPackageViaAtom(packageName);

        //???
        
        return info;
    }

    @Test
    public void storeTaskFormTest() throws Exception {
        finishWritingRunStoreTaskFormtest(guvnorUrl, profile, servletContext);
    }

    public void finishWritingRunStoreTaskFormtest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext)
            throws Exception {
        // Setup Url
        Map<String, String> info = setupGuvnorAndReturnInfo(guvnorUrl, profile);

        String uuid = info.get(UUID_STRING);
      
        // check retrieving versions
        HttpServletRequest request = fillRequest(servletContext, GET_VERSION_ACTION, uuid, "1");
        String response = callServletServiceMethodAndGetResponse(TaskFormsServlet.class, request, servletContext);
        assertTrue("Unexpected content: " + response, response.startsWith(VERSION_ONE_STRING));
        
    }

}

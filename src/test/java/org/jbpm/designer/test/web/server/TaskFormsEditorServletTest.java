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
import org.jbpm.designer.web.server.TaskFormsEditorServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class TaskFormsEditorServletTest extends AbstractGuvnorIntegrationTest {

    private static final String TASKFORM_FILE_EXTENSION = ".flt";
    private static final String TASKFORM_NAME_EXTENSION = "-taskform";
    private static final String ACTION_LOAD = "load";
    private static final String ACTION_SAVE = "save";

    private static final String TASK_NAME = "taskName";
    private static final String PUBLISHED_DATE = "publishedDate";
    private static final String TASK_FORM_CONTENT_STRING = "{ \"test\" : \"test\" }";

    private static MockHttpServletRequest fillRequest(ServletContext servletContext, String action, String uuid, String taskName,
            String taskFormValue) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setParameter("profile", "jbpm");
        request.setMethod("POST");

        request.setParameter("action", action);
        request.setParameter("uuid", uuid);
        request.setParameter("taskname", taskName);
        request.setParameter("tfvalue", taskFormValue);

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
        String taskName = "sample";
        info.put(TASK_NAME, taskName);
        String assetName = taskName + TASKFORM_NAME_EXTENSION;
        info.put(ASSET_NAME, assetName);
//        String taskFormAssetContent = readFile("/guvnor-integration/sample-taskform.flt").toString();

        info.put(ASSET_CONTENT, TASK_FORM_CONTENT_STRING);
        byte[] taskFormAssetContentBytes = TASK_FORM_CONTENT_STRING.getBytes("UTF-8");

        // Create things
        guvnor.createPackageViaAtom(packageName);
        String uuid = guvnor.createAssetAndReturnAssetUuid(packageName, assetName, TASKFORM_FILE_EXTENSION,
                taskFormAssetContentBytes);
        info.put(UUID_STRING, uuid);
        
        String assetInfo = guvnor.getAssetInfo(packageName, assetName);
        info.put(PUBLISHED_DATE, getPublishedDate(assetInfo));
        
        return info;
    }

    @Test
    public void storeAndGetTaskFormFromGuvnorTest() throws Exception {
        runStoreAndGetTaskFormFromGuvnorTest(guvnorUrl, profile, servletContext);
    }

    public static void runStoreAndGetTaskFormFromGuvnorTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext)
            throws Exception {
        // Setup Url
        Map<String, String> info = setupGuvnorAndReturnInfo(guvnorUrl, profile);

        String uuid = info.get(UUID_STRING);
        String taskName = info.get(TASK_NAME);
        String packageName = info.get(PACKAGE_NAME);
        String taskFormValue = info.get(ASSET_CONTENT);
        String origPublishedDate = info.get(PUBLISHED_DATE);

        // Run test method
        HttpServletRequest request = fillRequest(servletContext, ACTION_SAVE, uuid, taskName, taskFormValue);
        String taskFormValueOutput = callServletServiceMethodAndGetResponse(TaskFormsEditorServlet.class, request,
                servletContext);

        // Check test method effects
        assertEquals("Servlet method output was not correct.", "ok", taskFormValueOutput );
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        String assetSource = guvnor.getAssetSource(packageName, taskName + TASKFORM_NAME_EXTENSION);
        assertTrue( "Asset source is different: " + assetSource, assetSource.contains(TASK_FORM_CONTENT_STRING));
        
        String assetInfo = guvnor.getAssetInfo(packageName, taskName + TASKFORM_NAME_EXTENSION);
        String insertedPublishedDate = getPublishedDate(assetInfo); 
        assertTrue( "Asset was not overwritten.", ! origPublishedDate.equals(insertedPublishedDate) );
        
        uuid = getUuid(assetInfo);
        
        // Load with new uuid
        request = fillRequest(servletContext, ACTION_LOAD, uuid, taskName, taskFormValue);
        String newTaskFormValueOutput = callServletServiceMethodAndGetResponse(TaskFormsEditorServlet.class, request,
                servletContext);
        
        // Check method result
        assertEquals( "Retrieved task form source is different.", assetSource, newTaskFormValueOutput);
    }

}

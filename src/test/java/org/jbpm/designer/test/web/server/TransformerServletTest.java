package org.jbpm.designer.test.web.server;

import static junit.framework.Assert.*;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.TransformerServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(Arquillian.class)
@RunAsClient
public class TransformerServletTest extends AbstractGuvnorIntegrationTest {

    private static final String TO_PDF = "pdf";
    private static final String TO_PNG = "png";

    private static int UUID = 0;
    private static int TRANSFORM_TO = 1;
    private static int RESPONSE_ACTION = 2;
    private static int FORMATTED_SVG = 3;
    private static int RAW_SVG = 4;
    
    /**
     * Not used in this test: 
    private static int JPDL = 5;
    private static int GPD = 6;
    private static int BPMN2 = 7;
    private static int PP = 8; 
     */
    
    private static int PROCESS_ID = 9;
    
    private static final String [] paramNames = { 
        "uuid",
        "transformto",
        "respaction",
        "fsvg",
        "rsvg",
        "jpdl",
        "gpd",
        "bpmn2",
        "pp",
        "processid"
    };
    
    private static MockHttpServletRequest fillRequest(ServletContext servletContext, HashMap<String, String> params) {
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);

        for( String name : params.keySet() ) { 
            request.setParameter(name, params.get(name));
        }
        
        request.setParameter("profile", "jbpm");
        request.setMethod("POST");
        
        return request;
    }

    @Test
    public void transformToPdfTest() throws Exception { 
        runTransformToPdfTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runTransformToPdfTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception {
        doTransformToFormatTest(TO_PDF, guvnorUrl, profile, servletContext);
    }
    
    @Test
    public void transformToPngTest() throws Exception { 
        runTransformToPngTest(guvnorUrl, profile, servletContext);
    }
    
    public static void runTransformToPngTest(URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception {
        doTransformToFormatTest(TO_PNG, guvnorUrl, profile, servletContext);
    }
    
    private static void doTransformToFormatTest(String format, URL guvnorUrl, IDiagramProfile profile, ServletContext servletContext) throws Exception {
        // Setup
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        // package
        String packageName = sdf.format(new Date()) + "pkg";
        guvnor.createPackageViaAtom(packageName);
        // asset
        String assetName = "bpmn2Asset";
        String bpmn2Process = getBpmn2ProcessWithProcessIdAsString(getProcessId(assetName));
        String uuid = guvnor.createBpmn2AssetAndReturnAssetUuid(packageName, assetName, bpmn2Process.getBytes());
        String rawSvg = readFile("/guvnor-integration/example.svg").toString();

        // format extension
        String assetExt = null;
        if( TO_PDF.equals(format) ) { 
            assetExt = "-pdf";
        } else if( TO_PNG.equals(format) ) { 
            assetExt = "-image";
        } else { throw new RuntimeException("Unknown format: [" + format + "]"); }
        
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(paramNames[TRANSFORM_TO], format);
        params.put(paramNames[UUID], uuid);
        params.put(paramNames[RAW_SVG], rawSvg);
        
        // Run method
        HttpServletRequest request = fillRequest(servletContext, params);
        callServletServiceMethodAndGetResponse(TransformerServlet.class, request, servletContext);

        String transformedAssetName = getProcessId(assetName) + assetExt;
        assertFalse(packageName + "/" + transformedAssetName + " should not exist yet",
                guvnor.checkIfAssetExists(packageName, transformedAssetName));
        
        // Rerun method -- and this time, it should create something
        params.put(paramNames[PROCESS_ID], getProcessId(assetName));
        request = fillRequest(servletContext, params);
        String response = callServletServiceMethodAndGetResponse(TransformerServlet.class, request, servletContext);
        
        assertTrue(packageName + "/" + transformedAssetName + " should exist now!",
                guvnor.checkIfAssetExists(packageName, transformedAssetName));
        assertTrue("Response does not contain transcoded bytes [" + response.length() + "]", response.length() > 1900 );
        
        String assetXmlInfo = guvnor.getAssetInfo(packageName, transformedAssetName);
        assertTrue( assetXmlInfo != null && ! assetXmlInfo.isEmpty() );
        String origPublishedDate = getPublishedDate(assetXmlInfo);
        assertTrue( origPublishedDate != null && ! origPublishedDate.isEmpty() );

        // Rerun method -- does it replace the original? 
        request = fillRequest(servletContext, params);
        callServletServiceMethodAndGetResponse(TransformerServlet.class, request, servletContext);
        
        assertTrue(packageName + "/" + transformedAssetName + " should exist now!",
                guvnor.checkIfAssetExists(packageName, transformedAssetName));
        
        assetXmlInfo = guvnor.getAssetInfo(packageName, transformedAssetName);
        assertTrue( assetXmlInfo != null && ! assetXmlInfo.isEmpty() );
        String newPublishedDate = getPublishedDate(assetXmlInfo);
        
        assertTrue( "Published date should not be the same: " + origPublishedDate, ! origPublishedDate.equals(newPublishedDate) );
    }
    
}

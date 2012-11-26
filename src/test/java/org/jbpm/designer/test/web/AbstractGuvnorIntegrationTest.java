package org.jbpm.designer.test.web;

import static junit.framework.Assert.assertTrue;
import static org.jbpm.designer.test.web.util.ArquillianDeploymentUtil.createGuvnorWar;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.designer.test.ScratchTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.*;

public abstract class AbstractGuvnorIntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(AbstractGuvnorIntegrationTest.class);
    
    @Deployment(testable = false, name = "guvnor")
    public static WebArchive deployment() {
        return createGuvnorWar("5.4.0.Final");
    }

    @ArquillianResource
    protected URL guvnorUrl;

    public static Properties mavenProps = new Properties();
    static {
        assignOpenPortToArquillianServer();
        try {
            mavenProps.load(AbstractGuvnorIntegrationTest.class.getResourceAsStream("/mavenProperties"));
        } catch (IOException e) {
            // do nothing
        }
    }
    
    private static void assignOpenPortToArquillianServer() { 
        Random random = new Random();
        // http://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers#Dynamic.2C_private_or_ephemeral_ports 
        // Starts at  49152, max port is 65535
        
    }
    
    public static ServletContext createServletContext() {
        String mavenBaseDirPath = (String) mavenProps.getProperty("buildDir");
        assertTrue("buildDir property is empty or null.", mavenBaseDirPath != null && !mavenBaseDirPath.isEmpty());

        return new MockServletContext("file://" + mavenBaseDirPath);
    }

    protected static SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
    protected static SimpleDateFormat sdfPublished = new SimpleDateFormat("yyyy-MM-dd");
    
    protected ServletContext servletContext;
    protected IDiagramProfile profile;
    
    @Before
    public void before() throws Exception { 
        servletContext = createServletContext();
        profile = new JbpmProfileImpl(servletContext);
    }
    
    @After
    public void after() { 
        servletContext = null;
        profile = null;
    }
    
    protected static String [] packageNameList = new String[3];
    protected static HashMap<String, List<String>> packageToAssetUuidListMap = new HashMap<String, List<String>>();
    protected static HashMap<String, List<String>> packageToAssetNameListMap = new HashMap<String, List<String>>();
    protected static HashMap<String, String> uuidAssetMap = new HashMap<String, String>();
    
    protected static final String PACKAGE_NAME = "packageName";
    protected static final String ASSET_NAME = "assetName";
    protected static final String ASSET_CONTENT = "assetContent";
    protected static final String UUID_STRING = "uuid";
    
    protected static final String GLOBAL_AREA = "globalArea";
    
    private static final AtomicBoolean setupDone = new AtomicBoolean(false);
    
    /**
     * Method to fill Guvnor with information needed to test methods.
     * @throws Exception When something goes wrong. 
     */
    protected static void setupGuvnor(URL guvnorUrl, IDiagramProfile profile) throws Exception {
        if( setupDone.get() ) { 
            return;
        }
        setupDone.set(true);
        
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        
        // Insert packages
        final String now = sdf.format(new Date());
        packageNameList[0] = ("one" + now).intern();
        packageNameList[1] = ("two" + now).intern();
        packageNameList[2] = ("thr" + now).intern();

        for (String pkg : packageNameList) {
            guvnor.createPackageViaAtom(pkg);
        }

        // First package is empty
        String packageName = packageNameList[0];
        packageToAssetUuidListMap.put(packageName, new ArrayList<String>(0));
        packageToAssetNameListMap.put(packageName, new ArrayList<String>(0));

        int a = 0;
        
        // Second package contains 1 asset
        {
            packageName = packageNameList[1];
            List<String> uuids = new ArrayList<String>();
            List<String> assets = new ArrayList<String>();

            String assetName = "asset" + ++a;
            String processId = getProcessId(assetName);
            byte[] assetContentBytes = getBpmn2ProcessWithProcessIdAsString(processId).getBytes();
            String uuid = guvnor.createBpmn2AssetAndReturnAssetUuid(packageName, assetName, assetContentBytes);
            guvnor.createBinaryAsset(packageName, processId + "-image");

            uuids.add(uuid);
            assets.add(assetName);
            uuidAssetMap.put(uuid, assetName);
            packageToAssetUuidListMap.put(packageName, uuids);
            packageToAssetNameListMap.put(packageName, assets);
        }

        // Third package contains multiple assets
        packageName = packageNameList[2];
        List<String> uuids = new ArrayList<String>();
        List<String> assets = new ArrayList<String>();

        for (int i = 1; i < 4; ++i) {
            String assetName = "asset" + ++a;
            String processId = getProcessId(assetName);
            byte [] assetContentBytes = getBpmn2ProcessWithProcessIdAsString(processId).getBytes();
            String uuid = guvnor.createBpmn2AssetAndReturnAssetUuid(packageName, assetName, assetContentBytes);
            guvnor.createBinaryAsset(packageName, processId + "-image");
            
            uuids.add(uuid);
            assets.add(assetName);
            uuidAssetMap.put(uuid, assetName);
        }
        packageToAssetUuidListMap.put(packageName, uuids);
        packageToAssetNameListMap.put(packageName, assets);
    }
    
    protected static String getBpmn2ProcessWithProcessIdAsString(String processId) throws IOException {
        String resourcePath = "/guvnor-integration/process.bpmn2";
        StringBuilder processString = readFile(resourcePath);
        
        return processString.replace(544, 544+9, processId).toString();
    }
    
    protected static String getProcessId(String assetName) { 
       return "pid"+ assetName;
    }
    
    protected static String callServletServiceMethodAndGetResponse(Class servletClass, ServletRequest request, ServletContext servletContext) throws Exception {
        HttpServlet servlet = (HttpServlet) Class.forName(servletClass.getName()).newInstance();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        servlet.init(new MockServletConfig(servletContext));
        servlet.service(request, response);
        
        return response.getContentAsString();
    }
    
    
    protected static StringBuilder readFile(String resourcePath) throws IOException {
        InputStream is = ScratchTest.class.getResourceAsStream(resourcePath);
        
        final char[] buffer = new char[4096];
        final StringBuilder out = new StringBuilder();
        Reader in = null;
        try {
            in = new InputStreamReader(is, "UTF-8");
            int rsz;
            while ((rsz = in.read(buffer, 0, buffer.length)) >= 0) {
                out.append(buffer, 0, rsz);
            }
            return out;
        } finally {
            if (in != null) {
                in.close();
            }
        }
        
    }
    
    
    protected static String getPublishedDate(String assetInfo) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(assetInfo));
        String publishedDate = null;
        while (reader.hasNext()) {
            int next = reader.next();
            if (next == XMLStreamReader.START_ELEMENT) {
                if ("published".equals(reader.getLocalName())) {
                    publishedDate = reader.getElementText();
                    break;
                }
            }
        }     
        return publishedDate;
    }
    
    protected static String getUuid(String assetInfo) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(assetInfo));
        String uuid = null;
        while (reader.hasNext()) {
            int next = reader.next();
            if (next == XMLStreamReader.START_ELEMENT) {
                if ("uuid".equals(reader.getLocalName())) {
                    reader.next();
                    uuid = reader.getElementText();
                    break;
                }
            }
        }     
        return uuid;
    }
}

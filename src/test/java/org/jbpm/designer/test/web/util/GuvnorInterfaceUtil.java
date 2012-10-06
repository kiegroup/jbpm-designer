package org.jbpm.designer.test.web.util;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.*;
import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServletUtil;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;

public class GuvnorInterfaceUtil {

    private Abdera abdera = new Abdera();

    private URL guvnorUrl = null;

    // Copied from org.drools.guvnor.server.jaxrs.Translator
    public static final String NS = "";
    public static final QName UUID = new QName(NS, "uuid");
    public static final QName CATEGORIES = new QName(NS, "categories");
    public static final QName FORMAT = new QName(NS, "format");
    public static final QName STATE = new QName(NS, "state");
    public static final QName VERSION_NUMBER = new QName(NS, "versionNumber");
    public static final QName CHECKIN_COMMENT = new QName(NS, "checkinComment");
    public static final QName METADATA = new QName(NS, "metadata");
    public static final QName VALUE = new QName(NS, "value");
    public static final QName ARCHIVED = new QName(NS, "archived");

    private static final String SLUG_REQ_PROP = "Slug";
    private static final String ACCEPT_FORMAT_REQ_PROP = "Accept";
//    private static final String ACCEPT_CHARSET_REQ_PROP = "Accept-Charset";
    private static final String CONTENT_TYPE_REQ_PROP = "Content-Type";

    private final SimpleDateFormat sdf;
    
        
    private GuvnorInterfaceUtil() { 
        sdf = new SimpleDateFormat("HHmmss");
   }

    private GuvnorInterfaceUtil(URL url) {
        this();
        this.guvnorUrl = url;
    }

    public static GuvnorInterfaceUtil instance(URL url) {
        return new GuvnorInterfaceUtil(url);
    }

    public GuvnorInterfaceUtil createPackageViaAtom(String packageName) throws IOException, URISyntaxException {

        // create package entry
        Entry entry = abdera.newEntry();
        entry.setTitle(packageName);
        entry.setSummary(packageName + " created by " + this.getClass().getSimpleName());

        // Invoke Guvnor REST API
        AbderaClient client = createAuthorizedClient();
        ClientResponse resp = client.post(new URL(guvnorUrl, "rest/packages").toExternalForm(), entry);
        assertEquals(ResponseType.SUCCESS, resp.getType());

        return this;
    }

    private AbderaClient createAuthorizedClient() throws URISyntaxException {
        AbderaClient client = new AbderaClient(abdera);
        client.addCredentials(guvnorUrl.toExternalForm(), null, null, new org.apache.commons.httpclient.UsernamePasswordCredentials(
                "admin", "admin"));
        return client;
    }

    private String getGuvnorUrl(String urlEnd) {
        return guvnorUrl.toExternalForm() + "rest/packages/" + urlEnd;
    }

    public String createBpmn2AssetAndReturnAssetUuid(String packageName, String assetName, byte[] assetContentBytes) throws IOException {
        return createAssetAndReturnAssetUuid(packageName, assetName, "bpmn2", assetContentBytes);
    }
    
    public String createAssetAndReturnAssetUuid(String packageName, String assetName, String format, byte[] assetContentBytes) throws IOException {
        if( ! format.startsWith(".") ) { 
            format = "." + format;
        }
        
        // Create asset
        HttpURLConnection conn = internalCreateAsset(packageName, assetName + format, assetContentBytes);
        
        // connect
        conn.connect();
        assertEquals(200, conn.getResponseCode());

        // Get uuid element 
        InputStream is = (InputStream) conn.getContent();
        assertNotNull("Could not retrieve content from response.", is);

        Document<Entry> doc = abdera.getParser().parse(is);

        Element elem = findElem("uuid", doc.getRoot().getExtension(new QName("", "metadata")));
        String uuid = elem.getElements().get(0).getText();
        assertTrue("Empty uuid found.", uuid != null && !uuid.isEmpty());

        return uuid;
    }
   
    private Element findElem(String name, Element elem) {
        if (!name.equals(elem.getQName().getLocalPart())) {
            for (Element child : elem.getElements()) {
                Element result = findElem(name, child);
                if (result != null) {
                    return result;
                }
            }
        } else {
            return elem;
        }
        return null;
    }

    public GuvnorInterfaceUtil createJsonAsset(String packageName, String assetName, String objectName ) throws Exception {
        String jsonAssetContentString = "{ \"" + objectName + "\": \" \"" + sdf.format(new Date()) + "\" }"; 
        byte [] assetContentBytes = jsonAssetContentString.getBytes();
        
        HttpURLConnection conn = internalCreateAsset(packageName, assetName + ".json", assetContentBytes);
        
        conn.connect();
        assertEquals(200, conn.getResponseCode());
        
        return this;
    }
    
    public String createFormWidgetAsset(String assetName) throws IOException {
        // Create content
        String assetContentString = "<test name=\"" + assetName +"\" date=\"" + sdf.format(new Date()) + "\" />";

        // Create asset
        HttpURLConnection conn = internalCreateAsset("globalArea", assetName + ".fw", assetContentString.getBytes());
        
        // connect
        conn.connect();
        assertEquals(200, conn.getResponseCode());

        return assetContentString;
    }

    public GuvnorInterfaceUtil createBinaryAsset(String pkgName, String assetName) throws IOException { 
        // Get binary content
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[1000];
        int count = 0;
        InputStream is = this.getClass().getResourceAsStream("/guvnor-integration/test-image.gif");
        while((count = is.read(data,0,1000)) != -1) {
            out.write(data, 0, count);
        }
        out.close();

        // Create asset
        HttpURLConnection conn = internalCreateAsset(pkgName, assetName + ".gif", out.toByteArray());
        
        // connect and verify
        conn.connect();
        assertEquals(200, conn.getResponseCode());
    
        return this;
    }

    public GuvnorInterfaceUtil createAsset(String packageName, String assetNamePlusFormat, byte [] assetContentBytes ) throws IOException { 
        HttpURLConnection conn = internalCreateAsset(packageName, assetNamePlusFormat, assetContentBytes);
        
        // connect and verify
        conn.connect();
        assertEquals(200, conn.getResponseCode());
    
        return this;
    }
    
    private HttpURLConnection internalCreateAsset(String packageName, String assetNamePlusFormat, byte [] assetContentBytes ) throws IOException { 
        Map<String, String> reqProps = new HashMap<String, String>();
        reqProps.put(CONTENT_TYPE_REQ_PROP, MediaType.APPLICATION_OCTET_STREAM);
        reqProps.put(SLUG_REQ_PROP, assetNamePlusFormat);
        
        String url = getGuvnorUrl(packageName + "/assets" );
        HttpURLConnection conn = ConnectionMaker.create(url, "POST", reqProps, true);
        conn.getOutputStream().write(assetContentBytes);
        
        return conn;
    }
    
    public GuvnorInterfaceUtil updateAssetSource(String packageName, String assetName, String format, byte [] assetContentBytes ) throws IOException { 
        Map<String, String> reqProps = new HashMap<String, String>();
        if( format.startsWith(".") ) { 
           format = "." + format; 
        }
        reqProps.put(SLUG_REQ_PROP, assetName + format);
        
        String url = getGuvnorUrl(packageName + "/assets/" + assetName + "/source");
        HttpURLConnection conn = ConnectionMaker.create(url, "PUT", reqProps, true);
        conn.getOutputStream().write(assetContentBytes);
        
        // connect and verify
        conn.connect();
        assertTrue("Did not expect " + conn.getResponseCode(), conn.getResponseCode() < 300 && conn.getResponseCode() >= 200);
    
        return this;
    }
    
    public boolean checkIfAssetExists(String packageName, String assetName) throws Exception {
        String themesUrl = getGuvnorUrl(packageName + "/assets/" + assetName);

        HttpURLConnection conn = ConnectionMaker.create(themesUrl, "GET");
        conn.connect();
        
        int respCode = conn.getResponseCode();
        assertTrue( "Unexpected response code: " + respCode, respCode == 200 || respCode == 404);
        if( respCode == 200 ) { 
            return true;
        }
        else {
            return false;
        } 
    }

    public String getAssetInfo(String packageName, String assetName) throws Exception {
        // Create connection
        String url = getGuvnorUrl(packageName + "/assets/" + assetName);
        
        HttpURLConnection connection = ConnectionMaker.create(url, "GET", null, false, 5 * 1000);
        
        // connect
        connection.connect();
        assertEquals("Unexpected code for " + packageName + "/" + assetName, 
                200, connection.getResponseCode());

        // Read output
        BufferedReader sreader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        return stringBuilder.toString();
    }
    
    public Map<String, String> getAssetsAndUuidsFromPackage(String packageName) throws Exception { 
        // Create connection
        String url = getGuvnorUrl(packageName + "/assets/");
        HttpURLConnection connection = ConnectionMaker.create(url, "GET", null, false, 5 * 1000);
        
        // connect
        connection.connect();
        assertEquals("Unexpected code for " + packageName + "/" , 200, connection.getResponseCode());

        // Read output
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new InputStreamReader(connection.getInputStream()));
        String title = "";
        String readuuid = "";
        HashMap<String, String> info = new HashMap<String, String>();
        while (reader.hasNext()) {
            int next = reader.next();
            if (next == XMLStreamReader.START_ELEMENT) {
                if ("title".equals(reader.getLocalName())) {
                    title = reader.getElementText();
                }
                if ("uuid".equals(reader.getLocalName())) {
                    readuuid = reader.getElementText();
                }
            }
            if (next == XMLStreamReader.END_ELEMENT) {
                if ("asset".equals(reader.getLocalName())) {
                    if(title.length() > 0 && readuuid.length() > 0) { 
                        info.put(title, readuuid);
                    }
                }
            }
        }
        
        return info;
    }
        
    public String getAssetSource(String packageName, String assetName) throws Exception {
        // Create connection
        String url = getGuvnorUrl(packageName + "/assets/" + assetName + "/source");
        HashMap<String, String> requestProps = new HashMap<String, String>();
        
        String formats = MediaType.TEXT_HTML 
                + "," + MediaType.APPLICATION_XHTML_XML 
                + "," + MediaType.APPLICATION_XML 
                + "," + MediaType.APPLICATION_JSON 
                + "," + MediaType.APPLICATION_OCTET_STREAM 
                + "," + MediaType.TEXT_PLAIN 
                + "," + "text/json";
        requestProps.put(ACCEPT_FORMAT_REQ_PROP, formats);
                       
        HttpURLConnection connection = ConnectionMaker.create(url, "GET", requestProps, false, 5 * 1000);
        
        // connect
        connection.connect();
        assertEquals(200, connection.getResponseCode());

        // Read output
        BufferedReader sreader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        return stringBuilder.toString();
    }

    public GuvnorInterfaceUtil deleteAssetViaAbdera(String packageName, String assetName, boolean checkResponse) throws Exception { 
        AbderaClient client = createAuthorizedClient();
        ClientResponse resp = client.delete(new URL(guvnorUrl, packageName + "/assets/" + assetName).toExternalForm());
        if( checkResponse ) { 
            assertEquals(ResponseType.SUCCESS, resp.getType());
        } 
        
        return this;
    }

    public GuvnorInterfaceUtil deleteAsset(String packageName, String assetName, boolean checkResponse) throws Exception { 
        String urlString = getGuvnorUrl(packageName + "/assets/" + assetName);
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String("admin:admin".getBytes()));
        conn.setRequestMethod("DELETE");
        conn.connect();
        
        if( checkResponse ) { 
            assertTrue("Response code was not 20X: " + conn.getResponseCode(), conn.getResponseCode() < 210);
        }
        return this;
    }

    private static class ConnectionMaker {
        
        public static HttpURLConnection create(String urlString, String method) throws IOException {
            return create(urlString, method, null, null, null);
        }


        public static HttpURLConnection create(String urlString, String method, Map<String, String> extraReqProps, 
                boolean responseOutput) throws IOException {
            return create(urlString, method, extraReqProps, responseOutput, null);
        }

        public static HttpURLConnection create(String urlString, String method, Map<String, String> reqPropsMap,
                Boolean responseOutput, Integer readTimeout) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String("admin:admin".getBytes()));
            conn.setRequestMethod(method);
            conn.setRequestProperty(ACCEPT_FORMAT_REQ_PROP, MediaType.APPLICATION_ATOM_XML); // default, overwritten if exists in reqPropsMap
            conn.setRequestProperty("charset", "UTF-8");
            
            if( responseOutput != null && responseOutput ) { 
                conn.setDoOutput(true);
            }
            if( reqPropsMap != null ) { 
                for (String property : reqPropsMap.keySet()) {
                    conn.setRequestProperty(property, reqPropsMap.get(property));
                }
            }
            if (readTimeout != null) {
                conn.setReadTimeout(readTimeout);
            }

            return conn;
        }
    }
}

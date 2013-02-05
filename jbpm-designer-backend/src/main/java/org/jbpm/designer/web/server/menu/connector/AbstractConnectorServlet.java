package org.jbpm.designer.web.server.menu.connector;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.AssetNotFoundException;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServletUtil;
import org.jbpm.designer.web.server.menu.connector.commands.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public abstract class AbstractConnectorServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(AbstractConnectorServlet.class);

    private Map<String, Object> requestParams;
    private List<FileItemStream> listFiles;
    private List<ByteArrayOutputStream> listFileStreams;
    private boolean initialized = false;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void initializeDefaultRepo(IDiagramProfile profile, Repository repository, HttpServletRequest request) throws Exception {
        String sampleBpmn2 = getServletContext().getRealPath("/defaults/SampleProcess.bpmn2");
        createAssetIfNotExisting(repository, "/defaultPackage", "BPMN2-SampleProcess", "bpmn2", getBytesFromFile(new File(sampleBpmn2)));
        if(profile.getRepositoryGlobalDir() != null) {
            createDirectoryIfNotExist(repository, profile.getRepositoryGlobalDir());
        }
    }

    /**
     * Processing a new request from ElFinder client.
     * @param request
     * @param response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        parseRequest(request, response);
        IDiagramProfile profile = ServletUtil.getProfile(request, "jbpm", getServletContext());
        Repository repository = profile.getRepository();
        if(!initialized) {
            try {
                initializeDefaultRepo(profile, repository, request);
                initialized = true;
            } catch (Exception e) {
                logger.error("Unable to initialize repository: " + e.getMessage());
            }
        }
        JSONObject returnJson = new JSONObject();
        try {
            Iterator<String> keys = requestParams.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
            }

            String cmd = (String) requestParams.get("cmd");
            if(cmd != null && cmd.equals("open")) {
                OpenCommand command = new OpenCommand();
                command.init(request, response, profile, repository, requestParams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("mkdir")) {
                MakeDirCommand command = new MakeDirCommand();
                command.init(request, response, profile, repository, requestParams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("mkfile")) {
                MakeFileCommand command = new MakeFileCommand();
                command.init(request, response, profile, repository, requestParams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("rm")) {
                RemoveAssetCommand command = new RemoveAssetCommand();
                command.init(request, response, profile, repository, requestParams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("rename")) {
                RenameCommand command = new RenameCommand();
                command.init(request, response, profile, repository, requestParams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("paste")) {
                PasteCommand command = new PasteCommand();
                command.init(request, response, profile, repository, requestParams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("upload")) {
                UploadCommand command = new UploadCommand();
                command.init(request, response, profile, repository, requestParams, listFiles, listFileStreams);
                output(response, false, command.execute());
            } else if(cmd != null && cmd.equals("getsvg")) {
                try {
                    Asset asset = profile.getRepository().loadAssetFromPath((String) requestParams.get("current"));
                    if(asset != null && asset.getAssetContent() != null) {
                        outputPlain(response, false, (String) asset.getAssetContent(), "image/svg+xml");
                    } else {
                        outputPlain(response, true, "<p><b>Process image not available.</p><p>You can generate the process image in the process editor.</b></p>", null);
                    }
                } catch (AssetNotFoundException e) {
                    logger.warn("Error loading process image: " + e.getMessage());
                    outputPlain(response, true, "<p><b>Could not find process image.</p><p>You can generate the process image in the process editor.</b></p>", null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            putResponse(returnJson, "error", e.getMessage());

            // output the error
            try {
                output(response, false, returnJson);
            } catch (Exception ee) {
                logger.error("", ee);
            }
        }
    }

    protected static void output(HttpServletResponse response, boolean isResponseTextHtml, JSONObject json) {
        if (isResponseTextHtml) {
            response.setContentType("text/html; charset=UTF-8");
        } else {
            response.setContentType("application/json; charset=UTF-8");
        }
        try {
            json.write(response.getWriter());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void outputPlain(HttpServletResponse response, boolean isResponseTextHtml, String txt, String ctype) {
        if (isResponseTextHtml) {
            response.setContentType("text/html; charset=UTF-8");
        } else {
            if(ctype != null) {
                response.setContentType(ctype + "; charset=UTF-8");
            } else {
                response.setContentType("text/plain; charset=UTF-8");
            }
        }

        try {
            PrintWriter out = response.getWriter();
            out.print(txt);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Parse request parameters and files.
     * @param request
     * @param response
     */
    protected void parseRequest(HttpServletRequest request, HttpServletResponse response) {
        requestParams = new HashMap<String, Object>();
        listFiles = new ArrayList<FileItemStream>();
        listFileStreams = new ArrayList<ByteArrayOutputStream>();

        // Parse the request
        if (ServletFileUpload.isMultipartContent(request)) {
            // multipart request
            try {
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator iter = upload.getItemIterator(request);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String name = item.getFieldName();
                    InputStream stream = item.openStream();
                    if (item.isFormField()) {
                        requestParams.put(name, Streams.asString(stream));
                    } else {
                        String fileName = item.getName();
                        if (fileName != null && !"".equals(fileName.trim())) {
                            listFiles.add(item);

                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            IOUtils.copy(stream, os);
                            listFileStreams.add(os);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error parsing multipart content", e);
            }
        } else {
            // not a multipart
            for (Object mapKey : request.getParameterMap().keySet()) {
                String mapKeyString = (String) mapKey;

                if (mapKeyString.endsWith("[]")) {
                    // multiple values
                    String values[] = request.getParameterValues(mapKeyString);
                    List<String> listeValues = new ArrayList<String>();
                    for (String value : values) {
                        listeValues.add(value);
                    }
                    requestParams.put(mapKeyString, listeValues);
                } else {
                    // single value
                    String value = request.getParameter(mapKeyString);
                    requestParams.put(mapKeyString, value);
                }
            }
        }
    }

    /**
     * Append data to JSON response.
     * @param param
     * @param value
     */
    protected void putResponse(JSONObject json, String param, Object value) {
        try {
            json.put(param, value);
        } catch (JSONException e) {
            logger.error("json write error", e);
        }
    }

    private void createDirectoryIfNotExist(Repository repository, String location) throws Exception {
        if(!repository.directoryExists(location)) {
            repository.createDirectory(location);
        }
    }

    private String createAssetIfNotExisting(Repository repository, String location, String name, String type, byte[] content) {
        try {
            boolean assetExists = repository.assetExists(location + "/" + name + "." + type);
            if (!assetExists) {
                // create theme asset
                AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
                assetBuilder.content(content)
                        .location(location)
                        .name(name)
                        .type(type)
                        .version("1.0");

                Asset<byte[]> customEditorsAsset = assetBuilder.getAsset();

                return repository.createAsset(customEditorsAsset);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = null;
        is = new FileInputStream(file);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            is.close();
            return null; // File is too large
        }

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }
}

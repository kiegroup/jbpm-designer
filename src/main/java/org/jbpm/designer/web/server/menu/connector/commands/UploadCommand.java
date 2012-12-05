package org.jbpm.designer.web.server.menu.connector.commands;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class UploadCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(RenameCommand.class);
    private HttpServletRequest request;
    private HttpServletResponse response;
    private IDiagramProfile profile;
    private Repository repository;
    private Map<String, Object> requestParams;
    List<FileItemStream> listFiles;
    List<ByteArrayOutputStream> listFileStreams;

    public void init(HttpServletRequest request, HttpServletResponse response, IDiagramProfile profile, Repository repository, Map<String, Object> requestParams, List<FileItemStream> listFiles, List<ByteArrayOutputStream> listFileStreams) {
        this.request = request;
        this.response = response;
        this.profile = profile;
        this.repository = repository;
        this.requestParams = requestParams;
        this.listFiles = listFiles;
        this.listFileStreams = listFileStreams;
    }

    public JSONObject execute() throws Exception {
        String tree = (String) requestParams.get("tree");
        String current = (String) requestParams.get("current");

        return uploadFiles(profile, current, listFiles, listFileStreams, Boolean.parseBoolean(tree));
    }
}

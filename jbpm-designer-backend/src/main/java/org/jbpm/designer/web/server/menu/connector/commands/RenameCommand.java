package org.jbpm.designer.web.server.menu.connector.commands;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetNotFoundException;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class RenameCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(RenameCommand.class);
    private HttpServletRequest request;
    private HttpServletResponse response;
    private IDiagramProfile profile;
    private Repository repository;
    private Map<String, Object> requestParams;

    public void init(HttpServletRequest request, HttpServletResponse response, IDiagramProfile profile, Repository repository, Map<String, Object> requestParams) {
        this.request = request;
        this.response = response;
        this.profile = profile;
        this.repository = repository;
        this.requestParams = requestParams;
    }

    public JSONObject execute() throws Exception {
        String name = (String) requestParams.get("name");
        String target = (String) requestParams.get("target");
        String tree = (String) requestParams.get("tree");
        String current = (String) requestParams.get("current");

        return moveDirectoryOrAsset(profile, name, target, current, Boolean.parseBoolean(tree));
    }
}

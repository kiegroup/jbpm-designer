package org.jbpm.designer.web.server.menu.connector.commands;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class PasteCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(PasteCommand.class);
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
        String current = (String) requestParams.get("current");
        List<String> targets = (List<String>) requestParams.get("targets[]");
        String cut = (String) requestParams.get("cut");
        String dst = (String) requestParams.get("dst");
        String src = (String) requestParams.get("src");
        String tree = (String) requestParams.get("tree");

        return pasteDirectoriesOrAssets(profile, current, targets, cut, dst, src, Boolean.parseBoolean(tree));
    }

}

package org.jbpm.designer.web.server.menu.connector.commands;

import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class MakeFileCommand extends AbstractCommand {
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
        String name = (String) requestParams.get("name");
        String tree = (String) requestParams.get("tree");

        return makeFile(profile, current, name, Boolean.parseBoolean(tree));
    }
}

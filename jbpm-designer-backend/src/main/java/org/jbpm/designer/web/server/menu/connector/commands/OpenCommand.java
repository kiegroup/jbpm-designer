/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.web.server.menu.connector.commands;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;

public class OpenCommand extends AbstractCommand {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private IDiagramProfile profile;
    private Repository repository;
    private Map<String, Object> requestParams;

    public void init(HttpServletRequest request,
                     HttpServletResponse response,
                     IDiagramProfile profile,
                     Repository repository,
                     Map<String, Object> requestParams) {
        this.request = request;
        this.response = response;
        this.profile = profile;
        this.repository = repository;
        this.requestParams = requestParams;
    }

    public JSONObject execute() throws Exception {
        String current = (String) requestParams.get("current");
        String target = (String) requestParams.get("target");
        String tree = (String) requestParams.get("tree");

        return listContent(profile,
                           target,
                           current,
                           Boolean.parseBoolean(tree));
    }
}

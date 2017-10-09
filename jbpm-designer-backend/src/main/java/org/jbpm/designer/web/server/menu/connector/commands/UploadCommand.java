/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.web.server.menu.connector.commands;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemStream;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadCommand extends AbstractCommand {

    private static Logger logger = LoggerFactory.getLogger(RenameCommand.class);
    private HttpServletRequest request;
    private HttpServletResponse response;
    private IDiagramProfile profile;
    private Repository repository;
    private Map<String, Object> requestParams;
    List<FileItemStream> listFiles;
    List<ByteArrayOutputStream> listFileStreams;

    public void init(HttpServletRequest request,
                     HttpServletResponse response,
                     IDiagramProfile profile,
                     Repository repository,
                     Map<String, Object> requestParams,
                     List<FileItemStream> listFiles,
                     List<ByteArrayOutputStream> listFileStreams) {
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

        return uploadFiles(profile,
                           current,
                           listFiles,
                           listFileStreams,
                           Boolean.parseBoolean(tree));
    }
}

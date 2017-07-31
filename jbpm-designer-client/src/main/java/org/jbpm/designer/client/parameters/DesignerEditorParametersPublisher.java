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

package org.jbpm.designer.client.parameters;

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.rpc.SessionInfo;

@Dependent
public class DesignerEditorParametersPublisher {

    public static final String PROCESS_SOURCE_KEY = "processsource";
    public static final String INSTANCE_VIEWMODE_KEY = "instanceviewmode";
    public static final String ACTIVE_NODES_KEY = "activenodes";
    public static final String COMPLETED_NODES_KEY = "completednodes";

    @Inject
    private DesignerEditorParametersPublisherView view;

    @Inject
    SessionInfo sessionInfo;

    public Map<String, String> publish(Map<String, String> editorParameters) {
        publishProcessSources(editorParameters);

        publishActiveNodes(editorParameters);

        publishCompletedNodes(editorParameters);

        putTimeStampToParameters(editorParameters);

        putSessionIdToParameters(editorParameters);

        return editorParameters;
    }

    protected Map<String, String> publishProcessSources(Map<String, String> editorParameters) {
        if (editorParameters.containsKey(PROCESS_SOURCE_KEY)) {
            String processSources = editorParameters.get(PROCESS_SOURCE_KEY);
            if (processSources != null && processSources.length() > 0) {
                view.publishProcessSourcesInfo(editorParameters.get(PROCESS_SOURCE_KEY));
                editorParameters.put(INSTANCE_VIEWMODE_KEY,
                                     "true");
            } else {
                editorParameters.put(INSTANCE_VIEWMODE_KEY,
                                     "false");
            }
            editorParameters.remove(PROCESS_SOURCE_KEY);
        }

        return editorParameters;
    }

    protected Map<String, String> publishActiveNodes(Map<String, String> editorParameters) {

        if (editorParameters.containsKey(ACTIVE_NODES_KEY)) {
            String activeNodes = editorParameters.get(ACTIVE_NODES_KEY);
            if (activeNodes != null && activeNodes.length() > 0) {
                view.publishActiveNodesInfo(editorParameters.get(ACTIVE_NODES_KEY));
            }
            editorParameters.remove(ACTIVE_NODES_KEY);
        }

        return editorParameters;
    }

    protected Map<String, String> publishCompletedNodes(Map<String, String> editorParameters) {
        if (editorParameters.containsKey(COMPLETED_NODES_KEY)) {
            String activeNodes = editorParameters.get(COMPLETED_NODES_KEY);
            if (activeNodes != null && activeNodes.length() > 0) {
                view.publishCompletedNodesInfo(editorParameters.get(COMPLETED_NODES_KEY));
            }
            editorParameters.remove(COMPLETED_NODES_KEY);
        }

        return editorParameters;
    }

    protected void putTimeStampToParameters(Map<String, String> editorParameters) {
        editorParameters.put("ts",
                             Long.toString(System.currentTimeMillis()));
    }

    protected void putSessionIdToParameters(Map<String, String> editorParameters) {
        editorParameters.put("sessionId",
                             sessionInfo.getId());
    }
}

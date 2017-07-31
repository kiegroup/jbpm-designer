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

package org.jbpm.designer.service;

import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;

/**
 * Designer service for loading\saving BPMN2 processes
 */
@Remote
public interface DesignerAssetService {

    public DesignerContent loadContent(Path path);

    void updateMetadata(final Path resource,
                        final Metadata metadata);

    public Map<String, String> getEditorParameters(final Path path,
                                                   final String editorID,
                                                   String hostInfo,
                                                   PlaceRequest place);

    public String getEditorID();

    public Path createProcess(final Path context,
                              final String fileName);

    public Path createCaseDefinition(final Path context,
                                     final String fileName,
                                     String caseIdPrefix);

    public boolean isCaseProject(Path rootProjectPath);
}

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

package org.jbpm.designer.helper;

import java.util.Collection;
import javax.servlet.ServletContext;

import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;

public class TestIDiagramProfile implements IDiagramProfile {

    private Repository repository;

    public TestIDiagramProfile(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getStencilSet() {
        return null;
    }

    @Override
    public Collection<String> getStencilSetExtensions() {
        return null;
    }

    @Override
    public String getSerializedModelExtension() {
        return null;
    }

    @Override
    public String getStencilSetURL() {
        return null;
    }

    @Override
    public String getStencilSetNamespaceURL() {
        return null;
    }

    @Override
    public String getStencilSetExtensionURL() {
        return null;
    }

    @Override
    public Collection<String> getPlugins() {
        return null;
    }

    @Override
    public IDiagramMarshaller createMarshaller() {
        return null;
    }

    @Override
    public IDiagramUnmarshaller createUnmarshaller() {
        return null;
    }

    public String getRepositoryId() {
        return null;
    }

    public String getRepositoryName() {
        return null;
    }

    public String getRepositoryRoot() {
        return null;
    }

    public String getRepositoryHost() {
        return null;
    }

    public String getRepositoryProtocol() {
        return null;
    }

    public String getRepositorySubdomain() {
        return null;
    }

    public String getRepositoryUsr() {
        return null;
    }

    public String getRepositoryPwd() {
        return null;
    }

    @Override
    public String getRepositoryGlobalDir() {
        return "/global";
    }

    @Override
    public String getRepositoryGlobalDir(String uuid) {
        return "/global";
    }

    @Override
    public String getLocalHistoryEnabled() {
        return null;
    }

    @Override
    public String getLocalHistoryTimeout() {
        return null;
    }

    @Override
    public Repository getRepository() {
        return this.repository;
    }

    @Override
    public String getStoreSVGonSaveOption() {
        return "true";
    }

    @Override
    public String getBpsimDisplay() {
        return "true";
    }

    @Override
    public String getFormsType() {
        return "";
    }

    @Override
    public void init(ServletContext context) {

    }
}
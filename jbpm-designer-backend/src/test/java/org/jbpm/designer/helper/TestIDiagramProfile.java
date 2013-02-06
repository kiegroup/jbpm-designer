package org.jbpm.designer.helper;

import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;

import javax.servlet.ServletContext;
import java.util.Collection;

public class TestIDiagramProfile implements IDiagramProfile {

    private Repository repository;

    public TestIDiagramProfile(Repository repository) {
        this.repository = repository;
    }

    public String getName() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public String getStencilSet() {
        return null;
    }

    public Collection<String> getStencilSetExtensions() {
        return null;
    }

    public String getSerializedModelExtension() {
        return null;
    }

    public String getStencilSetURL() {
        return null;
    }

    public String getStencilSetNamespaceURL() {
        return null;
    }

    public String getStencilSetExtensionURL() {
        return null;
    }

    public Collection<String> getPlugins() {
        return null;
    }

    public IDiagramMarshaller createMarshaller() {
        return null;
    }

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

    public String getRepositoryGlobalDir() {
        return "/global";
    }

    public String getLocalHistoryEnabled() {
        return null;
    }

    public String getLocalHistoryTimeout() {
        return null;
    }

    public Repository getRepository() {
        return this.repository;
    }

    @Override
    public void init(ServletContext context) {

    }
}
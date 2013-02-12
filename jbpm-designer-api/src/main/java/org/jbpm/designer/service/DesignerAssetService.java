package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.mvp.PlaceRequest;


/**
 * Designer service for loading\saving BPMN2 processes
 */
@Remote
public interface DesignerAssetService {

    public String loadEditorBody(final Path path, final String editorID, String hostInfo, PlaceRequest place);
    public String getEditorID();
}

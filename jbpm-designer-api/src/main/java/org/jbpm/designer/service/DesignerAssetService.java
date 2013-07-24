package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;

import java.util.Map;

/**
 * Designer service for loading\saving BPMN2 processes
 */
@Remote
public interface DesignerAssetService {

    public Map<String, String> getEditorParameters( final Path path,
                                  final String editorID,
                                  String hostInfo,
                                  PlaceRequest place );

    public String getEditorID();

    public Path createProcess( final Path context,
                               final String fileName );
}

package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

/**
 * Designer service for loading\saving BPMN2 processes
 */
@Remote
public interface DesignerAssetService {

    public String loadJsonModel( final Path path );

    public void saveJsonModel( final Path path,
                               final String jsonModel );

}

package org.jbpm.designer.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class Bpmn2TypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "bpmn2";
    }

    @Override
    public String getDescription() {
        return "BPMN2 file";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "bpmn2";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accept( final Path path ) {
        return ( path.getFileName().endsWith( "." + getSuffix() ) || path.getFileName().endsWith( ".bpmn" ) );
    }

    public String getSimpleWildcardPattern() {
        return ".+bpmn[2]?$";
    }
}
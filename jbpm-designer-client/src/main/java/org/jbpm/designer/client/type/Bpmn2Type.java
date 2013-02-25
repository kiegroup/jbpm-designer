package org.jbpm.designer.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class Bpmn2Type
        extends Bpmn2TypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
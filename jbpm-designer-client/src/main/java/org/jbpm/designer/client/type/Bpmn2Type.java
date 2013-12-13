package org.jbpm.designer.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.client.resources.DesignerEditorResources;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;
import com.google.gwt.user.client.ui.Image;

@ApplicationScoped
public class Bpmn2Type
        extends Bpmn2TypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image(DesignerEditorResources.INSTANCE.images().typeForm());

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }
}
package org.jbpm.designer.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
@WorkbenchPerspective(identifier = "home", isDefault = true)
public class HomePerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName( "Home" );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "FileExplorer" ) ) );

        p.getRoot().appendChild( Position.WEST, west );

        return p;
    }
}

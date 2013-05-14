package org.jbpm.designer.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.widgets.common.client.handlers.NewResourcePresenter;
import org.kie.workbench.widgets.common.client.handlers.NewResourcesMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
@WorkbenchPerspective(identifier = "home", isDefault = true)
public class HomePerspective {

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu      newResourcesMenu;
    @Inject
    private PlaceManager          placeManager;
    private PerspectiveDefinition perspective;
    private Menus                 menus;

    @PostConstruct
    public void init() {
        buildPerspective();
        buildMenuBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    public PerspectiveDefinition buildPerspective() {
        perspective = new PerspectiveDefinitionImpl();
        perspective.setName( "Home" );

        final PanelDefinition west = new PanelDefinitionImpl();
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );

        perspective.getRoot().appendChild( Position.WEST, west );

        return perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu("Projects")
                    .respondsWith( new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo( "org.kie.guvnor.explorer" );
                        }
                    } )
                .endMenu()
                .newTopLevelMenu( "New" )
                    .withItems( newResourcesMenu.getMenuItems() )
                .endMenu().build();
    }
}

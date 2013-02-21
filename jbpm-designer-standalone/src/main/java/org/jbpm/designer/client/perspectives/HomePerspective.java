package org.jbpm.designer.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcePresenter;
import org.kie.guvnor.commons.ui.client.handlers.NewResourcesMenu;
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
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
@WorkbenchPerspective(identifier = "home", isDefault = true)
public class HomePerspective {

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;
    @Inject
    private PlaceManager placeManager;
    private PerspectiveDefinition perspective;
    private MenuBar menuBar;

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
        west.setWidth(300);
        west.setMinWidth(200);
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("org.kie.guvnor.explorer")));

        perspective.getRoot().appendChild( Position.WEST, west );

        return perspective;
    }


    @WorkbenchMenu
    public MenuBar getMenuBar() {
        return this.menuBar;
    }

    private void buildMenuBar() {
        this.menuBar = new DefaultMenuBar();
        menuBar.addItem(new DefaultMenuItemCommand("Projects",
                new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo("org.kie.guvnor.explorer");
                    }
                }));

        this.menuBar.addItem(newResourcesMenu);
    }
}

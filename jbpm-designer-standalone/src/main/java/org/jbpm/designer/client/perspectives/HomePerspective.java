package org.jbpm.designer.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.editors.repository.clone.CloneRepositoryForm;
import org.uberfire.client.editors.repository.create.CreateRepositoryForm;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.IconType;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBarItem;

import static org.uberfire.workbench.model.toolbar.IconType.DOWNLOAD_ALT;
import static org.uberfire.workbench.model.toolbar.IconType.FOLDER_CLOSE_ALT;

@ApplicationScoped
@WorkbenchPerspective(identifier = "home", isDefault = true)
public class HomePerspective {

    private static String[] PERMISSIONS_ADMIN = new String[]{ "ADMIN" };

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    private PerspectiveDefinition perspective;
    private Menus menus;
    private ToolBar toolBar;

    private Command newRepoCommand   = null;
    private Command cloneRepoCommand = null;

	private Command resetCommand;

	private PanelDefinitionImpl west;

	private PanelDefinition root;

    @PostConstruct
    public void init() {
        buildCommands();
        buildPerspective();
        buildMenuBar();
        buildToolBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    public PerspectiveDefinition buildPerspective( ) {
        perspective = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        perspective.setName( "Home" );

        root = this.perspective.getRoot();
        
        root.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "RepositoriesEditor" ) ) );

        initWestPanel();

        perspective.setTransient(true);
        return perspective;
    }

	protected void initWestPanel() {
		west = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        west.setWidth( 300 );
        west.setMinWidth( 200 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );
        root.appendChild( Position.WEST, west );
	}

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    private void buildCommands() {
        this.cloneRepoCommand = new Command() {

            @Override
            public void execute() {
                final CloneRepositoryForm cloneRepositoryWizard = iocManager.lookupBean( CloneRepositoryForm.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                cloneRepositoryWizard.addCloseHandler( new CloseHandler<PopupPanel>() {

                    @Override
                    public void onClose( CloseEvent<PopupPanel> event ) {
                        iocManager.destroyBean( cloneRepositoryWizard );
                    }

                } );
                cloneRepositoryWizard.show();
            }

        };

        this.newRepoCommand = new Command() {
            @Override
            public void execute() {
                final CreateRepositoryForm newRepositoryWizard = iocManager.lookupBean( CreateRepositoryForm.class ).getInstance();
                //When pop-up is closed destroy bean to avoid memory leak
                newRepositoryWizard.addCloseHandler( new CloseHandler<CreateRepositoryForm>() {
                    @Override
                    public void onClose( CloseEvent<CreateRepositoryForm> event ) {
                        iocManager.destroyBean( newRepositoryWizard );
                    }
                } );
                newRepositoryWizard.show();
            }
        };
        
        this.resetCommand = new Command() {
        	@Override
            public void execute() {
        		placeManager.goTo("RepositoriesEditor", root);
        		if (west == null || root.getChild(Position.WEST) == null) {
        			initWestPanel();
        		}
        		placeManager.goTo("org.kie.guvnor.explorer", west);
        		
        	}
        };
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
//                .newTopLevelMenu( "Projects" )
//                .respondsWith( new Command() {
//                    @Override
//                    public void execute() {
//                        placeManager.goTo( "org.kie.guvnor.explorer" );
//                    }
//                } )
//                .endMenu()
                .newTopLevelMenu( "New" )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu( "Repositories" )
                .menus()
                .menu( "Clone Repo" )
                .withRoles(PERMISSIONS_ADMIN)
                .respondsWith(cloneRepoCommand)
                .endMenu()
                .menu("New Repo")
                .withRoles(PERMISSIONS_ADMIN)
                .respondsWith(newRepoCommand)
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( "View" )
                .menus()
                .menu("Reset")
                .respondsWith(resetCommand)
                .endMenu()
                .endMenus()
                .endMenu().build();
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "file.explorer" );
        final DefaultToolBarItem i1 = new DefaultToolBarItem( FOLDER_CLOSE_ALT,
                "New Repository",
                newRepoCommand );
        final DefaultToolBarItem i2 = new DefaultToolBarItem( DOWNLOAD_ALT,
                "Clone Repository",
                cloneRepoCommand );
        i1.setRoles( PERMISSIONS_ADMIN );
        i2.setRoles( PERMISSIONS_ADMIN );
        toolBar.addItem( i1 );
        toolBar.addItem( i2 );
    }
    
}

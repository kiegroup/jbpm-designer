package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.commons.services.cdi.Startup;
import org.kie.commons.services.cdi.StartupType;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.security.server.cdi.SecurityFactory;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class AppSetup {

    private static final String JBPM_REPO_PLAYGROUND = "jbpm-playground";
    private static final String GUVNOR_REPO_PLAYGROUND = "uf-playground";
    // default repository section - start
    private static final String JBPM_URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private static final String GUVNOR_URL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    private final String userName = "guvnorngtestuser1";
    private final String password = "test1234";

    private static final String GLOBAL_SETTINGS = "settings";

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void onStartup() {
        try {
            SecurityFactory.setAuthzManager( new RuntimeAuthorizationManager() );

            Repository jbpmRepo = repositoryService.getRepository( JBPM_REPO_PLAYGROUND );
            if ( jbpmRepo == null ) {
                jbpmRepo = repositoryService.createRepository( "git",
                                                               JBPM_REPO_PLAYGROUND,
                                                               new HashMap<String, Object>() {{
                                                                   put( "origin", JBPM_URL );
                                                                   put( "username", userName );
                                                                   put( "crypt:password", password );
                                                               }} );
            }

            // TODO in case repo is not defined in system repository so we add default
            Repository guvnorRepo = repositoryService.getRepository( GUVNOR_REPO_PLAYGROUND );
            if ( guvnorRepo == null ) {
                guvnorRepo = repositoryService.createRepository( "git",
                                                                 GUVNOR_REPO_PLAYGROUND,
                                                                 new HashMap<String, Object>() {{
                                                                     put( "origin", GUVNOR_URL );
                                                                     put( "username", userName );
                                                                     put( "crypt:password", password );
                                                                 }} );
            }

            // TODO in case groups are not defined
            Collection<OrganizationalUnit> groups = organizationalUnitService.getOrganizationalUnits();
            if ( groups == null || groups.isEmpty() ) {
                final List<Repository> repositories = new ArrayList<Repository>();
                repositories.add( jbpmRepo );
                repositories.add( guvnorRepo );

                organizationalUnitService.createOrganizationalUnit( "demo",
                                                                     "demo@jbpm.org",
                                                                    repositories );
            }

            //Define mandatory properties
            List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration( ConfigType.GLOBAL );
            boolean globalSettingsDefined = false;
            for ( ConfigGroup globalConfigGroup : globalConfigGroups ) {
                if ( GLOBAL_SETTINGS.equals( globalConfigGroup.getName() ) ) {
                    globalSettingsDefined = true;
                    break;
                }
            }
            if ( !globalSettingsDefined ) {
                configurationService.addConfiguration( getGlobalConfiguration() );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Error when starting designer " + e.getMessage(), e );
        }
    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup( ConfigType.GLOBAL,
                                                                       GLOBAL_SETTINGS,
                                                                       "" );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.dateformat",
                                                                 "dd-MMM-yyyy" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.datetimeformat",
                                                                 "dd-MMM-yyyy hh:mm:ss" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultlanguage",
                                                                 "en" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "drools.defaultcountry",
                                                                 "US" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "build.enable-incremental",
                                                                 "true" ) );
        group.addConfigItem( configurationFactory.newConfigItem( "rule-modeller-onlyShowDSLStatements",
                                                                 "false" ) );
        return group;
    }
}


/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfiguration;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class AppSetup {

    private static final String JBPM_REPO_PLAYGROUND = "jbpm-playground";
    //    private static final String GUVNOR_REPO_PLAYGROUND = "uf-playground";
    // default repository section - start
    private static final String JBPM_URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
//    private static final String GUVNOR_URL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    private final static String userName = "guvnorngtestuser1";
    private final static String password = "test1234";

    private static final String GLOBAL_SETTINGS = "settings";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

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
            Repository jbpmRepo = repositoryService.getRepository( JBPM_REPO_PLAYGROUND );
            if ( jbpmRepo == null ) {

                final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
                configurations.setOrigin( JBPM_URL );
                configurations.setUserName( userName );
                configurations.setPassword( password );

                jbpmRepo = repositoryService.createRepository( "git",
                                                               JBPM_REPO_PLAYGROUND,
                                                               configurations );
            }

            // TODO in case repo is not defined in system repository so we add default
//            Repository guvnorRepo = repositoryService.getRepository( GUVNOR_REPO_PLAYGROUND );
//            if ( guvnorRepo == null ) {
//                guvnorRepo = repositoryService.createRepository( "git",
//                                                                 GUVNOR_REPO_PLAYGROUND,
//                                                                 new HashMap<String, Object>() {{
//                                                                     put( "origin", GUVNOR_URL );
//                                                                     put( "username", userName );
//                                                                     put( "crypt:password", password );
//                                                                 }} );
//            }

            // TODO in case groups are not defined
            Collection<OrganizationalUnit> groups = organizationalUnitService.getOrganizationalUnits();
            if ( groups == null || groups.isEmpty() ) {
                final List<Repository> repositories = new ArrayList<Repository>();
                repositories.add( jbpmRepo );
//                repositories.add( guvnorRepo );

                organizationalUnitService.createOrganizationalUnit( "demo",
                                                                    "demo@jbpm.org",
                                                                    null,
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


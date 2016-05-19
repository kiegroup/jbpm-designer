/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.security.KieWorkbenchSecurityService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class StandaloneEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private KieWorkbenchSecurityService kieSecurityService;
    private CallerMock<KieWorkbenchSecurityService> kieSecurityServiceCallerMock;

    @Mock
    private PlaceManagerActivityService pmas;
    private CallerMock<PlaceManagerActivityService> pmasCallerMock;

    @Mock
    private KieWorkbenchACL kieACL;

    @Mock
    private ActivityBeansCache activityBeansCache;


    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    private StandaloneEntryPoint standaloneEntryPoint;

    @Before
    public void setup() {
        appConfigServiceCallerMock = new CallerMock<>( appConfigService );
        kieSecurityServiceCallerMock = new CallerMock<>( kieSecurityService );
        pmasCallerMock = new CallerMock<>( pmas );

        standaloneEntryPoint = spy( new StandaloneEntryPoint( appConfigServiceCallerMock,
                                                              kieSecurityServiceCallerMock,
                                                              pmasCallerMock,
                                                              kieACL,
                                                              activityBeansCache,
                                                              menusHelper ) );
    }

    @Test
    public void setupMenuTest() {
        standaloneEntryPoint.setupMenu();

        verify( menusHelper ).addLogoutMenuItem();
    }

}

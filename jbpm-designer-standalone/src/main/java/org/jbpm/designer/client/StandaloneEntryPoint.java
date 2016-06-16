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

package org.jbpm.designer.client;

import javax.inject.Inject;

import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jbpm.designer.client.resources.StandaloneResources;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.ActivityBeansCache;

@EntryPoint
public class StandaloneEntryPoint extends DefaultWorkbenchEntryPoint {

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Inject
    public StandaloneEntryPoint( final Caller<AppConfigService> appConfigService,
                                 final Caller<PlaceManagerActivityService> pmas,
                                 final ActivityBeansCache activityBeansCache,
                                 final DefaultWorkbenchFeaturesMenusHelper menusHelper ) {
        super( appConfigService, pmas, activityBeansCache );
        this.menusHelper = menusHelper;

        StandaloneResources.INSTANCE.CSS().ensureInjected();
    }

    @Override
    protected void setupMenu() {
        menusHelper.addLogoutMenuItem();
    }
}

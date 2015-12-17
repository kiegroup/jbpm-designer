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

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Window;

/**
 * Bootstraps the core Designer JS Files.
 */
@ApplicationScoped
public class Bootstrap {
    private boolean initialized = false;
    private static final String path = "/org.jbpm.designer.jBPMDesigner/";

    public void boostrapDesigner(final String bodyString) {
        if ( initialized ) {
            return;
        }

        // not needed .. can remove

        this.initialized = true;
    }
}

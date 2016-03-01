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

package org.jbpm.designer.backend.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Constants {

    private static ResourceBundle messages = ResourceBundle.getBundle("org.jbpm.designer.backend.i18n.Constants");

    public String getMessage( String key ) {
        return key != null ? messages.getString( key ) : null;
    }

    public String getMessage( String key, Object... params ) {
        final String value = getMessage( key );
        if ( value != null ) {
            return MessageFormat.format( value, params );
        }
        return null;
    }

    public String MaxExecutionTime() {
        return getMessage("MaxExecutionTime");
    }

    public String MinExecutionTime() {
        return getMessage("MinExecutionTime");
    }

    public String AvgExecutionTime() {
        return getMessage("AvgExecutionTime");
    }

    public String Max() {
        return getMessage("Max");
    }

    public String Min() {
        return getMessage("Min");
    }

    public String Average() {
        return getMessage("Average");
    }

    public String ActivityInstances() {
        return getMessage("ActivityInstances");
    }

    public String ProcessAverages() {
        return getMessage("ProcessAverages");
    }
}

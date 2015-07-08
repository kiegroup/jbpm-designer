/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.designer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;


public interface DesignerEditorConstants extends
        Messages {
    DesignerEditorConstants INSTANCE = GWT.create(DesignerEditorConstants.class);

    String Add();

    String businessProcess();

    String businessProcessResourceTypeDescription();

    String Cancel();

    String Constant();

    String Custom();

    String Data_Input_and_Assignment();

    String Data_Inputs_and_Assignments();

    String Data_Output_and_Assignment();

    String Data_Outputs_and_Assignments();

    String Data_IO();

    String Edit();

    String Enter_constant();

    String Enter_type();

    String Only_single_entry_allowed();

    String Save();

    String Source();

    String Target();
}

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

package org.jbpm.designer.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.Header;

import static java.lang.Integer.*;

@ApplicationScoped
public class ComplementNavAreaPresenter implements Header {

    public interface View extends IsWidget {

    }

    @Inject
    public View view;

    @Override
    public String getId() {
        return "ComplementNavArea";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}

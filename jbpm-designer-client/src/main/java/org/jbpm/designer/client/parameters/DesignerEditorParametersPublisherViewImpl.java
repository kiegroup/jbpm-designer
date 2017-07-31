/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.parameters;

import javax.enterprise.context.Dependent;

@Dependent
public class DesignerEditorParametersPublisherViewImpl implements DesignerEditorParametersPublisherView {

    @Override
    public native void publishProcessSourcesInfo(String ps)/*-{

        $wnd.designerprocesssources = function () {
            return ps;
        }
    }-*/;

    @Override
    public native void publishActiveNodesInfo(String an)/*-{
        $wnd.designeractivenodes = function () {
            return an;
        }
    }-*/;

    @Override
    public native void publishCompletedNodesInfo(String cn)/*-{
        $wnd.designercompletednodes = function () {
            return cn;
        }
    }-*/;
}

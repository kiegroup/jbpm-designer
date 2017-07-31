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

package org.jbpm.designer.utilities.svginline;

public class RunSvgInline {

    public static final String SS_IN_FILE = "/home/jeremy/Documents/gitRepositories/kiegroup/jbpm-designer/jbpm-designer-client/src/main/resources/org/jbpm/designer/public/stencilsets/bpmn2.0jbpm/stencildata/bpmn2.0jbpm.orig";
    public static final String SS_OUT_FILE = "/home/jeremy/Temp/bpmn2.0jbpm.orig.svginline";

    public static void main(String... args) {
        try {
            System.out.println("Adding svg images inline to file\n\t" + SS_IN_FILE);
            SvgInline inliner = new SvgInline(SS_IN_FILE,
                                              SS_OUT_FILE);
            inliner.processStencilSet();
            System.out.println("Output saved to file\n\t" + SS_OUT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

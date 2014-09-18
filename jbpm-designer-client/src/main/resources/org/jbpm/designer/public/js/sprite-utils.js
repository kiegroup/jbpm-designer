/*
 * Copyright 2014 JBoss Inc
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

/**
 * SpriteUtils object.
 */
if (!window.SpriteUtils) {

    window.SpriteUtils = {

        /** The context path for jbpm-designer resources. */
        designerContextPath: "org.jbpm.designer.jBPMDesigner",
        stencilsetsFolder: "stencilsets",

        /**
         * Return the basename for a given URL.
         * @param str The URL string.
         * @returns The baseame of the URL string.
         */
        baseName: function (str) {
            if (str) {
                var base = new String(str).substring(str.lastIndexOf('/') + 1);
                if (base.lastIndexOf(".") != -1) {
                    base = base.substring(0, base.lastIndexOf("."));
                }
                return base;
            }
            return undefined;
        },

        /**
         * Given a input URL string:
         *  - Substring from "org.jbpm.designer.jBPMDesigner"  or "stencilsets" folder name
         *  - Replaces
         *      - "/" for "_"
         *      - "." for "_"
         *  - Removes URL file extension.
         *
         *  Examples:
         *  Input: "http://localhost:8080/designer/org.jbpm.designer.jBPMDesigner/activity/list/type.business.rule.png"
         *  Output: "activity_list_type_business_rule"
         *
         *  Input: "http://kie-eap-distributions-bpms-webapp-6_2_0-SNAPSHOT-kie-wb/stencilset//org.kie.workbench.KIEWebapp/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json/icons/activity/list/type.script"
         *  Output: "stencilsets_bpmn2_0jbpm_bpmn2_0jbpm_json_icons_activity_list_type_script"
         * @param str The input URL string.
         */
        toUniqueId: function (str) {
            if (str) {
                var base = new String(str);

                // Substring from designer context path.
                var designerIndex = str.indexOf(this.designerContextPath);
                if (designerIndex != -1) {
                    base = base.substring(designerIndex + this.designerContextPath.length + 1, base.length);
                }
                else {
                    var stencilsetsIndex = str.lastIndexOf(this.stencilsetsFolder);
                    if (stencilsetsIndex != -1) {
                        base = base.substring(stencilsetsIndex, base.length);
                    }
                }


                // Remove the URL file extension.
                var lastDot = base.lastIndexOf(".");
                if (lastDot != -1) {
                    base = base.substring(0, lastDot);
                }

                // Replace the special characters.
                base = base.replace(/\//g, "_").replace(/\./g, "_");
                return base;
            }
            return undefined;
        },

        isIconFile: function (filename) {
            if (typeof filename !== 'string') {
                return false;
            }
            else if (filename.endsWith(".png") || filename.endsWith(".gif") ) {
                return true;
            }
            else {
                return false;
            }
        }

    };
}
;


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

package org.jbpm.designer.repository;

import java.util.HashMap;
import java.util.Map;

public class AssetTypeMapper {

    private static Map<String, String> mimeTypes = new HashMap<String, String>();

    static {
        mimeTypes.put("text",
                      "text/plain");
        mimeTypes.put("pdf",
                      "application/pdf");
        mimeTypes.put("ftl",
                      "text/plain");
        mimeTypes.put("form",
                      "text/xml");
        mimeTypes.put("xml",
                      "text/xml");
        mimeTypes.put("json",
                      "text/json");
        mimeTypes.put("html",
                      "text/html");
        mimeTypes.put("htm",
                      "text/html");
        mimeTypes.put("js",
                      "text/javascript");
        mimeTypes.put("css",
                      "text/css");
        mimeTypes.put("java",
                      "text/x-java-source");
        mimeTypes.put("bpmn",
                      "text/xml");
        mimeTypes.put("bpmn2",
                      "text/xml");
        mimeTypes.put("frm",
                      "application/json");
    }

    public static String findMimeType(Asset asset) {
        if (mimeTypes.containsKey(asset.getAssetType().toLowerCase())) {
            return mimeTypes.get(asset.getAssetType().toLowerCase());
        }

        return "text/plain";
    }
}

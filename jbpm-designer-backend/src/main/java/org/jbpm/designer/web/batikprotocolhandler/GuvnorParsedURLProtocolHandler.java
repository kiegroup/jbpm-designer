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

package org.jbpm.designer.web.batikprotocolhandler;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLProtocolHandler;
import org.jbpm.designer.web.profile.IDiagramProfile;

public class GuvnorParsedURLProtocolHandler implements ParsedURLProtocolHandler {

    private IDiagramProfile profile;

    public GuvnorParsedURLProtocolHandler() {
    }

    public GuvnorParsedURLProtocolHandler(IDiagramProfile profile) {
        this.profile = profile;
    }

    public String getProtocolHandled() {
        return "http";
    }

    public GuvnorParsedURLData parseURL(ParsedURL basepurl,
                                        String urlStr) {
        return parseURL(urlStr);
    }

    public GuvnorParsedURLData parseURL(String urlStr) {
        return new GuvnorParsedURLData(profile,
                                       urlStr);
    }
}

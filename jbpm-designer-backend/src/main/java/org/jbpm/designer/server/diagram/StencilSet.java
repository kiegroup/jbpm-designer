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

package org.jbpm.designer.server.diagram;

/**
 * @author Philipp Represents a Stencilset of a shape
 */
public class StencilSet {

    String url;
    String namespace;

    /**
     * Constructs a stencilSet with url and namespace
     * @param url
     * @param namespace
     */
    public StencilSet(String url,
                      String namespace) {
        this.url = url;
        this.namespace = namespace;
    }

    /**
     * Minimal constructor of an stencilset, only expects an uri
     * @param url
     */
    public StencilSet(String url) {
        this.url = url;
    }

    /**
     * Gice the specific url of an stencilset
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set a new specific url for an stencilset
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Give the namespace of a stencilset
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set a new namespace for a stencil set
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

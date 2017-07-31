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

package org.jbpm.designer.repository.impl;

import org.jbpm.designer.repository.Asset;

public class AssetBuilder {

    private AbstractAsset asset;

    public AssetBuilder(AbstractAsset asset) {
        this.asset = asset;
    }

    public AssetBuilder type(String type) {
        this.asset.setAssetTpe(type);
        return this;
    }

    public AssetBuilder name(String name) {
        this.asset.setName(name);
        return this;
    }

    public AssetBuilder description(String description) {
        this.asset.setDescription(description);
        return this;
    }

    public AssetBuilder version(String version) {
        this.asset.setVersion(version);
        return this;
    }

    public AssetBuilder uniqueId(String uniqueId) {
        this.asset.setUniqueId(uniqueId);
        return this;
    }

    public AssetBuilder owner(String owner) {
        this.asset.setOwner(owner);
        return this;
    }

    public AssetBuilder location(String location) {
        this.asset.setAssetLocation(location);
        return this;
    }

    public AssetBuilder creationDate(String creationDate) {
        this.asset.setCreationDate(creationDate);
        return this;
    }

    public AssetBuilder lastModificationDate(String modificationDate) {
        this.asset.setLastModificationDate(modificationDate);
        return this;
    }

    public AssetBuilder content(Object content) {
        this.asset.setAssetContent(content);
        return this;
    }

    public Asset getAsset() {
        return this.asset;
    }

    public AssetBuilder decode() {
//        this.asset.setName(UriUtils.decode(this.asset.getName()));
        return this;
    }
}

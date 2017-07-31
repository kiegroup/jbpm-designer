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

public abstract class AbstractAsset<T> implements Asset<T> {

    private String uniqueId;
    private String name;
    private String description;
    private String version;
    private String owner;
    private String assetTpe;
    private String assetLocation;

    private String creationDate;
    private String lastModificationDate;

    public String getAssetLocation() {
        return this.assetLocation;
    }

    public String getAssetType() {
        return this.assetTpe;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getVersion() {
        return this.version;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public String getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setAssetTpe(String assetTpe) {
        this.assetTpe = assetTpe;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getFullName() {
        return name + "." + assetTpe;
    }

    public abstract void setAssetContent(T content);

    public abstract boolean acceptBytes();
}

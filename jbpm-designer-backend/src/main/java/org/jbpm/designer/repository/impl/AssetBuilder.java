package org.jbpm.designer.repository.impl;

import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.type.Asset;

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
        this.asset.setName(UriUtils.encode(name));
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
        this.asset.setUniqueId(UriUtils.encode(uniqueId));
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
        this.asset.setName(UriUtils.decode(this.asset.getName()));
        return this;
    }


}

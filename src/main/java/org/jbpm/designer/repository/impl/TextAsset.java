package org.jbpm.designer.repository.impl;

import org.jbpm.designer.repository.Asset;

public class TextAsset extends AbstractAsset<String> {

    private String assetContent;

    public String getAssetContent() {
        return this.assetContent;
    }

    @Override
    public void setAssetContent(String content) {
        this.assetContent = content;
    }

    @Override
    public boolean acceptBytes() {
        return false;
    }
}

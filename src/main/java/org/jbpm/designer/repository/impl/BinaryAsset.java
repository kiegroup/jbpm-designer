package org.jbpm.designer.repository.impl;

import org.jbpm.designer.repository.Asset;

public class BinaryAsset extends AbstractAsset<byte[]> {

    private byte[] assetContent;


    public byte[] getAssetContent() {
        return this.assetContent;
    }

    @Override
    public void setAssetContent(byte[] content) {
        this.assetContent = content;
    }

    @Override
    public boolean acceptBytes() {
        return true;
    }
}

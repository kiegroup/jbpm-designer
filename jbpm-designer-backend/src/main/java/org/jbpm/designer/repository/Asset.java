package org.jbpm.designer.repository;

/**
 * Primary component managed by repository and can represent any type of underlying files.
 */
public interface Asset<T> extends Item {


    public enum AssetType {
        Text,
        Byte;
    }

    /**
     * Returns location in the repository where this asset is stored
     * @return - asset location
     */
    String getAssetLocation();

    /**
     * Returns type of the asset.
     * @return - asset type
     */
    String getAssetType();

    /**
     * Returns actual content of this asset
     * @return - asset content
     */
    T getAssetContent();
}

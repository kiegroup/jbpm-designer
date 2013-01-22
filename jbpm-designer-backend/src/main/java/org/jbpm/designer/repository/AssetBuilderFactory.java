package org.jbpm.designer.repository;

import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.impl.BinaryAsset;
import org.jbpm.designer.repository.impl.TextAsset;

import java.util.HashSet;
import java.util.Set;

public class AssetBuilderFactory {

    private static Set<String> binaryFormats = new HashSet<String>();

    static {
        // TODO load the list in better way
        binaryFormats.add("png");
        binaryFormats.add("gif");
        binaryFormats.add("jpeg");
        binaryFormats.add("jpg");
        binaryFormats.add("pdf");
        binaryFormats.add("binary");
    }



    public static AssetBuilder getAssetBuilder(Asset.AssetType type) {
        if(type == Asset.AssetType.Text) {
            return new AssetBuilder(new TextAsset());
        } else if(type == Asset.AssetType.Byte) {
            return new AssetBuilder(new BinaryAsset());
        } else {
            throw new IllegalArgumentException("Unknown asset type " + type);
        }
    }

    public static AssetBuilder getAssetBuilder(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".")+1);
        String nameOnly = fileName.substring(0, fileName.lastIndexOf("."));

        if(binaryFormats.contains(extension)) {
            return getAssetBuilder(Asset.AssetType.Byte).type(extension).name(nameOnly);
        } else {
            return getAssetBuilder(Asset.AssetType.Text).type(extension).name(nameOnly);
        }
    }

    public static AssetBuilder getAssetBuilder(Asset asset) {
        AssetBuilder builder = null;
        if(binaryFormats.contains(asset.getAssetType())) {
            builder = getAssetBuilder(Asset.AssetType.Byte);


        } else {
            builder = getAssetBuilder(Asset.AssetType.Text);
        }
        builder.type(asset.getAssetType())
                .name(asset.getName())
                .version(asset.getVersion())
                .location(asset.getAssetLocation())
                .uniqueId(asset.getUniqueId())
                .creationDate(asset.getCreationDate())
                .lastModificationDate(asset.getLastModificationDate())
                .description(asset.getDescription())
                .owner(asset.getOwner());

        return builder;
    }
}

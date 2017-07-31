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

import java.util.HashSet;
import java.util.Set;

import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.impl.BinaryAsset;
import org.jbpm.designer.repository.impl.TextAsset;

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
        if (type == Asset.AssetType.Text) {
            return new AssetBuilder(new TextAsset());
        } else if (type == Asset.AssetType.Byte) {
            return new AssetBuilder(new BinaryAsset());
        } else {
            throw new IllegalArgumentException("Unknown asset type " + type);
        }
    }

    public static AssetBuilder getAssetBuilder(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String nameOnly = fileName.substring(0,
                                             fileName.lastIndexOf("."));

        if (binaryFormats.contains(extension)) {
            return getAssetBuilder(Asset.AssetType.Byte).type(extension).name(nameOnly);
        } else {
            return getAssetBuilder(Asset.AssetType.Text).type(extension).name(nameOnly);
        }
    }

    public static AssetBuilder getAssetBuilder(Asset asset) {
        AssetBuilder builder = null;
        if (binaryFormats.contains(asset.getAssetType())) {
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

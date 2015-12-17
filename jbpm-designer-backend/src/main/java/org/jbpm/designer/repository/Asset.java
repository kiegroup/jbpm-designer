/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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

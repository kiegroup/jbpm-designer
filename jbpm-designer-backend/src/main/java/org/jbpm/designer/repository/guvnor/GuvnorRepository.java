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

package org.jbpm.designer.repository.guvnor;

import java.util.Collection;
import javax.enterprise.inject.Alternative;

import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Directory;
import org.jbpm.designer.repository.Filter;
import org.jbpm.designer.repository.Repository;
import org.uberfire.java.nio.file.NoSuchFileException;

/**
 * Repository implementation that is using Guvnor as a storage
 */
@Alternative
public class GuvnorRepository implements Repository {

    public GuvnorRepository() {

    }

    @Override
    public String getName() {
        return "guvnor";
    }

    public Collection<Directory> listDirectories(String startAt) {
        throw new UnsupportedOperationException();
    }

    public Collection<Asset> listAssetsRecursively(String startAt,
                                                   Filter filter) {
        throw new UnsupportedOperationException();
    }

    public Directory createDirectory(String location) {
        throw new UnsupportedOperationException();
    }

    public boolean directoryExists(String directory) {
        throw new UnsupportedOperationException();
    }

    public boolean deleteDirectory(String directory,
                                   boolean failIfNotEmpty) {
        throw new UnsupportedOperationException();
    }

    public boolean copyDirectory(String uniqueId,
                                 String location) {
        throw new UnsupportedOperationException();
    }

    public boolean moveDirectory(String uniqueId,
                                 String location,
                                 String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<Asset> listAssets(String location) {
        throw new UnsupportedOperationException();
    }

    public Collection<Asset> listAssets(String location,
                                        Filter filter) {
        throw new UnsupportedOperationException();
    }

    public Asset loadAsset(String assetUniqueId) throws NoSuchFileException {
        throw new UnsupportedOperationException();
    }

    public Asset loadAssetFromPath(String path) throws NoSuchFileException {
        throw new UnsupportedOperationException();
    }

    public String createAsset(Asset asset) {
        throw new UnsupportedOperationException();
    }

    public String updateAsset(Asset asset,
                              String commitMessage,
                              String sessionId) throws NoSuchFileException {
        return null;
    }

    public boolean deleteAsset(String assetUniqueId) {
        throw new UnsupportedOperationException();
    }

    public boolean deleteAssetFromPath(String path) {
        throw new UnsupportedOperationException();
    }

    public boolean assetExists(String assetUniqueId) {
        throw new UnsupportedOperationException();
    }

    public boolean copyAsset(String uniqueId,
                             String location) {
        throw new UnsupportedOperationException();
    }

    public boolean moveAsset(String uniqueId,
                             String location,
                             String name) {
        throw new UnsupportedOperationException();
    }
}

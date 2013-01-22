package org.jbpm.designer.repository.guvnor;

import org.jbpm.designer.repository.*;
import org.jbpm.designer.web.profile.IDiagramProfile;

import java.util.Collection;
import java.util.Map;

/**
 * Repository implementation that is using Guvnor as a storage
 */
public class GuvnorRepository implements Repository {

    public GuvnorRepository(IDiagramProfile profile) {

    }

    public Collection<Directory> listDirectories(String startAt) {
        throw new UnsupportedOperationException();
    }

    public Collection<Asset> listAssetsRecursively(String startAt, Filter filter) {
        throw new UnsupportedOperationException();
    }

    public Directory createDirectory(String location) {
        throw new UnsupportedOperationException();
    }

    public boolean directoryExists(String directory) {
        throw new UnsupportedOperationException();
    }

    public boolean deleteDirectory(String directory, boolean failIfNotEmpty) {
        throw new UnsupportedOperationException();
    }

    public boolean copyDirectory(String uniqueId, String location) {
        throw new UnsupportedOperationException();
    }

    public boolean moveDirectory(String uniqueId, String location, String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<Asset> listAssets(String location) {
        throw new UnsupportedOperationException();
    }

    public Collection<Asset> listAssets(String location, Filter filter) {
        throw new UnsupportedOperationException();
    }

    public Asset loadAsset(String assetUniqueId) throws AssetNotFoundException {
        throw new UnsupportedOperationException();
    }

    public Asset loadAssetFromPath(String path) throws AssetNotFoundException {
        throw new UnsupportedOperationException();
    }

    public String createAsset(Asset asset) {
        throw new UnsupportedOperationException();
    }

    public String updateAsset(Asset asset) throws AssetNotFoundException {
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

    public boolean copyAsset(String uniqueId, String location) {
        throw new UnsupportedOperationException();
    }

    public boolean moveAsset(String uniqueId, String location, String name) {
        throw new UnsupportedOperationException();
    }
}

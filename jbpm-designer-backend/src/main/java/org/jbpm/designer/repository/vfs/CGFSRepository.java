package org.jbpm.designer.repository.vfs;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jbpm.designer.repository.*;
import org.jbpm.designer.repository.impl.TextAsset;

/**
 * @author Golovlyev
 */
//@ApplicationScoped
public class CGFSRepository implements Repository {
  private static Repository INSTANCE = new CGFSRepository();

  private CGFSRepository() {
  }

  public static Repository getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() {
    return "cg_fs";
  }

  @Override
  public Collection<Directory> listDirectories(String startAt) {
    return null;  //TODO: implement this method
  }

  @Override
  public Collection<Asset> listAssetsRecursively(String startAt, Filter filter) {
    return null;  //TODO: implement this method
  }

  @Override
  public Directory createDirectory(String location) {
    return null;  //TODO: implement this method
  }

  @Override
  public boolean directoryExists(String directory) {
    return false;  //TODO: implement this method
  }

  @Override
  public boolean deleteDirectory(String directory, boolean failIfNotEmpty) {
    return false;  //TODO: implement this method
  }

  @Override
  public boolean copyDirectory(String sourceDirectory, String location) {
    return false;  //TODO: implement this method
  }

  @Override
  public boolean moveDirectory(String sourceDirectory, String location, String name) {
    return false;  //TODO: implement this method
  }

  @Override
  public Collection<Asset> listAssets(String location) {
    return null;  //TODO: implement this method
  }

  @Override
  public Asset loadAsset(String pathToAsset) {
    TextAsset asset = new TextAsset();
    asset.setUniqueId(Base64.encodeBase64String(pathToAsset.getBytes()));
    asset.setAssetLocation(pathToAsset);
    asset.setCreationDate(new Date().toString());
    asset.setLastModificationDate(new Date().toString());
    asset.setName(UUID.randomUUID().toString());
    asset.setOwner("");

    try {
      asset.setAssetContent(FileUtils.readFileToString(new File(pathToAsset)));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return asset;
  }

  @Override
  public Asset loadAssetFromPath(String path) {
    return null;  //TODO: implement this method
  }

  @Override
  public String createAsset(Asset asset) {
    return null;  //TODO: implement this method
  }

  @Override
  public String updateAsset(Asset asset, String commitMessage) {
    return null;  //TODO: implement this method
  }

  @Override
  public Collection<Asset> listAssets(String location, Filter filter) {
    return Collections.emptyList();
  }

  @Override
  public boolean deleteAsset(String assetUniqueId) {
    return true;
  }

  @Override
  public boolean deleteAssetFromPath(String path) {
    return false;  //TODO: implement this method
  }

  @Override
  public boolean assetExists(String assetUniqueId) {
    return true;
  }

  @Override
  public boolean copyAsset(String uniqueId, String location) {
    return true;
  }

  @Override
  public boolean moveAsset(String uniqueId, String location, String name) {
    return true;
  }
}

package org.jbpm.designer.repository.vfs;

/**
 * @author Golovlyev
 */
//@ApplicationScoped
public class CGFSRepository extends VFSRepository {

//  @Override
//  public String getName() {
//    return "cg_fs";
//  }

//  @Override
//  public Asset loadAsset(String pathToAsset) throws AssetNotFoundException {
//    TextAsset asset = new TextAsset();
//    asset.setUniqueId(Base64.encodeBase64String(pathToAsset.getBytes()));
//    asset.setAssetLocation(pathToAsset);
//    asset.setCreationDate(new Date().toString());
//    asset.setLastModificationDate(new Date().toString());
//    asset.setName(pathToAsset);
//    asset.setOwner("");
//
//    try {
//      asset.setAssetContent(FileUtils.readFileToString(new File(pathToAsset)));
//    }
//    catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return asset;
//  }

//  @Override
//  public Collection<Asset> listAssets(String location, Filter filter) {
//    return Collections.EMPTY_LIST;
//  }

//  @Override
//  public boolean deleteAsset(String assetUniqueId) {
//    return super.deleteAsset(assetUniqueId);    //TODO: implement this method
//  }
//
//  @Override
//  public boolean assetExists(String assetUniqueId) {
//    return super.assetExists(assetUniqueId);    //TODO: implement this method
//  }
//
//  @Override
//  public boolean copyAsset(String uniqueId, String location) {
//    return super.copyAsset(uniqueId, location);    //TODO: implement this method
//  }
//
//  @Override
//  public boolean moveAsset(String uniqueId, String location, String name) {
//    return super.moveAsset(uniqueId, location, name);    //TODO: implement this method
//  }
}

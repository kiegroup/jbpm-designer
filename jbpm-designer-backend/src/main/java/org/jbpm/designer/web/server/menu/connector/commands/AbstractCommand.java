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

package org.jbpm.designer.web.server.menu.connector.commands;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.fileupload.FileItemStream;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.AssetTypeMapper;
import org.jbpm.designer.repository.Directory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.NoSuchFileException;

public abstract class AbstractCommand {

    private static Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

    public JSONObject listContent(IDiagramProfile profile,
                                  String target,
                                  String current,
                                  boolean tree) throws Exception {
        try {
            if (target == null || target.length() < 1) {
                target = "/";
            } else if (!target.startsWith("/")) {
                target = "/" + target;
            }
            JSONObject retObj = new JSONObject();
            retObj.put("cwd",
                       getCwd(profile,
                              target,
                              tree));
            retObj.put("cdc",
                       getCdc(profile,
                              target,
                              tree));
            if (target == "/") {
                retObj.put("tree",
                           getTree(profile,
                                   target,
                                   tree));
            }
            addParams(retObj);
            return retObj;
        } catch (JSONException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new JSONObject();
        }
    }

    public JSONObject pasteDirectoriesOrAssets(IDiagramProfile profile,
                                               String current,
                                               List<String> targets,
                                               String cut,
                                               String dst,
                                               String src,
                                               boolean tree) throws Exception {
        if (current == null || current.length() < 1) {
            current = "/";
        } else if (!current.startsWith("/")) {
            current = "/" + current;
        }
        if (current.startsWith("//")) {
            current = current.substring(1,
                                        current.length());
        }

        if (targets != null) {
            for (String target : targets) {
                if (target.startsWith("//")) {
                    target = target.substring(1,
                                              target.length());
                }
                if (profile.getRepository().directoryExists(target)) {
                    boolean copied = profile.getRepository().copyDirectory(target,
                                                                           dst);
                    if (!copied) {
                        logger.error("Unable to copy directory: " + target + " to " + dst);
                    } else {
                        if (cut != null && cut.equals("1")) {
                            boolean deleted = profile.getRepository().deleteDirectory(target,
                                                                                      false);
                            if (!deleted) {
                                logger.error("Unable to delete directory: " + target);
                            }
                        }
                    }
                } else {
                    Asset toPasteAsset = null;
                    try {
                        toPasteAsset = profile.getRepository().loadAssetFromPath(target);
                        boolean copied = profile.getRepository().copyAsset(toPasteAsset.getUniqueId(),
                                                                           dst);
                        if (!copied) {
                            logger.error("Unable to copy asset: " + toPasteAsset.getUniqueId() + " to " + dst);
                        } else {
                            if (cut != null && cut.equals("1")) {
                                boolean deleted = profile.getRepository().deleteAsset(toPasteAsset.getUniqueId());
                                if (!deleted) {
                                    logger.error("Unable to delete asset: " + toPasteAsset.getUniqueId());
                                }
                            }
                        }
                    } catch (NoSuchFileException e) {
                        logger.error("Unable to retrieve asset: " + target);
                    }
                }
            }
        }

        JSONObject retObj = new JSONObject();
        retObj.put("cwd",
                   getCwd(profile,
                          current,
                          tree));
        retObj.put("cdc",
                   getCdc(profile,
                          current,
                          tree));
        retObj.put("tree",
                   getTree(profile,
                           "/",
                           tree));
        retObj.put("select",
                   current);

        return retObj;
    }

    public JSONObject moveDirectoryOrAsset(IDiagramProfile profile,
                                           String name,
                                           String target,
                                           String current,
                                           boolean tree) throws Exception {
        if (current == null || current.length() < 1) {
            current = "/";
        } else if (!current.startsWith("/")) {
            current = "/" + current;
        }
        if (current.startsWith("//")) {
            current = current.substring(1,
                                        current.length());
        }

        if (target.startsWith("//")) {
            target = target.substring(1,
                                      target.length());
        }

        Repository repository = profile.getRepository();

        if (repository.directoryExists(target)) {
            boolean moved = repository.moveDirectory(target,
                                                     current,
                                                     name);
            if (!moved) {
                logger.error("Unable to move directory: " + target);
            }
        } else {
            Asset tobeRenamedAsset = null;
            try {
                tobeRenamedAsset = repository.loadAssetFromPath(target);
            } catch (NoSuchFileException e) {
                logger.error("Unable to retrieve asset: " + target);
            }
            if (tobeRenamedAsset != null) {
                if (name.indexOf(".") < 0) {
                    name += "." + tobeRenamedAsset.getAssetType();
                }
                boolean moved = repository.moveAsset(tobeRenamedAsset.getUniqueId(),
                                                     current,
                                                     name);
                if (!moved) {
                    logger.error("Unable to move asset: " + target);
                }
            }
        }

        JSONObject retObj = new JSONObject();
        retObj.put("cwd",
                   getCwd(profile,
                          current,
                          tree));
        retObj.put("cdc",
                   getCdc(profile,
                          current,
                          tree));
        retObj.put("tree",
                   getTree(profile,
                           "/",
                           tree));
        retObj.put("select",
                   current);

        return retObj;
    }

    public JSONObject removeAssets(IDiagramProfile profile,
                                   String current,
                                   List<String> targets,
                                   boolean tree) throws Exception {
        if (current == null || current.length() < 1) {
            current = "/";
        } else if (!current.startsWith("/")) {
            current = "/" + current;
        }
        if (current.startsWith("//")) {
            current = current.substring(1,
                                        current.length());
        }

        if (profile.getRepository().directoryExists(current)) {
            for (String target : targets) {
                if (target.startsWith("//")) {
                    target = target.substring(1,
                                              target.length());
                }
                boolean deleted;
                if (profile.getRepository().directoryExists(target)) {
                    deleted = profile.getRepository().deleteDirectory(target,
                                                                      false);
                } else {
                    deleted = profile.getRepository().deleteAssetFromPath(target);
                }
                if (!deleted) {
                    logger.error("Unable to delete asset: " + target);
                }
            }
        }

        JSONObject retObj = new JSONObject();
        retObj.put("cwd",
                   getCwd(profile,
                          current,
                          tree));
        retObj.put("cdc",
                   getCdc(profile,
                          current,
                          tree));
        retObj.put("tree",
                   getTree(profile,
                           "/",
                           tree));
        retObj.put("select",
                   "");

        return retObj;
    }

    public JSONObject uploadFiles(IDiagramProfile profile,
                                  String current,
                                  List<FileItemStream> listFiles,
                                  List<ByteArrayOutputStream> listFileStreams,
                                  boolean tree) throws Exception {
        if (current == null || current.length() < 1) {
            current = "/";
        } else if (!current.startsWith("/")) {
            current = "/" + current;
        }
        if (current.startsWith("//")) {
            current = current.substring(1,
                                        current.length());
        }

        if (profile.getRepository().directoryExists(current)) {
            try {
                int i = 0;
                for (FileItemStream uplFile : listFiles) {
                    String fileName = uplFile.getName();
                    String fileContentType = uplFile.getContentType();

                    ByteArrayOutputStream os = listFileStreams.get(i);
                    checkUploadFile(fileName,
                                    os);
                    checkAlreadyExists(profile,
                                       fileName,
                                       current);

                    String[] fileParts = fileName.split("\\.");
                    String fileType = fileParts[fileParts.length - 1];
                    String fileNameOnly = fileName.substring(0,
                                                             fileName.length() - (fileType.length() + 1));

                    AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
                    assetBuilder.content(os.toByteArray())
                            .location(current)
                            .name(fileNameOnly)
                            .type(fileType)
                            .version("1.0");
                    Asset newAsset = assetBuilder.getAsset();
                    profile.getRepository().createAsset(newAsset);
                    i++;
                }
            } catch (Exception e) {
                logger.error("Unable to upload file: " + e.getMessage());
            }
        } else {
            logger.error("Directory does not exist: " + current);
        }

        JSONObject retObj = new JSONObject();
        retObj.put("cwd",
                   getCwd(profile,
                          current,
                          tree));
        retObj.put("cdc",
                   getCdc(profile,
                          current,
                          tree));
        retObj.put("tree",
                   getTree(profile,
                           current,
                           tree));
        retObj.put("select",
                   current);
        addParams(retObj);
        return retObj;
    }

    public JSONObject makeDirectory(IDiagramProfile profile,
                                    String current,
                                    String name,
                                    boolean tree) throws Exception {
        if (current == null || current.length() < 1) {
            current = "/";
        } else if (!current.startsWith("/")) {
            current = "/" + current;
        }

        Directory newDir = null;
        try {
            newDir = profile.getRepository().createDirectory(current + "/" + name);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        JSONObject retObj = new JSONObject();
        retObj.put("cwd",
                   getCwd(profile,
                          current,
                          tree));
        retObj.put("cdc",
                   getCdc(profile,
                          current,
                          tree));
        retObj.put("tree",
                   getTree(profile,
                           "/",
                           tree));
        retObj.put("select",
                   newDir == null ? "" : newDir.getName());

        return retObj;
    }

    public JSONObject makeFile(IDiagramProfile profile,
                               String current,
                               String name,
                               boolean tree) throws Exception {
        if (current == null || current.length() < 1) {
            current = "/";
        } else if (!current.startsWith("/")) {
            current = "/" + current;
        }

        if (name.endsWith(".bpmn2")) {
            name = name.substring(0,
                                  name.length() - 6);
        } else if (name.endsWith(("bpmn"))) {
            name = name.substring(0,
                                  name.length() - 5);
        }
        String fullName = name + ".bpmn2";

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("")
                .type("bpmn2")
                .name(name)
                .location(current);

        String newFileId = profile.getRepository().createAsset(builder.getAsset());
        if (newFileId == null) {
            logger.error("Unable to create asset: " + current + "/" + fullName);
        }

        JSONObject retObj = new JSONObject();
        retObj.put("cwd",
                   getCwd(profile,
                          current,
                          tree));
        retObj.put("cdc",
                   getCdc(profile,
                          current,
                          tree));
        retObj.put("tree",
                   getTree(profile,
                           "/",
                           tree));
        retObj.put("select",
                   newFileId == null ? "" : current + "/" + fullName);

        return retObj;
    }

    public Map<String, Object> getTree(IDiagramProfile profile,
                                       String path,
                                       boolean tree) throws Exception {
        String qname = "";
        if (!"/".equals(path)) {
            String[] pathParts = path.split("/");
            qname = pathParts[pathParts.length - 1];
        } else {
            qname = path;
        }

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("hash",
                 path);
        info.put("name",
                 qname);
        info.put("read",
                 "true");
        info.put("write",
                 "true");

        Collection<Directory> subdirs = profile.getRepository().listDirectories(path);
        List<Object> dirs = new ArrayList<Object>();
        if (subdirs != null) {
            for (Directory sub : subdirs) {
                dirs.add(getTree(profile,
                                 path.endsWith("/") ? path + sub.getName() : path + "/" + sub.getName(),
                                 tree));
            }
        }

        info.put("dirs",
                 dirs);
        return info;
    }

    public List<Map<String, Object>> getCdc(IDiagramProfile profile,
                                            String path,
                                            boolean tree) throws Exception {
        List<Map<String, Object>> cdcinfo = new ArrayList<Map<String, Object>>();
        Collection<Asset> assets = profile.getRepository().listAssets(path);
        Collection<Directory> dirs = profile.getRepository().listDirectories(path);

        if (assets != null) {
            for (Asset asset : assets) {
                cdcinfo.add(getAssetInfo(profile,
                                         asset));
            }
        }
        if (dirs != null) {
            for (Directory dir : dirs) {
                cdcinfo.add(getDirectoryInfo(profile,
                                             dir));
            }
        }
        return cdcinfo;
    }

    public Map<String, Object> getCwd(IDiagramProfile profile,
                                      String path,
                                      boolean tree) throws Exception {
        Map<String, Object> cwdinfo = new HashMap<String, Object>();
        cwdinfo.put("hash",
                    path);
        cwdinfo.put("name",
                    path);
        cwdinfo.put("mime",
                    "directory");
        cwdinfo.put("rel",
                    path);
        cwdinfo.put("size",
                    "0");
        cwdinfo.put("date",
                    ""); // TODO fix
        cwdinfo.put("read",
                    true);
        cwdinfo.put("write",
                    true);
        cwdinfo.put("rm",
                    false);
        return cwdinfo;
    }

    public void addParams(JSONObject retObj) throws Exception {
        JSONObject paramsObj = new JSONObject();
        paramsObj.put("dotFiles",
                      "true");
        paramsObj.put("archives",
                      new JSONArray());
        paramsObj.put("uplMaxSize",
                      "100M");
        paramsObj.put("url",
                      "");
        paramsObj.put("extract",
                      new JSONArray());
        retObj.put("params",
                   paramsObj);
        retObj.put("disabled",
                   new JSONArray());
    }

    protected Map<String, Object> getDirectoryInfo(IDiagramProfile profile,
                                                   Directory dir) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("name",
                 dir.getName());
        info.put("hash",
                 dir.getLocation() + "/" + dir.getName());
        info.put("mime",
                 "directory");
        info.put("date",
                 "");
        info.put("size",
                 "");
        info.put("read",
                 true);
        info.put("write",
                 true);
        info.put("rm",
                 true);
        info.put("url",
                 "");
        info.put("tmb",
                 "");

        return info;
    }

    protected Map<String, Object> getAssetInfo(IDiagramProfile profile,
                                               Asset asset) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("name",
                 asset.getFullName());
        info.put("hash",
                 asset.getAssetLocation() + "/" + asset.getFullName());
        info.put("mime",
                 AssetTypeMapper.findMimeType(asset));
        info.put("date",
                 "");
        info.put("size",
                 "");
        info.put("read",
                 true);
        info.put("write",
                 true);
        info.put("rm",
                 true);
        info.put("url",
                 asset.getAssetType() + "|" + asset.getUniqueId());

        if (asset.getAssetType().equals("bpmn") || asset.getAssetType().equals("bpmn2")) {
            try {
                info.put("processlocation",
                         asset.getAssetLocation());
                Asset ab = profile.getRepository().loadAssetFromPath(asset.getAssetLocation() + "/" + asset.getFullName());

                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();

                Definitions def = ((JbpmProfileImpl) profile).getDefinitions((String) ab.getAssetContent());

                List<RootElement> rootElements = def.getRootElements();
                for (RootElement root : rootElements) {
                    if (root instanceof Process) {
                        Process process = (Process) root;
                        info.put("processid",
                                 process.getId());

                        boolean foundVersion = false;
                        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("version")) {
                                info.put("assetversion",
                                         entry.getValue());
                                foundVersion = true;
                            }
                        }
                        if (!foundVersion) {
                            info.put("assetversion",
                                     "");
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Unable to extract process id and version from: " + asset.getFullName());
                info.put("processid",
                         "");
                info.put("assetversion",
                         "");
            }
        } else {
            info.put("assetversion",
                     "");
        }

        return info;
    }

    protected void checkUploadFile(String fileName,
                                   ByteArrayOutputStream os) throws Exception {
        if (!_checkName(fileName)) {
            throw new Exception("Invalid upload file name: " + fileName);
        }
        int uploadSizeOctets = os.size();
        checkUploadSizes(uploadSizeOctets);
    }

    protected void checkAlreadyExists(IDiagramProfile profile,
                                      String fileName,
                                      String current) throws Exception {
        Collection<Asset> assets = profile.getRepository().listAssets(current);
        for (Asset asset : assets) {
            String assetFull = asset.getName() + "." + asset.getAssetType();
            if (assetFull.equals(fileName)) {
                throw new Exception("File name " + fileName + " already exists in directory " + current);
            }
        }
    }

    protected void checkUploadSizes(int uploadSizeOctets) throws Exception {
        if (uploadSizeOctets > (100 * 1024 * 1024)) {
            throw new Exception("File exceeds the maximum allowed filesize.");
        }
    }

    public boolean _checkName(String n) {
        if (n == null) {
            return false;
        }
        n = n.trim();
        if ("".equals(n)) {
            return false;
        }

        return n.matches("|^[^\\\\/\\<\\>:]+$|");
    }
}

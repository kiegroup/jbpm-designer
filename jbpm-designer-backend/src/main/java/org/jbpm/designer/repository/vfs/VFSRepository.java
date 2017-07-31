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

package org.jbpm.designer.repository.vfs;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Directory;
import org.jbpm.designer.repository.Filter;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.impl.AbstractAsset;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.server.service.PathEvent;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

@ApplicationScoped
public class VFSRepository implements Repository {

    private IOService ioService;

    @Inject
    private RepositoryDescriptor descriptor;

    @Inject
    private Event<PathEvent> pathEvent;

    @Inject
    User identity;

    public VFSRepository() {

    }

    @Inject
    public VFSRepository(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    public RepositoryDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(RepositoryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return "vfs";
    }

    public Collection<Directory> listDirectories(String startAt) {
        startAt = UriUtils.encode(startAt);
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + startAt));
        DirectoryStream<Path> directories = ioService.newDirectoryStream(path,
                                                                         new DirectoryStream.Filter<Path>() {

                                                                             public boolean accept(final Path entry) throws IOException {
                                                                                 if (Files.isDirectory(entry)) {
                                                                                     return true;
                                                                                 }
                                                                                 return false;
                                                                             }
                                                                         });
        Collection<Directory> foundDirectories = new ArrayList<Directory>();
        Iterator<Path> it = directories.iterator();
        while (it.hasNext()) {
            Path dir = it.next();
            String uniqueId = encodeUniqueId(dir.toUri().toString());
            foundDirectories.add(new Directory(uniqueId,
                                               dir.getFileName().toString(),
                                               trimLocation(dir)));
        }

        return foundDirectories;
    }

    public Collection<Asset> listAssetsRecursively(String startAt,
                                                   final Filter filter) {
        startAt = UriUtils.encode(startAt);
        final Collection<Asset> foundAssets = new ArrayList<Asset>();
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + startAt));

        Files.walkFileTree(path,
                           new SimpleFileVisitor<Path>() {

                               public FileVisitResult visitFile(Path paths,
                                                                BasicFileAttributes basicFileAttributes) throws IOException {
                                   if (filter.accept(paths)) {
                                       foundAssets.add(buildAsset(paths,
                                                                  false));
                                   }
                                   return FileVisitResult.CONTINUE;
                               }
                           });

        return foundAssets;
    }

    public Directory createDirectory(String location) {
        location = UriUtils.encode(location);
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + location));

        path = ioService.createDirectories(path);
        String uniqueId = encodeUniqueId(path.toUri().toString());
        Directory directory = new Directory(uniqueId,
                                            path.getFileName().toString(),
                                            trimLocation(path));
        return directory;
    }

    public boolean directoryExists(String directory) {
        directory = UriUtils.encode(directory);
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + directory));

        return ioService.exists(path) && Files.isDirectory(path);
    }

    public boolean deleteDirectory(String directory,
                                   boolean failIfNotEmpty) {
        directory = UriUtils.encode(directory);
        try {
            Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + directory));
            if (!Files.isDirectory(path)) {
                return false;
            }
            Files.walkFileTree(path,
                               new SimpleFileVisitor<Path>() {
                                   @Override
                                   public FileVisitResult visitFile(Path paths,
                                                                    BasicFileAttributes basicFileAttributes) throws IOException {
                                       ioService.delete(paths);

                                       return FileVisitResult.CONTINUE;
                                   }

                                   @Override
                                   public FileVisitResult postVisitDirectory(Path dir,
                                                                             IOException e) throws IOException {
                                       if (e == null) {
                                           ioService.deleteIfExists(dir);
                                           return FileVisitResult.CONTINUE;
                                       } else {
                                           // directory iteration failed
                                           throw e;
                                       }
                                   }
                               });

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean copyDirectory(String sourceDirectory,
                                 String location) {
        sourceDirectory = UriUtils.encode(sourceDirectory);
        location = UriUtils.encode(location);
        if (!directoryExists(sourceDirectory)) {
            throw new IllegalArgumentException("Directory does not exist " + sourceDirectory);
        }
        try {
            final FileSystem fileSystem = descriptor.getFileSystem();
            final Path sourcePath = fileSystem.provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + sourceDirectory));
            if (!Files.isDirectory(sourcePath)) {
                return false;
            }
            final String destinationPathRoot = descriptor.getStringRepositoryRoot() + location + fileSystem.getSeparator() + sourcePath.getFileName().toString();
            Files.walkFileTree(sourcePath,
                               new SimpleFileVisitor<Path>() {
                                   @Override
                                   public FileVisitResult preVisitDirectory(Path dir,
                                                                            BasicFileAttributes attrs) throws IOException {
                                       Path destinationPath = fileSystem.provider().getPath(URI.create(destinationPathRoot +
                                                                                                               fileSystem.getSeparator() + sourcePath.relativize(dir)));
                                       fileSystem.provider().createDirectory(destinationPath);

                                       return FileVisitResult.CONTINUE;
                                   }

                                   @Override
                                   public FileVisitResult visitFile(Path currentFile,
                                                                    BasicFileAttributes basicFileAttributes) throws IOException {

                                       // .gitkeep for empty directories (UF-456)
                                       if (!currentFile.endsWith(".gitignore") && !currentFile.endsWith(".gitkeep")) {
                                           Path destinationPath = fileSystem.provider().getPath(URI.create(destinationPathRoot +
                                                                                                                   fileSystem.getSeparator() + sourcePath.relativize(currentFile)));
                                           createIfNotExists(destinationPath);

                                           fileSystem.provider().copy(currentFile,
                                                                      destinationPath,
                                                                      StandardCopyOption.REPLACE_EXISTING);
                                       }
                                       return FileVisitResult.CONTINUE;
                                   }
                               });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean moveDirectory(String sourceDirectory,
                                 String location,
                                 String name) {
        sourceDirectory = UriUtils.encode(sourceDirectory);
        location = UriUtils.encode(location);
        if (!directoryExists(sourceDirectory)) {
            throw new IllegalArgumentException("Directory does not exist " + sourceDirectory);
        }
        try {
            final FileSystem fileSystem = descriptor.getFileSystem();
            final Path sourcePath = fileSystem.provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + sourceDirectory));
            if (!Files.isDirectory(sourcePath)) {
                return false;
            }
            if (name == null) {
                name = sourcePath.getFileName().toString();
            }

            String destinationFolder = descriptor.getStringRepositoryRoot() + location;
            if (!destinationFolder.endsWith(fileSystem.getSeparator())) {
                destinationFolder = destinationFolder + fileSystem.getSeparator();
            }
            final String destinationPathRoot = destinationFolder + name;

            Files.walkFileTree(sourcePath,
                               new SimpleFileVisitor<Path>() {
                                   @Override
                                   public FileVisitResult visitFile(Path currentFile,
                                                                    BasicFileAttributes basicFileAttributes) throws IOException {
                                       Path destinationPath = fileSystem.provider().getPath(URI.create(destinationPathRoot
                                                                                                               + fileSystem.getSeparator() + sourcePath.relativize(currentFile)));
                                       createIfNotExists(destinationPath);
                                       fileSystem.provider().move(currentFile,
                                                                  destinationPath,
                                                                  StandardCopyOption.REPLACE_EXISTING);

                                       return FileVisitResult.CONTINUE;
                                   }

                                   @Override
                                   public FileVisitResult postVisitDirectory(Path dir,
                                                                             IOException e) throws IOException {
                                       if (e == null) {
                                           try {
                                               Path destinationPath = fileSystem.provider().getPath(URI.create(destinationPathRoot
                                                                                                                       + fileSystem.getSeparator() + sourcePath.relativize(dir)));
                                               createIfNotExists(destinationPath);
                                               fileSystem.provider().move(dir,
                                                                          destinationPath,
                                                                          StandardCopyOption.REPLACE_EXISTING);
                                           } catch (Exception e1) {
                                               fileSystem.provider().deleteIfExists(dir);
                                           }
                                           return FileVisitResult.CONTINUE;
                                       } else {
                                           // directory iteration failed
                                           throw e;
                                       }
                                   }
                               });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Collection<Asset> listAssets(String location) {
        location = UriUtils.encode(location);
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + location));
        DirectoryStream<Path> directories = ioService.newDirectoryStream(path,
                                                                         new DirectoryStream.Filter<Path>() {

                                                                             public boolean accept(final Path entry) throws IOException {
                                                                                 if (!Files.isDirectory(entry)) {
                                                                                     return true;
                                                                                 }
                                                                                 return false;
                                                                             }
                                                                         });
        Collection<Asset> foundDirectories = new ArrayList<Asset>();
        Iterator<Path> it = directories.iterator();
        while (it.hasNext()) {
            Asset asset = buildAsset(it.next(),
                                     false);
            foundDirectories.add(asset);
        }

        return foundDirectories;
    }

    public Collection<Asset> listAssets(String location,
                                        final Filter filter) {
        location = UriUtils.encode(location);
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + location));
        DirectoryStream<Path> directories = ioService.newDirectoryStream(path,
                                                                         new DirectoryStream.Filter<Path>() {

                                                                             public boolean accept(final Path entry) throws IOException {

                                                                                 return filter.accept(entry);
                                                                             }
                                                                         });
        Collection<Asset> foundDirectories = new ArrayList<Asset>();
        Iterator<Path> it = directories.iterator();
        while (it.hasNext()) {
            Asset asset = buildAsset(it.next(),
                                     false);
            foundDirectories.add(asset);
        }

        return foundDirectories;
    }

    public Asset loadAsset(String assetUniqueId) throws NoSuchFileException {
        String uniqueId = decodeUniqueId(assetUniqueId);
        Path assetPath = getFileSystem(uniqueId).provider().getPath(URI.create(uniqueId));

        Asset asset = buildAsset(assetPath,
                                 true);

        return asset;
    }

    public Asset loadAssetFromPath(String location) throws NoSuchFileException {
        location = UriUtils.encode(location);
        Path path = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + location));

        if (ioService.exists(path)) {
            return loadAsset(path.toUri().toString());
        } else {
            throw new NoSuchFileException();
        }
    }

    public String createAsset(Asset asset) {
        encodeAsset(asset);
        FileSystem fileSystem = getFileSystem(asset.getUniqueId());
        URI pathURI = null;
        if (asset.getAssetLocation().startsWith(fileSystem.provider().getScheme()) ||
                asset.getAssetLocation().startsWith("default://")) {
            pathURI = URI.create(asset.getAssetLocation() + "/" + asset.getFullName());
        } else {
            pathURI = URI.create(descriptor.getStringRepositoryRoot() + (asset.getAssetLocation().equals("/") ? "" : asset.getAssetLocation()) + "/" + asset.getFullName());
        }

        Path filePath = fileSystem.provider().getPath(pathURI);

        if (assetExists(filePath.toUri().toString())) {
            throw new org.uberfire.java.nio.file.FileAlreadyExistsException(pathURI.toString());
        }
        createIfNotExists(filePath);
        try {
            CommentedOption commentedOption = new CommentedOption(getIdentity(),
                                                                  "Created asset " + asset.getFullName());
            OutputStream outputStream = fileSystem.provider().newOutputStream(filePath,
                                                                              StandardOpenOption.TRUNCATE_EXISTING,
                                                                              commentedOption);
            if (((AbstractAsset) asset).acceptBytes()) {
                outputStream.write(((Asset<byte[]>) asset).getAssetContent());
            } else {
                try {
                    outputStream.write(asset.getAssetContent().toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    outputStream.write(asset.getAssetContent().toString().getBytes());
                }
            }
            outputStream.close();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error when creating asset",
                                       e);
        }
        return encodeUniqueId(filePath.toUri().toString());
    }

    public String updateAsset(Asset asset,
                              String commitMessage,
                              String sessionId) throws NoSuchFileException {
        encodeAsset(asset);
        String uniqueId = decodeUniqueId(asset.getUniqueId());
        Path filePath = getFileSystem(uniqueId).provider().getPath(URI.create(uniqueId));
        if (commitMessage == null) {
            commitMessage = "Updated asset ";
        }
        if (!ioService.exists(filePath)) {
            throw new NoSuchFileException();
        }
        CommentedOption commentedOption = new CommentedOption(sessionId,
                                                              getIdentity(),
                                                              null,
                                                              commitMessage,
                                                              new Date());
        if (((AbstractAsset) asset).acceptBytes()) {
            ioService.write(filePath,
                            ((Asset<byte[]>) asset).getAssetContent(),
                            StandardOpenOption.TRUNCATE_EXISTING,
                            commentedOption);
        } else {
            try {
                ioService.write(filePath,
                                asset.getAssetContent().toString().getBytes("UTF-8"),
                                StandardOpenOption.TRUNCATE_EXISTING,
                                commentedOption);
            } catch (UnsupportedEncodingException e) {
                ioService.write(filePath,
                                asset.getAssetContent().toString().getBytes(),
                                StandardOpenOption.TRUNCATE_EXISTING,
                                commentedOption);
            }
        }

        return asset.getUniqueId();
    }

    public boolean deleteAsset(String assetUniqueId) {
        String uniqueId = decodeUniqueId(assetUniqueId);
        try {
            return ioService.deleteIfExists(getFileSystem(uniqueId).provider().getPath(URI.create(uniqueId)));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteAssetFromPath(String path) {
        path = UriUtils.encode(path);
        Path filePath = descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + path));

        return deleteAsset(filePath.toUri().toString());
    }

    public boolean assetExists(String assetUniqueId) {
        String uniqueId = decodeUniqueId(assetUniqueId);
        try {
            return ioService.exists(descriptor.getFileSystem().provider().getPath(URI.create(uniqueId)));
        } catch (Exception e) {
            return ioService.exists(descriptor.getFileSystem().provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + assetUniqueId)));
        }
    }

    public boolean copyAsset(String uniqueId,
                             String location) {
        location = UriUtils.encode(location);
        String decodedUniqueId = decodeUniqueId(uniqueId);
        if (!assetExists(decodedUniqueId)) {
            throw new IllegalArgumentException("Asset does not exist");
        }
        try {
            FileSystem fileSystem = descriptor.getFileSystem();
            Path sourcePath = fileSystem.provider().getPath(URI.create(decodedUniqueId));
            Path destinationPath = fileSystem.provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + location
                                                                                    + fileSystem.getSeparator() + sourcePath.getFileName().toString()));
            createIfNotExists(destinationPath);

            CommentedOption commentedOption = new CommentedOption(getIdentity(),
                                                                  "Copied asset " + sourcePath.getFileName()
                                                                          + " into " + location);

            fileSystem.provider().copy(sourcePath,
                                       destinationPath,
                                       StandardCopyOption.REPLACE_EXISTING,
                                       commentedOption);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean moveAsset(String uniqueId,
                             String location,
                             String name) {
        name = UriUtils.encode(name);
        location = UriUtils.encode(location);
        String decodedUniqueId = decodeUniqueId(uniqueId);
        if (!assetExists(decodedUniqueId)) {
            throw new IllegalArgumentException("Asset does not exist");
        }
        try {
            FileSystem fileSystem = descriptor.getFileSystem();
            Path sourcePath = fileSystem.provider().getPath(URI.create(decodedUniqueId));
            if (name == null) {
                name = sourcePath.getFileName().toString();
            }

            Path destinationPath = fileSystem.provider().getPath(URI.create(descriptor.getStringRepositoryRoot() + location + fileSystem.getSeparator() + name));
            createIfNotExists(destinationPath);
            CommentedOption commentedOption = new CommentedOption(getIdentity(),
                                                                  "Moved asset " + sourcePath.getFileName()
                                                                          + " into " + location);
            fileSystem.provider().move(sourcePath,
                                       destinationPath,
                                       StandardCopyOption.REPLACE_EXISTING,
                                       commentedOption);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected Asset buildAsset(Path file,
                               boolean loadContent) {
        String name = file.getFileName().toString();
        String location = trimLocation(file);

        AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(name);
        BasicFileAttributes attrs = getFileSystem(file.toUri().toString()).provider().readAttributes(file,
                                                                                                     BasicFileAttributes.class);
        assetBuilder.uniqueId(encodeUniqueId(file.toUri().toString()))
                .location(location)
                .creationDate(attrs.creationTime() == null ? "" : new Date(attrs.creationTime().toMillis()).toString())
                .lastModificationDate(attrs.lastModifiedTime() == null ? "" : new Date(attrs.lastModifiedTime().toMillis()).toString())
                // TODO some provider specific details
                .description("")
                .owner("");

        if (loadContent) {
            if (((AbstractAsset) assetBuilder.getAsset()).acceptBytes()) {
                assetBuilder.content(ioService.readAllBytes(file));
            } else {
                assetBuilder.content(ioService.readAllString(file,
                                                             Charset.forName("UTF-8")));
            }
        }

        return assetBuilder.getAsset();
    }

    private String decodeUniqueId(String uniqueId) {
        if (Base64.isBase64(uniqueId)) {
            byte[] decoded = Base64.decodeBase64(uniqueId);
            try {
                String uri = new String(decoded,
                                        "UTF-8");

                return UriUtils.encode(uri);
            } catch (UnsupportedEncodingException e) {

            }
        }

        return UriUtils.encode(uniqueId);
    }

    private String encodeUniqueId(String uniqueId) {
        try {
            return Base64.encodeBase64URLSafeString(UriUtils.decode(uniqueId).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private String trimLocation(Path file) {
        String location = "";
        String pathAsString = file.getParent().toString();
        if (pathAsString.startsWith(descriptor.getRepositoryRoot().getScheme())) {
            location = pathAsString.replaceFirst(descriptor.getStringRepositoryRoot(),
                                                 "");
        } else {
            location = pathAsString.replaceFirst(descriptor.getRepositoryRootPath().toString(),
                                                 "");
        }

        if (!location.startsWith(descriptor.getFileSystem().getSeparator())) {
            location = descriptor.getFileSystem().getSeparator() + location;
        }

        return location;
    }

    private void createIfNotExists(Path filePath) {
        if (!ioService.exists(filePath.getParent())) {
            try {
                FileSystem fileSystem = getFileSystem(filePath.toUri().toString());
                fileSystem.provider().createDirectory(filePath.getParent(),
                                                      null);
            } catch (FileAlreadyExistsException e) {
                // TODO currently git provider does not properly check existence of directories
            }
        }
    }

    private FileSystem getFileSystem(String uri) {
        if (pathEvent != null) {
            pathEvent.fire(new PathEvent(uri));
        }
        return descriptor.getFileSystem();
    }

    private Asset encodeAsset(Asset asset) {
        ((AbstractAsset) asset).setAssetLocation(UriUtils.encode(asset.getAssetLocation()));
        ((AbstractAsset) asset).setName(UriUtils.encode(asset.getName()));

        return asset;
    }

    private String getIdentity() {
        if (this.identity != null && this.identity.getIdentifier() != null) {
            return identity.getIdentifier();
        } else {
            return "admin";
        }
    }

    public void createGlobalDirOnNewProject(@Observes NewProjectEvent newProjectEvent) {
        // create the global dir before the asset is created (upon new project creation)
        KieProject project = (KieProject) newProjectEvent.getProject();
        String projectPath = org.uberfire.backend.server.util.Paths.convert(project.getRootPath()).toUri().toString();
        String separator = org.uberfire.backend.server.util.Paths.convert(project.getRootPath()).getFileSystem().getSeparator();
        String globalDirPath = projectPath + separator + "global";
        String resourcesDirPath = projectPath + separator + "src" + separator + "main" + separator + "resources";
        Path globalDirVFSPath = ioService.get(URI.create(globalDirPath));

        ioService.startBatch(new FileSystem[]{ioService.getFileSystem(URI.create(globalDirPath))});

        if (!ioService.exists(globalDirVFSPath)) {
            ioService.createDirectory(globalDirVFSPath);

            // custom editors default
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "customeditors.json")),
                            "{ \"editors\":{\n" +
                                    "            \"Actors\" : \"/designer/customeditors/sampleactorseditor.html\"\n" +
                                    "        }}");

            // default color themes
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "themes.json")),
                            "{ \"themes\":{\n" +
                                    "        \"jBPM\":{\n" +
                                    "           \"Start Events\" : \"#9acd32|#000000|#000000\",\n" +
                                    "           \"Catching Intermediate Events\" : \"#f5deb3|#a0522d|#000000\",\n" +
                                    "           \"Throwing Intermediate Events\" : \"#8cabff|#008cec|#000000\",\n" +
                                    "           \"End Events\" : \"#ff6347|#000000|#000000\",\n" +
                                    "           \"Gateways\" : \"#f0e68c|#a67f00|#000000\",\n" +
                                    "           \"Tasks\" : \"#fafad2|#000000|#000000\",\n" +
                                    "           \"Subprocesses\" : \"#fafad2|#000000|#000000\",\n" +
                                    "           \"Service Tasks\" : \"#fafad2|#000000|#000000\",\n" +
                                    "           \"Data Objects\" : \"#C0C0C0|#000000|#000000\",\n" +
                                    "           \"Swimlanes\" : \"#ffffff|#000000|#000000\",\n" +
                                    "           \"Artifacts\" : \"#ffffff|#000000|#000000\",\n" +
                                    "           \"Connecting Objects\" : \"#000000|#000000|#000000\"\n" +
                                    "        },\n" +
                                    "        \"HighContrast\":{\n" +
                                    "           \"Start Events\" : \"#d2b29f|#000000|#000000\",\n" +
                                    "           \"Catching Intermediate Events\" : \"#ffd3a6|#a37e25|#000000\",\n" +
                                    "           \"Throwing Intermediate Events\" : \"#adbaf2|#000099|#000000\",\n" +
                                    "           \"End Events\" : \"#ffc4d1|#000000|#000000\",\n" +
                                    "           \"Gateways\" : \"#ccaea0|#330600|#000000\",\n" +
                                    "           \"Tasks\" : \"#f3df8c|#000000|#000000\",\n" +
                                    "           \"Subprocesses\" : \"#fafad2|#000000|#000000\",\n" +
                                    "           \"Service Tasks\" : \"#f3df8c|#000000|#000000\",\n" +
                                    "           \"Data Objects\" : \"#C0C0C0|#000000|#000000\",\n" +
                                    "           \"Swimlanes\" : \"#ffffff|#000000|#000000\",\n" +
                                    "           \"Artifacts\" : \"#ffffff|#000000|#000000\",\n" +
                                    "           \"Connecting Objects\" : \"#000000|#000000|#000000\"\n" +
                                    "        }\n" +
                                    "   }\n" +
                                    "}\n");

            // default images
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "defaultemailicon.gif")),
                            DatatypeConverter.parseBase64Binary("R0lGODlhEAAQANUAAChilmd9qW2DrXeMtJiYkZuajqGeiqZrEKehh6m30qyjhK1yErCmgbOpfrZ8FLmter2EFr+wd8HG2ca0ceDq9+Ps+Ojv+Ovx+fL1+vb4+/j5/Pvll/vusPvyufz62/797wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAACAALAAAAAAQABAAAAaAQJBwSCwaJ8ikclLUOJ9QJtEpqVolGekQAsl4v16tEPKBYKpnCSYC4ro/ZYx8/oB47vi7GcDHPBwdgYKBHA4DAgEXDQsbjY6NCxd8ABcMIAeYmI0HFp2eCkUHGwcVCQmlpwihpBUVFK2vBkWtprWmFbJEFK+7rrsUBUUEw8TFBUEAOw=="));
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "defaultlogicon.gif")),
                            DatatypeConverter.parseBase64Binary("R0lGODlhEAAQAMQAAG+Fr3CFr3yRuIOSsYaUroidwIuWrI+ZqJGlx5WdpZugoKGknaeomK6slLKvkL21idSyaNq9fN3o+ODIj+Ps+evx+vP2+/f4+/n6/AAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABkALAAAAAAQABAAAAVlYCaOZEk+aIqa4oO9MPSwLvxGsvlcwYUglwluRnJYjkiko9SwBCy/guDZKDEq2GyWUVpUApXotLIoKSjodFpRSlACFDGAkigdJHg8Gn8oGSQBEnISBiUEeYh4BCUDjY6PAyySIyEAOw=="));
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "defaultservicenodeicon.png")),
                            DatatypeConverter.parseBase64Binary("iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAC7mlDQ1BJQ0MgUHJvZmlsZQAAeAGFVM9rE0EU/jZuqdAiCFprDrJ4kCJJWatoRdQ2/RFiawzbH7ZFkGQzSdZuNuvuJrWliOTi0SreRe2hB/+AHnrwZC9KhVpFKN6rKGKhFy3xzW5MtqXqwM5+8943731vdt8ADXLSNPWABOQNx1KiEWlsfEJq/IgAjqIJQTQlVdvsTiQGQYNz+Xvn2HoPgVtWw3v7d7J3rZrStpoHhP1A4Eea2Sqw7xdxClkSAog836Epx3QI3+PY8uyPOU55eMG1Dys9xFkifEA1Lc5/TbhTzSXTQINIOJT1cVI+nNeLlNcdB2luZsbIEL1PkKa7zO6rYqGcTvYOkL2d9H5Os94+wiHCCxmtP0a4jZ71jNU/4mHhpObEhj0cGDX0+GAVtxqp+DXCFF8QTSeiVHHZLg3xmK79VvJKgnCQOMpkYYBzWkhP10xu+LqHBX0m1xOv4ndWUeF5jxNn3tTd70XaAq8wDh0MGgyaDUhQEEUEYZiwUECGPBoxNLJyPyOrBhuTezJ1JGq7dGJEsUF7Ntw9t1Gk3Tz+KCJxlEO1CJL8Qf4qr8lP5Xn5y1yw2Fb3lK2bmrry4DvF5Zm5Gh7X08jjc01efJXUdpNXR5aseXq8muwaP+xXlzHmgjWPxHOw+/EtX5XMlymMFMXjVfPqS4R1WjE3359sfzs94i7PLrXWc62JizdWm5dn/WpI++6qvJPmVflPXvXx/GfNxGPiKTEmdornIYmXxS7xkthLqwviYG3HCJ2VhinSbZH6JNVgYJq89S9dP1t4vUZ/DPVRlBnM0lSJ93/CKmQ0nbkOb/qP28f8F+T3iuefKAIvbODImbptU3HvEKFlpW5zrgIXv9F98LZua6N+OPwEWDyrFq1SNZ8gvAEcdod6HugpmNOWls05Uocsn5O66cpiUsxQ20NSUtcl12VLFrOZVWLpdtiZ0x1uHKE5QvfEp0plk/qv8RGw/bBS+fmsUtl+ThrWgZf6b8C8/UXAeIuJAAAACXBIWXMAAAsTAAALEwEAmpwYAAADrUlEQVQ4EYVUfUxTVxT/3de+wqNUJDgU+ZRiC+goKyIZy5wSSBB04WMMZ1yiy5Y4kpksWxwxRCdmxLk/JCQzS8yYMVsym6HDwDAoYHHgClhb+QhlRUQYwVrBFUof7ePdvdfMfbCYndx7cz/y+52T3zn3gFKK500TNSnSys4Yn/f+7J7BKvuw8FaMvf/hFnhdBsepjSYqkubakz1mgf8tz9oz8erhI23pqyAgMtuftqZpsu9o+7WpkuvNzsAE6yfwhOnALHLxRCOuj/I55qGiudEpyr05ic0VB4xnJJxbxirlhRDCfvfNwDFeraoSXESzTrMRx8uKkJbGw3aHImAEw4yHpl34vhV0CVgMYaquTFhQuinnaBAvR1L1Y2tWoMd/yfcEWhKqpq/vjiX5uQFMzjzC6JCI+BcJNvti0e0jaL0zTYX5JcLF0DGmK6z0K9PukaAmBwtTImyzTr8z1oM9RfFkh/YpZmdL4Oh6E+o1DfDerAB1v4WClwgKs1LJyJAHA233hbc/To2SIwmSGJdDIc77cbC8GPk5AhYHD2P4bhIqd9Rjb/ExxOU2wB7pha/7a+wMDcM7BwrBcX7ySoaK/Yukrn76hFvF6fl+Hva2GSyFD+GNffvBL+iBwDLS+TTEoRL3xs/Cqb6LFa0A9gmnb//8wQmZRGkorzeOiUIy7/EynCSzixOxNLMHOksmBiiPl3tlZxKRsgy/csOYGxERsgiY6QJjsd1O1pX3GpmafbH240ey+zK1ap9Pymp0QIEktCBEdRk5lAMU8uAwEnIZ4UIL4gJKBESCjGS1T8bVKJLswWr102kDimptv9jcdO7xPdpxUU8t56Lo6O1qKtYP0NHeanpDOptHo6l12kbPn3fTrZW1Nirh5OwGhWVvUZrOqPDlpVZc71EicVsjNuQp0Gk9jeGnp9A5eBqbNC8gXrwC6wCLCx0/weVXUXvXihDURF46m8d+3xmnVz2KWMFVxyRlkUC2b2vHukgX5jcIiFyuQnJBTNBBt/Sekh1GtGM61nR1cs6wK+Hvsv+20fpZeDTzQe/YuOZGnxfv5RdLlcxjmaFQSP4ys0NxTooUa8NRsEW7EP5YbKg4ZKyRg/jX33E2Wj9p6n9YWu1y+LeyfhANpxu87+UMERrRHvA5NrMqvLY+RbVre8IP+w9l1Ul4j0zynzZQ9lFH4mD3VIYsWl3nzabUki+mTn76szmw8CDPYnbmvft+S5YEY5+1geAH/udh9d5kMilQdvZ/+8kfxh8EsHymFKsAAAAASUVORK5CYII="));
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "defaultmilestoneicon.png")),
                            DatatypeConverter.parseBase64Binary("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAArklEQVR42mNQUVH5GyMnt+s+P/+zBwICTx/w8S0HsjMecHEZvWJg4GEgBIAG/NNQUXkO1HQbaMB/JPz1AS/vRiBOusvAwE+OARj4IR/f0ftcXP5PeXlFyDIAGd/j47v/iIfHjjgD+Pl/3efjOwF0QdwVBgaJUAYGZtwu4Of/B8TfgXj/fR6e+v8MDCzYvF7IwMCJYgDQSUcfsbKa7sehAW8gAvFjBnLBqAGjBgABAJ+4l3hcf8LlAAAAAElFTkSuQmCC"));

            // default workflow patterns
            ioService.write(ioService.get(URI.create(globalDirPath + separator + "patterns.json")),
                            "[\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-sequence\",\n" +
                                    "        \"name\": \"Sequence\",\n" +
                                    "        \"description\" : \"Sequence Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\"],\n" +
                                    "                \"xyOffset\" : [140,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [140,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-parallelsplit\",\n" +
                                    "        \"name\": \"Parallel Split\",\n" +
                                    "        \"description\" : \"Parallel Split Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#ParallelGateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\", \"4\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"B1\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,-60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"B2\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,60]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-xorsplit\",\n" +
                                    "        \"name\": \"XOR Split\",\n" +
                                    "        \"description\" : \"XOR Split Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\", \"4\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"B1\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,-60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"B2\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,60]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-exclusivechoice\",\n" +
                                    "        \"name\": \"Exclusive Choice\",\n" +
                                    "        \"description\" : \"Exclusive Choice Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\", \"4\", \"5\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,-90]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"5\",\n" +
                                    "                \"name\": \"D\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,90]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-synchronization\",\n" +
                                    "        \"name\": \"Synchronization\",\n" +
                                    "        \"description\" : \"Synchronization Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"B1\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"3\"],\n" +
                                    "                \"xyOffset\" : [0,-60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"B2\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"3\"],\n" +
                                    "                \"xyOffset\" : [0,60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#ParallelGateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\",\"2\"],\n" +
                                    "                \"children\" : [\"4\"],\n" +
                                    "                \"xyOffset\" : [120,60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"3\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-implicittermination\",\n" +
                                    "        \"name\": \"Implicit Termination\",\n" +
                                    "        \"description\" : \"Implicit Termination Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#StartNoneEvent\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"4\",\"5\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"3\"],\n" +
                                    "                \"children\" : [\"6\"],\n" +
                                    "                \"xyOffset\" : [120,-60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"5\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"3\"],\n" +
                                    "                \"children\" : [\"7\"],\n" +
                                    "                \"xyOffset\" : [120,60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"6\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#EndTerminateEvent\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"4\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"7\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#EndTerminateEvent\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"5\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-simplemerge\",\n" +
                                    "        \"name\": \"Simple Merge\",\n" +
                                    "        \"description\" : \"Simple Merge Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"4\"],\n" +
                                    "                \"xyOffset\" : [0,-60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"4\"],\n" +
                                    "                \"xyOffset\" : [0,60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\",\"2\",\"3\"],\n" +
                                    "                \"children\" : [\"5\"],\n" +
                                    "                \"xyOffset\" : [120,60]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"5\",\n" +
                                    "                \"name\": \"D\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"4\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-synchronizingmerge\",\n" +
                                    "        \"name\": \"Synchronizing Merge\",\n" +
                                    "        \"description\" : \"Synchronizing Merge Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#InclusiveGateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\",\"4\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"5\"],\n" +
                                    "                \"xyOffset\" : [120,-90]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"5\"],\n" +
                                    "                \"xyOffset\" : [120,90]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"5\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#InclusiveGateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"3\",\"4\"],\n" +
                                    "                \"children\" : [\"6\"],\n" +
                                    "                \"xyOffset\" : [120,90]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"6\",\n" +
                                    "                \"name\": \"D\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"5\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-arbitrarycycles\",\n" +
                                    "        \"name\": \"Arbitrary Cycles\",\n" +
                                    "        \"description\" : \"Arbitrary Cycles Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\",\"4\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"5\"],\n" +
                                    "                \"xyOffset\" : [0,180]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"6\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"5\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"3\"],\n" +
                                    "                \"children\" : [\"6\"],\n" +
                                    "                \"xyOffset\" : [120,-90]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"6\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"5\"],\n" +
                                    "                \"children\" : [\"7\"],\n" +
                                    "                \"xyOffset\" : [120,45]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"7\",\n" +
                                    "                \"name\": \"D\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"6\"],\n" +
                                    "                \"children\" : [\"8\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"8\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"7\"],\n" +
                                    "                \"children\" : [\"9\",\"10\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"9\",\n" +
                                    "                \"name\": \"E\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"8\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,-45]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"10\",\n" +
                                    "                \"name\": \"F\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"8\"],\n" +
                                    "                \"children\" : [\"11\"],\n" +
                                    "                \"xyOffset\" : [120,45]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"11\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"10\"],\n" +
                                    "                \"children\" : [\"3\",\"12\"],\n" +
                                    "                \"xyOffset\" : [0,90]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"12\",\n" +
                                    "                \"name\": \"G\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"11\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-miwithoutsynchronization\",\n" +
                                    "        \"name\": \"MI Without Synchronization\",\n" +
                                    "        \"description\" : \"Multiple Instance Without Synchronization Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#MultipleInstanceSubprocess\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\"],\n" +
                                    "                \"xyOffset\" : [240,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"c\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [240,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "        \"id\": \"wp-deferredchoice\",\n" +
                                    "        \"name\": \"Deferred Choice\",\n" +
                                    "        \"description\" : \"Deferred Choice Pattern\",\n" +
                                    "        \"elements\" : [\n" +
                                    "            {\n" +
                                    "                \"id\": \"1\",\n" +
                                    "                \"name\": \"A\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [],\n" +
                                    "                \"children\" : [\"2\"],\n" +
                                    "                \"xyOffset\" : [0,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"2\",\n" +
                                    "                \"name\": \"\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#EventbasedGateway\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"1\"],\n" +
                                    "                \"children\" : [\"3\",\"4\"],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"3\",\n" +
                                    "                \"name\": \"b\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#IntermediateMessageEventCatching\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"5\"],\n" +
                                    "                \"xyOffset\" : [60,-45]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\" : \"4\",\n" +
                                    "                \"name\": \"c\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#IntermediateMessageEventCatching\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\": \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"2\"],\n" +
                                    "                \"children\" : [\"6\"],\n" +
                                    "                \"xyOffset\" : [60,45]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\": \"5\",\n" +
                                    "                \"name\": \"B\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"3\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            },\n" +
                                    "            {\n" +
                                    "                \"id\": \"6\",\n" +
                                    "                \"name\": \"C\",\n" +
                                    "                \"nodetype\" : \"http://b3mn.org/stencilset/bpmn2.0#Task\",\n" +
                                    "                \"namespace\" : \"http://b3mn.org/stencilset/bpmn2.0#\",\n" +
                                    "                \"connectingType\" : \"http://b3mn.org/stencilset/bpmn2.0#SequenceFlow\",\n" +
                                    "                \"parent\" : [\"4\"],\n" +
                                    "                \"children\" : [],\n" +
                                    "                \"xyOffset\" : [120,0]\n" +
                                    "            }\n" +
                                    "        ]\n" +
                                    "    }\n" +
                                    "]");
        }

        ioService.endBatch();
    }
}

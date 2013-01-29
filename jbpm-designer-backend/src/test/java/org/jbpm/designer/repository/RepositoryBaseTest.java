package org.jbpm.designer.repository;

import java.io.File;

import org.jbpm.designer.repository.vfs.VFSFileSystemProducer;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.kie.commons.java.nio.file.FileSystem;

public class RepositoryBaseTest {

    protected static final String REPOSITORY_ROOT = (System.getProperty("java.io.tmpdir").endsWith(File.separator)
            ?System.getProperty("java.io.tmpdir"):(System.getProperty("java.io.tmpdir") + File.separator)) + "designer-repo";
    protected static final String VFS_REPOSITORY_ROOT = "default://" + REPOSITORY_ROOT;
    
    protected JbpmProfileImpl profile;
    protected FileSystem fileSystem;
    protected VFSFileSystemProducer producer;

    protected void deleteFiles(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteFiles(file);
            }
            file.delete();
        }
    }
    
}

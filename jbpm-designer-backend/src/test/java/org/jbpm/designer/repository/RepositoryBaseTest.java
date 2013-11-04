package org.jbpm.designer.repository;

import java.io.File;

import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;

public class RepositoryBaseTest {

    protected static final String REPOSITORY_ROOT = (System.getProperty("java.io.tmpdir").endsWith(File.separator)
            ?System.getProperty("java.io.tmpdir"):(System.getProperty("java.io.tmpdir") + File.separator)) + "designer-repo";
    //protected static final String VFS_REPOSITORY_ROOT = "default://" + REPOSITORY_ROOT;
    protected static final String VFS_REPOSITORY_ROOT = "file:///D:/apps/tmp/designer-repo";
    
    protected JbpmProfileImpl profile;
    protected RepositoryDescriptor descriptor;
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

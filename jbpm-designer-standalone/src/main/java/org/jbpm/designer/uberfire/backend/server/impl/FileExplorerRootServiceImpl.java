package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class FileExplorerRootServiceImpl
        implements
        FileExplorerRootService {

    protected final Set<Root> roots = new HashSet<Root>();

    @Inject
    @Named("fs")
    private ActiveFileSystems fileSystems;

    @PostConstruct
    protected void init() {
        setupGitRepos();
    }

    private void setupGitRepos() {
        final Path rootPath = fileSystems.getBootstrapFileSystem().getRootDirectories().get( 0 );
        final Root root = new Root( rootPath,
                new DefaultPlaceRequest( "RepositoryEditor" ) );

        roots.add( root );
    }

    @Override
    public Collection<Root> listRoots() {
        return unmodifiableSet( roots );
    }

    @Override
    public void addRoot(final Root root) {
        roots.add( root );
    }

    @Override
    public void removeRoot(final Root root) {
        roots.remove( root );
    }

    @Override
    public void clear() {
        roots.clear();
    }

}


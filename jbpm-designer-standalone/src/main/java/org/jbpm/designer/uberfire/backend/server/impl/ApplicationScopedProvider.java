package org.jbpm.designer.uberfire.backend.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.io.IOSearchIndex;
import org.uberfire.metadata.io.IOServiceIndexedImpl;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@ApplicationScoped
public class ApplicationScopedProvider {

    @Inject
    private IOWatchServiceNonDotImpl watchService;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private IOService ioService;
    private IOSearchService ioSearchService;

    @Inject
    @Named("luceneConfig")
    private LuceneConfig config;

    @PostConstruct
    public void setup() {

        final IOService service = new IOServiceIndexedImpl( watchService,
                                config.getIndexEngine(),
                                config.getIndexers(),
                                DublinCoreView.class,
                                VersionAttributeView.class,
                                OtherMetaView.class );


        if ( clusterServiceFactory == null ) {
            ioService = service;
        } else {
            ioService = new IOServiceClusterImpl( service,
                                        clusterServiceFactory,
                                        false );
        }
        this.ioSearchService = new IOSearchIndex(config.getSearchIndex(), ioService);
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("ioSearchStrategy")
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }
}

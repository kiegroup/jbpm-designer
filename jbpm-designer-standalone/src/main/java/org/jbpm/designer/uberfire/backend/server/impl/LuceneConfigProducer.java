package org.jbpm.designer.uberfire.backend.server.impl;

import org.apache.lucene.analysis.Analyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.metadata.engine.Indexer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static org.apache.lucene.util.Version.*;
import static org.apache.lucene.util.Version.LUCENE_40;

@ApplicationScoped
public class LuceneConfigProducer {

    @Inject
    @Any
    private Instance<Indexer> indexers;

    private LuceneConfig config;

    @PostConstruct
    public void setup() {
        final Set<Indexer> indexers = getIndexers();
        final Map<String, Analyzer> analyzers = getAnalyzers();
        this.config = new LuceneConfigBuilder().withInMemoryMetaModelStore()
                .usingIndexers( indexers )
                .usingAnalyzers( analyzers )
                .useDirectoryBasedIndex()
                .useNIODirectory()
                .build();
    }

    @Produces
    @Named("luceneConfig")
    public LuceneConfig configProducer() {
        return this.config;
    }

    private Set<Indexer> getIndexers() {
        if ( indexers == null ) {
            return Collections.emptySet();
        }
        final Set<Indexer> result = new HashSet<Indexer>();
        for ( Indexer indexer : indexers ) {
            result.add( indexer );
        }
        return result;
    }

    private Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleIndexTerm.TERM,
                    new RuleAttributeNameAnalyzer( LUCENE_40 ) );
        }};
    }

}

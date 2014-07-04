package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.lucene.analysis.Analyzer;
import org.kie.uberfire.metadata.backend.lucene.LuceneConfig;
import org.kie.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.kie.uberfire.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;

import static org.apache.lucene.util.Version.*;

@ApplicationScoped
public class LuceneConfigProducer {

    private LuceneConfig config;

    @PostConstruct
    public void setup() {
        final Map<String, Analyzer> analyzers = getAnalyzers();
        this.config = new LuceneConfigBuilder().withInMemoryMetaModelStore()
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

    private Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer( LUCENE_40 ) );
            put( ProjectRootPathIndexTerm.TERM,
                 new FilenameAnalyzer( LUCENE_40 ) );
        }};
    }

}

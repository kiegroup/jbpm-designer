package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.lucene.analysis.Analyzer;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.FieldTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeInterfaceIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeNameIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeParentIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.FullyQualifiedClassNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.TypeIndexTerm;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;

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
            put( RuleAttributeIndexTerm.TERM,
                    new RuleAttributeNameAnalyzer( LUCENE_40 ) );
            put( RuleAttributeValueIndexTerm.TERM,
                    new RuleAttributeNameAnalyzer( LUCENE_40 ) );

            put( ProjectRootPathIndexTerm.TERM,
                    new FilenameAnalyzer( LUCENE_40 ) );

            put( PackageNameIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
            put( FieldTypeIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
            put( JavaTypeIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
            put( JavaTypeInterfaceIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
            put( JavaTypeNameIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
            put( JavaTypeParentIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
            put( TypeIndexTerm.TERM,
                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
        }};
    }

}
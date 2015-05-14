package org.jbpm.designer.uberfire.backend.server.impl;

//@ApplicationScoped
public class LuceneConfigProducer {

//    private LuceneConfig config;
//
//    @PostConstruct
//    public void setup() {
//        final Map<String, Analyzer> analyzers = getAnalyzers();
//        this.config = new LuceneConfigBuilder().withInMemoryMetaModelStore()
//                .usingAnalyzers( analyzers )
//                .useDirectoryBasedIndex()
//                .useNIODirectory()
//                .build();
//    }
//
//    @Produces
//    @Named("luceneConfig")
//    public LuceneConfig configProducer() {
//        return this.config;
//    }
//
//    private Map<String, Analyzer> getAnalyzers() {
//        return new HashMap<String, Analyzer>() {{
//            put( RuleIndexTerm.TERM,
//                 new RuleAttributeNameAnalyzer( LUCENE_40 ) );
//            put( RuleAttributeIndexTerm.TERM,
//                    new RuleAttributeNameAnalyzer( LUCENE_40 ) );
//            put( RuleAttributeValueIndexTerm.TERM,
//                    new RuleAttributeNameAnalyzer( LUCENE_40 ) );
//
//            put( ProjectRootPathIndexTerm.TERM,
//                    new FilenameAnalyzer( LUCENE_40 ) );
//
//            put( PackageNameIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//            put( FieldTypeIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//            put( JavaTypeIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//            put( JavaTypeInterfaceIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//            put( JavaTypeNameIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//            put( JavaTypeParentIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//            put( TypeIndexTerm.TERM,
//                    new FullyQualifiedClassNameAnalyzer( LUCENE_40 ) );
//        }};
//    }

}
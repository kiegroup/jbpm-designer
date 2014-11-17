package org.jbpm.designer.query;

import org.apache.lucene.search.Query;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.QueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.TypeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@ApplicationScoped
public class FindDataTypesQuery implements NamedQuery {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private DataTypesResponseBuilder responseBuilder = new DataTypesResponseBuilder();

    @Override
    public String getName() {
        return "DesignerFindTypesQuery";
    }

    @Override
    public Set<IndexTerm> getTerms() {
        return new HashSet<IndexTerm>() {{
            add( new TypeIndexTerm() );
        }};
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms,
                          boolean useWildcards ) {
        if ( terms.size() != 1 ) {
            throw new IllegalArgumentException( "Required term has not been provided. Require '" + TypeIndexTerm.TERM + "'." );
        }
        final Map<String, ValueIndexTerm> normalizedTerms = normalizeTerms( terms );
        final ValueIndexTerm typeTerm = normalizedTerms.get( TypeIndexTerm.TERM );
        if ( typeTerm == null ) {
            throw new IllegalArgumentException( "Required term has not been provided. Require '" + TypeIndexTerm.TERM + "'." );
        }

        final QueryBuilder builder = new QueryBuilder();
        if ( useWildcards ) {
            builder.useWildcards();
        }
        builder.addTerm( typeTerm );
        return builder.build();
    }

    private Map<String, ValueIndexTerm> normalizeTerms( final Set<ValueIndexTerm> terms ) {
        final Map<String, ValueIndexTerm> normalizedTerms = new HashMap<String, ValueIndexTerm>();
        for ( ValueIndexTerm term : terms ) {
            normalizedTerms.put( term.getTerm(),
                    term );
        }
        return normalizedTerms;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    private static class DataTypesResponseBuilder implements ResponseBuilder {
        private IOService ioService;

        public void setIOService(IOService ioService) {
            this.ioService = ioService;
        }

        @Override
        public PageResponse<RefactoringPageRow> buildResponse( final int pageSize,
                                                               final int startRow,
                                                               final List<KObject> kObjects ) {
            final int hits = kObjects.size();
            final PageResponse<RefactoringPageRow> response = new PageResponse<RefactoringPageRow>();
            final List<RefactoringPageRow> result = buildResponse( kObjects );
            response.setTotalRowSize( hits );
            response.setPageRowList( result );
            response.setTotalRowSizeExact( true );
            response.setStartRowIndex( startRow );
            response.setLastPage( ( pageSize * startRow + 2 ) >= hits );

            return response;
        }

        @Override
        public List<RefactoringPageRow> buildResponse( final List<KObject> kObjects ) {
            final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>( kObjects.size() );
            final Map<String, String> uniqueDataTypeNames = new HashMap<String, String>();
            for ( final KObject kObject : kObjects ) {
                final Map<String, String> dataTypeNames = getDataTypeNamesFromKObject(kObject);
                uniqueDataTypeNames.putAll(dataTypeNames);
            }

            Iterator<Map.Entry<String, String>> dataTypesIterator = uniqueDataTypeNames.entrySet().iterator();
            while(dataTypesIterator.hasNext()) {
                Map.Entry<String, String> entry = dataTypesIterator.next();
                final RefactoringStringPageRow row = new RefactoringStringPageRow();
                row.setValue(entry.getKey());
                result.add(row);
            }
            return result;
        }

        private Map<String, String> getDataTypeNamesFromKObject( final KObject kObject ) {
            final Map<String, String> dataTypeNames = new HashMap<String, String>();
            if ( kObject == null ) {
                return dataTypeNames;
            }
            for ( KProperty property : kObject.getProperties() ) {
                if ( property.getName().equals( "java_type_name" ) ) {
                    if(!dataTypeNames.containsKey(property.getValue())) {
                        dataTypeNames.put(property.getValue().toString(), property.getValue().toString());
                    }
                }
            }
            return dataTypeNames;
        }

    }

}

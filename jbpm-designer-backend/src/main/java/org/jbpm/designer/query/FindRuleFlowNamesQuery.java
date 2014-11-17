package org.jbpm.designer.query;

import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRuleAttributesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.*;

@ApplicationScoped
public class FindRuleFlowNamesQuery extends FindRuleAttributesQuery {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private RuleFlorNamesResponseBuilder responseBuilder = new RuleFlorNamesResponseBuilder();

    @Override
    public String getName() {
        return "FindRuleFlowNamesQuery";
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        responseBuilder.setIOService(ioService);
        return responseBuilder;
    }

    private static class RuleFlorNamesResponseBuilder implements ResponseBuilder {
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
            final Map<String, List<String>> uniqueRuleFlowNames = new HashMap<String, List<String>>();
            for ( final KObject kObject : kObjects ) {
                final Map<String, List<String>> ruleFlowGroupNames = getRuleFlowGroupNamesNamesFromKObject(kObject);
                uniqueRuleFlowNames.putAll(ruleFlowGroupNames);
            }

            Iterator<Map.Entry<String, List<String>>> ruleFlorGroupsIterator = uniqueRuleFlowNames.entrySet().iterator();
            while(ruleFlorGroupsIterator.hasNext()) {
                Map.Entry<String, List<String>> entry = ruleFlorGroupsIterator.next();
                final RefactoringStringPageRow row = new RefactoringStringPageRow();
                row.setValue(entry.getKey() + "||" + entry.getValue().toString());
                result.add(row);
            }
            return result;
        }

        private Map<String, List<String>> getRuleFlowGroupNamesNamesFromKObject( final KObject kObject ) {
            final Map<String, List<String>> ruleFlowGroupNames = new HashMap<String, List<String>>();
            if ( kObject == null ) {
                return ruleFlowGroupNames;
            }
            for ( KProperty property : kObject.getProperties() ) {
                if ( property.getName().equals( "rule_attribute:ruleflow-group:rule_attribute_value" ) ) {
                    if(ruleFlowGroupNames.containsKey(property.getValue().toString())) {
                        final Path path = Paths.convert(ioService.get(URI.create(kObject.getKey())));
                        ruleFlowGroupNames.get(property.getValue().toString()).add(path.getFileName() + "^^" + path.toURI());
                    } else {
                        final Path path = Paths.convert(ioService.get(URI.create(kObject.getKey())));
                        List<String> pathsList = new ArrayList<String>();
                        pathsList.add(path.getFileName() + "^^" + path.toURI());
                        ruleFlowGroupNames.put(property.getValue().toString(), pathsList);
                    }
                }
            }
            return ruleFlowGroupNames;
        }

    }
}

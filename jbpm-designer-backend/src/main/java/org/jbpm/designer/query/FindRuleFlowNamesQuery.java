/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRuleAttributesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FindRuleFlowNamesQuery extends FindRuleAttributesQuery {

    private static final Logger logger = LoggerFactory.getLogger(FindRuleFlowNamesQuery.class);

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
                for(String rkey : ruleFlowGroupNames.keySet()) {
                    if(uniqueRuleFlowNames.containsKey(rkey)) {
                        List<String> rvalList = ruleFlowGroupNames.get(rkey);
                        for(String rvalKey : rvalList) {
                            uniqueRuleFlowNames.get(rkey).add(rvalKey);
                        }
                    } else {
                        uniqueRuleFlowNames.put(rkey, ruleFlowGroupNames.get(rkey));
                    }
                }
            }

            for(String uniqueKey : uniqueRuleFlowNames.keySet()) {
                List<String> uniqueValueList = uniqueRuleFlowNames.get(uniqueKey);
                String fstr = "";
                for(String val : uniqueValueList) {
                    fstr += val + "<<";
                }

                fstr = fstr.substring(0, fstr.length() - 2);

                final RefactoringStringPageRow row = new RefactoringStringPageRow();
                row.setValue(uniqueKey + "||" + fstr);
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
                if ( property.getName().equals(  RuleAttributeIndexTerm.TERM + ":" + "ruleflow-group" + ":" + RuleAttributeValueIndexTerm.TERM) ) {
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

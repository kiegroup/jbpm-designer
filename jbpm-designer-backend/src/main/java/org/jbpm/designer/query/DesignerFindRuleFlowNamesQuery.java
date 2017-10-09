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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.lucene.search.Query;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.AbstractFindQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.SharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueBranchNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringStringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;

@ApplicationScoped
public class DesignerFindRuleFlowNamesQuery extends AbstractFindQuery implements NamedQuery {

    private static final Logger logger = LoggerFactory.getLogger(DesignerFindRuleFlowNamesQuery.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private RuleFlowNamesResponseBuilder responseBuilder = new RuleFlowNamesResponseBuilder();

    public static final String NAME = DesignerFindRuleFlowNamesQuery.class.getSimpleName();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        responseBuilder.setIOService(ioService);
        return responseBuilder;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.query.IndexQuery#toQuery(java.util.Set)
     */
    @Override
    public Query toQuery(Set<ValueIndexTerm> terms) {
        return buildFromSingleTerm(terms);
    }

    private static final ValueSharedPartIndexTerm ruleFlowTerm = new ValueSharedPartIndexTerm("not-used",
                                                                                              PartType.RULEFLOW_GROUP);

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery#validateTerms(java.util.Set)
     */
    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        checkNotNullAndNotEmpty(queryTerms);

        checkInvalidAndRequiredTerms(queryTerms,
                                     NAME,
                                     new String[]{
                                             null, null,  // not required
                                             ruleFlowTerm.getTerm()
                                     },
                                     (t) -> (t instanceof ValueProjectNameIndexTerm),
                                     (t) -> (t instanceof ValueBranchNameIndexTerm),
                                     (t) -> (t.getTerm().equals(ruleFlowTerm.getTerm())));
    }

    private static class RuleFlowNamesResponseBuilder implements ResponseBuilder {

        private IOService ioService;

        public void setIOService(IOService ioService) {
            this.ioService = ioService;
        }

        @Override
        public PageResponse<RefactoringPageRow> buildResponse(final int pageSize,
                                                              final int startRow,
                                                              final List<KObject> kObjects) {
            final int hits = kObjects.size();
            final PageResponse<RefactoringPageRow> response = new PageResponse<RefactoringPageRow>();
            final List<RefactoringPageRow> result = buildResponse(kObjects);
            response.setTotalRowSize(hits);
            response.setPageRowList(result);
            response.setTotalRowSizeExact(true);
            response.setStartRowIndex(startRow);
            response.setLastPage((pageSize * startRow + 2) >= hits);

            return response;
        }

        @Override
        public List<RefactoringPageRow> buildResponse(final List<KObject> kObjects) {
            final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>(kObjects.size());
            final Map<String, List<String>> uniqueRuleFlowNames = new HashMap<String, List<String>>();
            for (final KObject kObject : kObjects) {
                final Map<String, List<String>> ruleFlowGroupNames = getRuleFlowGroupNamesNamesFromKObject(kObject);
                for (String rkey : ruleFlowGroupNames.keySet()) {
                    if (uniqueRuleFlowNames.containsKey(rkey)) {
                        List<String> rvalList = ruleFlowGroupNames.get(rkey);
                        for (String rvalKey : rvalList) {
                            uniqueRuleFlowNames.get(rkey).add(rvalKey);
                        }
                    } else {
                        uniqueRuleFlowNames.put(rkey,
                                                ruleFlowGroupNames.get(rkey));
                    }
                }
            }

            for (String uniqueKey : uniqueRuleFlowNames.keySet()) {
                List<String> uniqueValueList = uniqueRuleFlowNames.get(uniqueKey);
                String fstr = "";
                for (String val : uniqueValueList) {
                    fstr += val + "<<";
                }

                fstr = fstr.substring(0,
                                      fstr.length() - 2);

                final RefactoringStringPageRow row = new RefactoringStringPageRow();
                row.setValue(uniqueKey + "||" + fstr);
                result.add(row);
            }

            return result;
        }

        private Map<String, List<String>> getRuleFlowGroupNamesNamesFromKObject(final KObject kObject) {
            final Map<String, List<String>> ruleFlowGroupNames = new HashMap<String, List<String>>();
            if (kObject == null) {
                return ruleFlowGroupNames;
            }
            for (KProperty property : kObject.getProperties()) {
                if (property.getName().equals(SharedPartIndexTerm.TERM + ":" + PartType.RULEFLOW_GROUP.toString())) {
                    if (ruleFlowGroupNames.containsKey(property.getValue().toString())) {
                        final Path path = Paths.convert(ioService.get(URI.create(kObject.getKey())));
                        ruleFlowGroupNames.get(property.getValue().toString()).add(path.getFileName() + "^^" + path.toURI());
                    } else {
                        final Path path = Paths.convert(ioService.get(URI.create(kObject.getKey())));
                        List<String> pathsList = new ArrayList<String>();
                        pathsList.add(path.getFileName() + "^^" + path.toURI());
                        ruleFlowGroupNames.put(property.getValue().toString(),
                                               pathsList);
                    }
                }
            }

            return ruleFlowGroupNames;
        }
    }
}

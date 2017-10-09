/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringMapPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;

@ApplicationScoped
public class FindBpmn2ProcessIdsQuery extends FindResourcesQuery implements NamedQuery {

    public static final String NAME = FindBpmn2ProcessIdsQuery.class.getSimpleName();

    @Inject
    private Bpmn2ProcessIdsResponseBuilder responseBuilder;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        responseBuilder.setIOService(ioService);
        return responseBuilder;
    }

    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        this.checkInvalidAndRequiredTerms(queryTerms,
                                          NAME,
                                          new String[]{ValueResourceIndexTerm.class.getSimpleName()},
                                          new Predicate[]{t -> {
                                              if (!(t instanceof ValueResourceIndexTerm)) {
                                                  return false;
                                              } else {
                                                  return ((ValueResourceIndexTerm) t).getTerm().equals(ResourceType.BPMN2.toString());
                                              }
                                          }});
        this.checkTermsSize(1,
                            queryTerms);
    }

    public static class Bpmn2ProcessIdsResponseBuilder implements ResponseBuilder {

        private IOService ioService;

        public Bpmn2ProcessIdsResponseBuilder() {
        }

        public Bpmn2ProcessIdsResponseBuilder(IOService ioService) {
            this.ioService = ioService;
        }

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
            for (final KObject kObject : kObjects) {
                for (KProperty property : kObject.getProperties()) {
                    if (property.getName().equals("bpmn2id")) {
                        String bpmnProcessId = (String) property.getValue();
                        final Path path = Paths.convert(ioService.get(URI.create(kObject.getKey())));
                        Map<String, Path> map = new HashMap<String, Path>();
                        map.put(bpmnProcessId,
                                path);
                        RefactoringMapPageRow row = new RefactoringMapPageRow();
                        row.setValue(map);
                        result.add(row);
                    }
                }
            }
            return result;
        }
    }
}

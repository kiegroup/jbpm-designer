/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.designer.server.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.jbpm.designer.query.FindBpmn2ProcessIdsQuery;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BPMN2FileIndexerIndexingTest extends BaseIndexingTest<Bpmn2TypeDefinition> {

    private final static List<String> PROCESS_IDS = Arrays.asList(new String[]{"hiring", "ParentProcess", "SubProcess", "multiple-rule-tasks", "org.jbpm.signal", "org.jbpm.broken"});

    private final static String[] BPMN_FILES = {
            "callActivity.bpmn2",
            "callActivityByName.bpmn2",
            "callActivityCalledSubProcess.bpmn2",
            "hiring.bpmn2",
            "multipleRuleTasksWithDataInput.bpmn2",
            "signal.bpmn2",
            "brokenSignal.bpmn2",
    };

    private static final String DEPLOYMENT_ID = "org.kjar:test:1.0";

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindResourcesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindAllChangeImpactQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindBpmn2ProcessIdsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new FindBpmn2ProcessIdsQuery.Bpmn2ProcessIdsResponseBuilder(ioService());
                }
            });
        }};
    }

    private static final long WAIT_TIME_MILLIS = 2000;

    private static final int MAX_WAIT_TIMES = 8;

    @Test
    public void testBpmnIndexing() throws Exception {

        List<Path> pathList = new ArrayList<>();
        for (int i = 0; i < BPMN_FILES.length; ++i) {
            String bpmnFile = BPMN_FILES[i];
            if (bpmnFile.endsWith("bpmn2")) {
                Path path = basePath.resolve(bpmnFile);
                pathList.add(path);
                String bpmnStr = loadText(bpmnFile);
                ioService().write(path,
                                  bpmnStr);
            }
        }
        Path[] paths = pathList.toArray(new Path[pathList.size()]);

        {
            PageResponse<RefactoringPageRow> response = null;
            try {
                for (int i = 0; i < MAX_WAIT_TIMES; i++) {
                    Thread.sleep(WAIT_TIME_MILLIS);
                    response = queryBPMN2Resources();
                    if (response != null && response.getPageRowList() != null && response.getPageRowList().size() >= paths.length) {
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }

            assertNotNull(response);
            assertEquals(paths.length,
                         response.getPageRowList().size());
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("*",
                                          PartType.RULEFLOW_GROUP,
                                          ValueIndexTerm.TermSearchType.WILDCARD)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(1,
                             response.size());
                assertResponseContains(response,
                                       paths[4]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("MySignal",
                                          PartType.SIGNAL)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(1,
                             response.size());
                assertResponseContains(response,
                                       paths[5]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("BrokenSignal",
                                          PartType.SIGNAL)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(1,
                             response.size());
                assertResponseContains(response,
                                       paths[6]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("name",
                                          PartType.GLOBAL)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList(request);
                assertNotNull(response);
                assertEquals(2,
                             response.size());
                assertResponseContains(response,
                                       paths[5]);
                assertResponseContains(response,
                                       paths[6]);
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
        {

            final Set<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>() {{
                add(new ValueResourceIndexTerm("*",
                                               ResourceType.BPMN2,
                                               ValueIndexTerm.TermSearchType.WILDCARD));
            }};
            try {
                List<RefactoringPageRow> response = service.query(
                        FindBpmn2ProcessIdsQuery.NAME,
                        queryTerms);
                assertNotNull(response);
                assertEquals(paths.length,
                             response.size());

                for (String expectedId : PROCESS_IDS) {
                    boolean foundId = false;
                    for (RefactoringPageRow row : response) {
                        Map<String, org.uberfire.backend.vfs.Path> mapRow = (Map<String, org.uberfire.backend.vfs.Path>) row.getValue();
                        for (String rKey : mapRow.keySet()) {
                            assertTrue(PROCESS_IDS.contains(rKey));
                            foundId = true;
                        }
                    }
                    if (!foundId) {
                        fail("Process with ID <" + expectedId + " not found in results for " + FindBpmn2ProcessIdsQuery.NAME);
                    }
                }
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    private PageResponse<RefactoringPageRow> queryBPMN2Resources() throws IllegalArgumentException {
        final RefactoringPageRequest request = new RefactoringPageRequest(FindResourcesQuery.NAME,
                                                                          new HashSet<ValueIndexTerm>() {{
                                                                              add(new ValueResourceIndexTerm("*",
                                                                                                             ResourceType.BPMN2,
                                                                                                             ValueIndexTerm.TermSearchType.WILDCARD));
                                                                          }},
                                                                          0,
                                                                          10);

        return service.query(request);
    }

    @Override
    protected KieProjectService getProjectService() {
        final org.uberfire.backend.vfs.Path mockRoot = mock(org.uberfire.backend.vfs.Path.class);
        when(mockRoot.toURI()).thenReturn(TEST_PROJECT_ROOT);

        final KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(mockRoot);
        when(mockProject.getProjectName()).thenReturn(TEST_PROJECT_NAME);

        POM mockPom = mock(POM.class);
        when(mockProject.getPom()).thenReturn(mockPom);
        GAV mockGAV = mock(GAV.class);
        when(mockPom.getGav()).thenReturn(mockGAV);
        when(mockGAV.toString()).thenReturn(DEPLOYMENT_ID);

        final Package mockPackage = mock(Package.class);
        when(mockPackage.getPackageName()).thenReturn(TEST_PACKAGE_NAME);

        final KieProjectService mockProjectService = mock(KieProjectService.class);
        when(mockProjectService.resolveProject(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockProject);
        when(mockProjectService.resolvePackage(any(org.uberfire.backend.vfs.Path.class))).thenReturn(mockPackage);

        return mockProjectService;
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestBPMN2FileIndexer();
    }

    @Override
    protected Bpmn2TypeDefinition getResourceTypeDefinition() {
        return new Bpmn2TypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}

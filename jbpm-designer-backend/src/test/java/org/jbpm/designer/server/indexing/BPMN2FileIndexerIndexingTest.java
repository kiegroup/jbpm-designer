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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
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
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
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

public class BPMN2FileIndexerIndexingTest extends BaseIndexingTest<Bpmn2TypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add( new FindResourcesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            });
            add( new FindAllChangeImpactQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            });
        }};
    }

    private static final String DEPLOYMENT_ID = "org.kjar:test:1.0";

    @Test
    public void testBPMN2Indexing() throws Exception {

        String [] bpmn2Files = {
                "callActivity.bpmn2",
                "callActivityByName.bpmn2",
                "callActivityCalledSubProcess.bpmn2",
                "hiring.bpmn2",
                "multipleRuleTasksWithDataInput.bpmn2",
                "signal.bpmn2",
                "brokenSignal.bpmn2",
        };

        List<Path> pathList = new ArrayList<>();
        for( int i = 0; i < bpmn2Files.length; ++i ) {
            String bpmn2File = bpmn2Files[i];
           if( bpmn2File.endsWith("bpmn2") ) {
               Path path = basePath.resolve( bpmn2File );
               pathList.add( path );
               String bpmn2Str = loadText( bpmn2File );
               ioService().write( path, bpmn2Str );
           }
        }
        Path [] path = pathList.toArray(new Path[pathList.size()]);

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcesQuery.NAME,
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueResourceIndexTerm( "*", ResourceType.BPMN2, TermSearchType.WILDCARD ) );
                                                                               }},
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( path.length, response.getPageRowList().size() );
            } catch( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("*", PartType.RULEFLOW_GROUP, TermSearchType.WILDCARD)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList( request );
                assertNotNull( response );
                assertEquals( 1, response.size() );
                assertResponseContains(response, path[4]);
            } catch( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("MySignal", PartType.SIGNAL)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList( request );
                assertNotNull( response );
                assertEquals( 1, response.size() );
                assertResponseContains(response, path[5]);
            } catch( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }

        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("BrokenSignal", PartType.SIGNAL)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList( request );
                assertNotNull( response );
                assertEquals( 1, response.size() );
                assertResponseContains(response, path[6]);
            } catch( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
        {
            QueryOperationRequest request = QueryOperationRequest
                    .referencesSharedPart("name", PartType.GLOBAL)
                    .inAllProjects().onAllBranches();

            try {
                final List<RefactoringPageRow> response = service.queryToList( request );
                assertNotNull( response );
                assertEquals( 2, response.size() );
                assertResponseContains(response, path[5]);
                assertResponseContains(response, path[6]);
            } catch( IllegalArgumentException e ) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    @Override
    protected KieProjectService getProjectService() {
        final org.uberfire.backend.vfs.Path mockRoot = mock( org.uberfire.backend.vfs.Path.class );
        when( mockRoot.toURI() ).thenReturn( TEST_PROJECT_ROOT );

        final KieProject mockProject = mock( KieProject.class );
        when( mockProject.getRootPath() ).thenReturn( mockRoot );
        when( mockProject.getProjectName() ).thenReturn( TEST_PROJECT_NAME );

        POM mockPom = mock(POM.class);
        when(mockProject.getPom()).thenReturn(mockPom);
        GAV mockGAV = mock(GAV.class);
        when(mockPom.getGav()).thenReturn(mockGAV);
        when(mockGAV.toString()).thenReturn(DEPLOYMENT_ID);

        final Package mockPackage = mock( Package.class );
        when( mockPackage.getPackageName() ).thenReturn( TEST_PACKAGE_NAME );

        final KieProjectService mockProjectService = mock( KieProjectService.class );
        when( mockProjectService.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( mockProject );
        when( mockProjectService.resolvePackage( any( org.uberfire.backend.vfs.Path.class ) ) ).thenReturn( mockPackage );

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

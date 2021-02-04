/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.server.indexing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.xml.SemanticModules;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.designer.server.indexing.bpmn2.DesignerProcessDataEventListener;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.kie.api.definition.process.Process;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class BPMN2FileIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(BPMN2FileIndexer.class);

    private static final SemanticModules modules = new SemanticModules();

    static {
        modules.addSemanticModule(new BPMNSemanticModule());
        modules.addSemanticModule(new BPMNDISemanticModule());
        modules.addSemanticModule(new BPMNExtensionsSemanticModule());
    }

    @Inject
    protected Bpmn2TypeDefinition bpmn2TypeDefinition;

    @Inject
    protected ModuleClassLoaderHelper classLoaderHelper;

    @Override
    public boolean supportsPath(Path path) {
        return bpmn2TypeDefinition.accept(Paths.convert(path));
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer#fillIndexBuilder(org.uberfire.java.nio.file.Path)
     */
    @Override
    protected DefaultIndexBuilder fillIndexBuilder(Path path) throws Exception {
        final KieModule module = moduleService.resolveModule(Paths.convert(path));
        if (module == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": module could not be resolved.");
            return null;
        }

        // responsible for basic index info: project name, branch, etc
        final DefaultIndexBuilder builder = getIndexBuilder(path,
                                                            module);
        String bpmnStr = ioService.readAllString(path);
        ClassLoader projectClassLoader = getProjectClassLoader(module);

        XmlProcessReader xmlReader = new XmlProcessReader(modules,
                                                          projectClassLoader);
        List<Process> processes = xmlReader.read(new StringReader(bpmnStr));
        if (processes != null) {
            try {

                // create process data list and validate each
                List<DesignerProcessDataEventListener> processDataList = new ArrayList<>();
                processes.forEach(
                        process -> {
                            DesignerProcessDataEventListener processData
                                    = (DesignerProcessDataEventListener) process.getMetaData().get(DesignerProcessDataEventListener.NAME);
                            processDataList.add(processData);

                            logger.info("Validating process with id: " + process.getId());
                            ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess((RuleFlowProcess) process);
                            for (ProcessValidationError error : errors) {
                                logger.warn("Process validation error: " + error.getMessage());
                            }

                            xmlReader.getProcessBuildData().onBuildComplete(process);
                        });

                for (DesignerProcessDataEventListener processData : processDataList) {
                    addReferencedResourcesToIndexBuilder(builder,
                                                         processData);
                    builder.setPackageName(processData.getProcess().getPackageName());
                }
            } catch (Throwable t) {
                logger.warn("Exception during indexing: " + t.getMessage());
            }
        } else {
            logger.warn("No process was found in file: " + path.toUri());
        }

        return builder;
    }

    // Protected method for testing
    protected ClassLoader getProjectClassLoader(final KieModule module) {
        return classLoaderHelper.getModuleClassLoader(module);
    }

    protected DefaultIndexBuilder getIndexBuilder(Path path,
                                                  Module module) {
        final Package pkg = moduleService.resolvePackage(Paths.convert(path));
        if (pkg == null) {
            logger.error("Unable to index " + path.toUri().toString() + ": package could not be resolved.");
            return null;
        }

        // responsible for basic index info: project name, branch, etc
        return new DefaultIndexBuilder(Paths.convert(path).getFileName(),
                                       module,
                                       pkg);
    }
}

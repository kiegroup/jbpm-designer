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
package org.jbpm.designer.server;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import bpsim.impl.BpsimFactoryImpl;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.helper.CopyHelper;
import org.uberfire.io.IOService;

/**
 * CopyHelper for Business Processes
 */
@ApplicationScoped
public class BusinessProcessCopyHelper implements CopyHelper {

    private IOService ioService;
    private CommentedOptionFactory commentedOptionFactory;
    private Bpmn2TypeDefinition bpmn2ResourceType;

    private static Logger logger = LoggerFactory.getLogger(BusinessProcessCopyHelper.class);

    public BusinessProcessCopyHelper() {
    }

    @Inject
    public BusinessProcessCopyHelper(final @Named("ioStrategy") IOService ioService,
                                     final Bpmn2TypeDefinition bpmn2ResourceType,
                                     final CommentedOptionFactory commentedOptionFactory) {
        this.ioService = ioService;
        this.bpmn2ResourceType = bpmn2ResourceType;
        this.commentedOptionFactory = commentedOptionFactory;
    }

    @Override
    public boolean supports(final Path destination) {
        return (bpmn2ResourceType.accept(destination));
    }

    @Override
    public void postProcess(final Path source,
                            final Path destination) {
        //Load existing file
        final org.uberfire.java.nio.file.Path _destination = Paths.convert(destination);
        final String processSource = ioService.readAllString(_destination);

        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        try {
            Definitions def = new JbpmProfileImpl().getDefinitions(processSource);
            Process process = getRootProcess(def);

            String destinationPkg = "";
            if (!"/".equals(_destination.getParent().toString())) {
                String[] pathParts = _destination.getParent().toString().split("/");
                destinationPkg = pathParts[1];
            }

            String destinationID = destinationPkg + "." + destination.getFileName().toString().substring(0,
                                                                                                         destination.getFileName().length() - 6);
            process.setId(Utils.toBPMNIdentifier(destinationID));

            ResourceSet rSet = new ResourceSetImpl();
            rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2",
                                                                             new JBPMBpmn2ResourceFactoryImpl());
            JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
            rSet.getResources().add(bpmn2resource);
            bpmn2resource.getContents().add(def);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bpmn2resource.save(outputStream,
                               new HashMap<Object, Object>());
            String destinationBpmn2 = outputStream.toString();

            if (destinationBpmn2 != null) {
                ioService.write(_destination,
                                destinationBpmn2,
                                commentedOptionFactory.makeCommentedOption("File [" + source.toURI() + "] copied to [" + destination.toURI() + "]."));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public Process getRootProcess(Definitions def) {
        for (RootElement nextRootElement : def.getRootElements()) {
            if (nextRootElement instanceof Process) {
                return (Process) nextRootElement;
            }
        }
        return null;
    }
}

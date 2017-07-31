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
package org.jbpm.designer.web.repository.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfile.IDiagramMarshaller;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Antoine Toulme
 *         a simple implementation of the UUID repository storing files directly inside the public.
 *         <p>
 *         Convenient for development.
 */
public class UUIDBasedFileRepository implements IUUIDBasedRepository {

    private static final Logger _logger = LoggerFactory.getLogger(UUIDBasedFileRepository.class);

    /**
     * the path to the repository inside the servlet.
     */
    private final static String REPOSITORY_PATH = "repository";

    private String _repositoryPath;

    public void configure(HttpServlet servlet) {
        _repositoryPath = servlet.getServletContext().getRealPath("/" + REPOSITORY_PATH);
    }

    public byte[] load(HttpServletRequest req,
                       String uuid,
                       IDiagramProfile profile,
                       ServletContext servletContext) throws Exception {

        String filename = _repositoryPath + "/" + uuid + ".json";
        if (!new File(filename).exists()) {
            return new byte[0]; // then return nothing.
        }
        InputStream input = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            input = new FileInputStream(filename);
            byte[] buffer = new byte[4096];
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer,
                             0,
                             read);
            }
        } catch (FileNotFoundException e) {
            //unlikely since we just checked.
            _logger.error(e.getMessage(),
                          e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            _logger.error(e.getMessage(),
                          e);
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
        }

        return output.toByteArray();
    }

    public void save(HttpServletRequest req,
                     String uuid,
                     String json,
                     String svg,
                     IDiagramProfile profile,
                     Boolean autosave) {
        String ext = profile.getSerializedModelExtension();
        String preProcessingParam = req.getParameter("pp");
        String model = "";
        try {
            IDiagramMarshaller marshaller = profile.createMarshaller();
            model = marshaller.parseModel(json,
                                          preProcessingParam);
        } catch (Exception e) {
            _logger.error(e.getMessage(),
                          e);
        }
        writeFile(model,
                  _repositoryPath + "/" + uuid + "." + ext);
        writeFile(json,
                  _repositoryPath + "/" + uuid + ".json");
        if (!autosave) {
            writeFile(svg,
                      _repositoryPath + "/" + uuid + ".svg");
        }
    }

    private static void writeFile(String contents,
                                  String filename) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write(contents);
        } catch (IOException e) {
            _logger.error(e.getMessage(),
                          e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public String toXML(String json,
                        IDiagramProfile profile,
                        String preProcessingData) throws Exception {
        return profile.createMarshaller().parseModel(json,
                                                     preProcessingData);
    }
}

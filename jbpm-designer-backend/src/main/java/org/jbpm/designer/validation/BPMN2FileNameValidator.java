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

package org.jbpm.designer.validation;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.FileNameValidator;
import org.uberfire.ext.editor.commons.backend.validation.ValidationUtils;

/**
 * Validator of BPMN2 process file names
 */
@ApplicationScoped
public class BPMN2FileNameValidator implements FileNameValidator {

    private static List<String> EXTRA_INVALID_FILENAME_CHARS = Arrays.asList(new String[]{"+"});

    @Inject
    private Bpmn2TypeDefinition resourceType;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean accept(final String fileName) {
        return fileName.endsWith("." + resourceType.getSuffix());
    }

    @Override
    public boolean accept(final Path path) {
        return resourceType.accept(path);
    }

    @Override
    public boolean isValid(final String value) {
        if (!(processAssetFileNameValid(value))) {
            return false;
        }
        return ValidationUtils.isFileName(value);
    }

    private boolean processAssetFileNameValid(String str) {
        for (String item : EXTRA_INVALID_FILENAME_CHARS) {
            if (str.contains(item)) {
                return false;
            }
        }
        return true;
    }
}

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
package org.jbpm.designer.web.preference;

/**
 * @author xu
 * @author Antoine Toulme
 *         <p>
 *         A IDiagramPreference object contains the information about the preferences
 *         for the diagram editor, as stored in the platform or extracted from the URL
 *         of the editor.
 */
public interface IDiagramPreference {

    /**
     * @return true if autosave is enabled.
     */
    public boolean isAutoSaveEnabled();

    /**
     * @return the preferred autosave interval.
     */
    public int getAutosaveInterval();
}

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
package org.jbpm.designer.notification;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChangeType;
import org.uberfire.workbench.events.ResourceEvent;

@Portable
public class DesignerWorkitemInstalledEvent implements ResourceEvent {

    private String message;
    private Path path;
    private String resolver;
    private String value;
    private String name;

    public DesignerWorkitemInstalledEvent() {
    }

    public DesignerWorkitemInstalledEvent(final Path path,
                                          final String resolver,
                                          final String value,
                                          final String name,
                                          final String message) {
        this.path = path;
        this.resolver = resolver;
        this.value = value;
        this.name = name;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getResolver() {
        return resolver;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public ResourceChangeType getType() {
        return ResourceChangeType.ADD;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DesignerWorkitemInstalledEvent [resolver=" + resolver + ", value=" + value + ", name=" + name + "]";
    }
}

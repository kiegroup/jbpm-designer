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

package org.jbpm.designer.filter;

import java.io.StringWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class DesignerStream extends ServletOutputStream {

    private StringWriter stringWriter;
    private WriteListener writeListener;

    public DesignerStream(StringWriter stringWriter) {
        this.stringWriter = stringWriter;
    }

    @Override
    public void write(int c) {
        stringWriter.write(c);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        this.writeListener = writeListener;
    }
}

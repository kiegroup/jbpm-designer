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

package org.jbpm.designer.stencilset;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Beautifier {

    private static final Logger logger = LoggerFactory.getLogger(Beautifier.class);

    protected static String getScriptFromFile(String filename) throws IOException {
        if (filename == null) {
            return null;
        }

        FileInputStream fis = null;
        byte[] b = null;

        try {
            // read the file contents into a byte array.
            fis = new FileInputStream(filename);
            int x = fis.available();
            b = new byte[x];
            fis.read(b);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        // create the stencilset string
        return new String(b);
    }

    /**
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        // assertions and beautifier instantiation.
        assert (args.length != 0);

        // get the stencil set and beautifier scripts.
        String stencilsetScript = "set = " + getScriptFromFile(args[0]);
        String beautifierScript = getScriptFromFile(ClassLoader.getSystemResource("org/oryxeditor/stencilset/beautifier.js").getFile());

        // java script runtime initialization
        Context cx = Context.enter();
        cx.setOptimizationLevel(-1);

        try {

            ScriptableObject scope = new ImporterTopLevel(cx);

            // evaluate stencil set and beautifier
            cx.evaluateString(scope,
                              stencilsetScript,
                              "<cmd>",
                              1,
                              null);
            cx.evaluateString(scope,
                              beautifierScript,
                              "<cmd>",
                              1,
                              null);

            // run beautifier on stencil set
            String result = (String) ScriptableObject.callMethod(scope,
                                                                 "beautify",
                                                                 new Object[]{scope.get("set",
                                                                                        scope)});

            System.out.println(result.toString());
        } finally {
            Context.exit();
        }
    }
}

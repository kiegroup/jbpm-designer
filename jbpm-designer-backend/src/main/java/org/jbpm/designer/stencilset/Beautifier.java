/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.designer.stencilset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Beautifier {

    private String getScriptFromFile(String filename) throws IOException {

        // read the file contents into a byte array.
        FileInputStream fis = new FileInputStream(filename);
        int x = fis.available();
        byte b[] = new byte[x];
        fis.read(b);

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
        Beautifier beautifier = new Beautifier();

        // get the stencil set and beautifier scripts.
        String stencilsetScript = "set = " + beautifier.getScriptFromFile(args[0]);
        String beautifierScript = beautifier.getScriptFromFile(ClassLoader.getSystemResource("org/oryxeditor/stencilset/beautifier.js").getFile());

        // java script runtime initialization
        Context cx = Context.enter();
        cx.setOptimizationLevel(-1);

        try {

            ScriptableObject scope = new ImporterTopLevel(cx);

            // evaluate stencil set and beautifier
            cx.evaluateString(scope, stencilsetScript, "<cmd>", 1, null);
            cx.evaluateString(scope, beautifierScript, "<cmd>", 1, null);

            // run beautifier on stencil set
            String result = (String) ScriptableObject.callMethod(scope, "beautify", new Object[] { scope.get("set", scope) });

            System.out.println(result.toString());

        } finally {
            Context.exit();
        }
    }
}

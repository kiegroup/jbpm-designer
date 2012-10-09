package org.jbpm.designer.stencilset;

import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.javascript.*;

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
     * @throws IOException
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

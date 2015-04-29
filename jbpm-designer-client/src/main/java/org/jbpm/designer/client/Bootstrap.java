package org.jbpm.designer.client;

/**
 * Bootstraps the core Designer JS Files.
 */
//@ApplicationScoped
public class Bootstrap {
    private boolean initialized = false;
    private static final String path = "/org.jbpm.designer.jBPMDesigner/";

    public void boostrapDesigner(final String bodyString) {
        if ( initialized ) {
            return;
        }

        // not needed .. can remove

        this.initialized = true;
    }
}

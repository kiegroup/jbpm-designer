package org.jbpm.designer.client;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Window;

/**
 * Bootstraps the core Designer JS Files.
 */
@ApplicationScoped
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

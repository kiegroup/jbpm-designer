package org.jbpm.designer.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface StandaloneResources
        extends
        ClientBundle {

    StandaloneResources INSTANCE = GWT.create( StandaloneResources.class );

    @Source("css/Standalone.css")
    StandaloneCss CSS();

    StandaloneImages images();

}

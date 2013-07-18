package org.jbpm.designer.client.resources.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Application specific Images.
 */
public interface StandaloneImages
        extends
        ClientBundle {

    StandaloneImages INSTANCE = GWT.create( StandaloneImages.class );

    @Source("logo.png")
    ImageResource logo();

}

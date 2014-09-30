package org.jbpm.designer.client.resources.css;

import com.google.gwt.resources.client.CssResource;

/**
 * Application specific CSS.
 */
public interface StandaloneCss
        extends
        CssResource {

    @ClassName("userInfo")
    String userInfoClass();

    @ClassName("perspectives")
    String perspectivesClass();

    @ClassName("controls")
    String controlsClass();

    @ClassName("logo")
    String logoClass();

}

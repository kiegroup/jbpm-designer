package org.jbpm.designer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 1/28/14
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */

public interface DesignerEditorConstants extends
        Messages {
    DesignerEditorConstants INSTANCE = GWT.create(DesignerEditorConstants.class);

    String businessProcess();
}

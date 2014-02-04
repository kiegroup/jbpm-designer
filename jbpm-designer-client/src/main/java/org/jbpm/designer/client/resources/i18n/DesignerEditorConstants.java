package org.jbpm.designer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;


public interface DesignerEditorConstants extends
        Messages {
    DesignerEditorConstants INSTANCE = GWT.create(DesignerEditorConstants.class);

    String businessProcess();
}

package org.jbpm.designer.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.Header;

import static java.lang.Integer.*;

@ApplicationScoped
public class ComplementNavAreaPresenter implements Header {

    public interface View extends IsWidget {

    }

    @Inject
    public View view;

    @Override
    public String getId() {
        return "ComplementNavArea";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}

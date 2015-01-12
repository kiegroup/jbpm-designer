package org.jbpm.designer.service;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DesignerContent {

    private Overview overview;

    public DesignerContent() {
    }

    public DesignerContent(Overview overview) {
        this.overview = overview;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }
}

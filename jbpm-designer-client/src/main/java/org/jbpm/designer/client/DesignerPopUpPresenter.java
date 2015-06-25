/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.client;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "jbpm.designer.popup")
public class DesignerPopUpPresenter extends Composite implements RequiresResize {

    @Inject
    private DesignerWidgetPresenter designerWidget;

    @Inject
    private Caller<DesignerAssetService> assetService;

    protected boolean isReadOnly;

    private FlowPanel container = new FlowPanel();

    @PostConstruct
    public void init() {
        container.clear();

        container.setSize( "1000px", ( 600 - 51 ) + "px" );
        container.add( designerWidget.getView() );
    }

    @OnOpen
    public void onOpen() {
        designerWidget.setSize( 1000, 600 );
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        // read only set to true for now
        this.isReadOnly = true;

        if ( place instanceof PathPlaceRequest ) {
            assetService.call( new RemoteCallback<String>() {
                @Override
                public void callback( final String editorID ) {
                    String url = GWT.getHostPageBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" );
                    assetService.call( new RemoteCallback<Map<String, String>>() {
                        @Override
                        public void callback( Map<String, String> editorParameters ) {
                            if ( editorParameters != null ) {
                                if ( editorParameters.containsKey( "readonly" ) ) {
                                    isReadOnly = Boolean.valueOf( editorParameters.get( "readonly" ) );
                                }
                                if ( editorParameters.containsKey( "processsource" ) ) {
                                    String processSources = editorParameters.get( "processsource" );
                                    if ( processSources != null && processSources.length() > 0 ) {
                                        publishProcessSourcesInfo( editorParameters.get( "processsource" ) );
                                    }
                                    editorParameters.remove( "processsource" );
                                }

                                if ( editorParameters.containsKey( "activenodes" ) ) {
                                    String activeNodes = editorParameters.get( "activenodes" );
                                    if ( activeNodes != null && activeNodes.length() > 0 ) {
                                        publishActiveNodesInfo( editorParameters.get( "activenodes" ) );
                                    }
                                    editorParameters.remove( "activenodes" );
                                }

                                if ( editorParameters.containsKey( "completednodes" ) ) {
                                    String activeNodes = editorParameters.get( "completednodes" );
                                    if ( activeNodes != null && activeNodes.length() > 0 ) {
                                        publishCompletedNodesInfo( editorParameters.get( "completednodes" ) );
                                    }
                                    editorParameters.remove( "completednodes" );
                                }
                                editorParameters.put( "ts", Long.toString( System.currentTimeMillis() ) );
                                designerWidget.setup( editorID, editorParameters );
                            }
                        }

                    } ).getEditorParameters( ( (PathPlaceRequest) place ).getPath(), editorID, url, place );
                }
            } ).getEditorID();
        }
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Process Model";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return container;
    }

    private native void publishProcessSourcesInfo( String ps )/*-{
        $wnd.designerprocesssources = function () {
            return ps;
        }
    }-*/;

    private native void publishActiveNodesInfo( String an )/*-{
        $wnd.designeractivenodes = function () {
            return an;
        }
    }-*/;

    private native void publishCompletedNodesInfo( String cn )/*-{
        $wnd.designercompletednodes = function () {
            return cn;
        }
    }-*/;

    @Override
    public void onResize() {
        Widget parent = container.getParent();
        final int width = parent.getOffsetWidth();
        final int height = parent.getOffsetHeight();
        container.setWidth( width + "px" );
        container.setHeight( height + "px" );
    }

}

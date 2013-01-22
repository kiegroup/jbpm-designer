package org.jbpm.designer.client;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DesignerView
        extends Composite
        implements DesignerPresenter.View,
        RequiresResize {

    private DesignerPresenter presenter;

    //[manstis] The HTML elementID of the Oryx container - this needs to be unique to support multiple
    // instances. This value however is also serialized in the BPMN2 definition as the "resourceId".
    private final String panelId = "Definition";

    private VerticalPanel vp = new VerticalPanel();

    private String jsonModel = "";

    @PostConstruct
    public void init() {
        initWidget( vp );

        //Bootstrap controls (hack for now to ensure start-up scripts are loaded and ran)
        final HorizontalPanel buttonsContainer = new HorizontalPanel();
        final Button bootstrapButton = new Button( "Bootstrap" );
        bootstrapButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                bootstrap();
            }
        } );
        buttonsContainer.add( bootstrapButton );

        final Button loadProcessButton = new Button( "Load process" );
        loadProcessButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                loadProcess( panelId,
                        jsonModel );
            }
        } );
        buttonsContainer.add( loadProcessButton );

        final Button newProcessButton = new Button( "New process" );
        newProcessButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                newProcess( panelId );
            }
        } );
        buttonsContainer.add( newProcessButton );

        vp.add( buttonsContainer );

        //Otyx container
        final HTML html = new HTML();
        final DivElement designerDiv = DivElement.as( DOM.createDiv() );
        designerDiv.setId( panelId );
        html.getElement().insertFirst( designerDiv );
        vp.add( html );
    }

    @Override
    public void init( final DesignerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setJsonModel( final String jsonModel ) {
        this.jsonModel = jsonModel;
    }

    private native void bootstrap()  /*-{
        $wnd.bootstrap();
    }-*/;

    private native void loadProcess( final String panelId,
                                     final String jsonModel )  /*-{
        $wnd.Kickstart.load();
        $wnd.loadProcess(panelId, jsonModel);
    }-*/;

    private native void newProcess( final String panelId )  /*-{
        $wnd.Kickstart.load();
        $wnd.newProcess(panelId);
    }-*/;

    @Override
    public void onResize() {
        final Widget w = getParent();
        final int width = w.getOffsetWidth();
        vp.setWidth( width + "px" );
    }
}

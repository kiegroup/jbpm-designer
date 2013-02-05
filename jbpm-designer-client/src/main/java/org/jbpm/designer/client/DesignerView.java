package org.jbpm.designer.client;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

public class DesignerView
        extends Composite
        implements DesignerPresenter.View,
        RequiresResize {

    private DesignerPresenter presenter;
    private SimplePanel sp = new SimplePanel();
    private String editorID = "";

    @PostConstruct
    public void initPanel() {
        initWidget(sp);


        //Bootstrap controls (hack for now to ensure start-up scripts are loaded and ran)
//        final HorizontalPanel buttonsContainer = new HorizontalPanel();
//        final Button bootstrapButton = new Button( "Bootstrap" );
//        bootstrapButton.addClickHandler( new ClickHandler() {
//            @Override
//            public void onClick( ClickEvent event ) {
//                bootstrap();
//            }
//        } );
//        buttonsContainer.add( bootstrapButton );
//
//        final Button loadProcessButton = new Button( "Load process" );
//        loadProcessButton.addClickHandler( new ClickHandler() {
//            @Override
//            public void onClick( ClickEvent event ) {
//                loadProcess( panelId,
//                        jsonModel );
//            }
//        } );
//        buttonsContainer.add( loadProcessButton );
//
//        final Button newProcessButton = new Button( "New process" );
//        newProcessButton.addClickHandler( new ClickHandler() {
//            @Override
//            public void onClick( ClickEvent event ) {
//                newProcess( panelId );
//            }
//        } );
//        buttonsContainer.add( newProcessButton );
//
//        vp.add( buttonsContainer );
    }

    @Override
    public void init( final DesignerPresenter presenter ) {
        this.presenter = presenter;
        initWidget(sp);
    }

    @Override
    public void setEditorID( final String editorID ) {
        this.editorID = editorID;
        this.sp.getElement().setId(editorID);

//        Window.alert("ADDING DESIGNER DIV: " + this.editorID);
//
//
//        final HTML html = new HTML();
//        final DivElement designerDiv = DivElement.as( DOM.createDiv() );
//        designerDiv.setId( this.editorID );
//        html.getElement().insertFirst(designerDiv);
//        this.vp.add( html );
    }

//    @Override
//    public void startDesigner() {
//        initDesigner();
//        startDesigner();
//    }

    @Override
    public void startDesigneInstancer() {
//        this.sp.remove(innerPannel);
//        this.innerPannel = new SimplePanel();
//        this.innerPannel.getElement().setId(this.editorID);
//        this.sp.add(innerPannel);

        initDesigner();
        startDesigner();
        kickstartEditor();
    }

    private native void startDesigner()  /*-{
        $wnd.startEditorInstance();
    }-*/;

    private native void initDesigner()  /*-{
        $wnd.initDesigner();
    }-*/;

    private native void kickstartEditor()  /*-{
        $wnd.Kickstart.load();
    }-*/;

    @Override
    public void onResize() {
        final Widget w = getParent();
        final int width = w.getOffsetWidth();
        sp.setWidth( width + "px" );
    }
}

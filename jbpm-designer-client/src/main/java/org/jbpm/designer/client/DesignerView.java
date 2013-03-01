package org.jbpm.designer.client;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

public class DesignerView
        extends Composite
        implements DesignerPresenter.View,
        RequiresResize {

    private DesignerPresenter presenter;
    private Frame inlineFrame = new Frame();
    private String editorID = "";

    @PostConstruct
    public void initPanel() {
        setupInlineFrame();
        initWidget(inlineFrame);

    }

    @Override
    public void init( final DesignerPresenter presenter ) {
        this.presenter = presenter;
        setupInlineFrame();
        initWidget(inlineFrame);
    }

    private void setupInlineFrame() {
        inlineFrame.setWidth("100%");
        inlineFrame.setHeight("680");
        inlineFrame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        inlineFrame.getElement().getStyle().setOverflowX(Style.Overflow.AUTO);
        inlineFrame.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
        inlineFrame.getElement().getStyle().setOverflow(Style.Overflow.AUTO);
        inlineFrame.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        inlineFrame.getElement().getStyle().setHeight(680, Style.Unit.PX);
        inlineFrame.setUrl(GWT.getModuleBaseURL() + "inlineeditor.jsp");
    }

    @Override
    public void setEditorID( final String editorID ) {
        this.editorID = editorID;
        inlineFrame.getElement().setId(editorID);
    }


    @Override
    public void startDesigneInstancer() {
        initDesigner(this.editorID);
        startDesigner(this.editorID);
        kickstartEditor(editorID);
    }

    private native void startDesigner(String editorid)  /*-{
        $doc.getElementById(editorid).contentWindow.startEditorInstance();
    }-*/;

    private native void initDesigner(String editorid)  /*-{
        $doc.getElementById(editorid).contentWindow.initEditorInstance();
    }-*/;

    private native void kickstartEditor(String editorid)  /*-{
        $doc.getElementById(editorid).contentWindow.Kickstart.load();
    }-*/;

    @Override
    public void onResize() {
        final Widget w = getParent();
        final int width = w.getOffsetWidth();
        inlineFrame.setWidth(width + "px");
    }
}

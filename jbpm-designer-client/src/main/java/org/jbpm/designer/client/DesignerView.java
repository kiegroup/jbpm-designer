package org.jbpm.designer.client;

import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Iterator;
import java.util.Map;

public class DesignerView
        extends Composite
        implements DesignerPresenter.View,
        RequiresResize {

    private DesignerPresenter presenter;
    private Frame inlineFrame = new Frame();
    private String editorID = "";
    private Map<String, String> editorParameters;

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
        inlineFrame.setWidth("85%");
        inlineFrame.setHeight("600");
        inlineFrame.getElement().setPropertyBoolean("webkitallowfullscreen", true);
        inlineFrame.getElement().setPropertyBoolean("mozallowfullscreen", true);
        inlineFrame.getElement().setPropertyBoolean("allowfullscreen", true);
        inlineFrame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        inlineFrame.getElement().getStyle().setOverflowX(Style.Overflow.AUTO);
        inlineFrame.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
        inlineFrame.getElement().getStyle().setOverflow(Style.Overflow.AUTO);
        inlineFrame.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        inlineFrame.getElement().getStyle().setHeight(680, Style.Unit.PX);


    }

    @Override
    public void setEditorID( final String editorID ) {
        this.editorID = editorID;
        inlineFrame.getElement().setId(editorID);
    }

    @Override
    public void setEditorParamters( final Map<String, String> editorParameters) {
        this.editorParameters = editorParameters;

        // fix locale if needed (for "default")
        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
        if(locale == null) {
            locale = "en";
        } else if(locale.equals("default")) {
            locale = "en";
        }

        String paramsStr = "";
        Iterator<String> paramsIter = this.editorParameters.keySet().iterator();
        while(paramsIter.hasNext()) {
            String paramsKey = paramsIter.next();
            paramsStr += "&" + paramsKey + "=" + editorParameters.get(paramsKey);
        }
        inlineFrame.getElement().setAttribute("src", GWT.getModuleBaseURL() + "inlineeditor.jsp?locale=" + locale + paramsStr);
    }

    @Override
    public String getEditorID() {
        return this.editorID;
    }

    public boolean confirmClose() {
        return Window.confirm( "Business Process may contain unsaved changes. Are you sure you would like to close the editor?" );
    }

    public native boolean canSaveDesignerModel(String editorID) /*-{
        if($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor && (typeof($wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved) == "function")) {
            return $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved();
        }
        return true;
    }-*/;

    @Override
    public void onResize() {
        final Widget w = getParent();
        final int width = w.getOffsetWidth();
        inlineFrame.setWidth(width + "px");
    }
}

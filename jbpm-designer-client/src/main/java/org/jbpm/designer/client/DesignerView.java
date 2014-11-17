package org.jbpm.designer.client;

import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

public class DesignerView
        extends Composite
        implements DesignerWidgetPresenter.View,
                   RequiresResize {

    private Frame inlineFrame = new Frame();
    private String editorID = "";
    private Map<String, String> editorParameters;

    @PostConstruct
    public void initPanel() {
        setupInlineFrame();
        initWidget( inlineFrame );

    }

    private void setupInlineFrame() {
        inlineFrame.setWidth( "85%" );
        inlineFrame.setHeight( "600" );
        inlineFrame.getElement().setPropertyBoolean( "webkitallowfullscreen", true );
        inlineFrame.getElement().setPropertyBoolean( "mozallowfullscreen", true );
        inlineFrame.getElement().setPropertyBoolean( "allowfullscreen", true );
        inlineFrame.getElement().getStyle().setBorderWidth( 0, Style.Unit.PX );
        inlineFrame.getElement().getStyle().setOverflowX( Style.Overflow.AUTO );
        inlineFrame.getElement().getStyle().setOverflowY( Style.Overflow.AUTO );
        inlineFrame.getElement().getStyle().setOverflow( Style.Overflow.AUTO );
        inlineFrame.getElement().getStyle().setWidth( 100, Style.Unit.PCT );
        inlineFrame.getElement().getStyle().setHeight( 680, Style.Unit.PX );
    }

    @Override
    public void setEditorID( final String editorID ) {
        this.editorID = editorID;
        inlineFrame.getElement().setId( editorID );
        inlineFrame.getElement().setAttribute( "name", editorID );
    }

    @Override
    public void setEditorParamters( final Map<String, String> editorParameters ) {
        this.editorParameters = editorParameters;
        // fix locale if needed (for "default")
        String locale = LocaleInfo.getCurrentLocale().getLocaleName();
/*        if(locale == null) {
            locale = "en";
        } else {
            if(locale.equals("default")) {
                locale = "en";
            }
            if(locale.indexOf("_") > 0) {
                locale = locale.split("_")[0]; // just use the language (ommit country and variant)
            }
            if(!(locale.equalsIgnoreCase("en") || locale.equalsIgnoreCase("ja"))) {
                // we currently only support english and japanese. default to english
                locale = "en";
            }
        }
*/
        String paramsStr = "";
        Iterator<String> paramsIter = this.editorParameters.keySet().iterator();
        while ( paramsIter.hasNext() ) {
            String paramsKey = paramsIter.next();
            paramsStr += "&" + paramsKey + "=" + editorParameters.get( paramsKey );
        }
        // add timestamp to end of url for caching
        paramsStr += "&ms=" + System.currentTimeMillis();
        inlineFrame.getElement().setAttribute( "src", GWT.getModuleBaseURL() + "inlineeditor.jsp?locale=" + locale + paramsStr );
    }

    @Override
    public String getEditorID() {
        return this.editorID;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( "Business Process may contain unsaved changes. Are you sure you would like to close the editor?" );
    }

    public native void setProcessSaved( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor) {
            $wnd.document.getElementById(editorID).contentWindow.ORYX.PROCESS_SAVED = true;
        }
    }-*/;

    public native boolean getIsReadOnly( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor) {
            return $wnd.document.getElementById(editorID).contentWindow.ORYX.READONLY;
        }
    }-*/;

    public native void setProcessUnSaved( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor) {
            $wnd.document.getElementById(editorID).contentWindow.ORYX.PROCESS_SAVED = false;
        }
    }-*/;

    public native boolean canSaveDesignerModel( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor && (typeof($wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved) == "function")) {
            return $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved();
        }
        return true;
    }-*/;

    public native void raiseEventSave( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor && (typeof($wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved) == "function")) {
            $wnd.document.getElementById(editorID).contentWindow.ORYX.EDITOR._pluginFacade.raiseEvent({
                type: "designereventdosave"
            });
        }
    }-*/;

    public native void raiseEventSaveCancel( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor && (typeof($wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved) == "function")) {
            $wnd.document.getElementById(editorID).contentWindow.ORYX.EDITOR._pluginFacade.raiseEvent({
                type: "designereventcancelsave"
            });
        }
    }-*/;

    public native void raiseEventReload( String editorID ) /*-{
        if ($wnd.document.getElementById(editorID) && $wnd.document.getElementById(editorID).contentWindow.ORYX && $wnd.document.getElementById(editorID).contentWindow.ORYX.Editor && (typeof($wnd.document.getElementById(editorID).contentWindow.ORYX.Editor.checkIfSaved) == "function")) {
            $wnd.document.getElementById(editorID).contentWindow.ORYX.EDITOR._pluginFacade.raiseEvent({
                type: "designereventreloads"
            });
        }
    }-*/;

    @Override
    public void onResize() {
        final Widget w = getParent();
        final int width = w.getOffsetWidth();
        final int height = w.getOffsetHeight();
        inlineFrame.setWidth( width + "px" );
        inlineFrame.setHeight( height + "px" );
    }

    public void setSize( final int width,
                         final int height ) {
        if ( getParent().getParent().getParent() instanceof Modal ) {
            final Modal modal = (Modal) getParent().getParent().getParent();
            modal.setWidth( width + "px" );
            modal.setHeight( height + "px" );
            modal.getElement().getStyle().setMarginLeft( ( width / 2 ) * -1, Style.Unit.PX );
            modal.getElement().getStyle().setMarginTop( ( height / 2 ) * -1, Style.Unit.PX );
            getParent().getParent().removeStyleName( "modal-body" );
            super.setSize( width + "px", ( height - 51 ) + "px" );
        }
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}

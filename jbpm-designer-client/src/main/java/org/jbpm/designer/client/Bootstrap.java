package org.jbpm.designer.client;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.StyleElement;

/**
 * Bootstrap GWT's host page to support jBPM Designer. This implementation hard-codes all the properties
 * that are normally derived from the IDiagramProfile. Different IDiagramProfile implementations (such as
 * org.kie.drools.designer.web.profile.impl.DefaultProfileImpl) need access to the Servlet Context to load the
 * profile configuration files from the webapp. A fuller implementation of this bootstrap would callback
 * to the server to get the necessary configuration parameters.
 *
 * This is a hard-coded representation of the jBPM profile.
 */
@ApplicationScoped
public class Bootstrap {

    private boolean initialised = false;

    public void init() {
        if ( initialised ) {
            return;
        }
        //Get the HEAD
        final Document doc = Document.get();
        final NodeList<Element> nodes = doc.getElementsByTagName( HeadElement.TAG );
        final HeadElement head = nodes.getItem( 0 ).cast();

        //Append script tags
        appendScriptUrl( head,
                "/designer/lib/prototype-1.5.1.js" );
        appendScriptUrl( head,
                "/designer/lib/path_parser.js" );
        appendScriptUrl( head,
                "/designer/lib/ext-2.0.2/adapter/ext/ext-base.js" );
        appendScriptUrl( head,
                "/designer/lib/ext-2.0.2/ext-all.js" );
        appendScriptUrl( head,
                "/designer/lib/ext-2.0.2/color-field.js" );
        appendScriptUrl( head,
                "/designer/lib/jquery-1.7.2.min.js" );
        appendScriptUrl( head,
                "/designer/lib/handlebars-1.0.0.beta.6.js" );

        //[manstis] GWT StyleElement is not setting the following CSS. Using unmodified CSS file for now
        //appendStyle( head,
        //             "screen",
        //             "text/css",
        //             "@import url(\"/designer/lib/ext-2.0.2/resources/css/ext-all.css\");\n" +
        //                     ".extensive-remove {\n" +
        //                     "background-image: url(/designer/images/remove.gif) ! important;\n" +
        //                     "}\n" );
        appendLink( head,
                "Stylesheet",
                "screen",
                "/designer/lib/ext-2.0.2/resources/css/ext-all.css",
                "text/css" );

        appendScriptUrl( head,
                "/designer/js/cm.js" );
        appendScriptUrl( head,
                "/designer/js/cmxml.js" );
        appendScriptUrl( head,
                "/designer/js/cmjs.js" );
        appendScriptUrl( head,
                "/designer/js/cmcss.js" );
        appendScriptUrl( head,
                "/designer/js/cmclike.js" );
        appendScriptUrl( head,
                "/designer/js/cmhtmlmixed.js" );
        appendScriptUrl( head,
                "/designer/js/closetags.js" );
        appendScriptUrl( head,
                "/designer/js/hint.js" );
        appendScriptUrl( head,
                "/designer/js/hintjbpm.js" );
        appendScriptUrl( head,
                "/designer/js/cmsearch.js" );
        appendScriptUrl( head,
                "/designer/js/cmsearchcursor.js" );
        appendScriptUrl( head,
                "/designer/js/cmdialog.js" );
        appendScriptUrl( head,
                "/designer/js/cmfold.js" );

        appendLink( head,
                "Stylesheet",
                "screen",
                "/designer/css/codemirror.css",
                "text/css" );
        appendLink( head,
                "Stylesheet",
                "screen",
                "/designer/css/cmdialog.css",
                "text/css" );
        //Full Screen
        appendLink( head,
                "Stylesheet",
                "screen",
                "/designer/css/fullscreen.css",
                "text/css" );
        //Mic
        appendLink( head,
                "Stylesheet",
                "screen",
                "/designer/css/mic.css",
                "text/css" );
        //Chrome Frame
        appendScriptUrl( head,
                "/designer/js/CFInstall.min.js" );

        //Skins
        appendLink( head,
                "Stylesheet",
                "screen",
                "/designer/css/theme-default.css",
                "text/css" );

        // erdf schemas
        appendLink( head,
                "schema.dc",
                "http://purl.org/dc/elements/1.1/" );
        appendLink( head,
                "schema.dcTerms",
                "http://purl.org/dc/terms/" );
        appendLink( head,
                "schema.b3mn",
                "http://b3mn.org" );
        appendLink( head,
                "schema.oryx",
                "http://oryx-editor.org/" );
        appendLink( head,
                "schema.raziel",
                "http://raziel.org/" );

        //Environment files
        final List<String> envFiles = new ArrayList<String>();
        envFiles.add( "/designer/i18n/translation_" + getDesignerLocale() + ".js" );
        envFiles.add( "/designer/js/kickstart.js" );
        envFiles.add( "/designer/js/config.js" );
        envFiles.add( "/designer/js/oryx.js" );
        envFiles.add( "/designer/js/clazz.js" );
        envFiles.add( "/designer/js/Core/main.js" );
        envFiles.add( "/designer/js/utils.js" );
        envFiles.add( "/designer/js/erdfparser.js" );
        envFiles.add( "/designer/js/datamanager.js" );
        envFiles.add( "/designer/js/Core/SVG/editpathhandler.js" );
        envFiles.add( "/designer/js/Core/SVG/minmaxpathhandler.js" );
        envFiles.add( "/designer/js/Core/SVG/pointspathhandler.js" );
        envFiles.add( "/designer/js/Core/SVG/svgmarker.js" );
        envFiles.add( "/designer/js/Core/SVG/svgshape.js" );
        envFiles.add( "/designer/js/Core/SVG/label.js" );
        envFiles.add( "/designer/js/Core/Math/math.js" );
        envFiles.add( "/designer/js/Core/StencilSet/stencil.js" );
        envFiles.add( "/designer/js/Core/StencilSet/property.js" );
        envFiles.add( "/designer/js/Core/StencilSet/propertyitem.js" );
        envFiles.add( "/designer/js/Core/StencilSet/complexpropertyitem.js" );
        envFiles.add( "/designer/js/Core/StencilSet/rules.js" );
        envFiles.add( "/designer/js/Core/StencilSet/stencilset.js" );
        envFiles.add( "/designer/js/Core/StencilSet/stencilsets.js" );
        envFiles.add( "/designer/js/Core/command.js" );
        envFiles.add( "/designer/js/Core/bounds.js" );
        envFiles.add( "/designer/js/Core/uiobject.js" );
        envFiles.add( "/designer/js/Core/abstractshape.js" );
        envFiles.add( "/designer/js/Core/canvas.js" );
        envFiles.add( "/designer/js/Core/svgDrag.js" );
        envFiles.add( "/designer/js/Core/shape.js" );
        envFiles.add( "/designer/js/Core/Controls/control.js" );
        envFiles.add( "/designer/js/Core/Controls/magnet.js" );
        envFiles.add( "/designer/js/Core/Controls/docker.js" );
        envFiles.add( "/designer/js/Core/node.js" );
        envFiles.add( "/designer/js/Core/edge.js" );
        envFiles.add( "/designer/js/Core/abstractPlugin.js" );
        envFiles.add( "/designer/js/Core/abstractLayouter.js" );
        envFiles.add( "/designer/js/Core/abstractDragTracker.js" );
        envFiles.add( "/designer/js/diff_match_patch.js" );
        envFiles.add( "/designer/js/itemdeleter.js" );
        envFiles.add( "/designer/js/jsonpath.js" );
        envFiles.add( "/designer/js/imageviewer.js" );
        envFiles.add( "/designer/js/svgviewer.js" );
        envFiles.add( "/designer/js/panelcollapsedtitle.js" );
        for ( String envFile : envFiles ) {
            appendScriptUrl( head,
                    envFile );
        }

        //Plugin files -- hard coded for now (need a new PluginServiceImpl exposed via errai-rpc)
        final List<String> pluginFiles = new ArrayList<String>();
        pluginFiles.add( "/designer/js/Plugins/toolbar.js" );
        pluginFiles.add( "/designer/js/Plugins/uuidRepository.js" );
        pluginFiles.add( "/designer/js/Plugins/shapemenu.js" );
        pluginFiles.add( "/designer/js/Plugins/shaperepository.js" );
        pluginFiles.add( "/designer/js/Plugins/propertywindow.js" );
        pluginFiles.add( "/designer/js/Plugins/canvasResize.js" );
        pluginFiles.add( "/designer/js/Plugins/view.js" );
        pluginFiles.add( "/designer/js/Plugins/dragdropresize.js" );
        pluginFiles.add( "/designer/js/Plugins/renameShapes.js" );
        pluginFiles.add( "/designer/js/Plugins/undo.js" );
        pluginFiles.add( "/designer/js/Plugins/arrangement.js" );
        pluginFiles.add( "/designer/js/Plugins/grouping.js" );
        pluginFiles.add( "/designer/js/Plugins/dragDocker.js" );
        pluginFiles.add( "/designer/js/Plugins/addDocker.js" );
        pluginFiles.add( "/designer/js/Plugins/selectionframe.js" );
        pluginFiles.add( "/designer/js/Plugins/shapeHighlighting.js" );
        pluginFiles.add( "/designer/js/Plugins/edit.js" );
        pluginFiles.add( "/designer/js/Plugins/keysMove.js" );
        pluginFiles.add( "/designer/js/Plugins/Layouter/edgeLayouter.js" );
        pluginFiles.add( "/designer/js/Plugins/Layouter/containerLayouter.js" );
        pluginFiles.add( "/designer/js/Plugins/dragTracker/laneDragTracker.js" );
        pluginFiles.add( "/designer/js/Plugins/dragTracker/poolDragTracker.js" );
        for ( String pluginFile : pluginFiles ) {
            appendScriptUrl( head,
                    pluginFile );
        }

        //Oryx bootstrap
        appendScriptSource( head,
                "function bootstrap() {\n" +
                        "jQuery.noConflict();\n" +
                        "ORYX.VERSION = \"" + getDesignerVersion() + "\";\n" +
                        "ORYX.LOCALE = \"" + getDesignerLocale() + "\";\n" +
                        "ORYX.CONFIG.SSET = \"" + getStencilSet() + "\";\n" +
                        "ORYX.CONFIG.DEV = " + getDebug() + ";\n" +
                        "if (ORYX.CONFIG.DEV) {\n" +
                        "    ORYX_LOGLEVEL = 4;\n" +
                        " }\n" +
                        "ORYX.PREPROCESSING = \"" + getPreprocessing() + "\";\n" +
                        "ORYX.EXTERNAL_PROTOCOL = \"" + getExternalProtocol() + "\";\n" +
                        "ORYX.EXTERNAL_HOST = \"" + getExternalHost() + "\";\n" +
                        "ORYX.EXTERNAL_SUBDOMAIN = \"" + getExternalSubDomain() + "\";\n" +
                        "params = window.location.search.toQueryParams();\n" +
                        "if (params['profile'] === undefined) {\n" +
                        "    params['profile'] = 'jbpm';\n" +
                        " }\n" +
                        "ORYX.UUID = params['uuid'];\n" +
                        "ORYX.PROFILE = params['profile'];\n" +
                        "ORYX.LOCAL_HISTORY_ENABLED = " + getLocalHistoryEnabled() + ";\n" +
                        "ORYX.LOCAL_HISTORY_TIMEOUT = " + getLocalHistoryTimeout() + ";\n" +
                        "var segments = window.location.pathname.split(\"/\").without(\"\");\n" +
                        "ORYX.CONFIG.ROOT_PATH = \"/designer/\";\n" +
                        "ORYX.PATH = ORYX.CONFIG.ROOT_PATH;\n" +
                        "if (ORYX.UUID === undefined) {\n" +
                        "    ORYX.UUID = segments.pop();\n" +
                        "}\n" +
                        "ORYX.CONFIG.UUID_AUTOSAVE_INTERVAL = " + getAutoSaveInterval() + ";\n" +
                        "ORYX.CONFIG.UUID_AUTOSAVE_DEFAULT = " + getAutoSaveDefault() + ";\n" +
                        "ORYX.CONFIG.SSEXTS= [" + getSSExtensions() + "].map(function(ssext) {\n" +
                        "    // for each of the extensions, we get the extension file and return its contents.\n" +
                        "    var contents = null;\n" +
                        "    new Ajax.Request(ORYX.PATH + \"stencilset/\" + ssext, {\n" +
                        "        asynchronous: false,\n" +
                        "        method: 'get',\n" +
                        "        contentType: 'application/json',\n" +
                        "        onSuccess: function(result) {\n" +
                        "            contents = result.responseText.evalJSON();\n" +
                        "        },\n" +
                        "        onFailure: function(result) {\n" +
                        "            alert(\"[jBPM Designer NG] - Could not load Process Designer\"); //TODO even better logging ?\n" +
                        "        }\n" +
                        "    });\n" +
                        "    return contents;\n" +
                        "});\n" +
                        "new Ajax.Request(ORYX.PATH + \"plugins\", {\n" +
                        "    asynchronous: false,\n" +
                        "    method: 'get',\n" +
                        "    contentType: 'application/json',\n" +
                        "    onSuccess: function(result) {\n" +
                        "        var allPlugins = {};\n" +
                        "        result.responseText.evalJSON().each(function (p) {\n" +
                        "            allPlugins[p.name] = p;\n" +
                        "        }.bind(allPlugins));\n" +
                        "        // install the current plugins\n" +
                        "        ORYX.availablePlugins = [];\n" +
                        "        [" + getProfilePlugins() + "].each(function(pluginName) {\n" +
                        "            p = allPlugins[pluginName];\n" +
                        "            if (p) {\n" +
                        "                ORYX.availablePlugins.push(p);\n" +
                        "            } else {\n" +
                        "                ORYX.Log.error(\"missing plugin \" + pluginName);\n" +
                        "            }\n" +
                        "          }.bind(allPlugins));\n" +
                        "        },\n" +
                        "        onFailure: function(result) {\n" +
                        "            alert(\"[jBPM Designer NG] - Could not load Process Designer\"); //TODO even better logging ?\n" +
                        "        }\n" +
                        "});\n" +
                        "alert(\"jBPM Designer NG - Bootstrap complete\");\n" +
                        "}\n" );

        addScripts();
        initialised = true;
    }

    final List<String> scripts = new ArrayList<String>();

    private void appendSequentialScript( final String url ) {
        scripts.add( url );
    }

    private void addScripts() {
        if ( scripts.isEmpty() ) {
            return;
        }
        final StringBuffer sb = new StringBuffer();
        for ( final String url : scripts ) {
            sb.append( url ).append( "," );
        }
        sb.delete( sb.length() - 1, sb.length() );
        addScripts( sb.toString() );
    }

    private native void addScripts( final String p0 ) /*-{
        var addScript = function (url, scripts) {
            var script = document.createElement("script")
            script.type = "text/javascript";
            if (scripts) {
                // wait for the script to be loaded to add a new script to respect loading order.
                if (script.readyState) {  //IE
                    script.onreadystatechange = function () {
                        if (script.readyState == "loaded" || script.readyState == "complete") {
                            script.onreadystatechange = null;
                            addScriptsSequentially(scripts);
                        }
                    }.bind(scripts);
                } else {  //Others
                    script.onload = function () {
                        addScriptsSequentially(scripts);
                    }.bind(scripts);
                }
            }
            script.src = url;
            document.head.appendChild(script);
        }

        var addScriptsSequentially = function (scripts) {

            if (scripts.length == 0) {
                return;
            }
            url = scripts.shift();
            addScript(url, scripts);
        }

        var scripts = p0.split(",");
        addScriptsSequentially(scripts);

    }-*/;

    private void appendScriptUrl( final Element element,
                                  final String url ) {
        final ScriptElement scriptElement = Document.get().createScriptElement();
        scriptElement.setType( "text/javascript" );
        scriptElement.setSrc( url );
        element.appendChild( scriptElement );
    }

    private void appendScriptSource( final Element element,
                                     final String source ) {
        final ScriptElement scriptElement = Document.get().createScriptElement( source );
        scriptElement.setType( "text/javascript" );
        element.appendChild( scriptElement );
    }

    private void appendStyle( final Element e,
                              final String media,
                              final String type,
                              final String css ) {
        final StyleElement style = Document.get().createStyleElement();
        if ( media != null ) {
            style.setMedia( media );
        }
        if ( type != null ) {
            style.setType( type );
        }
        if ( css != null ) {
            style.setCssText( css );
        }
        e.appendChild( style );
    }

    private void appendLink( final Element e,
                             final String rel,
                             final String media,
                             final String href,
                             final String type ) {
        final LinkElement link = Document.get().createLinkElement();
        if ( rel != null ) {
            link.setRel( rel );
        }
        if ( media != null ) {
            link.setMedia( media );
        }
        if ( type != null ) {
            link.setType( type );
        }
        if ( href != null ) {
            link.setHref( href );
        }
        e.appendChild( link );
    }

    private void appendLink( final Element e,
                             final String rel,
                             final String href ) {
        final LinkElement link = Document.get().createLinkElement();
        if ( rel != null ) {
            link.setRel( rel );
        }
        if ( href != null ) {
            link.setHref( href );
        }
        e.appendChild( link );
    }

    private String getDesignerVersion() {
        return "0.0.1-SNAPSHOT";
    }

    private String getDesignerLocale() {
        return "en";
    }

    private String getStencilSet() {
        return "bpmn2.0jbpm";
    }

    private String getDebug() {
        return Boolean.FALSE.toString();
    }

    private String getPreprocessing() {
        return "";
    }

    private String getExternalProtocol() {
        return "";
    }

    private String getExternalHost() {
        return "";
    }

    private String getExternalSubDomain() {
        return "";
    }

    private String getLocalHistoryEnabled() {
        return Boolean.FALSE.toString();
    }

    private String getLocalHistoryTimeout() {
        return "0";
    }

    private String getAutoSaveInterval() {
        return "0";
    }

    private String getAutoSaveDefault() {
        return Boolean.FALSE.toString();
    }

    private String getSSExtensions() {
        return "";
    }

    private String getProfilePlugins() {
        final String[] plugins = new String[]{
                "ORYX.Plugins.Toolbar",//toolbar.js
                "ORYX.Plugins.UUIDRepositorySave",//uuidRepository.js
                "ORYX.Plugins.ShapeMenuPlugin",//shapemenu.js
                "ORYX.Plugins.ShapeRepository", //shaperepository.js
                "ORYX.Plugins.PropertyWindow",//propertywindow.js
                "ORYX.Plugins.CanvasResize",//canvasResize.js
                "ORYX.Plugins.View",//view.js
                "ORYX.Plugins.DragDropResize",//dragdropresize.js
                "ORYX.Plugins.RenameShapes",//renameShapes.js
                "ORYX.Plugins.Undo",//undo.js
                "ORYX.Plugins.Arrangement",//arrangement.js
                "ORYX.Plugins.Grouping",//grouping.js
                "ORYX.Plugins.DragDocker",//dragDocker.js
                "ORYX.Plugins.AddDocker",//addDocker.js
                "ORYX.Plugins.SelectionFrame",//selectionframe.js
                "ORYX.Plugins.ShapeHighlighting",//shapeHighlighting.js
                "ORYX.Plugins.Edit",//edit.js
                "ORYX.Plugins.KeysMove",//keysMove.js
                "ORYX.Plugins.Layouter.EdgeLayouter",//Layouter/edgeLayouter.js
                "ORYX.Plugins.ContainerLayouter",//Layouter/containerLayouter.js
                "ORYX.Plugins.DragTracker.LaneDragTracker",//dragTracker/laneDragTracker.js
                "ORYX.Plugins.DragTracker.PoolDragTracker" //dragTracker/poolDragTracker.js
        };
        final StringBuilder result = new StringBuilder();
        for ( int i = 0; i < plugins.length; i++ ) {
            result.append( "\"" ).append( plugins[ i ] ).append( "\"" );
            if ( i < plugins.length - 1 ) {
                result.append( "," );
            }
        }
        return result.toString();
    }

}


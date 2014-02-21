<%@ page contentType="text/html;charset=UTF-8" %>
<%
    response.setHeader("Cache-Control","no-cache");
    response.setHeader("Pragma","no-cache");
    response.setDateHeader ("Expires", -1);
%>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:b3mn="http://b3mn.org/2007/b3mn" xmlns:ext="http://b3mn.org/2007/ext" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:atom="http://b3mn.org/2007/atom+xhtml">
<head profile="http://purl.org/NET/erdf/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <link xmlns="http://www.w3.org/1999/xhtml" rel="icon" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/jbpm.gif" />
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/prototype-1.5.1.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/path_parser.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/ext-2.0.2/adapter/ext/ext-base.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/ext-2.0.2/ext-all.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/ext-2.0.2/color-field.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" type="text/javascript">jQuery.noConflict();</script>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/handlebars-1.0.0.beta.6.js" type="text/javascript"></script>
    <style xmlns="http://www.w3.org/1999/xhtml" media="screen" type="text/css">@import url("<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/ext-2.0.2/resources/css/ext-all.css");
    .extensive-remove {
        background-image: url(<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/remove.gif) ! important;
    }</style>
    <!-- utility scripts -->
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/compressed/designer-utils.js" type="text/javascript"></script>
    <!-- styles -->
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/codemirror.css" type="text/css" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/cmdialog.css" type="text/css" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/fullscreen.css" type="text/css" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/mic.css" type="text/css" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/notifications.css" type="text/css" />
    <script xmlns="http://www.w3.org/1999/xhtml" type="text/javascript" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/CFInstall.min.js"></script>
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/theme-default.css" type="text/css" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/xtheme-gray-colors.css" type="text/css" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/xtheme-gray.css" type="text/css" />
    <!-- schemas -->
    <link xmlns="http://www.w3.org/1999/xhtml" rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="schema.dcTerms" href="http://purl.org/dc/terms/" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="schema.b3mn" href="http://b3mn.org" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="schema.oryx" href="http://oryx-editor.org/" />
    <link xmlns="http://www.w3.org/1999/xhtml" rel="schema.raziel" href="http://raziel.org/" />
    <!-- core scripts -->
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/compressed/designer-core.js" type="text/javascript"></script>
    <!-- translations -->

    <jsp:include page="i18n/translation.jsp"/>
    <!-- plugins -->
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/compressed/designer-plugins.js" type="text/javascript"></script>
    <!-- dynamic properties -->
    <script xmlns="http://www.w3.org/1999/xhtml" type="text/javascript" src="<%=request.getContextPath()%>/editor/?uuid=<%=request.getParameter("uuid")%>&profile=<%=request.getParameter("profile")%>&pp=<%=request.getParameter("pp")%>&editorid=<%=request.getParameter("editorid")%>&readonly=<%=request.getParameter("readonly")%>&ts=<%=request.getParameter("ts")%>"></script>

</head>
<body style="overflow:hidden;" onload="startit();">
    <div id="Definition"></div>

    <script>
        if(parent.document.getElementById(ORYX.EDITORID) && parent.document.getElementById(ORYX.EDITORID).parentNode && parent.document.getElementById(ORYX.EDITORID).parentNode.parentNode) {
            parent.document.getElementById(ORYX.EDITORID).parentNode.parentNode.style.overflow = 'hidden';
        }
    </script>
</body>
</html>

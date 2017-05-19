<%@ page contentType="text/html;charset=UTF-8" %>
<%
    response.setHeader("Cache-Control","no-cache");
    response.setHeader("Pragma","no-cache");
    response.setDateHeader ("Expires", -1);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head profile="http://purl.org/NET/erdf/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <link xmlns="http://www.w3.org/1999/xhtml" rel="icon" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/jbpm.gif" />
  <!-- lib scripts -->
  <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/compressed/designer-lib.js" type="text/javascript"></script>
  <!-- ext scripts -->
  <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/compressed/designer-ext.js" type="text/javascript"></script>

    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script xmlns="http://www.w3.org/1999/xhtml" type="text/javascript">jQuery.noConflict();</script>

    <style xmlns="http://www.w3.org/1999/xhtml" media="screen" type="text/css">@import url("<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/lib/ext-2.0.2/resources/css/ext-all.css");
    .extensive-remove {
        background-image: url(<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/remove.gif) ! important;
    }
    .extensive-install {
        background-image: url(<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/images/wrench.png) ! important;
    }
    </style>
    <!-- utility scripts -->
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/compressed/designer-utils.js" type="text/javascript"></script>

  <!-- compressed stylesheets -->
  <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/designer-compressed-stylesheets.css" type="text/css" />
  <!-- The sprite css files included here must come after the ext css because the classes override classes in ext styles -->
  <link xmlns="http://www.w3.org/1999/xhtml" rel="Stylesheet" media="screen" href="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/css/sprites/sprite-stylesheets.css" type="text/css" />

  <script xmlns="http://www.w3.org/1999/xhtml" type="text/javascript" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/CFInstall.min.js"></script>

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
    <script xmlns="http://www.w3.org/1999/xhtml" type="text/javascript" src="<%=request.getContextPath()%>/editor/?uuid=<%=request.getParameter("uuid")%>&profile=<%=request.getParameter("profile")%>&pp=<%=request.getParameter("pp")%>&editorid=<%=request.getParameter("editorid")%>&readonly=<%=request.getParameter("readonly")%>&instanceviewmode=<%=request.getParameter("instanceviewmode")%>&ts=<%=request.getParameter("ts")%>&sessionId=<%=request.getParameter("sessionId")%>"></script>

</head>
<body style="overflow:hidden;" onload="startit();">
    <div id="Definition"> <!-- <canvas id="comp"></canvas> --> </div>
    <!-- <video id="video" autoplay width="300" style="display:none"></video>
    <canvas id="canvas" style="width:300px;display:none;"></canvas>
    <script xmlns="http://www.w3.org/1999/xhtml" src="<%=request.getContextPath()%>/org.jbpm.designer.jBPMDesigner/js/gestures/gesture.js" type="text/javascript"></script>
    -->
</body>
</html>

<%
    String queryString = request.getQueryString();
    String redirectURL = "org.jbpm.designer.jBPMDesigner/designer.html?" + ( queryString == null ? "" : queryString );
    response.sendRedirect( redirectURL );
%>

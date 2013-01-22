<%
    String queryString = request.getQueryString();
    String redirectURL = "org.jbpm.designer.jBPMDesigner/Designer.html?" + ( queryString == null ? "" : queryString );
    response.sendRedirect( redirectURL );
%>

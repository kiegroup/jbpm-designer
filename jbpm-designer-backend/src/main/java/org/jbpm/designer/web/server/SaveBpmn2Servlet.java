package org.jbpm.designer.web.server;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;
import org.jbpm.designer.web.repository.impl.UUIDBasedJbpmRepository;

/**
 * @author Golovlyev
 */
public class SaveBpmn2Servlet extends HttpServlet {
  private static final long serialVersionUID = 3857121919916498228L;

  private IUUIDBasedRepository _repository;

  @Inject
  private IDiagramProfileService _profileService = null;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    try {
      _repository = new UUIDBasedJbpmRepository();
      _repository.configure(this);
    }
    catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.setCharacterEncoding("UTF-8");
      String json = req.getParameter("data");
      String preProcessingParam = req.getParameter("pp");
      String profileParam = req.getParameter("profile");
      String procDefPath = req.getParameter("procDefPath");
      File bpmnXml = new File(new String(Base64.decodeBase64(procDefPath), "UTF-8"));
      IDiagramProfile profile = _profileService.findProfile(req, profileParam);
      String xml = _repository.toXML(json, profile, preProcessingParam);
      FileUtils.writeStringToFile(bpmnXml, xml);
    }
    catch (Exception ex) {
      throw new ServletException();
    }
  }
}

package org.jbpm.designer.web.server;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;
import org.jbpm.designer.web.repository.impl.UUIDBasedJbpmRepository;
import org.jbpm.designer.web.server.exception.BpmnDesignerException;
import org.jbpm.designer.web.server.exception.BpmnSaveException;

/**
 * @author Golovlyev
 */
public class SaveBpmn2Servlet extends HttpServlet {
  private static final long serialVersionUID = 3857121919916498228L;

  private IUUIDBasedRepository _repository;
  private Properties messages;

  //  @Inject
  private static IDiagramProfileService _profileService = ProfileServiceImpl.getInstance();

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    _repository = new UUIDBasedJbpmRepository();
    _repository.configure(this);
    messages = new Properties();
    try {
      messages.load(this.getClass().getResourceAsStream("/messages.properties"));
    }
    catch (IOException e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      req.setCharacterEncoding("UTF-8");
      String json = req.getParameter("data");
      String preProcessingParam = req.getParameter("pp");
      String profileParam = req.getParameter("profile");
      String uuid = Utils.getUUID(req);
      File bpmnFile = new File(uuid);
      IDiagramProfile profile = _profileService.findProfile(req, profileParam);
      String xml = _repository.toXML(json, profile, preProcessingParam);
      if (bpmnFile.canWrite()) {
        FileUtils.writeStringToFile(bpmnFile, xml);
      } else {
        throw new BpmnSaveException(messages.getProperty("cant.write.bpmn.file"));
      }
    }
    catch (BpmnDesignerException e) {
      resp.setStatus(500);
      resp.getWriter().write(e.getMessage());
    }
    catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}

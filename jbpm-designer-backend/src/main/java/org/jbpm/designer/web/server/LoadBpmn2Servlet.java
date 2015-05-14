package org.jbpm.designer.web.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;

/**
 * @author Golovlyev
 */
public class LoadBpmn2Servlet extends HttpServlet {
  private static final long serialVersionUID = -1095623166420186064L;

//  @Inject
  private static IDiagramProfileService _profileService = ProfileServiceImpl.getInstance();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.setCharacterEncoding("UTF-8");
      String uuid = Utils.getUUID(req);
      String profileParam = req.getParameter("profile");
      String pp = req.getParameter("pp");

      String convertServiceTasks = "true";//TODO


      File file = new File(uuid);
      String bpmnXmlStr = FileUtils.readFileToString(file);

      if (convertServiceTasks != null && convertServiceTasks.equals("true")) {
        bpmnXmlStr = bpmnXmlStr.replaceAll("drools:taskName=\".*?\"", "drools:taskName=\"ReadOnlyService\"");
        bpmnXmlStr = bpmnXmlStr.replaceAll("tns:taskName=\".*?\"", "tns:taskName=\"ReadOnlyService\"");
      }

      IDiagramProfile profile = _profileService.findProfile(req, profileParam);
      Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmnXmlStr);
      if (def != null) {
        def.setTargetNamespace("http://www.omg.org/bpmn20");

        if (convertServiceTasks != null && convertServiceTasks.equals("true")) {
          // fix the data input associations for converted tasks
          List<RootElement> rootElements = def.getRootElements();
          for (RootElement root : rootElements) {
            if (root instanceof Process) {
              updateTaskDataInputs((Process) root, def);
            }
          }
        }


        // get the xml from Definitions
        ResourceSet rSet = new ResourceSetImpl();
        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
        JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
        bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
        bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION, true);
        bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY, true);
        bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF, JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);
        bpmn2resource.setEncoding("UTF-8");
        rSet.getResources().add(bpmn2resource);
        bpmn2resource.getContents().add(def);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bpmn2resource.save(outputStream, new HashMap<Object, Object>());
        String revisedXmlModel = outputStream.toString();
        String json = profile.createUnmarshaller().parseModel(revisedXmlModel, profile, pp);
        resp.setContentType("application/json");
        resp.getWriter().print(json);
      }
    }
    catch (Exception ex) {
      throw new ServletException(ex);
    }
  }

  private void updateTaskDataInputs(FlowElementsContainer container, Definitions def) {
    List<FlowElement> flowElements = container.getFlowElements();
    for (FlowElement fe : flowElements) {
      if (fe instanceof Task && !(fe instanceof UserTask)) {
        Task task = (Task) fe;
        boolean foundReadOnlyServiceTask = false;
        Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
        while (iter.hasNext()) {
          FeatureMap.Entry entry = iter.next();
          if (entry.getEStructuralFeature().getName().equals("taskName")) {
            if (entry.getValue().equals("ReadOnlyService")) {
              foundReadOnlyServiceTask = true;
            }
          }
        }

        if (foundReadOnlyServiceTask) {
          if (task.getDataInputAssociations() != null) {
            List<DataInputAssociation> dataInputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation dia : dataInputAssociations) {
              if (dia.getTargetRef().getId().endsWith("TaskNameInput")) {
                ((FormalExpression) dia.getAssignment().get(0).getFrom()).setBody("ReadOnlyService");
              }
            }
          }
        }
      }
      else if (fe instanceof FlowElementsContainer) {
        updateTaskDataInputs((FlowElementsContainer) fe, def);
      }
    }
  }
}

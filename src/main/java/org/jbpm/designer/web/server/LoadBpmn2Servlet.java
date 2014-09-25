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
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;

/**
 * @author Golovlyev
 */
public class LoadBpmn2Servlet extends HttpServlet {
  private static final long serialVersionUID = -1095623166420186064L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String procDefPath = req.getParameter("procDefPath");
    String profileParam = req.getParameter("profile");
    String pp = req.getParameter("pp");
    String uuid = req.getParameter("uuid");


    File file = new File(procDefPath);
    String bpmnXmlStr = FileUtils.readFileToString(file);

    IDiagramProfile profile = ServletUtil.getProfile(req, profileParam, getServletContext());
    // fix package name if needed
    String[] packageAssetName = ServletUtil.findPackageAndAssetInfo(uuid, profile);
    String packageName = packageAssetName[0];
    Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmnXmlStr);
    if (def != null) {
      List<RootElement> rootElements = def.getRootElements();
      for (RootElement root : rootElements) {
        if (root instanceof org.eclipse.bpmn2.Process) {
          Process process = (Process) root;
          Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
          FeatureMap.Entry toDeleteFeature = null;
          while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if ("packageName".equals(entry.getEStructuralFeature().getName())) {
              String pname = (String) entry.getValue();
              if (pname == null || !pname.equals(packageName)) {
                toDeleteFeature = entry;
              }
            }
          }
          if (toDeleteFeature != null) {
            process.getAnyAttribute().remove(toDeleteFeature);
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature("http://www.jboss.org/drools", "packageName", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute, packageName);
            process.getAnyAttribute().add(extensionEntry);
          }
        }
      }
      // get the xml from Definitions
      ResourceSet rSet = new ResourceSetImpl();
      rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
      JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
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
}

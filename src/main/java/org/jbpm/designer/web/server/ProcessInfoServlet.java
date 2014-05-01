package org.jbpm.designer.web.server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;

/** 
 * 
 * Queries repository to get the process information.
 * 
 * @author Tihomir Surdilovic
 */
public class ProcessInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(ProcessInfoServlet.class);
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");

        String gatewayId = req.getParameter("gatewayid");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());

        if(gatewayId != null && gatewayId.length() > 0) {
            String json = req.getParameter("json");
            String preprocessingData = req.getParameter("ppdata");
            DroolsFactoryImpl.init();

            Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
            Definitions def = ((Definitions) unmarshaller.unmarshall(json, preprocessingData).getContents().get(0));
            List<String> outgoingInfo= new ArrayList<String>();
            List<RootElement> rootElements =  def.getRootElements();
            for(RootElement root : rootElements) {
                if(root instanceof Process) {
                    getAllOutgoing((Process) root, def, outgoingInfo, gatewayId);
                }
            }

            StringBuffer buff = new StringBuffer();
            buff.append("[");
            for(String nextOutgoingValue : outgoingInfo) {
                buff.append("{");
                buff.append("\"sequenceflowinfo\":").append("\"").append(nextOutgoingValue).append("\"");
                buff.append("}");
                buff.append(",");
            }

            String outputStr = buff.toString();
            if(outputStr.endsWith(",")) {
                outputStr = outputStr.substring(0, outputStr.length() - 1);
            }

            outputStr += "]";

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().print(outputStr);

        } else {
            try {
                // find out what package the uuid belongs to
                String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
                String packageName = packageAssetInfo[0];
                String assetName = packageAssetInfo[1];
                Map<String, String> processInfo = getProcessInfo(packageName, assetName, uuid, profile);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/html");
                resp.getWriter().write(createHtmlTable(processInfo));
            } catch (Exception e) {
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/html");
                resp.getWriter().write("<center><b>Unable to retrieve process information.</b></center>");
            }
        }
	}

    private void getAllOutgoing(FlowElementsContainer container, Definitions def, List<String> outgoingInfo, String gatewayId) {
        List<FlowElement> flowElements = container.getFlowElements();
        for(FlowElement fe : flowElements) {
            if(fe instanceof Gateway) {
                Gateway gw = (Gateway) fe;
                if(gw.getId().equals(gatewayId)) {
                    List<SequenceFlow> outgoingFlows = gw.getOutgoing();
                    if(outgoingFlows != null) {
                        for(SequenceFlow sf : outgoingFlows) {
                            if(sf.getName() != null && sf.getName().length() > 0) {
                                outgoingInfo.add(sf.getName() + " : " + sf.getId());
                            } else {
                                outgoingInfo.add(sf.getId());
                            }
                        }
                    }
                }

            } else if(fe instanceof FlowElementsContainer) {
                getAllOutgoing((FlowElementsContainer) fe, def, outgoingInfo, gatewayId);
            }
        }
    }
	
	private String createHtmlTable(Map<String, String> processInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"0\" width=\"100%\">");
		Iterator<String> keyIterator = processInfo.keySet().iterator();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next();
			sb.append("<tr>");
			sb.append("<td><b>").append(key).append(":").append("</b></td>");
			sb.append("<td>").append(processInfo.get(key)).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	private Map<String, String> getProcessInfo(String packageName, String assetName, String uuid, IDiagramProfile profile) throws Exception {
		Map<String, String> infoMap = new LinkedHashMap<String, String>();
		infoMap.put("Name", assetName);
		infoMap.put("Format", "");
		infoMap.put("Package", packageName);
		infoMap.put("Created", "");
		infoMap.put("Created By", "");
		infoMap.put("Last Modified", "");
		infoMap.put("Comment", "");
		infoMap.put("Version", "");
		
		String assetInfoURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName;
		XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory
               .createXMLStreamReader(ServletUtil.getInputStreamForURL(assetInfoURL,
                       "GET", profile), "UTF-8");
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.START_ELEMENT) {
                if ("format".equals(reader.getLocalName())) {
                    infoMap.put("Format", reader.getElementText());
                }
                if ("checkInComment".equals(reader.getLocalName())) {
                    infoMap.put("Comment", reader.getElementText());
                }
                if ("created".equals(reader.getLocalName())) {
                    infoMap.put("Created", reader.getElementText());
                }
                if ("createdBy".equals(reader.getLocalName())) {
                    infoMap.put("Created By", reader.getElementText());
                }
                if ("lastModified".equals(reader.getLocalName())) {
                    infoMap.put("Last Modified", reader.getElementText());
                }
                if ("version".equals(reader.getLocalName())) {
                    infoMap.put("Version", reader.getElementText());
                }
            }
        }
        return infoMap;
	}
}

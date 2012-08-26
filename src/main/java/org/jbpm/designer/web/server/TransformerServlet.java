/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.ParsedURL;
import org.apache.commons.io.IOUtils;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.web.batikprotocolhandler.GuvnorParsedURLProtocolHandler;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jbpm.migration.JbpmMigration;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Encoder;

/** 
 * 
 * Transformer for svg process representation to
 * various formats.
 * 
 * @author Tihomir Surdilovic
 */
public class TransformerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger
            .getLogger(TransformerServlet.class);
    private static final String TO_PDF = "pdf";
    private static final String TO_PNG = "png";
    private static final String JPDL_TO_BPMN2 = "jpdl2bpmn2";
    private static final String BPMN2_TO_JSON = "bpmn2json";
    private static final String RESPACTION_SHOWURL = "showurl";
    private static final String RESPACTION_SHOWEMBEDDABLE = "showembeddable";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String formattedSvg = req.getParameter("fsvg");
        String rawSvg = req.getParameter("rsvg");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String transformto = req.getParameter("transformto");
        String jpdl = req.getParameter("jpdl");
        String gpd = req.getParameter("gpd");
        String bpmn2in = req.getParameter("bpmn2");
        String respaction = req.getParameter("respaction");
        String pp = req.getParameter("pp");
        String processid = req.getParameter("processid");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());

        if (transformto != null && transformto.equals(TO_PDF)) {
            try {
            	if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {
            		ByteArrayOutputStream bout = new ByteArrayOutputStream();
            		PDFTranscoder t = new PDFTranscoder();
            		TranscoderInput input = new TranscoderInput(new StringReader(formattedSvg));
            		TranscoderOutput output = new TranscoderOutput(bout);
            		t.transcode(input, output);
	                resp.setCharacterEncoding("UTF-8");
                	resp.setContentType("text/plain");
                	resp.getWriter().write("<object data=\"data:application/pdf;base64," + Base64.encodeBase64(bout.toByteArray()) +  "\" type=\"application/pdf\"></object>");
            	} else {
	                storeToGuvnor(uuid, profile, formattedSvg, rawSvg,
	                        transformto, processid);
	                
	                resp.setContentType("application/pdf");
	                if (processid != null) {
	                    resp.setHeader("Content-Disposition",
	                            "attachment; filename=\"" + processid + ".pdf\"");
	                } else {
	                    resp.setHeader("Content-Disposition",
	                            "attachment; filename=\"" + uuid + ".pdf\"");
	                }
	                
	                PDFTranscoder t = new PDFTranscoder();
	                TranscoderInput input = new TranscoderInput(new StringReader(
	                		formattedSvg));
	                TranscoderOutput output = new TranscoderOutput(
	                		resp.getOutputStream());
	                t.transcode(input, output);
            	}
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
        } else if (transformto != null && transformto.equals(TO_PNG)) {
            try {
                if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {
                	ByteArrayOutputStream bout = new ByteArrayOutputStream();
                	PNGTranscoder t = new PNGTranscoder();
                	t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                	TranscoderInput input = new TranscoderInput(new StringReader(formattedSvg));
                	TranscoderOutput output = new TranscoderOutput(bout);
                	t.transcode(input, output);
                	resp.setCharacterEncoding("UTF-8");
                	resp.setContentType("text/plain");
                	BASE64Encoder enc = new sun.misc.BASE64Encoder();
                	resp.getWriter().write("<img src=\"data:image/png;base64," + enc.encode(bout.toByteArray()) + "\">");
                } else {
                	ParsedURL.registerHandler(new GuvnorParsedURLProtocolHandler(profile));
                    storeToGuvnor(uuid, profile, formattedSvg, rawSvg,
                            transformto, processid);
                	resp.setContentType("image/png");
                	if (processid != null) {
                		resp.setHeader("Content-Disposition",
                				"attachment; filename=\"" + processid + ".png\"");
                	} else {
                		resp.setHeader("Content-Disposition",
                				"attachment; filename=\"" + uuid + ".png\"");
                	}
                
                	PNGTranscoder t = new PNGTranscoder();
                	t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                	TranscoderInput input = new TranscoderInput(new StringReader(
                			formattedSvg));
                	TranscoderOutput output = new TranscoderOutput(
                			resp.getOutputStream());
                	t.transcode(input, output);
                }
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
        } else if (transformto != null && transformto.equals(JPDL_TO_BPMN2)) { 
        	String bpmn2 = JbpmMigration.transform(jpdl);
        	Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmn2);
        	// add bpmndi info to Definitions with help of gpd
        	addBpmnDiInfo(def, gpd);
        	// hack for now
        	revisitSequenceFlows(def, bpmn2);
        	// another hack if id == name
        	revisitNodeNames(def);
        	
        	// get the xml from Definitions
        	ResourceSet rSet = new ResourceSetImpl();
            rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
            JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
            rSet.getResources().add(bpmn2resource);
            bpmn2resource.getContents().add(def);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bpmn2resource.save(outputStream, new HashMap<Object, Object>());
            String fullXmlModel =  outputStream.toString();
        	// convert to json and write response
        	String json = profile.createUnmarshaller().parseModel(fullXmlModel, profile, pp);
        	resp.setContentType("application/json");
        	resp.getWriter().print(json);
        }  else if (transformto != null && transformto.equals(BPMN2_TO_JSON)) { 
        	String json = profile.createUnmarshaller().parseModel(bpmn2in, profile, pp);
        	resp.setContentType("application/json");
			resp.getWriter().print(json);
        } else if(transformto == null && respaction != null && respaction.equals(RESPACTION_SHOWEMBEDDABLE)) {
        	resp.setCharacterEncoding("UTF-8");
        	resp.setContentType("text/plain");
        	String editorURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/org.drools.guvnor.Guvnor/standaloneEditorServlet?assetsUUIDs="
                    + uuid
                    + "&client=oryx";
        	resp.getWriter().write("<iframe id=\"processFrame\" src=\"" + editorURL + "\" width=\"650\" height=\"580\"/>");
        }
    }
    
    private void revisitNodeNames(Definitions def) {
    	List<RootElement> rootElements =  def.getRootElements();
		for(RootElement root : rootElements) {
			if(root instanceof Process) {
				Process process = (Process) root;
		        List<FlowElement> flowElements = process.getFlowElements();
		        for(FlowElement fe : flowElements) {
		        	if(fe.getName() != null && fe.getId().equals(fe.getName())) {
		        		// change the name so they are not the same
		        		fe.setName("_" + fe.getName());
		        	}
		        }
			}
		}
    }
    
    private void revisitSequenceFlows(Definitions def, String orig) {
    	try {
			Map<String, Map<String, String>> sequenceFlowMapping = new HashMap<String, Map<String,String>>();
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(orig));
			while(reader.hasNext()) {
				if (reader.next() == XMLStreamReader.START_ELEMENT) {
					if ("sequenceFlow".equals(reader.getLocalName())) {
						String id = "";
						String source = "";
						String target = "";
						for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
							if ("id".equals(reader.getAttributeLocalName(i))) {
								id = reader.getAttributeValue(i);
							}
							if ("sourceRef".equals(reader.getAttributeLocalName(i))) {
								source = reader.getAttributeValue(i);
							}
							if ("targetRef".equals(reader.getAttributeLocalName(i))) {
								target = reader.getAttributeValue(i);
							}
						}
						Map<String, String> valueMap = new HashMap<String, String>();
						valueMap.put("sourceRef", source);
						valueMap.put("targetRef", target);
						sequenceFlowMapping.put(id, valueMap);
					}
				}
			}
			List<RootElement> rootElements =  def.getRootElements();
			for(RootElement root : rootElements) {
				if(root instanceof Process) {
					Process process = (Process) root;
			        List<FlowElement> flowElements = process.getFlowElements();
			        for(FlowElement fe : flowElements) {
			        	if(fe instanceof SequenceFlow) {
			        		SequenceFlow sf = (SequenceFlow) fe;
			        		if(sequenceFlowMapping.containsKey(sf.getId())) {
			        			sf.setSourceRef(getFlowNode(def, sequenceFlowMapping.get(sf.getId()).get("sourceRef")));
			        			sf.setTargetRef(getFlowNode(def, sequenceFlowMapping.get(sf.getId()).get("targetRef")));
			        		} else {
			        			_logger.error("Could not find mapping for sequenceFlow: " + sf.getId());
			        		}
			        	}
			        }
				}
			}
		} catch (FactoryConfigurationError e) {
			_logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			_logger.error(e.getMessage());
			e.printStackTrace();
		}
    }
    
    private FlowNode getFlowNode(Definitions def, String nodeId) {
    	List<RootElement> rootElements =  def.getRootElements();
    	for(RootElement root : rootElements) {
        	if(root instanceof Process) {
        		Process process = (Process) root;
                List<FlowElement> flowElements = process.getFlowElements();
                for(FlowElement fe : flowElements) {
                	if(fe instanceof FlowNode) {
                		if(fe.getId().equals(nodeId)) {
                    		return (FlowNode) fe;
                    	}
                	}
                }
        	}
    	}
    	return null;
    }
    
    private void addBpmnDiInfo(Definitions def, String gpd) {
    	try {
    		Map<String, Bounds> _bounds = new HashMap<String, Bounds>();
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(gpd));
			while(reader.hasNext()) {
				if (reader.next() == XMLStreamReader.START_ELEMENT) {
					if ("node".equals(reader.getLocalName())) {
						Bounds b = DcFactory.eINSTANCE.createBounds();
						String nodeName = null;
						String nodeX = null;
						String nodeY = null;
						String nodeWidth = null;
						String nodeHeight = null;
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("name".equals(reader.getAttributeLocalName(i))) {
                            	nodeName = reader.getAttributeValue(i);
                            } else if("x".equals(reader.getAttributeLocalName(i))) {
                            	nodeX = reader.getAttributeValue(i);
                            } else if("y".equals(reader.getAttributeLocalName(i))) {
                            	nodeY = reader.getAttributeValue(i);
                            } else if("width".equals(reader.getAttributeLocalName(i))) {
                            	nodeWidth = reader.getAttributeValue(i);
                            } else if("height".equals(reader.getAttributeLocalName(i))) {
                            	nodeHeight = reader.getAttributeValue(i);
                            }
                        }
                        b.setX(new Float(nodeX).floatValue());
                        b.setY(new Float(nodeY).floatValue());
                        b.setWidth(new Float(nodeWidth).floatValue());
                        b.setHeight(new Float(nodeHeight).floatValue());
                        _bounds.put(nodeName, b);
                    }
				}
			}
			
			for (RootElement rootElement: def.getRootElements()) {
	    		if (rootElement instanceof Process) {
	    			Process process = (Process) rootElement;
	    			BpmnDiFactory diFactory = BpmnDiFactory.eINSTANCE;
	        		BPMNDiagram diagram = diFactory.createBPMNDiagram();
	        		BPMNPlane plane = diFactory.createBPMNPlane();
	        		plane.setBpmnElement(process);
	        		diagram.setPlane(plane);
	        		for (FlowElement flowElement: process.getFlowElements()) {
	        			if (flowElement instanceof FlowNode) {
	        				Bounds b = _bounds.get(flowElement.getId());
	        				if (b != null) {
	        					BPMNShape shape = diFactory.createBPMNShape();
	        					shape.setBpmnElement(flowElement);
	        					shape.setBounds(b);
	        					plane.getPlaneElement().add(shape);
	        				}
	        			} else if (flowElement instanceof SequenceFlow) {
	        				SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
	        				BPMNEdge edge = diFactory.createBPMNEdge();
	    					edge.setBpmnElement(flowElement);
	    					DcFactory dcFactory = DcFactory.eINSTANCE;
	    					Point point = dcFactory.createPoint();
	    					if(sequenceFlow.getSourceRef() != null) {
	    						Bounds sourceBounds = _bounds.get(sequenceFlow.getSourceRef().getId());
	    						point.setX(sourceBounds.getX() + (sourceBounds.getWidth()/2));
	    						point.setY(sourceBounds.getY() + (sourceBounds.getHeight()/2));
	    					}
	    					edge.getWaypoint().add(point);
//	    					List<Point> dockers = _dockers.get(sequenceFlow.getId());
//	    					for (int i = 1; i < dockers.size() - 1; i++) {
//	    						edge.getWaypoint().add(dockers.get(i));
//	    					}
	    					point = dcFactory.createPoint();
	    					if(sequenceFlow.getTargetRef() != null) {
	    						Bounds targetBounds = _bounds.get(sequenceFlow.getTargetRef().getId());
	    						point.setX(targetBounds.getX() + (targetBounds.getWidth()/2));
	    						point.setY(targetBounds.getY() + (targetBounds.getHeight()/2));
	    					}
	    					edge.getWaypoint().add(point);
	    					plane.getPlaneElement().add(edge);
	        			}
	        		}

	        		def.getDiagrams().add(diagram);
	    		}
			} 
		} catch (FactoryConfigurationError e) {
			_logger.error("Exception adding bpmndi info: " + e.getMessage());
		} catch (Exception e) {
			_logger.error("Exception adding bpmndi info: " + e.getMessage());
		}
    }
    
    private void storeToGuvnor(String uuid, IDiagramProfile profile,
            String formattedSvg, String rawSvg, String transformto, String processid) {
        String[] packageAssetName =  ServletUtil.findPackageAndAssetInfo(uuid, profile);
        String processContent = getProcessContent(packageAssetName[0],
                packageAssetName[1], uuid, profile);
        if(processid != null) {
        	guvnorStore(packageAssetName[0], processid,
                    profile, formattedSvg, rawSvg, transformto);
        }
    }

    private void guvnorStore(String packageName, String assetName,
            IDiagramProfile profile, String formattedSvg, String rawSvg, String transformto) {
        try {
            String assetExt = "";
            String assetFileExt = "";
            if (transformto.equals(TO_PDF)) {
                assetExt = "-pdf";
                assetFileExt = ".pdf";
            }
            if (transformto.equals(TO_PNG)) {
                assetExt = "-image";
                assetFileExt = ".png";
            }

            String pngURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName
                    + assetExt;

            String packageAssetsURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/";

            String deleteURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName
                    + assetExt;

            // check if the image already exists
            URL checkURL = new URL(pngURL);
            HttpURLConnection checkConnection = (HttpURLConnection) checkURL
                    .openConnection();
            ServletUtil.applyAuth(profile, checkConnection);
            checkConnection.setRequestMethod("GET");
            checkConnection
                    .setRequestProperty("Accept", "application/atom+xml");
            checkConnection.connect();
            _logger.info("check connection response code: " + checkConnection.getResponseCode());
            if (checkConnection.getResponseCode() == 200) {
                URL deleteAssetURL = new URL(deleteURL);
                HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
                        .openConnection();
                ServletUtil.applyAuth(profile, deleteConnection);
                deleteConnection.setRequestMethod("DELETE");
                deleteConnection.connect();
                _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
            }
            // create new
            URL createURL = new URL(packageAssetsURL);
            HttpURLConnection createConnection = (HttpURLConnection) createURL
                    .openConnection();
            ServletUtil.applyAuth(profile, createConnection);
            createConnection.setRequestMethod("POST");
            createConnection.setRequestProperty("Content-Type",
                    "application/octet-stream");
            createConnection.setRequestProperty("Accept",
                    "application/atom+xml");
            createConnection.setRequestProperty("Slug", assetName + assetExt + assetFileExt);
            createConnection.setDoOutput(true);

            if (transformto.equals(TO_PDF)) {
            	PDFTranscoder t = new PDFTranscoder();
            	TranscoderInput input = new TranscoderInput(new StringReader(
            			rawSvg));
            	TranscoderOutput output = new TranscoderOutput(
            			createConnection.getOutputStream());
            	t.transcode(input, output);
            }

            if (transformto.equals(TO_PNG)) {
            	PNGTranscoder t = new PNGTranscoder();
            	t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
            	TranscoderInput input = new TranscoderInput(new StringReader(
            			rawSvg));
            	TranscoderOutput output = new TranscoderOutput(
            			createConnection.getOutputStream());
            	try {
					t.transcode(input, output);
				} catch (Exception e) {
					// issue with batik here..do not make a big deal
					_logger.debug(e.getMessage());
				}
            }
            createConnection.connect();
            _logger.info("create connection response code: " + createConnection.getResponseCode());
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
    }

    private String getProcessContent(String packageName, String assetName,
            String uuid, IDiagramProfile profile) {
    	try {
	    	String assetSourceURL = ExternalInfo.getExternalProtocol(profile)
	                + "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	                        profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName
	                + "/source/";
	        
            InputStream in = ServletUtil.getInputStreamForURL(assetSourceURL, "GET",
                    profile);
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer);
            return writer.toString();
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return "";
        }
    }

    private Definitions getDefinitions(String xml) {
        try {
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet
                    .getResourceFactoryRegistry()
                    .getExtensionToFactoryMap()
                    .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                            new JBPMBpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put(
                    "http://www.omg.org/spec/BPMN/20100524/MODEL",
                    Bpmn2Package.eINSTANCE);
            XMLResource resource = (XMLResource) resourceSet.createResource(URI
                    .createURI("inputStream://dummyUriWithValidSuffix.xml"));
            resource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING,
                    "UTF-8");
            resource.setEncoding("UTF-8");
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(XMLResource.OPTION_ENCODING, "UTF-8");
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            resource.load(is, options);
            return ((DocumentRoot) resource.getContents().get(0))
                    .getDefinitions();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}

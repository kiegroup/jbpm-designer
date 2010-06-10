package de.hpi.xforms.generation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class WSDL2XFormsTransformation {
	
	public static List<Document> transform(ServletContext context, Node node, String wsdlId) {
		List<Document> result = new ArrayList<Document>();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {

			builder = docFactory.newDocumentBuilder();
		
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setURIResolver(new XsltUriResolver(context));
		
			/*InputStream xsltStream = context.getResourceAsStream("/WEB-INF/lib/wsdl2xforms.xsl");
			Templates pss = factory.newTemplates(new StreamSource(xsltStream));
			Transformer transformer = pss.newTransformer();*/
			
			InputStream xsltStream = context.getResourceAsStream("/WEB-INF/lib/wsdl2xforms.xsl");
			Transformer transformer = factory.newTransformer(new StreamSource(xsltStream));
			
		/*	String tmpDirPath = context.getRealPath("generated-forms/" + wsdlId);
		    if (!(new File(tmpDirPath)).mkdirs())
		    	System.out.println("Could not create XSLT output directory."); */
			
			Document outDoc = builder.newDocument();
			Node outNode = outDoc.createElement("generated-forms");
			outDoc.appendChild(outNode);
			
			transformer.setParameter("wsdlid", wsdlId);
			//transformer.setParameter("outdir", tmpDirPath);
	        transformer.transform(new DOMSource(node), new DOMResult(outNode));
	        
	        List<String> returnDocsInstanceIds = new ArrayList<String>();
	        if(outNode.hasChildNodes()) {
	        	for(Node child = outNode.getFirstChild();
	        			child!=null; child = child.getNextSibling()) {
	        		
	        		// filter out junk output
	        		String instanceId = getInstanceId(child);
	        		if(!instanceId.equals(".instance")
	        			&& !getSubmissionAction(child).equals("")
	        			&& !returnDocsInstanceIds.contains(instanceId)
	        				) { 
	        			returnDocsInstanceIds.add(instanceId); // remove duplicate documents
	        			
	        			Document formDoc = builder.newDocument();
	        			Node formNode = formDoc.importNode(child, true);
	        			formDoc.appendChild(formNode);
	        			result.add(formDoc);
	        		}
	        			
	        	}
	        }
	        
	       /* File tmpDir = new File(tmpDirPath);
	        File[] files = tmpDir.listFiles();
	        for(int i=0; i<files.length; i++) {
	        	result.add(builder.parse(files[i]));
	        	files[i].delete();
	        }
	        tmpDir.delete();*/
	        
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
        //System.out.println("RESULTSIZE: " +result.size());
		return result;
	}
	
	private static String getInstanceId(Node n) {
		
		Node instanceNode = getChild(getChild(getChild(n, "xhtml:head"), "xforms:model"), "xforms:instance");
		if(instanceNode!=null) {
			return getAttributeValue(instanceNode, "id");
		}
		return null;
	}
	
	private static String getSubmissionAction(Node n) {
		
		Node instanceNode = getChild(getChild(getChild(n, "xhtml:head"), "xforms:model"), "xforms:submission");
		if(instanceNode!=null) {
			return getAttributeValue(instanceNode, "action");
		}
		return null;
	}
	
	private static Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}
	
	private static String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}
	
}

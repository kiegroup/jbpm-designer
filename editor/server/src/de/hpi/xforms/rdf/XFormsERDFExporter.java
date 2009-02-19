package de.hpi.xforms.rdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.hpi.xforms.AbstractAction;
import de.hpi.xforms.ActionContainer;
import de.hpi.xforms.Alert;
import de.hpi.xforms.Bind;
import de.hpi.xforms.Case;
import de.hpi.xforms.Copy;
import de.hpi.xforms.Help;
import de.hpi.xforms.Hint;
import de.hpi.xforms.Item;
import de.hpi.xforms.Itemset;
import de.hpi.xforms.Label;
import de.hpi.xforms.LabelContainer;
import de.hpi.xforms.ListUICommon;
import de.hpi.xforms.ListUICommonContainer;
import de.hpi.xforms.PCDataContainer;
import de.hpi.xforms.Submission;
import de.hpi.xforms.Switch;
import de.hpi.xforms.UICommonContainer;
import de.hpi.xforms.UIElementContainer;
import de.hpi.xforms.Value;
import de.hpi.xforms.XForm;
import de.hpi.xforms.XFormsElement;
import de.hpi.xforms.XFormsUIElement;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 * 
 */
public class XFormsERDFExporter {
	
	private static final String STENCILSET_URI = "http://b3mn.org/stencilset/xforms";
	private static final int DISTANCE_FACTOR = 10000; // a value > maximum stencil height
	
	private class ExportContext {
		
		private XForm form;
		private Integer resourceId;
		private Map<XFormsElement, XFormsElement> parentRelationships;
		
		public ExportContext(XForm form) {
			this.form = form;
			this.form.setResourceId("oryx-canvas123");
			this.resourceId = 0;
			this.parentRelationships = new HashMap<XFormsElement, XFormsElement>();
		}

		private void registerResource(XFormsElement element, XFormsElement parent) {
			element.setResourceId(resourceId.toString());
			parentRelationships.put(element, parent);
			resourceId++;
		}
		
		public List<String> getResourceIds() {
			List<String> resourceIds = new ArrayList<String>();
			for(XFormsElement e : this.getRegisteredElements())
				resourceIds.add(e.getResourceId());
			return resourceIds;
		}
		
		public Set<XFormsElement> getRegisteredElements() {
			return this.parentRelationships.keySet();
		}
		
		public XFormsElement getParent(XFormsElement element) {
			return parentRelationships.get(element);
		}
		
		public XForm getForm() {
			return form;
		}
		
	}
	
	private ExportContext context;
	
	public XFormsERDFExporter(XForm form) {
		super();
		context = new ExportContext(form);
	}
	
	public void exportERDF(Writer writer) {
		
		for(XFormsUIElement element : context.getForm().getChildElements()) {
			registerResourcesRecursive(element, context.getForm());
		}
		
		try {
			writer.append("<div class=\"processdata\">");
			appendFormERDF(writer);
			
			for(XFormsElement element : context.getRegisteredElements()) {
				appendElementERDF(writer, element);
			}
			
			writer.append("</div>");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Recursively traverse all elements with stencil representations and register them - and parent
	 * relationships between them - on export context 
	 * @param element
	 */
	private void registerResourcesRecursive(XFormsElement element, XFormsElement parent) {
		context.registerResource(element, parent);
		
		if(element instanceof UIElementContainer) {
			for(XFormsUIElement child : ((UIElementContainer) element).getChildElements()) {
				registerResourcesRecursive(child, element);
			}
		}
		if(element instanceof ListUICommonContainer) {
			for(ListUICommon child : ((ListUICommonContainer) element).getListUICommons()) {
				registerResourcesRecursive(child, element);
			}
		}
		if(element instanceof ActionContainer) {
			for(AbstractAction child : ((ActionContainer) element).getActions()) {
				registerResourcesRecursive(child, element);
			}
		}
		if(element instanceof Switch) {
			for(Case child : ((Switch) element).getCases()) {
				registerResourcesRecursive(child, element);
			}
		}
		if(element instanceof LabelContainer) {
			Label label = ((LabelContainer) element).getLabel();
			if(label!=null)
				registerResourcesRecursive(label, element);
		}
		if(element instanceof UICommonContainer) {
			
			Help help = ((UICommonContainer) element).getHelp();
			if(help!=null)
				registerResourcesRecursive(help, element);
			
			Hint hint = ((UICommonContainer) element).getHint();
			if(hint!=null)
				registerResourcesRecursive(hint, element);
			
			Alert alert = ((UICommonContainer) element).getAlert();
			if(alert!=null)
				registerResourcesRecursive(alert, element);
			
		}
	}
	
	private void appendFormERDF(Writer writer) throws IOException {
		String name = context.getForm().getAttributes().get("name");
		if(name==null) name = "";
		
		String head = "";
		if(context.getForm().getHead()!=null) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				XMLSerializer serializer = new XMLSerializer();
				serializer.setOutputByteStream(stream);
				serializer.asDOMSerializer();
				serializer.setNamespaces(true);
				serializer.serialize(context.getForm().getHead());
				head = StringEscapeUtils.escapeXml(stream.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String nsDeclarations = "";
		for(String key : context.getForm().getNSDeclarations().keySet()) {
			nsDeclarations += "["  + key + "," + context.getForm().getNSDeclarations().get(key) + "]";
		}
		
		writer.append("<div id=\""+ context.getForm().getResourceId() +"\" class=\"-oryx-canvas\">");
		appendOryxField(writer, "type", STENCILSET_URI + "#" + context.getForm().getStencilId());
		appendXFormsField(writer, "id", "");
		appendXFormsField(writer, "name", name);
		appendXFormsField(writer, "version", "");
		appendXFormsField(writer, "head", head);
		appendXFormsField(writer, "nsdeclarations", nsDeclarations);
		appendOryxField(writer, "mode", "writable");
		appendOryxField(writer, "mode", "fullscreen");
		writer.append("<a rel=\"oryx-stencilset\" href=\"/oryx/stencilsets/xforms/xforms.json\"/>"); // TODO: HACK TO MAKE IT WORK FOR NOW

		for(String id : context.getResourceIds()){
			writer.append("<a rel=\"oryx-render\" href=\"#" + id + "\"/>");
		}
		
		writer.append("</div>");
	}
	
	private void appendElementERDF(Writer writer, XFormsElement element) throws IOException {
		writer.append("<div id=\""+ element.getResourceId() +"\">");
		appendOryxField(writer,"type",STENCILSET_URI + "#" + element.getStencilId());
		
		for(String field : element.getAttributes().keySet()) {
			if(!field.equals("bind"))
				appendXFormsField(writer, field, element.getAttributes().get(field));
		}
		
		// handle PCData containers
		if(element instanceof PCDataContainer) {
			String content = ((PCDataContainer) element).getContent();
			if(element instanceof Label) {
				appendXFormsField(writer, "text", content);
			} else {
				appendXFormsField(writer, "message", content);
			}
		}
		
		// handle model item properties
		String ref = element.getAttributes().get("ref");
		if(ref!=null) {
			
			if(!ref.startsWith("/") && (element instanceof XFormsUIElement)) {
				ref = getNodesetContext((XFormsUIElement) element) + ref;
			}
			
			Bind bind = getBindByNodeset(ref);
			
			if(bind!=null) {
				for(String field : bind.getAttributes().keySet()) {
					if(!field.equals("id") && !field.equals("nodeset"))
						appendXFormsField(writer, field, bind.getAttributes().get(field));
				}
			}
		}
		
		// handle submission properties
		String submissionId = element.getAttributes().get("submission");
		if(submissionId!=null) {
			Submission submission = getSubmissionById(submissionId);
			if(submission!=null) {
				for(String field : submission.getAttributes().keySet()) {
					if(!field.equals("id"))
						appendXFormsField(writer, field, submission.getAttributes().get(field));
				}
			}
		}
		
		// handle item element
		if(element instanceof Item) {
			Item item = (Item) element;
			Value value = item.getValue();
			if(value!=null) {
				for(String field : value.getAttributes().keySet()) {
					if(!field.equals("id"))
						appendXFormsField(writer, "value_" + field, value.getAttributes().get(field));
				}
			}
			appendOryxField(writer, "bounds", "0," + item.getYPosition() + ",0," + item.getYPosition());
		}
		
		// handle itemset element
		if(element instanceof Itemset) {
			Itemset itemset = (Itemset) element;
			Value value = itemset.getValue();
			if(value!=null) {
				for(String field : value.getAttributes().keySet()) {
					appendXFormsField(writer, "value_" + field, value.getAttributes().get(field));
				}
			}
			Copy copy = ((Itemset) element).getCopy();
			if(copy!=null) {
				for(String field : copy.getAttributes().keySet()) {
					appendXFormsField(writer, "copy_" + field, copy.getAttributes().get(field));
				}
			}
			appendOryxField(writer, "bounds", "0," + itemset.getYPosition() + ",0," + itemset.getYPosition());
		}
		
		if(element instanceof XFormsUIElement) {
			int x = ((XFormsUIElement) element).getXPosition();
			int y = (((XFormsUIElement) element).getYPosition() + 10) * DISTANCE_FACTOR;
			appendOryxField(writer, "bounds", x + "," + y + "," + x + "," + y );
		}
		
		writer.append("<a rel=\"raziel-parent\" href=\"#" + context.getParent(element).getResourceId() + "\"/>");
		writer.append("</div>");
	}
	
	private void appendOryxField(Writer writer, String field, String entry) throws IOException {
		writer.append("<span class=\"oryx-");
		writer.append(field);
		if (entry != null) {
			writer.append("\">");
			writer.append(StringEscapeUtils.escapeXml(entry));
			writer.append("</span>");
		}
		else {
			writer.append("\"/>");
		}
	}
	
	private void appendXFormsField(Writer writer, String field, String entry) throws IOException {
		if(field.startsWith("ev:")) {
			writer.append("<span class=\"oryx-ev_");
			writer.append(field.substring(3));
		} else {
			writer.append("<span class=\"oryx-xf_");
			writer.append(field);
		}
		if (entry != null) {
			writer.append("\">");
			writer.append(StringEscapeUtils.escapeXml(entry));
			writer.append("</span>");
		}
		else {
			writer.append("\"/>");
		}
	}
	
	private Bind getBindByNodeset(String nodeset) {
		if(context.getForm().getModel()==null) return null;
		for(Bind bind : context.getForm().getModel().getBinds()) {
			Bind result = getBindByNodesetRecursive(bind, nodeset, "");
			if(result!=null) return result;
		}
		return null;
	}
	
	private Bind getBindByNodesetRecursive(Bind bind, String searchedNodeset, String accNodeset) {
		String ns = bind.getAttributes().get("nodeset");
		if(ns!=null)
			accNodeset += "/" + ns;
		
		if(accNodeset.equals(searchedNodeset) || accNodeset.substring(1).equals(searchedNodeset)) {
			// found matching bind (first / is optional)
			return bind;
		} else if(bind.getBinds().size()>0) {
			// not found, continue search in child binds
			for(Bind nestedBind : bind.getBinds()) {
				 Bind result = getBindByNodesetRecursive(nestedBind, searchedNodeset, accNodeset);
				 if(result!=null) return result;
			}
			return null;
		} else {
			// not found, no children
			return null;
		}
	}

	private String getNodesetContext(XFormsUIElement element) {
		String nodesetContext = "";
		while(element.getParent()!=null && !(element.getParent() instanceof XForm)) {
			if(element.getParent() instanceof XFormsUIElement)
				element = (XFormsUIElement) element.getParent();
			else if(element.getParent() instanceof Case)
				element = ((Case) element.getParent()).getSwitch();
			String nodeset = element.getAttributes().get("nodeset");
			if((nodeset==null) || nodeset.equals("") || nodeset.equals("/"))
				nodeset = element.getAttributes().get("ref");
			if((nodeset!=null) && !nodeset.equals("") && !nodeset.equals("/"))
				nodesetContext = nodeset + "/" + nodesetContext;
		}
		return nodesetContext;
	}
	
	private Submission getSubmissionById(String id) {
		if(context.getForm().getModel()==null) return null;
		for(Submission submission : context.getForm().getModel().getSubmissions()) {
			if(submission.getAttributes().get("id")!=null && submission.getAttributes().get("id").equals(id))
				return submission;
		}
		return null;
	}

}

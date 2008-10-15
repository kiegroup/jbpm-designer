package de.hpi.xforms.rdf;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.xforms.AbstractAction;
import de.hpi.xforms.ActionContainer;
import de.hpi.xforms.Alert;
import de.hpi.xforms.Bind;
import de.hpi.xforms.Help;
import de.hpi.xforms.Hint;
import de.hpi.xforms.Label;
import de.hpi.xforms.LabelContainer;
import de.hpi.xforms.ListUICommon;
import de.hpi.xforms.ListUICommonContainer;
import de.hpi.xforms.PCDataContainer;
import de.hpi.xforms.UICommonContainer;
import de.hpi.xforms.UIElementContainer;
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
	
	public void exportERDF(PrintWriter writer) {
		
		for(XFormsUIElement element : context.getForm().getChildElements()) {
			registerResourcesRecursive(element, context.getForm());
		}
		
		writer.append("<div class=\"processdata\">");
		
		appendFormERDF(writer);
		
		for(XFormsElement element : context.getRegisteredElements()) {
			appendElementERDF(writer, element);
		}
		
		writer.append("</div>");
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
		if(element instanceof LabelContainer) {
			Label label = ((LabelContainer) element).getLabel();
			if(label!=null) {
				registerResourcesRecursive(label, element);
			}
		}
		if(element instanceof UICommonContainer) {
			
			Help help = ((UICommonContainer) element).getHelp();
			if(help!=null) {
				registerResourcesRecursive(help, element);
			}
			
			Hint hint = ((UICommonContainer) element).getHint();
			if(hint!=null) {
				registerResourcesRecursive(hint, element);
			}
			
			Alert alert = ((UICommonContainer) element).getAlert();
			if(alert!=null) {
				registerResourcesRecursive(alert, element);
			}
			
		}
		
	}
	
	private void appendFormERDF(PrintWriter writer) {
		String name = context.getForm().getAttributes().get("name");
		if(name==null) name = "";
		
		writer.append("<div id=\""+ context.getForm().getResourceId() +"\" class=\"-oryx-canvas\">");
		writer.append("<span class=\"oryx-type\">" + STENCILSET_URI + "#" + context.getForm().getStencilId() + "</span>");
		writer.append("<span class=\"xforms-id\"></span>");
		writer.append("<span class=\"xforms-name\">" + name + "</span>");
		writer.append("<span class=\"xforms-version\"></span>");
		writer.append("<span class=\"oryx-mode\">writable</span>");
		writer.append("<span class=\"oryx-mode\">fullscreen</span>");
		writer.append("<a rel=\"oryx-stencilset\" href=\"./stencilsets/xforms/xforms.json\"/>");

		for(String id : context.getResourceIds()){
			writer.append("<a rel=\"oryx-render\" href=\"#" + id + "\"/>");
		}
		
		writer.append("</div>");
	}
	
	private void appendElementERDF(PrintWriter writer, XFormsElement element) {
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
		String bindId = element.getAttributes().get("bind");
		if(bindId!=null) {
			Bind bind = getBindById(bindId);
			if(bind!=null) {
				for(String field : bind.getAttributes().keySet()) {
					if(!field.equals("id"))
						appendXFormsField(writer, field, bind.getAttributes().get(field));
				}
			}
		}
		
		if(element instanceof XFormsUIElement) {
			int x = ((XFormsUIElement) element).getXPosition();
			int y = ((XFormsUIElement) element).getYPosition() * DISTANCE_FACTOR;
			appendOryxField(writer, "bounds", x + "," + y + "," + x + "," + y );
		}
		
		writer.append("<a rel=\"raziel-parent\" href=\"#" + context.getParent(element).getResourceId() + "\"/>");
		writer.append("</div>");
	}
	
	private void appendOryxField(PrintWriter writer, String field, String entry) {
		writer.append("<span class=\"oryx-");
		writer.append(field);
		if (entry != null) {
			writer.append("\">");
			writer.append(entry);
			writer.append("</span>");
		}
		else {
			writer.append("\"/>");
		}
	}
	
	private void appendXFormsField(PrintWriter writer, String field, String entry) {
		writer.append("<span class=\"xforms-");
		writer.append(field);
		if (entry != null) {
			writer.append("\">");
			writer.append(entry);
			writer.append("</span>");
		}
		else {
			writer.append("\"/>");
		}
	}
	
	private Bind getBindById(String id) {
		for(Bind bind : context.getForm().getModel().getBinds()) {
			if(bind.getAttributes().get("id")!=null && bind.getAttributes().get("id").equals(id))
				return bind;
		}
		return null;
	}

}

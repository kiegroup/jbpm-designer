package de.hpi.xforms.serialization;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.xforms.*;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class XFormsXHTMLImporter {
	
	private XForm form;
	private Document doc;
	private XFormsFactory factory;
	private String[] htmlBlockElements = { "div", "p", "tr" };
	
	public XFormsXHTMLImporter(Document doc) {
		super();
		this.doc = doc;
		this.factory = new XFormsFactory();
	}
	
	public XForm getXForm() {
		form = factory.createXForm();
		Element root = doc.getDocumentElement();
		addElementsRecursive(form, root, 0);
		
		if(doc.getElementsByTagNameNS("http://www.w3.org/1999/xhtml", "head").getLength()>0) {
			form.setHead((Element) doc.getElementsByTagName("head").item(0));
		}
		
		return form;
	}
	
	private void addElementsRecursive(XFormsElement xfElement, Node node, int row) {
		for(Node child = node.getFirstChild(); 
					child != null; 
					child = child.getNextSibling()) {
			
			if(isHtmlBlockElement(child)) row++; // start new row
			if(child.getNamespaceURI()!=null && child.getNamespaceURI().equals("http://www.w3.org/2002/xforms")) {
				XFormsElement newXfElement = addXFormsElement(child.getLocalName(), xfElement, row);
				if(newXfElement!=null) {
					addAttributes(newXfElement, child);
					addElementsRecursive(newXfElement, child, 0);
				}
			} else {
				addElementsRecursive(xfElement, child, row);
			}
			
		}
	}
	
	private XFormsElement addXFormsElement(String tagName, XFormsElement parent, int row) {
		XFormsElement element = factory.createElementByTagName(tagName);
		
		if(element instanceof XFormsUIElement) {
			if(parent instanceof UIElementContainer) {
				XFormsUIElement uiElement = (XFormsUIElement) element;
				uiElement.setXPosition(((UIElementContainer) parent).getChildElements().size());
				uiElement.setYPosition(row);
				uiElement.setParent((UIElementContainer) parent);
			}
		} else if(element instanceof ListUICommon) {
			if(parent instanceof ListUICommonContainer) {
				ListUICommon listUICommon = (ListUICommon) element;
				listUICommon.setYPosition(((ListUICommonContainer) parent).getListUICommons().size());
				listUICommon.setParent((ListUICommonContainer) parent);
			}
		} else if(element instanceof AbstractAction) {
			if(parent instanceof ActionContainer) {
				ActionContainer actionContainer = (ActionContainer) parent;
				actionContainer.getActions().add((AbstractAction) element);
			}
		} else if(element instanceof Case) {
			if(parent instanceof Switch) {
				Switch switchObj = (Switch) parent;
				switchObj.getCases().add((Case) element);
			}
		} else if(element instanceof Label) {
			if(parent instanceof LabelContainer) {
				LabelContainer labelContainer = (LabelContainer) parent;
				labelContainer.setLabel((Label) element);
			}
		} else if(element instanceof Help) {
			if(parent instanceof UICommonContainer) {
				UICommonContainer uiCommonContainer = (UICommonContainer) parent;
				uiCommonContainer.setHelp((Help) element);
			}
		} else if(element instanceof Hint) {
			if(parent instanceof UICommonContainer) {
				UICommonContainer uiCommonContainer = (UICommonContainer) parent;
				uiCommonContainer.setHint((Hint) element);
			}
		} else if(element instanceof Alert) {
			if(parent instanceof UICommonContainer) {
				UICommonContainer uiCommonContainer = (UICommonContainer) parent;
				uiCommonContainer.setAlert((Alert) element);
			}
		} else if(element instanceof Value) {
			if(parent instanceof Item) {
				Item item = (Item) parent;
				item.setValue((Value) element);
			} else if(parent instanceof Itemset) {
				Itemset itemset = (Itemset) parent;
				itemset.setValue((Value) element);
			}
		} else if(element instanceof Copy) {
			if(parent instanceof Itemset) {
				Itemset itemset = (Itemset) parent;
				itemset.setCopy((Copy) element);
			}
		} else if(element instanceof Submission) {
			if(parent instanceof Model) {
				Model model = (Model) parent;
				model.getSubmissions().add((Submission) element);
			}
		} else if(element instanceof Instance) {
			if(parent instanceof Model) {
				Model model = (Model) parent;
				model.setInstance((Instance) element);
			}
		} else if(element instanceof Bind) {
			if(parent instanceof Model) {
				Model model = (Model) parent;
				model.getBinds().add((Bind) element);
			}
		} else if(element instanceof Model) {
			if(parent instanceof XForm) {
				XForm xForm = (XForm) parent;
				xForm.setModel((Model) element); // note: only single model per form supported atm
			}
		}
		
		return element;
	}
	
	private void addAttributes(XFormsElement xfElement, Node node) {
		for(String key : xfElement.getAttributes().keySet()) {
			Attr attr = ((Attr) node.getAttributes().getNamedItem(key));
			if(attr!=null)
				xfElement.getAttributes().put(key, attr.getValue());
		}
		
		if(xfElement instanceof PCDataContainer) {
			((PCDataContainer) xfElement).setContent(node.getTextContent());
		}
	}
	
	private boolean isHtmlBlockElement(Node node) {
		if(node.getNamespaceURI()==null) 
			return false;
		if(!node.getNamespaceURI().equals("http://www.w3.org/1999/xhtml"))
			return false;
		for(String blockElement : htmlBlockElements) {
			if(blockElement.equals(node.getLocalName())) return true;
		}
		return false;
	}

}

package de.hpi.xforms.rdf;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import de.hpi.xforms.*;

/**
 * Import XForm from Oryx RDF representation
 * main method: loadXForm()
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 * 
 */

public class XFormsRDFImporter {
	private static final String PREFIX = "instance('data')/";
	
	protected Document doc;
	protected Document instanceModelDoc;
	protected XFormsFactory factory;

	protected class ImportContext {
		XForm form;
		Map<String, XFormsElement> objects; // key = resource id, value = xforms element
		Map<XFormsElement, String> parentRelationships; // key = xforms element, value = parent resource id
		Map<XFormsElement, Bind> bindings; // key = xforms element, value = associated bind element
	}

	public XFormsRDFImporter(Document doc) {
		super();
		this.doc = doc;
	}
	
	public XFormsRDFImporter(Document doc, Document instanceModelDoc) {
		super();
		this.doc = doc;
		this.instanceModelDoc = instanceModelDoc;
	}
	
	public XForm loadXForm() {
		
		Node root = getRootNode(doc);
		if (root == null)
			return null;

		factory = new XFormsFactory();

		ImportContext c = new ImportContext();
		c.form = factory.createXForm();
		c.objects = new HashMap<String, XFormsElement>();
		c.parentRelationships = new HashMap<XFormsElement, String>();
		c.bindings = new HashMap<XFormsElement, Bind>();
		c.form.setModel(factory.createModel());
		c.form.setResourceId("#oryx-canvas123");
		c.objects.put("#oryx-canvas123", c.form);

		if(root.hasChildNodes()) {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				instanceModelDoc = builder.newDocument();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			
			Element element = (Element) instanceModelDoc.createElement("model");
			instanceModelDoc.appendChild(element);
						
			for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
				
				if (node instanceof Text)
					continue;

				String type = getType(node);
				if (type == null)
					continue;
				
				if (type.equals("XForm")) {
					handleAttributes(node, c.form, c);
				} else if (type.equals("Input")) {
					addInput(node, c);
				} else if (type.equals("Secret")) {
					addSecret(node, c);
				} else if (type.equals("Textarea")) {
					addTextarea(node, c);
				} else if (type.equals("Output")) {
					addOutput(node, c);
				} else if (type.equals("Upload")) {
					addUpload(node, c);
				} else if (type.equals("Range")) {
					addRange(node, c);
				} else if (type.equals("Trigger")) {
					addTrigger(node, c);
				} else if (type.equals("Submit")) {
					addSubmit(node, c);
				} else if (type.equals("Select")) {
					addSelect(node, c);
				} else if (type.equals("Select1")) {
					addSelect1(node, c);
				} else if (type.equals("Group")) {
					addGroup(node, c);
				} else if (type.equals("Repeat")) {
					addRepeat(node, c);
				} else if (type.equals("Switch")) {
					addSwitch(node, c);
				} else if (type.equals("Case")) {
					addCase(node, c);
				} else if (type.equals("Label")) {
					addLabel(node, c);
				} else if (type.equals("Help")) {
					addHelp(node, c);
				} else if (type.equals("Hint")) {
					addHint(node, c);
				} else if (type.equals("Alert")) {
					addAlert(node, c);
				} else if (type.equals("Item")) {
					addItem(node, c);
				} else if (type.equals("Itemset")) {
					addItemset(node, c);
				} else if (type.equals("Choices")) {
					addChoices(node, c);
				} else if (type.equals("Action")) {
					addAction(node, c);
				} else if (type.equals("SetValue")) {
					addSetValue(node, c);
				} else if (type.equals("Insert")) {
					addInsert(node, c);
				} else if (type.equals("Delete")) {
					addDelete(node, c);
				} else if (type.equals("SetIndex")) {
					addSetIndex(node, c);
				} else if (type.equals("Toggle")) {
					addToggle(node, c);
				} else if (type.equals("SetFocus")) {
					addSetFocus(node, c);
				} else if (type.equals("Dispatch")) {
					addDispatch(node, c);
				} else if (type.equals("Rebuild")) {
					addRebuild(node, c);
				} else if (type.equals("Recalculate")) {
					addRecalculate(node, c);
				} else if (type.equals("Revalidate")) {
					addRevalidate(node, c);
				} else if (type.equals("Refresh")) {
					addRefresh(node, c);
				} else if (type.equals("Reset")) {
					addReset(node, c);
				} else if (type.equals("Load")) {
					addLoad(node, c);
				} else if (type.equals("Send")) {
					addSend(node, c);
				} else if (type.equals("Message")) {
					addMessage(node, c);
				}          
				
			}
		}
		
		setupParentRelationships(c);
		addModel(c);
		setupBinds(c);
		return c.form;
	}
	
	private void handleAttributes(Node node, XFormsElement element, ImportContext c) {
		handleAttributes(node, element, null, c);
	}
	
	private void handleAttributes(Node node, XFormsElement element, String prefix, ImportContext c) {
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				
				String content = null;
				if(getContent(n)!=null)
					content = StringEscapeUtils.unescapeXml(getContent(n));
				
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);
				
				if(attribute.startsWith("xf_")) {
					// handle attributes of xforms namespace
					
					attribute = attribute.substring(3);
					
					if((prefix!=null) && (attribute.startsWith(prefix)))
						attribute = attribute.substring(prefix.length());
						
					if(element.getAttributes().containsKey(attribute)) {
						element.getAttributes().put(attribute, content);
					} else {
						if(element instanceof Submit) {
							handleSubmissionProperty(attribute, content, (Submit) element, c);
						} else if((element instanceof XForm) && attribute.equals("head")) {
							if(content!=null)
								addHead(content, c);
						} else if((element instanceof XForm) && attribute.equals("nsdeclarations")) {
							if(content!=null)
								addNSDeclarations(content, c);
						} else {
							handleModelItemProperty(attribute, content, element, c);
						}
					}
				} else if(attribute.startsWith("ev_")) {
					// handle attributes of xml events namespace
					attribute = "ev:" + attribute.substring(3);
					if(element.getAttributes().containsKey(attribute)) {
						element.getAttributes().put(attribute, content);
					} else {
						if(element instanceof Submit)
							handleSubmissionProperty(attribute, content, (Submit) element, c);
						else
							handleModelItemProperty(attribute, content, element, c);
					}
				} else {
					// handle oryx attributes
						
					if (attribute.equals("bounds")) {
						if(element instanceof XFormsUIElement) {
							XFormsUIElement uiElement = (XFormsUIElement) element;
							String[] bounds = getContent(n).split(",");
							uiElement.setXPosition(Integer.parseInt(bounds[0]));
							uiElement.setYPosition(Integer.parseInt(bounds[1]));
						} else if(element instanceof ListUICommon) {
							ListUICommon listUICommon = (ListUICommon) element;
							String[] bounds = getContent(n).split(",");
							listUICommon.setYPosition(Integer.parseInt(bounds[1]));
						}
					}
					if (attribute.equals("parent")) {
						c.parentRelationships.put(element, getResourceId(getAttributeValue(n, "rdf:resource")));
					}
					if (attribute.equals("nodeset") && prefix == null) {
						try {
							String path = generateCodeTable(c, content, element);
							element.getAttributes().put(attribute, path);
						} catch (JSONException e) {
							element.getAttributes().put(attribute, content);
						}
					}
				}
			}
		}
	}
	
	private void handleModelItemProperty(String attribute, String value, XFormsElement element, ImportContext c) {
		//if(!element.getAttributes().containsKey("bind")) return;
		if((value==null) || value.equals("") || value.equals("/")) return;
		Bind bind = new Bind();
		if(c.bindings.containsKey(element))
			bind = c.bindings.get(element);
		if(!bind.getAttributes().containsKey(attribute)) return;
		bind.getAttributes().put(attribute, value);
		c.bindings.put(element, bind);
	}
	
	private void handleSubmissionProperty(String attribute, String value, Submit submit, ImportContext c) {
		if(!submit.getSubmission().getAttributes().containsKey(attribute)) return;
		submit.getSubmission().getAttributes().put(attribute, value);
	}
	
	private void setupBinds(ImportContext c) {
		for(XFormsElement element : c.bindings.keySet()) {
			
			// use ref attribute of control element as nodeset attribute for bind element
			String xPath = element.getAttributes().get("ref");
			if(xPath!=null) {
				Bind bind = c.bindings.get(element);
				 
				/*String bindId = "bind_";
				if(element.getAttributes().get("id")==null)
					bindId += element.getResourceId().substring(1);
				else 
					bindId += element.getAttributes().get("id");
				bind.getAttributes().put("id", bindId);
				element.getAttributes().put("bind", bindId);*/
				
				if((element instanceof XFormsUIElement) && (!xPath.startsWith("/")) && !xPath.startsWith(PREFIX))
					xPath = getNodesetContext((XFormsUIElement) element) + xPath;
				bind.getAttributes().put("nodeset", xPath);
				
				c.form.getModel().getBinds().add(bind);
			}
			
		}
	}
	
	private void setupParentRelationships(ImportContext c) {
		for(XFormsElement element : c.objects.values()) {
			String parentResourceId = c.parentRelationships.get(element);
			if(parentResourceId!=null) {
				XFormsElement parent = c.objects.get(parentResourceId);
				
				if(element instanceof XFormsUIElement) {
					if(parent instanceof UIElementContainer) {
						XFormsUIElement uiElement = (XFormsUIElement) element;
						uiElement.setParent((UIElementContainer) parent);
					}
				} else if(element instanceof ListUICommon) {
					if(parent instanceof ListUICommonContainer) {
						ListUICommon listUICommon = (ListUICommon) element;
						listUICommon.setParent((ListUICommonContainer) parent);
					}
				} else if(element instanceof AbstractAction) {
					if(parent instanceof ActionContainer) {
						ActionContainer actionContainer = (ActionContainer) parent;
						actionContainer.getActions().add((AbstractAction) element);
					}
				} else if(element instanceof Case) {
					if(parent instanceof Switch) {
						Case caseObj = (Case) element;
						caseObj.setSwitch((Switch) parent);
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
				} 
				
			} else {
				// no parent relationship found: set form as parent
				if(element instanceof XFormsUIElement) {
					XFormsUIElement uiElement = (XFormsUIElement) element;
					uiElement.setParent(c.form);
				}
			}
		}
		
	}
	
	private void addModel(ImportContext c) {
		c.form.getModel().setInstance(factory.createInstance());
		if((c.form.getHead()==null))
			generateInstanceModelDoc(c);
		c.form.getModel().getInstance().setContent(instanceModelDoc);
	}
	
	private String generateCodeTable(ImportContext c, String content, XFormsElement element) throws JSONException {
		JSONObject nodes = new JSONObject(content);
		try {
			JSONArray items = nodes.getJSONArray("items");
			String id = element.getAttributes().get("id");
			
			Element codeTable = (Element) instanceModelDoc.createElement(id);
			instanceModelDoc.getFirstChild().appendChild(codeTable);

			for(int j=0; j < items.length(); j++) {
				Node item = (Node) instanceModelDoc.createElement("item");
				
				Node name = (Node) instanceModelDoc.createElement("name");
				name.setTextContent(items.getJSONObject(j).getString("name"));
				item.appendChild(name);
				try {
					Node value = (Node) instanceModelDoc.createElement("value");
					value.setTextContent(items.getJSONObject(j).getString("value"));
					item.appendChild(value);
				} catch (Exception e) {}
				
				codeTable.appendChild(item);
			}
			return "instance('" + id + "')/item";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private void generateInstanceModelDoc(ImportContext c) {
		// instance model generation
		
		Element root = (Element) instanceModelDoc.createElement("data");
		instanceModelDoc.getFirstChild().appendChild(root);
		
		for(XFormsElement element : c.objects.values()) {
			if(!(element instanceof XFormsUIElement)) continue;
			XFormsUIElement uiElement = (XFormsUIElement) element;
			
			String xPath = uiElement.getAttributes().get("ref");
			if(xPath!=null && (!xPath.startsWith("instance('") || xPath.startsWith(PREFIX)) ) {
				if(!xPath.startsWith("/")) xPath = getNodesetContext(uiElement) + xPath;

				Node instanceModelNode = (Node) root;
				// handle references to attributes
				if(xPath.split("@").length==2)
					xPath = xPath.split("@")[0];
				
				if(xPath.startsWith(PREFIX))
					xPath = xPath.substring(PREFIX.length());
				uiElement.getAttributes().put("ref", PREFIX + xPath);
				
				String[] tags = xPath.split("/");
				Node child = null;
				for(int i = 0; i < tags.length; i++) {
					String tagName = tags[i];
					if(tagName.length()>0) {
						child = getChild(instanceModelNode, tagName);
						if(child==null) {
							// create new element
							child = (Node) instanceModelDoc.createElement(tagName);
							instanceModelNode.appendChild(child);
						}
						instanceModelNode = child;
					}
				}
				String defaultValue = uiElement.getAttributes().get("default");
				if(defaultValue != null)
					child.setTextContent(defaultValue);
			}
		}
	}
	
	private String getNodesetContext(XFormsUIElement element) {
		String nodesetContext = "";
		while(element.getParent()!=null && !(element.getParent() instanceof XForm)) {
			if(element.getParent() instanceof XFormsUIElement)
				element = (XFormsUIElement) element.getParent();
			else if(element.getParent() instanceof Case)
				element = ((Case) element.getParent()).getSwitch();
			String nodeset = element.getAttributes().get("nodeset"); // look for nodeset attr first...
			if((nodeset==null) || nodeset.equals("") || nodeset.equals("/"))
				nodeset = element.getAttributes().get("ref");		 // ... then for ref attr
			if((nodeset!=null) && !nodeset.equals("") && !nodeset.equals("/"))
				nodesetContext = nodeset + "/" + nodesetContext;
			if(nodesetContext.startsWith(PREFIX)) break;
		}
		return nodesetContext;
	}
	
	private void addInput(Node node, ImportContext c) {
		Input input = factory.createInput();
		input.setResourceId(getResourceId(node));
		c.objects.put(input.getResourceId(), input);
		handleAttributes(node, input, c);
	}
	
	private void addSecret(Node node, ImportContext c) {
		Secret secret = factory.createSecret();
		secret.setResourceId(getResourceId(node));
		c.objects.put(secret.getResourceId(), secret);
		handleAttributes(node, secret, c);
	}
	
	private void addTextarea(Node node, ImportContext c) {
		Textarea textarea = factory.createTextarea();
		textarea.setResourceId(getResourceId(node));
		c.objects.put(textarea.getResourceId(), textarea);
		handleAttributes(node, textarea, c);
	}
	
	private void addOutput(Node node, ImportContext c) {
		Output output = factory.createOutput();
		output.setResourceId(getResourceId(node));
		c.objects.put(output.getResourceId(), output);
		handleAttributes(node, output, c);
	}
	
	private void addUpload(Node node, ImportContext c) {
		Upload upload = factory.createUpload();
		upload.setResourceId(getResourceId(node));
		c.objects.put(upload.getResourceId(), upload);
		handleAttributes(node, upload, c);
	}
	
	private void addRange(Node node, ImportContext c) {
		Range range = factory.createRange();
		range.setResourceId(getResourceId(node));
		c.objects.put(range.getResourceId(), range);
		handleAttributes(node, range, c);
	}
	
	private void addTrigger(Node node, ImportContext c) {
		Trigger trigger = factory.createTrigger();
		trigger.setResourceId(getResourceId(node));
		c.objects.put(trigger.getResourceId(), trigger);
		handleAttributes(node, trigger, c);
	}
	
	private void addSubmit(Node node, ImportContext c) {
		Submit submit = factory.createSubmit();
		submit.setResourceId(getResourceId(node));
		submit.setSubmission(factory.createSubmission());
		c.form.getModel().getSubmissions().add(submit.getSubmission());
		c.objects.put(submit.getResourceId(), submit);
		handleAttributes(node, submit, c);
		if(submit.getAttributes().get("submission")==null) {
			String submissionId = "subm_";
			if(submit.getAttributes().get("id")==null)
				submissionId += submit.getResourceId().substring(1);
			else 
				submissionId += submit.getAttributes().get("id");
			submit.getAttributes().put("submission", submissionId);
		}
		submit.getSubmission().getAttributes().put("id", submit.getAttributes().get("submission"));
		// reassign ref attribute -> oryx property 'ref' of submit stencil specifies the 'ref' attribute of the submission element
		submit.getSubmission().getAttributes().put("ref", submit.getAttributes().get("ref"));
		submit.getAttributes().put("ref", null);
	}
	
	private void addSelect(Node node, ImportContext c) {
		Select select = factory.createSelect();
		select.setResourceId(getResourceId(node));
		c.objects.put(select.getResourceId(), select);
		handleAttributes(node, select, c);
	}
	
	private void addSelect1(Node node, ImportContext c) {
		Select1 select1 = factory.createSelect1();
		select1.setResourceId(getResourceId(node));
		c.objects.put(select1.getResourceId(), select1);
		handleAttributes(node, select1, c);
	}
	
	private void addGroup(Node node, ImportContext c) {
		Group group = factory.createGroup();
		group.setResourceId(getResourceId(node));
		c.objects.put(group.getResourceId(), group);
		handleAttributes(node, group, c);
	}
	
	private void addRepeat(Node node, ImportContext c) {
		Repeat repeat = factory.createRepeat();
		repeat.setResourceId(getResourceId(node));
		c.objects.put(repeat.getResourceId(), repeat);
		handleAttributes(node, repeat, c);
	}
	
	private void addSwitch(Node node, ImportContext c) {
		Switch switchObj = factory.createSwitch();
		switchObj.setResourceId(getResourceId(node));
		c.objects.put(switchObj.getResourceId(), switchObj);
		handleAttributes(node, switchObj, c);
	}
	
	private void addCase(Node node, ImportContext c) {
		Case caseObj = factory.createCase();
		caseObj.setResourceId(getResourceId(node));
		c.objects.put(caseObj.getResourceId(), caseObj);
		handleAttributes(node, caseObj, c);
	}
	
	private void addLabel(Node node, ImportContext c) {
		Label label = factory.createLabel();
		label.setResourceId(getResourceId(node));
		c.objects.put(label.getResourceId(), label);
		handleAttributes(node, label, c);
		label.setContent(getContent(getChild(node, "xf_text")));
	}
	
	private void addHelp(Node node, ImportContext c) {
		Help help = factory.createHelp();
		help.setResourceId(getResourceId(node));
		c.objects.put(help.getResourceId(), help);
		handleAttributes(node, help, c);
		help.setContent(getContent(getChild(node, "xf_message")));
	}
	
	private void addHint(Node node, ImportContext c) {
		Hint hint = factory.createHint();
		hint.setResourceId(getResourceId(node));
		c.objects.put(hint.getResourceId(), hint);
		handleAttributes(node, hint, c);
		hint.setContent(getContent(getChild(node, "xf_message")));
	}
	
	private void addAlert(Node node, ImportContext c) {
		Alert alert = factory.createAlert();
		alert.setResourceId(getResourceId(node));
		c.objects.put(alert.getResourceId(), alert);
		handleAttributes(node, alert, c);
		alert.setContent(getContent(getChild(node, "xf_message")));
	}
	
	private void addItem(Node node, ImportContext c) {
		Item item = factory.createItem();
		item.setResourceId(getResourceId(node));
		c.objects.put(item.getResourceId(), item);
		handleAttributes(node, item, c);
		
		Value value = factory.createValue();
		value.setContent(getContent(getChild(node, "xf_value")));
		handleAttributes(node, value, "value_", c);
		if(((value.getAttributes().get("ref")!=null) && !value.getAttributes().get("ref").equals("/"))
				|| (value.getContent()!=null) || (value.getAttributes().get("bind")!=null))
			item.setValue(value);
	}
	
	private void addItemset(Node node, ImportContext c) {
		Itemset itemset = factory.createItemset();
		itemset.setResourceId(getResourceId(node));
		c.objects.put(itemset.getResourceId(), itemset);
		handleAttributes(node, itemset, c);
		
		Value value = factory.createValue();
		value.getAttributes().put("ref", getContent(getChild(node, "xf_value_ref")));
		handleAttributes(node, value, "value_", c);
		if(((value.getAttributes().get("ref")!=null) && !value.getAttributes().get("ref").equals("/")) 
				|| (value.getContent()!=null) || (value.getAttributes().get("bind")!=null))
			itemset.setValue(value);
		
		Copy copy = factory.createCopy();
		handleAttributes(node, copy, "copy_", c);
		if(((copy.getAttributes().get("ref")!=null) && !copy.getAttributes().get("ref").equals("/"))
				|| (copy.getAttributes().get("bind")!=null))
			itemset.setCopy(copy);
	}
	
	private void addChoices(Node node, ImportContext c) {
		Choices choices = factory.createChoices();
		choices.setResourceId(getResourceId(node));
		c.objects.put(choices.getResourceId(), choices);
		handleAttributes(node, choices, c);
	}
	
	private void addAction(Node node, ImportContext c) {
		Action action = factory.createAction();
		action.setResourceId(getResourceId(node));
		c.objects.put(action.getResourceId(), action);
		handleAttributes(node, action, c);
	}
	
	private void addSetValue(Node node, ImportContext c) {
		SetValue setValue = factory.createSetValue();
		setValue.setResourceId(getResourceId(node));
		c.objects.put(setValue.getResourceId(), setValue);
		handleAttributes(node, setValue, c);
	}
	
	private void addInsert(Node node, ImportContext c) {
		Insert insert = factory.createInsert();
		insert.setResourceId(getResourceId(node));
		c.objects.put(insert.getResourceId(), insert);
		handleAttributes(node, insert, c);
	}
	
	private void addDelete(Node node, ImportContext c) {
		Delete delete = factory.createDelete();
		delete.setResourceId(getResourceId(node));
		c.objects.put(delete.getResourceId(), delete);
		handleAttributes(node, delete, c);
	}
	
	private void addSetIndex(Node node, ImportContext c) {
		SetIndex setIndex = factory.createSetIndex();
		setIndex.setResourceId(getResourceId(node));
		c.objects.put(setIndex.getResourceId(), setIndex);
		handleAttributes(node, setIndex, c);
	}
	
	private void addToggle(Node node, ImportContext c) {
		Toggle toggle = factory.createToggle();
		toggle.setResourceId(getResourceId(node));
		c.objects.put(toggle.getResourceId(), toggle);
		handleAttributes(node, toggle, c);
	}
	
	private void addSetFocus(Node node, ImportContext c) {
		SetFocus setFocus = factory.createSetFocus();
		setFocus.setResourceId(getResourceId(node));
		c.objects.put(setFocus.getResourceId(), setFocus);
		handleAttributes(node, setFocus, c);
	}
	
	private void addDispatch(Node node, ImportContext c) {
		Dispatch dispatch = factory.createDispatch();
		dispatch.setResourceId(getResourceId(node));
		c.objects.put(dispatch.getResourceId(), dispatch);
		handleAttributes(node, dispatch, c);
	}
	
	private void addRebuild(Node node, ImportContext c) {
		Rebuild rebuild = factory.createRebuild();
		rebuild.setResourceId(getResourceId(node));
		c.objects.put(rebuild.getResourceId(), rebuild);
		handleAttributes(node, rebuild, c);
	}
	
	private void addRecalculate(Node node, ImportContext c) {
		Recalculate recalculate = factory.createRecalculate();
		recalculate.setResourceId(getResourceId(node));
		c.objects.put(recalculate.getResourceId(), recalculate);
		handleAttributes(node, recalculate, c);
	}
	
	private void addRevalidate(Node node, ImportContext c) {
		Revalidate revalidate = factory.createRevalidate();
		revalidate.setResourceId(getResourceId(node));
		c.objects.put(revalidate.getResourceId(), revalidate);
		handleAttributes(node, revalidate, c);
	}
	
	private void addRefresh(Node node, ImportContext c) {
		Refresh refresh = factory.createRefresh();
		refresh.setResourceId(getResourceId(node));
		c.objects.put(refresh.getResourceId(), refresh);
		handleAttributes(node, refresh, c);
	}
	
	private void addReset(Node node, ImportContext c) {
		Reset reset = factory.createReset();
		reset.setResourceId(getResourceId(node));
		c.objects.put(reset.getResourceId(), reset);
		handleAttributes(node, reset, c);
	}
	
	private void addLoad(Node node, ImportContext c) {
		Load load = factory.createLoad();
		load.setResourceId(getResourceId(node));
		c.objects.put(load.getResourceId(), load);
		handleAttributes(node, load, c);
	}
	
	private void addSend(Node node, ImportContext c) {
		Send send = factory.createSend();
		send.setResourceId(getResourceId(node));
		c.objects.put(send.getResourceId(), send);
		handleAttributes(node, send, c);
	}
	
	private void addMessage(Node node, ImportContext c) {
		Message message = factory.createMessage();
		message.setResourceId(getResourceId(node));
		c.objects.put(message.getResourceId(), message);
		handleAttributes(node, message, c);
		message.setContent(getContent(getChild(node, "xf_message")));
	}
	
	private void addHead(String head, ImportContext c) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document headDoc = builder.parse(new InputSource(new StringReader(head)));
			c.form.setHead(headDoc.getDocumentElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addNSDeclarations(String nsDeclarations, ImportContext c) {
		// deserialize namespace declaraions
		// format: [prefix1,namespace1][prefix2,namespace2] ...
		
		int b = nsDeclarations.indexOf("[");
		while(b>=0) {
			int t = nsDeclarations.indexOf(",");
			int e = nsDeclarations.indexOf("]");
			c.form.getNSDeclarations().put(
					nsDeclarations.substring(b+1, t), nsDeclarations.substring(t+1, e));
			nsDeclarations = nsDeclarations.substring(e+1);
			b = nsDeclarations.indexOf("[");
		}
	}
	
	private String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}

	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	private String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#') + 1);
		else
			return null;
	}

	private String getResourceId(Node node) {
		Node item = node.getAttributes().getNamedItem("rdf:about");
		if (item != null)
			return getResourceId(item.getNodeValue());
		else
			return null;
	}

	private String getResourceId(String id) {
		return id.substring(id.indexOf('#'));
	}

	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}

	private Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}

}

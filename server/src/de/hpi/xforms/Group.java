package de.hpi.xforms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group extends XFormsUIElement implements UIElementContainer, ActionContainer, UICommonContainer, LabelContainer {
	
	protected List<XFormsUIElement> childElements;
	
	protected Help help;
	protected Hint hint;
	protected Alert alert;
	protected List<AbstractAction> actions;
	
	protected Label label;

	public Group() {
		super();
		attributes.put("ref", null);
		attributes.put("bind", null);
	}

	public List<XFormsUIElement> getChildElements() {
		if(childElements==null)
			childElements = new ArrayList<XFormsUIElement>();
		Collections.sort(childElements, new UIElementComparator());
		return childElements;
	}

	public Help getHelp() {
		return help;
	}

	public void setHelp(Help help) {
		this.help = help;
	}

	public Hint getHint() {
		return hint;
	}

	public void setHint(Hint hint) {
		this.hint = hint;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public List<AbstractAction> getActions() {
		if(actions==null)
			actions = new ArrayList<AbstractAction>();
		return actions;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}
	
	@Override
	public String getStencilId() {
		return "Group";
	}
	
	@Override
	public String getTagName() {
		return "group";
	}

}

package de.hpi.xforms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Case extends XFormsElement implements UIElementContainer, ActionContainer, LabelContainer {
	
	protected Switch switchObj;
	protected List<XFormsUIElement> childElements;
	protected List<AbstractAction> actions;
	protected Label label;

	public Case() {
		super();
		attributes.put("nodeset", null);
		attributes.put("model", null);
		attributes.put("bind", null);
		attributes.put("startindex", null);
		attributes.put("number", null);
	}

	public List<XFormsUIElement> getChildElements() {
		if(childElements==null)
			childElements = new ArrayList<XFormsUIElement>();
		Collections.sort(childElements, new UIElementComparator());
		return childElements;
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
	
	public Switch getSwitch() {
		return switchObj;
	}
	
	public void setSwitch(Switch switchObj) {
		if (this.switchObj!=switchObj) {
			if (this.switchObj!=null)
				this.switchObj.getCases().remove(this);
			if (switchObj!=null)
				switchObj.getCases().add(this);
		}
		this.switchObj = switchObj;
	}
	
	@Override
	public String getStencilId() {
		return "Case";
	}
	
	@Override
	public String getTagName() {
		return "case";
	}

}

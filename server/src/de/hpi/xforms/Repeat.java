package de.hpi.xforms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repeat extends XFormsUIElement implements UIElementContainer, ActionContainer {
	
	protected List<XFormsUIElement> childElements;
	protected List<AbstractAction> actions;

	public Repeat() {
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
	
	@Override
	public String getStencilId() {
		return "Repeat";
	}
	
	@Override
	public String getTagName() {
		return "repeat";
	}

}

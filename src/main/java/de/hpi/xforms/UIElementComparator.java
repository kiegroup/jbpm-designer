package de.hpi.xforms;

import java.util.Comparator;

public class UIElementComparator implements Comparator<XFormsUIElement> {

	public int compare(XFormsUIElement element1, XFormsUIElement element2) {
		if(element1.getYPosition() == element2.getYPosition())
			return (element1.getXPosition() - element2.getXPosition());
		else
			return (element1.getYPosition() - element2.getYPosition());
	}

}

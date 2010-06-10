package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ExtendedAttributes")
public class XPDLExtendedAttributes extends XMLConvertible {

	@Element("ExtendedAttribute")
	protected ArrayList<XPDLExtendedAttribute> extendedAttributes;

	public void add(XPDLExtendedAttribute newAttribute) {
		initializeExtendedAttributes();
		
		getExtendedAttributes().add(newAttribute);
	}
	
	public ArrayList<XPDLExtendedAttribute> getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(ArrayList<XPDLExtendedAttribute> newAttribute) {
		this.extendedAttributes = newAttribute;
	}
	
	protected void initializeExtendedAttributes() {
		if (getExtendedAttributes() == null) {
			setExtendedAttributes(new ArrayList<XPDLExtendedAttribute>());
		}
	}
}

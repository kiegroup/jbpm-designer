package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public abstract class XFormsUIElement extends XFormsElement {
	
	protected UIElementContainer parent;
	protected int xPosition;
	protected int yPosition;
	
	public XFormsUIElement() {
		super();
		attributes.put("appearance", null);
		attributes.put("navindex", null);
		attributes.put("accesskey", null);
	}

	public UIElementContainer getParent() {
		return parent;
	}

	public void setParent(UIElementContainer parent) {
		if (this.parent!=parent) {
			if (this.parent!=null)
				this.parent.getChildElements().remove(this);
			if (parent!=null)
				parent.getChildElements().add(this);
		}
		this.parent = parent;
	}

	public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(int position) {
		xPosition = position;
	}

	public int getYPosition() {
		return yPosition;
	}

	public void setYPosition(int position) {
		yPosition = position;
	}

}

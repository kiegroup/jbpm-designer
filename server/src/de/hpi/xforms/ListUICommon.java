package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public abstract class ListUICommon extends XFormsElement {
	
	protected ListUICommonContainer parent;
	protected int yPosition;
	
	public ListUICommon() {
		super();
	}
	
	public ListUICommonContainer getParent() {
		return parent;
	}

	public void setParent(ListUICommonContainer parent) {
		if (this.parent!=parent) {
			if (this.parent!=null)
				this.parent.getListUICommons().remove(this);
			if (parent!=null)
				parent.getListUICommons().add(this);
		}
		this.parent = parent;
	}
	
	public int getYPosition() {
		return yPosition;
	}

	public void setYPosition(int position) {
		yPosition = position;
	}
	
}

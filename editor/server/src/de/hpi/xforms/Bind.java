package de.hpi.xforms;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Bind extends XFormsElement {
	
	protected Bind parent;
	protected List<Bind> binds;

	public Bind() {
		super();
		attributes.put("nodeset", null);
		attributes.put("type", null);
		attributes.put("readonly", null);
		attributes.put("required", null);
		attributes.put("relevant", null);
		attributes.put("calculate", null);
		attributes.put("constraint", null);
		attributes.put("p3ptype", null);
	}
	
	public List<Bind> getBinds() {
		if(binds==null)
			binds = new ArrayList<Bind>();
		return binds;
	}
	
	public Bind getParentBind() {
		return parent;
	}
	
	public void setParentBind(Bind parent) {
		if (this.parent!=parent) {
			if (this.parent!=null)
				this.parent.getBinds().remove(this);
			if (parent!=null)
				parent.getBinds().add(this);
		}
		this.parent = parent;
	}
	
	@Override
	public String getTagName() {
		return "bind";
	}

}

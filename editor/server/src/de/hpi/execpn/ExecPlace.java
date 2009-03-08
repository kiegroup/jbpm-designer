package de.hpi.execpn;

import de.hpi.execpn.pnml.Locator;
import de.hpi.petrinet.Place;

public class ExecPlace extends ExecNode implements Place {
	
	public enum Type {
		data, context, flow
	}
	
	// Constructors
	public ExecPlace(){
		this.type = Type.flow;
	}
	
	/** Converts place to ExecPlace
	 * 
	 * @param place
	 */
	public ExecPlace(ExecPlace place){
		this.type = Type.flow;
		for (Locator locator : place.getLocators())
			this.addLocator(locator);
		this.setGuard(place.getGuard());
		this.setId(place.getId());
		this.setRolename(place.getRolename());
	}
	
	// Attributes
	protected Type type;
	protected String model = null;
	protected String name = null;
	
	// Accessors
	public Type getType () {
		return this.type;
	}
	
	public void setType (Type type) {
		this.type = type;
	}
	
	public String getModel () {
		return model;
	}
	
	public void setModel (String model) {
		if (type == Type.data)
			this.model = model;
	}
	
	public String getName () {
		return name;
	}
	
	public void setName (String name) {
		if (type == Type.data)
			this.name = name;
	}

	public boolean isFinalPlace() {
		// TODO Auto-generated method stub
		return false;
	}
	
}

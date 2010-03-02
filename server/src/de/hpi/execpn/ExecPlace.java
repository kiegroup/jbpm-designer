package de.hpi.execpn;

import de.hpi.execpn.pnml.Locator;
import de.hpi.petrinet.Place;

public class ExecPlace extends ExecNode implements Place {
	
	public enum Type {
		data, context, flow
	}
	
	/*
	 * label for places, added from de.hpi.petrinet.Place
	 */
	protected String label = null;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public boolean isInitialPlace() {
		// TODO Auto-generated method stub
		return false;
	}
	
}

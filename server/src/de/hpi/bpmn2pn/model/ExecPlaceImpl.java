package de.hpi.bpmn2pn.model;

import de.hpi.petrinet.*;
import de.hpi.execpn.pnml.*;
import de.hpi.petrinet.ExecPlace.Type;
import de.hpi.petrinet.impl.*;

public class ExecPlaceImpl extends PlaceImpl implements ExecPlace {

	// Constructors
	public ExecPlaceImpl (){
		this.type = Type.flow;
	}
	
	/** Converts place to ExecPlace
	 * 
	 * @param place
	 */
	public ExecPlaceImpl (Place place){
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
	
}

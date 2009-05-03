package de.hpi.execpn;

import java.util.Vector;

import de.hpi.execpn.pnml.Locator;
import de.hpi.petrinet.NodeImpl;

/**
 * @author gero.decker
 */
public class ExecNode extends NodeImpl {

	private String guard;
	private Vector<Locator> locators = new Vector<Locator>();
	private String rolename;
	private String contextPlaceID;

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}
	
	public String getRolename() {
		return rolename;
	}
	
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getContextPlaceID(){
		return contextPlaceID;
	}
	
	public void setContextPlaceID(String contextPlaceID){
		this.contextPlaceID = contextPlaceID;
	}
	
	public Vector<Locator> getLocators() {
		return locators;
	}

	public void addLocator(Locator locator) {
		this.locators.add(locator);
	}
	
	public ExecNode getCopy() {
		ExecNode n = (ExecNode) super.getCopy();
		n.setGuard(this.getGuard());
		n.setRolename(this.getRolename());
		n.setContextPlaceID(this.getContextPlaceID());
		for(Locator l : this.getLocators())
			n.addLocator(l.getCopy());
		return n;
	}
	
}



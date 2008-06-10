package de.hpi.petrinet;

import de.hpi.petrinet.ExecPlace.Type;
import de.hpi.petrinet.*;

public interface ExecPlace extends Place {
	
	// Define types
	public enum Type {
		data, context, flow
	}
	
	public Type getType ();
	
	public void setType (Type type);
	
	public void setModel (String model);
	
	public String getModel ();
	
	public void setName (String name);
	
	public String getName ();
}

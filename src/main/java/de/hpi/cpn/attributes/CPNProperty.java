package de.hpi.cpn.attributes;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNProperty extends CPNModellingThing
{
	// CPNProperty is like the class CPNLittleProperty. This class is used for a lot of
	// XML Nodes. The difference to the different types of CPNLittleProperty is the following.
	// The different types of CPNLittleProperty distinguish in their attribute variables. 
	// CPNProperty in case does have an defined number of attribute variables (mainly defined in
	// CPNModellingThing). They only distinguish in their behavior of how to set up the object.
	// You will see it in the next lines of code where different readJSON... methods are defined.
	
	private CPNText text = new CPNText();

	public CPNProperty()
	{
		super();
		
		getFillattr().setPattern("Solid");
		getLineattr().setThick("0");		
	}
	
	public void readJSONpostattrX(JSONObject modelElement) throws JSONException
	{		
		String postattrX = modelElement.getString("postattrX");
		
		getPosattr().setX(postattrX);		
	}
	
	public void readJSONpostattrY(JSONObject modelElement) throws JSONException
	{		
		String postattrY = modelElement.getString("postattrY");
		
		getPosattr().setY(postattrY);		
	}

	// -------------------------------------- JSON Reader ------------------------------------
	
	public void readJSONid(JSONObject modelElement) throws JSONException
	{
		String id = modelElement.getString("id");
		
		setId(id);
	}
	
	// ------------------ Place
	
	public void readJSONinitialmarking(JSONObject modelElement) throws JSONException
	{
		String initialmarking = modelElement.getString("initialmarking");
		String quantity = modelElement.getString("quantity");
		
		if (quantity.isEmpty())
			quantity = "1";
		
		getText().insertTextforToken(initialmarking, quantity);
	}

	public void readJSONcolordefinition(JSONObject modelElement) throws JSONException
	{
		String colorsettype = modelElement.getString("colordefinition");
		
		getText().setText(colorsettype);
	}
		
	public void readJSONinitpostattrX(JSONObject modelElement) throws JSONException
	{
		String initpostattrX = modelElement.getString("initpostattrX");
		String initpostattrY = modelElement.getString("initpostattrY");
		
		getPosattr().setX(initpostattrX);
		getPosattr().setY(initpostattrY);
	}
	
	public void readJSONtypepostattrX(JSONObject modelElement) throws JSONException
	{
		String typepostattrX = modelElement.getString("typepostattrX");
		String typepostattrY = modelElement.getString("typepostattrY");
		
		getPosattr().setX(typepostattrX);
		getPosattr().setY(typepostattrY);
	}
	
	
	// ------------ Transition
	
	public void readJSONguard(JSONObject modelElement) throws JSONException
	{
		String guard = modelElement.getString("guard");
		
		getText().setText(guard);
	}
	
	// ------------ Arc
	
	public void readJSONlabel(JSONObject modelElement) throws JSONException
	{
		String annot = modelElement.getString("label");
		
		getText().setText(annot);
	}
	
	// ---------------------------------------- Accessory -----------------------------------
	
	public void setText(CPNText text) 
	{
		this.text = text;
	}

	public CPNText getText() 
	{
		return text;
	}
}


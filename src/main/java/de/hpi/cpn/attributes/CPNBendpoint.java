package de.hpi.cpn.attributes;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNBendpoint extends CPNModellingThing
{
	private String serial;
	
	public CPNBendpoint()
	{
		super();
		
		getFillattr().setPattern("Solid");
		getLineattr().setThick("0");
	}
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("bendpoint", CPNBendpoint.class);
		xstream.useAttributeFor(CPNBendpoint.class, "serial");
	}
	
	
	public void readJSONx(JSONObject modelElement) throws JSONException
	{
		String x = getXCoordinateWith(0, modelElement);
		String y = getYCoordinateWith(0, modelElement);
		
		getPosattr().setX(x);
		getPosattr().setY(y);
	}
	
	public void readJSONserial(JSONObject modelElement) throws JSONException
	{
		String serial = modelElement.getString("serial");
		
		setSerial(serial);
	}
	
	// ------------------------------ Mapping ------------------------------------
	
	public void setSerial(String serial)
	{
		this.serial = serial;
	}

	public String getSerial()
	{
		return serial;
	}
}

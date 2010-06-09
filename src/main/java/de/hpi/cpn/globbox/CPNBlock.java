package de.hpi.cpn.globbox;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNBlock extends XMLConvertable 
{
	//	Example
	//	<block id="ID170089">
	//    <id>Variables</id>
	//			...
	//		declarations
	//			...
	//	</block>
	
	private String idattri;
	private String idtag;	
	private ArrayList<CPNVariable> vars;
	private ArrayList<CPNColor> colors;
	
	
	public CPNBlock(String idtag, String idattr)
	{
		setIdtag(idtag);
		setIdattri(idattr);
	}

	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("block", CPNBlock.class);
		
		xstream.aliasField("id", CPNBlock.class, "idattri");
		xstream.aliasField("id", CPNBlock.class, "idtag");
		
		xstream.useAttributeFor(CPNBlock.class, "idattri");
		
		xstream.addImplicitCollection(CPNBlock.class, "vars", CPNVariable.class);
		xstream.addImplicitCollection(CPNBlock.class, "colors", CPNColor.class);
		
		CPNColor.registerMapping(xstream);
		CPNVariable.registerMapping(xstream);
	}
	
	
	// ------------------------------------ JSON Reader ------------------------------------------
	
	public void readJSONname(JSONObject modelElement) throws JSONException 
	{
		String declarationType = modelElement.getString("declarationtype");
		
		if	(declarationType.equalsIgnoreCase("Colorset"))
		{
			CPNColor tempColor = new CPNColor();
			
			// Forward the JSONObject to the CPNColor class to be handled there
			tempColor.parse(modelElement);			
			
			getColors().add(tempColor);
		}
		else
		{
			CPNVariable tempVariable = new CPNVariable();
			
			// Forward the JSONObject to the CPNVarible class to be handled there
			tempVariable.parse(modelElement);			
			
			getVars().add(tempVariable);
		}	
	}
	
	// ------------------------------ Accessor ---------------------------------------------------
	
	public void setIdtag(String idtag) {
		this.idtag = idtag;
	}
	public String getIdtag() {
		return idtag;
	}

	public ArrayList<CPNVariable> getVars()
    {
       return this.vars;
    }
    public void setVars(ArrayList<CPNVariable> _vars)
    {
       this.vars = _vars;
    }
    public void addVar(CPNVariable _var)
    {
       this.vars.add(_var);
    }
    public void removeVar(CPNVariable _var)
    {
       this.vars.remove(_var);
    }
    public CPNVariable getVar( int i)
    {
       return (CPNVariable) this.vars.get(i);
    }

	public void setIdattri(String idattri) {
		this.idattri = idattri;
	}

	public String getIdattri() {
		return idattri;
	}

	public void setColors(ArrayList<CPNColor> colors) {
		this.colors = colors;
	}

	public ArrayList<CPNColor> getColors() {
		return colors;
	}
}

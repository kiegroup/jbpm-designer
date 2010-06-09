package de.hpi.cpn.globbox;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;


public class CPNGlobbox extends XMLConvertable
{	
	// Elements which are neither important for the Export nor the Import, but these elements
	// are necessary for XStream otherwise XStream raises an error
	private transient Object mltag;
	
	private ArrayList<CPNColor> colors = new ArrayList<CPNColor>();
	private ArrayList<CPNVariable> vars = new ArrayList<CPNVariable>();
	private ArrayList<CPNBlock> blocks = new ArrayList<CPNBlock>();
	private CPNBlock colorSetBlock;
	private CPNBlock variableBlock;
	
	// ------------------------------------- Initialization ------------------------------
	public CPNGlobbox()
	{
		initializeList();
	}
	
	private void initializeList()
	{	
		setColorSetBlock(initializeColorBlock("ID1001"));
		setVaraibleBlock(initializeVariableBlock("ID1002"));
	}
	
	private static CPNBlock initializeColorBlock( String idattr)
	{
		CPNBlock tempBlock = new CPNBlock("Colorset", idattr);
		tempBlock.setColors(new ArrayList<CPNColor>());
		
		return tempBlock;
	}
	
	private static CPNBlock initializeVariableBlock(String idattr)
	{
		CPNBlock tempBlock = new CPNBlock("Variable", idattr);
		tempBlock.setVars(new ArrayList<CPNVariable>());
		
		return tempBlock;
	}
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("globbox", CPNGlobbox.class);
	   
	   xstream.aliasField("ml", CPNGlobbox.class, "mltag");
	   
	   xstream.addImplicitCollection(CPNGlobbox.class, "colors", CPNColor.class);
	   xstream.addImplicitCollection(CPNGlobbox.class, "vars", CPNVariable.class);
	   xstream.addImplicitCollection(CPNGlobbox.class, "blocks", CPNBlock.class);
	   
	   // These instance variables are not needed for the mapping, that's why they are excluded
	   xstream.omitField(CPNGlobbox.class, "colorSetBlock");
	   xstream.omitField(CPNGlobbox.class, "variableBlock");
	   
	   CPNBlock.registerMapping(xstream);	   
	}
	
	
	// ----------------------------------- JSON Reader -----------------------------------
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException 
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.parse(properties);
	}
	
	public void readJSONdeclarations(JSONObject modelElement) throws JSONException
	{
		JSONObject declarations = modelElement.optJSONObject("declarations");
		
		if (declarations != null)
		{
			JSONArray jsonDeclarations = declarations.optJSONArray("items");
			
			// Iterating over all declarations			
			for (int i = 0; i < jsonDeclarations.length(); i++)
			{			
				JSONObject declaration = jsonDeclarations.getJSONObject(i);
					
				addDeclaration(declaration, i);
			}
			
			getBlocks().add(getColorSetBlock());
			getBlocks().add(getVaraibleBlock());
		}
	}
	
	
	
	// ------------------------------ Helper Export ---------------------------------------------
	
	private void addDeclaration(JSONObject declarationJSON, int id) throws JSONException
	{
		
		String declarationType = declarationJSON.getString("declarationtype");		
		// The id is important because each colorset must have an unique Id
		declarationJSON.put("id", "ID2000" + id);
				
		// Choose correct block depending on the declarationType
		if	(declarationType.equalsIgnoreCase("Colorset"))
			getColorSetBlock().parse(declarationJSON);
				
		else
			getVaraibleBlock().parse(declarationJSON);
	}
	
	// ------------------------------ Accessor ------------------------------------------
	public ArrayList<CPNColor> getColors()
	   {
	      return this.colors;
	   }
	   public void setColors(ArrayList<CPNColor> _colors)
	   {
	      this.colors = _colors;
	   }
	   public void addColor(CPNColor _color)
	   {
	      this.colors.add(_color);
	   }
	   public void removeColor(CPNColor _color)
	   {
	      this.colors.remove(_color);
	   }
	   public CPNColor getColor( int i)
	   {
	      return (CPNColor) this.colors.get(i);
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

	   public ArrayList<CPNBlock> getBlocks()
	   {
	      return this.blocks;
	   }
	   public void setBlocks(ArrayList<CPNBlock> _blocks)
	   {
	      this.blocks = _blocks;
	   }
	   public void addBlock(CPNBlock _block)
	   {
	      this.blocks.add(_block);
	   }
	   public void removeBlock(CPNBlock _block)
	   {
	      this.blocks.remove(_block);
	   }
	   public CPNBlock getBlock( int i)
	   {
	      return (CPNBlock) this.blocks.get(i);
	   }
	   
	   private void setVaraibleBlock(CPNBlock _varaibleBlock) {
		   this.variableBlock = _varaibleBlock;
	   }
	   private CPNBlock getVaraibleBlock() {
		   return variableBlock;
	   }
	   
	   private void setColorSetBlock(CPNBlock _colorSetBlock) {
		   this.colorSetBlock = _colorSetBlock;
	   }
	   private CPNBlock getColorSetBlock() {
		   return colorSetBlock;
	   }
}

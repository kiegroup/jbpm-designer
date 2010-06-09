package de.hpi.cpn.attributes;

public class CPNText
{	
	private String tool;
	private String version;
	private String text = "";
	
	public CPNText()
	{
		String defaultTool = "CPN Tools";
		String defaultVersion = "2.2.0";
		
		setTool(defaultTool);
		setVersion(defaultVersion);
	}	
	
	// ----------------------------------- Helper -----------------------------------
	
	public void insertTextforToken(String initialmarking, String quantity)
	{
		// Example
		// 1`("Gerardo",20)++
		// 1`("David",9)
		
		if (! getText().isEmpty())
			setText(getText().concat("++"));
		
		insertRecord(initialmarking, quantity);			
	}
	
	private void insertRecord(String initialmarking, String quantity)
	{
		String text = getText();
		
		text += quantity + "`(";
		initialmarking.replace(";",",");
		text += initialmarking + ")";
		
		setText(text);
	}
	
	// ------------------------------ Accessor ------------------------------
	
	public void setTool(String tool) 
	{
		this.tool = tool;
	}
	public String getTool()
	{
		return tool;
	}
	
	public void setVersion(String version) 
	{
		this.version = version;
	}
	public String getVersion() 
	{
		return version;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	public String getText() 
	{
		return text;
	}
}

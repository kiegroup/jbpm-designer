package de.hpi.cpn.attributes;

import com.thoughtworks.xstream.XStream;

public class CPNLittleProperty 
{
	//		Example of little properties in the XML
	//		Place:
	//		<ellipse w="60.000000" h="40.000000"/>
	//		<token x="-10.000000" y="0.000000"/>
	//		<marking x="0.000000" y="0.000000" hidden="false"/>
	//
	//		Trans:                 
	//		<box w="60.000000" h="40.000000"/>
	//		<binding x="7.200000" y="-3.000000"/>
	//		                 
	//		Arc:                 
	//		<arrowattr headsize="1.200000"
	//		                   currentcyckle="2"/>
	
	// Normaly you should create for each element like box, ellipse an own
	// container class. But except of the attributes in the XML nodes, they all
	// nearly look the same so I decided to make it variable and let "factory methods"
	// create the different elements.
	
	private String x;
	private String y;
	private String w; 
	private String h;
	private String hidden;
	private String headsize;
	private String currentcyckle;
	private String idref;
	
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("littleProperty", CPNLittleProperty.class);
				
		xstream.useAttributeFor(CPNLittleProperty.class, "x");
		xstream.useAttributeFor(CPNLittleProperty.class, "y");
		xstream.useAttributeFor(CPNLittleProperty.class, "w");
		xstream.useAttributeFor(CPNLittleProperty.class, "h");
		xstream.useAttributeFor(CPNLittleProperty.class, "hidden");
		xstream.useAttributeFor(CPNLittleProperty.class, "headsize");
		xstream.useAttributeFor(CPNLittleProperty.class, "currentcyckle");
		xstream.useAttributeFor(CPNLittleProperty.class, "idref");
	}
	
	// ----------------------------------- "Factory methods" -----------------------------------
	
	// ------------------------ Arc
	
	public static CPNLittleProperty arrowattr()
	{
		String defaultheadsize = "1.200000";
        String defaultcurrentcyckle = "2";
		
		CPNLittleProperty tempPro = new CPNLittleProperty();
		
		tempPro.setHeadsize(defaultheadsize);
		tempPro.setCurrentcyckle(defaultcurrentcyckle);
		
		return tempPro;
	}
	
	public static CPNLittleProperty transend(String idref)
	{		
		CPNLittleProperty tempPro = new CPNLittleProperty();
		
		tempPro.setIdref(idref);
		
		return tempPro;
	}
	
	public static CPNLittleProperty placeend(String idref)
	{		
		CPNLittleProperty tempPro = new CPNLittleProperty();
		
		tempPro.setIdref(idref);
		
		return tempPro;
	}
	
	
	// ------------------------ Place
	
	public static CPNLittleProperty ellipse()
	{		
		String defaultW = "60.000000";
        String defaultH = "40.000000";
		
        CPNLittleProperty tempPro = new CPNLittleProperty();
        
        tempPro.setW(defaultW);
		tempPro.setH(defaultH);
		
		return tempPro;
	}
	
	public static CPNLittleProperty token()
	{
		String defaultX = "-10.000000";
		String defaultY = "0.000000";
        
        CPNLittleProperty tempPro = new CPNLittleProperty();
        
        tempPro.setX(defaultX);
    	tempPro.setY(defaultY);
        
        return tempPro;
	}
	
	public static CPNLittleProperty marking()
	{
		String defaultHidden = "false";
		String defaultX = "0.000000";
		String defaultY = "0.000000";
        
        CPNLittleProperty tempPro = new CPNLittleProperty();
        
        tempPro.setX(defaultX);
    	tempPro.setY(defaultY);
    	tempPro.setHidden(defaultHidden);
        
        return tempPro;
	}
	
	// ------------------------ Transition
	
	public static CPNLittleProperty box()
	{
		String defaultW = "60.000000";
        String defaultH = "40.000000";
        
        CPNLittleProperty tempPro = new CPNLittleProperty();
        
        tempPro.setW(defaultW);
        tempPro.setH(defaultH);
        
        return tempPro;
	}
	
	public static CPNLittleProperty binding()
	{
		String defaultX = "-10.000000";
		String defaultY = "0.000000";
        
        CPNLittleProperty tempPro = new CPNLittleProperty();
        
        tempPro.setX(defaultX);
    	tempPro.setY(defaultY);
        
        return tempPro;
	}
	
	// ----------------------------------- Accessory ----------------------------------------
	
	public void setX(String x) 
	{
		this.x = x;
	}
	public String getX() 
	{
		return x;
	}
	
	public void setY(String y) 
	{
		this.y = y;
	}
	public String getY() 
	{
		return y;
	}
	public void setW(String w) 
	{
		this.w = w;
	}
	public String getW() {
		return w;
	}
	
	public void setH(String h) 
	{
		this.h = h;
	}
	public String getH() 
	{
		return h;
	}
	
	public void setHidden(String hidden) 
	{
		this.hidden = hidden;
	}
	public String getHidden() 
	{
		return hidden;
	}
	
	public void setHeadsize(String headsize) 
	{
		this.headsize = headsize;
	}
	public String getHeadsize() 
	{
		return headsize;
	}
	
	public void setCurrentcyckle(String currentcyckle) 
	{
		this.currentcyckle = currentcyckle;
	}
	public String getCurrentcyckle() 
	{
		return currentcyckle;
	}

	public void setIdref(String idref)
	{
		this.idref = idref;
	}
	public String getIdref()
	{
		return idref;
	}
}
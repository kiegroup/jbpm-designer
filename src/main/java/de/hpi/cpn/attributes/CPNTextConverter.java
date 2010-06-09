package de.hpi.cpn.attributes;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CPNTextConverter implements Converter
{
	// This is a class which helps XStream to convert the object structure into a XML
	// and also the other way around. 
    public boolean canConvert(Class clazz) {
            return clazz.equals(CPNText.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context)
    {
            CPNText cpnText = (CPNText) value;
            writer.addAttribute("tool", cpnText.getTool());
            writer.addAttribute("version", cpnText.getVersion());
            writer.setValue(cpnText.getText());
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) 
    {	
    	CPNText cpnText = new CPNText();
    	
    	cpnText.setText(reader.getValue());
    		        	
    	return cpnText;	    	       
    }
}

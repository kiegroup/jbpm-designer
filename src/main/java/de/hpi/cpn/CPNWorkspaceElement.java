package de.hpi.cpn;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

import de.hpi.cpn.mapperhelper.XMLConvertable;

public class CPNWorkspaceElement extends XMLConvertable
{	
   private CPNGenerator generator = new CPNGenerator();
   private CPNCpnet cpnet = new CPNCpnet();
   
   // ---------------------------------------- Mapping ----------------------------------------
	
   public static void registerMapping(XStream xstream)
   {
	   // In the XML the class is represented as a workspaceElement - tag
	   xstream.alias("workspaceElements", CPNWorkspaceElement.class);
	   
	   CPNCpnet.registerMapping(xstream);
	   CPNGenerator.registerMapping(xstream);
   }	
   
   // ---------------------------------------- JSON Reader ----------------------------------------
   
   public void readJSONresourceId(JSONObject modelElement) throws JSONException
   {
	   // forward the JSON file
	   getCpnet().parse(modelElement);
   }
   
   
   // ---------------------------------------- Accessory -----------------------------------------
   
   public CPNCpnet getCpnet()
   {
      return this.cpnet;
   }
   public void setCpnet(CPNCpnet _cpnet)
   {
      this.cpnet = _cpnet;
   }
   
   public CPNGenerator getGenerator()
   {
      return this.generator;
   }
   public void setGenerator(CPNGenerator _generator)
   {
      this.generator = _generator;
   }   
}

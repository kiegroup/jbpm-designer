package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import org.json.JSONObject;

public class Hql extends Sql {
	
	public Hql(JSONObject hql) {
		super(hql);
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("<hql");
		
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("var", var));
		if(unique != null)
			jpdl.write(JsonToJpdl.transformAttribute("unique", unique.toString()));
		
		if(bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid HQL activity. Bounds is missing.");
		}
			
		jpdl.write(" >\n");
		
		if(query != null) {
			jpdl.write("<query>\n");
			jpdl.write(query);
			jpdl.write("\n</query>\n");
		} else {
			throw new InvalidModelException("Invalid HQL activity. Query is missing.");
		}
		
		if (parameters != null) {
			jpdl.write(parameters.toJpdl());
		}
		
		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}
		
		jpdl.write("</hql>\n");
		
		return jpdl.toString();
	}

}

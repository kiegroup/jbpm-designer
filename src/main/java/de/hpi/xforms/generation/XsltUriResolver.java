package de.hpi.xforms.generation;

import java.io.File;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class XsltUriResolver implements URIResolver {
	
	String base_path;
	public XsltUriResolver(ServletContext context) {
		this.base_path = context.getRealPath("/WEB-INF/lib/");
	}

	public Source resolve(String href,String base) {
		StringBuffer path = new StringBuffer(this.base_path);
		path.append("/" + href);
		File file = new File(path.toString());
		if(file.exists()) return new StreamSource(file);
		return null;
	}
	
}

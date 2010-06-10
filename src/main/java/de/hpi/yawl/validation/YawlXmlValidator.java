package de.hpi.yawl.validation;

/**
 * Copyright (c) 2010, Armin Zamani
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import javax.xml.validation.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class YawlXmlValidator {
	
	private Validator validator;
	
	/**
	 * constructor of class 
	 */
	public YawlXmlValidator(){
		instantiate();
	}
	
	/**
	 * instantiates the validator
	 */
	private void instantiate(){
		//1. Lookup a factory for the YAWL XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		
		//2. Compile the schema.
		try {
			URL schemaLocation = new URL("http://www.yawlfoundation.org/yawlschema/YAWL_Schema2.0.xsd");
			Schema schema = factory.newSchema(schemaLocation);
			
			//3. Get a validator from the schema
			this.validator = schema.newValidator();
			
		} catch (MalformedURLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * validates the given file
	 * @param file file to be validated
	 * @return result of validation
	 */
	public Boolean validate(File file){
		try{
			//4. Parse the document you want to check
			Source source = new StreamSource(file);

			//5. Check the document
			this.validator.validate(source);
			System.out.println("Generated YAWL file is valid.\n");
			return true;
		} catch (SAXException ex) {
			System.out.println("Generated YAWL file is not valid because ");
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return false;
	}
}


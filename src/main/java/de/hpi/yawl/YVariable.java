package de.hpi.yawl;

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

public class YVariable {

	private String name = "";
	private String type = "";
	private String namespace = "http://www.w3.org/2001/XMLSchema";
	private String initialValue = "";
	private Boolean readOnly = false;
	
	/**
	 * constructor of class 
	 */
	public YVariable(String name, String type, String namespace, String initialValue, Boolean readOnly){
		setName(name);
		setType(type);
		setNamespace(namespace);
		setInitialValue(initialValue);
		setReadOnly(readOnly);
	}

	/**
	 * constructor of class 
	 */
	public YVariable() {
		
	}

	public String getName(){
		return name;
	}
	
	public void setName(String variableName){
		name = variableName;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String variableType){
		type = variableType;
	}
	
	public String getNamespace(){
		return namespace;
	}
	
	public void setNamespace(String variableNamespace){
		namespace = variableNamespace;
	}
	
	public String getInitialValue(){
		return initialValue;
	}
	
	public void setInitialValue(String variableValue){
		initialValue = variableValue;
	}
	
	public Boolean getReadOnly(){
		return readOnly;
	}
	
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	/**
	 * serializes the variable settings to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeVariableSettingsToYAWL(String s) {
		s += String.format("\t\t\t\t\t\t<name>%s</name>\n", getName());
		s += String.format("\t\t\t\t\t\t<type>%s</type>\n", getType());
		s += String.format("\t\t\t\t\t\t<namespace>%s</namespace>\n", getNamespace());
		return s;
	}
	
	/**
	 * serializes a variable as a parameter to XML
	 * @param s XML String
	 * @return XML String
	 */
	public String writeAsParameterToYAWL(){
		String s = "";
		s = writeVariableSettingsToYAWL(s);
		return s;
	}
}

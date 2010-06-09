package de.hpi.bpmn;

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

public class Property {

	private String name;
	private String type;
	private String value = "";
	private Boolean correlation = false;
	
	/**
	 * constructor of class 
	 */
	public Property(String newName, String newType, String newValue, Boolean hasCorrelation){
		setName(newName);
		setType(newType);
		setValue(newValue);
		setCorrelation(hasCorrelation);
	}
	
	/**
	 * the name setter
	 * @param newName
	 */
	public void setName(String newName){
		name = newName;
	}
	
	/**
	 * the name getter
	 * @return name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * the type setter
	 * @param newType
	 */
	public void setType(String newType){
		type = newType;
	}
	
	/**
	 * the type getter
	 * @return type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * the value setter
	 * @param newValue
	 */
	public void setValue(String newValue){
		value = newValue;
	}
	
	/**
	 * the value getter
	 * @return value
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * the correlation setter
	 * @param isCorrelation
	 */
	public void setCorrelation(Boolean isCorrelation){
		correlation = isCorrelation;
	}
	
	/**
	 * isCorrelation (getter)
	 * @return isCorrelation
	 */
	public Boolean isCorrelation(){
		return correlation;
	}
	
	
}

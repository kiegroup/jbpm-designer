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

public class YVariableMapping implements FileWritingForYAWL {

	private String query = "";
	private YVariable mapsTo = null;
	
	/**
	 * constructor of class 
	 */
	public YVariableMapping(String newQuery, YVariable newMapsTo){
		setQuery(newQuery);
		setMapsTo(newMapsTo);
	}
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String newQuery){
		query = newQuery;
	}
	
	public YVariable getMapsTo(){
		return mapsTo;
	}
	
	public void setMapsTo(YVariable mapped){
		mapsTo = mapped; 
	}
	
	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t\t<mapping>\n";
		s += String.format("\t\t\t\t\t\t<expression query=\"%s\" />\n", getQuery());
		s += String.format("\t\t\t\t\t\t<mapsTo>%s</mapsTo>\n", getMapsTo().getName());
		s += "\t\t\t\t\t</mapping>\n";
		
		return s;
	}
}

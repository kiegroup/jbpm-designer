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

public class YMIDataInput implements FileWritingForYAWL {

	private String expression = "";
	private String splittingExpression = "";
	private YVariable formalInputParam = null;
	
	/**
	 * constructor of class 
	 */
	public YMIDataInput() {
		super();
	}

	/**
	 * constructor of class 
	 */
	public YMIDataInput(String expression, String splittingExpression,
			YVariable formalInputParam) {
		super();
		this.expression = expression;
		this.splittingExpression = splittingExpression;
		this.formalInputParam = formalInputParam;
	}

	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String getSplittingExpression() {
		return splittingExpression;
	}
	
	public void setSplittingExpression(String splittingExpression) {
		this.splittingExpression = splittingExpression;
	}
	
	public YVariable getFormalInputParam() {
		return formalInputParam;
	}
	
	public void setFormalInputParam(YVariable formalInputParam) {
		this.formalInputParam = formalInputParam;
	}
	
	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t<miDataInput>\n";
		
		s += String.format("\t\t\t\t\t<expression query=\"%s\" />\n", getExpression());
        s += String.format("\t\t\t\t\t<splittingExpression query=\"%s\" />\n", getSplittingExpression());
        s += String.format("\t\t\t\t\t<formalInputParam>%s</formalInputParam>\n", getFormalInputParam().getName());
        
        s += "\t\t\t\t</miDataInput>\n";
        
        return s;
	}
}

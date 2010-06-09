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

public class YMIDataOutput implements FileWritingForYAWL {

	private String formalOutputExpression = "";
	private String outputJoiningExpression = "";
	private YVariable resultAppliedToLocalVariable = null;
	
	/**
	 * constructor of class 
	 */
	public YMIDataOutput() {
		super();
	}

	/**
	 * constructor of class 
	 */
	public YMIDataOutput(String formalOutputExpression, String outputJoiningExpression,
			YVariable resultAppliedToLocalVariable) {
		super();
		this.formalOutputExpression = formalOutputExpression;
		this.outputJoiningExpression = outputJoiningExpression;
		this.resultAppliedToLocalVariable = resultAppliedToLocalVariable;
	}

	public String getFormalOutputExpression() {
		return formalOutputExpression;
	}
	
	public void setFormalOutputExpression(String formalOutputExpression) {
		this.formalOutputExpression = formalOutputExpression;
	}
	
	public String getOutputJoiningExpression() {
		return outputJoiningExpression;
	}
	
	public void setOutputJoiningExpression(String outputJoiningExpression) {
		this.outputJoiningExpression = outputJoiningExpression;
	}
	
	public YVariable getResultAppliedToLocalVariable() {
		return resultAppliedToLocalVariable;
	}
	
	public void setResultAppliedToLocalVariable(YVariable resultAppliedToLocalVariable) {
		this.resultAppliedToLocalVariable = resultAppliedToLocalVariable;
	}
	
	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t<miDataOutput>\n";
		
        s += String.format("\t\t\t\t\t<formalOutputExpression query=\"%s\" />\n",getFormalOutputExpression());
        s += String.format("\t\t\t\t\t<outputJoiningExpression query=\"%s\" />\n",getOutputJoiningExpression());
        s += String.format("\t\t\t\t\t<resultAppliedToLocalVariable>%s</resultAppliedToLocalVariable>\n", getResultAppliedToLocalVariable().getName());
        
        s += "\t\t\t\t</miDataOutput>\n";
        
        return s;
	}
	
}

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

import java.util.Locale;

public class YMultiInstanceParam implements FileWritingForYAWL {
	
	public enum CreationMode {
		DYNAMIC, STATIC
	}
	
	private int minimum = 0;
	private int maximum = 0;
	private int threshold = 0;
	private CreationMode creationMode = CreationMode.STATIC;
	private YMIDataInput miDataInput = null;
	private YMIDataOutput miDataOutput = null;
	
	public int getMinimum() {
		return minimum;
	}
	
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	
	public int getMaximum() {
		return maximum;
	}
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	public CreationMode getCreationMode() {
		return creationMode;
	}
	
	public void setCreationMode(CreationMode creationMode) {
		this.creationMode = creationMode;
	}
	
	public void setMiDataInput(YMIDataInput miDataInput) {
		this.miDataInput = miDataInput;
	}

	public YMIDataInput getMiDataInput() {
		return miDataInput;
	}

	public void setMiDataOutput(YMIDataOutput miDataOutput) {
		this.miDataOutput = miDataOutput;
	}

	public YMIDataOutput getMiDataOutput() {
		return miDataOutput;
	}

	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t\t<minimum>" + getMinimum() + "</minimum>\n";
        s += "\t\t\t\t\t<maximum>" + getMaximum() + "</maximum>\n";
        s += "\t\t\t\t\t<threshold>" + getThreshold() + "</threshold>\n";            
        s += "\t\t\t\t\t<creationMode code=\"" + getCreationMode().toString().toLowerCase(Locale.ENGLISH) + "\" />\n";
        
        s += getMiDataInput().writeToYAWL();
        s += getMiDataOutput().writeToYAWL();
        
        return s;
	}
}

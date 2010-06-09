package de.hpi.yawl.resourcing;

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

import java.util.ArrayList;

import de.hpi.yawl.FileWritingForYAWL;

public class DistributionSet implements FileWritingForYAWL {

	private ArrayList<ResourcingType> initialSetList = new ArrayList<ResourcingType>();
	

	public ArrayList<ResourcingType> getInitialSetList() {
		return initialSetList;
	}


	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	@Override
	public String writeToYAWL() {
		String s = "";
		s += "\t\t<distributionSet>\n";
        s += "\t\t\t<initialSet>\n";
        for(ResourcingType resource : initialSetList){
        	//SUBCLASS RESPONSIBILITY
        	if (resource instanceof Capability)
        		s += ((Capability)resource).writeAsMemberOfDistributionSetToYAWL();
        	else if (resource instanceof Participant)
        		s += ((Participant)resource).writeAsMemberOfDistributionSetToYAWL();
        	else if (resource instanceof Position)
        		s += ((Position)resource).writeAsMemberOfDistributionSetToYAWL();
        	else if (resource instanceof Role)
        		s += ((Role)resource).writeAsMemberOfDistributionSetToYAWL();
        }
        
        s += "\t\t\t</initialSet>\n";
        s += "\t\t\t</distributionSet>\n";
		return s;
	}

}

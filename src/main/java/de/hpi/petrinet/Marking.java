package de.hpi.petrinet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (c) 2008 Gero Decker
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
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public interface Marking {
		
	int getNumTokens(Place p);
	
	/**
	 * 
	 * @return the total number of tokens in the net
	 */
	int getNumTokens();
	
	void setNumTokens(Place place, int numTokens);
	
	/**
	 * Returns json representation of marking
	 * {@code {'place1' => 1, 'place2' => 3}}
	 * @throws JSONException 
	 */
	JSONObject toJson() throws JSONException;
	
	/**
	 * Calculates if given marking is a final marking. 
	 * @return true
	 */
	boolean isFinalMarking();
	
	/**
	 * Culculates if given marking is a deadlock.
	 * @return true if deadlock.
	 */
	boolean isDeadlock();
}

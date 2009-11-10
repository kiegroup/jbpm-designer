/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
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
package de.hpi.bpmn2_0.factory;

import java.math.BigInteger;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.activity.Activity;

/**
 * Common factory for BPMN 2.0 activities
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public abstract class AbstractActivityFactory extends AbstractBpmnFactory {

	protected void setStandardAttributes(Activity activity, Shape shape) {
		try {
			activity.setStartQuantity(new BigInteger(shape
					.getProperty("startquantity")));
		} catch (NumberFormatException nfe) {
			activity.setStartQuantity(new BigInteger("1"));
		}

		try {
			activity.setCompletionQuantity(new BigInteger(shape
					.getProperty("completionquantity")));
		} catch (NumberFormatException nfe) {
			activity.setCompletionQuantity(new BigInteger("1"));
		}
		activity
				.setIsForCompensation((shape.getProperty("isforcompensation") != null ? shape
						.getProperty("isforcompensation").equalsIgnoreCase(
								"true")
						: false));
	}

}

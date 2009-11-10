/**
 * Copyright (c) 2009
 * Sven Wagner-Boysen
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

package de.hpi.bpmn2_0.exceptions;

/**
 * This exception class encapsulates exceptions that occurs during the 
 * transformation to or from a BPMN 2.0 model.
 * 
 * @author Sven Wagner-Boysen
 *
 */
public class BpmnConverterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8385535771020951694L;

	public BpmnConverterException() {
	}

	/**
	 * @param message
	 */
	public BpmnConverterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BpmnConverterException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BpmnConverterException(String message, Throwable cause) {
		super(message, cause);
	}

}

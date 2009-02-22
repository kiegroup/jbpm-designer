package org.oryxeditor.server;

import org.w3c.dom.Document;

/**
 * Copyright (c) 2008-2009 
 * 
 * Zhen Peng
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
public class BPELImportPreprocessor {
	
	
	// do a pre-processing on this bpel source
	// in this preprocessor the following works will be done:
	//  	1. handle different namespaces of bpel process
	//  	2. calculate the bounding of each shape
	//  	3. move the <link> elements from <links> element to
	//         to top of the root <process> element, so they could
	//         be easier to handle in BPEL2eRDF.xslt
	//      4. integrate the first <condition> and <activity> element
	//         under a If-block into a <elseIF> element, so they
	//         they could be easier to transform in BPEL2eRDF.xslt
	public Document preprocessDocument(Document oldDocument) {
		return oldDocument;
	}

}
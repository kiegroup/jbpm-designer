/**
 * 
 */
package de.hpi.bpmn2bpel;

import java.util.Iterator;

import org.w3c.dom.Document;


import de.hpi.bpel4chor.util.Output;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn2bpel.factories.ProcessFactory;

/**
 * Copyright (c) 2009 Falko Menge
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
 *
 * @author Falko Menge
 *
 */
public class BPMN2BPELTransformer {

	public Document transform(BPMNDiagram diagram) {
		Document process = null;
		ProcessFactory factory = new ProcessFactory(diagram);
		// use only the first pool
		// TODO rewrite to work without pools 
		Iterator<de.hpi.bpmn.Pool> it = diagram.getPools().iterator();
		if (it.hasNext()) {
			Output processOutput = new Output();
			process = factory.transformProcess(it.next(), processOutput);
		}
		return process;
	}
}

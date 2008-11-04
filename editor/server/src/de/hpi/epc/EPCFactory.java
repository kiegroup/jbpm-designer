package de.hpi.epc;

import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.Document;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.Organization;
import de.hpi.bpt.process.epc.ProcessInterface;
import de.hpi.bpt.process.epc.Role;
import de.hpi.bpt.process.epc.System;

/**
 * Copyright (c) 2008 Kai Schlichting
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

public class EPCFactory {
	public EPC createEPC(){
		return new EPC();
	}
	
	public Function createFunction(){
		return new Function();
	}
	
	public Event createEvent(){
		return new Event();
	}
	
	public Connector createOrConnector(){
		return new Connector(ConnectorType.OR);
	}
	
	public Connector createXorConnector(){
		return new Connector(ConnectorType.XOR);
	}
	
	public Connector createAndConnector(){
		return new Connector(ConnectorType.AND);
	}
	
	public ProcessInterface createProcessInterface(){
		return new ProcessInterface();
	}
	
	public Document createDocument(){
		return new Document();
	}

	public System createSystem(){
		return new System();
	}
	
	public Organization createOrganization(){
		return new Organization();
	}
	
	public Role createRole(){
		return new Role();
	}
	
	public ControlFlow createControlFlow(FlowObject source, FlowObject target){
		return new ControlFlow(source, target);
	}
}

package de.iaas.bpel;

import de.iaas.bpel.models.Assign;
import de.iaas.bpel.models.BPELDiagram;
import de.iaas.bpel.models.Catch;
import de.iaas.bpel.models.CatchAll;
import de.iaas.bpel.models.Compensante;
import de.iaas.bpel.models.CompensanteHandler;
import de.iaas.bpel.models.CompensanteScope;
import de.iaas.bpel.models.Empty;
import de.iaas.bpel.models.EventHandler;
import de.iaas.bpel.models.Exit;
import de.iaas.bpel.models.ExtensionActivity;
import de.iaas.bpel.models.FaultHandler;
import de.iaas.bpel.models.Flow;
import de.iaas.bpel.models.ForEach;
import de.iaas.bpel.models.If;
import de.iaas.bpel.models.IfCondition;
import de.iaas.bpel.models.Invoke;
import de.iaas.bpel.models.OnAlarm;
import de.iaas.bpel.models.OnEvent;
import de.iaas.bpel.models.OnMessage;
import de.iaas.bpel.models.OpaqueActivity;
import de.iaas.bpel.models.Pick;
import de.iaas.bpel.models.Process;
import de.iaas.bpel.models.Receive;
import de.iaas.bpel.models.RepeatUntil;
import de.iaas.bpel.models.Reply;
import de.iaas.bpel.models.Rethrow;
import de.iaas.bpel.models.Scope;
import de.iaas.bpel.models.Sequence;
import de.iaas.bpel.models.SequenceFlow;
import de.iaas.bpel.models.SequenceIfFalse;
import de.iaas.bpel.models.SequenceIfTrue;
import de.iaas.bpel.models.SequenceOrder;
import de.iaas.bpel.models.TerminationHandler;
import de.iaas.bpel.models.Throw;
import de.iaas.bpel.models.Validate;
import de.iaas.bpel.models.Wait;
import de.iaas.bpel.models.While;

/**
 * Copyright (c) 2008 Zhen Peng
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

public class BPELFactory {
	
	public static BPELFactory eINSTANCE = new BPELFactory();
	
	public BPELDiagram createBPELDiagram() {
		return new BPELDiagram();
	}
	
	public Process createProcess() {
		return new Process();
	}
	
	public Invoke createInvoke() {
		return new Invoke();
	}
	public Receive createReceive() {
		return new Receive();
	}
	public Reply createReply() {
		return new Reply();
	}
	public Assign createAssign() {
		return new Assign();
	}
	public Empty createEmpty() {
		return new Empty();
	}
	public OpaqueActivity createOpaqueActivity() {
		return new OpaqueActivity();
	}
	public Validate createValidate() {
		return new Validate();
	}
	public ExtensionActivity createExtensionActivity() {
		return new ExtensionActivity();
	}
	public Wait createWait() {
		return new Wait();
	}	
	public Throw createThrow() {
		return new Throw();
	}
	public Exit createExit() {
		return new Exit();
	}
	public Rethrow createRethrow() {
		return new Rethrow();
	}
	public If createIf() {
		return new If();
	}
	public IfCondition createIfCondition() {
		return new IfCondition();
	}
	public Flow createFlow() {
		return new Flow();
	}
	public ForEach createForEach() {
		return new ForEach();
	}
	public Pick createPick() {
		return new Pick();
	}
	public Sequence createSequence() {
		return new Sequence();
	}
	public While createWhile() {
		return new While();
	}
	public RepeatUntil createRepeatUntil() {
		return new RepeatUntil();
	}
	public OnMessage createOnMessage() {
		return new OnMessage();
	}
	public OnAlarm createOnAlarm() {
		return new OnAlarm();
	}
	public Scope createScope() {
		return new Scope();
	}
	public Compensante createCompensante() {
		return new Compensante();
	}
	public CompensanteScope createCompensanteScope() {
		return new CompensanteScope();
	}
	public CompensanteHandler createCompensanteHandler() {
		return new CompensanteHandler();
	}
	public EventHandler createEventHandler() {
		return new EventHandler();
	}
	public OnEvent createOnEvent() {
		return new OnEvent();
	}
	public TerminationHandler createTerminationHandler() {
		return new TerminationHandler();
	}
	public FaultHandler createFaultHandler() {
		return new FaultHandler();
	}
	public Catch createCatch() {
		return new Catch();
	}
	public CatchAll createCatchAll() {
		return new CatchAll();
	}
	public SequenceOrder createSequenceOrder() {
		return new SequenceOrder();
	}
	public SequenceFlow createSequenceFlow() {
		return new SequenceFlow();
	}
	public SequenceIfTrue createSequenceIfTrue() {
		return new SequenceIfTrue();
	}
	public SequenceIfFalse createSequenceIfFalse() {
		return new SequenceIfFalse();
	}


}

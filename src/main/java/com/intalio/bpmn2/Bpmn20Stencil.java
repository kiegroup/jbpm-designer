/***************************************
 * Copyright (c) Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/
package com.intalio.bpmn2;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.emf.ecore.EClass;

/**
 * @author Antoine Toulme
 * the mapping to stencil ids to BPMN 2.0 metamodel classes
 *
 */
public enum Bpmn20Stencil {

    Task(Bpmn2Package.eINSTANCE.getTask()), 
    BPMNDiagram(Bpmn2Package.eINSTANCE.getDefinitions()),
    Pool(Bpmn2Package.eINSTANCE.getProcess()), 
    Lane(Bpmn2Package.eINSTANCE.getLane()),
    SequenceFlow(Bpmn2Package.eINSTANCE.getSequenceFlow()),
    Task_None(Bpmn2Package.eINSTANCE.getTask()), 
    Task_Script(Bpmn2Package.eINSTANCE.getScriptTask()),
    Task_User(Bpmn2Package.eINSTANCE.getUserTask()),
    Task_Business_Rule(Bpmn2Package.eINSTANCE.getBusinessRuleTask()),
    Task_Manual(Bpmn2Package.eINSTANCE.getManualTask()),
    Task_Service(Bpmn2Package.eINSTANCE.getServiceTask()),
    Task_Send(Bpmn2Package.eINSTANCE.getSendTask()),
    Task_Receive(Bpmn2Package.eINSTANCE.getReceiveTask()),
    Exclusive_Databased_Gateway(Bpmn2Package.eINSTANCE.getExclusiveGateway());
    
    public String id;
    public EClass className;
    private Bpmn20Stencil(EClass className) {
        this.className = className;
    }
    
    public static final EClass getClass(String stencilId, String taskType) {
        Bpmn20Stencil stencil = Bpmn20Stencil.valueOf(taskType == null ? stencilId : stencilId + "_" + taskType.replaceAll(" ", "_"));
        if (stencil == null) {
            throw new IllegalArgumentException("unregistered stencil id: " + stencilId);
        }
        return stencil.className;
    }
}

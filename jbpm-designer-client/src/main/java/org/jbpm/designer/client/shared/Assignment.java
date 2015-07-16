/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.shared;

import org.jbpm.designer.client.shared.Variable.VariableType;

public class Assignment {

    private Variable variable;

    AssignmentData assignmentData;

    /*
        Assignments have either a processVar or a constant
     */
    private Variable processVar;
    private String constant;

    private static final String INPUT_ASSIGNMENT_PREFIX = "[din]";
    private static final String OUTPUT_ASSIGNMENT_PREFIX = "[dout]";
    private static final String ASSIGNMENT_OPERATOR_TOVARIABLE = "->";
    private static final String ASSIGNMENT_OPERATOR_TOCONSTANT = "=";

    public Assignment() {
    }

    public Assignment(AssignmentData assignmentData, String variableName, VariableType variableType, String dataType, String customDataType,
            String processVarName, String constant) {
        this.assignmentData = assignmentData;
        variable = assignmentData.findVariable(variableName, variableType);
        if (variable == null) {
            variable = new Variable(variableName, variableType, dataType, customDataType);
            assignmentData.addVariable(variable);
        }

        this.processVar = assignmentData.findProcessVariable(processVarName);
        this.constant = constant;
    }

    public Assignment(AssignmentData assignmentData, String variableName, VariableType variableType, String processVariableName,
            String constant) {
        this.assignmentData = assignmentData;
        variable = assignmentData.findVariable(variableName, variableType);
        if (variable == null) {
            variable = new Variable(variableName, variableType);
            assignmentData.addVariable(variable);
        }

        if (processVariableName != null && !processVariableName.isEmpty()) {
            processVar = assignmentData.findProcessVariable(processVariableName);
            // Didn't find processVar - add one
            if (processVar == null) {
                processVar = new Variable(processVariableName, VariableType.PROCESS);
                assignmentData.addVariable(processVar);
            }
        }

        this.constant = constant;
    }

    public String getName() {
        return variable.getName();
    }

    public void setName(String name) {
        variable.setName(name);
    }

    public VariableType getVariableType() {
        return variable.getVariableType();
    }

    public void setVariableType(VariableType variableType) {
       variable.setVariableType(variableType);
    }

    public Variable getVariable() {
        return variable;
    }


    public String getDataType() {
        return variable.getDataType();
    }

    public void setDataType(String dataType) {
        variable.setDataType(dataType);
    }

    public String getCustomDataType() {
        return variable.getCustomDataType();
    }

    public void setCustomDataType(String customDataType) {
       variable.setCustomDataType(customDataType);
    }

    public String getProcessVarName() {
        return ((processVar != null) ?  processVar.getName() :  null);
    }

    public void setProcessVarName(String processVarName) {
        this.processVar = assignmentData.findProcessVariable(processVarName);
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public AssignmentData getAssignmentData() {
        return assignmentData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Assignment other = (Assignment) obj;
        if (variable == null) {
            if (other.variable != null) {
                return false;
            }
        }
        else if (variable != variable) {
            return false;
        }
        return true;
        
    }

    /**
     * Serializes assignment
     * e.g. e.g. [din]str1->inStr, [din]inStrConst=TheString, [dout]outStr1->str1
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getVariableType() == VariableType.INPUT) {
            if (getConstant() != null && !getConstant().isEmpty()) {
                sb.append(INPUT_ASSIGNMENT_PREFIX).append(getName()).append(ASSIGNMENT_OPERATOR_TOCONSTANT).append(getConstant());
            }
            else if (getProcessVarName() != null && !getProcessVarName().isEmpty()) {
                sb.append(INPUT_ASSIGNMENT_PREFIX).append(getProcessVarName()).append(ASSIGNMENT_OPERATOR_TOVARIABLE).append(getName());
            }
            else {
                sb.append(INPUT_ASSIGNMENT_PREFIX).append(ASSIGNMENT_OPERATOR_TOVARIABLE).append(getName());
            }
        }
        else {
            if (getProcessVarName() != null && !getProcessVarName().isEmpty()) {
                sb.append(OUTPUT_ASSIGNMENT_PREFIX).append(getName()).append(ASSIGNMENT_OPERATOR_TOVARIABLE).append(getProcessVarName());
            }
            else {
                sb.append(OUTPUT_ASSIGNMENT_PREFIX).append(getName()).append(ASSIGNMENT_OPERATOR_TOVARIABLE);
            }
        }
        return sb.toString();
    }

    /**
     * Deserializes an assignment string
     * e.g. [din]str1->inStr, [din]inStrConst=TheString, [dout]outStr1->str1
     *
     * @param sAssignment
     * @return Assignment
     */
    public static Assignment deserialize(AssignmentData assignmentData, String sAssignment) {
        if (sAssignment == null || sAssignment.isEmpty()) {
            return null;
        }

        // Parse the assignment string
        VariableType assignmentType = null;
        if (sAssignment.startsWith(INPUT_ASSIGNMENT_PREFIX)) {
            assignmentType = VariableType.INPUT;
            sAssignment = sAssignment.substring(INPUT_ASSIGNMENT_PREFIX.length());
        }
        else if (sAssignment.startsWith(OUTPUT_ASSIGNMENT_PREFIX)) {
            assignmentType = VariableType.OUTPUT;
            sAssignment = sAssignment.substring(OUTPUT_ASSIGNMENT_PREFIX.length());
        }
        String variableName = null;
        String processVariableName = null;
        String constant = null;
        if (sAssignment.contains(ASSIGNMENT_OPERATOR_TOVARIABLE)) {
            int i = sAssignment.indexOf(ASSIGNMENT_OPERATOR_TOVARIABLE);
            if (assignmentType == VariableType.INPUT) {
                processVariableName = sAssignment.substring(0, i);
                variableName = sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOVARIABLE.length());
            }
            else {
                variableName = sAssignment.substring(0, i);
                processVariableName = sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOVARIABLE.length());
            }
        }
        else if (sAssignment.contains(ASSIGNMENT_OPERATOR_TOCONSTANT)) {
            int i = sAssignment.indexOf(ASSIGNMENT_OPERATOR_TOCONSTANT);
            variableName = sAssignment.substring(0, i);
            constant = sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOCONSTANT.length());
        }

        // Create the new assignment
        return new Assignment(assignmentData, variableName, assignmentType, processVariableName, constant);
    }
}
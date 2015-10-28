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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.designer.client.shared.Variable.VariableType;

/**
 *
 * Class which contains everything associated with Assignments which
 * is passed between the Designer properties and the DataIOEditor, i.e.
 * Assignments, InputVariables, OutputVariables, DataTypes and ProcessVariables
 */
@Portable
public class AssignmentData {

    private List<Variable> inputVariables = new ArrayList<Variable>();

    private List<Variable> outputVariables = new ArrayList<Variable>();

    private List<Variable> processVariables = new ArrayList<Variable>();

    private List<Assignment> assignments = new ArrayList<Assignment>();

    private List<String> dataTypes = new ArrayList<String>();
    private List<String> dataTypeDisplayNames = new ArrayList<String>();
    private Map<String, String> mapDisplayNameToDataType = new HashMap<String, String>();
    private Map<String, String> mapDataTypeToDisplayName = new HashMap<String, String>();
    private Map<String, String> mapSimpleDataTypeToDisplayName = new HashMap<String, String>();

    private List<String> disallowedPropertyNames = new ArrayList<String>();

    public AssignmentData() {

    }

    public AssignmentData(String sInputVariables, String sOutputVariables, String sProcessVariables,
            String sAssignments, String sDataTypes, String sDisallowedPropertyNames) {
        // setDataTypes before variables because these determine whether variable datatypes are custom or not
        setDataTypes(sDataTypes);
        setProcessVariables(sProcessVariables);
        setInputVariables(sInputVariables, dataTypes);
        setOutputVariables(sOutputVariables, dataTypes);
        setAssignments(sAssignments);
        setDisallowedPropertyNames(sDisallowedPropertyNames);
    }

    /**
     * Creates AssignmentData based on a list of inputAssignmentRows and outputAssignmentRows.
     *
     * @param inputAssignmentRows
     * @param outputAssignmentRows
     */
    public AssignmentData(List<AssignmentRow> inputAssignmentRows, List<AssignmentRow> outputAssignmentRows,
            List<String> dataTypes, List<String> dataTypeDisplayNames) {
        setDataTypes(dataTypes, dataTypeDisplayNames);
        if (inputAssignmentRows != null) {
            for (AssignmentRow row : inputAssignmentRows) {
                convertAssignmentRow(row);
            }
        }
        if (outputAssignmentRows != null) {
            for (AssignmentRow row : outputAssignmentRows) {
                convertAssignmentRow(row);
            }
        }
    }


    protected void convertAssignmentRow(AssignmentRow assignmentRow) {
        if (assignmentRow.getName() == null || assignmentRow.getName().isEmpty()) {
            return;
        }

        if (findVariable(assignmentRow.getName(), assignmentRow.getVariableType()) == null) {
            Variable var = new Variable(assignmentRow.getName(), assignmentRow.getVariableType(),
                    getDataTypeFromDisplayName(assignmentRow.getDataType()), assignmentRow.getCustomDataType());
            addVariable(var);
        }

        String processVarName;
        // If there's a constant, use it rather than processVar
        String constant = assignmentRow.getConstant();
        if (constant != null && !constant.isEmpty()) {
            processVarName = null;
            constant = AssignmentData.createUnquotedConstant(constant);
        }
        else {
            processVarName = assignmentRow.getProcessVar();
            if (processVarName != null && !processVarName.isEmpty()) {
                Variable processVar = new Variable(processVarName, VariableType.PROCESS,
                        assignmentRow.getDataType(), assignmentRow.getCustomDataType());
                processVariables.add(processVar);
            }
        }
        if ((constant == null || constant.isEmpty()) && (processVarName == null || processVarName.isEmpty()))
        {
            return;
        }

        Assignment assignment = new Assignment(this, assignmentRow.getName(), assignmentRow.getVariableType(),
                processVarName, constant);
        assignments.add(assignment);
    }

    public List<Variable> getInputVariables() {
        return inputVariables;
    }

    public String getInputVariablesString() {
        StringBuilder sb = new StringBuilder();
        for (Variable var : inputVariables) {
            sb.append(var.toString()).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void setInputVariables(String sInputVariables, List<String> dataTypes) {
        inputVariables.clear();
        if (sInputVariables != null && !sInputVariables.isEmpty()) {
            String[] inputs = sInputVariables.split(",");
            for (String input : inputs) {
                if (!input.isEmpty()) {
                    Variable var = Variable.deserialize(input, Variable.VariableType.INPUT, dataTypes);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        inputVariables.add(var);
                    }
                }
            }
        }
    }

    public List<Variable> getOutputVariables() {
        return outputVariables;
    }

    public String getOutputVariablesString() {
        StringBuilder sb = new StringBuilder();
        for (Variable var : outputVariables) {
            sb.append(var.toString()).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void setOutputVariables(String sOutputVariables, List<String> dataTypes) {
        outputVariables.clear();
        if (sOutputVariables != null && !sOutputVariables.isEmpty()) {
            String[] outputs = sOutputVariables.split(",");
            for (String output : outputs) {
                if (!output.isEmpty()) {
                    Variable var = Variable.deserialize(output, Variable.VariableType.OUTPUT, dataTypes);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        outputVariables.add(var);
                    }
                }
            }
        }
    }

    public List<Variable> getProcessVariables() {
        return processVariables;
    }

    public String getProcessVariablesString() {
        StringBuilder sb = new StringBuilder();
        for (Variable var : processVariables) {
            sb.append(var.toString()).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void setProcessVariables(String sProcessVariables) {
        processVariables.clear();
        if (sProcessVariables != null && !sProcessVariables.isEmpty()) {
            HashSet<String> procVarNames = new HashSet<String>();
            String[] processVars = sProcessVariables.split(",");
            for (String processVar : processVars) {
                if (!processVar.isEmpty()) {
                    Variable var = Variable.deserialize(processVar, Variable.VariableType.PROCESS);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        if (!procVarNames.contains(var.getName())) {
                            procVarNames.add(var.getName());
                            processVariables.add(var);
                        }
                    }
                }
            }
        }
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public String getAssignmentsString() {
        StringBuilder sb = new StringBuilder();
        for (Assignment a : assignments) {
            sb.append(a.toString()).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void setAssignments(String sAssignments) {
        assignments.clear();
        if (sAssignments != null && !sAssignments.isEmpty()) {
            String[] as = sAssignments.split(",");
            for (String a : as) {
                if (!a.isEmpty()) {
                    Assignment ass = Assignment.deserialize(this, a);
                    if (ass != null && ass.getName() != null && !ass.getName().isEmpty()) {
                        assignments.add(ass);
                    }
                }
            }
        }
    }

    public List<String> getDataTypes() {
        return dataTypes;
    }

    protected void setDataTypes(String dataTypes) {
        this.dataTypes.clear();
        this.dataTypeDisplayNames.clear();
        mapDisplayNameToDataType.clear();
        mapDataTypeToDisplayName.clear();
        mapSimpleDataTypeToDisplayName.clear();

        if (dataTypes != null && !dataTypes.isEmpty()) {
            String[] dts = dataTypes.split(",");
            for (String dt : dts) {
                dt = dt.trim();
                if (!dt.isEmpty() && !dt.startsWith("*")) {
                    String dtName = "";
                    String dtDisplayName = "";
                    String dtSimpleType = "";
                    if (dt.contains(":")) {
                        dtDisplayName = dt.substring(0, dt.indexOf(':')).trim();
                        dtName = dt.substring(dt.indexOf(':') + 1).trim();
                    }
                    else {
                        dtDisplayName = dt.trim();
                        dtName = dt.trim();
                    }
                    if (dtDisplayName.indexOf(' ') > 0) {
                        dtSimpleType = dtDisplayName.substring(0, dtDisplayName.indexOf(' '));
                    }
                    else {
                        dtSimpleType = dtDisplayName;
                    }
                    if (!dtName.isEmpty()) {
                        this.dataTypeDisplayNames.add(dtDisplayName);
                        this.dataTypes.add(dtName);
                        mapDisplayNameToDataType.put(dtDisplayName, dtName);
                        mapDataTypeToDisplayName.put(dtName, dtDisplayName);
                    }
                    if (!dtSimpleType.isEmpty()){
                        mapSimpleDataTypeToDisplayName.put(dtSimpleType, dtDisplayName);
                    }
                }
            }
        }
    }

    protected void setDataTypes(List<String> dataTypes, List<String> dataTypeDisplayNames) {
        this.dataTypes.clear();
        this.dataTypeDisplayNames.clear();
        mapDisplayNameToDataType.clear();
        mapDataTypeToDisplayName.clear();
        mapSimpleDataTypeToDisplayName.clear();

        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;

        for (int i = 0; i < dataTypeDisplayNames.size(); i++) {
            if (i < dataTypes.size()) {
                mapDisplayNameToDataType.put(dataTypeDisplayNames.get(i), dataTypes.get(i));
                mapDataTypeToDisplayName.put(dataTypes.get(i), dataTypeDisplayNames.get(i));
            }
            else {
                mapDisplayNameToDataType.put(dataTypeDisplayNames.get(i), dataTypeDisplayNames.get(i));
                mapDataTypeToDisplayName.put(dataTypeDisplayNames.get(i), dataTypeDisplayNames.get(i));
            }
        }
    }

    public List<String> getDisallowedPropertyNames() {
        return disallowedPropertyNames;
    }

    protected void setDisallowedPropertyNames(String disallowedPropertyNames) {
        this.disallowedPropertyNames.clear();

        if (disallowedPropertyNames != null && !disallowedPropertyNames.isEmpty()) {
            String[] hps = disallowedPropertyNames.split(",");
            for (String hp : hps) {
                hp = hp.trim();
                if (!hp.isEmpty()) {
                    this.disallowedPropertyNames.add(hp);
                }
            }
        }
    }


    public Variable findProcessVariable(String processVarName) {
        if (processVarName == null || processVarName.isEmpty()) {
            return null;
        }
        for (Variable var : processVariables) {
            if (processVarName.equals(var.getName())) {
                return var;
            }
        }
        return null;
    }

    public Variable findVariable(String variableName, VariableType variableType) {
        if (variableName == null || variableName.isEmpty()) {
            return null;
        }
        if (variableType == Variable.VariableType.INPUT) {
            for (Variable var : inputVariables) {
                if (variableName.equals(var.getName())) {
                    return var;
                }
            }
        } else if (variableType == Variable.VariableType.OUTPUT) {
            for (Variable var : outputVariables) {
                if (variableName.equals(var.getName())) {
                    return var;
                }
            }
        }
        return null;
    }

    public void addVariable(Variable variable) {
        if (variable.getName() == null || variable.getName().isEmpty()) {
            return;
        }
        if (findVariable(variable.getName(), variable.getVariableType()) != null) {
            return;
        }
        if (variable.getVariableType() == VariableType.INPUT) {
            inputVariables.add(variable);
        }
        else if (variable.getVariableType() == VariableType.OUTPUT) {
            outputVariables.add(variable);
        }
        else if (variable.getVariableType() == VariableType.PROCESS) {
            processVariables.add(variable);
        }
    }

    public List<String> getDataTypeDisplayNames() {
        return dataTypeDisplayNames;
    }

    public String getDataTypeFromDisplayName(String dataTypeDisplayName) {
        if (mapDisplayNameToDataType.get(dataTypeDisplayName) != null) {
            return mapDisplayNameToDataType.get(dataTypeDisplayName);
        }
        else {
            return dataTypeDisplayName;
        }
    }

    public String getDisplayNameFromDataType(String dataType) {
        if (mapDataTypeToDisplayName.get(dataType) != null) {
            return mapDataTypeToDisplayName.get(dataType);
        }
        else {
            return dataType;
        }
    }

    public String getDataTypeDisplayNameForUserString(String userValue) {
        if (mapDataTypeToDisplayName.containsKey(userValue)) {
            return mapDataTypeToDisplayName.get(userValue);
        }
        else if (mapSimpleDataTypeToDisplayName.containsKey(userValue)) {
            return mapSimpleDataTypeToDisplayName.get(userValue);
        }

        return null;
    }

    public String getDataTypesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataTypes.size(); i++) {
            String dataTypeDisplayName = dataTypes.get(i);
            String dataType = dataTypes.get(i);
            sb.append(dataTypeDisplayName).append(':').append(dataType).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public String getDisallowedPropertyNamesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < disallowedPropertyNames.size(); i++) {
            sb.append(disallowedPropertyNames.get(i)).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public List<String> getProcessVariableNames() {
        List<String> processVarNames = new ArrayList<String>();
        for (Variable processVar : processVariables) {
            processVarNames.add(processVar.getName());
        }
        return processVarNames;
    }

    /**
     * Gets a list of AssignmentRows based on the current Assignments
     *
     * @return
     */
    public List<AssignmentRow> getAssignmentRows(VariableType varType) {
        List<AssignmentRow> rows = new ArrayList<AssignmentRow>();
        List<Variable> handledVariables = new ArrayList<Variable>();
        // Create an AssignmentRow for each Assignment
        for (Assignment assignment : assignments) {
            if (assignment.getVariableType() == varType) {
                String dataType = getDisplayNameFromDataType(assignment.getDataType());
                AssignmentRow row = new AssignmentRow(assignment.getName(), assignment.getVariableType(), dataType,
                        assignment.getCustomDataType(), assignment.getProcessVarName(), assignment.getConstant());
                rows.add(row);
                handledVariables.add(assignment.getVariable());
            }
        }
        List<Variable> vars = null;
        if (varType == VariableType.INPUT) {
            vars = inputVariables;
        }
        else {
            vars = outputVariables;
        }
        // Create an AssignmentRow for each Variable that doesn't have an Assignment
        for (Variable var : vars) {
            if (!handledVariables.contains(var)) {
                AssignmentRow row = new AssignmentRow(var.getName(), var.getVariableType(), var.getDataType(),
                        var.getCustomDataType(), null, null);
                rows.add(row);
            }
        }

        return rows;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"inputVariables\":\"").append(getInputVariablesString()).append("\"").append(",\n");
        sb.append("\"outputVariables\":\"").append(getOutputVariablesString()).append("\"").append(",\n");
        sb.append("\"processVariables\":\"").append(getProcessVariablesString()).append("\"").append(",\n");
        sb.append("\"assignments\":\"").append(getAssignmentsString()).append("\"").append(",\n");
        sb.append("\"dataTypes\":\"").append(getDataTypesString()).append("\"").append(",\n");
        sb.append("\"disallowedPropertyNames\":\"").append(getDisallowedPropertyNamesString()).append("\"");

        return sb.toString();
    }

    public static String createQuotedConstant(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return "\"" + str + "\"";
        }
        return str;
    }

    public static String createUnquotedConstant(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

}

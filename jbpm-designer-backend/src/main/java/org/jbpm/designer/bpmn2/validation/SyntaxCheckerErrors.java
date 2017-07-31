/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.bpmn2.validation;

public class SyntaxCheckerErrors {

    public static final String AT_LEAST_ONE_OUTGOING_PROBABILITY_VALUE_100 = "At least one outgoing connection should have probability equal to 100.";
    public static final String BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP = "Business Rule Task has no ruleflow-group.";
    public static final String DMN_BUSINESS_RULE_TASK_NO_NAMESPACE = "DMN Business Rule Task has no namespace set.";
    public static final String DMN_BUSINESS_RULE_TASK_NO_MODEL = "DMN Business Rule Task has no model set.";
    public static final String COST_PER_TIME_UNIT_MUST_BE_POSITIVE = "Cost per Time Unit value must be positive.";
    public static final String END_NODE_NO_INCOMING_CONNECTIONS = "End node has no incoming connections";
    public static final String EVENT_HAS_NO_ERROR_REF = " Event has no errorref.";
    public static final String MULTIPLE_START_EVENTS = "Multiple start events not allowed.";
    public static final String NO_CALLED_ELEMENT_SPECIFIED = "Reusable Subprocess has no called element specified.";
    public static final String NODE_NO_INCOMING_CONNECTIONS = "Node has no incoming connections";
    public static final String NODE_NO_OUTGOING_CONNECTIONS = "Node has no outgoing connections";
    public static final String NOT_VALID_DEFAULT_GATE = "Default gate must be one of outgoing flows.";
    public static final String PROBABILITY_MUST_BE_POSITIVE = "Probability value must be positive.";
    public static final String SERVICE_TASK_NO_OPERATION = "Service Task has no operation.";
    public static final String SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED = "Sequence flow has no condition expression defined.";
    public static final String SEQUENCE_FLOW_NO_PROBABILITY_DEFINED = "Sequence Flow has no probability defined.";
    public static final String SCRIPT_TASK_NO_SCRIPT = "Script Task has no script.";
    public static final String SCRIPT_TASK_NO_SCRIPT_FORMAT = "Script Task has no script format.";
    public static final String STAFF_AVAILABILITY_MUST_BE_POSITIVE = "Staff Availability value must be positive.";
    public static final String START_NODE_NO_OUTGOING_CONNECTIONS = "Start node has no outgoing connections";
    public static final String TASK_NO_MESSAGE = "Task has no message.";
    public static final String THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100 = "The sum of probability values of all outgoing Sequence Flows must be equal 100.";
    public static final String USER_TASK_HAS_NO_TASK_NAME = "User Task has no task name.";
    public static final String WORKING_HOURS_MUST_BE_POSITIVE = "Working Hours value must be positive.";
    public static final String PACKAGE_NAME_CONTAINS_INVALID_CHARACTERS = "Package name contains invalid characters.";
}

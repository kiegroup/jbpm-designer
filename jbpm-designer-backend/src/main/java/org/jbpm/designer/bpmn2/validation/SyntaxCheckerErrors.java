package org.jbpm.designer.bpmn2.validation;

public class SyntaxCheckerErrors {
    public static final String AN_EDGE_MUST_HAVE_A_SOURCE_NODE = "An Edge must have a source node.";
    public static final String AN_EDGE_MUST_HAVE_A_TARGET_NODE = "An Edge must have a target node.";
    public static final String AT_LEAST_ONE_OUTGOING_PROBABILITY_VALUE_100 = "At least one outgoing connection should have probability equal to 100.";
    public static final String BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP = "Business Rule Task has no ruleflow-group.";
    public static final String CATCH_EVENT = "Catch Event ";
    public static final String COMPLEX_GATEWAY = "Complex Gateway.";
    public static final String COST_PER_TIME_UNIT_VALUE_MUST_BE_POSITIVE = "Cost per Time Unit value must be positive.";
    public static final String COULD_NOT_PARSE_BPMN2_PROCESS = "Could not parse BPMN2 process.";
    public static final String COULD_NOT_PARSE_BPMN2_TO_RULE_FLOW_PROCESS = "Could not parse BPMN2 to RuleFlowProcess.";
    public static final String DATA_OBJECT_HAS_NO_NAME_DEFINED = "Data Object has no name defined.";
    public static final String DATA_OBJECT_NAME_CONTAINS_WHITE_SPACES = "Data Object name contains white spaces.";
    public static final String END_NODE_NO_INCOMING_CONNECTIONS = "End node has no incoming connections";
    public static final String EVENT_BASED_GATEWAY = "EventBased Gateway.";
    public static final String EXCLUSING_GATEWAY = "Exclusing Gateway.";
    public static final String GATEWAY_DOES_NOT_SPECIFY_A_VALID_DIRECTION = "Gateway does not specify a valid direction.";
    public static final String HAS_BOTH_TIME_DATE_AND_TIME_CYCLE_DEFINED = " has both timeDate and timeCycle defined.";
    public static final String HAS_BOTH_TIME_DATE_AND_TIME_DURATION_DEFINED = " has both timeDate and timeDuration defined.";
    public static final String HAS_BOTH_TIMEDURATION_AND_TIMECYCLE_DEFINED = " has both timeduration and timecycle defined.";
    public static final String HAS_NO_ACTIVITYREF = " has no activityref.";
    public static final String HAS_NO_CONDITIONAL_EXPRESSION = " has no conditional expression.";
    public static final String HAS_NO_ERRORREF = " has no errorref.";
    public static final String HAS_NO_ESCALATIONREF = " has no escalationref.";
    public static final String HAS_NO_MESSAGEREF = " has no messageref.";
    public static final String HAS_NO_SIGNALREF = " has no signalref.";
    public static final String HAS_NO_TIME_DATE_OR_TIME_DURATION_OR_TIME_CYCLE_DEFINED = " has no timeDate or timeDuration or timeCycle defined.";
    public static final String HAS_TIME_DATE_AND_TIME_DURATION_AND_TIME_CYCLE_DEFINED = " has timeDate and timeDuration and timeCycle defined.";
    public static final String INCLUSIVE_GATEWAY = "Inclusive Gateway.";
    public static final String INVALID_CONDITION_EXPRESSION_ON_SEQUENCE_FLOW = "Invalid condition expression on sequence flow.";
    public static final String INVALID_GATEWAY_DIRECTION_FOR = "Invalid Gateway direction for ";
    public static final String INVALID_PROCESS_ID = "Invalid process id. See http://www.w3.org/TR/REC-xml-names/#NT-NCName for more info.";
    public static final String IT_SHOULD_BE_CONVERGING_OR_DIVERGING = " It should be 'Converging' or 'Diverging'.";
    public static final String IT_SHOULD_BE_DIVERGING = " It should be 'Diverging'.";
    public static final String NODE_NO_INCOMING_CONNECTIONS = "Node has no incoming connections";
    public static final String NODE_NO_OUTGOING_CONNECTIONS = "Node has no outgoing connections";
    public static final String NOT_VALID_DEFAULT_GATE = "Default gate must be one of outgoing flows.";
    public static final String PACKAGE_NAME_CONTAINS_INVALID_CHARACTERS = "Package name contains invalid characters.";
    public static final String PARALLEL_GATEWAY = "Parallel Gateway.";
    public static final String PROBABILITY_VALUE_MUST_BE_POSITIVE = "Probability value must be positive.";
    public static final String PROCESS_HAS_NO_END_NODE = "Process has no end node.";
    public static final String PROCESS_HAS_NO_ID = "Process has no id.";
    public static final String PROCESS_HAS_NO_NAME = "Process has no name.";
    public static final String PROCESS_HAS_NO_PACKAGE_NAME = "Process has no package name.";
    public static final String PROCESS_HAS_NO_START_NODE = "Process has no start node.";
    public static final String REUSABLE_SUBPROCESS_HAS_NO_CALLED_ELEMENT_SPECIFIED = "Reusable Subprocess has no called element specified.";
    public static final String SCRIPT_TASK_HAS_NO_SCRIPT_FORMAT = "Script Task has no script format.";
    public static final String SCRIPT_TASK_HAS_NO_SCRIPT = "Script Task has no script.";
    public static final String SEND_TASK_HAS_NO_MESSAGE = "Send Task has no message.";
    public static final String SEQUENCE_FLOW_HAS_NO_PROBABILITY_DEFINED = "Sequence Flow has no probability defined.";
    public static final String SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED = "Sequence flow has no condition expression defined.";
    public static final String SERVICE_TASK_HAS_NO_OPERATION = "Service Task has no operation.";
    public static final String STAFF_AVAILABILITY_VALUE_MUST_BE_POSITIVE = "Staff Availability value must be positive.";
    public static final String START_NODE_NO_OUTGOING_CONNECTIONS = "Start node has no outgoing connections";
    public static final String THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100 = "The sum of probability values of all outgoing Sequence Flows must be equal 100.";
    public static final String THROW_EVENT = "Throw Event ";
    public static final String USER_TASK_HAS_NO_TASK_NAME = "User Task has no task name.";
    public static final String WORKING_HOURS_VALUE_MUST_BE_POSITIVE = "Working Hours value must be positive.";

    public static final String processVariableContainsWhiteSpaces(String variableId) {
        return "Process variable \"" + variableId + "\" contains white spaces.";
    }
}
